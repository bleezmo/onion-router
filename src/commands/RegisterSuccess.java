package commands;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

/**
 * all REGISTER_SUCCESS commands have the same format
 * ******************************************
 * | N # of nodes | node1 host | node1 port |
 * | ----------   | ---------- | ---------- |
 * | 4 bytes      | 4 bytes    | 2 bytes    |
 * ******************************************
 * @author josh
 *
 */
public class RegisterSuccess extends ORCommand{

	@Override
	protected void ORDecode(ChannelHandlerContext ctx, Channel ch, ChannelBuffer buffer){
		
	}
	
}
