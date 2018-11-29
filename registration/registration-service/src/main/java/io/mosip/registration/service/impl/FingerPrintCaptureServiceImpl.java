package io.mosip.registration.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.validator.AuthenticationValidatorFactory;
import io.mosip.registration.validator.AuthenticationValidatorImplementation;

@Service
public class FingerPrintCaptureServiceImpl {

	@Autowired
	AuthenticationValidatorFactory authenticationValidatorFactory;
	
	public boolean validateFingerprint(List<FingerprintDetailsDTO> fingerprintDetailsDTOs) {
		AuthenticationValidatorDTO authenticationValidatorDTO=new AuthenticationValidatorDTO();
		authenticationValidatorDTO.setUserId(SessionContext.getInstance().getUserContext().getUserId());
		authenticationValidatorDTO.setFingerPrintDetails(fingerprintDetailsDTOs);
		AuthenticationValidatorImplementation authenticationValidatorImplementation=authenticationValidatorFactory.getValidator("Fingerprint");
		return authenticationValidatorImplementation.validate(authenticationValidatorDTO);		
		
	}
}
