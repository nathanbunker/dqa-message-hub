package org.immregistries.mqe.hub.report;

import java.util.HashMap;
import java.util.Map;

import org.immregistries.mqe.hub.report.vaccineReport.AgeCategory;
import org.immregistries.mqe.hub.report.vaccineReport.VaccineReportGroup;

public class VaccinationExpectedCollectionMap {

  private Map<VaccineReportGroup, Map<AgeCategory, VaccineAdministered>> map = new HashMap<>();

  public Map<VaccineReportGroup, Map<AgeCategory, VaccineAdministered>> getMap() {
    return map;
  }

  public void setMap(Map<VaccineReportGroup, Map<AgeCategory, VaccineAdministered>> map) {
    this.map = map;
  }
}
