package org.immregistries.mqe.hub.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.immregistries.mqe.hub.rest.model.Hl7MessageSubmission;
import org.immregistries.mqe.hub.submission.Hl7MessageConsumer;
import org.immregistries.mqe.validator.MqeMessageService;
import org.immregistries.mqe.validator.MqeMessageServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/tester")
@RestController
public class MqeHl7TestingController {

  private static final Log logger = LogFactory.getLog(MqeHl7TestingController.class);

  @Autowired
  private Hl7MessageConsumer messageConsumer;

  @RequestMapping(value = "hl7", method = RequestMethod.POST)
  public String hl7MessageInterface(
      @RequestBody String message) throws Exception {

    logger.info("hl7MessageInterface demo!");

    Hl7MessageSubmission messageSubmission = new Hl7MessageSubmission();
    messageSubmission.setMessage(message);

    return messageConsumer.processMessageAndSaveMetrics(messageSubmission).getAck();
  }


  @RequestMapping(value = "hl7/validationList", method = RequestMethod.POST)
  public MqeMessageServiceResponse hl7ValidationList(
      @RequestBody String message) throws Exception {
    logger.info("hl7ValidationList demo!");
    //send through the Validator in the MQE Validator project.
    MqeMessageService validator = MqeMessageService.INSTANCE;
    return validator.processMessage(message);
    //Use the results to build an ACK using the MQE util project.
  }

}
