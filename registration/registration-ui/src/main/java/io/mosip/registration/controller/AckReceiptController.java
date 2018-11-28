package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.templatemanager.spi.TemplateManagerBuilder;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationExceptions;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.demographic.AddressDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.NotificationService;
import io.mosip.registration.service.PacketHandlerService;
import io.mosip.registration.service.TemplateService;
import io.mosip.registration.util.acktemplate.TemplateGenerator;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.WritableImage;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * Class for showing the Acknowledgement Receipt
 * 
 * @author Himaja Dhanyamraju
 *
 */
@Controller
public class AckReceiptController extends BaseController implements Initializable {

	private static final Logger LOGGER = AppConfig.getLogger(RegistrationOfficerPacketController.class);

	@Autowired
	private RegistrationOfficerPacketController packetController;
	@Autowired
	private PacketHandlerService packetHandlerService;
	@Autowired
	private TemplateService templateService;
	@Autowired
	private NotificationService notificationService;

	@Autowired
	private TemplateManagerBuilder templateManagerBuilder;

	private TemplateGenerator templateGenerator = new TemplateGenerator();

	private RegistrationDTO registrationData;
	private Writer stringWriter;

	@FXML
	private WebView webView;

	private WebEngine engine;

	public RegistrationDTO getRegistrationData() {
		return registrationData;
	}

	public void setRegistrationData(RegistrationDTO registrationData) {
		this.registrationData = registrationData;
	}

	/**
	 * @return the stringWriter
	 */
	public Writer getStringWriter() {
		return stringWriter;
	}

	/**
	 * @param stringWriter
	 *            the stringWriter to set
	 */
	public void setStringWriter(Writer stringWriter) {
		this.stringWriter = stringWriter;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		try {
			// network availability check
			if (RegistrationAppHealthCheckUtil.isNetworkAvailable()) {
				// get the mode of communication

				String notificationServiceName = String.valueOf(ApplicationContext.getInstance().getApplicationMap()
						.get(RegistrationConstants.MODE_OF_COMMUNICATION));

				if (notificationServiceName != null && !notificationServiceName.equals("NONE")) {
					ResponseDTO responseDTO = null;

					// get the data for notification template
					String notificationTemplate = templateService
							.getHtmlTemplate(RegistrationConstants.NOTIFICATION_TEMPLATE);
					String alert = RegistrationConstants.EMPTY;
					if (!notificationTemplate.isEmpty()) {
						// generate the notification template
						Writer writeNotificationTemplate = templateGenerator.generateNotificationTemplate(
								notificationTemplate, getRegistrationData(), templateManagerBuilder);

						String number = getRegistrationData().getDemographicDTO().getDemoInUserLang().getMobile();
						String rid = getRegistrationData() == null ? "RID" : getRegistrationData().getRegistrationId();

						if (!number.isEmpty()
								&& notificationServiceName.contains(RegistrationConstants.SMS_SERVICE.toUpperCase())) {
							// send sms
							responseDTO = notificationService.sendSMS(writeNotificationTemplate.toString(), number,
									rid);
							if (responseDTO != null && responseDTO.getErrorResponseDTOs() != null
									&& responseDTO.getErrorResponseDTOs().get(0) != null) {
								alert = RegistrationConstants.SMS_SERVICE.toUpperCase();
							}
						}

						String emailId = getRegistrationData().getDemographicDTO().getDemoInUserLang().getEmailId();

						if (!emailId.isEmpty() && notificationServiceName
								.contains(RegistrationConstants.EMAIL_SERVICE.toUpperCase())) {
							// send email
							responseDTO = notificationService.sendEmail(writeNotificationTemplate.toString(), emailId,
									rid);
							if (responseDTO != null && responseDTO.getErrorResponseDTOs() != null
									&& responseDTO.getErrorResponseDTOs().get(0) != null) {
								alert = alert + RegistrationConstants.EMAIL_SERVICE.toUpperCase();
							}
						}
						// generate alert
						if (!alert.equals("")) {
							String data = "Unable to send notification";
							if (alert.equals("SMS")) {
								data = "Unable to send SMS notification";
							} else if (alert.equals("EMAIL")) {
								data = "Unable to send Email notification";
							}
							generateNotificationAlert(data);
						}
					}
				}
			}
		} catch (RegBaseCheckedException regBaseCheckedException) {
			LOGGER.error("REGISTRATION - ACK RECEIPT CONTROLLER ", APPLICATION_NAME, APPLICATION_ID,
					regBaseCheckedException.getMessage());
		} catch (RegBaseUncheckedException regBaseUncheckedException) {
			LOGGER.error("REGISTRATION - ACK RECEIPT CONTROLLER ", APPLICATION_NAME, APPLICATION_ID,
					regBaseUncheckedException.getMessage());
		}
		engine = webView.getEngine();
		engine.loadContent(stringWriter.toString());
	}

	@FXML
	public void saveReceipt(ActionEvent event) throws RegBaseCheckedException {
		WritableImage ackImage = webView.snapshot(null, null);

		byte[] acknowledgement;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try {
			ImageIO.write(SwingFXUtils.fromFXImage(ackImage, null), RegistrationConstants.IMAGE_FORMAT,
					byteArrayOutputStream);
			acknowledgement = byteArrayOutputStream.toByteArray();
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(RegistrationExceptions.REG_ACK_TEMPLATE_IO_EXCEPTION.getErrorCode(),
					RegistrationExceptions.REG_ACK_TEMPLATE_IO_EXCEPTION.getErrorMessage());
		}

		registrationData.getDemographicDTO().getApplicantDocumentDTO().setAcknowledgeReceipt(acknowledgement);

		registrationData.getDemographicDTO().getApplicantDocumentDTO().setAcknowledgeReceiptName(
				registrationData.getRegistrationId() + "_Ack." + RegistrationConstants.IMAGE_FORMAT);
		ResponseDTO response = packetHandlerService.handle(registrationData);

		generateAlert("Success", AlertType.INFORMATION, "Packet Created Successfully!");
		// Adding individual address to session context
		if (response.getSuccessResponseDTO() != null
				&& response.getSuccessResponseDTO().getMessage().equals("Success")) {
			AddressDTO addressDTO = registrationData.getDemographicDTO().getDemoInUserLang().getAddressDTO();
			Map<String, Object> addr = SessionContext.getInstance().getMapObject();
			addr.put("PrevAddress", addressDTO);
			SessionContext.getInstance().setMapObject(addr);
		}

		goToHomePage();
	}

	private void generateNotificationAlert(String alertMessage) {
		/* Generate Alert */
		generateAlert(RegistrationConstants.NOTIFICATION_CODE, AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
				alertMessage);
	}

	@FXML
	public void goToNewRegistrationPage() {
		packetController.createPacket();
	}

}
