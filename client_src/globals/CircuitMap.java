package globals;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.UUID;


public final class CircuitMap {
	private static final HashMap<UUID,InetSocketAddress> circuitMap = new HashMap<UUID,InetSocketAddress>();
	
	public static void put(UUID circuitId, InetSocketAddress addr){
		circuitMap.put(circuitId, addr);
	}
	public static InetSocketAddress get(UUID circuitId){
		if(circuitMap == null) return null;
		return circuitMap.get(circuitId);
	}
	public static InetSocketAddress remove(UUID circuitId){
		return circuitMap.remove(circuitId);
	}
}
