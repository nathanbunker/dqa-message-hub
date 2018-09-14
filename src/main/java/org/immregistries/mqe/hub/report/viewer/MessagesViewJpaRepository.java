package org.immregistries.mqe.hub.report.viewer;

import java.util.Date;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessagesViewJpaRepository extends JpaRepository<MessageMetadata, Long> {
	/**
	 * Query part to get the entries based on a date and a facility. 
	 */
	static final String BASE =
		" select mm from MessageMetadata as mm where provider = :providerKey and trunc(inputTime) = :forDate ";
	/**
	 * A filter for filtering by message text.  
	 */
	static final String TEXT_FILTER =  " and LOCATE(UPPER(:searchText), UPPER(mm.message)) > 0 ";
	/**
	 * Filtering by a statistic (An error type)
	 */
	static final String STATISTIC_FILTER = " and exists (select 1 from MessageDetection d where d.messageMetadata.id = mm.id and d.detectionId = :detectionId)))";
	
	@Query(value=BASE)
	Page<MessageMetadata> findByProviderAndDate(@Param("providerKey") String providerKey, @Param("forDate") Date dateCreated, Pageable pager);
	
	@Query(value=BASE+TEXT_FILTER)
	Page<MessageMetadata> findByProviderAndDateAndMessageText(@Param("providerKey") String providerKey, @Param("forDate") Date dateCreated, @Param("searchText") String messageRequest, Pageable pager);
	
	@Query(value=BASE+STATISTIC_FILTER)
	Page<MessageMetadata> findByDetectionId(@Param("providerKey") String providerKey, @Param("forDate") Date dateCreated, @Param("detectionId") String detectionId, Pageable pager);

	@Query(value=BASE+TEXT_FILTER+STATISTIC_FILTER)
	Page<MessageMetadata> findByDetectionIdAndMessageText(@Param("providerKey") String providerKey, @Param("forDate") Date dateCreated, @Param("detectionId") String detectionId, @Param("searchText") String messageRequest, Pageable pager);

}

