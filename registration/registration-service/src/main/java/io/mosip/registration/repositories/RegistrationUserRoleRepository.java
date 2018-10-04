package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import io.mosip.registration.entity.RegistrationUserRole;
import io.mosip.registration.entity.RegistrationUserRoleID;

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
