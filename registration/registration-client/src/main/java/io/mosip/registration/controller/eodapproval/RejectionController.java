package io.mosip.registration.controller.eodapproval;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_REJECT_CONTROLLER;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.vo.RegistrationApprovalVO;
import io.mosip.registration.dto.mastersync.ReasonListDto;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.sync.MasterSyncService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

	@Autowired
	private RegistrationApprovalController registrationApprovalController;
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

	/** The rejectionmap list. */
	private List<Map<String, String>> rejectionmapList;

	/** The rej reg data. */
	private RegistrationApprovalVO rejRegData;

	/** The rejection table. */
	private TableView<RegistrationApprovalVO> regRejectionTable;

	private ObservableList<RegistrationApprovalVO> observableList;
	private Map<String, Integer> packetIds = new HashMap<>();

	private String controllerName;

	@FXML
	private Button closeButton;

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
		List<ReasonListDto> reasonList;
		try {
			reasonList = masterSyncService.getAllReasonsList(applicationContext.getApplicationLanguage());
			closeButton.setGraphic(new ImageView(new Image(
					this.getClass().getResourceAsStream(RegistrationConstants.CLOSE_IMAGE_PATH), 20, 20, true, true)));
			rejectionComboBox.setItems(FXCollections
					.observableArrayList(reasonList.stream().map(list -> list.getName()).collect(Collectors.toList())));
			disableColumnsReorder(regRejectionTable);
		} catch (RegBaseCheckedException exRegBaseCheckedException) {
			LOGGER.error(LOG_REG_REJECT_CONTROLLER, APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					exRegBaseCheckedException.getMessage() + ExceptionUtils.getStackTrace(exRegBaseCheckedException));
		}
		LOGGER.info(LOG_REG_REJECT_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Page loading has been ended");
	}

	/**
	 * Method to get the Stage, Registration Data,Table Data and list map from the
	 * other controller page.
	 *
	 * @param regData
	 * @param packetIds
	 * @param stage
	 * @param mapList
	 * @param table
	 * @param filterField
	 */
	public void initData(RegistrationApprovalVO regData, Map<String, Integer> packets, Stage stage,
			List<Map<String, String>> mapList, ObservableList<RegistrationApprovalVO> oList,
			TableView<RegistrationApprovalVO> table, String controller) {
		rejRegData = regData;
		packetIds = packets;
		rejPrimarystage = stage;
		rejectionmapList = mapList;
		observableList = oList;
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

		Map<String, String> map = new WeakHashMap<>();
		map.put(RegistrationConstants.REGISTRATIONID, rejRegData.getId());
		map.put(RegistrationConstants.STATUSCODE, RegistrationClientStatusCode.REJECTED.getCode());
		map.put(RegistrationConstants.STATUSCOMMENT, rejectionComboBox.getSelectionModel().getSelectedItem());
		rejectionmapList.add(map);

		rejectionSubmit.disableProperty().set(true);
		rejPrimarystage.close();

		if (controllerName.equals(RegistrationConstants.EOD_PROCESS_REGISTRATIONAPPROVALCONTROLLER)) {

			int focusedIndex = regRejectionTable.getSelectionModel().getFocusedIndex();

			int rowNum = packetIds.get(regRejectionTable.getSelectionModel().getSelectedItem().getId());
			RegistrationApprovalVO approvalDTO = new RegistrationApprovalVO(
					regRejectionTable.getSelectionModel().getSelectedItem().getSlno(),
					regRejectionTable.getSelectionModel().getSelectedItem().getId(),
					regRejectionTable.getSelectionModel().getSelectedItem().getDate(),
					regRejectionTable.getSelectionModel().getSelectedItem().getAcknowledgementFormPath(),
					RegistrationUIConstants.REJECTED);

			observableList.set(rowNum, approvalDTO);
			registrationApprovalController.wrapListAndAddFiltering(observableList);
			regRejectionTable.requestFocus();
			regRejectionTable.getFocusModel().focus(focusedIndex);
			regRejectionTable.getSelectionModel().select(focusedIndex);
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