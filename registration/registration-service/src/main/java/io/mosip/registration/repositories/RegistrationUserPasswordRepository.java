package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import io.mosip.registration.entity.RegistrationUserPassword;
import io.mosip.registration.entity.RegistrationUserPasswordID;

@Repository
public interface RegistrationUserPasswordRepository extends BaseRepository<RegistrationUserPassword, RegistrationUserPasswordID> {
	
	List<RegistrationUserPassword> findByRegistrationUserPasswordID(RegistrationUserPasswordID registrationUserPasswordID);
}
