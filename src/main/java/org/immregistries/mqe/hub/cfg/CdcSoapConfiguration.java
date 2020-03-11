package org.immregistries.mqe.hub.cfg;

import org.immregistries.mqe.hub.submission.SubmitSingleMessageClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class CdcSoapConfiguration {

  
  @Bean
  public Jaxb2Marshaller marshaller() {
    System.out.println("--> marshalling!!!!!");
    Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    // this package must match the package in the <generatePackage> specified in
    // pom.xml
    marshaller.setContextPath("org.immregistries.mqe.cdc_wsdl");
    return marshaller;
  }
  
  @Bean
  public SubmitSingleMessageClient getSubmitSingleMessage(Jaxb2Marshaller marshaller)
  {
    SubmitSingleMessageClient client = new SubmitSingleMessageClient();
    client.setDefaultUri("http://florence.immregistries.org/mqe/");
    client.setMarshaller(marshaller);
    client.setUnmarshaller(marshaller);
    return client;
  }
}
