package main.java.commands;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import main.java.globals.NodeList;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

import main.java.utils.Log;

/**
 * all REGISTER_SUCCESS commands have the same format
 * ********************************************************************
 * | message size | node1 size | node1 name | node1 host | node1 port |
 * | ----------   | ---------- | ---------- |----------  | ---------- |....
 * | 4 bytes      | 1 byte     | size - 6   |4 bytes     | 2 bytes    |
 * *******************************************************************
 * 
 * @author josh
 *
 */
public class RegisterSuccess extends ORCommand{
	private ArrayList<NodeList.Node> nodeList = new ArrayList<NodeList.Node>();

	public ArrayList<NodeList.Node> getNodeList() {
		return nodeList;
	}

	public void setNodeList(ArrayList<NodeList.Node> nodeList) {
		if(openWrite()){
			this.nodeList = nodeList;
		}
	}
	private static final NodeList.Node decodeNode(ChannelBuffer buffer){
		if(buffer.readableBytes() < 1) return null;
		byte nodeSize = buffer.readByte();
		int nameSize = nodeSize - 6;
		byte[] name = new byte[nameSize];
		if(buffer.readableBytes() < nameSize) return null;
		buffer.readBytes(name);
		byte[] addr = new byte[4];
		if(buffer.readableBytes() < 4) return null;
		buffer.readBytes(addr);
		if(buffer.readableBytes() < 2) return null;
		int port = buffer.readShort();
		try {
			return new NodeList.Node(new String(name), new InetSocketAddress(InetAddress.getByAddress(addr),port));
		} catch (UnknownHostException e) {
			Log.e("bad host address: "+e.getMessage());
			return null;
		}
	}
	private static final byte[] encodeNode(NodeList.Node node){
		byte[] name = node.getNodeName().getBytes();
		byte[] addr = node.getAddr().getAddress().getAddress();
		int port = node.getAddr().getPort();
		ByteBuffer bb = ByteBuffer.allocate(name.length+6);
		bb.put((byte) (name.length + 6));
		bb.put(name);
		bb.put(addr);
		bb.putShort((short) port);
		return bb.array();
	}
	@Override
	protected void ORDecode(ChannelHandlerContext ctx, Channel ch, ChannelBuffer buffer){
		if(buffer.readableBytes() < 4) return;
		int messageSize = buffer.readInt();
		if(buffer.readableBytes() < messageSize) return;
		while(buffer.readable()){
			NodeList.Node node = decodeNode(buffer);
			if(node != null) nodeList.add(node);
			else Log.f("bad node");
		}
		done();
	}

	@Override
	public byte[] encode() {
		ArrayList<byte[]> nodes = new ArrayList<byte[]>();
		int messageSize = 0;
		for(int i = 0; i < nodeList.size(); i++){
			byte[] node = encodeNode(nodeList.get(i));
			nodes.add(node);
			messageSize = messageSize + node.length;
		}
		ByteBuffer bb = ByteBuffer.allocate(messageSize + 4 + 1);
		bb.put(CommandType.REGISTER_SUCCESS);
		bb.putInt(messageSize);
		for(int i = 0; i < nodes.size(); i++){
			bb.put(nodes.get(i));
		}
		return bb.array();
	}

	@Override
	public byte getCommandType() {
		return CommandType.REGISTER_SUCCESS;
	}
	
	@Override
	public String toString(){
		String message = "[";
		for(int i = 0; i < nodeList.size(); i++){
			if(i > 0) message += ", ";
			message += 
					"{"+nodeList.get(i).getNodeName()+
					" "+nodeList.get(i).getAddr().getHostName()+
					" "+nodeList.get(i).getAddr().getPort()+"}";
		}
		message += "]";
		return message;
	}
}
