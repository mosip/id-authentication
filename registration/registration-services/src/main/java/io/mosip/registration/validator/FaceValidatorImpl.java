package io.mosip.registration.validator;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_FACE_FACADE;
import static io.mosip.registration.constants.LoggerConstants.LOG_REG_FACE_VALIDATOR;
import static io.mosip.registration.constants.LoggerConstants.LOG_REG_FINGERPRINT_FACADE;
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
import io.mosip.registration.dao.UserDetailDAO;
import io.mosip.registration.dto.AuthTokenDTO;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.biometric.FaceDetailsDTO;
import io.mosip.registration.entity.UserBiometric;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;

/**
 * This class will take the face details from the DB and verify against the
 * captured face.
 * <p>
 * The validation will be happen by calling the corresponding method in the
 * bioservice
 * </p>
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Service("faceValidator")
public class FaceValidatorImpl extends AuthenticationBaseValidator {

	private static final Logger LOGGER = AppConfig.getLogger(FaceValidatorImpl.class);

	@Autowired
	private UserDetailDAO userDetailDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.validator.AuthenticationBaseValidator#validate(io.mosip
	 * .registration.dto.AuthenticationValidatorDTO)
	 */
	@Override
	public boolean validate(AuthenticationValidatorDTO authenticationValidatorDTO) {

		LOGGER.info(LOG_REG_FACE_VALIDATOR, APPLICATION_NAME, APPLICATION_ID,
				"validating face capture details for user registration");

		try {
			userIdValidation(authenticationValidatorDTO.getUserId());

			List<UserBiometric> userFaceDetails = userDetailDAO
					.getUserSpecificBioDetails(authenticationValidatorDTO.getUserId(), RegistrationConstants.FACE);

			LOGGER.info(LOG_REG_FACE_VALIDATOR, APPLICATION_NAME, APPLICATION_ID,
					"validated face details for user registration");

			return validateFaceAgainstDb(authenticationValidatorDTO.getFaceDetail(), userFaceDetails);
		} catch (RegBaseCheckedException regBaseCheckedException) {
			LOGGER.error(LOG_REG_FACE_VALIDATOR, APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(regBaseCheckedException));
		}

		LOGGER.info(LOG_REG_FACE_VALIDATOR, APPLICATION_NAME, APPLICATION_ID,
				"validated face capture details for user registration");

		return false;
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

	@Autowired
	@Qualifier("face")	
	IBioApi ibioApi;

	private boolean validateFaceAgainstDb(FaceDetailsDTO faceDetail, List<UserBiometric> userFaceDetails) {

		LOGGER.info(LOG_REG_FACE_FACADE, APPLICATION_NAME, APPLICATION_ID,

				"Stubbing face details for user registration");
		
		BIR capturedBir = new BIRBuilder().withBdb(faceDetail.getFaceISO()).withBdbInfo(new BDBInfo.BDBInfoBuilder().withType(Collections.singletonList(SingleType.FACE)).build()).build();
		BIR[] registeredBir = new BIR[userFaceDetails.size()];
		Score[] scores = null;
		boolean flag = false;
		int i = 0;
		for (UserBiometric userBiometric : userFaceDetails) {
			try {
			registeredBir[i] = new BIRBuilder().withBdb(userBiometric.getBioIsoImage()).withBdbInfo(new BDBInfo.BDBInfoBuilder().withType(Collections.singletonList(SingleType.FACE)).build()).build();
			}catch(Exception e) {
				return false;
			}
			i++;
		}
		try {
			scores = ibioApi.match(capturedBir, registeredBir, null);
			int faceScore = 80;
			for (Score score : scores) {
				if (score.getInternalScore() >= faceScore) {
					flag = true;
				}
			}
		} catch (BiometricException exception) {
			LOGGER.error(LOG_REG_FINGERPRINT_FACADE, APPLICATION_NAME, APPLICATION_ID, String.format(
					"Exception while validating the face with bio api: %s caused by %s",
					exception.getMessage(), exception.getCause()));

		}
		return flag;
		
	}
}