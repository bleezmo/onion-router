package main.java.commands;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;


public class Response extends ORCommand{

	@Override
	protected void ORDecode(ChannelHandlerContext ctx, Channel ch, ChannelBuffer buffer){
		
	}

	@Override
	public byte[] encode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte getCommandType() {
		return CommandType.RESPONSE;
	}

	@Override
	public ORCommand decode(byte[] data) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
