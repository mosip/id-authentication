package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.UserDetail;

/**
 * The repository interface for {@link UserDetail} entity
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */

public interface UserDetailRepository extends BaseRepository<UserDetail, String> {

	/**
	 * This method returns the list of {@link UserDetail} based on id
	 * 
	 * @param userId
	 *            the registration user id
	 * @return the list of {@link UserDetail}
	 */
	List<UserDetail> findByIdIgnoreCaseAndIsActiveTrue(String userId);

	/**
	 * To get the list of {@link UserDetail} based on the isActive statuc
	 * 
	 * @return the list of {@link UserDetail}
	 */
	List<UserDetail> findByIsActiveTrue();
	
	void deleteById(String id);
}
