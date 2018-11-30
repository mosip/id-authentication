package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_REJECT_CONTROLLER;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.dto.RegistrationApprovalDTO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;

/**
 *
 * {@code RejectionController} is the controller class for rejection of packets
 * 
 * @author Mahesh Kumar
 */
@Controller
public class RejectionController extends BaseController implements Initializable {
	/**
	 * Stage
	 */
	private Stage rejPrimarystage;

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(RejectionController.class);

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

	/**
	 * HyperLink for Exit
	 */
	@FXML
	private Hyperlink rejectionExit;

	/** The rejectionmap list. */
	private List<Map<String, String>> rejectionmapList;

	/** The rej reg data. */
	private RegistrationApprovalDTO rejRegData;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.debug(LOG_REG_REJECT_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Page loading has been started");
		rejectionSubmit.disableProperty().set(true);
		rejectionComboBox.getItems().clear();
		rejectionComboBox.setItems(FXCollections.observableArrayList(String.valueOf(ApplicationContext.getInstance().getApplicationMap().get(RegistrationConstants.REJECTION_COMMENTS)).split(",")));
		LOGGER.debug(LOG_REG_REJECT_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Page loading has been ended");
	}

	/**
	 * Method to get the Stage, Registration Data,Table Data and list map from the
	 * other controller page.
	 *
	 * @param regData
	 * @param stage
	 * @param mapList
	 * @param table
	 */
	public void initData(RegistrationApprovalDTO regData, Stage stage, List<Map<String, String>> mapList) {
		rejRegData = regData;
		rejPrimarystage = stage;
		rejectionmapList = mapList;
	}

	/**
	 * {@code updatePacketStatus} is for updating packet status to
	 * reject
	 * 
	 * @param event
	 */
	public void packetUpdateStatus() {
		LOGGER.debug(LOG_REG_REJECT_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
				"Packet updation as rejection has been started");

		for (Map<String, String> registrationMap : rejectionmapList) {
			if (registrationMap.containsValue(rejRegData.getId())) {
				rejectionmapList.remove(registrationMap);
				break;
			}
		}

		Map<String, String> map = new HashMap<>();
		map.put("registrationID", rejRegData.getId());
		map.put("statusCode", RegistrationClientStatusCode.REJECTED.getCode());
		map.put("statusComment", rejectionComboBox.getSelectionModel().getSelectedItem());
		rejectionmapList.add(map);

		rejectionSubmit.disableProperty().set(true);
		rejPrimarystage.close();
		LOGGER.debug(LOG_REG_REJECT_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
				"Packet updation as rejection has been ended");
	}

	/**
	 * {@code rejectionWindowExit} is event class to exit from reason for rejection
	 * pop up window.
	 * 
	 * @param event
	 */
	public void rejectionWindowExit() {
		LOGGER.debug(LOG_REG_REJECT_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Rejection Popup window is closed");
		rejPrimarystage.close();
	}

	/**
	 * Rejection combobox action.
	 * 
	 * @param event
	 */
	public void rejectionComboboxAction() {
		rejectionSubmit.disableProperty().set(false);
	}
}
