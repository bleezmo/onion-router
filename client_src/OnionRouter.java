
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
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
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.ChannelGroupFutureListener;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import commands.ORCommands;


public class OnionRouter {
    private static final ChannelGroup allChannels = new DefaultChannelGroup("onion-router");
	private static volatile boolean doShutdown = false;
	private static volatile ChannelFactory serverFactory;
	private static volatile ChannelFactory clientFactory;
    /**
	 * @param args
	 */
	public static void main(String[] args) {
		Executor executor = Executors.newCachedThreadPool();
		serverFactory = new NioServerSocketChannelFactory(executor,executor);
		clientFactory = new NioServerSocketChannelFactory(executor,executor);
		ServerBootstrap sb = new ServerBootstrap(serverFactory);
		sb.setPipelineFactory(new ChannelPipelineFactory(){
			@Override
			public ChannelPipeline getPipeline(){
				return Channels.pipeline(
						new FrameDecoder(){
							@Override
							protected Object decode(ChannelHandlerContext ctx,Channel ch, ChannelBuffer buffer)throws Exception {
								return ORCommands.decode(ctx, ch, buffer);
							}
						}
						);
			}
		});
		sb.setOption("child.tcpNoDelay", true);
		sb.setOption("child.keepAlive", true);
		Channel ch = sb.bind(new InetSocketAddress(8080));
		allChannels.add(ch);
	}
	public static final void close(){
		ChannelGroupFuture future = allChannels.close();
		future.addListener(new ChannelGroupFutureListener(){
			@Override
			public void operationComplete(ChannelGroupFuture future)
					throws Exception {
				doShutdown = true;
			}
		});
		serverFactory.releaseExternalResources();
	}
	public static ChannelFactory getClientFactory() {
		return clientFactory;
	}
}
