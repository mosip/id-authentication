package io.mosip.registration.dto.mastersync;

import java.util.List;

/**
 * 
 * @author Sreekar Chukka
 * @Version 1.0.0
 */

public class LanguageResponseDto {
	
	private List<LanguageDto> language;

	/**
	 * @return the language
	 */
	public List<LanguageDto> getLanguage() {
		return language;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(List<LanguageDto> language) {
		this.language = language;
	}

	

}
