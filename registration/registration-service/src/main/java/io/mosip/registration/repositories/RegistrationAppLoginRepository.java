package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import io.mosip.registration.entity.RegistrationAppLoginMethod;
import io.mosip.registration.entity.RegistrationAppLoginMethodID;

/**
 * The repository interface for {@link RegistrationAppLoginMethod} entity
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@Repository
public interface RegistrationAppLoginRepository extends BaseRepository<RegistrationAppLoginMethod, RegistrationAppLoginMethodID>{
	
	/**
	 * This method returns the list of {@link RegistrationAppLoginMethod} based on status
	 * 
	 * @return the list of {@link RegistrationAppLoginMethod}
	 */
	List<RegistrationAppLoginMethod> findByIsActiveTrueOrderByMethodSeq();

}
