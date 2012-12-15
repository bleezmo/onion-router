package main.java.utils;

import main.java.commands.CommandType;
import main.java.commands.ORCoder;
import main.java.commands.ORCommand;
import main.java.commands.Reverse;

public class Utils {
	public static final String toHex(byte[] bytes){
	    StringBuilder sb = new StringBuilder();
	    for (byte b : bytes) {
	        sb.append(String.format("%02X ", b));
	    }
	    return sb.toString();
	}
	public static final ORCommand unravel(Reverse r){
		byte[] data = r.getData();
		if(data[0] == CommandType.REVERSE){
			return unravel((Reverse) new Reverse().decode(data));
		}else{
			return CommandType.getORCommand(data[0]).decode(data);
		}
	}
}
