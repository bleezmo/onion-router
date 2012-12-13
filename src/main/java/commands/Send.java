package main.java.commands;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;


/**
 * all SEND commands have the following format in common
 * *********************************************************
 * |  size    | circuit id | payload                       |
 * | -------- | ---------- | ----------------------------- | 
 * |  4 bytes | 16 bytes   | variable (determined by size) |
 * *********************************************************
 * @author josh
 *
 */
public class Send extends ORCommand{
	private int size;
	private byte[] payload;
	private UUID circuitId;
	
	public UUID getCircuitId() {
		return circuitId;
	}

	public void setCircuitId(UUID circuitId) {
		if(openWrite()){
			this.circuitId = circuitId;
		}
	}

	public void setSize(int size) {
		if(openWrite()){
			this.size = size;
		}
	}

	public void setPayload(byte[] payload) {
		if(openWrite()){
			this.payload = payload;
		}
	}

	public int getSize() {
		return size;
	}

	public byte[] getPayload() {
		return payload;
	}

	/* ignore the first byte as it has already been read
	 * (non-Javadoc)
	 * @see commands.ORCommand#ORDecode(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.Channel, org.jboss.netty.buffer.ChannelBuffer)
	 */
	@Override
	protected void ORDecode(ChannelHandlerContext ctx, Channel ch, ChannelBuffer buffer){
		//read size
		if(buffer.readableBytes() < 4) return;
		size = buffer.readInt();
		//read circuit id
		if(buffer.readableBytes() < 16) return;
		long mostSignificant = buffer.readLong();
		long leastSignificant = buffer.readLong();
		circuitId = new UUID(mostSignificant, leastSignificant);
		//read payload
		if(buffer.readableBytes() < size) return;
		payload = new byte[size];
		buffer.readBytes(payload);
		done();
	}

	@Override
	public byte[] encode() {
		return null;
	}

	@Override
	public byte getCommandType() {
		return CommandType.SEND;
	}
}
