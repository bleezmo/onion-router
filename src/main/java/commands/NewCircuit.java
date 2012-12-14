package main.java.commands;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;


/**
 * all NEW_CIRCUIT commands have the same format
 * ***************************************
 * | circuit id | name size | node name  |
 * | ---------- | --------- | ---------- |
 * | 16 bytes   | 1 byte	| size bytes |
 * ***************************************
 * @author josh
 *
 */
public class NewCircuit extends ORCommand{
	private UUID circuitId;
	
	public UUID getCircuitId() {
		return circuitId;
	}
	public void setCircuitId(UUID circuitId){
		if(openWrite()){
			this.circuitId = circuitId;
		}
	}

	@Override
	protected void ORDecode(ChannelHandlerContext ctx, Channel ch, ChannelBuffer buffer){
		//read circuit id
		if(buffer.readableBytes() < 16) return;
		long mostSignificant = buffer.readLong();
		long leastSignificant = buffer.readLong();
		circuitId = new UUID(mostSignificant, leastSignificant);
		done();
	}

	@Override
	public byte[] encode() {
		ByteBuffer bb = ByteBuffer.allocate(16+1);
		bb.put(CommandType.NEW_CIRCUIT);
		bb.putLong(circuitId.getMostSignificantBits());
		bb.putLong(circuitId.getLeastSignificantBits());
		return bb.array();
	}
	@Override
	public byte getCommandType() {
		return CommandType.NEW_CIRCUIT;
	}
	
}
