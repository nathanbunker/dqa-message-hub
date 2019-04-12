package org.immregistries.mqe.hub;

import org.immregistries.mqe.validator.detection.DetectionDocumentation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
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

  @Bean
  public DetectionDocumentation createDocumentation() throws java.lang.NoSuchFieldException {
    return DetectionDocumentation.getDetectionDocumentation();
  }

  public static void main(String[] args) throws Exception {
    new SpringApplication(MqeMessageHubApplicationConfig.class).run(args);
  }
}
