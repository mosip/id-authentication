package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationUIExceptionEnum.REG_UI_AUTHORIZATION_EXCEPTION;
import static io.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.TemplateService;
import io.mosip.registration.ui.constants.RegistrationUIConstants;
import io.mosip.registration.util.acktemplate.VelocityPDFGenerator;
import io.mosip.registration.util.dataprovider.DataProvider;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Class for Registration Packet operations
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@Controller
public class RegistrationOfficerPacketController extends BaseController {

	private static MosipLogger LOGGER;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	@FXML
	private AnchorPane acknowRoot;

	@FXML
	private BorderPane uploadRoot;

	@Autowired
	private AckReceiptController ackReceiptController;

	@Autowired
	private TemplateService templateService;

	@Autowired
	private VelocityPDFGenerator velocityGenerator;

	/**
	 * Validating screen authorization and Creating Packet and displaying
	 * acknowledgement form
	 */
	public void createPacket(ActionEvent event) throws RegBaseCheckedException {

		try {
			RegistrationDTO registrationDTO = DataProvider.getPacketDTO();
			ackReceiptController.setRegistrationData(registrationDTO);

			File ackTemplate = templateService.createReceipt();
			Writer writer = velocityGenerator.generateTemplate(ackTemplate, registrationDTO);
			ackReceiptController.setStringWriter(writer);

			Parent createRoot = BaseController.load(getClass().getResource("/fxml/AckReceipt.fxml"));

			LOGGER.debug("REGISTRATION - CREATE_PACKET - REGISTRATION_OFFICER_PACKET_CONTROLLER",
					getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID),
					"Validating Create Packet screen for specific role");

			if (!validateScreenAuthorization(createRoot.getId())) {
				generateAlert(RegistrationUIConstants.AUTHORIZATION_ALERT_TITLE,
						AlertType.valueOf(RegistrationUIConstants.ALERT_ERROR),
						RegistrationUIConstants.AUTHORIZATION_INFO_MESSAGE,
						REG_UI_AUTHORIZATION_EXCEPTION.getErrorMessage());
			} else {
				Stage primaryStage = new Stage();

				primaryStage.setResizable(false);
				primaryStage.setTitle("Registration Acknowledgement");
				Scene scene = new Scene(createRoot);
				primaryStage.setScene(scene);
				primaryStage.show();
			}
		} catch (RegBaseCheckedException regBaseCheckedException) {
			LOGGER.error("REGISTRATION - OFFICER_PACKET_MANAGER - CREATE PACKET", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), regBaseCheckedException.getMessage());
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
			Parent root = BaseController.load(getClass().getResource("/fxml/RegistrationApproval.fxml"));

			LOGGER.debug("REGISTRATION - APPROVE_PACKET - REGISTRATION_OFFICER_PACKET_CONTROLLER",
					getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID),
					"Validating Approve Packet screen for specific role");

			if (!validateScreenAuthorization(root.getId())) {
				generateAlert(RegistrationUIConstants.AUTHORIZATION_ALERT_TITLE,
						AlertType.valueOf(RegistrationUIConstants.ALERT_ERROR),
						RegistrationUIConstants.AUTHORIZATION_INFO_MESSAGE,
						REG_UI_AUTHORIZATION_EXCEPTION.getErrorMessage());
			} else {
				Button button = (Button) event.getSource();
				AnchorPane anchorPane = (AnchorPane) button.getParent();
				VBox vBox = (VBox) (anchorPane.getParent());
				ObservableList<Node> nodes = vBox.getChildren();
				Node child;
				for (int index = 1; index < nodes.size(); index++) {
					child = nodes.get(index);
					child.setVisible(false);
					child.setManaged(false);
				}
				nodes.add(root);
			}
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - UI- Officer Packet approve ", APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage());
		}
	}

	/**
	 * Validating screen authorization and Uploading packets to FTP server
	 */
	public void uploadPacket(ActionEvent event) {
		try {
			uploadRoot = BaseController.load(getClass().getResource("/fxml/FTPLogin.fxml"));

			LOGGER.debug("REGISTRATION - UPLOAD_PACKET - REGISTRATION_OFFICER_PACKET_CONTROLLER",
					getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID),
					"Validating Upload Packet screen for specific role");

			if (!validateScreenAuthorization(uploadRoot.getId())) {
				generateAlert(RegistrationUIConstants.AUTHORIZATION_ALERT_TITLE,
						AlertType.valueOf(RegistrationUIConstants.ALERT_ERROR),
						RegistrationUIConstants.AUTHORIZATION_INFO_MESSAGE,
						REG_UI_AUTHORIZATION_EXCEPTION.getErrorMessage());
			} else {
				Stage uploadStage = new Stage();
				Scene scene = new Scene(uploadRoot, 600, 600);
				uploadStage.setResizable(false);
				uploadStage.setScene(scene);
				uploadStage.show();
			}
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - UI- Officer Packet upload", APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage());
		}
	}

}
