package io.mosip.preregistration.booking.repository;

import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.booking.entity.RegistrationBookingEntity;

@Repository("registrationBookingRepository")
@Transactional
public interface RegistrationBookingRepository extends BaseRepository<RegistrationBookingEntity, String> {


	
	/**
	 * @param preregistrationId
	 * @param statusCode
	 * @return true or false
	 */
	public boolean existsByPreIdandStatusCode(String preregistrationId, String statusCode);

	public RegistrationBookingEntity findByPreIdAndStatusCode(String preregistrationId, String statusCode);

}
