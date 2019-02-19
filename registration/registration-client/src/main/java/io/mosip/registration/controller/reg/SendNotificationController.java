package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.dto.ResponseDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

@Controller
public class SendNotificationController extends BaseController {

	private static final Logger LOGGER = AppConfig.getLogger(SendNotificationController.class);

	@FXML
	private TextField email;
	@FXML
	private TextField mobile;
	@FXML
	private ImageView emailImageView;
	@FXML
	private ImageView mobileImageView;
	
	private Stage popupStage;

	public void init() {
		try {
			popupStage = new Stage();
			popupStage.initStyle(StageStyle.UNDECORATED);
			Parent sendEmailPopup = BaseController
					.load(getClass().getResource(RegistrationConstants.SEND_NOTIFICATION_PAGE));
			popupStage.setResizable(false);
			Scene scene = new Scene(sendEmailPopup);
			ClassLoader loader = ClassLoader.getSystemClassLoader();
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

		ResponseDTO emailNotificationResponse = new ResponseDTO();
		ResponseDTO smsNotificationResponse = new ResponseDTO();

		if (!email.getText().isEmpty()) {
			String emails = email.getText();
			List<String> emailList = Arrays.asList(emails.split(","));
			for (String emailId : emailList) {
				emailNotificationResponse = sendEmailNotification(emailId);
			}
			if (smsNotificationResponse != null && smsNotificationResponse.getSuccessResponseDTO() != null) {
				generateAlert(RegistrationConstants.SUCCESS, RegistrationUIConstants.NOTIFICATION_SUCCESS);
			} else {
				String alert = RegistrationConstants.EMPTY;
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
			}
		}
		if (!mobile.getText().isEmpty()) {
			String mobileNos = mobile.getText();
			List<String> mobileList = Arrays.asList(mobileNos.split(","));
			for (String mobileNo : mobileList) {
				smsNotificationResponse = sendSMSNotification(mobileNo);
			}
			if (smsNotificationResponse != null && smsNotificationResponse.getSuccessResponseDTO() != null) {
				generateAlert(RegistrationConstants.SUCCESS, RegistrationUIConstants.NOTIFICATION_SUCCESS);
			} else {
				String alert = RegistrationConstants.EMPTY;
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
