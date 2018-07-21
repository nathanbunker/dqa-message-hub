package org.immregistries.mqe.hub.report;

import org.immregistries.mqe.validator.MqeMessageServiceResponse;
import org.springframework.stereotype.Service;

@Service
public class MessageEvaluator {

  public MessageEvaluation evaluate(MqeMessageServiceResponse validationResults) {
    MessageEvaluation me = new MessageEvaluation();

    return me;
  }
}
