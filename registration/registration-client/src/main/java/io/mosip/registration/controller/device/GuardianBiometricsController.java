package io.mosip.registration.controller.device;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

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
	
	@Autowired
	private ScanPopUpViewController scanPopUpViewController;
	
	@Autowired
	private RegistrationController registrationController;
	
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
				scanIris(RegistrationConstants.LEFT.concat(RegistrationConstants.IRIS), RegistrationConstants.THUMBS_SEGMNTD_FILE_PATHS_USERONBOARD,
						popupStage, Double.parseDouble(
								getValueFromApplicationContext(RegistrationConstants.IRIS_THRESHOLD)));
			} else if (biometricType.getText().equalsIgnoreCase("Left Iris")) {
				scanIris(RegistrationConstants.RIGHT.concat(RegistrationConstants.IRIS), RegistrationConstants.THUMBS_SEGMNTD_FILE_PATHS_USERONBOARD,
						popupStage, Double.parseDouble(
								getValueFromApplicationContext(RegistrationConstants.IRIS_THRESHOLD)));
			}
			scanBtn.setDisable(true);
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
	}
	
	private void scanIris(String irisType, String[] capturedIris, Stage popupStage, Double thresholdValue) throws RegBaseCheckedException {

		IrisDetailsDTO irisDetailsDTO = null;

		List<IrisDetailsDTO> irisDetailsDTOs = getRegistrationDTOFromSession().getBiometricDTO()
				.getApplicantBiometricDTO().getIrisDetailsDTO();
		
		if (irisDetailsDTOs == null || irisDetailsDTOs.isEmpty()) {
			irisDetailsDTOs = new ArrayList<>(1);
			getRegistrationDTOFromSession().getBiometricDTO().getIntroducerBiometricDTO()
					.setIrisDetailsDTO(irisDetailsDTOs);
		} else {
			irisDetailsDTO = irisDetailsDTOs.get(0);
		}
		if (!(boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
			irisDetailsDTO.setNumOfIrisRetry(irisDetailsDTO.getNumOfIrisRetry() + 1);
		}
		
		irisFacade.getIrisImageAsDTO(irisDetailsDTO, irisType);
		
		scanPopUpViewController.getScanImage().setImage(convertBytesToImage(irisDetailsDTO.getIris()));
		generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationUIConstants.IRIS_SUCCESS_MSG);
		
		setCapturedValues(irisDetailsDTO.getIris(), irisDetailsDTO.getQualityScore(), irisDetailsDTO.getNumOfIrisRetry(), thresholdValue);
		
		popupStage.close();
		
		scanBtn.setDisable(true);

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
					if (!(boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
						detailsDTO.setNumRetry(fingerprintDetailsDTO.getNumRetry() + 1);
					}
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
		
		scanBtn.setDisable(true);

//		if (validateFingerPrints()) {
//			continueBtn.setDisable(false);
//		}

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


}
