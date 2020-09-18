package io.mosip.authentication.common.service.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * To expose the documentation for entire API.
 *
 * @author Dinesh Karuppiah
 */

@Configuration(value = "ida_swagger_config")
@EnableSwagger2
public class SwaggerConfig {

	@Value("${" + IdAuthConfigKeyConstants.APP_ENVIRONMENT_LOCAL+ ":false}")
	private Boolean localEnv;

	@Value("${" + IdAuthConfigKeyConstants.SWAGGER_BASE_URL + ":#{null}}")
	private String swaggerUrl;

	@Value("${" + IdAuthConfigKeyConstants.SERVER_PORT + ":8080}")
	private int serverPort;

	private String host;
	private String proto = "http";
	private int port = -1;

	private String hostWithPort;

	@PostConstruct
	public void init() {
		host = "localhost";
		hostWithPort = "localhost:" + serverPort;
	}

	/**
	 * Set the api info.
	 *
	 * @return the api info
	 */
	ApiInfo getApiInfo() {
		return new ApiInfoBuilder().title("Id Authentication Service").description("Id Authentication Service").build();
	}

	/**
	 * Docket bean provides more control over the API for Documentation Generation.
	 *
	 * @return the docket
	 */

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
		ParameterBuilder aParameterBuilder = new ParameterBuilder();
		aParameterBuilder.name("Authorization").modelRef(new ModelRef("Authorization")).parameterType("header").build();
		aParameterBuilder.name("signature").modelRef(new ModelRef("signature")).parameterType("header").build();
		List<Parameter> aParameters = new ArrayList<>();
		aParameters.add(aParameterBuilder.build());
		Docket docket = new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.regex("(?!/(error|actuator).*).*")).build().globalOperationParameters(aParameters);

		if (targetSwagger) {
			docket.protocols(protocols()).host(hostWithPort);
		}

		return docket;
	}

	/**
	 * Protocols
	 * 
	 * @return
	 */
	private Set<String> protocols() {
		Set<String> protocols = new HashSet<>();
		protocols.add(proto);
		return protocols;
	}

}
