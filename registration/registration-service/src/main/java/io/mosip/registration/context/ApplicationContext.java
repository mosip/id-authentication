package io.mosip.registration.context;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import io.mosip.registration.config.AppConfig;

public class ApplicationContext{

	private static ApplicationContext applicationContext;

	private ResourceBundle applicationLanguageBundle;
	private ResourceBundle localLanguageBundle;

	public ResourceBundle getApplicationLanguageBundle() {
		return applicationLanguageBundle;
	}

	public void setApplicationLanguageBundle() {
		applicationLanguageBundle = ResourceBundle.getBundle("labels",new Locale(AppConfig.getApplicationProperty("application_language")));
	}

	public ResourceBundle getLocalLanguageProperty() {
		return localLanguageBundle;
	}

	public void setLocalLanguageProperty() {
		localLanguageBundle = ResourceBundle.getBundle("labels", new Locale(AppConfig.getApplicationProperty("local_language")));
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
