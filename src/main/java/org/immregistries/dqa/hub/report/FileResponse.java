package org.immregistries.dqa.hub.report;

import java.util.ArrayList;

public class FileResponse {
	
	private ArrayList<String> responseMessages;
	private String responseMessage;
	private int aa_count = 0;
	private int ae_count = 0;
	
	public FileResponse() {
		responseMessages = new ArrayList<>();
	}
	
	public int getAa_count() {
		return aa_count;
	}

	public void setAa_count(int aa_count) {
		this.aa_count = aa_count;
	}

	public int getAe_count() {
		return ae_count;
	}

	public void setAe_count(int ae_count) {
		this.ae_count = ae_count;
	}

	public ArrayList<String> getResponseMessages() {
		return responseMessages;
	}

	public void setResponseMessages(ArrayList<String> responseMessages) {
		this.responseMessages = responseMessages;
	}
	
	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	
	public void addAckMessage(String ackMessage) {
		this.responseMessages.add(ackMessage);
	}
	
	public String getAckMessagge(int index) {
		return this.responseMessages.get(index);
	}
}