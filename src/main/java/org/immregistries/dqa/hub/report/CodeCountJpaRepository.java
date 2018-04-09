package org.immregistries.dqa.hub.report;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodeCountJpaRepository extends JpaRepository<CodeCount, Long> {

  List<CodeCount> findBySenderMetrics(SenderMetrics sm);
}
