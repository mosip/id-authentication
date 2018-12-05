package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.RegistrationAppAuthenticationMethod;
import io.mosip.registration.entity.RegistrationAppAuthenticationMethodId;

/**
 * The repository interface for {@link RegistrationAppAuthenticationMethod} entity
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */

public interface RegistrationAppAuthenticationRepository
		extends BaseRepository<RegistrationAppAuthenticationMethod, RegistrationAppAuthenticationMethodId> {

	/**
	 * This method returns the list of {@link RegistrationAppAuthenticationMethod} based on
	 * status
	 * 
	 * @return the list of {@link RegistrationAppAuthenticationMethod}
	 */
	List<RegistrationAppAuthenticationMethod> findByIsActiveTrueAndRegistrationAppAuthenticationMethodIdProcessNameOrderByMethodSeq(String authType);

}
