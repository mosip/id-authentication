package org.mosip.registration.controller;

import static org.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static org.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static org.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.mosip.kernel.logger.factory.MosipLogfactory;
import org.mosip.registration.dto.PacketUploadDTO;
import org.mosip.registration.dto.PacketUploadStatusDTO;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.service.PacketUploadService;
import org.mosip.registration.util.kernal.FTPUploadManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

@Controller
public class PacketUploadController extends BaseController {

	@FXML
	private TextField username;

	@FXML
	private TextField password;

	@FXML
	private TextField filepath;

	@FXML
	private TableColumn<PacketUploadStatusDTO, String> fileName;

	@FXML
	private TableColumn<PacketUploadStatusDTO, String> status;

	@FXML
	private TableColumn<PacketUploadStatusDTO, String> time;

	@FXML
	private TableView<PacketUploadStatusDTO> table;

	@FXML
	private ProgressBar progressBar;

	@Autowired
	private PacketUploadService packetUploadService;

	@Autowired
	private FTPUploadManager ftpUploadManager;

	@Value("${FTP_USER_ID}")
	private String ftpId;

	@Value("${FTP_PASSWORD}")
	private String ftpPassword;

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
		PacketUploadDTO packetdto = new PacketUploadDTO();

		if (ftpId.equals(username.getText()) && ftpPassword.equals(password.getText())) {
			if (filepath.getText() == null || filepath.getText().isEmpty()) {
				generateAlert("INFO", Alert.AlertType.INFORMATION, "Please select the packet folder");
				return;
			}
			packetdto.setUserid(username.getText());
			packetdto.setPassword(password.getText());
			packetdto.setFilepath(filepath.getText());
			List<File> verifiedPackets = new ArrayList<>();
			try {
				verifiedPackets = handleUpload(packetdto);
				if (!verifiedPackets.isEmpty()) {
					generateAlert("INFO", AlertType.INFORMATION, "Packets Uploaded Successfully");
					displayData(populateTableData(verifiedPackets));
				}
			} catch (RegBaseCheckedException regBaseCheckedException) {
				generateAlert("Error", Alert.AlertType.ERROR, regBaseCheckedException.getErrorText());
			}
		} else {
			generateAlert("INFO", Alert.AlertType.INFORMATION, "UserName and Password Mismatch");
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
		fileName.setCellValueFactory(new PropertyValueFactory<>("fileName"));
		status.setCellValueFactory(new PropertyValueFactory<>("uploadStatus"));
		time.setCellValueFactory(new PropertyValueFactory<>("uploadTime"));

		ObservableList<PacketUploadStatusDTO> list = FXCollections.observableArrayList(tableData);
		table.setItems(list);
	}

	/**
	 * To browse the packet source path in the UI
	 * 
	 * @param event
	 */
	public void browse(ActionEvent event) {
		LOGGER.debug("REGISTRATION - BROWSE_FOLDER - PACKET_UPLOAD_CONTROLLER",
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID),
				"TO select the packet location in the UI");
		final DirectoryChooser directoryChooser = new DirectoryChooser();
		Stage stage = new Stage();
		File dir = directoryChooser.showDialog(stage);
		if (dir != null) {
			filepath.setText(dir.getAbsolutePath());
		}
	}

	/**
	 * All the Packet upload functionalities are done here
	 * 
	 * @param packetUploadDto
	 * @return
	 * @throws IDISBaseCheckedException
	 */
	private List<File> handleUpload(PacketUploadDTO packetUploadDto) throws RegBaseCheckedException {
		LOGGER.debug("REGISTRATION - HANDLE_PACKET_UPLOAD - PACKET_UPLOAD_CONTROLLER",
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID),
				"Handling all the packet upload activities");
		Map<String, File> packetMap = new HashMap<>();
		List<String> packetNames = new ArrayList<>();
		List<File> verifiedPackets = new ArrayList<>();
		File packetPath = new File(packetUploadDto.getFilepath());
		File[] localPacketList = packetPath.listFiles();
		if (localPacketList.length > 0) {
			for (File packet : localPacketList) {

				if (packet.getName().endsWith(".zip")) {
					String[] packetName = packet.getName().split("\\.");
					packetNames.add(packetName[0]);
					packetMap.put(packetName[0], packet);
				}
			}
			verifiedPackets = packetUploadService.verifyPacket(packetNames, packetMap);
			if (verifiedPackets.isEmpty()) {
				String[] filePath = packetUploadDto.getFilepath().split("\\\\");
				ftpUploadManager.pushPacket(verifiedPackets, filePath[filePath.length - 1], packetUploadDto);
				packetUploadService.updateStatus(verifiedPackets);
			} else {
				generateAlert("INFO", Alert.AlertType.INFORMATION, "No files needs to upload");
			}
		} else {
			generateAlert("INFO", Alert.AlertType.INFORMATION, "No files Present in the selected folder");
		}
		return verifiedPackets;
	}

	/**
	 * To populate the data for the UI table
	 * 
	 * @param verifiedPackets
	 * @return
	 */
	private List<PacketUploadStatusDTO> populateTableData(List<File> verifiedPackets) {
		LOGGER.debug("REGISTRATION - POPULATE_UI_TABLE_DATA - PACKET_UPLOAD_CONTROLLER",
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID),
				"Populating the table data with the Updated details");
		List<PacketUploadStatusDTO> listUploadStatus = new ArrayList<>();
		PacketUploadStatusDTO packetUploadStatusDTO;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		String date = simpleDateFormat.format(new Date());
		for (File f : verifiedPackets) {
			packetUploadStatusDTO = new PacketUploadStatusDTO();
			packetUploadStatusDTO.setUploadStatus("Uploaded");
			packetUploadStatusDTO.setUploadTime(date);
			packetUploadStatusDTO.setFileName(f.getName());
			listUploadStatus.add(packetUploadStatusDTO);
		}
		return listUploadStatus;
	}

}