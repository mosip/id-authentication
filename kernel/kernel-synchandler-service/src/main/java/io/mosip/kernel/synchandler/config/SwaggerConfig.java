package io.mosip.kernel.synchandler.config;

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
	private static final String SYNCHANDLER_SERVICE_VERSION = "0.0.1";
	/**
	 * Application Title
	 */
	private static final String TITLE = "Sync handler Service";
	/**
	 * Master Data Service
	 */
	private static final String DESCRIPTION = "Sync handler Service";

	/**
	 * Produces {@link ApiInfo}
	 * 
	 * @return {@link ApiInfo}
	 */
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title(TITLE).description(DESCRIPTION).version(SYNCHANDLER_SERVICE_VERSION).build();
	}

	/**
	 * Produce Docket bean
	 * 
	 * @return Docket bean
	 */
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
				.tags(new Tag("languages", "Operation performed on Language")).select()
				.apis(RequestHandlerSelectors.any()).paths(PathSelectors.any()).build();
	}
}
