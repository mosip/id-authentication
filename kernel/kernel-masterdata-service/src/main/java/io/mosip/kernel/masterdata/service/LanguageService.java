package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.CodeResponseDto;
import io.mosip.kernel.masterdata.dto.LanguageDto;
import io.mosip.kernel.masterdata.dto.LanguageResponseDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;

/**
 * This interface provides methods to do CRUD operations on Language.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
public interface LanguageService {

	/**
	 * This method provides all the languages present in database.
	 * 
	 * @return LanguageResponseDto
	 * 
	 * @throws MasterDataServiceException
	 *             if any error occurs while retrieving languages
	 * 
	 * @throws DataNotFoundException
	 *             if no language found
	 */
	LanguageResponseDto getAllLaguages();

	/**
	 * This method save {@link LanguageDto} provide by the user in database.
	 * 
	 * @param requestDto
	 *            request {@link LanguageDto} data provided by the user which is
	 *            going to be persisted
	 * 
	 * @return language code which is created of type {@link CodeResponseDto}
	 * 
	 * 
	 * @throws MasterDataServiceException
	 *             if any error occurred while saving languages
	 */
	CodeResponseDto saveLanguage(RequestDto<LanguageDto> requestDto);

}
