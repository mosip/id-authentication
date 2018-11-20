package io.mosip.registration.context;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import io.mosip.registration.config.AppConfig;

public class ApplicationContext{

	private static ApplicationContext applicationContext;

	private ResourceBundle applicationLanguageBundle;
	private Properties localLanguageProperty;

	public ResourceBundle getApplicationLanguageBundle() {
		return applicationLanguageBundle;
	}

	public void setApplicationLanguageBundle() {
		applicationLanguageBundle = ResourceBundle.getBundle("labels",new Locale(AppConfig.getApplicationProperty("application_language")));
	}

	public Properties getLocalLanguageProperty() {
		return localLanguageProperty;
	}

	public void setLocalLanguageProperty() {
		
		ResourceBundle localLanguage = ResourceBundle.getBundle("labels", new Locale(AppConfig.getApplicationProperty("local_language")));
		localLanguageProperty = new Properties();

	    Enumeration<String> keys = localLanguage.getKeys();
	    while (keys.hasMoreElements()) {
	    	String key = keys.nextElement();
	    	localLanguageProperty.put(key, localLanguage.getString(key));
	    }
	}

	private ApplicationContext() {
		
	}
	
	public static ApplicationContext getInstance() {
		if(applicationContext == null) {
			applicationContext = new ApplicationContext();
			return applicationContext;
		} else {
			return applicationContext;
		}
	}
}
