package main.java.commands;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

/**
 * **************************************
 * |circuit id | data size | data       |
 * | --------- | --------- | ---------- |
 * | 16 bytes  | 4 bytes   | size bytes |
 * **************************************
 * @author josh
 *
 */
public class Reverse extends ORCommand{
	private byte[] data;
	private UUID circuitId;
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		if(openWrite()) this.data = data;
	}
	public UUID getCircuitId() {
		return circuitId;
	}
	public void setCircuitId(UUID circuitId) {
		if(openWrite())this.circuitId = circuitId;
	}
	@Override
	public byte[] encode() {
		ByteBuffer bb = ByteBuffer.allocate(1+16+4+data.length);
		bb.put(CommandType.REVERSE);
		bb.putLong(circuitId.getMostSignificantBits());
		bb.putLong(circuitId.getLeastSignificantBits());
		bb.putInt(data.length);
		bb.put(data);
		return bb.array();
	}

	@Override
	public byte getCommandType() {
		return CommandType.REVERSE;
	}

	@Override
	protected void ORDecode(ChannelHandlerContext ctx, Channel ch, ChannelBuffer buffer) {
		if(buffer.readableBytes() < 16) return;
		long msb = buffer.readLong();
		long lsb = buffer.readLong();
		circuitId = new UUID(msb,lsb);
		if(buffer.readableBytes() < 4) return;
		int size = buffer.readInt();
		if(buffer.readableBytes() < size) return;
		data = new byte[size];
		buffer.readBytes(data);
		done();
	}
	@Override
	public ORCommand decode(byte[] data) {
		ByteBuffer bb = ByteBuffer.wrap(data);
		if(bb.get() != CommandType.REVERSE) throw new RuntimeException("wrong type");
		long msb = bb.getLong();
		long lsb = bb.getLong();
		circuitId = new UUID(msb,lsb);
		int dataSize = bb.getInt();
		this.data = new byte[dataSize];
		bb.get(this.data);
		return this;
	}
}
