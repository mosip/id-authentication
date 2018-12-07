package io.mosip.preregistration.booking.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.booking.entity.RegistrationBookingEntity;

@Repository("registrationBookingRepository")
@Transactional
public interface RegistrationBookingRepository extends BaseRepository<RegistrationBookingEntity, String> {

	public static final String existsQry = "SELECT CASE WHEN COUNT(u) > 0 THEN 'true' ELSE 'false' END FROM RegistrationBookingEntity u WHERE u.bookingPK.preregistrationId = ?1 and u.status_code = ?2";

	/**
	 * @param preregistrationId
	 * @param statusCode
	 * @return true or false
	 */
	@Query(existsQry)
	public boolean existsByPreIdandStatusCode(String preregistrationId, String statusCode);

}
