package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.dto.ResponseDTO;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
	@FXML
	private Button send;

	@Autowired
	private Validations validations;

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
			send.disableProperty()
					.bind(Bindings.isEmpty(email.textProperty()).and(Bindings.isEmpty(mobile.textProperty())));
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
		List<String> notifications = new ArrayList<>();
		if (email.getText() != null && !email.getText().isEmpty()) {
			String emails = email.getText();
			List<String> emailList = getRecipients(emails, RegistrationConstants.CONTENT_TYPE_EMAIL);
			if (!emailList.isEmpty()) {
				StringBuilder unsentMails = new StringBuilder();
				String prefix = "";
				for (String emailId : emailList) {
					ResponseDTO emailNotificationResponse = sendEmailNotification(emailId);
					if (emailNotificationResponse.getErrorResponseDTOs() != null) {
						unsentMails.append(prefix);
						prefix = ",";
						unsentMails.append(emailId);
					}
				}
				if (unsentMails.length() > 1) {
					generateAlert(RegistrationConstants.ERROR,
							RegistrationUIConstants.NOTIFICATION_EMAIL_FAIL + " to " + unsentMails);
				} else {
					notifications.add(RegistrationConstants.CONTENT_TYPE_EMAIL);

				}
			} else {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.NO_VALID_EMAIL);
			}
		}
		if (mobile.getText() != null && !mobile.getText().isEmpty()) {
			String mobileNos = mobile.getText();
			List<String> mobileList = getRecipients(mobileNos, RegistrationConstants.CONTENT_TYPE_MOBILE);
			if (!mobileList.isEmpty()) {
				StringBuilder unsentSMS = new StringBuilder();
				String prefix = "";
				for (String mobileNo : mobileList) {
					ResponseDTO smsNotificationResponse = sendSMSNotification(mobileNo);
					if (smsNotificationResponse.getErrorResponseDTOs() != null) {
						unsentSMS.append(prefix);
						prefix = ",";
						unsentSMS.append(mobileNo);
					}
				}
				if (unsentSMS.length() > 1) {
					generateAlert(RegistrationConstants.ERROR,
							RegistrationUIConstants.NOTIFICATION_SMS_FAIL + " to " + unsentSMS);
				} else {
					notifications.add(RegistrationConstants.CONTENT_TYPE_MOBILE);
				}
			} else {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.NO_VALID_MOBILE);
			}
		}
		if (notifications.size() > 1) {
			generateAlert(RegistrationConstants.SUCCESS, RegistrationUIConstants.NOTIFICATION_SUCCESS);
			popupStage.close();
		} else if (notifications.size() == 1) {
			if (notifications.get(0).equals(RegistrationConstants.CONTENT_TYPE_EMAIL)) {
				generateAlert(RegistrationConstants.SUCCESS, RegistrationUIConstants.EMAIL_NOTIFICATION_SUCCESS);
			} else if (notifications.get(0).equals(RegistrationConstants.CONTENT_TYPE_MOBILE)) {
				generateAlert(RegistrationConstants.SUCCESS, RegistrationUIConstants.SMS_NOTIFICATION_SUCCESS);				
			}
			popupStage.close();
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

	private List<String> getRecipients(String textField, String contentType) {

		/* List of E-Mails */
		List<String> contentsList = new LinkedList<>();
		String delimiter = ",";

		/* remove spaces as the mail will not contain any spaces */
		textField = textField.replaceAll("\\s", "");

		/* Split the mails through Comma */
		List<String> contents = new ArrayList<String>(Arrays.asList(textField.split(delimiter)));

		for (Iterator<String> iterator = contents.iterator(); iterator.hasNext();) {
			String content = iterator.next();
			if (content.isEmpty()) {
				iterator.remove();
			}
		}
		if (contents.size() > 5) {
			generateAlert(
					"Maximum recipients exceeded. You can send the acknowledgement to a maximum of 5 email addresses and 5 phone numbers only.");
		} else {
			for (String content : contents) {
				if (RegistrationConstants.CONTENT_TYPE_EMAIL.equalsIgnoreCase(contentType) ? validateMail(content)
						: validateMobile(content)) {
					contentsList.add(content);
				}
			}
		}
		return contentsList;
	}

	private boolean validateMail(String emailId) {
		return validations.validateSingleString(emailId, email.getId());
	}

	private boolean validateMobile(String mobileNo) {
		return validations.validateSingleString(mobileNo, mobile.getId());
	}
}
