package main.java.actions;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Executors;

import main.java.client_src.Circuit;
import main.java.client_src.Node;
import main.java.client_src.terminal.TerminalWrite;
import main.java.commands.ExtendCircuit;
import main.java.commands.ExtendCircuitAck;
import main.java.commands.NewCircuit;
import main.java.commands.NewCircuitAck;
import main.java.commands.ORCommand;
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

public class ExtendMyCircuit {
	/**
	 * @param terminalChannel - required to give feedback on the result
	 */
	public static final void run(final Channel terminalChannel){
		ClientBootstrap cb = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),Executors.newCachedThreadPool()));
//		ArrayList<Node> nodeList = NodeList.getAll();
//		int nodeIndex = (int) Math.rint((Math.random()*nodeList.size()));
//		final Node node = nodeList.get(nodeIndex);
//		final UUID circuitId = UUID.randomUUID();
		cb.setPipelineFactory(new ORPipelineFactory(new SimpleChannelHandler(){

			@Override
			public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
				ORCommand command = (ORCommand) e.getMessage();
				if(command.isOk()){
					if(command instanceof ExtendCircuitAck){
						ExtendCircuitAck ack = (ExtendCircuitAck) command;
						MyCircuit.extendCircuit(ack.getNextCircuitId());
						TerminalWrite.write(terminalChannel, "circuit successfully extended\n>");
						Log.db("circuit successfully extended");
					}else{
						TerminalWrite.write(terminalChannel, "received an unexpected command ["+command+"]\n>");
					}
				}else{
					TerminalWrite.write(terminalChannel,"an error occured when trying to create a new circuit ["+command.getError()+"]\n>");
				}
			}

			@Override
			public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
				ExtendCircuit ec = new ExtendCircuit();
				ec.setCircuitId(MyCircuit.get(0));
				e.getChannel().write(ec).addListener(new ChannelFutureListener(){
					@Override
					public void operationComplete(ChannelFuture arg0) throws Exception {
						Log.db("sent request to extend circuit");
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
		cb.connect(MyCircuit.getEntryNode().getAddr());
	}
}
