package commands;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

import utils.Log;

/**
 * all SEND commands have the following format in common
 * *********************************************************************************************
 * |  size    | UUID      | destination host | destination port | payload                       |
 * | -------- | --------- | ---------------- | ---------------- | ----------------------------- | 
 * |  4 bytes | 16 bytes  | 4 bytes		  | 2 bytes			 | variable (determined by size) |
 * *********************************************************************************************
 * @author josh
 *
 */
public class Send extends ORCommand{
	private int size;
	private byte[] payload;
	private int destPort;
	private InetAddress destHost;
	private UUID id;
	
	public int getSize() {
		return size;
	}

	public byte[] getPayload() {
		return payload;
	}

	public int getdestPort() {
		return destPort;
	}

	public InetAddress getdestHost() {
		return destHost;
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
		//read id
		if(buffer.readableBytes() < 16) return;
		long mostSignificant = buffer.readLong();
		long leastSignificant = buffer.readLong();
		id = new UUID(mostSignificant, leastSignificant);
		//read host
		if(buffer.readableBytes() < 4) return;
		byte[] addr = new byte[4];
		buffer.readBytes(addr);
		try {
			destHost = InetAddress.getByAddress(addr);
		} catch (UnknownHostException e) {
			Log.f("bad host address: "+e.getMessage());
			setError("bad host address");
		}
		//read port
		if(buffer.readableBytes() < 2) return;
		destPort = buffer.readShort();
		if(buffer.readableBytes() < size) return;
		payload = new byte[size];
		buffer.readBytes(payload);
		done();
	}
}
