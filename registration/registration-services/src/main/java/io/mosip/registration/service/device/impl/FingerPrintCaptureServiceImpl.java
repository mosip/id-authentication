package io.mosip.registration.service.device.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.service.AuthenticationService;

/**
 * The FingerPrintCaptureServiceImpl class.
 * 
 * @author Saravana Kumar
 */
@Service
public class FingerPrintCaptureServiceImpl {

	@Autowired
	private AuthenticationService authenticationService;

	/**
	 * Validate the scanned fingers.
	 *
	 * @param fingerprintDetailsDTOs 
	 * 				the fingerprint details DT os
	 * @return Boolean isMatched
	 */
	public boolean validateFingerprint(List<FingerprintDetailsDTO> fingerprintDetailsDTOs) {
		AuthenticationValidatorDTO authenticationValidatorDTO=new AuthenticationValidatorDTO();
		authenticationValidatorDTO.setUserId(SessionContext.userContext().getUserId());
		authenticationValidatorDTO.setFingerPrintDetails(fingerprintDetailsDTOs);
		authenticationValidatorDTO.setAuthValidationType("multiple");
		return authenticationService.authValidator("Fingerprint", authenticationValidatorDTO);
	}
}
