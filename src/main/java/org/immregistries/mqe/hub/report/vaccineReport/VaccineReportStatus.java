package org.immregistries.mqe.hub.report.vaccineReport;

public enum VaccineReportStatus {
                                 EXPECTED("Expected"),
                                 POSSIBLE("Possible"),
                                 NOT_EXPECTED("Not Expected"),
                                 NOT_POSSIBLE("Not Possible");

  private String label = "";

  public String getLabel() {
    return label;
  }

  @Override
  public String toString() {
    return label;
  }

  private VaccineReportStatus(String label) {
    this.label = label;
  }


  public static VaccineReportStatus getVaccineReportStatus(String label) {
    for (VaccineReportStatus vrs : VaccineReportStatus.values()) {
      if (vrs.label.equals(label)) {
        return vrs;
      }
    }
    return null;
  }
}
