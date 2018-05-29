package org.immregistries.dqa.hub.settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "MQE_SETTINGS", uniqueConstraints = @UniqueConstraint(name = "UNIQUE_MQE_SETTINGS", columnNames = {
	    "NAME"}))
public class MqeSettings {

	@Id
	@SequenceGenerator(name = "MQE_SETTINGS_GENERATOR", sequenceName = "MQE_SETTINGS_SEQ", allocationSize = 5)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MQE_SETTINGS_GENERATOR")
	@Column(name = "MQE_SETTINGS_ID")
	private long id;

	private String name;
	private String value;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "MqeSettings [id=" + id + ", name=" + name + ", value=" + value + "]";
	}

}
