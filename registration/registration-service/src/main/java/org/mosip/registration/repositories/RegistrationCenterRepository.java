package org.mosip.registration.repositories;

import java.util.Optional;

import org.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.mosip.registration.entity.RegistrationCenter;
import org.springframework.stereotype.Repository;


@Repository
public interface RegistrationCenterRepository extends BaseRepository<RegistrationCenter, String>{
	
	Optional<RegistrationCenter> findById(String id);

}
