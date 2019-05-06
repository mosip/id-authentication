package io.mosip.admin.accountmgmt.service;


/**
 * The Interface AccountManagementService.
 *
 * @author Srinivasan
 * @since 1.0.0
 */
public interface AccountManagementService {

	/**
	 * Gets the user name.
	 *
	 * @param userId the user id
	 * @param phoneNumber the phone number
	 * @return {@link String} user name
	 */
	public String getUserName(String userId,String phoneNumber);
}

