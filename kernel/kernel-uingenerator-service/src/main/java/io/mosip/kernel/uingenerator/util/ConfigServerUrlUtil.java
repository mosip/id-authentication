package io.mosip.kernel.uingenerator.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.mosip.kernel.uingenerator.constant.UinGeneratorConstant;

public class ConfigServerUrlUtil {

	private ConfigServerUrlUtil() {

	}

	private static String propertyFileName = "bootstrap.properties";

	public static List<String> getURLs() {
		List<String> urlS = new ArrayList<>();
		getAppNames().forEach(appName -> {
			String url = getProperty(ConfigServerUrlUtil.class, propertyFileName, "spring.cloud.config.uri");
			url = url + UinGeneratorConstant.FORWARD_SLASH + appName + UinGeneratorConstant.FORWARD_SLASH
					+ getActiveProfile() + UinGeneratorConstant.FORWARD_SLASH + getCloudConfigLabel();
			urlS.add(url);
		});
		return urlS;
	}

	private static String getProperty(Class<?> clazz, String fileName, String key) {
		Properties prop = new Properties();
		String value = null;
		try (InputStream input = clazz.getClassLoader().getResourceAsStream(fileName)) {
			if (input == null) {
				System.out.println("Input null");
			}
			prop.load(input);
			value = prop.getProperty(key);
		} catch (IOException ex) {
			// TODO: Handle the exception !
		}
		return value;
	}

	private static String getActiveProfile() {
		String profile = System.getProperty(UinGeneratorConstant.SPRING_PROFILES_ACTIVE);
		if (profile == null) {
			profile = getProperty(ConfigServerUrlUtil.class, propertyFileName,
					UinGeneratorConstant.SPRING_PROFILES_ACTIVE);
		}
		return profile;
	}

	private static String getCloudConfigLabel() {
		String label = System.getProperty(UinGeneratorConstant.SPRING_CLOUD_CONFIG_LABEL);
		if (label == null) {
			label = getProperty(ConfigServerUrlUtil.class, propertyFileName,
					UinGeneratorConstant.SPRING_CLOUD_CONFIG_LABEL);
		}
		return label;
	}

	public static List<String> getAppNames() {
		String names = System.getProperty(UinGeneratorConstant.SPRING_CLOUD_CONFIG_NAME);
		if (names == null) {
			names = getProperty(ConfigServerUrlUtil.class, propertyFileName,
					UinGeneratorConstant.SPRING_CLOUD_CONFIG_NAME);
		}
		return Stream.of(names.split(UinGeneratorConstant.COMMA)).collect(Collectors.toList());
	}
}
