package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.entity.UserDetail;
import io.mosip.registration.dto.UserDetailResponseDto;
import io.mosip.registration.entity.UserBiometric;

/**
 * This class is used to get respective user details by providing their id from {@link UserDetail} table.
 * This class is used to update the login parameters of the user, get the list of user specific biometric details by
 * by passing user id and bio type, get all the active users by passing attribute code, get user specific biometric 
 * details by passing user id, bio type and sub type. 
 * This class is used to save the user detail response to {@link UserDetail} table.
 *  
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */

public interface UserDetailDAO {

	/**
	 * This method is used to get the User Details by passing user id as parameter from {@link UserDetail} table.
	 * 
	 * @param userId
	 *            id of the user
	 * 
	 * @return {@link UserDetail} based on the userId
	 */
	UserDetail getUserDetail(String userId);

	/**
	 * This method is used update login params by passing the user detail as parameter. 
	 * 
	 * @param userDetail
	 *            user details
	 */
	void updateLoginParams(UserDetail userDetail);

	/**
	 * This method is used to gets the all active users by passing the attrCode as parameter.
	 *
	 * @param attrCode
	 *            the attr code
	 * @return the all active users
	 */
	List<UserBiometric> getAllActiveUsers(String attrCode);

	/**
	 * This method is used to gets the user specific bio details by passing user and bioType as parameters.
	 *
	 * @param userId
	 *            the user id
	 * @param bioType
	 *            the bio type
	 * @return the list of user specific biometric details
	 */
	List<UserBiometric> getUserSpecificBioDetails(String userId, String bioType);

	/**
	 * This method is used to get the user specific bio details  by passing user, bioType and sub type as parameters.
	 *
	 * @param userId
	 *            the user id
	 * @param bioType
	 *            the bio type
	 * @param subType
	 *            the bio subtype
	 * @return the user specific biometric details
	 * 
	 */
	UserBiometric getUserSpecificBioDetail(String userId, String bioType, String subType);

	
	/**
	 * This method is used to save the user details response to the {@link UserDetail} table.
	 *
	 * @param userDetailsResponse
	 *            the user details response
	 */
	void save(UserDetailResponseDto userDetailsResponse);

}
