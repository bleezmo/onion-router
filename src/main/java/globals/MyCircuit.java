package main.java.globals;

import java.util.ArrayList;
import java.util.LinkedList;

import main.java.client_src.Circuit;

public class MyCircuit {
	private static ArrayList<Circuit> mycircuit;

	public static final void closeCircuit(){
		mycircuit = null;
	}
	public static final void newCircuit(Circuit c){
		mycircuit = new ArrayList<Circuit>();
		mycircuit.add(c);
	}
	public static final void extendCircuit(Circuit c){
		if(mycircuit != null) mycircuit.add(c);
	}
	public static final Circuit get(int index){
		if(mycircuit != null && mycircuit.size() > index) return mycircuit.get(index);
		else return null;
	}
	public static final int getSize(){
		if(mycircuit != null) return mycircuit.size();
		else return 0;
	}
	
}
