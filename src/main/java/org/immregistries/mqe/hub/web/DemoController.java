package org.immregistries.mqe.hub.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value = "/")
@Controller
public class DemoController {

  private static final Log logger = LogFactory.getLog(DemoController.class);

  @RequestMapping(value = "/")
  public String startOld() throws Exception {
    return "app/demo-app.html";
    //Use the results to build an ACK using the MQE util project.
  }

  @RequestMapping(value = "/new")
  public String startNew() throws Exception {
    return "ngx/index.html";
    //Use the results to build an ACK using the MQE util project.
  }

}
