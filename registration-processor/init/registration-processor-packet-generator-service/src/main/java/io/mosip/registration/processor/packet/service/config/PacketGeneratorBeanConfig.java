package io.mosip.registration.processor.packet.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.kernel.core.idobjectvalidator.spi.IdObjectValidator;
import io.mosip.kernel.idobjectvalidator.impl.IdObjectSchemaValidator;

@Configuration
public class PacketGeneratorBeanConfig {
	
	@Bean
	public IdObjectValidator getidObjectSchemaValidator() {
		return new IdObjectSchemaValidator();
	}

}
