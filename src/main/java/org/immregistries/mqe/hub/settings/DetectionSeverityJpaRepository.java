package org.immregistries.mqe.hub.settings;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetectionSeverityJpaRepository extends JpaRepository<DetectionSeverityOverride, Long> {
  List<DetectionSeverityOverride> findByDetectionSeverityOverrideGroupName(String groupName);
  DetectionSeverityOverride findByDetectionSeverityOverrideGroupNameAndMqeCode(String groupId, String MqeCode);
}
