package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.LoggerConstants.LOG_PACKET_UPLOAD;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.AuditReferenceIdTypes;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.vo.PacketStatusVO;
import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.service.packet.PacketUploadService;
import io.mosip.registration.service.sync.PacketSynchService;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
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
	private TableView<PacketStatusVO> table;

	@FXML
	private TableColumn<PacketStatusVO, Boolean> checkBoxColumn;

	@FXML
	private TableColumn<PacketStatusVO, Boolean> regDate;

	@FXML
	private TableColumn<PacketStatusVO, Boolean> slno;

	@FXML
	private Button saveToDevice;

	@FXML
	private Button uploadBtn;

	@FXML
	private TextField filterField;

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

	@FXML
	private ImageView exportCSVIcon;

	private ObservableList<PacketStatusVO> list;

	private List<PacketStatusVO> selectedPackets = new ArrayList<>();

	private static final Logger LOGGER = AppConfig.getLogger(PacketUploadController.class);

	private ObservableList<PacketStatusVO> observableList;

	private SortedList<PacketStatusVO> sortedList;

	/**
	 * This method is used to Sync as well as upload the packets.
	 * 
	 */
	public void syncAndUploadPacket() {

		LOGGER.info("REGISTRATION - SYNC_PACKETS_AND_PUSH_TO_SERVER - PACKET_UPLOAD_CONTROLLER", APPLICATION_NAME,
				APPLICATION_ID, "Sync the packets and push it to the server");
		observableList.clear();
		table.refresh();
		service.reset();
		try {
			if (RegistrationAppHealthCheckUtil.isNetworkAvailable()) {
				if (!selectedPackets.isEmpty()) {
					List<PacketStatusDTO> packetsToBeSynced = new ArrayList<>();
					selectedPackets.forEach(packet -> {
						if (packet.getPacketServerStatus() == null
								|| !packet.getPacketServerStatus()
										.equalsIgnoreCase(RegistrationConstants.SERVER_STATUS_RESEND)
								|| !RegistrationClientStatusCode.META_INFO_SYN_SERVER.getCode()
										.equalsIgnoreCase(packet.getPacketClientStatus())) {
							PacketStatusDTO packetStatusVO = new PacketStatusDTO();
							packetStatusVO.setClientStatusComments(packet.getClientStatusComments());
							packetStatusVO.setFileName(packet.getFileName());
							packetStatusVO.setPacketClientStatus(packet.getPacketClientStatus());
							packetStatusVO.setPacketPath(packet.getPacketPath());
							packetStatusVO.setPacketServerStatus(packet.getPacketServerStatus());
							packetStatusVO.setPacketStatus(packet.getPacketStatus());
							packetStatusVO.setUploadStatus(packet.getUploadStatus());
							packetStatusVO.setSupervisorStatus(packet.getSupervisorStatus());
							packetStatusVO.setSupervisorComments(packet.getSupervisorComments());

							try (FileInputStream fis = new FileInputStream(new File(
									packet.getPacketPath().replace(RegistrationConstants.ACKNOWLEDGEMENT_FILE_EXTENSION,
											RegistrationConstants.ZIP_FILE_EXTENSION)))) {
								byte[] byteArray = new byte[(int) fis.available()];
								fis.read(byteArray);
								byte[] packetHash = HMACUtils.generateHash(byteArray);
								packetStatusVO.setPacketHash(HMACUtils.digestAsPlainText(packetHash));
								packetStatusVO.setPacketSize(BigInteger.valueOf(byteArray.length));

							} catch (IOException ioException) {
								LOGGER.error("REGISTRATION_BASE_SERVICE", APPLICATION_NAME, APPLICATION_ID,
										ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));
							}
							packetsToBeSynced.add(packetStatusVO);
						}
					});
					if (!packetsToBeSynced.isEmpty()) {
						String packetSyncStatus = packetSynchService.packetSync(packetsToBeSynced);
						if (!RegistrationConstants.EMPTY.equals(packetSyncStatus)) {
							generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.SYNC_FAILURE);
						}
					}
					auditFactory.audit(AuditEvent.UPLOAD_PACKET, Components.UPLOAD_PACKET,
							SessionContext.userContext().getUserId(),
							AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

					progressIndicator.progressProperty().bind(service.progressProperty());
					service.start();
					service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
						@Override
						public void handle(WorkerStateEvent t) {
							String status = service.getValue();
							if (!status.equals(RegistrationConstants.EMPTY)) {
								generateAlert(RegistrationConstants.ERROR, status);
							}
						}
					});
				} else {
					loadInitialPage();
					generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.PACKET_UPLOAD_EMPTY_ERROR);
				}
			} else {
				loadInitialPage();
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
					List<PacketStatusVO> packetUploadList = new ArrayList<>();
					String status = "";

					Map<String, String> tableMap = new HashMap<>();
					if (!selectedPackets.isEmpty()) {
						auditFactory.audit(AuditEvent.PACKET_UPLOAD, Components.PACKET_UPLOAD,
								SessionContext.userContext().getUserId(), RegistrationConstants.PACKET_UPLOAD_REF_ID);

						progressIndicator.setVisible(true);
						for (int i = 0; i < selectedPackets.size(); i++) {
							PacketStatusVO synchedPacket = selectedPackets.get(i);
							if ((packetSynchService.fetchSynchedPacket(synchedPacket.getFileName())
									|| RegistrationConstants.SERVER_STATUS_RESEND
											.equalsIgnoreCase(synchedPacket.getPacketServerStatus()))
									&& !RegistrationConstants.PACKET_STATUS_CODE_REREGISTER
											.equalsIgnoreCase(synchedPacket.getPacketServerStatus())) {
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
											synchedPacket.setPacketServerStatus(
													response.getSuccessResponseDTO().getMessage());
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

									LOGGER.error(
											"REGISTRATION - HANDLE_PACKET_UPLOAD_URI_ERROR - PACKET_UPLOAD_CONTROLLER",
											APPLICATION_NAME, APPLICATION_ID,
											"Error in uri syntax" + ExceptionUtils.getStackTrace(uriSyntaxException));
									status = RegistrationUIConstants.PACKET_UPLOAD_ERROR;
								} catch (RegBaseCheckedException regBaseCheckedException) {
									LOGGER.error("REGISTRATION - HANDLE_PACKET_UPLOAD_ERROR - PACKET_UPLOAD_CONTROLLER",
											APPLICATION_NAME, APPLICATION_ID,
											"Error while pushing packets to the server"
													+ ExceptionUtils.getStackTrace(regBaseCheckedException));

									synchedPacket.setUploadStatus(
											RegistrationClientStatusCode.UPLOAD_ERROR_STATUS.getCode());
									if(regBaseCheckedException.getErrorCode().equals(RegistrationExceptionConstants.AUTH_ADVICE_USR_ERROR.getErrorCode())) {
										tableMap.put(synchedPacket.getFileName(),
												RegistrationUIConstants.AUTH_ADVICE_FAILURE);
									} else {
										tableMap.put(synchedPacket.getFileName(),
												RegistrationUIConstants.PACKET_UPLOAD_SERVICE_ERROR);
									}
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
						}
						List<PacketStatusDTO> packetsToBeExport = new ArrayList<>();
						packetUploadList.forEach(packet -> {
							PacketStatusDTO packetStatusDTO = new PacketStatusDTO();
							packetStatusDTO.setClientStatusComments(packet.getClientStatusComments());
							packetStatusDTO.setFileName(packet.getFileName());
							packetStatusDTO.setPacketClientStatus(packet.getPacketClientStatus());
							packetStatusDTO.setPacketPath(packet.getPacketPath());
							packetStatusDTO.setPacketServerStatus(packet.getPacketServerStatus());
							packetStatusDTO.setPacketStatus(packet.getPacketStatus());
							packetStatusDTO.setUploadStatus(packet.getUploadStatus());
							packetsToBeExport.add(packetStatusDTO);
						});
						packetUploadService.updateStatus(packetsToBeExport);
						progressIndicator.setVisible(false);
						if (!tableMap.isEmpty()) {
							displayStatus(populateTableData(tableMap));
						} else {
							loadInitialPage();
						}
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
		List<PacketStatusVO> packetsToBeExport = new ArrayList<>();
		exportedPackets.forEach(packet -> {
			PacketStatusVO packetStatusVO = new PacketStatusVO();
			packetStatusVO.setClientStatusComments(packet.getClientStatusComments());
			packetStatusVO.setFileName(packet.getFileName());
			packetStatusVO.setPacketClientStatus(packet.getPacketClientStatus());
			packetStatusVO.setPacketPath(packet.getPacketPath());
			packetStatusVO.setPacketServerStatus(packet.getPacketServerStatus());
			packetStatusVO.setPacketStatus(packet.getPacketStatus());
			packetStatusVO.setStatus(false);
			packetStatusVO.setUploadStatus(packet.getUploadStatus());
			packetsToBeExport.add(packetStatusVO);
		});
		Map<String, String> exportedPacketMap = new HashMap<>();
		packetsToBeExport.forEach(regPacket -> {
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
	private void displayData(List<PacketStatusVO> tableData) {

		LOGGER.info("REGISTRATION - DISPLAY_DATA - PACKET_UPLOAD_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"To display all the ui data");
		checkBoxColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
		fileNameColumn.setCellValueFactory(new PropertyValueFactory<>(RegistrationConstants.PACKET_UPLOAD_FILE));
		regDate.setCellValueFactory(new PropertyValueFactory<>(RegistrationConstants.PACKET_UPLOAD_DATE));
		slno.setCellValueFactory(new PropertyValueFactory<>(RegistrationConstants.PACKET_UPLOAD_SNO));

		this.list = FXCollections.observableArrayList(new Callback<PacketStatusVO, Observable[]>() {

			@Override
			public Observable[] call(PacketStatusVO param) {
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
		list.addListener(new ListChangeListener<PacketStatusVO>() {
			@Override
			public void onChanged(Change<? extends PacketStatusVO> c) {
				while (c.next()) {
					if (c.wasUpdated()) {
						if (!selectedPackets.contains(table.getItems().get(c.getFrom()))) {
							selectedPackets.add(table.getItems().get(c.getFrom()));
						} else {
							selectedPackets.remove(table.getItems().get(c.getFrom()));
						}
						saveToDevice.setDisable(!selectedPackets.isEmpty());
					}
				}
			}
		});
		// 1. Wrap the ObservableList in a FilteredList (initially display all data).
		observableList = FXCollections.observableArrayList(list);

		wrapListAndAddFiltering();

		table.setItems(sortedList);
		table.setEditable(true);
	}

	private void wrapListAndAddFiltering() {
		FilteredList<PacketStatusVO> filteredList = new FilteredList<>(observableList, p -> true);

		// 2. Set the filter Predicate whenever the filter changes.
		filterField.textProperty().addListener((observable, oldValue, newValue) -> {
			filterData(newValue, filteredList);
		});

		if (!filterField.getText().isEmpty()) {
			filterData(filterField.getText(), filteredList);
		}

		// 3. Wrap the FilteredList in a SortedList.
		sortedList = new SortedList<>(filteredList);

		// 4. Bind the SortedList comparator to the TableView comparator.
		sortedList.comparatorProperty().bind(table.comparatorProperty());
	}

	private void filterData(String newValue, FilteredList<PacketStatusVO> filteredList) {
		filteredList.setPredicate(reg -> {
			// If filter text is empty, display all ID's.
			if (newValue == null || newValue.isEmpty()) {
				return true;
			}

			// Compare every ID with filter text.
			String lowerCaseFilter = newValue.toLowerCase();

			if (reg.getFileName().contains(lowerCaseFilter)) {
				// Filter matches first name.
				table.getSelectionModel().selectFirst();
				return true;
			}
			return false; // Does not match.
		});
		table.getSelectionModel().selectFirst();
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
		exportCSVIcon.setDisable(synchedPackets.isEmpty());
		filterField.setDisable(synchedPackets.isEmpty());
		table.setDisable(synchedPackets.isEmpty());
		saveToDevice.setVisible(!synchedPackets.isEmpty());
		uploadBtn.setVisible(!synchedPackets.isEmpty());

		List<PacketStatusVO> packetsToBeExport = new ArrayList<>();
		int count = 1;
		for (PacketStatusDTO packet : synchedPackets) {
			PacketStatusVO packetStatusVO = new PacketStatusVO();
			packetStatusVO.setClientStatusComments(packet.getClientStatusComments());
			packetStatusVO.setFileName(packet.getFileName());
			packetStatusVO.setPacketClientStatus(packet.getPacketClientStatus());
			packetStatusVO.setPacketPath(packet.getPacketPath());
			packetStatusVO.setPacketServerStatus(packet.getPacketServerStatus());
			packetStatusVO.setPacketStatus(packet.getPacketStatus());
			packetStatusVO.setStatus(false);
			packetStatusVO.setUploadStatus(packet.getUploadStatus());
			packetStatusVO.setSupervisorStatus(packet.getSupervisorStatus());
			packetStatusVO.setSupervisorComments(packet.getSupervisorComments());
			packetStatusVO.setCreatedTime(packet.getCreatedTime());
			packetStatusVO.setSlno(String.valueOf(count++));
			packetsToBeExport.add(packetStatusVO);
		}
		if (packetsToBeExport.isEmpty()) {
			selectAllCheckBox.setDisable(true);
		} else {
			displayData(packetsToBeExport);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		selectedPackets.clear();
		loadInitialPage();
		fileNameColumn.setResizable(false);
		checkBoxColumn.setResizable(false);
		regDate.setResizable(false);
		
		disableColumnsReorder(table);
		// fileColumn.setResizable(false);
		// statusColumn.setResizable(false);
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
			fileNameCol.getStyleClass().add("tableId");
			TableColumn<PacketStatusDTO, String> statusCol = new TableColumn<>(
					RegistrationUIConstants.UPLOAD_COLUMN_HEADER_STATUS);
			statusCol.setMinWidth(250);

			statusCol.getStyleClass().add("tableId");
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
			stage.getIcons().add(new Image(getClass().getResource(RegistrationConstants.LOGO).toExternalForm()));
			stage.show();
			stage.setOnCloseRequest((e) -> {
				saveToDevice.setDisable(false);
				loadInitialPage();
			});
		});

	}

	public void exportData() {
		LOGGER.info(LOG_PACKET_UPLOAD, APPLICATION_NAME, APPLICATION_ID, "Exporting the packet upload status details");
		String str = filterField.getText();
		Stage stage = new Stage();
		DirectoryChooser destinationSelector = new DirectoryChooser();
		destinationSelector.setTitle(RegistrationConstants.FILE_EXPLORER_NAME);
		Path currentRelativePath = Paths.get("");
		File defaultDirectory = new File(currentRelativePath.toAbsolutePath().toString());
		destinationSelector.setInitialDirectory(defaultDirectory);
		File destinationPath = destinationSelector.showDialog(stage);
		if (destinationPath != null) {

			filterField.clear();
			String fileData = table.getItems().stream()
					.map(packetVo -> packetVo.getSlno().trim().concat(RegistrationConstants.COMMA).concat("'")
							.concat(packetVo.getFileName()).concat("'").concat(RegistrationConstants.COMMA).concat("'")
							.concat(packetVo.getCreatedTime()).concat("'"))
					.collect(Collectors.joining(RegistrationConstants.NEW_LINE));
			String headers = RegistrationUIConstants.EOD_SLNO_LABEL.concat(RegistrationConstants.COMMA)
					.concat(RegistrationUIConstants.PACKETUPLOAD_PACKETID_LABEL).concat(RegistrationConstants.COMMA)
					.concat(RegistrationUIConstants.EOD_REGISTRATIONDATE_LABEL).concat(RegistrationConstants.COMMA)
					.concat(RegistrationConstants.NEW_LINE);
			fileData = headers + fileData;
			filterField.setText(str);
			try (Writer writer = new BufferedWriter(new FileWriter(destinationPath + "/"
					+ RegistrationConstants.UPLOAD_FILE_NAME.concat(RegistrationConstants.UNDER_SCORE)
							.concat(getcurrentTimeStamp()).concat(RegistrationConstants.EXPORT_FILE_TYPE)))) {
				writer.write(fileData);

				generateAlert(RegistrationConstants.ALERT_INFORMATION,
						RegistrationUIConstants.EOD_DETAILS_EXPORT_SUCCESS);

			} catch (IOException ioException) {
				LOGGER.error(LOG_PACKET_UPLOAD, APPLICATION_NAME, APPLICATION_ID,
						ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));

				generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationUIConstants.PACKET_STATUS_EXPORT);

			}
		}
		LOGGER.info(LOG_PACKET_UPLOAD, APPLICATION_NAME, APPLICATION_ID,
				"Exporting Packet Upload status details has been ended");
	}

	public void selectAllCheckBox(ActionEvent e) {
		saveToDevice.setDisable(((CheckBox) e.getSource()).isSelected());
		list.forEach(item -> {
			item.setStatus(((CheckBox) e.getSource()).isSelected());
		});
	}

	/**
	 * This method gets the current timestamp in yyyymmddhhmmss format.
	 * 
	 * @return current timestamp in fourteen digits
	 */
	private String getcurrentTimeStamp() {
		DateTimeFormatter format = DateTimeFormatter.ofPattern(RegistrationConstants.EOD_PROCESS_DATE_FORMAT_FOR_FILE);
		return LocalDateTime.now().format(format);
	}
}