package org.immregistries.mqe.hub.settings;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

//, uniqueConstraints = @UniqueConstraint(name = "UNIQUE_DETECTION_SETTINGS", columnNames = {"mqeCode", "settingsGroup"})
@Entity
@Table(name = "DETECTION_SEVERITY")
public class DetectionSeverity {
	@Id
	private String mqeCode;
	private String severity;
	
	public DetectionSeverity() {}
	
	public DetectionSeverity(DetectionSeverityOverrideGroup sg, String mqeCode, String severity) {
		this.mqeCode = mqeCode;
		this.severity = severity;
	}

	public String getMqeCode() {
		return mqeCode;
	}

	public void setMqeCode(String mqeCode) {
		this.mqeCode = mqeCode;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

}
