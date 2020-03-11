package org.immregistries.mqe.hub;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.immregistries.mqe.validator.detection.DetectionDocumentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
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

  //https://github.com/shekhargulati/spring-boot-maven-angular-starter/blob/master/backend/src/main/java/com/shekhargulati/app/Application.java#L29
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurerAdapter() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("*").allowedOrigins("http://localhost:4200");
      }
    };
  }

  //  @Bean
  //  public EmbeddedServletContainerFactory servletContainer() {
  //    TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
  //    if (tomcatAjpEnabled) {
  //      Connector ajpConnector = new Connector("AJP/1.3");
  //      ajpConnector.setProtocol("AJP/1.3");
  //      ajpConnector.setPort(ajpPort);
  //      ajpConnector.setSecure(false);
  //      ajpConnector.setAllowTrace(false);
  //      ajpConnector.setScheme("http");
  //      tomcat.addAdditionalTomcatConnectors(ajpConnector);
  //    }
  //    return tomcat;
  //  }



  @Bean
  public ServletWebServerFactory servletContainer() {
    TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
      @Override
      protected void postProcessContext(Context context) {
        // This forces the connect to HTTPS
//        SecurityConstraint securityConstraint = new SecurityConstraint();
//        securityConstraint.setUserConstraint("CONFIDENTIAL");
//        SecurityCollection collection = new SecurityCollection();
//        collection.addPattern("/*");
//        securityConstraint.addCollection(collection);
//        context.addConstraint(securityConstraint);
      }
    };
    tomcat.addAdditionalTomcatConnectors(redirectConnector());
    return tomcat;
  }

  private Connector redirectConnector() {
    Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
    connector.setScheme("http");
    connector.setPort(ajpPort);
    connector.setSecure(false);
    connector.setAllowTrace(false);
    return connector;
  }

  @Bean
  public DetectionDocumentation createDocumentation() throws java.lang.NoSuchFieldException {
    return DetectionDocumentation.getDetectionDocumentation();
  }

  public static void main(String[] args) throws Exception {
    new SpringApplication(MqeMessageHubApplicationConfig.class).run(args);
  }
}
