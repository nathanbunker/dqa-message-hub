package org.immregistries.mqe.hub.settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "DETECTION_GUIDANCE")
public class DetectionGuidance {
  @Id
  private String mqeCode;
  @Column(length = 2500)
  private String howToFix;
  @Column(length = 2500)
  private String whyToFix;
  
  public DetectionGuidance() {}
  
  public String getHowToFix() {
    return howToFix;
  }

  public void setHowToFix(String howToFix) {
    this.howToFix = howToFix;
  }

  public String getWhyToFix() {
    return whyToFix;
  }

  public void setWhyToFix(String whyToFix) {
    this.whyToFix = whyToFix;
  }

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
