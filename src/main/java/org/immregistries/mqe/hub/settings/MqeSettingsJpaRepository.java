package org.immregistries.mqe.hub.settings;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MqeSettingsJpaRepository extends JpaRepository<MqeSettings, Long> {

  public MqeSettings findByName(String name);
}
