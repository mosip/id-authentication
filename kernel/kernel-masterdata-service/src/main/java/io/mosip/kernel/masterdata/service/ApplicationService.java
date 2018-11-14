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

	public List<ApplicationDto> getAllApplication();

	public List<ApplicationDto> getAllApplicationByLanguageCode(String languageCode);

	public ApplicationDto getApplicationByCodeAndLanguageCode(String code, String languageCode);

}
