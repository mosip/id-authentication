package io.mosip.authentication.common.service.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.util.LanguageComparator;

/**
 * Configuartion class for language comparator
 * 
 * @author Nagarjuna
 *
 */
@Configuration
public class LangComparatorConfig {

	@Autowired
	private EnvUtil environment;

	@Bean(name = "NotificationLangComparator")
	public LanguageComparator getLanguageComparator() {
		return new LanguageComparator(getSystemSupportedLanguageCodes());
	}

	public List<String> getSystemSupportedLanguageCodes() {
		String languages = EnvUtil.getMandatoryLanguages() + ","
				+ EnvUtil.getOptionalLanguages();
		return List.of(languages.split(","));
	}

}
