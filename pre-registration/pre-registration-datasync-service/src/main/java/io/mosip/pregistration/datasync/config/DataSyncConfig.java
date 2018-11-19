package io.mosip.pregistration.datasync.config;

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

	@Bean
	public Docket registrationStatusBean() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("Pre-Registration-Datasync").select()
				.apis(RequestHandlerSelectors.basePackage("io.mosip.pregistration.datasync.controller"))
				.paths(PathSelectors.ant("/v0.1/pre-registration/data-sync/*")).build();

	}

}
