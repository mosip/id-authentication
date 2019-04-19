package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.entity.UserDetail;
import io.mosip.registration.dto.UserDetailResponseDto;
import io.mosip.registration.entity.UserBiometric;

/**
 * DAO class for UserDetail
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */

public interface UserDetailDAO {

	/**
	 * This method is used to get the User Details
	 * 
	 * @param userId
	 *            id of the user
	 * 
	 * @return {@link UserDetail} based on the userId
	 */
	UserDetail getUserDetail(String userId);

	/**
	 * This method is used update login params
	 * 
	 * @param userDetail
	 *            user details
	 */
	void updateLoginParams(UserDetail userDetail);

	/**
	 * Gets the all active users.
	 *
	 * @param attrCode
	 *            the attr code
	 * @return the all active users
	 */
	List<UserBiometric> getAllActiveUsers(String attrCode);

	/**
	 * Gets the user specific bio details.
	 *
	 * @param userId
	 *            the user id
	 * @param bioType
	 *            the bio type
	 * @return the user specific bio details
	 */
	List<UserBiometric> getUserSpecificBioDetails(String userId, String bioType);

	/**
	 * Saves the user details response
	 *
	 * @param userDetailsResponse
	 *            the user details response
	 */
	void save(UserDetailResponseDto userDetailsResponse);

}
