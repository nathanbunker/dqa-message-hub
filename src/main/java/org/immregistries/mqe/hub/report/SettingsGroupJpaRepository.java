package org.immregistries.mqe.hub.report;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsGroupJpaRepository extends JpaRepository<DetectionSettingsGroup, Long> {
  DetectionSettingsGroup findByName(String name);
}
