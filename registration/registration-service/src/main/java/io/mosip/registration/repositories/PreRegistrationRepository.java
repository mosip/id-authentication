package io.mosip.registration.repositories;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.CenterMachine;
import io.mosip.registration.entity.CenterMachineId;
import io.mosip.registration.entity.PreRegistration;

/**
 * Pre registration repository to get/save/update and verify pre-reg
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
public interface PreRegistrationRepository extends BaseRepository<PreRegistration, String> {
	

}
