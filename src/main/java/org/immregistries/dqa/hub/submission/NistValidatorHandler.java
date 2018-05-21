package org.immregistries.dqa.hub.submission;

import java.util.ArrayList;
import java.util.List;
import org.immregistries.dqa.hl7util.Reportable;
import org.immregistries.dqa.nist.validator.connector.NISTValidator;

public enum NistValidatorHandler {
                                   INSTANCE;
  private NISTValidator nistValidator = null;
  private String nistValidatorUrl =
      "http://localhost:8080/hl7v2ws//services/soap/MessageValidationV2";
  // private String nistValidatorUrl = "https://hl7v2.ws.nist.gov/hl7v2ws//services/soap/MessageValidationV2";
  private NistValidatorConnectionStatus nistValidatorConnectionStatus =
      NistValidatorConnectionStatus.ENABLED;
  private Throwable exception = null;

  public NISTValidator getNISTValidator() {
    if (nistValidator == null) {
      nistValidator = new NISTValidator(nistValidatorUrl);
    }
    return nistValidator;
  }

  public NISTValidator getNistValidator() {
    return nistValidator;
  }

  public void setNistValidator(NISTValidator nistValidator) {
    this.nistValidator = nistValidator;
  }

  public String getNistValidatorUrl() {
    return nistValidatorUrl;
  }

  public void setNistValidatorUrl(String nistValidatorUrl) {
    if (!this.nistValidatorUrl.equals(nistValidatorUrl)) {
      nistValidator = null;
    }
    this.nistValidatorUrl = nistValidatorUrl;
  }

  public NistValidatorConnectionStatus getNistValidatorConnectionStatus() {
    return nistValidatorConnectionStatus;
  }

  public void setNistValidatorConnectionStatus(
      NistValidatorConnectionStatus nistValidatorConnectionStatus) {
    this.nistValidatorConnectionStatus = nistValidatorConnectionStatus;
  }

  public Throwable getException() {
    return exception;
  }

  public void setException(Throwable exception) {
    this.exception = exception;
  }

  public List<Reportable> validate(String message) {
    List<Reportable> nistReportableList = null;
    if (nistValidatorConnectionStatus != NistValidatorConnectionStatus.DISABLED) {
      try {
        NISTValidator nistValidator = getNISTValidator();
        nistReportableList = nistValidator.validateAndReport(message);
        nistValidatorConnectionStatus = NistValidatorConnectionStatus.CONNECTED;
      } catch (Exception e) {
        this.exception = e;
        switch (nistValidatorConnectionStatus) {
          case ENABLED:
          case CONNECTED:
            nistValidatorConnectionStatus = NistValidatorConnectionStatus.FIRST_FAILURE;
          case FIRST_FAILURE:
            nistValidatorConnectionStatus = NistValidatorConnectionStatus.SECOND_FAILURE;
            break;
          case DISABLED:
          case SECOND_FAILURE:
            nistValidatorConnectionStatus = NistValidatorConnectionStatus.DISABLED;
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
