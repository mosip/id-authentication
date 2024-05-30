package io.mosip.authentication.common.service.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.entity.OtpTransaction;
import io.mosip.authentication.common.service.factory.AuditRequestFactory;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.integration.dto.OtpGeneratorRequestDto;
import io.mosip.authentication.common.service.integration.dto.OtpGeneratorResponseDto;
import io.mosip.authentication.common.service.repository.OtpTxnRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.OtpErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;
import io.mosip.authentication.core.spi.notification.service.NotificationService;
import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.dto.RestRequestDTO;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.idrepository.core.helper.RestHelper;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.exception.JsonProcessingException;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class OTPManagerTest {

	private static final String FROZEN_ERROR_MESSAGE = "OTP request/validation has been frozen for the Individual-ID for 30 seconds due to consecutive failure attempts for 5 times.";

	@InjectMocks
	private OTPManager otpManager;

	@Mock
	private RestRequestFactory restRequestFactory;

	@InjectMocks
	AuditRequestFactory auditFactory;

	private OtpGeneratorRequestDto otpGeneratorRequestDto;

	@Mock
	EnvUtil environment;

	@Mock
	private RestHelper restHelper;

	@Mock
	private EnvUtil env;

	@Mock
	RestServiceException e;

	@Mock
	private OtpTxnRepository otpRepo;

	@Mock
	private IdAuthSecurityManager securityManager;

	@Mock
	private NotificationService notificationService;

	private int otpExpiryTime;

	List<String> templateLanguages = new ArrayList<>();

	@Before
	public void before() {
		ReflectionTestUtils.setField(restRequestFactory, "env", environment);
		ReflectionTestUtils.setField(env, "otpExpiryTime", 12);
		ReflectionTestUtils.setField(otpManager, "numberOfValidationAttemptsAllowed", 5);
		ReflectionTestUtils.setField(otpManager, "otpFrozenTimeMinutes", 30);
		templateLanguages.add("eng");
		templateLanguages.add("ara");
		EnvUtil.setKeySplitter("#KEY_SPLITTER#");
	}

	private static final String VALIDATION_UNSUCCESSFUL = "VALIDATION_UNSUCCESSFUL";

	private static final String OTP_EXPIRED = "OTP_EXPIRED";

	private static final String USER_BLOCKED = "USER_BLOCKED";

	@SuppressWarnings("rawtypes")
	@Test
	public void sendOtpTest() throws RestServiceException, IdAuthenticationBusinessException {
		OtpGeneratorRequestDto otpGeneratorRequestDto = getOtpGeneratorRequestDto();
		ResponseWrapper<Map> otpGeneratorResponsetDto = new ResponseWrapper<>();
		Map<String, Object> response = new HashMap<>();
		response.put("status", "success");
		otpGeneratorResponsetDto.setResponse(response);
		RestRequestDTO restRequestDTO = getRestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_GENERATE_SERVICE, otpGeneratorRequestDto,
				OtpGeneratorResponseDto.class)).thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(otpGeneratorResponsetDto);
		OtpRequestDTO otpRequestDTO = getOtpRequestDto();
		Map<String, String> valueMap = new HashMap<>();
		valueMap.put("namePri", "Name in PrimaryLang");
		valueMap.put("nameSec", "Name in SecondaryLang");
		try {
			when(otpRepo.save(Mockito.any())).thenAnswer(invocation -> {
				assertEquals(IdAuthCommonConstants.ACTIVE_STATUS, ((OtpTransaction)invocation.getArguments()[0]).getStatusCode());
				return null;
			});
			boolean result =  otpManager.sendOtp(otpRequestDTO, "426789089018", "UIN", valueMap, templateLanguages);
			assertTrue(result);
			verify(otpRepo, times(1)).save(Mockito.any());
		} catch(IdAuthUncheckedException ex) {
			fail();
		}
	}

	@Test
	public void sendOtpTest_frozen_within30mins() throws RestServiceException, IdAuthenticationBusinessException {
		OtpGeneratorRequestDto otpGeneratorRequestDto = getOtpGeneratorRequestDto();
		ResponseWrapper<Map> otpGeneratorResponsetDto = new ResponseWrapper<>();
		Map<String, Object> response = new HashMap<>();
		response.put("status", "success");
		otpGeneratorResponsetDto.setResponse(response);
		RestRequestDTO restRequestDTO = getRestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_GENERATE_SERVICE, otpGeneratorRequestDto,
				OtpGeneratorResponseDto.class)).thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(otpGeneratorResponsetDto);
		OtpRequestDTO otpRequestDTO = getOtpRequestDto();
		Map<String, String> valueMap = new HashMap<>();
		valueMap.put("namePri", "Name in PrimaryLang");
		valueMap.put("nameSec", "Name in SecondaryLang");
		when(securityManager.hash(Mockito.anyString())).thenReturn("refidHash");
		OtpTransaction entity = new OtpTransaction();
		entity.setStatusCode(IdAuthCommonConstants.FROZEN);
		entity.setUpdDTimes(DateUtils.getUTCCurrentDateTime().minus(30, ChronoUnit.MINUTES));
		when(otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(Mockito.anyString(), Mockito.anyList())).thenReturn(Optional.of(entity));
		try {
			otpManager.sendOtp(otpRequestDTO, "426789089018", "UIN", valueMap, templateLanguages);
		} catch(IdAuthenticationBusinessException ex) {
			assertEquals(IdAuthenticationErrorConstants.OTP_FROZEN.getErrorCode(), ex.getErrorCode());
			assertEquals(FROZEN_ERROR_MESSAGE, ex.getErrorText());
		}
	}

	@Test
	public void sendOtpTest_frozen_In31mins() throws RestServiceException, IdAuthenticationBusinessException {
		OtpGeneratorRequestDto otpGeneratorRequestDto = getOtpGeneratorRequestDto();
		ResponseWrapper<Map> otpGeneratorResponsetDto = new ResponseWrapper<>();
		Map<String, Object> response = new HashMap<>();
		response.put("status", "success");
		otpGeneratorResponsetDto.setResponse(response);
		RestRequestDTO restRequestDTO = getRestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_GENERATE_SERVICE, otpGeneratorRequestDto,
				OtpGeneratorResponseDto.class)).thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(otpGeneratorResponsetDto);
		OtpRequestDTO otpRequestDTO = getOtpRequestDto();
		Map<String, String> valueMap = new HashMap<>();
		valueMap.put("namePri", "Name in PrimaryLang");
		valueMap.put("nameSec", "Name in SecondaryLang");
		when(securityManager.hash(Mockito.anyString())).thenReturn("refidHash");
		OtpTransaction entity = new OtpTransaction();
		entity.setStatusCode(IdAuthCommonConstants.FROZEN);
		entity.setUpdDTimes(DateUtils.getUTCCurrentDateTime().minus(31, ChronoUnit.MINUTES));
		when(otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(Mockito.anyString(), Mockito.anyList())).thenReturn(Optional.of(entity));
		try {
			when(otpRepo.save(Mockito.any())).thenAnswer(invocation -> {
				assertEquals(IdAuthCommonConstants.ACTIVE_STATUS, ((OtpTransaction)invocation.getArguments()[0]).getStatusCode());
				return null;
			});
			boolean result =  otpManager.sendOtp(otpRequestDTO, "426789089018", "UIN", valueMap, templateLanguages);
			assertTrue(result);
		} catch(IdAuthUncheckedException ex) {
			fail();
		}
	}

	@Test
	public void sendOtpTest_USED_entry() throws RestServiceException, IdAuthenticationBusinessException {
		OtpGeneratorRequestDto otpGeneratorRequestDto = getOtpGeneratorRequestDto();
		ResponseWrapper<Map> otpGeneratorResponsetDto = new ResponseWrapper<>();
		Map<String, Object> response = new HashMap<>();
		response.put("status", "success");
		otpGeneratorResponsetDto.setResponse(response);
		RestRequestDTO restRequestDTO = getRestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_GENERATE_SERVICE, otpGeneratorRequestDto,
				OtpGeneratorResponseDto.class)).thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(otpGeneratorResponsetDto);
		OtpRequestDTO otpRequestDTO = getOtpRequestDto();
		Map<String, String> valueMap = new HashMap<>();
		valueMap.put("namePri", "Name in PrimaryLang");
		valueMap.put("nameSec", "Name in SecondaryLang");
		when(securityManager.hash(Mockito.anyString())).thenReturn("refidHash");
		OtpTransaction entity = new OtpTransaction();
		entity.setStatusCode(IdAuthCommonConstants.USED_STATUS);
		entity.setUpdDTimes(DateUtils.getUTCCurrentDateTime().minus(31, ChronoUnit.MINUTES));
		when(otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(Mockito.anyString(), Mockito.anyList())).thenReturn(Optional.of(entity));
		try {
			when(otpRepo.save(Mockito.any())).thenAnswer(invocation -> {
				assertEquals(IdAuthCommonConstants.ACTIVE_STATUS, ((OtpTransaction)invocation.getArguments()[0]).getStatusCode());
				return null;
			});
			boolean result =  otpManager.sendOtp(otpRequestDTO, "426789089018", "UIN", valueMap, templateLanguages);
			assertTrue(result);
		} catch(IdAuthUncheckedException ex) {
			fail();
		}
	}

	@Test
	public void sendOtpTest_frozen_within25mins() throws RestServiceException, IdAuthenticationBusinessException {
		OtpGeneratorRequestDto otpGeneratorRequestDto = getOtpGeneratorRequestDto();
		ResponseWrapper<Map> otpGeneratorResponsetDto = new ResponseWrapper<>();
		Map<String, Object> response = new HashMap<>();
		response.put("status", "success");
		otpGeneratorResponsetDto.setResponse(response);
		RestRequestDTO restRequestDTO = getRestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_GENERATE_SERVICE, otpGeneratorRequestDto,
				OtpGeneratorResponseDto.class)).thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(otpGeneratorResponsetDto);
		OtpRequestDTO otpRequestDTO = getOtpRequestDto();
		Map<String, String> valueMap = new HashMap<>();
		valueMap.put("namePri", "Name in PrimaryLang");
		valueMap.put("nameSec", "Name in SecondaryLang");
		when(securityManager.hash(Mockito.anyString())).thenReturn("refidHash");
		OtpTransaction entity = new OtpTransaction();
		entity.setStatusCode(IdAuthCommonConstants.FROZEN);
		entity.setUpdDTimes(DateUtils.getUTCCurrentDateTime().minus(25, ChronoUnit.MINUTES));
		when(otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(Mockito.anyString(), Mockito.anyList())).thenReturn(Optional.of(entity));
		try {
			otpManager.sendOtp(otpRequestDTO, "426789089018", "UIN", valueMap, templateLanguages);
		} catch(IdAuthenticationBusinessException ex) {
			assertEquals(IdAuthenticationErrorConstants.OTP_FROZEN.getErrorCode(), ex.getErrorCode());
			assertEquals(FROZEN_ERROR_MESSAGE, ex.getErrorText());
		}
	}

	@Test(expected = IdAuthUncheckedException.class)
	public void sendOtpNullResponseExceptionTest() throws RestServiceException, IdAuthenticationBusinessException {
		OtpGeneratorRequestDto otpGeneratorRequestDto = getOtpGeneratorRequestDto();
		ResponseWrapper<Map> otpGeneratorResponsetDto = new ResponseWrapper<>();
		Map<String, Object> response = new HashMap<>();
		response.put("status", "success");
		otpGeneratorResponsetDto.setResponse(response);
		RestRequestDTO restRequestDTO = getRestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_GENERATE_SERVICE, otpGeneratorRequestDto,
				OtpGeneratorResponseDto.class)).thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(null);
		OtpRequestDTO otpRequestDTO = getOtpRequestDto();
		Map<String, String> valueMap = new HashMap<>();
		valueMap.put("namePri", "Name in PrimaryLang");
		valueMap.put("nameSec", "Name in SecondaryLang");
		boolean sendOtpResponse = otpManager.sendOtp(otpRequestDTO, "426789089018", "UIN", valueMap, templateLanguages);
		assertEquals(sendOtpResponse, true);
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void sendOtpTest_existingEntry() throws RestServiceException, IdAuthenticationBusinessException {
		OtpGeneratorRequestDto otpGeneratorRequestDto = getOtpGeneratorRequestDto();
		ResponseWrapper<Map> otpGeneratorResponsetDto = new ResponseWrapper<>();
		Map<String, Object> response = new HashMap<>();
		response.put("status", "success");
		otpGeneratorResponsetDto.setResponse(response);
		RestRequestDTO restRequestDTO = getRestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_GENERATE_SERVICE, otpGeneratorRequestDto,
				OtpGeneratorResponseDto.class)).thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(otpGeneratorResponsetDto);
		OtpRequestDTO otpRequestDTO = getOtpRequestDto();
		Map<String, String> valueMap = new HashMap<>();
		valueMap.put("namePri", "Name in PrimaryLang");
		valueMap.put("nameSec", "Name in SecondaryLang");
		boolean sendOtpResponse = otpManager.sendOtp(otpRequestDTO, "426789089018", "UIN", valueMap, templateLanguages);
		assertEquals(sendOtpResponse, true);
	}

	@Test
	public void sendOtpTest_blockedStatus() throws RestServiceException, IdAuthenticationBusinessException {
		OtpGeneratorRequestDto otpGeneratorRequestDto = getOtpGeneratorRequestDto();
		ResponseWrapper<Map> otpGeneratorResponsetDto = new ResponseWrapper<>();
		Map<String, Object> response = new HashMap<>();
		response.put("status", USER_BLOCKED);
		otpGeneratorResponsetDto.setResponse(response);
		RestRequestDTO restRequestDTO = getRestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_GENERATE_SERVICE, otpGeneratorRequestDto,
				OtpGeneratorResponseDto.class)).thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(otpGeneratorResponsetDto);
		OtpRequestDTO otpRequestDTO = getOtpRequestDto();
		Map<String, String> valueMap = new HashMap<>();
		valueMap.put("namePri", "Name in PrimaryLang");
		valueMap.put("nameSec", "Name in SecondaryLang");
		try {
			otpManager.sendOtp(otpRequestDTO, "426789089018", "UIN", valueMap, templateLanguages);
		} catch (IdAuthUncheckedException ex) {
			assertEquals(IdAuthenticationErrorConstants.BLOCKED_OTP_VALIDATE.getErrorCode(), ex.getErrorCode());
		}
	}

	@Ignore
	@Test
	public void TestPhoneNumberNotRegistered() throws RestServiceException, IdAuthenticationBusinessException {
		OtpGeneratorRequestDto otpGeneratorRequestDto = getOtpGeneratorRequestDto();
		List<String> otpChannel = new ArrayList<>();
		otpGeneratorRequestDto.setOtpChannel(otpChannel);
		ResponseWrapper<OtpGeneratorResponseDto> response = new ResponseWrapper<>();
		List<ServiceError> errors = new ArrayList<>();
		ServiceError serviceError = new ServiceError();
		serviceError.setErrorCode(OtpErrorConstants.PHONENOTREGISTERED.getErrorCode());
		serviceError.setMessage(OtpErrorConstants.PHONENOTREGISTERED.getErrorMessage());
		errors.add(serviceError);
		response.setErrors(errors);

		RestRequestDTO restRequestDTO = getRestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_GENERATE_SERVICE, otpGeneratorRequestDto,
				OtpGeneratorResponseDto.class)).thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdRepoErrorConstants.CLIENT_ERROR, response.toString(), response));
		OtpRequestDTO otpRequestDTO = getOtpRequestDto();
		Map<String, String> valueMap = new HashMap<>();
		valueMap.put("namePri", "Name in PrimaryLang");
		valueMap.put("nameSec", "Name in SecondaryLang");
		try {
			otpManager.sendOtp(otpRequestDTO, "426789089018", "UIN", valueMap, templateLanguages);
		} catch (IdAuthenticationBusinessException ex) {
			assertEquals(IdAuthenticationErrorConstants.PHONE_EMAIL_NOT_REGISTERED.getErrorCode(), ex.getErrorCode());
		}

	}

	@Ignore
	@Test
	public void TestEmailNotRegistered() throws RestServiceException, IdAuthenticationBusinessException {
		OtpGeneratorRequestDto otpGeneratorRequestDto = getOtpGeneratorRequestDto();
		List<String> otpChannel = new ArrayList<>();
		otpGeneratorRequestDto.setOtpChannel(otpChannel);
		ResponseWrapper<OtpGeneratorResponseDto> response = new ResponseWrapper<>();
		List<ServiceError> errors = new ArrayList<>();
		ServiceError serviceError = new ServiceError();
		serviceError.setErrorCode(OtpErrorConstants.EMAILNOTREGISTERED.getErrorCode());
		serviceError.setMessage(OtpErrorConstants.EMAILNOTREGISTERED.getErrorMessage());
		errors.add(serviceError);
		response.setErrors(errors);

		RestRequestDTO restRequestDTO = getRestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdRepoErrorConstants.CLIENT_ERROR, response.toString(), response));
		OtpRequestDTO otpRequestDTO = getOtpRequestDto();
		Map<String, String> valueMap = new HashMap<>();
		valueMap.put("namePri", "Name in PrimaryLang");
		valueMap.put("nameSec", "Name in SecondaryLang");
		try {
			otpManager.sendOtp(otpRequestDTO, "426789089018", "UIN", valueMap, templateLanguages);
		} catch (IdAuthenticationBusinessException ex) {
			assertEquals(IdAuthenticationErrorConstants.PHONE_EMAIL_NOT_REGISTERED.getErrorCode(), ex.getErrorCode());
		}

	}

	@Ignore
	@Test
	public void TestEmailandPhoneNotRegistered() throws RestServiceException, IdAuthenticationBusinessException {
		OtpGeneratorRequestDto otpGeneratorRequestDto = getOtpGeneratorRequestDto();
		List<String> otpChannel = new ArrayList<>();
		otpGeneratorRequestDto.setOtpChannel(otpChannel);
		ResponseWrapper<OtpGeneratorResponseDto> response = new ResponseWrapper<>();
		List<ServiceError> errors = new ArrayList<>();
		ServiceError serviceError = new ServiceError();
		serviceError.setErrorCode(OtpErrorConstants.EMAILPHONENOTREGISTERED.getErrorCode());
		serviceError.setMessage(OtpErrorConstants.EMAILPHONENOTREGISTERED.getErrorMessage());
		errors.add(serviceError);
		response.setErrors(errors);

		RestRequestDTO restRequestDTO = getRestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_GENERATE_SERVICE, otpGeneratorRequestDto,
				OtpGeneratorResponseDto.class)).thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdRepoErrorConstants.CLIENT_ERROR, response.toString(), response));
		OtpRequestDTO otpRequestDTO = getOtpRequestDto();
		Map<String, String> valueMap = new HashMap<>();
		valueMap.put("namePri", "Name in PrimaryLang");
		valueMap.put("nameSec", "Name in SecondaryLang");
		try {
			otpManager.sendOtp(otpRequestDTO, "426789089018", "UIN", valueMap, templateLanguages);
		} catch (IdAuthenticationBusinessException ex) {
			assertEquals(IdAuthenticationErrorConstants.PHONE_EMAIL_NOT_REGISTERED.getErrorCode(), ex.getErrorCode());
		}
	}

	private OtpGeneratorRequestDto getOtpGeneratorRequestDto() {
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		otpGeneratorRequestDto.setAppId("IDA");
		otpGeneratorRequestDto.setContext("ida-auth-otp");
		List<String> otpChannel = new ArrayList<>();
		otpChannel.add("mobile");
		otpChannel.add("email");
		otpGeneratorRequestDto.setOtpChannel(otpChannel);
		otpGeneratorRequestDto.setUserId("426789089018");
		otpGeneratorRequestDto.setUseridtype(IdType.UIN.getType());
		return otpGeneratorRequestDto;
	}

	// ====================================================================
	// ********************** Helper Method *******************************
	// ====================================================================
	private RestRequestDTO getRestRequestDTO() {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		restRequestDTO.setHttpMethod(HttpMethod.POST);
		restRequestDTO.setUri("http://localhost:8083/otpmanager/otps");
		restRequestDTO.setRequestBody(otpGeneratorRequestDto);
		restRequestDTO.setResponseType(OtpGeneratorResponseDto.class);
		restRequestDTO.setTimeout(23);
		return restRequestDTO;
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void TestGenerateKeyForBlockedUser() throws RestServiceException, IdAuthenticationBusinessException {
		ResponseWrapper<OtpGeneratorResponseDto> response = new ResponseWrapper<>();
		List<ServiceError> errors = new ArrayList<>();
		ServiceError serviceError = new ServiceError();
		serviceError.setErrorCode(OtpErrorConstants.USERBLOCKED.getErrorCode());
		serviceError.setMessage(OtpErrorConstants.USERBLOCKED.getErrorMessage());
		errors.add(serviceError);
		response.setErrors(errors);
		ResponseWrapper<Map> otpGeneratorResponsetDto = new ResponseWrapper<>();
		Map<String, Object> response1 = new HashMap<>();
		response1.put("status", "failure");
		response1.put("message", USER_BLOCKED);
		otpGeneratorResponsetDto.setResponse(response1);
		Map<String, String> valueMap = new HashMap<String, String>();
		valueMap.put("status", "failure");
		valueMap.put("message", "USER_BLOCKED");
		Map<String, Object> wrapperMap = new HashMap<String, Object>();
		wrapperMap.put("response", valueMap);
		RestRequestDTO restRequestDTO = getRestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_GENERATE_SERVICE, null,
				ResponseWrapper.class)).thenReturn(restRequestDTO);

		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(otpGeneratorResponsetDto);
		OtpRequestDTO otpRequestDTO = getOtpRequestDto();
		Map<String, String> valuesMap = new HashMap<>();
		valuesMap.put("namePri", "Name in PrimaryLang");
		valuesMap.put("nameSec", "Name in SecondaryLang");
		try {
			otpManager.sendOtp(otpRequestDTO, "123456", "UIN", valuesMap, templateLanguages);
		} catch (IdAuthenticationBusinessException ex) {
			assertEquals(IdAuthenticationErrorConstants.BLOCKED_OTP_GENERATE.getErrorCode(), ex.getErrorCode());
			assertEquals(IdAuthenticationErrorConstants.BLOCKED_OTP_GENERATE.getErrorMessage(), ex.getErrorText());
		}

	}

	@Ignore
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestRestServiceException() throws IdAuthenticationBusinessException, JsonProcessingException,
			com.fasterxml.jackson.core.JsonProcessingException, RestServiceException {
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failure");
		response.put("message", "OTP_EXPIRED");
		Map<String, Object> response1 = new HashMap<>();
		response1.put("response", response);

		ObjectMapper mapper = new ObjectMapper();
		String output = mapper.writeValueAsString(response);
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Map<String, Object> valuemap = new HashMap<>();

		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("response", valuemap);
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdRepoErrorConstants.CLIENT_ERROR, output, valuemap));
		otpManager.validateOtp("Test123", "123456", "426789089018");
	}

	@Ignore
	@Test
	public void TestOtpAuth() throws RestServiceException, IdAuthenticationBusinessException, JsonProcessingException {
		Map<String, Object> response = new HashMap<>();
		response.put("status", "success");
		response.put("message", "VALIDATION_SUCCESSFUL");
		Map<String, Object> response1 = new HashMap<>();
		response1.put("response", response);

		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(response1);
		boolean expactedOTP = otpManager.validateOtp("Test123", "123456", "426789089018");
		assertTrue(expactedOTP);
	}

	@Test
	public void TestOtpAuthFailure()
			throws RestServiceException, IdAuthenticationBusinessException, JsonProcessingException {

		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("status", "failure");
		valueMap.put("message", "Validation_Unsuccessful");
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(valueMap);
		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("hash");
		OtpTransaction otpEntity = new OtpTransaction();
		otpEntity.setStatusCode(IdAuthCommonConstants.ACTIVE_STATUS);
		otpEntity.setOtpHash("otphash");

		Mockito.when(otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(Mockito.anyString(), Mockito.anyList())).thenReturn(Optional.of(otpEntity ));

		boolean expactedOTP = otpManager.validateOtp("Test123", "123456", "426789089018");
		assertFalse(expactedOTP);
	}

	@Ignore
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestRestServiceExceptionwithInvalid() throws RestServiceException, IdAuthenticationBusinessException,
			JsonProcessingException, com.fasterxml.jackson.core.JsonProcessingException {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("status", "failure");
		valueMap.put("message", "VALIDATION_UNSUCCESSFUL");
		response.put("response", valueMap);

		ObjectMapper mapper = new ObjectMapper();
		String output = mapper.writeValueAsString(response);
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdRepoErrorConstants.CLIENT_ERROR, output, response));
		otpManager.validateOtp("Test123", "123456", "426789089018");
	}

	@Ignore
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestRestServiceExceptionwithInvalidUnknownMessage()
			throws RestServiceException, IdAuthenticationBusinessException, JsonProcessingException,
			com.fasterxml.jackson.core.JsonProcessingException {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("status", "failure");
		valueMap.put("message", "VALIDATION_UNSUCCESSFUL");
		response.put("response", valueMap);

		ObjectMapper mapper = new ObjectMapper();
		String output = mapper.writeValueAsString(response);
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdRepoErrorConstants.CLIENT_ERROR, output, response));
		otpManager.validateOtp("Test123", "123456", "426789089018");
	}

	@Ignore
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestRestServiceExceptionwithInvalidWithoutStatus()
			throws RestServiceException, IdAuthenticationBusinessException, JsonProcessingException,
			com.fasterxml.jackson.core.JsonProcessingException {

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> responseMap = new HashMap<>();
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failure");
		response.put("message", "VALIDATION_UNSUCCESSFUL");
		responseMap.put("response", response);
		String output = mapper.writeValueAsString(response);
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdRepoErrorConstants.CLIENT_ERROR, output, responseMap));
		otpManager.validateOtp("Test123", "123456", "426789089018");
	}

	@Ignore
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestRestServiceExceptionwithInvalidWithoutStatusWithOTPNOTGENERATEDError()
			throws RestServiceException, IdAuthenticationBusinessException, JsonProcessingException {

		Map<String, Object> valuemap = new HashMap<>();
		valuemap.put("status", "failure");
		valuemap.put("message", "VALIDATION_UNSUCCESSFUL");
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("response", valuemap);
		String output = "{\"errors\":[{\"errorCode\":\"KER-OTV-005\",\"errorMessage\":\"Validation can't be performed against this key. Generate OTP first.\"}]}";
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		RestServiceException restServiceException = new RestServiceException(IdRepoErrorConstants.CLIENT_ERROR, output,
				responseMap);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(restServiceException);
		otpManager.validateOtp("Test123", "123456", "426789089018");
	}

	@Ignore
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestRestServiceExceptionwithInvalidWithoutStatusWithSOMEOTHERERRORDError()
			throws RestServiceException, IdAuthenticationBusinessException, JsonProcessingException {

		Map<String, Object> response = new HashMap<>();
		response.put("status", "failure");
		response.put("message", "VALIDATION_UNSUCCESSFUL");
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("response", response);
		String output = "{\"errors\":[{\"errorCode\":\"KER-SOME-OTHER-001\",\"errorMessage\":\"Some other error.\"}]}";
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		RestServiceException restServiceException = new RestServiceException(IdRepoErrorConstants.CLIENT_ERROR, output,
				responseMap);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(restServiceException);
		otpManager.validateOtp("Test123", "123456", "426789089018");
	}

	@Ignore
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestDataValidationException() throws IdAuthenticationBusinessException {
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenThrow(new IDDataValidationException());
		otpManager.validateOtp("Test123", "123456", "426789089018");
	}

	@Test(expected = IdAuthUncheckedException.class)
	public void TestGenerateOtpDataValidationException() throws IdAuthenticationBusinessException {
		OtpRequestDTO otpRequestDTO = getOtpRequestDto();
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenThrow(new IDDataValidationException(
						IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorCode(),
						IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorMessage()));
		Map<String, String> valueMap = new HashMap<>();
		valueMap.put("namePri", "Name in PrimaryLang");
		valueMap.put("nameSec", "Name in SecondaryLang");
		otpManager.sendOtp(otpRequestDTO, "123456", "UIN", valueMap, templateLanguages);
	}

	@Ignore
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestDataValidationExceptiononGenerateOtp()
			throws IdAuthenticationBusinessException, RestServiceException {
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenThrow(new IDDataValidationException());
		ResponseWrapper<OtpGeneratorResponseDto> otpGeneratorResponsedto = new ResponseWrapper<>();
		List<ServiceError> errors = new ArrayList<>();
		ServiceError serviceError = new ServiceError();
		serviceError.setErrorCode(OtpErrorConstants.PHONENOTREGISTERED.getErrorCode());
		serviceError.setMessage(OtpErrorConstants.PHONENOTREGISTERED.getErrorCode());
		errors.add(serviceError);
		otpGeneratorResponsedto.setErrors(errors);
		OtpGeneratorResponseDto response = new OtpGeneratorResponseDto();
		response.setStatus("failure");
		response.setMessage(USER_BLOCKED);
		otpGeneratorResponsedto.setResponse(response);
		RestServiceException restServiceException = new RestServiceException(IdRepoErrorConstants.CLIENT_ERROR,
				otpGeneratorResponsedto.toString(), otpGeneratorResponsedto);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(restServiceException);
		OtpRequestDTO otpRequestDTO = getOtpRequestDto();
		Map<String, String> valueMap = new HashMap<>();
		valueMap.put("namePri", "Name in PrimaryLang");
		valueMap.put("nameSec", "Name in SecondaryLang");
		otpManager.sendOtp(otpRequestDTO, "Test123", "UIN", valueMap, templateLanguages);
	}

	@Ignore
	@Test(expected = IdAuthenticationBusinessException.class)
	public void testValidateOTP_ThrowRestServiceExceptionWith_StatusFailureAndMessageUSER_BLOCKED()
			throws JsonProcessingException, RestServiceException, IdAuthenticationBusinessException,
			com.fasterxml.jackson.core.JsonProcessingException {
		Map<Object, Object> response = new HashMap<>();
		Map<Object, Object> valueMap = new HashMap<>();

		response.put("status", "failure");
		response.put("message", "USER_BLOCKED");
		valueMap.put("response", response);

		ObjectMapper mapper = new ObjectMapper();
		String output = mapper.writeValueAsString(response);
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);

		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdRepoErrorConstants.CLIENT_ERROR, output, valueMap));
		otpManager.validateOtp("Test123", "123456", "426789089018");
	}

	@Ignore
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestResponseBodyisEmpty() throws RestServiceException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdRepoErrorConstants.CLIENT_ERROR));
		OtpRequestDTO otpRequestDTO = getOtpRequestDto();
		Map<String, String> valueMap = new HashMap<>();
		valueMap.put("namePri", "Name in PrimaryLang");
		valueMap.put("nameSec", "Name in SecondaryLang");
		otpManager.sendOtp(otpRequestDTO, "123456", "UIN", valueMap, templateLanguages);
	}

	@Test
	public void TestInvalidAttemptWith_noEntity()
			throws RestServiceException, IdAuthUncheckedException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);

		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("hash");

		try {
			otpManager.validateOtp("Test123", "123456", "426789089018");
		} catch (IdAuthenticationBusinessException ex) {
			assertEquals(IdAuthenticationErrorConstants.OTP_REQUEST_REQUIRED.getErrorCode(), ex.getErrorCode());
		}
	}

	@Test
	public void TestInvalidAttemptWith_UsedEntity()
			throws RestServiceException, IdAuthUncheckedException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);

		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("hash");
		OtpTransaction otpEntity = new OtpTransaction();
		otpEntity.setStatusCode(IdAuthCommonConstants.USED_STATUS);
		otpEntity.setOtpHash("otphash");

		Mockito.when(otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(Mockito.anyString(), Mockito.anyList())).thenReturn(Optional.of(otpEntity ));

		try {
			otpManager.validateOtp("Test123", "123456", "426789089018");
		} catch (IdAuthenticationBusinessException ex) {
			assertEquals(IdAuthenticationErrorConstants.OTP_REQUEST_REQUIRED.getErrorCode(), ex.getErrorCode());
		}
	}

	@Test
	public void TestInvalidAttemptWith_nullUpdateCount()
			throws RestServiceException, IdAuthUncheckedException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);

		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("hash");
		OtpTransaction otpEntity = new OtpTransaction();
		otpEntity.setStatusCode(IdAuthCommonConstants.ACTIVE_STATUS);
		otpEntity.setOtpHash("otphash");

		Mockito.when(otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(Mockito.anyString(), Mockito.anyList())).thenReturn(Optional.of(otpEntity ));

		try {
			boolean result = otpManager.validateOtp("Test123", "123456", "426789089018");
			assertFalse(result);
			assertEquals((long)1, (long)otpEntity.getValidationRetryCount());
			assertEquals(IdAuthCommonConstants.ACTIVE_STATUS, otpEntity.getStatusCode());
			verify(otpRepo, times(1)).save(otpEntity);
		} catch (IdAuthenticationBusinessException ex) {
			fail();
		}
	}

	@Test
	public void TestInvalidAttemptWith_1UpdateCount()
			throws RestServiceException, IdAuthUncheckedException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);

		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("hash");
		OtpTransaction otpEntity = new OtpTransaction();
		otpEntity.setStatusCode(IdAuthCommonConstants.ACTIVE_STATUS);
		otpEntity.setValidationRetryCount(1);
		otpEntity.setOtpHash("otphash");

		Mockito.when(otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(Mockito.anyString(), Mockito.anyList())).thenReturn(Optional.of(otpEntity ));

		try {
			boolean result = otpManager.validateOtp("Test123", "123456", "426789089018");
			assertFalse(result);
			assertEquals((long)2, (long)otpEntity.getValidationRetryCount());
			assertEquals(IdAuthCommonConstants.ACTIVE_STATUS, otpEntity.getStatusCode());
			verify(otpRepo, times(1)).save(otpEntity);
		} catch (IdAuthenticationBusinessException ex) {
			fail();
		}
	}

	@Test
	public void TestInvalidAttemptWith_4UpdateCount()
			throws RestServiceException, IdAuthUncheckedException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);

		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("hash");
		OtpTransaction otpEntity = new OtpTransaction();
		otpEntity.setStatusCode(IdAuthCommonConstants.ACTIVE_STATUS);
		otpEntity.setValidationRetryCount(4);
		otpEntity.setOtpHash("otphash");

		Mockito.when(otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(Mockito.anyString(), Mockito.anyList())).thenReturn(Optional.of(otpEntity ));

		try {
			otpManager.validateOtp("Test123", "123456", "426789089018");
		} catch (IdAuthenticationBusinessException ex) {
			assertEquals((long)5, (long)otpEntity.getValidationRetryCount());
			assertEquals(IdAuthCommonConstants.FROZEN, otpEntity.getStatusCode());
			verify(otpRepo, times(1)).save(otpEntity);
			assertEquals(IdAuthenticationErrorConstants.OTP_FROZEN.getErrorCode(), ex.getErrorCode());
			assertEquals(FROZEN_ERROR_MESSAGE, ex.getErrorText());
		}
	}

	@Test
	public void TestInvalidAttemptWith_FrozenStatus()
			throws RestServiceException, IdAuthUncheckedException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);

		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("hash");
		OtpTransaction otpEntity = new OtpTransaction();
		otpEntity.setStatusCode(IdAuthCommonConstants.FROZEN);
		otpEntity.setUpdDTimes(DateUtils.getUTCCurrentDateTime().minus(25, ChronoUnit.MINUTES));
		otpEntity.setValidationRetryCount(5);
		otpEntity.setOtpHash("otphash");

		Mockito.when(otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(Mockito.anyString(), Mockito.anyList())).thenReturn(Optional.of(otpEntity ));

		try {
			otpManager.validateOtp("Test123", "123456", "426789089018");
			fail();
		} catch (IdAuthenticationBusinessException ex) {
			assertEquals((long)5, (long)otpEntity.getValidationRetryCount());
			assertEquals(IdAuthCommonConstants.FROZEN, otpEntity.getStatusCode());
			verify(otpRepo, times(0)).save(otpEntity);
			assertEquals(IdAuthenticationErrorConstants.OTP_FROZEN.getErrorCode(), ex.getErrorCode());
			assertEquals(FROZEN_ERROR_MESSAGE, ex.getErrorText());
		}
	}

	@Test
	public void TestInvalidAttemptWith_FrozenStatusWithin25Mins()
			throws RestServiceException, IdAuthUncheckedException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);

		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("hash");
		OtpTransaction otpEntity = new OtpTransaction();
		otpEntity.setStatusCode(IdAuthCommonConstants.FROZEN);
		otpEntity.setValidationRetryCount(5);
		otpEntity.setUpdDTimes(DateUtils.getUTCCurrentDateTime().minus(25, ChronoUnit.MINUTES));
		otpEntity.setOtpHash("otphash");

		Mockito.when(otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(Mockito.anyString(), Mockito.anyList())).thenReturn(Optional.of(otpEntity ));

		try {
			otpManager.validateOtp("Test123", "123456", "426789089018");
			fail();
		} catch (IdAuthenticationBusinessException ex) {
			assertEquals((long)5, (long)otpEntity.getValidationRetryCount());
			assertEquals(IdAuthCommonConstants.FROZEN, otpEntity.getStatusCode());
			verify(otpRepo, times(0)).save(otpEntity);
			assertEquals(IdAuthenticationErrorConstants.OTP_FROZEN.getErrorCode(), ex.getErrorCode());
			assertEquals(FROZEN_ERROR_MESSAGE, ex.getErrorText());
		}
	}

	@Test
	public void TestInvalidAttemptWith_FrozenStatusWithin29Mins()
			throws RestServiceException, IdAuthUncheckedException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);

		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("hash");
		OtpTransaction otpEntity = new OtpTransaction();
		otpEntity.setStatusCode(IdAuthCommonConstants.FROZEN);
		otpEntity.setValidationRetryCount(5);
		otpEntity.setUpdDTimes(DateUtils.getUTCCurrentDateTime().minus(29, ChronoUnit.MINUTES));
		otpEntity.setOtpHash("otphash");

		Mockito.when(otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(Mockito.anyString(), Mockito.anyList())).thenReturn(Optional.of(otpEntity ));

		try {
			otpManager.validateOtp("Test123", "123456", "426789089018");
			fail();
		} catch (IdAuthenticationBusinessException ex) {
			assertEquals((long)5, (long)otpEntity.getValidationRetryCount());
			assertEquals(IdAuthCommonConstants.FROZEN, otpEntity.getStatusCode());
			verify(otpRepo, times(0)).save(otpEntity);
			assertEquals(IdAuthenticationErrorConstants.OTP_FROZEN.getErrorCode(), ex.getErrorCode());
			assertEquals(FROZEN_ERROR_MESSAGE, ex.getErrorText());
		}
	}

	@Test
	public void TestInvalidAttemptWith_FrozenStatusWithin31Mins()
			throws RestServiceException, IdAuthUncheckedException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);

		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("hash");
		OtpTransaction otpEntity = new OtpTransaction();
		otpEntity.setStatusCode(IdAuthCommonConstants.FROZEN);
		otpEntity.setValidationRetryCount(5);
		otpEntity.setUpdDTimes(DateUtils.getUTCCurrentDateTime().minus(31, ChronoUnit.MINUTES));
		otpEntity.setOtpHash("otphash");

		Mockito.when(otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(Mockito.anyString(), Mockito.anyList())).thenReturn(Optional.of(otpEntity ));

		try {
			otpManager.validateOtp("Test123", "123456", "426789089018");
		} catch (IdAuthenticationBusinessException ex) {
			assertEquals(IdAuthCommonConstants.UNFROZEN, otpEntity.getStatusCode());
			assertEquals((long)5, (long)otpEntity.getValidationRetryCount());
			verify(otpRepo, times(1)).save(otpEntity);
			assertEquals(IdAuthenticationErrorConstants.OTP_REQUEST_REQUIRED.getErrorCode(), ex.getErrorCode());
		}
	}


	@Test
	public void TestValidAttemptWith_nullUpdateCount()
			throws RestServiceException, IdAuthUncheckedException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);

		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("hash");
		OtpTransaction otpEntity = new OtpTransaction();
		otpEntity.setStatusCode(IdAuthCommonConstants.ACTIVE_STATUS);
		otpEntity.setOtpHash("313233343536234B45595F53504C49545445522354657374313233");
		otpEntity.setExpiryDtimes(DateUtils.getUTCCurrentDateTime().plus(1, ChronoUnit.MINUTES));

		Mockito.when(otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(Mockito.anyString(), Mockito.anyList())).thenReturn(Optional.of(otpEntity ));

		try {
			boolean result = otpManager.validateOtp("Test123", "123456", "426789089018");
			assertTrue(result);
			assertEquals(IdAuthCommonConstants.USED_STATUS, otpEntity.getStatusCode());
			verify(otpRepo, times(1)).save(otpEntity);
		} catch (IdAuthenticationBusinessException ex) {
			fail();
		}
	}

	@Test
	public void TestValidAttemptWith_1UpdateCount()
			throws RestServiceException, IdAuthUncheckedException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);

		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("hash");
		OtpTransaction otpEntity = new OtpTransaction();
		otpEntity.setStatusCode(IdAuthCommonConstants.ACTIVE_STATUS);
		otpEntity.setValidationRetryCount(1);
		otpEntity.setOtpHash("313233343536234B45595F53504C49545445522354657374313233");
		otpEntity.setExpiryDtimes(DateUtils.getUTCCurrentDateTime().plus(1, ChronoUnit.MINUTES));

		Mockito.when(otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(Mockito.anyString(), Mockito.anyList())).thenReturn(Optional.of(otpEntity ));

		try {
			boolean result = otpManager.validateOtp("Test123", "123456", "426789089018");
			assertTrue(result);
			assertEquals(IdAuthCommonConstants.USED_STATUS, otpEntity.getStatusCode());
			verify(otpRepo, times(1)).save(otpEntity);
		} catch (IdAuthenticationBusinessException ex) {
			fail();
		}
	}

	@Test
	public void TestValidAttemptWith_4UpdateCount()
			throws RestServiceException, IdAuthUncheckedException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);

		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("hash");
		OtpTransaction otpEntity = new OtpTransaction();
		otpEntity.setStatusCode(IdAuthCommonConstants.ACTIVE_STATUS);
		otpEntity.setValidationRetryCount(4);
		otpEntity.setOtpHash("313233343536234B45595F53504C49545445522354657374313233");
		otpEntity.setExpiryDtimes(DateUtils.getUTCCurrentDateTime().plus(1, ChronoUnit.MINUTES));

		Mockito.when(otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(Mockito.anyString(), Mockito.anyList())).thenReturn(Optional.of(otpEntity ));

		try {
			boolean result = otpManager.validateOtp("Test123", "123456", "426789089018");
			assertTrue(result);
			assertEquals(IdAuthCommonConstants.USED_STATUS, otpEntity.getStatusCode());
			verify(otpRepo, times(1)).save(otpEntity);
		} catch (IdAuthenticationBusinessException ex) {
			fail();
		}
	}

	@Test
	public void TestValidAttemptWith_FrozenStatus()
			throws RestServiceException, IdAuthUncheckedException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);

		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("hash");
		OtpTransaction otpEntity = new OtpTransaction();
		otpEntity.setStatusCode(IdAuthCommonConstants.FROZEN);
		otpEntity.setUpdDTimes(DateUtils.getUTCCurrentDateTime().minus(25, ChronoUnit.MINUTES));
		otpEntity.setValidationRetryCount(5);
		otpEntity.setOtpHash("313233343536234B45595F53504C49545445522354657374313233");
		otpEntity.setExpiryDtimes(DateUtils.getUTCCurrentDateTime().plus(1, ChronoUnit.MINUTES));

		Mockito.when(otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(Mockito.anyString(), Mockito.anyList())).thenReturn(Optional.of(otpEntity ));

		try {
			otpManager.validateOtp("Test123", "123456", "426789089018");
			fail();
		} catch (IdAuthenticationBusinessException ex) {
			assertEquals((long)5, (long)otpEntity.getValidationRetryCount());
			assertEquals(IdAuthCommonConstants.FROZEN, otpEntity.getStatusCode());
			verify(otpRepo, times(0)).save(otpEntity);
			assertEquals(IdAuthenticationErrorConstants.OTP_FROZEN.getErrorCode(), ex.getErrorCode());
			assertEquals(FROZEN_ERROR_MESSAGE, ex.getErrorText());
		}
	}

	@Test
	public void TestValidAttemptWith_FrozenStatusWithin25Mins()
			throws RestServiceException, IdAuthUncheckedException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);

		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("hash");
		OtpTransaction otpEntity = new OtpTransaction();
		otpEntity.setStatusCode(IdAuthCommonConstants.FROZEN);
		otpEntity.setValidationRetryCount(5);
		otpEntity.setUpdDTimes(DateUtils.getUTCCurrentDateTime().minus(25, ChronoUnit.MINUTES));
		otpEntity.setOtpHash("313233343536234B45595F53504C49545445522354657374313233");
		otpEntity.setExpiryDtimes(DateUtils.getUTCCurrentDateTime().plus(1, ChronoUnit.MINUTES));

		Mockito.when(otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(Mockito.anyString(), Mockito.anyList())).thenReturn(Optional.of(otpEntity ));

		try {
			otpManager.validateOtp("Test123", "123456", "426789089018");
			fail();
		} catch (IdAuthenticationBusinessException ex) {
			assertEquals((long)5, (long)otpEntity.getValidationRetryCount());
			assertEquals(IdAuthCommonConstants.FROZEN, otpEntity.getStatusCode());
			verify(otpRepo, times(0)).save(otpEntity);
			assertEquals(IdAuthenticationErrorConstants.OTP_FROZEN.getErrorCode(), ex.getErrorCode());
			assertEquals(FROZEN_ERROR_MESSAGE, ex.getErrorText());
		}
	}

	@Test
	public void TestValidAttemptWith_FrozenStatusWithin29Mins()
			throws RestServiceException, IdAuthUncheckedException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);

		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("hash");
		OtpTransaction otpEntity = new OtpTransaction();
		otpEntity.setStatusCode(IdAuthCommonConstants.FROZEN);
		otpEntity.setValidationRetryCount(5);
		otpEntity.setUpdDTimes(DateUtils.getUTCCurrentDateTime().minus(29, ChronoUnit.MINUTES));
		otpEntity.setOtpHash("313233343536234B45595F53504C49545445522354657374313233");
		otpEntity.setExpiryDtimes(DateUtils.getUTCCurrentDateTime().plus(1, ChronoUnit.MINUTES));

		Mockito.when(otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(Mockito.anyString(), Mockito.anyList())).thenReturn(Optional.of(otpEntity ));

		try {
			otpManager.validateOtp("Test123", "123456", "426789089018");
			fail();
		} catch (IdAuthenticationBusinessException ex) {
			assertEquals((long)5, (long)otpEntity.getValidationRetryCount());
			assertEquals(IdAuthCommonConstants.FROZEN, otpEntity.getStatusCode());
			verify(otpRepo, times(0)).save(otpEntity);
			assertEquals(IdAuthenticationErrorConstants.OTP_FROZEN.getErrorCode(), ex.getErrorCode());
			assertEquals(FROZEN_ERROR_MESSAGE, ex.getErrorText());
		}
	}

	@Test
	public void TestValidAttemptWith_FrozenStatusWithin31Mins()
			throws RestServiceException, IdAuthUncheckedException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);

		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("hash");
		OtpTransaction otpEntity = new OtpTransaction();
		otpEntity.setStatusCode(IdAuthCommonConstants.FROZEN);
		otpEntity.setValidationRetryCount(5);
		otpEntity.setUpdDTimes(DateUtils.getUTCCurrentDateTime().minus(31, ChronoUnit.MINUTES));
		otpEntity.setOtpHash("313233343536234B45595F53504C49545445522354657374313233");
		otpEntity.setExpiryDtimes(DateUtils.getUTCCurrentDateTime().plus(1, ChronoUnit.MINUTES));

		Mockito.when(otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(Mockito.anyString(), Mockito.anyList())).thenReturn(Optional.of(otpEntity ));

		try {
			otpManager.validateOtp("Test123", "123456", "426789089018");
		} catch (IdAuthenticationBusinessException ex) {
			assertEquals(IdAuthCommonConstants.UNFROZEN, otpEntity.getStatusCode());
			assertEquals((long)5, (long)otpEntity.getValidationRetryCount());
			verify(otpRepo, times(1)).save(otpEntity);
			assertEquals(IdAuthenticationErrorConstants.OTP_REQUEST_REQUIRED.getErrorCode(), ex.getErrorCode());
		}
	}

	@Test
	public void TestValidAttemptWith_FrozenStatusWithin31Mins_expiredOtp()
			throws RestServiceException, IdAuthUncheckedException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);

		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("hash");
		OtpTransaction otpEntity = new OtpTransaction();
		otpEntity.setStatusCode(IdAuthCommonConstants.FROZEN);
		otpEntity.setValidationRetryCount(5);
		otpEntity.setUpdDTimes(DateUtils.getUTCCurrentDateTime().minus(31, ChronoUnit.MINUTES));
		otpEntity.setOtpHash("313233343536234B45595F53504C49545445522354657374313233");
		otpEntity.setExpiryDtimes(DateUtils.getUTCCurrentDateTime().minus(1, ChronoUnit.MINUTES));

		Mockito.when(otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(Mockito.anyString(), Mockito.anyList())).thenReturn(Optional.of(otpEntity ));

		try {
			otpManager.validateOtp("Test123", "123456", "426789089018");
		} catch (IdAuthenticationBusinessException ex) {
			assertEquals((long)5, (long)otpEntity.getValidationRetryCount());
			assertEquals(IdAuthCommonConstants.UNFROZEN, otpEntity.getStatusCode());
			verify(otpRepo, times(1)).save(otpEntity);
			assertEquals(IdAuthenticationErrorConstants.OTP_REQUEST_REQUIRED.getErrorCode(), ex.getErrorCode());
		}
	}

	@Test
	public void TestThrowOtpException_UINLocked()
			throws RestServiceException, IdAuthUncheckedException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Map<String, Object> responseMap = new HashMap<>();

		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("status", "failure");
		valueMap.put("message", USER_BLOCKED);
		responseMap.put("response", valueMap);

		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdRepoErrorConstants.CLIENT_ERROR, responseMap.toString(), (Object) responseMap));

		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("hash");
		OtpTransaction otpEntity = new OtpTransaction();
		otpEntity.setOtpHash("otphash");
		otpEntity.setStatusCode(IdAuthCommonConstants.ACTIVE_STATUS);

		Mockito.when(otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(Mockito.anyString(), Mockito.anyList())).thenReturn(Optional.of(otpEntity ));

		try {
			otpManager.validateOtp("Test123", "123456", "426789089018");
		} catch (IdAuthenticationBusinessException ex) {
			assertEquals(IdAuthenticationErrorConstants.BLOCKED_OTP_VALIDATE.getErrorCode(), ex.getErrorCode());
			assertEquals(IdAuthenticationErrorConstants.BLOCKED_OTP_VALIDATE.getErrorMessage(), ex.getErrorText());
		}
	}

	@Test
	public void TestThrowOtpException_OtpExpired() throws RestServiceException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("status", "failure");
		valueMap.put("message", OTP_EXPIRED);
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("response", valueMap);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdRepoErrorConstants.CLIENT_ERROR, responseMap.toString(), (Object) responseMap));

		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("hash");
		OtpTransaction otpEntity = new OtpTransaction();
		otpEntity.setStatusCode(IdAuthCommonConstants.ACTIVE_STATUS);
		otpEntity.setOtpHash("otphash");

		Mockito.when(otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(Mockito.anyString(), Mockito.anyList())).thenReturn(Optional.of(otpEntity ));

		try {
			otpManager.validateOtp("Test123", "123456", "426789089018");
		} catch (IdAuthenticationBusinessException ex) {
			assertEquals(IdAuthenticationErrorConstants.EXPIRED_OTP.getErrorCode(), ex.getErrorCode());
			assertEquals(IdAuthenticationErrorConstants.EXPIRED_OTP.getErrorMessage(), ex.getErrorText());
		}
	}

	@Test
	public void TestThrowOtpException_ValidationUnsuccessful()
			throws RestServiceException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("status", "failure");
		valueMap.put("message", VALIDATION_UNSUCCESSFUL);
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("response", valueMap);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdRepoErrorConstants.CLIENT_ERROR, responseMap.toString(), (Object) responseMap));
		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("hash");
		OtpTransaction otpEntity = new OtpTransaction();
		otpEntity.setStatusCode(IdAuthCommonConstants.ACTIVE_STATUS);
		otpEntity.setOtpHash("otphash");

		Mockito.when(otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(Mockito.anyString(), Mockito.anyList())).thenReturn(Optional.of(otpEntity ));
		try {
			otpManager.validateOtp("Test123", "123456", "426789089018");
		} catch (IdAuthenticationBusinessException ex) {
			assertEquals(IdAuthenticationErrorConstants.INVALID_OTP.getErrorCode(), ex.getErrorCode());
			assertEquals(IdAuthenticationErrorConstants.INVALID_OTP.getErrorMessage(), ex.getErrorText());
		}
	}

	@Test
	public void TestThrowOtpException_OtpPresent_Expired()
			throws RestServiceException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("status", "failure");
		valueMap.put("message", VALIDATION_UNSUCCESSFUL);
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("response", valueMap);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdRepoErrorConstants.CLIENT_ERROR, responseMap.toString(), (Object) responseMap));
		OtpTransaction otpEntry = new OtpTransaction();
		otpEntry.setExpiryDtimes(DateUtils.getUTCCurrentDateTime().minus(1, ChronoUnit.HOURS));
		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("hash");
		otpEntry.setStatusCode(IdAuthCommonConstants.ACTIVE_STATUS);
		otpEntry.setOtpHash("otphash");
		Mockito.when(otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(Mockito.anyString(), Mockito.anyList())).thenReturn(Optional.of(otpEntry));
		try {
			otpManager.validateOtp("Test123", "123456", "426789089018");
		} catch (IdAuthenticationBusinessException ex) {
			assertEquals(IdAuthenticationErrorConstants.EXPIRED_OTP.getErrorCode(), ex.getErrorCode());
			assertEquals(IdAuthenticationErrorConstants.EXPIRED_OTP.getErrorMessage(), ex.getErrorText());
		}
	}

	@Test
	public void TestThrowOtpException_OtpPresent_NotExpired_Valid()
			throws RestServiceException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("status", "failure");
		valueMap.put("message", VALIDATION_UNSUCCESSFUL);
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("response", valueMap);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdRepoErrorConstants.CLIENT_ERROR, responseMap.toString(), (Object) responseMap));
		OtpTransaction otpEntry = new OtpTransaction();
		otpEntry.setExpiryDtimes(DateUtils.getUTCCurrentDateTime().plus(1, ChronoUnit.HOURS));
		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("hash");
		otpEntry.setStatusCode(IdAuthCommonConstants.ACTIVE_STATUS);
		otpEntry.setOtpHash("otphash");
		Mockito.when(otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(Mockito.anyString(), Mockito.anyList())).thenReturn(Optional.of(otpEntry));
		try {
			otpManager.validateOtp("Test123", "123456", "426789089018");
		} catch (IdAuthenticationBusinessException ex) {
			assertEquals(IdAuthenticationErrorConstants.INVALID_OTP.getErrorCode(), ex.getErrorCode());
			assertEquals(IdAuthenticationErrorConstants.INVALID_OTP.getErrorMessage(), ex.getErrorText());
		}
	}

	@Ignore
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestThrowOtpException_ValidationUnsuccessful_Invalid()
			throws RestServiceException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Map<String, Object> responseMap = new HashMap<>();
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("status", "failure");
		valueMap.put("message", "invalid");
		responseMap.put("response", valueMap);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdRepoErrorConstants.CLIENT_ERROR, responseMap.toString(), (Object) responseMap));
		otpManager.validateOtp("Test123", "123456", "426789089018");
	}

	@Ignore
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestthrowOTPException() throws Throwable {
		try {
			ReflectionTestUtils.invokeMethod(otpManager, "throwOtpException", OTP_EXPIRED);
		} catch (Exception e) {
			throw e.getCause();
		}

	}

	@Ignore
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestThrowKeynotFound() throws RestServiceException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Map<String, Object> responseMap = new HashMap<>();
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("status", "12343");
		valueMap.put("message", "12343");
		responseMap.put("response", valueMap);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdRepoErrorConstants.CLIENT_ERROR, responseMap.toString(), (Object) responseMap));
		otpManager.validateOtp("Test123", "123456", "426789089018");
	}

	@Ignore
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestthrowKeyNotFoundException() throws IdAuthenticationBusinessException, RestServiceException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("status", "failure");
		valueMap.put("message", OTP_EXPIRED);
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("response", valueMap);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdRepoErrorConstants.CLIENT_ERROR, responseMap.toString(), (Object) responseMap));
		otpManager.validateOtp("Test123", "123456", "426789089018");
	}

	@Ignore
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestIDDataValidationException() throws RestServiceException, IdAuthenticationBusinessException {
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenThrow(new IDDataValidationException());
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("status", "failure");
		valueMap.put("message", OTP_EXPIRED);
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("response", valueMap);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdRepoErrorConstants.CLIENT_ERROR, responseMap.toString(), (Object) responseMap));
		otpManager.validateOtp("Test123", "123456", "426789089018");

	}

	@Ignore
	@SuppressWarnings("rawtypes")
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestUserBlocked() throws RestServiceException, IdAuthenticationBusinessException {
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenThrow(new IDDataValidationException());
		ResponseWrapper<Map> otpGeneratorResponsetDto = new ResponseWrapper<>();
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failure");
		response.put("message", USER_BLOCKED);
		otpGeneratorResponsetDto.setResponse(response);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(otpGeneratorResponsetDto);
		OtpRequestDTO otpRequestDTO = getOtpRequestDto();
		Map<String, String> valueMap = new HashMap<>();
		valueMap.put("primaryLang", "fra");
		valueMap.put("secondayLang", "ara");
		valueMap.put("namePri", "ida-otp-auth");
		valueMap.put("nameSec", "ida-otp-auth");
		otpManager.sendOtp(otpRequestDTO, "426789089018", "UIN", valueMap, templateLanguages);
	}

	@Test(expected = IdAuthUncheckedException.class)
	public void TestInvalidGenerateOtp() throws RestServiceException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdRepoErrorConstants.CLIENT_ERROR, null, null));
		OtpRequestDTO otpRequestDTO = getOtpRequestDto();
		Map<String, String> valueMap = new HashMap<>();
		valueMap.put("namePri", "Name in PrimaryLang");
		valueMap.put("nameSec", "Name in SecondaryLang");
		otpManager.sendOtp(otpRequestDTO, "Test123", "UIN", valueMap, templateLanguages);
	}

	@Ignore
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestThrowOtpKeyException() throws RestServiceException, IdAuthenticationBusinessException,
			JsonProcessingException, com.fasterxml.jackson.core.JsonProcessingException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Map<String, Object> valueMap = new HashMap<>();
		Map<Object, Object> errorMap = new HashMap<>();
		List<Map<Object, Object>> errorList = new ArrayList<Map<Object, Object>>();
		errorMap.put("errorCode", "KER-OTV-005");
		errorList.add(errorMap);
		valueMap.put("errors", (Object) errorList);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdRepoErrorConstants.CLIENT_ERROR, new ObjectMapper().writeValueAsString(valueMap), valueMap));
		otpManager.validateOtp("Test123", "123456", "426789089018");

	}

	private OtpRequestDTO getOtpRequestDto() {
		OtpRequestDTO otpRequestDTO = new OtpRequestDTO();
		otpRequestDTO.setId("mosip.identity.otp");
		otpRequestDTO.setIndividualId("426789089018");
		otpRequestDTO.setIndividualIdType(IdType.UIN.getType());
		List<String> otpChannel = new ArrayList<>();
		otpChannel.add("mobile");
		otpChannel.add("email");
		otpRequestDTO.setOtpChannel(otpChannel);
		otpRequestDTO.setRequestTime(LocalDateTime.now().toString());
		return otpRequestDTO;
	}

	private RestRequestDTO getRestRequestvalidDTO() {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		restRequestDTO.setHttpMethod(HttpMethod.POST);
		restRequestDTO.setUri("http://localhost:8083/otpmanager/otps");
		restRequestDTO.setResponseType(Map.class);
		restRequestDTO.setTimeout(23);
		return restRequestDTO;
	}

}
