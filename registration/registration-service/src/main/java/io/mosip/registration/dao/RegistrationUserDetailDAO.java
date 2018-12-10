package io.mosip.registration.dao;

import io.mosip.registration.entity.RegistrationUserDetail;

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

}
