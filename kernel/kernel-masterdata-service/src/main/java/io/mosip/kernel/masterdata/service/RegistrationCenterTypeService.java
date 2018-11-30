package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.RegistrationCenterTypeRequestDto;
import io.mosip.kernel.masterdata.dto.postresponse.PostResponseDto;

/**
 * Interface that provides methods for RegistrationCenterType operations.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public interface RegistrationCenterTypeService {
	/**
	 * Method to add registration center type.
	 * 
	 * @param registrationCenterTypeRequestDto
	 *            the request dto {@link RegistrationCenterTypeRequestDto}.
	 * @return the response {@link PostResponseDto}.
	 */
	PostResponseDto addRegistrationCenterType(RegistrationCenterTypeRequestDto registrationCenterTypeRequestDto);

}
