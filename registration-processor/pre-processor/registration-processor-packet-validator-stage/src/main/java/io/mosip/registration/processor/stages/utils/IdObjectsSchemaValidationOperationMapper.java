package io.mosip.registration.processor.stages.utils;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorSupportedOperations;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
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
	
	public IdObjectValidatorSupportedOperations getOperation(String registrationId) throws ApisResourceAccessException, IOException {
		
		int age = utility.getApplicantAge(registrationId);
		int ageThreshold = Integer.parseInt(ageLimit);
		if (age < ageThreshold) {
			return IdObjectValidatorSupportedOperations.CHILD_REGISTRATION;
		}
		
		SyncRegistrationEntity regEntity=syncRegistrationService.findByRegistrationId(registrationId);
		
		if(regEntity.getRegistrationType().matches(SyncTypeDto.NEW.getValue())) {
			return IdObjectValidatorSupportedOperations.NEW_REGISTRATION;
		}
		else if(regEntity.getRegistrationType().matches(SyncTypeDto.LOST.getValue())) {
			return IdObjectValidatorSupportedOperations.LOST_UIN;
		}
		else if(regEntity.getRegistrationType().matches(SyncTypeDto.UPDATE.getValue())) {
			return IdObjectValidatorSupportedOperations.UPDATE_UIN;
		}
		else if(regEntity.getRegistrationType().matches(SyncTypeDto.RES_UPDATE.getValue())) {
			return IdObjectValidatorSupportedOperations.UPDATE_UIN;
		}
		else if(regEntity.getRegistrationType().matches(SyncTypeDto.ACTIVATED.getValue())) {
			return IdObjectValidatorSupportedOperations.UPDATE_UIN;
		}
		else if(regEntity.getRegistrationType().matches(SyncTypeDto.DEACTIVATED.getValue())) {
			return IdObjectValidatorSupportedOperations.UPDATE_UIN;
		}
		return null;
		
	}
}
