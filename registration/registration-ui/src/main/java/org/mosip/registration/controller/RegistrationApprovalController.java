package org.mosip.registration.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.mosip.registration.dto.RegistrationApprovalUiDto;
import org.mosip.registration.service.RegistrationApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

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

@Controller
public class RegistrationApprovalController extends BaseController implements Initializable {

	RegistrationApprovalUiDto list;

	@Autowired
	RegistrationApprovalService registration;

	@FXML
	TableView<RegistrationApprovalUiDto> table;
	@FXML
	TableColumn<RegistrationApprovalUiDto, String> id;
	@FXML
	TableColumn<RegistrationApprovalUiDto, String> type;
	@FXML
	TableColumn<RegistrationApprovalUiDto, String> residentName;
	@FXML
	TableColumn<RegistrationApprovalUiDto, String> operatorId;
	@FXML
	TableColumn<RegistrationApprovalUiDto, String> operatorName;
	@FXML
	TableColumn<RegistrationApprovalUiDto, Boolean> expand;
	@FXML
	TableColumn<RegistrationApprovalUiDto, String> acknowledgementFormPath;
	@FXML
	Pagination pagination;

	@FXML
	Button approvalBtn;
	@FXML
	Button rejectionBtn;
	@FXML
	Button onHoldBtn;

	int itemsPerPage = 1;
	List<RegistrationApprovalUiDto> listData = null;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		reloadTableView();
	}

	public void reloadTableView() {
		approvalBtn.disableProperty().set(true);
		rejectionBtn.disableProperty().set(true);
		onHoldBtn.disableProperty().set(true);

		id.setCellValueFactory(new PropertyValueFactory<RegistrationApprovalUiDto, String>("id"));
		type.setCellValueFactory(new PropertyValueFactory<RegistrationApprovalUiDto, String>("type"));
		residentName.setCellValueFactory(new PropertyValueFactory<RegistrationApprovalUiDto, String>("name"));
		operatorId.setCellValueFactory(new PropertyValueFactory<RegistrationApprovalUiDto, String>("operatorId"));
		operatorName.setCellValueFactory(new PropertyValueFactory<RegistrationApprovalUiDto, String>("operatorName"));
		acknowledgementFormPath.setCellValueFactory(new PropertyValueFactory<RegistrationApprovalUiDto, String>("acknowledgementFormPath"));

		expand.setCellFactory(
				new Callback<TableColumn<RegistrationApprovalUiDto, Boolean>, TableCell<RegistrationApprovalUiDto, Boolean>>() {

					@Override
					public TableCell<RegistrationApprovalUiDto, Boolean> call(
							TableColumn<RegistrationApprovalUiDto, Boolean> col) {
						
						return new ViewAcknowledgementForm(BaseController.stage, table);
					}
				});
	
		Pagination();
		
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

	public void approvePacket(ActionEvent event) {

		RegistrationApprovalUiDto regData = table.getSelectionModel().getSelectedItem();

		if (registration.packetUpdateStatus(regData.getId(), "A", "mahesh123", "", "mahesh123")) {
			listData = registration.getAllEnrollments();
			generateAlert("Status",AlertType.INFORMATION,"Registration Approved successfully..");
			Pagination();
		}else {
			generateAlert("Status",AlertType.INFORMATION,"");
		}

	}

	void Pagination() {
		listData = registration.getAllEnrollments();
		if(listData.size() != 0) {
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
		
	}
	
	public void rejectPacket(ActionEvent event) {
		RegistrationApprovalUiDto regData = table.getSelectionModel().getSelectedItem();

		Stage primarystage = new Stage();
		try {
			AnchorPane rejectRoot = BaseController.load(getClass().getResource("/fxml/RejectionComment.fxml"));
			RejectionController rejectionController = (RejectionController)RegistrationAppInitialization.applicationContext.getBean("rejectionController");

			rejectionController.initData(regData.getId(),primarystage);
			Scene scene = new Scene(rejectRoot);
			primarystage.setScene(scene);
			primarystage.show();
			primarystage.resizableProperty().set(false);
			Pagination();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void onHoldPacket(ActionEvent event) {
		RegistrationApprovalUiDto regData = table.getSelectionModel().getSelectedItem();
		Stage primarystage = new Stage();
		try {
			AnchorPane holdRoot = BaseController.load(getClass().getResource("/fxml/OnholdComment.fxml"));
			OnHoldController onHoldController = (OnHoldController)RegistrationAppInitialization.applicationContext.getBean("onHoldController");

			onHoldController.initData(regData.getId(),primarystage);
			Scene scene = new Scene(holdRoot);
			primarystage.setScene(scene);
			primarystage.show();
			primarystage.resizableProperty().set(false);
			Pagination();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
