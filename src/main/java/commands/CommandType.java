package main.java.commands;

import java.util.HashMap;

public class CommandType {
	public static final byte ERROR = 0;
	public static final byte UNKNOWN = 1;
	public static final byte REGISTER = 2;
	public static final byte REGISTER_SUCCESS = 3;
	public static final byte 	NEW_CIRCUIT = 4;
	public static final byte NEW_CIRCUIT_ACK = 5;
	public static final byte REQUEST = 6;
	public static final byte RESPONSE = 7;
	public static final byte EXTEND_CIRCUIT = 8;
	public static final byte EXTEND_CIRCUIT_ACK = 9;
	public static final byte FORWARD = 10;
	public static final byte REVERSE = 11;
	
	public static final ORCommand getORCommand(byte command){
		if(command == CommandType.REGISTER){
			return new Register();
		}else if(command == CommandType.REGISTER_SUCCESS){
			return new RegisterSuccess();
		}else if(command == CommandType.NEW_CIRCUIT){
			return new NewCircuit();
		}else if(command == CommandType.NEW_CIRCUIT_ACK){
			return new NewCircuitAck();
		}else if(command == CommandType.REQUEST){
			return new Request();
		}else if(command == CommandType.RESPONSE){
			return new Response();
		}else if(command == CommandType.EXTEND_CIRCUIT){
			return new ExtendCircuit();
		}else if(command == CommandType.EXTEND_CIRCUIT_ACK){
			return new ExtendCircuitAck();
		}else if(command == CommandType.ERROR){
			return new ORError();
		}else if(command == CommandType.FORWARD){
			return new Forward();
		}else if(command == CommandType.REVERSE){
			return new Reverse();
		}else return new Unknown();
	}
}
