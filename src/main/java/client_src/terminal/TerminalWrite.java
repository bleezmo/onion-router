package main.java.client_src.terminal;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;

public class TerminalWrite {
	public static final ChannelFuture write(Channel ch, String data){
		return write(ch,data.getBytes());
	}
	public static final ChannelFuture write(Channel ch, byte[] data){
		ChannelBuffer cb = ChannelBuffers.buffer(data.length);
		cb.writeBytes(data);
		return ch.write(cb);
	}
}
