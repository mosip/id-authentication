package io.mosip.kernel.pridgenerator;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.kernel.pridgenerator.config.ConfigUrlsBuilder;
import io.mosip.kernel.pridgenerator.config.HibernateDaoConfig;
import io.mosip.kernel.pridgenerator.constant.EventType;
import io.mosip.kernel.pridgenerator.constant.PRIDGeneratorConstant;
import io.mosip.kernel.pridgenerator.verticle.PridPoolCheckerVerticle;
import io.mosip.kernel.pridgenerator.verticle.PridPopulatorVerticle;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.logging.SLF4JLogDelegateFactory;

@SpringBootApplication
public class KernelPridgeneratorServiceApplication {

	private static Vertx vertx;

	/**
	 * The field for Logger
	 * 
	 */
	private static Logger LOGGER;

	/**
	 * Server context path.
	 */
	@Value("${server.servlet.path}")
	private String contextPath;

	/**
	 * This method create or update swagger json for swagger ui after service start.
	 */
	@PostConstruct
	private void swaggerJSONFileUpdate() {
		try {
			TemplateManager templateManager;
			File swaggerJsonnFile = new File(PRIDGeneratorConstant.SWAGGER_UI_JSON_PATH);
			templateManager = new TemplateManagerBuilderImpl().build();
			Map<String, Object> map = new HashMap<>();
			map.put("servletpath", contextPath);
			InputStream is = this.getClass().getClassLoader()
					.getResourceAsStream(PRIDGeneratorConstant.SWAGGER_JSON_TEMPLATE);
			InputStream out = templateManager.merge(is, map);
			String merged = IOUtils.toString(out, StandardCharsets.UTF_8.name());
			FileUtils.writeStringToFile(swaggerJsonnFile, merged, StandardCharsets.UTF_8.name());
		} catch (Exception e) {
			LOGGER.warn(e.getMessage());
		}
	}

	@PostConstruct
	private static void initPool() {
		LOGGER.info("Service will be started after pooling vids..");
		EventBus eventBus = vertx.eventBus();
		LOGGER.info("eventBus deployer {}", eventBus);
		eventBus.publish(EventType.INITPOOL, EventType.INITPOOL);
	}

	/**
	 * main method for the application
	 * 
	 * @param args the argument
	 */
	public static void main(String[] args) {
		System.setProperty("vertx.logger-delegate-factory-class-name", SLF4JLogDelegateFactory.class.getName());
		LOGGER = LoggerFactory.getLogger(KernelPridgeneratorServiceApplication.class);
		loadPropertiesFromConfigServer();
	}

	/**
	 * This method retrieves and loads the configuration fetched from
	 * spring-config-server. If retrievation succeeds, then the local properties
	 * present are over-ridden. If retrievation fails, the local properties are used
	 * for running the application.
	 */
	private static void loadPropertiesFromConfigServer() {
		Vertx vertx = Vertx.vertx();
		try {
			List<ConfigStoreOptions> configStores = new ArrayList<>();
			List<String> configUrls = ConfigUrlsBuilder.getURLs();
			configUrls.forEach(url -> configStores
					.add(new ConfigStoreOptions().setType(PRIDGeneratorConstant.CONFIG_STORE_OPTIONS_TYPE)
							.setConfig(new JsonObject().put(PRIDGeneratorConstant.URL, url).put(
									PRIDGeneratorConstant.TIME_OUT,
									Long.parseLong(PRIDGeneratorConstant.CONFIG_SERVER_FETCH_TIME_OUT)))));
			ConfigRetrieverOptions configRetrieverOptions = new ConfigRetrieverOptions();
			configStores.forEach(configRetrieverOptions::addStore);
			ConfigRetriever retriever = ConfigRetriever.create(vertx, configRetrieverOptions.setScanPeriod(0));
			LOGGER.info("Retrieving configuration from Spring-Config-Server");
			retriever.getConfig(json -> {
				if (json.succeeded()) {
					JsonObject jsonObject = json.result();
					if (jsonObject != null) {
						jsonObject.iterator().forEachRemaining(sourceValue -> System.setProperty(sourceValue.getKey(),
								sourceValue.getValue().toString()));
					}
					json.mapEmpty();
					retriever.close();
					vertx.close();
					startApplication();
				} else {
					LOGGER.warn(json.cause().getMessage() + "\n");
					json.otherwiseEmpty();
					retriever.close();
					vertx.close();
					startApplication();
				}
			});
		} catch (Exception exception) {
			LOGGER.warn(exception.getMessage() + "\n");
			vertx.close();
			startApplication();
		}
	}

	/**
	 * This method sets the Application Context, deploys the verticles.
	 * 
	 * @throws InterruptedException
	 */
	private static void startApplication() {
		ApplicationContext context = new AnnotationConfigApplicationContext(HibernateDaoConfig.class);
		VertxOptions options = new VertxOptions();
		DeploymentOptions workerOptions = new DeploymentOptions().setWorker(true);
		vertx = Vertx.vertx(options);
		Verticle[] workerVerticles = { new PridPoolCheckerVerticle(context), new PridPopulatorVerticle(context) };
		Stream.of(workerVerticles).forEach(verticle -> deploy(verticle, workerOptions, vertx));
		vertx.setTimer(1000, handler -> initPool());
	}

	private static void deploy(Verticle verticle, DeploymentOptions opts, Vertx vertx) {
		vertx.deployVerticle(verticle, opts, res -> {
			if (res.failed()) {
				LOGGER.info("Failed to deploy verticle " + verticle.getClass().getSimpleName() + " " + res.cause());
			} else if (res.succeeded()) {
				LOGGER.info("Deployed verticle " + verticle.getClass().getSimpleName());

			}
		});
	}

}
