

public class IPAddress {
	public static final int newIP(byte octet1, byte octet2, byte octet3, byte octet4){
		int ip = octet1;
		ip = ip << 8;
		ip = ip + octet2;
		ip = ip << 8;
		ip = ip + octet3;
		ip = ip << 8;
		ip = ip + octet4;
		return ip;
	}
	public static final short newPort(byte octet1, byte octet2){
		short port = octet1;
		port = (short) (port << 8);
		port = (short) (port + octet2);
		return port;
	}
}
