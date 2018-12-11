package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.GenderTypeDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.GenderTypeResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;

/**
 * This class contains methods to fetch gender types
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public interface GenderTypeService {
	/**
	 * This method returns all the gender types available
	 * 
	 * @return
	 */
	GenderTypeResponseDto getAllGenderTypes();

	/**
	 * This method is used to get all gender types for a particular language code
	 * 
	 * @param langCode
	 *            the language code for which the gender types are needed
	 * @return all gender types for the given language code
	 */
	GenderTypeResponseDto getGenderTypeByLangCode(String langCode);

	/**
	 * @param genderRequestDto
	 *            input parameters for creating gender data
	 * @return code and langCode of the data entered
	 */
	public CodeAndLanguageCodeID saveGenderType(RequestDto<GenderTypeDto> genderRequestDto);

}
