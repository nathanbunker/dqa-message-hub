package org.immregistries.dqa.hub.submission;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.immregistries.dqa.hl7util.Reportable;
import org.immregistries.dqa.nist.validator.connector.NISTValidator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/nist/validator")
@RestController
public class NistValidatorService {
                                  //INSTANCE;
	
  private static final Log logger = LogFactory.getLog(NistValidatorService.class);

  private NISTValidator nistValidator = null;
  private String nistValidatorUrl =
      "http://localhost:8080/hl7v2ws//services/soap/MessageValidationV2";
  // private String nistValidatorUrl = "https://hl7v2.ws.nist.gov/hl7v2ws//services/soap/MessageValidationV2";
  private NistValidatorConnectionStatus nistValidatorConnectionStatus =
      NistValidatorConnectionStatus.CONFIGURED;
  private Throwable exception = null;

  @RequestMapping(value = "/url", method = RequestMethod.GET)
  public String getNistValidatorUrl() {
    return "{\"url\""+":\""+nistValidatorUrl+"\"}";
  }

  @RequestMapping(value = "/url", method = RequestMethod.POST)
  public void setNistValidatorUrl(@RequestBody String nistValidatorUrl) {
	logger.info("setNistValidatorUrl demo! Setting URL: " + nistValidatorUrl);
    this.nistValidatorUrl = nistValidatorUrl;
  }

  public NistValidatorConnectionStatus getNistValidatorConnectionStatus() {
    return nistValidatorConnectionStatus;
  }
  
  @RequestMapping(value = "/connection", method = RequestMethod.GET)
  public String getNistValidatorConnectionStatusString() {
	    return "{\"status\""+":\""+nistValidatorConnectionStatus.toString()+"\"}";
  }

  public Throwable getException() {
    return exception;
  }
  
  @RequestMapping(value = "/exception", method = RequestMethod.GET)
  public String getExceptionString() {
	  	if (this.getException() == null) {
	  		return "{\"exception\""+":\"No exceptions.\"}";
	  	}
	    return "{\"exception\""+":\""+this.getException().getMessage()+"\"}";
  }

  @RequestMapping(value = "/clear-exception", method = RequestMethod.POST)
  public void clearException() {
    exception = null;
  }

  private NISTValidator getNISTValidator() {
    if (nistValidator == null) {
      nistValidator = new NISTValidator(nistValidatorUrl);
    }
    return nistValidator;
  }

  public List<Reportable> validate(String message) {
    List<Reportable> nistReportableList = null;
    if (nistValidatorConnectionStatus != NistValidatorConnectionStatus.DOWN) {
      try {
        NISTValidator nistValidator = getNISTValidator();
        nistReportableList = nistValidator.validateAndReport(message);
        nistValidatorConnectionStatus = NistValidatorConnectionStatus.CONNECTED;
      } catch (Exception e) {
        this.exception = e;
        switch (nistValidatorConnectionStatus) {
          case CONFIGURED:
          case CONNECTED:
            nistValidatorConnectionStatus = NistValidatorConnectionStatus.FAILURE1;
          case FAILURE1:
            nistValidatorConnectionStatus = NistValidatorConnectionStatus.FAILURE2;
            break;
          case DOWN:
          case FAILURE2:
            nistValidatorConnectionStatus = NistValidatorConnectionStatus.DOWN;
            break;
        }
      }
    }
    if (nistReportableList == null) {
      nistReportableList = new ArrayList<>();
    }
    return nistReportableList;
  }


}
