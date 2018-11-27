package io.mosip.registration.test.clientmachinemapping;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.context.SessionContext.UserContext;
import io.mosip.registration.dao.impl.MachineMappingDAOImpl;
import io.mosip.registration.entity.CenterMachine;
import io.mosip.registration.entity.CenterMachineId;
import io.mosip.registration.entity.DeviceType;
import io.mosip.registration.entity.MachineMaster;
import io.mosip.registration.entity.RegCenterDevice;
import io.mosip.registration.entity.RegCentreMachineDevice;
import io.mosip.registration.entity.RegistrationUserDetail;
import io.mosip.registration.entity.RegistrationUserRole;
import io.mosip.registration.entity.RegistrationUserRoleID;
import io.mosip.registration.entity.UserMachineMapping;
import io.mosip.registration.entity.UserMachineMappingID;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.CenterMachineRepository;
import io.mosip.registration.repositories.DeviceTypeRepository;
import io.mosip.registration.repositories.MachineMasterRepository;
import io.mosip.registration.repositories.RegistrationCenterDeviceRepository;
import io.mosip.registration.repositories.RegistrationCenterMachineDeviceRepository;
import io.mosip.registration.repositories.RegistrationUserDetailRepository;
import io.mosip.registration.repositories.UserMachineMappingRepository;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.doNothing;

public class UserClientMachineMappingDAOTest {

	@Mock
	private UserMachineMappingRepository machineMappingRepository;
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	MachineMappingDAOImpl machineMappingDAOImpl;
	@Mock
	private CenterMachineRepository centerMachineRepository;
	@Mock
	private MachineMasterRepository machineMasterRepository;
	@Mock
	private RegistrationUserDetailRepository userDetailRepository;
	@Mock
	private AuditFactoryImpl auditFactory;
	@Mock
	private DeviceTypeRepository deviceTypeRepository;
	@Mock
	private RegistrationCenterDeviceRepository registrationCenterDeviceRepository;
	@Mock
	private RegistrationCenterMachineDeviceRepository registrationCenterMachineDeviceRepository;

	@Before
	public void initialize() throws IOException, URISyntaxException {
		doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(AppModule.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void updateTest() throws RegBaseCheckedException {

		UserMachineMapping machineMapping = new UserMachineMapping();
		Mockito.when(machineMappingRepository.update(Mockito.any(UserMachineMapping.class))).thenReturn(machineMapping);
		Assert.assertSame(machineMappingDAOImpl.update(machineMapping), RegistrationConstants.MACHINE_MAPPING_UPDATED);
	}

	@Test
	public void savetest() {
		UserMachineMapping machineMapping = new UserMachineMapping();
		Mockito.when(machineMappingRepository.save(Mockito.any(UserMachineMapping.class))).thenReturn(machineMapping);
		Assert.assertSame(machineMappingDAOImpl.save(machineMapping), RegistrationConstants.MACHINE_MAPPING_CREATED);
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void saveFailuretest() throws RegBaseCheckedException {

		UserMachineMapping machineMapping = new UserMachineMapping();
		Mockito.when(machineMappingRepository.save(Mockito.any(UserMachineMapping.class)))
				.thenThrow(RuntimeException.class);
		machineMappingDAOImpl.save(machineMapping);
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void updateFailuretest() throws RegBaseCheckedException {

		UserMachineMapping machineMapping = new UserMachineMapping();
		Mockito.when(machineMappingRepository.update(Mockito.any(UserMachineMapping.class)))
				.thenThrow(RuntimeException.class);
		machineMappingDAOImpl.update(machineMapping);
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void findByIDFailuretest() throws RegBaseCheckedException {
		UserMachineMappingID machineMapping = new UserMachineMappingID();
		Mockito.when(machineMappingRepository.findById(Mockito.any(), Mockito.any())).thenThrow(RuntimeException.class);
		machineMappingDAOImpl.findByID(machineMapping);
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void getStationIDRunException() throws RegBaseCheckedException {
		Mockito.when(machineMasterRepository.findByMacAddress(Mockito.anyString()))
				.thenThrow(new RegBaseUncheckedException());
		machineMappingDAOImpl.getStationID("8C-16-45-88-E7-0B");
	} 

	@Test
	public void getStationID() throws RegBaseCheckedException {
		MachineMaster machineMaster = new MachineMaster();
		machineMaster.setMacAddress("8C-16-45-88-E7-0C");
		machineMaster.setId("StationID1947");
		Mockito.when(machineMasterRepository.findByMacAddress(Mockito.anyString())).thenReturn(machineMaster);
		String stationId = machineMappingDAOImpl.getStationID("8C-16-45-88-E7-0C");
		Assert.assertSame("StationID1947", stationId);
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void getCenterIDRunExceptionTest() throws RegBaseCheckedException {
		Mockito.when(centerMachineRepository.findByCenterMachineIdId(Mockito.anyString()))
				.thenThrow(new RegBaseUncheckedException());
		machineMappingDAOImpl.getCenterID("StationID1947");
	}

	@Test
	public void getCenterID() throws RegBaseCheckedException {
		CenterMachineId centerMachineId = new CenterMachineId();
		centerMachineId.setId("StationID1947");
		centerMachineId.setCentreId("CenterID1947");

		CenterMachine centerMachine = new CenterMachine();
		centerMachine.setCenterMachineId(centerMachineId);

		Mockito.when(centerMachineRepository.findByCenterMachineIdId(Mockito.anyString())).thenReturn(centerMachine);
		String stationId = machineMappingDAOImpl.getCenterID("StationID1947");
		Assert.assertSame("CenterID1947", stationId);
	}

	@Test
	public void findByIDTest() {
		UserMachineMappingID userID = new UserMachineMappingID();
		userID.setUserID("USR1234");
		userID.setCentreID("CNTR123");
		userID.setMachineID("MCHN123");

		UserMachineMapping machineMapping = new UserMachineMapping();
		Mockito.when(machineMappingRepository.findById(Mockito.any(), Mockito.any())).thenReturn(machineMapping);
		Mockito.when(machineMappingRepository.save(Mockito.any(UserMachineMapping.class))).thenReturn(machineMapping);
		Assert.assertNotNull(machineMappingDAOImpl.findByID(userID));
	}

	@Test
	public void findByIDTestNull() {
		UserMachineMappingID userID = null;

		UserMachineMapping machineMapping = new UserMachineMapping();
		Mockito.when(machineMappingRepository.findById(Mockito.any(), Mockito.any())).thenReturn(null);
		Mockito.when(machineMappingRepository.save(Mockito.any(UserMachineMapping.class))).thenReturn(machineMapping);
		Assert.assertNull(machineMappingDAOImpl.findByID(userID));
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void getUsersRunException() throws RegBaseCheckedException {
		UserContext userContext = SessionContext.getInstance().getUserContext();
		userContext.setUserId("ID007");
		Mockito.when(userDetailRepository
				.findByRegistrationCenterUserRegistrationCenterUserIdRegcntrIdAndIsActiveTrueAndStatusCodeNotLikeAndIdNotLike(
						"Center123", RegistrationConstants.BLACKLISTED, userContext.getUserId()))
				.thenThrow(new RegBaseUncheckedException());
		machineMappingDAOImpl.getUsers("Center123");
	}

	@Test
	public void getUsers() throws RegBaseCheckedException {
		UserContext userContext = SessionContext.getInstance().getUserContext();
		userContext.setUserId("ID007");

		List<RegistrationUserDetail> registrationUserDetailList = new ArrayList<>();

		// Sample Data 1
		Set<RegistrationUserRole> registrationUserRolesList = new HashSet<>();
		Set<UserMachineMapping> regUserMachineMappingList = new HashSet<>();
		UserMachineMappingID userMachineMappingID = new UserMachineMappingID();
		userMachineMappingID.setCentreID("Center123");
		userMachineMappingID.setMachineID("StationID1947");
		userMachineMappingID.setUserID("ID007");

		UserMachineMapping userMachineMapping = new UserMachineMapping();
		userMachineMapping.setUserMachineMappingId(userMachineMappingID);
		regUserMachineMappingList.add(userMachineMapping);

		RegistrationUserRoleID registrationUserRoleID = new RegistrationUserRoleID();
		registrationUserRoleID.setRoleCode("Super Admin");
		registrationUserRoleID.setUsrId("ID007");

		RegistrationUserRole registrationUserRole = new RegistrationUserRole();
		registrationUserRole.setIsActive(true);
		registrationUserRole.setRegistrationUserRoleID(registrationUserRoleID);
		registrationUserRolesList.add(registrationUserRole);

		RegistrationUserDetail registrationUserDetail = new RegistrationUserDetail();
		registrationUserDetail.setId("ID007");
		registrationUserDetail.setName("testName");
		registrationUserDetail.setStatusCode("Active");
		registrationUserDetail.setUserMachineMapping(regUserMachineMappingList);
		registrationUserDetail.setUserRole(registrationUserRolesList);

		// Sample Data2
		Set<RegistrationUserRole> registrationUserRolesList1 = new HashSet<>();
		Set<UserMachineMapping> regUserMachineMappingList2 = new HashSet<>();
		UserMachineMappingID userMachineMappingID1 = new UserMachineMappingID();
		userMachineMappingID1.setCentreID("Center123");
		userMachineMappingID1.setMachineID("StationID1947");
		userMachineMappingID1.setUserID("ID008");

		UserMachineMapping userMachineMapping1 = new UserMachineMapping();
		userMachineMapping1.setUserMachineMappingId(userMachineMappingID1);
		regUserMachineMappingList2.add(userMachineMapping1);

		RegistrationUserRoleID registrationUserRoleID1 = new RegistrationUserRoleID();
		registrationUserRoleID1.setRoleCode("Supervisor");
		registrationUserRoleID1.setUsrId("ID008");

		RegistrationUserRole registrationUserRole1 = new RegistrationUserRole();
		registrationUserRole1.setIsActive(true);
		registrationUserRole1.setRegistrationUserRoleID(registrationUserRoleID1);

		registrationUserRolesList1.add(registrationUserRole1);

		RegistrationUserDetail registrationUserDetail1 = new RegistrationUserDetail();
		registrationUserDetail1.setId("ID008");
		registrationUserDetail1.setName("testName1");
		registrationUserDetail1.setStatusCode("Active");
		registrationUserDetail1.setUserMachineMapping(regUserMachineMappingList2);
		registrationUserDetail1.setUserRole(registrationUserRolesList1);

		registrationUserDetailList.add(registrationUserDetail);
		registrationUserDetailList.add(registrationUserDetail1);

		Mockito.when(userDetailRepository
				.findByRegistrationCenterUserRegistrationCenterUserIdRegcntrIdAndIsActiveTrueAndStatusCodeNotLikeAndIdNotLike(
						"Center123", RegistrationConstants.BLACKLISTED, userContext.getUserId()))
				.thenReturn(registrationUserDetailList);

		List<RegistrationUserDetail> details = machineMappingDAOImpl.getUsers("Center123");

		Assert.assertSame("ID007", details.get(0).getId());
	}

	@Test
	public void getAllDeviceTypesTest() {
		List<DeviceType> deviceTypes = new ArrayList<>();

		Mockito.when(deviceTypeRepository.findByIsActiveTrue()).thenReturn(deviceTypes);

		List<DeviceType> expectedDeviceTypes = machineMappingDAOImpl.getAllDeviceTypes();

		Assert.assertThat(expectedDeviceTypes, is(deviceTypes));
	}

	@Test
	public void getAllValidDevicesByCenterIdTest() {
		List<RegCenterDevice> regCenterDevices = new ArrayList<>();

		Mockito.when(registrationCenterDeviceRepository
				.findByRegCenterDeviceIdRegCenterIdAndIsActiveTrueAndRegDeviceMasterValidityEndDtimesGreaterThanEqual(
						Mockito.anyString(), Mockito.any(Timestamp.class)))
				.thenReturn(regCenterDevices);

		List<RegCenterDevice> expectedregCenterDevices = machineMappingDAOImpl.getAllValidDevicesByCenterId("C001");

		Assert.assertThat(expectedregCenterDevices, is(regCenterDevices));
	}

	@Test
	public void getAllMappedDevicesTest() {
		List<RegCentreMachineDevice> centreMachineDevices = new ArrayList<>();

		Mockito.when(registrationCenterMachineDeviceRepository
				.findByRegCentreMachineDeviceIdRegCentreIdAndRegCentreMachineDeviceIdMachineId(Mockito.anyString(),
						Mockito.anyString()))
				.thenReturn(centreMachineDevices);

		List<RegCentreMachineDevice> actualRegCentreMachineDevice = machineMappingDAOImpl.getAllMappedDevices("M001", "M001");

		Assert.assertThat(actualRegCentreMachineDevice, is(centreMachineDevices));

	}

	@Test
	public void deleteUnMappedDeviceTest() {
		Mockito.doNothing().when(registrationCenterMachineDeviceRepository).deleteAll();

		machineMappingDAOImpl.deleteUnMappedDevice(new ArrayList<>());
	}

	@Test
	public void addedMappedDeviceTest() {
		Mockito.when(registrationCenterMachineDeviceRepository.saveAll(Mockito.anyListOf(RegCentreMachineDevice.class)))
				.thenReturn(new ArrayList<>());

		machineMappingDAOImpl.addedMappedDevice(new ArrayList<>());
	}
	
	
	@Test
	public void getCenterIDNullTest()  {
		Mockito.when(centerMachineRepository.findByCenterMachineIdId(Mockito.anyString())).thenReturn(null);
		try {
		machineMappingDAOImpl.getCenterID("StationID1947");
		} catch (RegBaseCheckedException regBaseCheckedException) {
			Assert.assertNotNull(regBaseCheckedException);
		}
	}
	@Test
	public void getStationIDNullTest()  {
		Mockito.when(centerMachineRepository.findByCenterMachineIdId(Mockito.anyString())).thenReturn(null);
		try {
			machineMappingDAOImpl.getStationID("8C-16-45-88-E7-0C");
		} catch (RegBaseCheckedException regBaseCheckedException) {
			Assert.assertNotNull(regBaseCheckedException);
		}
	}
	@Test
	public void getUsersNullTest()  {
		Mockito.when(centerMachineRepository.findByCenterMachineIdId(Mockito.anyString())).thenReturn(null);
		try {
			machineMappingDAOImpl.getUsers("8C-16-45-88-E7-0C");
		} catch (RegBaseCheckedException regBaseCheckedException) {
			Assert.assertNotNull(regBaseCheckedException);
		}
	}

}
