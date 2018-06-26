package org.immregistries.mqe.hub.cfg;

import javax.annotation.PostConstruct;
import org.immregistries.mqe.hub.settings.MqeSettings;
import org.immregistries.mqe.hub.settings.MqeSettingsJpaRepository;
import org.immregistries.mqe.hub.settings.MqeSettingsName;
import org.immregistries.mqe.hub.submission.NistValidatorConnectionStatus;
import org.immregistries.mqe.hub.submission.NistValidatorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "message-hub")
public class MqeMessageHubApplicationProperties {

  private static final Logger logger =
      LoggerFactory.getLogger(MqeMessageHubApplicationProperties.class);

  private String testName;
  private int interestingProperty;
  @Autowired
  NistValidatorHandler nistValidatorHandler;


  @Autowired
  private MqeSettingsJpaRepository settingsRepo;

  private String nistValidatorUrl =
      "http://localhost:8080/hl7v2ws//services/soap/MessageValidationV2";

  public String getNistValidatorUrl() {
    return nistValidatorUrl;
  }

  public void setNistValidatorUrl(String nistValidatorUrl) {
    saveProperty(MqeSettingsName.NIST_URL, nistValidatorUrl);
    this.nistValidatorUrl = nistValidatorUrl;
  }

  public void setNistValidatorConnectionStatus(
      NistValidatorConnectionStatus nistValidatorConnectionStatus) {
    saveProperty(MqeSettingsName.NIST_ACTIVATION, nistValidatorConnectionStatus.toString());
    this.nistValidatorConnectionStatus = nistValidatorConnectionStatus;
  }

  public NistValidatorConnectionStatus getNistValidatorConnectionStatus() {
    return nistValidatorConnectionStatus;
  }

  public void saveProperty(MqeSettingsName name, String value) {
    MqeSettings s = settingsRepo.findByName(name.name);
    if (s == null) {
      s = new MqeSettings();
    }
    s.setName(name.name);
    s.setValue(value);
    settingsRepo.save(s);
  }

  private NistValidatorConnectionStatus nistValidatorConnectionStatus =
      NistValidatorConnectionStatus.ENABLED;


  @PostConstruct
  public void postInit() {
    logger.info("Initializing Extra Values");
    //things here happen after it gets all the properties it can
    //find in the file.
    //Do other DBParameter lookups here.
    {
      MqeSettings s = settingsRepo.findByName(MqeSettingsName.NIST_URL.name);
      if (s == null) {
        saveProperty(MqeSettingsName.NIST_URL, nistValidatorUrl);
      } else {
        nistValidatorUrl = s.getValue();
      }
    }
    {
      MqeSettings s = settingsRepo.findByName(MqeSettingsName.NIST_ACTIVATION.name);
      if (s == null) {
        saveProperty(MqeSettingsName.NIST_ACTIVATION, nistValidatorConnectionStatus.toString());
      } else {
        nistValidatorConnectionStatus = NistValidatorConnectionStatus.valueOf(s.getValue());
      }
    }
    nistValidatorHandler.resetNistValidator();
  }

  public String getTestName() {
    return testName;
  }

  public void setTestName(String testName) {
    this.testName = testName;
  }

  public int getInterestingProperty() {
    return interestingProperty;
  }

  public void setInterestingProperty(int interestingProperty) {
    this.interestingProperty = interestingProperty;
  }

}
