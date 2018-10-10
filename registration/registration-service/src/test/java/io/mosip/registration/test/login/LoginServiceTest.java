package io.mosip.registration.test.login;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.constants.AppModuleEnum;
import io.mosip.registration.constants.AuditEventEnum;
import io.mosip.registration.dao.RegistrationAppLoginDAO;
import io.mosip.registration.dao.RegistrationCenterDAO;
import io.mosip.registration.dao.RegistrationScreenAuthorizationDAO;
import io.mosip.registration.dao.RegistrationUserDetailDAO;
import io.mosip.registration.dao.RegistrationUserPasswordDAO;
import io.mosip.registration.dao.RegistrationUserRoleDAO;
import io.mosip.registration.dto.OtpGeneratorRequestDto;
import io.mosip.registration.dto.OtpGeneratorResponseDto;
import io.mosip.registration.dto.OtpValidatorResponseDto;
import io.mosip.registration.entity.RegistrationAppLoginMethod;
import io.mosip.registration.entity.RegistrationAppLoginMethodId;
import io.mosip.registration.entity.RegistrationCenter;
import io.mosip.registration.entity.RegistrationScreenAuthorization;
import io.mosip.registration.entity.RegistrationScreenAuthorizationId;
import io.mosip.registration.entity.RegistrationUserDetail;
import io.mosip.registration.entity.RegistrationUserPassword;
import io.mosip.registration.entity.RegistrationUserRole;
import io.mosip.registration.entity.RegistrationUserRoleId;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.RegistrationAppLoginRepository;
import io.mosip.registration.repositories.RegistrationCenterRepository;
import io.mosip.registration.repositories.RegistrationScreenAuthorizationRepository;
import io.mosip.registration.repositories.RegistrationUserDetailRepository;
import io.mosip.registration.repositories.RegistrationUserPasswordRepository;
import io.mosip.registration.repositories.RegistrationUserRoleRepository;
import io.mosip.registration.service.LoginServiceImpl;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

public class LoginServiceTest {

	@Mock
	private ServiceDelegateUtil serviceDelegateUtil;

	@Mock
	private MosipLogger logger;
	private MosipRollingFileAppender mosipRollingFileAppender;

	@Mock
	private AuditFactory auditFactory;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private LoginServiceImpl loginServiceImpl;

	@Mock
	private RegistrationAppLoginRepository registrationAppLoginRepository;

	@Mock
	private RegistrationAppLoginDAO registrationAppLoginDAO;

	@Mock
	private RegistrationUserPasswordRepository registrationUserPasswordRepository;

	@Mock
	private RegistrationUserPasswordDAO registrationUserPasswordDAO;

	@Mock
	private RegistrationUserDetailRepository registrationUserDetailRepository;

	@Mock
	private RegistrationUserDetailDAO registrationUserDetailDAO;

	@Mock
	private RegistrationCenterRepository registrationCenterRepository;

	@Mock
	private RegistrationCenterDAO registrationCenterDAO;

	@Mock
	private RegistrationUserRoleRepository registrationUserRoleRepository;

	@Mock
	private RegistrationUserRoleDAO registrationUserRoleDAO;

	@Mock
	private RegistrationScreenAuthorizationRepository registrationScreenAuthorizationRepository;

	@Mock
	private RegistrationScreenAuthorizationDAO registrationScreenAuthorizationDAO;
	
	@Before
	public void initialize() {
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
		ReflectionTestUtils.invokeMethod(loginServiceImpl, "initializeLogger", mosipRollingFileAppender);
	}
	
	@Test
	public void getModesOfLoginTest() {

		ReflectionTestUtils.setField(loginServiceImpl, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

		doNothing().when(auditFactory).audit(Mockito.any(AuditEventEnum.class), Mockito.any(AppModuleEnum.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

		RegistrationAppLoginMethod registrationAppLoginMethod = new RegistrationAppLoginMethod();
		RegistrationAppLoginMethodId registrationAppLoginMethodID = new RegistrationAppLoginMethodId();
		registrationAppLoginMethodID.setLoginMethod("PWD");
		registrationAppLoginMethod.setMethodSeq(1);
		registrationAppLoginMethod.setRegistrationAppLoginMethodId(registrationAppLoginMethodID);
		List<RegistrationAppLoginMethod> loginList = new ArrayList<RegistrationAppLoginMethod>();
		loginList.add(registrationAppLoginMethod);
		Map<String, Object> modes = new LinkedHashMap<String, Object>();

		Mockito.when(registrationAppLoginRepository.findByIsActiveTrueOrderByMethodSeq()).thenReturn(loginList);
		for (int mode = 0; mode < loginList.size(); mode++) {
			modes.put("" + loginList.get(mode).getMethodSeq(),
					loginList.get(mode).getRegistrationAppLoginMethodId().getLoginMethod());
		}

		loginServiceImpl.getModesOfLogin();
	}

	@Test
	public void getUserStatusTest() {

		ReflectionTestUtils.setField(loginServiceImpl, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

		doNothing().when(auditFactory).audit(Mockito.any(AuditEventEnum.class), Mockito.any(AppModuleEnum.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

		RegistrationUserDetail registrationUserDetail = new RegistrationUserDetail();
		registrationUserDetail.setName("Sravya");
		registrationUserDetail.setCntrId("000567");
		List<RegistrationUserDetail> registrationUserDetailList = new ArrayList<RegistrationUserDetail>();
		registrationUserDetailList.add(registrationUserDetail);

		Mockito.when(registrationUserDetailRepository.findByIdAndIsActiveTrue(Mockito.anyString()))
				.thenReturn(registrationUserDetailList);

		loginServiceImpl.getUserStatus("Sravya");
	}

	@Test
	public void validateUserPasswordTest() {

		ReflectionTestUtils.setField(loginServiceImpl, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

		doNothing().when(auditFactory).audit(Mockito.any(AuditEventEnum.class), Mockito.any(AppModuleEnum.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

		List<RegistrationUserPassword> registrationUserPasswordList = new ArrayList<RegistrationUserPassword>();
		RegistrationUserPassword registrationUserPassword = new RegistrationUserPassword();
		registrationUserPasswordList.add(registrationUserPassword);
		Mockito.when(registrationUserPasswordRepository.findByRegistrationUserPasswordIdUsrId(Mockito.anyString()))
				.thenReturn(registrationUserPasswordList);

		loginServiceImpl.validateUserPassword("mosip",
				"E2E488ECAF91897D71BEAC2589433898414FEEB140837284C690DFC26707B262");
	}

	@Test
	public void getUserDetailTest() {

		ReflectionTestUtils.setField(loginServiceImpl, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

		doNothing().when(auditFactory).audit(Mockito.any(AuditEventEnum.class), Mockito.any(AppModuleEnum.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

		RegistrationUserDetail registrationUserDetail = new RegistrationUserDetail();
		registrationUserDetail.setName("Sravya");
		registrationUserDetail.setCntrId("000567");
		List<RegistrationUserDetail> registrationUserDetailList = new ArrayList<RegistrationUserDetail>();
		registrationUserDetailList.add(registrationUserDetail);
		Mockito.when(registrationUserDetailRepository.findByIdAndIsActiveTrue(Mockito.anyString()))
				.thenReturn(registrationUserDetailList);
		LinkedHashMap<String, String> userDetails = new LinkedHashMap<String, String>();
		if (registrationUserDetailList.size() > 0) {
			userDetails.put("name", registrationUserDetailList.get(0).getName());
			userDetails.put("centerId", registrationUserDetailList.get(0).getCntrId());
		}
		loginServiceImpl.getUserDetail("Sravya");
	}

	@Test
	public void getRegistrationCenterDetailsTest() {

		ReflectionTestUtils.setField(loginServiceImpl, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

		doNothing().when(auditFactory).audit(Mockito.any(AuditEventEnum.class), Mockito.any(AppModuleEnum.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

		RegistrationCenter registrationCenter = new RegistrationCenter();

		Optional<RegistrationCenter> registrationCenterList = Optional.of(registrationCenter);
		Mockito.when(registrationCenterRepository.findByRegistrationCenterIdCenterIdAndIsActiveTrue(Mockito.anyString()))
				.thenReturn(registrationCenterList);

		loginServiceImpl.getRegistrationCenterDetails("Sravya");
	}

	@Test
	public void getRolesTest() {

		ReflectionTestUtils.setField(loginServiceImpl, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

		doNothing().when(auditFactory).audit(Mockito.any(AuditEventEnum.class), Mockito.any(AppModuleEnum.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

		RegistrationUserRole registrationUserRole = new RegistrationUserRole();
		RegistrationUserRoleId registrationUserRoleID = new RegistrationUserRoleId();
		registrationUserRoleID.setUsrId(Mockito.anyString());
		registrationUserRole.setRegistrationUserRoleId(registrationUserRoleID);
		List<RegistrationUserRole> registrationUserRoles = new ArrayList<RegistrationUserRole>();
		registrationUserRoles.add(registrationUserRole);
		Mockito.when(registrationUserRoleRepository.findByRegistrationUserRoleIdUsrIdAndIsActiveTrue("sravya"))
				.thenReturn(registrationUserRoles);
		List<String> roles = new ArrayList<String>();
		for (int role = 0; role < registrationUserRoles.size(); role++) {
			roles.add(registrationUserRoles.get(role).getRegistrationUserRoleId().getRoleCode());
		}

		loginServiceImpl.getRoles("Sravya");
	}

	@Test
	public void getScreenAuthorizationDetailsTest() {

		ReflectionTestUtils.setField(loginServiceImpl, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

		doNothing().when(auditFactory).audit(Mockito.any(AuditEventEnum.class), Mockito.any(AppModuleEnum.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

		RegistrationScreenAuthorization registrationScreenAuthorization = new RegistrationScreenAuthorization();
		RegistrationScreenAuthorizationId registrationScreenAuthorizationId = new RegistrationScreenAuthorizationId();

		registrationScreenAuthorizationId.setRoleCode("OFFICER");
		registrationScreenAuthorizationId.setAppId("REGISTRATION");
		registrationScreenAuthorization.setRegistrationScreenAuthorizationId(registrationScreenAuthorizationId);
		registrationScreenAuthorization.setPermitted(true);

		List<RegistrationScreenAuthorization> authorizationList = new ArrayList<>();
		authorizationList.add(registrationScreenAuthorization);

		Mockito.when(registrationScreenAuthorizationRepository
				.findByRegistrationScreenAuthorizationIdRoleCodeAndIsPermittedTrueAndIsActiveTrue(Mockito.anyString()))
				.thenReturn(authorizationList);
		loginServiceImpl.getScreenAuthorizationDetails("Sravya");

	}

	@Test
	public void getOTPSuccessResponseTest() throws ClassNotFoundException, RegBaseCheckedException {
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		otpGeneratorRequestDto.setKey("yash");
		OtpGeneratorResponseDto otpGeneratorResponseDto = new OtpGeneratorResponseDto();
		otpGeneratorResponseDto.setOtp("09876");
		ReflectionTestUtils.setField(loginServiceImpl, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());
		when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.any(OtpGeneratorRequestDto.class)))
				.thenReturn(otpGeneratorResponseDto);

		Assert.assertNotNull(loginServiceImpl.getOTP(otpGeneratorRequestDto.getKey()).getSuccessResponseDTO());

	}

	@Test
	public void getOTPFailureResponseTest() throws RegBaseCheckedException {
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		otpGeneratorRequestDto.setKey("ya");
		OtpGeneratorResponseDto otpGeneratorResponseDto = null;
		when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.any(OtpGeneratorRequestDto.class)))
				.thenReturn(otpGeneratorResponseDto);
		ReflectionTestUtils.setField(loginServiceImpl, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

		Assert.assertNotNull(loginServiceImpl.getOTP(otpGeneratorRequestDto.getKey()).getErrorResponseDTOs());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void validateOTPSuccessTest() throws RegBaseCheckedException {
		OtpValidatorResponseDto otpGeneratorRequestDto = new OtpValidatorResponseDto();
		otpGeneratorRequestDto.setOrdMessage("OTP is valid");
		otpGeneratorRequestDto.setstatus("true");
		ReflectionTestUtils.setField(loginServiceImpl, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());
		when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap())).thenReturn(otpGeneratorRequestDto);
		Assert.assertNotNull(loginServiceImpl.validateOTP("yashReddy683", "099887").getSuccessResponseDTO());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void validateOTPFailureTest() throws RegBaseCheckedException {
		OtpValidatorResponseDto otpGeneratorRequestDto = new OtpValidatorResponseDto();
		otpGeneratorRequestDto.setOrdMessage("OTP is valid");
		otpGeneratorRequestDto.setstatus("false");
		ReflectionTestUtils.setField(loginServiceImpl, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());
		when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap())).thenReturn(otpGeneratorRequestDto);

		Assert.assertNotNull(loginServiceImpl.validateOTP("yashReddy683", "099887").getErrorResponseDTOs());

	}

}
