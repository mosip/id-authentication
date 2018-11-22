package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.entity.RegistrationUserDetail;
import io.mosip.registration.service.LoginService;
import io.mosip.registration.util.biometric.FingerprintFacade;
import io.mosip.registration.util.biometric.FingerprintProviderNew;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

@Controller
public class FingerPrintAuthenticationController extends BaseController implements Initializable {

	@FXML
	private AnchorPane authenticateRootPane;
	@FXML
	private AnchorPane authenticateRootSubPane;
	@FXML
	private AnchorPane leftPalmAnchorPane;
	@FXML
	private AnchorPane rightPalmAnchorPane;
	@FXML
	private ImageView leftPalmImageView;
	@FXML
	private ImageView rightPalmImageView;
	@FXML
	private Button scanBtn;
	@FXML
	private ProgressIndicator scanProgress;

	@FXML
	private ComboBox<String> deviceCmbBox;
	@FXML
	private ImageView fingerScannedImage;

	@Autowired
	LoginService loginService;

	@Value("${FINGER_PRINT_SCORE}")
	private long fingerPrintScore;

	/**
	 * Instance of {@link MosipLogger}
	 */
	private Logger LOGGER = AppConfig.getLogger(FingerPrintAuthenticationController.class);

	/**
	 * Stage
	 */
	private Stage primaryStage;

	@Value("${QUALITY_SCORE}")
	private int qualityScore;

	@Value("${CAPTURE_TIME_OUT}")
	private int captureTimeOut;

	@Value("${DEVICE_NAME}")
	private String deviceName;

	@Autowired
	private BaseController baseController;

	private FingerprintFacade fingerprintFactory = new FingerprintFacade();

	public void init(BaseController parentControllerObj) {
		baseController = parentControllerObj;
	}

	/**
	 * Scan the finger and validate with the database
	 * 
	 * @param event
	 */
	public void scanFinger(ActionEvent event) {
		LOGGER.debug("REGISTRATION - SCAN_FINGER - USER_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Start the device to scan the finger");
		primaryStage = (Stage) ((Node) event.getSource()).getParent().getScene().getWindow();
		FingerprintProviderNew fingerprintProviderNew = fingerprintFactory.getFingerprintProviderFactory(deviceName);
		fingerprintProviderNew.captureFingerprint(qualityScore, captureTimeOut,
				RegistrationConstants.FINGER_TYPE_MINUTIA);
		int count = 0;
		while (count < 5) {
			if (!RegistrationConstants.EMPTY.equals(fingerprintFactory.getMinutia())
					|| !RegistrationConstants.EMPTY.equals(fingerprintFactory.getErrorMessage())) {
				break;
			} else {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					LOGGER.error("FINGERPRINT_AUTHENTICATION_CONTROLLER - ERROR_SCANNING_FINGER", APPLICATION_NAME,
							APPLICATION_ID, e.getMessage());
				}
			}
			count++;
		}
		LOGGER.debug("REGISTRATION - SCAN_FINGER - SCAN_FINGER_COMPLETED", APPLICATION_NAME, APPLICATION_ID,
				"Fingerprint scan done");
		fingerScannedImage.setImage(fingerprintFactory.getFingerPrintImage());
		RegistrationUserDetail registrationUserDetail = loginService
				.getUserDetail(SessionContext.getInstance().getUserContext().getUserId());
		if (!RegistrationConstants.EMPTY.equals(fingerprintFactory.getMinutia())) {
			boolean isValidFingerPrint = registrationUserDetail.getUserBiometric().stream()
					.anyMatch(bio -> fingerprintProviderNew.scoreCalculator(fingerprintFactory.getMinutia(),
							bio.getBioMinutia()) > fingerPrintScore);
			if (isValidFingerPrint) {
				generateAlert("Info", AlertType.INFORMATION, "Records approved Successfully.");
				primaryStage.close();
				baseController.getFingerPrintStatus();
			} else {
				generateAlert("Info", AlertType.INFORMATION, "Fingerprint Mismatch");
				primaryStage.close();
			}
		} else if (!RegistrationConstants.EMPTY.equals(fingerprintFactory.getErrorMessage())) {
			if (fingerprintFactory.getErrorMessage().equals("Timeout")) {
				generateAlert("Info", AlertType.INFORMATION, "Fingerprint got timedout. Please try again.");
			} else {
				generateAlert("Info", AlertType.INFORMATION, "Error in fingerprint scan");
			}
		}
		LOGGER.debug("REGISTRATION - SCAN_FINGER - FINGER_VALIDATION", APPLICATION_NAME, APPLICATION_ID,
				"Fingerprint validation done");

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		deviceCmbBox.getItems().clear();
		deviceCmbBox.setItems(FXCollections.observableArrayList(RegistrationConstants.ONBOARD_DEVICE_TYPES));
		deviceCmbBox.getSelectionModel().selectFirst();
	}

	/**
	 * event class to exit from authentication window. pop up window.
	 * 
	 * @param event
	 */
	public void exitWindow(ActionEvent event) {
		primaryStage = (Stage) ((Node) event.getSource()).getParent().getScene().getWindow();
		primaryStage.close();

	}

}
