package org.immregistries.mqe.hub.detection.documentation;

import org.immregistries.mqe.validator.detection.DetectionDocumentation;

public interface DetectionDocumentationSerializer<T> {

	T serialize(DetectionDocumentation documentation) throws Exception;
	
}
