package io.mosip.registration.processor.camel.bridge.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import io.mosip.registration.processor.core.exception.ConfigurationServerFailureException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * This class interacts with config server and gets the required values
 *
 * @author Pranav Kumar
 * @since 0.0.1
 *
 */
public class BridgeUtil {

	private static JsonObject bridgeConfiguration = null;
	private static String propertyFileName="bootstrap.properties";

	private BridgeUtil() {

	}

	/**
	 * This method gets all values from config server as a JsonObject and stores it
	 * locally
	 */
	public static void getConfiguration() {
		String profile = System.getProperty("spring.profiles.active");
		String label = System.getProperty("spring.cloud.config.label");
		if(profile==null) {
			profile=PropertyFileUtil.getProperty(BridgeUtil.class, propertyFileName, "spring.profiles.active");
		}
		if(label==null) {
			label=PropertyFileUtil.getProperty(BridgeUtil.class, propertyFileName, "spring.cloud.config.label");
		}
		String url = PropertyFileUtil.getProperty(BridgeUtil.class, propertyFileName, "url");
		url=url+"/"+profile+"/"+label;
		String configServerTimer = PropertyFileUtil.getProperty(BridgeUtil.class, propertyFileName, "config.server.timer");
		Long configServerTimerInMs=Long.parseLong(configServerTimer);
		CompletableFuture<JsonObject> configuration = new CompletableFuture<>();

		ConfigStoreOptions configStoreOptions = new ConfigStoreOptions().setType("spring-config-server")
				.setConfig(new JsonObject().put("url", url).put("timeout", 70000));

		ConfigRetrieverOptions configRetrieverOptions = new ConfigRetrieverOptions();
		configRetrieverOptions.setScanPeriod(configServerTimerInMs);
		configRetrieverOptions.addStore(configStoreOptions);

		ConfigRetriever configRetriever = ConfigRetriever.create(Vertx.vertx(), configRetrieverOptions);

		configRetriever.getConfig(config -> {
			if (config.succeeded()) {
				configuration.complete(config.result());
			} else {
				throw new ConfigurationServerFailureException(
						PlatformErrorMessages.RPR_CMB_CONFIGURATION_SERVER_FAILURE_EXCEPTION.getMessage());
			}
		});

		try {
			BridgeUtil.bridgeConfiguration = configuration.get();
		} catch (InterruptedException | ExecutionException e) {
			Thread.currentThread().interrupt();
			throw new ConfigurationServerFailureException(
					PlatformErrorMessages.RPR_CMB_CONFIGURATION_SERVER_FAILURE_EXCEPTION.getMessage(), e);
		}

	}

	/**
	 * This method returns the camel endpoint along with component
	 *
	 * @param messageBusAddress
	 *            The address to be used for endpoint
	 * @return The address as per the configured camel component
	 */
	public static String getPropertyFromConfigServer(String key) {
		if (BridgeUtil.bridgeConfiguration == null) {
			getConfiguration();
		}
		return BridgeUtil.bridgeConfiguration.getString(key);
	}
}
