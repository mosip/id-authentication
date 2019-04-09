package io.mosip.registration.repositories;

import java.util.List;
import java.util.Set;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.dao.AppAuthenticationDetails;
import io.mosip.registration.entity.AppAuthenticationMethod;
import io.mosip.registration.entity.id.AppAuthenticationMethodId;

/**
 * The repository interface for {@link AppAuthenticationMethod} entity
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */

public interface AppAuthenticationRepository
		extends BaseRepository<AppAuthenticationMethod, AppAuthenticationMethodId> {

	/**
	 * This method returns the list of {@link AppAuthenticationDetails} based on
	 * status.
	 *
	 * @param processId the process id
	 * @param roleCode the role code
	 * @return the list of AppAuthenticationDetails
	 */
	List<AppAuthenticationDetails> findByIsActiveTrueAndAppAuthenticationMethodIdProcessIdAndAppAuthenticationMethodIdRoleCodeInOrderByMethodSequence(String processId, Set<String> roleCode);

}
