package io.mosip.registration.controller.eodapproval;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_REJECT_CONTROLLER;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.dto.RegistrationApprovalDTO;
import io.mosip.registration.dto.mastersync.MasterReasonListDto;
import io.mosip.registration.service.MasterSyncService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableView;
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

	@Autowired
	private MasterSyncService masterSyncService;
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

	/** The rejection table. */
	private TableView<RegistrationApprovalDTO> regRejectionTable;

	private String controllerName;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.info(LOG_REG_REJECT_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Page loading has been started");
		rejectionSubmit.disableProperty().set(true);
		rejectionComboBox.getItems().clear();

		List<MasterReasonListDto> reasonList = masterSyncService.getAllReasonsList(ApplicationContext.getInstance().getApplicationLanguage());
		
		rejectionComboBox.setItems(FXCollections
				.observableArrayList(reasonList.stream().map(list -> list.getName()).collect(Collectors.toList())));

		LOGGER.info(LOG_REG_REJECT_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Page loading has been ended");
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
	public void initData(RegistrationApprovalDTO regData, Stage stage, List<Map<String, String>> mapList,
			TableView<RegistrationApprovalDTO> table, String controller) {
		rejRegData = regData;
		rejPrimarystage = stage;
		rejectionmapList = mapList;
		regRejectionTable = table;
		controllerName = controller;
	}

	/**
	 * {@code updatePacketStatus} is for updating packet status to reject
	 * 
	 * 
	 * @param event
	 */
	public void packetUpdateStatus() {
		LOGGER.info(LOG_REG_REJECT_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
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

		if (controllerName.equals("RegistrationApprovalController")) {

			int rowNum=(regRejectionTable.getSelectionModel().getFocusedIndex());
			RegistrationApprovalDTO approvalDTO = new RegistrationApprovalDTO(
					regRejectionTable.getItems().get(regRejectionTable.getSelectionModel().getFocusedIndex()).getId(),
					regRejectionTable.getItems().get(regRejectionTable.getSelectionModel().getFocusedIndex()).getAcknowledgementFormPath(),
					RegistrationConstants.REJECTED);

			regRejectionTable.getItems().set(rowNum, approvalDTO);
			regRejectionTable.requestFocus();
			regRejectionTable.getFocusModel().focus(rowNum);
			LOGGER.info(LOG_REG_REJECT_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Packet updation as rejection has been ended");
		}
	}

	/**
	 * {@code rejectionWindowExit} is event class to exit from reason for rejection
	 * pop up window.
	 * 
	 * @param event
	 */
	public void rejectionWindowExit() {
		LOGGER.info(LOG_REG_REJECT_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Rejection Popup window is closed");
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
