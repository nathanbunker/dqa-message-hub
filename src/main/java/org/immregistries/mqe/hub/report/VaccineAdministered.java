package org.immregistries.mqe.hub.report;

import org.immregistries.mqe.hub.report.vaccineReport.AgeCategory;
import org.immregistries.mqe.hub.report.vaccineReport.VaccineReportGroup;
import org.immregistries.mqe.hub.report.vaccineReport.VaccineReportStatus;

public class VaccineAdministered {

  public static final String REPORT_STYLE_CLASS_PRESENT_EXPECTED = "vaccineTypePresentExpected";
  public static final String REPORT_STYLE_CLASS_PRESENT_POSSIBLE = "vaccineTypePresentPossible";
  public static final String REPORT_STYLE_CLASS_PRESENT_NOT_EXPECTED = "vaccineTypePresentNotExpected";
  public static final String REPORT_STYLE_CLASS_PRESENT_NOT_POSSIBLE = "vaccineTypePresentNotPossible";
  public static final String REPORT_STYLE_CLASS_NOT_PRESENT_EXPECTED = "vaccineTypeNotPresentExpected";
  public static final String REPORT_STYLE_CLASS_NOT_PRESENT_POSSIBLE = "vaccineTypeNotPresentPossible";
  public static final String REPORT_STYLE_CLASS_NOT_PRESENT_NOT_EXPECTED = "vaccineTypeNotPresentNotExpected";
  public static final String REPORT_STYLE_CLASS_NOT_PRESENT_NOT_POSSIBLE = "vaccineTypeNotPresentNotPossible";
  
  
  // Total Number of Vaccination Visits
  private int vaccinationVisits;
  private int count;
  private String status;
  private VaccineReportGroup vaccine;
  private double percent;
  private AgeCategory age;
  private VaccineReportStatus vaccineReportStatus;
  private String reportStyleClass;

  public String getReportStyleClass() {
    return reportStyleClass;
  }

  public void setReportStyleClass(String reportStyleClass) {
    this.reportStyleClass = reportStyleClass;
  }

  public VaccineReportStatus getVaccineReportStatus() {
    return vaccineReportStatus;
  }

  public int getAgeOrder() {
    return age == null ? 0 : age.getOrder();
  }

  public void setVaccineReportStatus(VaccineReportStatus vaccineReportStatus) {
    this.vaccineReportStatus = vaccineReportStatus;
  }

  public int getVaccinationVisits() {
    return vaccinationVisits;
  }

  public void setVaccinationVisits(int vaccinationVisits) {
    this.vaccinationVisits = vaccinationVisits;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public VaccineReportGroup getVaccine() {
    return vaccine;
  }

  public void setVaccine(VaccineReportGroup vaccine) {
    this.vaccine = vaccine;
  }

  public double getPercent() {
    return percent;
  }

  public void setPercent(double percent) {
    this.percent = percent;
  }

  public AgeCategory getAge() {
    return age;
  }

  public void setAge(AgeCategory age) {
    this.age = age;
  }


}
