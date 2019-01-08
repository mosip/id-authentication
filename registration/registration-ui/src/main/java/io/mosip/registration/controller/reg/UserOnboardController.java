package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.controller.BaseController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author Sreekar Chukka
 *
 * @since 1.0.0
 */
@Controller
public class UserOnboardController extends BaseController implements Initializable {
	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(HomeController.class);

	@FXML
	private VBox mainBox;

	/**
	 * Building Home screen on Login success
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}

	public void loadFingerPrint() {
		System.out.println("Hello");
		//Parent createRoot  =  BaseController.load(getClass().getResource(RegistrationConstants.ONBOARDING_EXCPTION));
		System.out.println("Hello1");
		//getScene(createRoot);
	}

	/**
	 * @return the mainBox
	 */
	public VBox getMainBox() {
		return mainBox;
	}

	/**
	 * @param mainBox the mainBox to set
	 */
	public void setMainBox(VBox mainBox) {
		this.mainBox = mainBox;
	}
}
