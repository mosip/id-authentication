package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.RegistrationCenterTypeDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
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
	 *            the request dto {@link RegistrationCenterTypeRequestDto}.
	 * @return the response {@link CodeAndLanguageCodeID}.
	 */
	CodeAndLanguageCodeID createRegistrationCenterType(RequestDto<RegistrationCenterTypeDto> registrationCenterTypeDto);

}
