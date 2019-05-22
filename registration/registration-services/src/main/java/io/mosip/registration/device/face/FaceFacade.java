package io.mosip.registration.device.face;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_FACE_FACADE;
import static io.mosip.registration.constants.LoggerConstants.LOG_REG_IRIS_FACADE;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dto.biometric.FaceDetailsDTO;
import io.mosip.registration.entity.UserBiometric;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.mdm.service.impl.MosipBioDeviceManager;

/**
 * It takes a decision based on the input provider name and initialize the
 * respective implementation class and perform the required operation.
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Component
public class FaceFacade {

	private static final Logger LOGGER = AppConfig.getLogger(FaceFacade.class);

	@Autowired
	MosipBioDeviceManager mosipBioDeviceManager;

	/**
	 * Capture Face
	 * 
	 * @return byte[] of captured Face
	 */
	public byte[] captureFace() {

		LOGGER.info(LOG_REG_IRIS_FACADE, APPLICATION_NAME, APPLICATION_ID, "Stub data for Face");
		byte[] capturedByte = null;

		try {
			if(RegistrationConstants.ENABLE.equalsIgnoreCase(((String)ApplicationContext.getInstance().map().get(RegistrationConstants.MDM_ENABLED))))
				capturedByte = mosipBioDeviceManager.scan("FACE").get("FACE");
			else
				capturedByte=RegistrationConstants.FACE.toLowerCase().getBytes();
		} catch (RegBaseCheckedException | RuntimeException e) {
			e.printStackTrace();
		}
		return capturedByte;
	}

	/**
	 * Validate Face
	 * 
	 * @param faceDetail
	 *            details of the captured face
	 * @param userFaceDetails
	 *            details of the user face from db
	 * 
	 * @return boolean of captured Face
	 */
	public boolean validateFace(FaceDetailsDTO faceDetail, List<UserBiometric> userFaceDetails) {

		LOGGER.info(LOG_REG_FACE_FACADE, APPLICATION_NAME, APPLICATION_ID,
				"Stubbing face details for user registration");

		return userFaceDetails.stream().anyMatch(face -> Arrays.equals(faceDetail.getFace(), face.getBioIsoImage()));
	}

}
