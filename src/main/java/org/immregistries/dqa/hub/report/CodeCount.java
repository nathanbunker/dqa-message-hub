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
@Table(name="CODE_COUNT")
public class CodeCount {
	@Id
	@SequenceGenerator(name="CODE_COUNT_GENERATOR", sequenceName="CODE_COUNT_SEQ", allocationSize = 100)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CODE_COUNT_GENERATOR")
	@Column(name="CODE_COUNT_ID")
	private long id;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="sender_metrics_id")
	private SenderMetrics senderMetrics;
	
	private String codeType;
	private String attribute;
	private String codeValue;
	private int codeCount;

	public long getId() {
		return id;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getCodeType() {
		return codeType;
	}
	public void setCodeType(String codeType) {
		this.codeType = codeType;
	}
	public String getCodeValue() {
		return codeValue;
	}
	public void setCodeValue(String codeValue) {
		this.codeValue = codeValue;
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

    @Override public String toString() {
        return "CodeCount{" + "id=" + id + ", senderMetrics=" + senderMetrics.getId() + ", codeType='" + codeType + '\'' + ", attribute='" + attribute + '\''
            + ", codeValue='" + codeValue + '\'' + ", codeCount=" + codeCount + '}';
    }
}
