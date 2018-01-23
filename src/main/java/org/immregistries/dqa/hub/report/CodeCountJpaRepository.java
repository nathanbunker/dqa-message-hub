package org.immregistries.dqa.hub.report;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface CodeCountJpaRepository extends JpaRepository<CodeCount, Long> {
    List<CodeCount> findBySenderMetrics(SenderMetrics sm);
}
