package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.entity.RegistrationUserDetail;
import io.mosip.registration.entity.UserBiometric;

/**
 * DAO class for RegistrationUserDetail
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */

public interface RegistrationUserDetailDAO {

	/**
	 * This method is used to get the User Details
	 * 
	 * @return {@link RegistrationUserDetail} based on the userId
	 */
	RegistrationUserDetail getUserDetail(String userId);

	/**
	 * This method is used update login params
	 * 
	 * @param registrationUserDetail
	 *            user details
	 */
	void updateLoginParams(RegistrationUserDetail registrationUserDetail);
	
	List<UserBiometric> getAllActiveUsers(String attrCode);
	
	List<UserBiometric> getUserSpecificFingerprintDetails(String userId);

}
