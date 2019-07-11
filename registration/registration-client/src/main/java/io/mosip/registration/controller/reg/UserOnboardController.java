package io.mosip.registration.controller.reg;

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
import io.mosip.registration.dto.biometric.BiometricDTO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * {@code UserOnboardController} is to initialize user onboard 
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		operatorName.setText(RegistrationUIConstants.USER_ONBOARD_HI + " " + SessionContext.userContext().getName()
				+ ", " + RegistrationUIConstants.USER_ONBOARD_NOTONBOARDED);
	}

	@FXML
	public void initUserOnboard() {
		clearOnboard();
		BiometricDTO biometricDTO = new BiometricDTO();
		biometricDTO.setOperatorBiometricDTO(createBiometricInfoDTO());
		SessionContext.map().put(RegistrationConstants.USER_ONBOARD_DATA, biometricDTO);
		userOnboardParentController.showCurrentPage(RegistrationConstants.ONBOARD_USER_PARENT,
				getOnboardPageDetails(RegistrationConstants.ONBOARD_USER_PARENT, RegistrationConstants.NEXT));
		clearAllValues();
		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "User Onboard Controller initUserOnboard Method Exit");
	}

	public void clearOnboard() {
		SessionContext.map().remove(RegistrationConstants.USER_ONBOARD_DATA);
		SessionContext.map().remove(RegistrationConstants.OLD_BIOMETRIC_EXCEPTION);
		SessionContext.map().remove(RegistrationConstants.NEW_BIOMETRIC_EXCEPTION);
	}
}