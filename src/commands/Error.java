package commands;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class Error extends ORCommand{
	
	public static final byte UNREADABLE = 0;
	public static final byte UNKNOWN_COMMAND = 1;
	
	private byte error;
	public Error(byte error){
		this.error = error;
	}
	
	@Override
	public byte getCommand() {
		return ORCommands.ERROR;
	}

	public byte getError() {
		return error;
	}

	@Override
	public ChannelBuffer getResult() {
		ChannelBuffer wb = ChannelBuffers.buffer(1);
		wb.writeByte(error);
		return wb;
	}

}
