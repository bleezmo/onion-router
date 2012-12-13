package actions;

import globals.GlobalVars;
import globals.NodeList;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import commands.ORCommand;
import commands.Register;
import commands.RegisterSuccess;

import utils.Log;
import utils.ORPipelineFactory;

public class ClientRegister {
	public static final void run(){
		ClientBootstrap cb = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),Executors.newCachedThreadPool()));
		cb.setPipelineFactory(new ORPipelineFactory(new SimpleChannelHandler(){

			@Override
			public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
				ORCommand command = (ORCommand) e.getMessage();
				if(command.isOk()){
					if(command instanceof RegisterSuccess){
						RegisterSuccess rs = (RegisterSuccess) command;
						NodeList.addAll(rs.getNodeList());
					}else{
						Log.f("received the wrong command: "+command.getCommandType());
					}
				}else Log.f(command.getError());
				e.getChannel().close();
			}

			@Override
			public void channelConnected(ChannelHandlerContext ctx,
					ChannelStateEvent e) throws Exception {
				Register r = new Register();
				r.setServerPort(GlobalVars.serverPort);
				Channel ch = e.getChannel();
				ch.write(r);
			}
			
		}));
		cb.connect(new InetSocketAddress(GlobalVars.directoryHost,GlobalVars.directoryPort));
	}
}
