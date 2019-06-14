package org.immregistries.mqe.hub.cfg;

import java.util.HashSet;

import javax.annotation.PostConstruct;

import org.immregistries.mqe.hl7util.SeverityLevel;
import org.immregistries.mqe.hub.settings.DetectionProperties;
import org.immregistries.mqe.hub.settings.DetectionsSettings;
import org.immregistries.mqe.hub.settings.DetectionsSettingsJpaRepository;
import org.immregistries.mqe.hub.settings.DetectionsSettingsService;
import org.immregistries.mqe.hub.settings.MqeSettings;
import org.immregistries.mqe.hub.settings.MqeSettingsJpaRepository;
import org.immregistries.mqe.hub.settings.MqeSettingsName;
import org.immregistries.mqe.hub.submission.MqeServiceConnectionStatus;
import org.immregistries.mqe.hub.submission.NistValidatorHandler;
import org.immregistries.mqe.validator.ValidatorProperties;
import org.immregistries.mqe.validator.detection.Detection;
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
  private DetectionsSettingsService detectionsSettingsSvc;
  
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
    this.nistValidatorConnectionStatus = nistValidatorConnectionStatus;
  }

  public MqeServiceConnectionStatus getNistValidatorConnectionStatus() {
    return nistValidatorConnectionStatus;
  }
  
  public void disableNistValidator() {
	saveProperty(MqeSettingsName.NIST_ACTIVATION, MqeServiceConnectionStatus.DISABLED.toString());
	this.nistValidatorConnectionStatus = MqeServiceConnectionStatus.DISABLED;
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
  
  public void initializeDatabaseProperties() {
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
  }

  @PostConstruct
  public void postInit() {
    updateDetectionsInMemory(detectionProp);
    detectionsSettingsSvc.loadDetectionsToDB(detectionProp.getAllPropertySettings()); 
    DetectionProperties detectionProp = DetectionProperties.INSTANCE;
    // have to do all autowired service calls outside of enum
    
	  initializeDatabaseProperties();
  }

  private void updateDetectionsInMemory(DetectionProperties detectionProp) {
	for (Detection detection : Detection.values()) {
		String mqeCode = detection.getMqeMqeCode();
		
		// set default application overrides
		SeverityLevel defaultSeverityLevel = getDefaultSeverityByCode(detectionProp, mqeCode);
		if (defaultSeverityLevel != null) {
			detection.setSeverity(defaultSeverityLevel);
		}
	}
  }
  
  private SeverityLevel getDefaultSeverityByCode(DetectionProperties detectionProp, String mqeCode) {
	  SeverityLevel severityLevel = null;
	  
	  DetectionsSettings ds = detectionsSettingsRepo.findByGroupIdAndMqeCode(detectionProp.DEFAULT_GROUP, mqeCode);
	  if (ds != null) {
		  severityLevel = SeverityLevel.findByLabel(ds.getSeverity());
	  }
	  return severityLevel;
  }
}
