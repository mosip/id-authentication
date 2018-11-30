package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.auth.LoginController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Class for Home Page 
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@Component
public class RegistrationOfficerController extends BaseController implements Initializable {
	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(RegistrationOfficerController.class);

	@FXML
	private VBox mainBox;

	/**
	 * Building Home screen on Login success
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {

			LOGGER.debug("REGISTRATION - REGSITRATION_HOME_PAGE_LAYOUT", APPLICATION_NAME,
					APPLICATION_ID, "Constructing Registration Home Page");

			HBox headerRoot = BaseController.load(getClass().getResource(RegistrationConstants.HEADER_PAGE));
			mainBox.getChildren().add(headerRoot);
			AnchorPane updateRoot = BaseController.load(getClass().getResource(RegistrationConstants.UPDATE_PAGE));
			mainBox.getChildren().add(updateRoot);
			AnchorPane optionRoot = BaseController
					.load(getClass().getResource(RegistrationConstants.OFFICER_PACKET_PAGE));
			mainBox.getChildren().add(optionRoot);

			LoginController.getScene().setRoot(mainBox);
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			LoginController.getScene().getStylesheets()
					.add(loader.getResource(RegistrationConstants.CSS_FILE_PATH).toExternalForm());

		} catch (IOException | RuntimeException exception) {
			
			LOGGER.error("REGISTRATION - HOME_PAGE - REGISTRATION_OFFICER_CONTROLLER", APPLICATION_NAME,
					APPLICATION_ID, exception.getMessage());
			
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.UNABLE_LOAD_HOME_PAGE);
		}
	}
}
