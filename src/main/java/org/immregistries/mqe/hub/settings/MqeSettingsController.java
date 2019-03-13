package org.immregistries.mqe.hub.settings;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.immregistries.mqe.hub.cfg.MqeMessageHubApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "api/settings")
@RestController
public class MqeSettingsController {

  private static final Log logger = LogFactory.getLog(MqeSettingsController.class);

  @Autowired
  private MqeSettingsJpaRepository settingsRepo;
  
  @Autowired
  private MqeMessageHubApplicationProperties props;

  
  @RequestMapping(value = "/reset", method = RequestMethod.GET)
  public void reset() throws Exception {
    logger.info("resetting properties from database");
    props.postInit();
  }
  
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

    props.postInit();
	  
	  return settingsNameValue;
  }


  @RequestMapping(value = "", method = RequestMethod.POST)
  public String settingsSetter(@RequestBody SettingsContainer settings) throws Exception {

    logger.info("settingsSetter: " + settings);

    for (MqeSettings setting : settings.getSettings()) {
      MqeSettings s = settingsRepo.findByName(setting.getName());

      if (s != null && s.getValue() != null && setting.getValue() != null
          && s.getValue().equals(setting.getValue())) {
        //do nothing!
        continue;
      } else if (s == null) {
        s = new MqeSettings();
      }

      s.setValue(setting.getValue());
      s.setName(setting.getName());
      settingsRepo.save(s);
    }
    return "Complete!";
  }

  @RequestMapping(value = "", method = RequestMethod.GET)
  public SettingsContainer settingsSetterempty() throws Exception {
    List<MqeSettings> s = settingsRepo.findAll();
    SettingsContainer c = new SettingsContainer();
    c.setSettings(s);
    return c;
  }
}
