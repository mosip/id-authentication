package io.mosip.kernel.uingenerator.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.mosip.kernel.uingenerator.constant.UinGeneratorConstant;
import io.mosip.kernel.uingenerator.exception.UinGeneratorServiceException;

/**
 * Utility class to fetch config server related URLs.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public class ConfigReader {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private ConfigReader() {

	}

	/**
	 * Static field value for the property file name.
	 */
	private static String propertyFileName = "bootstrap.properties";

	/**
	 * Method to get URL's.
	 * 
	 * @return the URLs.
	 */
	public static List<String> getURLs() {
		List<String> urlS = new ArrayList<>();
		getAppNames().forEach(appName -> {
			String url = getProperty(UinGeneratorConstant.SPRING_CLOUD_CONFIG_URI);
			url = url + UinGeneratorConstant.FORWARD_SLASH + appName + UinGeneratorConstant.FORWARD_SLASH
					+ getProperty(UinGeneratorConstant.SPRING_PROFILES_ACTIVE) + UinGeneratorConstant.FORWARD_SLASH
					+ getProperty(UinGeneratorConstant.SPRING_CLOUD_CONFIG_LABEL);
			urlS.add(url);
		});
		return urlS;
	}

	/**
	 * Method to get property values.
	 * 
	 * @param clazz    the class.
	 * @param fileName file name from which property values are to be fetched.
	 * @param key      the key for which value needs to be fetched.
	 * @return the property value.
	 */
	private static String getProperty(String key) {
		String value = System.getProperty(key);
		if (value != null)
			return value;
		else {
			value = System.getenv(key);
			if (value != null)
				return value;
		}
		Properties prop = new Properties();
		try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream(propertyFileName)) {
			if (input == null) {
				throw new UinGeneratorServiceException("", "File Not Available : " + propertyFileName);
			}
			prop.load(input);
			value = prop.getProperty(key);
		} catch (IOException ex) {
			throw new UinGeneratorServiceException("", "Failed to read properties from : " + propertyFileName);
		}
		return value;
	}

	/**
	 * Method to get the cloud config names.
	 * 
	 * @return the app names.
	 */
	public static List<String> getAppNames() {
		String names = System.getProperty(UinGeneratorConstant.SPRING_CLOUD_CONFIG_NAME);
		if (names == null) {
			names = getProperty(UinGeneratorConstant.SPRING_CLOUD_CONFIG_NAME);
		}
		return Stream.of(names.split(UinGeneratorConstant.COMMA)).collect(Collectors.toList());
	}
}
