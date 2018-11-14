package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.TitleResponseDto;

public interface TitleService {
	
	TitleResponseDto getAllTitles();
	
	TitleResponseDto getByLanguageCode(String languageCode);

}
