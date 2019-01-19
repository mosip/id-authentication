package io.mosip.preregistration.batchjobservices.repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.batchjobservices.entity.RegistrationBookingEntity;

/**
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@Repository("preRegistartionExpiredStatusRepository")
public interface PreRegistartionExpiredStatusRepository extends BaseRepository<RegistrationBookingEntity, String> {

	public static final String preIdQuery = "SELECT u FROM RegistrationBookingEntity u WHERE u.bookingPK.preregistrationId = ?1";
	/**
	 * @param currentdate
	 * @return List of RegistrationBookingEntity before current date.
	 */
	List<RegistrationBookingEntity> findByRegDateBefore(@Param("currentDate") LocalDate currentdate);
	
	/**
	 * @param preRegId
	 * @return RegistrationBookingEntity of the given Pre Id.
	 */
	@Query(preIdQuery)
	RegistrationBookingEntity getPreRegId(@Param("preRegId") String preRegId);
}
