/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.booking.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.booking.entity.RegistrationBookingEntity;

/**
 * This repository interface is used to define the JPA methods for Booking application.
 * 
 * @author Kishan Rathore
 * @author Jagadishwari
 * @author Ravi C. Balaji
 * @since 1.0.0
 *
 */
@Repository("registrationBookingRepository")
@Transactional
public interface RegistrationBookingRepository extends BaseRepository<RegistrationBookingEntity, String> {

	public static final String preIdQuery = "SELECT u FROM RegistrationBookingEntity u WHERE u.bookingPK.preregistrationId = ?1";
	/**
	 * @param preregistrationId
	 * @param statusCode
	 * @return RegistrationBookingEntity based on Pre-registration-Id and status code
	 */
	public RegistrationBookingEntity findPreIdAndStatusCode(String preregistrationId, String statusCode);
	 
	@Query(preIdQuery)
	RegistrationBookingEntity getPreRegId(@Param("preRegId") String preRegId);
	
	/**
	 * @param registrationCenterId
	 * @param statusCode
	 * @return List RegistrationBookingEntity based on Registration center id and status code
 	 */
	public List<RegistrationBookingEntity> findByRegistrationCenterIdAndStatusCode(@Param("regcntr_id")String registrationCenterId, @Param("status_code")String statusCode);
}
