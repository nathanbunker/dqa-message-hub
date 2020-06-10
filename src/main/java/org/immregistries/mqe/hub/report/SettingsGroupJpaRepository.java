package org.immregistries.mqe.hub.report;

import org.immregistries.mqe.hub.settings.DetectionSeverityOverrideGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsGroupJpaRepository extends JpaRepository<DetectionSeverityOverrideGroup, Long> {
  DetectionSeverityOverrideGroup findByName(String name);
}
