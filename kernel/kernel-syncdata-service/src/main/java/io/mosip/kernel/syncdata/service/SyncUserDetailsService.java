package io.mosip.kernel.syncdata.service;

import io.mosip.kernel.syncdata.dto.response.UserDetailResponseDto;

/**
 * This service class handles CRUD opertaion method signature
 * @author Srinivasan
 *
 */
public interface SyncUserDetailsService {

	/**
	 * This method will fetch all userdetails
	 * @param user
	 * @param timestamp
	 * @return {@link UserDetailResponseDto}
	 */
	UserDetailResponseDto getAllUserDetail(String regId);
}
