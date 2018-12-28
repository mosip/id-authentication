package io.mosip.preregistration.transliteration.util;

import org.springframework.stereotype.Component;

import com.ibm.icu.text.Transliterator;

@Component
public class PreRegistrationTransliterator {
	
	public String translitrator(String languageId, String fieldValue) {
		
		Transliterator translitratedLanguage = Transliterator.getInstance(languageId);
		return translitratedLanguage.transliterate(fieldValue);
	}

}
