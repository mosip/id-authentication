package io.mosip.registration.processor.core.bridge.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyFileUtil {

	public static String getProperty(Class<?> clazz, String fileName, String key) {

		Properties prop = new Properties();
		String value = null;

		try (InputStream input = clazz.getClassLoader().getResourceAsStream(fileName)) {

			if (input == null) {
				throw new FileNotFoundException("File Not available " + fileName);
			}
			prop.load(input);
			value = prop.getProperty("component");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return value;
	}
}
