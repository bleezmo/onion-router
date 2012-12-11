package commands;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

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
				NodeList.add(new InetSocketAddress(InetAddress.getByAddress(addr),port));
			} catch (UnknownHostException e) {
				Log.e("bad address: "+e.getMessage());
			}
		}
		done();
	}
	
}
