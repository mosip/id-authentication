package io.mosip.registration.controller.device;

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

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.util.biometric.FingerprintFacade;
import io.mosip.registration.util.biometric.MosipFingerprintProvider;
import io.mosip.registration.validator.AuthenticationService;
import io.mosip.registration.validator.AuthenticationValidatorImplementation;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * 
 * @author M1046564
 *
 */
@Controller
public class FingerPrintAuthenticationController extends BaseController implements Initializable {
	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(FingerPrintAuthenticationController.class);
	
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

	@Value("${FINGER_PRINT_SCORE}")
	private long fingerPrintScore;

	/**
	 * Stage
	 */
	private Stage primaryStage;

	@Value("${QUALITY_SCORE}")
	private int qualityScore;

	@Value("${CAPTURE_TIME_OUT}")
	private int captureTimeOut;

	@Value("${PROVIDER_NAME}")
	private String providerName;

	@Autowired
	private BaseController baseController;

	private FingerprintFacade fingerprintFacade = null;

	@Autowired
	private AuthenticationService validator;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		deviceCmbBox.getItems().clear();
		deviceCmbBox.setItems(FXCollections.observableArrayList(RegistrationConstants.ONBOARD_DEVICE_TYPES));
		deviceCmbBox.getSelectionModel().selectFirst();
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

		try {

			MosipFingerprintProvider fingerPrintConnector = fingerprintFacade
					.getFingerprintProviderFactory(providerName);
			int statusCode = fingerPrintConnector.captureFingerprint(qualityScore, captureTimeOut, "");
			if (statusCode != 0) {

				generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.DEVICE_FP_NOT_FOUND);

			} else {

				// Thread to wait until capture the bio image/ minutia from FP. based on the
				// error code or success code the respective action will be taken care.
				waitToCaptureBioImage(5, 2000, fingerprintFacade);

				LOGGER.debug("REGISTRATION - SCAN_FINGER - SCAN_FINGER_COMPLETED", APPLICATION_NAME, APPLICATION_ID,
						"Fingerprint scan done");
				fingerPrintConnector.uninitFingerPrintDevice();
				fingerScannedImage.setImage(fingerprintFacade.getFingerPrintImage());

				if (!RegistrationConstants.EMPTY.equals(fingerprintFacade.getMinutia())) {
					// if FP data fetched then retrieve the user specific detail from db.
					AuthenticationValidatorDTO authenticationValidatorDTO=new AuthenticationValidatorDTO();
					List<FingerprintDetailsDTO> fingerprintDetailsDTOs=new ArrayList<FingerprintDetailsDTO>();
					FingerprintDetailsDTO fingerprintDetailsDTO=new FingerprintDetailsDTO();
					fingerprintDetailsDTO.setFingerPrint(fingerprintFacade.getIsoTemplate());
					fingerprintDetailsDTOs.add(fingerprintDetailsDTO);
					authenticationValidatorDTO.setFingerPrintDetails(fingerprintDetailsDTOs);
					authenticationValidatorDTO.setUserId(SessionContext.getInstance().getUserContext().getUserId());
					AuthenticationValidatorImplementation authenticationValidatorImplementation=validator.getValidator("Fingerprint");
					authenticationValidatorImplementation.setFingerPrintType("single");
					/*RegistrationUserDetail registrationUserDetail = userDataService
							.getUserDetail("mosip");

					boolean isValidFingerPrint = registrationUserDetail.getUserBiometric().stream()
							.anyMatch(bio -> fingerPrintConnector.scoreCalculator(fingerprintFacade.getMinutia(),
									bio.getBioMinutia()) > fingerPrintScore);*/

					if (authenticationValidatorImplementation.validate(authenticationValidatorDTO)) {
						baseController.getFingerPrintStatus(primaryStage);
					} else {
						generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationConstants.AUTHENTICATION_FAILURE);
						primaryStage.close();
					}
				} else if (!RegistrationConstants.EMPTY.equals(fingerprintFacade.getErrorMessage())) {
					if (fingerprintFacade.getErrorMessage().equals("Timeout")) {
						generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationConstants.FP_DEVICE_TIMEOUT);
					} else {
						generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationConstants.FP_DEVICE_ERROR);
					}
				}
				LOGGER.debug("REGISTRATION - SCAN_FINGER - FINGER_VALIDATION", APPLICATION_NAME, APPLICATION_ID,
						"Fingerprint validation done");
			}
		} catch (IOException e) {
			LOGGER.debug("REGISTRATION - SCAN_FINGER - FINGER_VALIDATION_ERROR", APPLICATION_NAME, APPLICATION_ID,
					e.getMessage());
		}

	}

	/**
	 * Setting the init method to the Basecontroller
	 * 
	 * @param parentControllerObj
	 */
	public void init(BaseController parentControllerObj) {
		baseController = parentControllerObj;
		fingerprintFacade = new FingerprintFacade();
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
