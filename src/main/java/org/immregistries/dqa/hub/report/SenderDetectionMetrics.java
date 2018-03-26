package org.immregistries.dqa.hub.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "SENDER_DETECTION_METRICS")
public class SenderDetectionMetrics {

  @Id
  @SequenceGenerator(name = "SENDER_DETECTION_METRICS_GENERATOR", sequenceName = "SENDER_DETECTION_METRICS_SEQ", allocationSize = 100)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SENDER_DETECTION_METRICS_GENERATOR")
  @Column(name = "SENDER_DETECTION_METRICS_ID")
  private long id;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "sender_metrics_id")
  private SenderMetrics senderMetrics;
  private String dqaDetectionCode;
  private int attributeCount = 0;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getDqaDetectionCode() {
    return dqaDetectionCode;
  }

  public void setDqaDetectionCode(String dqaDetectionCode) {
    this.dqaDetectionCode = dqaDetectionCode;
  }

  public int getAttributeCount() {
    return attributeCount;
  }

  public void setAttributeCount(int attributeCount) {
    this.attributeCount = attributeCount;
  }

  public SenderMetrics getSenderMetrics() {
    return senderMetrics;
  }

  public void setSenderMetrics(SenderMetrics senderMetrics) {
    this.senderMetrics = senderMetrics;
  }
}
