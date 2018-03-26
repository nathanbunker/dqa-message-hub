package org.immregistries.dqa.hub.report;

import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SenderMetricsJpaRepository extends JpaRepository<SenderMetrics, Long> {

  SenderMetrics findBySenderAndMetricsDate(String sender, Date sendDate);

  SenderMetrics findBySenderAndMetricsDateGreaterThanEqualAndMetricsDateLessThanEqual(String sender,
      Date beginDate, Date endDate);
}
