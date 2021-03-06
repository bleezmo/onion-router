package main.java.globals;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.UUID;

import main.java.client_src.Circuit;
import main.java.client_src.Node;

public class MyCircuit {
	private static ArrayList<UUID> mycircuit;
	private static Node entryNode;
	
	public static final void closeCircuit(){
		mycircuit = null;
		entryNode = null;
	}
	public static final void newCircuit(Circuit c){
		mycircuit = new ArrayList<UUID>();
		mycircuit.add(c.getCircuitId());
		entryNode = c.getNode();
	}
	public static final void extendCircuit(UUID circuitId){
		if(mycircuit != null) mycircuit.add(circuitId);
	}
	public static final UUID get(int index){
		if(mycircuit != null && mycircuit.size() > index) return mycircuit.get(index);
		else return null;
	}
	public static final int getSize(){
		if(mycircuit != null) return mycircuit.size();
		else return 0;
	}
	public static Node getEntryNode() {
		return entryNode;
	}
	public static final boolean hasCircuit(){
		if(mycircuit != null && entryNode != null) return true;
		else return false;
	}
	public static final String buildString(){
		if(mycircuit == null) return "[]";
		int end = mycircuit.size()-1;
		String out = "[";
		for(int i = 0; i < end; i++){
			out += mycircuit.get(i).toString()+" -> ";
		}
		out += mycircuit.get(end).toString()+"]";
		return out;
	}
}
