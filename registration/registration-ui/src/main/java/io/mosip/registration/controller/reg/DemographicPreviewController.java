package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
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
		demoGraphicVbox.getChildren().add(getDemoGraphicPane1Content());

	}

	private void loadScreen(String screen) throws IOException {
		Parent createRoot = BaseController.load(RegistrationController.class.getResource(screen),
				applicationContext.getApplicationLanguageBundle());
		getScene(createRoot);
	}

	/**
	 * This method is used to handle the edit action of registration preview
	 * screen
	 */
	public void handleEdit() {
		try {
			SessionContext.getInstance().getMapObject().put(RegistrationConstants.REGISTRATION_ISEDIT, true);
			loadScreen(RegistrationConstants.CREATE_PACKET_PAGE);
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - UI-  Preview ", APPLICATION_NAME, APPLICATION_ID, ioException.getMessage());
		}
	}

	/**
	 * This method is used to handle the next button action of registration
	 * preview screen
	 */
	public void handleNextBtnAction() {
		try {
			if (isInPane1) {
				demoGraphicVbox.getChildren().clear();
				getDemoGraphicPane2Content().setVisible(true);
				demographicPreview.setDisable(false);
				AnchorPane demoGraphicPane2 = getDemoGraphicPane2Content();
				demoGraphicPane2.lookup("#poaDocuments").setDisable(true);
				demoGraphicPane2.lookup("#poiDocuments").setDisable(true);
				demoGraphicPane2.lookup("#porDocuments").setDisable(true);
				demoGraphicPane2.lookup("#dobDocuments").setDisable(true);
				demoGraphicPane2.lookup("#biometricException").setDisable(true);
				demoGraphicPane2.lookup("#childSpecificFields").setDisable(true);
				String vBoxesId[] = { "#poaBox", "#poiBox", "#porBox", "#dobBox" };
				for (String vBoxId : vBoxesId) {
					VBox vbox = (VBox) demoGraphicPane2.lookup(vBoxId);
					for (Node node : vbox.getChildren()) {
						GridPane paneGrid = (GridPane) node;
						for (Node gridNode : paneGrid.getChildren()) {
							if (gridNode instanceof ImageView) {
								gridNode.setVisible(false);
							}
						}
					}
				}

				demoGraphicVbox.getChildren().add(demoGraphicPane2);
				isInPane1 = false;
			} else {
				isInPane1 = true;
				loadScreen(RegistrationConstants.BIOMETRIC_PREVIEW);
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

	private AnchorPane getDemoGraphicPane1Content() {
		return (AnchorPane) SessionContext.getInstance().getMapObject()
				.get(RegistrationConstants.REGISTRATION_PANE1_DATA);
	}

	private AnchorPane getDemoGraphicPane2Content() {
		return (AnchorPane) SessionContext.getInstance().getMapObject()
				.get(RegistrationConstants.REGISTRATION_PANE2_DATA);
	}

}
