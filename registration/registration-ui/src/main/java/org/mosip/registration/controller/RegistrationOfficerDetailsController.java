package org.mosip.registration.controller;


import org.mosip.registration.context.SessionContext;
import org.springframework.stereotype.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

@Controller
public class RegistrationOfficerDetailsController extends BaseController {

	@FXML
	private Label registrationOfficerName;
	
	@FXML
	private Label registrationOfficeId;
	
	@FXML
	private Label registrationOfficeLocation;
			
	public void initialize() throws Exception {
		registrationOfficerName.setText(LoginController.userDTO.getUsername());
		registrationOfficeId.setText(LoginController.userDTO.getCenterId());
		registrationOfficeLocation.setText(LoginController.userDTO.getCenterLocation());
	}
	
	@FXML
	public void logout(ActionEvent event) {
		SessionContext.getInstance().destroySession();
		//System.out.println(SessionContext.getInstance().getMapObject().entrySet());
		BorderPane loginpage = BaseController.load(getClass().getResource("/fxml/RegistrationLogin.fxml"));
		String loginModeFXMLpath = "/fxml/LoginWithCredentials.fxml";
		AnchorPane loginType = BaseController.load(getClass().getResource(loginModeFXMLpath));
		loginpage.setCenter(loginType);
		RegistrationAppInitialization.scene.setRoot(loginpage);
	}
	
	@FXML
	public void redirectHome(ActionEvent event) {
		VBox homePage = BaseController.load(getClass().getResource("/fxml/RegistrationOfficerLayout.fxml"));
		RegistrationAppInitialization.scene.setRoot(homePage);
	}
}

