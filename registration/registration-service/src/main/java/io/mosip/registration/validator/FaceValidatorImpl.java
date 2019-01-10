package io.mosip.registration.validator;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_FACE_VALIDATOR;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.UserDetailDAO;
import io.mosip.registration.device.face.FaceFacade;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.entity.UserBiometric;

@Service("faceValidator")
public class FaceValidatorImpl extends AuthenticationBaseValidator{
	
	private static final Logger LOGGER = AppConfig.getLogger(FaceValidatorImpl.class);

	@Autowired
	private UserDetailDAO userDetailDAO;

	@Autowired
	private FaceFacade faceFacade;

	@Override
	public boolean validate(AuthenticationValidatorDTO authenticationValidatorDTO) {
		
		LOGGER.debug(LOG_REG_FACE_VALIDATOR, APPLICATION_NAME, APPLICATION_ID,
				"Stubbing face capture details for user registration");
		
		List<UserBiometric> userFaceDetails = userDetailDAO
				.getUserSpecificBioDetails(authenticationValidatorDTO.getUserId(), RegistrationConstants.FACE);
		
		LOGGER.debug(LOG_REG_FACE_VALIDATOR, APPLICATION_NAME, APPLICATION_ID,
				"validating face details for user registration");
		
		return faceFacade.validateFace(authenticationValidatorDTO.getFaceDetail(), userFaceDetails);
	}

}
