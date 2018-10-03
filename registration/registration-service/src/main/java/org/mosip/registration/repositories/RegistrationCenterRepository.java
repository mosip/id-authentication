package org.mosip.registration.repositories;

import java.util.Optional;

import org.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.mosip.registration.entity.RegistrationCenter;
import org.springframework.stereotype.Repository;

/**
 * The repository interface for {@link RegistrationCenter} entity
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@Repository
public interface RegistrationCenterRepository extends BaseRepository<RegistrationCenter, String>{
	
	/**
	 * This method returns the optional of {@link RegistrationCenter} based on id
	 * 
	 * @param id
	 * 		 the registration center id 
	 * @return the optional of {@link RegistrationCenter}
	 */
	Optional<RegistrationCenter> findById(String id);

}
