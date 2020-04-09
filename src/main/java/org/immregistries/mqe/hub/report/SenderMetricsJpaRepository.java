package org.immregistries.mqe.hub.report;

import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SenderMetricsJpaRepository extends JpaRepository<SenderMetrics, Long> {

  SenderMetrics findBySenderNameAndMetricsDate(String sender, Date sendDate);

  SenderMetrics findBySenderNameAndMetricsDateGreaterThanEqualAndMetricsDateLessThanEqual(String sender, Date beginDate, Date endDate);

  SenderMetrics findBySenderNameAndMetricsDateGreaterThanEqualAndMetricsDateLessThanEqualAndUsername(String sender, Date beginDate, Date endDate, String username);

  SenderMetrics findBySenderNameAndMetricsDateAndUsername(String sender, Date beginDate, String username);
}
