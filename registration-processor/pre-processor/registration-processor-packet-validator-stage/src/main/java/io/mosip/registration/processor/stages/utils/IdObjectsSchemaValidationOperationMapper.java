package io.mosip.registration.processor.stages.utils;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorSupportedOperations;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.dto.SyncResponseDto;
import io.mosip.registration.processor.status.dto.SyncTypeDto;
import io.mosip.registration.processor.status.entity.SyncRegistrationEntity;
import io.mosip.registration.processor.status.service.SyncRegistrationService;

@Component
public class IdObjectsSchemaValidationOperationMapper {
	
	@Autowired
	private SyncRegistrationService<SyncResponseDto, SyncRegistrationDto> syncRegistrationService;
	
	@Autowired
	private Utilities utility;
	
	@Value("${mosip.kernel.applicant.type.age.limit}")
	private String ageLimit;
	
		/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(IdObjectsSchemaValidationOperationMapper.class);
	
	public IdObjectValidatorSupportedOperations getOperation(String registrationId) throws ApisResourceAccessException, IOException, PacketDecryptionFailureException, io.mosip.kernel.core.exception.IOException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
				"IdObjectsSchemaValidationOperationMapper::getOperation()::entry");
		SyncRegistrationEntity regEntity=syncRegistrationService.findByRegistrationId(registrationId);
		
		if(regEntity.getRegistrationType().matches(SyncTypeDto.NEW.getValue())) {
			int age = utility.getApplicantAge(registrationId);
			int ageThreshold = Integer.parseInt(ageLimit);
			if (age < ageThreshold) {
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
						"IdObjectsSchemaValidationOperationMapper::getOperation()::exit-NEW child");
				return IdObjectValidatorSupportedOperations.CHILD_REGISTRATION;
			}
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
					"IdObjectsSchemaValidationOperationMapper::getOperation()::exit-NEW");
			return IdObjectValidatorSupportedOperations.NEW_REGISTRATION;
		}
		else if(regEntity.getRegistrationType().matches(SyncTypeDto.LOST.getValue())) {
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
					"IdObjectsSchemaValidationOperationMapper::getOperation()::exit-LOST");
			return IdObjectValidatorSupportedOperations.LOST_UIN;
		}
		else if(regEntity.getRegistrationType().matches(SyncTypeDto.UPDATE.getValue())) {
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
					"IdObjectsSchemaValidationOperationMapper::getOperation()::exit-UPDATE");
			return IdObjectValidatorSupportedOperations.UPDATE_UIN;
		}
		else if(regEntity.getRegistrationType().matches(SyncTypeDto.RES_UPDATE.getValue())) {
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
					"IdObjectsSchemaValidationOperationMapper::getOperation()::exit-RES_UPDATE");
			return IdObjectValidatorSupportedOperations.UPDATE_UIN;
		}
		else if(regEntity.getRegistrationType().matches(SyncTypeDto.ACTIVATED.getValue())) {
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
					"IdObjectsSchemaValidationOperationMapper::getOperation()::exit-ACTIVATED");
			return IdObjectValidatorSupportedOperations.UPDATE_UIN;
		}
		else if(regEntity.getRegistrationType().matches(SyncTypeDto.DEACTIVATED.getValue())) {
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
					"IdObjectsSchemaValidationOperationMapper::getOperation()::exit-DEACTIVATED");
			return IdObjectValidatorSupportedOperations.UPDATE_UIN;
		}
		return null;
		
	}
}
