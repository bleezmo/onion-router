package main.java.globals;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.UUID;

import main.java.client_src.Node;


public final class CircuitMap {
	private static final HashMap<UUID,Node> circuitMap = new HashMap<UUID,Node>();

	public static void put(UUID circuitId, Node node){
		circuitMap.put(circuitId, node);
	}
	public static Node get(UUID circuitId){
		if(circuitMap == null) return null;
		return circuitMap.get(circuitId);
	}
	public static Node remove(UUID circuitId){
		return circuitMap.remove(circuitId);
	}
}
