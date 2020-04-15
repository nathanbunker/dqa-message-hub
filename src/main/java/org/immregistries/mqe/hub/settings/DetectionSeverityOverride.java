package org.immregistries.mqe.hub.settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

//, uniqueConstraints = @UniqueConstraint(name = "UNIQUE_DETECTION_SETTINGS", columnNames = {"mqeCode", "settingsGroup"})
@Entity
@Table(name = "DETECTION_SEVERITY_OVERRIDE")
public class DetectionSeverityOverride {
	@Id
	@SequenceGenerator(name = "DETECTION_SEVERITY_OVERRIDE_GENERATOR", sequenceName = "DETECTION_SEVERITY_OVERRIDE_SEQ", allocationSize = 5)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DETECTION_SEVERITY_OVERRIDE_GENERATOR")
	@Column(name = "DETECTION_SEVERITY_OVERRIDE_ID")
	private long id;

	@OneToOne
	@JoinColumn(name="DETECTION_SEVERITY_OVERRIDE_GROUP_ID")
	private DetectionSeverityOverrideGroup detectionSeverityOverrideGroup;
	private String mqeCode;
	private String severity;
	
	public DetectionSeverityOverride() {}
	
	public DetectionSeverityOverride(DetectionSeverityOverrideGroup sg, String mqeCode, String severity) {
		this.detectionSeverityOverrideGroup = sg;
		this.mqeCode = mqeCode;
		this.severity = severity;
	}

	public DetectionSeverityOverrideGroup getDetectionSeverityOverrideGroup() {
		return detectionSeverityOverrideGroup;
	}

	public void setDetectionSeverityOverrideGroup(
			DetectionSeverityOverrideGroup detectionSeverityOverrideGroup) {
		this.detectionSeverityOverrideGroup = detectionSeverityOverrideGroup;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	@Override
	public String toString() {
		return "DetectionsSettings{" +
				"id=" + id +
				", settingsGroup=" + detectionSeverityOverrideGroup +
				", mqeCode='" + mqeCode + '\'' +
				", severity='" + severity + '\'' +
				'}';
	}
}
