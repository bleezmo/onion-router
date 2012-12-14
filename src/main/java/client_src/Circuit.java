package main.java.client_src;

import java.util.UUID;

public class Circuit {
	private final UUID circuitId;
	private final Node node;
	public Circuit(UUID circuitId, Node node){
		this.circuitId = circuitId;
		this.node = node;
	}
	public UUID getCircuitId() {
		return circuitId;
	}
	public Node getNode() {
		return node;
	}
}
