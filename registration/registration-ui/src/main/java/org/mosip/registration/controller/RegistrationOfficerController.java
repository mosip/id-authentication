package org.mosip.registration.controller;

import static org.mosip.registration.constants.RegistrationUIExceptionEnum.REG_UI_HOMEPAGE_NULLPOINTER_EXCEPTION;

import java.net.URL;
import java.util.ResourceBundle;

import org.mosip.registration.constants.RegistrationUIExceptionCode;
import org.mosip.registration.exception.RegBaseUncheckedException;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

@Component
public class RegistrationOfficerController extends BaseController implements Initializable {

	@FXML
	VBox mainBox;	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {	
			HBox headerRoot = BaseController.load(getClass().getResource("/fxml/Header.fxml"));
			mainBox.getChildren().add(headerRoot);
			AnchorPane updateRoot = BaseController.load(getClass().getResource("/fxml/UpdateLayout.fxml"));
			mainBox.getChildren().add(updateRoot);
			AnchorPane optionRoot = BaseController.load(getClass().getResource("/fxml/RegistrationOfficerPacketLayout.fxml"));
			mainBox.getChildren().add(optionRoot);
			
			RegistrationAppInitialization.scene.setRoot(mainBox);			
			ClassLoader loader = Thread.currentThread().getContextClassLoader(); 
			RegistrationAppInitialization.scene.getStylesheets().add(loader.getResource("application.css").toExternalForm());
			
		} catch (NullPointerException nullPointerException) {
			throw new RegBaseUncheckedException(REG_UI_HOMEPAGE_NULLPOINTER_EXCEPTION.getErrorMessage(),
					REG_UI_HOMEPAGE_NULLPOINTER_EXCEPTION.getErrorMessage());
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationUIExceptionCode.REG_UI_HOMEPAGE_LOADER_EXCEPTION,
					runtimeException.getMessage());
		}
	}	
}

	
