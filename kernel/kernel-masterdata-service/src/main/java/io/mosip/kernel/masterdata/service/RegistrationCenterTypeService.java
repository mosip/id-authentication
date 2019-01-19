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
	 * Method to update registration center type.
	 * 
	 * @param registrationCenterTypeDto
	 *            the request dto {@link RegistrationCenterTypeDto}.
	 * @return the response {@link CodeAndLanguageCodeID}.
	 */
	public CodeAndLanguageCodeID updateRegistrationCenterType(
			RequestDto<RegistrationCenterTypeDto> registrationCenterTypeDto);

	/**
	 * Method to delete registration center type.
	 * 
	 * @param registrationCenterTypeCode
	 *            the code of the registration center type which needs to be
	 *            deleted.
	 * @return the response {@link CodeResponseDto}.
	 */
	public CodeResponseDto deleteRegistrationCenterType(String registrationCenterTypeCode);
}
