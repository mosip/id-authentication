package io.mosip.registration.controller.device;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_IRIS_CAPTURE_CONTROLLER;
import static io.mosip.registration.constants.LoggerConstants.LOG_REG_SCAN_CONTROLLER;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.controller.BaseController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

@Controller
public class ScanController extends BaseController {
	private static final Logger LOGGER = AppConfig.getLogger(ScanController.class);

	@Autowired
	private BaseController baseController;
	
	@FXML
	private ImageView scanImage;
	
	@FXML
	private Label popupTitle;
		
	/**
	 * @return the scanImage
	 */
	public ImageView getScanImage() {
		return scanImage;
	}

	private Stage popupStage; 
	
	/**
	 * This method will open popup to scan
	 * @param parentControllerObj
	 * @param title
	 */
	public void init(BaseController parentControllerObj, String title)  {
		
		try {
			
			LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Opening pop-up screen to scan for user registration");
			
			baseController = parentControllerObj;
			popupStage = new Stage();
			popupStage.initStyle(StageStyle.UNDECORATED);
			Parent scanPopup = BaseController
					.load(getClass().getResource(RegistrationConstants.SCAN_PAGE));
			popupStage.setResizable(false);
			popupTitle.setText(title);
			Scene scene = new Scene(scanPopup);
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			scene.getStylesheets().add(loader.getResource(RegistrationConstants.CSS_FILE_PATH).toExternalForm());
			popupStage.setScene(scene);
			popupStage.initModality(Modality.WINDOW_MODAL);
			popupStage.initOwner(fXComponents.getStage());
			popupStage.show();
			
			LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Opening pop-up screen to scan for user registration");
			
		} catch (IOException ioException) {
			LOGGER.error(LOG_REG_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, String.format(
					"%s -> Exception while Opening pop-up screen to capture in user registration  %s -> %s",
					RegistrationConstants.USER_REG_SCAN_EXP, ioException.getMessage(),
					ioException.getCause()));

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.UNABLE_LOAD_SCAN_POPUP);
		}
		
	}
	
	/**
	 * This method will allow to scan
	 */
	@FXML
	public void scan() {
		
		LOGGER.debug(LOG_REG_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
				"Invoke scan method for the passed controller");
		
		baseController.scan(popupStage);
	}
	
	/**
	 * event class to exit from present pop up window.
	 * 
	 * @param event
	 */
	public void exitWindow(ActionEvent event) {
		
		LOGGER.debug(LOG_REG_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
				"Calling exit window to close the popup");
		
		popupStage = (Stage) ((Node) event.getSource()).getParent().getScene().getWindow();
		popupStage.close();
		
		LOGGER.debug(LOG_REG_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
				"Popup is closed");

	}
}
