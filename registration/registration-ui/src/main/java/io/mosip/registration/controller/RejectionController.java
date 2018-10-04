package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.registration.constants.RegClientStatusCode;
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

/**
*
* {@code RejectionController} is the controller class for rejection of packets
* @author Mahesh Kumar
*/
@Controller
public class RejectionController extends BaseController implements Initializable{
	/**
	 * Registration Id
	 */
	private String regRejId = null;

	/**
	 * Stage
	 */
	private Stage rejPrimarystage;
			
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
	private RegistrationApprovalController rejRegistrationController;
	/**
	 * Object for RegistrationApprovalService
	 */
	@Autowired
	private RegistrationApprovalService rejRegistration;
	/**
	 * Combobox for for rejection reason
	 */
	@FXML
	private ComboBox<String> rejectionComboBox;
	/**
	 * Button for Submit
	 */
	@FXML
	private Button rejectionSubmit;
	
	ObservableList<String> rejectionCommentslist=FXCollections.observableArrayList("Correction not possible",
            "Wrong Person",
            "Invalid Data",
            "Incorrect indroducer",
            "Incorrect ID");
	
	/* (non-Javadoc)
	 * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.debug("REGISTRATION - PAGE_LOADING - REGISTRATION_REJECTION_CONTROLLER",
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID), "Page loading has been started");
		rejectionComboBox.getItems().clear();
		rejectionComboBox.setItems(rejectionCommentslist);
	}
	
	/**
	 * Method to get the Stage and Registration Id from the other controller page
	 * 
	 * @param id
	 * @param stage
	 */
	public void initData(String id,Stage stage) {
		regRejId=id;
		rejPrimarystage = stage;
	}
	/**
	 * {@code updatePacketStatus} is event class for updating packet status to
	 * reject
	 * 
	 * @param event
	 */
	public void packetUpdateStatus(ActionEvent event) {
		LOGGER.debug("REGISTRATION - UPDATE_PACKET_STATUS - REGISTRATION_REJECTION_CONTROLLER",
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID),
				"Packet updation as rejection has been started");
		
		if(rejRegistration.packetUpdateStatus(regRejId, RegClientStatusCode.REJECTED.getCode(),"mahesh123", 
				rejectionComboBox.getSelectionModel().getSelectedItem(), "mahesh123")) {
		generateAlert("Status", AlertType.INFORMATION, "Packet Rejected Successfully..");
		rejectionSubmit.disableProperty().set(true);
		rejRegistrationController.tablePagination();
		
		}
		else {
			generateAlert("Status",AlertType.INFORMATION,"");
		}
		rejPrimarystage.close();
		LOGGER.debug("REGISTRATION - UPDATE_PACKET_STATUS - REGISTRATION_REJECTION_CONTROLLER",
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID),
				"Packet updation as rejection has been started");
	}
}
