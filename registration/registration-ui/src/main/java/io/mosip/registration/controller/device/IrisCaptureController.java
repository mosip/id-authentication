package io.mosip.registration.controller.device;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
import io.mosip.registration.device.iris.IrisFacade;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.biometric.BiometricExceptionDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;

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
	@Autowired
	private IrisFacade irisFacade;

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
			String irisThreshold = getValueFromApplicationMap(RegistrationConstants.IRIS_THRESHOLD);
			leftIrisThreshold.setText(irisThreshold.concat(RegistrationConstants.PERCENTAGE));
			rightIrisThreshold.setText(irisThreshold.concat(RegistrationConstants.PERCENTAGE));

			// Disable Scan button
			scanIris.setDisable(true);

			// Display the Captured Iris
			if (getRegistrationDTOFromSession() != null) {
				for (IrisDetailsDTO capturedIris : getIrises()) {
					if (capturedIris.getIrisType().contains(RegistrationConstants.LEFT)) {
						leftIrisImage.setImage(convertBytesToImage(capturedIris.getIris()));
						leftIrisQualityScore.setText(getQualityScoreAsString(capturedIris.getQualityScore()));
					} else if (capturedIris.getIrisType().contains(RegistrationConstants.RIGHT)) {
						rightIrisImage.setImage(convertBytesToImage(capturedIris.getIris()));
						rightIrisQualityScore.setText(getQualityScoreAsString(capturedIris.getQualityScore()));
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
	 * This event handler will be invoked when left iris or right iris {@link Pane}
	 * is clicked.
	 * 
	 * @param mouseEvent
	 *            the triggered {@link MouseEvent} object
	 */
	@FXML
	private void enableScan(MouseEvent mouseEvent) {
		try {
			LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Enabling scan button for user registration");

			Pane sourcePane = (Pane) mouseEvent.getSource();
			sourcePane.requestFocus();
			selectedIris = sourcePane;
			scanIris.setDisable(true);

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
			// 3. If iris is not forced captured
			// 4. If iris is an exception iris
			if (irisDetailsDTO == null || isExceptionIris
					|| (Double.compare(irisDetailsDTO.getQualityScore(),
							Double.parseDouble(getValueFromApplicationMap(RegistrationConstants.IRIS_THRESHOLD))) < 0)
					|| irisDetailsDTO.isForceCaptured()) {
				scanIris.setDisable(false);
			}

			LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Enabling scan button for user registration completed");
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format("%s -> Exception while enabling scan button for user registration  %s %s",
							RegistrationConstants.USER_REG_IRIS_CAPTURE_POPUP_LOAD_EXP, runtimeException.getMessage(),
							runtimeException.getStackTrace()));

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.UNABLE_LOAD_IRIS_SCAN_POPUP);
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

			IrisDetailsDTO irisDetailsDTO = getIrisBySelectedPane().findFirst().orElse(null);

			if (irisDetailsDTO == null || (irisDetailsDTO.getNumOfIrisRetry() < Integer
					.parseInt(getValueFromApplicationMap(RegistrationConstants.IRIS_RETRY_COUNT)))) {
				scanController.init(this, "Iris");
			} else {
				generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.IRIS_SCAN_RETRIES_EXCEEDED);
			}

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

			Optional<IrisDetailsDTO> captiredIrisDetailsDTO = getIrisBySelectedPane().findFirst();

			IrisDetailsDTO irisDetailsDTO = null;
			if (!captiredIrisDetailsDTO.isPresent()) {
				irisDetailsDTO = new IrisDetailsDTO();
				getIrises().add(irisDetailsDTO);
			} else {
				irisDetailsDTO = captiredIrisDetailsDTO.get();
				irisDetailsDTO.setNumOfIrisRetry(irisDetailsDTO.getNumOfIrisRetry() + 1);
			}

			String irisType = StringUtils.containsIgnoreCase(selectedIris.getId(), RegistrationConstants.LEFT)
					? RegistrationConstants.LEFT
					: RegistrationConstants.RIGHT;

			irisFacade.getIrisImageAsDTO(irisDetailsDTO, irisType.concat(RegistrationConstants.EYE));

			// Display the Scanned Iris Image in the Scan pop-up screen
			scanController.getScanImage().setImage(convertBytesToImage(irisDetailsDTO.getIris()));

			generateAlert(RegistrationConstants.ALERT_INFORMATION, "Iris captured successfully");

			if (irisType.equals(RegistrationConstants.LEFT)) {
				leftIrisImage.setImage(convertBytesToImage(irisDetailsDTO.getIris()));
				leftIrisQualityScore.setText(getQualityScoreAsString(irisDetailsDTO.getQualityScore()));
			} else {
				rightIrisImage.setImage(convertBytesToImage(irisDetailsDTO.getIris()));
				rightIrisQualityScore.setText(getQualityScoreAsString(irisDetailsDTO.getQualityScore()));
			}

			popupStage.close();

			LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of iris details for user registration completed");
		} catch (RegBaseCheckedException regBaseCheckedException) {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.IRIS_SCANNING_ERROR);
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, String.format(
					"%s Exception while getting the scanned iris details for user registration: %s caused by %s",
					RegistrationConstants.USER_REG_IRIS_SAVE_EXP, runtimeException.getMessage(),
					runtimeException.getCause()));

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.IRIS_SCANNING_ERROR);
		} finally {
			selectedIris.requestFocus();
		}
	}

	private boolean validateIrisLocalDedup() {
		// TODO: Implement Local Dedup for Iris
		return true;
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

			if (validateIris() && validateIrisLocalDedup()) {
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

			if (validateIris() && validateIrisLocalDedup()) {
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
				if (validateIrisCapture(irisDetailsDTO)) {
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

	private boolean validateIrisCapture(IrisDetailsDTO irisDetailsDTO) {
		try {
			LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Validating the quality score of the captured iris");

			// Get Configured Threshold and Number of Retries from properties file
			double irisThreshold = Double.parseDouble(getValueFromApplicationMap(RegistrationConstants.IRIS_THRESHOLD));
			int numOfRetries = Integer.parseInt(getValueFromApplicationMap(RegistrationConstants.IRIS_RETRY_COUNT));

			LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Validating the quality score of the captured iris completed");

			return irisDetailsDTO.getQualityScore() >= irisThreshold
					|| (Double.compare(irisDetailsDTO.getQualityScore(), irisThreshold) < 0
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

	private String getQualityScoreAsString(double qualityScore) {
		return String.valueOf(Math.round(qualityScore)).concat(RegistrationConstants.PERCENTAGE);
	}

	private String getValueFromApplicationMap(String key) {
		return (String) applicationContext.getApplicationMap().get(key);
	}

}
