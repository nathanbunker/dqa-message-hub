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
import org.immregistries.mqe.hub.report.DetectionSettingsGroup;
//, uniqueConstraints = @UniqueConstraint(name = "UNIQUE_DETECTIONS_SETTINGS", columnNames = {"mqeCode", "settingsGroup"})
@Entity
@Table(name = "DETECTIONS_SETTINGS")
public class DetectionsSettings {
	@Id
	@SequenceGenerator(name = "DETECTIONS_SETTINGS_GENERATOR", sequenceName = "DETECTIONS_SETTINGS_SEQ", allocationSize = 5)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DETECTIONS_SETTINGS_GENERATOR")
	@Column(name = "DETECTIONS_SETTINGS_ID")
	private long id;

	@OneToOne
	@JoinColumn(name="DETECTION_SETTINGS_GROUP_ID")
	private DetectionSettingsGroup detectionSettingsGroup;
	private String mqeCode;
	private String severity;
	
	public DetectionsSettings() {}
	
	public DetectionsSettings(DetectionSettingsGroup sg, String mqeCode, String severity) {
		this.detectionSettingsGroup = sg;
		this.mqeCode = mqeCode;
		this.severity = severity;
	}

	public DetectionSettingsGroup getDetectionSettingsGroup() {
		return detectionSettingsGroup;
	}

	public void setDetectionSettingsGroup(DetectionSettingsGroup detectionSettingsGroup) {
		this.detectionSettingsGroup = detectionSettingsGroup;
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
				", settingsGroup=" + detectionSettingsGroup +
				", mqeCode='" + mqeCode + '\'' +
				", severity='" + severity + '\'' +
				'}';
	}
}
