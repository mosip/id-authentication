package io.mosip.registration.service.device.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.validator.AuthenticationService;
import io.mosip.registration.validator.AuthenticationValidatorImplementation;

@Service
public class FingerPrintCaptureServiceImpl {

	@Autowired
	private AuthenticationService authenticationValidatorFactory;

	public boolean validateFingerprint(List<FingerprintDetailsDTO> fingerprintDetailsDTOs) {
		AuthenticationValidatorDTO authenticationValidatorDTO=new AuthenticationValidatorDTO();
		authenticationValidatorDTO.setUserId(SessionContext.getInstance().getUserContext().getUserId());
		authenticationValidatorDTO.setFingerPrintDetails(fingerprintDetailsDTOs);
		AuthenticationValidatorImplementation authenticationValidatorImplementation=authenticationValidatorFactory.getValidator("Fingerprint");
		authenticationValidatorImplementation.setFingerPrintType("multiple");
		return authenticationValidatorImplementation.validate(authenticationValidatorDTO);		
		
	}
}
