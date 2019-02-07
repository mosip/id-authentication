package io.mosip.kernel.transliteration.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ibm.icu.text.Transliterator;

import io.mosip.kernel.core.transliteration.spi.Transliteration;
import io.mosip.kernel.transliteration.constant.LanguageIdConstant;
import io.mosip.kernel.transliteration.constant.TransliterationErrorConstant;
import io.mosip.kernel.transliteration.constant.TransliterationPropertyConstant;
import io.mosip.kernel.transliteration.exception.InvalidTransliterationException;

/**
 * This class perform transliteration of text based on language code mention.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Component
public class TransliterationImpl implements Transliteration<String> {

	/**
	 * 
	 * Key for arabic language.
	 */
	@Value("${mosip.kernel.transliteration.arabic-language-code}")
	private String arabicLanguageCode;

	/**
	 * Key for french language.
	 */
	@Value("${mosip.kernel.transliteration.franch-language-code}")
	private String frenchLanguageCode;

	/**
	 * Language and corresponding id map.
	 */
	Map<String, String> languageIdMap;

	/**
	 * Method to create language map.
	 */
	@PostConstruct
	private void getLanguageMap() {

		languageIdMap = new HashMap<>();

		languageIdMap.put(arabicLanguageCode, LanguageIdConstant.ARABIC.getLanguage());

		languageIdMap.put(frenchLanguageCode, LanguageIdConstant.FRENCH.getLanguage());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.transliteration.spi.Transliteration#transliterate(java.
	 * lang.Object, java.lang.Object, java.lang.String)
	 */
	@Override
	public String transliterate(String fromLanguage, String toLanguage, String text) {

		Transliterator translitratedLanguage;

		String fromLanguageCode = languageIdMap.get(fromLanguage);

		String toLanguageCode = languageIdMap.get(toLanguage);

		if (fromLanguageCode == null || toLanguageCode == null) {
			throw new InvalidTransliterationException(
					TransliterationErrorConstant.TRANSLITERATION_INVALID_LANGUAGE_CODE.getErrorCode(),
					TransliterationErrorConstant.TRANSLITERATION_INVALID_LANGUAGE_CODE.getErrorMessage());
		}

		String languageId = fromLanguageCode
				+ TransliterationPropertyConstant.TRANSLITERATION_ID_SEPARATOR.getProperty() + toLanguageCode;

		try {
			translitratedLanguage = Transliterator.getInstance(languageId);

		} catch (IllegalArgumentException e) {
			throw new InvalidTransliterationException(
					TransliterationErrorConstant.TRANSLITERATION_INVALID_ID.getErrorCode(),
					TransliterationErrorConstant.TRANSLITERATION_INVALID_ID.getErrorMessage(), e);
		}

		return translitratedLanguage.transliterate(text);
	}

}
