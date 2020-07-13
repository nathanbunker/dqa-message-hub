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
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "FACILITY_CODE_COUNTS")
//, uniqueConstraints = @UniqueConstraint(name = "UNIQUE_CODE_COUNT", columnNames = {
//    "ATTRIBUTE", "FACILITY_MESSAGE_COUNTS_ID", "CODE_TYPE", "CODE_VALUE"}))
public class FacilityCodeCount {

  @Id
  @SequenceGenerator(name = "CODE_COUNT_GENERATOR", sequenceName = "CODE_COUNT_SEQ", allocationSize = 100)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CODE_COUNT_GENERATOR")
  @Column(name = "CODE_COUNT_ID")
  private long id;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "FACILITY_MESSAGE_COUNTS_ID")
  private FacilityMessageCounts facilityMessageCounts;
  private String codeType;
  private String origin;
  private String attribute;
  private String codeStatus;
  private String codeValue;
  private int codeCount;

  public String getCodeStatus() {
    return codeStatus;
  }

  public void setCodeStatus(String codeStatus) {
    this.codeStatus = codeStatus;
  }

  public long getId() {
    return id;
  }

  public String getAttribute() {
    return attribute;
  }

  public void setAttribute(String attribute) {
    this.attribute = attribute;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getCodeType() {
    return codeType;
  }

  public void setCodeType(String codeType) {
    this.codeType = codeType;
  }

  public String getCodeValue() {
    return codeValue;
  }

  public void setCodeValue(String codeValue) {
    this.codeValue = codeValue;
  }

  public int getCodeCount() {
    return codeCount;
  }

  public void setCodeCount(int count) {
    this.codeCount = count;
  }

  public FacilityMessageCounts getFacilityMessageCounts() {
    return facilityMessageCounts;
  }

  public void setFacilityMessageCounts(FacilityMessageCounts facilityMessageCounts) {
    this.facilityMessageCounts = facilityMessageCounts;
  }

  public String getOrigin() {
    return origin;
  }

  public void setOrigin(String origin) {
    this.origin = origin;
  }

  @Override
  public String toString() {
    return "CodeCount{" + "id=" + id + ", facilityMessageCounts=" + facilityMessageCounts.getId() + ", codeType='"
        + codeType + '\'' + ", attribute='" + attribute + '\''
        + ", codeValue='" + codeValue + '\'' + ", codeCount=" + codeCount + '}';
  }
}
