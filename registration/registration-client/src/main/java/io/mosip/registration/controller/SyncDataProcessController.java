package io.mosip.registration.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.controller.vo.SyncDataProccessVO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.SyncDataProcessDTO;
import io.mosip.registration.service.config.JobConfigurationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.AnchorPane;

import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn;

/**
 * Sync Data Process contrmoller
 * 
 * @author YASWANTH S
 *
 * @since 1.0.0
 */
@Controller
public class SyncDataProcessController extends BaseController implements Initializable {
	@FXML
	private Label syncDataLabelId;

	@FXML
	private AnchorPane syncDataPaneId;
	@FXML
	private AnchorPane syncDataActionsPaneId;
	@FXML
	private Button syncDataRunningButton;
	@FXML
	private Button syncDataLastCompletedButton;
	@FXML
	private Button syncDataHistoryButton;
	@FXML
	private AnchorPane syncDataTableViewPaneId;

	/** Sync Data Process Table View */
	@FXML
	private TableView<SyncDataProccessVO> syncDataTableViewId;
	@FXML
	private TableColumn<SyncDataProccessVO, String> syncDataJobId;
	@FXML
	private TableColumn<SyncDataProccessVO, String> syncDataJobNameId;
	@FXML
	private TableColumn<SyncDataProccessVO, String> syncDataStatusId;
	@FXML
	private TableColumn<SyncDataProccessVO, String> syncDataLastUpdTimesId;

	@Autowired
	private JobConfigurationService jobConfigurationService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		syncDataTableViewId.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		/** Assign DTO values to Table view */
		syncDataJobId.setCellValueFactory(new PropertyValueFactory<SyncDataProccessVO, String>("jobId"));
		syncDataJobNameId.setCellValueFactory(new PropertyValueFactory<SyncDataProccessVO, String>("jobName"));
		syncDataStatusId.setCellValueFactory(new PropertyValueFactory<SyncDataProccessVO, String>("jobStatus"));
		syncDataLastUpdTimesId
				.setCellValueFactory(new PropertyValueFactory<SyncDataProccessVO, String>("lastUpdatedTimes"));

	}

	// Event Listener on Button[#syncDataRunningButton].onAction
	@FXML
	public void getRunningJobs(ActionEvent event) {

		ResponseDTO responseDTO = jobConfigurationService.getCurrentRunningJobDetails();

		if (responseDTO.getSuccessResponseDTO() != null) {
			@SuppressWarnings("unchecked")
			List<SyncDataProcessDTO> dataProcessDTOs = (List<SyncDataProcessDTO>) responseDTO.getSuccessResponseDTO()
					.getOtherAttributes().get(RegistrationConstants.SYNC_DATA_DTO);

			final ObservableList<SyncDataProccessVO> syncDataProcessDTOsObservableList = FXCollections
					.observableArrayList(convertDtoToVO(dataProcessDTOs));

			syncDataTableViewId.setItems(syncDataProcessDTOsObservableList);
		} else if (responseDTO.getErrorResponseDTOs() != null) {

			ErrorResponseDTO errorresponse = responseDTO.getErrorResponseDTOs().get(0);
			generateAlertLanguageSpecific(errorresponse.getCode(), errorresponse.getMessage());

		}

	}

	private List<SyncDataProccessVO> convertDtoToVO(List<SyncDataProcessDTO> dataProcessDTOs) {
		List<SyncDataProccessVO> dataProccessVOs = new LinkedList<>();
		dataProcessDTOs.forEach(dto -> {
			dataProccessVOs.add(new SyncDataProccessVO(dto.getJobId(), dto.getJobName(), dto.getJobStatus(),
					dto.getLastUpdatedTimes()));
		});

		return dataProccessVOs;
	}

	// Event Listener on Button[#syncDataLastCompletedButton].onAction
	@FXML
	public void getLastCompletedJobs(ActionEvent event) {
		ResponseDTO responseDTO = jobConfigurationService.getLastCompletedSyncJobs();

		if (responseDTO.getSuccessResponseDTO() != null) {
			@SuppressWarnings("unchecked")
			List<SyncDataProcessDTO> dataProcessDTOs = (List<SyncDataProcessDTO>) responseDTO.getSuccessResponseDTO()
					.getOtherAttributes().get(RegistrationConstants.SYNC_DATA_DTO);

			final ObservableList<SyncDataProccessVO> syncDataProcessDTOsObservableList = FXCollections
					.observableArrayList(convertDtoToVO(dataProcessDTOs));

			syncDataTableViewId.setItems(syncDataProcessDTOsObservableList);
		} else if (responseDTO.getErrorResponseDTOs() != null) {
			ErrorResponseDTO errorResponseDTO = responseDTO.getErrorResponseDTOs().get(0);
			generateAlertLanguageSpecific(RegistrationConstants.ALERT_INFORMATION, errorResponseDTO.getMessage());
		}

	}

	// Event Listener on Button[#syncDataHistoryButton].onAction
	@FXML
	public void getJobsHistory(ActionEvent event) {
		ResponseDTO responseDTO = jobConfigurationService.getSyncJobsTransaction();

		if (responseDTO.getSuccessResponseDTO() != null) {
			@SuppressWarnings("unchecked")
			List<SyncDataProcessDTO> dataProcessDTOs = (List<SyncDataProcessDTO>) responseDTO.getSuccessResponseDTO()
					.getOtherAttributes().get(RegistrationConstants.SYNC_DATA_DTO);

			final ObservableList<SyncDataProccessVO> syncDataProcessDTOsObservableList = FXCollections
					.observableArrayList(convertDtoToVO(dataProcessDTOs));

			syncDataTableViewId.setItems(syncDataProcessDTOsObservableList);
		} else if (responseDTO.getErrorResponseDTOs() != null) {
			ErrorResponseDTO errorResponseDTO = responseDTO.getErrorResponseDTOs().get(0);
			generateAlertLanguageSpecific(RegistrationConstants.ALERT_INFORMATION, errorResponseDTO.getMessage());
		}
	}

	// Event Listener on Button[#startSyncDataButton].onAction
	@FXML
	public void start(ActionEvent event) {

		ResponseDTO responseDTO = jobConfigurationService.startScheduler();

		if (responseDTO.getErrorResponseDTOs() != null) {
			ErrorResponseDTO errorresponse = responseDTO.getErrorResponseDTOs().get(0);
			generateAlertLanguageSpecific(errorresponse.getCode(), errorresponse.getMessage());
		} else if (responseDTO.getSuccessResponseDTO() != null) {
			SuccessResponseDTO successResponseDTO = responseDTO.getSuccessResponseDTO();
			generateAlertLanguageSpecific(successResponseDTO.getCode(), successResponseDTO.getMessage());
		}

	}

	// Event Listener on Button[#stopSyncDataButton].onAction
	@FXML
	public void stop(ActionEvent event) {

		ResponseDTO responseDTO = jobConfigurationService.stopScheduler();

		if (responseDTO.getErrorResponseDTOs() != null) {
			ErrorResponseDTO errorresponse = responseDTO.getErrorResponseDTOs().get(0);
			generateAlertLanguageSpecific(errorresponse.getCode(), errorresponse.getMessage());
		} else if (responseDTO.getSuccessResponseDTO() != null) {
			SuccessResponseDTO successResponseDTO = responseDTO.getSuccessResponseDTO();
			generateAlertLanguageSpecific(successResponseDTO.getCode(), successResponseDTO.getMessage());
		}

	}

}
