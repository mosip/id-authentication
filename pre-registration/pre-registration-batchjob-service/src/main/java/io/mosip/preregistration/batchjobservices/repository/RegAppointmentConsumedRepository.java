package io.mosip.preregistration.batchjobservices.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.batchjobservices.entity.RegistrationBookingEntityConsumed;

@Repository("regAppointmentConsumedRepository")
public interface RegAppointmentConsumedRepository extends BaseRepository<RegistrationBookingEntityConsumed, String> {

}
