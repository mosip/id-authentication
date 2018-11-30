package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.getresponse.GenderRequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.GenderTypeResponseDto;

/**
 * This class contains methods to getch gender types
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
	 * @param languageCode
	 *            the language code for which the gender types are needed
	 * @return all gender types for the given language code
	 */
	GenderTypeResponseDto getGenderTypeByLanguageCode(String languageCode);

	/**
	 * Method to add a gender type
	 * 
	 * @param genderRequestDto
	 *            input column parameters to add gender data
	 * @return added row of gender type
	 */
	GenderTypeResponseDto saveGenderType(GenderRequestDto genderRequestDto);

}
