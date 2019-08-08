package io.mosip.kernel.masterdata.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class LanguageUtils {

	private String primaryLanguage;
	private List<String> secondaryLanguages;
	private List<String> configuredLanguages;
	private List<String> supportedLangugaes;

	@Autowired
	public LanguageUtils(@Value("${mosip.primary-language}") String primary,
			@Value("${mosip.secondary-language:NOTSET}") String secondary,
			@Value("${mosip.supported-languages:NOTSET}") String supported) {

		this.primaryLanguage = primary;
		if ("NOTSET".equals(secondary)) {
			this.secondaryLanguages = Collections.emptyList();
		} else {
			this.secondaryLanguages = Arrays.asList(secondary.split(","));
		}

		if ("NOTSET".equals(supported)) {
			this.supportedLangugaes = Collections.emptyList();
		} else {
			this.supportedLangugaes = Arrays.asList(supported.split(","));
		}
		configuredLanguages = new ArrayList<>();
		configuredLanguages.add(primaryLanguage);
		configuredLanguages.addAll(secondaryLanguages);
	}

}
