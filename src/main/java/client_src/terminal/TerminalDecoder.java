package main.java.client_src.terminal;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class TerminalDecoder extends FrameDecoder{
	
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
}
