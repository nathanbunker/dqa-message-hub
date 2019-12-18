package org.immregistries.mqe.hub.settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.immregistries.mqe.hub.report.SeverityGroup;
//, uniqueConstraints = @UniqueConstraint(name = "UNIQUE_DETECTIONS_SETTINGS", columnNames = {"mqeCode", "detectionGroup"})
@Entity
@Table(name = "DETECTIONS_SETTINGS")
public class DetectionsSettings {
	@Id
	@SequenceGenerator(name = "DETECTIONS_SETTINGS_GENERATOR", sequenceName = "DETECTIONS_SETTINGS_SEQ", allocationSize = 5)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DETECTIONS_SETTINGS_GENERATOR")
	@Column(name = "DETECTIONS_SETTINGS_ID")
	private long id;

	@OneToOne
	private SeverityGroup detectionGroup;
	private String mqeCode;
	private String severity;
	
	public DetectionsSettings() {}
	
	public DetectionsSettings(SeverityGroup sg, String mqeCode, String severity) {
		this.detectionGroup = sg;
		this.mqeCode = mqeCode;
		this.severity = severity;
	}

	public SeverityGroup getDetectionGroup() {
		return detectionGroup;
	}

	public void setDetectionGroup(SeverityGroup detectionGroup) {
		this.detectionGroup = detectionGroup;
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
				", detectionGroup=" + detectionGroup +
				", mqeCode='" + mqeCode + '\'' +
				", severity='" + severity + '\'' +
				'}';
	}
}
