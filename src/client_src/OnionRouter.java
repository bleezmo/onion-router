package client_src;

import globals.GlobalVars;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.ChannelGroupFutureListener;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import actions.ClientRegister;

import utils.ORPipelineFactory;

import commands.ORCommand;
import commands.ORDecoder;


public class OnionRouter {
    private static final ChannelGroup allChannels = new DefaultChannelGroup("onion-router");
	private static volatile boolean doShutdown = false;
    /**
	 * @param args
	 */
	public static void main(String[] args) {
		Executor executor = Executors.newCachedThreadPool();
		ChannelFactory serverFactory = new NioServerSocketChannelFactory(executor,executor);
		ChannelFactory terminalFactory = new NioServerSocketChannelFactory(executor,executor);
		
		ServerBootstrap sb = new ServerBootstrap(serverFactory);
		ServerBootstrap terminalBootstrap = new ServerBootstrap(terminalFactory);
		
		sb.setPipelineFactory(new ORPipelineFactory(new ServerHandler()));
		terminalBootstrap.setPipelineFactory(new ChannelPipelineFactory(){
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline();
			}
		});
		sb.setOption("child.tcpNoDelay", true);
		sb.setOption("child.keepAlive", true);
		Channel server = sb.bind(new InetSocketAddress(GlobalVars.serverPort));
		Channel terminal = terminalBootstrap.bind(new InetSocketAddress(GlobalVars.terminalPort));
		allChannels.add(server);
		allChannels.add(terminal);
		ClientRegister.run();
	}
}
