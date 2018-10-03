package org.mosip.registration.repositories;

import java.util.List;

import org.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.mosip.registration.entity.RegistrationAppLoginMethod;
import org.mosip.registration.entity.RegistrationAppLoginMethodID;
import org.springframework.stereotype.Repository;

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
