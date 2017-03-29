package org.immregistries.dqa.hub.persistence;

import org.apache.commons.lang3.StringUtils;

public class ProviderInfo {
	private String pin;
	private String name;
	private long id;
	
	
	
	@Override
	public String toString() {
		return "FacilityInfo [pin=" + pin + ", name=" + name + ", id=" + id
				+ "]";
	}
	
	/**
	 * @return the pin
	 */
	public String getPin() {
		return pin;
	}
	/**
	 * @param pin the pin to set
	 */
	public void setPin(String pin) {
		this.pin = pin;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param inName the name to set
	 */
	public void setName(String inName) {
		if (StringUtils.isNotBlank(inName)) {
			inName = inName.replace("VXU ", "");
		}
		
		this.name = inName;
	}
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
 
	
}
