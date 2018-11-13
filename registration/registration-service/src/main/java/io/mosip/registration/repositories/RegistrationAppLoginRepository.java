package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.RegistrationAppLoginMethod;
import io.mosip.registration.entity.RegistrationAppLoginMethodId;

/**
 * The repository interface for {@link RegistrationAppLoginMethod} entity
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */

public interface RegistrationAppLoginRepository
		extends BaseRepository<RegistrationAppLoginMethod, RegistrationAppLoginMethodId> {

	/**
	 * This method returns the list of {@link RegistrationAppLoginMethod} based on
	 * status
	 * 
	 * @return the list of {@link RegistrationAppLoginMethod}
	 */
	List<RegistrationAppLoginMethod> findByIsActiveTrueOrderByMethodSeq();

}
