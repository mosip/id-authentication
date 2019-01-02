package io.mosip.registration.processor.status.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
	
/**
 * The Class RegistrationStatusConfig.
 */
@Configuration
@EnableSwagger2
public class RegistrationStatusConfig {

	/**
	 * Registration status bean.
	 *
	 * @return the docket
	 */
	@Bean
	public Docket registrationStatusBean() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("Registration status").select()
				.apis(RequestHandlerSelectors.basePackage("io.mosip.registration.processor.status.controller"))
				.paths(PathSelectors.ant("/v0.1/registration-processor/registration-status/*")).build();
	}

}
