package org.immregistries.mqe.hub.settings;

import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DetectionsSettingsService {
	
	private static final Logger logger = LoggerFactory.getLogger(DetectionsSettingsService.class);

	@Autowired
	DetectionsSettingsJpaRepository dsRepo;
	
	public void saveDetectionSetting(DetectionsSettings ds) {
		dsRepo.save(ds);
	}
	
	/**
	 * 
	 * Only loads new entries or updates existing entries
	 * Does not remove entries in the db that aren't defined in the file
	 */
	public void loadDetectionsToDB(HashSet<DetectionsSettings> allPropertySettings) {
	  for (DetectionsSettings ds : allPropertySettings) {
		  DetectionsSettings dbDetectionSetting = dsRepo.findByDetectionSettingsGroupNameAndMqeCode(ds.getDetectionSettingsGroup().getName(), ds.getMqeCode());
		  if (dbDetectionSetting != null) {
			  dbDetectionSetting.setSeverity(ds.getSeverity());
			  dsRepo.save(dbDetectionSetting);
		  } else {
			  dsRepo.save(ds);
		  }
	  }
	}
}
