package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.File;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.exception.JsonProcessingException;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationExceptions;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.dto.SyncRegistrationDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.packet.PacketUploadService;
import io.mosip.registration.service.sync.PacketSynchService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

@Controller
public class PacketUploadController extends BaseController {

	@FXML
	private TableColumn<PacketStatusDTO, String> fileNameColumn;

	@FXML
	private TableColumn<PacketStatusDTO, String> uploadStatusColumn;

	@FXML
	private TableView<PacketStatusDTO> table;

	@FXML
	private ProgressIndicator progressIndicator;

	@Autowired
	private PacketUploadService packetUploadService;

	@Autowired
	private PacketSynchService packetSynchService;

	private static final Logger LOGGER = AppConfig.getLogger(PacketUploadController.class);

	/**
	 * This method is used to Sync as well as upload the packets.
	 * 
	 */
	public void syncAndUploadPacket() {
		LOGGER.debug("REGISTRATION - SYNCH_PACKETS_AND_PUSH_TO_SERVER - PACKET_UPLOAD_CONTROLLER", APPLICATION_NAME,
				APPLICATION_ID, "Sync the packets and push it to the server");
		table.getItems().clear();
		table.refresh();
		service.reset();
		try {
			String packetSyncStatus = packetSync();
			progressIndicator.progressProperty().bind(service.progressProperty());
			service.start();
			service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent t) {
					String status = service.getValue();
					if (!status.equals(RegistrationConstants.EMPTY)) {
						String[] displayStatus = status.split("-");
						if (RegistrationConstants.PACKET_SYNC_ERROR.equals(displayStatus[0])) {
							if (!RegistrationConstants.EMPTY.equals(packetSyncStatus)) {
								generateAlert(displayStatus[0], AlertType.ERROR,
										displayStatus[1] + " " + packetSyncStatus);
							} else {
								generateAlert(displayStatus[0], AlertType.ERROR, displayStatus[1]);
							}
						} else {
							generateAlert(displayStatus[0], AlertType.INFORMATION, displayStatus[1]);
						}

					}
				}
			});
		} catch (RegBaseCheckedException checkedException) {
			generateAlert(RegistrationConstants.PACKET_SYNC_ERROR, AlertType.ERROR, checkedException.getErrorText());
		}

	}

	/**
	 * This method is used to synch the local packets with the server
	 * 
	 * @throws RegBaseCheckedException
	 * 
	 */
	private String packetSync() throws RegBaseCheckedException {
		LOGGER.debug("REGISTRATION - SYNCH_PACKETS_TO_SERVER - PACKET_UPLOAD_CONTROLLER", APPLICATION_NAME,
				APPLICATION_ID, "Sync the packets to the server");
		String syncErrorStatus = "";
		try {
			auditFactory.audit(AuditEvent.SYNC_SERVER, Components.PACKET_SYNC, "Sync the packets status to the server",
					SessionContext.getInstance().getUserContext().getUserId(),
					RegistrationConstants.PACKET_SYNC_REF_ID);
			List<Registration> packetsToBeSynched = packetSynchService.fetchPacketsToBeSynched();
			List<SyncRegistrationDTO> syncDtoList = new ArrayList<>();
			Object response = null;
			if (!packetsToBeSynched.isEmpty()) {
				for (Registration packetToBeSynch : packetsToBeSynched) {
					SyncRegistrationDTO syncDto = new SyncRegistrationDTO();
					syncDto.setLangCode("ENG");
					syncDto.setStatusComment(packetToBeSynch.getClientStatusCode() + " " + "-" + " "
							+ packetToBeSynch.getClientStatusComments());
					syncDto.setRegistrationId(packetToBeSynch.getId());
					syncDto.setParentRegistrationId(packetToBeSynch.getId());
					syncDto.setSyncStatus(RegistrationConstants.PACKET_STATUS_PRE_SYNC);
					syncDto.setSyncType(RegistrationConstants.PACKET_STATUS_SYNC_TYPE);
					syncDtoList.add(syncDto);
				}
				response = packetSynchService.syncPacketsToServer(syncDtoList);
			}
			if (response != null) {
				packetSynchService.updateSyncStatus(packetsToBeSynched);
			}
		} catch (RegBaseUncheckedException | RegBaseCheckedException | JsonProcessingException | URISyntaxException e) {
			LOGGER.error("REGISTRATION - SYNCH_PACKETS_TO_SERVER - PACKET_UPLOAD_CONTROLLER", APPLICATION_NAME,
					APPLICATION_ID, "Error while Synching packets to the server");
			if (e instanceof RegBaseUncheckedException) {

				throw new RegBaseCheckedException(RegistrationExceptions.REG_PACKET_SYNC_EXCEPTION.getErrorCode(),
						RegistrationExceptions.REG_PACKET_SYNC_EXCEPTION.getErrorMessage());
			} else {
				syncErrorStatus = e.getMessage();
			}
		}
		return syncErrorStatus;
	}

	/**
	 * To display the Uploaded packet details in UI
	 * 
	 * @param tableData
	 */
	private void displayData(List<PacketStatusDTO> tableData) {
		LOGGER.debug("REGISTRATION - DISPLAY_DATA - PACKET_UPLOAD_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"To display all the ui data");
		fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
		uploadStatusColumn.setCellValueFactory(new PropertyValueFactory<>("uploadStatus"));

		ObservableList<PacketStatusDTO> list = FXCollections.observableArrayList(tableData);
		table.setItems(list);
	}

	/**
	 * To populate the data for the UI table
	 * 
	 * @param verifiedPackets
	 * @return
	 */
	private List<PacketStatusDTO> populateTableData(List<Registration> packetStatus) {
		LOGGER.debug("REGISTRATION - POPULATE_UI_TABLE_DATA - PACKET_UPLOAD_CONTROLLER", APPLICATION_NAME,
				APPLICATION_ID, "Populating the table data with the Updated details");
		List<PacketStatusDTO> listUploadStatus = new ArrayList<>();
		PacketStatusDTO packetUploadStatusDTO;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		String date = simpleDateFormat.format(new Date());
		for (Registration registrationPacket : packetStatus) {
			packetUploadStatusDTO = new PacketStatusDTO();
			if (RegistrationClientStatusCode.UPLOADED_SUCCESSFULLY.getCode()
					.equals(registrationPacket.getClientStatusCode())) {
				packetUploadStatusDTO.setUploadStatus("Uploaded");
			} else {
				packetUploadStatusDTO.setUploadStatus("Error");
			}
			packetUploadStatusDTO.setUploadTime(date);
			packetUploadStatusDTO.setFileName(registrationPacket.getId());
			listUploadStatus.add(packetUploadStatusDTO);
		}
		return listUploadStatus;
	}

	/**
	 * This anonymous service class will do the packet upload as well as the upload
	 * progress.
	 * 
	 */
	Service<String> service = new Service<String>() {
		@Override
		protected Task<String> createTask() {
			return /**
					 * @author SaravanaKumar
					 *
					 */
			new Task<String>() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see javafx.concurrent.Task#call()
				 */
				@Override
				protected String call() {

					LOGGER.debug("REGISTRATION - HANDLE_PACKET_UPLOAD_START - PACKET_UPLOAD_CONTROLLER",
							APPLICATION_NAME, APPLICATION_ID, "Handling all the packet upload activities");
					List<Registration> synchedPackets = packetUploadService.getSynchedPackets();
					List<Registration> packetUploadList = new ArrayList<>();
					String status = "";
					if (!synchedPackets.isEmpty()) {
						auditFactory.audit(AuditEvent.PACKET_UPLOAD, Components.PACKET_UPLOAD,
								"Upload packets to the server",
								SessionContext.getInstance().getUserContext().getUserId(),
								RegistrationConstants.PACKET_UPLOAD_REF_ID);
						progressIndicator.setVisible(true);
						for (int i = 0; i < synchedPackets.size(); i++) {
							Registration synchedPacket = synchedPackets.get(i);
							synchedPacket.setUploadCount((short) (synchedPacket.getUploadCount() + 1));
							String ackFileName = synchedPacket.getAckFilename();
							int lastIndex = ackFileName.indexOf(RegistrationConstants.ACKNOWLEDGEMENT_FILE);
							String packetPath = ackFileName.substring(0, lastIndex);
							File packet = new File(packetPath + RegistrationConstants.ZIP_FILE_EXTENSION);
							try {
								if (("resend".equals(synchedPacket.getServerStatusCode()) && synchedPacket
										.getServerStatusTimestamp().compareTo(synchedPacket.getUploadTimestamp()) == 1)
										|| RegistrationClientStatusCode.META_INFO_SYN_SERVER.getCode()
												.equals(synchedPacket.getClientStatusCode())

										|| "E".equals(synchedPacket.getFileUploadStatus())) {
									Object response = packetUploadService.pushPacket(packet);
									String responseCode = response.toString();
									if (responseCode.equals("PACKET_UPLOADED_TO_LANDING_ZONE")) {
										synchedPacket.setClientStatusCode(
												RegistrationClientStatusCode.UPLOADED_SUCCESSFULLY.getCode());
										synchedPacket.setFileUploadStatus(
												RegistrationClientStatusCode.UPLOAD_SUCCESS_STATUS.getCode());

									} else {
										synchedPacket.setFileUploadStatus(
												RegistrationClientStatusCode.UPLOAD_ERROR_STATUS.getCode());
									}
								}

							} catch (URISyntaxException e) {

								LOGGER.error("REGISTRATION - HANDLE_PACKET_UPLOAD_URI_ERROR - PACKET_UPLOAD_CONTROLLER",
										APPLICATION_NAME, APPLICATION_ID, "Error in uri syntax");
								status = "Error-Unable to push packets to the server.";
							} catch (RegBaseCheckedException e) {
								LOGGER.error("REGISTRATION - HANDLE_PACKET_UPLOAD_ERROR - PACKET_UPLOAD_CONTROLLER",
										APPLICATION_NAME, APPLICATION_ID, "Error while pushing packets to the server");
								synchedPacket.setFileUploadStatus(
										RegistrationClientStatusCode.UPLOAD_ERROR_STATUS.getCode());
								synchedPacket.setUploadCount((short) (synchedPacket.getUploadCount() + 1));
								packetUploadList.add(synchedPacket);
							} catch (RuntimeException e) {
								LOGGER.error(
										"REGISTRATION - HANDLE_PACKET_UPLOAD_RUNTIME_ERROR - PACKET_UPLOAD_CONTROLLER",
										APPLICATION_NAME, APPLICATION_ID,
										"Run time error while connecting to the server");
								if (i == 0) {
									status = "Error-Unable to push packets to the server.";
								} else if (i > 0) {
									status = "Error-Unable to push some packets to the server.";
								}
								for (int count = i; count < synchedPackets.size(); count++) {
									synchedPacket = synchedPackets.get(count);
									synchedPacket.setFileUploadStatus(
											RegistrationClientStatusCode.UPLOAD_ERROR_STATUS.getCode());
									synchedPacket.setUploadCount((short) (synchedPacket.getUploadCount() + 1));
									packetUploadList.add(synchedPacket);
								}
								break;
							}
								packetUploadList.add(synchedPacket);
							this.updateProgress(i, synchedPackets.size());
						}
						packetUploadService.updateStatus(packetUploadList);
						progressIndicator.setVisible(false);
						displayData(populateTableData(packetUploadList));
					} else {
						status = "Info-No packets to upload.";
					}

					return status;
				}
			};
		}
	};
}