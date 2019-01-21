package io.mosip.registration.controller.device;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.reg.RegistrationController;
import io.mosip.registration.device.fp.FingerprintFacade;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.biometric.BiometricExceptionDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.device.impl.FingerPrintCaptureServiceImpl;
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
	private ScanPopUpViewController scanPopUpViewController;

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

	/** The duplicate check label. */
	@FXML
	private Label duplicateCheckLbl;

	/** The selected pane. */
	private AnchorPane selectedPane = null;

	/** The selected pane. */
	@Autowired
	private FingerprintFacade fingerPrintFacade = null;

	private List<BiometricExceptionDTO> bioExceptionList = new ArrayList<>();
	/** The scan btn. */
	@FXML
	private Button scanBtn;

	private int leftSlapCount;
	private int rightSlapCount;
	private int thumbCount;
	private Boolean isLeftPalmChanged;
	private Boolean isRightPalmChanged;
	private Boolean isThumbChanged;

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
					scanBtn.setDisable(true);
					duplicateCheckLbl.setText(RegistrationConstants.EMPTY);

					exceptionFingersCount();

					// Get the Fingerprint from RegistrationDTO based on selected Fingerprint Pane
					FingerprintDetailsDTO fpDetailsDTO = getFingerprintBySelectedPane().findFirst().orElse(null);

					if ((leftHandPalmPane.getId().equals(selectedPane.getId()) && leftSlapCount < 4)
							&& (fpDetailsDTO == null
									|| (fpDetailsDTO.getFingerType().equals(RegistrationConstants.LEFTPALM)
											&& fpDetailsDTO.getQualityScore() < Double
													.parseDouble(getValueFromSessionMap(
															RegistrationConstants.LEFTSLAP_FINGERPRINT_THRESHOLD)))
									|| isLeftPalmChanged)
							|| (rightHandPalmPane.getId().equals(selectedPane.getId()) && rightSlapCount < 4)
									&& (fpDetailsDTO == null || (fpDetailsDTO.getFingerType()
											.equals(RegistrationConstants.RIGHTPALM)
											&& fpDetailsDTO.getQualityScore() < Double
													.parseDouble(getValueFromSessionMap(
															RegistrationConstants.RIGHTSLAP_FINGERPRINT_THRESHOLD)))
											|| isRightPalmChanged)
							|| (thumbPane.getId().equals(selectedPane.getId()) && thumbCount < 2)
									&& (fpDetailsDTO == null || (fpDetailsDTO.getFingerType()
											.equals(RegistrationConstants.THUMBS)
											&& fpDetailsDTO.getQualityScore() < Double
													.parseDouble(getValueFromSessionMap(
															RegistrationConstants.THUMBS_FINGERPRINT_THRESHOLD)))
											|| isThumbChanged)) {
						scanBtn.setDisable(false);
					}
				}
			};

			// Add event handler object to mouse click event
			leftHandPalmPane.setOnMouseClicked(mouseClick);
			rightHandPalmPane.setOnMouseClicked(mouseClick);
			thumbPane.setOnMouseClicked(mouseClick);

			leftSlapThresholdScoreLbl.setText(getQualityScore(
					Double.parseDouble(getValueFromSessionMap(RegistrationConstants.LEFTSLAP_FINGERPRINT_THRESHOLD))));

			rightSlapThresholdScoreLbl.setText(getQualityScore(
					Double.parseDouble(getValueFromSessionMap(RegistrationConstants.RIGHTSLAP_FINGERPRINT_THRESHOLD))));

			thumbsThresholdScoreLbl.setText(getQualityScore(
					Double.parseDouble(getValueFromSessionMap(RegistrationConstants.THUMBS_FINGERPRINT_THRESHOLD))));

			loadingImageFromSessionContext();

			LOGGER.debug(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Loading of FingerprintCapture screen ended");
		} catch (

		RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format(
							"%s -> Exception while initializing Fingerprint Capture page for user registration  %s",
							RegistrationConstants.USER_REG_FINGERPRINT_PAGE_LOAD_EXP, runtimeException.getMessage()));

			throw new RegBaseUncheckedException(RegistrationConstants.USER_REG_FINGERPRINT_PAGE_LOAD_EXP,
					String.format("Exception while initializing Fingerprint Capture page for user registration  %s",
							runtimeException.getMessage()));
		}
	}

	public void clearImage() {
		isLeftPalmChanged = false;
		isRightPalmChanged = false;
		isThumbChanged = false;

		exceptionFingersCount();
		if (leftSlapCount == 4) {
			leftHandPalmImageview
					.setImage(new Image(getClass().getResource(RegistrationConstants.LEFTPALM_IMG_PATH).toExternalForm()));
			leftSlapQualityScore.setText(RegistrationConstants.EMPTY);

			removeFingerPrint(RegistrationConstants.LEFTPALM);

		}
		if (rightSlapCount == 4) {
			rightHandPalmImageview
					.setImage(new Image(getClass().getResource(RegistrationConstants.RIGHTPALM_IMG_PATH).toExternalForm()));
			rightSlapQualityScore.setText(RegistrationConstants.EMPTY);

			removeFingerPrint(RegistrationConstants.RIGHTPALM);

		}
		if (thumbCount == 2) {
			thumbImageview.setImage(new Image(getClass().getResource(RegistrationConstants.THUMB_IMG_PATH).toExternalForm()));
			thumbsQualityScore.setText(RegistrationConstants.EMPTY);

			removeFingerPrint(RegistrationConstants.THUMBS);

		}
		List<BiometricExceptionDTO> tempExceptionList = getRegistrationDTOFromSession().getBiometricDTO()
				.getApplicantBiometricDTO().getBiometricExceptionDTO();
		if (tempExceptionList == null || tempExceptionList.isEmpty()) {
			leftHandPalmImageview
					.setImage(new Image(getClass().getResource(RegistrationConstants.LEFTPALM_IMG_PATH).toExternalForm()));
			leftSlapQualityScore.setText(RegistrationConstants.EMPTY);
			rightHandPalmImageview
					.setImage(new Image(getClass().getResource(RegistrationConstants.RIGHTPALM_IMG_PATH).toExternalForm()));
			rightSlapQualityScore.setText(RegistrationConstants.EMPTY);
			thumbImageview.setImage(new Image(getClass().getResource(RegistrationConstants.THUMB_IMG_PATH).toExternalForm()));
			thumbsQualityScore.setText(RegistrationConstants.EMPTY);
		}

		if (bioExceptionList.isEmpty()) {
			List<BiometricExceptionDTO> lis = getRegistrationDTOFromSession().getBiometricDTO()
					.getApplicantBiometricDTO().getBiometricExceptionDTO();
			bioExceptionList.addAll(lis);
		} else {
			List<String> bioList1 = null;
			List<String> bioList = bioExceptionList.stream().map(bio -> bio.getMissingBiometric())
					.collect(Collectors.toList());
			if (null != tempExceptionList) {
				bioList1 = tempExceptionList.stream().map(bio -> bio.getMissingBiometric())
						.collect(Collectors.toList());
			}

			@SuppressWarnings("unchecked")
			List<String> changedException = (List<String>) CollectionUtils.disjunction(bioList, bioList1);

			changedException.forEach(biometricException -> {
				if (biometricException.contains(RegistrationConstants.LEFT.toLowerCase())
						&& !biometricException.contains(RegistrationConstants.THUMB)
						&& !biometricException.contains(RegistrationConstants.EYE)) {
					isLeftPalmChanged = true;
				} else if (biometricException.contains(RegistrationConstants.RIGHT.toLowerCase())
						&& !biometricException.contains(RegistrationConstants.THUMB)
						&& !biometricException.contains(RegistrationConstants.EYE)) {
					isRightPalmChanged = true;
				} else if (biometricException.contains(RegistrationConstants.THUMB)) {
					isThumbChanged = true;
				}
			});

		}
		bioExceptionList.clear();
		bioExceptionList.addAll(tempExceptionList);

	}

	private void removeFingerPrint(String handSlap) {
		Iterator<FingerprintDetailsDTO> iterator = getRegistrationDTOFromSession().getBiometricDTO()
				.getApplicantBiometricDTO().getFingerprintDetailsDTO().iterator();

		while (iterator.hasNext()) {
			FingerprintDetailsDTO value = iterator.next();
				if (value.getFingerType().contains(handSlap)) {
					iterator.remove();
					break;
			}
		}
	}

	public void clearFingerPrintDTO() {
		leftHandPalmImageview.setImage(new Image(getClass().getResource(RegistrationConstants.LEFTPALM_IMG_PATH).toExternalForm()));
		leftSlapQualityScore.setText(RegistrationConstants.EMPTY);
		removeFingerPrint(RegistrationConstants.LEFTPALM);
		
		rightHandPalmImageview
				.setImage(new Image(getClass().getResource(RegistrationConstants.RIGHTPALM_IMG_PATH).toExternalForm()));
		rightSlapQualityScore.setText(RegistrationConstants.EMPTY);
		removeFingerPrint(RegistrationConstants.RIGHTPALM);
		
		thumbImageview.setImage(new Image(getClass().getResource(RegistrationConstants.THUMB_IMG_PATH).toExternalForm()));
		thumbsQualityScore.setText(RegistrationConstants.EMPTY);
		removeFingerPrint(RegistrationConstants.THUMBS);
	}

	private void exceptionFingersCount() {
		leftSlapCount = 0;
		rightSlapCount = 0;
		thumbCount = 0;

		List<BiometricExceptionDTO> biometricExceptionDTOs = getRegistrationDTOFromSession().getBiometricDTO()
				.getApplicantBiometricDTO().getBiometricExceptionDTO();
		for (BiometricExceptionDTO biometricExceptionDTO : biometricExceptionDTOs) {

			if (biometricExceptionDTO.getMissingBiometric().contains(RegistrationConstants.LEFT.toLowerCase())
					&& !biometricExceptionDTO.getMissingBiometric().contains(RegistrationConstants.THUMB)
					&& !biometricExceptionDTO.getMissingBiometric().contains(RegistrationConstants.EYE)) {
				leftSlapCount++;
			}
			if (biometricExceptionDTO.getMissingBiometric().contains(RegistrationConstants.RIGHT.toLowerCase())
					&& !biometricExceptionDTO.getMissingBiometric().contains(RegistrationConstants.THUMB)
					&& !biometricExceptionDTO.getMissingBiometric().contains(RegistrationConstants.EYE)) {
				rightSlapCount++;
			}
			if (biometricExceptionDTO.getMissingBiometric().contains(RegistrationConstants.THUMB)) {
				thumbCount++;
			}
		}
	}

	private void loadingImageFromSessionContext() {
		RegistrationDTO registrationDTOContent = (RegistrationDTO) SessionContext.getInstance().getMapObject()
				.get(RegistrationConstants.REGISTRATION_DATA);
		if (null != registrationDTOContent) {
			registrationDTOContent.getBiometricDTO().getApplicantBiometricDTO().getFingerprintDetailsDTO()
					.forEach(item -> {
						if (item.getFingerType().equals(RegistrationConstants.LEFTPALM)) {
							leftHandPalmImageview.setImage(new Image(new ByteArrayInputStream(item.getFingerPrint())));
							leftSlapQualityScore.setText(getQualityScore(item.getQualityScore()));
						} else if (item.getFingerType().equals(RegistrationConstants.RIGHTPALM)) {
							rightHandPalmImageview.setImage(new Image(new ByteArrayInputStream(item.getFingerPrint())));
							rightSlapQualityScore.setText(getQualityScore(item.getQualityScore()));
						} else if (item.getFingerType().equals(RegistrationConstants.THUMBS)) {
							thumbImageview.setImage(new Image(new ByteArrayInputStream(item.getFingerPrint())));
							thumbsQualityScore.setText(getQualityScore(item.getQualityScore()));
						}
					});
		}
	}

	private String getQualityScore(Double qulaityScore) {
		return String.valueOf(Math.round(qulaityScore)).concat(RegistrationConstants.PERCENTAGE);
	}

	public void scan() {
		try {
			LOGGER.debug(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Opening pop-up screen to capture fingerprint for user registration");
			FingerprintDetailsDTO fpDetailsDTO = getFingerprintBySelectedPane().findFirst().orElse(null);

			if (fpDetailsDTO == null || fpDetailsDTO.getNumRetry() < Integer
					.parseInt(getValueFromSessionMap(RegistrationConstants.FINGERPRINT_RETRIES_COUNT))) {

				scanPopUpViewController.init(this, RegistrationConstants.FINGERPRINT);
			} else {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.FINGERPRINT_MAX_RETRIES_ALERT);
			}

			LOGGER.debug(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of fingersplaced ended");

		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, String.format(
					"%s -> Exception while Opening pop-up screen to capture fingerprint for user registration  %s",
					RegistrationConstants.USER_REG_FINGERPRINT_CAPTURE_POPUP_LOAD_EXP, runtimeException.getMessage()));

			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_FINGERPRINT_SCAN_POPUP);
		}
	}

	@Override
	public void scan(Stage popupStage) {

		try {

			FingerprintDetailsDTO detailsDTO = null;

			List<FingerprintDetailsDTO> fingerprintDetailsDTOs = getRegistrationDTOFromSession().getBiometricDTO()
					.getApplicantBiometricDTO().getFingerprintDetailsDTO();

			if (fingerprintDetailsDTOs == null || fingerprintDetailsDTOs.isEmpty()) {
				fingerprintDetailsDTOs = new ArrayList<>(3);
				getRegistrationDTOFromSession().getBiometricDTO().getApplicantBiometricDTO()
						.setFingerprintDetailsDTO(fingerprintDetailsDTOs);
			}

			if (selectedPane.getId() == leftHandPalmPane.getId()) {

				scanFingers(detailsDTO, fingerprintDetailsDTOs, RegistrationConstants.LEFTPALM,

						RegistrationConstants.LEFTHAND_SEGMNTD_FILE_PATHS, leftHandPalmImageview,

						leftSlapQualityScore, popupStage);

			} else if (selectedPane.getId() == rightHandPalmPane.getId()) {

				if (SessionContext.getInstance().getMapObject().containsKey(RegistrationConstants.DUPLICATE_FINGER)) {

					scanFingers(detailsDTO, fingerprintDetailsDTOs, RegistrationConstants.RIGHTPALM,

							RegistrationConstants.RIGHTHAND_SEGMNTD_FILE_PATHS, rightHandPalmImageview,

							rightSlapQualityScore, popupStage);

				} else {
					scanFingers(detailsDTO, fingerprintDetailsDTOs, RegistrationConstants.RIGHTPALM,

							RegistrationConstants.RIGHTHAND_SEGMNTD_DUPLICATE_FILE_PATHS, rightHandPalmImageview,

							rightSlapQualityScore, popupStage);
				}

			} else if (selectedPane.getId() == thumbPane.getId()) {

				scanFingers(detailsDTO, fingerprintDetailsDTOs, RegistrationConstants.THUMBS,

						RegistrationConstants.THUMBS_SEGMNTD_FILE_PATHS, thumbImageview, thumbsQualityScore,
						popupStage);

			}

		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format(
							"Exception while getting the scanned Finger details for user registration: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));

			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.FINGERPRINT_SCANNING_ERROR);
		} catch (RegBaseCheckedException regBaseCheckedException) {
			LOGGER.error(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format(
							"Exception while getting the scanned Finger details for user registration: %s caused by %s",
							regBaseCheckedException.getMessage(), regBaseCheckedException.getCause()));

			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.FINGERPRINT_SCANNING_ERROR);
		}
		LOGGER.debug(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Scan Finger has ended");

	}

	private void scanFingers(FingerprintDetailsDTO detailsDTO, List<FingerprintDetailsDTO> fingerprintDetailsDTOs,
			String fingerType, String[] segmentedFingersPath, ImageView fingerImageView, Label scoreLabel,
			Stage popupStage) throws RegBaseCheckedException {

		ImageView imageView = fingerImageView;
		Label qualityScoreLabel = scoreLabel;
		if (fingerprintDetailsDTOs != null) {

			for (FingerprintDetailsDTO fingerprintDetailsDTO : fingerprintDetailsDTOs) {
				if (fingerprintDetailsDTO.getFingerType().equals(fingerType)) {
					detailsDTO = fingerprintDetailsDTO;

					for (String segmentedFingerPath : segmentedFingersPath) {
						String[] path = segmentedFingerPath.split("/");
						for (FingerprintDetailsDTO segmentedfpDetailsDTO : fingerprintDetailsDTO
								.getSegmentedFingerprints()) {
							if (segmentedfpDetailsDTO.getFingerType().equals(path[3])) {
								fingerprintDetailsDTO.getSegmentedFingerprints().remove(segmentedfpDetailsDTO);
								break;
							}
						}
					}

					detailsDTO.setNumRetry(fingerprintDetailsDTO.getNumRetry() + 1);
					break;
				}
			}
			if (detailsDTO == null) {
				detailsDTO = new FingerprintDetailsDTO();
				fingerprintDetailsDTOs.add(detailsDTO);
			}
		}
		fingerPrintFacade.getFingerPrintImageAsDTO(detailsDTO, fingerType);

		fingerPrintFacade.segmentFingerPrintImage(detailsDTO, segmentedFingersPath);

		scanPopUpViewController.getScanImage().setImage(convertBytesToImage(detailsDTO.getFingerPrint()));

		generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationUIConstants.FP_CAPTURE_SUCCESS);

		popupStage.close();

		imageView.setImage(convertBytesToImage(detailsDTO.getFingerPrint()));
		qualityScoreLabel.setText(getQualityScore(detailsDTO.getQualityScore()));
		scanBtn.setDisable(true);
	}

	/**
	 * {@code saveBiometricDetails} is to check the deduplication of captured finger
	 * prints
	 */
	public void goToNextPage() {
		try {
			LOGGER.debug(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Navigating to Iris capture page for user registration started");

			exceptionFingersCount();
			if (getRegistrationDTOFromSession().getSelectionListDTO() != null) {
				if (validateFingerPrints()) {
					SessionContext.getInstance().getMapObject().remove(RegistrationConstants.DUPLICATE_FINGER);

					long irisCount = getRegistrationDTOFromSession().getBiometricDTO().getApplicantBiometricDTO()
							.getBiometricExceptionDTO().stream()
							.filter(bio -> bio.getBiometricType().equalsIgnoreCase(RegistrationConstants.IRIS)).count();

					if (getRegistrationDTOFromSession().getSelectionListDTO().isBiometricIris() || irisCount > 0) {
						registrationController.toggleFingerprintCaptureVisibility(false);
						registrationController.toggleIrisCaptureVisibility(true);
					} else {
						registrationController.toggleFingerprintCaptureVisibility(false);
						registrationController.togglePhotoCaptureVisibility(true);
					}
				}
			} else {
				if (validateFingerPrints()) {
					SessionContext.getInstance().getMapObject().remove(RegistrationConstants.DUPLICATE_FINGER);
					registrationController.toggleFingerprintCaptureVisibility(false);
					registrationController.toggleIrisCaptureVisibility(true);
				}
			}
			LOGGER.debug(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Navigating to Iris capture page for user registration ended");
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format("%s -> Exception while navigating to Iris capture page for user registration  %s",
							RegistrationConstants.USER_REG_FINGERPRINT_CAPTURE_NEXT_SECTION_LOAD_EXP,
							runtimeException.getMessage()));

			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.FINGERPRINT_NAVIGATE_NEXT_SECTION_ERROR);
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

			exceptionFingersCount();
			if (validateFingerPrints()) {
				SessionContext.getInstance().getMapObject().remove(RegistrationConstants.DUPLICATE_FINGER);
				if ((boolean) SessionContext.getInstance().getUserContext().getUserMap()
						.get(RegistrationConstants.TOGGLE_BIO_METRIC_EXCEPTION)) {
					registrationController.toggleFingerprintCaptureVisibility(false);
					registrationController.toggleBiometricExceptionVisibility(true);
				} else {
					registrationController.getDemoGraphicTitlePane().setExpanded(true);
				}
			}
			LOGGER.debug(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Navigating to Demographic capture page for user registration ended");
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format(
							"%s -> Exception while navigating to Demographic capture page for user registration  %s",
							RegistrationConstants.USER_REG_FINGERPRINT_CAPTURE_PREV_SECTION_LOAD_EXP,
							runtimeException.getMessage()));

			generateAlert(RegistrationConstants.ERROR,
					RegistrationUIConstants.FINGERPRINT_NAVIGATE_PREVIOUS_SECTION_ERROR);
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

			List<FingerprintDetailsDTO> fingerprintDetailsDTOs = getRegistrationDTOFromSession().getBiometricDTO()
					.getApplicantBiometricDTO().getFingerprintDetailsDTO();

			for (FingerprintDetailsDTO fingerprintDetailsDTO : fingerprintDetailsDTOs) {
				for (FingerprintDetailsDTO segmentedFingerprintDetailsDTO : fingerprintDetailsDTO
						.getSegmentedFingerprints()) {
					segmentedFingerprintDetailsDTOs.add(segmentedFingerprintDetailsDTO);
				}
			}

			for (FingerprintDetailsDTO fingerprintDetailsDTO : fingerprintDetailsDTOs) {
				if (validateQualityScore(fingerprintDetailsDTO)) {
					if (fingerprintDetailsDTO.getFingerType().equalsIgnoreCase(RegistrationConstants.LEFTPALM)
							|| leftSlapCount >= 4) {
						isleftHandSlapCaptured = true;
					}
					if (fingerprintDetailsDTO.getFingerType().equalsIgnoreCase(RegistrationConstants.RIGHTPALM)
							|| rightSlapCount >= 4) {
						isrightHandSlapCaptured = true;
					}
					if (fingerprintDetailsDTO.getFingerType().equalsIgnoreCase(RegistrationConstants.THUMBS)
							|| thumbCount >= 2) {
						isthumbsCaptured = true;
					}
				} else {
					generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.IRIS_QUALITY_SCORE_ERROR);
					return isValid;
				}
			}

			if (fingerprintDetailsDTOs.isEmpty() && leftSlapCount >= 4 && rightSlapCount >= 4 && thumbCount >= 2) {
				isleftHandSlapCaptured = true;
				isrightHandSlapCaptured = true;
				isthumbsCaptured = true;
			}

			if (isleftHandSlapCaptured && isrightHandSlapCaptured && isthumbsCaptured) {
				if (!fingerPrintCaptureServiceImpl.validateFingerprint(segmentedFingerprintDetailsDTOs)) {
					isValid = true;
				} else {
					FingerprintDetailsDTO duplicateFinger = (FingerprintDetailsDTO) SessionContext.getInstance()
							.getMapObject().get(RegistrationConstants.DUPLICATE_FINGER);

					Iterator<FingerprintDetailsDTO> iterator = fingerprintDetailsDTOs.iterator();

					while (iterator.hasNext()) {
						FingerprintDetailsDTO value = iterator.next();
						for (FingerprintDetailsDTO duplicate : value.getSegmentedFingerprints()) {
							if (duplicate.getFingerType().equals(duplicateFinger.getFingerType())) {
								iterator.remove();
								break;
							}
						}
					}
					String finger;
					if (duplicateFinger.getFingerType().contains(RegistrationConstants.LEFT.toLowerCase())) {
						finger = duplicateFinger.getFingerType().replace(RegistrationConstants.LEFT.toLowerCase(),
								RegistrationConstants.LEFT_HAND);
					} else {
						finger = duplicateFinger.getFingerType().replace(RegistrationConstants.RIGHT.toLowerCase(),
								RegistrationConstants.RIGHT_HAND);
					}
					duplicateCheckLbl.setText(finger + " " + RegistrationUIConstants.FINGERPRINT_DUPLICATION_ALERT);
				}
			} else {
				generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationUIConstants.FINGERPRINT_SCAN_ALERT);
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
				return fingerprintDetailsDTO.getQualityScore() >= Double
						.parseDouble(getValueFromSessionMap(RegistrationConstants.LEFTSLAP_FINGERPRINT_THRESHOLD))
						|| (fingerprintDetailsDTO.getQualityScore() < Double.parseDouble(
								getValueFromSessionMap(RegistrationConstants.LEFTSLAP_FINGERPRINT_THRESHOLD))
								&& fingerprintDetailsDTO.getNumRetry() == Integer.parseInt(
										getValueFromSessionMap(RegistrationConstants.FINGERPRINT_RETRIES_COUNT)))
						|| fingerprintDetailsDTO.isForceCaptured();
			} else if (fingerprintDetailsDTO.getFingerType().equals(RegistrationConstants.RIGHTPALM)) {
				return fingerprintDetailsDTO.getQualityScore() >= Double
						.parseDouble(getValueFromSessionMap(RegistrationConstants.RIGHTSLAP_FINGERPRINT_THRESHOLD))
						|| (fingerprintDetailsDTO.getQualityScore() < Double.parseDouble(
								getValueFromSessionMap(RegistrationConstants.RIGHTSLAP_FINGERPRINT_THRESHOLD))
								&& fingerprintDetailsDTO.getNumRetry() == Integer.parseInt(
										getValueFromSessionMap(RegistrationConstants.FINGERPRINT_RETRIES_COUNT)))
						|| fingerprintDetailsDTO.isForceCaptured();
			} else if (fingerprintDetailsDTO.getFingerType().equals(RegistrationConstants.THUMBS)) {
				return fingerprintDetailsDTO.getQualityScore() >= Double
						.parseDouble(getValueFromSessionMap(RegistrationConstants.THUMBS_FINGERPRINT_THRESHOLD))
						|| (fingerprintDetailsDTO.getQualityScore() < Double
								.parseDouble(getValueFromSessionMap(RegistrationConstants.THUMBS_FINGERPRINT_THRESHOLD))
								&& fingerprintDetailsDTO.getNumRetry() == Integer.parseInt(
										getValueFromSessionMap(RegistrationConstants.FINGERPRINT_RETRIES_COUNT)))
						|| fingerprintDetailsDTO.isForceCaptured();
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

	private Stream<FingerprintDetailsDTO> getFingerprintBySelectedPane() {
		return getRegistrationDTOFromSession().getBiometricDTO().getApplicantBiometricDTO().getFingerprintDetailsDTO()
				.stream().filter(fingerprint -> {
					String fingerType;
					if (StringUtils.containsIgnoreCase(selectedPane.getId(), leftHandPalmPane.getId())) {
						fingerType = RegistrationConstants.LEFTPALM;
					} else {
						if (StringUtils.containsIgnoreCase(selectedPane.getId(), rightHandPalmPane.getId())) {
							fingerType = RegistrationConstants.RIGHTPALM;
						} else {
							fingerType = RegistrationConstants.THUMBS;
						}
					}
					return fingerprint.getFingerType().contains(fingerType);
				});
	}

	private RegistrationDTO getRegistrationDTOFromSession() {
		return (RegistrationDTO) SessionContext.getInstance().getMapObject()
				.get(RegistrationConstants.REGISTRATION_DATA);
	}

	private String getValueFromSessionMap(String key) {
		return (String) applicationContext.getApplicationMap().get(key);
	}
}
