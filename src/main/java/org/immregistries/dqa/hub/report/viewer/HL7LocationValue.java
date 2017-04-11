package org.immregistries.dqa.hub.report.viewer;

public class HL7LocationValue {
	private String value;
	private String location;
	private int fieldRepetition;
	private int segmentIndex;
	private int valueIndex;//this is the index of the value in the message!
	private String locationDescription;
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	/**
	 * @return the locationDescription
	 */
	public String getLocationDescription() {
		return locationDescription;
	}
	/**
	 * @param locationDescription the locationDescription to set
	 */
	public void setLocationDescription(String locationDescription) {
		this.locationDescription = locationDescription;
	}
	/**
	 * @return the fieldRepetition
	 */
	public int getFieldRepetition() {
		return fieldRepetition;
	}
	/**
	 * @param fieldRepetition the fieldRepetition to set
	 */
	public void setFieldRepetition(int fieldRepetition) {
		this.fieldRepetition = fieldRepetition;
	}
	/**
	 * @return the segmentIndex
	 */
	public int getSegmentIndex() {
		return segmentIndex;
	}
	/**
	 * @param segmentIndex the segmentIndex to set
	 */
	public void setSegmentIndex(int segmentIndex) {
		this.segmentIndex = segmentIndex;
	}
	public int getValueIndex() {
		return valueIndex;
	}
	public void setValueIndex(int valueIndex) {
		this.valueIndex = valueIndex;
	}
	
	
}
