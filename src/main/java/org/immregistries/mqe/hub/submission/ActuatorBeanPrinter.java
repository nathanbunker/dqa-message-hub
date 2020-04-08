package org.immregistries.mqe.hub.submission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ActuatorBeanPrinter  {


  @Autowired
  ApplicationContext ctx;

  
  
  public class ContainerObject {
    private List<String> messages = new ArrayList<>();

    public List<String> getMessages() {
      return messages;
    }

    public void setMessages(List<String> messages) {
      this.messages = messages;
    }
  }
  
  @RequestMapping(method = RequestMethod.GET, value = "/bl/beanList")
  public ContainerObject invoke() {
    ContainerObject co = new ContainerObject();
    co.messages.add("Let's inspect the beans provided by Spring Boot:");
    String[] beanNames = ctx.getBeanDefinitionNames();
    Arrays.sort(beanNames);
    for (String beanName : beanNames) {
      co.messages.add(beanName + ": " + ctx.getBean(beanName));
      
    }
    return co;
  }
}
