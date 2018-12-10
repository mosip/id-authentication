package io.mosip.kernel.keymanagerservice.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

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
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

	/**
	 * Master service Version
	 */
	private static final String KEYMANAGER_SERVICE_VERSION = "1.0";
	/**
	 * Application Title
	 */
	private static final String TITLE = "Key Manager Service";
	/**
	 * Master Data Service
	 */
	private static final String DISCRIPTION = "Key Manager Service for Security";

	@Value("${application.env.local:false}")
	private Boolean localEnv;

	@Value("${swagger.base-url:#{null}}")
	private String swaggerUrl;

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
		return new ApiInfoBuilder().title(TITLE).description(DISCRIPTION).version(KEYMANAGER_SERVICE_VERSION).build();
	}

	/**
	 * Produce Docket bean
	 * 
	 * @return Docket bean
	 */
	@Bean
	public Docket api() {
		boolean targetSwagger = false;
		if (!localEnv && swaggerUrl != null && !swaggerUrl.isEmpty()) {
			try {
				proto = new URL(swaggerUrl).getProtocol();
				host = new URL(swaggerUrl).getHost();
				port = new URL(swaggerUrl).getPort();
				if (port == -1) {
					hostWithPort = host;
				} else {
					hostWithPort = host + ":" + port;
				}
				targetSwagger = true;
			} catch (MalformedURLException e) {
				System.err.println("SwaggerUrlException: " + e);
			}
		}

		Docket docket = new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
				.tags(new Tag("keymanager", "operation related to keymanagement and interaction with softhsm")).groupName(TITLE)
				.select().apis(RequestHandlerSelectors.any()).paths(PathSelectors.regex("(?!/(error|actuator).*).*"))
				.build();

		if (targetSwagger) {
			docket.protocols(protocols()).host(hostWithPort);
		}
		System.out.println("\nSwagger Base URL: " + proto + "://" + hostWithPort + "\n");

		return docket;
	}

	private Set<String> protocols() {
		Set<String> protocols = new HashSet<>();
		protocols.add(proto);
		return protocols;
	}
}
