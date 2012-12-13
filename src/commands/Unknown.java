package commands;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

public class Unknown extends ORCommand{

	@Override
	public byte[] encode() {
		return null;
	}

	@Override
	public byte getCommandType() {
		return CommandType.UNKNOWN;
	}

	@Override
	protected void ORDecode(ChannelHandlerContext arg0, Channel arg1,
			ChannelBuffer arg2) {
		done();
	}

}
