package main.java.directory_src;
import main.java.globals.DbManager;
import main.java.globals.GlobalVars;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import main.java.utils.ORPipelineFactory;

import main.java.commands.ORCommand;
import main.java.commands.ORCoder;
import main.java.commands.Register;


public class ORDirectory {
    /**
	 * @param args
	 */
	public static void main(String[] args) {
		DbManager.get();
		GlobalVars.setVarTemplate(GlobalVars.templateDirectory);
		ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),Executors.newCachedThreadPool());
		ServerBootstrap bootstrap = new ServerBootstrap(factory);
		bootstrap.setPipelineFactory(new ORPipelineFactory(new DirectoryHandler()));
		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.keepAlive", true);
		bootstrap.bind(new InetSocketAddress(GlobalVars.directoryPort));
	}
}
