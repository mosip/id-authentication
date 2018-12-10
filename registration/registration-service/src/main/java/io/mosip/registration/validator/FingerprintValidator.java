package io.mosip.registration.validator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.machinezoo.sourceafis.FingerprintTemplate;

import io.mosip.registration.device.fp.MosipFingerprintProvider;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.entity.UserBiometric;
import io.mosip.registration.service.LoginService;

@Component
public class FingerprintValidator extends AuthenticationValidatorImplementation {

	@Autowired
	private LoginService userDataService;

	@Autowired
	private MosipFingerprintProvider fingerPrintConnector;

	@Value("${FINGER_PRINT_SCORE}")
	private long fingerPrintScore;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.validator.AuthenticationValidatorImplementation#
	 * validate(io.mosip.registration.dto.AuthenticationValidatorDTO)
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
	 * 
	 * @param userId
	 * @param fingerprintDetailsDTO
	 * @return
	 */
	private boolean validateOneToManyFP(String userId, FingerprintDetailsDTO fingerprintDetailsDTO) {
		List<UserBiometric> userFingerprintDetails = userDataService.getUserSpecificFingerprintDetails(userId);
		return validateFP(fingerprintDetailsDTO, userFingerprintDetails);
	}

	/**
	 * Validate all the user input finger details with all the finger details form
	 * the DB.
	 * 
	 * @param fingerprintDetailsDTOs
	 * @return
	 */
	private boolean validateManyToManyFP(List<FingerprintDetailsDTO> fingerprintDetailsDTOs) {
		return fingerprintDetailsDTOs.stream().anyMatch(fingerprintDetailsDTO -> validateFP(fingerprintDetailsDTO,
				userDataService.getAllActiveUsers(fingerprintDetailsDTO.getFingerType())));

	}

	/**
	 * Comparing two fingerprint image and send the matching status
	 * 
	 * @param fingerprintDetailsDTO
	 * @param registrationUserDetail
	 * @return
	 */
	private boolean validateFP(FingerprintDetailsDTO fingerprintDetailsDTO,
			List<UserBiometric> userFingerprintDetails) {
		FingerprintTemplate fingerprintTemplate = new FingerprintTemplate()
				.convert(fingerprintDetailsDTO.getFingerPrint());
		String minutiae = fingerprintTemplate.serialize();
		return userFingerprintDetails.stream().anyMatch(
				bio -> fingerPrintConnector.scoreCalculator(minutiae, bio.getBioMinutia()) > fingerPrintScore);
	}
}
