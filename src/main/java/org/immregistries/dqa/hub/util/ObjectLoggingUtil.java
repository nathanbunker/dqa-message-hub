package org.immregistries.dqa.hub.util;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ObjectLoggingUtil {

	private static final Logger logger = LoggerFactory
			.getLogger(ObjectLoggingUtil.class);

	public String jsonify(Object o) {
		ObjectMapper mapr = new ObjectMapper();
		String jsonOutputString = "";
		try {
			jsonOutputString = mapr.writeValueAsString(o);
		} catch (JsonProcessingException e) {
			logger.warn("error turning the object into JSON", e);
			jsonOutputString = ReflectionToStringBuilder.toString(0);
		}
		
		return "\"" + o.getClass().getSimpleName() + "\":" + jsonOutputString;
	}
	
	public String logify(String methodName, Object o) {
		return "\"" + methodName + "\": {" + jsonify(o) + "}";
	}
}
