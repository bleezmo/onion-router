package commands;

import org.jboss.netty.buffer.ChannelBuffer;

public class Register extends ORCommand{

	@Override
	public byte getCommand() {
		return ORCommands.REGISTER;
	}

	@Override
	protected void decode(ChannelBuffer buffer) {
		
	}

	@Override
	public ChannelBuffer getResult() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
