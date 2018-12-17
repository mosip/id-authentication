package io.mosip.pregistration.datasync.config;

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
 * Config for Data sync
 * 
 * @author M1046129 - Jagadishwari
 *
 */
@Configuration
@EnableSwagger2
public class DataSyncConfig {

	/**
	 * @return docket
	 */
	
	@Value("${application.env.local:false}")
	private Boolean localEnv;

	@Value("${swagger.base-url:#{null}}")
	private String swaggerBaseUrl;

	@Value("${server.port:9094}")
	private int serverPort;

	String proto = "http";
	String host = "localhost";
	int port = -1;
	String hostWithPort = "localhost:9094";
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

		Docket docket = new Docket(DocumentationType.SWAGGER_2).groupName("Pre-Registration-Datasync").select()
				.apis(RequestHandlerSelectors.basePackage("io.mosip.pregistration.datasync.controller"))
				.paths(PathSelectors.ant("/v0.1/pre-registration/data-sync/*")).build();

		if (swaggerBaseUrlSet) {
			docket.protocols(protocols()).host(hostWithPort);
			System.out.println("\nSwagger Base URL: " + proto + "://" + hostWithPort + "\n");
		}
		return docket;
	}
	
	private Set<String> protocols() {
		Set<String> protocols = new HashSet<>();
		protocols.add(proto);
		return protocols;
	}

}
