package org.immregistries.mqe.hub.report;

import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityMessageCountsJpaRepository extends JpaRepository<FacilityMessageCounts, Long> {
  FacilityMessageCounts findByFacilityNameAndUploadDateAndUsername(String facilityName, Date uploadDate, String username);
  FacilityMessageCounts findByUsernameEqualsAndFacilityNameAndUploadDateGreaterThanEqualAndUploadDateLessThanEqual(String username, String sender, Date beginDate, Date endDate);
}
