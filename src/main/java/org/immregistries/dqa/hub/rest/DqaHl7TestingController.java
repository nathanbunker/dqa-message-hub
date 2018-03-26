package org.immregistries.dqa.hub.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.immregistries.dqa.hub.rest.model.Hl7MessageSubmission;
import org.immregistries.dqa.hub.submission.Hl7MessageConsumer;
import org.immregistries.dqa.validator.DqaMessageService;
import org.immregistries.dqa.validator.DqaMessageServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/tester")
@RestController
public class DqaHl7TestingController {

  private static final Log logger = LogFactory.getLog(DqaHl7TestingController.class);

  @Autowired
  private Hl7MessageConsumer messageConsumer;

  @RequestMapping(value = "hl7", method = RequestMethod.POST)
  public String hl7MessageInterface(
      @RequestBody String message) throws Exception {

    logger.info("hl7MessageInterface demo!");

    Hl7MessageSubmission messageSubmission = new Hl7MessageSubmission();
    messageSubmission.setMessage(message);

    return messageConsumer.processMessageAndMakeAck(messageSubmission).getAck();
  }


  @RequestMapping(value = "hl7/validationList", method = RequestMethod.POST)
  public DqaMessageServiceResponse hl7ValidationList(
      @RequestBody String message) throws Exception {
    logger.info("hl7ValidationList demo!");
    //send through the Validator in the DQA Validator project.
    DqaMessageService validator = DqaMessageService.INSTANCE;
    return validator.processMessage(message);
    //Use the results to build an ACK using the DQA util project.
  }

}
