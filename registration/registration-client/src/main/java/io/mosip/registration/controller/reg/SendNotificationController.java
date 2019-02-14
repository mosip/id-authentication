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
import io.mosip.registration.dto.ResponseDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

@Controller
public class SendNotificationController extends BaseController implements Initializable {

	private static final Logger LOGGER = AppConfig.getLogger(SendNotificationController.class);

	@FXML
	private TextField email;
	@FXML
	private TextField mobile;
	@FXML
	private ImageView emailImageView;
	@FXML
	private ImageView mobileImageView;
	
	private Image deSelectImage;
	
	private boolean selectedEmail = false;
	private boolean selectedMobile = false;

	private Image selectImage;
	
	private Stage popupStage;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		deSelectImage = emailImageView.getImage();
		selectImage = new Image(getClass().getResourceAsStream("/images/GreenRoundTick.png"));
	}

	public void init() {
		try {
			popupStage = new Stage();
			popupStage.initStyle(StageStyle.UNDECORATED);
			Parent sendEmailPopup = BaseController
					.load(getClass().getResource(RegistrationConstants.SEND_NOTIFICATION_PAGE));
			popupStage.setResizable(false);
			Scene scene = new Scene(sendEmailPopup);
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			scene.getStylesheets().add(loader.getResource(RegistrationConstants.CSS_FILE_PATH).toExternalForm());
			popupStage.setScene(scene);
			popupStage.initModality(Modality.WINDOW_MODAL);
			popupStage.initOwner(fXComponents.getStage());
			popupStage.show();
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - UI- ACKNOWLEDGEMENT", APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage());
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_NOTIFICATION_PAGE);
		}
	}

	/**
	 * To generate email and SMS notification to the user after successful
	 * registration
	 */
	@FXML
	public void sendNotification(ActionEvent event) {
		LOGGER.debug("REGISTRATION - UI - GENERATE_NOTIFICATION", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "generating Email/SMS notification after packet creation");

		ResponseDTO emailNotificationResponse = sendEmailNotification(email.getText());
		ResponseDTO smsNotificationResponse = sendSMSNotification(mobile.getText());

		String alert = RegistrationConstants.EMPTY;

		if (smsNotificationResponse != null && emailNotificationResponse != null
				&& smsNotificationResponse.getSuccessResponseDTO() != null
				&& emailNotificationResponse.getSuccessResponseDTO() != null) {
			generateAlert(RegistrationConstants.SUCCESS, RegistrationUIConstants.NOTIFICATION_SUCCESS);
		} else {
			if (emailNotificationResponse != null && emailNotificationResponse.getErrorResponseDTOs() != null
					&& emailNotificationResponse.getErrorResponseDTOs().get(0) != null) {
				alert = alert + RegistrationConstants.EMAIL_SERVICE.toUpperCase();
			}
			// generate alert for email notification
			if (!alert.equals("")) {
				generateNotificationAlert(RegistrationUIConstants.NOTIFICATION_FAIL);
				if (alert.equals("EMAIL")) {
					generateNotificationAlert(RegistrationUIConstants.NOTIFICATION_EMAIL_FAIL);
				}
			}

			if (smsNotificationResponse != null && smsNotificationResponse.getErrorResponseDTOs() != null
					&& smsNotificationResponse.getErrorResponseDTOs().get(0) != null) {
				alert = RegistrationConstants.SMS_SERVICE.toUpperCase();
			}
			// generate alert for SMS notification
			if (!alert.equals("")) {
				generateNotificationAlert(RegistrationUIConstants.NOTIFICATION_FAIL);
				if (alert.equals("SMS")) {
					generateNotificationAlert(RegistrationUIConstants.NOTIFICATION_SMS_FAIL);
				}
			}
		}
	}

	@FXML
	public void selectMobile(MouseEvent event) {
		if (selectedMobile) {
			selectedMobile = false;
			mobileImageView.setImage(deSelectImage);
			mobile.setDisable(true);
		} else {
			selectedMobile = true;
			mobileImageView.setImage(selectImage);
			mobile.setDisable(false);
		}
	}

	@FXML
	public void selectEmail(MouseEvent event) {
		if (selectedEmail) {
			selectedEmail = false;
			emailImageView.setImage(deSelectImage);
			email.setDisable(true);
		} else {
			selectedEmail = true;
			emailImageView.setImage(selectImage);
			email.setDisable(false);
		}
	}

	@FXML
	public void closeWindow(MouseEvent event) {
		LOGGER.debug("REGISTRATION - UI- ACKNOWLEDGEMENT", APPLICATION_NAME, APPLICATION_ID,
				"Calling exit window to close the popup");

		popupStage = (Stage) ((Node) event.getSource()).getParent().getScene().getWindow();
		popupStage.close();

		LOGGER.debug("REGISTRATION - UI- ACKNOWLEDGEMENT", APPLICATION_NAME, APPLICATION_ID, "Popup is closed");
	}

	/**
	 * To generate alert if the email/sms notification is not sent
	 */
	private void generateNotificationAlert(String alertMessage) {
		/* Generate Alert */
		generateAlert(RegistrationConstants.ERROR, alertMessage);
	}

}
