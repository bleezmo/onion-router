package commands;

import org.jboss.netty.buffer.ChannelBuffer;

public class SendAck extends ORCommand{

	@Override
	public byte getCommand() {
		return ORCommands.SEND_ACK;
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
