package org.immregistries.mqe.hub.submission;

import java.util.ArrayList;
import java.util.List;
import org.immregistries.mqe.hl7util.Reportable;
import org.immregistries.mqe.hub.cfg.MqeMessageHubApplicationProperties;
import org.immregistries.nist.validator.connector.NISTValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NistValidatorHandler {
  private NISTValidator nistValidator = null;
  private Throwable exception = null;

  @Autowired
  private MqeMessageHubApplicationProperties props;

  public NISTValidator getNISTValidator() {
    if (nistValidator == null) {
      nistValidator = new NISTValidator(props.getNistValidatorUrl());
    }
    return nistValidator;
  }

  public NISTValidator getNistValidator() {
    return nistValidator;
  }

  public void setNistValidator(NISTValidator nistValidator) {
    this.nistValidator = nistValidator;
  }

  public void resetNistValidator() {
    nistValidator = null;
  }

  public Throwable getException() {
    return exception;
  }

  public void setException(Throwable exception) {
    this.exception = exception;
  }

  public List<Reportable> validate(String message) {
    List<Reportable> nistReportableList = null;
    NistValidatorConnectionStatus nistValidatorConnectionStatus =
        props.getNistValidatorConnectionStatus();
    if (nistValidatorConnectionStatus != NistValidatorConnectionStatus.DISABLED) {
      try {
        NISTValidator nistValidator = getNISTValidator();
        nistReportableList = nistValidator.validateAndReport(message);
        if (nistValidatorConnectionStatus != NistValidatorConnectionStatus.CONNECTED) {
          props.setNistValidatorConnectionStatus(NistValidatorConnectionStatus.CONNECTED);
        }
      } catch (Exception e) {
        this.exception = e;
        switch (nistValidatorConnectionStatus) {
          case ENABLED:
          case CONNECTED:
            props.setNistValidatorConnectionStatus(NistValidatorConnectionStatus.FIRST_FAILURE);
          case FIRST_FAILURE:
            props.setNistValidatorConnectionStatus(NistValidatorConnectionStatus.SECOND_FAILURE);
            break;
          case DISABLED:
          case SECOND_FAILURE:
            props.setNistValidatorConnectionStatus(NistValidatorConnectionStatus.DISABLED);
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
