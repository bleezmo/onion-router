import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import commands.ORCommand;
import commands.ORCommands;


public class ORDecoder extends FrameDecoder{

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel ch,
			ChannelBuffer buffer) throws Exception {
		return ORCommands.decode(ctx, ch, buffer);
	}
	
}
