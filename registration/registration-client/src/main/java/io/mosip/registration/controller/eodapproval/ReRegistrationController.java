package io.mosip.registration.controller.eodapproval;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_PENDING_APPROVAL;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.auth.AuthenticationController;
import io.mosip.registration.controller.vo.PacketStatusVO;
import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.packet.ReRegistrationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

@Controller
public class ReRegistrationController extends BaseController implements Initializable {

	/**
	 * Instance of {@link MosipLogger}
	 */
	private Logger LOGGER = AppConfig.getLogger(ReRegistrationController.class);

	/**
	 * object for Registration approval service class
	 */
	@Autowired
	private ReRegistrationService reRegistrationServiceImpl;

	/**
	 * Table to display the created packets
	 */
	@FXML
	private TableView<PacketStatusVO> table;
	/**
	 * Registration Id column in the table
	 */
	@FXML
	private TableColumn<PacketStatusVO, String> slno;
	/**
	 * Registration Id column in the table
	 */
	@FXML
	private TableColumn<PacketStatusVO, String> id;
	/**
	 * Acknowledgement form column in the table
	 */
	@FXML
	private TableColumn<PacketStatusVO, String> acknowledgementFormPath;
	/**
	 * Registration Id column in the table
	 */
	@FXML
	private TableColumn<PacketStatusVO, String> date;
	/**
	 * ReRegistration Status column in the table
	 */
	@FXML
	private TableColumn<PacketStatusVO, String> status;
	
	@FXML
	private ToggleButton informedBtn;

	@FXML
	private ToggleButton notInformedBtn;

	@FXML
	private Button authenticateBtn;
	/** The image view. */
	@FXML
	private WebView webView;

	/** The image anchor pane. */
	@FXML
	private GridPane imageAnchorPane;

	@FXML
	private GridPane reRegistrationChildPane;

	@FXML
	private TextField filterField;

	private Map<String, String> reRegisterStatusMap;

	@Autowired
	private AuthenticationController authenticationController;

	private Stage primaryStage;

	private ObservableList<PacketStatusVO> observableList;

	private Map<String, Integer> packetIds = new HashMap<>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.	fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.info("REGISTRATION - PAGE_LOADING - RE_REGISTRATION_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Page loading has been started");
		reloadTableView();
		tableCellColorChangeListener();
		disableColumnsReorder(table);
	}

	private void tableCellColorChangeListener() {
		status.setCellFactory(column -> {
			return new TableCell<PacketStatusVO, String>() {
				@Override
				public void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					setText(item);
					if (item != null && item.equals(RegistrationUIConstants.INFORMED)) {
						actionCounter++;
						setTextFill(Color.GREEN);
					} else if (item != null && item.equals(RegistrationUIConstants.CANTINFORMED)) {
						actionCounter++;
						setTextFill(Color.RED);
					} else {
						setTextFill(Color.BLACK);
					}
				}
			};
		});
	}
	
	/**
	 * This method is used to load the ui table
	 * 
	 */
	private void reloadTableView() {
		LOGGER.info("REGISTRATION - LOADING_TABLE - RE_REGISTRATION_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Loading the table in the ui");
		
		reRegisterStatusMap = new WeakHashMap<>();
		authenticateBtn.setDisable(true);
		informedBtn.setVisible(false);
		notInformedBtn.setVisible(false);
		imageAnchorPane.setVisible(false);
		
		reRegisterStatusMap.clear();

		slno.setCellValueFactory(new PropertyValueFactory<PacketStatusVO, String>("slno"));
		id.setCellValueFactory(new PropertyValueFactory<PacketStatusVO, String>("fileName"));
		date.setCellValueFactory(new PropertyValueFactory<PacketStatusVO, String>("createdTime"));
		acknowledgementFormPath.setCellValueFactory(new PropertyValueFactory<PacketStatusVO, String>("sourcePath"));
		status.setCellValueFactory(new PropertyValueFactory<PacketStatusVO, String>("packetStatus"));
		
		showReregisterdPackets();
		
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
	}

	/**
	 * Viewing RegistrationAcknowledgement on selecting the ReRegistration record
	 */
	private void viewAck() {
		LOGGER.info("RE_REGISTRATION_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Displaying the Acknowledgement form image beside the Table");
		if (table.getSelectionModel().getSelectedItem() != null) {
			informedBtn.setVisible(true);
			notInformedBtn.setVisible(true);
			imageAnchorPane.setVisible(true);
		
			webView.getEngine().loadContent(RegistrationConstants.EMPTY);
			try (FileInputStream file = new FileInputStream(
					new File(table.getSelectionModel().getSelectedItem().getPacketPath()))) {
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(file, RegistrationConstants.TEMPLATE_ENCODING));
				StringBuilder acknowledgementContent = new StringBuilder();
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					acknowledgementContent.append(line);
				}
				webView.getEngine().loadContent(acknowledgementContent.toString());
			} catch (FileNotFoundException fileNotFoundException) {
				LOGGER.error("RE_REGISTRATION_CONTROLLER - REGSITRATION_ACKNOWLEDGEMENT_PAGE_LOADING_FAILED",
						APPLICATION_NAME, APPLICATION_ID, ExceptionUtils.getStackTrace(fileNotFoundException));
			} catch (IOException ioException) {
				LOGGER.error("RE_REGISTRATION_CONTROLLER - FAILED_WHILE_READING_ACKNOWLEDGEMENT", APPLICATION_NAME,
						APPLICATION_ID, ExceptionUtils.getStackTrace(ioException));
			}

		}
	}

	/**
	 * This method will call on click of Informed Button
	 */
	public void informedToUser() {
		authenticateBtn.setDisable(false);
		reRegisterStatusMap.put(table.getSelectionModel().getSelectedItem().getFileName(), "informed");
		
		int focusedIndex = table.getSelectionModel().getFocusedIndex();

		int row = packetIds.get(table.getSelectionModel().getSelectedItem().getFileName());
		PacketStatusVO packetStatusVO = new PacketStatusVO();
		packetStatusVO.setSlno(table.getSelectionModel().getSelectedItem().getSlno());
		packetStatusVO.setFileName(table.getSelectionModel().getSelectedItem().getFileName());
		packetStatusVO.setCreatedTime(table.getSelectionModel().getSelectedItem().getCreatedTime());
		packetStatusVO.setPacketPath(table.getSelectionModel().getSelectedItem().getPacketPath());
		packetStatusVO.setPacketStatus(RegistrationUIConstants.INFORMED);
		observableList.set(row, packetStatusVO);
		
		wrapListAndAddFiltering(observableList);
		table.requestFocus();
		table.getFocusModel().focus(focusedIndex);
		table.getSelectionModel().select(focusedIndex);
	}

	/**
	 * This method will call on click of Not Informed Button
	 */
	public void notInformedToUser() {
		authenticateBtn.setDisable(false);
		reRegisterStatusMap.put(table.getSelectionModel().getSelectedItem().getFileName(), "notinformed");

		int focusedIndex = table.getSelectionModel().getFocusedIndex();

		int row = packetIds.get(table.getSelectionModel().getSelectedItem().getFileName());
		PacketStatusVO packetStatusVO = new PacketStatusVO();
		packetStatusVO.setSlno(table.getSelectionModel().getSelectedItem().getSlno());
		packetStatusVO.setFileName(table.getSelectionModel().getSelectedItem().getFileName());
		packetStatusVO.setCreatedTime(table.getSelectionModel().getSelectedItem().getCreatedTime());
		packetStatusVO.setPacketPath(table.getSelectionModel().getSelectedItem().getPacketPath());
		packetStatusVO.setPacketStatus(RegistrationUIConstants.CANTINFORMED);
		observableList.set(row, packetStatusVO);

		wrapListAndAddFiltering(observableList);
		table.requestFocus();
		table.getFocusModel().focus(focusedIndex);
		table.getSelectionModel().select(focusedIndex);
	}

	/**
	 * To display the Authentication UI page
	 */
	public void authenticateReregister() {
		LOGGER.info("RE_REGISTRATION_CONTROLLER - AUTHENTICATE_USER", APPLICATION_NAME, APPLICATION_ID,
				"Updating the table after the authentication finished successfully");
		Stage primarystage = new Stage();
		try {
			showAuthenticatePage(primarystage);
			authenticationController.init(this, ProcessNames.EOD.getType());

		} catch (IOException ioException) {
			LOGGER.error("RE_REGISTRATION_CONTROLLER - AUTHENTICATE_USER_FAILED", APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));
		} catch (RegBaseCheckedException regBaseCheckedException) {
			primarystage.close();
			LOGGER.error("RE_REGISTRATION_CONTROLLER - AUTHENTICATE_USER_FAILED", APPLICATION_NAME, APPLICATION_ID,
					"No of authentication modes is empty" + ExceptionUtils.getStackTrace(regBaseCheckedException));
		}
	}

	private void showAuthenticatePage(Stage primarystage) throws IOException {
		GridPane authRoot = BaseController.load(getClass().getResource(RegistrationConstants.USER_AUTHENTICATION));
		Scene scene = new Scene(authRoot);
		scene.getStylesheets().add(
				ClassLoader.getSystemClassLoader().getResource(RegistrationConstants.CSS_FILE_PATH).toExternalForm());
		primarystage.initStyle(StageStyle.UNDECORATED);
		primarystage.setScene(scene);
		primarystage.initModality(Modality.WINDOW_MODAL);
		primarystage.initOwner(fXComponents.getStage());
		primarystage.show();
		primarystage.resizableProperty().set(false);
		this.primaryStage = primarystage;
	}

	/**
	 * TO the scan the finger and validate with database
	 * 
	 * @throws IOException
	 */

	@Override
	public void updateAuthenticationStatus() {
		LOGGER.info("REGISTRATION - PAGINATION - REGISTRATION", APPLICATION_NAME, APPLICATION_ID,
				"Pagination has been started");
		reRegistrationServiceImpl.updateReRegistrationStatus(reRegisterStatusMap);
		generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationUIConstants.REREGISTRATION_APPROVE_SUCCESS);
		actionCounter = 0;
		primaryStage.close();
		reloadTableView();
	}

	/**
	 * {@code Pagination} method is used for paginating packet data
	 * 
	 */
	private void showReregisterdPackets() {
		LOGGER.info("REGISTRATION - POPULATE_TABLE_DATA - REGISTRATION", APPLICATION_NAME, APPLICATION_ID,
				"Pagination has been started");
		List<PacketStatusDTO> reRegistrationPacketsList = reRegistrationServiceImpl.getAllReRegistrationPackets();
		List<PacketStatusVO> packetStatusVOs = new ArrayList<>();

		if (!reRegistrationPacketsList.isEmpty()) {

			int count = 1;
			for (PacketStatusDTO reRegisterPacket : reRegistrationPacketsList) {
				PacketStatusVO packetStatusVO = new PacketStatusVO();
				packetStatusVO.setSlno(String.valueOf("    "+count++));
				packetStatusVO.setFileName(reRegisterPacket.getFileName());
				packetStatusVO.setPacketPath(reRegisterPacket.getPacketPath());
				packetStatusVO.setCreatedTime(reRegisterPacket.getCreatedTime());
				packetStatusVO.setPacketStatus(RegistrationUIConstants.PENDING);
				packetStatusVOs.add(packetStatusVO);
			}
			int rowNum = 0;
			for(PacketStatusDTO packetStatusDTO : reRegistrationPacketsList) {
				packetIds.put(packetStatusDTO.getFileName(), rowNum++);
			}
			observableList = FXCollections.observableArrayList(packetStatusVOs);
			wrapListAndAddFiltering(observableList);
		} else {
			reRegistrationChildPane.disableProperty().set(true);
			if (observableList != null) {
				observableList.clear();
				wrapListAndAddFiltering(observableList);
			}
			filterField.clear();
		}
		LOGGER.info("REGISTRATION - TABLE_DATA_POPULATED - REGISTRATION", APPLICATION_NAME, APPLICATION_ID,
				"Pagination has been ended");
	}

	private void wrapListAndAddFiltering(ObservableList<PacketStatusVO> oList) {
		FilteredList<PacketStatusVO> filteredList = new FilteredList<>(oList, p -> true);

		// 2. Set the filter Predicate whenever the filter changes.
		filterField.textProperty().addListener((observable, oldValue, newValue) -> filterData(newValue, filteredList));
		if (!filterField.getText().isEmpty()) {
			filterData(filterField.getText(), filteredList);
		}

		// 3. Wrap the FilteredList in a SortedList.
		SortedList<PacketStatusVO> sortedList = new SortedList<>(filteredList);

		// 4. Bind the SortedList comparator to the TableView comparator.
		sortedList.comparatorProperty().bind(table.comparatorProperty());
		table.setItems(sortedList);
	}

	private void filterData(String newValue, FilteredList<PacketStatusVO> filteredList) {
		filteredList.setPredicate(reg -> {
			// If filter text is empty, display all ID's.
			if (newValue == null || newValue.isEmpty()) {
				return true;
			}

			// Compare every ID with filter text.
			String lowerCaseFilter = newValue.toLowerCase();

			if (reg.getFileName().contains(lowerCaseFilter)) {
				// Filter matches first name.
				table.getSelectionModel().selectFirst();
				return true;
			}
			return false; // Does not match.
		});
		table.getSelectionModel().selectFirst();
		if (table.getSelectionModel().getSelectedItem() != null) {
			viewAck();
			informedBtn.setDisable(false);
			notInformedBtn.setDisable(false);
		}else {
			webView.getEngine().loadContent(RegistrationConstants.EMPTY);
			informedBtn.setDisable(true);
			notInformedBtn.setDisable(true);
		}
	}
	
	private int actionCounter=0;

	/**
	 * Opens the home page screen.
	 */
	public void goToHomePageFromReRegistration() {
		try {
			if (actionCounter > 0) {
				if (pageNavigantionAlert()) {
					BaseController.load(getClass().getResource(RegistrationConstants.HOME_PAGE));
					if (!(boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
						clearOnboardData();
						clearRegistrationData();
					} else {
						SessionContext.map().put(RegistrationConstants.ISPAGE_NAVIGATION_ALERT_REQ,
								RegistrationConstants.ENABLE);
					}
					actionCounter = 0;
				}
			} else {
				BaseController.load(getClass().getResource(RegistrationConstants.HOME_PAGE));
				if (!(boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
					clearOnboardData();
					clearRegistrationData();
				} else {
					SessionContext.map().put(RegistrationConstants.ISPAGE_NAVIGATION_ALERT_REQ,
							RegistrationConstants.ENABLE);
				}
				actionCounter = 0;
			}
		} catch (IOException ioException) {
			LOGGER.error(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_HOME_PAGE);
		} catch (RuntimeException runtimException) {
			LOGGER.error(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
					runtimException.getMessage() + ExceptionUtils.getStackTrace(runtimException));
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_HOME_PAGE);
		}
	}
}
