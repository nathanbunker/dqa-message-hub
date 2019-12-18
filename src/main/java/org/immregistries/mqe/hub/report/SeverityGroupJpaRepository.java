package org.immregistries.mqe.hub.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface SeverityGroupJpaRepository extends JpaRepository<SeverityGroup, Long> {
  SeverityGroup findByName(String name);
}
