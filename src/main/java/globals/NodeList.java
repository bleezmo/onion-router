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
	public static void add(Node node){
		if(node.getNodeName().equals(GlobalVars.nodeName())) return;
		for(int i = 0; i < nodeList.size(); i++){
			if(nodeList.get(i).getNodeName().equals(node.getNodeName())){
				return;
			}
		}
		nodeList.add(node);
	}
	public static void addAll(ArrayList<Node> nodes){
		for(int i = 0; i < nodes.size(); i++){
			add(nodes.get(i));
		}
	}
	/**
	 * @return a new array list copied from the internal node list
	 */
	public static ArrayList<Node> getAll(){
		ArrayList<Node>  newList = new ArrayList<Node> ();
		newList.addAll(nodeList);
		return newList;
	}
	public static boolean removeNode(Node node){
		return nodeList.remove(node);
	}
}
