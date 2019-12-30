/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.demographic.repository;

import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.core.common.entity.DemographicEntity;

/**
 * This repository interface is used to define the JPA methods for Demographic
 * service.
 * 
 * @author Rajath KR
 * @author Sanober Noor
 * @author Tapaswini Behera
 * @author Jagadishwari S
 * @author Ravi C Balaji
 * @since 1.0.0
 * 
 */
@Repository("registrationRepository")
@Transactional
public interface DemographicRepository extends BaseRepository<DemographicEntity, String> {

	/**
	 * @param userId
	 *            pass userId
	 * @param statusCode
	 *            pass statusCode
	 * @return list of preregistration data for the created date
	 */
	public List<DemographicEntity> findByCreatedBy(@Param("userId") String userId,
			@Param("statusCode") String statusCode);

	/**
	 * @param userId
	 *            pass userId
	 * @param statusCode
	 *            pass statusCode
	 * @param pageable
	 *            pass pageable object
	 * @return list of preregistration data for the created user
	 */
	public Page<DemographicEntity> findByCreatedByOrderByCreateDateTime(@Param("userId") String userId,
			@Param("statusCode") String statusCode, Pageable pageable);

	/**
	 * @param preRegId
	 *            pass preRegId
	 * @return preregistration date for a pre-id
	 */
	public DemographicEntity findBypreRegistrationId(@Param("preRegId") String preRegId);

	/**
	 * @param preId
	 *            pass preId
	 * @return the number of rows deleted for a pre-id
	 */
	public int deleteByPreRegistrationId(String preId);

	/**
	 * @param start
	 *            pass startTime
	 * @param end
	 *            pass endTime
	 * @return list of preregistration data between start and end date
	 */
	public List<DemographicEntity> findBycreateDateTimeBetween(LocalDateTime start, LocalDateTime end);

	/**
	 * @param preRegistrationIds
	 * @return list of preregistration data for list of pre-ids
	 */
	public List<DemographicEntity> findByStatusCodeInAndPreRegistrationIdIn(List<String> statusCode,
			List<String> preRegistrationIds);
}
