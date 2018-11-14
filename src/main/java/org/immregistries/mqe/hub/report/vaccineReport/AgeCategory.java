package org.immregistries.mqe.hub.report.vaccineReport;

public class AgeCategory implements Comparable<AgeCategory> {

  private String label;
  private int ageLow;
  private int ageHigh;
  private int order;

  public int getOrder() {
    return order;
  }

  public void setOrder(int order) {
    this.order = order;
  }

  public int getAgeLow() {
    return ageLow;
  }

  public int getAgeHigh() {
    return ageHigh;
  }

  public String getLabel() {
    return label;
  }

  @Override
  public String toString() {
    return label;
  }

  public AgeCategory(String label, int low, int high) {
    this.label = label;
    this.ageHigh = high;
    this.ageLow = low;
  }



  public boolean isInCategory(int age) {
    return age >= ageLow && age < ageHigh;
  }

  @Override
  public int compareTo(AgeCategory ac) {
    if (this.order < ac.order) {
      return -1;
    }
    if (this.order > ac.order) {
      return 1;
    }
    return 0;
  }
}
