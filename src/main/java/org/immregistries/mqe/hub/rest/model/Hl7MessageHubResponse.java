package org.immregistries.mqe.hub.rest.model;

import org.immregistries.mqe.validator.MqeMessageServiceResponse;

public class Hl7MessageHubResponse {

  private String vxu;
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
    return vxu;
  }

  public void setVxu(String vxu) {
    this.vxu = vxu;
  }


}
