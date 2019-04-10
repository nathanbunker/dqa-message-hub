package org.immregistries.mqe.hub.cfg;

import javax.annotation.PostConstruct;

import org.immregistries.mqe.hub.settings.DetectionProperties;
import org.immregistries.mqe.hub.settings.DetectionsSettings;
import org.immregistries.mqe.hub.settings.DetectionsSettingsJpaRepository;
import org.immregistries.mqe.hub.settings.MqeSettings;
import org.immregistries.mqe.hub.settings.MqeSettingsJpaRepository;
import org.immregistries.mqe.hub.settings.MqeSettingsName;
import org.immregistries.mqe.hub.submission.MqeServiceConnectionStatus;
import org.immregistries.mqe.hub.submission.NistValidatorHandler;
import org.immregistries.mqe.validator.ValidatorProperties;
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

  @Autowired
  NistValidatorHandler nistValidatorHandler;

  @Autowired
  private MqeSettingsJpaRepository settingsRepo;
  
  @Autowired
  private DetectionsSettingsJpaRepository detectionsSettingsRepo;

  private MqeServiceConnectionStatus nistValidatorConnectionStatus = MqeServiceConnectionStatus.ENABLED;
  private String nistValidatorUrl = "http://localhost:8756/hl7v2ws//services/soap/MessageValidationV2";
  private String ssApiKey;
  private String ssAuthId;
  private String ssActivationStatus;

  public String getNistValidatorUrl() {
    return nistValidatorUrl;
  }

  public void setNistValidatorConnectionStatus(
      MqeServiceConnectionStatus nistValidatorConnectionStatus) {
    saveProperty(MqeSettingsName.NIST_ACTIVATION, nistValidatorConnectionStatus.toString());
    this.nistValidatorConnectionStatus = nistValidatorConnectionStatus;
  }

  public MqeServiceConnectionStatus getNistValidatorConnectionStatus() {
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
  
  public void saveDetectionsGroupProperty(String groupId, String mqeCode, String value) {
	  DetectionsSettings d = detectionsSettingsRepo.findByGroupId(groupId);
	  if (d == null) {
		  d = new DetectionsSettings();
	  }
	  d.setGroupId(groupId);
	  d.setMqeCode(mqeCode);
	  d.setSeverity(value);
	  detectionsSettingsRepo.save(d);
  }

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
        nistValidatorConnectionStatus = MqeServiceConnectionStatus.valueOf(s.getValue());
      }
    }
    nistValidatorHandler.resetNistValidator();

    ValidatorProperties vp = ValidatorProperties.INSTANCE;
    MqeSettings s = settingsRepo.findByName(MqeSettingsName.SS_ACTIVATION.name);
    if (s == null) {
      this.ssActivationStatus = vp.isAddressCleanserEnabled() ? MqeServiceConnectionStatus.ENABLED.toString() : MqeServiceConnectionStatus.ENABLED.toString();
    } else {
      this.ssActivationStatus = MqeServiceConnectionStatus.valueOf(s.getValue()).toString();
      vp.setAddressCleanserEnabled(MqeServiceConnectionStatus.ENABLED.toString().equals(s.getValue()));
    }

    MqeSettings ssApiKey = settingsRepo.findByName(MqeSettingsName.SS_API_KEY.name);
    if (ssApiKey == null) {
      this.ssApiKey = vp.getSsApiAuthToken();
    } else {
      this.ssApiKey = ssApiKey.getValue();
      vp.setSsApiAuthToken(this.ssApiKey);
    }
    MqeSettings ssAuthId = settingsRepo.findByName(MqeSettingsName.SS_AUTH_ID.name);
    if (ssApiKey == null) {
      this.ssAuthId = vp.getSsApiAuthId();
    } else {
      this.ssAuthId = ssAuthId.getValue();
      vp.setSsApiAuthId(this.ssAuthId);
    }

    DetectionProperties dp = DetectionProperties.INSTANCE;
    
  }

}
