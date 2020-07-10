package org.immregistries.mqe.hub.settings;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.test.context.jdbc.Sql;


@Entity
@Table(name = "DETECTION_GUIDANCE")
@Sql({"/detectionGuidance.sql"})
public class DetectionGuidance {
  @Id
  private String mqeCode;
  private String howToFix;
  private String whyToFix;

  public String getWhyToFix() {
    return whyToFix;
  }

  public void setWhyToFix(String whyToFix) {
    this.whyToFix = whyToFix;
  }

  public DetectionGuidance() {}

  public String getMqeCode() {
    return mqeCode;
  }

  public void setMqeCode(String mqeCode) {
    this.mqeCode = mqeCode;
  }

  public String getSeverity() {
    return howToFix;
  }

  public void setSeverity(String severity) {
    this.howToFix = severity;
  }

}
