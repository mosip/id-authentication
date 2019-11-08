package io.mosip.authentication.common.service.config;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.COMPOSITE_BIO_PROVIDER;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.FACE_PROVIDER;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.FINGERPRINT_PROVIDER;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IRIS_PROVIDER;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.MOSIP_ERRORMESSAGES_DEFAULT_LANG;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;

import org.hibernate.Interceptor;
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
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.bioapi.spi.IBioApi;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.dataaccess.hibernate.config.HibernateDaoConfig;

/**
 * Class for defining configurations for the service.
 * 
 * @author Manoj SP
 *
 */
public abstract class IdAuthConfig extends HibernateDaoConfig{
	
	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(IdAuthConfig.class);

	/** The environment. */
	@Autowired
	private Environment environment;
	
	/** The interceptor. */
	@Autowired
	private Interceptor interceptor;
	
	/** The bio api. */
	private IBioApi bioApi;
	
	/**
	 * Initialize.
	 */
	@PostConstruct
	public void initialize() {
		IdType.initializeAliases(environment);
	}
	
	/* (non-Javadoc)
	 * @see io.mosip.kernel.dataaccess.hibernate.config.HibernateDaoConfig#jpaProperties()
	 */
	@Override
	public Map<String, Object> jpaProperties() {
		Map<String, Object> jpaProperties = super.jpaProperties();
		jpaProperties.put("hibernate.ejb.interceptor", interceptor);
		return jpaProperties;
	}

	/**
	 * Finger provider.
	 *
	 * @return the i bio api
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@Bean("finger")
	public IBioApi fingerProvider() throws IdAuthenticationAppException {
		return getBiometricProvider(FINGERPRINT_PROVIDER, "finger", this::isFingerAuthEnabled);
	}

	/**
	 * Face provider.
	 *
	 * @return the i bio api
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@Bean("face")
	public IBioApi faceProvider() throws IdAuthenticationAppException {
		return getBiometricProvider(FACE_PROVIDER, "face", this::isFaceAuthEnabled);
	}
	
	/**
	 * Iris provider.
	 *
	 * @return the i bio api
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@Bean("iris")
	public IBioApi irisProvider() throws IdAuthenticationAppException {
		return getBiometricProvider(IRIS_PROVIDER, "iris", this::isIrisAuthEnabled);
	}
	
	/**
	 * Composite biometric provider.
	 *
	 * @return the i bio api
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@Bean("composite")
	public IBioApi compositeBiometricProvider() throws IdAuthenticationAppException {
		return getBiometricProvider(COMPOSITE_BIO_PROVIDER, "CompositBiometrics", 
					() -> this.isFingerAuthEnabled() || this.isIrisAuthEnabled() || this.isFaceAuthEnabled());
	}

	/**
	 * Gets the biometric provider.
	 *
	 * @param property the property
	 * @param modalityName the modality name
	 * @param enablementChecker the enablement checker
	 * @return the biometric provider
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	private IBioApi getBiometricProvider(String property, String modalityName, Supplier<Boolean> enablementChecker)
			throws IdAuthenticationAppException {
		try {
			if (Objects.nonNull(bioApi)
					&& bioApi.getClass().getName().contentEquals(environment.getProperty(property))) {
				return bioApi;
			} else if (Objects.nonNull(environment.getProperty(property)) && enablementChecker.get()) {
				bioApi = (IBioApi) Class.forName(environment.getProperty(property)).newInstance();
				return bioApi;
			} else {
				return null;
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, "IdAuthConfig", modalityName + "Provider",
					ExceptionUtils.getStackTrace(e));
			throw new IdAuthenticationAppException("", "Unable to load " + modalityName + " provider", e);
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
