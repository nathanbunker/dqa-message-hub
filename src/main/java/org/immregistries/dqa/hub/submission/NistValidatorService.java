package org.immregistries.dqa.hub.submission;

import java.util.ArrayList;
import java.util.List;
import org.immregistries.dqa.hl7util.Reportable;
import org.immregistries.dqa.nist.validator.connector.NISTValidator;

public enum NistValidatorService {
                                  INSTANCE;

  private NISTValidator nistValidator = null;
  private String nistValidatorUrl =
      "http://localhost:8080/hl7v2ws//services/soap/MessageValidationV2";
  // private String nistValidatorUrl = "https://hl7v2.ws.nist.gov/hl7v2ws//services/soap/MessageValidationV2";
  private NistValidatorConnectionStatus nistValidatorConnectionStatus =
      NistValidatorConnectionStatus.CONFIGURED;
  private Throwable exception = null;

  public String getNistValidatorUrl() {
    return nistValidatorUrl;
  }

  public void setNistValidatorUrl(String nistValidatorUrl) {
    this.nistValidatorUrl = nistValidatorUrl;
  }

  public NistValidatorConnectionStatus getNistValidatorConnectionStatus() {
    return nistValidatorConnectionStatus;
  }

  public Throwable getException() {
    return exception;
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
