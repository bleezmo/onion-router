package main.java.client_src;

import java.net.InetSocketAddress;

public class Node{
	private final String nodeName;
	private final InetSocketAddress addr;
	public Node(String nodeName, InetSocketAddress addr){
		this.nodeName = nodeName;
		this.addr = addr;
	}
	public String getNodeName() {
		return nodeName;
	}
	public InetSocketAddress getAddr() {
		return addr;
	}
}
