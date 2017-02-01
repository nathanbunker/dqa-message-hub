package org.immregistries.dqa.hub.cfg;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "dqa")
public class DqaMessageHubApplicationProperties {
	
	private static final Logger logger = LoggerFactory
			.getLogger(DqaMessageHubApplicationProperties.class);
	
	private String testName;
	private int interestingProperty;

	@PostConstruct
	public void postInit() {
		logger.info("Initializing Extra Values");
		//things here happen after it gets all the properties it can 
		//find in the file. 
		//Do other DBParameter lookups here. 
		
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public int getInterestingProperty() {
		return interestingProperty;
	}

	public void setInterestingProperty(int interestingProperty) {
		this.interestingProperty = interestingProperty;
	}
	
	}
