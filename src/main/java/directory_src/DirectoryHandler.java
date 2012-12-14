package main.java.directory_src;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.Executors;

import main.java.globals.GlobalVars;
import main.java.globals.NodeList;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import main.java.utils.Log;
import main.java.utils.ORPipelineFactory;

import main.java.client_src.Node;
import main.java.client_src.terminal.TerminalWrite;
import main.java.commands.ORCommand;
import main.java.commands.Register;
import main.java.commands.RegisterSuccess;

public class DirectoryHandler extends SimpleChannelHandler{

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		ORCommand orc = (ORCommand) e.getMessage();
		if(orc.isOk()){
			if(orc.getClass() == Register.class){
				final Register r = (Register) orc;
				final Channel ch = e.getChannel();
				RegisterSuccess rs = new RegisterSuccess();
				ArrayList<Node> nodeList = NodeList.getAll();
				rs.setNodeList(nodeList);
				byte[] data= rs.encode();
				ChannelBuffer cb = ChannelBuffers.buffer(data.length);
				cb.writeBytes(data);
				ch.write(rs).addListener(new ChannelFutureListener(){
					@Override
					public void operationComplete(ChannelFuture cf) throws Exception {
						InetSocketAddress sa = (InetSocketAddress) ch.getRemoteAddress();
						Log.db("adding node: "+r.getNodeName()+" "+sa.getAddress().getHostAddress()+" "+r.getServerPort());
						ArrayList<Node> nodeList = NodeList.getAll();
						NodeList.add(
								new Node(r.getNodeName(), new InetSocketAddress(sa.getAddress(), r.getServerPort()))
								); //use the server port given
						ch.close();
						//broadcast node list to other nodes
						for(int i = 0; i < nodeList.size(); i++){
							broadcastNodeList(nodeList.get(i));
						}
					}
				});
			}else {
				Log.w("received unexpected command: "+orc.getCommandType());
				e.getChannel().close();
			}
		}else {
			Log.f(orc.getError());
			e.getChannel().close();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		e.getChannel().close();
		e.getCause().printStackTrace();
	}
	
	private static final void broadcastNodeList(final Node node){
		ClientBootstrap cb = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),Executors.newCachedThreadPool()));
		cb.setPipelineFactory(new ORPipelineFactory(new SimpleChannelHandler(){

			@Override
			public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
				RegisterSuccess rs = new RegisterSuccess();
				ArrayList<Node> nodeList = NodeList.getAll();
				rs.setNodeList(nodeList);
				e.getChannel().write(rs).addListener(new ChannelFutureListener(){
					@Override
					public void operationComplete(ChannelFuture cf) throws Exception {
						cf.getChannel().close();
					}
				});
			}

			@Override
			public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
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
