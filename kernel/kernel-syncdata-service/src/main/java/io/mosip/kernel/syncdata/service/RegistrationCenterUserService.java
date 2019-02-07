package io.mosip.kernel.syncdata.service;

import io.mosip.kernel.syncdata.dto.response.RegistrationCenterUserResponseDto;

/**
 * 
 * @author Srinivasan
 *
 */
public interface RegistrationCenterUserService {

	public RegistrationCenterUserResponseDto getUsersBasedOnRegistrationCenterId(String regCenterId);
}
