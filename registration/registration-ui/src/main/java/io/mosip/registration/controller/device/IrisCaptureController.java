package io.mosip.registration.controller.device;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.reg.RegistrationController;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.biometric.BiometricExceptionDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_IRIS_CAPTURE_CONTROLLER;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

/**
 * This is the {@link Controller} class for capturing the Iris image
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Controller
public class IrisCaptureController extends BaseController {

	private static final Logger LOGGER = AppConfig.getLogger(IrisCaptureController.class);

	@FXML
	private ImageView leftIrisImage;
	@FXML
	private Label rightIrisThreshold;
	@FXML
	private Label leftIrisQualityScore;
	@FXML
	private Pane rightIrisPane;
	@FXML
	private Pane leftIrisPane;
	@FXML
	private Label leftIrisThreshold;
	@FXML
	private Label rightIrisQualityScore;
	@FXML
	private ImageView rightIrisImage;
	@FXML
	private Button scanIris;

	@Autowired
	private RegistrationController registrationController;
	@Autowired
	private ScanController scanController;

	private Pane selectedIris;

	/**
	 * This method is invoked when IrisCapture FXML page is loaded. This method
	 * initializes the Iris Capture page.
	 */
	@FXML
	public void initialize() {

		try {
			LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Initializing Iris Capture page for user registration");

			// Set Threshold
			leftIrisThreshold.setText(AppConfig.getApplicationProperty(RegistrationConstants.IRIS_THRESHOLD));
			rightIrisThreshold.setText(AppConfig.getApplicationProperty(RegistrationConstants.IRIS_THRESHOLD));

			// Disable Scan button
			scanIris.setDisable(true);

			// Create EventHandler object for Mouse Click
			EventHandler<Event> mouseClick = event -> {
				if (event.getSource() instanceof Pane) {
					Pane sourcePane = (Pane) event.getSource();
					sourcePane.requestFocus();
					selectedIris = sourcePane;

					// Get the Iris from RegistrationDTO based on selected Iris Pane
					IrisDetailsDTO irisDetailsDTO = getIrisBySelectedPane().findFirst().orElse(null);

					boolean isExceptionIris = getIrisExceptions().stream()
							.anyMatch(exceptionIris -> StringUtils.containsIgnoreCase(exceptionIris.getBiometricType(),
									StringUtils.containsIgnoreCase(selectedIris.getId(), RegistrationConstants.LEFT)
											? RegistrationConstants.LEFT
											: RegistrationConstants.RIGHT));

					// Enable the scan button, if any of the following satisfies
					// 1. If Iris was not scanned
					// 2. Quality score of the scanned image is less than threshold
					// 3. If number of retries attempted is not same as configured number of retries
					// attempt
					// 4. If iris is not forced captured
					// 5. If iris is an exception iris
					if (irisDetailsDTO == null || isExceptionIris || !validateQualityScore(irisDetailsDTO)) {
						scanIris.setDisable(false);
						if (irisDetailsDTO != null) {
							irisDetailsDTO.setNumOfIrisRetry(irisDetailsDTO.getNumOfIrisRetry() + 1);
						}
					}
				}
			};

			// Add event handler object to mouse click event
			rightIrisPane.setOnMouseClicked(mouseClick);
			leftIrisPane.setOnMouseClicked(mouseClick);

			// Display the Captured Iris
			if (getRegistrationDTOFromSession() != null) {
				for (IrisDetailsDTO capturedIris : getIrises()) {
					if (capturedIris.getIrisType().contains(RegistrationConstants.LEFT)) {
						leftIrisImage.setImage(convertBytesToImage(capturedIris.getIris()));
						leftIrisQualityScore.setText(String.valueOf(capturedIris.getQualityScore()));
					} else if (capturedIris.getIrisType().contains(RegistrationConstants.RIGHT)) {
						rightIrisImage.setImage(convertBytesToImage(capturedIris.getIris()));
						rightIrisQualityScore.setText(String.valueOf(capturedIris.getQualityScore()));
					}
				}
			}

			LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Initializing Iris Capture page for user registration completed");
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format("%s -> Exception while initializing Iris Capture page for user registration  %s",
							RegistrationConstants.USER_REG_IRIS_CAPTURE_PAGE_LOAD_EXP, runtimeException.getMessage()));

			throw new RegBaseUncheckedException(RegistrationConstants.USER_REG_IRIS_CAPTURE_PAGE_LOAD_EXP,
					String.format("Exception while initializing Iris Capture page for user registration  %s",
							runtimeException.getMessage()));
		}

	}

	/**
	 * This method displays the Biometric Scan pop-up window. This method will be
	 * invoked when Scan button is clicked.
	 */
	@FXML
	private void scan() {
		try {
			LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Opening pop-up screen to capture Iris for user registration");

			scanController.init(this, "Iris");

			// Disable the scan button
			scanIris.setDisable(true);

			LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Opening pop-up screen to capture Iris for user registration completed");
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format(
							"%s -> Exception while Opening pop-up screen to capture Iris for user registration  %s",
							RegistrationConstants.USER_REG_IRIS_CAPTURE_POPUP_LOAD_EXP, runtimeException.getMessage()));

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.UNABLE_LOAD_IRIS_SCAN_POPUP);
		}
	}

	@Override
	public void scan(Stage popupStage) {
		try {
			LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of iris details for user registration");

			Map<String, Object> scannedIrisMap = getIrisScannedImage();

			byte[] irisImageBytes = (byte[]) scannedIrisMap.get(RegistrationConstants.IMAGE_BYTE_ARRAY_KEY);

			Image scannedIrisImage = convertBytesToImage(irisImageBytes);

			double qualityScore = (double) scannedIrisMap.get(RegistrationConstants.IMAGE_SCORE_KEY);

			if (validateIrisLocalDedup(irisImageBytes)) {
				// Display the Scanned Iris Image in the Scan pop-up screen
				scanController.getScanImage().setImage(scannedIrisImage);

				generateAlert(RegistrationConstants.ALERT_INFORMATION, "Iris captured successfully");

				if (getIrisQualityScore() < qualityScore) {
					// Display the Scanned Iris Image and its corresponding quality score in the
					// Iris Biometric Screen
					String irisType;
					if (StringUtils.containsIgnoreCase(selectedIris.getId(), RegistrationConstants.LEFT)) {
						leftIrisImage.setImage(scannedIrisImage);
						leftIrisQualityScore.setText(String.valueOf(qualityScore));
						irisType = RegistrationConstants.LEFT.concat(RegistrationConstants.EYE);
					} else {
						rightIrisImage.setImage(scannedIrisImage);
						rightIrisQualityScore.setText(String.valueOf(qualityScore));
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

					// Remove the previously captured Iris image
					getIrises().removeIf(iris -> iris.getIrisType().equals(irisType));

					// Add the captured iris to RegistrationDTO
					getIrises().add(irisDetailsDTO);
				}

				popupStage.close();
			}

			LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of iris details for user registration completed");
		} catch (RegBaseCheckedException regBaseCheckedException) {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.IRIS_SCANNING_ERROR);
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format(
							"%s Exception while getting the scanned iris details for user registration: %s caused by %s",
							RegistrationConstants.USER_REG_IRIS_SAVE_EXP, runtimeException.getMessage(), runtimeException.getCause()));

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.IRIS_SCANNING_ERROR);
		} finally {
			selectedIris.requestFocus();
		}
	}

	private Map<String, Object> getIrisScannedImage() throws RegBaseCheckedException {
		try {
			LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of iris details for user registration");

			InputStream inputStream = this.getClass().getResourceAsStream("/images/scanned-iris.png");
			BufferedImage bufferedImage = ImageIO.read(inputStream);
			
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, RegistrationConstants.IMAGE_FORMAT, byteArrayOutputStream);

			byte[] scannedIrisBytes = byteArrayOutputStream.toByteArray();

			// Add image format, image and quality score in bytes array to map
			Map<String, Object> scannedIris = new HashMap<>();
			scannedIris.put(RegistrationConstants.IMAGE_FORMAT_KEY, "png");
			scannedIris.put(RegistrationConstants.IMAGE_BYTE_ARRAY_KEY, scannedIrisBytes);
			scannedIris.put(RegistrationConstants.IMAGE_SCORE_KEY, 90.5);

			LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of iris details for user registration completed");

			return scannedIris;
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(RegistrationExceptionConstants.REG_IRIS_SCANNING_ERROR.getErrorCode(),
					RegistrationExceptionConstants.REG_IRIS_SCANNING_ERROR.getErrorMessage());
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format("%s Exception while scanning iris details for user registration: %s caused by %s",
							RegistrationConstants.USER_REG_IRIS_SCAN_EXP, runtimeException.getMessage(), runtimeException.getCause()));

			throw new RegBaseUncheckedException(RegistrationConstants.USER_REG_IRIS_SCAN_EXP,
					String.format("Exception while scanning iris details for user registration: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));
		}
	}

	private boolean validateIrisLocalDedup(byte[] scannedIrisImage) {
		// TODO: Implement Local Dedup for Iris
		return true;
	}

	private double getIrisQualityScore() {
		try {
			LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Getting the quality score of previously captured iris");

			return getIrisBySelectedPane().findFirst().orElse(new IrisDetailsDTO()).getQualityScore();
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format(
							"%s Exception while getting the quality score of previously captured iris: %s caused by %s",
							RegistrationConstants.USER_REG_GET_IRIS_QUALITY_SCORE_EXP, runtimeException.getMessage(), runtimeException.getCause()));

			throw new RegBaseUncheckedException(RegistrationConstants.USER_REG_GET_IRIS_QUALITY_SCORE_EXP,
					String.format(
							"Exception while getting the quality score of previously captured iris: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));
		}
	}

	/**
	 * This method will be invoked when Next button is clicked. The next section
	 * will be displayed.
	 */
	@FXML
	private void nextSection() {
		try {
			LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Navigating to Photo capture page for user registration");

			if (validateIris()) {
				registrationController.toggleIrisCaptureVisibility(false);
				registrationController.togglePhotoCaptureVisibility(true);
			}

			LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Navigating to Photo capture page for user registration completed");
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format("%s -> Exception while navigating to Photo capture page for user registration  %s",
							RegistrationConstants.USER_REG_IRIS_CAPTURE_NEXT_SECTION_LOAD_EXP,
							runtimeException.getMessage()));

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.IRIS_NAVIGATE_NEXT_SECTION_ERROR);
		}
	}

	/**
	 * This method will be invoked when Previous button is clicked. The previous
	 * section will be displayed.
	 */
	@FXML
	private void previousSection() {
		try {
			LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Navigating to Fingerprint capture page for user registration");

			if (validateIris()) {
				registrationController.toggleIrisCaptureVisibility(false);
				registrationController.toggleFingerprintCaptureVisibility(true);
			}

			LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Navigating to Fingerprint capture page for user registration completed");
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, String.format(
					"%s -> Exception while navigating to Fingerprint capture page for user registration  %s",
					RegistrationConstants.USER_REG_IRIS_CAPTURE_PREV_SECTION_LOAD_EXP, runtimeException.getMessage()));

			generateAlert(RegistrationConstants.ALERT_ERROR,
					RegistrationConstants.IRIS_NAVIGATE_PREVIOUS_SECTION_ERROR);
		}
	}

	private boolean validateIris() {
		try {
			LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Validating the captured irises of individual");

			boolean isValid = false;
			boolean isLeftEyeCaptured = false;
			boolean isRightEyeCaptured = false;

			for (IrisDetailsDTO irisDetailsDTO : getIrises()) {
				if (validateQualityScore(irisDetailsDTO)) {
					if (irisDetailsDTO.getIrisType()
							.equalsIgnoreCase(RegistrationConstants.LEFT.concat(RegistrationConstants.EYE))) {
						isLeftEyeCaptured = true;
					} else if (irisDetailsDTO.getIrisType()
							.equalsIgnoreCase(RegistrationConstants.RIGHT.concat(RegistrationConstants.EYE))) {
						isRightEyeCaptured = true;
					}
				}
			}

			for (BiometricExceptionDTO exceptionIris : getIrisExceptions()) {
				if (exceptionIris.getMissingBiometric()
						.equalsIgnoreCase(RegistrationConstants.LEFT.concat(RegistrationConstants.EYE))) {
					isLeftEyeCaptured = true;
				} else if (exceptionIris.getMissingBiometric()
						.equalsIgnoreCase(RegistrationConstants.RIGHT.concat(RegistrationConstants.EYE))) {
					isRightEyeCaptured = true;
				}
			}

			if (isLeftEyeCaptured && isRightEyeCaptured) {
				isValid = true;
			} else {
				generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.IRIS_VALIDATION_ERROR);
			}

			LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Validating the captured irises of individual completed");

			return isValid;
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.USER_REG_IRIS_VALIDATION_EXP,
					String.format("Exception while validating the captured irises of individual: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));
		}
	}

	private boolean validateQualityScore(IrisDetailsDTO irisDetailsDTO) {
		try {
			LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Validating the quality score of the captured iris");

			// Get Configured Threshold and Number of Retries from properties file
			double irisThreshold = Double
					.parseDouble(AppConfig.getApplicationProperty(RegistrationConstants.IRIS_THRESHOLD));
			int numOfRetries = Integer
					.parseInt(AppConfig.getApplicationProperty(RegistrationConstants.IRIS_RETRY_COUNT));
			LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Validating the quality score of the captured iris completed");

			return irisDetailsDTO.getQualityScore() >= irisThreshold
					|| (irisDetailsDTO.getQualityScore() < irisThreshold
							&& irisDetailsDTO.getNumOfIrisRetry() == numOfRetries)
					|| irisDetailsDTO.isForceCaptured();
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.USER_REG_IRIS_SCORE_VALIDATION_EXP,
					String.format("Exception while validating the quality score of captured iris: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));
		}
	}

	private List<IrisDetailsDTO> getIrises() {
		return getRegistrationDTOFromSession().getBiometricDTO().getApplicantBiometricDTO().getIrisDetailsDTO();
	}

	private List<BiometricExceptionDTO> getIrisExceptions() {
		return getRegistrationDTOFromSession().getBiometricDTO().getApplicantBiometricDTO()
				.getIrisBiometricExceptionDTO();
	}

	private Stream<IrisDetailsDTO> getIrisBySelectedPane() {
		return getIrises().stream()
				.filter(iris -> iris.getIrisType()
						.contains(StringUtils.containsIgnoreCase(selectedIris.getId(), RegistrationConstants.LEFT)
								? RegistrationConstants.LEFT
								: RegistrationConstants.RIGHT));
	}

	private RegistrationDTO getRegistrationDTOFromSession() {
		return (RegistrationDTO) SessionContext.getInstance().getMapObject()
				.get(RegistrationConstants.REGISTRATION_DATA);
	}

}
