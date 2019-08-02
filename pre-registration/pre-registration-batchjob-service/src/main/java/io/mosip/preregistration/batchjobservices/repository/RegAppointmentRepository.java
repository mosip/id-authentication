/* 
  * Copyright
 * 
 */
package io.mosip.preregistration.batchjobservices.repository;

import java.time.LocalDate;
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
	List<RegistrationBookingEntity> findByRegDateBetween(LocalDate currentdate, LocalDate tillDate);

	/**
	 * @param preRegId
	 * @return RegistrationBookingEntity of the given Pre Id.
	 */
	@Query(preIdQuery)
	RegistrationBookingEntity getDemographicEntityPreRegistrationId(@Param("preRegId") String preRegId);
}
