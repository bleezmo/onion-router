package main.java.globals;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import main.java.client_src.Node;

/**
 * list of nodes. at the moment it updates only once
 * TODO 
 * @author josh
 *
 */
public class NodeList {
	private static final ArrayList<Node> nodeList = new ArrayList<Node>();
	
	public static Node get(int index){
		return nodeList.get(index);
	}
	public static void add(String nodeName, InetSocketAddress node){
		for(int i = 0; i < nodeList.size(); i++){
			if(nodeList.get(i).getNodeName().equals(nodeName)){
				return;
			}
		}
		nodeList.add(new Node(nodeName,node));
	}
	public static void addAll(ArrayList<Node> nodes){
		nodeList.addAll(nodes);
	}
	/**
	 * @return a new array list copied from the internal node list
	 */
	public static ArrayList<Node> getAll(){
		ArrayList<Node>  newList = new ArrayList<Node> ();
		newList.addAll(nodeList);
		return newList;
	}
}
