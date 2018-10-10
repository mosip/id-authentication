package io.mosip.registration.repositories;

import java.util.Optional;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import io.mosip.registration.entity.RegistrationCenter;
import io.mosip.registration.entity.RegistrationCenterId;

/**
 * The repository interface for {@link RegistrationCenter} entity
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@Repository
public interface RegistrationCenterRepository extends BaseRepository<RegistrationCenter, RegistrationCenterId> {

	/**
	 * This method returns the optional of {@link RegistrationCenter} based on id
	 * 
	 * @param id
	 *            the registration center id
	 * @return the optional of {@link RegistrationCenter}
	 */
	Optional<RegistrationCenter> findByRegistrationCenterIdCenterIdAndIsActiveTrue(String id);

}
