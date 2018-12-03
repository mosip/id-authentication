package io.mosip.registration.controller.device;

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
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.BiometricInfoDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.service.device.impl.FingerPrintCaptureServiceImpl;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.paint.Color;
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

	@Value("${leftHand_Slap_Threshold_Score}")
	private double leftHandSlapThresholdScore;

	@Value("${rightHand_Slap_Threshold_Score}")
	private double rightHandSlapThresholdScore;

	@Value("${thumbs_Threshold_Score}")
	private double thumbsThresholdScore;
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

	@FXML
	private Label leftSlapQualityScore;

	@FXML
	private Label rightSlapQualityScore;

	@FXML
	private Label thumbsQualityScore;

	@FXML
	private Label leftSlapThresholdScoreLbl;

	@FXML
	private Label rightSlapThresholdScoreLbl;

	@FXML
	private Label thumbsThresholdScoreLbl;

	/** The selected pane. */
	private AnchorPane selectedPane = null;

	/** List of FingerprintDetailsDTOs */
	private List<FingerprintDetailsDTO> fingerprintDetailsDTOs;

	/** List of FingerprintDTOs */
	private List<FingerprintDetailsDTO> fingerprintDTOs;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.debug("REGISTRATION - FINGER_PRINT_CAPTURE_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Loading of FingerprintCapture screen started");
		fingerprintDetailsDTOs = new ArrayList<>();
		fingerprintDTOs = new ArrayList<>();
		selectAnchorPane();

		leftSlapThresholdScoreLbl.setText(String.valueOf(leftHandSlapThresholdScore) + "%");
		rightSlapThresholdScoreLbl.setText(String.valueOf(rightHandSlapThresholdScore) + "%");
		thumbsThresholdScoreLbl.setText(String.valueOf(thumbsThresholdScore) + "%");

		RegistrationDTO registrationDTOContent = (RegistrationDTO) SessionContext.getInstance().getMapObject()
				.get(RegistrationConstants.REGISTRATION_DATA);
		if (null != registrationDTOContent) {
			if (null != registrationDTOContent.getBiometricDTO().getApplicantBiometricDTO()
					.getFingerprintDetailsDTO()) {
				registrationDTOContent.getBiometricDTO().getApplicantBiometricDTO().getFingerprintDetailsDTO()
						.forEach(item -> {
							if (item.getFingerType().equals("LeftPalm")) {
								leftHandPalmImageview
										.setImage(new Image(new ByteArrayInputStream(item.getFingerPrint())));
							} else if (item.getFingerType().equals("RightPalm")) {
								rightHandPalmImageview
										.setImage(new Image(new ByteArrayInputStream(item.getFingerPrint())));
							} else if (item.getFingerType().equals("BothThumbs")) {
								thumbImageview.setImage(new Image(new ByteArrayInputStream(item.getFingerPrint())));
							}
						});
			} else {
				leftHandPalmImageview.setImage(null);
				rightHandPalmImageview.setImage(null);
				thumbImageview.setImage(null);
			}
		}
		LOGGER.debug("REGISTRATION - FINGER_PRINT_CAPTURE_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Loading of FingerprintCapture screen ended");
	}

	/**
	 * {@code selectAnchorPane} to select the anchorpane and to highlight the
	 * selected pane
	 */
	private void selectAnchorPane() {
		LOGGER.debug("REGISTRATION - FINGER_PRINT_CAPTURE_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Selection of Anchorpane for fingerprint capture started");
		Border border = new Border(new BorderStroke(Color.LIGHTGREY, BorderStrokeStyle.SOLID, null, null));
		Border focusedBorder = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, null));

		fingerPrintCapturePane.getChildren().stream().filter(obj -> obj instanceof AnchorPane)
				.map(obj -> (AnchorPane) obj).forEach(anchorPane -> anchorPane.setOnMouseClicked(e -> {
					anchorPane.requestFocus();
					selectedPane = anchorPane;
					anchorPane.borderProperty()
							.bind(Bindings.when(anchorPane.focusedProperty()).then(focusedBorder).otherwise(border));
				}));
		LOGGER.debug("REGISTRATION - FINGER_PRINT_CAPTURE_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Selection of Anchorpane for fingerprint capture ended");
	}

	public void scan() {
		LOGGER.debug("REGISTRATION - FINGER_PRINT_CAPTURE_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Scanning of fingersplaced started");
		if (null != selectedPane) {

			try {
				selectedPane.requestFocus();
				Stage primaryStage = new Stage();
				primaryStage.initStyle(StageStyle.UNDECORATED);
				Parent ackRoot = BaseController
						.load(getClass().getResource(RegistrationConstants.USER_REGISTRATION_BIOMETRIC_CAPTURE_PAGE));
				fingerPrintScanController.init(selectedPane, primaryStage, fingerprintDetailsDTOs, fingerprintDTOs);
				primaryStage.setResizable(false);
				Scene scene = new Scene(ackRoot);
				ClassLoader loader = Thread.currentThread().getContextClassLoader();
				scene.getStylesheets().add(loader.getResource(RegistrationConstants.CSS_FILE_PATH).toExternalForm());
				primaryStage.setScene(scene);
				primaryStage.initModality(Modality.WINDOW_MODAL);
				primaryStage.initOwner(stage);
				primaryStage.show();

			} catch (IOException ioException) {
				LOGGER.error("REGISTRATION - FINGER_PRINT_CAPTURE_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
						ioException.getMessage());
			}
		} else {
			generateAlert(RegistrationConstants.ALERT_INFORMATION, "Please select a pane to continue scan.");
		}
		LOGGER.debug("REGISTRATION - FINGER_PRINT_CAPTURE_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Scanning of fingersplaced ended");
	}

	/**
	 * {@code saveBiometricDetails} is to check the deduplication of captured finger
	 * prints
	 */
	public void saveBiometricDetails() {
		LOGGER.debug("REGISTRATION - FINGER_PRINT_CAPTURE_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Validating captured fingerprints has started");

		if (null != leftHandPalmImageview.getImage() && null != rightHandPalmImageview.getImage()
				&& null != thumbImageview.getImage()) {

			RegistrationDTO registrationDTOContent = (RegistrationDTO) SessionContext.getInstance().getMapObject()
					.get(RegistrationConstants.REGISTRATION_DATA);

			BiometricDTO biometricDTO = new BiometricDTO();
			BiometricInfoDTO biometricInfoDTO = new BiometricInfoDTO();

			biometricInfoDTO.setFingerprintDetailsDTO(fingerprintDTOs);
			biometricDTO.setApplicantBiometricDTO(biometricInfoDTO);

			if (null != registrationDTOContent) {
				registrationDTOContent.setBiometricDTO(biometricDTO);
			}

			fingerPrintCaptureServiceImpl.validateFingerprint(fingerprintDetailsDTOs);

			registrationController.toggleFingerprintCaptureVisibility(false);
			registrationController.toggleIrisCaptureVisibility(true);

		} else {
			generateAlert(RegistrationConstants.ALERT_INFORMATION, "Please scan your Fingers to continue.");
		}
		LOGGER.debug("REGISTRATION - FINGER_PRINT_CAPTURE_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Validating captured fingerprints has ended");
	}

	/**
	 * {@code saveBiometricDetails} is to check the deduplication of captured finger
	 * prints
	 */
	public void goToPreviousPage() {
		LOGGER.debug("REGISTRATION - FINGER_PRINT_CAPTURE_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"going to previous page started");
		registrationController.getDemoGraphicTitlePane().setExpanded(true);
		LOGGER.debug("REGISTRATION - FINGER_PRINT_CAPTURE_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"going to previous page ended");
	}

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

}
