package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.LanguageDto;
import io.mosip.kernel.masterdata.dto.LanguageRequestResponseDto;
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
	 * @return LanguageRequestResponseDto
	 */
	LanguageRequestResponseDto getAllLaguages();

	/**
	 * This method save {@link LanguageDto} provide by the user in database.
	 * 
	 * @param dto
	 *            request {@link LanguageDto} data provided by the user which is
	 *            going to be persisted
	 * 
	 * @return language code which is created of type {@link String}
	 * 
	 * 
	 * @throws MasterDataServiceException
	 *             if any error occurred while saving languages
	 */
	String saveLanguage(LanguageDto dto);

}
