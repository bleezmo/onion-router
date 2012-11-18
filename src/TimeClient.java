
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;


public class TimeClient {

	public static void main(String[] args) throws Exception {
		String host = "localhost";
		int port = 8080;

		ChannelFactory factory =
				new NioClientSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool());

		ClientBootstrap bootstrap = new ClientBootstrap(factory);

		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() {
				return Channels.pipeline(new TimeDecoder(), new TimeClientHandler());
			}
		});

		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("keepAlive", true);
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
		future.getChannel().getCloseFuture().awaitUninterruptibly();
		factory.releaseExternalResources();
	}
	
	public static class TimeClientHandler extends SimpleChannelHandler {

		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
			TimeDecoder.UnixTime ut = (TimeDecoder.UnixTime) e.getMessage();
			System.out.println(ut.toString());
			ChannelBuffer buf = ChannelBuffers.buffer(4);
			buf.writeBytes(new String("exit").getBytes());
			final Channel ch = e.getChannel();
			ch.write(buf).addListener(new ChannelFutureListener(){
				@Override
				public void operationComplete(ChannelFuture arg0)
						throws Exception {
					System.out.println("shutdown server");
					ch.close();
				}
			});
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
			e.getCause().printStackTrace();
			e.getChannel().close();
		}
	}
	
}
