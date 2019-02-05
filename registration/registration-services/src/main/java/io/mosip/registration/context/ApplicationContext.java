package io.mosip.registration.context;

import java.util.Locale;
import java.util.Map;
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
	private ResourceBundle applicationLanguageBundle;
	private ResourceBundle localLanguageBundle;
	private ResourceBundle applicationMessagesBundle;
	private ResourceBundle localMessagesBundle;
	private Map<String, Object> applicationMap;
	private ResourceBundle applicationLanguagevalidationBundle;
	private String localLanguage;
	private String applicationLanguge;

	private ApplicationContext() {
	
	}

	public ResourceBundle getApplicationLanguagevalidationBundle() {
		return applicationLanguagevalidationBundle;
	}

	/**
	 * Loading resource bundle
	 */
	public void loadResourceBundle() {
		String primaryLanguage = "";
		String secondaryLanguage = "";
		try {
			primaryLanguage = (String) applicationMap.get(RegistrationConstants.PRIMARY_LANGUAGE);
			secondaryLanguage = (String) applicationMap.get(RegistrationConstants.SECONDARY_LANGUAGE);
		} catch (RuntimeException exception) {
			LOGGER.error("Application Context","", RegistrationConstants.APPLICATION_ID,
					exception.getMessage());
		}
		if (RegistrationConstants.EMPTY.equals(primaryLanguage)) {
			primaryLanguage = RegistrationConstants.LANGUAGE_ENGLISH;
		}
		if (RegistrationConstants.EMPTY.equals(secondaryLanguage)) {
			secondaryLanguage = RegistrationConstants.LANGUAGE_ARABIC;
		}

		if (primaryLanguage == null) {
			primaryLanguage = RegistrationConstants.LANGUAGE_ENGLISH;
		}
		if (secondaryLanguage == null) {
			secondaryLanguage = RegistrationConstants.LANGUAGE_ARABIC;
		}

		applicationLanguge = primaryLanguage.substring(0, 3);
		localLanguage = secondaryLanguage.substring(0, 3);

		applicationLanguageBundle = ResourceBundle.getBundle("labels", new Locale(applicationLanguge.substring(0, 2)));
		localLanguageBundle = ResourceBundle.getBundle("labels", new Locale(localLanguage.substring(0, 2)));
		applicationMessagesBundle = ResourceBundle.getBundle("messages",
				new Locale(applicationLanguge.substring(0, 2)));
		localMessagesBundle = ResourceBundle.getBundle("messages", new Locale(localLanguage.substring(0, 2)));
		applicationLanguagevalidationBundle = ResourceBundle.getBundle("validations",
				new Locale(applicationLanguge.substring(0, 2)));
	}

	public void setApplicationLanguagevalidationBundle(ResourceBundle applicationLanguagevalidationBundle) {
		this.applicationLanguagevalidationBundle = applicationLanguagevalidationBundle;
	}

	public static ApplicationContext getInstance() {
		if (applicationContext == null) {
			applicationContext = new ApplicationContext();
			return applicationContext;
		} else {
			return applicationContext;
		}
	}

	public static Map<String, Object> map() {
		return applicationContext.getApplicationMap();
	}

	public static String applicationLanguage() {
		return applicationContext.getApplicationLanguage();
	}

	public static String localLanguage() {
		return applicationContext.getLocalLanguage();
	}

	public static ResourceBundle localLanguageProperty() {
		return applicationContext.getLocalLanguageProperty();
	}

	public static ResourceBundle applicationLanguageBundle() {
		return applicationContext.getApplicationLanguageBundle();
	}

	public static ResourceBundle localLanguageBundle() {
		return applicationContext.getLocalLanguageProperty();
	}

	public static ResourceBundle applicationLanguageValidationBundle() {
		return applicationContext.getApplicationLanguagevalidationBundle();
	}

	public static ResourceBundle localLanguageValidationBundle() {
		return applicationContext.getLocalMessagesBundle();
	}

	public static ResourceBundle applicationMessagesBundle() {
		return applicationContext.getApplicationMessagesBundle();
	}

	public static void loadResources() {
		applicationContext.loadResourceBundle();
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

	public ResourceBundle getApplicationLanguageBundle() {
		return applicationLanguageBundle;
	}

	public void setApplicationLanguageBundle() {
		applicationLanguageBundle = ResourceBundle.getBundle("labels",
				new Locale(AppConfig.getApplicationProperty("application_language")));
	}
	
	/**
	 * Get application language
	 */
	public String getApplicationLanguage() {
		return applicationLanguge;
	}

	/**
	 * Get local language
	 */
	public String getLocalLanguage() {
		return localLanguage;
	}

	public ResourceBundle getLocalLanguageProperty() {
		return localLanguageBundle;
	}

	public void setLocalLanguageProperty() {
		localLanguageBundle = ResourceBundle.getBundle("labels",
				new Locale(AppConfig.getApplicationProperty("local_language")));
	}

	/**
	 * @return the applicationMessagesBundle
	 */
	public ResourceBundle getApplicationMessagesBundle() {
		return applicationMessagesBundle;
	}

	public void setApplicationMessagesBundle() {
		applicationMessagesBundle = ResourceBundle.getBundle("messages",
				new Locale(AppConfig.getApplicationProperty("application_language")));
	}

	/**
	 * @return the localMessagesBundle
	 */
	public ResourceBundle getLocalMessagesBundle() {
		return localMessagesBundle;
	}

	public void setLocalMessagesBundle() {
		localMessagesBundle = ResourceBundle.getBundle("messages",
				new Locale(AppConfig.getApplicationProperty("local_language")));
	}

}
