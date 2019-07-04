package io.mosip.kernel.applicanttype.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Configuration class for swagger config
 * 
 * @author Bal Vikash Sharma
 *
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerConfig.class);

	/**
	 * Master service Version
	 */
	private static final String APPLICANT_TYPE_SERVICE_VERSION = "1.0";
	/**
	 * Application Title
	 */
	private static final String TITLE = "Applicant Type Service";
	/**
	 * Master Data Service
	 */
	private static final String DISCRIBTION = "Service to get Applicant type";

	@Value("${application.env.local:false}")
	private Boolean localEnv;

	@Value("${swagger.base-url:#{null}}")
	private String swaggerBaseUrl;

	@Value("${server.port:8080}")
	private int serverPort;

	String proto = "http";
	String host = "localhost";
	int port = -1;
	String hostWithPort = "localhost:8080";

	/**
	 * Produces {@link ApiInfo}
	 * 
	 * @return {@link ApiInfo}
	 */
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title(TITLE).description(DISCRIBTION).version(APPLICANT_TYPE_SERVICE_VERSION)
				.build();
	}

	/**
	 * Produce Docket bean
	 * 
	 * @return Docket bean
	 */
	@Bean
	public Docket api() {
		boolean swaggerBaseUrlSet = false;
		if (!localEnv && swaggerBaseUrl != null && !swaggerBaseUrl.isEmpty()) {
			try {
				proto = new URL(swaggerBaseUrl).getProtocol();
				host = new URL(swaggerBaseUrl).getHost();
				port = new URL(swaggerBaseUrl).getPort();
				if (port == -1) {
					hostWithPort = host;
				} else {
					hostWithPort = host + ":" + port;
				}
				swaggerBaseUrlSet = true;
			} catch (MalformedURLException e) {
				LOGGER.error("SwaggerUrlException: {}", e);
			}
		}

		Docket docket = new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
				.tags(new Tag("ApplicantType", "This service provide operations on applicant type")).groupName(TITLE)
				.select().apis(RequestHandlerSelectors.any()).paths(PathSelectors.regex("(?!/(error|actuator).*).*"))
				.build();

		if (swaggerBaseUrlSet) {
			docket.protocols(protocols()).host(hostWithPort);
			LOGGER.info("\nSwagger Base URL: " + proto + "://" + hostWithPort + "\n");
		}

		return docket;
	}

	private Set<String> protocols() {
		Set<String> protocols = new HashSet<>();
		protocols.add(proto);
		return protocols;
	}
}
