package org.immregistries.mqe.hub.settings;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetectionsSettingsJpaRepository extends JpaRepository<DetectionsSettings, Long> {
  public List<DetectionsSettings> findByDetectionSettingsGroupName(String groupName);
  public DetectionsSettings findByDetectionSettingsGroupNameAndMqeCode(String groupId, String MqeCode);
}
