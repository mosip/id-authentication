package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.LanguageRequestResponseDto;

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
	 * This method create all Languages present in <code>dto</code>.
	 * 
	 * @see LanguageRequestResponseDto
	 * @param dto
	 * @return LanguageRequestResponseDto
	 */
	LanguageRequestResponseDto saveAllLanguages(LanguageRequestResponseDto dto);

}
