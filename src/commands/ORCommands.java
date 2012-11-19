package commands;

import org.jboss.netty.buffer.ChannelBuffer;

public class ORCommands {
	public static final byte ERROR = 0;
	public static final byte REGISTER = 1;
	public static final byte REGISTER_SUCCESS = 2;
	public static final byte PROBE = 3;
	public static final byte PROBE_ACK = 4;
	public static final byte SEND = 5;
	public static final byte SEND_ACK = 6;
	
	public static final ORCommand decode(ChannelBuffer buffer){
		if(buffer.readable()){
			byte command = buffer.readByte();
			if(command == ORCommands.REGISTER){
				Register register = new Register();
				register.decode(buffer);
				return register;
			}else if(command == ORCommands.REGISTER_SUCCESS){
				RegisterSuccess rs = new RegisterSuccess();
				rs.decode(buffer);
				return rs;
			}else if(command == ORCommands.PROBE){
				Probe probe = new Probe();
				probe.decode(buffer);
				return probe;
			}else if(command == ORCommands.PROBE_ACK){
				ProbeAck pa = new ProbeAck();
				pa.decode(buffer);
				return pa;
			}else if(command == ORCommands.SEND){
				Send send = new Send();
				send.decode(buffer);
				return send;
			}else if(command == ORCommands.SEND_ACK){
				SendAck sa = new SendAck();
				sa.decode(buffer);
				return sa;
			}else{
				return new Error(Error.UNKNOWN_COMMAND);
			}
		}else{
			return new Error(Error.UNREADABLE);
		}
	}
}
