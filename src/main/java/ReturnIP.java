package main.java;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import main.java.directory_src.DirectoryHandler;
import main.java.globals.DbManager;
import main.java.globals.GlobalVars;
import main.java.utils.ORPipelineFactory;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class ReturnIP {
	public static void main(String[] args) {
		DbManager.get();
		GlobalVars.setVarTemplate(GlobalVars.templateDirectory);
		ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),Executors.newCachedThreadPool());
		ServerBootstrap bootstrap = new ServerBootstrap(factory);
		final FrameDecoder decoder = new FrameDecoder(){
			@Override
			protected Object decode(ChannelHandlerContext ctx, Channel ch, ChannelBuffer cb) throws Exception {
				cb.markReaderIndex();
				StringBuilder sb = new StringBuilder();
				while(cb.readable()){
					char c = (char) cb.readByte();
					if(c == ';'){
						return sb.toString();
					}else {
						sb.append(c);
					}
				}
				return null;
			}
		};
		bootstrap.setPipelineFactory(new ChannelPipelineFactory(){
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(decoder);
			}
		});
		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.keepAlive", true);
		bootstrap.bind(new InetSocketAddress(GlobalVars.directoryPort));
	}
	public static class PingHandler extends SimpleChannelHandler{

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
				throws Exception {
		}

		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
				throws Exception {
			
		}
		
	}
}
