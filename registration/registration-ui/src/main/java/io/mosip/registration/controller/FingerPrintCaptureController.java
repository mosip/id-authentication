package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
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

	@Value("${capture_photo_using_device}")
	public String capturePhotoUsingDevice;

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
	protected AnchorPane leftHandPalmPane;

	/** The right hand palm pane. */
	@FXML
	protected AnchorPane rightHandPalmPane;

	/** The thumb pane. */
	@FXML
	protected AnchorPane thumbPane;

	/** The left hand palm imageview. */
	@FXML
	protected ImageView leftHandPalmImageview;

	/** The right hand palm imageview. */
	@FXML
	protected ImageView rightHandPalmImageview;

	/** The thumb imageview. */
	@FXML
	protected ImageView thumbImageview;

	/** The selected pane. */
	private AnchorPane selectedPane = null;

	/** List of FingerprintDetailsDTO */
	private List<FingerprintDetailsDTO> fingerprintDetailsDTOs;

	private final Border border = new Border(new BorderStroke(Color.LIGHTGREY, BorderStrokeStyle.SOLID, null, null));
	private final Border focusedBorder = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, null));

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
		registrationController.biometricsPane.setVisible(false);
		selectAnchorPane();

		if (SessionContext.getInstance().getMapObject().get("LEFT_PALM_PATH") != null
				|| SessionContext.getInstance().getMapObject().get("RIGHT_PALM_PATH") != null
				|| SessionContext.getInstance().getMapObject().get("THUMB_PATH") != null) {
			leftHandPalmImageview.setImage(fingerPrintScanController
					.loadImage(SessionContext.getInstance().getMapObject().get("LEFT_PALM_PATH").toString()));
			rightHandPalmImageview.setImage(fingerPrintScanController
					.loadImage(SessionContext.getInstance().getMapObject().get("RIGHT_PALM_PATH").toString()));
			thumbImageview.setImage(fingerPrintScanController
					.loadImage(SessionContext.getInstance().getMapObject().get("THUMB_PATH").toString()));
		} else {
			leftHandPalmImageview.setImage(null);
			rightHandPalmImageview.setImage(null);
			thumbImageview.setImage(null);
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
				Parent ackRoot = BaseController.load(getClass().getResource("/fxml/FingerPrintScan.fxml"));
				fingerPrintScanController.init(selectedPane, primaryStage, fingerprintDetailsDTOs);
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
			generateAlert(RegistrationConstants.ALERT_INFORMATION,  "Please select a pane to continue scan.");
		}
		LOGGER.debug("REGISTRATION - FINGER_PRINT_CAPTURE_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Scanning of fingersplaced ended");
	}

	/**
	 * {@code saveBiometricDetails} is to save the captured fingerprints to session
	 * context and check the deduplication
	 */
	public void saveBiometricDetails() {
		LOGGER.debug("REGISTRATION - FINGER_PRINT_CAPTURE_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Saving and Validating of captured fingerprints has started");
		
		if(null!=leftHandPalmImageview.getImage() && null!=rightHandPalmImageview.getImage() && null!=thumbImageview.getImage()) {
			
			RegistrationDTO registrationDTOContent = (RegistrationDTO) SessionContext.getInstance().getMapObject()
				.get(RegistrationConstants.REGISTRATION_DATA);

		BiometricDTO biometricDTO = new BiometricDTO();
		BiometricInfoDTO biometricInfoDTO = new BiometricInfoDTO();

		biometricInfoDTO.setFingerprintDetailsDTO(fingerprintDetailsDTOs);
		biometricDTO.setApplicantBiometricDTO(biometricInfoDTO);

		fingerPrintCaptureServiceImpl.validateFingerprint(fingerprintDetailsDTOs);

		if (capturePhotoUsingDevice.equals("Y")) {
			registrationController.biometricsPane.setVisible(true);
		} else {
			registrationController.biometricsPane.setVisible(false);
		}
		if (null != registrationDTOContent) {
			registrationDTOContent.setBiometricDTO(biometricDTO);
		}

		fingerPrintCapturePane.setVisible(false);
		}else {
			generateAlert(RegistrationConstants.ALERT_INFORMATION,  "Please scan your Fingers to continue.");
		}
		LOGGER.debug("REGISTRATION - FINGER_PRINT_CAPTURE_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Saving and Validating of captured fingerprints has ended");
	}

}
