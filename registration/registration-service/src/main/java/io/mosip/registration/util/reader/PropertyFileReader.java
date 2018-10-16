package io.mosip.registration.util.reader;

import java.io.IOException;
import java.util.Properties;

import io.mosip.registration.constants.RegConstants;
import io.mosip.registration.constants.RegProcessorExceptionCode;
import io.mosip.registration.exception.RegBaseUncheckedException;

/**
 * The class to read the values from the properties file.
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public class PropertyFileReader {

	/**
	 * Instance of {@link Properties} class
	 */
	private static Properties properties;

	private PropertyFileReader() {

	}

	// Loads the property file
	static {
		properties = new Properties();
		try {
			properties.load(ClassLoader.getSystemResourceAsStream(RegConstants.CONSTANTS_FILE_NAME));
		} catch (IOException ioException) {
			throw new RegBaseUncheckedException(RegProcessorExceptionCode.READ_PROPERTY_FILE_ERROR,
					ioException.toString());
		}
	}

	/**
	 * Gets the value of the property
	 * 
	 * @param propertyKey
	 *            the key for which value has to be returned
	 * @return String value of the property
	 */
	public static String getPropertyValue(String propertyKey) {
		return properties.getProperty(propertyKey);
	}

}
