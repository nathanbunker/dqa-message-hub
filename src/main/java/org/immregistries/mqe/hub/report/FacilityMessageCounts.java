package org.immregistries.mqe.hub.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "FACILITY_MESSAGE_COUNTS", indexes = { @Index(name = "IDX_FACILITY_MESSAGE_COUNTS_X1", columnList = "username, uploadDate") })
public class FacilityMessageCounts {

  @Id
  @SequenceGenerator(name = "FACILITY_MESSAGE_COUNTS_GENERATOR", sequenceName = "FACILITY_MESSAGE_COUNTS_SEQ")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FACILITY_MESSAGE_COUNTS_GENERATOR")
  @Column(name = "FACILITY_MESSAGE_COUNTS_ID")
  private long id;


  @NotNull
  @ManyToOne(fetch= FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinColumn(name="FACILITY_ID")
  private Facility facility;

  @javax.persistence.Temporal(TemporalType.DATE)
  private Date uploadDate;

  private int patientCount;

  private int vaccinationCount;

  private int score;

  private String username;

  @OneToMany(mappedBy = "facilityMessageCounts", cascade = CascadeType.ALL)
  private List<FacilityCodeCount> codes = new ArrayList<>();

  @OneToMany(mappedBy = "facilityMessageCounts", cascade = CascadeType.ALL)
  private List<FacilityDetections> detectionMetrics = new ArrayList<>();

  @OneToMany(mappedBy = "facilityMessageCounts", cascade = CascadeType.ALL)
  private List<FacilityVaccineCounts> facilityVaccineCounts = new ArrayList<>();

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public Facility getFacility() {
    return facility;
  }

  public void setFacility(Facility facility) {
    this.facility = facility;
  }

  public int getPatientCount() {
    return patientCount;
  }

  public void setPatientCount(int patientCount) {
    this.patientCount = patientCount;
  }

  public int getVaccinationCount() {
    return vaccinationCount;
  }

  public void setVaccinationCount(int vaccinationCount) {
    this.vaccinationCount = vaccinationCount;
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public List<FacilityCodeCount> getCodes() {
    return codes;
  }

  public void setCodes(List<FacilityCodeCount> codes) {
    this.codes = codes;
  }

  public List<FacilityDetections> getDetectionMetrics() {
    return detectionMetrics;
  }

  public void setDetectionMetrics(List<FacilityDetections> detectionMetrics) {
    this.detectionMetrics = detectionMetrics;
  }

  public List<FacilityVaccineCounts> getFacilityVaccineCounts() {
    return facilityVaccineCounts;
  }

  public void setFacilityVaccineCounts(List<FacilityVaccineCounts> facilityVaccineCounts) {
    this.facilityVaccineCounts = facilityVaccineCounts;
  }

  public Date getUploadDate() {
    return uploadDate;
  }

  public void setUploadDate(Date metricsDate) {
    this.uploadDate = metricsDate;
  }

  @Override
  public String toString() {
    return "FacilityMessageCounts{" + "id=" + id + ", sender='" + facility + '\'' + ", metricsDate="
        + uploadDate + ", patientCount=" + patientCount
        + ", vaccinationCount=" + vaccinationCount + ", score=" + score + ", codes=" + codes
        + ", detectionMetrics=" + detectionMetrics + '}';
  }
}
