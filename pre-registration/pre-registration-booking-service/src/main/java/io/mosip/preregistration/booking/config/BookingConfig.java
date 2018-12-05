package io.mosip.preregistration.booking.config;

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
 * @author M1037717
 *
 */
@Configuration
@EnableSwagger2
public class BookingConfig {

	/**
	 * @return docket
	 */
	@Bean
	public Docket registrationStatusBean() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("Pre-Registration-Booking").select()
				.apis(RequestHandlerSelectors.basePackage("io.mosip.preregistration.booking.controller"))
				.paths(PathSelectors.ant("/v0.1/pre-registration/book/*")).build();

	}

}
