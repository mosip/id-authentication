/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.application.entity.DemographicEntity;

/**
 * This repository interface is used to define the JPA methods for Demographic service.
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
	 * 			pass userId
	 * @return list of preregistration data for the created date
	 */
	public List<DemographicEntity> findByCreatedBy(@Param("userId") String userId);

	/**
	 * @param userId pass userId
	 * @return list of group ids for a user id
	 */
	public List<String> noOfGroupIds(@Param("userId") String userId);

	/**
	 * @param preRegId pass preRegId
	 * @return preregistration date for a pre-id
	 */
	public DemographicEntity findBypreRegistrationId(@Param("preRegId") String preRegId);

	/**
	 * @param preId pass preId
	 * @return the number of rows deleted for a pre-id
	 */
	public int deleteByPreRegistrationId(String preId);

	/**
	 * @param start pass startTime
	 * @param end pass endTime
	 * @return list of preregistration data between start and end date
	 */
	public List<DemographicEntity> findBycreateDateTimeBetween(LocalDateTime start, LocalDateTime end);

}
