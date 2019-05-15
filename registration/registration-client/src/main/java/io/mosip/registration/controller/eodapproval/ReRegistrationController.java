package io.mosip.registration.controller.eodapproval;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.ProcessNames;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.auth.AuthenticationController;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
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
	private TableView<PacketStatusDTO> table;
	/**
	 * Registration Id column in the table
	 */
	@FXML
	private TableColumn<PacketStatusDTO, String> id;
	/**
	 * Acknowledgement form column in the table
	 */
	@FXML
	private TableColumn<PacketStatusDTO, String> acknowledgementFormPath;

	@FXML
	private ToggleButton informedBtn;

	@FXML
	private ToggleButton notInformedBtn;

	@FXML
	private Button submitBtn;
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

	private Map<String, String> reRegisterStatusMap = new HashMap<>();

	@Autowired
	private AuthenticationController authenticationController;

	private Stage primaryStage;

	private ObservableList<PacketStatusDTO> observableList;

	private SortedList<PacketStatusDTO> sortedList;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.info("REGISTRATION - PAGE_LOADING - RE_REGISTRATION_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Page loading has been started");
		reloadTableView();
		id.setResizable(false);
		acknowledgementFormPath.setResizable(false);
	}

	/**
	 * This method is used to load the ui table
	 * 
	 */
	private void reloadTableView() {
		LOGGER.info("REGISTRATION - LOADING_TABLE - RE_REGISTRATION_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Loading the table in the ui");
		reRegisterStatusMap.clear();
		setInvisible();
		id.setCellValueFactory(new PropertyValueFactory<PacketStatusDTO, String>("fileName"));
		acknowledgementFormPath.setCellValueFactory(new PropertyValueFactory<PacketStatusDTO, String>("sourcePath"));
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
			informedBtn.setSelected(false);
			notInformedBtn.setSelected(false);
			for (Map.Entry<String, String> statusMap : reRegisterStatusMap.entrySet()) {
				if (statusMap.getKey().equals(table.getSelectionModel().getSelectedItem().getFileName())) {
					if (statusMap.getValue().equals("informed")) {
						informedBtn.setSelected(true);
					} else if (statusMap.getValue().equals("notinformed")) {
						notInformedBtn.setSelected(true);
					}
				}
			}
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
				LOGGER.error("RE_REGISTRATION_CONTROLLER - REGSITRATION_ACKNOWLEDGEMNT_PAGE_LOADING_FAILED",
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
		submitBtn.setDisable(false);
		reRegisterStatusMap.put(table.getSelectionModel().getSelectedItem().getFileName(), "informed");
		informedBtn.setSelected(true);
		notInformedBtn.setSelected(false);
	}

	/**
	 * This method will call on click of Not Informed Button
	 */
	public void notInformedToUser() {
		submitBtn.setDisable(false);
		reRegisterStatusMap.put(table.getSelectionModel().getSelectedItem().getFileName(), "notinformed");
		informedBtn.setSelected(false);
		notInformedBtn.setSelected(true);
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

		setInvisible();
		if (!reRegistrationPacketsList.isEmpty()) {
			observableList = FXCollections
					.observableArrayList(reRegistrationPacketsList);
			wrapListAndAddFiltering();
			table.setItems(sortedList);
		} else {
			reRegistrationChildPane.disableProperty().set(true);
			observableList.clear();
			wrapListAndAddFiltering();
			table.setItems(sortedList);
		}
		LOGGER.info("REGISTRATION - TABLE_DATA_POPULATED - REGISTRATION", APPLICATION_NAME, APPLICATION_ID,
				"Pagination has been ended");
	}

	private void wrapListAndAddFiltering() {
		FilteredList<PacketStatusDTO> filteredList = new FilteredList<>(observableList, p -> true);

		// 2. Set the filter Predicate whenever the filter changes.
		filterField.textProperty().addListener((observable, oldValue, newValue) -> {
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
			}
		});

		// 3. Wrap the FilteredList in a SortedList.
		sortedList = new SortedList<>(filteredList);

		// 4. Bind the SortedList comparator to the TableView comparator.
		sortedList.comparatorProperty().bind(table.comparatorProperty());
	}

	private void setInvisible() {
		informedBtn.setVisible(false);
		notInformedBtn.setVisible(false);
		submitBtn.setDisable(true);
		imageAnchorPane.setVisible(false);
		webView.getEngine().loadContent(RegistrationConstants.EMPTY);
	}
}
