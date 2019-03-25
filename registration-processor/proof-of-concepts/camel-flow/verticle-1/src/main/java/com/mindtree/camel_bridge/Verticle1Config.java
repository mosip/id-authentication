package com.mindtree.camel_bridge;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Verticle1Config {
	@Bean
	public Docket verticle1Docket() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("Verticle1").select()
				.apis(RequestHandlerSelectors.basePackage("com.mindtree.camel_bridge")).paths(PathSelectors.ant("/initiate"))
				.build();
	}
}
