package entities;

import java.io.Serializable;
import java.util.Hashtable;

import Enum.Instruction;

/**
 * Every Communication between client and server will be
 * an object of this class 
 */
public class ServerMessage implements Serializable{
	

	// Attributes
	private static final long serialVersionUID = -1123917389592782100L;
	private Instruction instruction;
	private Hashtable<String, Object> data;
	
	// Constructors
	
	public ServerMessage(Instruction instruction,Hashtable<String, Object> data) {
		super();
		this.instruction = instruction;
		this.data=data;
	}
	
	public ServerMessage() {
		super();
	}
	
	// Getters and Setters

	public Instruction getInstruction() {
		return instruction;
	}

	public void setInstruction(Instruction instruction) {
		this.instruction = instruction;
	}

	public Hashtable<String, Object> getData() {
		return data;
	}

	public void setData(Hashtable<String, Object> data) {
		this.data = data;
	}
	
}
