package org.immregistries.mqe.hub.report;

import java.util.ArrayList;
import java.util.List;

public class FacilityIdentifiers {
  private String location;
  private List<FacilityIdentifierCount> identifiers = new ArrayList<>();

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public List<FacilityIdentifierCount> getIdentifiers() {
    return identifiers;
  }

  public void setIdentifiers(
      List<FacilityIdentifierCount> identifiers) {
    this.identifiers = identifiers;
  }

  public class FacilityIdentifierCount {
    private String value;
    private int count;

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }

    public int getCount() {
      return count;
    }

    public void setCount(int count) {
      this.count = count;
    }
  }
}
