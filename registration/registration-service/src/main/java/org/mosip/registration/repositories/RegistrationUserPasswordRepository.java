package org.mosip.registration.repositories;

import java.util.List;

import org.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.mosip.registration.entity.RegistrationUserPassword;
import org.mosip.registration.entity.RegistrationUserPasswordID;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationUserPasswordRepository extends BaseRepository<RegistrationUserPassword, RegistrationUserPasswordID> {
	
	List<RegistrationUserPassword> findByRegistrationUserPasswordID(RegistrationUserPasswordID registrationUserPasswordID);
}
