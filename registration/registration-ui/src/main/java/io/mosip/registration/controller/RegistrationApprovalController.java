package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.REG_UI_LOGIN_LOADER_EXCEPTION;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.logback.factory.MosipLogfactory;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationExceptions;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.RegistrationApprovalUiDto;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.RegistrationApprovalService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * {@code RegistrationApprovalController} is the controller class for
 * Registration approval.
 *
 * @author Mahesh Kumar
 */
@Controller
public class RegistrationApprovalController extends BaseController implements Initializable {

	/**
	 * Instance of {@link MosipLogger}
	 */
	private MosipLogger LOGGER;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/**
	 * object for Registration approval service class
	 */
	@Autowired
	private RegistrationApprovalService registration;

	/**
	 * Table to display the created packets
	 */
	@FXML
	private TableView<RegistrationApprovalUiDto> table;
	/**
	 * Registration Id column in the table
	 */
	@FXML
	private TableColumn<RegistrationApprovalUiDto, String> id;
	/**
	 * Type/Status column of the Registration Packet in the table
	 */
	@FXML
	private TableColumn<RegistrationApprovalUiDto, String> type;
	/**
	 * Individual name column in the table
	 */
	@FXML
	private TableColumn<RegistrationApprovalUiDto, String> residentName;
	/**
	 * OperatorId column in the table
	 */
	@FXML
	private TableColumn<RegistrationApprovalUiDto, String> operatorId;
	/**
	 * Operator Name column in the table
	 */
	@FXML
	private TableColumn<RegistrationApprovalUiDto, String> operatorName;
	/**
	 * Column Expand the roe of the table and display acknowledgement form
	 */
	@FXML
	private TableColumn<RegistrationApprovalUiDto, Boolean> expand;
	/**
	 * Acknowledgement form column in the table
	 */
	@FXML
	private TableColumn<RegistrationApprovalUiDto, String> acknowledgementFormPath;
	/**
	 * Pagination for the table
	 */
	@FXML
	private Pagination pagination;
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
	private Button onHoldBtn;

	/** The image view. */
	@FXML
	private ImageView imageView;

	/** The approve registration root sub pane. */
	@FXML
	private AnchorPane approveRegistrationRootSubPane;
	
	@FXML
	private AnchorPane imageAnchorPane;
	
	/** Object for environment */
	@Autowired
	private Environment environment;

	int itemsPerPage = 4;
	List<RegistrationApprovalUiDto> listData = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.debug("REGISTRATION - PAGE_LOADING - REGISTRATION_APPROVAL_CONTROLLER",
				APPLICATION_NAME, APPLICATION_ID, "Page loading has been started");
		reloadTableView();
	}

	/**
	 * Method to reload table
	 */
	public void reloadTableView() {
		
		approvalBtn.setVisible(false);
		rejectionBtn.setVisible(false);
		onHoldBtn.setVisible(false);
		imageAnchorPane.setVisible(false);

		id.setCellValueFactory(new PropertyValueFactory<RegistrationApprovalUiDto, String>("id"));
		type.setCellValueFactory(new PropertyValueFactory<RegistrationApprovalUiDto, String>("type"));
		residentName.setCellValueFactory(new PropertyValueFactory<RegistrationApprovalUiDto, String>("name"));
		operatorId.setCellValueFactory(new PropertyValueFactory<RegistrationApprovalUiDto, String>("operatorId"));
		operatorName.setCellValueFactory(new PropertyValueFactory<RegistrationApprovalUiDto, String>("operatorName"));
		acknowledgementFormPath.setCellValueFactory(
				new PropertyValueFactory<RegistrationApprovalUiDto, String>("acknowledgementFormPath"));

		tablePagination();

		table.setOnMouseClicked((MouseEvent event) -> {
			if (event.getClickCount() == 1) {
				viewAck();
			}
		});
	}

	/**
	 * Viewing RegistrationAcknowledgement on selecting the Registration record
	 */
	public void viewAck() {
		LOGGER.debug("REGISTRATION_APPROVAL_CONTROLLER", environment.getProperty(APPLICATION_NAME),
				environment.getProperty(APPLICATION_ID), "Displaying the Acknowledgement form image beside the Table");
		if (table.getSelectionModel().getSelectedItem() != null) {
			imageAnchorPane.setVisible(true);
			approvalBtn.setVisible(true);
			rejectionBtn.setVisible(true);
			onHoldBtn.setVisible(true);
			FileInputStream file;
			try {
				file = new FileInputStream(
						new File(table.getSelectionModel().getSelectedItem().getAcknowledgementFormPath()));
				imageView.setImage(new Image(file));
			} catch (FileNotFoundException fileNotFoundException) {
				LOGGER.error("REGISTRATION_APPROVAL_CONTROLLER - REGSITRATION_ACKNOWLEDGEMNT_PAGE_LOADING_FAILED",
						APPLICATION_NAME, environment.getProperty(APPLICATION_ID),
						fileNotFoundException.getMessage());			}

		}
	}

	private Node createPage(int pageIndex) {
		int fromindex = 0;
		int toindex = 0;
		fromindex = pageIndex * itemsPerPage;
		toindex = Math.min(fromindex + itemsPerPage, listData.size());
		table.setItems(FXCollections.observableArrayList(listData.subList(fromindex, toindex)));
		return table;
	}

	/**
	 * 
	 * Opens the home page screen
	 * 
	 */
	public void goToHomePage() {
		LOGGER.debug("REGISTRATION_APPROVAL_CONTROLLER", environment.getProperty(APPLICATION_NAME),
				environment.getProperty(APPLICATION_ID), "Going to home page");

		try {
			BaseController.load(getClass().getResource(RegistrationConstants.HOME_PAGE));
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION_APPROVAL_CONTROLLER - REGSITRATION_HOME_PAGE_LAYOUT_LOADING_FAILED",
					APPLICATION_NAME, environment.getProperty(APPLICATION_ID),
					ioException.getMessage());
		}
	}

	/**
	 * Opening registration acknowledgement form on clicking on image.
	 */
	public void openAckForm() {
		LOGGER.debug("REGISTRATION_APPROVAL_CONTROLLER", environment.getProperty(APPLICATION_NAME),
				environment.getProperty(APPLICATION_ID), "Opening the Acknowledgement Form");

		try {
			Stage primaryStage = new Stage();
			FileInputStream file = new FileInputStream(
					new File(table.getSelectionModel().getSelectedItem().getAcknowledgementFormPath()));
			primaryStage.setTitle(RegistrationConstants.ACKNOWLEDGEMENT_FORM_TITLE);
			ImageView imageView = new ImageView(new Image(file));
			HBox hbox = new HBox(imageView);
			Scene scene = new Scene(hbox, 800, 600);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (FileNotFoundException fileNotFoundException) {
			LOGGER.error("REGISTRATION_APPROVAL_CONTROLLER - REGSITRATION_ACKNOWLEDGEMNT_PAGE_LOADING_FAILED",
					APPLICATION_NAME, environment.getProperty(APPLICATION_ID),
					fileNotFoundException.getMessage());
		}

	}

	/**
	 * Event method for Approving packet
	 * 
	 * @param event
	 */
	public void approvePacket(ActionEvent event) {
		LOGGER.debug("REGISTRATION - APPROVE_PACKET - REGISTRATION", APPLICATION_NAME,
				APPLICATION_ID, "Packet updation has been started");

		RegistrationApprovalUiDto regData = table.getSelectionModel().getSelectedItem();

		String approverUserId = SessionContext.getInstance().getUserContext().getUserId();
		String approverRoleCode = SessionContext.getInstance().getUserContext().getRoles().get(0);
		if (registration.packetUpdateStatus(regData.getId(), RegistrationClientStatusCode.APPROVED.getCode(), approverUserId, "",
				approverRoleCode)) {
			listData = registration.getAllEnrollments();
			generateAlert(RegistrationConstants.STATUS, AlertType.INFORMATION, RegistrationConstants.APPROVED_STATUS_MESSAGE);
			tablePagination();
		} else {
			generateAlert(RegistrationConstants.STATUS, AlertType.INFORMATION, RegistrationConstants.APPROVED_STATUS_FAILURE_MESSAGE);
		}
		LOGGER.debug("REGISTRATION - APPROVE_PACKET - REGISTRATION_", APPLICATION_NAME,
				APPLICATION_ID, "Packet updation has been ended");
	}

	/**
	 * {@code Pagination} method is used for paginating packet data
	 * 
	 */
	public void tablePagination() {
		LOGGER.debug("REGISTRATION - PAGINATION - REGISTRATION", APPLICATION_NAME,
				APPLICATION_ID, "Pagination has been started");
		listData = registration.getAllEnrollments();
		approvalBtn.setVisible(false);
		rejectionBtn.setVisible(false);
		onHoldBtn.setVisible(false);
		imageAnchorPane.setVisible(false);
		imageView.imageProperty().set(null);
		if (listData.size() != 0) {
			int pageCount = 0;
			if (listData.size() % itemsPerPage == 0) {
				pageCount = (listData.size() / itemsPerPage);
			} else {
				pageCount = (listData.size() / itemsPerPage) + 1;
			}
			pagination.setPageCount(pageCount);
			pagination.setPageFactory(this::createPage);
		} else {
			approveRegistrationRootSubPane.disableProperty().set(true);
			table.setPlaceholder(new Label(RegistrationConstants.PLACEHOLDER_LABEL));
		}
		LOGGER.debug("REGISTRATION - PAGINATION - REGISTRATION", APPLICATION_NAME,
				APPLICATION_ID, "Pagination has been ended");
	}

	/**
	 * Event method for packet Rejection
	 * 
	 * @param event
	 * @throws RegBaseCheckedException
	 */
	public void rejectPacket(ActionEvent event) throws RegBaseCheckedException {
		try {
			LOGGER.debug("REGISTRATION - REJECTION_PACKET - REGISTRATION", APPLICATION_NAME,
					APPLICATION_ID, "Rejection of packet has been started");

			RegistrationApprovalUiDto regData = table.getSelectionModel().getSelectedItem();
			Stage primarystage = new Stage();
			AnchorPane rejectRoot = BaseController.load(getClass().getResource(RegistrationConstants.REJECTION_PAGE));
			RejectionController rejectionController = (RejectionController) RegistrationAppInitialization
					.getApplicationContext().getBean(RegistrationConstants.REJECTION_BEAN_NAME);

			rejectionController.initData(regData.getId(), primarystage);
			Scene scene = new Scene(rejectRoot);
			primarystage.setScene(scene);
			primarystage.show();
			primarystage.resizableProperty().set(false);
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(RegistrationExceptions.REG_UI_LOGIN_IO_EXCEPTION.getErrorCode(),
					RegistrationExceptions.REG_UI_LOGIN_IO_EXCEPTION.getErrorMessage(), ioException);
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(REG_UI_LOGIN_LOADER_EXCEPTION,
					runtimeException.getMessage());
		}
		LOGGER.debug("REGISTRATION - REJECTION_PACKET - REGISTRATION", APPLICATION_NAME,
				APPLICATION_ID, "Rejection of packet has been ended");

	}

	/**
	 * Event method for OnHolding Packet
	 * 
	 * @param event
	 * @throws RegBaseCheckedException
	 */
	public void onHoldPacket(ActionEvent event) throws RegBaseCheckedException {
		try {
			LOGGER.debug("REGISTRATION - ONHOLD_PACKET - REGISTRATION", APPLICATION_NAME,
					APPLICATION_ID, "OnHold of packet has been started");

			RegistrationApprovalUiDto regData = table.getSelectionModel().getSelectedItem();
			Stage primarystage = new Stage();
			AnchorPane holdRoot = BaseController.load(getClass().getResource(RegistrationConstants.ONHOLD_PAGE));
			OnHoldController onHoldController = (OnHoldController) RegistrationAppInitialization.getApplicationContext()
					.getBean(RegistrationConstants.ONHOLD_BEAN_NAME);

			onHoldController.initData(regData.getId(), primarystage);
			Scene scene = new Scene(holdRoot);
			primarystage.setScene(scene);
			primarystage.show();
			primarystage.resizableProperty().set(false);
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(RegistrationExceptions.REG_UI_LOGIN_IO_EXCEPTION.getErrorCode(),
					RegistrationExceptions.REG_UI_LOGIN_IO_EXCEPTION.getErrorMessage(), ioException);
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(REG_UI_LOGIN_LOADER_EXCEPTION,
					runtimeException.getMessage());
		}
		LOGGER.debug("REGISTRATION - ONHOLD_PACKET - REGISTRATION", APPLICATION_NAME,
				APPLICATION_ID, "OnHold of packet has been ended");

	}
}
