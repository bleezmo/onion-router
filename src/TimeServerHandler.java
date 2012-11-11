import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.handler.codec.frame.FrameDecoder;


public class TimeServerHandler extends SimpleChannelHandler{
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e){
		Channel ch = e.getChannel();
		ChannelBuffer time = ChannelBuffers.buffer(4);
		time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));
		
		ChannelFuture f = ch.write(time);
		
		f.addListener(new ChannelFutureListener(){
			public void operationComplete(ChannelFuture future){
				Channel ch = future.getChannel();
				ch.close();
			}
		});
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		String message = (String) e.getMessage();
		if(message.equals("exit")) OnionRouter.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e){
		e.getCause().printStackTrace();
		e.getChannel().close();
	}
	
	public static class ShutdownDecoder extends FrameDecoder{

		@Override
		protected Object decode(ChannelHandlerContext ctx, Channel ch,
				ChannelBuffer buffer) throws Exception {
			
			if(buffer.readableBytes() < 4) return null;
			else{
				byte[] barray = new byte[4];
				buffer.readBytes(barray);
				return new String(barray);
			}
		}
		
	}
}
