package io.mosip.kernel.uingenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;

import io.mosip.kernel.uingenerator.config.UinGeneratorConfiguration;
import io.mosip.kernel.uingenerator.constant.UinGeneratorConstant;
import io.mosip.kernel.uingenerator.util.ConfigServerUrlUtil;
import io.mosip.kernel.uingenerator.verticle.UinGeneratorServerVerticle;
import io.mosip.kernel.uingenerator.verticle.UinGeneratorVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;

/**
 * Uin Generator vertx application
 * 
 * @author Dharmesh Khandelwal
 * @author Urvil Joshi
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@SpringBootApplication
@PropertySource({ "classpath:application.properties", "classpath:bootstrap.properties" })
public class UinGeneratorVertxApplication {

	/**
	 * The field for Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UinGeneratorVertxApplication.class);

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		List<ConfigStoreOptions> configStores = new ArrayList<>();
		ConfigServerUrlUtil.getURLs()
				.forEach(url -> configStores
						.add(new ConfigStoreOptions().setType(UinGeneratorConstant.CONFIG_STORE_OPTIONS_TYPE)
								.setConfig(new JsonObject().put(UinGeneratorConstant.URL, url).put(
										UinGeneratorConstant.TIME_OUT,
										Long.parseLong(UinGeneratorConstant.CONFIG_SERVER_FETCH_TIME_OUT)))));
		ConfigRetrieverOptions configRetrieverOptions = new ConfigRetrieverOptions();
		configStores.forEach(configRetrieverOptions::addStore);
		ConfigRetriever retriever = ConfigRetriever.create(vertx, configRetrieverOptions.setScanPeriod(0));
		retriever.getConfig(json -> {
			if (json.succeeded()) {
				LOGGER.info("Retrieving from Config Server");
				JsonObject jsonObject = json.result();
				if (jsonObject != null) {
					jsonObject.iterator().forEachRemaining(
							sourceValue -> System.setProperty(sourceValue.getKey(), sourceValue.getValue().toString()));
				}

				json.mapEmpty();
				retriever.close();
				vertx.close();
				startApplication();

			} else {
				LOGGER.info(json.cause().getMessage());
				json.otherwiseEmpty();
				retriever.close();
				vertx.close();
				startApplication();
			}
		});
	}

	private static void startApplication() {
		ApplicationContext context = new AnnotationConfigApplicationContext(UinGeneratorConfiguration.class);
		VertxOptions options = new VertxOptions();
		Vertx vertx = Vertx.vertx(options);
		Verticle[] verticles = { new UinGeneratorVerticle(context), new UinGeneratorServerVerticle(context) };
		Stream.of(verticles).forEach(verticle -> vertx.deployVerticle(verticle, stringAsyncResult -> {
			if (stringAsyncResult.succeeded()) {
				LOGGER.info("Succesfully deployed :  {}", verticle.getClass().getSimpleName());
			} else {
				LOGGER.info("Failed to deploy {} \\nCause: {}", verticle.getClass().getSimpleName(),
						stringAsyncResult.cause());
			}
		}));
	}
}