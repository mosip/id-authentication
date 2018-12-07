package io.mosip.registration.util.biometric;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_FINGERPRINT_FACADE;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

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

	public void getFingerPrintImageAsDTO(FingerprintDetailsDTO fpDetailsDTO, String fingerType) throws IOException {

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
			fpDetailsDTO.setFingerPrint((byte[]) fingerMap.get(RegistrationConstants.IMAGE_BYTE_ARRAY_KEY));
			fpDetailsDTO.setFingerprintImageName(fingerType.concat(RegistrationConstants.DOT)
					.concat((String) fingerMap.get(RegistrationConstants.IMAGE_FORMAT_KEY)));
			fpDetailsDTO.setFingerType(fingerType);
			fpDetailsDTO.setForceCaptured(false);
			fpDetailsDTO.setNumRetry(2);
			fpDetailsDTO.setQualityScore((double) fingerMap.get(RegistrationConstants.IMAGE_SCORE_KEY));
		} catch (RegBaseCheckedException e) {

		} finally {
			if (!fingerMap.isEmpty())
				fingerMap.clear();
		}
	}

	public void segmentFingerPrintImage(FingerprintDetailsDTO fingerprintDetailsDTO, String filePath) {
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
			InputStream inputStream = this.getClass().getResourceAsStream(path);
			BufferedImage bufferedImage = ImageIO.read(inputStream);
			
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, "jpeg" , byteArrayOutputStream);

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
	 */
	private void readSegmentedFingerPrintsSTUB(FingerprintDetailsDTO fingerprintDetailsDTO, String path)
			throws RegBaseCheckedException {
		LOGGER.debug(LOG_REG_FINGERPRINT_FACADE, APPLICATION_NAME, APPLICATION_ID,
				"Reading scanned Finger has started");

		try (Stream<Path> paths = Files.walk(Paths.get(path))) {
			paths.filter(Files::isRegularFile).forEach(e -> {
				File file = e.getFileName().toFile();
				if (file.getName().equals(RegistrationConstants.ISO_FILE)) {
					readFingerFromFileSTUB(e, fingerprintDetailsDTO, RegistrationConstants.ISO_FILE_NAME);
				} else if (file.getName().equals(RegistrationConstants.ISO_IMAGE_FILE)) {
					readFingerFromFileSTUB(e, fingerprintDetailsDTO, RegistrationConstants.ISO_IMAGE_FILE_NAME);
				}
			});
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

	/**
	 * Reading finger based on the isoFile type.
	 *
	 * @param path        the path
	 * @param isoFileType the iso file type
	 */
	private void readFingerFromFileSTUB(Path path, FingerprintDetailsDTO fingerprintDetailsDTO, String isoFileType) {
		try {
			FingerprintDetailsDTO segmentedDetailsDTO = new FingerprintDetailsDTO();
			byte[] allBytes = Files.readAllBytes(path.toAbsolutePath());

			segmentedDetailsDTO.setFingerPrint(allBytes);
			segmentedDetailsDTO.setFingerType(path.toFile().getParentFile().getName().concat(isoFileType));
			segmentedDetailsDTO.setFingerprintImageName(path.toFile().getParentFile().getName().concat(isoFileType));
			segmentedDetailsDTO.setNumRetry(1);
			segmentedDetailsDTO.setForceCaptured(false);
			segmentedDetailsDTO.setQualityScore(90);

			if (fingerprintDetailsDTO.getSegmentedFingerprints() == null) {
				List<FingerprintDetailsDTO> segmentedFingerprints = new ArrayList<FingerprintDetailsDTO>(5);
				fingerprintDetailsDTO.setSegmentedFingerprints(segmentedFingerprints);
			}
			fingerprintDetailsDTO.getSegmentedFingerprints().add(segmentedDetailsDTO);
		} catch (IOException ioException) {
			LOGGER.error(LOG_REG_FINGERPRINT_FACADE, APPLICATION_NAME, APPLICATION_ID, ioException.getMessage());
		}
	}

}
