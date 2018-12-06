package io.mosip.registration.controller.device;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.reg.RegistrationController;
import io.mosip.registration.dto.biometric.BiometricExceptionDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
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
	private FingerPrintScanController fingerPrintScanController;

	private Pane selectedIris;

	/**
	 * Gets the selected iris.
	 *
	 * @return the selected iris
	 */
	public Pane getSelectedIris() {
		return selectedIris;
	}

	/**
	 * @return the leftIrisImage
	 */
	public ImageView getLeftIrisImage() {
		return leftIrisImage;
	}

	/**
	 * @return the rightIrisImage
	 */
	public ImageView getRightIrisImage() {
		return rightIrisImage;
	}

	/**
	 * @return the registrationController
	 */
	public RegistrationController getRegistrationController() {
		return registrationController;
	}

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
			if (registrationController.getRegistrationDtoContent() != null) {
				for (IrisDetailsDTO capturedIris : getIrises()) {
					if (capturedIris.getIrisType().contains(RegistrationConstants.LEFT)) {
						getLeftIrisImage().setImage(convertBytesToImage(capturedIris.getIris()));
					} else if (capturedIris.getIrisType().contains(RegistrationConstants.RIGHT)) {
						getRightIrisImage().setImage(convertBytesToImage(capturedIris.getIris()));
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

			Stage popupStage = new Stage();
			popupStage.initStyle(StageStyle.UNDECORATED);
			Parent biometricScanPopup = BaseController
					.load(getClass().getResource(RegistrationConstants.SCAN_PAGE));
			fingerPrintScanController.setPopupTitle("Iris");
			fingerPrintScanController.setPrimarystage(popupStage);
			popupStage.setResizable(false);
			Scene scene = new Scene(biometricScanPopup);
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			scene.getStylesheets().add(loader.getResource(RegistrationConstants.CSS_FILE_PATH).toExternalForm());
			popupStage.setScene(scene);
			popupStage.initModality(Modality.WINDOW_MODAL);
			popupStage.initOwner(initializeParentRoot.getStage());
			popupStage.show();

			// Disable the scan button
			scanIris.setDisable(true);

			LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Opening pop-up screen to capture Iris for user registration completed");
		} catch (IOException ioException) {
			LOGGER.error(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, String.format(
					"%s -> Exception while Opening pop-up screen to capture Iris for user registration  %s -> %s",
					RegistrationConstants.USER_REG_IRIS_CAPTURE_POPUP_LOAD_EXP, ioException.getMessage(),
					ioException.getCause()));

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.UNABLE_LOAD_IRIS_SCAN_POPUP);
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format(
							"%s -> Exception while Opening pop-up screen to capture Iris for user registration  %s",
							RegistrationConstants.USER_REG_IRIS_CAPTURE_POPUP_LOAD_EXP, runtimeException.getMessage()));

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.UNABLE_LOAD_IRIS_SCAN_POPUP);
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
			LOGGER.error(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format("%s -> Exception while navigating to Fingerprint capture page for user registration  %s",
							RegistrationConstants.USER_REG_IRIS_CAPTURE_PREV_SECTION_LOAD_EXP,
							runtimeException.getMessage()));

			generateAlert(RegistrationConstants.ALERT_ERROR,
					RegistrationConstants.IRIS_NAVIGATE_PREVIOUS_SECTION_ERROR);
		}
	}

	/**
	 * Sets the quality score for Left Iris
	 * 
	 * @param qualityScore
	 *            the leftIrisQualityScore to set
	 */
	public void setLeftIrisQualityScore(double qualityScore) {
		this.leftIrisQualityScore.setText(String.valueOf(qualityScore));
	}

	/**
	 * Sets the quality score for Right Iris
	 * 
	 * @param qualityScore
	 *            the rightIrisQualityScore to set
	 */
	public void setRightIrisQualityScore(double qualityScore) {
		this.rightIrisQualityScore.setText(String.valueOf(qualityScore));
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

	public List<IrisDetailsDTO> getIrises() {
		return registrationController.getRegistrationDtoContent().getBiometricDTO().getApplicantBiometricDTO()
				.getIrisDetailsDTO();
	}

	private List<BiometricExceptionDTO> getIrisExceptions() {
		return registrationController.getRegistrationDtoContent().getBiometricDTO().getApplicantBiometricDTO()
				.getIrisBiometricExceptionDTO();
	}

	public Stream<IrisDetailsDTO> getIrisBySelectedPane() {
		return getIrises().stream()
				.filter(iris -> iris.getIrisType()
						.contains(StringUtils.containsIgnoreCase(getSelectedIris().getId(), RegistrationConstants.LEFT)
								? RegistrationConstants.LEFT
								: RegistrationConstants.RIGHT));
	}

}
