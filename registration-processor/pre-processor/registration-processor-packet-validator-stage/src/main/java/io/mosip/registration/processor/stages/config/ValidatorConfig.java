package io.mosip.registration.processor.stages.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.kernel.core.idobjectvalidator.spi.IdObjectValidator;
import io.mosip.kernel.idobjectvalidator.impl.IdObjectSchemaValidator;
import io.mosip.registration.processor.core.packet.dto.applicantcategory.ApplicantTypeDocument;
import io.mosip.registration.processor.stages.packet.validator.PacketValidateProcessor;
import io.mosip.registration.processor.stages.packet.validator.PacketValidatorStage;
import io.mosip.registration.processor.stages.utils.DocumentUtility;
import io.mosip.registration.processor.stages.utils.IdObjectsSchemaValidationOperationMapper;

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
	public IdObjectsSchemaValidationOperationMapper getIdObjectsSchemaValidationOperationMapper() {
		return new IdObjectsSchemaValidationOperationMapper();
	}

	@Bean
	public IdObjectValidator getIdObjectValidator() {
		return new IdObjectSchemaValidator();
	}

	@Bean
	public ApplicantTypeDocument getApplicantTypeDocument() {
		return new ApplicantTypeDocument();
	}
}
