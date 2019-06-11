package io.mosip.registration.test.login;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.audit.AuditManagerSerivceImpl;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dao.AppAuthenticationDAO;
import io.mosip.registration.dao.AppAuthenticationDetails;
import io.mosip.registration.dao.RegistrationCenterDAO;
import io.mosip.registration.dao.ScreenAuthorizationDAO;
import io.mosip.registration.dao.UserDetailDAO;
import io.mosip.registration.dao.ScreenAuthorizationDetails;
import io.mosip.registration.dto.AuthorizationDTO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
<<<<<<< HEAD
=======
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.UserDTO;
import io.mosip.registration.entity.MachineMaster;
import io.mosip.registration.entity.RegCenterUser;
>>>>>>> 4483d04c7d451fda25350bad5c0d157b05369082
import io.mosip.registration.entity.RegistrationCenter;
import io.mosip.registration.entity.UserDetail;
import io.mosip.registration.entity.UserMachineMapping;
import io.mosip.registration.entity.UserRole;
import io.mosip.registration.entity.id.RegCenterUserId;
import io.mosip.registration.entity.id.UserMachineMappingID;
import io.mosip.registration.entity.id.UserRoleID;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.repositories.AppAuthenticationRepository;
import io.mosip.registration.repositories.RegistrationCenterRepository;
import io.mosip.registration.repositories.ScreenAuthorizationRepository;
import io.mosip.registration.repositories.UserDetailRepository;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.login.impl.LoginServiceImpl;
import io.mosip.registration.service.operator.UserDetailService;
import io.mosip.registration.service.operator.UserOnboardService;
import io.mosip.registration.service.operator.UserSaltDetailsService;
import io.mosip.registration.service.sync.MasterSyncService;
import io.mosip.registration.service.sync.PublicKeySync;
import io.mosip.registration.service.sync.TPMPublicKeySyncService;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ApplicationContext.class })
public class LoginServiceTest {

	@Mock
	private AuditManagerSerivceImpl auditFactory;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private LoginServiceImpl loginServiceImpl;

	@Mock
	private AppAuthenticationRepository appAuthenticationRepository;

	@Mock
	private AppAuthenticationDAO appAuthenticationDAO;

	@Mock
	private UserDetailRepository userDetailRepository;

	@Mock
	private UserDetailDAO userDetailDAO;

	@Mock
	private RegistrationCenterRepository registrationCenterRepository;

	@Mock
	private RegistrationCenterDAO registrationCenterDAO;

	@Mock
	private ScreenAuthorizationRepository screenAuthorizationRepository;

	@Mock
	private ScreenAuthorizationDAO screenAuthorizationDAO;

	@Mock
	private PublicKeySync publicKeySyncImpl;

	@Mock
	private GlobalParamService globalParamService;

	@Mock
	private MasterSyncService masterSyncService;

	@Mock
	private UserDetailService userDetailService;

	@Mock
	private UserOnboardService userOnboardService;

	@Mock
	private UserSaltDetailsService userSaltDetailsService;
	@Mock
	private TPMPublicKeySyncService tpmPublicKeySyncService;
	
	@Before
	public void initialize() throws IOException, URISyntaxException {
		doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(Components.class),
				Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void getModesOfLoginTest() {

		List<AppAuthenticationDetails> loginList = new ArrayList<AppAuthenticationDetails>();
		Set<String> roleSet = new HashSet<>();
		roleSet.add("OFFICER");
		Mockito.when(appAuthenticationRepository
				.findByIsActiveTrueAndAppAuthenticationMethodIdProcessIdAndAppAuthenticationMethodIdRoleCodeInOrderByMethodSequence(
						"LOGIN", roleSet))
				.thenReturn(loginList);

		List<String> modes = new ArrayList<>();
		loginList.stream().map(loginMethod -> loginMethod.getAppAuthenticationMethodId().getAuthMethodCode())
				.collect(Collectors.toList());

		Mockito.when(appAuthenticationRepository
				.findByIsActiveTrueAndAppAuthenticationMethodIdProcessIdAndAppAuthenticationMethodIdRoleCodeInOrderByMethodSequence(
						"LOGIN", roleSet))
				.thenReturn(loginList);

<<<<<<< HEAD
	@Test
=======
		Mockito.when(appAuthenticationDAO.getModesOfLogin("LOGIN", roleSet)).thenReturn(modes);
		assertEquals(modes, loginServiceImpl.getModesOfLogin("LOGIN", roleSet));
	}

	@Test
>>>>>>> 4483d04c7d451fda25350bad5c0d157b05369082
	public void getUserDetailTest() {

		UserDetail userDetail = new UserDetail();
		List<UserDetail> userDetailList = new ArrayList<UserDetail>();
		userDetailList.add(userDetail);
		Mockito.when(userDetailRepository.findByIdIgnoreCaseAndIsActiveTrue(Mockito.anyString()))
				.thenReturn(userDetailList);

		Mockito.when(userDetailDAO.getUserDetail(Mockito.anyString())).thenReturn(userDetail);
<<<<<<< HEAD
		
		assertEquals(userDetail,loginServiceImpl.getUserDetail("mosip"));		
=======

		UserDTO userDTO = new UserDTO();
		userDTO.setId(userDetail.getId());

		assertEquals(userDTO, loginServiceImpl.getUserDetail("mosip"));
>>>>>>> 4483d04c7d451fda25350bad5c0d157b05369082
	}

	@Test
	public void getRegistrationCenterDetailsTest() {

		RegistrationCenter registrationCenter = new RegistrationCenter();

		RegistrationCenterDetailDTO centerDetailDTO = new RegistrationCenterDetailDTO();
		Optional<RegistrationCenter> registrationCenterList = Optional.of(registrationCenter);
		Mockito.when(
				registrationCenterRepository.findByIsActiveTrueAndRegistartionCenterIdIdAndRegistartionCenterIdLangCode(
						Mockito.anyString(), Mockito.anyString()))
				.thenReturn(registrationCenterList);

		Mockito.when(registrationCenterDAO.getRegistrationCenterDetails(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(centerDetailDTO);

		assertEquals(centerDetailDTO, loginServiceImpl.getRegistrationCenterDetails("mosip", "eng"));
	}

	@Test
	public void getScreenAuthorizationDetailsTest() {

		Set<ScreenAuthorizationDetails> authorizationList = new HashSet<>();
		List<String> roleList = new ArrayList<>();
		Mockito.when(screenAuthorizationRepository
				.findByScreenAuthorizationIdRoleCodeInAndIsPermittedTrueAndIsActiveTrue(roleList))
				.thenReturn(authorizationList);
		AuthorizationDTO authorizationDTO = new AuthorizationDTO();
		Mockito.when(screenAuthorizationDAO.getScreenAuthorizationDetails(roleList)).thenReturn(authorizationDTO);
		assertNotNull(loginServiceImpl.getScreenAuthorizationDetails(roleList));

	}
<<<<<<< HEAD

	@Test
=======

	@Test
>>>>>>> 4483d04c7d451fda25350bad5c0d157b05369082
	public void updateLoginParamsTest() {
		doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(Components.class),
				Mockito.anyString(), Mockito.anyString());
		doNothing().when(userDetailDAO).updateLoginParams(Mockito.any(UserDetail.class));
<<<<<<< HEAD
		
=======

		UserDTO userDTO = new UserDTO();
		userDTO.setId("mosip");
		userDTO.setUnsuccessfulLoginCount(0);
		userDTO.setLastLoginDtimes(new Timestamp(System.currentTimeMillis()));
		userDTO.setLastLoginMethod("PWD");
		userDTO.setUserlockTillDtimes(new Timestamp(System.currentTimeMillis()));

>>>>>>> 4483d04c7d451fda25350bad5c0d157b05369082
		UserDetail userDetail = new UserDetail();
		userDetail.setId("mosip");
		userDetail.setUnsuccessfulLoginCount(0);
		userDetail.setLastLoginDtimes(new Timestamp(System.currentTimeMillis()));
<<<<<<< HEAD
		userDetail.setLastLoginMethod("PWD");
		
		loginServiceImpl.updateLoginParams(userDetail);
=======
		userDetail.setLastLoginMethod(userDTO.getLastLoginMethod());
		userDetail.setUserlockTillDtimes(userDTO.getUserlockTillDtimes());

		Mockito.when(userDetailDAO.getUserDetail(userDTO.getId())).thenReturn(userDetail);

		loginServiceImpl.updateLoginParams(userDTO);
	}

	@Test
	public void initialSyncTest() throws RegBaseCheckedException {
		Map<String, Object> applicationMap = new HashMap();
		applicationMap.put(RegistrationConstants.INITIAL_SETUP, "Y");
		applicationMap.put(RegistrationConstants.TPM_AVAILABILITY, RegistrationConstants.ENABLE);

		ResponseDTO responseDTO = new ResponseDTO();
		SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
		successResponseDTO.setOtherAttributes(new HashMap<>());
		responseDTO.setSuccessResponseDTO(successResponseDTO);

		Mockito.when(tpmPublicKeySyncService.syncTPMPublicKey()).thenReturn("MyIndex");

		Mockito.when(publicKeySyncImpl.getPublicKey(RegistrationConstants.JOB_TRIGGER_POINT_USER))
				.thenReturn(responseDTO);

		Mockito.when(globalParamService.synchConfigData(false)).thenReturn(responseDTO);

		Mockito.when(masterSyncService.getMasterSync(RegistrationConstants.OPT_TO_REG_MDS_J00001,
				RegistrationConstants.JOB_TRIGGER_POINT_USER, "MyIndex")).thenReturn(responseDTO);

		Mockito.when(userDetailService.save(RegistrationConstants.JOB_TRIGGER_POINT_USER)).thenReturn(responseDTO);
		
		Mockito.when(userSaltDetailsService
				.getUserSaltDetails(RegistrationConstants.JOB_TRIGGER_POINT_USER)).thenReturn(responseDTO);


		ApplicationContext.setApplicationMap(applicationMap);
		Assert.assertTrue(loginServiceImpl.initialSync().contains(RegistrationConstants.SUCCESS));
	}
	
	@Test
	public void initialSyncFailureTest() throws RegBaseCheckedException {
		Map<String, Object> applicationMap = new HashMap();
		applicationMap.put(RegistrationConstants.INITIAL_SETUP, RegistrationConstants.ENABLE);
		applicationMap.put(RegistrationConstants.TPM_AVAILABILITY, RegistrationConstants.ENABLE);
		
		ApplicationContext.setApplicationMap(applicationMap);

		ResponseDTO responseDTO = new ResponseDTO();
		ErrorResponseDTO errorResponseDTO=new ErrorResponseDTO();
		
		List<ErrorResponseDTO> errorResponseDTOs=new LinkedList<>();
		errorResponseDTOs.add(errorResponseDTO);
		
		responseDTO.setErrorResponseDTOs(errorResponseDTOs);
		
		Mockito.when(tpmPublicKeySyncService.syncTPMPublicKey()).thenReturn("MyIndex");

		Mockito.when(publicKeySyncImpl.getPublicKey(RegistrationConstants.JOB_TRIGGER_POINT_USER))
				.thenReturn(responseDTO);

		Mockito.when(globalParamService.synchConfigData(false)).thenReturn(responseDTO);

		Mockito.when(masterSyncService.getMasterSync(RegistrationConstants.OPT_TO_REG_MDS_J00001,
				RegistrationConstants.JOB_TRIGGER_POINT_USER, "MyIndex")).thenReturn(responseDTO);

		Mockito.when(userDetailService.save(RegistrationConstants.JOB_TRIGGER_POINT_USER)).thenReturn(responseDTO);
		
		Mockito.when(userSaltDetailsService
				.getUserSaltDetails(RegistrationConstants.JOB_TRIGGER_POINT_USER)).thenReturn(responseDTO);



		Assert.assertTrue(loginServiceImpl.initialSync().contains(RegistrationConstants.FAILURE));
	}
	
	@Test
	public void initialSyncFailureExceptionTest() throws RegBaseCheckedException {
		Map<String, Object> applicationMap = new HashMap();
		applicationMap.put(RegistrationConstants.INITIAL_SETUP, "Y");
		applicationMap.put(RegistrationConstants.TPM_AVAILABILITY, RegistrationConstants.ENABLE);
		ApplicationContext.setApplicationMap(applicationMap);

		ResponseDTO responseDTO = new ResponseDTO();
		ErrorResponseDTO errorResponseDTO=new ErrorResponseDTO();
		
		List<ErrorResponseDTO> errorResponseDTOs=new LinkedList<>();
		errorResponseDTOs.add(errorResponseDTO);
		
		responseDTO.setErrorResponseDTOs(errorResponseDTOs);
		
		Mockito.when(tpmPublicKeySyncService.syncTPMPublicKey()).thenThrow(RegBaseCheckedException.class);

		
		Assert.assertTrue(loginServiceImpl.initialSync().contains(RegistrationConstants.FAILURE));
	}
	
	@Test
	public void initialSyncFalseTest() throws RegBaseCheckedException {
		Map<String, Object> applicationMap = new HashMap();
		applicationMap.put(RegistrationConstants.INITIAL_SETUP, RegistrationConstants.DISABLE);
		applicationMap.put(RegistrationConstants.TPM_AVAILABILITY, RegistrationConstants.ENABLE);
		
		ApplicationContext.setApplicationMap(applicationMap);

		ResponseDTO responseDTO = new ResponseDTO();
		ErrorResponseDTO errorResponseDTO=new ErrorResponseDTO();
		
		List<ErrorResponseDTO> errorResponseDTOs=new LinkedList<>();
		errorResponseDTOs.add(errorResponseDTO);
		
		responseDTO.setErrorResponseDTOs(errorResponseDTOs);
		
		Mockito.when(tpmPublicKeySyncService.syncTPMPublicKey()).thenReturn("MyIndex");

		Mockito.when(publicKeySyncImpl.getPublicKey(RegistrationConstants.JOB_TRIGGER_POINT_USER))
				.thenReturn(responseDTO);

		Mockito.when(globalParamService.synchConfigData(false)).thenReturn(responseDTO);

		Mockito.when(masterSyncService.getMasterSync(RegistrationConstants.OPT_TO_REG_MDS_J00001,
				RegistrationConstants.JOB_TRIGGER_POINT_USER)).thenReturn(responseDTO);

		Mockito.when(userDetailService.save(RegistrationConstants.JOB_TRIGGER_POINT_USER)).thenReturn(responseDTO);
		
		Mockito.when(userSaltDetailsService
				.getUserSaltDetails(RegistrationConstants.JOB_TRIGGER_POINT_USER)).thenReturn(responseDTO);



		Assert.assertTrue(loginServiceImpl.initialSync().contains(RegistrationConstants.FAILURE));
	}
	
	
	@Test
	public void validateUserFailureTest() throws Exception {
		ResponseDTO responseDTO = new ResponseDTO();
		UserDetail userDetail = new UserDetail();
		UserDTO userDTO = null;
		Mockito.when(userDetailDAO.getUserDetail(Mockito.anyString())).thenReturn(userDetail);
		Mockito.when(loginServiceImpl.getUserDetail("")).thenReturn(userDTO);
		
		List<ErrorResponseDTO> errorResponseDTOs = new ArrayList<>();
		ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
		errorResponseDTO.setMessage(RegistrationConstants.USER_MACHINE_VALIDATION_MSG);
		errorResponseDTOs.add(errorResponseDTO);
		responseDTO.setErrorResponseDTOs(errorResponseDTOs);
		
		assertNotNull(loginServiceImpl.validateUser("").getErrorResponseDTOs());
		
	}
	@Test
	public void validateUserTest() throws Exception {
		ResponseDTO responseDTO = new ResponseDTO();
		UserDetail userDetail = new UserDetail();
		RegCenterUser regCenterUser = new RegCenterUser();
		RegCenterUserId regCenterUserId = new RegCenterUserId();
		regCenterUserId.setRegcntrId("10011");
		regCenterUser.setRegCenterUserId(regCenterUserId);
		userDetail.setRegCenterUser(regCenterUser);
		userDetail.setStatusCode("ACTIVE");
		Set<UserMachineMapping> userMachineMappings = new HashSet<>();
		UserMachineMapping userMachineMapping = new UserMachineMapping();
		MachineMaster machineMaster = new MachineMaster();
		machineMaster.setSerialNum("12345");
		userMachineMapping.setMachineMaster(machineMaster);
		UserMachineMappingID userMachineMappingId = new UserMachineMappingID();
		userMachineMapping.setUserMachineMappingId(userMachineMappingId);
		userMachineMapping.setIsActive(true);
		userMachineMappings.add(userMachineMapping);
		userDetail.setUserMachineMapping(userMachineMappings);
		Set<UserRole> userRoles = new HashSet<>();
		UserRole userRole = new UserRole();
		UserRoleID userRoleID = new UserRoleID();
		userRoleID.setRoleCode("REGISTRATION_OFFICER");
		userRoleID.setUsrId("mosip");
		userRole.setUserRoleID(userRoleID);
		userRole.setIsActive(true);
		userRoles.add(userRole);
		userDetail.setUserRole(userRoles);
		Mockito.when(userDetailDAO.getUserDetail(Mockito.anyString())).thenReturn(userDetail);
		
		loginServiceImpl.getUserDetail("mosip");
		
		Map<String, String> map = new HashMap<>();
		map.put(RegistrationConstants.USER_CENTER_ID, "10011");
		map.put(RegistrationConstants.USER_STATION_ID,"10011");
		Mockito.when(userOnboardService.getMachineCenterId()).thenReturn(map);
		
		HashMap<String, Object> sessionMap = new HashMap<>();
		sessionMap.put(RegistrationConstants.USER_CENTER_ID, "11011");
		
		PowerMockito.mockStatic(ApplicationContext.class);
		PowerMockito.doReturn(sessionMap).when(ApplicationContext.class, "map");
		
		SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
		successResponseDTO.setMessage(RegistrationConstants.SUCCESS);
		responseDTO.setSuccessResponseDTO(successResponseDTO);
		assertNotNull(loginServiceImpl.validateUser("mosip").getSuccessResponseDTO());
	}
	
	@Test
	public void validateUserStatusTest() throws Exception {
		ResponseDTO responseDTO = new ResponseDTO();
		UserDetail userDetail = new UserDetail();
		RegCenterUser regCenterUser = new RegCenterUser();
		RegCenterUserId regCenterUserId = new RegCenterUserId();
		regCenterUserId.setRegcntrId("10011");
		regCenterUser.setRegCenterUserId(regCenterUserId);
		userDetail.setRegCenterUser(regCenterUser);
		userDetail.setStatusCode("BLOCKED");
		Mockito.when(userDetailDAO.getUserDetail(Mockito.anyString())).thenReturn(userDetail);
		
		loginServiceImpl.getUserDetail("mosip");
		
		Map<String, String> map = new HashMap<>();
		map.put(RegistrationConstants.USER_CENTER_ID, "10011");
		Mockito.when(userOnboardService.getMachineCenterId()).thenReturn(map);
		
		HashMap<String, Object> sessionMap = new HashMap<>();
		sessionMap.put(RegistrationConstants.USER_CENTER_ID, "11011");
		
		PowerMockito.mockStatic(ApplicationContext.class);
		PowerMockito.doReturn(sessionMap).when(ApplicationContext.class, "map");
		
		List<ErrorResponseDTO> errorResponseDTOs = new ArrayList<>();
		ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
		errorResponseDTO.setMessage(RegistrationConstants.BLOCKED_USER_ERROR);
		errorResponseDTOs.add(errorResponseDTO);
		responseDTO.setErrorResponseDTOs(errorResponseDTOs);
		assertNotNull(loginServiceImpl.validateUser("mosip").getErrorResponseDTOs());
	}
	
	@Test
	public void validateUserCenterTest() throws Exception {
		ResponseDTO responseDTO = new ResponseDTO();
		UserDetail userDetail = new UserDetail();
		RegCenterUser regCenterUser = new RegCenterUser();
		RegCenterUserId regCenterUserId = new RegCenterUserId();
		regCenterUserId.setRegcntrId("11234");
		regCenterUser.setRegCenterUserId(regCenterUserId);
		userDetail.setRegCenterUser(regCenterUser);
		Mockito.when(userDetailDAO.getUserDetail(Mockito.anyString())).thenReturn(userDetail);
		
		loginServiceImpl.getUserDetail("mosip");
		
		Map<String, String> map = new HashMap<>();
		map.put(RegistrationConstants.USER_CENTER_ID, "10011");
		Mockito.when(userOnboardService.getMachineCenterId()).thenReturn(map);
		
		List<ErrorResponseDTO> errorResponseDTOs = new ArrayList<>();
		ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
		errorResponseDTO.setMessage(RegistrationConstants.USER_MACHINE_VALIDATION_MSG);
		errorResponseDTOs.add(errorResponseDTO);
		responseDTO.setErrorResponseDTOs(errorResponseDTOs);
		assertNotNull(loginServiceImpl.validateUser("mosip").getErrorResponseDTOs());
	}
	
	@Test
	public void validateRoleTest() throws Exception {
		ResponseDTO responseDTO = new ResponseDTO();
		UserDetail userDetail = new UserDetail();
		RegCenterUser regCenterUser = new RegCenterUser();
		RegCenterUserId regCenterUserId = new RegCenterUserId();
		regCenterUserId.setRegcntrId("10011");
		regCenterUser.setRegCenterUserId(regCenterUserId);
		userDetail.setRegCenterUser(regCenterUser);
		userDetail.setStatusCode("ACTIVE");
		Set<UserMachineMapping> userMachineMappings = new HashSet<>();
		UserMachineMapping userMachineMapping = new UserMachineMapping();
		MachineMaster machineMaster = new MachineMaster();
		machineMaster.setSerialNum("12345");
		userMachineMapping.setMachineMaster(machineMaster);
		UserMachineMappingID userMachineMappingId = new UserMachineMappingID();
		userMachineMapping.setUserMachineMappingId(userMachineMappingId);
		userMachineMapping.setIsActive(true);
		userMachineMappings.add(userMachineMapping);
		userDetail.setUserMachineMapping(userMachineMappings);
		Set<UserRole> userRoles = new HashSet<>();
		UserRole userRole = new UserRole();
		UserRoleID userRoleID = new UserRoleID();
		userRoleID.setRoleCode("OFFICER");
		userRoleID.setUsrId("mosip");
		userRole.setUserRoleID(userRoleID);
		userRole.setIsActive(true);
		userRoles.add(userRole);
		userDetail.setUserRole(userRoles);
		Mockito.when(userDetailDAO.getUserDetail(Mockito.anyString())).thenReturn(userDetail);
		
		loginServiceImpl.getUserDetail("mosip");
		
		Map<String, String> map = new HashMap<>();
		map.put(RegistrationConstants.USER_CENTER_ID, "10011");
		map.put(RegistrationConstants.USER_STATION_ID,"10011");
		Mockito.when(userOnboardService.getMachineCenterId()).thenReturn(map);
		
		HashMap<String, Object> sessionMap = new HashMap<>();
		sessionMap.put(RegistrationConstants.USER_CENTER_ID, "11011");
		
		PowerMockito.mockStatic(ApplicationContext.class);
		PowerMockito.doReturn(sessionMap).when(ApplicationContext.class, "map");
		
		List<ErrorResponseDTO> errorResponseDTOs = new ArrayList<>();
		ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
		errorResponseDTO.setMessage(RegistrationConstants.ROLES_EMPTY_ERROR);
		errorResponseDTOs.add(errorResponseDTO);
		responseDTO.setErrorResponseDTOs(errorResponseDTOs);
		assertNotNull(loginServiceImpl.validateUser("mosip").getErrorResponseDTOs());
	}
	
	@Test
	public void validateRoleActiveTest() throws Exception {
		ResponseDTO responseDTO = new ResponseDTO();
		UserDetail userDetail = new UserDetail();
		RegCenterUser regCenterUser = new RegCenterUser();
		RegCenterUserId regCenterUserId = new RegCenterUserId();
		regCenterUserId.setRegcntrId("10011");
		regCenterUser.setRegCenterUserId(regCenterUserId);
		userDetail.setRegCenterUser(regCenterUser);
		userDetail.setStatusCode("ACTIVE");
		Set<UserMachineMapping> userMachineMappings = new HashSet<>();
		UserMachineMapping userMachineMapping = new UserMachineMapping();
		MachineMaster machineMaster = new MachineMaster();
		machineMaster.setSerialNum("12345");
		userMachineMapping.setMachineMaster(machineMaster);
		UserMachineMappingID userMachineMappingId = new UserMachineMappingID();
		userMachineMapping.setUserMachineMappingId(userMachineMappingId);
		userMachineMapping.setIsActive(true);
		userMachineMappings.add(userMachineMapping);
		userDetail.setUserMachineMapping(userMachineMappings);
		Set<UserRole> userRoles = new HashSet<>();
		UserRole userRole = new UserRole();
		UserRoleID userRoleID = new UserRoleID();
		userRoleID.setRoleCode("OFFICER");
		userRoleID.setUsrId("mosip");
		userRole.setUserRoleID(userRoleID);
		userRole.setIsActive(false);
		userRoles.add(userRole);
		userDetail.setUserRole(userRoles);
		Mockito.when(userDetailDAO.getUserDetail(Mockito.anyString())).thenReturn(userDetail);
		
		loginServiceImpl.getUserDetail("mosip");
		
		Map<String, String> map = new HashMap<>();
		map.put(RegistrationConstants.USER_CENTER_ID, "10011");
		map.put(RegistrationConstants.USER_STATION_ID,"10011");
		Mockito.when(userOnboardService.getMachineCenterId()).thenReturn(map);
		
		HashMap<String, Object> sessionMap = new HashMap<>();
		sessionMap.put(RegistrationConstants.USER_CENTER_ID, "11011");
		
		PowerMockito.mockStatic(ApplicationContext.class);
		PowerMockito.doReturn(sessionMap).when(ApplicationContext.class, "map");
		
		List<ErrorResponseDTO> errorResponseDTOs = new ArrayList<>();
		ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
		errorResponseDTO.setMessage(RegistrationConstants.ROLES_EMPTY_ERROR);
		errorResponseDTOs.add(errorResponseDTO);
		responseDTO.setErrorResponseDTOs(errorResponseDTOs);
		assertNotNull(loginServiceImpl.validateUser("mosip").getErrorResponseDTOs());
	}
	
	@Test
	public void validateInvalidLoginTest() {
		UserDTO userDTO = new UserDTO();
		userDTO.setId("mosip");
		userDTO.setUnsuccessfulLoginCount(2);
		userDTO.setUserlockTillDtimes(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
		userDTO.setLastLoginDtimes(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
		userDTO.setLastLoginMethod("PWD");		
		
		UserDetail userDetail = new UserDetail();
		
		Mockito.when(userDetailDAO.getUserDetail("mosip")).thenReturn(userDetail);
		
		Mockito.doNothing().when(userDetailDAO).updateLoginParams(userDetail);
		
		assertEquals("true", loginServiceImpl.validateInvalidLogin(userDTO, "", 1, -1));
	}
	
	@Test
	public void validateInvalidLoginTest2() {
		UserDTO userDTO = new UserDTO();
		userDTO.setId("mosip");
		userDTO.setUnsuccessfulLoginCount(3);
		userDTO.setUserlockTillDtimes(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
		userDTO.setLastLoginDtimes(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
		userDTO.setLastLoginMethod("PWD");		
		
		UserDetail userDetail = new UserDetail();
		
		Mockito.when(userDetailDAO.getUserDetail("mosip")).thenReturn(userDetail);
		
		Mockito.doNothing().when(userDetailDAO).updateLoginParams(userDetail);
		
		assertEquals("ERROR", loginServiceImpl.validateInvalidLogin(userDTO, "sample", 1, 3));
	}
	
	@Test
	public void validateInvalidLoginTest3() {
		UserDTO userDTO = new UserDTO();
		userDTO.setId("mosip");
		userDTO.setUnsuccessfulLoginCount(1);
		userDTO.setUserlockTillDtimes(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
		userDTO.setLastLoginDtimes(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
		userDTO.setLastLoginMethod("PWD");		
		
		UserDetail userDetail = new UserDetail();
		
		Mockito.when(userDetailDAO.getUserDetail("mosip")).thenReturn(userDetail);
		
		Mockito.doNothing().when(userDetailDAO).updateLoginParams(userDetail);
		
		assertEquals("sample", loginServiceImpl.validateInvalidLogin(userDTO, "sample", 3, 3));
>>>>>>> 4483d04c7d451fda25350bad5c0d157b05369082
	}
}
