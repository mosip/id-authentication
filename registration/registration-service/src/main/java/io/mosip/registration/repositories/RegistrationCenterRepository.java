package io.mosip.registration.repositories;

import java.util.Optional;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import io.mosip.registration.entity.RegistrationCenter;


@Repository
public interface RegistrationCenterRepository extends BaseRepository<RegistrationCenter, String>{
	
	Optional<RegistrationCenter> findById(String id);

}
