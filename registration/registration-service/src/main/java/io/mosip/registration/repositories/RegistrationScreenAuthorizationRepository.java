package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import io.mosip.registration.entity.RegistrationScreenAuthorization;
import io.mosip.registration.entity.RegistrationScreenAuthorizationId;

/**
 * The repository interface for {@link RegistrationScreenAuthorization} entity
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */

public interface RegistrationScreenAuthorizationRepository
		extends BaseRepository<RegistrationScreenAuthorization, RegistrationScreenAuthorizationId> {

	/**
	 * This method returns the list of {@link RegistrationScreenAuthorization} based
	 * on role code
	 * 
	 * @param roleCode
	 *            the roleCode
	 * @return the list of {@link RegistrationScreenAuthorization}
	 */
	List<RegistrationScreenAuthorization> findByRegistrationScreenAuthorizationIdRoleCodeAndIsPermittedTrueAndIsActiveTrue(
			String rolecode);

}
