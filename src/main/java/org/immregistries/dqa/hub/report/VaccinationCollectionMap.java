package org.immregistries.dqa.hub.report;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.immregistries.dqa.validator.report.codes.VaccineBucket;

public class VaccinationCollectionMap {

  private Map<AgeCategory, List<VaccineBucket>> map = new HashMap<>();

  public Map<AgeCategory, List<VaccineBucket>> getMap() {
    return map;
  }

  public void setMap(Map<AgeCategory, List<VaccineBucket>> map) {
    this.map = map;
  }
}
