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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.registration.audit.AuditManagerSerivceImpl;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dao.AppAuthenticationDAO;
import io.mosip.registration.dao.AppAuthenticationDetails;
import io.mosip.registration.dao.RegistrationCenterDAO;
import io.mosip.registration.dao.ScreenAuthorizationDAO;
import io.mosip.registration.dao.ScreenAuthorizationDetails;
import io.mosip.registration.dao.UserDetailDAO;
import io.mosip.registration.dto.AuthorizationDTO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.UserDTO;
import io.mosip.registration.entity.RegistrationCenter;
import io.mosip.registration.entity.UserDetail;
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

		Mockito.when(appAuthenticationDAO.getModesOfLogin("LOGIN", roleSet)).thenReturn(modes);
		assertEquals(modes, loginServiceImpl.getModesOfLogin("LOGIN", roleSet));
	}

	// @Test
	public void getUserDetailTest() {

		UserDetail userDetail = new UserDetail();
		userDetail.setId("mosip");
		List<UserDetail> userDetailList = new ArrayList<UserDetail>();
		userDetailList.add(userDetail);
		Mockito.when(userDetailRepository.findByIdIgnoreCaseAndIsActiveTrue(Mockito.anyString()))
				.thenReturn(userDetailList);

		Mockito.when(userDetailDAO.getUserDetail(Mockito.anyString())).thenReturn(userDetail);

		UserDTO userDTO = new UserDTO();
		userDTO.setId(userDetail.getId());

		assertEquals(userDTO, loginServiceImpl.getUserDetail("mosip"));
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

	// @Test
	public void updateLoginParamsTest() {
		doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(Components.class),
				Mockito.anyString(), Mockito.anyString());
		doNothing().when(userDetailDAO).updateLoginParams(Mockito.any(UserDetail.class));

		UserDTO userDTO = new UserDTO();
		userDTO.setId("mosip");
		userDTO.setUnsuccessfulLoginCount(0);
		userDTO.setLastLoginDtimes(new Timestamp(System.currentTimeMillis()));
		userDTO.setLastLoginMethod("PWD");
		userDTO.setUserlockTillDtimes(new Timestamp(System.currentTimeMillis()));

		UserDetail userDetail = new UserDetail();
		userDetail.setId(userDTO.getId());
		userDetail.setUnsuccessfulLoginCount(userDTO.getUnsuccessfulLoginCount());
		userDetail.setLastLoginDtimes(new Timestamp(System.currentTimeMillis()));
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
}
