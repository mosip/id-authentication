package io.mosip.registration.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.registration.service.RegistrationApprovalService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

@Controller
public class RejectionController extends BaseController implements Initializable{

	String regId=null;
	Stage primarystage ;
	
	@Autowired
	RegistrationApprovalController registrationController;
	@Autowired
	RegistrationApprovalService registration;
	@FXML
	ComboBox<String> rejectionComboBox;
	@FXML
	Button submit;
	
	ObservableList<String> rejectionCommentslist=FXCollections.observableArrayList("Correction not possible",
            "Wrong Person",
            "Invalid Data",
            "Incorrect indroducer",
            "Incorrect ID");
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		rejectionComboBox.getItems().clear();
		rejectionComboBox.setItems(rejectionCommentslist);
	}
	
	public void initData(String id,Stage stage) {
		regId=id;
		primarystage = stage;
	}

	public void packetUpdateStatus(ActionEvent event) {
		if(registration.packetUpdateStatus(regId, "I","mahesh123", 
				rejectionComboBox.getSelectionModel().getSelectedItem(), "mahesh123")) {
		generateAlert("Status", AlertType.INFORMATION, "Packet Rejected Successfully..");
		submit.disableProperty().set(true);
		registrationController.Pagination();
		
		}
		else {
			generateAlert("Status",AlertType.INFORMATION,"");
		}
		primarystage.close();
	}
}
