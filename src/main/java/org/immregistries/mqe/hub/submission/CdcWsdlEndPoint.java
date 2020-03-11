package org.immregistries.mqe.hub.submission;

import javax.persistence.EntityManager;
import org.immregistries.mqe.cdc_wsdl.SubmitSingleMessageRequestType;
import org.immregistries.mqe.cdc_wsdl.SubmitSingleMessageResponseType;
import org.immregistries.mqe.hub.rest.MessageInputController;
import org.immregistries.mqe.hub.rest.model.Hl7MessageSubmission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;

@Endpoint
public class CdcWsdlEndPoint {
  private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";


  @Autowired
  private IISGateway iisGatewayService;

  @Autowired
  private Hl7MessageConsumer messageConsumer;

  @Autowired
  EntityManager em;

  // @PayloadRoot(namespace = "", localPart = "");
  public SubmitSingleMessageResponseType submitSingleMessage(
      @RequestPayload SubmitSingleMessageRequestType request) {
    Hl7MessageSubmission messageSubmission = new Hl7MessageSubmission();
    messageSubmission.setMessage(request.getHl7Message());
    messageSubmission.setUser(request.getUsername());
    messageSubmission.setPassword(request.getPassword());
    messageSubmission.setFacilityCode(request.getFacilityID());

    SubmitSingleMessageResponseType response = new SubmitSingleMessageResponseType();
    if (MessageInputController.isQBP(request.getHl7Message())) {
      response.setReturn(iisGatewayService.queryIIS(messageSubmission));
    } else {
      String ack = messageConsumer.processMessageAndSaveMetrics(messageSubmission).getAck();

      em.flush();
      em.clear();
      response.setReturn(ack);
    }
    return response;

  }
}
