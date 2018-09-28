package org.mosip.registration.repositories;

import java.util.List;

import org.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.mosip.registration.entity.RegistrationAppLoginMethod;
import org.mosip.registration.entity.RegistrationAppLoginMethodID;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationAppLoginRepository extends BaseRepository<RegistrationAppLoginMethod, RegistrationAppLoginMethodID>{
	
	List<RegistrationAppLoginMethod> findByIsActiveTrueOrderByMethodSeq();

}
