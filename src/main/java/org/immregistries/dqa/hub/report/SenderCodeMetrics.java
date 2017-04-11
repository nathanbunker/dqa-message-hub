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
@Table(name="SENDER_CODE_METRICS")
public class SenderCodeMetrics {
	@Id
	@SequenceGenerator(name="SENDER_CODE_METRICS_GENERATOR", sequenceName="SENDER_CODE_METRICS_SEQ", allocationSize = 100)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SENDER_CODE_METRICS_GENERATOR")
	@Column(name="SENDER_CODE_METRICS_ID")
	private long id;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="sender_metrics_id")
	private SenderMetrics senderMetrics;
	
	private String codeSet;
	private String value;
	private int codeCount;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getCodeSet() {
		return codeSet;
	}
	public void setCodeSet(String codeSet) {
		this.codeSet = codeSet;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getCodeCount() {
		return codeCount;
	}
	public void setCodeCount(int count) {
		this.codeCount = count;
	}
	public SenderMetrics getSenderMetrics() {
		return senderMetrics;
	}
	public void setSenderMetrics(SenderMetrics senderMetrics) {
		this.senderMetrics = senderMetrics;
	}
}
