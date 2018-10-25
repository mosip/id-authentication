package io.mosip.registration.test.clientmachinemapping;

import static org.mockito.Mockito.doNothing;

import java.io.IOException;
import java.net.URISyntaxException;
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
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.constants.AppModule;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.context.SessionContext.UserContext;
import io.mosip.registration.dao.impl.MachineMappingDAOImpl;
import io.mosip.registration.entity.CenterMachine;
import io.mosip.registration.entity.CenterMachineId;
import io.mosip.registration.entity.MachineMaster;
import io.mosip.registration.entity.RegistrationUserDetail;
import io.mosip.registration.entity.RegistrationUserRole;
import io.mosip.registration.entity.RegistrationUserRoleID;
import io.mosip.registration.entity.UserMachineMapping;
import io.mosip.registration.entity.UserMachineMappingID;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.CenterMachineRepository;
import io.mosip.registration.repositories.MachineMasterRepository;
import io.mosip.registration.repositories.RegistrationUserDetailRepository;
import io.mosip.registration.repositories.UserMachineMappingRepository;

public class UserClientMachineMappingDAOTest {

	@Mock
	private UserMachineMappingRepository machineMappingRepository;

	@Mock
	private MosipLogger logger;
	private MosipRollingFileAppender mosipRollingFileAppender;

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
	private AuditFactory auditFactory;

	@Before
	public void initialize() throws IOException, URISyntaxException {
		mosipRollingFileAppender = new MosipRollingFileAppender();
		mosipRollingFileAppender.setAppenderName("org.apache.log4j.RollingFileAppender");
		mosipRollingFileAppender.setFileName("logs");
		mosipRollingFileAppender.setFileNamePattern("logs/registration-processor-%d{yyyy-MM-dd-HH-mm}-%i.log");
		mosipRollingFileAppender.setMaxFileSize("1MB");
		mosipRollingFileAppender.setTotalCap("10MB");
		mosipRollingFileAppender.setMaxHistory(10);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(true);

		ReflectionTestUtils.setField(RegBaseUncheckedException.class, "LOGGER", logger);
		ReflectionTestUtils.setField(RegBaseCheckedException.class, "LOGGER", logger);
		ReflectionTestUtils.invokeMethod(machineMappingDAOImpl, "initializeLogger", mosipRollingFileAppender);
		ReflectionTestUtils.setField(machineMappingDAOImpl, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());
		doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(AppModule.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void updateTest() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(machineMappingDAOImpl, "LOGGER", logger);

		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

		UserMachineMapping machineMapping = new UserMachineMapping();
		Mockito.when(machineMappingRepository.update(Mockito.any(UserMachineMapping.class))).thenReturn(machineMapping);
		Assert.assertSame(machineMappingDAOImpl.update(machineMapping), RegistrationConstants.MACHINE_MAPPING_UPDATED);
	}

	@Test
	public void savetest() {
		ReflectionTestUtils.setField(machineMappingDAOImpl, "LOGGER", logger);

		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

		UserMachineMapping machineMapping = new UserMachineMapping();
		Mockito.when(machineMappingRepository.save(Mockito.any(UserMachineMapping.class))).thenReturn(machineMapping);
		Assert.assertSame(machineMappingDAOImpl.save(machineMapping), RegistrationConstants.MACHINE_MAPPING_CREATED);
	}
	@Test(expected = RegBaseUncheckedException.class)
	public void saveFailuretest() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(machineMappingDAOImpl, "LOGGER", logger);

		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

		UserMachineMapping machineMapping = new UserMachineMapping();
		Mockito.when(machineMappingRepository.save(Mockito.any(UserMachineMapping.class))).thenThrow(RuntimeException.class);
		machineMappingDAOImpl.save(machineMapping);
	}
	@Test(expected = RegBaseUncheckedException.class)
	public void updateFailuretest() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(machineMappingDAOImpl, "LOGGER", logger);

		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

		UserMachineMapping machineMapping = new UserMachineMapping();
		Mockito.when(machineMappingRepository.update(Mockito.any(UserMachineMapping.class))).thenThrow(RuntimeException.class);
		machineMappingDAOImpl.update(machineMapping);
	}
	@Test(expected = RegBaseUncheckedException.class)
	public void findByIDFailuretest() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(machineMappingDAOImpl, "LOGGER", logger);

		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

		UserMachineMappingID machineMapping = new UserMachineMappingID();
		Mockito.when(machineMappingRepository.findById(Mockito.any(),Mockito.any())).thenThrow(RuntimeException.class);
		machineMappingDAOImpl.findByID(machineMapping);
	}

	@Test(expected = RegBaseCheckedException.class)
	public void getStationIDNullException() throws RegBaseCheckedException {
		Mockito.when(machineMasterRepository.findByMacAddress(Mockito.anyString()))
				.thenThrow(new NullPointerException());
		machineMappingDAOImpl.getStationID("8C-16-45-88-E7-0B");
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
	@Test(expected = RegBaseCheckedException.class)
	public void getCenterIDNullExceptionTest() throws RegBaseCheckedException {
		Mockito.when(centerMachineRepository.findByCenterMachineIdId(Mockito.anyString())).thenThrow(new NullPointerException());
		machineMappingDAOImpl.getCenterID("StationID1947");
	}
	@Test(expected = RegBaseUncheckedException.class)
	public void getCenterIDRunExceptionTest() throws RegBaseCheckedException {
		Mockito.when(centerMachineRepository.findByCenterMachineIdId(Mockito.anyString())).thenThrow(new RegBaseUncheckedException());
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
		ReflectionTestUtils.setField(machineMappingDAOImpl, "LOGGER", logger);

		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());
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
		ReflectionTestUtils.setField(machineMappingDAOImpl, "LOGGER", logger);

		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());
		UserMachineMappingID userID = null;

		UserMachineMapping machineMapping = new UserMachineMapping();
		Mockito.when(machineMappingRepository.findById(Mockito.any(), Mockito.any())).thenReturn(null);
		Mockito.when(machineMappingRepository.save(Mockito.any(UserMachineMapping.class))).thenReturn(machineMapping);
		Assert.assertNull(machineMappingDAOImpl.findByID(userID));
	}
	
	@Test(expected = RegBaseCheckedException.class)
	public void getUsersNullException() throws RegBaseCheckedException {
		UserContext userContext = SessionContext.getInstance().getUserContext();
		userContext.setUserId("ID007");
		Mockito.when(userDetailRepository.findByCntrIdAndIsActiveTrueAndUserStatusNotLikeAndIdNotLike("Center123",
				RegistrationConstants.BLACKLISTED, userContext.getUserId())).thenThrow(new NullPointerException());
		machineMappingDAOImpl.getUsers("Center123");		
	}  
	@Test(expected = RegBaseUncheckedException.class)
	public void getUsersRunException() throws RegBaseCheckedException {
		UserContext userContext = SessionContext.getInstance().getUserContext();
		userContext.setUserId("ID007");
		Mockito.when(userDetailRepository.findByCntrIdAndIsActiveTrueAndUserStatusNotLikeAndIdNotLike("Center123",
				RegistrationConstants.BLACKLISTED, userContext.getUserId())).thenThrow(new RegBaseUncheckedException());
		machineMappingDAOImpl.getUsers("Center123");		
	}
	

	@Test
	public void getUsers() throws RegBaseCheckedException {
		UserContext userContext = SessionContext.getInstance().getUserContext();
		userContext.setUserId("ID007");

		List<RegistrationUserDetail> registrationUserDetailList = new ArrayList<>();

		// Sample Data 1
		Set<RegistrationUserRole> registrationUserRolesList = new HashSet();
		Set<UserMachineMapping> regUserMachineMappingList = new HashSet();
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
		registrationUserDetail.setCntrId("Center123");
		registrationUserDetail.setId("ID007");
		registrationUserDetail.setName("testName");
		registrationUserDetail.setUserStatus("Active");
		registrationUserDetail.setUserMachineMapping(regUserMachineMappingList);
		registrationUserDetail.setUserRole(registrationUserRolesList);

		// Sample Data2
		Set<RegistrationUserRole> registrationUserRolesList1 = new HashSet();
		Set<UserMachineMapping> regUserMachineMappingList2 = new HashSet();
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
		registrationUserDetail1.setCntrId("Center123");
		registrationUserDetail1.setId("ID008");
		registrationUserDetail1.setName("testName1");
		registrationUserDetail1.setUserStatus("Active");
		registrationUserDetail1.setUserMachineMapping(regUserMachineMappingList2);
		registrationUserDetail1.setUserRole(registrationUserRolesList1);

		registrationUserDetailList.add(registrationUserDetail);
		registrationUserDetailList.add(registrationUserDetail1);

		Mockito.when(userDetailRepository.findByCntrIdAndIsActiveTrueAndUserStatusNotLikeAndIdNotLike("Center123",
				RegistrationConstants.BLACKLISTED, userContext.getUserId())).thenReturn(registrationUserDetailList);

		List<RegistrationUserDetail> details = machineMappingDAOImpl.getUsers("Center123");

		Assert.assertSame("ID007", details.get(0).getId());
		// Assert.assertSame("Super Admin", details.get(0).getUserRole());

		// Assert.assertSame("Supervisor", details.get(0).getUserStatus());
		// Assert.assertSame("Supervisor", details.get(0).getUserRole());

	}

}
