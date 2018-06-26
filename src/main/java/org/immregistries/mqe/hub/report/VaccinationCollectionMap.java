package org.immregistries.mqe.hub.report;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VaccinationCollectionMap {

  private Map<AgeCategory, Map<VaccineReportGroup, VaccineAdministered>> map = new HashMap<>();

  public Map<AgeCategory, Map<VaccineReportGroup, VaccineAdministered>> getMap() {
    return map;
  }

  public void setMap(Map<AgeCategory, Map<VaccineReportGroup, VaccineAdministered>> map) {
    this.map = map;
  }
}
