package io.mosip.testrig.apirig.auth.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import io.mosip.testrig.apirig.auth.testrunner.MosipTestRunner;
import io.mosip.testrig.apirig.testrunner.BaseTestCase;
import io.mosip.testrig.apirig.utils.ConfigManager;

public class IdAuthConfigManager extends ConfigManager{
	private static final Logger LOGGER = Logger.getLogger(IdAuthConfigManager.class);

	public static final String LOCAL_RUN_KEY = "localRun";

	private static final String IDA_PROPERTIES = "/config/Ida.properties";
	private static final String IDA_LOCAL_PROPERTIES = "/config/Ida-local.properties";

	public static void init() {
		Logger configManagerLogger = Logger.getLogger(ConfigManager.class);
		configManagerLogger.setLevel(Level.WARN);

		Map<String, Object> moduleSpecificPropertiesMap = new HashMap<>();
		// Load scope specific properties
		loadInto(moduleSpecificPropertiesMap, IDA_PROPERTIES);

		if (isLocalRunRequested(moduleSpecificPropertiesMap)) {
			loadInto(moduleSpecificPropertiesMap, IDA_LOCAL_PROPERTIES);
			moduleSpecificPropertiesMap.put(LOCAL_RUN_KEY, "yes");
			LOGGER.info("LOCAL RUN MODE ENABLED - overlaid " + IDA_LOCAL_PROPERTIES);
			makeCertDirNameFilesystemSafe();
		}

		// Add module specific properties as well.
		init(moduleSpecificPropertiesMap);
	}

	/**
	 * A local endpoint carries a port, and AdminTestUtil.getKeysDirPath() names the
	 * certificate directory after the endpoint host. On Windows a colon makes that
	 * directory uncreatable, so the partner keystore cannot be written and no auth
	 * request can be signed.
	 *
	 * Rewriting BaseTestCase.domain is safe: it feeds only the certs directory name
	 * and BiometricDataProvider, which sanitizes it the same way already. ApplnURI
	 * keeps its port, so requests are unaffected.
	 *
	 * Called before suiteSetup(), i.e. before anything reads the path.
	 */
	private static void makeCertDirNameFilesystemSafe() {
		String current = BaseTestCase.domain;
		if (current == null) {
			return;
		}
		String safe = current.replaceFirst("^https?://", "").replace(":", "_").replaceAll("/+$", "");
		if (!safe.equals(current)) {
			LOGGER.info("Local run - certs directory key '" + current + "' -> '" + safe + "'");
			BaseTestCase.domain = safe;
		}
	}

	private static void loadInto(Map<String, Object> target, String relativePath) {
		try {
			Properties props = getproperties(MosipTestRunner.getGlobalResourcePath() + relativePath);
			for (String key : props.stringPropertyNames()) {
				target.put(key, props.getProperty(key));
			}
		} catch (Exception e) {
			LOGGER.error("Failed to load " + relativePath + " : " + e.getMessage());
		}
	}

	/**
	 * Resolved before the properties map is published, so it reads the raw map
	 * rather than going through getproperty().
	 */
	private static boolean isLocalRunRequested(Map<String, Object> props) {
		String value = System.getProperty(LOCAL_RUN_KEY);
		if (value == null || value.isBlank()) {
			value = System.getenv(LOCAL_RUN_KEY);
		}
		if (value == null || value.isBlank()) {
			Object fromFile = props.get(LOCAL_RUN_KEY);
			value = fromFile == null ? null : fromFile.toString();
		}
		return isEnabled(value);
	}

	public static boolean isLocalRun() {
		String value = System.getProperty(LOCAL_RUN_KEY);
		if (value == null || value.isBlank()) {
			value = getproperty(LOCAL_RUN_KEY);
		}
		return isEnabled(value);
	}

	private static boolean isEnabled(String value) {
		return value != null && (value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true"));
	}

	public static boolean isLocalFlagEnabled(String key) {
		return isLocalRun() && isEnabled(getproperty(key));
	}
}
