package commands;

import java.util.UUID;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

/**
 * the EXTEND_CIRCUIT_ACK command format is as follows:
 * ********************************
 * | circuit id | next circuit id |
 * | ---------- | --------------- |
 * | 16 bytes   | 16 bytes		  |
 * ********************************
 * @author josh
 *
 */
public class ExtendCircuitAck extends ORCommand{
	private UUID circuitId;
	private UUID nextCircuitId;
	
	public UUID getNextCircuitId() {
		return nextCircuitId;
	}

	public UUID getCircuitId() {
		return circuitId;
	}
	
	@Override
	protected void ORDecode(ChannelHandlerContext ctx, Channel ch,
			ChannelBuffer buffer) {
		//read circuit id
		if(buffer.readableBytes() < 16) return;
		circuitId = toUUID(buffer.readLong(),buffer.readLong());
		//read next circuit id
		if(buffer.readableBytes() < 16) return;
		nextCircuitId = toUUID(buffer.readLong(),buffer.readLong());
		done();
	}
	private static final UUID toUUID(long mostSignificant, long leastSignificant){
		return new UUID(mostSignificant, leastSignificant);
	}
}
