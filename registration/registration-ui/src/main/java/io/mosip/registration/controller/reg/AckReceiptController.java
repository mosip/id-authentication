package io.mosip.registration.controller.reg;

import static io.mosip.kernel.core.util.DateUtils.formatDate;
import static io.mosip.registration.constants.RegistrationConstants.ACKNOWLEDGEMENT_TEMPLATE;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.exception.RegistrationExceptionConstants.REG_IO_EXCEPTION;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Writer;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.idgenerator.spi.RidGenerator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.templatemanager.spi.TemplateManagerBuilder;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.registration.builder.Builder;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.demographic.AddressDTO;
import io.mosip.registration.dto.demographic.Identity;
import io.mosip.registration.dto.demographic.LocationDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.packet.PacketHandlerService;
import io.mosip.registration.service.template.NotificationService;
import io.mosip.registration.service.template.TemplateService;
import io.mosip.registration.util.acktemplate.TemplateGenerator;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import javafx.animation.PauseTransition;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.WritableImage;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;

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

	@Autowired
	private RidGenerator<String> ridGeneratorImpl;

	private TemplateGenerator templateGenerator = new TemplateGenerator();

	private RegistrationDTO registrationData;
	private Writer stringWriter;

	@Value("${SAVE_ACKNOWLEDGEMENT_INSIDE_PACKET}")
	private String saveAck;

	@FXML
	private WebView webView;

	@Autowired
	private Environment environment;

	private byte[] acknowledgement = null;

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
			stringWriter = templateGenerator.generateTemplate(ackTemplateText, getRegistrationData(),
					templateManagerBuilder);

			// network availability check
			if (RegistrationAppHealthCheckUtil.isNetworkAvailable()) {
				// get the mode of communication
				String notificationServiceName = String.valueOf(
						applicationContext.getApplicationMap().get(RegistrationConstants.MODE_OF_COMMUNICATION));

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

						String number = getRegistrationData().getDemographicDTO().getDemographicInfoDTO().getIdentity()
								.getPhone().getValue();
						String rid = getRegistrationData() == null ? "RID"
								: ridGeneratorImpl.generateId(RegistrationConstants.CENTER_ID,
										RegistrationConstants.MACHINE_ID_GEN);

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

						String emailId = getRegistrationData().getDemographicDTO().getDemographicInfoDTO().getIdentity()
								.getEmail().getValue();

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
							String data = RegistrationUIConstants.NOTIFICATION_FAIL;
							if (alert.equals("SMS")) {
								data = RegistrationUIConstants.NOTIFICATION_SMS_FAIL;
							} else if (alert.equals("EMAIL")) {
								data = RegistrationUIConstants.NOTIFICATION_EMAIL_FAIL;
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
		WebEngine engine = webView.getEngine();
		engine.loadContent(stringWriter.toString());
		PauseTransition pause = new PauseTransition(Duration.seconds(3));
		pause.setOnFinished(e -> {
			saveRegistrationData();
		});
		pause.play();
	}

	private void saveRegistrationData() {
		WritableImage ackImage = webView.snapshot(null, null);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			ImageIO.write(SwingFXUtils.fromFXImage(ackImage, null), RegistrationConstants.IMAGE_FORMAT,
					byteArrayOutputStream);
			acknowledgement = byteArrayOutputStream.toByteArray();
		} catch (java.io.IOException ioException) {
			LOGGER.error("REGISTRATION - UI - ACKNOWLEDGEMENT", APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage());
		}

		if (saveAck.equalsIgnoreCase("Y")) {
			registrationData.getDemographicDTO().getApplicantDocumentDTO().setAcknowledgeReceipt(acknowledgement);
			registrationData.getDemographicDTO().getApplicantDocumentDTO()
					.setAcknowledgeReceiptName("RegistrationAcknowledgement." + RegistrationConstants.IMAGE_FORMAT);
		}

		ResponseDTO response = packetHandlerService.handle(registrationData);
		if (response.getSuccessResponseDTO() != null
				&& response.getSuccessResponseDTO().getMessage().equals("Success")) {
			Identity identity = registrationData.getDemographicDTO().getDemographicInfoDTO().getIdentity();
			AddressDTO addressDTO = Builder.build(AddressDTO.class)
					.with(address -> address
							.setAddressLine1(identity.getAddressLine1().getValues().getFirst().getValue()))
					.with(address -> address
							.setAddressLine2(identity.getAddressLine2().getValues().getFirst().getValue()))
					.with(address -> address.setLine3(identity.getAddressLine3().getValues().getFirst().getValue()))
					.with(address -> address.setLocationDTO(Builder.build(LocationDTO.class)
							.with(location -> location.setCity(identity.getCity().getValues().getFirst().getValue()))
							.with(location -> location
									.setProvince(identity.getProvince().getValues().getFirst().getValue()))
							.with(location -> location
									.setRegion(identity.getRegion().getValues().getFirst().getValue()))
							.with(location -> location.setPostalCode(identity.getPostalCode())).get()))
					.get();
			Map<String, Object> addr = SessionContext.getInstance().getMapObject();
			addr.put("PrevAddress", addressDTO);
			SessionContext.getInstance().setMapObject(addr);
		}
	}

	@FXML
	public void saveReceipt(ActionEvent event) throws RegBaseCheckedException {
		try {
			// Generate the file path for storing the Encrypted Packet and Acknowledgement
			// Receipt
			String seperator = "/";
			String filePath = environment.getProperty(RegistrationConstants.PACKET_STORE_LOCATION) + seperator
					+ formatDate(new Date(), environment.getProperty(RegistrationConstants.PACKET_STORE_DATE_FORMAT))
							.concat(seperator).concat(registrationData.getRegistrationId());

			// Storing the Registration Acknowledge Receipt Image
			FileUtils.copyToFile(new ByteArrayInputStream(acknowledgement),
					new File(filePath.concat("_Ack.").concat(RegistrationConstants.IMAGE_FORMAT)));

			LOGGER.debug("REGISTRATION - UI - ACKNOWLEDGEMENT", APPLICATION_NAME, APPLICATION_ID,
					"Registration's Acknowledgement Receipt saved");

			generateAlert(RegistrationConstants.SUCCESS_MSG, RegistrationUIConstants.PACKET_CREATED_SUCCESS);
			registrationController.goToHomePage();
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(REG_IO_EXCEPTION.getErrorCode(), REG_IO_EXCEPTION.getErrorMessage());
		}
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
