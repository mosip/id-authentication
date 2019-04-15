package io.mosip.kernel.syncdata.service;

import io.mosip.kernel.syncdata.dto.SyncUserDetailDto;

/**
 * This service class handles CRUD opertaion method signature
 * 
 * @author Srinivasan
 * @author Megha Tanga
 *
 */
public interface SyncUserDetailsService {

	/**
	 * This method would fetch all user details for that registration center id
	 * 
	 * @param regId - registration center id
	 * @return {@link SyncUserDetailDto}
	 */
	SyncUserDetailDto getAllUserDetail(String regId);
}
