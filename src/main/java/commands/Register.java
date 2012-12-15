package main.java.commands;

import java.nio.ByteBuffer;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

/**
 * Register command has the following format:
 * **************************************
 * | message size | node name | port    |
 * | ------------ | --------- | ------- |
 * | 1 byte       | size - 2  | 2 bytes |
 * **************************************
 * @author josh
 *
 */
public class Register extends ORCommand{
	private String nodeName;
	private int serverPort;
	
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		if(openWrite())this.nodeName = nodeName;
	}
	public int getServerPort() {
		return serverPort;
	}
	public void setServerPort(int serverPort) {
		if(openWrite()) this.serverPort = serverPort;
	}

	@Override
	protected void ORDecode(ChannelHandlerContext ctx, Channel ch, ChannelBuffer buffer){
		if(buffer.readableBytes() < 1) return;
		int messageSize = buffer.readByte();
		if(buffer.readableBytes() < messageSize) return;
		byte[] name = new byte[messageSize - 2];
		buffer.readBytes(name);
		nodeName = new String(name);
		serverPort = buffer.readShort();
		done();
	}

	@Override
	public byte[] encode() {
		ByteBuffer bb = ByteBuffer.allocate(nodeName.getBytes().length+1+1+2);
		bb.put(CommandType.REGISTER);
		bb.put((byte) (nodeName.getBytes().length+2));
		bb.put(nodeName.getBytes());
		bb.putShort((short) serverPort);
		return bb.array();
	}

	@Override
	public byte getCommandType() {
		return CommandType.REGISTER;
	}
	@Override
	public ORCommand decode(byte[] data) {
		ByteBuffer bb = ByteBuffer.wrap(data);
		if(bb.get() != CommandType.REGISTER) throw new RuntimeException("wrong type");
		int size = bb.get();
		byte[] name = new byte[size-2];
		bb.get(name);
		nodeName = new String(name);
		serverPort = bb.getShort();
		return this;
	}
}
