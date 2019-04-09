package io.mosip.kernel.syncdata.test.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.syncdata.dto.ApplicationDto;
import io.mosip.kernel.syncdata.dto.HolidayDto;
import io.mosip.kernel.syncdata.dto.MachineDto;
import io.mosip.kernel.syncdata.dto.MachineSpecificationDto;
import io.mosip.kernel.syncdata.dto.MachineTypeDto;
import io.mosip.kernel.syncdata.dto.PublicKeyResponse;
import io.mosip.kernel.syncdata.dto.SyncUserDetailDto;
import io.mosip.kernel.syncdata.dto.UserDetailMapDto;
import io.mosip.kernel.syncdata.dto.response.MasterDataResponseDto;
import io.mosip.kernel.syncdata.dto.response.RolesResponseDto;
import io.mosip.kernel.syncdata.entity.RegistrationCenterUser;
import io.mosip.kernel.syncdata.entity.id.RegistrationCenterUserID;
import io.mosip.kernel.syncdata.exception.DataNotFoundException;
import io.mosip.kernel.syncdata.exception.SyncDataServiceException;
import io.mosip.kernel.syncdata.repository.RegistrationCenterUserRepository;
import io.mosip.kernel.syncdata.service.RegistrationCenterUserService;
import io.mosip.kernel.syncdata.service.SyncConfigDetailsService;
import io.mosip.kernel.syncdata.service.SyncMasterDataService;
import io.mosip.kernel.syncdata.service.SyncRolesService;
import io.mosip.kernel.syncdata.service.SyncUserDetailsService;
import io.mosip.kernel.syncdata.utils.SigningUtil;
import net.minidev.json.JSONObject;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class SyncDataControllerTest {
	private MasterDataResponseDto masterDataResponseDto;
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private SyncMasterDataService masterDataService;

	@MockBean
	private SyncConfigDetailsService syncConfigDetailsService;

	@MockBean
	private SyncUserDetailsService syncUserDetailsService;
	
	@MockBean
	private SyncRolesService syncRolesService;

	@Autowired
	private RegistrationCenterUserService registrationCenterUserService;

	@MockBean
	private RegistrationCenterUserRepository registrationCenterUserRepository;
	
	@MockBean
	private SigningUtil signingUtil;

	JSONObject globalConfigMap = null;
	JSONObject regCentreConfigMap = null;

	@Before
	public void setup() {

		configDetialsSyncSetup();
		syncMasterDataSetup();
		getUsersBasedOnRegCenterSetUp();

	}

	SyncUserDetailDto syncUserDetailDto;
	List<UserDetailMapDto> users;
	UserDetailMapDto userDetailMapDto;

	public void getUsersBasedOnRegCenterSetUp() {
		List<String> roles = new ArrayList<>();
		roles.add("admin");
		roles.add("superAdmin");
		syncUserDetailDto = new SyncUserDetailDto();
		users = new ArrayList<>();
		userDetailMapDto = new UserDetailMapDto();
		userDetailMapDto.setMail("mosip@gmail.com");
		userDetailMapDto.setMobile("9988866600");
		userDetailMapDto.setName("100022");
		userDetailMapDto.setUserName("individula");
		userDetailMapDto.setRoles(roles);
		users.add(userDetailMapDto);
		syncUserDetailDto.setUserDetails(users);
	}

	public void configDetialsSyncSetup() {
		globalConfigMap = new JSONObject();
		globalConfigMap.put("archivalPolicy", "arc_policy_2");
		globalConfigMap.put("otpTimeOutInMinutes", 2);
		globalConfigMap.put("numberOfWrongAttemptsForOtp", 5);
		globalConfigMap.put("uinLength", 24);

		regCentreConfigMap = new JSONObject();

		regCentreConfigMap.put("fingerprintQualityThreshold", 120);
		regCentreConfigMap.put("irisQualityThreshold", 25);
		regCentreConfigMap.put("irisRetryAttempts", 10);
		regCentreConfigMap.put("faceQualityThreshold", 25);
		regCentreConfigMap.put("faceRetry", 12);
		regCentreConfigMap.put("supervisorVerificationRequiredForExceptions", true);
		regCentreConfigMap.put("operatorRegSubmissionMode", "fingerprint");

	}

	public void syncMasterDataSetup() {
		masterDataResponseDto = new MasterDataResponseDto();
		List<ApplicationDto> applications = new ArrayList<>();
		applications.add(new ApplicationDto("01", "REG FORM", "REG Form"));
		masterDataResponseDto.setApplications(applications);
		List<HolidayDto> holidays = new ArrayList<>();
		holidays.add(new HolidayDto("1", "2018-01-01", "01", "01", "2018", "NEW YEAR", "LOC01"));
		masterDataResponseDto.setHolidays(holidays);
		List<MachineDto> machines = new ArrayList<>();
		machines.add(new MachineDto("1001", "Laptop", "QWE23456", "1223:23:31:23", "172.12.128.1", "1",
				LocalDateTime.parse("2018-01-01T01:01:01")));
		masterDataResponseDto.setMachineDetails(machines);
		List<MachineSpecificationDto> machineSpecifications = new ArrayList<>();
		machineSpecifications.add(new MachineSpecificationDto("1", "lenovo Thinkpad", "Lenovo", "T480", "1", "1.0.1",
				"Thinkpad"));
		masterDataResponseDto.setMachineSpecification(machineSpecifications);
		List<MachineTypeDto> machineTypes = new ArrayList<>();
		machineTypes.add(new MachineTypeDto("1", "ENG", "Laptop"));
		masterDataResponseDto.setMachineType(machineTypes);
	}

	@Test
	public void syncGlobalConfigDetailsSuccess() throws Exception {
		when(signingUtil.signResponseData(Mockito.anyString())).thenReturn("EWQRFDSERDWSRDSRSDF");
		when(syncConfigDetailsService.getGlobalConfigDetails()).thenReturn(globalConfigMap);
		mockMvc.perform(get("/globalconfigs")).andExpect(status().isOk());
	}

	@Test
	public void syncRegistrationConfigDetailsSuccess() throws Exception {
		when(signingUtil.signResponseData(Mockito.anyString())).thenReturn("EWQRFDSERDWSRDSRSDF");
		when(syncConfigDetailsService.getRegistrationCenterConfigDetails(Mockito.anyString()))
				.thenReturn(globalConfigMap);
		mockMvc.perform(get("/registrationcenterconfig/1")).andExpect(status().isOk());
	}

	@Test
	public void syncGlobalConfigDetailsFailure() throws Exception {
		when(syncConfigDetailsService.getGlobalConfigDetails())
				.thenThrow(new SyncDataServiceException("KER-SYNC-127", "Error occured in service"));
		mockMvc.perform(get("/globalconfigs")).andExpect(status().isInternalServerError());
	}

	@Test
	public void getUsersBasedOnRegCenter() throws Exception {
		String regId = "110044";
		when(signingUtil.signResponseData(Mockito.anyString())).thenReturn("EWQRFDSERDWSRDSRSDF");
		when(syncUserDetailsService.getAllUserDetail(regId)).thenReturn(syncUserDetailDto);
		mockMvc.perform(get("/userdetails/{regid}", "110044")).andExpect(status().isOk());

	}

	@Test
	public void getUsersBasedOnRegCenterFailure() throws Exception {
		String regId = "110044";
		when(syncUserDetailsService.getAllUserDetail(regId))
				.thenThrow(new SyncDataServiceException("KER-SYNC-301", "Error occured while fetching User Details"));
		mockMvc.perform(get("/userdetails/{regid}", "110044")).andExpect(status().isInternalServerError());

	}

	@Test(expected = SyncDataServiceException.class)
	public void syncMasterDataRegistrationCenterUserFetchException() throws Exception {

		when(registrationCenterUserRepository.findByRegistrationCenterUserByRegCenterId(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		registrationCenterUserService.getUsersBasedOnRegistrationCenterId("110044");
	}

	@Test(expected = DataNotFoundException.class)
	public void getRegistrationCenterUserMasterDataNotFoundExcepetion() throws Exception {
		when(registrationCenterUserRepository.findByRegistrationCenterUserByRegCenterId(Mockito.anyString()))
				.thenReturn(new ArrayList<RegistrationCenterUser>());
		registrationCenterUserService.getUsersBasedOnRegistrationCenterId("110044");

	}

	@Test
	public void getRegistrationCenterUsers() throws Exception {
		RegistrationCenterUser registrationCenterUser = new RegistrationCenterUser();
		RegistrationCenterUserID registrationCenterUserID = new RegistrationCenterUserID();
		registrationCenterUserID.setRegCenterId("110044");
		registrationCenterUserID.setUserId("individual");
		registrationCenterUser.setRegistrationCenterUserID(registrationCenterUserID);
		List<RegistrationCenterUser> regList = new ArrayList<>();
		regList.add(registrationCenterUser);
		when(registrationCenterUserRepository.findByRegistrationCenterUserByRegCenterId(Mockito.anyString()))
				.thenReturn(regList);

		registrationCenterUserService.getUsersBasedOnRegistrationCenterId("110044");
	}

	// -----------------------public key-------------------------------------//

	@Test
	public void getPublicKey() throws Exception {
		PublicKeyResponse<String> publicKeyResponse = new PublicKeyResponse<>();
		publicKeyResponse.setPublicKey("aasfdsfsadfdsaf");
		when(signingUtil.signResponseData(Mockito.anyString())).thenReturn("EWQRFDSERDWSRDSRSDF");
		Mockito.when(syncConfigDetailsService.getPublicKey(Mockito.anyString(), Mockito.anyString(), Mockito.any()))
				.thenReturn(publicKeyResponse);
		mockMvc.perform(get("/publickey/REGISTRATION").param("timeStamp", "2019-09-09T09%3A00%3A00.000Z")).andExpect(status().isOk());
	}
	
	//-----------------AllRoles-------------------------------//
	
	@Test
	public void getAllRoles() throws Exception{
		when(signingUtil.signResponseData(Mockito.anyString())).thenReturn("EWQRFDSERDWSRDSRSDF");
		RolesResponseDto rolesResponseDto= new RolesResponseDto();
		rolesResponseDto.setLastSyncTime("2019-09-09T09:09:09.000Z");
		Mockito.when(syncRolesService.getAllRoles()).thenReturn(rolesResponseDto);
		mockMvc.perform(get("/roles")).andExpect(status().isOk());
	}
}
