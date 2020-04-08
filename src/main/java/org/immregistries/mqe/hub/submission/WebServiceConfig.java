package org.immregistries.mqe.hub.submission;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

  @Bean
  public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(
      ApplicationContext applicationContext) {
    MessageDispatcherServlet servlet = new MessageDispatcherServlet();
    servlet.setApplicationContext(applicationContext);
    servlet.setTransformWsdlLocations(true);
    return new ServletRegistrationBean<MessageDispatcherServlet>(servlet, "/ws/*");
  }
  
  @Bean
  public SaajSoapMessageFactory messageFactory()
  {
    SaajSoapMessageFactory messageFactory = new SaajSoapMessageFactory();
    messageFactory.setSoapVersion(SoapVersion.SOAP_12);
    return messageFactory;
  }
  
  @Bean(name = "soap")
  public SimpleWsdl11Definition wsdl11Definition()
  {
    SimpleWsdl11Definition simpleDef = new SimpleWsdl11Definition();
    simpleDef.setWsdl(new ClassPathResource("cdcsoap.wsdl"));
    return simpleDef;
  }

}
