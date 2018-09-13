package org.immregistries.mqe.hub.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.TemporalType;

@Entity
@Table(name = "SENDER_METRICS", indexes = { @Index(name = "IDX_SENDER_METRICS_X1", columnList = "SENDER, metricsDate") })
public class SenderMetrics {

  @Id
  @SequenceGenerator(name = "SENDER_METRICS_GENERATOR", sequenceName = "SENDER_METRICS_SEQ")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SENDER_METRICS_GENERATOR")
  @Column(name = "SENDER_METRICS_ID")
  private long id;

  private String sender;

  @javax.persistence.Temporal(TemporalType.DATE)
  private Date metricsDate;

  private int patientCount;

  private int vaccinationCount;

  private int score;

  @OneToMany(mappedBy = "senderMetrics", cascade = CascadeType.ALL)
  private List<CodeCount> codes = new ArrayList<>();

  @OneToMany(mappedBy = "senderMetrics", cascade = CascadeType.ALL)
  private List<SenderDetectionMetrics> detectionMetrics = new ArrayList<>();

  @OneToMany(mappedBy = "senderMetrics", cascade = CascadeType.ALL)
  private List<VaccineCount> vaccineCounts = new ArrayList<>();

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
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

  public List<CodeCount> getCodes() {
    return codes;
  }

  public void setCodes(List<CodeCount> codes) {
    this.codes = codes;
  }

  public List<SenderDetectionMetrics> getDetectionMetrics() {
    return detectionMetrics;
  }

  public void setDetectionMetrics(List<SenderDetectionMetrics> detectionMetrics) {
    this.detectionMetrics = detectionMetrics;
  }

  public List<VaccineCount> getVaccineCounts() {
    return vaccineCounts;
  }

  public void setVaccineCounts(List<VaccineCount> vaccineCounts) {
    this.vaccineCounts = vaccineCounts;
  }

  public Date getMetricsDate() {
    return metricsDate;
  }

  public void setMetricsDate(Date metricsDate) {
    this.metricsDate = metricsDate;
  }

  @Override
  public String toString() {
    return "SenderMetrics{" + "id=" + id + ", sender='" + sender + '\'' + ", metricsDate="
        + metricsDate + ", patientCount=" + patientCount
        + ", vaccinationCount=" + vaccinationCount + ", score=" + score + ", codes=" + codes
        + ", detectionMetrics=" + detectionMetrics + '}';
  }
}
