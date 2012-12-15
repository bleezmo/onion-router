package main.java.actions;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Executors;

import main.java.client_src.Circuit;
import main.java.client_src.Node;
import main.java.client_src.terminal.TerminalWrite;
import main.java.commands.ExtendCircuit;
import main.java.commands.ExtendCircuitAck;
import main.java.commands.Forward;
import main.java.commands.NewCircuit;
import main.java.commands.NewCircuitAck;
import main.java.commands.ORCommand;
import main.java.commands.ORError;
import main.java.commands.Reverse;
import main.java.globals.MyCircuit;
import main.java.globals.NodeList;
import main.java.utils.Log;
import main.java.utils.ORPipelineFactory;
import main.java.utils.Utils;

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
		cb.setPipelineFactory(new ORPipelineFactory(new SimpleChannelHandler(){

			@Override
			public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
				ORCommand command = (ORCommand) e.getMessage();
				if(command.isOk()){
					if(command instanceof ExtendCircuitAck){
						ExtendCircuitAck ack = (ExtendCircuitAck) command;
						MyCircuit.extendCircuit(ack.getNextCircuitId());
						TerminalWrite.write(terminalChannel, "circuit successfully extended. Circuit map is now: "+MyCircuit.buildString()+"\n>");
						Log.db("circuit successfully extended. Circuit map is now: "+MyCircuit.buildString());
					}else if(command instanceof Reverse){
						ORCommand inner = Utils.unravel((Reverse) command);
						if(inner instanceof ExtendCircuitAck){
							ExtendCircuitAck ack = (ExtendCircuitAck) inner;
							MyCircuit.extendCircuit(ack.getNextCircuitId());
							TerminalWrite.write(terminalChannel, "circuit successfully extended. Circuit map is now: "+MyCircuit.buildString()+"\n>");
							Log.db("circuit successfully extended. Circuit map is now: "+MyCircuit.buildString());
						}
					}else if(command instanceof ORError){
						ORError error = (ORError) command;
						TerminalWrite.write(terminalChannel, "an error occurred ["+error.getMessage()+"]");
					}else{
						TerminalWrite.write(terminalChannel, "received an unexpected command ["+command+"]\n>");
					}
				}else{
					TerminalWrite.write(terminalChannel,"an error occured when trying to create a new circuit ["+command.getError()+"]\n>");
				}
			}

			@Override
			public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
				ORCommand command;
				if(MyCircuit.getSize() > 1){
					ExtendCircuit ec = new ExtendCircuit();
					ec.setCircuitId(MyCircuit.get(MyCircuit.getSize()-1));
					int index = MyCircuit.getSize()-2;
					Forward forward = new Forward();
					forward.setCircuitId(MyCircuit.get(index));
					forward.setData(ec.encode());
					index--;
					while(index >= 0){
						UUID circuitId = MyCircuit.get(index);
						Forward f = new Forward();
						f.setCircuitId(circuitId);
						f.setData(forward.encode());
						forward = f;
						index--;
					}
					command = forward;
				}else{
					ExtendCircuit ec = new ExtendCircuit();
					ec.setCircuitId(MyCircuit.get(0));
					command = ec;
				}
				e.getChannel().write(command).addListener(new ChannelFutureListener(){
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
