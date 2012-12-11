package globals;

import java.net.InetSocketAddress;
import java.util.ArrayList;

/**
 * list of nodes. at the moment it updates only once
 * TODO 
 * @author josh
 *
 */
public class NodeList {
	private static final ArrayList<InetSocketAddress> nodeList = new ArrayList<InetSocketAddress>();
	
	public static InetSocketAddress get(int index){
		return nodeList.get(index);
	}
	public static void add(InetSocketAddress node){
		nodeList.add(node);
	}
}
