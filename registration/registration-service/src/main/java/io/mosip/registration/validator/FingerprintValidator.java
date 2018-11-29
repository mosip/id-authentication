package io.mosip.registration.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.machinezoo.sourceafis.FingerprintTemplate;

import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.entity.RegistrationUserDetail;
import io.mosip.registration.service.LoginService;
import io.mosip.registration.util.biometric.MosipFingerprintProvider;

@Component
public class FingerprintValidator extends AuthenticationValidatorImplementation {

	@Autowired
	private LoginService userDataService;

	@Autowired
	private MosipFingerprintProvider fingerPrintConnector;

	@Value("${FINGER_PRINT_SCORE}")
	private long fingerPrintScore;

	public boolean validate(AuthenticationValidatorDTO authenticationValidatorDTO) {
		for (FingerprintDetailsDTO fingerprintDetailsDTO : authenticationValidatorDTO.getFingerPrintDetails()) {
			if (oneToManyValidation(authenticationValidatorDTO.getUserId(), fingerprintDetailsDTO)) {
				return true;
			}
		}
		return false;
	}

	private boolean oneToManyValidation(String userId, FingerprintDetailsDTO fingerprintDetailsDTO) {

		FingerprintTemplate fingerprintTemplate = new FingerprintTemplate()
				.create(fingerprintDetailsDTO.getFingerPrint());
		String minutiae = fingerprintTemplate.serialize();
		RegistrationUserDetail registrationUserDetail = userDataService.getUserDetail(userId);
		return registrationUserDetail.getUserBiometric().stream().anyMatch(
				bio -> fingerPrintConnector.scoreCalculator(minutiae, bio.getBioMinutia()) > fingerPrintScore);
	}
}
