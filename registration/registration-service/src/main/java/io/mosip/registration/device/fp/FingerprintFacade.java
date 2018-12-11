package io.mosip.registration.device.fp;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_FINGERPRINT_FACADE;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import javafx.scene.image.WritableImage;

/**
 * It takes a decision based on the input provider name and initialize the
 * respective implementation class and perform the required operation.
 * 
 * @author M1046564
 *
 */
@Component
public class FingerprintFacade {

	private static final Logger LOGGER = AppConfig.getLogger(FingerprintFacade.class);
	private List<MosipFingerprintProvider> fingerprintProviders;

	private MosipFingerprintProvider fingerprintProvider;

	/**
	 * provide the minutia of a finger.
	 * 
	 * @return
	 */
	public String getMinutia() {
		return fingerprintProvider.getMinutia();
	}

	public byte[] getIsoTemplate() {
		return fingerprintProvider.getIsoTemplate();
	}

	public String getErrorMessage() {
		return fingerprintProvider.getErrorMessage();

	}

	/**
	 * 
	 * @return
	 */
	public WritableImage getFingerPrintImage() throws IOException {
		return fingerprintProvider.getFingerPrintImage();
	}

	public void getFingerPrintImageAsDTO(FingerprintDetailsDTO fpDetailsDTO, String fingerType) {

		Map<String, Object> fingerMap = null;

		try {
			// TODO : Currently stubbing the data. once we have the device, we can remove
			// this.

			if (fingerType.equals(RegistrationConstants.LEFTPALM)) {
				fingerMap = getFingerPrintScannedImageWithStub(RegistrationConstants.LEFTHAND_SLAP_FINGERPRINT_PATH);
			} else if (fingerType.equals(RegistrationConstants.RIGHTPALM)) {
				fingerMap = getFingerPrintScannedImageWithStub(RegistrationConstants.RIGHTHAND_SLAP_FINGERPRINT_PATH);
			} else if (fingerType.equals(RegistrationConstants.THUMBS)) {
				fingerMap = getFingerPrintScannedImageWithStub(RegistrationConstants.BOTH_THUMBS_FINGERPRINT_PATH);
			}
			if (fpDetailsDTO.getQualityScore() < (double) fingerMap.get(RegistrationConstants.IMAGE_SCORE_KEY)) {
				fpDetailsDTO.setFingerPrint((byte[]) fingerMap.get(RegistrationConstants.IMAGE_BYTE_ARRAY_KEY));
				fpDetailsDTO.setFingerprintImageName(fingerType.concat(RegistrationConstants.DOT)
						.concat((String) fingerMap.get(RegistrationConstants.IMAGE_FORMAT_KEY)));
				fpDetailsDTO.setFingerType(fingerType);
				fpDetailsDTO.setForceCaptured(false);

				fpDetailsDTO.setQualityScore((double) fingerMap.get(RegistrationConstants.IMAGE_SCORE_KEY));
			}
		} catch (RegBaseCheckedException e) {

		} finally {
			if (!fingerMap.isEmpty())
				fingerMap.clear();
		}
	}

	public void segmentFingerPrintImage(FingerprintDetailsDTO fingerprintDetailsDTO, String[] filePath) {
		try {
			readSegmentedFingerPrintsSTUB(fingerprintDetailsDTO, filePath);
		} catch (RegBaseCheckedException e) {

		}
	}

	@Autowired
	public FingerprintFacade(List<MosipFingerprintProvider> fingerprintProviders) {
		this.fingerprintProviders = fingerprintProviders;
	}

	public MosipFingerprintProvider getFingerprintProviderFactory(String make) {
		for (MosipFingerprintProvider mosipFingerprintProvider : fingerprintProviders) {
			if (mosipFingerprintProvider.getClass().getName().toLowerCase().contains(make.toLowerCase())) {
				fingerprintProvider = mosipFingerprintProvider;
			}
		}
		return fingerprintProvider;
	}

	/**
	 * Stub method to get the finger print scanned image from local hard disk. Once
	 * SDK and device avilable then we can remove it.
	 *
	 * @param path the path
	 * @return the finger print scanned image
	 * @throws RegBaseCheckedException the reg base checked exception
	 */
	private Map<String, Object> getFingerPrintScannedImageWithStub(String path) throws RegBaseCheckedException {
		try {
			LOGGER.debug(LOG_REG_FINGERPRINT_FACADE, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of fingerprints details for user registration");

			BufferedImage bufferedImage = ImageIO.read(this.getClass().getResourceAsStream(path));

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, "jpeg", byteArrayOutputStream);

			byte[] scannedFingerPrintBytes = byteArrayOutputStream.toByteArray();

			// Add image format, image and quality score in bytes array to map
			Map<String, Object> scannedFingerPrints = new HashMap<>();
			scannedFingerPrints.put(RegistrationConstants.IMAGE_FORMAT_KEY, "jpg");
			scannedFingerPrints.put(RegistrationConstants.IMAGE_BYTE_ARRAY_KEY, scannedFingerPrintBytes);
			scannedFingerPrints.put(RegistrationConstants.IMAGE_SCORE_KEY, 90.0);

			LOGGER.debug(LOG_REG_FINGERPRINT_FACADE, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of fingerprints details for user registration completed");

			return scannedFingerPrints;
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_FINGERPRINT_SCANNING_ERROR.getErrorCode(),
					RegistrationExceptionConstants.REG_FINGERPRINT_SCANNING_ERROR.getErrorMessage());
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_FINGERPRINT_FACADE, APPLICATION_NAME, APPLICATION_ID,
					String.format(
							"Exception while scanning fingerprints details for user registration: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));

			throw new RegBaseUncheckedException(RegistrationConstants.USER_REG_FINGERPRINT_SCAN_EXP,
					String.format(
							"Exception while scanning fingerprints details for user registration: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));
		}
	}

	/**
	 * {@code readFingerPrints} is to read the scanned fingerprints.
	 * 
	 * @param path
	 * @throws RegBaseCheckedException
	 * @throws URISyntaxException
	 */
	private void readSegmentedFingerPrintsSTUB(FingerprintDetailsDTO fingerprintDetailsDTO, String[] path)
			throws RegBaseCheckedException {
		LOGGER.debug(LOG_REG_FINGERPRINT_FACADE, APPLICATION_NAME, APPLICATION_ID,
				"Reading scanned Finger has started");

		try {

			List<String> filePaths = Arrays.asList(path);

			for (String folderPath : filePaths) {

				String[] imageFileName = folderPath.split("/");
				FingerprintDetailsDTO segmentedDetailsDTO = new FingerprintDetailsDTO();

				byte[] isoTemplateBytes = IOUtils
						.resourceToByteArray(folderPath.concat(RegistrationConstants.ISO_FILE));
				segmentedDetailsDTO.setFingerPrint(isoTemplateBytes);

				byte[] isoImageBytes = IOUtils
						.resourceToByteArray(folderPath.concat(RegistrationConstants.ISO_IMAGE_FILE));
				segmentedDetailsDTO.setFingerPrintISOImage(isoImageBytes);

				segmentedDetailsDTO.setFingerType(imageFileName[3]);
				segmentedDetailsDTO.setFingerprintImageName(imageFileName[3]);
				segmentedDetailsDTO.setNumRetry(fingerprintDetailsDTO.getNumRetry());
				segmentedDetailsDTO.setForceCaptured(false);
				segmentedDetailsDTO.setQualityScore(90);

				if (fingerprintDetailsDTO.getSegmentedFingerprints() == null) {
					List<FingerprintDetailsDTO> segmentedFingerprints = new ArrayList<>(5);
					fingerprintDetailsDTO.setSegmentedFingerprints(segmentedFingerprints);
				}
				fingerprintDetailsDTO.getSegmentedFingerprints().add(segmentedDetailsDTO);
			}

		} catch (IOException ioException) {
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_FINGERPRINT_SCANNING_ERROR.getErrorCode(),
					RegistrationExceptionConstants.REG_FINGERPRINT_SCANNING_ERROR.getErrorMessage());
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_FINGERPRINT_FACADE, APPLICATION_NAME, APPLICATION_ID, String.format(
					"Exception while reading scanned fingerprints details for user registration: %s caused by %s",
					runtimeException.getMessage(), runtimeException.getCause()));

			throw new RegBaseUncheckedException(RegistrationConstants.USER_REG_FINGERPRINT_SCAN_EXP, String.format(
					"Exception while reading scanned fingerprints details for user registration: %s caused by %s",
					runtimeException.getMessage(), runtimeException.getCause()));
		}
		LOGGER.debug(LOG_REG_FINGERPRINT_FACADE, APPLICATION_NAME, APPLICATION_ID, "Reading scanned Finger has ended");
	}

}
