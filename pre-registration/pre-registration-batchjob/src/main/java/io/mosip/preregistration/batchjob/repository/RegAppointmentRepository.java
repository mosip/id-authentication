/* 
  * Copyright
 * 
 */
package io.mosip.preregistration.batchjob.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.core.common.entity.RegistrationBookingEntity;

/**
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@Repository("regAppointmentRepository")
public interface RegAppointmentRepository extends BaseRepository<RegistrationBookingEntity, String> {

	public static final String preIdQuery = "SELECT u FROM RegistrationBookingEntity u WHERE u.demographicEntity.preRegistrationId = ?1";

	/**
	 * @param currentdate
	 * @return List of RegistrationBookingEntity before current date.
	 */
	@Query("SELECT u FROM RegistrationBookingEntity u WHERE u.demographicEntity.statusCode = ?1 and u.regDate<?2")
	List<RegistrationBookingEntity> findByRegDateBetween(String statusCode, LocalDate currentDate);

	public List<RegistrationBookingEntity> findByRegistrationCenterIdAndRegDate(String registrationCenterId,
			LocalDate regDate);
	
	@Query("SELECT e FROM RegistrationBookingEntity e  WHERE e.registrationCenterId= ?1 and e.regDate>=?2")
	public List<RegistrationBookingEntity> findByRegId(String registrationCenterId, LocalDate regDate);
	
	/**
	 * @param preRegId
	 * @return RegistrationBookingEntity of the given Pre Id.
	 */
	@Query(preIdQuery)
	RegistrationBookingEntity getDemographicEntityPreRegistrationId(@Param("preRegId") String preRegId);
	
	List<RegistrationBookingEntity> findByRegistrationCenterIdAndRegDateAndSlotFromTimeBetween(String regCenterId,LocalDate date,LocalTime fromTime,LocalTime toTime);
}
