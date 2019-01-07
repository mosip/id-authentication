package io.mosip.registration.validator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.RegistrationUserDetailDAO;
import io.mosip.registration.device.fp.FingerprintFacade;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.entity.UserBiometric;

/**
 * @author SaravanaKumar G
 *
 */
@Service("fingerprintValidator")
public class FingerprintValidatorImpl extends AuthenticationBaseValidator {

	@Autowired
	private RegistrationUserDetailDAO registrationUserDetailDAO;

	@Autowired
	FingerprintFacade fingerprintFacade;

	/**
	 * Validate the Fingerprint with the AuthenticationValidatorDTO as input
	 */
	@Override
	public boolean validate(AuthenticationValidatorDTO authenticationValidatorDTO) {
		if (RegistrationConstants.FINGER_PRINT_SINGLE.equals(authenticationValidatorDTO.getAuthValidationType())) {
			return validateOneToManyFP(authenticationValidatorDTO.getUserId(),
					authenticationValidatorDTO.getFingerPrintDetails().get(0));
		} else if (RegistrationConstants.FINGER_PRINT_MULTIPLE.equals(authenticationValidatorDTO.getAuthValidationType())) {
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
		List<UserBiometric> userFingerprintDetails = registrationUserDetailDAO
				.getUserSpecificBioDetails(userId, RegistrationConstants.VALIDATION_TYPE_FP);
		return fingerprintFacade.validateFP(fingerprintDetailsDTO, userFingerprintDetails);
	}

	/**
	 * Validate all the user input finger details with all the finger details form
	 * the DB.
	 * 
	 * @param fingerprintDetailsDTOs
	 * @return
	 */
	private boolean validateManyToManyFP(List<FingerprintDetailsDTO> fingerprintDetailsDTOs) {
		Boolean isMatchFound=false;
		for(FingerprintDetailsDTO fingerprintDetailsDTO:fingerprintDetailsDTOs) {
			isMatchFound=fingerprintFacade.validateFP(fingerprintDetailsDTO,
					registrationUserDetailDAO.getAllActiveUsers(fingerprintDetailsDTO.getFingerType()));
			if (isMatchFound) {
				SessionContext.getInstance().getMapObject().put(RegistrationConstants.DUPLICATE_FINGER, fingerprintDetailsDTO);
				break;
			}
		}
		return isMatchFound;

	}
}
