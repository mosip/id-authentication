package io.mosip.registration.processor.abis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * The Class RegistrationProcessorStatusConfig.
 */
@Configuration
@EnableSwagger2
public class RegistrationProcessorStatusConfig {

	/**
	 * Registration status bean.
	 *
	 * @return the docket
	 */
	@Bean
	public Docket registrationStatusBean() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("Abis").select()
				.apis(RequestHandlerSelectors.basePackage("io.mosip.registration.processor.abis.controller"))
				.paths(PathSelectors.ant("/v0.1/registration-processor/abis/*")).build();
	}

}
