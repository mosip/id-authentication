package io.mosip.authentication.core.spi.indauth.match;

import java.util.Optional;

import io.mosip.authentication.core.dto.indauth.LanguageType;;

/**
 * The Id info fetcher interface.
 *
 * @author Loganathan.Sekaran
 */
public interface IdInfoFetcher {
	
	/**
	 * Get Language code for Language tupe
	 *
	 * @param langType language type
	 * @return language code
	 */
	public String getLanguageCode(LanguageType langType);
	
	/**
	 * Get language name for language code.
	 *
	 * @param languageCode language code
	 * @return language name
	 */
	public Optional<String> getLanguageName(String languageCode);
	

}
