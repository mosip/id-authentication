package io.mosip.authentication.service.impl.indauth.service.demo;

import io.mosip.authentication.core.dto.indauth.LanguageType;

@FunctionalInterface
public interface LanguageInfoFetcher {
	String getLanguage(LanguageType langType);
}
