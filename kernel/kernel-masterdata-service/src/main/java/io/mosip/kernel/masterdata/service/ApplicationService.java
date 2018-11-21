package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.ApplicationDto;
import io.mosip.kernel.masterdata.dto.ApplicationRequestDto;
import io.mosip.kernel.masterdata.dto.ApplicationResponseDto;
import io.mosip.kernel.masterdata.dto.PostResponseDto;

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
	public ApplicationResponseDto getAllApplication();

	/**
	 * Get All Applications by language  code
	 * 
	 * @param languageCode
	 * @return {@link List<ApplicationDto>}
	 */
	public ApplicationResponseDto getAllApplicationByLanguageCode(String languageCode);

	/**
	 * Get An Application by code and language  code
	 * 
	 * @param code
	 * @param languageCode
	 * @return {@link ApplicationDto}
	 */
	public ApplicationResponseDto getApplicationByCodeAndLanguageCode(String code, String languageCode);

	public PostResponseDto addApplicationData(ApplicationRequestDto application);

}
