package org.immregistries.mqe.hub.detection.documentation;

import java.util.List;

public class DocumentationTableRow {
	public static class Details {
		
		private String rule;
		private String details;
		
		public Details(String rule, String details) {
			super();
			this.rule = rule;
			this.details = details;
		}
		public String getRule() {
			return rule;
		}
		public void setRule(String rule) {
			this.rule = rule;
		}
		public String getDetails() {
			return details;
		}
		public void setDetails(String details) {
			this.details = details;
		}
	}
	
	private String code;
	private String field;
	private String description;
	private boolean active;
	private String target;
	private String severity;
	private List<Details> details;
	
	public DocumentationTableRow(String code, String description, boolean active, String field, String target, String severity,
			List<Details> details) {
		super();
		this.code = code;
		this.description = description;
		this.active = active;
		this.field = field;
		this.target = target;
		this.severity = severity;
		this.details = details;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getSeverity() {
		return severity;
	}
	public void setSeverity(String severity) {
		this.severity = severity;
	}
	public List<Details> getDetails() {
		return details;
	}
	public void setDetails(List<Details> details) {
		this.details = details;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	
	
	
}
