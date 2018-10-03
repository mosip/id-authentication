package org.mosip.registration.repositories;

import java.util.List;

import org.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.mosip.registration.entity.RegistrationUserRole;
import org.mosip.registration.entity.RegistrationUserRoleID;
import org.springframework.stereotype.Repository;

/**
 * The repository interface for {@link RegistrationUserRole} entity
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@Repository
public interface RegistrationUserRoleRepository extends BaseRepository<RegistrationUserRole, RegistrationUserRoleID>{
	
	/**
	 * This method returns the list of {@link RegistrationUserRole} based on registrationUserRoleID
	 * 
	 * @param registrationUserRoleID
	 * 		  the registration user role composite key	
	 * @return the list of {@link RegistrationUserRole}
	 */

	List<RegistrationUserRole> findByRegistrationUserRoleID(RegistrationUserRoleID registrationUserRoleID);

}
