package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.REG_UI_LOGIN_LOADER_EXCEPTION;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

@Controller
public class RegistrationPendingActionController extends BaseController implements Initializable{

	/**
	 * Instance of {@link Logger}
	 */
	private Logger LOGGER = AppConfig.getLogger(RegistrationApprovalController.class);

	/**
	 * object for Registration approval service class
	 */
	@Autowired
	private RegistrationApprovalService registrationApprovalService;

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
	private Button approvalBtn;
	/**
	 * Button for rejection
	 */
	@FXML
	private Button rejectionBtn;
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
	private AnchorPane approveRegistrationRootSubPane;

	/** The image anchor pane. */
	@FXML
	private AnchorPane pendingActionImageAnchorPane;

	private List<Map<String, String>> approvalmapList = null;

	private Timeline timeline;

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
		setInvisible();
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
			if (!approvalmapList.isEmpty() ) {
				submitBtn.setVisible(true);
			}
			pendingActionImageAnchorPane.setVisible(true);
			approvalBtn.setVisible(true);
			rejectionBtn.setVisible(true);
			
			
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

		try {

			int startTime = 5;
			IntegerProperty timeSeconds = new SimpleIntegerProperty(startTime);

			Stage primaryStage = new Stage();
			autoCloseStage(primaryStage);
			Group root = new Group();
			Scene scene = new Scene(root, 800, 600);
			FileInputStream file = new FileInputStream(
					new File(pendingActionTable.getSelectionModel().getSelectedItem().getAcknowledgementFormPath()));
			primaryStage.setTitle(RegistrationConstants.ACKNOWLEDGEMENT_FORM_TITLE);
			ImageView newimageView = new ImageView(new Image(file));
			HBox hbox = new HBox(newimageView);
			Label timerLabel = new Label();
			timerLabel.textProperty().bind(timeSeconds.asString());
			timerLabel.setTextFill(Color.RED);
			timerLabel.setStyle("-fx-font-size: 2em;");

			VBox vb = new VBox(20);
			vb.setAlignment(Pos.TOP_RIGHT);
			vb.setPrefWidth(scene.getWidth());
			vb.getChildren().addAll(timerLabel);

			root.getChildren().add(hbox);
			root.getChildren().add(vb);

			primaryStage.setOnShowing((WindowEvent event) -> {

				if (timeline != null) {
					timeline.stop();
				}
				timeSeconds.set(startTime);
				timeline = new Timeline();
				timeline.getKeyFrames()
						.add(new KeyFrame(Duration.seconds(startTime + 1), new KeyValue(timeSeconds, 0)));
				timeline.playFromStart();
			});

			primaryStage.setScene(scene);
			scene.setOnKeyPressed((KeyEvent event) -> {
				if (event.getCode() == KeyCode.ESCAPE) {
					primaryStage.close();
				}
			});
			primaryStage.initModality(Modality.WINDOW_MODAL);
			primaryStage.initOwner(stage);
			primaryStage.resizableProperty().set(false);
			primaryStage.show();

		} catch (FileNotFoundException fileNotFoundException) {
			LOGGER.error("REGISTRATION_APPROVAL_CONTROLLER - REGSITRATION_ACKNOWLEDGEMNT_PAGE_LOADING_FAILED",
					APPLICATION_NAME, APPLICATION_ID, fileNotFoundException.getMessage());
		}

	}

	/**
	 * Event method for Approving packet
	 * 
	 * @param event
	 */
	public void pendingActionApprovePacket(ActionEvent event) {
		LOGGER.debug("REGISTRATION - APPROVE_PACKET - REGISTRATION", APPLICATION_NAME, APPLICATION_ID,
				"Packet updation has been started");
		RegistrationApprovalDTO regData = pendingActionTable.getSelectionModel().getSelectedItem();
		Map<String, String> map = new HashMap<>();
		map.put("registrationID", regData.getId());
		map.put("statusCode", RegistrationClientStatusCode.APPROVED.getCode());
		map.put("statusComment", "");
		approvalmapList.add(map);
		
		pendingActionTable.getItems().remove(pendingActionTable.getSelectionModel().getSelectedItem());
		setInvisible();
		generateAlert(RegistrationConstants.STATUS, AlertType.INFORMATION, RegistrationConstants.APPROVED_STATUS_MESSAGE);
		LOGGER.debug("REGISTRATION - APPROVE_PACKET - REGISTRATION_", APPLICATION_NAME, APPLICATION_ID,
				"Packet updation has been ended");
	}

	/**
	 * {@code populateTable} method is used for populating registration data
	 * 
	 */
	public void populateTable() {
		LOGGER.debug("REGISTRATION_APPROVAL_CONTROLLER ", APPLICATION_NAME, APPLICATION_ID,
				"table population has been started");
		 List<RegistrationApprovalDTO> listData = registrationApprovalService.getEnrollmentByStatus(RegistrationClientStatusCode.ON_HOLD.getCode());
		setInvisible();
		if (!listData.isEmpty()) {
			ObservableList<RegistrationApprovalDTO> oList = FXCollections.observableArrayList(listData);
			pendingActionTable.setItems(oList);
		} else {
			pendingActionTable.setPlaceholder(new Label(RegistrationConstants.PLACEHOLDER_LABEL));
			pendingActionTable.getItems().remove(pendingActionTable.getSelectionModel().getSelectedItem());
			generateAlert(RegistrationConstants.STATUS, AlertType.INFORMATION, RegistrationConstants.PLACEHOLDER_LABEL);

		}
		LOGGER.debug("REGISTRATION_APPROVAL_CONTROLLER ", APPLICATION_NAME, APPLICATION_ID,
				"table population has been ended");
	}

	/**
	 * Event method for packet Rejection
	 * 
	 * @param event
	 * @throws RegBaseCheckedException
	 */
	public void pendingActionRejectPacket(ActionEvent event) throws RegBaseCheckedException {
		try {
			LOGGER.debug("REGISTRATION - REJECTION_PACKET - REGISTRATION", APPLICATION_NAME, APPLICATION_ID,
					"Rejection of packet has been started");

			Stage primarystage = new Stage();
			primarystage.initStyle(StageStyle.UNDECORATED);
			RejectionController rejectionController = (RejectionController) RegistrationAppInitialization
					.getApplicationContext().getBean(RegistrationConstants.REJECTION_BEAN_NAME);

			rejectionController.initData(pendingActionTable.getSelectionModel().getSelectedItem(), primarystage, approvalmapList, pendingActionTable);
			LoadStage(primarystage, RegistrationConstants.REJECTION_PAGE, pendingActionTable.getSelectionModel().getSelectedItem());

		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(REG_UI_LOGIN_LOADER_EXCEPTION, runtimeException.getMessage());
		}
		LOGGER.debug("REGISTRATION - REJECTION_PACKET - REGISTRATION", APPLICATION_NAME, APPLICATION_ID,
				"Rejection of packet has been ended");

	}

	public void pendingActionSubmit(ActionEvent event) throws RegBaseCheckedException {

		try {

			Stage primarystage = new Stage();
			AnchorPane authRoot = BaseController.load(getClass().getResource("/fxml/Authentication.fxml"));
			FingerPrintAuthenticationController fingerPrintAuthenticationController = (FingerPrintAuthenticationController) RegistrationAppInitialization
					.getApplicationContext().getBean("authenticationController");
			fingerPrintAuthenticationController.initData(primarystage, approvalmapList);
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
	}
	
	private Stage LoadStage(Stage primarystage,String fxmlPath,RegistrationApprovalDTO registrationApprovalDTO) throws RegBaseCheckedException {
		
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
	
	private void setInvisible() {
		if (approvalmapList == null) {
			submitBtn.setVisible(false);
		}
		approvalBtn.setVisible(false);
		rejectionBtn.setVisible(false);
		pendingActionImageAnchorPane.setVisible(false);
		pendingActionImageView.imageProperty().set(null);
	}
}
