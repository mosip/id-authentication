package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

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
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.registration.constants.RegClientStatusCode;
import io.mosip.registration.constants.RegistrationUIExceptionCode;
import io.mosip.registration.constants.RegistrationUIExceptionEnum;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.RegistrationApprovalUiDto;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.RegistrationApprovalService;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

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

	@FXML
	private ImageView imageView;

	@FXML
	private AnchorPane approveRegistrationRootSubPane;
	
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
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID), "Page loading has been started");
		reloadTableView();
	}

	/**
	 * Method to reload table
	 */
	public void reloadTableView() {
		approvalBtn.disableProperty().set(true);
		rejectionBtn.disableProperty().set(true);
		onHoldBtn.disableProperty().set(true);

		id.setCellValueFactory(new PropertyValueFactory<RegistrationApprovalUiDto, String>("id"));

		/*Callback<TableColumn<RegistrationApprovalUiDto, String>, TableCell<RegistrationApprovalUiDto, String>> cellFactory0 = (
				final TableColumn<RegistrationApprovalUiDto, String> entry) -> {
			return new TableCell<RegistrationApprovalUiDto, String>() {

				Hyperlink hyperlink = new Hyperlink();

				@Override
				public void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setGraphic(null);
						setText(null);
					} else {
						RegistrationApprovalUiDto tempParam = table.getItems().get(getIndex());
						if (tempParam.getId() != null) {
							System.out.println("set hyperlink " + tempParam.getName());
							hyperlink.setText(item);
							hyperlink.setOnAction(event -> {
								System.out.println("Go to URL");
								onEdit();
							});
							setGraphic(hyperlink);
							setText(null);
						} else {
							hyperlink.setText("");
							setGraphic(null);
							setText(null);
						}
					}
				}
			};
		};
		id.setCellFactory(cellFactory0);*/

		type.setCellValueFactory(new PropertyValueFactory<RegistrationApprovalUiDto, String>("type"));
		residentName.setCellValueFactory(new PropertyValueFactory<RegistrationApprovalUiDto, String>("name"));
		operatorId.setCellValueFactory(new PropertyValueFactory<RegistrationApprovalUiDto, String>("operatorId"));
		operatorName.setCellValueFactory(new PropertyValueFactory<RegistrationApprovalUiDto, String>("operatorName"));
		acknowledgementFormPath.setCellValueFactory(
				new PropertyValueFactory<RegistrationApprovalUiDto, String>("acknowledgementFormPath"));

		tablePagination();

		approvalBtn.disableProperty().bind(Bindings.isEmpty(table.getSelectionModel().getSelectedItems()));
		rejectionBtn.disableProperty().bind(Bindings.isEmpty(table.getSelectionModel().getSelectedItems()));
		onHoldBtn.disableProperty().bind(Bindings.isEmpty(table.getSelectionModel().getSelectedItems()));

		table.setOnMouseClicked((MouseEvent event) -> {
			if (event.getClickCount() == 1) {
				onEdit();
			}
		});
	}

	public void onEdit() {
		// check the table's selected item and get selected item
		if (table.getSelectionModel().getSelectedItem() != null) {
			FileInputStream file;
			try {
				file = new FileInputStream(
						new File(table.getSelectionModel().getSelectedItem().getAcknowledgementFormPath()));
				imageView.setImage(new Image(file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		}
	}

	private Node createPage(int pageIndex) {
		int fromindex = 0;
		int toindex = 0;
		fromindex = pageIndex * itemsPerPage;
		toindex = Math.min(fromindex + itemsPerPage, listData.size());
		table.setItems(FXCollections.observableArrayList(listData.subList(fromindex, toindex)));
		/*
		 * expand.setCellFactory( new Callback<TableColumn<RegistrationApprovalUiDto,
		 * Boolean>, TableCell<RegistrationApprovalUiDto, Boolean>>() {
		 * 
		 * @Override public TableCell<RegistrationApprovalUiDto, Boolean> call(
		 * TableColumn<RegistrationApprovalUiDto, Boolean> col) { return new
		 * ViewAcknowledgementController(table); } });
		 */
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
			BaseController.load(getClass().getResource("/fxml/RegistrationOfficerLayout.fxml"));
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION_APPROVAL_CONTROLLER - REGSITRATION_HOME_PAGE_LAYOUT_LOADING_FAILED",
					getPropertyValue(APPLICATION_NAME), environment.getProperty(APPLICATION_ID),
					ioException.getMessage());
		}
	}

	public void openAckForm() {
		try {
			Stage primaryStage = new Stage();
			FileInputStream file = new FileInputStream(
					new File(table.getSelectionModel().getSelectedItem().getAcknowledgementFormPath()));
			primaryStage.setTitle("Acknowlegement Form");
			ImageView imageView = new ImageView(new Image(file));

			HBox hbox = new HBox(imageView);

			Scene scene = new Scene(hbox, 800, 600);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Event method for Approving packet
	 * 
	 * @param event
	 */
	public void approvePacket(ActionEvent event) {
		LOGGER.debug("REGISTRATION - APPROVE_PACKET - REGISTRATION", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "Packet updation has been started");

		RegistrationApprovalUiDto regData = table.getSelectionModel().getSelectedItem();

		String approverUserId = SessionContext.getInstance().getUserContext().getUserId();
		if (registration.packetUpdateStatus(regData.getId(), RegClientStatusCode.APPROVED.getCode(), approverUserId, "",
				approverUserId)) {
			listData = registration.getAllEnrollments();
			generateAlert("Status", AlertType.INFORMATION, "Registration Approved successfully..");
			tablePagination();
		} else {
			generateAlert("Status", AlertType.INFORMATION, "");
		}
		LOGGER.debug("REGISTRATION - APPROVE_PACKET - REGISTRATION_", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "Packet updation has been ended");
	}

	/**
	 * {@code Pagination} method is used for paginating packet data
	 * 
	 */
	public void tablePagination() {
		LOGGER.debug("REGISTRATION - PAGINATION - REGISTRATION", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "Pagination has been started");
		listData = registration.getAllEnrollments();
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
			imageView.imageProperty().set(null);
			approveRegistrationRootSubPane.disableProperty().set(true);
			table.setPlaceholder(new Label("No Packets for approval"));
		}
		LOGGER.debug("REGISTRATION - PAGINATION - REGISTRATION", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "Pagination has been ended");
	}

	/**
	 * Event method for packet Rejection
	 * 
	 * @param event
	 * @throws RegBaseCheckedException
	 */
	public void rejectPacket(ActionEvent event) throws RegBaseCheckedException {
		try {
			LOGGER.debug("REGISTRATION - REJECTION_PACKET - REGISTRATION", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), "Rejection of packet has been started");

			RegistrationApprovalUiDto regData = table.getSelectionModel().getSelectedItem();
			Stage primarystage = new Stage();
			AnchorPane rejectRoot = BaseController.load(getClass().getResource("/fxml/RejectionComment.fxml"));
			RejectionController rejectionController = (RejectionController) RegistrationAppInitialization
					.getApplicationContext().getBean("rejectionController");

			rejectionController.initData(regData.getId(), primarystage);
			Scene scene = new Scene(rejectRoot);
			primarystage.setScene(scene);
			primarystage.show();
			primarystage.resizableProperty().set(false);
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(RegistrationUIExceptionEnum.REG_UI_LOGIN_IO_EXCEPTION.getErrorCode(),
					RegistrationUIExceptionEnum.REG_UI_LOGIN_IO_EXCEPTION.getErrorMessage(), ioException);
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationUIExceptionCode.REG_UI_LOGIN_LOADER_EXCEPTION,
					runtimeException.getMessage());
		}
		LOGGER.debug("REGISTRATION - REJECTION_PACKET - REGISTRATION", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "Rejection of packet has been ended");

	}

	/**
	 * Event method for OnHolding Packet
	 * 
	 * @param event
	 * @throws RegBaseCheckedException
	 */
	public void onHoldPacket(ActionEvent event) throws RegBaseCheckedException {
		try {
			LOGGER.debug("REGISTRATION - ONHOLD_PACKET - REGISTRATION", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), "OnHold of packet has been started");

			RegistrationApprovalUiDto regData = table.getSelectionModel().getSelectedItem();
			Stage primarystage = new Stage();
			AnchorPane holdRoot = BaseController.load(getClass().getResource("/fxml/OnholdComment.fxml"));
			OnHoldController onHoldController = (OnHoldController) RegistrationAppInitialization.getApplicationContext()
					.getBean("onHoldController");

			onHoldController.initData(regData.getId(), primarystage);
			Scene scene = new Scene(holdRoot);
			primarystage.setScene(scene);
			primarystage.show();
			primarystage.resizableProperty().set(false);
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(RegistrationUIExceptionEnum.REG_UI_LOGIN_IO_EXCEPTION.getErrorCode(),
					RegistrationUIExceptionEnum.REG_UI_LOGIN_IO_EXCEPTION.getErrorMessage(), ioException);
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationUIExceptionCode.REG_UI_LOGIN_LOADER_EXCEPTION,
					runtimeException.getMessage());
		}
		LOGGER.debug("REGISTRATION - ONHOLD_PACKET - REGISTRATION", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "OnHold of packet has been ended");

	}
}
