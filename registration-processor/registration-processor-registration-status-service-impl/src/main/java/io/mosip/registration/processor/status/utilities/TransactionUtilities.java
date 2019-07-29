package io.mosip.registration.processor.status.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class TransactionUtilities {
	@Autowired
	Environment environment;
	
	public String getMessageInPreferedLanguage(String message
			,String langCode) throws IOException {
		
		ClassLoader classLoader = getClass().getClassLoader();
		String messagesPropertiesFileName = environment.getProperty("registration.processor.status.messages."+langCode);
		File messagesPropertiesFile = new File(classLoader.getResource(messagesPropertiesFileName).getFile());
		InputStream inputStream = new FileInputStream(messagesPropertiesFile);
		Properties prop = new Properties();
		prop.load(inputStream);
		
		return prop.getProperty(message);
		
	}
}
