package org.immregistries.mqe.hub.report.vaccineReport;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonValue;

public class VaccineReportGroup implements Comparable<VaccineReportGroup> {


  private String label;
  private int displayPriority;
  private String[] cvxList;
  private Map<AgeCategory, VaccineReportStatus> vaccineReportStatusMap = new HashMap<>();

  public int getDisplayPriority() {
    return displayPriority;
  }
  
  public void setDisplayPriority(int displayPriority) {
    this.displayPriority = displayPriority;
  }

  public Map<AgeCategory, VaccineReportStatus> getVaccineReportStatusMap() {
    return vaccineReportStatusMap;
  }

  public String[] getCvxList() {
    return cvxList;
  }

  @JsonValue
  public String getLabel() {
    return label;
  }

  

  public VaccineReportGroup(String label, String... cvx) {
    this.label = label;
    this.cvxList = cvx;
  }

  @Override
  public int compareTo(VaccineReportGroup other) {
    if (this.displayPriority < other.displayPriority)
    {
      return -1;
    }
    if (this.displayPriority > other.displayPriority)
    {
      return -1;
    }
    return 0;
  }


}
