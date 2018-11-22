package io.mosip.kernel.masterdata.service;

import java.util.List;

import io.mosip.kernel.masterdata.dto.ApplicationDto;

/**
 * 
 * @author Neha
 * @since 1.0.0
 * 
 */
public interface ApplicationService {
	
	/**
	 * Get All Applications
	 * 
	 * @return {@link List<ApplicationDto>}
	 */
	public List<ApplicationDto> getAllApplication();

	/**
	 * Get All Applications by language  code
	 * 
	 * @param languageCode
	 * @return {@link List<ApplicationDto>}
	 */
	public List<ApplicationDto> getAllApplicationByLanguageCode(String languageCode);

	/**
	 * Get An Application by code and language  code
	 * 
	 * @param code
	 * @param languageCode
	 * @return {@link ApplicationDto}
	 */
	public ApplicationDto getApplicationByCodeAndLanguageCode(String code, String languageCode);

}
