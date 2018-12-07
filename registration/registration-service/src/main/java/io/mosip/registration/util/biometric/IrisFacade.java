package io.mosip.registration.util.biometric;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_IRIS_FACADE;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

/**
 * It takes a decision based on the input provider name and initialize the
 * respective implementation class and perform the required operation.
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Component
public class IrisFacade {

	private static final Logger LOGGER = AppConfig.getLogger(IrisFacade.class);

	public void getIrisImageAsDTO(IrisDetailsDTO irisDetailsDTO, String irisType) throws RegBaseCheckedException {
		try {
			LOGGER.debug(LOG_REG_IRIS_FACADE, APPLICATION_NAME, APPLICATION_ID,
					"Stubbing iris details for user registration");

			Map<String, Object> scannedIrisMap = getIrisScannedImage(irisType);

			double qualityScore = (double) scannedIrisMap.get(RegistrationConstants.IMAGE_SCORE_KEY);

			if (irisDetailsDTO.getQualityScore() < qualityScore) {
				// Set the values in IrisDetailsDTO object
				irisDetailsDTO.setIris((byte[]) scannedIrisMap.get(RegistrationConstants.IMAGE_BYTE_ARRAY_KEY));
				irisDetailsDTO.setForceCaptured(false);
				irisDetailsDTO.setQualityScore(qualityScore);
				irisDetailsDTO.setIrisImageName(irisType.concat(RegistrationConstants.DOT)
						.concat((String) scannedIrisMap.get(RegistrationConstants.IMAGE_FORMAT_KEY)));
				irisDetailsDTO.setIrisType(irisType);
			}

			LOGGER.debug(LOG_REG_IRIS_FACADE, APPLICATION_NAME, APPLICATION_ID,
					"Stubbing iris details for user registration completed");
		} catch (RegBaseCheckedException regBaseCheckedException) {
			throw regBaseCheckedException;
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.USER_REG_IRIS_SCAN_EXP,
					String.format("Exception while stubbing the iris details for user registration: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));
		}
	}

	private Map<String, Object> getIrisScannedImage(String irisType) throws RegBaseCheckedException {
		try {
			LOGGER.debug(LOG_REG_IRIS_FACADE, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of iris details for user registration");

			InputStream inputStream = this.getClass().getResourceAsStream("/images/scanned-iris.png");

			byte[] scannedIrisBytes = new byte[inputStream.available()];
			inputStream.read(scannedIrisBytes);

			// Add image format, image and quality score in bytes array to map
			Map<String, Object> scannedIris = new HashMap<>();
			scannedIris.put(RegistrationConstants.IMAGE_FORMAT_KEY, "png");
			scannedIris.put(RegistrationConstants.IMAGE_BYTE_ARRAY_KEY, scannedIrisBytes);
			scannedIris.put(RegistrationConstants.IMAGE_SCORE_KEY, 90.5);

			LOGGER.debug(LOG_REG_IRIS_FACADE, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of iris details for user registration completed");

			return scannedIris;
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(RegistrationExceptionConstants.REG_IRIS_SCANNING_ERROR.getErrorCode(),
					RegistrationExceptionConstants.REG_IRIS_SCANNING_ERROR.getErrorMessage());
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.USER_REG_IRIS_STUB_IMAGE_EXP,
					String.format("Exception while scanning iris details for user registration: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));
		}
	}

}
