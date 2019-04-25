package io.mosip.registration.controller.device;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_IRIS_CAPTURE_CONTROLLER;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.reg.RegistrationController;
import io.mosip.registration.device.fp.FingerprintFacade;
import io.mosip.registration.device.iris.IrisFacade;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.device.impl.FingerPrintCaptureServiceImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * {@code GuardianBiometricscontroller} is to capture and display the captured
 * biometrics of Guardian
 * 
 * @author Sravya Surampalli
 * @since 1.0
 */
@Controller
public class GuardianBiometricsController extends BaseController implements Initializable{
	
	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(GuardianBiometricsController.class);
	
	@FXML
	private GridPane biometricBox;
	
	@FXML
	private GridPane retryBox;
	
	@FXML
	private ComboBox<String> biometricTypecombo;
	
	@FXML
	private Label biometricType;
	
	@FXML
	private ImageView biometricImage;
	
	@FXML
	private Label qualityScore;
	
	@FXML
	private Label attemptSlap;
	
	@FXML
	private Label thresholdScoreLabel;
	
	@FXML
	private Label thresholdLabel;
	
	@FXML
	private GridPane biometricPane;
	
	@FXML
	private Button scanBtn;
	
	@FXML
	private ProgressBar bioProgress;
	
	@FXML
	private Label qualityText;
	
	@FXML
	private ColumnConstraints thresholdPane1;

	/** The threshold pane 2. */
	@FXML
	private ColumnConstraints thresholdPane2;
	
	@FXML
	private HBox bioRetryBox;
	
	@FXML
	private Button continueBtn;
	
	@FXML
	private Label duplicateCheckLbl;
	
	@Autowired
	private ScanPopUpViewController scanPopUpViewController;
	
	@Autowired
	private RegistrationController registrationController;
	
	/** The finger print capture service impl. */
	@Autowired
	private FingerPrintCaptureServiceImpl fingerPrintCaptureServiceImpl;
	
	/** The finger print facade. */
	@Autowired
	private FingerprintFacade fingerPrintFacade;
	
	/** The iris facade. */
	@Autowired
	private IrisFacade irisFacade;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		biometricBox.setVisible(false);	
		retryBox.setVisible(false);
		biometricTypecombo.getItems().removeAll(biometricTypecombo.getItems());
		biometricTypecombo.getItems().addAll("RightHand", "LeftHand", "Thumbs", "RightIris", "LeftIris");
		continueBtn.setDisable(true);
	}
	
	@FXML
	private void displayBiometric(ActionEvent event) {
				
		switch (biometricTypecombo.getValue()) {
		case "RightHand":
			updateBiometric("Right Hand", RegistrationConstants.RIGHTPALM_IMG_PATH, RegistrationConstants.RIGHTSLAP_FINGERPRINT_THRESHOLD, RegistrationConstants.FINGERPRINT_RETRIES_COUNT);
			break;
		case "LeftHand":
			updateBiometric("Left Hand", RegistrationConstants.LEFTPALM_IMG_PATH, RegistrationConstants.LEFTSLAP_FINGERPRINT_THRESHOLD, RegistrationConstants.FINGERPRINT_RETRIES_COUNT);
			break;
		case "Thumbs":
			updateBiometric("Thumbs", RegistrationConstants.THUMB_IMG_PATH, RegistrationConstants.THUMBS_FINGERPRINT_THRESHOLD, RegistrationConstants.FINGERPRINT_RETRIES_COUNT);
			break;
		case "RightIris":
			updateBiometric("Right Iris", RegistrationConstants.RIGHT_IRIS_IMG_PATH, RegistrationConstants.IRIS_THRESHOLD, RegistrationConstants.IRIS_RETRY_COUNT);
			break;
		case "LeftIris":
			updateBiometric("Left Iris", RegistrationConstants.LEFT_IRIS_IMG_PATH, RegistrationConstants.IRIS_THRESHOLD, RegistrationConstants.IRIS_RETRY_COUNT);
			break;
		default:
			
		}
		
		biometricBox.setVisible(true);	
		retryBox.setVisible(true);
		scanBtn.setDisable(false);
	}
	
	@FXML
	private void scan(ActionEvent event) {
		String headerText = "";
		if(biometricType.getText().contains("Hand") || biometricType.getText().contains("Thumbs")) {
			headerText = RegistrationUIConstants.FINGERPRINT;
		} else if(biometricType.getText().contains("Iris")) {
			headerText = RegistrationUIConstants.IRIS_SCAN;
		}
		scanPopUpViewController.init(this, headerText);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.controller.BaseController#scan(javafx.stage.Stage)
	 */
	@Override
	public void scan(Stage popupStage) {
		try {
			if (biometricType.getText().equalsIgnoreCase("Right Hand")) {
				scanFingers(RegistrationConstants.RIGHTPALM,
						RegistrationConstants.RIGHTHAND_SEGMNTD_DUPLICATE_FILE_PATHS, popupStage, Double.parseDouble(
								getValueFromApplicationContext(RegistrationConstants.RIGHTSLAP_FINGERPRINT_THRESHOLD)));
			} else if (biometricType.getText().equalsIgnoreCase("Left Hand")) {
				scanFingers(RegistrationConstants.LEFTPALM,
						RegistrationConstants.LEFTHAND_SEGMNTD_FILE_PATHS_USERONBOARD, popupStage, Double.parseDouble(
								getValueFromApplicationContext(RegistrationConstants.LEFTSLAP_FINGERPRINT_THRESHOLD)));
			} else if (biometricType.getText().equalsIgnoreCase("Thumbs")) {
				scanFingers(RegistrationConstants.THUMBS, RegistrationConstants.THUMBS_SEGMNTD_FILE_PATHS_USERONBOARD,
						popupStage, Double.parseDouble(
								getValueFromApplicationContext(RegistrationConstants.THUMBS_FINGERPRINT_THRESHOLD)));
			} else if (biometricType.getText().equalsIgnoreCase("Right Iris")) {
				scanIris(RegistrationConstants.RIGHT.concat(RegistrationConstants.EYE), popupStage,
						Double.parseDouble(getValueFromApplicationContext(RegistrationConstants.IRIS_THRESHOLD)));
			} else if (biometricType.getText().equalsIgnoreCase("Left Iris")) {
				scanIris(RegistrationConstants.LEFT.concat(RegistrationConstants.EYE), popupStage,
						Double.parseDouble(getValueFromApplicationContext(RegistrationConstants.IRIS_THRESHOLD)));
			}
		} catch (NumberFormatException | RegBaseCheckedException e) {
			// TODO Auto-generated catch block
		}
	}

	@FXML
	private void previous(ActionEvent event) {
		registrationController.showCurrentPage(RegistrationConstants.GUARDIAN_BIOMETRIC,
				getPageDetails(RegistrationConstants.GUARDIAN_BIOMETRIC, RegistrationConstants.PREVIOUS));
	}
	
	@FXML
	private void next(ActionEvent event) {
		registrationController.showCurrentPage(RegistrationConstants.GUARDIAN_BIOMETRIC,
				getPageDetails(RegistrationConstants.GUARDIAN_BIOMETRIC, RegistrationConstants.NEXT));
	}
	
	private void updateBiometric(String bioType, String bioImage, String biometricThreshold, String retryCount) {
		clearCaptureData();
		biometricType.setText(bioType);
		biometricImage.setImage(new Image(this.getClass().getResourceAsStream(bioImage)));
		thresholdScoreLabel.setText(getQualityScore(Double.parseDouble(
				getValueFromApplicationContext(biometricThreshold))));
		createQualityBox(retryCount, biometricThreshold);
		qualityScore.setText("-");
		attemptSlap.setText("-");
		getRegistrationDTOFromSession().getBiometricDTO().getIntroducerBiometricDTO().getFingerprintDetailsDTO().clear();
		getRegistrationDTOFromSession().getBiometricDTO().getIntroducerBiometricDTO().getIrisDetailsDTO().clear();
	}
	
	private void scanIris(String irisType, Stage popupStage, Double thresholdValue)
			throws RegBaseCheckedException {

		IrisDetailsDTO detailsDTO = null;

		List<IrisDetailsDTO> irisDetailsDTOs = getRegistrationDTOFromSession().getBiometricDTO()
				.getIntroducerBiometricDTO().getIrisDetailsDTO();

		if (irisDetailsDTOs == null || irisDetailsDTOs.isEmpty()) {
			irisDetailsDTOs = new ArrayList<>(1);
			getRegistrationDTOFromSession().getBiometricDTO().getIntroducerBiometricDTO()
					.setIrisDetailsDTO(irisDetailsDTOs);
		}
		if ( irisDetailsDTOs != null) {
			for (IrisDetailsDTO irisDetailsDTO2 : irisDetailsDTOs) {
				if (irisDetailsDTO2.getIrisType().equals(irisType)) {
					detailsDTO = irisDetailsDTO2;
					detailsDTO.setNumOfIrisRetry(irisDetailsDTO2.getNumOfIrisRetry() + 1);
					break;
				}
			}
			if (detailsDTO == null) {
				detailsDTO = new IrisDetailsDTO();
				detailsDTO.setNumOfIrisRetry(detailsDTO.getNumOfIrisRetry() + 1);
				irisDetailsDTOs.add(detailsDTO);
			}
		}
		irisFacade.getIrisImageAsDTO(detailsDTO, irisType);

		scanPopUpViewController.getScanImage().setImage(convertBytesToImage(detailsDTO.getIris()));
		generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationUIConstants.IRIS_SUCCESS_MSG);

		setCapturedValues(detailsDTO.getIris(), detailsDTO.getQualityScore(), detailsDTO.getNumOfIrisRetry(),
				thresholdValue);

		popupStage.close();
		
		if(validateIrisQulaity(detailsDTO, thresholdValue)) {
			scanBtn.setDisable(true);
			continueBtn.setDisable(false);
		} else {
			scanBtn.setDisable(false);
			continueBtn.setDisable(true);
		}

	}
	
	private void scanFingers(String fingerType, String[] segmentedFingersPath, Stage popupStage, Double thresholdValue)
			throws RegBaseCheckedException {
		
		FingerprintDetailsDTO detailsDTO = null;

		List<FingerprintDetailsDTO> fingerprintDetailsDTOs = getRegistrationDTOFromSession().getBiometricDTO()
				.getIntroducerBiometricDTO().getFingerprintDetailsDTO();

		if (fingerprintDetailsDTOs == null || fingerprintDetailsDTOs.isEmpty()) {
			fingerprintDetailsDTOs = new ArrayList<>(1);
			getRegistrationDTOFromSession().getBiometricDTO().getIntroducerBiometricDTO()
					.setFingerprintDetailsDTO(fingerprintDetailsDTOs);
		}

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
				detailsDTO.setNumRetry(detailsDTO.getNumRetry() + 1);
				fingerprintDetailsDTOs.add(detailsDTO);
			}
		}
		fingerPrintFacade.getFingerPrintImageAsDTO(detailsDTO, fingerType);

		fingerPrintFacade.segmentFingerPrintImage(detailsDTO, segmentedFingersPath);

		scanPopUpViewController.getScanImage().setImage(convertBytesToImage(detailsDTO.getFingerPrint()));

		generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationUIConstants.FP_CAPTURE_SUCCESS);

		setCapturedValues(detailsDTO.getFingerPrint(), detailsDTO.getQualityScore(), detailsDTO.getNumRetry(), thresholdValue);
		
		popupStage.close();

		if (validateFingerPrintQulaity(detailsDTO, thresholdValue) && fingerdeduplicationCheck(fingerprintDetailsDTOs)) {
			scanBtn.setDisable(true);
			continueBtn.setDisable(false);
		}

	}
	
	private void setCapturedValues(byte[] capturedBio, double qltyScore, int retry, double thresholdValue ) {
		biometricPane.getStyleClass().add(RegistrationConstants.FINGERPRINT_PANES_SELECTED);
		biometricImage.setImage(convertBytesToImage(capturedBio));
		qualityScore.setText(getQualityScore(qltyScore));
		attemptSlap.setText(String.valueOf(retry));
			
		bioProgress.setProgress(Double.valueOf(
					getQualityScore(qltyScore).split(RegistrationConstants.PERCENTAGE)[0]) / 100);
			qualityText.setText(getQualityScore(qltyScore));
			if (Double.valueOf(getQualityScore(qltyScore)
					.split(RegistrationConstants.PERCENTAGE)[0]) >= thresholdValue) {
				clearAttemptsBox(RegistrationConstants.QUALITY_LABEL_GREEN, retry);
				bioProgress.getStyleClass().removeAll(RegistrationConstants.PROGRESS_BAR_RED);
				bioProgress.getStyleClass().add(RegistrationConstants.PROGRESS_BAR_GREEN);
				qualityText.getStyleClass().removeAll(RegistrationConstants.LABEL_RED);
				qualityText.getStyleClass().add(RegistrationConstants.LABEL_GREEN);
			} else {
				clearAttemptsBox(RegistrationConstants.QUALITY_LABEL_RED, retry);
				bioProgress.getStyleClass().removeAll(RegistrationConstants.PROGRESS_BAR_GREEN);
				bioProgress.getStyleClass().add(RegistrationConstants.PROGRESS_BAR_RED);
				qualityText.getStyleClass().removeAll(RegistrationConstants.LABEL_GREEN);
				qualityText.getStyleClass().add(RegistrationConstants.LABEL_RED);
			}
	}
	
	private void createQualityBox(String retryCount, String biometricThreshold) {
		if (!(boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
			for (int retry = 0; retry < Integer.parseInt(
					getValueFromApplicationContext(retryCount)); retry++) {
				Label label = new Label();
				label.getStyleClass().add(RegistrationConstants.QUALITY_LABEL_GREY);
				label.setId(RegistrationConstants.RETRY_ATTEMPT_ID + (retry + 1));
				label.setVisible(true);
				label.setText(String.valueOf(retry + 1));
				label.setAlignment(Pos.CENTER);
				bioRetryBox.getChildren().add(label);
			}

			String threshold = getValueFromApplicationContext(biometricThreshold);

			thresholdLabel.setAlignment(Pos.CENTER);
			thresholdLabel.setText(RegistrationUIConstants.THRESHOLD.concat("  ").concat(threshold)
					.concat(RegistrationConstants.PERCENTAGE));
			thresholdPane1.setPercentWidth(Double.parseDouble(threshold));
			thresholdPane2.setPercentWidth(100.00 - (Double.parseDouble(threshold)));
		}

	}
	
	/**
	 * Clear attempts box.
	 *
	 * @param styleClass the style class
	 * @param retries    the retries
	 */
	private void clearAttemptsBox(String styleClass, int retries) {
		bioRetryBox.lookup(RegistrationConstants.RETRY_ATTEMPT + retries).getStyleClass().clear();
		bioRetryBox.lookup(RegistrationConstants.RETRY_ATTEMPT + retries).getStyleClass().add(styleClass);
	}

	private void clearCaptureData() {
		bioProgress.setProgress(0);
		bioProgress.getStyleClass().removeAll(RegistrationConstants.PROGRESS_BAR_RED);
		bioProgress.getStyleClass().removeAll(RegistrationConstants.PROGRESS_BAR_GREEN);

		qualityText.setText(RegistrationConstants.EMPTY);
		qualityText.getStyleClass().removeAll(RegistrationConstants.LABEL_RED);
		qualityText.getStyleClass().removeAll(RegistrationConstants.LABEL_GREEN);

		bioRetryBox.getChildren().clear();
	}
	
	/**
	 * Validates QualityScore.
	 *
	 * @param fingerprintDetailsDTO the fingerprint details DTO
	 * @param handThreshold         the hand threshold
	 * @return boolean
	 */
	private Boolean validateFingerPrintQulaity(FingerprintDetailsDTO fingerprintDetailsDTO, Double handThreshold) {
		return fingerprintDetailsDTO.getQualityScore() >= handThreshold
				|| (fingerprintDetailsDTO.getQualityScore() < handThreshold)
						&& fingerprintDetailsDTO.getNumRetry() == Integer.parseInt(
								getValueFromApplicationContext(RegistrationConstants.FINGERPRINT_RETRIES_COUNT))
				|| fingerprintDetailsDTO.isForceCaptured();
	}
	
	/**
	 * Fingerdeduplication check.
	 *
	 * @param segmentedFingerprintDetailsDTOs the segmented fingerprint details
	 *                                        DTO's
	 * @param isValid                         the isvalid flag
	 * @param fingerprintDetailsDTOs          the fingerprint details DT os
	 * @return true, if successful
	 */
	private boolean fingerdeduplicationCheck(List<FingerprintDetailsDTO> fingerprintDetailsDTOs) {
		
		List<FingerprintDetailsDTO> segmentedFingerprintDetailsDTOs = new ArrayList<>();
		
		boolean isValid = false;
		
		for (FingerprintDetailsDTO fingerprintDetailsDTO : fingerprintDetailsDTOs) {
			for (FingerprintDetailsDTO segmentedFingerprintDetailsDTO : fingerprintDetailsDTO
					.getSegmentedFingerprints()) {
				segmentedFingerprintDetailsDTOs.add(segmentedFingerprintDetailsDTO);
			}
		}
			if (!fingerPrintCaptureServiceImpl.validateFingerprint(segmentedFingerprintDetailsDTOs)) {
				isValid = true;
			} else {
				FingerprintDetailsDTO duplicateFinger = (FingerprintDetailsDTO) SessionContext.map()
						.get(RegistrationConstants.DUPLICATE_FINGER);

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
		return isValid;
	}
	
	private boolean validateIrisQulaity(IrisDetailsDTO irisDetailsDTO, Double irisThreshold) {
			LOGGER.info(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Validating the quality score of the captured iris");

			return irisDetailsDTO.getQualityScore() >= irisThreshold
					|| (Double.compare(irisDetailsDTO.getQualityScore(), irisThreshold) < 0
							&& irisDetailsDTO.getNumOfIrisRetry() == Integer.parseInt(
									getValueFromApplicationContext(RegistrationConstants.IRIS_RETRY_COUNT))
					|| irisDetailsDTO.isForceCaptured());
	}
	
	public void clearCapturedBioData() {
		
		if (getRegistrationDTOFromSession() != null) {
			getRegistrationDTOFromSession().getBiometricDTO().getIntroducerBiometricDTO()
			.setFingerprintDetailsDTO(new ArrayList<>());
			
			getRegistrationDTOFromSession().getBiometricDTO().getIntroducerBiometricDTO()
			.setIrisDetailsDTO(new ArrayList<>());
		}
		duplicateCheckLbl.setText(RegistrationConstants.EMPTY);
		clearCaptureData();
		biometricBox.setVisible(false);	
		retryBox.setVisible(false);
	}


}
