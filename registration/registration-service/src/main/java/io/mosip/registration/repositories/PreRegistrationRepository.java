package io.mosip.registration.repositories;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.PreRegistrationList;

/**
 * Pre registration repository to get/save/update and verify pre-reg
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
public interface PreRegistrationRepository extends BaseRepository<PreRegistrationList, String> {
	

}
