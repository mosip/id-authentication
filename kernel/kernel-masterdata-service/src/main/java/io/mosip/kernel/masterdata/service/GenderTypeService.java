package io.mosip.kernel.masterdata.service;

import java.util.List;

import javax.validation.Valid;

import io.mosip.kernel.masterdata.dto.GenderTypeDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.GenderTypeResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;

/**
 * This class contains methods to fetch gender types
 * 
 * @author Urvil Joshi
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public interface GenderTypeService {
	/**
	 * This method returns all the gender types available
	 * 
	 * @return list of all gender data
	 * @throws MasterDataServiceException
	 *             when data not fetched from DB
	 * @throws DataNotFoundException
	 *             when data not found
	 */
	GenderTypeResponseDto getAllGenderTypes();

	/**
	 * This method is used to get all gender types for a particular language code
	 * 
	 * @param langCode
	 *            the language code for which the gender types are needed
	 * @return all gender types for the given language code
	 * @throws MasterDataServiceException
	 *             when data not fetched from DB
	 * @throws DataNotFoundException
	 *             when data not found
	 */
	GenderTypeResponseDto getGenderTypeByLangCode(String langCode);

	/**
	 * @param genderRequestDto
	 *            input parameters for creating gender data
	 * @return code and langCode of the data entered
	 * @throws MasterDataServiceException
	 *             when entered data not created
	 */
	public CodeAndLanguageCodeID saveGenderType(RequestDto<GenderTypeDto> genderRequestDto);

	/**
	 * Method to update Gender Type based on data provided.
	 * 
	 * @param gender
	 *            {@link RequestDto} the request dto.
	 * @return {@link CodeAndLanguageCodeID}
	 */
	public CodeAndLanguageCodeID updateGenderType(@Valid RequestDto<GenderTypeDto> gender);

	/**
	 * Method to delete Gender Type based on code provided.
	 * 
	 * @param code
	 *            the gender code.
	 * @return {@link CodeResponseDto}
	 */
	public CodeResponseDto deleteGenderType(String code);

}
