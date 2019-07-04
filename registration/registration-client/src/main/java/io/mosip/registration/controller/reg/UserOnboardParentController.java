package io.mosip.registration.controller.reg;

import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.controller.BaseController;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;


/**
 * {@code UserOnboardParentController} is to load FXML of
 * fingerprints,Iris and face.
 * 
 * @author Sravya Surampalli
 * @version 1.0
 *
 */
@Controller
public class UserOnboardParentController extends BaseController{
	
	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(UserOnboardParentController.class);
	
	@FXML
	protected GridPane userOnboardId;
	
	
	public void showCurrentPage(String notTosShow, String show) {
		
		LOGGER.debug(LoggerConstants.LOG_REG_PARENT_USER_ONBOARD, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Navigating to next page based on the current page" + notTosShow + " ::: Show " + show);
		
		getCurrentPage(userOnboardId, notTosShow, show);
		
		LOGGER.debug(LoggerConstants.LOG_REG_PARENT_USER_ONBOARD, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Navigated to next page based on the current page");
		
	}

}
