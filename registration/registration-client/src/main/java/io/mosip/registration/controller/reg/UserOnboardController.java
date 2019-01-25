package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.BiometricInfoDTO;
import io.mosip.registration.dto.biometric.FaceDetailsDTO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;

/**
 * {@code UserOnboardController} is to capture and display the captured
 * fingerprints,Iris and face.
 * 
 * @author Dinesh Ashokan
 * @version 1.0
 *
 */
@Controller
public class UserOnboardController extends BaseController implements Initializable {

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(UserOnboardController.class);

	

	private BiometricDTO biometricDTO;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	@FXML
	public void initUserOnboard() {		
		biometricDTO = new BiometricDTO();
		biometricDTO.setOperatorBiometricDTO(createBiometricInfoDTO());
		SessionContext.getInstance().getMapObject().put(RegistrationConstants.USER_ONBOARD_DATA, biometricDTO);		
		loadPage(RegistrationConstants.BIO_EXCEPTION_PAGE);
		clearAllValues();
	}

	/**
	 * Method to load the biometric fingerprint page
	 */
	public void loadFingerPrint() {
		loadPage(RegistrationConstants.USER_ONBOARD_FP);
	}

	/**
	 * Method to load fxml page
	 * 
	 * @param fxml file name
	 */
	private void loadPage(String page) {
		Parent createRoot;
		try {
			createRoot = BaseController.load(getClass().getResource(page));
			getScene(createRoot).setRoot(createRoot);
		} catch (IOException exception) {
			LOGGER.error("REGISTRATION - USERONBOARD CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
					exception.getMessage());
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_USERONBOARD_SCREEN);
		}
	}

}