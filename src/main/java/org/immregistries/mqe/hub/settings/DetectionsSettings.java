package org.immregistries.mqe.hub.settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "DETECTIONS_SETTINGS", uniqueConstraints = @UniqueConstraint(name = "UNIQUE_DETECTIONS_SETTINGS", columnNames = {
	    "mqeCode", "groupId"}))
public class DetectionsSettings {
	@Id
	@SequenceGenerator(name = "DETECTIONS_SETTINGS_GENERATOR", sequenceName = "DETECTIONS_SETTINGS_SEQ", allocationSize = 5)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DETECTIONS_SETTINGS_GENERATOR")
	@Column(name = "DETECTIONS_SETTINGS_ID")
	private long id;

	private String groupId;
	private String mqeCode;
	private String severity;
	
	public DetectionsSettings() {}
	
	public DetectionsSettings(String groupId, String mqeCode, String severity) {
		this.groupId = groupId;
		this.mqeCode = mqeCode;
		this.severity = severity;
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

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	@Override
	public String toString() {
		return "DetectionsSettings [id=" + id + ", groupId="+groupId+", mqeCode=" + mqeCode + ", severity=" + severity + "]";
	}

}
