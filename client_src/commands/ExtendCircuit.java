package commands;

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
	
	@Override
	protected void ORDecode(ChannelHandlerContext ctx, Channel ch, ChannelBuffer buffer){
		//read circuit id
		if(buffer.readableBytes() < 16) return;
		long mostSignificant = buffer.readLong();
		long leastSignificant = buffer.readLong();
		circuitId = new UUID(mostSignificant, leastSignificant);
		done();
	}

}
