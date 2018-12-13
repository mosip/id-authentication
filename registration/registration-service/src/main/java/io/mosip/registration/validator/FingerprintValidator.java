package io.mosip.registration.validator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.RegistrationUserDetailDAO;
import io.mosip.registration.device.fp.FingerprintFacade;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.entity.UserBiometric;

@Component
public class FingerprintValidator extends AuthenticationValidatorImplementation {

	@Autowired
	private RegistrationUserDetailDAO registrationUserDetailDAO;

	@Autowired
	FingerprintFacade fingerprintFacade;

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
		List<UserBiometric> userFingerprintDetails = registrationUserDetailDAO
				.getUserSpecificFingerprintDetails(userId);
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
				SessionContext.getInstance().getMapObject().put("DuplicateFinger", fingerprintDetailsDTO);
				break;
			}
		}
		return isMatchFound;

	}
}
