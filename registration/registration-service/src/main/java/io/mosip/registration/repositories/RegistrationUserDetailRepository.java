package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.RegistrationUserDetail;

/**
 * The repository interface for {@link RegistrationUserDetail} entity
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */

public interface RegistrationUserDetailRepository extends BaseRepository<RegistrationUserDetail, String> {

	/**
	 * This method returns the list of {@link RegistrationUserDetail} based on id
	 * 
	 * @param userid
	 *            the registration user id
	 * @return the list of {@link RegistrationUserDetail}
	 */
	List<RegistrationUserDetail> findByIdAndIsActiveTrue(String userId);
	
	
	/**
	 * To get the list of {@link RegistrationUserDetail} based on the center id
	 * 
	 * @param cntrId
	 * @param statusCode
	 * @param id
	 * @return the list of {@link RegistrationUserDetail}
	 */	
	List<RegistrationUserDetail> findByRegistrationCenterUserRegistrationCenterUserIdRegcntrIdAndIsActiveTrueAndStatusCodeNotLikeAndIdNotLike(String cntrId, String statusCode,String userId);
	
	
	List<RegistrationUserDetail> findByIsActiveTrue();
}
