package io.mosip.admin.accountmgmt.service;

import io.mosip.admin.accountmgmt.dto.UnBlockResponseDto;
import io.mosip.admin.accountmgmt.dto.UserNameDto;


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
	 * @param userId            the user id
	 * @return {@link String} user name
	 */
	public UserNameDto getUserName(String userId);
	
	
	/**
	 * Un block user name.
	 *
	 * @param userId the user id
	 * @return the un block response dto
	 */
	public UnBlockResponseDto unBlockUserName(String userId);
}
