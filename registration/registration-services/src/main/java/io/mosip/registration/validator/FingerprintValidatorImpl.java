package io.mosip.registration.validator;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_FINGERPRINT_FACADE;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.machinezoo.sourceafis.FingerprintTemplate;

import io.mosip.kernel.core.bioapi.exception.BiometricException;
import io.mosip.kernel.core.bioapi.model.Score;
import io.mosip.kernel.core.bioapi.spi.IBioApi;
import io.mosip.kernel.core.cbeffutil.entity.BIR;
import io.mosip.kernel.core.cbeffutil.entity.BIR.BIRBuilder;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.UserDetailDAO;
import io.mosip.registration.dto.AuthTokenDTO;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.entity.UserBiometric;
import io.mosip.registration.service.security.impl.AuthenticationServiceImpl;

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
	private IBioApi ibioApi;

	/**
	 * Instance of LOGGER
	 */
	private static final Logger LOGGER = AppConfig.getLogger(AuthenticationServiceImpl.class);

	/**
	 * Validate the Fingerprint with the AuthenticationValidatorDTO as input
	 */
	@Override
	public boolean validate(AuthenticationValidatorDTO authenticationValidatorDTO) {
		LOGGER.info(LoggerConstants.FINGER_PRINT_AUTHENTICATION, APPLICATION_NAME, APPLICATION_ID,
				"Validating Scanned Finger");

		if (RegistrationConstants.FINGER_PRINT_SINGLE.equals(authenticationValidatorDTO.getAuthValidationType())) {
			return validateOneToManyFP(authenticationValidatorDTO.getUserId(),
					authenticationValidatorDTO.getFingerPrintDetails().get(0));
		} else if (RegistrationConstants.FINGER_PRINT_MULTIPLE
				.equals(authenticationValidatorDTO.getAuthValidationType())) {
			return validateManyToManyFP(authenticationValidatorDTO.getFingerPrintDetails());
		}
		return false;
	}

	/**
	 * Validate one finger print values with all the fingerprints from the table.
	 * 
	 * @param userId
	 * @param capturedFingerPrintDto
	 * @return
	 */
	private boolean validateOneToManyFP(String userId, FingerprintDetailsDTO capturedFingerPrintDto) {
		List<UserBiometric> userFingerprintDetails = userDetailDAO.getUserSpecificBioDetails(userId,
				RegistrationConstants.FIN);
		return validateFpWithBioApi(capturedFingerPrintDto, userFingerprintDetails);
	}

	private boolean validateFpWithBioApi(FingerprintDetailsDTO capturedFingerPrintDto,
			List<UserBiometric> userFingerprintDetails) {
		LOGGER.info(LoggerConstants.VALIDATE_FP_WITH_BIO_API, APPLICATION_NAME, APPLICATION_ID,
				"Validating finger print with bio api");
		boolean flag = false;
		Score[] scores = null;
		int i = 0;
		BIR[] registeredBir = new BIR[userFingerprintDetails.size()];
		String minutiae=null;
		try {
		FingerprintTemplate fingerprintTemplate = new FingerprintTemplate()
				.convert(capturedFingerPrintDto.getFingerPrint());
		minutiae = fingerprintTemplate.serialize();
		}catch (Exception e) {
			return false;
		}
		BIR capturedBir = new BIR(new BIRBuilder().withBdb(minutiae.getBytes()));

		for (UserBiometric userBiometric : userFingerprintDetails) {
			registeredBir[i] = new BIR(new BIRBuilder().withBdb(userBiometric.getBioMinutia().getBytes()));

			i++;
		}

		try {
			scores = ibioApi.match(capturedBir, registeredBir, null);
			int fingerPrintScore = Integer
					.parseInt(String.valueOf(ApplicationContext.map().get(RegistrationConstants.FINGER_PRINT_SCORE)));
			for (Score score : scores) {
				if (score.getInternalScore() >= fingerPrintScore) {
					flag = true;
				}
			}
		} catch (BiometricException exception) {
			LOGGER.error(LOG_REG_FINGERPRINT_FACADE, APPLICATION_NAME, APPLICATION_ID, String.format(
					"Exception while validating the finger print with bio api: %s caused by %s",
					exception.getMessage(), exception.getCause()));

		}
		LOGGER.info(LoggerConstants.VALIDATE_FP_WITH_BIO_API, APPLICATION_NAME, APPLICATION_ID,
				"Validating finger print with bio api ended");
		return flag;
	}

	/**
	 * Validate all the user input finger details with all the finger details form
	 * the DB.
	 * 
	 * @param capturedFingerPrintDetails
	 * @return
	 */
	private boolean validateManyToManyFP(List<FingerprintDetailsDTO> capturedFingerPrintDetails) {
		Boolean isMatchFound = false;
		for (FingerprintDetailsDTO fingerprintDetailsDTO : capturedFingerPrintDetails) {
			isMatchFound = validateFpWithBioApi(fingerprintDetailsDTO,
					userDetailDAO.getAllActiveUsers(fingerprintDetailsDTO.getFingerType()));
			if (isMatchFound) {
				SessionContext.map().put(RegistrationConstants.DUPLICATE_FINGER, fingerprintDetailsDTO);
				break;
			}
		}
		return isMatchFound;

	}

	@Override
	public AuthTokenDTO validate(String userId, String otp, boolean haveToSaveAuthToken) {
		return null;
	}	
}
