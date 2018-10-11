package org.immregistries.mqe.hub.report.viewer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "MESSAGE_CODE")
public class MessageCode {

  @Id
  @SequenceGenerator(name = "MESSAGE_CODE_GENERATOR", sequenceName = "MESSAGE_CODE_SEQ")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MESSAGE_CODE_GENERATOR")
  @Column(name = "MESSAGE_CODE_ID")
  private long id;

  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="MESSAGE_METADATA_ID")
  private MessageMetadata messageMetadata;

  private String codeType;
  private String codeValue;
  private String codeStatus;
  private int codeCount;

  public MessageMetadata getMessageMetadata() {
    return messageMetadata;
  }

  public void setMessageMetadata(MessageMetadata messageMetadata) {
    this.messageMetadata = messageMetadata;
  }

  public String getCodeStatus() {
    return codeStatus;
  }

  public void setCodeStatus(String codeStatus) {
    this.codeStatus = codeStatus;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getCodeType() {
    return codeType;
  }

  public void setCodeType(String codeType) {
    this.codeType = codeType;
  }

  public String getCodeValue() {
    return codeValue;
  }

  public void setCodeValue(String codeValue) {
    this.codeValue = codeValue;
  }

  public int getCodeCount() {
    return codeCount;
  }

  public void setCodeCount(int codeCount) {
    this.codeCount = codeCount;
  }
}
