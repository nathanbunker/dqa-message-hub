package org.immregistries.mqe.hub.report;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityJpaRepository extends JpaRepository<Facility, Long> {
  Facility findByName(String sender);
}
