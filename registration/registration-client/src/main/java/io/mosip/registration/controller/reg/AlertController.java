package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.LoggerConstants.LOG_ALERT_GENERATION;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.device.ScanPopUpViewController;
import io.mosip.registration.controller.eodapproval.RegistrationApprovalController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

@Component
public class AlertController extends BaseController implements Initializable {

	private static final Logger LOGGER = AppConfig.getLogger(AlertController.class);
	@FXML
	private ImageView alertImage;
	@FXML
	private Label header;
	@FXML
	private Label context;
	@FXML
	private Hyperlink exit;
	@FXML
	private HBox alertHbox;
	@FXML
	private VBox imageVbox;
	@FXML
	private VBox contextVbox;
	@FXML
	private GridPane alertGridPane;
	@Autowired
	private ScanPopUpViewController scanPopUpViewController;
	@Autowired
	private RegistrationApprovalController registrationApprovalController;
	
	/**
	 * @return the alertGridPane
	 */
	public GridPane getAlertGridPane() {
		return alertGridPane;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		fXComponents.getScene().getRoot().setDisable(true);
		
		if (scanPopUpViewController.getPopupStage() != null
				&& scanPopUpViewController.getPopupStage().isShowing()) {
			scanPopUpViewController.getPopupStage().getScene().getRoot().setDisable(true);
		} else if (registrationApprovalController.getPrimaryStage() != null
				&& registrationApprovalController.getPrimaryStage().isShowing()) {
			registrationApprovalController.getPrimaryStage().getScene().getRoot().setDisable(true);
		}
	}

	
	public void generateAlertResponse(String title, String contextString) {
		LOGGER.info(LOG_ALERT_GENERATION, APPLICATION_NAME, APPLICATION_ID, "Alert generation has been started");

		String[] split = contextString.split(RegistrationConstants.SPLITTER);
		String contextSecondMsg = RegistrationConstants.EMPTY;
		Image image;
		if (split[1].contains(RegistrationConstants.SUCCESS.toUpperCase())) {
			image = new Image(RegistrationConstants.SUCCESS_IMG_PTH);
			header.setText(RegistrationUIConstants.SUCCESS);
			alertImage.setImage(image);
			contextSecondMsg = gettingSecondErrorMessage(split,RegistrationConstants.SUCCESS.toUpperCase());
		} else if(split[1].contains(RegistrationConstants.ERROR.toUpperCase())){
			image = new Image(RegistrationConstants.FAILURE_IMG_PTH);
			header.setText(RegistrationUIConstants.ALERT_FAILED_LABEL);
			alertImage.setImage(image);
			contextSecondMsg = gettingSecondErrorMessage(split,RegistrationConstants.ERROR.toUpperCase());
		}else if(split[1].contains(RegistrationConstants.INFO)){
			header.setText(RegistrationUIConstants.ALERT_NOTE_LABEL);
			alertImage.setDisable(false);
			alertImage.setVisible(false);
			alertImage.setManaged(false);
			alertHbox.setManaged(false);
			imageVbox.setVisible(false);
			imageVbox.setManaged(false);
			contextSecondMsg = gettingSecondErrorMessage(split,RegistrationConstants.INFO);
		}
		context.setText(split[0].trim()+contextSecondMsg);
		
		if(context.getText().length()>50) {
			imageVbox.setAlignment(Pos.TOP_CENTER);
		}
		
		LOGGER.info(LOG_ALERT_GENERATION, APPLICATION_NAME, APPLICATION_ID, "Alert generation has been ended");

	}

	private String gettingSecondErrorMessage(String[] split,String splitter) {
		StringBuilder errorMessage=new StringBuilder();
		for(int i=1;i<split.length;i++) {
			errorMessage = errorMessage.append(split[i].replaceAll(splitter,RegistrationConstants.SPACE));
		}
		return errorMessage.toString();
	}
	
	@FXML
	private void alertWindowExit() {
		LOGGER.info(LOG_ALERT_GENERATION, APPLICATION_NAME, APPLICATION_ID, "Alert closing has been started");

		Stage stage = (Stage) exit.getScene().getWindow();
		stage.close();
		fXComponents.getScene().getRoot().setDisable(false);
		if (scanPopUpViewController.getPopupStage() != null
				&& scanPopUpViewController.getPopupStage().isShowing()) {
			scanPopUpViewController.getPopupStage().getScene().getRoot().setDisable(false);
		} else if (registrationApprovalController.getPrimaryStage() != null
				&& registrationApprovalController.getPrimaryStage().isShowing()) {
			registrationApprovalController.getPrimaryStage().getScene().getRoot().setDisable(false);
		}
		LOGGER.info(LOG_ALERT_GENERATION, APPLICATION_NAME, APPLICATION_ID, "Alert closing has been ended");

	}
}
