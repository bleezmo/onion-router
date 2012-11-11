
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;


public class DiscardServerHandler extends SimpleChannelHandler{
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e){
		ChannelBuffer buf = (ChannelBuffer) e.getMessage();
		System.out.print("message received: ");
		String m = "";
		while(buf.readable()){
			char ch = (char) buf.readByte();
			System.out.print(ch);
			System.out.flush();
			m = m + ch;
		}
		System.out.print("\n");
		buf.clear();
		buf.writeBytes(m.getBytes());
		e.getChannel().write(buf).addListener(new ChannelFutureListener(){
			@Override
			public void operationComplete(ChannelFuture cf) throws Exception {

			}
		});
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e){
		e.getCause().printStackTrace();
		Channel ch = e.getChannel();
		ch.close();
	}
}
