package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.entity.UserDetail;
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
	
	List<UserBiometric> getAllActiveUsers(String attrCode);
	
	List<UserBiometric> getUserSpecificBioDetails(String userId, String bioType);

}
