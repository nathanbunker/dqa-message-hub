package org.immregistries.mqe.hub.report.viewer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;

public class MessageListItem {

  private Long id;
  private String messageControlId;
  private String cvxList;
  private Date received;
  @JsonIgnore
  private String messageReceived;
  private String ackStatus;
  private String patientName;
//	private boolean mcirError;

  /**
   * @return the id
   */
  public Long getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * @return the messageControlId
   */
  public String getMessageControlId() {
    return messageControlId;
  }

  /**
   * @param messageControlId the messageControlId to set
   */
  public void setMessageControlId(String messageControlId) {
    this.messageControlId = messageControlId;
  }

  /**
   * @return the received
   */
  public Date getReceived() {
    return received;
  }

  /**
   * @param received the received to set
   */
  public void setReceived(Date received) {
    this.received = received;
  }

  /**
   * @return the ackStatus
   */
  public String getAckStatus() {
    return ackStatus;
  }

  /**
   * @param ackStatus the ackStatus to set
   */
  public void setAckStatus(String ackStatus) {
    this.ackStatus = ackStatus;
  }

  /**
   * @return the patientName
   */
  public String getPatientName() {
    return patientName;
  }

  /**
   * @param patientName the patientName to set
   */
  public void setPatientName(String patientName) {
    this.patientName = patientName;
  }

  /**
   * @return the cvxList
   */
  public String getCvxList() {
    return cvxList;
  }

  /**
   * @param cvxList the cvxList to set
   */
  public void setCvxList(String cvxList) {
    this.cvxList = cvxList;
  }

  /**
   * @return the messageReceived
   */
  public String getMessageReceived() {
    return messageReceived;
  }

  /**
   * @param messageReceived the messageReceived to set
   */
  public void setMessageReceived(String messageReceived) {
    this.messageReceived = messageReceived;
  }

}
