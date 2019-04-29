package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.AuditReferenceIdTypes;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.packet.PacketUploadService;
import io.mosip.registration.service.sync.PacketSynchService;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

@Controller
public class PacketUploadController extends BaseController implements Initializable {

	@FXML
	private ProgressIndicator progressIndicator;

	@Autowired
	private PacketUploadService packetUploadService;

	@FXML
	private TableColumn<PacketStatusDTO, String> fileNameColumn;

	@FXML
	private TableView<PacketStatusDTO> table;

	@FXML
	private TableColumn<PacketStatusDTO, Boolean> checkBoxColumn;

	@FXML
	private Button saveToDevice;

	@FXML
	private TableColumn<PacketStatusDTO, String> fileColumn;

	@FXML
	private TableColumn<PacketStatusDTO, String> statusColumn;

	@Autowired
	private PacketSynchService packetSynchService;

	@Autowired
	private PacketExportController packetExportController;

	@FXML
	private CheckBox selectAllCheckBox;

	private ObservableList<PacketStatusDTO> list;

	private List<PacketStatusDTO> selectedPackets = new ArrayList<>();

	private static final Logger LOGGER = AppConfig.getLogger(PacketUploadController.class);

	/**
	 * This method is used to Sync as well as upload the packets.
	 * 
	 */
	public void syncAndUploadPacket() {

		LOGGER.info("REGISTRATION - SYNCH_PACKETS_AND_PUSH_TO_SERVER - PACKET_UPLOAD_CONTROLLER", APPLICATION_NAME,
				APPLICATION_ID, "Sync the packets and push it to the server");
		table.getItems().clear();
		table.refresh();
		service.reset();
		try {
			if (RegistrationAppHealthCheckUtil.isNetworkAvailable()) {
				if (!selectedPackets.isEmpty()) {
					String packetSyncStatus = packetSynchService.packetSync(selectedPackets);

					auditFactory.audit(AuditEvent.UPLOAD_PACKET, Components.UPLOAD_PACKET,
							SessionContext.userContext().getUserId(),
							AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

					progressIndicator.progressProperty().bind(service.progressProperty());
					service.start();
					service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
						@Override
						public void handle(WorkerStateEvent t) {
							String status = service.getValue();
							if (!RegistrationConstants.EMPTY.equals(packetSyncStatus)) {
								generateAlert(RegistrationConstants.ERROR, status + " " + packetSyncStatus);
							} else if (!status.equals(RegistrationConstants.EMPTY)) {
								generateAlert(RegistrationConstants.ERROR, status);
							}
						}
					});
				} else {
					loadInitialPage();
					generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.PACKET_UPLOAD_EMPTY_ERROR);
				}
			} else {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.NETWORK_ERROR);
			}
		} catch (RegBaseCheckedException checkedException) {
			LOGGER.info("REGISTRATION - UPLOAD_ERROR - PACKET_UPLOAD_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
					checkedException.getMessage() + ExceptionUtils.getStackTrace(checkedException));
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.PACKET_UPLOAD_EMPTY_ERROR);
		}

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

					LOGGER.info("REGISTRATION - HANDLE_PACKET_UPLOAD_START - PACKET_UPLOAD_CONTROLLER",
							APPLICATION_NAME, APPLICATION_ID, "Handling all the packet upload activities");
					List<PacketStatusDTO> packetUploadList = new ArrayList<>();
					String status = "";
					Map<String, String> tableMap = new HashMap<>();
					if (!selectedPackets.isEmpty()) {
						auditFactory.audit(AuditEvent.PACKET_UPLOAD, Components.PACKET_UPLOAD,
								SessionContext.userContext().getUserId(), RegistrationConstants.PACKET_UPLOAD_REF_ID);

						progressIndicator.setVisible(true);
						for (int i = 0; i < selectedPackets.size(); i++) {
							PacketStatusDTO synchedPacket = selectedPackets.get(i);
							String ackFileName = synchedPacket.getPacketPath();
							int lastIndex = ackFileName.indexOf(RegistrationConstants.ACKNOWLEDGEMENT_FILE);
							String packetPath = ackFileName.substring(0, lastIndex);
							File packet = new File(packetPath + RegistrationConstants.ZIP_FILE_EXTENSION);
							try {
								if (packet.exists()) {
									ResponseDTO response = packetUploadService.pushPacket(packet);
									if (response.getSuccessResponseDTO() != null) {

										synchedPacket.setPacketClientStatus(
												RegistrationClientStatusCode.UPLOADED_SUCCESSFULLY.getCode());
										synchedPacket
												.setPacketServerStatus(response.getSuccessResponseDTO().getMessage());
										packetUploadList.add(synchedPacket);
										tableMap.put(synchedPacket.getFileName(),
												RegistrationUIConstants.PACKET_UPLOAD_SUCCESS);

									} else if (response.getErrorResponseDTOs() != null) {
										String errMessage = response.getErrorResponseDTOs().get(0).getMessage();
										if (errMessage.contains(RegistrationConstants.PACKET_DUPLICATE)) {

											tableMap.put(synchedPacket.getFileName(),
													RegistrationUIConstants.PACKET_UPLOAD_DUPLICATE);
											synchedPacket.setPacketClientStatus(
													RegistrationClientStatusCode.UPLOADED_SUCCESSFULLY.getCode());
											synchedPacket.setUploadStatus(
													RegistrationClientStatusCode.UPLOAD_SUCCESS_STATUS.getCode());
											packetUploadList.add(synchedPacket);

										} else {
											synchedPacket.setUploadStatus(
													RegistrationClientStatusCode.UPLOAD_ERROR_STATUS.getCode());
											packetUploadList.add(synchedPacket);
											tableMap.put(synchedPacket.getFileName(), RegistrationConstants.ERROR);
										}
									}

								} else {
									tableMap.put(synchedPacket.getFileName(),
											RegistrationUIConstants.PACKET_NOT_AVAILABLE);
								}

							} catch (URISyntaxException uriSyntaxException) {

								LOGGER.error("REGISTRATION - HANDLE_PACKET_UPLOAD_URI_ERROR - PACKET_UPLOAD_CONTROLLER",
										APPLICATION_NAME, APPLICATION_ID,
										"Error in uri syntax" + ExceptionUtils.getStackTrace(uriSyntaxException));
								status = RegistrationUIConstants.PACKET_UPLOAD_ERROR;
							} catch (RegBaseCheckedException regBaseCheckedException) {
								LOGGER.error("REGISTRATION - HANDLE_PACKET_UPLOAD_ERROR - PACKET_UPLOAD_CONTROLLER",
										APPLICATION_NAME, APPLICATION_ID, "Error while pushing packets to the server"
												+ ExceptionUtils.getStackTrace(regBaseCheckedException));

								synchedPacket
										.setUploadStatus(RegistrationClientStatusCode.UPLOAD_ERROR_STATUS.getCode());
								tableMap.put(synchedPacket.getFileName(),
										RegistrationUIConstants.PACKET_UPLOAD_SERVICE_ERROR);
								packetUploadList.add(synchedPacket);

							} catch (RuntimeException runtimeException) {
								LOGGER.error(
										"REGISTRATION - HANDLE_PACKET_UPLOAD_RUNTIME_ERROR - PACKET_UPLOAD_CONTROLLER",
										APPLICATION_NAME, APPLICATION_ID,
										"Run time error while connecting to the server"
												+ ExceptionUtils.getStackTrace(runtimeException));
								if (i == 0) {
									status = RegistrationUIConstants.PACKET_UPLOAD_ERROR;
								} else if (i > 0) {
									status = RegistrationUIConstants.PACKET_PARTIAL_UPLOAD_ERROR;
								}
								for (int count = i; count < selectedPackets.size(); count++) {
									synchedPacket = selectedPackets.get(count);
									synchedPacket.setUploadStatus(
											RegistrationClientStatusCode.UPLOAD_ERROR_STATUS.getCode());
									packetUploadList.add(synchedPacket);
									tableMap.put(synchedPacket.getFileName(), RegistrationConstants.ERROR);
								}
								break;
							}

							this.updateProgress(i, selectedPackets.size());
						}
						packetUploadService.updateStatus(packetUploadList);
						progressIndicator.setVisible(false);
						displayStatus(populateTableData(tableMap));
					} else {
						loadInitialPage();
						generateAlert(RegistrationConstants.ALERT_INFORMATION,
								RegistrationUIConstants.PACKET_UPLOAD_EMPTY);
					}
					selectedPackets.clear();
					return status;
				}
			};
		}
	};

	/**
	 * Export the packets and show the exported packets in the table
	 */
	public void packetExport() {

		LOGGER.info("REGISTRATION - PACKET_EXPORT_START - PACKET_UPLOAD_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Exporting the Synched the packets");

		List<PacketStatusDTO> exportedPackets = packetExportController.packetExport();
		Map<String, String> exportedPacketMap = new HashMap<>();
		exportedPackets.forEach(regPacket -> {
			exportedPacketMap.put(regPacket.getFileName(), RegistrationClientStatusCode.EXPORT.getCode());
		});
		if (!exportedPacketMap.isEmpty()) {
			displayStatus(populateTableData(exportedPacketMap));
		}
	}

	/**
	 * To display the Uploaded packet details in UI
	 * 
	 * @param tableData
	 */
	private void displayData(List<PacketStatusDTO> tableData) {

		LOGGER.info("REGISTRATION - DISPLAY_DATA - PACKET_UPLOAD_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"To display all the ui data");
		checkBoxColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
		fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));

		this.list = FXCollections.observableArrayList(new Callback<PacketStatusDTO, Observable[]>() {

			@Override
			public Observable[] call(PacketStatusDTO param) {
				return new Observable[] { param.selectedProperty() };
			}
		});
		list.addAll(tableData);
		checkBoxColumn
				.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {

					@Override
					public ObservableValue<Boolean> call(Integer param) {
						return list.get(param).selectedProperty();
					}
				}));
		list.addListener(new ListChangeListener<PacketStatusDTO>() {
			@Override
			public void onChanged(Change<? extends PacketStatusDTO> c) {
				while (c.next()) {
					if (c.wasUpdated()) {
						if (!selectedPackets.contains(list.get(c.getFrom()))) {
							selectedPackets.add(list.get(c.getFrom()));
						} else {
							selectedPackets.remove(list.get(c.getFrom()));
						}
						saveToDevice.setDisable(!selectedPackets.isEmpty());
					}
				}
			}
		});
		table.setItems(list);
		table.setEditable(true);

	}

	/**
	 * To populate the data for the UI table
	 * 
	 * @param verifiedPackets
	 * @return
	 */
	private List<PacketStatusDTO> populateTableData(Map<String, String> packetStatus) {
		LOGGER.info("REGISTRATION - POPULATE_UI_TABLE_DATA - PACKET_UPLOAD_CONTROLLER", APPLICATION_NAME,
				APPLICATION_ID, "Populating the table data with the Updated details");
		List<PacketStatusDTO> listUploadStatus = new ArrayList<>();
		packetStatus.forEach((id, status) -> {
			PacketStatusDTO packetUploadStatusDTO = new PacketStatusDTO();
			packetUploadStatusDTO.setFileName(id);
			packetUploadStatusDTO.setClientStatusComments(status);
			listUploadStatus.add(packetUploadStatusDTO);

		});
		return listUploadStatus;
	}

	private void loadInitialPage() {

		List<PacketStatusDTO> synchedPackets = packetSynchService.fetchPacketsToBeSynched();
		if (synchedPackets.isEmpty()) {
			selectAllCheckBox.setDisable(true);
		} else {
			displayData(synchedPackets);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		selectedPackets.clear();
		loadInitialPage();
		fileNameColumn.setResizable(false);
		checkBoxColumn.setResizable(false);
		fileColumn.setResizable(false);
		statusColumn.setResizable(false);
	}

	@SuppressWarnings("unchecked")
	private void displayStatus(List<PacketStatusDTO> filesToDisplay) {
		Platform.runLater(() -> {

			Stage stage = new Stage();

			stage.setTitle(RegistrationUIConstants.PACKET_UPLOAD_HEADER_NAME);
			stage.setWidth(500);
			stage.setHeight(500);

			TableView<PacketStatusDTO> statusTable = new TableView<>();
			TableColumn<PacketStatusDTO, String> fileNameCol = new TableColumn<>(
					RegistrationUIConstants.UPLOAD_COLUMN_HEADER_FILE);
			fileNameCol.setMinWidth(250);
			TableColumn<PacketStatusDTO, String> statusCol = new TableColumn<>(
					RegistrationUIConstants.UPLOAD_COLUMN_HEADER_STATUS);
			statusCol.setMinWidth(250);
			ObservableList<PacketStatusDTO> displayList = FXCollections.observableArrayList(filesToDisplay);
			statusTable.setItems(displayList);
			fileNameCol.setCellValueFactory(new PropertyValueFactory<>("fileName"));
			statusCol.setCellValueFactory(new PropertyValueFactory<>("clientStatusComments"));
			statusTable.getColumns().addAll(fileNameCol, statusCol);
			Scene scene = new Scene(new StackPane(statusTable), 800, 800);
			scene.getStylesheets().add(ClassLoader.getSystemClassLoader()
					.getResource(RegistrationConstants.CSS_FILE_PATH).toExternalForm());
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(fXComponents.getStage());
			stage.setResizable(false);
			stage.setScene(scene);
			stage.show();
			stage.setOnCloseRequest((e) -> {
				saveToDevice.setDisable(false);
				loadInitialPage();
			});
		});

	}

	public void selectAllCheckBox(ActionEvent e) {
		saveToDevice.setDisable(((CheckBox) e.getSource()).isSelected());
		list.forEach(item -> {
			item.setStatus(((CheckBox) e.getSource()).isSelected());
		});
	}
}