package io.mosip.registration.controller;

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
		LOGGER.debug("REGISTRATION_APPROVAL_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Page loading has been started");
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

		LOGGER.debug("REGISTRATION_APPROVAL_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Page loading has been completed");
	}

	/**
	 * Viewing RegistrationAcknowledgement on selecting the Registration record
	 */
	private void viewAck() {
		LOGGER.debug("REGISTRATION_APPROVAL_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Displaying the Acknowledgement form started");
		if (pendingActionTable.getSelectionModel().getSelectedItem() != null) {
			if (!approvalmapList.isEmpty()) {
				submitBtn.setVisible(true);
			}

			approvalBtn.setSelected(false);
			rejectionBtn.setSelected(false);

			approvalBtn.setVisible(false);
			rejectionBtn.setVisible(false);
			pendingActionImageAnchorPane.setVisible(false);

			for (Map<String, String> map : approvalmapList) {

				if (map.get("registrationID") == pendingActionTable.getSelectionModel().getSelectedItem().getId()) {
					if (map.get("statusCode") == RegistrationClientStatusCode.APPROVED.getCode()) {
						approvalBtn.setSelected(true);
						rejectionBtn.setSelected(false);
					} else if (map.get("statusCode") == RegistrationClientStatusCode.REJECTED.getCode()) {
						approvalBtn.setSelected(false);
						rejectionBtn.setSelected(true);
					}
				}
			}

			try (FileInputStream file = new FileInputStream(
					new File(pendingActionTable.getSelectionModel().getSelectedItem().getAcknowledgementFormPath()))) {

				pendingActionImageView.setImage(new Image(file));
			} catch (IOException ioException) {
				LOGGER.error("REGISTRATION_APPROVAL_CONTROLLER - REGSITRATION_ACKNOWLEDGEMNT_PAGE_LOADING_FAILED",
						APPLICATION_NAME, APPLICATION_ID, ioException.getMessage());
			}

		}
		LOGGER.debug("REGISTRATION_APPROVAL_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Displaying the Acknowledgement form completed");
	}

	/**
	 * Opening registration acknowledgement form on clicking on image.
	 */
	public void openAckForm() {
		LOGGER.debug("REGISTRATION_APPROVAL_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Opening the Acknowledgement Form");
		viewAckController.viewAck(pendingActionTable.getSelectionModel().getSelectedItem().getAcknowledgementFormPath(),
				stage);
	}

	/**
	 * {@code populateTable} method is used for populating registration data
	 * 
	 */
	public void populateTable() {
		LOGGER.debug("REGISTRATION_APPROVAL_CONTROLLER ", APPLICATION_NAME, APPLICATION_ID,
				"table population has been started");
		List<RegistrationApprovalDTO> listData = registrationApprovalService
				.getEnrollmentByStatus(RegistrationClientStatusCode.ON_HOLD.getCode());

		submitBtn.setVisible(false);
		approvalBtn.setVisible(false);
		rejectionBtn.setVisible(false);
		pendingActionImageAnchorPane.setVisible(false);

		if (!listData.isEmpty()) {
			ObservableList<RegistrationApprovalDTO> oList = FXCollections.observableArrayList(listData);
			pendingActionTable.setItems(oList);
		} else {
			pendingActionRegistrationRootSubPane.disableProperty().set(true);
			pendingActionTable.getItems().remove(pendingActionTable.getSelectionModel().getSelectedItem());
			pendingActionTable.setPlaceholder(new Label(RegistrationConstants.PLACEHOLDER_LABEL));
		}
		LOGGER.debug("REGISTRATION_APPROVAL_CONTROLLER ", APPLICATION_NAME, APPLICATION_ID,
				"table population has been ended");
	}

	/**
	 * Event method for Approving packet
	 * 
	 * @param event
	 */
	public void pendingActionApprovePacket() {
		LOGGER.debug("REGISTRATION - APPROVE_PACKET - REGISTRATION", APPLICATION_NAME, APPLICATION_ID,
				"Packet updation has been started");

		for (Map<String, String> registrationMap : approvalmapList) {
			if (registrationMap.containsValue(pendingActionTable.getSelectionModel().getSelectedItem().getId())) {
				approvalmapList.remove(registrationMap);
			}
		}
		Map<String, String> map = new HashMap<>();
		map.put("registrationID", pendingActionTable.getSelectionModel().getSelectedItem().getId());
		map.put("statusCode", RegistrationClientStatusCode.APPROVED.getCode());
		map.put("statusComment", "");
		approvalmapList.add(map);

		approvalBtn.setSelected(true);
		rejectionBtn.setSelected(false);
		submitBtn.setVisible(true);

		LOGGER.debug("REGISTRATION - APPROVE_PACKET - REGISTRATION_", APPLICATION_NAME, APPLICATION_ID,
				"Packet updation has been ended");
	}

	/**
	 * Event method for packet Rejection
	 * 
	 * @param event
	 * @throws RegBaseCheckedException
	 */
	public void pendingActionRejectPacket() throws RegBaseCheckedException {
		try {
			LOGGER.debug("REGISTRATION - REJECTION_PACKET - REGISTRATION", APPLICATION_NAME, APPLICATION_ID,
					"Rejection of packet has been started");

			Stage primarystage = new Stage();
			primarystage.initStyle(StageStyle.UNDECORATED);
			RejectionController rejectionController = (RejectionController) RegistrationAppInitialization
					.getApplicationContext().getBean(RegistrationConstants.REJECTION_BEAN_NAME);

			rejectionController.initData(pendingActionTable.getSelectionModel().getSelectedItem(), primarystage,
					approvalmapList);
			loadStage(primarystage, RegistrationConstants.REJECTION_PAGE,
					pendingActionTable.getSelectionModel().getSelectedItem());

			approvalBtn.setSelected(false);
			rejectionBtn.setSelected(true);
			submitBtn.setVisible(true);

		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(REG_UI_LOGIN_LOADER_EXCEPTION, runtimeException.getMessage());
		}
		LOGGER.debug("REGISTRATION - REJECTION_PACKET - REGISTRATION", APPLICATION_NAME, APPLICATION_ID,
				"Rejection of packet has been ended");

	}

	@Override
	public void getFingerPrintStatus() {
		for (Map<String, String> map : approvalmapList) {
			registrationApprovalService.updateRegistration(map.get("registrationID"), map.get("statusComment"),
					map.get("statusCode"));
		}
		reloadTableView();
	}

	public void pendingActionSubmit() throws RegBaseCheckedException {

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
	}

	private Stage loadStage(Stage primarystage, String fxmlPath, RegistrationApprovalDTO registrationApprovalDTO)
			throws RegBaseCheckedException {

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
