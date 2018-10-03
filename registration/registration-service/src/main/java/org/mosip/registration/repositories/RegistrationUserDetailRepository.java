package org.mosip.registration.repositories;

import java.util.List;

import org.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.mosip.registration.entity.RegistrationUserDetail;
import org.springframework.stereotype.Repository;

/**
 * The repository interface for {@link RegistrationUserDetail} entity
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@Repository
public interface RegistrationUserDetailRepository extends BaseRepository<RegistrationUserDetail, String>{
	
	/**
	 * This method returns the list of {@link RegistrationUserDetail} based on id
	 * 
	 * @param id
	 * 		  the registration user id	
	 * @return the list of {@link RegistrationUserDetail}
	 */

	List<RegistrationUserDetail> findByIdAndIsActiveTrue(String id);
}
