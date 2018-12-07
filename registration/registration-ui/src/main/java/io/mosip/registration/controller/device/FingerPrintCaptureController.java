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

import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.device.impl.FingerPrintCaptureServiceImpl;
import io.mosip.registration.util.biometric.FingerprintFacade;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import javafx.stage.Stage;

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

	/** The finger print capture service impl. */
	@Autowired
	private FingerPrintCaptureServiceImpl fingerPrintCaptureServiceImpl;

	/** The registration controller. */
	@Autowired
	private RegistrationController registrationController;

	@Autowired
	private ScanController scanController;

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

	/** The selected pane. */
	@Autowired
	private FingerprintFacade fingerPrintFacade = null;

	/** The scan btn. */
	@FXML
	private Button scanBtn;

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

			scanController.init(this, "Fingerprint");

			scanBtn.setDisable(true);

			LOGGER.debug(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of fingersplaced ended");

		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, String.format(
					"%s -> Exception while Opening pop-up screen to capture fingerprint for user registration  %s",
					RegistrationConstants.USER_REG_FINGERPRINT_CAPTURE_POPUP_LOAD_EXP, runtimeException.getMessage()));

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.UNABLE_LOAD_FINGERPRINT_SCAN_POPUP);
		}
	}

	@Override
	public void scan(Stage popupStage) {

		try {
			RegistrationDTO registrationDTO = (RegistrationDTO) SessionContext.getInstance().getMapObject()
					.get(RegistrationConstants.REGISTRATION_DATA);
			FingerprintDetailsDTO detailsDTO = null;

			List<FingerprintDetailsDTO> fingerprintDetailsDTOs = registrationDTO.getBiometricDTO()
					.getApplicantBiometricDTO().getFingerprintDetailsDTO();

			if (fingerprintDetailsDTOs == null || fingerprintDetailsDTOs.isEmpty()) {
				fingerprintDetailsDTOs = new ArrayList<>(3);
				registrationDTO.getBiometricDTO().getApplicantBiometricDTO()
						.setFingerprintDetailsDTO(fingerprintDetailsDTOs);
			}

			if (selectedPane.getId() == leftHandPalmPane.getId()) {

				if (fingerprintDetailsDTOs != null) {

					for (FingerprintDetailsDTO fingerprintDetailsDTO : fingerprintDetailsDTOs) {
						if (fingerprintDetailsDTO.getFingerType().equals(RegistrationConstants.LEFTPALM)) {
							detailsDTO = fingerprintDetailsDTO;
							break;
						}
					}
					if (detailsDTO == null) {
						detailsDTO = new FingerprintDetailsDTO();
						fingerprintDetailsDTOs.add(detailsDTO);
					}
				}
				fingerPrintFacade.getFingerPrintImageAsDTO(detailsDTO, RegistrationConstants.LEFTPALM);

				fingerPrintFacade.segmentFingerPrintImage(detailsDTO,
						RegistrationConstants.LEFTHAND_SEGMENTED_FINGERPRINT_PATH);

				scanController.getScanImage().setImage(convertBytesToImage(detailsDTO.getFingerPrint()));

				leftHandPalmImageview.setImage(convertBytesToImage(detailsDTO.getFingerPrint()));
				leftSlapQualityScore
						.setText(String.valueOf(detailsDTO.getQualityScore()) + RegistrationConstants.PERCENTAGE);

			} else if (selectedPane.getId() == rightHandPalmPane.getId()) {

				if (fingerprintDetailsDTOs != null) {

					for (FingerprintDetailsDTO fingerprintDetailsDTO : fingerprintDetailsDTOs) {
						if (fingerprintDetailsDTO.getFingerType().equals(RegistrationConstants.RIGHTPALM)) {
							detailsDTO = fingerprintDetailsDTO;
							break;
						}
					}
					if (detailsDTO == null) {
						detailsDTO = new FingerprintDetailsDTO();
						fingerprintDetailsDTOs.add(detailsDTO);
					}
				}
				fingerPrintFacade.getFingerPrintImageAsDTO(detailsDTO, RegistrationConstants.RIGHTPALM);

				fingerPrintFacade.segmentFingerPrintImage(detailsDTO,
						RegistrationConstants.RIGHTHAND_SEGMENTED_FINGERPRINT_PATH);
				scanController.getScanImage().setImage(convertBytesToImage(detailsDTO.getFingerPrint()));

				rightHandPalmImageview.setImage(convertBytesToImage(detailsDTO.getFingerPrint()));
				rightSlapQualityScore
						.setText(String.valueOf(detailsDTO.getQualityScore()) + RegistrationConstants.PERCENTAGE);

			} else if (selectedPane.getId() == thumbPane.getId()) {
				if (fingerprintDetailsDTOs != null) {

					for (FingerprintDetailsDTO fingerprintDetailsDTO : fingerprintDetailsDTOs) {
						if (fingerprintDetailsDTO.getFingerType().equals(RegistrationConstants.THUMBS)) {
							detailsDTO = fingerprintDetailsDTO;
							break;
						}
					}
					if (detailsDTO == null) {
						detailsDTO = new FingerprintDetailsDTO();
						fingerprintDetailsDTOs.add(detailsDTO);
					}
				}
				fingerPrintFacade.getFingerPrintImageAsDTO(detailsDTO, RegistrationConstants.THUMBS);

				fingerPrintFacade.segmentFingerPrintImage(detailsDTO,
						RegistrationConstants.THUMB_SEGMENTED_FINGERPRINT_PATH);
				scanController.getScanImage().setImage(convertBytesToImage(detailsDTO.getFingerPrint()));

				thumbImageview.setImage(convertBytesToImage(detailsDTO.getFingerPrint()));
				thumbsQualityScore
						.setText(String.valueOf(detailsDTO.getQualityScore()) + RegistrationConstants.PERCENTAGE);
			}

			generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationConstants.FP_CAPTURE_SUCCESS);
			popupStage.close();

		} catch (IOException ioException) {

			LOGGER.error(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format(
							"Exception while getting the scanned Finger details for user registration: %s caused by %s",
							ioException.getMessage(), ioException.getCause()));

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.FINGERPRINT_SCANNING_ERROR);
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format(
							"Exception while getting the scanned Finger details for user registration: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.FINGERPRINT_SCANNING_ERROR);
		}
		LOGGER.debug(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Scan Finger has ended");

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

			generateAlert(RegistrationConstants.ALERT_ERROR,
					RegistrationConstants.FINGERPRINT_NAVIGATE_NEXT_SECTION_ERROR);
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

			generateAlert(RegistrationConstants.ALERT_ERROR,
					RegistrationConstants.FINGERPRINT_NAVIGATE_PREVIOUS_SECTION_ERROR);
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

			List<FingerprintDetailsDTO> segmentedFingerprintDetailsDTOs = new ArrayList<>();
			boolean isValid = false;
			boolean isleftHandSlapCaptured = false;
			boolean isrightHandSlapCaptured = false;
			boolean isthumbsCaptured = false;

			RegistrationDTO registrationDTO = (RegistrationDTO) SessionContext.getInstance().getMapObject()
					.get(RegistrationConstants.REGISTRATION_DATA);

			List<FingerprintDetailsDTO> fingerprintDetailsDTOs = registrationDTO.getBiometricDTO()
					.getApplicantBiometricDTO().getFingerprintDetailsDTO();

			for (FingerprintDetailsDTO fingerprintDetailsDTO : fingerprintDetailsDTOs) {
				for (FingerprintDetailsDTO segmentedFingerprintDetailsDTO : fingerprintDetailsDTO
						.getSegmentedFingerprints()) {
					if (segmentedFingerprintDetailsDTO.getFingerType().contains(RegistrationConstants.ISO_FILE_NAME)) {
						segmentedFingerprintDetailsDTOs.add(segmentedFingerprintDetailsDTO);
					}
				}
			}

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

			if (isleftHandSlapCaptured && isrightHandSlapCaptured && isthumbsCaptured) {
				if (!fingerPrintCaptureServiceImpl.validateFingerprint(segmentedFingerprintDetailsDTOs)) {
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
