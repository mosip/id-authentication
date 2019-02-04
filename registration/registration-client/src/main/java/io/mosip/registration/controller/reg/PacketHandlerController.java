package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.ACKNOWLEDGEMENT_TEMPLATE;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import static io.mosip.kernel.core.util.DateUtils.formatDate;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.templatemanager.spi.TemplateManagerBuilder;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.registration.builder.Builder;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.demographic.AddressDTO;
import io.mosip.registration.dto.demographic.Identity;
import io.mosip.registration.dto.demographic.LocationDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.packet.PacketHandlerService;
import io.mosip.registration.service.sync.PreRegistrationDataSyncService;
import io.mosip.registration.service.template.NotificationService;
import io.mosip.registration.service.template.TemplateService;
import io.mosip.registration.util.acktemplate.TemplateGenerator;
import io.mosip.registration.util.dataprovider.DataProvider;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

/**
 * Class for Registration Packet operations
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@Controller
public class PacketHandlerController extends BaseController {

	private static final Logger LOGGER = AppConfig.getLogger(PacketHandlerController.class);

	@FXML
	private AnchorPane acknowRoot;

	@FXML
	private AnchorPane uploadRoot;

	@FXML
	private AnchorPane optionRoot;

	@Autowired
	private AckReceiptController ackReceiptController;

	@Autowired
	private HomeController homeController;

	@Autowired
	private TemplateService templateService;

	@Autowired
	private TemplateManagerBuilder templateManagerBuilder;

	@Autowired
	private TemplateGenerator templateGenerator;

	@Autowired
	PreRegistrationDataSyncService preRegistrationDataSyncService;

	@Autowired
	private UserOnboardController userOnboardController;
	
	@Autowired
	private PacketHandlerService packetHandlerService;

	@Autowired
	private NotificationService notificationService;

	@Value("${SAVE_ACKNOWLEDGEMENT_INSIDE_PACKET}")
	private String saveAck;

	@Autowired
	private Environment environment;

	/**
	 * Validating screen authorization and Creating Packet and displaying
	 * acknowledgement form
	 */
	public void createPacket() {

		try {
			Parent createRoot = BaseController.load(getClass().getResource(RegistrationConstants.CREATE_PACKET_PAGE),
					applicationContext.getApplicationLanguageBundle());
			LOGGER.debug("REGISTRATION - CREATE_PACKET - REGISTRATION_OFFICER_PACKET_CONTROLLER", APPLICATION_NAME,
					APPLICATION_ID, "Validating Create Packet screen for specific role");

			if (!validateScreenAuthorization(createRoot.getId())) {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.AUTHORIZATION_ERROR);
			} else {
				StringBuilder errorMessage = new StringBuilder();
				ResponseDTO responseDTO;
				responseDTO = validateSyncStatus();
				List<ErrorResponseDTO> errorResponseDTOs = responseDTO.getErrorResponseDTOs();
				if (errorResponseDTOs != null && !errorResponseDTOs.isEmpty()) {
					for (ErrorResponseDTO errorResponseDTO : errorResponseDTOs) {
						errorMessage
								.append(errorResponseDTO.getMessage() + " - " + errorResponseDTO.getCode() + "\n\n");
					}
					generateAlert(RegistrationConstants.ERROR, errorMessage.toString().trim());

				} else {
					getScene(createRoot).setRoot(createRoot);
				}
			}

		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - UI- Officer Packet Create ", APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage());

			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_REG_PAGE);
		}
	}

	public void showReciept(String capturePhotoUsingDevice) {
		try {
			RegistrationDTO registrationDTO = (RegistrationDTO) SessionContext.getInstance().getMapObject()
					.get(RegistrationConstants.REGISTRATION_DATA);
			registrationDTO = DataProvider.getPacketDTO(registrationDTO, capturePhotoUsingDevice);
			ackReceiptController.setRegistrationData(registrationDTO);
			String ackTemplateText = templateService.getHtmlTemplate(ACKNOWLEDGEMENT_TEMPLATE);
			ResponseDTO templateResponse = templateGenerator.generateTemplate(ackTemplateText, registrationDTO,
					templateManagerBuilder, RegistrationConstants.ACKNOWLEDGEMENT_TEMPLATE);
			if (templateResponse != null && templateResponse.getSuccessResponseDTO() != null) {
				Writer stringWriter = (Writer) templateResponse.getSuccessResponseDTO().getOtherAttributes()
						.get(RegistrationConstants.TEMPLATE_NAME);
				ackReceiptController.setStringWriter(stringWriter);
				ResponseDTO packetCreationResponse = savePacket(stringWriter, registrationDTO);
				if (packetCreationResponse.getSuccessResponseDTO() != null) {
					Parent createRoot = BaseController.load(getClass().getResource(RegistrationConstants.ACK_RECEIPT_PATH),
							applicationContext.getApplicationLanguageBundle());
					getScene(createRoot).setRoot(createRoot);
				} else {
					clearRegistrationData();
					createPacket();
				}
			} else if (templateResponse != null && templateResponse.getErrorResponseDTOs() != null) {
				generateAlert(RegistrationConstants.ERROR, "Unable to display Acknowledgement Screen");
				clearRegistrationData();
				createPacket();
			}

		} catch (RegBaseCheckedException regBaseCheckedException) {
			LOGGER.error("REGISTRATION - OFFICER_PACKET_MANAGER - CREATE PACKET", APPLICATION_NAME, APPLICATION_ID,
					regBaseCheckedException.getMessage());
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - UI- Officer Packet Create ", APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage());
		}
	}

	/**
	 * Validating screen authorization and Approve, Reject and Hold packets
	 */
	public void approvePacket() {
		try {
			Parent root = BaseController.load(getClass().getResource(RegistrationConstants.PENDING_APPROVAL_PAGE));

			LOGGER.debug("REGISTRATION - APPROVE_PACKET - REGISTRATION_OFFICER_PACKET_CONTROLLER", APPLICATION_NAME,
					APPLICATION_ID, "Validating Approve Packet screen for specific role");

			if (!validateScreenAuthorization(root.getId())) {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.AUTHORIZATION_ERROR);
			} else {
				ObservableList<Node> nodes = homeController.getMainBox().getChildren();
				IntStream.range(1, nodes.size()).forEach(index -> {
					nodes.get(index).setVisible(false);
					nodes.get(index).setManaged(false);
				});
				nodes.add(root);
			}
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - OFFICER_PACKET_MANAGER - APPROVE PACKET", APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage());

			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_APPROVAL_PAGE);
		}
	}

	/**
	 * Validating screen authorization and Uploading packets to FTP server
	 */
	public void uploadPacket() {
		try {
			uploadRoot = BaseController.load(getClass().getResource(RegistrationConstants.FTP_UPLOAD_PAGE));

			LOGGER.debug("REGISTRATION - UPLOAD_PACKET - REGISTRATION_OFFICER_PACKET_CONTROLLER", APPLICATION_NAME,
					APPLICATION_ID, "Validating Upload Packet screen for specific role");

			if (!validateScreenAuthorization(uploadRoot.getId())) {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.AUTHORIZATION_ERROR);
			} else {

				ObservableList<Node> nodes = homeController.getMainBox().getChildren();
				IntStream.range(1, nodes.size()).forEach(index -> {
					nodes.get(index).setVisible(false);
					nodes.get(index).setManaged(false);
				});
				nodes.add(uploadRoot);
			}
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - UI- Officer Packet upload", APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage());
		}
	}

	public void updateUIN(ActionEvent event) {
		try {
			Parent root = BaseController.load(getClass().getResource(RegistrationConstants.UIN_UPDATE));

			LOGGER.debug("REGISTRATION - update UIN - REGISTRATION_OFFICER_PACKET_CONTROLLER", APPLICATION_NAME,
					APPLICATION_ID, "updating UIN");

			if (!validateScreenAuthorization(root.getId())) {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.AUTHORIZATION_ERROR);
			} else {
				ObservableList<Node> nodes = homeController.getMainBox().getChildren();
				IntStream.range(1, nodes.size()).forEach(index -> {
					nodes.get(index).setVisible(false);
					nodes.get(index).setManaged(false);
				});
				nodes.add(root);
			}
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - UI- UIN Update", APPLICATION_NAME, APPLICATION_ID, ioException.getMessage());
		}
	}

	/**
	 * Sync data through batch jobs.
	 *
	 * @param event
	 *            the event
	 */
	public void syncData() {

		AnchorPane syncData;
		try {
			syncData = BaseController.load(getClass().getResource(RegistrationConstants.SYNC_DATA));
			ObservableList<Node> nodes = homeController.getMainBox().getChildren();
			IntStream.range(1, nodes.size()).forEach(index -> {
				nodes.get(index).setVisible(false);
				nodes.get(index).setManaged(false);
			});
			nodes.add(syncData);
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - REDIRECTHOME - REGISTRATION_OFFICER_DETAILS_CONTROLLER", APPLICATION_NAME,
					APPLICATION_ID, ioException.getMessage());
		}
	}

	/**
	 * This method is to trigger the Pre registration sync service
	 * 
	 * @param event
	 */
	@FXML
	public void downloadPreRegData() {
		ResponseDTO responseDTO = preRegistrationDataSyncService
				.getPreRegistrationIds(RegistrationConstants.JOB_TRIGGER_POINT_USER);

		if (responseDTO.getSuccessResponseDTO() != null) {
			SuccessResponseDTO successResponseDTO = responseDTO.getSuccessResponseDTO();
			generateAlert(successResponseDTO.getCode(), successResponseDTO.getMessage());

		} else if (responseDTO.getErrorResponseDTOs() != null) {

			ErrorResponseDTO errorresponse = responseDTO.getErrorResponseDTOs().get(0);
			generateAlert(errorresponse.getCode(), errorresponse.getMessage());

		}
	}

	/**
	 * change On-Board user Perspective
	 * 
	 * @param event
	 *            is an action event
	 * @throws IOException
	 */
	public void onBoardUser() {
		SessionContext.getInstance().getMapObject().put(RegistrationConstants.ONBOARD_USER, true);
		SessionContext.getInstance().getMapObject().put(RegistrationConstants.ONBOARD_USER_UPDATE, true);
		userOnboardController.initUserOnboard();
	}

	/**
	 * To save the acknowledgement receipt along with the registration data and
	 * create packet
	 */
	private ResponseDTO savePacket(Writer stringWriter, RegistrationDTO registrationDTO) {
		LOGGER.debug("REGISTRATION - SAVE_PACKET - REGISTRATION_OFFICER_PACKET_CONTROLLER",
				RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"packet creation has been started");
		byte[] ackInBytes = null;
		try {
			ackInBytes = stringWriter.toString().getBytes("UTF-8");
		} catch (java.io.IOException ioException) {
			LOGGER.error("REGISTRATION - SAVE_PACKET - REGISTRATION_OFFICER_PACKET_CONTROLLER", APPLICATION_NAME,
					APPLICATION_ID, ioException.getMessage());
		}

		if (saveAck.equalsIgnoreCase("Y")) {
			registrationDTO.getDemographicDTO().getApplicantDocumentDTO().setAcknowledgeReceipt(ackInBytes);
			registrationDTO.getDemographicDTO().getApplicantDocumentDTO().setAcknowledgeReceiptName(
					"RegistrationAcknowledgement." + RegistrationConstants.ACKNOWLEDGEMENT_FORMAT);
		}

		// packet creation
		ResponseDTO response = packetHandlerService.handle(registrationDTO);

		if (response.getSuccessResponseDTO() != null
				&& response.getSuccessResponseDTO().getMessage().equals("Success")) {
			generateEmailNotification(registrationDTO);
			try {
				// Generate the file path for storing the Encrypted Packet and Acknowledgement
				// Receipt
				String seperator = "/";
				String filePath = environment.getProperty(RegistrationConstants.PACKET_STORE_LOCATION) + seperator
						+ formatDate(new Date(),
								environment.getProperty(RegistrationConstants.PACKET_STORE_DATE_FORMAT))
										.concat(seperator).concat(registrationDTO.getRegistrationId());

				// Storing the Registration Acknowledge Receipt Image
				FileUtils.copyToFile(new ByteArrayInputStream(ackInBytes),
						new File(filePath.concat("_Ack.").concat(RegistrationConstants.ACKNOWLEDGEMENT_FORMAT)));

				LOGGER.debug("REGISTRATION - SAVE_PACKET - REGISTRATION_OFFICER_PACKET_CONTROLLER", APPLICATION_NAME,
						APPLICATION_ID, "Registration's Acknowledgement Receipt saved");
			} catch (io.mosip.kernel.core.exception.IOException ioException) {
				LOGGER.error("REGISTRATION - SAVE_PACKET - REGISTRATION_OFFICER_PACKET_CONTROLLER", APPLICATION_NAME,
						APPLICATION_ID, ioException.getMessage());
			}

			if (registrationDTO.getSelectionListDTO() == null) {

				Identity identity = registrationDTO.getDemographicDTO().getDemographicInfoDTO().getIdentity();
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
		} else {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.PACKET_CREATION_FAILURE);
		}
		return response;
	}

	/**
	 * To generate email and SMS notification to the user after successful
	 * registration
	 * 
	 * @param registrationDTO
	 */
	private void generateEmailNotification(RegistrationDTO registrationDTO) {
		LOGGER.debug("REGISTRATION - GENERATE_NOTIFICATION - REGISTRATION_OFFICER_PACKET_CONTROLLER",
				RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"generating Email/SMS notification after packet creation");

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
								notificationTemplate, registrationDTO, templateManagerBuilder);

						String number = registrationDTO.getDemographicDTO().getDemographicInfoDTO().getIdentity()
								.getPhone();
						String rid = registrationDTO.getRegistrationId();

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

						String emailId = registrationDTO.getDemographicDTO().getDemographicInfoDTO().getIdentity()
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

	/**
	 * To generate alert if the email/sms notification is not sent
	 */
	private void generateNotificationAlert(String alertMessage) {
		/* Generate Alert */
		generateAlert(RegistrationConstants.ERROR, alertMessage);
	}
}
