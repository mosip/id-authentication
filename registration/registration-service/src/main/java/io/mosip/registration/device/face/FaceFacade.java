package io.mosip.registration.device.face;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_FACE_FACADE;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.entity.UserBiometric;

@Component
public class FaceFacade {
	
	private static final Logger LOGGER = AppConfig.getLogger(FaceFacade.class);
	
	public boolean validateFace(byte[] faceDetail, List<UserBiometric> userFaceDetails) {
		
		LOGGER.debug(LOG_REG_FACE_FACADE, APPLICATION_NAME, APPLICATION_ID,
				"Stubbing face details for user registration");
		
		return userFaceDetails.stream().anyMatch(face -> Arrays.equals(faceDetail, face.getBioIsoImage()));
	}

}
