package io.mosip.registration.validator;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.bioapi.impl.BioApiImpl;
import io.mosip.kernel.core.bioapi.model.Score;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.BIRType;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.UserDetailDAO;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.entity.UserBiometric;
import io.mosip.registration.service.bio.BioService;

/**
 * This class is for validating Fingerprint Authentication
 * 
 * @author SaravanaKumar G
 *
 */
@Service("fingerprintValidator")
public class FingerprintValidatorImpl extends AuthenticationBaseValidator {

	@Autowired
	private UserDetailDAO userDetailDAO;

	@Autowired
	private BioApiImpl bioApiImpl;
	
	@Autowired
	private BioService bioService;
	
	/**
	 * Validate the Fingerprint with the AuthenticationValidatorDTO as input
	 */
	@Override
	public boolean validate(AuthenticationValidatorDTO authenticationValidatorDTO) {
		if (RegistrationConstants.FINGER_PRINT_SINGLE.equals(authenticationValidatorDTO.getAuthValidationType())) {
			return validateOneToOneFP(authenticationValidatorDTO.getUserId(),
					authenticationValidatorDTO.getFingerPrintDetails().get(0));
		} else if (RegistrationConstants.FINGER_PRINT_MULTIPLE
				.equals(authenticationValidatorDTO.getAuthValidationType())) {
			return validateManyToManyFP(authenticationValidatorDTO.getFingerPrintDetails());
		}
		return false;
	}

	/**
	 * Validate one finger print values with all the fingerprints from the
	 * table.
	 * 
	 * @param userId
	 * @param capturedFingerPrintDto
	 * @return
	 */
	private boolean validateOneToOneFP(String userId, FingerprintDetailsDTO capturedFingerPrintDto) {
		UserBiometric userFingerprintDetail = userDetailDAO.getUserSpecificBioDetail(userId, "FIN",
				capturedFingerPrintDto.getFingerType());
			return validateFpWithBioApi(capturedFingerPrintDto, Arrays.asList(userFingerprintDetail));
	}

	private boolean validateFpWithBioApi(FingerprintDetailsDTO capturedFingerPrintDto,
			List<UserBiometric> userFingerprintDetails) {
		boolean flag = false;
		BIRType[] registeredBir = null;
		BIRType capturedBir = new BIRType();
		capturedBir.setBDB(capturedFingerPrintDto.getFingerPrintISOImage());
		for (UserBiometric userBiometric : userFingerprintDetails) {
			int i = 0;
			registeredBir = new BIRType[userFingerprintDetails.size()];
			registeredBir[i].setBDB(userBiometric.getBioIsoImage());
			i++;
		}

		Score[] scores = bioApiImpl.match(capturedBir, registeredBir, null);
		int fingerPrintScore = Integer
				.parseInt(String.valueOf(ApplicationContext.map().get(RegistrationConstants.FINGER_PRINT_SCORE)));
		for (Score score : scores) {
			if (score.getScaleScore() > fingerPrintScore) {
				flag = true;
			}
		}

		return flag;
	}

	/**
	 * Validate all the user input finger details with all the finger details
	 * form the DB.
	 * 
	 * @param fingerprintDetailsDTOs
	 * @return
	 */
	private boolean validateManyToManyFP(List<FingerprintDetailsDTO> fingerprintDetailsDTOs) {
		Boolean isMatchFound = false;
		for (FingerprintDetailsDTO fingerprintDetailsDTO : fingerprintDetailsDTOs) {
			isMatchFound = bioService.validateFP(fingerprintDetailsDTO,
					userDetailDAO.getAllActiveUsers(fingerprintDetailsDTO.getFingerType()));
			if (isMatchFound) {
				SessionContext.map().put(RegistrationConstants.DUPLICATE_FINGER, fingerprintDetailsDTO);
				break;
			}
		}
		return isMatchFound;

	}
	
}
