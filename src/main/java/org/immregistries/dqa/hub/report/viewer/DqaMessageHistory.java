package org.immregistries.dqa.hub.report.viewer;

import java.util.ArrayList;
import java.util.List;

public class DqaMessageHistory {

  private String provider = "";

  private List<MessageCounts> messageHistory = new ArrayList<>();

  private int year = 2000;

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public List<MessageCounts> getMessageHistory() {
    return messageHistory;
  }

  public void setMessageHistory(List<MessageCounts> messageDays) {
    this.messageHistory = messageDays;
  }

  public void setYear(int year) {
    this.year = year;
  }

  public int getYear() {
    return this.year;
  }

}
