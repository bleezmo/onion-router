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
	private String nodeName;
	
	public UUID getCircuitId() {
		return circuitId;
	}
	public void setCircuitId(UUID circuitId){
		if(openWrite()) this.circuitId = circuitId;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		if(openWrite()) this.nodeName = nodeName;
	}
	@Override
	protected void ORDecode(ChannelHandlerContext ctx, Channel ch, ChannelBuffer buffer){
		//read circuit id
		if(buffer.readableBytes() < 16) return;
		long mostSignificant = buffer.readLong();
		long leastSignificant = buffer.readLong();
		circuitId = new UUID(mostSignificant, leastSignificant);
		if(buffer.readableBytes() < 1) return;
		int size = buffer.readByte();
		if(buffer.readableBytes() < size) return;
		byte[] name = new byte[size];
		buffer.readBytes(name);
		nodeName = new String(name);
		done();
	}

	@Override
	public byte[] encode() {
		byte[] name = nodeName.getBytes();
		ByteBuffer bb = ByteBuffer.allocate(1+16+1+name.length);
		bb.put(CommandType.NEW_CIRCUIT);
		bb.putLong(circuitId.getMostSignificantBits());
		bb.putLong(circuitId.getLeastSignificantBits());
		bb.put((byte) name.length);
		bb.put(name);
		return bb.array();
	}
	@Override
	public byte getCommandType() {
		return CommandType.NEW_CIRCUIT;
	}
	
}
