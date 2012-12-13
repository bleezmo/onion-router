package commands;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import globals.NodeList;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

import utils.Log;

/**
 * all REGISTER_SUCCESS commands have the same format
 * ******************************************************************************************************
 * | N # of nodes | node1 host | node1 port | node2 host | node2 port |       | nodeN host | nodeN port |
 * | ----------   | ---------- | ---------- | ---------- | ---------- | ..... | ---------- | ---------- |
 * | 4 bytes      | 4 bytes    | 2 bytes    | 4 bytes    | 2 bytes    |       | 4 bytes    | 2 bytes    |
 * ******************************************************************************************************
 * 
 * @author josh
 *
 */
public class RegisterSuccess extends ORCommand{
	private ArrayList<InetSocketAddress> nodeList = new ArrayList<InetSocketAddress>();

	public ArrayList<InetSocketAddress> getNodeList() {
		return nodeList;
	}

	public void setNodeList(ArrayList<InetSocketAddress> nodeList) {
		if(openWrite()){
			this.nodeList = nodeList;
		}
	}

	@Override
	protected void ORDecode(ChannelHandlerContext ctx, Channel ch, ChannelBuffer buffer){
		if(buffer.readableBytes() < 4) return;
		int nodes = buffer.readInt();
		int bytesRemaining = nodes * 6;
		if(buffer.readableBytes() < bytesRemaining) return;
		for(int i = 0; i < nodes; i++){
			byte[] addr = new byte[4];
			buffer.readBytes(addr);
			int port = buffer.readShort();
			try {
				nodeList.add(new InetSocketAddress(InetAddress.getByAddress(addr),port));
			} catch (UnknownHostException e) {
				Log.e("bad address: "+e.getMessage());
			}
		}
		done();
	}

	@Override
	public byte[] encode() {
		int nodes = nodeList.size();
		int size = (nodes * 6) + 4 + 1;
		ByteBuffer bb = ByteBuffer.allocate(size);
		bb.put(CommandType.REGISTER_SUCCESS);
		bb.putInt(nodes);
		for(int i = 0; i < nodes; i++){
			InetSocketAddress node = nodeList.get(i);
			bb.put(node.getAddress().getAddress());
			bb.putShort((short) node.getPort());
		}
		return bb.array();
	}

	@Override
	public byte getCommandType() {
		return CommandType.REGISTER_SUCCESS;
	}
	
}
