package io.mosip.kernel.uingenerator.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
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
public class ConfigServerUrlUtil {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private ConfigServerUrlUtil() {

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
			String url = getProperty(ConfigServerUrlUtil.class, propertyFileName,
					UinGeneratorConstant.SPRING_CLOUD_CONFIG_URI);
			url = url + UinGeneratorConstant.FORWARD_SLASH + appName + UinGeneratorConstant.FORWARD_SLASH
					+ getActiveProfile() + UinGeneratorConstant.FORWARD_SLASH + getCloudConfigLabel();
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
	private static String getProperty(Class<?> clazz, String fileName, String key) {

		Map<String, String> env = System.getenv();
		for (String envName : env.keySet()) {
			System.out.format("%s=%s%n", envName, env.get(envName));
		}

		System.out.println("----------------------------------------------");

		Properties p = System.getProperties();
		Enumeration keys = p.keys();
		while (keys.hasMoreElements()) {
			String keyS = (String) keys.nextElement();
			String value = (String) p.get(keyS);
			System.out.println(keyS + ": " + value);
		}

		String value = System.getProperty(key);
		if (value != null)
			return value;

		Properties prop = new Properties();
		try (InputStream input = clazz.getClassLoader().getResourceAsStream(fileName)) {
			if (input == null) {
				throw new UinGeneratorServiceException("", "File Not Available : " + fileName);
			}
			prop.load(input);
			value = prop.getProperty(key);
		} catch (IOException ex) {
			throw new UinGeneratorServiceException("", "Failed to read properties from : " + fileName);
		}
		return value;
	}

	/**
	 * Method to get active profile value.
	 * 
	 * @return the profile value.
	 */
	private static String getActiveProfile() {
		String profile = System.getProperty(UinGeneratorConstant.SPRING_PROFILES_ACTIVE);
		if (profile == null) {
			profile = getProperty(ConfigServerUrlUtil.class, propertyFileName,
					UinGeneratorConstant.SPRING_PROFILES_ACTIVE);
		}
		return profile;
	}

	/**
	 * Method to get the cloud config label.
	 * 
	 * @return the label value.
	 */
	private static String getCloudConfigLabel() {
		String label = System.getProperty(UinGeneratorConstant.SPRING_CLOUD_CONFIG_LABEL);
		if (label == null) {
			label = getProperty(ConfigServerUrlUtil.class, propertyFileName,
					UinGeneratorConstant.SPRING_CLOUD_CONFIG_LABEL);
		}
		return label;
	}

	/**
	 * Method to get the cloud config names.
	 * 
	 * @return the app names.
	 */
	public static List<String> getAppNames() {
		String names = System.getProperty(UinGeneratorConstant.SPRING_CLOUD_CONFIG_NAME);
		if (names == null) {
			names = getProperty(ConfigServerUrlUtil.class, propertyFileName,
					UinGeneratorConstant.SPRING_CLOUD_CONFIG_NAME);
		}
		return Stream.of(names.split(UinGeneratorConstant.COMMA)).collect(Collectors.toList());
	}
}
