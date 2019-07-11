/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.booking.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.booking.entity.RegistrationBookingEntity;

/**
 * This repository interface is used to define the JPA methods for Booking
 * application.
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
	public static final String deletePreIdQuery = "delete from RegistrationBookingEntity u where u.bookingPK.preregistrationId = ?1";
	public static final String getPreIdQuery = "select u from RegistrationBookingEntity u where u.registrationCenterId=?3 and u.regDate between ?1 and ?2";

	@Query(preIdQuery)
	RegistrationBookingEntity getPreRegId(@Param("preRegId") String preRegId);

	/**
	 * @param registrationCenterId
	 * @param statusCode
	 * @return List RegistrationBookingEntity based on Registration center id and
	 *         status code
	 */
	public List<RegistrationBookingEntity> findByRegistrationCenterId(@Param("regcntr_id") String registrationCenterId);

	@Query(preIdQuery)
	public List<RegistrationBookingEntity> findBypreregistrationId(String preId);

	@Transactional
	@Modifying
	@Query(deletePreIdQuery)
	public int deleteByPreRegistrationId(String preregistrationId);

	/**
	 * @param start
	 *            pass startTime
	 * @param end
	 *            pass endTime
	 * @return list of booked preregistration data between start and end date
	 */
	@Query(getPreIdQuery)
	public List<RegistrationBookingEntity> findByRegDateBetweenAndRegistrationCenterId(LocalDate start,
			LocalDate end, String regCenterId);
	
	public List<RegistrationBookingEntity> findByRegistrationCenterIdAndRegDate(String registrationCenterId,LocalDate regDate);
	
	
	@Query("SELECT e FROM RegistrationBookingEntity e  WHERE e.registrationCenterId= ?1 and e.regDate>=?2")
	public List<RegistrationBookingEntity> findByRegId(String registrationCenterId,LocalDate regDate);
}
