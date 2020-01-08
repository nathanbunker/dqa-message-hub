package org.immregistries.mqe.hub.settings;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetectionsSettingsJpaRepository extends JpaRepository<DetectionsSettings, Long> {

  public List<DetectionsSettings> findByDetectionGroupName(String groupName);
  
  public DetectionsSettings findByDetectionGroupNameAndMqeCode(String groupId, String MqeCode);
}
