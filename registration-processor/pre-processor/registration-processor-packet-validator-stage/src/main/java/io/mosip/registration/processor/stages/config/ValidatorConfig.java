package io.mosip.registration.processor.stages.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.kernel.core.jsonvalidator.spi.JsonValidator;
import io.mosip.kernel.jsonvalidator.impl.JsonSchemaLoader;
import io.mosip.kernel.jsonvalidator.impl.JsonValidatorImpl;
import io.mosip.registration.processor.stages.packet.validator.PacketValidateProcessor;
import io.mosip.registration.processor.stages.packet.validator.PacketValidatorStage;
import io.mosip.registration.processor.stages.utils.DocumentUtility;

@Configuration
public class ValidatorConfig {

	@Bean
	public PacketValidatorStage getPacketValidatorStage() {
		return new PacketValidatorStage();
	}

	@Bean
	public DocumentUtility getDocumentUtility() {
		return new DocumentUtility();
	}

	@Bean
	public PacketValidateProcessor getPacketValidateProcessor() {
		return new PacketValidateProcessor();
	}

	@Bean
	public JsonValidator getJsonValidator() {
		return new JsonValidatorImpl();
	}

	@Bean
	public JsonSchemaLoader getJsonSchemaLoader() {
		return new JsonSchemaLoader();
	}
}
