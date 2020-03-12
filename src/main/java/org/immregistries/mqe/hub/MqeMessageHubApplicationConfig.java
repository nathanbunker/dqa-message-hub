package org.immregistries.mqe.hub;

import org.apache.catalina.connector.Connector;
import org.immregistries.mqe.validator.detection.DetectionDocumentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@EnableConfigurationProperties
//@ComponentScan(basePackages={"org.immregistries.mqe.hub"})
public class MqeMessageHubApplicationConfig extends SpringBootServletInitializer {

  @Value("${tomcat.ajp.port}")
  int ajpPort;

  @Value("${tomcat.ajp.remoteauthentication}")
  String remoteAuthentication;

  @Value("${tomcat.ajp.enabled}")
  boolean tomcatAjpEnabled;

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder applicationBuilder) {
    return applicationBuilder.sources(MqeMessageHubApplicationConfig.class);
  }


  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurerAdapter() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("*").allowedOrigins("http://localhost:4200");
      }
    };
  }

  @Bean
  public EmbeddedServletContainerFactory servletContainer() {
    TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
    if (tomcatAjpEnabled) {
      Connector ajpConnector = new Connector("AJP/1.3");
      ajpConnector.setProtocol("AJP/1.3");
      ajpConnector.setPort(ajpPort);
      ajpConnector.setSecure(false);
      ajpConnector.setAllowTrace(false);
      ajpConnector.setScheme("http");
      tomcat.addAdditionalTomcatConnectors(ajpConnector);
    }
    return tomcat;
  }

  @Bean
  public DetectionDocumentation createDocumentation() throws java.lang.NoSuchFieldException {
    return DetectionDocumentation.getDetectionDocumentation();
  }

  public static void main(String[] args) throws Exception {
    new SpringApplication(MqeMessageHubApplicationConfig.class).run(args);
  }
}
