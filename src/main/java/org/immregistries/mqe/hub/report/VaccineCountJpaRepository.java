package org.immregistries.mqe.hub.report;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VaccineCountJpaRepository extends JpaRepository<FacilityVaccineCounts, Long> {

  List<FacilityVaccineCounts> findByFacilityMessageCounts(FacilityMessageCounts sm);
}
