package org.immregistries.mqe.hub.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "FACILITY_DETECTION_COUNTS")
public class FacilityDetections {

  @Id
  @SequenceGenerator(name = "FACILITY_DETECTIONS_GENERATOR", sequenceName = "FACILITY_DETECTIONS_SEQ", allocationSize = 100)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FACILITY_DETECTIONS_GENERATOR")
  @Column(name = "FACILITY_DETECTIONS_ID")
  private long id;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "FACILITY_MESSAGE_COUNTS_ID")
  private FacilityMessageCounts facilityMessageCounts;
  private String mqeDetectionCode;
  private int attributeCount = 0;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getMqeDetectionCode() {
    return mqeDetectionCode;
  }

  public void setMqeDetectionCode(String mqeDetectionCode) {
    this.mqeDetectionCode = mqeDetectionCode;
  }

  public int getAttributeCount() {
    return attributeCount;
  }

  public void setAttributeCount(int attributeCount) {
    this.attributeCount = attributeCount;
  }

  public FacilityMessageCounts getFacilityMessageCounts() {
    return facilityMessageCounts;
  }

  public void setFacilityMessageCounts(FacilityMessageCounts facilityMessageCounts) {
    this.facilityMessageCounts = facilityMessageCounts;
  }
}
