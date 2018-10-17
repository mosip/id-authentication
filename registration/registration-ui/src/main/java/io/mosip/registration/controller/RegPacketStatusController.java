package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.registration.constants.RegConstants;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.RegPacketStatusDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.service.RegPacketStatusService;
import io.mosip.registration.ui.constants.RegistrationUIConstants;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

@Controller
public class RegPacketStatusController extends BaseController implements Initializable {

	/**
	 * Instance of {@link MosipLogger}
	 */
	private static MosipLogger LOGGER;

	/**
	 * Initialize logger.
	 *
	 * @param mosipRollingFileAppender
	 *            the mosip rolling file appender
	 */
	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	@Autowired
	RegPacketStatusService regPacketStatusService;

	@FXML
	TableView<RegPacketStatusDTO> table = new TableView<>();

	@FXML
	TableColumn<RegPacketStatusDTO, String> regID;

	@FXML
	TableColumn<RegPacketStatusDTO, String> syncStatus;

	/**
	 * Building Sync Data Screen after sync with the server
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.debug("REGISTRATION - PAGE_LOADING - REG_PACKET_STATUS_CONTROLLER", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "Page loading has been started");
		packetSyncStatus();
		LOGGER.debug("REGISTRATION - PAGE_LOADING - REG_PACKET_STATUS_CONTROLLER", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "Registration Packet status sync has been done");
	}

	/**
	 * This method is used to sync packet status with server
	 */
	@SuppressWarnings("unchecked")
	private void packetSyncStatus() {
		ResponseDTO response = regPacketStatusService.packetSyncStatus();
		if (response.getSuccessResponseDTO() != null) {
			List<LinkedHashMap<String, String>> registrations = (List<LinkedHashMap<String, String>>) response
					.getSuccessResponseDTO().getOtherAttributes().get(RegConstants.PACKET_STATUS_SYNC_RESPONSE_ENTITY);

			ObservableList<RegPacketStatusDTO> packetStatus = FXCollections.observableArrayList();
			for (LinkedHashMap<String, String> registration : registrations) {
				packetStatus.add(new RegPacketStatusDTO(registration.get(RegConstants.PACKET_STATUS_SYNC_REGISTRATION_ID),
								registration.get(RegConstants.PACKET_STATUS_SYNC_STATUS_CODE)));
			}

			regID.setCellValueFactory(new PropertyValueFactory<RegPacketStatusDTO, String>("packetId"));
			syncStatus.setCellValueFactory(new PropertyValueFactory<RegPacketStatusDTO, String>("status"));

			table.setItems(packetStatus);
		} else if (response.getErrorResponseDTOs() != null) {
			/** Generate Alert to show No Packets Available. */
			ErrorResponseDTO errorResponseDTO = response.getErrorResponseDTOs().get(0);
			generateAlert(RegistrationUIConstants.PACKET_STATUS_SYNC_ALERT_TITLE,
					AlertType.valueOf(errorResponseDTO.getCode()),
					RegistrationUIConstants.PACKET_STATUS_SYNC_INFO_MESSAGE, errorResponseDTO.getMessage());

		}

	}

}
