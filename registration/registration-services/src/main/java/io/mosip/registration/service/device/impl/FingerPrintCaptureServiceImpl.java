package io.mosip.registration.service.device.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.service.AuthenticationService;

@Service
public class FingerPrintCaptureServiceImpl {

	@Autowired
	private AuthenticationService authenticationService;

	public boolean validateFingerprint(List<FingerprintDetailsDTO> fingerprintDetailsDTOs) {
		AuthenticationValidatorDTO authenticationValidatorDTO=new AuthenticationValidatorDTO();
		authenticationValidatorDTO.setUserId(SessionContext.getSessionContext().getUserContext().getUserId());
		authenticationValidatorDTO.setFingerPrintDetails(fingerprintDetailsDTOs);
		authenticationValidatorDTO.setAuthValidationType("multiple");
		return authenticationService.authValidator("Fingerprint", authenticationValidatorDTO);
	}
}
