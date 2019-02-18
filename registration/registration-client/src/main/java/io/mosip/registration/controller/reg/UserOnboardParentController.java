package io.mosip.registration.controller.reg;

import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
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
		
		if(notTosShow != null) {
			((AnchorPane) userOnboardId.lookup("#"+notTosShow)).setVisible(false);
		}
		if(show != null) {
			((AnchorPane) userOnboardId.lookup("#"+show)).setVisible(true);
		}
	}

}
