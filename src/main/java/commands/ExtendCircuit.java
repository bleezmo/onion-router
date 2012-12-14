package main.java.commands;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;


/**
 * the EXTEND_CIRCUIT command format is as follows:
 * **************
 * | circuit id |
 * | ---------- |
 * | 16 bytes   |
 * **************
 * @author josh
 *
 */
public class ExtendCircuit extends ORCommand{
	private UUID circuitId;
	
	public UUID getCircuitId() {
		return circuitId;
	}
	
	public void setCircuitId(UUID circuitId) {
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
		ByteBuffer bb = ByteBuffer.allocate(1+16);
		bb.put(CommandType.EXTEND_CIRCUIT);
		bb.putLong(circuitId.getMostSignificantBits());
		bb.putLong(circuitId.getLeastSignificantBits());
		return bb.array();
	}

	@Override
	public byte getCommandType() {
		return CommandType.EXTEND_CIRCUIT;
	}

}
