package org.immregistries.mqe.hub.report.vaccineReport;

public enum VaccineReportStatus {
  EXPECTED("Expected"), 
  POSSIBLE("Possible"),
  NOT_EXPECTED("Not Expected"),
  NOT_POSSIBLE("Not Possible");

  private String label = "";
  
  @Override
  public String toString() {
    return label;
  }
  
  private VaccineReportStatus(String label)
  {
    this.label = label;
  }
;
}
