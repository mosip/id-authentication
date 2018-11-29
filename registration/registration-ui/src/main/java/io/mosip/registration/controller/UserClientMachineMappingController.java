package io.mosip.registration.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.UserMachineMappingDTO;
import io.mosip.registration.service.mapping.MapMachineService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

@Controller
public class UserClientMachineMappingController extends BaseController implements Initializable {
	@FXML
	private AnchorPane onBoardRoot;
	@FXML
	private TableView<UserMachineMappingDTO> mapTable;
	@FXML
	private TableColumn<UserMachineMappingDTO, String> usernameColumn;
	@FXML
	private TableColumn<UserMachineMappingDTO, String> userIDColumn;
	@FXML
	private TableColumn<UserMachineMappingDTO, String> roleColumn;
	@FXML
	private TableColumn<UserMachineMappingDTO, String> statusColumn;
	@FXML
	private Label userClientMachineMappingLabel;
	@FXML
	private AnchorPane userClientMachineMappingEditPane;
	@FXML
	private Label bioDetails;
	@FXML
	private Button update;
	@FXML
	private AnchorPane fingerprintDetailsPane;
	@FXML
	private AnchorPane irisDetailsPane;
	@FXML
	private ComboBox<String> statusComboBox;
	@FXML
	private Label userClientMachineMappingEditDetailsLabel;
	@FXML
	private Label stationIdResult;
	@Autowired
	MapMachineService mapMachineService;

	UserMachineMappingDTO mappingDTO = null;

	public ObservableList<String> statusList = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		statusList = FXCollections.observableArrayList(RegistrationConstants.MACHINE_MAPPING_ACTIVE,
				RegistrationConstants.MACHINE_MAPPING_IN_ACTIVE);
		usernameColumn.setCellValueFactory(new PropertyValueFactory<UserMachineMappingDTO, String>("userName"));
		userIDColumn.setCellValueFactory(new PropertyValueFactory<UserMachineMappingDTO, String>("userID"));
		roleColumn.setCellValueFactory(new PropertyValueFactory<UserMachineMappingDTO, String>("role"));
		statusColumn.setCellValueFactory(new PropertyValueFactory<UserMachineMappingDTO, String>("status"));
		view();
		statusComboBox.setItems(statusList);

	}

	/**
	 * Get all the users in specified center for On-Boarding process
	 */
	private void view() {
		ResponseDTO responseDTO = mapMachineService.view();
		if (responseDTO != null && responseDTO.getSuccessResponseDTO() != null) {
			final ObservableList<UserMachineMappingDTO> observableList = FXCollections.observableArrayList();
			observableList.addAll((List<UserMachineMappingDTO>) responseDTO.getSuccessResponseDTO().getOtherAttributes()
					.get(RegistrationConstants.USER_MACHINE_MAPID));
			mapTable.setItems(observableList);
		} else if (responseDTO != null && responseDTO.getErrorResponseDTOs() != null
				&& responseDTO.getErrorResponseDTOs().get(0) != null) {
			/* Get error response */
			ErrorResponseDTO errorResponseDTO = responseDTO.getErrorResponseDTOs().get(0);
			/* Generate Alert */
			generateAlert(RegistrationConstants.ALERT_ERROR, errorResponseDTO.getMessage());
		}
	}

	/**
	 * Get specified user details from On boarding user table
	 * 
	 * @param mouseEvent mouse click
	 */
	@FXML
	public void showSelectedUserDetails(MouseEvent mouseEvent) {
		mappingDTO = mapTable.getSelectionModel().getSelectedItem();
		if (mappingDTO != null) {
			statusComboBox.getSelectionModel().clearSelection();
			userClientMachineMappingEditPane.setVisible(true);
			bioDetails.setText(RegistrationConstants.ONBOARD_BIOMETRICS+mappingDTO.getUserName());
			stationIdResult.setText(mappingDTO.getStationID());
			update.setDisable(true);

			/* Making dynamic status Box by disabling active/In-Active*/
			statusComboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
				@Override
				public ListCell<String> call(ListView<String> param) {
					return new ListCell<String>() {
						@Override
						protected void updateItem(String item, boolean empty) {
							super.updateItem(item, empty);
							if (item != null || !empty) {
								if (item != null && item.equalsIgnoreCase(mappingDTO.getStatus())) {
									this.setText(item);
									this.setDisable(true);
									this.setTextFill(Color.GREY);
								}

								else {
									this.setText(item);
									this.setDisable(false);

								}
							}

						}
					};
				}
			});

		}
	}

	/* Event Listener on Button.onAction*/
	/**
	 * change status (ACTIVE/IN-ACTIVE)
	 * 
	 * @param event action event
	 */
	@FXML
	public void changeStatus(ActionEvent event) {
		if (statusComboBox.getSelectionModel().getSelectedItem() != null) {
			// Enable update button
			update.setDisable(false);
		}
	}

	/* Event Listener on Button[#update].onAction*/
	/**
	 * update user details
	 * 
	 * @param event is action event
	 */
	@FXML
	public void updateUserMapDetails(ActionEvent event) {
		final String changedStatus = statusComboBox.getSelectionModel().getSelectedItem();
		mappingDTO.setStatus(changedStatus);
		// Send to service layer and get response
		ResponseDTO responseDTO = mapMachineService.saveOrUpdate(mappingDTO);

		if (responseDTO != null && responseDTO.getSuccessResponseDTO() != null) {
			// Get Selected Column
			UserMachineMappingDTO mappingDTODummy = mapTable.getSelectionModel().getSelectedItem();
			mappingDTODummy.setStatus((changedStatus.equalsIgnoreCase(RegistrationConstants.MACHINE_MAPPING_ACTIVE)
					? RegistrationConstants.MACHINE_MAPPING_ACTIVE
					: RegistrationConstants.MACHINE_MAPPING_IN_ACTIVE));
			SuccessResponseDTO successResponseDTO = responseDTO.getSuccessResponseDTO();
			generateAlert(RegistrationConstants.ALERT_INFORMATION, successResponseDTO.getMessage());
			userClientMachineMappingEditPane.setVisible(false);
			statusComboBox.getSelectionModel().clearSelection();
			view();

		} else if (responseDTO != null && responseDTO.getErrorResponseDTOs() != null
				&& responseDTO.getErrorResponseDTOs().get(0) != null) {
			/* clear status selection*/
			statusComboBox.getSelectionModel().clearSelection();

			/* Get error response*/
			ErrorResponseDTO errorResponseDTO = responseDTO.getErrorResponseDTOs().get(0);

			/* Generate Alert*/
			generateAlert(RegistrationConstants.ALERT_ERROR, errorResponseDTO.getMessage());
		}

	}
}