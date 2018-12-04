package io.mosip.registration.test.clientmachinemapping;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.audit.AuditFactoryImpl;
import io.mosip.registration.constants.AppModule;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.MachineMappingDAO;
import io.mosip.registration.dto.DeviceDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.UserMachineMappingDTO;
import io.mosip.registration.entity.DeviceType;
import io.mosip.registration.entity.RegCenterDevice;
import io.mosip.registration.entity.RegCenterDeviceId;
import io.mosip.registration.entity.RegCentreMachineDevice;
import io.mosip.registration.entity.RegCentreMachineDeviceId;
import io.mosip.registration.entity.RegDeviceMaster;
import io.mosip.registration.entity.RegDeviceSpec;
import io.mosip.registration.entity.RegDeviceType;
import io.mosip.registration.entity.RegDeviceTypeId;
import io.mosip.registration.entity.RegistrationUserDetail;
import io.mosip.registration.entity.RegistrationUserRole;
import io.mosip.registration.entity.RegistrationUserRoleID;
import io.mosip.registration.entity.UserMachineMapping;
import io.mosip.registration.entity.UserMachineMappingID;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.impl.MapMachineServiceImpl;
import io.mosip.registration.util.healthcheck.RegistrationSystemPropertiesChecker;

import static org.mockito.Mockito.doNothing;
import static org.hamcrest.CoreMatchers.is;

public class UserClientMachineMappingServiceTest {

	@Mock
	MachineMappingDAO machineMappingDAO;
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	MapMachineServiceImpl mapMachineServiceImpl;
	@Mock
	private AuditFactoryImpl auditFactory;

	@Before
	public void initialize() throws IOException, URISyntaxException {
		doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(AppModule.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void view() throws RegBaseCheckedException {

		ResponseDTO responseDTO = new ResponseDTO();
		String machineID = RegistrationSystemPropertiesChecker.getMachineId();

		Mockito.when(machineMappingDAO.getStationID(Mockito.anyString())).thenReturn("StationID");

		Mockito.when(machineMappingDAO.getCenterID(Mockito.anyString())).thenReturn("CenterID107");

		List<RegistrationUserDetail> userDetailsList = new ArrayList<>();

		UserMachineMappingID machineMappingID = new UserMachineMappingID();
		machineMappingID.setUserID("ID123456");
		machineMappingID.setMachineID(machineID);

		UserMachineMapping userMachineMapping = new UserMachineMapping();
		userMachineMapping.setIsActive(true);
		userMachineMapping.setUserMachineMappingId(machineMappingID);

		RegistrationUserRoleID registrationUserRoleID = new RegistrationUserRoleID();
		registrationUserRoleID.setRoleCode("101");

		RegistrationUserRole registrationUserRole = new RegistrationUserRole();
		registrationUserRole.setRegistrationUserRoleID(registrationUserRoleID);

		Set<RegistrationUserRole> userRole = new HashSet();
		userRole.add(registrationUserRole);

		Set<UserMachineMapping> userMachine = new HashSet();
		userMachine.add(userMachineMapping);

		RegistrationUserDetail registrationUserDetail = new RegistrationUserDetail();
		registrationUserDetail.setId("ID123456");
		registrationUserDetail.setName("Registration");
		registrationUserDetail.setUserMachineMapping(userMachine);
		registrationUserDetail.setUserRole(userRole);
		userDetailsList.add(registrationUserDetail);

		Mockito.when(machineMappingDAO.getUsers(Mockito.anyString())).thenReturn(userDetailsList);

		ResponseDTO res = mapMachineServiceImpl.view();

		Assert.assertSame("User Data Fetched Successfully", res.getSuccessResponseDTO().getMessage());
	}

	@Test
	public void viewFailureTest() throws RegBaseCheckedException {
		RegBaseCheckedException baseCheckedException = new RegBaseCheckedException("101", "No record Found");
		Mockito.when(machineMappingDAO.getStationID(Mockito.anyString())).thenReturn(baseCheckedException.getMessage());
		ResponseDTO res = mapMachineServiceImpl.view();
		Assert.assertSame("No Records Found", res.getErrorResponseDTOs().get(0).getMessage());
	}

	@Test
	public void viewRegBaseUncheckedExceptionTest() throws RegBaseCheckedException {
		Mockito.when(machineMappingDAO.getStationID(Mockito.anyString())).thenThrow(RegBaseUncheckedException.class);
		try {
			mapMachineServiceImpl.view();
		} catch (RegBaseUncheckedException regBaseUncheckedException) {
			Assert.assertNotNull(regBaseUncheckedException);
		}
	}

	@Test
	public void viewRegBaseCheckedExceptionTest() throws RegBaseCheckedException {
		Mockito.when(machineMappingDAO.getStationID(Mockito.anyString())).thenThrow(RegBaseCheckedException.class);
		try {
			mapMachineServiceImpl.view();
		} catch (RegBaseUncheckedException regBaseUncheckedException) {
			Assert.assertNotNull(regBaseUncheckedException);
		}
	}

	@Test
	public void updateTest() {
		UserMachineMappingDTO machineMappingDTO = new UserMachineMappingDTO("ID123", "Nm123", "ADmin", "ACTIVE",
				"CNTR123", "STN123", "MCHN123");
		UserMachineMapping user = new UserMachineMapping();

		ResponseDTO responseDTO = new ResponseDTO();
		SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
		successResponseDTO.setCode(RegistrationConstants.MACHINE_MAPPING_CODE);
		successResponseDTO.setInfoType(RegistrationConstants.ALERT_INFORMATION);
		successResponseDTO.setMessage(RegistrationConstants.MACHINE_MAPPING_SUCCESS_MESSAGE);
		responseDTO.setSuccessResponseDTO(successResponseDTO);

		Mockito.when(machineMappingDAO.update(Mockito.any(UserMachineMapping.class)))
				.thenReturn(RegistrationConstants.MACHINE_MAPPING_UPDATED);
		Mockito.when(machineMappingDAO.findByID(Mockito.any())).thenReturn(null);

		Assert.assertSame(mapMachineServiceImpl.saveOrUpdate(machineMappingDTO).getSuccessResponseDTO().getMessage(),
				responseDTO.getSuccessResponseDTO().getMessage());
	}

	@Test
	public void saveTest() {
		UserMachineMappingDTO machineMappingDTO = new UserMachineMappingDTO("ID123", "Nm123", "ADmin", "IN-ACTIVE",
				"CNTR123", "STN123", "MCHN123");
		UserMachineMapping user = new UserMachineMapping();

		ResponseDTO responseDTO = new ResponseDTO();
		SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
		successResponseDTO.setCode(RegistrationConstants.MACHINE_MAPPING_CODE);
		successResponseDTO.setInfoType(RegistrationConstants.ALERT_INFORMATION);
		successResponseDTO.setMessage(RegistrationConstants.MACHINE_MAPPING_SUCCESS_MESSAGE);
		responseDTO.setSuccessResponseDTO(successResponseDTO);

		Mockito.when(machineMappingDAO.save(Mockito.any(UserMachineMapping.class)))
				.thenReturn(RegistrationConstants.MACHINE_MAPPING_UPDATED);
		Mockito.when(machineMappingDAO.findByID(Mockito.any())).thenReturn(user);

		Assert.assertSame(mapMachineServiceImpl.saveOrUpdate(machineMappingDTO).getSuccessResponseDTO().getMessage(),
				responseDTO.getSuccessResponseDTO().getMessage());
	}

	@Test
	public void saveOrUpdateFailureTest() {
		UserMachineMappingDTO machineMappingDTO = new UserMachineMappingDTO("ID123", "Nm123", "ADmin", "IN-ACTIVE",
				"CNTR123", "STN123", "MCHN123");
		UserMachineMapping user = new UserMachineMapping();

		ResponseDTO responseDTO = new ResponseDTO();
		SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
		successResponseDTO.setCode(RegistrationConstants.MACHINE_MAPPING_CODE);
		successResponseDTO.setInfoType(RegistrationConstants.ALERT_INFORMATION);
		successResponseDTO.setMessage(RegistrationConstants.MACHINE_MAPPING_SUCCESS_MESSAGE);
		responseDTO.setSuccessResponseDTO(successResponseDTO);

		Mockito.when(machineMappingDAO.findByID(Mockito.any())).thenThrow(RegBaseUncheckedException.class);
		Assert.assertSame(
				mapMachineServiceImpl.saveOrUpdate(machineMappingDTO).getErrorResponseDTOs().get(0).getMessage(),
				"Unable to map user");
	}

	@Test
	public void getAllDeviceTypesTest() {
		// Add Device Types
		List<DeviceType> deviceTypes = new ArrayList<>();
		DeviceType deviceType = new DeviceType();
		RegDeviceTypeId deviceTypeId = new RegDeviceTypeId();

		deviceTypeId.setCode("Fingerprint");
		deviceType.setRegDeviceTypeId(deviceTypeId);
		deviceTypes.add(deviceType);

		deviceType = new DeviceType();
		deviceTypeId = new RegDeviceTypeId();
		deviceTypeId.setCode("Iris");
		deviceType.setRegDeviceTypeId(deviceTypeId);
		deviceTypes.add(deviceType);

		deviceType = new DeviceType();
		deviceTypeId = new RegDeviceTypeId();
		deviceTypeId.setCode("Camera");
		deviceType.setRegDeviceTypeId(deviceTypeId);
		deviceTypes.add(deviceType);

		// Mock DAO Call
		Mockito.when(machineMappingDAO.getAllDeviceTypes()).thenReturn(deviceTypes);

		List<String> types = mapMachineServiceImpl.getAllDeviceTypes();

		Assert.assertThat(Arrays.asList("Fingerprint", "Iris", "Camera"), is(types));
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void getAllDeviceTypesTestException() {
		// Mock DAO Call
		Mockito.when(machineMappingDAO.getAllDeviceTypes()).thenReturn(null);

		mapMachineServiceImpl.getAllDeviceTypes();
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void getDeviceMappingListTestException() {

		Mockito.when(machineMappingDAO.getAllMappedDevices(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(new RuntimeException("msg"));
		Mockito.when(machineMappingDAO.getAllValidDevicesByCenterId(Mockito.anyString())).thenReturn(new ArrayList<>());

		mapMachineServiceImpl.getDeviceMappingList("R001", "M001");

	}

	@Test
	public void getDeviceMappingListTest() {
		RegDeviceType deviceType = new RegDeviceType();
		RegDeviceTypeId deviceTypeId = new RegDeviceTypeId();
		RegDeviceSpec regDeviceSpec = new RegDeviceSpec();
		RegDeviceMaster regDeviceMaster = new RegDeviceMaster();
		List<RegCentreMachineDevice> devicesMapped = new ArrayList<>();
		RegCentreMachineDevice centreMachineDevice = new RegCentreMachineDevice();
		RegCentreMachineDeviceId centreMachineDeviceId = new RegCentreMachineDeviceId();

		deviceTypeId.setCode("Fingerprint");
		deviceType.setRegDeviceTypeId(deviceTypeId);
		regDeviceSpec.setRegDeviceType(deviceType);
		regDeviceSpec.setBrand("BrandA");
		regDeviceSpec.setModel("BM001");
		regDeviceMaster.setSerialNumber("S001");
		regDeviceMaster.setRegDeviceSpec(regDeviceSpec);
		centreMachineDeviceId.setDeviceId("D001");
		centreMachineDeviceId.setMachineId("M001");
		centreMachineDeviceId.setRegCentreId("R001");
		centreMachineDevice.setRegCentreMachineDeviceId(centreMachineDeviceId);
		centreMachineDevice.setRegDeviceMaster(regDeviceMaster);

		devicesMapped.add(centreMachineDevice);

		centreMachineDevice = new RegCentreMachineDevice();
		centreMachineDeviceId = new RegCentreMachineDeviceId();
		centreMachineDeviceId.setDeviceId("D002");
		centreMachineDeviceId.setMachineId("M001");
		centreMachineDeviceId.setRegCentreId("R001");
		centreMachineDevice.setRegCentreMachineDeviceId(centreMachineDeviceId);
		centreMachineDevice.setRegDeviceMaster(regDeviceMaster);

		devicesMapped.add(centreMachineDevice);

		List<RegCenterDevice> centerDevices = new ArrayList<>();
		RegCenterDevice centerDevice = new RegCenterDevice();
		RegCenterDeviceId regCenterDeviceId = new RegCenterDeviceId();

		regCenterDeviceId.setDeviceId("D002");
		regCenterDeviceId.setRegCenterId("R001");
		centerDevice.setRegCenterDeviceId(regCenterDeviceId);
		centerDevice.setRegDeviceMaster(regDeviceMaster);

		centerDevices.add(centerDevice);

		Mockito.when(machineMappingDAO.getAllMappedDevices(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(devicesMapped);
		Mockito.when(machineMappingDAO.getAllValidDevicesByCenterId(Mockito.anyString())).thenReturn(centerDevices);

		Map<String, List<DeviceDTO>> devicesMap = mapMachineServiceImpl.getDeviceMappingList("R001", "M001");

		Map<String, List<DeviceDTO>> expectedDevicesMap = new HashMap<>();
		List<DeviceDTO> deviceDTOs = new ArrayList<>();
		DeviceDTO deviceDTO = new DeviceDTO();
		deviceDTO.setDeviceId("D001");
		deviceDTO.setDeviceType("Fingerprint");
		deviceDTO.setMachineId("M001");
		deviceDTO.setManufacturerName("BrandA");
		deviceDTO.setModelName("BM001");
		deviceDTO.setRegCenterId("R001");
		deviceDTO.setSerialNo("S001");
		deviceDTOs.add(deviceDTO);

		deviceDTO = new DeviceDTO();
		deviceDTO.setDeviceId("D002");
		deviceDTO.setDeviceType("Fingerprint");
		deviceDTO.setMachineId("M001");
		deviceDTO.setManufacturerName("BrandA");
		deviceDTO.setModelName("BM001");
		deviceDTO.setRegCenterId("R001");
		deviceDTO.setSerialNo("S001");
		deviceDTOs.add(deviceDTO);

		expectedDevicesMap.put(RegistrationConstants.ONBOARD_MAPPED_DEVICES, deviceDTOs);
		expectedDevicesMap.put(RegistrationConstants.ONBOARD_AVAILABLE_DEVICES, new ArrayList<>());

		Assert.assertThat(devicesMap, is(expectedDevicesMap));

	}

	@Test
	public void updateMappedDeviceTest() {
		List<DeviceDTO> deleteDevices = new ArrayList<>();
		List<DeviceDTO> mappedDevices = new ArrayList<>();
		DeviceDTO deviceDTO = new DeviceDTO();

		deviceDTO.setDeviceId("D001");
		deviceDTO.setMachineId("M001");
		deviceDTO.setRegCenterId("R001");
		deleteDevices.add(deviceDTO);

		deviceDTO = new DeviceDTO();
		deviceDTO.setDeviceId("D002");
		deviceDTO.setMachineId("M001");
		deviceDTO.setRegCenterId("R001");
		deleteDevices.add(deviceDTO);

		deviceDTO = new DeviceDTO();
		deviceDTO.setDeviceId("D003");
		deviceDTO.setMachineId("M001");
		deviceDTO.setRegCenterId("R001");
		mappedDevices.add(deviceDTO);

		Mockito.doNothing().when(machineMappingDAO).addedMappedDevice(Mockito.anyListOf(RegCentreMachineDevice.class));
		Mockito.doNothing().when(machineMappingDAO)
				.deleteUnMappedDevice(Mockito.anyListOf(RegCentreMachineDevice.class));

		mapMachineServiceImpl.updateMappedDevice(deleteDevices, mappedDevices);

	}

	@Test
	public void updateMappedDeviceTestException() {
		List<DeviceDTO> deleteDevices = new ArrayList<>();
		List<DeviceDTO> mappedDevices = new ArrayList<>();
		DeviceDTO deviceDTO = new DeviceDTO();

		deviceDTO.setDeviceId("D001");
		deviceDTO.setRegCenterId("R001");
		deleteDevices.add(deviceDTO);

		deviceDTO = new DeviceDTO();
		deviceDTO.setDeviceId("D002");
		deviceDTO.setRegCenterId("R001");
		deleteDevices.add(deviceDTO);

		deviceDTO = null;
		mappedDevices.add(deviceDTO);

		Mockito.doNothing().when(machineMappingDAO).addedMappedDevice(Mockito.anyListOf(RegCentreMachineDevice.class));
		Mockito.doNothing().when(machineMappingDAO)
				.deleteUnMappedDevice(Mockito.anyListOf(RegCentreMachineDevice.class));

		mapMachineServiceImpl.updateMappedDevice(deleteDevices, mappedDevices);

	}

}
