package commands;

import java.util.HashMap;

public class CommandType {
	public static final byte ERROR = 0;
	public static final byte REGISTER = 1;
	public static final byte REGISTER_SUCCESS = 2;
	public static final byte 	NEW_CIRCUIT = 3;
	public static final byte NEW_CIRCUIT_ACK = 4;
	public static final byte SEND = 5;
	public static final byte SEND_ACK = 6;
	public static final byte EXTEND_CIRCUIT = 7;
	public static final byte EXTEND_CIRCUIT_ACK = 8;
	
	public static final ORCommand getORCommand(byte command){
		if(command == CommandType.REGISTER){
			return new Register();
		}else if(command == CommandType.REGISTER_SUCCESS){
			return new RegisterSuccess();
		}else if(command == CommandType.NEW_CIRCUIT){
			return new NewCircuit();
		}else if(command == CommandType.NEW_CIRCUIT_ACK){
			return new NewCircuitAck();
		}else if(command == CommandType.SEND){
			return new Send();
		}else if(command == CommandType.SEND_ACK){
			return new SendAck();
		}else if(command == CommandType.EXTEND_CIRCUIT){
			return new ExtendCircuit();
		}else if(command == CommandType.EXTEND_CIRCUIT_ACK){
			return new ExtendCircuitAck();
		}else{
			//TODO add error response here
			throw new RuntimeException();
		}
	}
}
