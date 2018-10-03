package org.mosip.registration.controller;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.mosip.kernel.logger.factory.MosipLogfactory;
import org.mosip.registration.dto.RegistrationDTO;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.service.TemplateService;
import org.mosip.registration.util.acktemplate.VelocityPDFGenerator;
import org.mosip.registration.util.dataprovider.DataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import static org.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;
import static org.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static org.mosip.registration.constants.RegConstants.APPLICATION_NAME;

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
	 * Creating Packet and displaying acknowledgement form
	 */
	public void createPacket(ActionEvent event) throws RegBaseCheckedException {

		try {
			RegistrationDTO registrationDTO = DataProvider.getPacketDTO();
			ackReceiptController.setRegistrationData(registrationDTO);

			File ackTemplate = templateService.createReceipt();
			Writer writer = velocityGenerator.generateTemplate(ackTemplate, registrationDTO);
			ackReceiptController.setStringWriter(writer);

			Stage primaryStage = new Stage();
			Parent root = BaseController.load(getClass().getResource("/fxml/AckReceipt.fxml"));
			primaryStage.setResizable(false);
			primaryStage.setTitle("Registration Acknowledgement");
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (RegBaseCheckedException regBaseCheckedException) {
			LOGGER.error("REGISTRATION - OFFICER_PACKET_MANAGER - CREATE PACKET", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), regBaseCheckedException.getMessage());
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - UI- Officer Packet Create ", APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage());
		}
	}

	/**
	 * Approve, Reject and Hold packets
	 */
	public void approvePacket(ActionEvent event) {
		try {
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
			Parent root = BaseController.load(getClass().getResource("/fxml/RegistrationApproval.fxml"));
			nodes.add(root);
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - UI- Officer Packet approve ", APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage());
		}
	}

	/**
	 * Uploading packets to FTP server
	 */
	public void uploadPacket(ActionEvent event) {
		try {
			uploadRoot = BaseController.load(getClass().getResource("/fxml/FTPLogin.fxml"));
			Stage uploadStage = new Stage();
			Scene scene = new Scene(uploadRoot, 600, 600);
			uploadStage.setResizable(false);
			uploadStage.setScene(scene);
			uploadStage.show();
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - UI- Officer Packet upload", APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage());
		}
	}

}
