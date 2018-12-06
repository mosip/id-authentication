package io.mosip.registration.controller.device;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_BIOMETRIC_SCAN_CONTROLLER;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * {@code FingerPrintScanController} is to scan fingerprint biometrics.
 * 
 * @author Mahesh Kumar
 * @author Balaji Sridharan
 * @since 1.0
 */
@Controller
public class FingerPrintScanController extends BaseController {

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(FingerPrintScanController.class);

	/** The fingerprint capture controller. */
	@Autowired
	private FingerPrintCaptureController fpCaptureController;
	@Autowired
	private IrisCaptureController irisCaptureController;

	/** The selected anchor pane. */
	@FXML
	private AnchorPane selectedAnchorPane;

	/** The finger print scan image. */
	@FXML
	private ImageView fingerPrintScanImage;
	@FXML
	private Label popupTitle;

	/** The primary stage. */
	private Stage primarystage;

	/**
	 * @param popupTitle the popupTitle to set
	 */
	public void setPopupTitle(String popupTitle) {
		this.popupTitle.setText(popupTitle);
	}

	/**
	 * @param primarystage the primarystage to set
	 */
	public void setPrimarystage(Stage primarystage) {
		this.primarystage = primarystage;
	}

	/**
	 * {@code init} method is to collect the values passed from
	 * {@link FingerPrintCaptureController}
	 *
	 * @param selectedPane
	 * @param stage
	 * @param detailsDTOs
	 */
	public void init(AnchorPane selectedPane, Stage stage) {
		selectedAnchorPane = selectedPane;
		primarystage = stage;
		popupTitle.setText(RegistrationConstants.FINGERPRINT);
	}

	/**
	 * This method scans the biometric of the individual
	 */
	@FXML
	public void scan() {
		try {
			LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of biometric details for user registration");

			if (popupTitle.getText().equalsIgnoreCase(RegistrationConstants.FINGERPRINT)) {
				scanFinger();
			} else {
				scanIris();
			}

			LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of biometric details for user registration completed");
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format("Exception while scanning biometric details for user registration: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.BIOMETRIC_SCANNING_ERROR);
		}
	}

	/**
	 * {@code ScanFinger} is to scan the fingers.
	 * 
	 * 
	 */
	private void scanFinger() {
		LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Scan Finger has started");

		try {
			if (selectedAnchorPane.getId() == fpCaptureController.getLeftHandPalmPane().getId()) {

				readFingerPrints(RegistrationConstants.LEFTHAND_SEGMENTED_FINGERPRINT_PATH);

				Map<String, Object> leftPalmMap = getFingerPrintScannedImage(
						RegistrationConstants.LEFTHAND_SLAP_FINGERPRINT_PATH);

				byte[] leftPalmImageBytes = (byte[]) leftPalmMap.get(RegistrationConstants.IMAGE_BYTE_ARRAY_KEY);

				Image leftPalmImage = convertBytesToImage(leftPalmImageBytes);

				double qualityScore = (double) leftPalmMap.get(RegistrationConstants.IMAGE_SCORE_KEY);

				fingerPrintScanImage.setImage(leftPalmImage);

				FingerprintDetailsDTO fpDetailsDTO = new FingerprintDetailsDTO();

				fpDetailsDTO.setFingerPrint(leftPalmImageBytes);
				fpDetailsDTO.setFingerprintImageName(RegistrationConstants.LEFTPALM.concat(RegistrationConstants.DOT)
						.concat((String) leftPalmMap.get(RegistrationConstants.IMAGE_FORMAT_KEY)));
				fpDetailsDTO.setFingerType(RegistrationConstants.LEFTPALM);
				fpDetailsDTO.setForceCaptured(false);
				fpDetailsDTO.setNumRetry(2);
				fpDetailsDTO.setQualityScore(qualityScore);

				fpCaptureController.getRegistrationController().getRegistrationDtoContent().getBiometricDTO()
						.getApplicantBiometricDTO().getFingerprintDetailsDTO().add(fpDetailsDTO);

				generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationConstants.FP_CAPTURE_SUCCESS);
				primarystage.close();
				fpCaptureController.getLeftHandPalmImageview().setImage(leftPalmImage);
				fpCaptureController.getLeftSlapQualityScore()
						.setText(String.valueOf(fpDetailsDTO.getQualityScore()) + RegistrationConstants.PERCENTAGE);

			} else if (selectedAnchorPane.getId() == fpCaptureController.getRightHandPalmPane().getId()) {

				readFingerPrints(RegistrationConstants.RIGHTHAND_SEGMENTED_FINGERPRINT_PATH);

				Map<String, Object> rightPalmMap = getFingerPrintScannedImage(
						RegistrationConstants.RIGHTHAND_SLAP_FINGERPRINT_PATH);

				byte[] rightPalmImageBytes = (byte[]) rightPalmMap.get(RegistrationConstants.IMAGE_BYTE_ARRAY_KEY);

				Image rightPalmImage = convertBytesToImage(rightPalmImageBytes);

				double qualityScore = (double) rightPalmMap.get(RegistrationConstants.IMAGE_SCORE_KEY);

				fingerPrintScanImage.setImage(rightPalmImage);
				FingerprintDetailsDTO fpDetailsDTO = new FingerprintDetailsDTO();

				fpDetailsDTO.setFingerPrint(rightPalmImageBytes);
				fpDetailsDTO.setFingerprintImageName(RegistrationConstants.RIGHTPALM.concat(RegistrationConstants.DOT)
						.concat((String) rightPalmMap.get(RegistrationConstants.IMAGE_FORMAT_KEY)));
				fpDetailsDTO.setFingerType(RegistrationConstants.RIGHTPALM);
				fpDetailsDTO.setForceCaptured(false);
				fpDetailsDTO.setNumRetry(2);
				fpDetailsDTO.setQualityScore(qualityScore);

				fpCaptureController.getRegistrationController().getRegistrationDtoContent().getBiometricDTO()
						.getApplicantBiometricDTO().getFingerprintDetailsDTO().add(fpDetailsDTO);

				generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationConstants.FP_CAPTURE_SUCCESS);
				primarystage.close();
				fpCaptureController.getRightHandPalmImageview().setImage(rightPalmImage);
				fpCaptureController.getRightSlapQualityScore()
						.setText(String.valueOf(fpDetailsDTO.getQualityScore()) + RegistrationConstants.PERCENTAGE);

			} else if (selectedAnchorPane.getId() == fpCaptureController.getThumbPane().getId()) {

				readFingerPrints(RegistrationConstants.THUMB_SEGMENTED_FINGERPRINT_PATH);

				Map<String, Object> thumbMap = getFingerPrintScannedImage(
						RegistrationConstants.BOTH_THUMBS_FINGERPRINT_PATH);

				byte[] thumbImageBytes = (byte[]) thumbMap.get(RegistrationConstants.IMAGE_BYTE_ARRAY_KEY);

				Image thumbImage = convertBytesToImage(thumbImageBytes);

				double qualityScore = (double) thumbMap.get(RegistrationConstants.IMAGE_SCORE_KEY);

				fingerPrintScanImage.setImage(thumbImage);

				FingerprintDetailsDTO fpDetailsDTO = new FingerprintDetailsDTO();

				fpDetailsDTO.setFingerPrint(thumbImageBytes);
				fpDetailsDTO.setFingerprintImageName(RegistrationConstants.THUMBS.concat(RegistrationConstants.DOT)
						.concat((String) thumbMap.get(RegistrationConstants.IMAGE_FORMAT_KEY)));
				fpDetailsDTO.setFingerType(RegistrationConstants.THUMBS);
				fpDetailsDTO.setForceCaptured(false);
				fpDetailsDTO.setNumRetry(1);
				fpDetailsDTO.setQualityScore(qualityScore);

				fpCaptureController.getRegistrationController().getRegistrationDtoContent().getBiometricDTO()
						.getApplicantBiometricDTO().getFingerprintDetailsDTO().add(fpDetailsDTO);

				generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationConstants.FP_CAPTURE_SUCCESS);
				primarystage.close();
				fpCaptureController.getThumbImageview().setImage(thumbImage);
				fpCaptureController.getThumbsQualityScore()
						.setText(String.valueOf(fpDetailsDTO.getQualityScore()) + RegistrationConstants.PERCENTAGE);

			}
		} catch (RegBaseCheckedException regBaseCheckedException) {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.FINGERPRINT_SCANNING_ERROR);
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format(
							"Exception while getting the scanned Finger details for user registration: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.FINGERPRINT_SCANNING_ERROR);
		}
		LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Scan Finger has ended");
	}

	/**
	 * {@code readFingerPrints} is to read the scanned fingerprints.
	 * 
	 * @param path
	 * @throws RegBaseCheckedException
	 */
	private void readFingerPrints(String path) throws RegBaseCheckedException {
		LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
				"Reading scanned Finger has started");

		try (Stream<Path> paths = Files.walk(Paths.get(path))) {
			paths.filter(Files::isRegularFile).forEach(e -> {
				File file = e.getFileName().toFile();
				if (file.getName().equals(RegistrationConstants.ISO_FILE)) {
					readFinger(e, RegistrationConstants.ISO_FILE_NAME);
				} else if (file.getName().equals(RegistrationConstants.ISO_IMAGE_FILE)) {
					readFinger(e, RegistrationConstants.ISO_IMAGE_FILE_NAME);
				}
			});
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_FINGERPRINT_SCANNING_ERROR.getErrorCode(),
					RegistrationExceptionConstants.REG_FINGERPRINT_SCANNING_ERROR.getErrorMessage());
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, String.format(
					"Exception while reading scanned fingerprints details for user registration: %s caused by %s",
					runtimeException.getMessage(), runtimeException.getCause()));

			throw new RegBaseUncheckedException(RegistrationConstants.USER_REG_FINGERPRINT_SCAN_EXP, String.format(
					"Exception while reading scanned fingerprints details for user registration: %s caused by %s",
					runtimeException.getMessage(), runtimeException.getCause()));
		}
		LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
				"Reading scanned Finger has ended");
	}

	/**
	 * Reading finger based on the isoFile type.
	 *
	 * @param path        the path
	 * @param isoFileType the iso file type
	 */
	private void readFinger(Path path, String isoFileType) {
		try {

			FingerprintDetailsDTO fingerprintDetailsDTO = new FingerprintDetailsDTO();
			byte[] allBytes = Files.readAllBytes(path.toAbsolutePath());

			fingerprintDetailsDTO.setFingerPrint(allBytes);
			fingerprintDetailsDTO.setFingerType(path.toFile().getParentFile().getName().concat(isoFileType));
			fingerprintDetailsDTO.setFingerprintImageName(path.toFile().getParentFile().getName().concat(isoFileType));
			fingerprintDetailsDTO.setNumRetry(1);
			fingerprintDetailsDTO.setForceCaptured(false);
			fingerprintDetailsDTO.setQualityScore(90);

			fpCaptureController.getRegistrationController().getRegistrationDtoContent().getBiometricDTO()
					.getApplicantBiometricDTO().getSegmentedFingerprints().add(fingerprintDetailsDTO);
		} catch (IOException ioException) {
			LOGGER.error(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, ioException.getMessage());
		}
	}

	/**
	 * Scan iris.
	 */
	private void scanIris() {
		try {
			LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of iris details for user registration");

			Map<String, Object> scannedIrisMap = getIrisScannedImage();

			Pane selectedIris = irisCaptureController.getSelectedIris();

			byte[] irisImageBytes = (byte[]) scannedIrisMap.get(RegistrationConstants.IMAGE_BYTE_ARRAY_KEY);

			Image scannedIrisImage = convertBytesToImage(irisImageBytes);

			double qualityScore = (double) scannedIrisMap.get(RegistrationConstants.IMAGE_SCORE_KEY);

			if (validateIrisLocalDedup(irisImageBytes)) {
				// Display the Scanned Iris Image in the Scan pop-up screen
				fingerPrintScanImage.setImage(scannedIrisImage);

				generateAlert(RegistrationConstants.ALERT_INFORMATION, "Iris captured successfully");

				if (getIrisQualityScore() < qualityScore) {
					// Display the Scanned Iris Image and its corresponding quality score in the
					// Iris Biometric Screen
					String irisType;
					if (StringUtils.containsIgnoreCase(selectedIris.getId(), RegistrationConstants.LEFT)) {
						irisCaptureController.getLeftIrisImage().setImage(scannedIrisImage);
						irisCaptureController.setLeftIrisQualityScore(qualityScore);
						irisType = RegistrationConstants.LEFT.concat(RegistrationConstants.EYE);
					} else {
						irisCaptureController.getRightIrisImage().setImage(scannedIrisImage);
						irisCaptureController.setRightIrisQualityScore(qualityScore);
						irisType = RegistrationConstants.RIGHT.concat(RegistrationConstants.EYE);
					}
	
					// Create IrisDetailsDTO object
					IrisDetailsDTO irisDetailsDTO = new IrisDetailsDTO();
					irisDetailsDTO.setIris((byte[]) scannedIrisMap.get(RegistrationConstants.IMAGE_BYTE_ARRAY_KEY));
					irisDetailsDTO.setForceCaptured(false);
					irisDetailsDTO.setQualityScore(qualityScore);
					irisDetailsDTO.setIrisImageName(irisType.concat(RegistrationConstants.DOT)
							.concat((String) scannedIrisMap.get(RegistrationConstants.IMAGE_FORMAT_KEY)));
					irisDetailsDTO.setIrisType(irisType);
	
					// Add the captured iris to RegistrationDTO
					irisCaptureController.getIrises().add(irisDetailsDTO);
				}

				primarystage.close();
			}

			LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of iris details for user registration completed");
		} catch (RegBaseCheckedException regBaseCheckedException) {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.IRIS_SCANNING_ERROR);
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format(
							"Exception while getting the scanned iris details for user registration: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.IRIS_SCANNING_ERROR);
		} finally {
			irisCaptureController.getSelectedIris().requestFocus();
		}
	}

	private boolean validateIrisLocalDedup(byte[] scannedIrisImage) {
		// TODO: Implement Local Dedup for Iris
		return true;
	}

	/**
	 * Gets the iris scanned image.
	 *
	 * @return the iris scanned image
	 * @throws RegBaseCheckedException the reg base checked exception
	 */
	private Map<String, Object> getIrisScannedImage() throws RegBaseCheckedException {
		try {
			LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of iris details for user registration");

			InputStream inputStream = this.getClass().getResourceAsStream("/images/scanned-iris.png");

			byte[] scannedIrisBytes = new byte[inputStream.available()];
			inputStream.read(scannedIrisBytes);

			// Add image format, image and quality score in bytes array to map
			Map<String, Object> scannedIris = new HashMap<>();
			scannedIris.put(RegistrationConstants.IMAGE_FORMAT_KEY, "png");
			scannedIris.put(RegistrationConstants.IMAGE_BYTE_ARRAY_KEY, scannedIrisBytes);
			scannedIris.put(RegistrationConstants.IMAGE_SCORE_KEY, 90.5);

			LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of iris details for user registration completed");

			return scannedIris;
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(RegistrationExceptionConstants.REG_IRIS_SCANNING_ERROR.getErrorCode(),
					RegistrationExceptionConstants.REG_IRIS_SCANNING_ERROR.getErrorMessage());
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format("Exception while scanning iris details for user registration: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));

			throw new RegBaseUncheckedException(RegistrationConstants.USER_REG_IRIS_SCAN_EXP,
					String.format("Exception while scanning iris details for user registration: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));
		}
	}

	/**
	 * Gets the finger print scanned image.
	 *
	 * @param path the path
	 * @return the finger print scanned image
	 * @throws RegBaseCheckedException the reg base checked exception
	 */
	private Map<String, Object> getFingerPrintScannedImage(String path) throws RegBaseCheckedException {
		try {
			LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of fingerprints details for user registration");
			InputStream inputStream = this.getClass().getResourceAsStream(path);

			byte[] scannedFingerPrintBytes = new byte[inputStream.available()];
			inputStream.read(scannedFingerPrintBytes);

			// Add image format, image and quality score in bytes array to map
			Map<String, Object> scannedFingerPrints = new HashMap<>();
			scannedFingerPrints.put(RegistrationConstants.IMAGE_FORMAT_KEY, "jpg");
			scannedFingerPrints.put(RegistrationConstants.IMAGE_BYTE_ARRAY_KEY, scannedFingerPrintBytes);
			scannedFingerPrints.put(RegistrationConstants.IMAGE_SCORE_KEY, 90.0);

			LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of fingerprints details for user registration completed");

			return scannedFingerPrints;
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_FINGERPRINT_SCANNING_ERROR.getErrorCode(),
					RegistrationExceptionConstants.REG_FINGERPRINT_SCANNING_ERROR.getErrorMessage());
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format(
							"Exception while scanning fingerprints details for user registration: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));

			throw new RegBaseUncheckedException(RegistrationConstants.USER_REG_FINGERPRINT_SCAN_EXP,
					String.format(
							"Exception while scanning fingerprints details for user registration: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));
		}
	}

	private double getIrisQualityScore() {
		try {
			LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Getting the quality score of previously captured iris");
			
			return irisCaptureController.getIrisBySelectedPane().findFirst().orElse(new IrisDetailsDTO())
					.getQualityScore();
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format(
							"Exception while getting the quality score of previously captured iris: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));

			throw new RegBaseUncheckedException(RegistrationConstants.USER_REG_FINGERPRINT_SCAN_EXP,
					String.format(
							"Exception while getting the quality score of previously captured iris: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));
		}
	}

}
