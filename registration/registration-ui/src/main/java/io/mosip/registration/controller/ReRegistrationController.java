package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.entity.RegistrationUserDetail;
import io.mosip.registration.service.LoginService;
import io.mosip.registration.service.impl.LoginServiceImpl;
import io.mosip.registration.service.impl.ReRegistrationService;
import io.mosip.registration.util.biometric.FingerprintProvider;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

@Controller
public class ReRegistrationController extends BaseController implements Initializable {

	/**
	 * Instance of {@link MosipLogger}
	 */
	private Logger LOGGER = AppConfig.getLogger(RegistrationApprovalController.class);

	/**
	 * object for Registration approval service class
	 */
	@Autowired
	private ReRegistrationService reRegistrationServiceImpl;

	@Autowired
	private FingerPrintAuthenticationController authenticationController;

	@Autowired
	LoginService loginService;

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
	private ToggleGroup informedStatus;

	@FXML
	private Button submitBtn;
	/** The image view. */
	@FXML
	private ImageView imageView;

	/** The image anchor pane. */
	@FXML
	private AnchorPane imageAnchorPane;

	@FXML
	private ComboBox<String> deviceCmbBox;

	@FXML
	private LoginServiceImpl loginServiceImpl;

	@Value("${FINGER_PRINT_SCORE}")
	private long fingerPrintScore;

	Map<String, String> contactStatusMap = new HashMap<>();

	private FingerprintProvider fingerprintProvider = new FingerprintProvider();

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.debug("REGISTRATION - PAGE_LOADING - RE_REGISTRATION_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Page loading has been started");
		reloadTableView();
		// writeToByte();
	}

	/**
	 * This method is used to load the ui table
	 * 
	 */
	public void reloadTableView() {
		LOGGER.debug("REGISTRATION - LOADING_TABLE - RE_REGISTRATION_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Loading the table in the ui");
		contactStatusMap.clear();
		setInvisible();
		id.setCellValueFactory(new PropertyValueFactory<PacketStatusDTO, String>("fileName"));
		acknowledgementFormPath.setCellValueFactory(new PropertyValueFactory<PacketStatusDTO, String>("sourcePath"));
		tablePagination();
		table.setOnMouseClicked((MouseEvent event) -> {
			if (event.getClickCount() == 1) {
				viewAck();
			}
		});
	}

	/**
	 * Viewing RegistrationAcknowledgement on selecting the ReRegistration record
	 */
	public void viewAck() {
		LOGGER.debug("RE_REGISTRATION_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Displaying the Acknowledgement form image beside the Table");
		if (table.getSelectionModel().getSelectedItem() != null) {
			informedBtn.setVisible(true);
			notInformedBtn.setVisible(true);
			imageAnchorPane.setVisible(true);
			submitBtn.setVisible(true);
			FileInputStream file;
			try {
				file = new FileInputStream(new File(table.getSelectionModel().getSelectedItem().getSourcePath()));
				imageView.setImage(new Image(file));
			} catch (FileNotFoundException fileNotFoundException) {
				LOGGER.error("RE_REGISTRATION_CONTROLLER - REGSITRATION_ACKNOWLEDGEMNT_PAGE_LOADING_FAILED",
						APPLICATION_NAME, APPLICATION_ID, fileNotFoundException.getMessage());
			}

		}
	}

	/**
	 * This method will call on click of Informed Button
	 */
	public void informedToUser() {
		contactStatusMap.put(table.getSelectionModel().getSelectedItem().getFileName(), "informed");
		informedBtn.setSelected(true);
		notInformedBtn.setSelected(false);
	}

	/**
	 * This method will call on click of Not Informed Button
	 */
	public void notInformedToUser() {
		contactStatusMap.put(table.getSelectionModel().getSelectedItem().getFileName(), "notinformed");
		informedBtn.setSelected(false);
		notInformedBtn.setSelected(true);
	}

	/**
	 * On click on Authenticate button this method will call which in turn call the
	 * Authentication UI page
	 */
	public void submitReRegistration() {
		LOGGER.debug("RE_REGISTRATION_CONTROLLER - SUBMIT_RE_REGISTRATION", APPLICATION_NAME, APPLICATION_ID,
				"Updating the table after the authentication finished successfully");
		authenticateUser();
		System.out.println("Map values are********** mosip" + contactStatusMap);
		// reRegistrationServiceImpl.updateReRegistrationStatus(contactStatusMap);
	}

	/**
	 * To display the Authentication UI page
	 */
	private void authenticateUser() {
		LOGGER.debug("RE_REGISTRATION_CONTROLLER - AUTHENTICATE_USER", APPLICATION_NAME, APPLICATION_ID,
				"Updating the table after the authentication finished successfully");
		Stage primaryStage = new Stage();
		Parent ackRoot;
		try {
			ackRoot = BaseController.load(getClass().getResource(RegistrationConstants.USER_AUTHENTICATION));
			deviceCmbBox.getItems().clear();
			deviceCmbBox.setItems(FXCollections.observableArrayList(RegistrationConstants.ONBOARD_DEVICE_TYPES));
			primaryStage.setResizable(false);
			Scene scene = new Scene(ackRoot);
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * TO the scan the finger and validate with database
	 */
	public void scanFinger() {
		LOGGER.debug("RE_REGISTRATION_CONTROLLER - SCAN_FINGER", APPLICATION_NAME, APPLICATION_ID,
				"Scanning the finger for biometric authentication");
		String minutia = authenticationController.scanFingerPrint();
		RegistrationUserDetail detail = loginService.getUserDetail("mosip");
		validateFingerPrint(minutia, detail);
	}

	/**
	 * Validate the Scanned Finger print
	 * 
	 * @param minutia
	 * @param detail
	 */
	public void validateFingerPrint(String minutia, RegistrationUserDetail detail) {
		LOGGER.debug("RE_REGISTRATION_CONTROLLER - VALIDATE_FINGER_PRINT", APPLICATION_NAME, APPLICATION_ID,
				"Validating the scanned finger print");
		if (validateBiometric(minutia, detail)) {
			if (detail.getStatusCode() != null
					&& detail.getStatusCode().equalsIgnoreCase(RegistrationConstants.BLOCKED)) {
				generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE,
						AlertType.valueOf(RegistrationConstants.ALERT_ERROR), RegistrationConstants.LOGIN_INFO_MESSAGE,
						RegistrationConstants.BLOCKED_USER_ERROR);
			}
		} else {
			generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE, AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
					RegistrationConstants.LOGIN_INFO_MESSAGE, RegistrationConstants.FINGER_PRINT_MATCH);
		}
	}

	/**
	 * Compare the scanned finger print with the database
	 * 
	 * @param minutia
	 * @param registrationUserDetail
	 * @return
	 */
	private boolean validateBiometric(String minutia, RegistrationUserDetail registrationUserDetail) {

		return registrationUserDetail.getUserBiometric().stream()
				.anyMatch(bio -> fingerprintProvider.scoreCalculator(minutia, bio.getBioMinutia()) > fingerPrintScore);
	}

	/**
	 * {@code Pagination} method is used for paginating packet data
	 * 
	 */
	private void tablePagination() {
		LOGGER.debug("REGISTRATION - PAGINATION - REGISTRATION", APPLICATION_NAME, APPLICATION_ID,
				"Pagination has been started");
		List<PacketStatusDTO> listData = null;
		listData = reRegistrationServiceImpl.getAllReRegistrationPackets();
		setInvisible();
		if (!listData.isEmpty()) {
			ObservableList<PacketStatusDTO> oListStavaka = FXCollections.observableArrayList(listData);
			table.setItems(oListStavaka);
		}
		LOGGER.debug("REGISTRATION - PAGINATION - REGISTRATION", APPLICATION_NAME, APPLICATION_ID,
				"Pagination has been ended");
	}

	private void setInvisible() {
		informedBtn.setVisible(false);
		notInformedBtn.setVisible(false);
		submitBtn.setVisible(false);
		imageAnchorPane.setVisible(false);
		imageView.imageProperty().set(null);
	}
}
