package commands;

import java.nio.ByteBuffer;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

/**
 * Register command has the following format:
 * ***********
 * | port    |
 * | ------- |
 * | 2 bytes |
 * ***********
 * @author josh
 *
 */
public class Register extends ORCommand{
	int serverPort;
	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		if(openWrite()) this.serverPort = serverPort;
	}

	@Override
	protected void ORDecode(ChannelHandlerContext ctx, Channel ch, ChannelBuffer buffer){
		if(buffer.readableBytes() < 2) return;
		serverPort = buffer.readShort();
		done();
	}

	@Override
	public byte[] encode() {
		ByteBuffer bb = ByteBuffer.allocate(3);
		bb.put(CommandType.REGISTER);
		bb.putShort((short) serverPort);
		return bb.array();
	}

	@Override
	public byte getCommandType() {
		return CommandType.REGISTER;
	}
	
}
