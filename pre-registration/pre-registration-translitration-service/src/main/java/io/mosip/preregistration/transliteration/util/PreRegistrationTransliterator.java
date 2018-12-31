package io.mosip.preregistration.transliteration.util;

import org.springframework.stereotype.Component;

import com.ibm.icu.text.Transliterator;

import io.mosip.preregistration.core.spi.translitertor.Translitertor;

@Component
public class PreRegistrationTransliterator implements Translitertor {

	@Override
	public String translitrator(String languageId, String fromFieldValue) {
		Transliterator translitratedLanguage = Transliterator.getInstance(languageId);
		return translitratedLanguage.transliterate(fromFieldValue);
	}

}
