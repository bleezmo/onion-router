package commands;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;


/**
 * all OR commands are prepended with a byte indicated the type of command
 * *******************************************************************************
 * command type | command content (including rest of command header if required) *
 * ------------ | -------------------------------------------------------------- *
 * 		1 byte	| variable number bytes depending on the command				 *
 * *******************************************************************************
 * @author josh
 *
 */
public class ORCommands{
	public static final byte ERROR = 0;
	public static final byte REGISTER = 1;
	public static final byte REGISTER_SUCCESS = 2;
	public static final byte PROBE = 3;
	public static final byte PROBE_ACK = 4;
	public static final byte SEND = 5;
	public static final byte SEND_ACK = 6;
	
	public static final Object decode(ChannelHandlerContext ctx, Channel ch, ChannelBuffer buffer) throws Exception{
		if(buffer.readable()){
			byte command = buffer.readByte();
			if(command == ORCommands.REGISTER){
				Register register = new Register();
				return register.decode(ctx, ch, buffer);
			}else if(command == ORCommands.REGISTER_SUCCESS){
				RegisterSuccess rs = new RegisterSuccess();
				return rs.decode(ctx, ch, buffer);
			}else if(command == ORCommands.PROBE){
				Probe probe = new Probe();
				return probe.decode(ctx, ch, buffer);
			}else if(command == ORCommands.PROBE_ACK){
				ProbeAck pa = new ProbeAck();
				return pa.decode(ctx, ch, buffer);
			}else if(command == ORCommands.SEND){
				Send send = new Send();
				return send.decode(ctx, ch, buffer);
			}else if(command == ORCommands.SEND_ACK){
				SendAck sa = new SendAck();
				return sa.decode(ctx, ch, buffer);
			}else{
				//TODO add error response here
				throw new RuntimeException();
			}
		}else return null;
	}
}
