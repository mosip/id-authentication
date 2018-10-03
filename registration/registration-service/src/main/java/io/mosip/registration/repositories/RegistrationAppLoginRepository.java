package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import io.mosip.registration.entity.RegistrationAppLoginMethod;
import io.mosip.registration.entity.RegistrationAppLoginMethodID;

@Repository
public interface RegistrationAppLoginRepository extends BaseRepository<RegistrationAppLoginMethod, RegistrationAppLoginMethodID>{
	
	List<RegistrationAppLoginMethod> findByIsActiveTrueOrderByMethodSeq();

}
