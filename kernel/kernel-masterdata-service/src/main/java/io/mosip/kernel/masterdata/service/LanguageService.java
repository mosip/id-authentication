package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.LanguageRequestResponseDto;

/**
 * This interface provides methods to do CRUD operations on Language details.
 * 
 * @author Bal Vikash Sharma
 * @Version 1.0.0
 */
public interface LanguageService {

	/**
	 * This method fetch all Languages present in database.
	 * 
	 * @return LanguageRequestResponseDto
	 */
	LanguageRequestResponseDto getAllLaguages();

	/**
	 * This method create all Languages present in dto.
	 * 
	 * @param dto
	 * @return LanguageRequestResponseDto
	 */
	LanguageRequestResponseDto saveAllLanguages(LanguageRequestResponseDto dto);

}
