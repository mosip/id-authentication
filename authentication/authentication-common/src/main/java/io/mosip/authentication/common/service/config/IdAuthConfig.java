package io.mosip.authentication.common.service.config;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.FACE_PROVIDER;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.FINGERPRINT_PROVIDER;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IRIS_PROVIDER;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.MOSIP_ERRORMESSAGES_DEFAULT_LANG;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.kernel.core.bioapi.spi.IBioApi;

/**
 * Class for defining configurations for the service.
 * 
 * @author Manoj SP
 *
 */
@Configuration
public class IdAuthConfig {

	/** The environment. */
	@Autowired
	private Environment environment;

	@Bean("finger")
	public IBioApi fingerApi() throws IdAuthenticationAppException {
		try {
			return (IBioApi) Class.forName(environment.getProperty(FINGERPRINT_PROVIDER)).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new IdAuthenticationAppException("", "Unable to load fingerprint provider", e);
		}
	}
	
//	@Bean("face")
	public IBioApi faceApi() throws IdAuthenticationAppException {
		try {
			return (IBioApi) Class.forName(environment.getProperty(FACE_PROVIDER)).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new IdAuthenticationAppException("", "Unable to load face provider", e);
		}
	}
	
//	@Bean("iris")
	public IBioApi irisApi() throws IdAuthenticationAppException {
		try {
			return (IBioApi) Class.forName(environment.getProperty(IRIS_PROVIDER)).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new IdAuthenticationAppException("", "Unable to load iris provider", e);
		}
	}

	/**
	 * Locale resolver.
	 *
	 * @return the locale resolver
	 */
	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
		Locale locale = new Locale(environment.getProperty(MOSIP_ERRORMESSAGES_DEFAULT_LANG));
		LocaleContextHolder.setLocale(locale);
		sessionLocaleResolver.setDefaultLocale(locale);
		return sessionLocaleResolver;
	}

	/**
	 * Message source.
	 *
	 * @return the message source
	 */
	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.addBasenames("errormessages", "actionmessages");
		return source;
	}

}
