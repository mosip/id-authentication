package io.mosip.registration.validator;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_FINGERPRINT_FACADE;
import static io.mosip.registration.constants.LoggerConstants.LOG_REG_IRIS_VALIDATOR;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.bioapi.exception.BiometricException;
import io.mosip.kernel.core.bioapi.model.Score;
import io.mosip.kernel.core.bioapi.spi.IBioApi;
import io.mosip.kernel.core.cbeffutil.entity.BDBInfo;
import io.mosip.kernel.core.cbeffutil.entity.BIR;
import io.mosip.kernel.core.cbeffutil.entity.BIR.BIRBuilder;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleType;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.UserDetailDAO;
import io.mosip.registration.dto.AuthTokenDTO;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.entity.UserBiometric;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;

/**
 * This class will take the Iris details from the DB and validate against the
 * captured Iris.
 * <p>
 * The validation will be happen by calling the corresponding method in the
 * bioservice
 * </p>
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Service("irisValidator")
public class IrisValidatorImpl extends AuthenticationBaseValidator {

	private static final Logger LOGGER = AppConfig.getLogger(IrisValidatorImpl.class);

	@Autowired
	private UserDetailDAO userDetailDAO;

	@Autowired
	@Qualifier("iris")
	IBioApi ibioApi;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.validator.AuthenticationBaseValidator#validate(io.mosip
	 * .registration.dto.AuthenticationValidatorDTO)
	 */
	@Override
	public boolean validate(AuthenticationValidatorDTO authenticationValidatorDTO) {

		LOGGER.info(LOG_REG_IRIS_VALIDATOR, APPLICATION_NAME, APPLICATION_ID,
				"Stubbing iris details for user registration");

		List<UserBiometric> userIrisDetails = userDetailDAO
				.getUserSpecificBioDetails(authenticationValidatorDTO.getUserId(), RegistrationConstants.IRS);

		try {
			userIdValidation(authenticationValidatorDTO.getUserId());
			if (RegistrationConstants.SINGLE.equals(authenticationValidatorDTO.getAuthValidationType())) {
				return validateOneToManyIris(authenticationValidatorDTO.getIrisDetails().get(0), userIrisDetails);
			} else if (RegistrationConstants.MULTIPLE.equals(authenticationValidatorDTO.getAuthValidationType())) {
				return validateManyToManyIris(authenticationValidatorDTO.getIrisDetails(), userIrisDetails);
			}
			return false;

		} catch (RegBaseCheckedException regBaseCheckedException) {
			LOGGER.error(LOG_REG_IRIS_VALIDATOR, APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(regBaseCheckedException));
		}
		return false;
	}

	/**
	 * Validate all the user input finger details with all the finger details form
	 * the DB.
	 * 
	 * @param capturedFingerPrintDetails
	 * @return
	 */
	private boolean validateManyToManyIris(List<IrisDetailsDTO> capturedIrisDetails,
			List<UserBiometric> userIrisDetails) {
		Boolean isMatchFound = false;
		for (IrisDetailsDTO irisDetailsDTO : capturedIrisDetails) {
			isMatchFound = validateOneToManyIris(irisDetailsDTO, userIrisDetails);
			if (isMatchFound) {
				SessionContext.map().put(RegistrationConstants.DUPLICATE_IRIS, irisDetailsDTO.getIrisType());
				break;
			}
		}
		return isMatchFound;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.bio.BioService#validateIrisAgainstDb(io.mosip.
	 * registration.dto.biometric.IrisDetailsDTO, java.util.List)
	 */
	private boolean validateOneToManyIris(IrisDetailsDTO irisDetailsDTO, List<UserBiometric> userIrisDetails) {

		BIR capturedBir = new BIRBuilder().withBdb(irisDetailsDTO.getIrisIso())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withType(Collections.singletonList(SingleType.IRIS)).build())
				.build();
		
		boolean flag = true;
		if((String.valueOf(ApplicationContext.map().get(RegistrationConstants.DEDUPLICATION_ENABLE_FLAG))).equalsIgnoreCase(RegistrationConstants.DISABLE))
		return false;
		
		BIR[] registeredBir = new BIR[userIrisDetails.size()];
		ApplicationContext.map().remove("IDENTY_SDK");
		Score[] scores = null;
		flag = false;
		int i = 0;
		for (UserBiometric userBiometric : userIrisDetails) {
			registeredBir[i] = new BIRBuilder().withBdb(userBiometric.getBioIsoImage())
					.withBdbInfo(
							new BDBInfo.BDBInfoBuilder().withType(Collections.singletonList(SingleType.IRIS)).build())
					.build();
			i++;
		}
		try {
			scores = ibioApi.match(capturedBir, registeredBir, null);
			int reqScore = 60;
			for (Score score : scores) {
				if (score.getScaleScore() >= reqScore) {
					flag = true;
				}
			}
		} catch (BiometricException exception) {
			LOGGER.error(LOG_REG_FINGERPRINT_FACADE, APPLICATION_NAME, APPLICATION_ID,
					String.format("Exception while validating the iris with bio api: %s caused by %s",
							exception.getMessage(), exception.getCause()));
			ApplicationContext.map().put("IDENTY_SDK", "FAILED");
			return false;

		}
		return flag;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.validator.AuthenticationBaseValidator#validate(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	public AuthTokenDTO validate(String userId, String otp, boolean haveToSaveAuthToken) {
		return null;
	}

	private void userIdValidation(String userId) throws RegBaseCheckedException {
		if (null == userId || userId.isEmpty()) {
			throw new RegBaseCheckedException(RegistrationExceptionConstants.REG_USER_ID_EXCEPTION.getErrorCode(),
					RegistrationExceptionConstants.REG_USER_ID_EXCEPTION.getErrorMessage());
		}

	}

}
