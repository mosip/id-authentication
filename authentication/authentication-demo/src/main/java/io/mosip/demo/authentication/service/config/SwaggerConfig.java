package io.mosip.demo.authentication.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 
 * To expose the documentation for entire API
 * 
 * @author Dinesh Karuppiah
 */

@Configuration(value = "ida_swagger_config")
@EnableSwagger2
public class SwaggerConfig {

	/**
	 * 
	 * Docket bean provides more control over the API for Documentation Generation
	 * 
	 */

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.regex("(?!/(error|actuator).*).*")).build();
	}

}
