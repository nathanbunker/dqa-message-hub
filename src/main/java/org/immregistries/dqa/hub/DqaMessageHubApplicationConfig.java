package org.immregistries.dqa.hub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.web.SpringBootServletInitializer;

@SpringBootApplication
@EnableConfigurationProperties
//@ComponentScan(basePackages={"org.immregistries.dqa.hub"})
public class DqaMessageHubApplicationConfig extends SpringBootServletInitializer {

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder applicationBuilder) {
    return applicationBuilder.sources(DqaMessageHubApplicationConfig.class);
  }

  public static void main(String[] args) throws Exception {
    new SpringApplication(DqaMessageHubApplicationConfig.class).run(args);
  }
}
