package io.mosip.registration.processor.quality.check.config;
	
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * The Class QualityCheckeConfig.
 */
@Configuration
@EnableSwagger2
public class QualityCheckeConfig {

	/**
	 * Registration status bean.
	 *
	 * @return the docket
	 */
	@Bean
	public Docket registrationStatusBean() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("Quality Checker").select()
				.apis(RequestHandlerSelectors.basePackage("io.mosip.registration.processor.quality.check"))
				.paths(PathSelectors.ant("/v0.1/registration-processor/quality-checker/*")).build();
	}

}