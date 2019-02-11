package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.io.Writer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.templatemanager.spi.TemplateManagerBuilder;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.template.NotificationService;
import io.mosip.registration.service.template.TemplateService;
import io.mosip.registration.util.acktemplate.TemplateGenerator;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
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

	private Stage popupStage;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private TemplateService templateService;

	@Autowired
	private TemplateManagerBuilder templateManagerBuilder;

	@Autowired
	private TemplateGenerator templateGenerator;

	private ResponseDTO smsNotificationResponse;
	private ResponseDTO emailNotificationResponse;

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
		LOGGER.debug("REGISTRATION - GENERATE_NOTIFICATION - REGISTRATION_OFFICER_PACKET_CONTROLLER",
				RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"generating Email/SMS notification after packet creation");

		RegistrationDTO registrationDTO = getRegistrationDTOFromSession();
		try {
			// network availability check
			if (RegistrationAppHealthCheckUtil.isNetworkAvailable()) {
				// get the mode of communication
				String notificationServiceName = String.valueOf(
						applicationContext.getApplicationMap().get(RegistrationConstants.MODE_OF_COMMUNICATION));

				if (notificationServiceName != null && !notificationServiceName.equals("NONE")) {
					// get the data for notification template
					String notificationTemplate = templateService
							.getHtmlTemplate(RegistrationConstants.NOTIFICATION_TEMPLATE);
					String alert = RegistrationConstants.EMPTY;
					if (!notificationTemplate.isEmpty()) {
						// generate the notification template
						Writer writeNotificationTemplate = templateGenerator.generateNotificationTemplate(
								notificationTemplate, registrationDTO, templateManagerBuilder);

						String number = mobile.getText();
						String rid = registrationDTO.getRegistrationId();

						if (number != null
								&& notificationServiceName.contains(RegistrationConstants.SMS_SERVICE.toUpperCase())) {
							// send sms
							smsNotificationResponse = notificationService.sendSMS(writeNotificationTemplate.toString(),
									number, rid);
							if (smsNotificationResponse != null
									&& smsNotificationResponse.getErrorResponseDTOs() != null
									&& smsNotificationResponse.getErrorResponseDTOs().get(0) != null) {
								alert = RegistrationConstants.SMS_SERVICE.toUpperCase();
							}
						}

						String emailId = email.getText();

						if (emailId != null && notificationServiceName
								.contains(RegistrationConstants.EMAIL_SERVICE.toUpperCase())) {
							// send email
							emailNotificationResponse = notificationService
									.sendEmail(writeNotificationTemplate.toString(), emailId, rid);
							if (emailNotificationResponse != null
									&& emailNotificationResponse.getErrorResponseDTOs() != null
									&& emailNotificationResponse.getErrorResponseDTOs().get(0) != null) {
								alert = alert + RegistrationConstants.EMAIL_SERVICE.toUpperCase();
							}
						}
						// generate alert
						if (!alert.equals("")) {
							generateNotificationAlert(RegistrationUIConstants.NOTIFICATION_FAIL);
							if (alert.equals("SMS")) {
								generateNotificationAlert(RegistrationUIConstants.NOTIFICATION_SMS_FAIL);
							} else if (alert.equals("EMAIL")) {
								generateNotificationAlert(RegistrationUIConstants.NOTIFICATION_EMAIL_FAIL);
							}
						}
					}
				}
			}
		} catch (RegBaseCheckedException regBaseCheckedException) {
			LOGGER.error("REGISTRATION - GENERATE_NOTIFICATION - REGISTRATION_OFFICER_PACKET_CONTROLLER",
					APPLICATION_NAME, APPLICATION_ID, regBaseCheckedException.getMessage());
		} catch (RegBaseUncheckedException regBaseUncheckedException) {
			LOGGER.error("REGISTRATION - GENERATE_NOTIFICATION - REGISTRATION_OFFICER_PACKET_CONTROLLER",
					APPLICATION_NAME, APPLICATION_ID, regBaseUncheckedException.getMessage());
		}
	}

	@FXML
	public void closeWindow(MouseEvent event) {
		LOGGER.debug("REGISTRATION - UI- ACKNOWLEDGEMENT", APPLICATION_NAME, APPLICATION_ID,
				"Calling exit window to close the popup");

		if (smsNotificationResponse != null && emailNotificationResponse != null
				&& smsNotificationResponse.getSuccessResponseDTO() != null
				&& emailNotificationResponse.getSuccessResponseDTO() != null) {
			generateAlert(RegistrationConstants.SUCCESS, RegistrationUIConstants.NOTIFICATION_SUCCESS);
		}

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

	private RegistrationDTO getRegistrationDTOFromSession() {
		return (RegistrationDTO) SessionContext.map()
				.get(RegistrationConstants.REGISTRATION_DATA);
	}
}
