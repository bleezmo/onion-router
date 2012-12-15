package main.java.commands;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.UUID;

import main.java.client_src.Node;
import main.java.utils.Log;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;


public class Request extends ORCommand{

	/* ignore the first byte as it has already been read
	 * (non-Javadoc)
	 * @see commands.ORCommand#ORDecode(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.Channel, org.jboss.netty.buffer.ChannelBuffer)
	 */
	@Override
	protected void ORDecode(ChannelHandlerContext ctx, Channel ch, ChannelBuffer buffer){

		done();
	}

	@Override
	public byte[] encode() {
		return null;
	}

	@Override
	public byte getCommandType() {
		return CommandType.REQUEST;
	}
	@Override
	public ORCommand decode(byte[] data) {

		return this;
	}
}
