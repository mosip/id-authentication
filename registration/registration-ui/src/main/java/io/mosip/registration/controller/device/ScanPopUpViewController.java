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
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.reg.DocumentScanController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

@Controller
public class ScanPopUpViewController extends BaseController {
	private static final Logger LOGGER = AppConfig.getLogger(ScanPopUpViewController.class);

	@Autowired
	private BaseController baseController;

	@FXML
	private ImageView scanImage;

	@FXML
	private Label popupTitle;

	@FXML
	private Text totalScannedPages;

	@FXML
	private Button saveBtn;

	@FXML
	private Label scannedPagesLabel;

	@FXML
	private Text scanningMsg;

	private boolean isDocumentScan;

	/**
	 * @return the scanImage
	 */
	public ImageView getScanImage() {
		return scanImage;
	}

	private Stage popupStage;

	/**
	 * This method will open popup to scan
	 * 
	 * @param parentControllerObj
	 * @param title
	 */
	public void init(BaseController parentControllerObj, String title) {

		try {

			LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Opening pop-up screen to scan for user registration");

			baseController = parentControllerObj;
			popupStage = new Stage();
			popupStage.initStyle(StageStyle.UNDECORATED);
			Parent scanPopup = BaseController.load(getClass().getResource(RegistrationConstants.SCAN_PAGE));
			popupStage.setResizable(false);
			popupTitle.setText(title);
			Scene scene = new Scene(scanPopup);
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			scene.getStylesheets().add(loader.getResource(RegistrationConstants.CSS_FILE_PATH).toExternalForm());
			popupStage.setScene(scene);
			popupStage.initModality(Modality.WINDOW_MODAL);
			popupStage.initOwner(fXComponents.getStage());
			popupStage.show();

			if (!isDocumentScan) {
				totalScannedPages.setVisible(false);
				saveBtn.setVisible(false);
				scannedPagesLabel.setVisible(false);
				scanningMsg.setVisible(false);
			}
			LOGGER.debug(LOG_REG_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Opening pop-up screen to scan for user registration");

		} catch (IOException ioException) {
			LOGGER.error(LOG_REG_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format(
							"%s -> Exception while Opening pop-up screen to capture in user registration  %s -> %s",
							RegistrationConstants.USER_REG_SCAN_EXP, ioException.getMessage(), ioException.getCause()));

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.UNABLE_LOAD_SCAN_POPUP);
		}

	}

	/**
	 * This method will allow to scan
	 */
	@FXML
	public void scan() {
		scanningMsg.setVisible(true);
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

		LOGGER.debug(LOG_REG_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Popup is closed");

	}

	@FXML
	private void save() {
		if (baseController instanceof DocumentScanController) {
			DocumentScanController documentScanController = (DocumentScanController) baseController;
			try {
				documentScanController.attachScannedDocument(popupStage);
			} catch (IOException e) {
				LOGGER.error(LOG_REG_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, e.getMessage());
			}
		}

	}

	public boolean isDocumentScan() {
		return isDocumentScan;
	}

	public void setDocumentScan(boolean isDocumentScan) {
		this.isDocumentScan = isDocumentScan;
	}

	public Text getTotalScannedPages() {
		return totalScannedPages;
	}

	public void setTotalScannedPages(Text totalScannedPages) {
		this.totalScannedPages = totalScannedPages;
	}

	public Text getScanningMsg() {
		return scanningMsg;
	}

	public void setScanningMsg(Text scanningMsg) {
		this.scanningMsg = scanningMsg;
	}

}
