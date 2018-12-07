package io.mosip.registration.controller.eodapproval;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_ONHOLD_CONTROLLER;
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
 * {@code OnHoldController} is controller class for onHoldComment fxml page
 * which consists the methods for updating the status to onhold
 *
 * @author Mahesh Kumar
 */
@Controller
public class OnHoldController extends BaseController implements Initializable {

	/**
	 * Stage
	 */
	private Stage primarystage;

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(OnHoldController.class);

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

	/** Hyperlink for exit. */
	@FXML
	private Hyperlink exit;

	/** The on hold map list. */
	private List<Map<String, String>> onHoldMapList;

	/** The on hold reg data. */
	private RegistrationApprovalDTO onHoldRegData;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.debug(LOG_REG_ONHOLD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Page loading has been started");

		submit.disableProperty().set(true);
		onHoldComboBox.getItems().clear();
		onHoldComboBox.setItems(FXCollections.observableArrayList(String.valueOf(applicationContext.getApplicationMap().get(RegistrationConstants.ONHOLD_COMMENTS)).split(",")));
		LOGGER.debug(LOG_REG_ONHOLD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Page loading has been ended");
	}

	/**
	 * Method to get the Stage and Registration Id from the other controller page
	 * 
	 * @param id
	 * @param stage
	 */
	public void initData(RegistrationApprovalDTO regData, Stage stage, List<Map<String, String>> mapList) {
		onHoldRegData = regData;
		primarystage = stage;
		onHoldMapList = mapList;
	}

	/**
	 * {@code updatePacketStatus} is for updating packet status to
	 * onhold
	 * 
	 * @param event
	 */
	public void updatePacketStatus() {
		LOGGER.debug(LOG_REG_ONHOLD_CONTROLLER, APPLICATION_NAME,
				APPLICATION_ID, "Packet updation to on hold has been started");

		for (Map<String, String> registrationMap : onHoldMapList) {
			if (registrationMap.containsValue(onHoldRegData.getId())) {
				onHoldMapList.remove(registrationMap);
				break;
			}
		}

		Map<String, String> map = new HashMap<>();
		map.put(RegistrationConstants.REGISTRATIONID, onHoldRegData.getId());
		map.put(RegistrationConstants.STATUSCODE, RegistrationClientStatusCode.ON_HOLD.getCode());
		map.put(RegistrationConstants.STATUSCOMMENT, onHoldComboBox.getSelectionModel().getSelectedItem());
		onHoldMapList.add(map);

		submit.disableProperty().set(true);
		primarystage.close();
		LOGGER.debug(LOG_REG_ONHOLD_CONTROLLER, APPLICATION_NAME,
				APPLICATION_ID, "Packet updation to on hold has been ended");
	}

	/**
	 * {@code rejectionWindowExit} is to exit from reason for rejection pop up window.
	 * 
	 * @param event
	 */
	public void exitWindow() {
		primarystage.close();

	}

	/**
	 * Rejection combobox action.
	 * 
	 * @param event
	 */
	public void onHoldComboboxAction() {
		submit.disableProperty().set(false);
	}
}