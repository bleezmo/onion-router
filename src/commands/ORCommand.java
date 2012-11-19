package commands;

import org.jboss.netty.buffer.ChannelBuffer;

public abstract class ORCommand {
	public abstract byte getCommand();
	protected void decode(ChannelBuffer buffer){}
	public abstract ChannelBuffer getResult();
}
