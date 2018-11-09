package io.mosip.registration.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AppModule;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.DeviceDTO;
import javafx.beans.value.ObservableValue;
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
	private TextField filterDevices;
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
	private static final MosipLogger LOGGER = AppConfig.getLogger(DeviceMappingController.class);

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
			auditFactory.audit(AuditEvent.GET_ONBOARDING_DEVICES_TYPES, AppModule.DEVICE_ONBOARD,
					"Get the types of onboarding devices", SessionContext.getInstance().getUserContext().getUserId(),
					RegistrationConstants.ONBOARD_DEVICES_REF_ID_TYPE);
			
			// Set the Device Types
			deviceTypes.getItems()
					.addAll(FXCollections.observableArrayList(RegistrationConstants.ONBOARD_DEVICE_TYPES));

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
			filterDevices.textProperty()
					.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
						@SuppressWarnings("unchecked")
						Map<String, List<DeviceDTO>> devicesMap = (Map<String, List<DeviceDTO>>) SessionContext
								.getInstance().getMapObject().get(RegistrationConstants.ONBOARD_DEVICES_MAP);
						if (devicesMap != null) {
							if (RegistrationConstants.EMPTY.equals(newValue)) {
								populateDevices(devicesMap.get(RegistrationConstants.ONBOARD_AVAILABLE_DEVICES),
										devicesMap.get(RegistrationConstants.ONBOARD_MAPPED_DEVICES));
							} else if (newValue.length() < oldValue.length()) {
								populateDevices(
										filterDevices(devicesMap.get(RegistrationConstants.ONBOARD_AVAILABLE_DEVICES)),
										filterDevices(devicesMap.get(RegistrationConstants.ONBOARD_MAPPED_DEVICES)));
							} else {
								populateDevices(filterDevices(availableDevices.getItems()),
										filterDevices(mappedDevices.getItems()));
							}
						}
					});
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
					"Loading list of available and mapped " + deviceTypes.getValue() + " devices");

			// Reset the Search TextField
			filterDevices.setText(RegistrationConstants.EMPTY);
			
			// Get the selected Device Type
			String selectedDeviceType = deviceTypes.getValue();
			
			auditFactory.audit(AuditEvent.GET_ONBOARDING_DEVICES, AppModule.DEVICE_ONBOARD,
					"Get the available and mapped devices for ".concat(selectedDeviceType),
					SessionContext.getInstance().getUserContext().getUserId(),
					RegistrationConstants.ONBOARD_DEVICES_REF_ID_TYPE);

			// Get the list of Available and Mapped Devices for selected Device Type
			Map<String, List<DeviceDTO>> devicesMap = getDevices(selectedDeviceType);
			SessionContext.getInstance().getMapObject().put(RegistrationConstants.ONBOARD_DEVICES_MAP, devicesMap);

			// Populate the Available Devices and Mapped Devices Tables
			populateDevices(devicesMap.get(RegistrationConstants.ONBOARD_AVAILABLE_DEVICES),
					devicesMap.get(RegistrationConstants.ONBOARD_MAPPED_DEVICES));
		} catch (RuntimeException runtimeException) {
			LOGGER.error(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.DEVICE_ONBOARD_LOADING_DEVICES_EXCEPTION
							+ "-> Exception while loading devices based on selected device type:"
							+ runtimeException.getMessage());

			generateAlert(DEVICE_ONBOARD_EXCEPTION_ALERT, AlertType.ERROR, DEVICE_ONBOARD_ERROR_MSG);
		} finally {
			LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Loading list of available and mapped " + deviceTypes.getValue() + " devices completed");
		}
	}

	private void populateDevices(List<DeviceDTO> availableDevices, List<DeviceDTO> mappedDevices) {
		// Set the Available Devices
		this.availableDevices.setItems(FXCollections.observableArrayList(availableDevices));

		// Set the Mapped Devices
		this.mappedDevices.setItems(FXCollections.observableArrayList(mappedDevices));
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
	@FXML
	private void mapAvailableDevices(MouseEvent mouseEvent) {
		LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Mapping selected devices");

		try {
			mapDevices(availableDevices, mappedDevices);
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
	@FXML
	private void unmapMappedDevices(MouseEvent mouseEvent) {
		LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Unmapping selected devices");

		try {
			mapDevices(mappedDevices, availableDevices);
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

	private void mapDevices(TableView<DeviceDTO> source, TableView<DeviceDTO> destination) {
		// Get the selected devices
		List<DeviceDTO> selectedDevices = source.getSelectionModel().getSelectedItems();

		// Check the selected devices. If no devices are selected, mapping is not
		// required
		if (selectedDevices != null && !selectedDevices.isEmpty()) {

			// Add the selected devices to the destination
			destination.getItems().addAll(selectedDevices);

			// Remove the selected devices from the source
			source.getItems().removeAll(selectedDevices);

			// Clear Selection - If not cleared, either the next or before item will be
			// selected by default
			source.getSelectionModel().clearSelection();
		}
	}

	/**
	 * Navigates to Registration Home Page
	 */
	@FXML
	private void goToHome() {
		LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
				"Navigating to Registration Home Page");
		try {
			// Remove the Onboard Devices from Session Context
			SessionContext.getInstance().getMapObject().remove(RegistrationConstants.ONBOARD_DEVICES_MAP);

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
	@FXML
	private void submit(ActionEvent actionEvent) {
		LOGGER.debug(DEVICE_ONBOARD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
				"Updating the mapping of onboarding devices for Registration Machine");

		try {
			auditFactory.audit(AuditEvent.UPDATE_DEVICES_ONBOARDING, AppModule.DEVICE_ONBOARD,
					String.format("Updating mapping of %s devices", filterDevices.getText()),
					SessionContext.getInstance().getUserContext().getUserId(),
					RegistrationConstants.ONBOARD_DEVICES_REF_ID_TYPE);
			
			// Add Mapping
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

	private List<DeviceDTO> filterDevices(List<DeviceDTO> devices) {
		// Get the search term
		String searchTerm = filterDevices.getText();
		
		return devices.parallelStream()
				.filter(deviceDTO -> (StringUtils.containsIgnoreCase(deviceDTO.getManufacturerName(), searchTerm)
						|| StringUtils.containsIgnoreCase(deviceDTO.getModelName(), searchTerm)
						|| StringUtils.containsIgnoreCase(deviceDTO.getSerialNo(), searchTerm)))
				.collect(Collectors.toList());
	}

	private Map<String, List<DeviceDTO>> getDevices(String deviceType) {
		Map<String, List<DeviceDTO>> deviceMap = new HashMap<>();
		List<DeviceDTO> availDevices = new ArrayList<>();
		deviceMap.put(RegistrationConstants.ONBOARD_AVAILABLE_DEVICES, availDevices);
		availDevices.add(getDeviceDTO("Samsung", "999XYZ", "1234ABCD"));
		availDevices.add(getDeviceDTO("LG", "998XYZ", "1235ABCD"));
		availDevices.add(getDeviceDTO("Onida", "997XYZ", "1236ABCD"));
		List<DeviceDTO> mapDevices = new ArrayList<>();
		deviceMap.put(RegistrationConstants.ONBOARD_MAPPED_DEVICES, mapDevices);
		mapDevices.add(getDeviceDTO("Samsung", "996XYZ", "1237ABCD"));
		mapDevices.add(getDeviceDTO("LG", "995XYZ", "1238ABCD"));
		return deviceMap;
	}

	private DeviceDTO getDeviceDTO(String manufacturerName, String modelName, String serialNo) {
		DeviceDTO deviceDTO = new DeviceDTO();
		deviceDTO.setManufacturerName(manufacturerName);
		deviceDTO.setModelName(modelName);
		deviceDTO.setSerialNo(serialNo);
		return deviceDTO;
	}

}
