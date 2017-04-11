package org.immregistries.dqa.hub.report;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="SENDER_ATTR_METRICS")
public class SenderAttributeMetrics {
	@Id
	@SequenceGenerator(name="SENDER_ATTR_METRICS_GENERATOR", sequenceName="SENDER_ATTR_METRICS_SEQ", allocationSize = 100)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SENDER_ATTR_METRICS_GENERATOR")
	@Column(name="SENDER_ATTR_METRICS_ID")
	private long id;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="sender_metrics_id")
	private SenderMetrics senderMetrics;
	
	private String dqaAttributeCode;
	
	private int attributeCount = 0;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getDqaAttributeCode() {
		return dqaAttributeCode;
	}
	public void setDqaAttributeCode(String dqaAttributeCode) {
		this.dqaAttributeCode = dqaAttributeCode;
	}
	public int getAttributeCount() {
		return attributeCount;
	}
	public void setAttributeCount(int attributeCount) {
		this.attributeCount = attributeCount;
	}
	public SenderMetrics getSenderMetrics() {
		return senderMetrics;
	}
	public void setSenderMetrics(SenderMetrics senderMetrics) {
		this.senderMetrics = senderMetrics;
	}
}
