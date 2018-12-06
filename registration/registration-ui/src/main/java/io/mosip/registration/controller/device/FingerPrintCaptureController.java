package io.mosip.registration.controller.device;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.reg.RegistrationController;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.biometric.BiometricInfoDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.device.impl.FingerPrintCaptureServiceImpl;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * {@code FingerPrintCaptureController} is to capture and display the captured
 * fingerprints.
 * 
 * @author Mahesh Kumar
 * @since 1.0
 */
@Controller
public class FingerPrintCaptureController extends BaseController implements Initializable {

	/** The left hand slap threshold score. */
	@Value("${leftHand_Slap_Threshold_Score}")
	private double leftHandSlapThresholdScore;

	/** The right hand slap threshold score. */
	@Value("${rightHand_Slap_Threshold_Score}")
	private double rightHandSlapThresholdScore;

	/** The thumbs threshold score. */
	@Value("${thumbs_Threshold_Score}")
	private double thumbsThresholdScore;

	@Value("${num_of_Fingerprint_retries}")
	private double noOfRetriesThreshold;

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(FingerPrintCaptureController.class);

	/** The finger print scan controller. */
	@Autowired
	private FingerPrintScanController fingerPrintScanController;

	/** The finger print capture service impl. */
	@Autowired
	private FingerPrintCaptureServiceImpl fingerPrintCaptureServiceImpl;

	/** The registration controller. */
	@Autowired
	private RegistrationController registrationController;

	/** The finger print capture pane. */
	@FXML
	private AnchorPane fingerPrintCapturePane;

	/** The left hand palm pane. */
	@FXML
	private AnchorPane leftHandPalmPane;

	/** The right hand palm pane. */
	@FXML
	private AnchorPane rightHandPalmPane;

	/** The thumb pane. */
	@FXML
	private AnchorPane thumbPane;

	/** The left hand palm imageview. */
	@FXML
	private ImageView leftHandPalmImageview;

	/** The right hand palm imageview. */
	@FXML
	private ImageView rightHandPalmImageview;

	/** The thumb imageview. */
	@FXML
	private ImageView thumbImageview;

	/** The left slap quality score. */
	@FXML
	private Label leftSlapQualityScore;

	/** The right slap quality score. */
	@FXML
	private Label rightSlapQualityScore;

	/** The thumbs quality score. */
	@FXML
	private Label thumbsQualityScore;

	/** The left slap threshold score label. */
	@FXML
	private Label leftSlapThresholdScoreLbl;

	/** The right slap threshold score label. */
	@FXML
	private Label rightSlapThresholdScoreLbl;

	/** The thumbs threshold score label. */
	@FXML
	private Label thumbsThresholdScoreLbl;

	/** The selected pane. */
	private AnchorPane selectedPane = null;

	/** The scan btn. */
	@FXML
	private Button scanBtn;

	/**
	 * @return the leftHandPalmPane
	 */
	public AnchorPane getLeftHandPalmPane() {
		return leftHandPalmPane;
	}

	/**
	 * @param leftHandPalmPane the leftHandPalmPane to set
	 */
	public void setLeftHandPalmPane(AnchorPane leftHandPalmPane) {
		this.leftHandPalmPane = leftHandPalmPane;
	}

	/**
	 * @return the rightHandPalmPane
	 */
	public AnchorPane getRightHandPalmPane() {
		return rightHandPalmPane;
	}

	/**
	 * @param rightHandPalmPane the rightHandPalmPane to set
	 */
	public void setRightHandPalmPane(AnchorPane rightHandPalmPane) {
		this.rightHandPalmPane = rightHandPalmPane;
	}

	/**
	 * @return the thumbPane
	 */
	public AnchorPane getThumbPane() {
		return thumbPane;
	}

	/**
	 * @param thumbPane the thumbPane to set
	 */
	public void setThumbPane(AnchorPane thumbPane) {
		this.thumbPane = thumbPane;
	}

	/**
	 * @return the leftHandPalmImageview
	 */
	public ImageView getLeftHandPalmImageview() {
		return leftHandPalmImageview;
	}

	/**
	 * @param leftHandPalmImageview the leftHandPalmImageview to set
	 */
	public void setLeftHandPalmImageview(ImageView leftHandPalmImageview) {
		this.leftHandPalmImageview = leftHandPalmImageview;
	}

	/**
	 * @return the rightHandPalmImageview
	 */
	public ImageView getRightHandPalmImageview() {
		return rightHandPalmImageview;
	}

	/**
	 * @param rightHandPalmImageview the rightHandPalmImageview to set
	 */
	public void setRightHandPalmImageview(ImageView rightHandPalmImageview) {
		this.rightHandPalmImageview = rightHandPalmImageview;
	}

	/**
	 * @return the thumbImageview
	 */
	public ImageView getThumbImageview() {
		return thumbImageview;
	}

	/**
	 * @param thumbImageview the thumbImageview to set
	 */
	public void setThumbImageview(ImageView thumbImageview) {
		this.thumbImageview = thumbImageview;
	}

	/**
	 * @return the leftSlapQualityScore
	 */
	public Label getLeftSlapQualityScore() {
		return leftSlapQualityScore;
	}

	/**
	 * @param leftSlapQualityScore the leftSlapQualityScore to set
	 */
	public void setLeftSlapQualityScore(Label leftSlapQualityScore) {
		this.leftSlapQualityScore = leftSlapQualityScore;
	}

	/**
	 * @return the rightSlapQualityScore
	 */
	public Label getRightSlapQualityScore() {
		return rightSlapQualityScore;
	}

	/**
	 * @param rightSlapQualityScore the rightSlapQualityScore to set
	 */
	public void setRightSlapQualityScore(Label rightSlapQualityScore) {
		this.rightSlapQualityScore = rightSlapQualityScore;
	}

	/**
	 * @return the thumbsQualityScore
	 */
	public Label getThumbsQualityScore() {
		return thumbsQualityScore;
	}

	/**
	 * @param thumbsQualityScore the thumbsQualityScore to set
	 */
	public void setThumbsQualityScore(Label thumbsQualityScore) {
		this.thumbsQualityScore = thumbsQualityScore;
	}

	/**
	 * @return the registrationController
	 */
	public RegistrationController getRegistrationController() {
		return registrationController;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.debug(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
				"Loading of FingerprintCapture screen started");
		try {
			scanBtn.setDisable(true);

			EventHandler<Event> mouseClick = event -> {
				if (event.getSource() instanceof AnchorPane) {
					AnchorPane sourcePane = (AnchorPane) event.getSource();
					sourcePane.requestFocus();
					selectedPane = sourcePane;
					scanBtn.setDisable(false);
				}
			};

			// Add event handler object to mouse click event
			leftHandPalmPane.setOnMouseClicked(mouseClick);
			rightHandPalmPane.setOnMouseClicked(mouseClick);
			thumbPane.setOnMouseClicked(mouseClick);

			leftSlapThresholdScoreLbl
					.setText(String.valueOf(leftHandSlapThresholdScore) + RegistrationConstants.PERCENTAGE);
			rightSlapThresholdScoreLbl
					.setText(String.valueOf(rightHandSlapThresholdScore) + RegistrationConstants.PERCENTAGE);
			thumbsThresholdScoreLbl.setText(String.valueOf(thumbsThresholdScore) + RegistrationConstants.PERCENTAGE);

			RegistrationDTO registrationDTOContent = (RegistrationDTO) SessionContext.getInstance().getMapObject()
					.get(RegistrationConstants.REGISTRATION_DATA);
			if (null != registrationDTOContent) {
				registrationDTOContent.getBiometricDTO().getApplicantBiometricDTO().getFingerprintDetailsDTO()
						.forEach(item -> {
							if (item.getFingerType().equals(RegistrationConstants.LEFTPALM)) {
								leftHandPalmImageview
										.setImage(new Image(new ByteArrayInputStream(item.getFingerPrint())));
							} else if (item.getFingerType().equals(RegistrationConstants.RIGHTPALM)) {
								rightHandPalmImageview
										.setImage(new Image(new ByteArrayInputStream(item.getFingerPrint())));
							} else if (item.getFingerType().equals(RegistrationConstants.THUMBS)) {
								thumbImageview.setImage(new Image(new ByteArrayInputStream(item.getFingerPrint())));
							}
						});
			} else {
				leftHandPalmImageview.setImage(null);
				rightHandPalmImageview.setImage(null);
				thumbImageview.setImage(null);
			}
			LOGGER.debug(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Loading of FingerprintCapture screen ended");
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format(
							"%s -> Exception while initializing Fingerprint Capture page for user registration  %s",
							RegistrationConstants.USER_REG_FINGERPRINT_PAGE_LOAD_EXP, runtimeException.getMessage()));

			throw new RegBaseUncheckedException(RegistrationConstants.USER_REG_FINGERPRINT_PAGE_LOAD_EXP,
					String.format("Exception while initializing Fingerprint Capture page for user registration  %s",
							runtimeException.getMessage()));
		}
	}

	public void scan() {
		try {
			LOGGER.debug(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Opening pop-up screen to capture fingerprint for user registration");
			if (null != selectedPane) {

				selectedPane.requestFocus();
				Stage primaryStage = new Stage();
				primaryStage.initStyle(StageStyle.UNDECORATED);
				Parent ackRoot = BaseController
						.load(getClass().getResource(RegistrationConstants.USER_REGISTRATION_BIOMETRIC_CAPTURE_PAGE));
				fingerPrintScanController.init(selectedPane, primaryStage);
				primaryStage.setResizable(false);
				Scene scene = new Scene(ackRoot);
				ClassLoader loader = Thread.currentThread().getContextClassLoader();
				scene.getStylesheets().add(loader.getResource(RegistrationConstants.CSS_FILE_PATH).toExternalForm());
				primaryStage.setScene(scene);
				primaryStage.initModality(Modality.WINDOW_MODAL);
				primaryStage.initOwner(stage);
				primaryStage.show();
				scanBtn.setDisable(true);
			} else {
				generateAlert(RegistrationConstants.ALERT_INFORMATION,
						RegistrationConstants.FINGERPRINT_SELECTION_PANE_ALERT);
			}
			LOGGER.debug(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of fingersplaced ended");
		} catch (IOException ioException) {
			LOGGER.error(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, String.format(
					"%s -> Exception while Opening pop-up screen to capture fingerprint for user registration  %s -> %s",
					RegistrationConstants.USER_REG_FINGERPRINT_CAPTURE_POPUP_LOAD_EXP, ioException.getMessage(),
					ioException.getCause()));

			generateAlert(RegistrationConstants.ALERT_ERROR, "Unable to load Fingerprint Scan Pop-up Screen");
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, String.format(
					"%s -> Exception while Opening pop-up screen to capture fingerprint for user registration  %s",
					RegistrationConstants.USER_REG_FINGERPRINT_CAPTURE_POPUP_LOAD_EXP, runtimeException.getMessage()));

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.UNABLE_LOAD_FINGERPRINT_SCAN_POPUP);
		}
	}

	/**
	 * {@code saveBiometricDetails} is to check the deduplication of captured finger
	 * prints
	 */
	public void goToNextPage() {
		try {
			LOGGER.debug(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Navigating to Iris capture page for user registration started");

			if (validateFingerPrints()) {
				registrationController.toggleFingerprintCaptureVisibility(false);
				registrationController.toggleIrisCaptureVisibility(true);
			}
			LOGGER.debug(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Navigating to Iris capture page for user registration ended");
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format("%s -> Exception while navigating to Iris capture page for user registration  %s",
							RegistrationConstants.USER_REG_FINGERPRINT_CAPTURE_NEXT_SECTION_LOAD_EXP,
							runtimeException.getMessage()));

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.FINGERPRINT_NAVIGATE_NEXT_SECTION_ERROR);
		}
	}

	/**
	 * {@code saveBiometricDetails} is to check the deduplication of captured finger
	 * prints
	 */
	public void goToPreviousPage() {
		try {
			LOGGER.debug(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Navigating to Demographic capture page for user registration started");
			registrationController.getDemoGraphicTitlePane().setExpanded(true);
			LOGGER.debug(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Navigating to Demographic capture page for user registration ended");
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format(
							"%s -> Exception while navigating to Demographic capture page for user registration  %s",
							RegistrationConstants.USER_REG_FINGERPRINT_CAPTURE_PREV_SECTION_LOAD_EXP,
							runtimeException.getMessage()));

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.FINGERPRINT_NAVIGATE_PREVIOUS_SECTION_ERROR);
		}
	}

	/**
	 * Validating finger prints.
	 *
	 * @return true, if successful
	 */
	private boolean validateFingerPrints() {
		try {
			LOGGER.debug(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Validating Fingerprints captured started");

			List<FingerprintDetailsDTO> segmentedFingerprintISOTemplateDTOs = new ArrayList<>();
			boolean isValid = false;
			boolean isleftHandSlapCaptured = false;
			boolean isrightHandSlapCaptured = false;
			boolean isthumbsCaptured = false;

			BiometricInfoDTO applicantBiometricInfoDTO = registrationController.getRegistrationDtoContent()
					.getBiometricDTO().getApplicantBiometricDTO();

			List<FingerprintDetailsDTO> fingerprintDetailsDTOs = applicantBiometricInfoDTO.getFingerprintDetailsDTO();
			List<FingerprintDetailsDTO> segmentedFingerprintDetailsDTOs = applicantBiometricInfoDTO
					.getSegmentedFingerprints();
			for (FingerprintDetailsDTO fingerprintDetailsDTO : fingerprintDetailsDTOs) {
				if (validateQualityScore(fingerprintDetailsDTO)) {
					if (fingerprintDetailsDTO.getFingerType().equalsIgnoreCase(RegistrationConstants.LEFTPALM)) {
						isleftHandSlapCaptured = true;
					} else if (fingerprintDetailsDTO.getFingerType()
							.equalsIgnoreCase(RegistrationConstants.RIGHTPALM)) {
						isrightHandSlapCaptured = true;
					} else if (fingerprintDetailsDTO.getFingerType().equalsIgnoreCase(RegistrationConstants.THUMBS)) {
						isthumbsCaptured = true;
					}
				}
			}

			for (FingerprintDetailsDTO fingerprintDetailsDTO : segmentedFingerprintDetailsDTOs) {
				if (fingerprintDetailsDTO.getFingerType().contains(RegistrationConstants.ISO_FILE_NAME)) {
					segmentedFingerprintISOTemplateDTOs.add(fingerprintDetailsDTO);
				}
			}

			if (isleftHandSlapCaptured && isrightHandSlapCaptured && isthumbsCaptured) {
				if (!fingerPrintCaptureServiceImpl.validateFingerprint(segmentedFingerprintISOTemplateDTOs)) {
					isValid = true;
				} else {
					generateAlert(RegistrationConstants.ALERT_INFORMATION,
							RegistrationConstants.FINGERPRINT_DUPLICATION_ALERT);
				}
			} else {
				generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationConstants.FINGERPRINT_SCAN_ALERT);
			}
			LOGGER.debug(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Validating Fingerprints captured ended");
			return isValid;
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.USER_REG_FINGERPRINT_VALIDATION_EXP,
					String.format("Exception while validating the captured fingerprints of individual: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));
		}
	}

	/**
	 * Validating quality score of captured fingerprints.
	 *
	 * @param fingerprintDetailsDTO the fingerprint details DTO
	 * @return true, if successful
	 */
	private boolean validateQualityScore(FingerprintDetailsDTO fingerprintDetailsDTO) {
		try {
			LOGGER.debug(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Validating quality score of captured fingerprints started");
			if (fingerprintDetailsDTO.getFingerType().equals(RegistrationConstants.LEFTPALM)) {
				return fingerprintDetailsDTO.getQualityScore() >= leftHandSlapThresholdScore
						|| (fingerprintDetailsDTO.getQualityScore() < leftHandSlapThresholdScore
								&& fingerprintDetailsDTO.getNumRetry() == noOfRetriesThreshold);
			} else if (fingerprintDetailsDTO.getFingerType().equals(RegistrationConstants.RIGHTPALM)) {
				return fingerprintDetailsDTO.getQualityScore() >= rightHandSlapThresholdScore
						|| (fingerprintDetailsDTO.getQualityScore() < rightHandSlapThresholdScore
								&& fingerprintDetailsDTO.getNumRetry() == noOfRetriesThreshold);
			} else if (fingerprintDetailsDTO.getFingerType().equals(RegistrationConstants.THUMBS)) {
				return fingerprintDetailsDTO.getQualityScore() >= thumbsThresholdScore
						|| (fingerprintDetailsDTO.getQualityScore() < thumbsThresholdScore
								&& fingerprintDetailsDTO.getNumRetry() == noOfRetriesThreshold);
			}
			LOGGER.debug(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Validating quality score of captured fingerprints ended");
			return false;
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.USER_REG_FINGERPRINT_SCORE_VALIDATION_EXP,
					String.format(
							"Exception while validating the quality score of captured Fingerprints: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));
		}
	}
}
