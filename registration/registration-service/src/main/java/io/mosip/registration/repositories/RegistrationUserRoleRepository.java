package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import io.mosip.registration.entity.RegistrationUserRole;
import io.mosip.registration.entity.RegistrationUserRoleID;

@Repository
public interface RegistrationUserRoleRepository extends BaseRepository<RegistrationUserRole, RegistrationUserRoleID>{
	
	List<RegistrationUserRole> findByRegistrationUserRoleID(RegistrationUserRoleID registrationUserRoleID);

}
