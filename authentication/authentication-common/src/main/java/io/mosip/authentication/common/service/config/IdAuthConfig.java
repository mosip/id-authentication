package io.mosip.authentication.common.service.config;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.FACE_PROVIDER;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.FINGERPRINT_PROVIDER;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IRIS_PROVIDER;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.MOSIP_ERRORMESSAGES_DEFAULT_LANG;

import java.util.Locale;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.bioapi.spi.IBioApi;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * Class for defining configurations for the service.
 * 
 * @author Manoj SP
 *
 */
public abstract class IdAuthConfig {
	
	private static Logger mosipLogger = IdaLogger.getLogger(IdAuthConfig.class);

	/** The environment. */
	@Autowired
	private Environment environment;

	/**
	 * Finger provider.
	 *
	 * @return the i bio api
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@Bean("finger")
	public IBioApi fingerProvider() throws IdAuthenticationAppException {
		try {
			if (Objects.nonNull(environment.getProperty(FINGERPRINT_PROVIDER)) && isFingerAuthEnabled()) {
				return (IBioApi) Class.forName(environment.getProperty(FINGERPRINT_PROVIDER)).newInstance();
			} else {
				return null;
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, "IdAuthConfig", "fingerProvider",
					ExceptionUtils.getStackTrace(e));
			throw new IdAuthenticationAppException("", "Unable to load fingerprint provider", e);
		}
	}

	/**
	 * Face provider.
	 *
	 * @return the i bio api
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@Bean("face")
	public IBioApi faceProvider() throws IdAuthenticationAppException {
		try {
			if (Objects.nonNull(environment.getProperty(FACE_PROVIDER)) && isFaceAuthEnabled()) {
				return (IBioApi) Class.forName(environment.getProperty(FACE_PROVIDER)).newInstance();
			} else {
				return null;
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, "IdAuthConfig", "faceProvider",
					ExceptionUtils.getStackTrace(e));
			throw new IdAuthenticationAppException("", "Unable to load face provider", e);
		}
	}
	
	/**
	 * Iris provider.
	 *
	 * @return the i bio api
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@Bean("iris")
	public IBioApi irisProvider() throws IdAuthenticationAppException {
		try {
			if (Objects.nonNull(environment.getProperty(IRIS_PROVIDER)) && isIrisAuthEnabled()) {
				return (IBioApi) Class.forName(environment.getProperty(IRIS_PROVIDER)).newInstance();
			} else {
				return null;
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, "IdAuthConfig", "irisProvider",
					ExceptionUtils.getStackTrace(e));
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
	
	/**
	 * Checks if is finger auth enabled.
	 *
	 * @return true, if is finger auth enabled
	 */
	protected abstract boolean isFingerAuthEnabled();
	
	/**
	 * Checks if is face auth enabled.
	 *
	 * @return true, if is face auth enabled
	 */
	protected abstract boolean isFaceAuthEnabled();
	
	/**
	 * Checks if is iris auth enabled.
	 *
	 * @return true, if is iris auth enabled
	 */
	protected abstract boolean isIrisAuthEnabled();

}
