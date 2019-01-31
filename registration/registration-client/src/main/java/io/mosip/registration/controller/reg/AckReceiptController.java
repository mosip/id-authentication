package io.mosip.registration.controller.reg;

import static io.mosip.kernel.core.util.DateUtils.formatDate;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

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
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.templatemanager.spi.TemplateManagerBuilder;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.registration.builder.Builder;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.ApplicationContext;
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
import io.mosip.registration.service.packet.PacketUploadService;
import io.mosip.registration.service.packet.RegistrationApprovalService;
import io.mosip.registration.service.sync.PacketSynchService;
import io.mosip.registration.service.template.NotificationService;
import io.mosip.registration.service.template.TemplateService;
import io.mosip.registration.util.acktemplate.TemplateGenerator;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import javafx.animation.PauseTransition;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.text.Text;
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
	private TemplateManagerBuilder templateManagerBuilder;

	@Autowired
	private TemplateGenerator templateGenerator;

	private RegistrationDTO registrationData;
	private Writer stringWriter;

	@Value("${SAVE_ACKNOWLEDGEMENT_INSIDE_PACKET}")
	private String saveAck;

	@FXML
	private WebView webView;

	@FXML
	private Button newRegistration;

	@FXML
	private Button print;

	@FXML
	private Text registrationNavLabel;

	@Autowired
	private Environment environment;

	@Autowired
	private PacketSynchService packetSynchService;

	@Autowired
	private PacketUploadService packetUploadService;

	@Autowired
	private RegistrationApprovalService registrationApprovalService;

	private String notificationAlertData = null;

	private ResponseDTO packetCreationResponse;

	private byte[] acknowledgement = null;

	public RegistrationDTO getRegistrationData() {
		return registrationData;
	}

	public void setRegistrationData(RegistrationDTO registrationData) {
		this.registrationData = registrationData;
	}

	public void setStringWriter(Writer stringWriter) {
		this.stringWriter = stringWriter;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.info("REGISTRATION - UI - ACKRECEIPTCONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Page loading has been started");

		if (getRegistrationData().getSelectionListDTO() != null) {
			registrationNavLabel.setText(RegistrationConstants.UIN_NAV_LABEL);
			newRegistration.setVisible(false);
		}

		WebEngine engine = webView.getEngine();
		// loads the generated HTML template content into webview
		engine.loadContent(stringWriter.toString());
		LOGGER.info("REGISTRATION - UI - ACKRECEIPTCONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Acknowledgement template has been loaded to webview");

		// pauses the view for 3 seconds so that the webview will be loaded with the
		// content and calls the method to create packet after 3 seconds
		PauseTransition pause = new PauseTransition(Duration.seconds(3));
		pause.setOnFinished(e -> saveRegistrationData());
		pause.play();
	}

	/**
	 * To generate email and SMS notification to the user after successful
	 * registration
	 */
	private void generateEmailNotification() {
		LOGGER.info("REGISTRATION - UI - ACKRECEIPTCONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "generating Email/SMS notification after packet creation");

		try {
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
								.getPhone();
						String rid = getRegistrationData().getRegistrationId();

						if (number != null
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
								.getEmail();

						if (emailId != null && notificationServiceName
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
							notificationAlertData = RegistrationUIConstants.NOTIFICATION_FAIL;
							if (alert.equals("SMS")) {
								notificationAlertData = RegistrationUIConstants.NOTIFICATION_SMS_FAIL;
							} else if (alert.equals("EMAIL")) {
								notificationAlertData = RegistrationUIConstants.NOTIFICATION_EMAIL_FAIL;
							}
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
	}

	private void updatePacketStatus() {
		registrationApprovalService.updateRegistration((getRegistrationDTOFromSession().getRegistrationId()),
				RegistrationConstants.EMPTY, RegistrationClientStatusCode.APPROVED.getCode());
	}

	private void syncAndUploadPacket() throws RegBaseCheckedException {
		if (RegistrationAppHealthCheckUtil.isNetworkAvailable()) {

			String response = packetSynchService.packetSync(getRegistrationDTOFromSession().getRegistrationId());

			if (response.equals(RegistrationConstants.EMPTY)) {

				packetUploadService.uploadPacket(getRegistrationDTOFromSession().getRegistrationId());
			}
		}
	}

	/**
	 * To save the acknowledgement receipt along with the registration data and
	 * create packet
	 */
	private void saveRegistrationData() {
		LOGGER.info("REGISTRATION - UI - ACKRECEIPTCONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "packet creation has been started");

		// take a snapshot of the webview to save it along with the packet
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

		// packet creation
		packetCreationResponse = packetHandlerService.handle(registrationData);

		if (packetCreationResponse.getSuccessResponseDTO() != null
				&& packetCreationResponse.getSuccessResponseDTO().getMessage().equals("Success")) {
			generateEmailNotification();

			try {

				if (!String
						.valueOf(ApplicationContext.getInstance().getApplicationMap()
								.get(RegistrationConstants.EOD_PROCESS_CONFIG_FLAG))
						.equals(RegistrationConstants.ENABLE)) {
					updatePacketStatus();
					syncAndUploadPacket();
				}
				// Generate the file path for storing the Encrypted Packet and Acknowledgement
				// Receipt
				String seperator = "/";
				String filePath = environment.getProperty(RegistrationConstants.PACKET_STORE_LOCATION) + seperator
						+ formatDate(new Date(),
								environment.getProperty(RegistrationConstants.PACKET_STORE_DATE_FORMAT))
										.concat(seperator).concat(registrationData.getRegistrationId());

				// Storing the Registration Acknowledge Receipt Image
				FileUtils.copyToFile(new ByteArrayInputStream(acknowledgement),
						new File(filePath.concat("_Ack.").concat(RegistrationConstants.IMAGE_FORMAT)));

				LOGGER.info("REGISTRATION - UI - ACKNOWLEDGEMENT", APPLICATION_NAME, APPLICATION_ID,
						"Registration's Acknowledgement Receipt saved");
			} catch (IOException ioException) {
				LOGGER.error("REGISTRATION - UI - ACKNOWLEDGEMENT", APPLICATION_NAME, APPLICATION_ID,
						ioException.getMessage());
			} catch (RegBaseCheckedException checkedException) {
				LOGGER.error("REGISTRATION - UI - ACKNOWLEDGEMENT", APPLICATION_NAME, APPLICATION_ID,
						checkedException.getMessage());
			}

			if (registrationData.getSelectionListDTO() == null) {

				Identity identity = registrationData.getDemographicDTO().getDemographicInfoDTO().getIdentity();
				AddressDTO addressDTO = Builder.build(AddressDTO.class)
						.with(address -> address.setAddressLine1(identity.getAddressLine1().get(0).getValue()))
						.with(address -> address.setAddressLine2(identity.getAddressLine2().get(0).getValue()))
						.with(address -> address.setLine3(identity.getAddressLine3().get(0).getValue()))
						.with(address -> address.setLocationDTO(Builder.build(LocationDTO.class)
								.with(location -> location.setCity(identity.getCity().get(0).getValue()))
								.with(location -> location.setProvince(identity.getProvince().get(0).getValue()))
								.with(location -> location.setRegion(identity.getRegion().get(0).getValue()))
								.with(location -> location.setPostalCode(identity.getPostalCode())).get()))
						.get();
				Map<String, Object> addr = SessionContext.getInstance().getMapObject();
				addr.put("PrevAddress", addressDTO);
				SessionContext.getInstance().setMapObject(addr);
			}
		}
	}

	/**
	 * To print the acknowledgement receipt after packet creation when the user
	 * clicks on print button.
	 */
	@FXML
	public void printReceipt(ActionEvent event) {
		LOGGER.info("REGISTRATION - UI - ACKRECEIPTCONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Printing the Acknowledgement Receipt");

		if (packetCreationResponse.getSuccessResponseDTO() != null) {
			if (notificationAlertData != null) {
				generateNotificationAlert(notificationAlertData);
			}
			PrinterJob job = PrinterJob.createPrinterJob();
			if (job != null) {
				webView.getEngine().print(job);
				job.endJob();
			}
			generateAlert(RegistrationConstants.SUCCESS, RegistrationUIConstants.PRINT_INITIATION_SUCCESS);
			goToHomePageFromRegistration();
		} else {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.PACKET_CREATION_FAILURE);
			goToHomePageFromRegistration();
		}
	}

	@FXML
	public void goToNewRegistration(ActionEvent event) {
		LOGGER.info("REGISTRATION - UI - ACKRECEIPTCONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Going to New Registration Page after packet creation");

		clearRegistrationData();
		packetController.createPacket();
	}

	/**
	 * To generate alert if the email/sms notification is not sent
	 */
	private void generateNotificationAlert(String alertMessage) {
		/* Generate Alert */
		generateAlert(RegistrationConstants.ERROR, alertMessage);
	}

	private RegistrationDTO getRegistrationDTOFromSession() {
		return (RegistrationDTO) SessionContext.getInstance().getMapObject()
				.get(RegistrationConstants.REGISTRATION_DATA);
	}
}