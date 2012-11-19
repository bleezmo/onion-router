package commands;

import org.jboss.netty.buffer.ChannelBuffer;

public class ProbeAck extends ORCommand{

	@Override
	public byte getCommand() {
		return ORCommands.PROBE_ACK;
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
