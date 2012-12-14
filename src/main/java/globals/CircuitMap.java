package main.java.globals;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.UUID;

import main.java.client_src.Node;


/**
 * stores nodes based on circuit ids. 
 * This is usually established when server receives an ExtendCircuit request.
 * used to forward information given a circuit id to a node;
 * @author josh
 *
 */
public final class CircuitMap {
	private static final HashMap<UUID,Node> forwardingCircuitMap = new HashMap<UUID,Node>();
	private static final HashMap<UUID,String> receivingCircuitMap = new HashMap<UUID,String>();
	
	public static void addForwardingCircuit(UUID circuitId, Node node){
		forwardingCircuitMap.put(circuitId, node);
	}
	public static Node getForwardingCircuit(UUID circuitId){
		if(forwardingCircuitMap == null) return null;
		return forwardingCircuitMap.get(circuitId);
	}
	public static Node removeForwardingCircuit(UUID circuitId){
		return forwardingCircuitMap.remove(circuitId);
	}
	
	public static void addReceivingCircuit(UUID circuitId, String nodeName){
		receivingCircuitMap.put(circuitId, nodeName);
	}
	public static String getReceivingCircuit(UUID circuitId){
		if(receivingCircuitMap == null) return null;
		return receivingCircuitMap.get(circuitId);
	}
	public static String removeReceivingCircuit(UUID circuitId){
		return receivingCircuitMap.remove(circuitId);
	}
}
