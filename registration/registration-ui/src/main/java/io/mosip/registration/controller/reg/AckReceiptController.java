package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.ACKNOWLEDGEMENT_TEMPLATE;
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
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.demographic.AddressDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.service.packet.PacketHandlerService;
import io.mosip.registration.service.template.NotificationService;
import io.mosip.registration.service.template.TemplateService;
import io.mosip.registration.util.acktemplate.TemplateGenerator;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

	private static final Logger LOGGER = AppConfig.getLogger(AckReceiptController.class);

	@Autowired
	private PacketHandlerController packetController;
	@Autowired
	private PacketHandlerService packetHandlerService;
	@Autowired
	private TemplateService templateService;
	@Autowired
	private NotificationService notificationService;
	@Autowired
	private RegistrationController registrationController;

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

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		try {
			
			String ackTemplateText = templateService.getHtmlTemplate(ACKNOWLEDGEMENT_TEMPLATE);
			stringWriter = templateGenerator.generateTemplate(ackTemplateText, getRegistrationData(), templateManagerBuilder);
			
			// network availability check
			if (RegistrationAppHealthCheckUtil.isNetworkAvailable()) {							
				// get the mode of communication
				String notificationServiceName = String.valueOf(applicationContext.getApplicationMap()
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
							String data = RegistrationConstants.NOTIFICATION_FAIL;
							if (alert.equals("SMS")) {
								data = RegistrationConstants.NOTIFICATION_SMS_FAIL;
							} else if (alert.equals("EMAIL")) {
								data = RegistrationConstants.NOTIFICATION_EMAIL_FAIL;
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
			throw new RegBaseCheckedException(RegistrationExceptionConstants.REG_ACK_TEMPLATE_IO_EXCEPTION.getErrorCode(),
					RegistrationExceptionConstants.REG_ACK_TEMPLATE_IO_EXCEPTION.getErrorMessage());
		}

		registrationData.getDemographicDTO().getApplicantDocumentDTO().setAcknowledgeReceipt(acknowledgement);

		registrationData.getDemographicDTO().getApplicantDocumentDTO().setAcknowledgeReceiptName(
				"RegistrationAcknowledgement." + RegistrationConstants.IMAGE_FORMAT);
		ResponseDTO response = packetHandlerService.handle(registrationData);

		generateAlert(RegistrationConstants.SUCCESS_MSG, RegistrationConstants.PACKET_CREATED_SUCCESS);
		// Adding individual address to session context
		if (response.getSuccessResponseDTO() != null
				&& response.getSuccessResponseDTO().getMessage().equals("Success")) {
			AddressDTO addressDTO = registrationData.getDemographicDTO().getDemoInUserLang().getAddressDTO();
			Map<String, Object> addr = SessionContext.getInstance().getMapObject();
			addr.put("PrevAddress", addressDTO);
			SessionContext.getInstance().setMapObject(addr);
		}

		registrationController.goToHomePage();
	}

	private void generateNotificationAlert(String alertMessage) {
		/* Generate Alert */
		generateAlert(RegistrationConstants.ALERT_ERROR, alertMessage);
	}

	@FXML
	public void goToNewRegistrationPage() {
		packetController.createPacket();
	}
	
	@FXML
	@Override
	public void goToHomePage() {
		registrationController.goToHomePage();
	}

}
