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
import main.java.commands.ORError;
import main.java.commands.RegisterSuccess;
import main.java.commands.Forward;
import main.java.commands.Reverse;
import main.java.globals.CircuitMap;
import main.java.globals.GlobalVars;
import main.java.globals.MyCircuit;
import main.java.globals.NodeList;
import main.java.utils.Log;
import main.java.utils.ORPipelineFactory;
import main.java.utils.Utils;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
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
				CircuitMap.addReceivingCircuit(circuit.getCircuitId(), circuit.getNodeName());
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
			}else if(command instanceof RegisterSuccess){
				RegisterSuccess rs = (RegisterSuccess) command;
				NodeList.addAll(rs.getNodeList());
				Log.db("received node list "+rs.toString());
			}else if(command instanceof Forward){
				forward(e.getChannel(),(Forward) command);
			}else{
				Log.e("received an unexpected command "+command.getCommandType());
				e.getChannel().close();
			}
		}else{
			Log.e("an error occurred while decoding ["+command.getError()+"]");
			ORError error = new ORError();
			error.setMessage(command.getError());
			e.getChannel().write(error).addListener(new ChannelFutureListener(){
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					future.getChannel().close();
				}
			});
		}
	}
	private static final void forward(final Channel returnChannel, final Forward f){
		ClientBootstrap cb = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),Executors.newCachedThreadPool()));
		final Node node = CircuitMap.getForwardingNode(CircuitMap.getForwardingCircuit(f.getCircuitId()));
		cb.setPipelineFactory(new ORPipelineFactory(new SimpleChannelHandler(){

			@Override
			public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
				ORCommand command = (ORCommand) e.getMessage();
				if(command.isOk()){
					Reverse r = new Reverse();
					Log.db("received result from "+node.getNodeName()+". Wrapping in a reverse and sending it back to the forwarder");
					r.setData(command.encode());
					r.setCircuitId(f.getCircuitId());
					returnChannel.write(r).addListener(new ChannelFutureListener(){
						@Override
						public void operationComplete(ChannelFuture future) throws Exception {
							future.getChannel().close();
						}
					});
				}else{
					ORError error = new ORError();
					error.setMessage(command.getError());
					returnChannel.write(error).addListener(new ChannelFutureListener(){
						@Override
						public void operationComplete(ChannelFuture future) throws Exception {
							future.getChannel().close();
						}
					});
				}
			}

			@Override
			public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
				ChannelBuffer cb = ChannelBuffers.buffer(f.getData().length);
				cb.writeBytes(f.getData());
				e.getChannel().write(cb).addListener(new ChannelFutureListener(){
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						Log.db("forwarded data to "+node.getNodeName());
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
	private static final void extendOtherCircuit(final Channel returnChannel, final UUID srcCircuitId){
		ClientBootstrap cb = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),Executors.newCachedThreadPool()));
		ArrayList<Node> nodeList = NodeList.getAll();
		String nodeName = CircuitMap.getReceivingNodeName(srcCircuitId);
		//first remove the source node from the list of nodes to extend to
		for(int i = 0; i < nodeList.size(); i++){
			if(nodeList.get(i).getNodeName().equals(nodeName)){
				nodeList.remove(i);
				break;
			}
		}
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
						CircuitMap.setLink(srcCircuitId, circuitId);
						ExtendCircuitAck ack = new ExtendCircuitAck();
						ack.setCircuitId(srcCircuitId);
						ack.setNextCircuitId(circuitId);
						returnChannel.write(ack).addListener(new ChannelFutureListener(){
							@Override
							public void operationComplete(ChannelFuture future) throws Exception {
								future.getChannel().close();
							}
						});
						Log.db("new circuit created with node "+node.getNodeName()+" to extend "+CircuitMap.getReceivingNodeName(srcCircuitId));
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
				circuit.setNodeName(GlobalVars.nodeName());
				e.getChannel().write(circuit).addListener(new ChannelFutureListener(){
					@Override
					public void operationComplete(ChannelFuture arg0) throws Exception {
						Log.db("requested new circuit with node "+node.getNodeName()+" to extend "+CircuitMap.getReceivingNodeName(srcCircuitId));
					}
				});
			}

			@Override
			public void exceptionCaught(ChannelHandlerContext ctx,
					ExceptionEvent e) throws Exception {
				e.getChannel().close();
				e.getCause().printStackTrace();
				//if we were unable to connect to the given node, assume the node is no longer reachable and remove it from the list of nodes
				if(e.getCause() instanceof java.net.ConnectException){
					NodeList.removeNode(node);
				}
			}
		}));
		cb.connect(node.getAddr());
	}
}
