package org.immregistries.mqe.hub.report;

import java.util.ArrayList;
import java.util.List;

public class MqeExampleMessage {
  private String mqeCode;
  private String message;
  private List<String> locations = new ArrayList<>();

  public MqeExampleMessage() {
  }

  public MqeExampleMessage(String messageIn) {
    //often a message will have /r in it instead of a line break the system can understand.
    //we replace it so that it's not displaying crazy.
    if (messageIn != null) {
      this.message = messageIn.replaceAll("\r", "\n");
    }
  }

  public MqeExampleMessage(String messageIn, String location_txt, String mqeCode) {
    this(messageIn);
    this.mqeCode = mqeCode;
    this.locations.add(location_txt);
  }

  public String getMqeCode() {
    return mqeCode;
  }

  public void setMqeCode(String mqeCode) {
    this.mqeCode = mqeCode;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public List<String> getLocations() {
    return locations;
  }

  public void setLocations(List<String> locations) {
    this.locations = locations;
  }
}
