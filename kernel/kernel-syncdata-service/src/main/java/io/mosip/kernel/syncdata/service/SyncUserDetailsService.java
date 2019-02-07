package io.mosip.kernel.syncdata.service;

import io.mosip.kernel.syncdata.dto.SyncUserDetailDto;
import io.mosip.kernel.syncdata.dto.response.UserDetailResponseDto;

/**
 * This service class handles CRUD opertaion method signature
 * @author Srinivasan
 * @author Megha Tanga
 *
 */
public interface SyncUserDetailsService {

	/**
	 * This method will fetch all userdetails
	 * @param user
	 * @param timestamp
	 * @return {@link UserDetailResponseDto}
	 */
	SyncUserDetailDto getAllUserDetail(String regId);
}
