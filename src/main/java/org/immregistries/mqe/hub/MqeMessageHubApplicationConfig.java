package org.immregistries.mqe.hub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.web.SpringBootServletInitializer;

@SpringBootApplication
@EnableConfigurationProperties
//@ComponentScan(basePackages={"org.immregistries.mqe.hub"})
public class MqeMessageHubApplicationConfig extends SpringBootServletInitializer {

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder applicationBuilder) {
    return applicationBuilder.sources(MqeMessageHubApplicationConfig.class);
  }

  public static void main(String[] args) throws Exception {
    new SpringApplication(MqeMessageHubApplicationConfig.class).run(args);
  }
}
