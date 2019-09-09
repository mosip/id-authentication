package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.UserAndRegCenterMappingResponseDto;

/**
 * @author Megha Tanga
 */

public interface RegistrationCenterUserService {
	
	/**
	 * This method un-maps a User  from a Registration Center .
	 * 
	 * @param userId
	 *            the device Id.
	 * @param regCenterId
	 *            the registration center Id.
	 * @return {@link UserAndRegCenterMappingResponseDto}.
	 */
	public UserAndRegCenterMappingResponseDto unmapUserRegCenter(String userId, String regCenterId ); 
	

}
