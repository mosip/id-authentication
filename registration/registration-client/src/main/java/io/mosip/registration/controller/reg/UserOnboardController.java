package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.device.FingerPrintCaptureController;
import io.mosip.registration.controller.device.IrisCaptureController;
import io.mosip.registration.dto.biometric.BiometricDTO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
	
	@FXML
	private Label operatorName;
	
	@Autowired
	private UserOnboardParentController userOnboardParentController;
	
	@Autowired
	private FingerPrintCaptureController fingerPrintCaptureController;

	@Autowired
	private IrisCaptureController irisCaptureController;

	private BiometricDTO biometricDTO;	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		operatorName
		.setText(SessionContext.userContext().getRegistrationCenterDetailDTO().getRegistrationCenterName());
	}

	@FXML
	public void initUserOnboard() {		
		biometricDTO = new BiometricDTO();
		biometricDTO.setOperatorBiometricDTO(createBiometricInfoDTO());
		SessionContext.map().put(RegistrationConstants.USER_ONBOARD_DATA, biometricDTO);		
		userOnboardParentController.showCurrentPage(RegistrationConstants.ONBOARD_USER_PARENT, getOnboardPageDetails(RegistrationConstants.ONBOARD_USER_PARENT,RegistrationConstants.NEXT));
		clearAllValues();
	}
}