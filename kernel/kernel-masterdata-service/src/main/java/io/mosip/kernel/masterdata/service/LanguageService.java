package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.LanguageDto;
import io.mosip.kernel.masterdata.dto.LanguageRequestResponseDto;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;

/**
 * This interface provides methods to do CRUD operations on Language.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
public interface LanguageService {

	/**
	 * This method provides all the languages having <b>isActive</b> is <b>true</b>
	 * and <b>isDeleted</b> is <b>false</b> present in MOSIP system.
	 * 
	 * @return LanguageRequestResponseDto
	 */
	LanguageRequestResponseDto getAllLaguages();

	/**
	 * This method save all {@link LanguageDto} provide by the user in
	 * {@link LanguageRequestResponseDto}
	 * 
	 * @param dto
	 *            request {@link LanguageRequestResponseDto} data contains list of
	 *            languages provided by the user which is going to be persisted
	 * 
	 * @return a {@link LanguageRequestResponseDto} which has all the list of saved
	 *         {@link LanguageDto}
	 * 
	 * @throws RequestException
	 *             if any request data is null
	 * 
	 * @throws MasterDataServiceException
	 *             if any error occurred while saving languages
	 */
	LanguageRequestResponseDto saveAllLanguages(LanguageRequestResponseDto dto);

}
