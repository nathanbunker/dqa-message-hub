package org.immregistries.dqa.hub.rest;

import java.util.ArrayList;

public class DqaMessageInfo {
	
	private int messageNum;
	private ArrayList<String> hl7Messages;
	
	public DqaMessageInfo (int messageNum) {
		this.messageNum = messageNum;
		hl7Messages = new ArrayList<String>();
	}
	
	public int getMessageNum() {
		return messageNum;
	}

	public void setMessageNum(int messageNum) {
		this.messageNum = messageNum;
	}

	public ArrayList<String> getHl7Messages() {
		return hl7Messages;
	}

	public void setHl7Messages(ArrayList<String> hl7Messages) {
		this.hl7Messages = hl7Messages;
	}
	
	public void addHl7Messages(String message) {
		this.hl7Messages.add(message);
	}
	
	public void printHL7Array() {
		for (int i = 0; i < this.hl7Messages.size(); i++) {
			System.out.println("PRINTING FROM METHOD, COUNT: " + i);
			System.out.println(this.hl7Messages.get(i));
		}
	}
	
} 
