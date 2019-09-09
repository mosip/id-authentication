package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.RegistrationCenterUserDto;
import io.mosip.kernel.masterdata.dto.UserAndRegCenterMappingResponseDto;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterUserID;

/**
 * @author Megha Tanga
 */

public interface RegistrationCenterUserService {
	
	/**
	 * This method un-maps a User  from a Registration Center .
	 * 
	 * @param userId
	 *            the user Id.
	 * @param regCenterId
	 *            the registration center Id.
	 * @return {@link UserAndRegCenterMappingResponseDto}.
	 */
	public UserAndRegCenterMappingResponseDto unmapUserRegCenter(String userId, String regCenterId ); 
	
	/**
	 * This method map a User  from a Registration Center .
	 * 
	 * @param userId
	 *            the user Id.
	 * @param regCenterId
	 *            the registration center Id.
	 * @return {@link UserAndRegCenterMappingResponseDto}.
	 */
	public UserAndRegCenterMappingResponseDto mapUserRegCenter(String userId, String regCenterId ); 
	
	public RegistrationCenterUserID createRegistrationCenterUser(RegistrationCenterUserDto regCenterUser);
	
	

}
