package main.java.commands;

import main.java.utils.Log;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class ORCoder{
	public static class ORDecoder extends FrameDecoder{
		@Override
		protected ORCommand decode(ChannelHandlerContext ctx, Channel ch, ChannelBuffer buffer) throws Exception{
			if(buffer.readable()){
				byte command = buffer.readByte();
				return CommandType.getORCommand(command).decode(ctx, ch, buffer);
			}else return null;
		}	
	}
	public static class OREncoder extends SimpleChannelHandler{
		@Override
		public void writeRequested(ChannelHandlerContext ctx, MessageEvent e)
				throws Exception {
			Object obj = e.getMessage();
			if(obj instanceof ORCommand){
				ORCommand command = (ORCommand) obj;
				byte[] data = command.encode();
				ChannelBuffer cb = ChannelBuffers.buffer(data.length);
				cb.writeBytes(data);
				Channels.write(ctx, e.getFuture(), cb);
			}
			
		}

		
	}
}

