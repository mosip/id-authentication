package org.mosip.registration.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.mosip.registration.service.RegistrationApprovalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

@Controller
public class OnHoldController extends BaseController implements Initializable{

	String regId = null;
	Stage primarystage;
			
	@Autowired
	RegistrationApprovalController registrationController;
	
	@Autowired
	RegistrationApprovalService registration;
	
	@FXML
	ComboBox<String> onHoldComboBox;
	@FXML
	Button submit;
	
	 ObservableList<String> onHoldCommentslist=FXCollections.observableArrayList("Gender/Photo mismatch",
	            "Partial Biometric",
	            "Partial Iries",
	            "Photo not clear",
	            "Id not clear");
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		onHoldComboBox.getItems().clear();
		onHoldComboBox.setItems(onHoldCommentslist);

	}
	public void initData(String id,Stage stage) {
	    regId=id;
	    primarystage=stage;
	  }
	public void updatePacketStatus(ActionEvent event) {
		
		if(registration.packetUpdateStatus(regId, "H","mahesh123", 
				onHoldComboBox.getSelectionModel()
								.getSelectedItem()
								.toString(), "mahesh123")) {
		generateAlert("Status",AlertType.INFORMATION,"Registration moved to On Hold.");
		submit.disableProperty().set(true);
		registrationController.Pagination();
	}
	else {
		generateAlert("Status",AlertType.INFORMATION,"");
	}
		primarystage.close();
	}
}