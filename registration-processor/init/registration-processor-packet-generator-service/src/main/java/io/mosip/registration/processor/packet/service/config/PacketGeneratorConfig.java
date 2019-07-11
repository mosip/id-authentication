package io.mosip.registration.processor.packet.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * The Class PacketGeneratorConfig.
 * 
 * @author Sowmya
 */
@Configuration
@EnableSwagger2
public class PacketGeneratorConfig {
	/**
	 * Registration status bean.
	 *
	 * @return the docket
	 */
	@Bean
	public Docket packetGeneratorBean() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("PacketGenerator").select()
				.apis(RequestHandlerSelectors.basePackage("io.mosip.registration.processor.packet.service.controller"))
				.paths(PathSelectors.ant("/*")).build();
	}

}
