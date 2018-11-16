package io.mosip.registration.context;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import org.springframework.util.ResourceUtils;

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

	public void setLocalLanguageProperty() throws IOException {
		localLanguageProperty = new Properties();
		localLanguageProperty.load(new FileInputStream(ResourceUtils.getFile("classpath:labels_"+AppConfig.getApplicationProperty("local_language")+".properties")));
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
