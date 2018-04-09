package org.immregistries.dqa.hub.report.viewer;

import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageMetadataJpaRepository extends JpaRepository<MessageMetadata, Long> {

  List<MessageMetadata> findByInputTimeGreaterThanAndInputTimeLessThan(Date dateStart,
      Date dateEnd);

  @Query(value = "select mm from MessageMetadata mm where provider = :providerKey and trunc(inputTime) = :forDate")
  List<MessageMetadata> findByProviderAndDate(@Param("providerKey") String providerKey,
      @Param("forDate") Date messageInDate);

  @Query(value = "select mm from MessageMetadata mm where provider = :providerKey and trunc(inputTime) = :forDate")
  Page<MessageMetadata> findByProviderAndDatePaged(@Param("providerKey") String providerKey,
      @Param("forDate") Date messageInDate, Pageable pager);

}
