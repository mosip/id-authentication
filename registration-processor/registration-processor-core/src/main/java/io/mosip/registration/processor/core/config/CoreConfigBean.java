package io.mosip.registration.processor.core.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.mosip.kernel.core.exception.ExceptionUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.abstractverticle.MosipRouter;
import io.mosip.registration.processor.core.logger.LogDescription;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.queue.factory.MosipQueueConnectionFactoryImpl;
import io.mosip.registration.processor.core.queue.impl.MosipActiveMqImpl;
import io.mosip.registration.processor.core.spi.queue.MosipQueueConnectionFactory;
import io.mosip.registration.processor.core.spi.queue.MosipQueueManager;
import io.mosip.registration.processor.core.token.validation.TokenValidator;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.mosip.registration.processor.core.util.DigitalSignatureUtility;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;

import org.springframework.context.annotation.Primary;

@PropertySource("classpath:bootstrap.properties")
@Configuration
public class CoreConfigBean {

	private static Logger regProcLogger = RegProcessorLogger.getLogger(CoreConfigBean.class);

	private enum HttpConstants {
		HTTP("http://"), HTTPS("https://");
		private String url;

		HttpConstants(String url) {
			this.url = url;
		}

		String getUrl() {
			return url;
		}

	}

	@Bean
	public PropertySourcesPlaceholderConfigurer getPropertiesFromConfigServer(Environment environment) {
		try {
			Vertx vertx = Vertx.vertx();
			List<ConfigStoreOptions> configStores = new ArrayList<>();
			List<String> configUrls = CoreConfigBean.getUrls(environment);
			configUrls.forEach(url -> {
				if (url.startsWith(HttpConstants.HTTP.getUrl()))
						configStores.add(new ConfigStoreOptions().setType(ConfigurationUtil.CONFIG_SERVER_TYPE).setConfig(new JsonObject().put("url", url)
								.put("timeout", Long.parseLong(ConfigurationUtil.CONFIG_SERVER_TIME_OUT))));
				else
						configStores.add(new ConfigStoreOptions().setType(ConfigurationUtil.CONFIG_SERVER_TYPE).setConfig(new JsonObject().put("url", url)
								.put("timeout", Long.parseLong(ConfigurationUtil.CONFIG_SERVER_TIME_OUT))
								.put("httpClientConfiguration", new JsonObject().put("trustAll", true).put("ssl", true))));
			});
			ConfigRetrieverOptions configRetrieverOptions = new ConfigRetrieverOptions();
			configStores.forEach(configRetrieverOptions::addStore);
			ConfigRetriever retriever = ConfigRetriever.create(vertx, configRetrieverOptions.setScanPeriod(0));
			regProcLogger.info(this.getClass().getName(), "", "", "Getting values from config Server");
			CompletableFuture<JsonObject> configLoader = new CompletableFuture<JsonObject>();
			retriever.getConfig(json -> {
				if (json.succeeded()) {
					JsonObject jsonObject = json.result();
					if (jsonObject != null) {
						jsonObject.iterator().forEachRemaining(sourceValue -> System.setProperty(sourceValue.getKey(),
								sourceValue.getValue().toString()));
					}
					configLoader.complete(json.result());
					json.mapEmpty();
					retriever.close();
					vertx.close();
				} else {
					regProcLogger.info(this.getClass().getName(), "", json.cause().getLocalizedMessage(),
							json.cause().getMessage());
					json.otherwiseEmpty();
					retriever.close();
					vertx.close();
				}
			});
			configLoader.get();
		} catch (Exception exception) {
			regProcLogger.error(this.getClass().getName(), "", "", ExceptionUtils.getStackTrace(exception));
		}
		return new PropertySourcesPlaceholderConfigurer();
	}

	private static List<String> getAppNames(Environment env) {
		String names = env.getProperty(ConfigurationUtil.APPLICATION_NAMES);
		return Stream.of(names.split(",")).collect(Collectors.toList());
	}

	private static List<String> getProfiles(Environment env) {
		String names = env.getProperty(ConfigurationUtil.ACTIVE_PROFILES);
		return Stream.of(names.split(",")).collect(Collectors.toList());
	}

	private static List<String> getUrls(Environment environment) {
		List<String> configUrls = new ArrayList<>();
		List<String> appNames = getAppNames(environment);
		String uri = environment.getProperty(ConfigurationUtil.CLOUD_CONFIG_URI);
		String label = environment.getProperty(ConfigurationUtil.CLOUD_CONFIG_LABEL);
		List<String> profiles = getProfiles(environment);
		profiles.forEach(profile -> {
			appNames.forEach(app -> {
				String url = uri + "/" + app + "/" + profile + "/" + label;
				configUrls.add(url);
			});
		});
		appNames.forEach(appName -> {
		});
		return configUrls;
	}

	@Bean
	@Primary
	public RegistrationProcessorIdentity getRegProcessorIdentityJson() {
		return new RegistrationProcessorIdentity();
	}

	@Bean
	MosipQueueManager<?, ?> getMosipQueueManager() {
		return new MosipActiveMqImpl();
	}

	@Bean
	MosipQueueConnectionFactory<?> getMosipQueueConnectionFactory() {
		return new MosipQueueConnectionFactoryImpl();
	}

	@Bean
	public TokenValidator getTokenValidator() {
		return new TokenValidator();
	}

	@Bean
	public MosipRouter getMosipRouter() {
		return new MosipRouter();
	}

	@Bean
	public DigitalSignatureUtility getDigitalSignatureUtility() {
		return new DigitalSignatureUtility();
	}

	@Bean
	public LogDescription getLogDescription() {
		return new LogDescription();
	}

	@Bean
	public RegistrationExceptionMapperUtil getRegistrationExceptionMapperUtil() {
		return new RegistrationExceptionMapperUtil();
	}
}
