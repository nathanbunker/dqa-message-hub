package org.immregistries.mqe.hub.report;

import java.util.ArrayList;
import java.util.List;

public class SiteIdentifiers {
  private String location;
  private List<SiteIdentifierCount> siteIdentifiers = new ArrayList<>();

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public List<SiteIdentifierCount> getSiteIdentifiers() {
    return siteIdentifiers;
  }

  public void setSiteIdentifiers(
      List<SiteIdentifierCount> siteIdentifiers) {
    this.siteIdentifiers = siteIdentifiers;
  }

  public class SiteIdentifierCount {
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
