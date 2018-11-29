package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.REG_UI_LOGIN_LOADER_EXCEPTION;
import static io.mosip.registration.constants.LoggerConstants.LOG_REG_PENDING_ACTION;

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
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationExceptions;
import io.mosip.registration.dto.RegistrationApprovalDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.RegistrationApprovalService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

@Controller
public class RegistrationPendingActionController extends BaseController implements Initializable {

	/**
	 * Instance of {@link Logger}
	 */
	private Logger LOGGER = AppConfig.getLogger(RegistrationApprovalController.class);

	/**
	 * object for Registration approval service class
	 */
	@Autowired
	private RegistrationApprovalService registrationApprovalService;

	@Autowired
	private ViewAckController viewAckController;

	/**
	 * Table to display the created packets
	 */
	@FXML
	private TableView<RegistrationApprovalDTO> pendingActionTable;
	/**
	 * Registration Id column in the table
	 */
	@FXML
	private TableColumn<RegistrationApprovalDTO, String> id;
	/**
	 * Acknowledgement form column in the table
	 */
	@FXML
	private TableColumn<RegistrationApprovalDTO, String> acknowledgementFormPath;
	/**
	 * Button for approval
	 */
	@FXML
	private ToggleButton approvalBtn;
	/**
	 * Button for rejection
	 */
	@FXML
	private ToggleButton rejectionBtn;
	/**
	 * Button for on hold
	 */
	@FXML
	private Button submitBtn;
	/** The image view. */
	@FXML
	private ImageView pendingActionImageView;

	/** The approve registration root sub pane. */
	@FXML
	private AnchorPane pendingActionRegistrationRootSubPane;

	/** The image anchor pane. */
	@FXML
	private AnchorPane pendingActionImageAnchorPane;

	/** The approvalmap list. */
	private List<Map<String, String>> approvalmapList = null;

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
	 * Method to reload table
	 */
	public void reloadTableView() {
		LOGGER.debug(LOG_REG_PENDING_ACTION, APPLICATION_NAME, APPLICATION_ID, "Page loading has been started");
		approvalmapList = new ArrayList<>(5);

		submitBtn.setVisible(false);
		approvalBtn.setVisible(false);
		rejectionBtn.setVisible(false);
		pendingActionImageAnchorPane.setVisible(false);

		id.setCellValueFactory(new PropertyValueFactory<RegistrationApprovalDTO, String>("id"));
		acknowledgementFormPath.setCellValueFactory(
				new PropertyValueFactory<RegistrationApprovalDTO, String>("acknowledgementFormPath"));

		populateTable();
		pendingActionTable.setOnMouseClicked((MouseEvent event) -> {
			if (event.getClickCount() == 1) {
				viewAck();
			}
		});

		LOGGER.debug(LOG_REG_PENDING_ACTION, APPLICATION_NAME, APPLICATION_ID, "Page loading has been completed");
	}

	/**
	 * Viewing RegistrationAcknowledgement on selecting the Registration record
	 */
	private void viewAck() {
		LOGGER.debug(LOG_REG_PENDING_ACTION, APPLICATION_NAME, APPLICATION_ID,
				"Displaying the Acknowledgement form started");
		if (pendingActionTable.getSelectionModel().getSelectedItem() != null) {
			if (!approvalmapList.isEmpty()) {
				submitBtn.setVisible(true);
			}

			approvalBtn.setSelected(false);
			rejectionBtn.setSelected(false);

			approvalBtn.setVisible(true);
			rejectionBtn.setVisible(true);
			pendingActionImageAnchorPane.setVisible(true);

			for (Map<String, String> map : approvalmapList) {

				if (map.get(RegistrationConstants.REGISTRATIONID) == pendingActionTable.getSelectionModel()
						.getSelectedItem().getId()) {
					if (map.get(RegistrationConstants.STATUSCODE) == RegistrationClientStatusCode.APPROVED.getCode()) {
						approvalBtn.setSelected(true);
					} else if (map.get(RegistrationConstants.STATUSCODE) == RegistrationClientStatusCode.REJECTED
							.getCode()) {
						rejectionBtn.setSelected(true);
					}
				}
			}

			try (FileInputStream file = new FileInputStream(
					new File(pendingActionTable.getSelectionModel().getSelectedItem().getAcknowledgementFormPath()))) {

				pendingActionImageView.setImage(new Image(file));
			} catch (IOException ioException) {
				LOGGER.error(LOG_REG_PENDING_ACTION, APPLICATION_NAME, APPLICATION_ID, ioException.getMessage());
			}

		}
		LOGGER.debug(LOG_REG_PENDING_ACTION, APPLICATION_NAME, APPLICATION_ID,
				"Displaying the Acknowledgement form completed");
	}

	/**
	 * Opening registration acknowledgement form on clicking on image.
	 */
	public void openAckForm() {
		LOGGER.debug(LOG_REG_PENDING_ACTION, APPLICATION_NAME, APPLICATION_ID,
				"Opening the Acknowledgement Form started");
		viewAckController.viewAck(pendingActionTable.getSelectionModel().getSelectedItem().getAcknowledgementFormPath(),
				stage);
	}

	/**
	 * {@code populateTable} method is used for populating registration data
	 * 
	 */
	public void populateTable() {
		LOGGER.debug(LOG_REG_PENDING_ACTION, APPLICATION_NAME, APPLICATION_ID, "table population has been started");
		List<RegistrationApprovalDTO> listData = registrationApprovalService
				.getEnrollmentByStatus(RegistrationClientStatusCode.ON_HOLD.getCode());

		submitBtn.setVisible(false);
		approvalBtn.setVisible(false);
		rejectionBtn.setVisible(false);
		pendingActionImageAnchorPane.setVisible(false);

		if (!listData.isEmpty()) {
			pendingActionRegistrationRootSubPane.disableProperty().set(false);
			ObservableList<RegistrationApprovalDTO> oList = FXCollections.observableArrayList(listData);
			pendingActionTable.setItems(oList);
		} else {
			pendingActionRegistrationRootSubPane.disableProperty().set(true);
			pendingActionTable.getItems().clear();
			pendingActionTable.setPlaceholder(new Label(RegistrationConstants.PLACEHOLDER_LABEL));
		}
		LOGGER.debug(LOG_REG_PENDING_ACTION, APPLICATION_NAME, APPLICATION_ID, "table population has been ended");
	}

	/**
	 * Event method for Approving packet
	 * 
	 * @param event
	 */
	public void pendingActionApprovePacket() {
		LOGGER.debug(LOG_REG_PENDING_ACTION, APPLICATION_NAME, APPLICATION_ID,
				"Registration Approval has been started");

		for (Map<String, String> registrationMap : approvalmapList) {
			if (registrationMap.containsValue(pendingActionTable.getSelectionModel().getSelectedItem().getId())) {
				approvalmapList.remove(registrationMap);
				break;
			}
		}
		Map<String, String> map = new HashMap<>();
		map.put(RegistrationConstants.REGISTRATIONID, pendingActionTable.getSelectionModel().getSelectedItem().getId());
		map.put(RegistrationConstants.STATUSCODE, RegistrationClientStatusCode.APPROVED.getCode());
		map.put(RegistrationConstants.STATUSCOMMENT, "");
		approvalmapList.add(map);

		approvalBtn.setSelected(true);
		rejectionBtn.setSelected(false);
		submitBtn.setVisible(true);

		LOGGER.debug(LOG_REG_PENDING_ACTION, APPLICATION_NAME, APPLICATION_ID, "Registration Approval has been ended");
	}

	/**
	 * Event method for packet Rejection
	 * 
	 * @param event
	 * @throws RegBaseCheckedException
	 */
	public void pendingActionRejectPacket() throws RegBaseCheckedException {
		try {
			LOGGER.debug(LOG_REG_PENDING_ACTION, APPLICATION_NAME, APPLICATION_ID,
					"Rejection of registration has been started");

			Stage primarystage = new Stage();
			primarystage.initStyle(StageStyle.UNDECORATED);
			RejectionController rejectionController = (RejectionController) RegistrationAppInitialization
					.getApplicationContext().getBean(RegistrationConstants.REJECTION_BEAN_NAME);

			rejectionController.initData(pendingActionTable.getSelectionModel().getSelectedItem(), primarystage,
					approvalmapList);
			loadStage(primarystage, RegistrationConstants.REJECTION_PAGE);

			approvalBtn.setSelected(false);
			rejectionBtn.setSelected(true);
			submitBtn.setVisible(true);

		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(REG_UI_LOGIN_LOADER_EXCEPTION, runtimeException.getMessage());
		}
		LOGGER.debug(LOG_REG_PENDING_ACTION, APPLICATION_NAME, APPLICATION_ID,
				"Rejection of registration has been ended");

	}

	
	/* (non-Javadoc)
	 * @see io.mosip.registration.controller.BaseController#getFingerPrintStatus(javafx.stage.Stage)
	 */
	@Override
	public void getFingerPrintStatus(Stage primaryStage) {
		LOGGER.debug(LOG_REG_PENDING_ACTION, APPLICATION_NAME, APPLICATION_ID,
				"Updation of registration according to status has been started");

		for (Map<String, String> map : approvalmapList) {
			registrationApprovalService.updateRegistration(map.get(RegistrationConstants.REGISTRATIONID),
					map.get(RegistrationConstants.STATUSCOMMENT), map.get(RegistrationConstants.STATUSCODE));
		}
		generateAlert(RegistrationConstants.AUTH_INFO, AlertType.INFORMATION,generateErrorMessage(RegistrationConstants.AUTH_PENDING_ACTION_SUCCESS_MSG));
		primaryStage.close();
		reloadTableView();
		LOGGER.debug(LOG_REG_PENDING_ACTION, APPLICATION_NAME, APPLICATION_ID,
				"Updation of registration according to status has been ended");
	}

	public void pendingActionSubmit() throws RegBaseCheckedException {
		LOGGER.debug(LOG_REG_PENDING_ACTION, APPLICATION_NAME, APPLICATION_ID,
				"Supervisor Authentication has been started");
		Parent ackRoot;
		try {
			Stage primaryStage = new Stage();
			primaryStage.initStyle(StageStyle.UNDECORATED);
			FXMLLoader fxmlLoader = BaseController
					.loadChild(getClass().getResource(RegistrationConstants.USER_AUTHENTICATION));
			ackRoot = fxmlLoader.load();
			primaryStage.setResizable(false);
			Scene scene = new Scene(ackRoot);
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			scene.getStylesheets().add(loader.getResource(RegistrationConstants.CSS_FILE_PATH).toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.initModality(Modality.WINDOW_MODAL);
			primaryStage.initOwner(stage);
			primaryStage.show();
			FingerPrintAuthenticationController fpcontroller = fxmlLoader.getController();
			fpcontroller.init(this);

		} catch (IOException ioException) {
			throw new RegBaseCheckedException(RegistrationExceptions.REG_UI_LOGIN_IO_EXCEPTION.getErrorCode(),
					RegistrationExceptions.REG_UI_LOGIN_IO_EXCEPTION.getErrorMessage(), ioException);
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(REG_UI_LOGIN_LOADER_EXCEPTION, runtimeException.getMessage());
		}
		LOGGER.debug(LOG_REG_PENDING_ACTION, APPLICATION_NAME, APPLICATION_ID,
				"Supervisor Authentication has been ended");
	}

	private Stage loadStage(Stage primarystage, String fxmlPath) throws RegBaseCheckedException {

		try {
			AnchorPane authRoot = BaseController.load(getClass().getResource(fxmlPath));
			Scene scene = new Scene(authRoot);
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			scene.getStylesheets().add(loader.getResource(RegistrationConstants.CSS_FILE_PATH).toExternalForm());
			primarystage.setScene(scene);
			primarystage.initModality(Modality.WINDOW_MODAL);
			primarystage.initOwner(stage);
			primarystage.show();
			primarystage.resizableProperty().set(false);

		} catch (IOException ioException) {
			throw new RegBaseCheckedException(RegistrationExceptions.REG_UI_LOGIN_IO_EXCEPTION.getErrorCode(),
					RegistrationExceptions.REG_UI_LOGIN_IO_EXCEPTION.getErrorMessage(), ioException);
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(REG_UI_LOGIN_LOADER_EXCEPTION, runtimeException.getMessage());
		}
		return primarystage;
	}
}
