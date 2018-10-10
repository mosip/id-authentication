package io.mosip.registration.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import io.mosip.registration.entity.RegistrationUserRole;
import io.mosip.registration.entity.RegistrationUserRoleId;

/**
 * The repository interface for {@link RegistrationUserRole} entity
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@Repository
public interface RegistrationUserRoleRepository extends BaseRepository<RegistrationUserRole, RegistrationUserRoleId> {

	/**
	 * This method returns the list of {@link RegistrationUserRole} based on
	 * registrationUserRoleID
	 * 
	 * @param usrId
	 *            the usrId entered
	 * @return the list of {@link RegistrationUserRole}
	 */

	List<RegistrationUserRole> findByRegistrationUserRoleIdUsrIdAndIsActiveTrue(String usrId);

}
