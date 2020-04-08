package org.immregistries.mqe.hub.submission;

import java.util.Arrays;
import javax.xml.bind.JAXBElement;
import org.immregistries.mqe.cdc_wsdl.ObjectFactory;
import org.immregistries.mqe.cdc_wsdl.SubmitSingleMessageRequestType;
import org.immregistries.mqe.cdc_wsdl.SubmitSingleMessageResponseType;
import org.immregistries.mqe.hub.rest.model.Hl7MessageSubmission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

@Component
public class SubmitSingleMessageClient extends WebServiceGatewaySupport {

  @Autowired
  private IISGateway iisGatewayService;

  private static Jaxb2Marshaller marshaller;


  @Bean
  @Qualifier("Jaxb2Marshaller")
  private static Jaxb2Marshaller marshaller() {
    if (marshaller == null) {
      System.out.println("--> getting marshaller");
      marshaller = new Jaxb2Marshaller();
      marshaller.setContextPath("org.immregistries.mqe.cdc_wsdl");
      System.out.println("--> marshaller 0 = " + marshaller);
    }
    return marshaller;
  }



  @Bean
  public SubmitSingleMessageClient getSubmitSingleMessage(Jaxb2Marshaller marshaller) {
    SubmitSingleMessageClient client = new SubmitSingleMessageClient();
    client.setDefaultUri("http://florence.immregistries.org/mqe/");
    client.setMarshaller(marshaller);
    client.setUnmarshaller(marshaller);

    return client;
  }


  public String submit(Hl7MessageSubmission messageSubmission) {
    SubmitSingleMessageRequestType request = new SubmitSingleMessageRequestType();
    request.setHl7Message(messageSubmission.getMessage());
    request.setFacilityID(messageSubmission.getFacilityCode());
    request.setPassword(messageSubmission.getPassword());
    request.setUsername(messageSubmission.getUser());

    System.out.println("--> submitting ");
    try {
      ObjectFactory of = new ObjectFactory();
      JAXBElement<SubmitSingleMessageRequestType> requestJ = of.createSubmitSingleMessage(request);
      System.out.println("--> marshaller 1 = " + marshaller);
      getWebServiceTemplate().setMarshaller(marshaller);
      getWebServiceTemplate().getMessageFactory();
      // iisGatewayService.getIisgatewayUrl()
      JAXBElement<SubmitSingleMessageResponseType> responseJ =
          (JAXBElement<SubmitSingleMessageResponseType>) getWebServiceTemplate()
              .marshalSendAndReceive("http://florence.immregistries.org/iis-sandbox/soap",
                  requestJ);
      SubmitSingleMessageResponseType response = responseJ.getValue();
      if (response != null) {
        return response.getReturn();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }


}
