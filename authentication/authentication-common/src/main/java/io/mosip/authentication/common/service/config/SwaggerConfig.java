package io.mosip.authentication.common.service.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.dataaccess.hibernate.repository.impl.HibernateRepositoryImpl;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.slf4j.LoggerFactory;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
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
public class SwaggerConfig {

	private static final Logger logger = IdaLogger.getLogger(SwaggerConfig.class);
//	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CredentialRequestGeneratorConfig.class);

	@Value("${" + IdAuthConfigKeyConstants.APP_ENVIRONMENT_LOCAL+ ":false}")
	private Boolean localEnv;

	@Value("${" + IdAuthConfigKeyConstants.SWAGGER_BASE_URL + ":#{null}}")
	private String swaggerUrl;

	@Value("${" + IdAuthConfigKeyConstants.SERVER_PORT + ":8080}")
	private int serverPort;

//	private String host;
//	private String proto = "http";
//	private int port = -1;
//
//	private String hostWithPort;
//
//	@PostConstruct
//	public void init() {
//		host = "localhost";
//		hostWithPort = "localhost:" + serverPort;
//	}
//
//	/**
//	 * Set the api info.
//	 *
//	 * @return the api info
//	 */
//	ApiInfo getApiInfo() {
//		return new ApiInfoBuilder().title("Id Authentication Service").description("Id Authentication Service").build();
//	}
//
//	/**
//	 * Docket bean provides more control over the API for Documentation Generation.
//	 *
//	 * @return the docket
//	 */
//
//	/**
//	 * Produce Docket bean
//	 *
//	 * @return Docket bean
//	 */
//	@Bean
//	public Docket api() {
//		boolean targetSwagger = false;
//		if (!localEnv && swaggerUrl != null && !swaggerUrl.isEmpty()) {
//			try {
//				proto = new URL(swaggerUrl).getProtocol();
//				host = new URL(swaggerUrl).getHost();
//				port = new URL(swaggerUrl).getPort();
//				if (port == -1) {
//					hostWithPort = host;
//				} else {
//					hostWithPort = host + ":" + port;
//				}
//				targetSwagger = true;
//			} catch (MalformedURLException e) {
//				logger.error(IdAuthCommonConstants.SESSION_ID, "","", ExceptionUtils.getStackTrace(e));
//			}
//		}
//		ParameterBuilder aParameterBuilder = new ParameterBuilder();
//		aParameterBuilder.name("Authorization").modelRef(new ModelRef("Authorization")).parameterType("header").build();
//		aParameterBuilder.name("signature").modelRef(new ModelRef("signature")).parameterType("header").build();
//		List<Parameter> aParameters = new ArrayList<>();
//		aParameters.add(aParameterBuilder.build());
//		Docket docket = new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any())
//				.paths(PathSelectors.regex("(?!/(error|actuator).*).*")).build().globalOperationParameters(aParameters);
//
//		if (targetSwagger) {
//			docket.protocols(protocols()).host(hostWithPort);
//		}
//
//		return docket;
//	}

	/**
	 * Protocols
	 * 
	 * @return
	 */
//	private Set<String> protocols() {
//		Set<String> protocols = new HashSet<>();
//		protocols.add(proto);
//		return protocols;
//	}



	@Autowired
	private OpenApiProperties openApiProperties;

	@Bean
	public OpenAPI openApi() {
		OpenAPI api = new OpenAPI()
				.components(new Components())
				.info(new Info()
						.title(openApiProperties.getInfo().getTitle())
						.version(openApiProperties.getInfo().getVersion())
						.description(openApiProperties.getInfo().getDescription())
						.license(new License()
								.name(openApiProperties.getInfo().getLicense().getName())
								.url(openApiProperties.getInfo().getLicense().getUrl())));

		openApiProperties.getService().getServers().forEach(server -> {
			api.addServersItem(new Server().description(server.getDescription()).url(server.getUrl()));
		});

		ParameterBuilder aParameterBuilder = new ParameterBuilder();
		aParameterBuilder.name("Authorization").modelRef(new ModelRef("Authorization")).parameterType("header").build();
		aParameterBuilder.name("signature").modelRef(new ModelRef("signature")).parameterType("header").build();
		List<Parameter> aParameters = new ArrayList<>();
		aParameters.add(aParameterBuilder.build());
		Docket docket = new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.regex("(?!/(error|actuator).*).*")).build().globalOperationParameters(aParameters);
		logger.info("swagger open api bean is ready");
		return api;
	}

	@Bean
	public GroupedOpenApi groupedOpenApi() {
		return GroupedOpenApi.builder().group(openApiProperties.getGroup().getName())
				.pathsToMatch(openApiProperties.getGroup().getPaths().stream().toArray(String[]::new))
				.build();
	}

}

