package org.mosip.registration.repositories;

import java.util.List;

import org.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.mosip.registration.entity.RegistrationUserRole;
import org.mosip.registration.entity.RegistrationUserRoleID;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationUserRoleRepository extends BaseRepository<RegistrationUserRole, RegistrationUserRoleID>{
	
	List<RegistrationUserRole> findByRegistrationUserRoleID(RegistrationUserRoleID registrationUserRoleID);

}
