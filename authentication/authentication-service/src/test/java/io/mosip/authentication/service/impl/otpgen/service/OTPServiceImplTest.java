package io.mosip.authentication.service.impl.otpgen.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.otpgen.OtpIdentityDTO;
import io.mosip.authentication.core.dto.otpgen.OtpRequestDTO;
import io.mosip.authentication.core.dto.otpgen.OtpResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.spi.id.service.IdRepoService;
import io.mosip.authentication.core.util.OTPUtil;
import io.mosip.authentication.service.entity.AutnTxn;
import io.mosip.authentication.service.factory.AuditRequestFactory;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.impl.notification.service.NotificationServiceImpl;
import io.mosip.authentication.service.integration.IdTemplateManager;
import io.mosip.authentication.service.integration.OTPManager;
import io.mosip.authentication.service.repository.AutnTxnRepository;

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
		ReflectionTestUtils.setField(restRequestFactory, "env", env);
		ReflectionTestUtils.setField(idInfoHelper, "environment", env);
		ReflectionTestUtils.setField(otpServiceImpl, "notificationService", notificationService);
		ReflectionTestUtils.setField(otpServiceImpl, "idAuthService", idAuthService);
		ReflectionTestUtils.setField(otpServiceImpl, "idInfoService", idInfoService);
		ReflectionTestUtils.setField(otpServiceImpl, "otpManager", otpManager);
		// ReflectionTestUtils.setField(otpService, "otpManager", otpManager);
		// ReflectionTestUtils.setField(otpManager, "restRequestFactory",
		// restRequestFactory);
		// ReflectionTestUtils.setField(otpManager, "restHelper", restHelper);

	}

	private OtpRequestDTO getOtpRequestDTO() {
		OtpRequestDTO otpRequestDto = new OtpRequestDTO();
		otpRequestDto.setId("id");
		otpRequestDto.setPartnerID("2345678901234");
		otpRequestDto.setRequestTime(new SimpleDateFormat(env.getProperty("datetime.pattern")).format(new Date()));
		otpRequestDto.setTransactionID("2345678901234");
		OtpIdentityDTO identityDTO = new OtpIdentityDTO();
		identityDTO.setUin("2345678901234");
//		otpRequestDto.setIdentity(identityDTO);
//		otpRequestDto.setIdvId("2345678901234");
		otpRequestDto.setRequestTime("2019-02-18T18:17:48.923+05:30");

		return otpRequestDto;
	}

	private OtpResponseDTO getOtpResponseDTO() {
		OtpResponseDTO otpResponseDTO = new OtpResponseDTO();
		otpResponseDTO.setStatus("OTP_GENERATED");
		otpResponseDTO.setResTime(new SimpleDateFormat(env.getProperty("datetime.pattern")).format(new Date()));

		return otpResponseDTO;
	}

	private Map<String, Object> repoDetails() {
		Map<String, Object> map = new HashMap<>();
		map.put("registrationId", "863537");
		return map;
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void test_GenerateOTP() throws IdAuthenticationBusinessException, IdAuthenticationDaoException {
		getOtpRequestDTO();
		String date = null;
		String time = null;
		String language = "fre";
		String mobileNumber = "7697698650";
		String emailId = "abc@abc.com";
		String name = "mosip";
//		String unqueId = otpRequestDto.getIdentity().getUin();
		String txnID = otpRequestDto.getTransactionID();
		String productid = "IDA";
		String uin = "8765";
		String otp = "987654";
//		Mockito.when(idAuthService.getIdRepoByUIN(unqueId, false)).thenReturn(repoDetails());
		String otpKey = OTPUtil.generateKey(productid, uin, txnID, otpRequestDto.getPartnerID());
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
		try {
			Mockito.when(idInfoHelper.getUTCTime(Mockito.anyString())).thenReturn("2019-02-18T12:28:17.078");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Optional<String> uinOpt = Optional.of("426789089018");
		otpServiceImpl.generateOtp(otpRequestDto);
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
//		String uin = otpRequestDto.getIdentity().getUin();
		String txnID = otpRequestDto.getTransactionID();
		String productid = "IDA";
		String otp = "987654";
		Map<String, Object> repoDetails = repoDetails();
//		repoDetails.put("uin", uin);
		repoDetails.put("mobileNumber", "7697698650");
		repoDetails.put("emailId", "abc@abc.com");
//		Mockito.when(idAuthService.getIdRepoByUIN(uin, false)).thenReturn(repoDetails);
//		String otpKey = OTPUtil.generateKey(productid, uin, txnID, otpRequestDto.getPartnerID());
//		Mockito.when(otpManager.generateOTP(otpKey)).thenReturn(otp);

		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO(language, name));
		list.add(new IdentityInfoDTO(language, emailId));
		list.add(new IdentityInfoDTO(language, mobileNumber));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);

//		Mockito.when(idAuthService.processIdType("D", uin, false)).thenReturn(repoDetails);
		Mockito.when(idAuthService.getIdInfo(repoDetails)).thenReturn(idInfo);
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.NAME, idInfo)).thenReturn(name);

		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo)).thenReturn(emailId);

		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.PHONE, idInfo)).thenReturn(mobileNumber);
		try {
			Mockito.when(idInfoHelper.getUTCTime(Mockito.anyString())).thenReturn("2019-02-18T12:28:17.078");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Optional<String> uinOpt = Optional.of("426789089018");

		otpServiceImpl.generateOtp(otpRequestDto);
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
		Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), false)).thenReturn(idRepo);
		String otpKey = "IDA_MTIzNDU2Nzg5MA==_2345678901234_2345678901234";

		Mockito.when(otpManager.generateOTP(otpKey)).thenReturn("123456");
		Mockito.when(otpManager.generateOTP(otpKey)).thenReturn("123456");
		Mockito.when(idAuthService.getIdInfo(repoDetails())).thenReturn(idInfo);
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo)).thenReturn("abc@test.com");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.PHONE, idInfo)).thenReturn("1234567890");

		ReflectionTestUtils.invokeMethod(otpServiceImpl, "getEmail", idInfo);
		ReflectionTestUtils.invokeMethod(otpServiceImpl, "getMobileNumber", idInfo);
		try {
			Mockito.when(idInfoHelper.getUTCTime(Mockito.anyString())).thenReturn("2019-02-18T12:28:17.078");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		otpServiceImpl.generateOtp(otpRequestDto);

	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void testGenerateOTP_WhenOtpIsFlooded_ThrowException() throws IdAuthenticationBusinessException {
		Mockito.when(autntxnrepository.countRequestDTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(5);
		ReflectionTestUtils.invokeMethod(otpServiceImpl, "isOtpFlooded", otpRequestDto);
		Mockito.when(otpServiceImpl.generateOtp(otpRequestDto))
				.thenThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_REQUEST_FLOODED));
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void testGenerateOTP_WhenOTPIsNull_ThrowException() throws IdAuthenticationBusinessException {
//		String unqueId = otpRequestDto.getIdentity().getUin();
		String txnID = otpRequestDto.getTransactionID();
		String productid = "IDA";
		String uin = "8765";
		String otp = null;

//		Mockito.when(idAuthService.getIdRepoByUIN(unqueId, false)).thenReturn(repoDetails());
		String otpKey = OTPUtil.generateKey(productid, uin, txnID, otpRequestDto.getPartnerID());
		try {
			Mockito.when(idInfoHelper.getUTCTime(Mockito.anyString())).thenReturn("2019-02-18T12:28:17.078");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Mockito.when(otpManager.generateOTP(otpKey)).thenReturn(otp);
		Mockito.when(otpServiceImpl.generateOtp(otpRequestDto))
				.thenThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED));
	}

	@Test
	public void testAddMinute() {
		otpRequestDto.getRequestTime();
	}

	@Test
	public void testGetRefIdForUIN() {
//		String uniqueID = otpRequestDto.getIdentity().getUin();
//		Object invokeMethod = ReflectionTestUtils.invokeMethod(idAuthService, "getIdRepoByUIN", uniqueID, false);
//		assertNotNull(invokeMethod);
	}

	@Test
	public void test_WhenInvalidID_ForUIN_RefIdIsNull() throws IdAuthenticationBusinessException {
//		otpRequestDto.getIdentity().setUin("cvcvcjhg76");
//		String uniqueID = otpRequestDto.getIdentity().getUin();
//		ReflectionTestUtils.invokeMethod(idAuthService, "getIdRepoByUIN", uniqueID, false);
	}

	@Test
	public void testGetRefIdForVID() {
//		String uniqueID = otpRequestDto.getIdentity().getUin();
//		Object invokeMethod = ReflectionTestUtils.invokeMethod(idAuthService, "getIdRepoByVID", uniqueID, false);
//		assertNotNull(invokeMethod);
	}

	@Test
	public void test_WhenInvalidID_ForVID_RefIdIsNull() throws IdAuthenticationBusinessException {
//		otpRequestDto.getIdentity().setUin("cvcvcjhg76");
//		String uniqueID = otpRequestDto.getIdentity().getUin();
//		String uniqueID = otpRequestDto.getIdvId();
//		ReflectionTestUtils.invokeMethod(idAuthService, "getIdRepoByVID", uniqueID, false);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestPhoneNoorEmailNotRegistered() throws IdAuthenticationBusinessException {
		Mockito.when(autntxnrepository.countRequestDTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(10);
		otpServiceImpl.generateOtp(getOtpRequestDTO());
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestOtpFlooded() throws IdAuthenticationBusinessException {
		Mockito.when(idInfoHelper.getEntityInfoAsString(Mockito.any(), Mockito.any())).thenReturn("1234567890");
		Mockito.when(autntxnrepository.countRequestDTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(10);
		otpServiceImpl.generateOtp(getOtpRequestDTO());
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestParseExceptioncreateAuthTxn() throws Throwable {
		Mockito.when(idInfoHelper.getUTCTime(Mockito.any())).thenReturn("2019-02-18T18:17:48.923+05:30");

		try {
			ReflectionTestUtils.invokeMethod(otpServiceImpl, "createAuthTxn", "", "", "",
					"2019-02-18T18:17:48.923+05:30", "", "", "", RequestType.OTP_REQUEST);
		} catch (Exception e) {
			throw e.getCause();
		}
	}

	@Test
	public void TestOtpFloodedException() throws Throwable {
//		OtpRequestDTO otpRequestDto = new OtpRequestDTO();
//		otpRequestDto.setReqTime("2019-02-18T18:17:48.923+05:30");
		try {
			ReflectionTestUtils.invokeMethod(otpServiceImpl, "isOtpFlooded", otpRequestDto);
		} catch (Exception e) {
			throw e.getCause();
		}
	}
}
