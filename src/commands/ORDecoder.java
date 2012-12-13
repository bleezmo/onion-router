package commands;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import utils.Log;

public class ORDecoder extends FrameDecoder{
	@Override
	protected ORCommand decode(ChannelHandlerContext ctx, Channel ch, ChannelBuffer buffer) throws Exception{
		if(buffer.readable()){
			byte command = buffer.readByte();
			return CommandType.getORCommand(command).decode(ctx, ch, buffer);
		}else return null;
	}	
}
