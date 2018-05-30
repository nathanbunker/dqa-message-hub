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
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "CODE_COUNT", uniqueConstraints = @UniqueConstraint(name = "UNIQUE_CODE_COUNT", columnNames = {
    "ATTRIBUTE", "SENDER_METRICS_ID", "CODE_TYPE", "CODE_VALUE"}))
public class CodeCount {

  @Id
  @SequenceGenerator(name = "CODE_COUNT_GENERATOR", sequenceName = "CODE_COUNT_SEQ", allocationSize = 100)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CODE_COUNT_GENERATOR")
  @Column(name = "CODE_COUNT_ID")
  private long id;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "sender_metrics_id")
  private SenderMetrics senderMetrics;

  @Column(name = "CODE_TYPE")
  private String codeType;

  private String origin;

  private String attribute;

  @Column(name = "CODE_VALUE")
  private String codeValue;
  private int codeCount;

  public long getId() {
    return id;
  }

  public String getAttribute() {
    return attribute;
  }

  public void setAttribute(String attribute) {
    this.attribute = attribute;
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

  public void setCodeCount(int count) {
    this.codeCount = count;
  }

  public SenderMetrics getSenderMetrics() {
    return senderMetrics;
  }

  public void setSenderMetrics(SenderMetrics senderMetrics) {
    this.senderMetrics = senderMetrics;
  }

  public String getOrigin() {
    return origin;
  }

  public void setOrigin(String origin) {
    this.origin = origin;
  }

  @Override
  public String toString() {
    return "CodeCount{" + "id=" + id + ", senderMetrics=" + senderMetrics.getId() + ", codeType='"
        + codeType + '\'' + ", attribute='" + attribute + '\''
        + ", codeValue='" + codeValue + '\'' + ", codeCount=" + codeCount + '}';
  }
}
