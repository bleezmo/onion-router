package main.java.commands;

import java.nio.ByteBuffer;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

/**
 * ****************************
 * | message size N | message |
 * | -------------- | ------- |
 * | 2 bytes	    | N bytes |
 * ****************************
 * @author josh
 *
 */
public class Error extends ORCommand{
	private String message;

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		if(openWrite())this.message = message;
	}

	@Override
	public byte[] encode() {
		byte[] data = message.getBytes();
		ByteBuffer bb = ByteBuffer.allocate(data.length+2+1);
		bb.put(CommandType.ERROR);
		bb.put((byte) data.length);
		bb.put(data);
		return bb.array();
	}

	@Override
	public byte getCommandType() {
		return CommandType.ERROR;
	}

	@Override
	protected void ORDecode(ChannelHandlerContext ctx, Channel ch, ChannelBuffer buf) {
		if(buf.readableBytes() < 2) return;
		int size = buf.readShort();
		if(buf.readableBytes() < size) return;
		StringBuffer sb = new StringBuffer(size);
		for(int i = 0; i < size; i++){
			sb.append(buf.readByte());
		}
		message = sb.toString();
		done();
	}

}
