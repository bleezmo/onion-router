import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.ChannelGroupFutureListener;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;


public class OnionRouter {
    private static final ChannelGroup allChannels = new DefaultChannelGroup("time-server");
	private static volatile boolean doShutdown = false;
    /**
	 * @param args
	 */
	public static void main(String[] args) {
		ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),Executors.newCachedThreadPool());
		ServerBootstrap bootstrap = new ServerBootstrap(factory);
		bootstrap.setPipelineFactory(new ChannelPipelineFactory(){
			@Override
			public ChannelPipeline getPipeline(){
				return Channels.pipeline(
						new TimeServerHandler.ShutdownDecoder(), 
						new TimeServerHandler());
			}
		});
		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.keepAlive", true);
		Channel ch = bootstrap.bind(new InetSocketAddress(8080));
		allChannels.add(ch);
		while(!doShutdown){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}
		}
		factory.releaseExternalResources();
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
	}
}
