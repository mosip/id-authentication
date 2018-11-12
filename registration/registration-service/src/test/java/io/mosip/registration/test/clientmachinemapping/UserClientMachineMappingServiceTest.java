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
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import io.mosip.registration.audit.AuditFactoryImpl;
import io.mosip.registration.constants.AppModule;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.MachineMappingDAO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.UserMachineMappingDTO;
import io.mosip.registration.entity.RegistrationUserDetail;
import io.mosip.registration.entity.RegistrationUserRole;
import io.mosip.registration.entity.RegistrationUserRoleID;
import io.mosip.registration.entity.UserMachineMapping;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.impl.MapMachineServiceImpl;
import io.mosip.registration.util.healthcheck.RegistrationSystemPropertiesChecker;
import io.mosip.registration.entity.UserMachineMappingID;

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
		registrationUserDetail.setCntrId("CenterID123");
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
}
