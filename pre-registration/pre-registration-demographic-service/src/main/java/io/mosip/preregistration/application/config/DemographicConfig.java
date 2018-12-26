/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * This class is used for Swagger configuration, also to configure Host and
 * Port.
 * 
 * @author Rajath KR
 * @since 1.0.0
 *
 */
@Configuration
@EnableSwagger2
public class DemographicConfig {

	/**
	 * Reference for ${application.env.local:false} from property file.
	 */
	@Value("${application.env.local:false}")
	private Boolean localEnv;

	/**
	 * Reference for ${swagger.base-url:#{null}} from property file.
	 */
	@Value("${swagger.base-url:#{null}}")
	private String swaggerBaseUrl;

	/**
	 * Reference for ${server.port:9092} from property file.
	 */
	@Value("${server.port:9092}")
	private int serverPort;

	/**
	 * To define Protocol
	 */
	String proto = "http";

	/**
	 * To define Host
	 */
	String host = "localhost";

	/**
	 * To define port
	 */
	int port = -1;

	/**
	 * To define host along with the port
	 */
	String hostWithPort = "localhost:9092";

	/**
	 * To configure Host and port along with docket.
	 * 
	 * @return
	 */
	@Bean
	public Docket registrationStatusBean() {

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
				System.err.println("SwaggerUrlException: " + e);
			}
		}

		Docket docket = new Docket(DocumentationType.SWAGGER_2).groupName("Pre-Registration").select()
				.apis(RequestHandlerSelectors.basePackage("io.mosip.preregistration.application.controller"))
				.paths(PathSelectors.ant("/v0.1/pre-registration/*")).build();

		if (swaggerBaseUrlSet) {
			docket.protocols(protocols()).host(hostWithPort);
			System.out.println("\nSwagger Base URL: " + proto + "://" + hostWithPort + "\n");
		}
		return docket;
	}

	/**
	 * @return set or protocols
	 */
	private Set<String> protocols() {
		Set<String> protocols = new HashSet<>();
		protocols.add(proto);
		return protocols;
	}
}
