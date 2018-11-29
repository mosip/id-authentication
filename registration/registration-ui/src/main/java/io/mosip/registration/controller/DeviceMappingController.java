package io.mosip.registration.controller;

import static io.mosip.registration.constants.LoggerConstants.DEVICE_ONBOARD_CONTROLLER;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.DeviceDTO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.mapping.MapMachineService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
	
	private static final Logger LOGGER = AppConfig.getLogger(DeviceMappingController.class);

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
	private TableColumn<DeviceDTO, String> availableDeviceType;
	@FXML
	private TableColumn<DeviceDTO, String> mappedDeviceName;
	@FXML
	private TableColumn<DeviceDTO, String> mappedDeviceModel;
	@FXML
	private TableColumn<DeviceDTO, String> mappedDeviceSerial;
	@FXML
	private TableColumn<DeviceDTO, String> mappedDeviceType;
	@FXML
	private TableView<DeviceDTO> mappedDevices;
	@FXML
	private Button submitOnboardDevices;
	@FXML
	private ImageView mapDevice;
	@FXML
	private ImageView unmapDevice;

	@Autowired
	private MapMachineService mapMachineService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 * 
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
				"Initializing Device On-boarding Page");

		try {
			auditFactory.audit(AuditEvent.GET_ONBOARDING_DEVICES_TYPES, Components.DEVICE_ONBOARD,
					"Get the types of onboarding devices", SessionContext.getInstance().getUserContext().getUserId(),
					RegistrationConstants.ONBOARD_DEVICES_REF_ID_TYPE);

			// Set Machine ID
			SessionContext.getInstance().getMapObject().put(RegistrationConstants.MACHINE_ID, "1947");

			// Add 'All' option to Device Types dropdown
			deviceTypes.getItems().add(RegistrationConstants.DEVICE_TYPES_ALL_OPTION);

			// Set the Device Types
			deviceTypes.getItems().addAll(FXCollections.observableArrayList(mapMachineService.getAllDeviceTypes()));

			// Select 'All' as default Device Type
			deviceTypes.getSelectionModel().select(RegistrationConstants.DEVICE_TYPES_ALL_OPTION);

			// Set the CellValueFactory attribute of TableView
			availableDeviceName
					.setCellValueFactory(new PropertyValueFactory<>(RegistrationConstants.DEVICE_MANUFACTURER_NAME));
			availableDeviceModel
					.setCellValueFactory(new PropertyValueFactory<>(RegistrationConstants.DEVICE_MODEL_NAME));
			availableDeviceSerial
					.setCellValueFactory(new PropertyValueFactory<>(RegistrationConstants.DEVICE_SERIAL_NO));
			availableDeviceType.setCellValueFactory(new PropertyValueFactory<>(RegistrationConstants.DEVICE_TYPE));
			mappedDeviceName
					.setCellValueFactory(new PropertyValueFactory<>(RegistrationConstants.DEVICE_MANUFACTURER_NAME));
			mappedDeviceModel.setCellValueFactory(new PropertyValueFactory<>(RegistrationConstants.DEVICE_MODEL_NAME));
			mappedDeviceSerial.setCellValueFactory(new PropertyValueFactory<>(RegistrationConstants.DEVICE_SERIAL_NO));
			mappedDeviceType.setCellValueFactory(new PropertyValueFactory<>(RegistrationConstants.DEVICE_TYPE));

			// Set selection Mode to multiple for Available Devices and Mapped Devices
			// Tables
			availableDevices.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			mappedDevices.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

			// Bind Event Handlers to Search Device Button
			searchField.textProperty().addListener((observable, oldValue, newValue) -> {
				try {
					LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
							"Searching the available devices based on search criteria");

					Map<String, List<DeviceDTO>> devicesMap = getDevicesByType(
							deviceTypes.getSelectionModel().getSelectedItem());

					if (RegistrationConstants.EMPTY.equals(newValue)) {
						populateDevices(devicesMap.get(RegistrationConstants.ONBOARD_AVAILABLE_DEVICES),
								mappedDevices.getItems());
					} else if (newValue.length() < oldValue.length()) {
						populateDevices(filterDevices(devicesMap.get(RegistrationConstants.ONBOARD_AVAILABLE_DEVICES)),
								mappedDevices.getItems());
					} else {
						populateDevices(filterDevices(availableDevices.getItems()), mappedDevices.getItems());
					}
					LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
							"Searching the available devices based on search criteria completed");
				} catch (RuntimeException runtimeException) {
					LOGGER.error(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
							RegistrationConstants.DEVICE_ONBOARD_SEARCH_DEVICE_EXCEPTION
									+ "-> Exception while searching the available devices based on search criteria: "
									+ runtimeException.getMessage());

					generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.DEVICE_ONBOARD_ERROR_MSG);
				}
			});

			// Display the all the available and mapped devices
			displayDevices();
		} catch (RuntimeException runtimeException) {
			LOGGER.error(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.DEVICE_ONBOARD_INITIALIZATION_EXCEPTION
							+ "-> Exception while initializing device onboarding page: "
							+ runtimeException.getMessage());

			throw new RegBaseUncheckedException(RegistrationConstants.DEVICE_ONBOARD_INITIALIZATION_EXCEPTION,
					"Exception while initializing device onboarding page: ".concat(runtimeException.getMessage()));
		} finally {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Device Onboarding page initialization method execution completed");
		}
	}

	private void displayDevices() {
		try {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Fetching and displaying all available and mapped devices from Service and UI");

			auditFactory.audit(AuditEvent.GET_ONBOARDING_DEVICES, Components.DEVICE_ONBOARD,
					"Get all the available and mapped devices",
					SessionContext.getInstance().getUserContext().getUserId(),
					RegistrationConstants.ONBOARD_DEVICES_REF_ID_TYPE);

			// Create a map of list based on device type
			Map<String, List<DeviceDTO>> devicesMap = mapMachineService.getDeviceMappingList(
					SessionContext.getInstance().getUserContext().getRegistrationCenterDetailDTO()
							.getRegistrationCenterId(),
					(String) SessionContext.getInstance().getMapObject().get(RegistrationConstants.MACHINE_ID));

			// If Available Devices or Mapped Devices or both not available, add new
			// ArrayList
			devicesMap.putIfAbsent(RegistrationConstants.ONBOARD_AVAILABLE_DEVICES, new ArrayList<>());
			devicesMap.putIfAbsent(RegistrationConstants.ONBOARD_MAPPED_DEVICES, new ArrayList<>());

			// Show the Devices in UI
			populateDevices(devicesMap.get(RegistrationConstants.ONBOARD_AVAILABLE_DEVICES),
					devicesMap.get(RegistrationConstants.ONBOARD_MAPPED_DEVICES));

			// Add the Actual Devices Map and Updated Devices Map (Placeholder) to
			// SessionContext object
			SessionContext.getInstance().getMapObject().put(RegistrationConstants.ONBOARD_DEVICES_MAP, devicesMap);
			Map<String, Set<DeviceDTO>> upadtedDevicesMap = new HashMap<>();
			upadtedDevicesMap.put(RegistrationConstants.ONBOARD_AVAILABLE_DEVICES, new HashSet<DeviceDTO>());
			upadtedDevicesMap.put(RegistrationConstants.ONBOARD_MAPPED_DEVICES, new HashSet<DeviceDTO>());
			SessionContext.getInstance().getMapObject().put(RegistrationConstants.ONBOARD_DEVICES_MAP_UPDATED,
					upadtedDevicesMap);
		} catch (RuntimeException runtimeException) {
			LOGGER.error(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.DEVICE_ONBOARD_DEVICE_FETCHING_EXCEPTION
							+ "-> Exception while fetching or displaying devices from Service and UI: "
							+ runtimeException.getMessage());

			throw new RegBaseUncheckedException(RegistrationConstants.DEVICE_ONBOARD_DEVICE_FETCHING_EXCEPTION,
					"Exception while fetching or displaying devices from Service and UI: "
							.concat(runtimeException.getMessage()));
		} finally {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Fetching and displaying all available and mapped devices from Service and UI method execution completed");
		}
	}

	/**
	 * Displays the lists of Available Devices and Mapped Devices on changing the
	 * Device Type.
	 * <p>
	 * The Available Devices and Mapped Devices fetched from Service base on the
	 * selected Device Type will be displayed in Available Devices and Mapped
	 * Devices Tables respectively.
	 * 
	 * @param actionEvent
	 *            the {@link ActionEvent} object
	 */
	@FXML
	private void loadDevices(ActionEvent actionEvent) {
		try {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Loading list of available and mapped devices for selected device type");

			// Get the selected device type
			String selectedDeviceType = deviceTypes.getSelectionModel().getSelectedItem();

			// Reset the Search TextField
			searchField.setText(RegistrationConstants.EMPTY);

			auditFactory.audit(AuditEvent.GET_ONBOARDING_DEVICES, Components.DEVICE_ONBOARD,
					"Get the available and mapped devices for ".concat(selectedDeviceType),
					SessionContext.getInstance().getUserContext().getUserId(),
					RegistrationConstants.ONBOARD_DEVICES_REF_ID_TYPE);

			// Get the list of Available and Mapped Devices for selected Device Type
			Map<String, List<DeviceDTO>> displayedDevicesMap = getDevicesByType(selectedDeviceType);

			// Populate the Available Devices and Mapped Devices Tables
			populateDevices(displayedDevicesMap.get(RegistrationConstants.ONBOARD_AVAILABLE_DEVICES),
					displayedDevicesMap.get(RegistrationConstants.ONBOARD_MAPPED_DEVICES));
		} catch (RuntimeException runtimeException) {
			LOGGER.error(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.DEVICE_ONBOARD_LOADING_DEVICES_EXCEPTION
							+ "-> Exception while loading devices based on selected device type: "
							+ runtimeException.getMessage());

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.DEVICE_ONBOARD_ERROR_MSG);
		} finally {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Loading list of available and mapped devices for selected device type method execution completed");
		}
	}

	private void populateDevices(List<DeviceDTO> availableDevices, List<DeviceDTO> mappedDevices) {
		try {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Populating devices in UI");

			// Set the Available Devices
			this.availableDevices.setItems(FXCollections.observableArrayList(availableDevices));

			// Set the Mapped Devices
			this.mappedDevices.setItems(FXCollections.observableArrayList(mappedDevices));
		} catch (RuntimeException runtimeException) {
			LOGGER.error(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.DEVICE_ONBOARD_DEVICE_POPULATION_EXCEPTION
							+ "-> Exception while populating devices in UI: " + runtimeException.getMessage());

			throw new RegBaseUncheckedException(RegistrationConstants.DEVICE_ONBOARD_DEVICE_POPULATION_EXCEPTION,
					"Exception while populating devices in UI: ".concat(runtimeException.getMessage()));
		} finally {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Populating devices in UI method execution completed");
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
			Map<String, Set<DeviceDTO>> updatedDevicesMap = (Map<String, Set<DeviceDTO>>) SessionContext.getInstance()
					.getMapObject().get(RegistrationConstants.ONBOARD_DEVICES_MAP_UPDATED);
			Set<DeviceDTO> deviceMaster = updatedDevicesMap.get(RegistrationConstants.ONBOARD_MAPPED_DEVICES);
			deviceMaster.addAll(devicesAdded);

			// Update the Devices removed in Session Context
			deviceMaster = updatedDevicesMap.get(RegistrationConstants.ONBOARD_AVAILABLE_DEVICES);
			deviceMaster.removeAll(devicesAdded);
		} catch (RuntimeException runtimeException) {
			LOGGER.error(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.DEVICE_ONBOARD_MAPPING_DEVICES_EXCEPTION
							+ "-> Exception while mapping devices: " + runtimeException.getMessage());

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.DEVICE_ONBOARD_ERROR_MSG);
		} finally {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Mapping of selected devices method execution completed");
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
			Map<String, Set<DeviceDTO>> updatedDevicesMap = (Map<String, Set<DeviceDTO>>) SessionContext.getInstance()
					.getMapObject().get(RegistrationConstants.ONBOARD_DEVICES_MAP_UPDATED);
			Set<DeviceDTO> deviceMaster = updatedDevicesMap.get(RegistrationConstants.ONBOARD_AVAILABLE_DEVICES);
			deviceMaster.addAll(devicesRemoved);

			// Update the Devices Added in Session Context
			deviceMaster = updatedDevicesMap.get(RegistrationConstants.ONBOARD_MAPPED_DEVICES);
			deviceMaster.removeAll(devicesRemoved);
		} catch (RuntimeException runtimeException) {
			LOGGER.error(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.DEVICE_ONBOARD_UNMAPPING_DEVICES_EXCEPTION
							+ "-> Exception while unmapping devices: " + runtimeException.getMessage());

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.DEVICE_ONBOARD_ERROR_MSG);
		} finally {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Unmapping of selected devices method execution completed");
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

			throw new RegBaseUncheckedException(RegistrationConstants.DEVICE_ONBOARD_DEVICE_GROUPING_EXCEPTION,
					"Exception while mapping of devices: ".concat(runtimeException.getMessage()));
		} finally {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Mapping of devices method execution completed");
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
							+ "-> Exception while navigating to Home page: " + ioException.getMessage());

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.DEVICE_ONBOARD_ERROR_MSG);
		} finally {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Navigation to Registration Home page method execution completed");
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
			auditFactory.audit(AuditEvent.UPDATE_DEVICES_ONBOARDING, Components.DEVICE_ONBOARD,
					"Updating mapping of devices", SessionContext.getInstance().getUserContext().getUserId(),
					RegistrationConstants.ONBOARD_DEVICES_REF_ID_TYPE);

			// Get updated added and removed devices
			Map<String, Set<DeviceDTO>> devicesMap = (Map<String, Set<DeviceDTO>>) SessionContext.getInstance()
					.getMapObject().get(RegistrationConstants.ONBOARD_DEVICES_MAP_UPDATED);
			Set<DeviceDTO> devicesAdded = devicesMap.get(RegistrationConstants.ONBOARD_MAPPED_DEVICES);
			Set<DeviceDTO> devicesRemoved = devicesMap.get(RegistrationConstants.ONBOARD_AVAILABLE_DEVICES);

			// Get existing available and mapped devices
			devicesMap = (Map<String, Set<DeviceDTO>>) SessionContext.getInstance().getMapObject()
					.get(RegistrationConstants.ONBOARD_DEVICES_MAP);

			// Update the Added and Removed Devices
			devicesAdded.retainAll(devicesMap.get(RegistrationConstants.ONBOARD_AVAILABLE_DEVICES));
			devicesRemoved.retainAll(devicesMap.get(RegistrationConstants.ONBOARD_MAPPED_DEVICES));

			// Get the Machine ID
			String machineId = (String) SessionContext.getInstance().getMapObject()
					.get(RegistrationConstants.MACHINE_ID);

			// Update the Machine ID
			devicesAdded.forEach(deviceDTO -> deviceDTO.setMachineId(machineId));

			// Update Devices Mapping
			ResponseDTO responseDTO = mapMachineService.updateMappedDevice(new ArrayList<>(devicesRemoved),
					new ArrayList<>(devicesAdded));

			if (responseDTO.getSuccessResponseDTO() != null) {
				generateAlert(RegistrationConstants.ALERT_INFORMATION, responseDTO.getSuccessResponseDTO().getMessage());
			} else {
				ErrorResponseDTO errorResponseDTO = responseDTO.getErrorResponseDTOs().get(0);
				generateAlert(RegistrationConstants.ALERT_ERROR, errorResponseDTO.getMessage());
			}

			// Set the device type drop-down to 'All'
			deviceTypes.getSelectionModel().select(RegistrationConstants.DEVICE_TYPES_ALL_OPTION);

			// Clear the Session Context objects used for Device Onboarding
			clearDeviceOnboardSessionContext();

			// Display the updated devices
			displayDevices();
		} catch (RuntimeException runtimeException) {
			LOGGER.error(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.DEVICE_ONBOARD_DEVICE_UPDATING_EXCEPTION
							+ "-> Exception while updating the mapping of onboarding devices: "
							+ runtimeException.getMessage());

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.DEVICE_ONBOARD_ERROR_MSG);
		} finally {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Mapping of onboarding devices for Registration Machine method execution completed");
		}
	}

	private List<DeviceDTO> filterDevices(List<DeviceDTO> devices) {
		List<DeviceDTO> filteredDevices = null;

		try {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Searching the devices based on given search criteria");

			// Get the search term
			String searchTerm = searchField.getText();

			// If Search term is empty, display all the devices, else filter the devices
			if (searchTerm.isEmpty()) {
				filteredDevices = devices;
			} else {
				filteredDevices = devices.parallelStream()
						.filter(deviceDTO -> (StringUtils.containsIgnoreCase(deviceDTO.getManufacturerName(),
								searchTerm) || StringUtils.containsIgnoreCase(deviceDTO.getModelName(), searchTerm)
								|| StringUtils.containsIgnoreCase(deviceDTO.getSerialNo(), searchTerm)
								|| StringUtils.containsIgnoreCase(deviceDTO.getDeviceType(), searchTerm)))
						.collect(Collectors.toList());
			}

			// Remove the devices that are mapped
			filteredDevices.removeAll((List<DeviceDTO>) mappedDevices.getItems());
		} catch (RuntimeException runtimeException) {
			LOGGER.error(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.DEVICE_ONBOARD_DEVICE_FILTERING_EXCEPTION
							+ "-> Exception while searching the devices based on given search criteria: "
							+ runtimeException.getMessage());

			throw new RegBaseUncheckedException(RegistrationConstants.DEVICE_ONBOARD_DEVICE_FILTERING_EXCEPTION,
					"Exception while searching the devices based on given search criteria: "
							.concat(runtimeException.getMessage()));
		} finally {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Searching the devices based on given search criteria method execution completed");
		}

		return filteredDevices;
	}

	private void clearDeviceOnboardSessionContext() {
		try {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Clearing Session Context objects used for Device Onboarding");

			// Remove the Onboard Devices specific objects from Session Context
			SessionContext.getInstance().getMapObject().remove(RegistrationConstants.ONBOARD_DEVICES_MAP);
			SessionContext.getInstance().getMapObject().remove(RegistrationConstants.ONBOARD_DEVICES_MAP_UPDATED);
		} catch (RuntimeException runtimeException) {
			LOGGER.error(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.DEVICE_ONBOARD_CLEAR_CONTEXT_EXCEPTION
							+ "-> Exception while clearing Session Context objects used for Device Onboarding: "
							+ runtimeException.getMessage());

			throw new RegBaseUncheckedException(RegistrationConstants.DEVICE_ONBOARD_CLEAR_CONTEXT_EXCEPTION,
					"Exception while clearing Session Context objects used for Device Onboarding: "
							.concat(runtimeException.getMessage()));
		} finally {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Clearing Session Context objects used for Device Onboarding method execution completed");
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, List<DeviceDTO>> getDevicesByType(String deviceType) {
		Map<String, List<DeviceDTO>> actualDevicesMap = null;
		try {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Filtering the devices by deviceType");

			// Get actual Devices from Session Context
			actualDevicesMap = new HashMap<>((Map<String, List<DeviceDTO>>) SessionContext.getInstance().getMapObject()
					.get(RegistrationConstants.ONBOARD_DEVICES_MAP));

			// Get updated Devices Map from Session Context
			Map<String, Set<DeviceDTO>> updatedDevicesMap = (Map<String, Set<DeviceDTO>>) SessionContext.getInstance()
					.getMapObject().get(RegistrationConstants.ONBOARD_DEVICES_MAP_UPDATED);

			// Get actual available devices
			List<DeviceDTO> availableDevicesToDisplay = (List<DeviceDTO>) getDevicesByType(
					actualDevicesMap.get(RegistrationConstants.ONBOARD_AVAILABLE_DEVICES), deviceType);

			// Get actual mapped devices
			List<DeviceDTO> mappedDevicesToDisplay = (List<DeviceDTO>) getDevicesByType(
					actualDevicesMap.get(RegistrationConstants.ONBOARD_MAPPED_DEVICES), deviceType);

			// Get updated mapped devices
			Set<DeviceDTO> updatedMappedDevices = new HashSet<>((List<DeviceDTO>) getDevicesByType(
					updatedDevicesMap.get(RegistrationConstants.ONBOARD_MAPPED_DEVICES), deviceType));

			// Get updated removed devices
			Set<DeviceDTO> updatedAvailableDevices = new HashSet<>((List<DeviceDTO>) getDevicesByType(
					updatedDevicesMap.get(RegistrationConstants.ONBOARD_AVAILABLE_DEVICES), deviceType));

			// Update available devices to display - Add the updated Available Devices and
			// Remove the updated Mapped Devices
			availableDevicesToDisplay.addAll(updatedAvailableDevices);
			availableDevicesToDisplay.removeAll(updatedMappedDevices);

			// Update mapped devices to display - Remove the updated Available Devices and
			// Add the updated Mapped Devices
			mappedDevicesToDisplay.removeAll(updatedAvailableDevices);
			mappedDevicesToDisplay.addAll(updatedMappedDevices);

			actualDevicesMap.put(RegistrationConstants.ONBOARD_AVAILABLE_DEVICES, availableDevicesToDisplay);
			actualDevicesMap.put(RegistrationConstants.ONBOARD_MAPPED_DEVICES, mappedDevicesToDisplay);
		} catch (RuntimeException runtimeException) {
			LOGGER.error(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.DEVICE_ONBOARD_FILTER_EXCEPTION
							+ "-> Exception while filtering the devices by deviceType: "
							+ runtimeException.getMessage());

			throw new RegBaseUncheckedException(RegistrationConstants.DEVICE_ONBOARD_FILTER_EXCEPTION,
					"Exception while filtering the devices by deviceType: ".concat(runtimeException.getMessage()));
		} finally {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Filtering the devices by deviceType method execution completed");
		}

		return actualDevicesMap;
	}

	private Collection<DeviceDTO> getDevicesByType(Collection<DeviceDTO> devices, String deviceType) {
		List<DeviceDTO> collection = new ArrayList<>();
		try {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Filtering the devices based on deviceType");
			if (deviceType.equals(RegistrationConstants.DEVICE_TYPES_ALL_OPTION)) {
				collection.addAll(devices);
			} else {
				collection = devices.parallelStream().filter(deviceDTO -> deviceDTO.getDeviceType().equals(deviceType))
						.collect(Collectors.toList());
			}
		} catch (RuntimeException runtimeException) {
			LOGGER.error(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.DEVICE_ONBOARD_FILTER_LIST_EXCEPTION
							+ "-> Exception while filtering the devices based on deviceType: "
							+ runtimeException.getMessage());

			throw new RegBaseUncheckedException(RegistrationConstants.DEVICE_ONBOARD_FILTER_LIST_EXCEPTION,
					"Exception while filtering the devices based on deviceType: "
							.concat(runtimeException.getMessage()));
		} finally {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Filtering the devices based on deviceType method execution completed");
		}
		return collection;
	}

}
