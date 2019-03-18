package io.mosip.authentication.service.impl.otpgen.service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.otpgen.ChannelDTO;
import io.mosip.authentication.core.dto.otpgen.OtpRequestDTO;
import io.mosip.authentication.core.dto.otpgen.OtpResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.spi.id.service.IdRepoService;
import io.mosip.authentication.core.util.OTPUtil;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.authentication.service.entity.AutnTxn;
import io.mosip.authentication.service.factory.AuditRequestFactory;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.impl.notification.service.NotificationServiceImpl;
import io.mosip.authentication.service.integration.IdTemplateManager;
import io.mosip.authentication.service.integration.OTPManager;
import io.mosip.authentication.service.integration.dto.OtpGeneratorResponseDto;
import io.mosip.authentication.service.repository.AutnTxnRepository;
import io.mosip.kernel.core.exception.ParseException;

/**
 * Test class for OTPServiceImpl.
 *
 * @author Rakesh Roshan
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@WebMvcTest
public class OTPServiceImplTest {

	OtpRequestDTO otpRequestDto;
	@Mock
	OtpResponseDTO otpResponseDTO;
	@Mock
	OTPServiceImpl otpService;

	@Mock
	AutnTxnRepository autntxnrepository;
	@Mock
	Date date;
	@Mock
	AutnTxn autnTxn;
	@Mock
	IdAuthService<AutnTxn> idAuthService;

	@Mock
	IdTemplateManager idTemplateService;

	@Mock
	private IdRepoService idInfoService;

	@Mock
	private IdInfoHelper idInfoHelper;

	@Mock
	NotificationServiceImpl notificationService;

	@Mock
	private OTPManager otpManager;

	@Mock
	private RestRequestFactory restRequestFactory;
	@Mock
	private AuditRequestFactory auditRequestFactory;
	@Mock
	private RestHelper restHelper;

	@Autowired
	Environment env;

	@InjectMocks
	private OTPServiceImpl otpServiceImpl;

	@Mock
	private OTPServiceImpl otpServiceImplmock;

	@Before
	public void before() {
		otpRequestDto = getOtpRequestDTO();
		otpResponseDTO = getOtpResponseDTO();
		ReflectionTestUtils.setField(otpServiceImpl, "env", env);
		ReflectionTestUtils.setField(otpServiceImpl, "idInfoHelper", idInfoHelper);
		ReflectionTestUtils.setField(notificationService, "env", env);
		ReflectionTestUtils.setField(notificationService, "idTemplateManager", idTemplateService);
		ReflectionTestUtils.setField(idInfoHelper, "environment", env);
		ReflectionTestUtils.setField(otpServiceImpl, "notificationService", notificationService);
		ReflectionTestUtils.setField(otpServiceImpl, "idAuthService", idAuthService);
		ReflectionTestUtils.setField(otpServiceImpl, "idInfoService", idInfoService);
	}

	private OtpRequestDTO getOtpRequestDTO() {
		OtpRequestDTO otpRequestDto = new OtpRequestDTO();
		otpRequestDto.setId("id");
		otpRequestDto.setRequestTime(new SimpleDateFormat(env.getProperty("datetime.pattern")).format(new Date()));
		otpRequestDto.setTransactionID("1234567890");
		ChannelDTO otpChannel = new ChannelDTO();
		otpChannel.setPhone(true);
		otpChannel.setEmail(true);
		otpRequestDto.setOtpChannel(otpChannel);
		otpRequestDto.setIndividualId("2345678901234");
		otpRequestDto.setIndividualIdType(IdType.UIN.getType());
		otpRequestDto.setRequestTime("2019-02-18T18:17:48.923+05:30");
		return otpRequestDto;
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestOtpChannelnotprovided() throws IdAuthenticationBusinessException {
		Map<String, Object> value = new HashMap<>();
		Map<String, List<IdentityInfoDTO>> idInfo = getIdInfo();
		value.put("response", idInfo);
		value.put("uin", "74834738743");
		Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean())).thenReturn(value);
		Mockito.when(idAuthService.getIdInfo(Mockito.any())).thenReturn(idInfo);
		Mockito.when(autntxnrepository.countRequestDTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
		Mockito.when(idInfoHelper.getEntityInfoAsString(Mockito.any(), Mockito.any())).thenReturn("9999999999");
		Mockito.when(idInfoHelper.getUTCTime(Mockito.anyString())).thenReturn(Instant.now().toString());
		OtpRequestDTO otpRequestDTO = getOtpRequestDTO();
		ChannelDTO otpChannel = new ChannelDTO();
		otpChannel.setEmail(false);
		otpChannel.setPhone(false);
		otpRequestDTO.setOtpChannel(otpChannel);
		Mockito.when(otpManager.generateOTP(Mockito.anyString())).thenReturn("123456");
		otpServiceImpl.generateOtp(otpRequestDTO, "TEST0000001");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestPhonenotRegistered() throws IdAuthenticationBusinessException {
		Map<String, Object> value = new HashMap<>();
		Map<String, List<IdentityInfoDTO>> idInfo = getIdInfo();
		value.put("response", idInfo);
		value.put("uin", "74834738743");
		Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean())).thenReturn(value);
		Mockito.when(idAuthService.getIdInfo(Mockito.any())).thenReturn(idInfo);
		Mockito.when(autntxnrepository.countRequestDTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
		Mockito.when(idInfoHelper.getEntityInfoAsString(Mockito.any(), Mockito.any())).thenReturn("");
		Mockito.when(idInfoHelper.getUTCTime(Mockito.anyString())).thenReturn(Instant.now().toString());
		OtpRequestDTO otpRequestDTO = getOtpRequestDTO();
		ChannelDTO otpChannel = new ChannelDTO();
		otpChannel.setEmail(false);
		otpChannel.setPhone(true);
		otpRequestDTO.setOtpChannel(otpChannel);
		Mockito.when(otpManager.generateOTP(Mockito.anyString())).thenReturn("123456");
		otpServiceImpl.generateOtp(otpRequestDTO, "TEST0000001");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestParseException() throws IdAuthenticationBusinessException {
		Map<String, Object> value = new HashMap<>();
		Map<String, List<IdentityInfoDTO>> idInfo = getIdInfo();
		value.put("response", idInfo);
		value.put("uin", "74834738743");
		Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean())).thenReturn(value);
		Mockito.when(idAuthService.getIdInfo(Mockito.any())).thenReturn(idInfo);
		Mockito.when(autntxnrepository.countRequestDTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
		Mockito.when(idInfoHelper.getEntityInfoAsString(Mockito.any(), Mockito.any())).thenReturn("9999999999");
		Mockito.when(idInfoHelper.getUTCTime(Mockito.anyString())).thenReturn("");
		OtpRequestDTO otpRequestDTO = getOtpRequestDTO();
		Mockito.when(otpManager.generateOTP(Mockito.anyString())).thenReturn("123456");
		otpServiceImpl.generateOtp(otpRequestDTO, "TEST0000001");
	}

	@Test
	public void TestOtpSuccess() throws IdAuthenticationBusinessException, RestServiceException {
		OtpRequestDTO otpRequestDto = getOtpRequestDTO();
		Map<String, Object> value = new HashMap<>();
		Map<String, List<IdentityInfoDTO>> idInfo = getIdInfo();
		value.put("response", idInfo);
		value.put("uin", "74834738743");
		Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean())).thenReturn(value);
		Mockito.when(idAuthService.getIdInfo(Mockito.any())).thenReturn(idInfo);
		Mockito.when(autntxnrepository.countRequestDTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
		Mockito.when(idInfoHelper.getEntityInfoAsString(Mockito.any(), Mockito.any())).thenReturn("9999999999");
		Mockito.when(idInfoHelper.getUTCTime(Mockito.anyString())).thenReturn(Instant.now().toString());
		RestRequestDTO requestDTO = getRestRequestDTO();
		Mockito.when(otpManager.generateOTP(Mockito.anyString())).thenReturn("123456");
		otpServiceImpl.generateOtp(otpRequestDto, "TEST0000001");
	}

	private Map<String, List<IdentityInfoDTO>> getIdInfo() {
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		IdentityInfoDTO phonedto = new IdentityInfoDTO();
		phonedto.setValue("9999999999");
		IdentityInfoDTO maildto = new IdentityInfoDTO();
		maildto.setValue("abc@test.com");
		identityList.add(maildto);
		identityList.add(phonedto);
		idInfo.put("identity", identityList);
		return idInfo;
	}

	@Test
	public void TestDateParseException() throws IdAuthenticationBusinessException {
		OtpRequestDTO otpRequestDTO = getOtpRequestDTO();
		Mockito.when(otpManager.generateOTP(Mockito.any())).thenReturn("123456");
		Mockito.when(idInfoHelper.getUTCTime(Mockito.any())).thenThrow(new DateTimeParseException("", "", 0));
		otpManager.generateOTP("123456");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestisPhonedisabled() throws IdAuthenticationBusinessException {
		OtpRequestDTO otpRequestDTO = getOtpRequestDTO();
		ChannelDTO otpChannel = new ChannelDTO();
		otpChannel.setPhone(false);
		otpRequestDTO.setOtpChannel(otpChannel);
		otpServiceImpl.generateOtp(otpRequestDto, "TEST0000001");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestgenerateOtpthrowsException() throws IdAuthenticationBusinessException, RestServiceException {
		OtpRequestDTO otpRequestDTO = getOtpRequestDTO();
		Map<String, Object> value = new HashMap<>();
		Map<String, List<IdentityInfoDTO>> idInfo = getIdInfo();
		value.put("response", idInfo);
		value.put("uin", "74834738743");
		Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean())).thenReturn(value);
		Mockito.when(idAuthService.getIdInfo(Mockito.any())).thenReturn(getIdInfo());
		Mockito.when(autntxnrepository.countRequestDTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
		Mockito.when(idInfoHelper.getEntityInfoAsString(Mockito.any(), Mockito.any())).thenReturn("9999999999");
		RestRequestDTO requestDTO = getRestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(requestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED));
		Mockito.when(idInfoHelper.getUTCTime(Mockito.anyString())).thenReturn(Instant.now().toString());
		otpServiceImpl.generateOtp(otpRequestDTO, "TEST0000001");

	}

	private RestRequestDTO getRestRequestDTO() {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		restRequestDTO.setHttpMethod(HttpMethod.POST);
		restRequestDTO.setUri("http://localhost:8083/otpmanager/otps");
		OtpGeneratorResponseDto otpGeneratorRequestDto = new OtpGeneratorResponseDto();
		otpGeneratorRequestDto.setOtp("123456");
		restRequestDTO.setRequestBody(otpGeneratorRequestDto);
		restRequestDTO.setResponseType(OtpGeneratorResponseDto.class);
		restRequestDTO.setTimeout(23);
		return restRequestDTO;
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestisPhoneorEmailisfalse() throws IdAuthenticationBusinessException {
		OtpRequestDTO otpRequestDTO = getOtpRequestDTO();
		ChannelDTO otpChannel = new ChannelDTO();
		otpChannel.setEmail(false);
		otpChannel.setPhone(false);
		otpRequestDTO.setOtpChannel(otpChannel);
		otpServiceImpl.generateOtp(otpRequestDto, "TEST0000001");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestisPhonefalse() throws IdAuthenticationBusinessException {
		OtpRequestDTO otpRequestDTO = getOtpRequestDTO();
		ChannelDTO otpChannel = new ChannelDTO();
		otpChannel.setEmail(true);
		otpChannel.setPhone(false);
		otpRequestDTO.setOtpChannel(otpChannel);
		otpServiceImpl.generateOtp(otpRequestDto, "TEST0000001");
	}

	private OtpResponseDTO getOtpResponseDTO() {
		OtpResponseDTO otpResponseDTO = new OtpResponseDTO();
		otpResponseDTO.setResponseTime(new SimpleDateFormat(env.getProperty("datetime.pattern")).format(new Date()));

		return otpResponseDTO;
	}

	private Map<String, Object> repoDetails() {
		Map<String, Object> map = new HashMap<>();
		map.put("registrationId", "863537");
		return map;
	}

	@Ignore
	@Test(expected = IdAuthenticationBusinessException.class)
	public void test_GenerateOTP() throws IdAuthenticationBusinessException, IdAuthenticationDaoException {
		getOtpRequestDTO();
		String date = null;
		String time = null;
		String language = "fre";
		String mobileNumber = "7697698650";
		String emailId = "abc@abc.com";
		String name = "mosip";
		String txnID = otpRequestDto.getTransactionID();
		String productid = "IDA";
		String uin = "8765";
		String otp = "987654";
		String otpKey = OTPUtil.generateKey(productid, uin, txnID, "TEST0000001");
		Mockito.when(otpManager.generateOTP(otpKey)).thenReturn(otp);
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO(language, name));
		list.add(new IdentityInfoDTO(language, emailId));
		list.add(new IdentityInfoDTO(language, mobileNumber));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(idAuthService.getIdInfo(repoDetails())).thenReturn(idInfo);
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.NAME, idInfo)).thenReturn(name);

		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo)).thenReturn(emailId);

		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.PHONE, idInfo)).thenReturn(mobileNumber);
		Mockito.when(idInfoHelper.getUTCTime(Mockito.anyString())).thenReturn("2019-02-18T12:28:17.078");
		Optional<String> uinOpt = Optional.of("426789089018");
		otpServiceImpl.generateOtp(otpRequestDto, "TEST0000001");
	}

	@Ignore
	@Test
	public void testGenerateOTPSuccess() throws IdAuthenticationBusinessException, IdAuthenticationDaoException {

		String date = null;
		String time = null;
		String language = "fre";
		String mobileNumber = "7697698650";
		String emailId = "abc@abc.com";
		String name = "mosip";
		getOtpRequestDTO();
		String txnID = otpRequestDto.getTransactionID();
		String productid = "IDA";
		String otp = "987654";
		Map<String, Object> repoDetails = repoDetails();
		repoDetails.put("mobileNumber", "7697698650");
		repoDetails.put("emailId", "abc@abc.com");

		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO(language, name));
		list.add(new IdentityInfoDTO(language, emailId));
		list.add(new IdentityInfoDTO(language, mobileNumber));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);

		Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(repoDetails);
		Mockito.when(idAuthService.getIdInfo(repoDetails)).thenReturn(idInfo);
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.NAME, idInfo)).thenReturn(name);

		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo)).thenReturn(emailId);

		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.PHONE, idInfo)).thenReturn(mobileNumber);
		Mockito.when(idInfoHelper.getUTCTime(Mockito.anyString())).thenReturn("2019-02-18T12:28:17.078");
		Optional<String> uinOpt = Optional.of("426789089018");
		ChannelDTO otpChannel = new ChannelDTO();
		otpChannel.setEmail(true);
		otpChannel.setPhone(true);
		otpRequestDto.setOtpChannel(otpChannel);
		otpServiceImpl.generateOtp(otpRequestDto, "TEST0000001");
	}

	@Ignore
	@Test(expected = IdAuthenticationBusinessException.class)
	public void testOTPGeneration() throws IdAuthenticationBusinessException {
		Map<String, Object> idRepo = new HashMap<>();
		idRepo.put("uin", "74834738743");
		idRepo.put("registrationId", "1234567890");
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
//		Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(idRepo);
		String otpKey = "IDA_MTIzNDU2Nzg5MA==_2345678901234_2345678901234";

		Mockito.when(otpManager.generateOTP(otpKey)).thenReturn("123456");
		Mockito.when(otpManager.generateOTP(otpKey)).thenReturn("123456");
		Mockito.when(idAuthService.getIdInfo(repoDetails())).thenReturn(idInfo);
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo)).thenReturn("abc@test.com");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.PHONE, idInfo)).thenReturn("1234567890");

		ReflectionTestUtils.invokeMethod(otpServiceImpl, "getEmail", idInfo);
		ReflectionTestUtils.invokeMethod(otpServiceImpl, "getMobileNumber", idInfo);
		Mockito.when(idInfoHelper.getUTCTime(Mockito.anyString())).thenReturn("2019-02-18T12:28:17.078");
		otpServiceImpl.generateOtp(otpRequestDto, "TEST0000001");

	}

	@Ignore
	@Test(expected = IdAuthenticationBusinessException.class)
	public void testGenerateOTP_WhenOtpIsFlooded_ThrowException() throws IdAuthenticationBusinessException {
		Mockito.when(autntxnrepository.countRequestDTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(5);
		ReflectionTestUtils.invokeMethod(otpServiceImpl, "isOtpFlooded", otpRequestDto);
		Mockito.when(otpServiceImplmock.generateOtp(Mockito.any(), Mockito.any()))
				.thenThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_REQUEST_FLOODED));
	}

	@Ignore
	@Test(expected = IdAuthenticationBusinessException.class)
	public void testGenerateOTP_WhenOTPIsNull_ThrowException() throws IdAuthenticationBusinessException {
		String txnID = otpRequestDto.getTransactionID();
		String productid = "IDA";
		String uin = "8765";
		String otp = null;

		String otpKey = OTPUtil.generateKey(productid, uin, txnID, "TEST0000001");
		Mockito.when(idInfoHelper.getUTCTime(Mockito.anyString())).thenReturn("2019-02-18T12:28:17.078");
		Mockito.when(otpManager.generateOTP(otpKey)).thenReturn(otp);
		Mockito.when(otpServiceImpl.generateOtp(Mockito.any(), Mockito.any()))
				.thenThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED));
	}

	@Test
	public void testAddMinute() {
		otpRequestDto.getRequestTime();
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestPhoneNoorEmailNotRegistered() throws IdAuthenticationBusinessException {
		Mockito.when(autntxnrepository.countRequestDTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(10);
		otpServiceImpl.generateOtp(getOtpRequestDTO(), "TEST0000001");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestOtpFlooded() throws IdAuthenticationBusinessException {
		Mockito.when(idInfoHelper.getEntityInfoAsString(Mockito.any(), Mockito.any())).thenReturn("1234567890");
		Mockito.when(autntxnrepository.countRequestDTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(10);
		otpServiceImpl.generateOtp(getOtpRequestDTO(), "TEST0000001");
	}

	@Ignore
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestParseExceptioncreateAuthTxn() throws Throwable {
		Mockito.when(idInfoHelper.getUTCTime(Mockito.any())).thenReturn("2019-02-18T18:17:48.923+05:30");
		try {
			ReflectionTestUtils.invokeMethod(otpServiceImpl, "createAuthTxn", "", "", "", "2019-02T18:17:48.923+05:30",
					"", "", "", RequestType.OTP_REQUEST);
		} catch (ParseException | DateTimeParseException e) {
			throw e.getCause();
		}
	}

	@Test
	public void TestOtpFloodedException() throws Throwable {
		try {
			ReflectionTestUtils.invokeMethod(otpServiceImpl, "isOtpFlooded", otpRequestDto);
		} catch (Exception e) {
			throw e.getCause();
		}
	}
}
