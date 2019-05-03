package io.mosip.registration.controller.eodapproval;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_PENDING_APPROVAL;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.REG_UI_LOGIN_LOADER_EXCEPTION;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.WeakHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.ProcessNames;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.auth.AuthenticationController;
import io.mosip.registration.dto.RegistrationApprovalDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.service.packet.PacketUploadService;
import io.mosip.registration.service.packet.RegistrationApprovalService;
import io.mosip.registration.service.sync.PacketSynchService;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
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
	private PacketSynchService packetSynchService;

	@Autowired
	private PacketUploadService packetUploadService;

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
	private WebView webView;

	/** The approve registration root sub pane. */
	@FXML
	private GridPane approveRegistrationRootSubPane;

	/** The image anchor pane. */
	@FXML
	private GridPane imageAnchorPane;

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
	
	@FXML
	private TextField filterField;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		reloadTableView();
		tableCellColorChangeListener();
		id.setResizable(false);
		statusComment.setResizable(false);
	}

	private void tableCellColorChangeListener() {
		statusComment.setCellFactory(column -> {
			return new TableCell<RegistrationApprovalDTO, String>() {
				@Override
				public void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					setText(item);
					if (item != null && item.equals(RegistrationUIConstants.APPROVED)) {
						setTextFill(Color.GREEN);
					} else if (item != null && item.equals(RegistrationUIConstants.REJECTED)) {
						setTextFill(Color.RED);
					} else {
						setTextFill(Color.BLACK);
					}
				}
			};
		});
	}

	/**
	 * Method to reload table.
	 */
	private void reloadTableView() {
		LOGGER.info(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID, "Page loading has been started");
		approvalmapList = new ArrayList<>(5);
		authenticateBtn.setDisable(true);
		approvalBtn.setVisible(false);
		rejectionBtn.setVisible(false);
		imageAnchorPane.setVisible(false);

		id.setCellValueFactory(
				new PropertyValueFactory<RegistrationApprovalDTO, String>(RegistrationConstants.EOD_PROCESS_ID));
		statusComment.setCellValueFactory(new PropertyValueFactory<RegistrationApprovalDTO, String>(
				RegistrationConstants.EOD_PROCESS_STATUSCOMMENT));
		acknowledgementFormPath.setCellValueFactory(new PropertyValueFactory<RegistrationApprovalDTO, String>(
				RegistrationConstants.EOD_PROCESS_ACKNOWLEDGEMENTFORMPATH));

		populateTable();
		
		table.getSelectionModel().selectFirst();

		if (table.getSelectionModel().getSelectedItem() != null) {
			viewAck();
		}

		table.setOnMouseClicked((MouseEvent event) -> {
			if (event.getClickCount() == 1) {
				viewAck();
			}
		});

		table.setOnKeyReleased(event -> {
			if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) {
				viewAck();
			}
		});
		
		LOGGER.info(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID, "Page loading has been completed");
	}

	/**
	 * Viewing RegistrationAcknowledgement on selecting the Registration record.
	 */
	private void viewAck() {
		LOGGER.info(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
				"Displaying the Acknowledgement form started");
		if (table.getSelectionModel().getSelectedItem() != null) {

			if (!approvalmapList.isEmpty()) {
				authenticateBtn.setDisable(false);
			}

			webView.getEngine().loadContent(RegistrationConstants.EMPTY);

			approvalBtn.setVisible(true);
			rejectionBtn.setVisible(true);
			imageAnchorPane.setVisible(true);

			try (FileInputStream file = new FileInputStream(
					new File(table.getSelectionModel().getSelectedItem().getAcknowledgementFormPath()))) {
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(file, RegistrationConstants.TEMPLATE_ENCODING));
				StringBuilder acknowledgementContent = new StringBuilder();
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					acknowledgementContent.append(line);
				}
				webView.getEngine().loadContent(acknowledgementContent.toString());
			} catch (IOException ioException) {
				LOGGER.error("REGISTRATION_APPROVAL_CONTROLLER - REGSITRATION_ACKNOWLEDGEMNT_PAGE_LOADING_FAILED",
						APPLICATION_NAME, APPLICATION_ID,
						ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));
			}

		}
		LOGGER.info(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
				"Displaying the Acknowledgement form completed");
	}

	/**
	 * {@code populateTable} method is used for populating registration data.
	 * 
	 */
	private void populateTable() {
		LOGGER.info(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID, "table population has been started");
		List<RegistrationApprovalDTO> listData = null;

		listData = registration.getEnrollmentByStatus(RegistrationClientStatusCode.CREATED.getCode());

		if (!listData.isEmpty()) {

			listData.forEach(approvalDTO -> approvalDTO.setStatusComment(RegistrationUIConstants.PENDING));

			// 1. Wrap the ObservableList in a FilteredList (initially display all data).
			ObservableList<RegistrationApprovalDTO> oList = FXCollections.observableArrayList(listData);

			  FilteredList<RegistrationApprovalDTO> filteredData = new FilteredList<>(oList, p -> true);
		        
		        // 2. Set the filter Predicate whenever the filter changes.
		        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
		            filteredData.setPredicate(reg -> {
		                // If filter text is empty, display all ID's.
		                if (newValue == null || newValue.isEmpty()) {
		                    return true;
		                }
		                
		                // Compare every ID with filter text.
		                String lowerCaseFilter = newValue.toLowerCase();
		                
		                if (reg.getId().contains(lowerCaseFilter)) {
		                    // Filter matches first name.
		                	table.getSelectionModel().selectFirst();
		                	if(table.getSelectionModel().getSelectedItem()!=null) {
		                		viewAck();	
		                	}
		                    return true;
		                } 
		                return false; // Does not match.
		            });
		            table.getSelectionModel().selectFirst();
                	if(table.getSelectionModel().getSelectedItem()!=null) {
                		viewAck();	
                	}
		        });
		        
		        // 3. Wrap the FilteredList in a SortedList. 
		        SortedList<RegistrationApprovalDTO> sortedData = new SortedList<>(filteredData);
		        
		        // 4. Bind the SortedList comparator to the TableView comparator.
		        sortedData.comparatorProperty().bind(table.comparatorProperty());
		        
			table.setItems(sortedData);
			
		} else {
			approveRegistrationRootSubPane.disableProperty().set(true);
			table.setPlaceholder(new Label(RegistrationUIConstants.PLACEHOLDER_LABEL));
			table.getItems().clear();
		}
		
		LOGGER.info(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID, "table population has been ended");
	}

	/**
	 * {@code updateStatus} is to update the status of registration.
	 *
	 * @param event the event
	 * @throws RegBaseCheckedException the reg base checked exception
	 */
	public void updateStatus(ActionEvent event) throws RegBaseCheckedException {

		LOGGER.info(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
				"Registration status updation has been started");

		ToggleButton tBtn = (ToggleButton) event.getSource();

		if (tBtn.getId().equals(approvalBtn.getId())) {

			for (Map<String, String> registrationMap : approvalmapList) {

				if (registrationMap
						.containsValue(table.getItems().get(table.getSelectionModel().getFocusedIndex()).getId())) {

					approvalmapList.remove(registrationMap);

					break;
				}
			}

			Map<String, String> map = new WeakHashMap<>();
			map.put(RegistrationConstants.REGISTRATIONID,
					table.getItems().get(table.getSelectionModel().getFocusedIndex()).getId());
			map.put(RegistrationConstants.STATUSCODE, RegistrationClientStatusCode.APPROVED.getCode());
			map.put(RegistrationConstants.STATUSCOMMENT, RegistrationConstants.EMPTY);
			approvalmapList.add(map);

			authenticateBtn.setDisable(false);

			int rowNum = table.getSelectionModel().getFocusedIndex();
			RegistrationApprovalDTO approvalDTO = new RegistrationApprovalDTO(
					table.getItems().get(table.getSelectionModel().getFocusedIndex()).getId(),
					table.getItems().get(table.getSelectionModel().getFocusedIndex()).getAcknowledgementFormPath(),
					RegistrationUIConstants.APPROVED);
			table.getItems().set(rowNum, approvalDTO);
			table.requestFocus();
			table.getFocusModel().focus(rowNum);

		} else {
			Stage primarystage = new Stage();
			try {

				if (tBtn.getId().equals(rejectionBtn.getId())) {

					rejectionController.initData(table.getItems().get(table.getSelectionModel().getFocusedIndex()),
							primarystage, approvalmapList, table,
							RegistrationConstants.EOD_PROCESS_REGISTRATIONAPPROVALCONTROLLER);

					loadStage(primarystage, RegistrationConstants.REJECTION_PAGE);

					authenticateBtn.setDisable(false);

				} else if (tBtn.getId().equals(authenticateBtn.getId())) {

					loadStage(primarystage, RegistrationConstants.USER_AUTHENTICATION);
					authenticationController.init(this, ProcessNames.EOD.getType());

				}

			} catch (RegBaseCheckedException checkedException) {
				primarystage.close();
				LOGGER.error(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
						"No of Authentication modes is empty");

			} catch (RuntimeException runtimeException) {
				LOGGER.error(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
						runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
			}
		}
		LOGGER.info(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
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

			Pane authRoot = BaseController.load(getClass().getResource(fxmlPath));
			Scene scene = new Scene(authRoot);
			scene.getStylesheets().add(ClassLoader.getSystemClassLoader()
					.getResource(RegistrationConstants.CSS_FILE_PATH).toExternalForm());
			primarystage.initStyle(StageStyle.UNDECORATED);
			primarystage.setScene(scene);
			primarystage.initModality(Modality.WINDOW_MODAL);
			primarystage.initOwner(fXComponents.getStage());
			primarystage.show();
			primarystage.resizableProperty().set(false);
			this.primaryStage = primarystage;

		} catch (IOException ioException) {
			LOGGER.error(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));

			throw new RegBaseCheckedException(RegistrationExceptionConstants.REG_UI_LOGIN_IO_EXCEPTION.getErrorCode(),
					RegistrationExceptionConstants.REG_UI_LOGIN_IO_EXCEPTION.getErrorMessage(), ioException);
		} catch (RuntimeException runtimeException) {

			LOGGER.error(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));

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
		LOGGER.info(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
				"Updation of registration according to status started");
		try {

			List<String> regIds = new ArrayList<>();
			for (Map<String, String> map : approvalmapList) {
				registrationApprovalService.updateRegistration(map.get(RegistrationConstants.REGISTRATIONID),
						map.get(RegistrationConstants.STATUSCOMMENT), map.get(RegistrationConstants.STATUSCODE));
				regIds.add(map.get(RegistrationConstants.REGISTRATIONID));
			}
			generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationUIConstants.AUTH_APPROVAL_SUCCESS_MSG);
			primaryStage.close();
			reloadTableView();

			if (RegistrationAppHealthCheckUtil.isNetworkAvailable() && !regIds.isEmpty()) {

				uploadPacketsInBackground(regIds);

			}
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
					"unable to sync and upload of packets" + runtimeException.getMessage()
							+ ExceptionUtils.getStackTrace(runtimeException));

			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_TO_SYNC_AND_UPLOAD);
		}
		LOGGER.info(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
				"Updation of registration according to status ended");
	}

	private void uploadPacketsInBackground(List<String> regIds) {
		Runnable upload = new Runnable() {
			public void run() {
				String response;
				try {
					response = packetSynchService.syncEODPackets(regIds);
					if (response.equals(RegistrationConstants.EMPTY)) {
						packetUploadService.uploadEODPackets(regIds);
					}
				} catch (RegBaseCheckedException checkedException) {
					LOGGER.error(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
							"Error in sync and upload of packets" + checkedException.getMessage()
									+ ExceptionUtils.getStackTrace(checkedException));
				}
			}
		};

		new Thread(upload).start();
	}
}
