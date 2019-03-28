package org.immregistries.mqe.hub.rest.model;

import org.immregistries.mqe.validator.MqeMessageServiceResponse;

public class Hl7MessageHubResponse {

  private String message;
  private String ack;

  private MqeMessageServiceResponse mqeResponse;

  private String sender;

  public String getAck() {
    return ack;
  }

  public void setAck(String ack) {
    this.ack = ack;
  }

  public MqeMessageServiceResponse getMqeResponse() {
    return mqeResponse;
  }

  public void setMqeResponse(MqeMessageServiceResponse mqeResponse) {
    this.mqeResponse = mqeResponse;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public String getVxu() {
    return message;
  }

  public void setVxu(String vxu) {
    this.message = vxu;
  }

  public void setQbp(String qbp) {
    this.message = qbp;
  }
  
  public String getQbp(String qbp) {
    return this.message;
  }
  
 
}
