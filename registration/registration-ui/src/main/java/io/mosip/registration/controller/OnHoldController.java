package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.logback.factory.MosipLogfactory;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.service.RegistrationApprovalService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
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
	
	@FXML
	private Hyperlink exit;

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
		LOGGER.debug("REGISTRATION - PAGE_LOADING - REGISTRATION_ONHOLD_CONTROLLER", APPLICATION_NAME,
				APPLICATION_ID, "Page loading has been started");
		submit.disableProperty().set(true);
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
		LOGGER.debug("REGISTRATION - UPDATE_PACKET_STATUS - REGISTRATION_ONHOLD_CONTROLLER", APPLICATION_NAME,
				APPLICATION_ID, "Packet updation as on hold has been started");
		String approverUserId = SessionContext.getInstance().getUserContext().getUserId();
		String approverRoleCode = SessionContext.getInstance().getUserContext().getRoles().get(0);

		if(registration.packetUpdateStatus(regId, RegistrationClientStatusCode.ON_HOLD.getCode(),approverUserId, 
				onHoldComboBox.getSelectionModel().getSelectedItem(), approverRoleCode)) {
		generateAlert(RegistrationConstants.STATUS,AlertType.INFORMATION,RegistrationConstants.ONHOLD_STATUS_MESSAGE);
		submit.disableProperty().set(true);
		registrationController.tablePagination();
	}
	else {
		generateAlert(RegistrationConstants.STATUS,AlertType.INFORMATION,RegistrationConstants.ONHOLD_STATUS_FAILURE_MESSAGE);
	}
		primarystage.close();
		LOGGER.debug("REGISTRATION - UPDATE_PACKET_STATUS - REGISTRATION_ONHOLD_CONTROLLER", APPLICATION_NAME,
				APPLICATION_ID, "Packet updation as on hold has been ended");
	}
	/**
	 * {@code rejectionWindowExit} is event class to exit from reason for rejection
	 * pop up window.
	 * 
	 * @param event
	 */
	public void exitWindow(ActionEvent event) {
		primarystage.close();
																										
																 
  
	}

	/**
	 * Rejection combobox action.
	 * 
	 * @param event
	 */
	public void onHoldComboboxAction(ActionEvent event) {
		submit.disableProperty().set(false);
	}
}