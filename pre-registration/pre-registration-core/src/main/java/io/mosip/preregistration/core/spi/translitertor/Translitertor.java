package io.mosip.preregistration.core.spi.translitertor;

public interface Translitertor {
	public String translitrator(String languageId, String formFieldValue);
}
