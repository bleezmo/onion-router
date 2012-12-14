package main.java.actions;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Executors;

import main.java.client_src.Circuit;
import main.java.client_src.Node;
import main.java.commands.NewCircuit;
import main.java.commands.NewCircuitAck;
import main.java.commands.ORCommand;
import main.java.commands.Register;
import main.java.commands.RegisterSuccess;
import main.java.globals.CircuitMap;
import main.java.globals.GlobalVars;
import main.java.globals.MyCircuit;
import main.java.globals.NodeList;
import main.java.utils.ChannelWrite;
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

public class CreateCircuit {
	/**
	 * @param terminalChannel - required to give feedback on the result
	 */
	public static final void run(final Channel terminalChannel){
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
						MyCircuit.newCircuit(new Circuit(circuitId,node));
						ChannelWrite.write(terminalChannel, "new circuit successfully created\n>");
						Log.db("new circuit created with entry node: "+node.getNodeName());
					}else{
						ChannelWrite.write(terminalChannel, "received an unexpected command ["+command+"]\n>");
					}
				}else{
					ChannelWrite.write(terminalChannel,"an error occured when trying to create a new circuit ["+command.getError()+"]\n>");
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
						Log.db("requested new circuit with entry node: "+node.getNodeName());
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
