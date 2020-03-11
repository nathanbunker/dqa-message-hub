package org.immregistries.mqe.hub.submission;

import org.immregistries.mqe.cdc_wsdl.SubmitSingleMessageRequestType;
import org.immregistries.mqe.cdc_wsdl.SubmitSingleMessageResponseType;
import org.immregistries.mqe.hub.rest.model.Hl7MessageSubmission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

@Component
public class SubmitSingleMessageClient extends WebServiceGatewaySupport {

  @Autowired
  private IISGateway iisGatewayService;


  public String submit(Hl7MessageSubmission messageSubmission) {
    SubmitSingleMessageRequestType request = new SubmitSingleMessageRequestType();
    request.setHl7Message(messageSubmission.getMessage());
    request.setFacilityID(messageSubmission.getFacilityCode());
    request.setPassword(messageSubmission.getPassword());
    request.setUsername(messageSubmission.getUser());

    // iisGatewayService.getIisgatewayUrl()
    SubmitSingleMessageResponseType response =
        (SubmitSingleMessageResponseType) getWebServiceTemplate()
            .marshalSendAndReceive("http://florence.immregistries.org/iis-sandbox/soap", request);
    if (response != null) {
      return response.getReturn();
    }
    return null;
  }
  
  
}
