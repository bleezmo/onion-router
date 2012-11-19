package commands;

import org.jboss.netty.buffer.ChannelBuffer;

public class RegisterSuccess extends ORCommand{

	@Override
	public byte getCommand() {
		return ORCommands.REGISTER_SUCCESS;
	}

	@Override
	protected void decode(ChannelBuffer buffer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ChannelBuffer getResult() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
