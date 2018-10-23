package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

import java.io.File;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.registration.dto.PacketUploadStatusDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.PacketUploadService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

@Controller
public class PacketUploadController extends BaseController {

	@FXML
	private TableColumn<PacketUploadStatusDTO, String> fileNameColumn;

	@FXML
	private TableColumn<PacketUploadStatusDTO, String> uploadStatusColumn;

	@FXML
	private TableView<PacketUploadStatusDTO> table;

	@Autowired
	private PacketUploadService packetUploadService;

	/** Object for Logger. */
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

	/**
	 * Validate the Username, Password and Packet Path
	 * 
	 * @param event
	 */
	public void validate(ActionEvent event) {
		LOGGER.debug("REGISTRATION - VALIDATE_USER_INPUT_DETAILS - PACKET_UPLOAD_CONTROLLER",
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID),
				"Validating the user input details");

		Map<String, String> statusMap = new HashMap<>();
		try {
			statusMap = handleUpload();
			if (!statusMap.isEmpty()) {
				generateAlert("INFO", AlertType.INFORMATION, "Packets Uploaded Successfully");
				displayData(populateTableData(statusMap));
			}
		} catch (RegBaseCheckedException regBaseCheckedException) {
			generateAlert("Error", Alert.AlertType.ERROR, regBaseCheckedException.getErrorText());
		}
	}

	/**
	 * To display the Uploaded packet details in UI
	 * 
	 * @param tableData
	 */
	private void displayData(List<PacketUploadStatusDTO> tableData) {
		LOGGER.debug("REGISTRATION - DISPLAY_DATA - PACKET_UPLOAD_CONTROLLER", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "To display all the ui data");
		fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
		uploadStatusColumn.setCellValueFactory(new PropertyValueFactory<>("uploadStatus"));

		ObservableList<PacketUploadStatusDTO> list = FXCollections.observableArrayList(tableData);
		table.setItems(list);
	}

	/**
	 * All the Packet upload functionalities are done here
	 * 
	 * @param packetUploadDto
	 * @return
	 * @throws IDISBaseCheckedException
	 */
	private Map<String, String> handleUpload() throws RegBaseCheckedException {
		LOGGER.debug("REGISTRATION - HANDLE_PACKET_UPLOAD - PACKET_UPLOAD_CONTROLLER",
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID),
				"Handling all the packet upload activities");
		List<Registration> synchedPackets = packetUploadService.getSynchedPackets();
		Map<String, String> uploadStatusMap = new HashMap<String, String>();
		if (!synchedPackets.isEmpty()) {
			for (Registration synchedPacket : synchedPackets) {
				String ackFileName = synchedPacket.getAckFilename();
				int lastIndex = ackFileName.indexOf("_Ack");
				String packetPath = ackFileName.substring(0, lastIndex);
				File packet = new File(packetPath + ".zip");
				String[] packetName = packet.getName().split("\\.");
				try {
					Object response = packetUploadService.pushPacket(packet);
					String responseCode = response.toString();
					if (responseCode.equals("PACKET_UPLOADED_TO_LANDING_ZONE")) {
						uploadStatusMap.put(packetName[0], "P");
					}

				} catch (URISyntaxException e) {
					e.printStackTrace();
				} catch (RegBaseCheckedException e) {
					uploadStatusMap.put(packetName[0], "E");
				}
			}
			packetUploadService.updateStatus(uploadStatusMap);
		} else {
			generateAlert("INFO", Alert.AlertType.INFORMATION, "No packets to upload");
		}

		return uploadStatusMap;

	}

	/**
	 * To populate the data for the UI table
	 * 
	 * @param verifiedPackets
	 * @return
	 */
	private List<PacketUploadStatusDTO> populateTableData(Map<String, String> packetStatus) {
		LOGGER.debug("REGISTRATION - POPULATE_UI_TABLE_DATA - PACKET_UPLOAD_CONTROLLER",
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID),
				"Populating the table data with the Updated details");
		List<PacketUploadStatusDTO> listUploadStatus = new ArrayList<>();
		PacketUploadStatusDTO packetUploadStatusDTO;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		String date = simpleDateFormat.format(new Date());
		for (Map.Entry<String, String> status : packetStatus.entrySet()) {
			packetUploadStatusDTO = new PacketUploadStatusDTO();
			if (status.getValue().equals("P")) {
				packetUploadStatusDTO.setUploadStatus("Uploaded");
			} else {
				packetUploadStatusDTO.setUploadStatus("Error");
			}
			packetUploadStatusDTO.setUploadTime(date);
			packetUploadStatusDTO.setFileName(status.getKey());
			listUploadStatus.add(packetUploadStatusDTO);
		}
		return listUploadStatus;
	}

}