package org.mosip.registration.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.mosip.kernel.logger.factory.MosipLogfactory;
import org.mosip.registration.constants.RegClientStatusCode;
import org.mosip.registration.service.RegistrationApprovalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import static org.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;
import static org.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static org.mosip.registration.constants.RegConstants.APPLICATION_NAME;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 *{@code OnHoldController} is controller class for onHoldComment fxml page 
 *which consists the methods for updating the status to onhold
 *
 * @author Mahesh Kumar
 */
@Controller
public class OnHoldController extends BaseController implements Initializable{

	
	/**
	 * Registration Id
	 */
	private String regId = null;

	/**
	 * Stage
	 */
	private Stage primarystage;
			
	/**
	 * Instance of {@link MosipLogger}
	 */
	private static MosipLogger LOGGER;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/**
	 * Object for RegistrationApprovalController
	 */
	@Autowired
	private RegistrationApprovalController registrationController;
	/**
	 * Object for RegistrationApprovalService
	 */
	@Autowired
	private RegistrationApprovalService registration;
	/**
	 * Combobox for for on hold reason
	 */
	@FXML
	private ComboBox<String> onHoldComboBox;
	/**
	 * Button for Submit
	 */
	@FXML
	private Button submit;

	 ObservableList<String> onHoldCommentslist=FXCollections.observableArrayList("Gender/Photo mismatch",
	            "Partial Biometric",
	            "Partial Iries",
	            "Photo not clear",
	            "Id not clear");
	/* (non-Javadoc)
	 * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.debug("REGISTRATION - PAGE_LOADING - REGISTRATION_ONHOLD_CONTROLLER", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "Page loading has been started");

		onHoldComboBox.getItems().clear();
		onHoldComboBox.setItems(onHoldCommentslist);

	}
	/**
	 * Method to get the Stage and Registration Id from the other controller page
	 * @param id
	 * @param stage
	 */
	public void initData(String id,Stage stage) {
	    regId=id;
	    primarystage=stage;
	  }
	/**
	 * {@code updatePacketStatus} is event class for updating packet status to onhold
	 * @param event
	 */
	public void updatePacketStatus(ActionEvent event) {
		LOGGER.debug("REGISTRATION - UPDATE_PACKET_STATUS - REGISTRATION_ONHOLD_CONTROLLER", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "Packet updation as on hold has been started");
		//TODO : get the approverId from session context
		if(registration.packetUpdateStatus(regId, RegClientStatusCode.ON_HOLD.getCode(),"mahesh123", 
				onHoldComboBox.getSelectionModel().getSelectedItem(), "mahesh123")) {
		generateAlert("Status",AlertType.INFORMATION,"Registration moved to On Hold.");
		submit.disableProperty().set(true);
		registrationController.tablePagination();
	}
	else {
		generateAlert("Status",AlertType.INFORMATION,"");
	}
		primarystage.close();
		LOGGER.debug("REGISTRATION - UPDATE_PACKET_STATUS - REGISTRATION_ONHOLD_CONTROLLER", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "Packet updation as on hold has been ended");
		
	}
}