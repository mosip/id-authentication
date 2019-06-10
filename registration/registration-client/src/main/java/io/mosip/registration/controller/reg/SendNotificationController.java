package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.template.NotificationService;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
public class SendNotificationController extends BaseController implements Initializable {

	private static final Logger LOGGER = AppConfig.getLogger(SendNotificationController.class);

	@FXML
	private TextField email;
	@FXML
	private TextField mobile;
	@FXML
	private ImageView emailIcon;
	@FXML
	private ImageView mobileIcon;
	@FXML
	private Button send;

	@Autowired
	private NotificationService notificationService;

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
			LOGGER.error("REGISTRATION - UI- SEND_NOTIFICATION", APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage());
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_NOTIFICATION_PAGE);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		String modeOfCommunication = (getValueFromApplicationContext(RegistrationConstants.MODE_OF_COMMUNICATION)).toLowerCase();
		if (!modeOfCommunication.contains(RegistrationConstants.EMAIL_SERVICE.toLowerCase())) {
			email.setVisible(false);
			emailIcon.setVisible(false);
		}
		if (!modeOfCommunication.contains(RegistrationConstants.SMS_SERVICE.toLowerCase())) {
			mobile.setVisible(false);
			mobileIcon.setVisible(false);
		}
	}

	/**
	 * To generate email and SMS notification to the user after successful
	 * registration
	 * 
	 * @param event
	 *            - action to be happened on click of send button
	 */
	@FXML
	public void sendNotification(ActionEvent event) {
		LOGGER.debug("REGISTRATION - UI - SEND_NOTIFICATION", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "generating Email/SMS notification after packet creation");

		try {
			if (RegistrationAppHealthCheckUtil.isNetworkAvailable()) {
				Writer emailNotificationTemplate = getNotificationTemplate(RegistrationConstants.EMAIL_TEMPLATE);
				Writer smsNotificationTemplate = getNotificationTemplate(RegistrationConstants.SMS_TEMPLATE);
				String registrationId = getRegistrationDTOFromSession().getRegistrationId();

				List<String> notifications = new ArrayList<>();
				if (email.getText() != null && !email.getText().isEmpty()) {
					String emails = email.getText();
					List<String> emailList = getRecipients(emails, RegistrationConstants.CONTENT_TYPE_EMAIL);
					if (!emailList.isEmpty()) {
						StringBuilder unsentMails = new StringBuilder();
						String prefix = "";
						for (String emailId : emailList) {
							ResponseDTO emailNotificationResponse = notificationService
									.sendEmail(emailNotificationTemplate.toString(), emailId, registrationId);
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
					}
				}
				if (mobile.getText() != null && !mobile.getText().isEmpty()) {
					String mobileNos = mobile.getText();
					List<String> mobileList = getRecipients(mobileNos, RegistrationConstants.CONTENT_TYPE_MOBILE);
					if (!mobileList.isEmpty()) {
						StringBuilder unsentSMS = new StringBuilder();
						String prefix = "";
						for (String mobileNo : mobileList) {
							ResponseDTO smsNotificationResponse = notificationService
									.sendSMS(smsNotificationTemplate.toString(), mobileNo, registrationId);
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
					}
				}
				if (notifications.size() > 1) {
					generateAlert(RegistrationConstants.ALERT_INFORMATION,
							RegistrationUIConstants.NOTIFICATION_SUCCESS);
					popupStage.close();
				} else if (notifications.size() == 1) {
					if (notifications.get(0).equals(RegistrationConstants.CONTENT_TYPE_EMAIL)) {
						generateAlert(RegistrationConstants.ALERT_INFORMATION,
								RegistrationUIConstants.EMAIL_NOTIFICATION_SUCCESS);
					} else if (notifications.get(0).equals(RegistrationConstants.CONTENT_TYPE_MOBILE)) {
						generateAlert(RegistrationConstants.ALERT_INFORMATION,
								RegistrationUIConstants.SMS_NOTIFICATION_SUCCESS);
					}
					popupStage.close();
				}
			} else {
				generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationUIConstants.NO_INTERNET_CONNECTION);
			}
		} catch (RegBaseUncheckedException regBaseUncheckedException) {
			LOGGER.error("REGISTRATION - UI - SEND_NOTIFICATION", APPLICATION_NAME, APPLICATION_ID,
					regBaseUncheckedException.getMessage() + ExceptionUtils.getStackTrace(regBaseUncheckedException));
		}
	}

	@FXML
	public void closeWindow(MouseEvent event) {
		LOGGER.debug("REGISTRATION - UI- SEND_NOTIFICATION", APPLICATION_NAME, APPLICATION_ID,
				"Calling exit window to close the popup");

		popupStage = (Stage) ((Node) event.getSource()).getParent().getScene().getWindow();
		popupStage.close();

		LOGGER.debug("REGISTRATION - UI- SEND_NOTIFICATION", APPLICATION_NAME, APPLICATION_ID, "Popup is closed");
	}

	private List<String> getRecipients(String textField, String contentType) {
		LOGGER.debug("REGISTRATION - UI- SEND_NOTIFICATION", APPLICATION_NAME, APPLICATION_ID,
				"Splitting the multiple emails/mobile numbers and validating each of them");

		/* List of Emails/Mobiles */
		List<String> contentsList = new LinkedList<>();
		String delimiter = ",";

		/* remove spaces as the mail/mobile will not contain any spaces */
		textField = textField.replaceAll("\\s", "");

		/* Split the mails/mobiles through Comma */
		List<String> contents = new ArrayList<>(Arrays.asList(textField.split(delimiter)));

		for (Iterator<String> iterator = contents.iterator(); iterator.hasNext();) {
			String content = iterator.next();
			if (content.isEmpty()) {
				iterator.remove();
			}
		}
		if (contents.size() > 5) {
			generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationUIConstants.NOTIFICATION_LIMIT_EXCEEDED);
		} else {
			for (String content : contents) {
				if (RegistrationConstants.CONTENT_TYPE_EMAIL.equalsIgnoreCase(contentType) ? validateMail(content)
						: validateMobile(content)) {
					contentsList.add(content);
				}
			}
			if(contents.size() != contentsList.size()) {
				if (RegistrationConstants.CONTENT_TYPE_EMAIL.equalsIgnoreCase(contentType)) {
					generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.INVALID_EMAIL);
				} else {
					generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.INVALID_MOBILE);
				}
				contentsList = new LinkedList<>();
			}
		}

		LOGGER.debug("REGISTRATION - UI- SEND_NOTIFICATION", APPLICATION_NAME, APPLICATION_ID,
				"validation for each of the input is done");

		return contentsList;
	}

	private boolean validateMail(String emailId) {
		return validations.validateSingleString(emailId, email.getId());
	}

	private boolean validateMobile(String mobileNo) {
		return validations.validateSingleString(mobileNo, mobile.getId());
	}
}
