package org.mosip.registration.controller;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;

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

@Controller
public class RegistrationOfficerPacketController extends BaseController{
	
	@FXML
	private AnchorPane acknowRoot;
	
	@FXML
	private BorderPane uploadRoot;
	
	@Autowired
	private AckReceiptController ackReceiptController;
	
	@Autowired
	TemplateService templateService;
	
	@Autowired
	VelocityPDFGenerator velocityGenerator;
	
	
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
			
		} catch (RegBaseCheckedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void approvePacket(ActionEvent event) {
		try {
			Button button = (Button) event.getSource();
			AnchorPane anchorPane = (AnchorPane) button.getParent();
			VBox vBox = (VBox)(anchorPane.getParent());
			ObservableList<Node> nodes = vBox.getChildren();
			Node child;
			for (int index=1; index<nodes.size(); index++) {
				child = nodes.get(index);
				child.setVisible(false);
				child.setManaged(false);
			}
			System.out.println(vBox.getChildren().size());
			//Stage primaryStage = new Stage();
			Parent root = BaseController.load(getClass().getResource("/fxml/RegistrationApproval.fxml"));
			nodes.add(root);
			//primaryStage.setResizable(false);
			//primaryStage.setTitle("Registration Acknowledgement");
			//Scene scene = new Scene(root);
			//primaryStage.setScene(scene);
			//primaryStage.show();
		} catch(Exception exception) {
			
		}
	}
	
	public void uploadPacket(ActionEvent event) {
		
		uploadRoot = BaseController.load(getClass().getResource("/fxml/FTPLogin.fxml"));
		Stage uploadStage = new Stage();
		Scene scene = new Scene(uploadRoot, 600, 600);
		uploadStage.setResizable(false);
		uploadStage.setScene(scene);
		uploadStage.show();
		
	}
	
	
}
