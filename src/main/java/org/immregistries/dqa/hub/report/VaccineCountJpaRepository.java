package org.immregistries.dqa.hub.report;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VaccineCountJpaRepository extends JpaRepository<VaccineCount, Long> {
    List<VaccineCount> findBySenderMetrics(SenderMetrics sm);
}
