package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.masterdata.dto.GenderTypeDto;
import io.mosip.kernel.masterdata.dto.getresponse.GenderTypeResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.StatusResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.GenderExtnDto;
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
	public CodeAndLanguageCodeID saveGenderType(GenderTypeDto genderRequestDto);

	/**
	 * Method to update Gender Type based on data provided.
	 * 
	 * @param gender
	 *            {@link RequestWrapper} the request dto.
	 * @return {@link CodeAndLanguageCodeID}
	 */
	public CodeAndLanguageCodeID updateGenderType(GenderTypeDto gender);

	/**
	 * Method to delete Gender Type based on code provided.
	 * 
	 * @param code
	 *            the gender code.
	 * @return {@link CodeResponseDto}
	 */
	public CodeResponseDto deleteGenderType(String code);

	/**
	 * Method to validate gender name
	 * 
	 * @param genderName
	 * @return StatusResponseCode
	 */
	public StatusResponseDto validateGender(String genderName);

	/**
	 * This method provides with all gender types.
	 * 
	 * @param pageNumber
	 *            the page number
	 * @param pageSize
	 *            the size of each page
	 * @param sortBy
	 *            the attributes by which it should be ordered
	 * @param orderBy
	 *            the order to be used
	 * 
	 * @return the response i.e. pages containing the gender types
	 */
	PageDto<GenderExtnDto> getGenderTypes(int pageNumber, int pageSize, String sortBy, String orderBy);

}