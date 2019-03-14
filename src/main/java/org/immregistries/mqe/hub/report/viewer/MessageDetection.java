package org.immregistries.mqe.hub.report.viewer;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "MESSAGE_DETECTION")
public class MessageDetection {

  @Id
  @SequenceGenerator(name = "MESSAGE_DETECTION_GENERATOR", sequenceName = "MESSAGE_DETECTION_SEQ")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MESSAGE_DETECTION_GENERATOR")
  @Column(name = "MESSAGE_DETECTION_ID")
  private long id;

  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="MESSAGE_METADATA_ID")
  private MessageMetadata messageMetadata;

  private String detectionId;
  private String locationTxt;

  public MessageMetadata getMessageMetadata() {
    return messageMetadata;
  }

  public void setMessageMetadata(MessageMetadata messageMetadata) {
    this.messageMetadata = messageMetadata;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getDetectionId() {
    return detectionId;
  }

  public void setDetectionId(String detectionId) {
    this.detectionId = detectionId;
  }

  public String getLocationTxt() {
    return locationTxt;
  }

  public void setLocationTxt(String locationTxt) {
    this.locationTxt = locationTxt;
  }
}
