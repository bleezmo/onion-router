package main.java.globals;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
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
	private static final HashMap<UUID,UUID> linkMap = new HashMap<UUID,UUID>();
	
	public static void setLink(UUID receiverCircuit, UUID forwardingCircuit){
		linkMap.put(receiverCircuit, forwardingCircuit);
	}
	public static UUID getForwardingCircuit(UUID receiverCircuit){
		return linkMap.get(receiverCircuit);
	}
	public static UUID getReceivingCircuit(UUID forwardingCircuit){
		Iterator<Entry<UUID,UUID>> iter = linkMap.entrySet().iterator();
		while(iter.hasNext()){
			Entry<UUID,UUID> entry = iter.next();
			if(entry.getValue().equals(forwardingCircuit)) return entry.getKey();
		}
		return null;
	}
	
	public static void addForwardingCircuit(UUID circuitId, Node node){
		forwardingCircuitMap.put(circuitId, node);
	}
	public static Node getForwardingNode(UUID circuitId){
		if(forwardingCircuitMap == null) return null;
		return forwardingCircuitMap.get(circuitId);
	}
	public static Node removeForwardingCircuit(UUID circuitId){
		return forwardingCircuitMap.remove(circuitId);
	}
	
	public static void addReceivingCircuit(UUID circuitId, String nodeName){
		receivingCircuitMap.put(circuitId, nodeName);
	}
	public static String getReceivingNodeName(UUID circuitId){
		if(receivingCircuitMap == null) return null;
		return receivingCircuitMap.get(circuitId);
	}
	public static String removeReceivingCircuit(UUID circuitId){
		return receivingCircuitMap.remove(circuitId);
	}
	public static final String buildString(){
		Iterator<Entry<UUID,UUID>> iter = linkMap.entrySet().iterator();
		String out = "[";
		while(iter.hasNext()){
			Entry<UUID,UUID> entry = iter.next();
			out += entry.getKey()+" -> "+entry.getValue();
			if(iter.hasNext()) out += ", ";
			else out += "]";
		}
		return out;
	}
}
