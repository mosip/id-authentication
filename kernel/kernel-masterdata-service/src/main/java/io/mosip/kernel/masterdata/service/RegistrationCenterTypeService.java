package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.RegistrationCenterTypeDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;

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
	 * @param registrationCenterTypeDto
	 *            the request dto {@link RegistrationCenterTypeDto}.
	 * @return the response {@link CodeAndLanguageCodeID}.
	 */
	public CodeAndLanguageCodeID createRegistrationCenterType(
			RequestDto<RegistrationCenterTypeDto> registrationCenterTypeDto);

	/**
	 * @param registrationCenterTypeDto
	 * @return
	 */
	public CodeAndLanguageCodeID updateRegistrationCenterType(
			RequestDto<RegistrationCenterTypeDto> registrationCenterTypeDto);

	/**
	 * @param registrationCenterTypeCode
	 * @return
	 */
	public CodeResponseDto deleteRegistrationCenterType(String registrationCenterTypeCode);

}
