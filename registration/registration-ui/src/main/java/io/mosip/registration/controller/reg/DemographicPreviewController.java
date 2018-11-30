package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.controller.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

/**
 * This controller class is to handle the preview screen of the Demographic
 * details
 * 
 * @author M1046540
 *
 */
@Controller
public class DemographicPreviewController extends BaseController {

	@FXML
	private VBox demoGraphicVbox;

	@FXML
	private Button nextBtn;

	@FXML
	private Button editBtn;

	@FXML
	private TitledPane demographicPreview;

	private boolean isInPane1;

	@Autowired
	private RegistrationController registrationController;
	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(DemographicPreviewController.class);

	@FXML
	private void initialize() {
		LOGGER.debug("REGISTRATION_PREVIEW_CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Entering the REGISTRATION_PREVIEW_CONTROLLER");
		isInPane1 = true;
		demographicPreview.setDisable(true);
		demoGraphicVbox.getChildren().add(RegistrationController.getDemoGraphicContent());

	}

	/**
	 * This method is used to handle the edit action of registration preview screen
	 */
	public void handleEdit() {
		try {
			RegistrationController.setEditPage(true);
			RegistrationController.loadScreen(RegistrationConstants.CREATE_PACKET_PAGE);
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - UI-  Preview ", APPLICATION_NAME, APPLICATION_ID, ioException.getMessage());
		}
	}

	/**
	 * This method is used to handle the next button action of registration preview
	 * screen
	 */
	public void handleNextBtnAction() {
		try {
			if (isInPane1) {
				demoGraphicVbox.getChildren().clear();
				RegistrationController.getDemoGraphicPane2Content().setVisible(true);
				demoGraphicVbox.getChildren().add(RegistrationController.getDemoGraphicPane2Content());
				isInPane1 = false;
			} else {
				isInPane1 = true;
				RegistrationController.loadScreen(RegistrationConstants.BIOMETRIC_PREVIEW);
			}
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - UI- Demographic Preview ", APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage());
		}
	}

	/**
	 * This method is used to navigate the screen to home page
	 */
	public void goToHomePage() {
		registrationController.goToHomePage();
	}

}
