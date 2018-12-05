package io.mosip.registration.validator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.machinezoo.sourceafis.FingerprintTemplate;

import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.entity.RegistrationUserDetail;
import io.mosip.registration.service.LoginService;
import io.mosip.registration.util.biometric.MosipFingerprintProvider;

@Component("fingerprintValidator")
public class FingerprintValidator extends AuthenticationValidatorImplementation {

	@Autowired
	private LoginService userDataService;

	@Autowired
	private MosipFingerprintProvider fingerPrintConnector;

	@Value("${FINGER_PRINT_SCORE}")
	private long fingerPrintScore;

	/* (non-Javadoc)
	 * @see io.mosip.registration.validator.AuthenticationValidatorImplementation#validate(io.mosip.registration.dto.AuthenticationValidatorDTO)
	 */
	@Override
	public boolean validate(AuthenticationValidatorDTO authenticationValidatorDTO) {
		if (fingerPrintType.equals("single")) {
			return validateOneToManyFP(authenticationValidatorDTO.getUserId(),
					authenticationValidatorDTO.getFingerPrintDetails().get(0));
		} else if (fingerPrintType.equals("multiple")) {
			return validateManyToManyFP(authenticationValidatorDTO.getFingerPrintDetails());
		}
		return false;
	}

	/**
	 * Validate one finger print values with all the fingerprints from the table.
	 * @param userId
	 * @param fingerprintDetailsDTO
	 * @return
	 */
	private boolean validateOneToManyFP(String userId, FingerprintDetailsDTO fingerprintDetailsDTO) {
		registrationUserDetail = userDataService.getUserDetail(userId);
		return validateFP(fingerprintDetailsDTO, registrationUserDetail);
	}

	/**
	 * Validate all the user input finger details with all the finger details form the DB.
	 * @param fingerprintDetailsDTOs
	 * @return
	 */
	private boolean validateManyToManyFP(List<FingerprintDetailsDTO> fingerprintDetailsDTOs) {
		List<RegistrationUserDetail> registrationUserDetails = userDataService.getAllActiveUsers();
		return fingerprintDetailsDTOs.stream().anyMatch(fingerprintDetailsDTO -> registrationUserDetails.stream()
				.anyMatch(userDetails -> validateFP(fingerprintDetailsDTO, userDetails)));

	}

	/**
	 * Comparing two fingerprint image and send the matching status
	 * @param fingerprintDetailsDTO
	 * @param registrationUserDetail
	 * @return
	 */
	private boolean validateFP(FingerprintDetailsDTO fingerprintDetailsDTO,
			RegistrationUserDetail registrationUserDetail) {
		FingerprintTemplate fingerprintTemplate = new FingerprintTemplate().convert(fingerprintDetailsDTO.getFingerPrint());
		String minutiae = fingerprintTemplate.serialize();
		return registrationUserDetail.getUserBiometric().stream().anyMatch(
				bio -> fingerPrintConnector.scoreCalculator(minutiae, bio.getBioMinutia()) > fingerPrintScore);
	}
}
