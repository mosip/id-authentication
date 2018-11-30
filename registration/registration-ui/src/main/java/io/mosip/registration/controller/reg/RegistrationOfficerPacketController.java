package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.auth.LoginController;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
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
public class RegistrationOfficerPacketController extends BaseController {

	private static final Logger LOGGER = AppConfig.getLogger(RegistrationOfficerPacketController.class);

	@FXML
	private AnchorPane acknowRoot;

	@FXML
	private AnchorPane uploadRoot;

	@Autowired
	private AckReceiptController ackReceiptController;

	/**
	 * Validating screen authorization and Creating Packet and displaying
	 * acknowledgement form
	 */

	public void createPacket() {

		try {
			Parent createRoot = BaseController.load(getClass().getResource(RegistrationConstants.CREATE_PACKET_PAGE), ApplicationContext.getInstance().getApplicationLanguageBundle());
			LOGGER.debug("REGISTRATION - CREATE_PACKET - REGISTRATION_OFFICER_PACKET_CONTROLLER",
					APPLICATION_NAME, APPLICATION_ID,
					"Validating Create Packet screen for specific role");

			if (!validateScreenAuthorization(createRoot.getId())) {
				generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.AUTHORIZATION_ERROR);
			} else {
				StringBuilder errorMessage = new StringBuilder();
				String errorAlert = null;
				ResponseDTO responseDTO;
				responseDTO = validateSyncStatus();
				List<ErrorResponseDTO> errorResponseDTOs = responseDTO.getErrorResponseDTOs();
				if (errorResponseDTOs != null && !errorResponseDTOs.isEmpty()) {
					for (ErrorResponseDTO errorResponseDTO : errorResponseDTOs) {
						errorMessage
						.append(errorResponseDTO.getMessage() + " - " + errorResponseDTO.getCode() + "\n\n");
				errorAlert = errorResponseDTO.getInfoType();
					}
					generateAlert(RegistrationConstants.ALERT_ERROR, errorMessage.toString().trim());

				} else {
					LoginController.getScene().setRoot(createRoot);
					ClassLoader loader = Thread.currentThread().getContextClassLoader();
					LoginController.getScene().getStylesheets()
							.add(loader.getResource("application.css").toExternalForm());
				}
			}

		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - UI- Officer Packet Create ", APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage());
		}
	}

	public void showReciept(RegistrationDTO registrationDTO, String capturePhotoUsingDevice) {

		try {
			registrationDTO = DataProvider.getPacketDTO(registrationDTO, capturePhotoUsingDevice);
			ackReceiptController.setRegistrationData(registrationDTO);			
			Parent createRoot = BaseController.load(getClass().getResource(RegistrationConstants.ACK_RECEIPT_PATH));
			LoginController.getScene().setRoot(createRoot);
		} catch (RegBaseCheckedException regBaseCheckedException) {
			LOGGER.error("REGISTRATION - OFFICER_PACKET_MANAGER - CREATE PACKET", APPLICATION_NAME,
					APPLICATION_ID, regBaseCheckedException.getMessage());
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - UI- Officer Packet Create ", APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage());
		}

	}

	/**
	 * Validating screen authorization and Approve, Reject and Hold packets
	 */
	public void approvePacket(ActionEvent event) {
		try {
			Parent root = BaseController.load(getClass().getResource(RegistrationConstants.APPROVAL_PAGE));

			LOGGER.debug("REGISTRATION - APPROVE_PACKET - REGISTRATION_OFFICER_PACKET_CONTROLLER",
					APPLICATION_NAME, APPLICATION_ID,
					"Validating Approve Packet screen for specific role");

			if (!validateScreenAuthorization(root.getId())) {
				generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.AUTHORIZATION_ERROR);
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
			LOGGER.error("REGISTRATION - OFFICER_PACKET_MANAGER - APPROVE PACKET", APPLICATION_NAME,
					APPLICATION_ID, ioException.getMessage());
		
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.UNABLE_LOAD_APPROVAL_PAGE);
		}
	}

	/**
	 * Validating screen authorization and Uploading packets to FTP server
	 */
	public void uploadPacket(ActionEvent event) {
		try {
			uploadRoot = BaseController.load(getClass().getResource(RegistrationConstants.FTP_UPLOAD_PAGE));

			LOGGER.debug("REGISTRATION - UPLOAD_PACKET - REGISTRATION_OFFICER_PACKET_CONTROLLER",
					APPLICATION_NAME, APPLICATION_ID,
					"Validating Upload Packet screen for specific role");

			if (!validateScreenAuthorization(uploadRoot.getId())) {
				generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.AUTHORIZATION_ERROR);
			} else {
				Button button = (Button) event.getSource();
				AnchorPane anchorPane = (AnchorPane) button.getParent();
				VBox vBox = (VBox) (anchorPane.getParent());
				ObservableList<Node> nodes = vBox.getChildren();
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

}
