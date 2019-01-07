package io.mosip.registration.processor.bio.dedupe.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * The Class BioDedupeConfig.
 */
@Configuration
@EnableSwagger2
public class BioDedupeConfig {

	/**
	 * Registration status bean.
	 *
	 * @return the docket
	 */
	@Bean
	public Docket biodedupeBean() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("Biodedupe").select()
				.apis(RequestHandlerSelectors.basePackage("io.mosip.registration.processor.bio.dedupe.controller"))
				.paths(PathSelectors.ant("/v0.1/registration-processor/bio-dedupe/*")).build();
	}

}
