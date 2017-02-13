package org.immregistries.dqa.hub.report;

import org.immregistries.dqa.validator.DqaMessageServiceResponse;
import org.springframework.stereotype.Service;

@Service
public class MessageEvaluator {
	
	public MessageEvaluation evaluate(DqaMessageServiceResponse validationResults) {
		MessageEvaluation me = new MessageEvaluation();
		
		return me;
	}
}
