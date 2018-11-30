package io.mosip.registration.controller.auth;

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
import io.mosip.registration.controller.reg.RegistrationOfficerPacketController;
import io.mosip.registration.dto.RegistrationDTO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

@Controller
public class OperatorAuthenticationController implements Initializable {
	
	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(OperatorAuthenticationController.class);
	
	@FXML
	private AnchorPane temporaryLogin;
	@FXML
	private AnchorPane pwdBasedROLogin;
	@FXML
	private AnchorPane otpBasedROLogin;
	@FXML
	private AnchorPane fingerprintBasedROLogin;
	@FXML
	private Label otpValidity;
	
	@Value("${capture_photo_using_device}")
	public String capturePhotoUsingDevice;
	
	@Value("${otp_validity_in_mins}")
	private long otpValidityInMins;
	
	@Autowired
	private RegistrationOfficerPacketController registrationOfficerPacketController;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.debug("REGISTRATION_OPERATOR_AUTHENTICATION_CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Entering the Operator Authentication Page");
		
		otpValidity.setText("Valid for " + otpValidityInMins + " minutes");
		temporaryLogin.setVisible(true);
	}
	
	public void validatePwd() {
		pwdBasedROLogin.setVisible(false);
		otpBasedROLogin.setVisible(true);
	}

	public void validateOTP() {
		otpBasedROLogin.setVisible(false);
		fingerprintBasedROLogin.setVisible(true);
	}

	public void validateFingerprint() {
		// to-do
	}

	public void submitRegistration() {
		registrationOfficerPacketController.showReciept((RegistrationDTO) SessionContext.getInstance().getMapObject()
				.get(RegistrationConstants.REGISTRATION_DATA), capturePhotoUsingDevice);
	}
}
