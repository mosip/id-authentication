package io.mosip.kernel.vidgenerator;

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
import org.springframework.core.env.Environment;

import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;
import io.mosip.kernel.vidgenerator.config.ConfigUrlsBuilder;
import io.mosip.kernel.vidgenerator.config.HibernateDaoConfig;
import io.mosip.kernel.vidgenerator.constant.EventType;
import io.mosip.kernel.vidgenerator.constant.VIDGeneratorConstant;
import io.mosip.kernel.vidgenerator.verticle.VidExpiryVerticle;
import io.mosip.kernel.vidgenerator.verticle.VidFetcherVerticle;
import io.mosip.kernel.vidgenerator.verticle.VidPoolCheckerVerticle;
import io.mosip.kernel.vidgenerator.verticle.VidPopulatorVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.logging.SLF4JLogDelegateFactory;

/**
 * VID Generator Vertx Application
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 *
 */
@SpringBootApplication
public class VinGeneratorVertxApplication {

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
			File swaggerJsonnFile = new File(VIDGeneratorConstant.SWAGGER_UI_JSON_PATH);
			templateManager = new TemplateManagerBuilderImpl().build();
			Map<String, Object> map = new HashMap<>();
			map.put("servletpath", contextPath);
			InputStream is = this.getClass().getClassLoader()
					.getResourceAsStream(VIDGeneratorConstant.SWAGGER_JSON_TEMPLATE);
			InputStream out = templateManager.merge(is, map);
			String merged = IOUtils.toString(out, StandardCharsets.UTF_8.name());
			FileUtils.writeStringToFile(swaggerJsonnFile, merged, StandardCharsets.UTF_8.name());
		} catch (Exception e) {
			LOGGER.warn(e.getMessage());
		}
	}

	/**
	 * main method for the application
	 * 
	 * @param args the argument
	 */
	public static void main(String[] args) {
		System.setProperty("vertx.logger-delegate-factory-class-name", SLF4JLogDelegateFactory.class.getName());
		LOGGER = LoggerFactory.getLogger(VinGeneratorVertxApplication.class);
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
					.add(new ConfigStoreOptions().setType(VIDGeneratorConstant.CONFIG_STORE_OPTIONS_TYPE)
							.setConfig(new JsonObject().put(VIDGeneratorConstant.URL, url).put(
									VIDGeneratorConstant.TIME_OUT,
									Long.parseLong(VIDGeneratorConstant.CONFIG_SERVER_FETCH_TIME_OUT)))));
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
	 */
	private static void startApplication() {
		ApplicationContext context = new AnnotationConfigApplicationContext(HibernateDaoConfig.class);
		VertxOptions options = new VertxOptions();
		DeploymentOptions workerOptions = new DeploymentOptions().setWorker(true);
		DeploymentOptions eventLoopOptions = new DeploymentOptions();
		Vertx vertx = Vertx.vertx(options);
		Verticle[] eventLoopVerticles = { new VidFetcherVerticle(context)};
		Verticle[] workerVerticles = {new VidPoolCheckerVerticle(context),new VidPopulatorVerticle(context),new VidExpiryVerticle(context)};
		Stream.of(workerVerticles).forEach(verticle -> deploy(verticle, workerOptions, vertx));
		Environment environment = context.getBean(Environment.class);
		DeliveryOptions deliveryOptions = new DeliveryOptions();
		deliveryOptions.setSendTimeout(environment.getProperty("mosip.kernel.vid.pool-population-timeout",Long.class));
		long start =System.currentTimeMillis();
		LOGGER.info("Service will be started after pooling vids..");
		vertx.eventBus().send(EventType.INITPOOL, EventType.INITPOOL, deliveryOptions,replyHandler -> {
			if(replyHandler.succeeded()) {
				LOGGER.info("population of pool is done starting fetcher verticle");
				Stream.of(eventLoopVerticles).forEach(verticle -> deploy(verticle, eventLoopOptions, vertx));		
			    LOGGER.info("Starting vidgenerator service... ");
			    LOGGER.info("service took {} ms to pool and start",(System.currentTimeMillis()-start));
			}else if(replyHandler.failed()) {
				LOGGER.error("population of pool failed with cause ",replyHandler.cause());
			}
		});
		
	}

	private static void deploy(Verticle verticle, DeploymentOptions opts, Vertx vertx) {
		vertx.deployVerticle(verticle, opts, res -> {
			if (res.failed()) {
				LOGGER.info("Failed to deploy verticle " + verticle.getClass().getSimpleName()+" "+res.cause());
			} else if(res.succeeded()) {
				LOGGER.info("Deployed verticle " + verticle.getClass().getSimpleName());
			}
		});
	}
}