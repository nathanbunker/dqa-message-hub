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

  @RequestMapping(value = "/url", method = RequestMethod.GET)
  public String getNistValidatorUrl() {
    return "{\"url\"" + ":\"" + props.getNistValidatorUrl() + "\"}";
  }

  @RequestMapping(value = "/status", method = RequestMethod.GET)
  public String getStatus()
  {
    return props.getNistValidatorConnectionStatus() == MqeServiceConnectionStatus.DISABLED ? "disable" : "enable";
  }

  
  @RequestMapping(value = "/url", method = RequestMethod.POST)
  public void setNistValidatorUrl(@RequestBody String nistValidatorUrl) {
    logger.info("setNistValidatorUrl demo! Setting URL: " + nistValidatorUrl);
    this.setNistValidatorUrl(nistValidatorUrl);
  }

  public MqeServiceConnectionStatus getNistValidatorConnectionStatus() {
    return props.getNistValidatorConnectionStatus();
  }

  @RequestMapping(value = "/connection", method = RequestMethod.GET)
  public String getNistValidatorConnectionStatusString() {
    return "{\"status\"" + ":\"" + props.getNistValidatorConnectionStatus().toString() + "\"}";
  }

  public Throwable getException() {
    return settings.getException();
  }

  @RequestMapping(value = "/exception", method = RequestMethod.GET)
  public String getExceptionString() {
    if (this.getException() == null) {
      return null;
    }
    return "{\"exception\"" + ":\"" + this.getException().getMessage() + "\"}";
  }

  @RequestMapping(value = "/clear-exception", method = RequestMethod.POST)
  public void clearException() {
    settings.setException(null);
  }

  


}
