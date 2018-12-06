package io.mosip.registration.test.login;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.registration.audit.AuditFactoryImpl;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dao.RegistrationAppAuthenticationDAO;
import io.mosip.registration.dao.RegistrationCenterDAO;
import io.mosip.registration.dao.RegistrationScreenAuthorizationDAO;
import io.mosip.registration.dao.RegistrationUserDetailDAO;
import io.mosip.registration.dto.AuthorizationDTO;
import io.mosip.registration.dto.OtpGeneratorRequestDTO;
import io.mosip.registration.dto.OtpGeneratorResponseDTO;
import io.mosip.registration.dto.OtpValidatorResponseDTO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.entity.RegistrationAppAuthenticationMethod;
import io.mosip.registration.entity.RegistrationAppAuthenticationMethodId;
import io.mosip.registration.entity.RegistrationCenter;
import io.mosip.registration.entity.RegistrationScreenAuthorization;
import io.mosip.registration.entity.RegistrationScreenAuthorizationId;
import io.mosip.registration.entity.RegistrationUserDetail;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.repositories.RegistrationAppAuthenticationRepository;
import io.mosip.registration.repositories.RegistrationCenterRepository;
import io.mosip.registration.repositories.RegistrationScreenAuthorizationRepository;
import io.mosip.registration.repositories.RegistrationUserDetailRepository;
import io.mosip.registration.service.impl.LoginServiceImpl;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

public class LoginServiceTest {

	@Mock
	private ServiceDelegateUtil serviceDelegateUtil;

	@Mock
	private AuditFactoryImpl auditFactory;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private LoginServiceImpl loginServiceImpl;

	@Mock
	private RegistrationAppAuthenticationRepository registrationAppLoginRepository;

	@Mock
	private RegistrationAppAuthenticationDAO registrationAppLoginDAO;

	@Mock
	private RegistrationUserDetailRepository registrationUserDetailRepository;

	@Mock
	private RegistrationUserDetailDAO registrationUserDetailDAO;

	@Mock
	private RegistrationCenterRepository registrationCenterRepository;

	@Mock
	private RegistrationCenterDAO registrationCenterDAO;
	
	@Mock
	private RegistrationScreenAuthorizationRepository registrationScreenAuthorizationRepository;

	@Mock
	private RegistrationScreenAuthorizationDAO registrationScreenAuthorizationDAO;
	
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	
	@Before
	public void initialize() throws IOException, URISyntaxException {
		doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(Components.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		applicationContext.setApplicationMessagesBundle();
	}
	
	@Test
	public void getModesOfLoginTest() {

		RegistrationAppAuthenticationMethod registrationAppLoginMethod = new RegistrationAppAuthenticationMethod();
		RegistrationAppAuthenticationMethodId registrationAppLoginMethodID = new RegistrationAppAuthenticationMethodId();
		registrationAppLoginMethodID.setLoginMethod("PWD");
		registrationAppLoginMethod.setMethodSeq(1);
		registrationAppLoginMethod.setregistrationAppAuthenticationMethodId(registrationAppLoginMethodID);
		List<RegistrationAppAuthenticationMethod> loginList = new ArrayList<RegistrationAppAuthenticationMethod>();
		loginList.add(registrationAppLoginMethod);
		Map<String, Object> modes = new LinkedHashMap<String, Object>();

		Mockito.when(registrationAppLoginRepository.findByIsActiveTrueAndRegistrationAppAuthenticationMethodIdProcessNameOrderByMethodSeq("LOGIN")).thenReturn(loginList);
		loginList.forEach(p -> modes.put(String.valueOf(p.getMethodSeq()), p.getregistrationAppAuthenticationMethodId().getLoginMethod()));
		Mockito.when(registrationAppLoginDAO.getModesOfLogin("LOGIN")).thenReturn(modes);
		assertEquals(modes,loginServiceImpl.getModesOfLogin("LOGIN"));
	}

	@Test
	public void getUserDetailTest() {

		RegistrationUserDetail registrationUserDetail = new RegistrationUserDetail();
		List<RegistrationUserDetail> registrationUserDetailList = new ArrayList<RegistrationUserDetail>();
		registrationUserDetailList.add(registrationUserDetail);
		Mockito.when(registrationUserDetailRepository.findByIdAndIsActiveTrue(Mockito.anyString()))
				.thenReturn(registrationUserDetailList);
		
		Mockito.when(registrationUserDetailDAO.getUserDetail(Mockito.anyString())).thenReturn(registrationUserDetail);
		
		assertEquals(registrationUserDetail,loginServiceImpl.getUserDetail("mosip"));		
	}

	@Test
	public void getRegistrationCenterDetailsTest() {

		RegistrationCenter registrationCenter = new RegistrationCenter();

		RegistrationCenterDetailDTO centerDetailDTO = new RegistrationCenterDetailDTO();
		Optional<RegistrationCenter> registrationCenterList = Optional.of(registrationCenter);
		Mockito.when(registrationCenterRepository.findByCenterIdAndIsActiveTrue(Mockito.anyString()))
				.thenReturn(registrationCenterList);
		
		Mockito.when(registrationCenterDAO.getRegistrationCenterDetails(Mockito.anyString())).thenReturn(centerDetailDTO);
		assertEquals(centerDetailDTO,loginServiceImpl.getRegistrationCenterDetails("mosip"));
	}


	@Test
	public void getScreenAuthorizationDetailsTest() {

		RegistrationScreenAuthorization registrationScreenAuthorization = new RegistrationScreenAuthorization();
		RegistrationScreenAuthorizationId registrationScreenAuthorizationId = new RegistrationScreenAuthorizationId();

		registrationScreenAuthorizationId.setRoleCode("OFFICER");
		registrationScreenAuthorization.setRegistrationScreenAuthorizationId(registrationScreenAuthorizationId);
		registrationScreenAuthorization.setPermitted(true);

		List<RegistrationScreenAuthorization> authorizationList = new ArrayList<>();
		authorizationList.add(registrationScreenAuthorization);

		Mockito.when(registrationScreenAuthorizationRepository
				.findByRegistrationScreenAuthorizationIdRoleCodeAndIsPermittedTrueAndIsActiveTrue(Mockito.anyString()))
				.thenReturn(authorizationList);
		AuthorizationDTO authorizationDTO = new AuthorizationDTO();
		
		Mockito.when(registrationScreenAuthorizationDAO.getScreenAuthorizationDetails(Mockito.anyString())).thenReturn(authorizationDTO);
		
		assertEquals(authorizationDTO,loginServiceImpl.getScreenAuthorizationDetails("mosip"));

	}

	@Test
	public void getOTPSuccessResponseTest() throws ClassNotFoundException, RegBaseCheckedException, HttpClientErrorException, ResourceAccessException, SocketTimeoutException {
		OtpGeneratorRequestDTO otpGeneratorRequestDTO = new OtpGeneratorRequestDTO();
		otpGeneratorRequestDTO.setKey("yash");
		OtpGeneratorResponseDTO otpGeneratorResponseDTO = new OtpGeneratorResponseDTO();
		otpGeneratorResponseDTO.setOtp("09876");
		when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.any(OtpGeneratorRequestDTO.class)))
				.thenReturn(otpGeneratorResponseDTO);

		Assert.assertNotNull(loginServiceImpl.getOTP(otpGeneratorRequestDTO.getKey()).getSuccessResponseDTO());

	}

	@Test
	public void getOTPFailureResponseTest() throws RegBaseCheckedException, HttpClientErrorException, ResourceAccessException, SocketTimeoutException {
		OtpGeneratorRequestDTO otpGeneratorRequestDTO = new OtpGeneratorRequestDTO();
		otpGeneratorRequestDTO.setKey("ya");
		OtpGeneratorResponseDTO otpGeneratorResponseDTO = null;
		when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.any(OtpGeneratorRequestDTO.class)))
				.thenReturn(otpGeneratorResponseDTO);

		Assert.assertNotNull(loginServiceImpl.getOTP(otpGeneratorRequestDTO.getKey()).getErrorResponseDTOs());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void validateOTPSuccessTest() throws RegBaseCheckedException, HttpClientErrorException, SocketTimeoutException {
		OtpValidatorResponseDTO otpGeneratorRequestDto = new OtpValidatorResponseDTO();
		otpGeneratorRequestDto.setOrdMessage("OTP is valid");
		otpGeneratorRequestDto.setstatus("true");
		when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap())).thenReturn(otpGeneratorRequestDto);
		Assert.assertNotNull(loginServiceImpl.validateOTP("yashReddy683", "099887").getSuccessResponseDTO());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void validateOTPFailureTest() throws RegBaseCheckedException, HttpClientErrorException, SocketTimeoutException {
		OtpValidatorResponseDTO otpGeneratorRequestDto = new OtpValidatorResponseDTO();
		otpGeneratorRequestDto.setOrdMessage("OTP is valid");
		otpGeneratorRequestDto.setstatus("false");
		when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap())).thenReturn(otpGeneratorRequestDto);

		Assert.assertNotNull(loginServiceImpl.validateOTP("yashReddy683", "099887").getErrorResponseDTOs());

	}

	@Test
	public void updateLoginParamsTest() {
		doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(Components.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		doNothing().when(registrationUserDetailDAO).updateLoginParams(Mockito.any(RegistrationUserDetail.class));
		
		RegistrationUserDetail registrationUserDetail = new RegistrationUserDetail();
		registrationUserDetail.setId("mosip");
		registrationUserDetail.setUnsuccessfulLoginCount(0);
		registrationUserDetail.setLastLoginDtimes(new Timestamp(System.currentTimeMillis()));
		registrationUserDetail.setLastLoginMethod("PWD");
		
		loginServiceImpl.updateLoginParams(registrationUserDetail);
	}
}
