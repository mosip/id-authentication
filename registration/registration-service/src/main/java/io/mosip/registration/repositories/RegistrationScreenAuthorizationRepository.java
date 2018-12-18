package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.dao.ScreenAuthorizationDetails;
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
	 * This method returns the list of {@link ScreenAuthorizationDetails} based
	 * on role code
	 * 
	 * @param roleCode
	 *            the roleCode
	 * @return the list of {@link ScreenAuthorizationDetails}
	 */
	List<ScreenAuthorizationDetails> findByRegistrationScreenAuthorizationIdRoleCodeInAndIsPermittedTrueAndIsActiveTrue(List<String> roleCode);

}
