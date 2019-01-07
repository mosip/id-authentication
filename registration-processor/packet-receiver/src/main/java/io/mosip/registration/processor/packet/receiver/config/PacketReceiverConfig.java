package io.mosip.registration.processor.packet.receiver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * The Class PacketReceiverConfig.
 */
@Configuration
@EnableSwagger2
public class PacketReceiverConfig {

	/**
	 * Packet uploader apis.
	 *
	 * @return the docket
	 */
	@Bean
	public Docket packetUploaderApis() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("Packet Receiver").select()
				.apis(RequestHandlerSelectors
						.basePackage("io.mosip.registration.processor.packet.receiver.controller"))
				.paths(PathSelectors.ant("/v0.1/registration-processor/packet-receiver/*")).build();
	}

}
