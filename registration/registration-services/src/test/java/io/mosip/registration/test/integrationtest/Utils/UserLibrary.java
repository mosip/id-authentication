package io.mosip.registration.test.integrationtest.Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class UserLibrary {
	public static Properties loadPropertiesFile() {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream("src/test/java/io/mosip/registration/test/integrationtest/Utils/Queries.properties");
			// load a properties file
			prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return prop;

	}
}
