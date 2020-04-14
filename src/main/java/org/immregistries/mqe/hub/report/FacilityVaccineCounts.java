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
@Table(name = "FACILITY_VACCINE_COUNTS")
public class FacilityVaccineCounts {

  @Id
  @SequenceGenerator(name = "FACILITY_VACCINE_COUNTS_GENERATOR", sequenceName = "FACILITY_VACCINE_COUNTS_SEQ", allocationSize = 100)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FACILITY_VACCINE_COUNTS_GENERATOR")
  @Column(name = "FACILITY_VACCINE_COUNTS_ID")
  private long id;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "FACILITY_MESSAGE_COUNTS_ID")
  private FacilityMessageCounts facilityMessageCounts;

  private int age = 0;
  private String vaccineCvx;
  private int count = 0;
  private boolean administered;

  public FacilityMessageCounts getFacilityMessageCounts() {
    return facilityMessageCounts;
  }

  public void setFacilityMessageCounts(FacilityMessageCounts facilityMessageCounts) {
    this.facilityMessageCounts = facilityMessageCounts;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public String getVaccineCvx() {
    return vaccineCvx;
  }

  public void setVaccineCvx(String vaccineCvx) {
    this.vaccineCvx = vaccineCvx;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public boolean isAdministered() {
    return administered;
  }

  public void setAdministered(boolean administered) {
    this.administered = administered;
  }

@Override
public String toString() {
	return "FacilityVaccineCounts [id=" + id + ", age=" + age + ", vaccineCvx=" + vaccineCvx
			+ ", count=" + count + ", administered=" + administered + "]";
}
}
