package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.ACKNOWLEDGEMENT_TEMPLATE;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.templatemanager.spi.TemplateManagerBuilder;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.template.TemplateService;
import io.mosip.registration.util.acktemplate.TemplateGenerator;
import io.mosip.registration.util.dataprovider.DataProvider;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

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
					templateManagerBuilder);
			if (templateResponse != null && templateResponse.getSuccessResponseDTO() != null) {
				Writer stringWriter = (Writer) templateResponse.getSuccessResponseDTO().getOtherAttributes()
						.get(RegistrationConstants.TEMPLATE_NAME);
				ackReceiptController.setStringWriter(stringWriter);
				Parent createRoot = BaseController.load(getClass().getResource(RegistrationConstants.ACK_RECEIPT_PATH));
				getScene(createRoot);
			} else if (templateResponse != null && templateResponse.getErrorResponseDTOs() != null) {
				generateAlert(RegistrationConstants.ERROR, "Unable to display Acknowledgement Screen");
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
			Parent root = BaseController.load(getClass().getResource(RegistrationConstants.APPROVAL_PAGE));

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

	public void updateUIN(ActionEvent event) {
		try {
			Parent root = BaseController.load(getClass().getResource(RegistrationConstants.UIN_UPDATE));

			LOGGER.info("REGISTRATION - update UIN - REGISTRATION_OFFICER_PACKET_CONTROLLER", APPLICATION_NAME,
					APPLICATION_ID, "updating UIN");

			if (!validateScreenAuthorization(root.getId())) {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.AUTHORIZATION_ERROR);
			} else {
				Button button = (Button) event.getSource();
				AnchorPane anchorPane = (AnchorPane) button.getParent();
				VBox vBox = (VBox) (anchorPane.getParent());
				ObservableList<Node> nodes = vBox.getChildren();
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
}
