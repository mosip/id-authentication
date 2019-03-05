package io.mosip.registration.processor.packet.service.context;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import io.mosip.kernel.core.logger.spi.Logger;

import io.mosip.registration.processor.packet.service.constants.RegistrationConstants;

public class ApplicationContext {

	/**
	 * Instance of {@link Logger}
	 */


	private static ApplicationContext applicationContext;
	private ResourceBundle applicationLanguageBundle;
	private ResourceBundle localLanguageBundle;
	private ResourceBundle applicationMessagesBundle;
	private ResourceBundle localMessagesBundle;
	private Map<String, Object> applicationMap = new HashMap<>();
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
		try {
			applicationLanguge = (String) applicationMap.get(RegistrationConstants.PRIMARY_LANGUAGE);
			localLanguage = (String) applicationMap.get(RegistrationConstants.SECONDARY_LANGUAGE);
		} catch (RuntimeException exception) {
			//LOGGER.error("Application Context", RegistrationConstants.APPLICATION_NAME,
					//RegistrationConstants.APPLICATION_ID, exception.getMessage());
		}
		if (applicationLanguge == null || RegistrationConstants.EMPTY.equals(applicationLanguge)) {
			applicationLanguge = RegistrationConstants.LANGUAGE_ENGLISH;
		}
		if (localLanguage == null || RegistrationConstants.EMPTY.equals(localLanguage)) {
			localLanguage = RegistrationConstants.LANGUAGE_ARABIC;
		}
		
		Locale applicationLanguageLocale = new Locale(applicationLanguge.substring(0, 2));
		Locale secondaryLanguageLocale = new Locale(localLanguage.substring(0, 2));

		applicationLanguageBundle = ResourceBundle.getBundle("labels", applicationLanguageLocale);
		localLanguageBundle = ResourceBundle.getBundle("labels", secondaryLanguageLocale);
		applicationMessagesBundle = ResourceBundle.getBundle("messages", applicationLanguageLocale);
		localMessagesBundle = ResourceBundle.getBundle("messages", secondaryLanguageLocale);
		applicationLanguagevalidationBundle = ResourceBundle.getBundle("validations", applicationLanguageLocale);
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
	
	/*
	 * To return the local language code with two letter
	 */
	public static String secondaryLanguageLocal() {
		return applicationContext.getLocalLanguage().substring(0,2);
	}
	
	/*
	 * To return the application language code with two letter
	 */		
	public static String primaryLanguageLocal() {
		return applicationContext.getApplicationLanguage().substring(0,2);
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
		//applicationLanguageBundle = ResourceBundle.getBundle("labels",
			//	new Locale(AppConfig.getApplicationProperty("application_language")));
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
		//localLanguageBundle = ResourceBundle.getBundle("labels",
			//	new Locale(AppConfig.getApplicationProperty("local_language")));
	}

	/**
	 * @return the applicationMessagesBundle
	 */
	public ResourceBundle getApplicationMessagesBundle() {
		return applicationMessagesBundle;
	}

	public void setApplicationMessagesBundle() {
	//	applicationMessagesBundle = ResourceBundle.getBundle("messages",
			//	new Locale(AppConfig.getApplicationProperty("application_language")));
	}

	/**
	 * @return the localMessagesBundle
	 */
	public ResourceBundle getLocalMessagesBundle() {
		return localMessagesBundle;
	}

	public void setLocalMessagesBundle() {
		//localMessagesBundle = ResourceBundle.getBundle("messages",
			//	new Locale(AppConfig.getApplicationProperty("local_language")));
	}

}
