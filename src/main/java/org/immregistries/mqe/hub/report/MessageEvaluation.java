package org.immregistries.mqe.hub.report;

import org.immregistries.mqe.hl7util.builder.AckResult;
import org.immregistries.mqe.validator.report.VxuScoredReport;
import org.joda.time.DateTime;

public class MessageEvaluation {

  private int vaccineCount;
  private AckResult messageResult;
  private boolean messageProcessed;
  private String messageAck;
  private String messageVxu;
  private int patientAge;
  private DateTime messageReceivedTime;
  private VxuScoredReport messageReport;


  public int getVaccineCount() {
    return vaccineCount;
  }

  public void setVaccineCount(int vaccineCount) {
    this.vaccineCount = vaccineCount;
  }

  public AckResult getMessageResult() {
    return messageResult;
  }

  public void setMessageResult(AckResult messageResult) {
    this.messageResult = messageResult;
  }

  public boolean isMessageProcessed() {
    return messageProcessed;
  }

  public void setMessageProcessed(boolean messageProcessed) {
    this.messageProcessed = messageProcessed;
  }

  public int getPatientAge() {
    return patientAge;
  }

  public void setPatientAge(int patientAge) {
    this.patientAge = patientAge;
  }

  public DateTime getMessageReceivedTime() {
    return messageReceivedTime;
  }

  public void setMessageReceivedTime(DateTime messageReceivedTime) {
    this.messageReceivedTime = messageReceivedTime;
  }

  public String getMessageAck() {
    return messageAck;
  }

  public void setMessageAck(String messageAck) {
    this.messageAck = messageAck;
  }

  public String getMessageVxu() {
    return messageVxu;
  }

  public void setMessageVxu(String messageVxu) {
    this.messageVxu = messageVxu;
  }

  public VxuScoredReport getMessageReport() {
    return messageReport;
  }

  public void setMessageReport(VxuScoredReport messageReport) {
    this.messageReport = messageReport;
  }
}
