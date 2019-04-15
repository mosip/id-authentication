package io.mosip.kernel.syncdata.service;

import io.mosip.kernel.syncdata.dto.response.RegistrationCenterUserResponseDto;

/**
 * 
 * @author Srinivasan
 *
 */
public interface RegistrationCenterUserService {
	/**
	 * 
	 * @param regCenterId -registration center id
	 * @return list of {@link RegistrationCenterUserResponseDto} - list of
	 *         registration user response dto
	 */
	public RegistrationCenterUserResponseDto getUsersBasedOnRegistrationCenterId(String regCenterId);
}
