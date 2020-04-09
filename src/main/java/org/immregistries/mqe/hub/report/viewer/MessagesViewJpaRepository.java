package org.immregistries.mqe.hub.report.viewer;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessagesViewJpaRepository extends JpaRepository<MessageMetadata, Long> {
	/**
	 * Query part to get the entries based on a date and a facility.
	 */

	static String BASE = " select mm from MessageMetadata as mm where mm.senderMetrics.username = :userId and mm.senderMetrics.sender.name = :providerKey and trunc(inputTime) >= trunc(:forDate) and trunc(inputTime) <= trunc(:forDateEnd) ";
	static String TEXT_FILTER =  " and LOCATE(UPPER(:searchText), UPPER(mm.message)) > 0 ";
	static String STATISTIC_FILTER = " and exists (select 1 from MessageDetection d where d.messageMetadata.id = mm.id and d.detectionId = :detectionId)";
	static String CODE_FILTER = " and exists (select 1 from MessageCode c where c.messageMetadata.id = mm.id and c.codeValue = :codeValue and c.codeType = :codeType)";
	static String CVX_FILTER = " and exists (select 1 from MessageVaccine v where v.messageMetadata.id = mm.id and v.vaccineCvx IN (:cvxList) and v.age >= :ageLow and v.age < :ageHigh)";
	static String ONE = " limit 1 ";

	@Query(value=BASE)
	Page<MessageMetadata> findByProviderAndDate(@Param("userId") String userId, @Param("providerKey") String providerKey, @Param("forDate") Date dateStart, @Param("forDateEnd") Date dateEnd, Pageable pager);
	
	@Query(value=BASE+TEXT_FILTER)
	Page<MessageMetadata> findByProviderAndDateAndMessageText(@Param("userId") String userId, @Param("providerKey") String providerKey, @Param("forDate") Date dateStart, @Param("forDateEnd") Date dateEnd, @Param("searchText") String messageRequest, Pageable pager);
	
	@Query(value=BASE+STATISTIC_FILTER)
	Page<MessageMetadata> findByDetectionId(@Param("userId") String userId, @Param("providerKey") String providerKey, @Param("forDate") Date dateStart, @Param("forDateEnd") Date dateEnd, @Param("detectionId") String detectionId, Pageable pager);

	@Query(value=BASE+CODE_FILTER)
	Page<MessageMetadata> findByCodeValue(@Param("userId") String userId, @Param("providerKey") String providerKey, @Param("forDate") Date dateStart, @Param("forDateEnd") Date dateEnd, @Param("codeValue") String codeValue, @Param("codeType") String codeType, Pageable pager);

	@Query(value=BASE+CVX_FILTER)
	Page<MessageMetadata> findByVaccine(@Param("userId") String userId, @Param("providerKey") String providerKey, @Param("forDate") Date dateStart, @Param("forDateEnd") Date dateEnd, @Param("cvxList") List<String> cvxList, @Param("ageHigh") int ageHigh, @Param("ageLow") int ageLow, Pageable pager);

	
	@Query(value=BASE+TEXT_FILTER+STATISTIC_FILTER)
	Page<MessageMetadata> findByDetectionIdAndMessageText(@Param("userId") String userId, @Param("providerKey") String providerKey, @Param("forDate") Date dateStart, @Param("forDateEnd") Date dateEnd, @Param("detectionId") String detectionId, @Param("searchText") String messageRequest, Pageable pager);

}

