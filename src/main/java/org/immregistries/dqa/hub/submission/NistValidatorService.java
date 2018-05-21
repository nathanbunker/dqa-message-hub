package org.immregistries.dqa.hub.submission;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/nist/validator")
@RestController
public class NistValidatorService {
  //INSTANCE;

  private static final Log logger = LogFactory.getLog(NistValidatorService.class);

  @RequestMapping(value = "/url", method = RequestMethod.GET)
  public String getNistValidatorUrl() {
    NistValidatorHandler settings = NistValidatorHandler.INSTANCE;
    return "{\"url\"" + ":\"" + settings.getNistValidatorUrl() + "\"}";
  }

  @RequestMapping(value = "/status", method = RequestMethod.GET)
  public String getStatus()
  {
    NistValidatorHandler settings = NistValidatorHandler.INSTANCE;
    return settings.getNistValidatorConnectionStatus() == NistValidatorConnectionStatus.DISABLED ? "disable" : "enable"; 
  }

  
  @RequestMapping(value = "/url", method = RequestMethod.POST)
  public void setNistValidatorUrl(@RequestBody String nistValidatorUrl) {
    logger.info("setNistValidatorUrl demo! Setting URL: " + nistValidatorUrl);
    this.setNistValidatorUrl(nistValidatorUrl);
  }

  public NistValidatorConnectionStatus getNistValidatorConnectionStatus() {
    NistValidatorHandler settings = NistValidatorHandler.INSTANCE;
    return settings.getNistValidatorConnectionStatus();
  }

  @RequestMapping(value = "/connection", method = RequestMethod.GET)
  public String getNistValidatorConnectionStatusString() {
    NistValidatorHandler settings = NistValidatorHandler.INSTANCE;
    return "{\"status\"" + ":\"" + settings.getNistValidatorConnectionStatus().toString() + "\"}";
  }

  public Throwable getException() {
    NistValidatorHandler settings = NistValidatorHandler.INSTANCE;
    return settings.getException();
  }

  @RequestMapping(value = "/exception", method = RequestMethod.GET)
  public String getExceptionString() {
    if (this.getException() == null) {
      return "{\"exception\"" + ":\"No exceptions.\"}";
    }
    return "{\"exception\"" + ":\"" + this.getException().getMessage() + "\"}";
  }

  @RequestMapping(value = "/clear-exception", method = RequestMethod.POST)
  public void clearException() {
    NistValidatorHandler settings = NistValidatorHandler.INSTANCE;
    settings.setException(null);
  }

  


}
