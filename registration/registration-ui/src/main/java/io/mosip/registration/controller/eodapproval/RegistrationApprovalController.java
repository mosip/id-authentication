package io.mosip.registration.controller.eodapproval;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_PENDING_APPROVAL;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.REG_UI_LOGIN_LOADER_EXCEPTION;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.ProcessNames;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.auth.AuthenticationController;
import io.mosip.registration.controller.reg.ViewAckController;
import io.mosip.registration.dto.RegistrationApprovalDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.service.packet.RegistrationApprovalService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * {@code RegistrationApprovalController} is the controller class for
 * Registration approval.
 *
 * @author Mahesh Kumar
 */
@Controller
public class RegistrationApprovalController extends BaseController implements Initializable {

	/** Instance of {@link Logger}. */

	private Logger LOGGER = AppConfig.getLogger(RegistrationApprovalController.class);

	/** object for Registration approval service class. */

	@Autowired
	private RegistrationApprovalService registration;
	
	@Autowired
	private EODController eodController;

	/** The view ack controller. */
	@Autowired
	private ViewAckController viewAckController;

	/** Table to display the created packets. */

	@FXML
	private TableView<RegistrationApprovalDTO> table;

	/** Registration Id column in the table. */

	@FXML
	private TableColumn<RegistrationApprovalDTO, String> id;

	/** status comment column in the table. */
	@FXML
	private TableColumn<RegistrationApprovalDTO, String> statusComment;

	/** Acknowledgement form column in the table. */

	@FXML
	private TableColumn<RegistrationApprovalDTO, String> acknowledgementFormPath;

	/** Button for approval. */

	@FXML
	private ToggleButton approvalBtn;

	/** Button for rejection. */

	@FXML
	private ToggleButton rejectionBtn;

	/** Button for authentication. */

	@FXML
	private ToggleButton authenticateBtn;
	/** The image view. */
	@FXML
	private ImageView imageView;

	/** The approve registration root sub pane. */
	@FXML
	private AnchorPane approveRegistrationRootSubPane;

	/** The image anchor pane. */
	@FXML
	private AnchorPane imageAnchorPane;

	/** The map list. */
	private List<Map<String, String>> approvalmapList = null;

	/** object for registration approval service. */
	@Autowired
	private RegistrationApprovalService registrationApprovalService;

	/** object for rejection controller. */
	@Autowired
	private RejectionController rejectionController;

	/** object for finger print authentication controller. */
	@Autowired
	private AuthenticationController authenticationController;
	
	private Stage primaryStage;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		reloadTableView();
	}

	/**
	 * Method to reload table.
	 */
	public void reloadTableView() {
		LOGGER.debug(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID, "Page loading has been started");

		approvalmapList = new ArrayList<>(5);
		authenticateBtn.setDisable(true);
		approvalBtn.setVisible(false);
		rejectionBtn.setVisible(false);
		imageAnchorPane.setVisible(false);

		id.setCellValueFactory(new PropertyValueFactory<RegistrationApprovalDTO, String>("id"));
		statusComment.setCellValueFactory(new PropertyValueFactory<RegistrationApprovalDTO, String>("statusComment"));
		acknowledgementFormPath.setCellValueFactory(
				new PropertyValueFactory<RegistrationApprovalDTO, String>("acknowledgementFormPath"));

		populateTable();
		table.setOnMouseClicked((MouseEvent event) -> {
			if (event.getClickCount() == 1) {
				viewAck();
			}
		});

		LOGGER.debug(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID, "Page loading has been completed");
	}

	/**
	 * Viewing RegistrationAcknowledgement on selecting the Registration record.
	 */
	private void viewAck() {
		LOGGER.debug(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
				"Displaying the Acknowledgement form started");
		if (table.getSelectionModel().getSelectedItem() != null) {

			if (!approvalmapList.isEmpty()) {
				authenticateBtn.setDisable(false);
			}

			imageView.setImage(null);

			approvalBtn.setSelected(false);
			rejectionBtn.setSelected(false);

			approvalBtn.setVisible(true);
			rejectionBtn.setVisible(true);
			imageAnchorPane.setVisible(true);

			for (Map<String, String> map : approvalmapList) {

				if (map.get(RegistrationConstants.REGISTRATIONID) == table.getSelectionModel().getSelectedItem()
						.getId()) {
					if (map.get(RegistrationConstants.STATUSCODE) == RegistrationClientStatusCode.APPROVED.getCode()) {
						approvalBtn.setSelected(true);
					} else if (map.get(RegistrationConstants.STATUSCODE) == RegistrationClientStatusCode.REJECTED
							.getCode()) {
						rejectionBtn.setSelected(true);
					}
				}
			}

			try (FileInputStream file = new FileInputStream(
					new File(table.getSelectionModel().getSelectedItem().getAcknowledgementFormPath()))) {
				imageView.setImage(new Image(file));
			} catch (IOException ioException) {
				LOGGER.error("REGISTRATION_APPROVAL_CONTROLLER - REGSITRATION_ACKNOWLEDGEMNT_PAGE_LOADING_FAILED",
						APPLICATION_NAME, APPLICATION_ID, ioException.getMessage());
			}

		}
		LOGGER.debug(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
				"Displaying the Acknowledgement form completed");
	}

	/**
	 * Opening registration acknowledgement form on clicking on image.
	 */
	public void openAckForm() {
		LOGGER.debug(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID, "Opening the Acknowledgement Form");
		viewAckController.viewAck(table.getSelectionModel().getSelectedItem().getAcknowledgementFormPath(),
				fXComponents.getStage());

	}

	/**
	 * {@code populateTable} method is used for populating registration data.
	 * 
	 */
	public void populateTable() {
		LOGGER.debug(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID, "table population has been started");
		List<RegistrationApprovalDTO> listData = null;

		listData = registration.getEnrollmentByStatus(RegistrationClientStatusCode.CREATED.getCode());
		
		
		if (!listData.isEmpty()) {
			eodController.getPendingApprovalTitledPane().setText("Pending Approval ( "+listData.size()+" )");
			ObservableList<RegistrationApprovalDTO> oList = FXCollections.observableArrayList(listData);
			table.setItems(oList);
		} else {
			approveRegistrationRootSubPane.disableProperty().set(true);
			table.setPlaceholder(new Label(RegistrationConstants.PLACEHOLDER_LABEL));
			table.getItems().clear();
		}

		LOGGER.debug(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID, "table population has been ended");
	}

	/**
	 * {@code updateStatus} is to update the status of registration.
	 *
	 * @param event the event
	 * @throws RegBaseCheckedException the reg base checked exception
	 */
	public void updateStatus(ActionEvent event) throws RegBaseCheckedException {

		LOGGER.debug(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
				"Registration status updation has been started");

		ToggleButton tBtn = (ToggleButton) event.getSource();

		if (tBtn.getId().equals(approvalBtn.getId())) {

			for (Map<String, String> registrationMap : approvalmapList) {

				if (registrationMap.containsValue(table.getSelectionModel().getSelectedItem().getId())) {

					approvalmapList.remove(registrationMap);

					break;
				}
			}

			Map<String, String> map = new HashMap<>();
			map.put(RegistrationConstants.REGISTRATIONID, table.getSelectionModel().getSelectedItem().getId());
			map.put(RegistrationConstants.STATUSCODE, RegistrationClientStatusCode.APPROVED.getCode());
			map.put(RegistrationConstants.STATUSCOMMENT, RegistrationConstants.EMPTY);
			approvalmapList.add(map);

			authenticateBtn.setDisable(false);
			approvalBtn.setSelected(true);
			rejectionBtn.setSelected(false);

			RegistrationApprovalDTO approvalDTO = new RegistrationApprovalDTO(
					table.getSelectionModel().getSelectedItem().getId(),
					table.getSelectionModel().getSelectedItem().getAcknowledgementFormPath(),RegistrationConstants.APPROVED);
			table.getItems().set(table.getSelectionModel().getSelectedIndex(), approvalDTO);

		} else {

			try {

				Stage primarystage = new Stage();

				if (tBtn.getId().equals(rejectionBtn.getId())) {

					rejectionController.initData(table.getSelectionModel().getSelectedItem(), primarystage,
							approvalmapList, table, "RegistrationApprovalController");

					loadStage(primarystage, RegistrationConstants.REJECTION_PAGE);

					rejectionBtn.setSelected(true);
					approvalBtn.setSelected(false);
					authenticateBtn.setDisable(false);

				} else if (tBtn.getId().equals(authenticateBtn.getId())) {

					loadStage(primarystage, RegistrationConstants.USER_AUTHENTICATION);

					authenticationController.init(this,ProcessNames.EOD.getType());
					
					authenticateBtn.setSelected(false);

				}

			} catch (RuntimeException runtimeException) {

				throw new RegBaseUncheckedException(REG_UI_LOGIN_LOADER_EXCEPTION, runtimeException.getMessage());
			}
		}
		LOGGER.debug(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
				"Registration status updation has been ended");
	}

	/**
	 * Loading stage.
	 *
	 * @param primarystage the stage
	 * @param fxmlPath     the fxml path
	 * @return the stage
	 * @throws RegBaseCheckedException the reg base checked exception
	 */
	private Stage loadStage(Stage primarystage, String fxmlPath) throws RegBaseCheckedException {

		try {

			AnchorPane authRoot = BaseController.load(getClass().getResource(fxmlPath));
			Scene scene = new Scene(authRoot);
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			scene.getStylesheets().add(loader.getResource(RegistrationConstants.CSS_FILE_PATH).toExternalForm());
			primarystage.initStyle(StageStyle.UNDECORATED);
			primarystage.setScene(scene);
			primarystage.initModality(Modality.WINDOW_MODAL);
			primarystage.initOwner(fXComponents.getStage());
			primarystage.show();
			primarystage.resizableProperty().set(false);
			this.primaryStage = primarystage;

		} catch (IOException ioException) {
			throw new RegBaseCheckedException(RegistrationExceptionConstants.REG_UI_LOGIN_IO_EXCEPTION.getErrorCode(),
					RegistrationExceptionConstants.REG_UI_LOGIN_IO_EXCEPTION.getErrorMessage(), ioException);

		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(REG_UI_LOGIN_LOADER_EXCEPTION, runtimeException.getMessage());
		}
		return primarystage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.controller.BaseController#getFingerPrintStatus()
	 */
	@Override
	public void getFingerPrintStatus() {
		LOGGER.debug(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
				"Updation of registration according to status started");

		for (Map<String, String> map : approvalmapList) {
			registrationApprovalService.updateRegistration(map.get(RegistrationConstants.REGISTRATIONID),
					map.get(RegistrationConstants.STATUSCOMMENT), map.get(RegistrationConstants.STATUSCODE));
		}
		generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationUIConstants.AUTH_APPROVAL_SUCCESS_MSG);
		primaryStage.close();
		reloadTableView();
		LOGGER.debug(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
				"Updation of registration according to status ended");
	}
}
