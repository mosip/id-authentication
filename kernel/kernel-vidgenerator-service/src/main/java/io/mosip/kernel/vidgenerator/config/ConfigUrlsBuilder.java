package io.mosip.kernel.vidgenerator.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.mosip.kernel.vidgenerator.constant.VIDGeneratorConstant;
import io.mosip.kernel.vidgenerator.exception.VidGeneratorServiceException;

/**
 * Utility class to fetch config server related URLs.
 * 
 * @author Sagar Mahapatra
 * @author Raj Jha 
 * @since 1.0.0
 *
 */
public class ConfigUrlsBuilder {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private ConfigUrlsBuilder() {

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
		getConfigNames().forEach(config -> {
			String url = getProperty(VIDGeneratorConstant.SPRING_CLOUD_CONFIG_URI);
			url = url + VIDGeneratorConstant.FORWARD_SLASH + config + VIDGeneratorConstant.FORWARD_SLASH
					+ getProperty(VIDGeneratorConstant.SPRING_PROFILES_ACTIVE) + VIDGeneratorConstant.FORWARD_SLASH
					+ getProperty(VIDGeneratorConstant.SPRING_CLOUD_CONFIG_LABEL);
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
		try (InputStream input = ConfigUrlsBuilder.class.getClassLoader().getResourceAsStream(propertyFileName)) {
			if (input == null) {
				throw new VidGeneratorServiceException("", "File Not Available : " + propertyFileName);
			}
			prop.load(input);
			value = prop.getProperty(key);
		} catch (IOException ex) {
			throw new VidGeneratorServiceException("", "Failed to read properties from : " + propertyFileName);
		}
		return value;
	}

	/**
	 * Method to get the cloud config names.
	 * 
	 * @return the app names.
	 */
	public static List<String> getConfigNames() {
		String names = System.getProperty(VIDGeneratorConstant.SPRING_CLOUD_CONFIG_NAME);
		if (names == null) {
			names = getProperty(VIDGeneratorConstant.SPRING_CLOUD_CONFIG_NAME);
		}
		return Stream.of(names.split(VIDGeneratorConstant.COMMA)).collect(Collectors.toList());
	}
}
