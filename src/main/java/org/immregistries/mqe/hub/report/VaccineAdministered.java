package org.immregistries.mqe.hub.report;

public class VaccineAdministered {

	// Total Number of Vaccination Visits
	private int vaccinationVisits;
	private int count;
	private String status;
	private VaccineReportGroup vaccine;
	private double percent;
	private AgeCategory age;
	
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
