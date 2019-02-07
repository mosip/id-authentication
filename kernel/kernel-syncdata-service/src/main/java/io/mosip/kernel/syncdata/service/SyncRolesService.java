package io.mosip.kernel.syncdata.service;

import io.mosip.kernel.syncdata.dto.response.RolesResponseDto;
/**
 * 
 * @author Srinivasan
 * Interface class where the methods are declared.
 *
 */
public interface SyncRolesService {

	/**
	 * This Method fethces all roles from the auth server
	 * @return RolesResponseDto
	 */
	RolesResponseDto getAllRoles();
}
