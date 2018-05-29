package org.immregistries.dqa.hub.settings;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/settings")
@RestController
public class MqeSettingsController {

  private static final Log logger = LogFactory.getLog(MqeSettingsController.class);

  @Autowired
  private MqeSettingsJpaRepository settingsRepo;

  @RequestMapping(value = "/name/{settingName}", method = RequestMethod.GET)
  public MqeSettings settingNameGetter(@PathVariable String settingName) throws Exception {

    logger.info("settingNameGetter demo! Setting Name: " + settingName);

    return settingsRepo.findByName(settingName);
  }
  
  @RequestMapping(value = "/name/{settingName}", method = RequestMethod.POST)
  public MqeSettings settingNameSetter(@PathVariable String settingName, @RequestBody String value) throws Exception {
	  
	  logger.info("settingNameSave demo! Setting Name: " + settingName);
	  
	  MqeSettings settingsNameValue = settingsRepo.findByName(settingName);
	  
	  if (settingsNameValue == null) {
		  settingsNameValue = new MqeSettings();
		  settingsNameValue.setName(settingName);
	  }
	  
	  settingsNameValue.setValue(value);
	  settingsRepo.save(settingsNameValue);
	  
	  return settingsNameValue;
  }

}
