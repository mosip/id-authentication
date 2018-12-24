package io.mosip.registration.context;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;

public class ApplicationContext {

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(ApplicationContext.class);

	private static ApplicationContext applicationContext;

	private ApplicationContext() {
		try {
			applicationLanguageBundle = ResourceBundle.getBundle("labels",
					new Locale(AppConfig.getApplicationProperty("application_language")));
			localLanguageBundle = ResourceBundle.getBundle("labels",
					new Locale(AppConfig.getApplicationProperty("local_language")));
			applicationMessagesBundle = ResourceBundle.getBundle("messages",
					new Locale(AppConfig.getApplicationProperty("application_language")));
			localMessagesBundle = ResourceBundle.getBundle("messages",
					new Locale(AppConfig.getApplicationProperty("local_language")));
			applicationLanguagevalidationBundle = ResourceBundle.getBundle("validations",
					new Locale(AppConfig.getApplicationProperty("application_language")));

			LOGGER.debug("APPLICATION CONTEXT", APPLICATION_NAME, APPLICATION_ID, "Loaded resources successfully");
		} catch (NullPointerException | MissingResourceException exception  ) {
			LOGGER.error("APPLICATION CONTEXT", APPLICATION_NAME, APPLICATION_ID, exception.getMessage());

		}
	}

	private ResourceBundle applicationLanguageBundle;
	private ResourceBundle localLanguageBundle;
	private ResourceBundle applicationMessagesBundle;
	private ResourceBundle localMessagesBundle;
	private ResourceBundle applicationLanguagevalidationBundle;
	private Map<String, Object> applicationMap;

	public static ApplicationContext getInstance() {
		if (applicationContext == null) {
			applicationContext = new ApplicationContext();
			return applicationContext;
		} else {
			return applicationContext;
		}
	}

	/**
	 * @return the application language validation bundle
	 */
	public ResourceBundle getApplicationLanguagevalidationBundle() {
		return applicationLanguagevalidationBundle;
	}

	/**
	 * @return the applicationMap
	 */
	public Map<String, Object> getApplicationMap() {
		return applicationMap;
	}

	/**
	 * @param applicationMap
	 *            the applicationMap to set
	 */
	public void setApplicationMap(Map<String, Object> applicationMap) {
		this.applicationMap = applicationMap;
	}

	/**
	 * @return the application language label bundle
	 */
	public ResourceBundle getApplicationLanguageBundle() {
		return applicationLanguageBundle;
	}

	/**
	 * @return the application language label bundle
	 */
	public ResourceBundle getLocalLanguageProperty() {
		return localLanguageBundle;
	}

	/**
	 * @return the applicationMessagesBundle
	 */
	public ResourceBundle getApplicationMessagesBundle() {
		return applicationMessagesBundle;
	}

	/**
	 * @return the localMessagesBundle
	 */
	public ResourceBundle getLocalMessagesBundle() {
		return localMessagesBundle;
	}

}
