package org.immregistries.mqe.hub.report.viewer;

public class DetectionDetail {
  private String description;
  private String name;
  private String detectionId;
  private String Severity;
  private String location;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDetectionId() {
    return detectionId;
  }

  public void setDetectionId(String detectionId) {
    this.detectionId = detectionId;
  }

  public String getSeverity() {
    return Severity;
  }

  public void setSeverity(String severity) {
    Severity = severity;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }
}
