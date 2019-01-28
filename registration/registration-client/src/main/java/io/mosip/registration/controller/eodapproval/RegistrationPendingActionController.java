package io.mosip.registration.controller.eodapproval;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_PENDING_ACTION;
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
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.controller.BaseController;
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

	/** object for viewing acknowledgement. */
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
	 * status comment column in the table
	 */
	@FXML
	private TableColumn<RegistrationApprovalDTO, String> statusComment;

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
	 * Button for authentication
	 */
	@FXML
	private ToggleButton authenticateBtn;
	/** The image view. */
	@FXML
	private ImageView pendingActionImageView;
	/**
	 * The approve registration root sub pane.
	 */
	@FXML
	private AnchorPane pendingActionRegistrationRootSubPane;
	/**
	 * The image anchor pane.
	 */
	@FXML
	private AnchorPane pendingActionImageAnchorPane;

	/** object for rejection controller. */
	@Autowired
	private RejectionController rejectionController;

	private Stage primaryStage;

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
	private void reloadTableView() {
		LOGGER.debug(LOG_REG_PENDING_ACTION, APPLICATION_NAME, APPLICATION_ID, "Page loading has been started");
		approvalmapList = new ArrayList<>(5);

		authenticateBtn.setVisible(false);
		approvalBtn.setVisible(false);
		rejectionBtn.setVisible(false);
		pendingActionImageAnchorPane.setVisible(false);

		id.setCellValueFactory(new PropertyValueFactory<RegistrationApprovalDTO, String>("id"));
		statusComment.setCellValueFactory(new PropertyValueFactory<RegistrationApprovalDTO, String>("statusComment"));
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
				authenticateBtn.setVisible(true);
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
				fXComponents.getStage());
	}

	/**
	 * {@code populateTable} method is used for populating registration data
	 * 
	 */
	private void populateTable() {
		LOGGER.debug(LOG_REG_PENDING_ACTION, APPLICATION_NAME, APPLICATION_ID, "table population has been started");
		List<RegistrationApprovalDTO> listData = registrationApprovalService
				.getEnrollmentByStatus(RegistrationClientStatusCode.ON_HOLD.getCode());

		authenticateBtn.setVisible(false);
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
	 * {@code updateStatus} is to update the status of registration.
	 *
	 * @param event
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	public void updateStatus(ActionEvent event) throws RegBaseCheckedException {

		LOGGER.debug(LOG_REG_PENDING_ACTION, APPLICATION_NAME, APPLICATION_ID,
				"Registration status updation has been started");

		ToggleButton btn = (ToggleButton) event.getSource();

		if (btn.getId().equals(approvalBtn.getId())) {

			for (Map<String, String> registrationMap : approvalmapList) {

				if (registrationMap.containsValue(pendingActionTable.getSelectionModel().getSelectedItem().getId())) {

					approvalmapList.remove(registrationMap);

					break;
				}
			}

			Map<String, String> map = new HashMap<>();
			map.put(RegistrationConstants.REGISTRATIONID,
					pendingActionTable.getSelectionModel().getSelectedItem().getId());
			map.put(RegistrationConstants.STATUSCODE, RegistrationClientStatusCode.APPROVED.getCode());
			map.put(RegistrationConstants.STATUSCOMMENT, RegistrationConstants.EMPTY);
			approvalmapList.add(map);

			approvalBtn.setSelected(true);
			rejectionBtn.setSelected(false);
			authenticateBtn.setVisible(true);

		} else {

			try {

				Stage primarystage = new Stage();

				if (btn.getId().equals(rejectionBtn.getId())) {

					rejectionController.initData(pendingActionTable.getSelectionModel().getSelectedItem(), primarystage,
							approvalmapList, pendingActionTable, "RegistrationPendingActionController");

					loadStage(primarystage, RegistrationConstants.REJECTION_PAGE);

					rejectionBtn.setSelected(true);
					approvalBtn.setSelected(false);
					authenticateBtn.setVisible(true);

				} else if (btn.getId().equals(authenticateBtn.getId())) {

					loadStage(primarystage, RegistrationConstants.USER_AUTHENTICATION);

					authenticateBtn.setSelected(false);
				}

			} catch (RuntimeException runtimeException) {

				throw new RegBaseUncheckedException(REG_UI_LOGIN_LOADER_EXCEPTION, runtimeException.getMessage());
			}
		}
		LOGGER.debug(LOG_REG_PENDING_ACTION, APPLICATION_NAME, APPLICATION_ID,
				"Registration status updation has been ended");
	}

	/**
	 * Loading stage.
	 *
	 * @param primarystage
	 *            the stage
	 * @param fxmlPath
	 *            the fxml path
	 * @return the stage
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
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
	public void updateAuthenticationStatus() {
		LOGGER.debug(LOG_REG_PENDING_ACTION, APPLICATION_NAME, APPLICATION_ID,
				"Updation of registration according to status has been started");

		for (Map<String, String> map : approvalmapList) {
			registrationApprovalService.updateRegistration(map.get(RegistrationConstants.REGISTRATIONID),
					map.get(RegistrationConstants.STATUSCOMMENT), map.get(RegistrationConstants.STATUSCODE));
		}
		generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationUIConstants.AUTH_PENDING_ACTION_SUCCESS_MSG);
		primaryStage.close();
		reloadTableView();
		LOGGER.debug(LOG_REG_PENDING_ACTION, APPLICATION_NAME, APPLICATION_ID,
				"Updation of registration according to status has been ended");

	}

}
