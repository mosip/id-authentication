package io.mosip.authentication.common.service.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
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
	private Environment environment;

	@Bean(name = "NotificationLangComparator")
	public LanguageComparator getLanguageComparator() {
		return new LanguageComparator(getSystemSupportedLanguageCodes());
	}

	public List<String> getSystemSupportedLanguageCodes() {
		String languages = environment.getProperty(IdAuthConfigKeyConstants.MOSIP_MANDATORY_LANGUAGES) + ","
				+ environment.getProperty(IdAuthConfigKeyConstants.MOSIP_OPTIONAL_LANGUAGES);
		return List.of(languages.split(","));
	}

}
