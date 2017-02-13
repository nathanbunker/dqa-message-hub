package org.immregistries.dqa.hub.report;

import java.util.List;

import org.immregistries.dqa.hl7util.builder.AckResult;
import org.immregistries.dqa.validator.report.Measureable;
import org.immregistries.dqa.validator.report.ReportableCode;
import org.joda.time.DateTime;

public class MessageEvaluation {
	
	private List<Measureable> messageMeasures;
	private List<ReportableCode> messageCodes;
	private int vaccineCount;
	private AckResult messageResult;
	private boolean messageProcessed;
	private int patientAge;
	private DateTime messageReceivedTime;
	
	public int getVaccineCount() {
		return vaccineCount;
	}
	public void setVaccineCount(int vaccineCount) {
		this.vaccineCount = vaccineCount;
	}
	public AckResult getMessageResult() {
		return messageResult;
	}
	public void setMessageResult(AckResult messageResult) {
		this.messageResult = messageResult;
	}
	public boolean isMessageProcessed() {
		return messageProcessed;
	}
	public void setMessageProcessed(boolean messageProcessed) {
		this.messageProcessed = messageProcessed;
	}
	public int getPatientAge() {
		return patientAge;
	}
	public void setPatientAge(int patientAge) {
		this.patientAge = patientAge;
	}
	public DateTime getMessageReceivedTime() {
		return messageReceivedTime;
	}
	public void setMessageReceivedTime(DateTime messageReceivedTime) {
		this.messageReceivedTime = messageReceivedTime;
	}
	public List<Measureable> getMessageMeasures() {
		return messageMeasures;
	}
	public void setMessageMeasures(List<Measureable> messageMeasures) {
		this.messageMeasures = messageMeasures;
	}
	public List<ReportableCode> getMessageCodes() {
		return messageCodes;
	}
	public void setMessageCodes(List<ReportableCode> messageCodes) {
		this.messageCodes = messageCodes;
	}
}
