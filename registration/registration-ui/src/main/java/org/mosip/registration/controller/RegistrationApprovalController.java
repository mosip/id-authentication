package org.mosip.registration.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.mosip.kernel.logger.factory.MosipLogfactory;
import org.mosip.registration.constants.RegistrationUIExceptionCode;
import org.mosip.registration.constants.RegistrationUIExceptionEnum;
import org.mosip.registration.dto.RegistrationApprovalUiDto;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.exception.RegBaseUncheckedException;
import org.mosip.registration.service.RegistrationApprovalService;
import org.mosip.registration.ui.constants.RegistrationUIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import static org.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;
import static org.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static org.mosip.registration.constants.RegConstants.APPLICATION_NAME;

import javafx.beans.binding.Bindings;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 *{@code RegistrationApprovalController} is the controller class for Registration approval.
 *
 * @author Mahesh Kumar
 */
@Controller
public class RegistrationApprovalController extends BaseController implements Initializable {

	/**
	 * Instance of {@link MosipLogger}
	 */
	private static MosipLogger LOGGER;
	
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
	 *Table to display the created packets 
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

	int itemsPerPage = 1;
	List<RegistrationApprovalUiDto> listData = null;

	/* (non-Javadoc)
	 * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.debug("REGISTRATION - PAGE_LOADING - REGISTRATION_APPROVAL_CONTROLLER", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "Page loading has been started");

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
		type.setCellValueFactory(new PropertyValueFactory<RegistrationApprovalUiDto, String>("type"));
		residentName.setCellValueFactory(new PropertyValueFactory<RegistrationApprovalUiDto, String>("name"));
		operatorId.setCellValueFactory(new PropertyValueFactory<RegistrationApprovalUiDto, String>("operatorId"));
		operatorName.setCellValueFactory(new PropertyValueFactory<RegistrationApprovalUiDto, String>("operatorName"));
		acknowledgementFormPath.setCellValueFactory(
				new PropertyValueFactory<RegistrationApprovalUiDto, String>("acknowledgementFormPath"));

		expand.setCellFactory(
				new Callback<TableColumn<RegistrationApprovalUiDto, Boolean>, TableCell<RegistrationApprovalUiDto, Boolean>>() {

					@Override
					public TableCell<RegistrationApprovalUiDto, Boolean> call(
							TableColumn<RegistrationApprovalUiDto, Boolean> col) {

						return new ViewAcknowledgementController(table);
					}
				});

		tablePagination();

		approvalBtn.disableProperty().bind(Bindings.isEmpty(table.getSelectionModel().getSelectedItems()));
		rejectionBtn.disableProperty().bind(Bindings.isEmpty(table.getSelectionModel().getSelectedItems()));
		onHoldBtn.disableProperty().bind(Bindings.isEmpty(table.getSelectionModel().getSelectedItems()));
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
	 * Event method for Approving packet
	 * @param event
	 */
	public void approvePacket(ActionEvent event) {
		LOGGER.debug("REGISTRATION - APPROVE_PACKET - REGISTRATION", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "Packet updation has been started");
		
		RegistrationApprovalUiDto regData = table.getSelectionModel().getSelectedItem();

		if (registration.packetUpdateStatus(regData.getId(), "A", "mahesh123", "", "mahesh123")) {
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
			pagination.disableProperty().set(true);
			approvalBtn.disableProperty().bind(Bindings.isEmpty(table.getSelectionModel().getSelectedItems()));
			rejectionBtn.disableProperty().bind(Bindings.isEmpty(table.getSelectionModel().getSelectedItems()));
			onHoldBtn.disableProperty().bind(Bindings.isEmpty(table.getSelectionModel().getSelectedItems()));
			table.setPlaceholder(new Label("No Packets for approval"));
		}
		LOGGER.debug("REGISTRATION - PAGINATION - REGISTRATION", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "Pagination has been ended");
	}

	/**
	 * Event method for packet Rejection 
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
		}catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationUIExceptionCode.REG_UI_LOGIN_LOADER_EXCEPTION,
					runtimeException.getMessage());
		} 
		LOGGER.debug("REGISTRATION - ONHOLD_PACKET - REGISTRATION", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "OnHold of packet has been ended");
	
	}
}
