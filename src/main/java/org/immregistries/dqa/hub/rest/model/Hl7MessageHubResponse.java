package org.immregistries.dqa.hub.rest.model;

import org.immregistries.dqa.validator.DqaMessageServiceResponse;
import org.immregistries.dqa.validator.report.codes.CodeCollection;

public class Hl7MessageHubResponse {
	private String vxu;
	private String ack;

	private DqaMessageServiceResponse dqaResponse;

	private String sender;

	public String getAck() {
		return ack;
	}
	public void setAck(String ack) {
		this.ack = ack;
	}
	public DqaMessageServiceResponse getDqaResponse() {
		return dqaResponse;
	}
	public void setDqaResponse(DqaMessageServiceResponse dqaResponse) {
		this.dqaResponse = dqaResponse;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getVxu() {
		return vxu;
	}
	public void setVxu(String vxu) {
		this.vxu = vxu;
	}
	
	
}
