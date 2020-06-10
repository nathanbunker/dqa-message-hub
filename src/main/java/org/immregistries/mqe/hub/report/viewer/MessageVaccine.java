package org.immregistries.mqe.hub.report.viewer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "MESSAGE_VACCINE")
public class MessageVaccine {

	@Id
	@SequenceGenerator(name = "MESSAGE_VACCINE_GENERATOR", sequenceName = "MESSAGE_VACCINE_SEQ")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MESSAGE_VACCINE_GENERATOR")
	@Column(name = "MESSAGE_VACCINE_ID")
	private long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MESSAGE_METADATA_ID")
	private MessageMetadata messageMetadata;

	private String vaccineCvx;
	private int count;
	private int age;
	private boolean administered;

	public boolean isAdministered() {
		return administered;
	}

	public void setAdministered(boolean administered) {
		this.administered = administered;
	}

	public MessageMetadata getMessageMetadata() {
		return messageMetadata;
	}

	public void setMessageMetadata(MessageMetadata messageMetadata) {
		this.messageMetadata = messageMetadata;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getVaccineCvx() {
		return vaccineCvx;
	}

	public void setVaccineCvx(String vaccineCvx) {
		this.vaccineCvx = vaccineCvx;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

}
