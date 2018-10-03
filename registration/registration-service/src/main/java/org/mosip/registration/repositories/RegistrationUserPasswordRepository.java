package org.mosip.registration.repositories;

import java.util.List;

import org.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.mosip.registration.entity.RegistrationUserPassword;
import org.mosip.registration.entity.RegistrationUserPasswordID;
import org.springframework.stereotype.Repository;

/**
 * The repository interface for {@link RegistrationUserPassword} entity
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@Repository
public interface RegistrationUserPasswordRepository extends BaseRepository<RegistrationUserPassword, RegistrationUserPasswordID> {
	
	/**
	 * This method returns the list of {@link RegistrationUserPassword} based on registrationUserPasswordID
	 * 
	 * @param registrationUserPasswordID
	 * 		  the registration user password composite key	
	 * @return the list of {@link RegistrationUserPassword}
	 */

	List<RegistrationUserPassword> findByRegistrationUserPasswordID(RegistrationUserPasswordID registrationUserPasswordID);
}
