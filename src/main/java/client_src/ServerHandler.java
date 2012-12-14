package main.java.client_src;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Executors;

import main.java.client_src.terminal.TerminalWrite;
import main.java.commands.ExtendCircuit;
import main.java.commands.ExtendCircuitAck;
import main.java.commands.NewCircuit;
import main.java.commands.NewCircuitAck;
import main.java.commands.ORCommand;
import main.java.globals.CircuitMap;
import main.java.globals.MyCircuit;
import main.java.globals.NodeList;
import main.java.utils.Log;
import main.java.utils.ORPipelineFactory;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;


public class ServerHandler  extends SimpleChannelHandler{
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		e.getChannel().close();
		e.getCause().printStackTrace();
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		ORCommand command = (ORCommand) e.getMessage();
		if(command.isOk()){
			if(command instanceof NewCircuit){
				NewCircuit circuit = (NewCircuit) command;
				NewCircuitAck ack = new NewCircuitAck();
				ack.setCircuitId(circuit.getCircuitId());
				Log.db("received circuit request. sending acknowledgement");
				e.getChannel().write(ack).addListener(new ChannelFutureListener(){
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						future.getChannel().close();
					}
				});
			}else if(command instanceof ExtendCircuit){
				ExtendCircuit circuit = (ExtendCircuit) command;
				extendOtherCircuit(e.getChannel(),circuit.getCircuitId());
			}else{
				Log.e("received an unexpected command "+command.getCommandType());
				e.getChannel().close();
			}
		}else{
			Log.e("an error occurred while decoding ["+command.getError()+"]");
			main.java.commands.Error error = new main.java.commands.Error();
			error.setMessage(command.getError());
			e.getChannel().write(error).addListener(new ChannelFutureListener(){
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					future.getChannel().close();
				}
			});
		}
	}
	private static final void extendOtherCircuit(final Channel returnChannel, final UUID srcCircuitId){
		ClientBootstrap cb = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),Executors.newCachedThreadPool()));
		ArrayList<Node> nodeList = NodeList.getAll();
		int nodeIndex = (int) Math.rint((Math.random()*(nodeList.size()-1)));
		final Node node = nodeList.get(nodeIndex);
		final UUID circuitId = UUID.randomUUID();
		cb.setPipelineFactory(new ORPipelineFactory(new SimpleChannelHandler(){

			@Override
			public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
				ORCommand command = (ORCommand) e.getMessage();
				if(command.isOk()){
					if(command instanceof NewCircuitAck){
						CircuitMap.addForwardingCircuit(circuitId, node);
						ExtendCircuitAck ack = new ExtendCircuitAck();
						ack.setCircuitId(srcCircuitId);
						ack.setNextCircuitId(circuitId);
						returnChannel.write(ack).addListener(new ChannelFutureListener(){
							@Override
							public void operationComplete(ChannelFuture future) throws Exception {
								future.getChannel().close();
							}
						});
						Log.db("new circuit created with node: "+node.getNodeName()+" to extend another");
					}else{
						Log.e("received an unexpected command ["+command+"]");
					}
				}else{
					Log.e("an error occured when trying to create a new circuit ["+command.getError()+"]");
				}
				e.getChannel().close();
			}

			@Override
			public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
				NewCircuit circuit = new NewCircuit();
				circuit.setCircuitId(circuitId);
				e.getChannel().write(circuit).addListener(new ChannelFutureListener(){
					@Override
					public void operationComplete(ChannelFuture arg0) throws Exception {
						Log.db("requested new circuit with node "+node.getNodeName()+" to extend another");
					}
				});
			}

			@Override
			public void exceptionCaught(ChannelHandlerContext ctx,
					ExceptionEvent e) throws Exception {
				e.getChannel().close();
				e.getCause().printStackTrace();
			}
		}));
		cb.connect(node.getAddr());
	}
}
