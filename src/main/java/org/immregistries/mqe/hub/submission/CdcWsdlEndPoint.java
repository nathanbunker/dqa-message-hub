package org.immregistries.mqe.hub.submission;

import javax.persistence.EntityManager;
import javax.xml.bind.JAXBElement;
import org.immregistries.mqe.cdc_wsdl.ConnectivityTestRequestType;
import org.immregistries.mqe.cdc_wsdl.ConnectivityTestResponseType;
import org.immregistries.mqe.cdc_wsdl.ObjectFactory;
import org.immregistries.mqe.cdc_wsdl.SubmitSingleMessageRequestType;
import org.immregistries.mqe.cdc_wsdl.SubmitSingleMessageResponseType;
import org.immregistries.mqe.hub.rest.MessageInputController;
import org.immregistries.mqe.hub.rest.model.Hl7MessageSubmission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class CdcWsdlEndPoint {
  private static final String NAMESPACE_URI = "urn:cdc:iisb:2011";


  @Autowired
  private IISGateway iisGatewayService;

  @Autowired
  private Hl7MessageConsumer messageConsumer;

  @Autowired
  EntityManager em;

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "connectivityTest")
  @ResponsePayload
  public JAXBElement<ConnectivityTestResponseType> connectivityTest(
      @RequestPayload JAXBElement<ConnectivityTestRequestType> request) {
    ConnectivityTestResponseType response = new ConnectivityTestResponseType();
    response.setReturn("You said: " + request.getValue().getEchoBack());
    ObjectFactory of = new ObjectFactory();
    return of.createConnectivityTestResponse(response);
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "submitSingleMessage")
  @ResponsePayload
  public JAXBElement<SubmitSingleMessageResponseType> submitSingleMessage(
      @RequestPayload JAXBElement<SubmitSingleMessageRequestType> request) {

    Hl7MessageSubmission messageSubmission = new Hl7MessageSubmission();
    messageSubmission.setMessage(request.getValue().getHl7Message());
    messageSubmission.setUser(request.getValue().getUsername());
    messageSubmission.setPassword(request.getValue().getPassword());
    messageSubmission.setFacilityCode(request.getValue().getFacilityID());

    SubmitSingleMessageResponseType response = new SubmitSingleMessageResponseType();
    String ack = messageConsumer.processMessageAndSaveMetrics(messageSubmission).getAck();
    response.setReturn(ack);
    ObjectFactory of = new ObjectFactory();
    return of.createSubmitSingleMessageResponse(response);
  }
}
