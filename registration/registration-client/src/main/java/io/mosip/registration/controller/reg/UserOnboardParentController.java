package io.mosip.registration.controller.reg;

import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.controller.BaseController;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

@Controller
public class UserOnboardParentController extends BaseController{
	
	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(UserOnboardController.class);
	
	@FXML
	private AnchorPane userOnboardId;
	
	@FXML
	private AnchorPane onBoardRoot;
	
	@FXML
	private AnchorPane onboardUser;
	@FXML
	private AnchorPane biometricException;
	
	
	public void showCurrentPage(String notTosShow, String show) {
		
		LOGGER.debug(LoggerConstants.LOG_REG_PARENT_USER_ONBOARD, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Navigating to next page based on the current page");
		
		getCurrentPage(userOnboardId, notTosShow, show);
		
		LOGGER.debug(LoggerConstants.LOG_REG_PARENT_USER_ONBOARD, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Navigated to next page based on the current page");
		
	}

}
