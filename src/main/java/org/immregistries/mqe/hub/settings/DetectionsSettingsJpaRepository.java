package org.immregistries.mqe.hub.settings;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DetectionsSettingsJpaRepository extends JpaRepository<DetectionsSettings, Long> {

  public DetectionsSettings findByGroupId(String groupId);
}
