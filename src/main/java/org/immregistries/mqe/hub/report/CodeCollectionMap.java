package org.immregistries.mqe.hub.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.immregistries.mqe.validator.report.codes.CollectionBucket;

public class CodeCollectionMap {

  private Map<String, List<CollectionBucket>> map = new HashMap<>();

  public Map<String, List<CollectionBucket>> getMap() {
    return map;
  }

  public List<CollectionBucket> getCodes() {
    List<CollectionBucket> codes = new ArrayList<>();
    for(List<CollectionBucket> values: map.values()) {
      codes.addAll(values);
    }
    return codes;
  }

  public void setMap(Map<String, List<CollectionBucket>> map) {
    this.map = map;
  }
}
