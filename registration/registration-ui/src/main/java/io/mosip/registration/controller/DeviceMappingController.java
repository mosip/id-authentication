package io.mosip.registration.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AppModule;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.DeviceDTO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.service.MapMachineService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.LoggerConstants.DEVICE_ONBOARD_CONTROLLER;
import static io.mosip.registration.constants.RegistrationConstants.DEVICE_ONBOARD_EXCEPTION_ALERT;
import static io.mosip.registration.constants.RegistrationConstants.DEVICE_ONBOARD_ERROR_MSG;

/**
 * The controller class for Device-Onboarding.
 * <p>
 * On UI page load, the list of device categories will be fetched from database.
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Controller
public class DeviceMappingController extends BaseController implements Initializable {

	@FXML
	private AnchorPane onBoardRoot;
	@FXML
	private TextField searchField;
	@FXML
	private ComboBox<String> deviceTypes;
	@FXML
	private TableView<DeviceDTO> availableDevices;
	@FXML
	private TableColumn<DeviceDTO, String> availableDeviceName;
	@FXML
	private TableColumn<DeviceDTO, String> availableDeviceModel;
	@FXML
	private TableColumn<DeviceDTO, String> availableDeviceSerial;
	@FXML
	private TableColumn<DeviceDTO, String> mappedDeviceName;
	@FXML
	private TableColumn<DeviceDTO, String> mappedDeviceModel;
	@FXML
	private TableColumn<DeviceDTO, String> mappedDeviceSerial;
	@FXML
	private TableView<DeviceDTO> mappedDevices;
	@FXML
	private Button submitOnboardDevices;
	@FXML
	private ImageView mapDevice;
	@FXML
	private ImageView unmapDevice;
	private static final Logger LOGGER = AppConfig.getLogger(DeviceMappingController.class);
	@Autowired
	private MapMachineService mapMachineService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
				"Initializing Device On-boarding Page");

		try {
			auditFactory.audit(AuditEvent.GET_ONBOARDING_DEVICES_TYPES, AppModule.DEVICE_ONBOARD,
					"Get the types of onboarding devices", SessionContext.getInstance().getUserContext().getUserId(),
					RegistrationConstants.ONBOARD_DEVICES_REF_ID_TYPE);

			// Set Machine ID
			SessionContext.getInstance().getMapObject().put(RegistrationConstants.MACHINE_ID, "1947");
			
			// Set the Device Types
			deviceTypes.getItems().addAll(FXCollections.observableArrayList(mapMachineService.getAllDeviceTypes()));

			// Set the CellValueFactory attribute of TableView
			availableDeviceName
					.setCellValueFactory(new PropertyValueFactory<>(RegistrationConstants.DEVICE_MANUFACTURER_NAME));
			availableDeviceModel
					.setCellValueFactory(new PropertyValueFactory<>(RegistrationConstants.DEVICE_MODEL_NAME));
			availableDeviceSerial
					.setCellValueFactory(new PropertyValueFactory<>(RegistrationConstants.DEVICE_SERIAL_NO));
			mappedDeviceName
					.setCellValueFactory(new PropertyValueFactory<>(RegistrationConstants.DEVICE_MANUFACTURER_NAME));
			mappedDeviceModel.setCellValueFactory(new PropertyValueFactory<>(RegistrationConstants.DEVICE_MODEL_NAME));
			mappedDeviceSerial.setCellValueFactory(new PropertyValueFactory<>(RegistrationConstants.DEVICE_SERIAL_NO));

			// Set selection Mode to multiple for Available Devices and Mapped Devices
			// Tables
			availableDevices.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			mappedDevices.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

			// Bind Event Handlers to Search Device Button
			searchField.textProperty().addListener((observable, oldValue, newValue) -> {
				Map<String, List<DeviceDTO>> devicesMap = (Map<String, List<DeviceDTO>>) SessionContext.getInstance()
						.getMapObject().get(RegistrationConstants.ONBOARD_DEVICES_MAP);
				if (devicesMap != null) {
					if (RegistrationConstants.EMPTY.equals(newValue)) {
						populateDevices(filterDevices(devicesMap.get(RegistrationConstants.ONBOARD_AVAILABLE_DEVICES)),
								mappedDevices.getItems());
					} else if (newValue.length() < oldValue.length()) {
						populateDevices(filterDevices(devicesMap.get(RegistrationConstants.ONBOARD_AVAILABLE_DEVICES)),
								mappedDevices.getItems());
					} else {
						populateDevices(filterDevices(availableDevices.getItems()), mappedDevices.getItems());
					}
				}
			});

			deviceTypes.valueProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue != null) {
					// Get the All Available and Mapped Devices from SessionContext
					Map<String, List<DeviceDTO>> existingDevicesMap = (Map<String, List<DeviceDTO>>) SessionContext
							.getInstance().getMapObject().get(RegistrationConstants.ONBOARD_DEVICES_MAP);
					List<DeviceDTO> updatedAvailableDevices = existingDevicesMap
							.get(RegistrationConstants.ONBOARD_AVAILABLE_DEVICES);
					List<DeviceDTO> updatedMappedDevices = existingDevicesMap
							.get(RegistrationConstants.ONBOARD_MAPPED_DEVICES);
					List<DeviceDTO> devicesAdded = new ArrayList<>((List<DeviceDTO>) SessionContext.getInstance()
							.getMapObject().get(RegistrationConstants.ONBOARD_DEVICES_ADDED));
					List<DeviceDTO> devicesRemoved = new ArrayList<>((List<DeviceDTO>) SessionContext.getInstance()
							.getMapObject().get(RegistrationConstants.ONBOARD_DEVICES_REMOVED));

					if (oldValue != null) {
						devicesAdded = devicesAdded.parallelStream()
								.filter(deviceDTO -> deviceDTO.getDeviceType().equals(oldValue))
								.collect(Collectors.toList());
						devicesRemoved = devicesRemoved.parallelStream()
								.filter(deviceDTO -> deviceDTO.getDeviceType().equals(oldValue))
								.collect(Collectors.toList());
					}

					// Add the Added Devices
					updatedMappedDevices.addAll(devicesAdded);
					// Remove the Removed Devices
					updatedMappedDevices.removeAll(devicesRemoved);

					// Remove the Added Devices
					updatedAvailableDevices.removeAll(devicesAdded);
					// Add the Removed Devices
					updatedAvailableDevices.addAll(devicesRemoved);

					// Remove the duplicates
					updatedAvailableDevices = updatedAvailableDevices.parallelStream().distinct()
							.collect(Collectors.toList());
					updatedMappedDevices = updatedMappedDevices.parallelStream().distinct()
							.collect(Collectors.toList());

					// Create Device Map for Updated Device Onboarding
					Map<String, List<DeviceDTO>> devicesMap = new HashMap<>();
					devicesMap.put(RegistrationConstants.ONBOARD_AVAILABLE_DEVICES, updatedAvailableDevices);
					devicesMap.put(RegistrationConstants.ONBOARD_MAPPED_DEVICES, updatedMappedDevices);

					// If no device type was selected previously, grouping the devices based on
					// device types. Else update the Available Grouped Devices and Mapped Grouped
					// Devices in Session Context
					if (oldValue == null) {
						createDeviceGroupMap(devicesMap);
					} else {
						Map<String, List<DeviceDTO>> groupedDevices = (Map<String, List<DeviceDTO>>) SessionContext
								.getInstance().getMapObject()
								.get(RegistrationConstants.ONBOARD_AVAILABLE_DEVICES_GROUP);
						groupedDevices.replace(oldValue, updatedAvailableDevices);

						groupedDevices = (Map<String, List<DeviceDTO>>) SessionContext.getInstance().getMapObject()
								.get(RegistrationConstants.ONBOARD_MAPPED_DEVICES_GROUP);
						groupedDevices.replace(oldValue, updatedMappedDevices);
					}

					// Display the devices based on Selected Device Type
					loadDevices(newValue);
				}
			});

			// Display the all the available and mapped devices
			displayDevices();
		} catch (RuntimeException runtimeException) {
			LOGGER.error(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.DEVICE_ONBOARD_INITIALIZATION_EXCEPTION + "-> Exception while initializing:"
							+ runtimeException.getMessage());

			generateAlert(DEVICE_ONBOARD_EXCEPTION_ALERT, AlertType.ERROR, DEVICE_ONBOARD_ERROR_MSG);
		} finally {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Device Onboarding page initialization completed");
		}
	}

	private void displayDevices() {
		try {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Fetching and displaying all available and mapped devices from Service and UI");

			auditFactory.audit(AuditEvent.GET_ONBOARDING_DEVICES, AppModule.DEVICE_ONBOARD,
					"Get all the available and mapped devices",
					SessionContext.getInstance().getUserContext().getUserId(),
					RegistrationConstants.ONBOARD_DEVICES_REF_ID_TYPE);

			// Create a map of list based on device type
			Map<String, List<DeviceDTO>> devicesMap = mapMachineService.getDeviceMappingList(SessionContext
					.getInstance().getUserContext().getRegistrationCenterDetailDTO().getRegistrationCenterId(),
					(String) SessionContext.getInstance().getMapObject().get(RegistrationConstants.MACHINE_ID));

			// If Available Devices or Mapped Devices or both not available, add new
			// ArrayList
			devicesMap.putIfAbsent(RegistrationConstants.ONBOARD_AVAILABLE_DEVICES, new ArrayList<>());
			devicesMap.putIfAbsent(RegistrationConstants.ONBOARD_MAPPED_DEVICES, new ArrayList<>());

			// Show the Devices in UI
			populateDevices(devicesMap.get(RegistrationConstants.ONBOARD_AVAILABLE_DEVICES),
					devicesMap.get(RegistrationConstants.ONBOARD_MAPPED_DEVICES));

			// Add the DevicesMap to SessionContext object
			SessionContext.getInstance().getMapObject().put(RegistrationConstants.ONBOARD_DEVICES_MAP, devicesMap);
			SessionContext.getInstance().getMapObject().put(RegistrationConstants.ONBOARD_DEVICES_ADDED,
					new ArrayList<DeviceDTO>());
			SessionContext.getInstance().getMapObject().put(RegistrationConstants.ONBOARD_DEVICES_REMOVED,
					new ArrayList<DeviceDTO>());
		} catch (RuntimeException runtimeException) {
			LOGGER.error(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.DEVICE_ONBOARD_DEVICE_FETCHING_EXCEPTION
							+ "-> Exception while fetching or displaying devices from Service and UI:"
							+ runtimeException.getMessage());

			generateAlert(DEVICE_ONBOARD_EXCEPTION_ALERT, AlertType.ERROR, DEVICE_ONBOARD_ERROR_MSG);
		} finally {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Fetching and displaying all available and mapped devices from Service and UI completed");
		}
	}

	@SuppressWarnings("unchecked")
	private void loadDevices(String selectedDeviceType) {
		try {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Loading list of available and mapped " + selectedDeviceType + " devices");

			// Reset the Search TextField
			searchField.setText(RegistrationConstants.EMPTY);

			auditFactory.audit(AuditEvent.GET_ONBOARDING_DEVICES, AppModule.DEVICE_ONBOARD,
					"Get the available and mapped devices for ".concat(selectedDeviceType),
					SessionContext.getInstance().getUserContext().getUserId(),
					RegistrationConstants.ONBOARD_DEVICES_REF_ID_TYPE);

			// Get the list of Available and Mapped Devices for selected Device Type
			List<DeviceDTO> availableDeviceDTOs = ((Map<String, List<DeviceDTO>>) SessionContext.getInstance()
					.getMapObject().get(RegistrationConstants.ONBOARD_AVAILABLE_DEVICES_GROUP)).get(selectedDeviceType);

			List<DeviceDTO> mappedDeviceDTOs = ((Map<String, List<DeviceDTO>>) SessionContext.getInstance()
					.getMapObject().get(RegistrationConstants.ONBOARD_MAPPED_DEVICES_GROUP)).get(selectedDeviceType);

			Map<String, List<DeviceDTO>> displayedDevicesMap = new HashMap<>();
			displayedDevicesMap.put(RegistrationConstants.ONBOARD_AVAILABLE_DEVICES, availableDeviceDTOs);
			displayedDevicesMap.put(RegistrationConstants.ONBOARD_MAPPED_DEVICES, mappedDeviceDTOs);
			SessionContext.getInstance().getMapObject().put(RegistrationConstants.ONBOARD_DEVICES_MAP,
					displayedDevicesMap);

			// Populate the Available Devices and Mapped Devices Tables
			populateDevices(availableDeviceDTOs, mappedDeviceDTOs);
		} catch (RuntimeException runtimeException) {
			LOGGER.error(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.DEVICE_ONBOARD_LOADING_DEVICES_EXCEPTION
							+ "-> Exception while loading devices based on selected device type:"
							+ runtimeException.getMessage());

			generateAlert(DEVICE_ONBOARD_EXCEPTION_ALERT, AlertType.ERROR, DEVICE_ONBOARD_ERROR_MSG);
		} finally {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Loading list of available and mapped " + selectedDeviceType + " devices completed");
		}
	}

	private void populateDevices(List<DeviceDTO> availableDevices, List<DeviceDTO> mappedDevices) {
		try {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Populating devices in UI");
			// Set the Available Devices
			this.availableDevices.setItems(FXCollections
					.observableArrayList(availableDevices == null ? Collections.emptyList() : availableDevices));

			// Set the Mapped Devices
			this.mappedDevices.setItems(
					FXCollections.observableArrayList(mappedDevices == null ? Collections.emptyList() : mappedDevices));
		} catch (RuntimeException runtimeException) {
			LOGGER.error(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.DEVICE_ONBOARD_DEVICE_POPULATION_EXCEPTION
							+ "-> Exception while populating devices in UI: " + runtimeException.getMessage());

			generateAlert(DEVICE_ONBOARD_EXCEPTION_ALERT, AlertType.ERROR, DEVICE_ONBOARD_ERROR_MSG);
		} finally {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Populating devices in UI completed");
		}
	}

	/**
	 * Maps the selected available devices to the mapped devices.
	 * <p>
	 * The selected devices in Available Devices table will be removed and will be
	 * displayed in the Mapped Devices table
	 * 
	 * @param mouseEvent
	 *            the {@link MouseEvent} object
	 */
	@SuppressWarnings("unchecked")
	@FXML
	private void mapAvailableDevices(MouseEvent mouseEvent) {
		LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Mapping selected devices");

		try {
			List<DeviceDTO> devicesAdded = mapDevices(availableDevices, mappedDevices);

			// Update the Devices added in Session Context
			List<DeviceDTO> deviceMaster = (List<DeviceDTO>) SessionContext.getInstance().getMapObject()
					.get(RegistrationConstants.ONBOARD_DEVICES_ADDED);
			devicesAdded.removeAll(deviceMaster);
			deviceMaster.addAll(devicesAdded);

			// Update the Devices removed in Session Context
			deviceMaster = (List<DeviceDTO>) SessionContext.getInstance().getMapObject()
					.get(RegistrationConstants.ONBOARD_DEVICES_REMOVED);
			deviceMaster.removeAll(devicesAdded);
		} catch (RuntimeException runtimeException) {
			LOGGER.error(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.DEVICE_ONBOARD_MAPPING_DEVICES_EXCEPTION
							+ "-> Exception while mapping devices:" + runtimeException.getMessage());

			generateAlert(DEVICE_ONBOARD_EXCEPTION_ALERT, AlertType.ERROR, DEVICE_ONBOARD_ERROR_MSG);
		} finally {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Mapping of selected devices completed");
		}
	}

	/**
	 * Unmaps the selected devices from the mapped devices.
	 * <p>
	 * The selected devices will be removed from the Mapped Devices Table and the
	 * same will be added to the Available Devices Table.
	 * 
	 * @param mouseEvent
	 *            the {@link MouseEvent} object
	 */
	@SuppressWarnings("unchecked")
	@FXML
	private void unmapMappedDevices(MouseEvent mouseEvent) {
		LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Unmapping selected devices");

		try {
			List<DeviceDTO> devicesRemoved = mapDevices(mappedDevices, availableDevices);

			// Update the Devices Removed in Session Context
			List<DeviceDTO> deviceMaster = (List<DeviceDTO>) SessionContext.getInstance().getMapObject()
					.get(RegistrationConstants.ONBOARD_DEVICES_REMOVED);
			devicesRemoved.removeAll(deviceMaster);
			deviceMaster.addAll(devicesRemoved);

			// Update the Devices Added in Session Context
			deviceMaster = (List<DeviceDTO>) SessionContext.getInstance().getMapObject()
					.get(RegistrationConstants.ONBOARD_DEVICES_ADDED);
			deviceMaster.removeAll(devicesRemoved);
		} catch (RuntimeException runtimeException) {
			LOGGER.error(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.DEVICE_ONBOARD_UNMAPPING_DEVICES_EXCEPTION
							+ "-> Exception while unmapping devices:" + runtimeException.getMessage());

			generateAlert(DEVICE_ONBOARD_EXCEPTION_ALERT, AlertType.ERROR, DEVICE_ONBOARD_ERROR_MSG);
		} finally {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Unmapping of selected devices completed");
		}
	}

	private List<DeviceDTO> mapDevices(TableView<DeviceDTO> source, TableView<DeviceDTO> destination) {
		List<DeviceDTO> selectedDevices = null;
		try {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Mapping of devices");

			// Get the selected devices
			selectedDevices = new ArrayList<>(source.getSelectionModel().getSelectedItems());

			// Check the selected devices. If no devices are selected, mapping is not
			// required
			if (!selectedDevices.isEmpty()) {

				// Add the selected devices to the destination
				destination.getItems().addAll(selectedDevices);

				// Remove the selected devices from the source
				source.getItems().removeAll(selectedDevices);

				// Clear Selection - If not cleared, either the next or before item will be
				// selected by default
				source.getSelectionModel().clearSelection();
			}
		} catch (RuntimeException runtimeException) {
			LOGGER.error(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.DEVICE_ONBOARD_DEVICE_GROUPING_EXCEPTION
							+ "-> Exception while mapping of devices: " + runtimeException.getMessage());

			generateAlert(DEVICE_ONBOARD_EXCEPTION_ALERT, AlertType.ERROR, DEVICE_ONBOARD_ERROR_MSG);
		} finally {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Mapping of devices completed");
		}

		return selectedDevices;
	}

	/**
	 * Navigates to Registration Home Page
	 */
	@FXML
	private void goToHome() {
		LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
				"Navigating to Registration Home Page");
		try {
			clearDeviceOnboardSessionContext();

			// Redirect to home page
			BaseController.load(getClass().getResource(RegistrationConstants.HOME_PAGE));
		} catch (IOException ioException) {
			LOGGER.error(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.DEVICE_ONBOARD_HOME_NAVIGATION_EXCEPTION
							+ "-> Exception while navigating to Home page:" + ioException.getMessage());

			generateAlert(DEVICE_ONBOARD_EXCEPTION_ALERT, AlertType.ERROR, DEVICE_ONBOARD_ERROR_MSG);
		} finally {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Navigation to Registration Home page completed");
		}
	}

	/**
	 * Submit the updated mapping of Onboarding Devices for Registration Machine
	 * 
	 * @param actionEvent
	 *            the {@link ActionEvent} object
	 */
	@SuppressWarnings("unchecked")
	@FXML
	private void submit(ActionEvent actionEvent) {
		LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
				"Updating the mapping of onboarding devices for Registration Machine");

		try {
			auditFactory.audit(AuditEvent.UPDATE_DEVICES_ONBOARDING, AppModule.DEVICE_ONBOARD,
					"Updating mapping of devices", SessionContext.getInstance().getUserContext().getUserId(),
					RegistrationConstants.ONBOARD_DEVICES_REF_ID_TYPE);

			// Get the list of devices added and removed
			List<DeviceDTO> devicesAdded = (List<DeviceDTO>) SessionContext.getInstance().getMapObject()
					.get(RegistrationConstants.ONBOARD_DEVICES_ADDED);
			List<DeviceDTO> devicesRemoved = (List<DeviceDTO>) SessionContext.getInstance().getMapObject()
					.get(RegistrationConstants.ONBOARD_DEVICES_REMOVED);
			
			// Get the Machine ID
			String machineId = (String) SessionContext.getInstance().getMapObject().get(RegistrationConstants.MACHINE_ID);
			
			// Update the Machine ID
			devicesAdded.forEach(deviceDTO -> deviceDTO.setMachineId(machineId));
			
			// Update Devices Mapping
			ResponseDTO responseDTO = mapMachineService.updateMappedDevice(devicesRemoved, devicesAdded);
			
			if (responseDTO.getSuccessResponseDTO() != null) {
				generateAlert(AlertType.INFORMATION, responseDTO.getSuccessResponseDTO().getMessage(), responseDTO.getSuccessResponseDTO().getCode());
			} else {
				ErrorResponseDTO errorResponseDTO = responseDTO.getErrorResponseDTOs().get(0);
				generateAlert(AlertType.ERROR, errorResponseDTO.getMessage(), errorResponseDTO.getCode());
			}
			
			deviceTypes.getSelectionModel().clearSelection();
			clearDeviceOnboardSessionContext();
			displayDevices();
		} catch (RuntimeException runtimeException) {
			LOGGER.error(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.DEVICE_ONBOARD_HOME_NAVIGATION_EXCEPTION
							+ "-> Exception while updating the mapping of onboarding devices :"
							+ runtimeException.getMessage());

			generateAlert(DEVICE_ONBOARD_EXCEPTION_ALERT, AlertType.ERROR, DEVICE_ONBOARD_ERROR_MSG);
		} finally {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Mapping of onboarding devices for Registration Machine completed");
		}
	}

	private void createDeviceGroupMap(Map<String, List<DeviceDTO>> devicesMap) {
		try {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Grouping of devices based of device types");

			// Fetch the Available and Mapped Devices
			List<DeviceDTO> availableDeviceDTOs = devicesMap.get(RegistrationConstants.ONBOARD_AVAILABLE_DEVICES);
			List<DeviceDTO> mappedDevicesDTOs = devicesMap.get(RegistrationConstants.ONBOARD_MAPPED_DEVICES);

			// Group the Available Devices based on Device Type
			Map<String, List<DeviceDTO>> deviceGroupMap = new HashMap<>();

			if (availableDeviceDTOs != null) {
				deviceGroupMap = availableDeviceDTOs.parallelStream()
						.collect(Collectors.groupingBy(DeviceDTO::getDeviceType));
			}
			deviceGroupMap.put("All", availableDeviceDTOs);
			updateDeviceGroupMap(deviceGroupMap);

			// Add the Grouped Available Devices to the SessionContext
			SessionContext.getInstance().getMapObject().put(RegistrationConstants.ONBOARD_AVAILABLE_DEVICES_GROUP,
					deviceGroupMap);

			// Group the Mapped Devices based on Device Type
			deviceGroupMap = new HashMap<>();

			if (mappedDevicesDTOs != null) {
				deviceGroupMap = mappedDevicesDTOs.parallelStream()
						.collect(Collectors.groupingBy(DeviceDTO::getDeviceType));
			}
			deviceGroupMap.put("All", mappedDevicesDTOs);
			updateDeviceGroupMap(deviceGroupMap);

			// Add the Grouped Mapped Devices to the SessionContext
			SessionContext.getInstance().getMapObject().put(RegistrationConstants.ONBOARD_MAPPED_DEVICES_GROUP,
					deviceGroupMap);

			// Populate the Available Devices and Mapped Devices Tables
			populateDevices(availableDeviceDTOs, mappedDevicesDTOs);
		} catch (RuntimeException runtimeException) {
			LOGGER.error(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.DEVICE_ONBOARD_DEVICE_GROUPING_EXCEPTION
							+ "-> Exception while grouping of devices based of device types :"
							+ runtimeException.getMessage());

			generateAlert(DEVICE_ONBOARD_EXCEPTION_ALERT, AlertType.ERROR, DEVICE_ONBOARD_ERROR_MSG);
		} finally {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Grouping of devices based of device types completed");
		}
	}

	private void updateDeviceGroupMap(Map<String, List<DeviceDTO>> deviceGroupMap) {
		try {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Updating the deviceGroupMap object");

			// If no devices is available for any device type, put value as new ArrayList
			// object in the map
			for (String deviceType : deviceTypes.getItems()) {
				deviceGroupMap.putIfAbsent(deviceType, new ArrayList<>());
			}
		} catch (RuntimeException runtimeException) {
			LOGGER.error(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.DEVICE_ONBOARD_DEVICE_GROUP_UPDATE_EXCEPTION
							+ "-> Exception while updating the deviceGroupMap object :"
							+ runtimeException.getMessage());

			generateAlert(DEVICE_ONBOARD_EXCEPTION_ALERT, AlertType.ERROR, DEVICE_ONBOARD_ERROR_MSG);
		} finally {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Updating of deviceGroupMap object completed");
		}
	}

	private List<DeviceDTO> filterDevices(List<DeviceDTO> devices) {
		List<DeviceDTO> filteredDevices = null;

		try {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Filtering of devices based on given search term");

			// Get the search term
			String searchTerm = searchField.getText();

			// If Search term is empty, display all the devices, else filter the devices
			if (searchTerm.isEmpty()) {
				filteredDevices = devices;
			} else {
				filteredDevices = devices.parallelStream()
						.filter(deviceDTO -> (StringUtils.containsIgnoreCase(deviceDTO.getManufacturerName(),
								searchTerm) || StringUtils.containsIgnoreCase(deviceDTO.getModelName(), searchTerm)
								|| StringUtils.containsIgnoreCase(deviceDTO.getSerialNo(), searchTerm)))
						.collect(Collectors.toList());
			}

			// Remove the devices that are mapped
			filteredDevices.removeAll((List<DeviceDTO>) mappedDevices.getItems());
		} catch (RuntimeException runtimeException) {
			LOGGER.error(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.DEVICE_ONBOARD_DEVICE_FILTERING_EXCEPTION
							+ "-> Exception while filtering the devices :" + runtimeException.getMessage());

			generateAlert(DEVICE_ONBOARD_EXCEPTION_ALERT, AlertType.ERROR, DEVICE_ONBOARD_ERROR_MSG);
		} finally {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Filtering of devices based on given search term completed");
		}

		return filteredDevices;
	}
	
	private void clearDeviceOnboardSessionContext() {
		try {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Clearing Session Context objects used for Device Onboarding");
			
			// Remove the Onboard Devices specific objects from Session Context
			SessionContext.getInstance().getMapObject().remove(RegistrationConstants.ONBOARD_DEVICES_MAP);
			SessionContext.getInstance().getMapObject().remove(RegistrationConstants.ONBOARD_AVAILABLE_DEVICES_GROUP);
			SessionContext.getInstance().getMapObject().remove(RegistrationConstants.ONBOARD_MAPPED_DEVICES_GROUP);
			SessionContext.getInstance().getMapObject().remove(RegistrationConstants.ONBOARD_DEVICES_ADDED);
			SessionContext.getInstance().getMapObject().remove(RegistrationConstants.ONBOARD_DEVICES_REMOVED);
		} catch (RuntimeException runtimeException) {
			LOGGER.error(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.DEVICE_ONBOARD_CLEAR_CONTEXT_EXCEPTION
							+ "-> Exception while clearing Session Context objects used for Device Onboarding:"
							+ runtimeException.getMessage());

			generateAlert(DEVICE_ONBOARD_EXCEPTION_ALERT, AlertType.ERROR, DEVICE_ONBOARD_ERROR_MSG);
		} finally {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Clearing Session Context objects used for Device Onboarding completed");
		}
	}

}
