package io.mosip.registration.context;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.AuthTokenDTO;


/**
 * This class will load all the property files as bundles
 * All application level details will be loaded in a map
 * 
 * @author Taleev Aalam
 *
 */
public class ApplicationContext {

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(ApplicationContext.class);

	/** The application context. */
	private static ApplicationContext applicationContext;

	/** The application language bundle. */
	private ResourceBundle applicationLanguageBundle;

	/** The local language bundle. */
	private ResourceBundle localLanguageBundle;

	/** The application messages bundle. */
	private ResourceBundle applicationMessagesBundle;

	/** The local messages bundle. */
	private ResourceBundle localMessagesBundle;

	/** The application map. */
	private static Map<String, Object> applicationMap = new HashMap<>();

	/** The application languagevalidation bundle. */
	private ResourceBundle applicationLanguagevalidationBundle;

	/** The local language. */
	private String localLanguage;

	/** The application languge. */
	private String applicationLanguge;

	/** The primary language right to left. */
	private boolean primaryLanguageRightToLeft;

	/** The secondary language right to left. */
	private boolean secondaryLanguageRightToLeft;

	/**
	 * Checks if is primary language right to left.
	 *
	 * @return true, if is primary language right to left
	 */
	public boolean isPrimaryLanguageRightToLeft() {
		return primaryLanguageRightToLeft;
	}

	/**
	 * Checks if is secondary language right to left.
	 *
	 * @return true, if is secondary language right to left
	 */
	public boolean isSecondaryLanguageRightToLeft() {
		return secondaryLanguageRightToLeft;
	}

	/** The auth token DTO. */
	private AuthTokenDTO authTokenDTO;

	/**
	 * Instantiates a new application context.
	 */
	private ApplicationContext() {

	}

	/**
	 * Gets the application languagevalidation bundle.
	 *
	 * @return the application languagevalidation bundle
	 */
	public ResourceBundle getApplicationLanguagevalidationBundle() {
		return applicationLanguagevalidationBundle;
	}

	
	/**
	 * here we will load the property files such as labels, messages and validation.
	 * <p>If we get primary and secondary languages</P>
	 * 			<p>Based on those languages these property files will be loaded.</p>
	 * <p>If we dont get primary and secondary languages</p>
	 * 			<p>Then the primary language will be English and the Secondary language will be 
	 * 				Arabic by default and the property files will be loaded based on that</p>
	 * 
	 * 
	 */
	public void loadResourceBundle() {
		try {

			if (null != applicationMap.get(RegistrationConstants.PRIMARY_LANGUAGE)
					&& !applicationMap.get(RegistrationConstants.PRIMARY_LANGUAGE).equals("")) {
				applicationLanguge = (String) applicationMap.get(RegistrationConstants.PRIMARY_LANGUAGE);
			} else {
				applicationLanguge = Locale.getDefault().getDisplayLanguage() != null
						? Locale.getDefault().getDisplayLanguage().toLowerCase().substring(0, 3)
						: "eng";
			}
			if (null != applicationMap.get(RegistrationConstants.SECONDARY_LANGUAGE)
					&& !applicationMap.get(RegistrationConstants.SECONDARY_LANGUAGE).equals("")) {
				localLanguage = (String) applicationMap.get(RegistrationConstants.SECONDARY_LANGUAGE);
			} else {
				localLanguage = Locale.getDefault().getDisplayLanguage() != null
						? Locale.getDefault().getDisplayLanguage().toLowerCase().substring(0, 3)
						: "eng";
			}
			String rightToLeft = (String) applicationContext.getApplicationMap().get("mosip.right_to_left_orientation");

			if (null != rightToLeft) {
				if (rightToLeft.contains(applicationLanguge)) {
					primaryLanguageRightToLeft = true;
				}
				if (null != localLanguage && rightToLeft.contains(localLanguage)) {
					secondaryLanguageRightToLeft = true;
				}
			}

		} catch (RuntimeException exception) {
			LOGGER.error("Application Context", RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, exception.getMessage());
		}

		Locale applicationLanguageLocale = new Locale(
				applicationLanguge != null ? applicationLanguge.substring(0, 2) : "");
		Locale secondaryLanguageLocale = new Locale(localLanguage != null ? localLanguage.substring(0, 2) : "");

		applicationLanguageBundle = ResourceBundle.getBundle("labels", applicationLanguageLocale);
		localLanguageBundle = ResourceBundle.getBundle("labels", secondaryLanguageLocale);
		applicationMessagesBundle = ResourceBundle.getBundle("messages", applicationLanguageLocale);
		localMessagesBundle = ResourceBundle.getBundle("messages", secondaryLanguageLocale);
		applicationLanguagevalidationBundle = ResourceBundle.getBundle("validations");
	}



	/**
	 * Gets the single instance of ApplicationContext.
	 *
	 * @return single instance of ApplicationContext
	 */
	public static ApplicationContext getInstance() {
		if (applicationContext == null) {
			applicationContext = new ApplicationContext();
			applicationContext.authTokenDTO = new AuthTokenDTO();
			return applicationContext;
		} else {
			return applicationContext;
		}
	}

	/**
	 * Map.
	 *
	 * @return the map
	 */
	public static Map<String, Object> map() {
		return applicationContext.getApplicationMap();
	}

	/**
	 * Application language.
	 *
	 * @return the string
	 */
	public static String applicationLanguage() {
		return applicationContext.getApplicationLanguage();
	}

	/**
	 * Secondary language local.
	 *
	 * @return the string
	 */
	/*
	 * To return the local language code with two letter
	 */
	public static String secondaryLanguageLocal() {
		return applicationContext.getLocalLanguage().substring(0, 2);
	}

	/**
	 * Primary language local.
	 *
	 * @return the string
	 */
	/*
	 * To return the application language code with two letter
	 */
	public static String primaryLanguageLocal() {
		return applicationContext.getApplicationLanguage().substring(0, 2);
	}

	/**
	 * Local language.
	 *
	 * @return the string
	 */
	public static String localLanguage() {
		return applicationContext.getLocalLanguage();
	}

	/**
	 * Local language property.
	 *
	 * @return the resource bundle
	 */
	public static ResourceBundle localLanguageProperty() {
		return applicationContext.getLocalLanguageProperty();
	}

	/**
	 * Application language bundle.
	 *
	 * @return the resource bundle
	 */
	public static ResourceBundle applicationLanguageBundle() {
		return applicationContext.getApplicationLanguageBundle();
	}

	/**
	 * Local language bundle.
	 *
	 * @return the resource bundle
	 */
	public static ResourceBundle localLanguageBundle() {
		return applicationContext.getLocalLanguageProperty();
	}

	/**
	 * Application language validation bundle.
	 *
	 * @return the resource bundle
	 */
	public static ResourceBundle applicationLanguageValidationBundle() {
		return applicationContext.getApplicationLanguagevalidationBundle();
	}

	/**
	 * Local language validation bundle.
	 *
	 * @return the resource bundle
	 */
	public static ResourceBundle localLanguageValidationBundle() {
		return applicationContext.getLocalMessagesBundle();
	}

	/**
	 * Application messages bundle.
	 *
	 * @return the resource bundle
	 */
	public static ResourceBundle applicationMessagesBundle() {
		return applicationContext.getApplicationMessagesBundle();
	}

	/**
	 * Local messages bundle.
	 *
	 * @return the resource bundle
	 */
	public static ResourceBundle localMessagesBundle() {
		return applicationContext.getLocalMessagesBundle();
	}

	/**
	 * Load resources.
	 */
	public static void loadResources() {
		applicationContext.loadResourceBundle();
	}

	/**
	 * Sets the auth token DTO.
	 *
	 * @param authTokenDTO
	 *            the new auth token DTO
	 */
	public static void setAuthTokenDTO(AuthTokenDTO authTokenDTO) {
		applicationContext.authTokenDTO = authTokenDTO;
	}

	/**
	 * Auth token DTO.
	 *
	 * @return the auth token DTO
	 */
	public static AuthTokenDTO authTokenDTO() {
		return applicationContext.authTokenDTO;
	}

	/**
	 * Gets the application map.
	 *
	 * @return the applicationMap
	 */
	public Map<String, Object> getApplicationMap() {
		return applicationMap;
	}

	/**
	 * Sets the application map.
	 *
	 * @param applicationMap
	 *            the applicationMap to set
	 */
	public static void setApplicationMap(Map<String, Object> applicationMap) {
		ApplicationContext.applicationMap.putAll(applicationMap);
	}

	/**
	 * Gets the application language bundle.
	 *
	 * @return the application language bundle
	 */
	public ResourceBundle getApplicationLanguageBundle() {
		return applicationLanguageBundle;
	}



	/**
	 * Get application language.
	 *
	 * @return the application language
	 */
	public String getApplicationLanguage() {
		return applicationLanguge;
	}

	/**
	 * Get local language.
	 *
	 * @return the local language
	 */
	public String getLocalLanguage() {
		return localLanguage;
	}

	/**
	 * Gets the local language property.
	 *
	 * @return the local language property
	 */
	public ResourceBundle getLocalLanguageProperty() {
		return localLanguageBundle;
	}

	/**
	 * Sets the local language property.
	 *//*
	public void setLocalLanguageProperty() {
		localLanguageBundle = ResourceBundle.getBundle("labels", new Locale("ara"));
	}*/

	/**
	 * Gets the application messages bundle.
	 *
	 * @return the applicationMessagesBundle
	 */
	public ResourceBundle getApplicationMessagesBundle() {
		return applicationMessagesBundle;
	}



	/**
	 * Gets the local messages bundle.
	 *
	 * @return the localMessagesBundle
	 */
	public ResourceBundle getLocalMessagesBundle() {
		return localMessagesBundle;
	}



	/**
	 * Sets the global config value of.
	 *
	 * @param code
	 *            the code
	 * @param val
	 *            the val
	 */
	public static void setGlobalConfigValueOf(String code, String val) {
		applicationMap.put(code, val);
	}

	/**
	 * Removes the global config value of.
	 *
	 * @param code
	 *            the code
	 */
	public static void removeGlobalConfigValueOf(String code) {
		applicationMap.remove(code);

	}
}
