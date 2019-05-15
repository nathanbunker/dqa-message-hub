package org.immregistries.mqe.hub.submission;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.immregistries.mqe.hub.cfg.MqeMessageHubApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/api/nist/validator")
@RestController
public class NistValidatorService {
  //INSTANCE;

  private static final Log logger = LogFactory.getLog(NistValidatorService.class);
  
  @Autowired 
  MqeMessageHubApplicationProperties props;
  
  @Autowired
  NistValidatorHandler settings;

  @RequestMapping(value = "/status", method = RequestMethod.GET)
  public String getNistValidatorConnectionStatusString() {
    return "{\"status\"" + ":\"" + props.getNistValidatorConnectionStatus().toString() + "\"}";
  }

  @RequestMapping(value = "/exception", method = RequestMethod.GET)
  public String getExceptionString() {
    if (settings.getException() == null) {
      return null;
    }
    return "{\"exception\"" + ":\"" + settings.getException().getMessage() + "\"}";
  }


}
