import java.util.Date;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;


public class TimeDecoder extends FrameDecoder{

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel ch,
			ChannelBuffer cb) throws Exception {
		if(cb.readableBytes() < 4) return null;
		else return new UnixTime(cb.readInt());
	}
	
	public static class UnixTime{
		private final int value;
		public UnixTime(int value){
			this.value = value;
		}
		public int getValue(){
			return value;
		}
		public String toString(){
			return new Date(value*1000L).toString();
		}
	}
}
