package org.immregistries.mqe.hub.report;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SenderJpaRepository extends JpaRepository<Sender, Long> {
  Sender findByName(String sender);
}
