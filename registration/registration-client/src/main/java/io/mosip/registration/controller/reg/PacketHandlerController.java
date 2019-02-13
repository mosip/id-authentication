package io.mosip.registration.controller.reg;

import static io.mosip.kernel.core.util.DateUtils.formatDate;
import static io.mosip.registration.constants.RegistrationConstants.ACKNOWLEDGEMENT_TEMPLATE;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
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
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.dto.RegistrationApprovalDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.demographic.AddressDTO;
import io.mosip.registration.dto.demographic.Identity;
import io.mosip.registration.dto.demographic.LocationDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.packet.PacketHandlerService;
import io.mosip.registration.service.packet.ReRegistrationService;
import io.mosip.registration.service.packet.RegistrationApprovalService;
import io.mosip.registration.service.sync.PreRegistrationDataSyncService;
import io.mosip.registration.service.template.TemplateService;
import io.mosip.registration.util.acktemplate.TemplateGenerator;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 * Class for Registration Packet operations
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@Controller
public class PacketHandlerController extends BaseController implements Initializable {

	private static final Logger LOGGER = AppConfig.getLogger(PacketHandlerController.class);

	@FXML
	private AnchorPane acknowRoot;

	@FXML
	private AnchorPane uploadRoot;

	@FXML
	private AnchorPane optionRoot;

	@FXML
	private Label pendingApprovalCountLbl;

	@FXML
	private Label reRegistrationCountLbl;

	@FXML
	private AnchorPane eodProcessAnchorPane;

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

	@Value("${SAVE_ACKNOWLEDGEMENT_INSIDE_PACKET}")
	private String saveAck;

	@Autowired
	private Environment environment;

	@Autowired
	private RegistrationApprovalService registrationApprovalService;

	@Autowired
	private ReRegistrationService reRegistrationService;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		if (SessionContext.userContext().getRoles().get(0).equals(RegistrationConstants.OFFICER)) {
			eodProcessAnchorPane.setVisible(false);
		}
		pendingApprovalCountLbl.setText(RegistrationUIConstants.NO_PENDING_APPLICATIONS);
		reRegistrationCountLbl.setText(RegistrationUIConstants.NO_RE_REGISTER_APPLICATIONS);

		List<RegistrationApprovalDTO> pendingApprovalRegistrations = registrationApprovalService
				.getEnrollmentByStatus(RegistrationClientStatusCode.CREATED.getCode());
		List<PacketStatusDTO> reRegisterRegistrations = reRegistrationService.getAllReRegistrationPackets();

		if (!pendingApprovalRegistrations.isEmpty()) {
			pendingApprovalCountLbl
					.setText(pendingApprovalRegistrations.size() + " " + RegistrationUIConstants.APPLICATIONS);
		}
		if (!reRegisterRegistrations.isEmpty()) {
			reRegistrationCountLbl.setText(reRegisterRegistrations.size() + " " + RegistrationUIConstants.APPLICATIONS);
		}

	}

	/**
	 * Validating screen authorization and Creating Packet and displaying
	 * acknowledgement form
	 */
	public void createPacket() {

		try {
			Parent createRoot = BaseController.load(getClass().getResource(RegistrationConstants.CREATE_PACKET_PAGE),
					applicationContext.getApplicationLanguageBundle());
			LOGGER.info("REGISTRATION - CREATE_PACKET - REGISTRATION_OFFICER_PACKET_CONTROLLER", APPLICATION_NAME,
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
						errorMessage.append(errorResponseDTO.getMessage() + "\n\n");
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
			RegistrationDTO registrationDTO = (RegistrationDTO) SessionContext.map()
					.get(RegistrationConstants.REGISTRATION_DATA);
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
					Parent createRoot = BaseController.load(
							getClass().getResource(RegistrationConstants.ACK_RECEIPT_PATH),
							applicationContext.getApplicationLanguageBundle());
					getScene(createRoot).setRoot(createRoot);
				} else {
					clearRegistrationData();
					createPacket();
				}
			} else if (templateResponse != null && templateResponse.getErrorResponseDTOs() != null) {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_ACKNOWLEDGEMENT_PAGE);
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

			LOGGER.info("REGISTRATION - APPROVE_PACKET - REGISTRATION_OFFICER_PACKET_CONTROLLER", APPLICATION_NAME,
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

			LOGGER.info("REGISTRATION - UPLOAD_PACKET - REGISTRATION_OFFICER_PACKET_CONTROLLER", APPLICATION_NAME,
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

	public void updateUIN() {
		try {
			Parent root = BaseController.load(getClass().getResource(RegistrationConstants.UIN_UPDATE));

			LOGGER.info("REGISTRATION - update UIN - REGISTRATION_OFFICER_PACKET_CONTROLLER", APPLICATION_NAME,
					APPLICATION_ID, "updating UIN");

			if (!validateScreenAuthorization(root.getId())) {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.AUTHORIZATION_ERROR);
			} else {

				StringBuilder errorMessage = new StringBuilder();
				ResponseDTO responseDTO;
				responseDTO = validateSyncStatus();
				List<ErrorResponseDTO> errorResponseDTOs = responseDTO.getErrorResponseDTOs();
				if (errorResponseDTOs != null && !errorResponseDTOs.isEmpty()) {
					for (ErrorResponseDTO errorResponseDTO : errorResponseDTOs) {
						errorMessage.append(errorResponseDTO.getMessage() + "\n\n");
					}
					generateAlert(RegistrationConstants.ERROR, errorMessage.toString().trim());

				} else {
					ObservableList<Node> nodes = homeController.getMainBox().getChildren();
					IntStream.range(1, nodes.size()).forEach(index -> {
						nodes.get(index).setVisible(false);
						nodes.get(index).setManaged(false);
					});
					nodes.add(root);
				}
			}
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - UI- UIN Update", APPLICATION_NAME, APPLICATION_ID, ioException.getMessage());
		}
	}

	/**
	 * Sync data through batch jobs.
	 *
	 * @param event the event
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
	 * @param event is an action event
	 * @throws IOException
	 */
	public void onBoardUser() {
		SessionContext.map().put(RegistrationConstants.ONBOARD_USER, true);
		SessionContext.map().put(RegistrationConstants.ONBOARD_USER_UPDATE, true);
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
			
			String mobile = registrationDTO.getDemographicDTO().getDemographicInfoDTO().getIdentity().getPhone();
			String email = registrationDTO.getDemographicDTO().getDemographicInfoDTO().getIdentity().getEmail();
			sendEmailNotification(email);
			sendSMSNotification(mobile);
			
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

				SessionContext.map().put(RegistrationConstants.ADDRESS_KEY, addressDTO);
			}
		} else {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.PACKET_CREATION_FAILURE);
		}
		return response;
	}

	/**
	 * Load re registration screen.
	 */
	public void loadReRegistrationScreen() {
		try {
			Parent root = BaseController.load(getClass().getResource(RegistrationConstants.REREGISTRATION_PAGE));

			LOGGER.info("REGISTRATION - LOAD_REREGISTRATION_SCREEN - REGISTRATION_OFFICER_PACKET_CONTROLLER",
					APPLICATION_NAME, APPLICATION_ID, "Loading reregistration screen");

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
			LOGGER.error("REGISTRATION - LOAD_REREGISTRATION_SCREEN - REGISTRATION_OFFICER_PACKET_CONTROLLER",
					APPLICATION_NAME, APPLICATION_ID, ioException.getMessage());

			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_APPROVAL_PAGE);
		}
	}
}
