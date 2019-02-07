package io.mosip.authentication.service.impl.otpgen.facade;

import static org.junit.Assert.assertNotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.otpgen.OtpRequestDTO;
import io.mosip.authentication.core.dto.otpgen.OtpResponseDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.spi.id.service.IdRepoService;
import io.mosip.authentication.core.util.OTPUtil;
import io.mosip.authentication.service.config.IDAMappingConfig;
import io.mosip.authentication.service.entity.AutnTxn;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.DateHelper;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.impl.notification.service.NotificationServiceImpl;
import io.mosip.authentication.service.impl.otpgen.service.OTPServiceImpl;
import io.mosip.authentication.service.integration.IdTemplateManager;
import io.mosip.authentication.service.integration.OTPManager;
import io.mosip.authentication.service.repository.AutnTxnRepository;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;

/**
 * Test class for OTPFacadeImpl. Mockito with PowerMockito.
 *
 * @author Rakesh Roshan
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, IdTemplateManager.class,
		TemplateManagerBuilderImpl.class })
public class OTPFacadeImplTest {

	OtpRequestDTO otpRequestDto;

	@InjectMocks
	DateHelper dateHelper;
	@Mock
	OtpResponseDTO otpResponseDTO;
	@Mock
	OTPServiceImpl otpService;
	@Autowired
	Environment env;
	@Mock
	AutnTxnRepository autntxnrepository;
	@Mock
	Date date;
	@Mock
	AutnTxn autnTxn;
	@Mock
	IdAuthService idAuthService;
	@InjectMocks
	IdTemplateManager idTemplateManager;

	@InjectMocks
	private RestRequestFactory restRequestFactory;
	@InjectMocks
	private RestHelper restHelper;

	@Mock
	IdRepoService idInfoService;
	@Mock
	private IdInfoHelper demoHelper;

	@Mock
	NotificationServiceImpl notificationService;

	@InjectMocks
	OTPFacadeImpl otpFacadeImpl;
	@Mock
	OTPManager otpManager;
	@Mock
	private IDAMappingConfig idMappingConfig;

	@Before
	public void before() {
		otpRequestDto = getOtpRequestDTO();
		otpResponseDTO = getOtpResponseDTO();

		ReflectionTestUtils.setField(otpFacadeImpl, "env", env);
		ReflectionTestUtils.setField(dateHelper, "env", env);
		ReflectionTestUtils.setField(otpFacadeImpl, "dateHelper", dateHelper);
		ReflectionTestUtils.setField(otpFacadeImpl, "demoHelper", demoHelper);
		ReflectionTestUtils.setField(notificationService, "env", env);
		ReflectionTestUtils.setField(notificationService, "idTemplateManager", idTemplateManager);
		ReflectionTestUtils.setField(restRequestFactory, "env", env);
		ReflectionTestUtils.setField(demoHelper, "environment", env);
		ReflectionTestUtils.setField(demoHelper, "idMappingConfig", idMappingConfig);
		ReflectionTestUtils.setField(otpFacadeImpl, "notificationService", notificationService);
		ReflectionTestUtils.setField(otpFacadeImpl, "otpService", otpService);
		// ReflectionTestUtils.setField(otpService, "otpManager", otpManager);
		// ReflectionTestUtils.setField(otpManager, "restRequestFactory",
		// restRequestFactory);
		// ReflectionTestUtils.setField(otpManager, "restHelper", restHelper);

	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void test_GenerateOTP() throws IdAuthenticationBusinessException, IdAuthenticationDaoException {

		String date = null;
		String time = null;
		String language = "fre";
		String mobileNumber = "7697698650";
		String emailId = "abc@abc.com";
		String name = "mosip";

		String unqueId = otpRequestDto.getIdvId();
		String txnID = otpRequestDto.getTxnID();
		String productid = "IDA";
		String uin = "8765";
		String otp = "987654";
		Mockito.when(idAuthService.getIdRepoByUIN(unqueId, false)).thenReturn(repoDetails());
		String otpKey = OTPUtil.generateKey(productid, uin, txnID, otpRequestDto.getTspID());
		Mockito.when(otpService.generateOtp(otpKey)).thenReturn(otp);

		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO(language, name));
		list.add(new IdentityInfoDTO(language, emailId));
		list.add(new IdentityInfoDTO(language, mobileNumber));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);

		Mockito.when(idInfoService.getIdInfo(repoDetails())).thenReturn(idInfo);
		Mockito.when(demoHelper.getEntityInfoAsString(DemoMatchType.NAME, idInfo)).thenReturn(name);

		Mockito.when(demoHelper.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo)).thenReturn(emailId);

		Mockito.when(demoHelper.getEntityInfoAsString(DemoMatchType.PHONE, idInfo)).thenReturn(mobileNumber);

		Optional<String> uinOpt = Optional.of("426789089018");

		ReflectionTestUtils.setField(dateHelper, "env", env);
		ReflectionTestUtils.setField(otpFacadeImpl, "dateHelper", dateHelper);

		otpFacadeImpl.generateOtp(otpRequestDto);
	}

	@Test
	public void testGenerateOTPSuccess() throws IdAuthenticationBusinessException, IdAuthenticationDaoException {

		String date = null;
		String time = null;
		String language = "fre";
		String mobileNumber = "7697698650";
		String emailId = "abc@abc.com";
		String name = "mosip";

		String uin = otpRequestDto.getIdvId();
		String txnID = otpRequestDto.getTxnID();
		String productid = "IDA";
		String otp = "987654";
		Map<String, Object> repoDetails = repoDetails();
		repoDetails.put("uin", uin);
		Mockito.when(idAuthService.getIdRepoByUIN(uin, false)).thenReturn(repoDetails);
		String otpKey = OTPUtil.generateKey(productid, uin, txnID, otpRequestDto.getTspID());
		Mockito.when(otpService.generateOtp(otpKey)).thenReturn(otp);

		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO(language, name));
		list.add(new IdentityInfoDTO(language, emailId));
		list.add(new IdentityInfoDTO(language, mobileNumber));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);

		Mockito.when(idAuthService.processIdType("D", uin, false)).thenReturn(repoDetails);
		Mockito.when(idInfoService.getIdInfo(repoDetails)).thenReturn(idInfo);
		Mockito.when(demoHelper.getEntityInfoAsString(DemoMatchType.NAME, idInfo)).thenReturn(name);

		Mockito.when(demoHelper.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo)).thenReturn(emailId);

		Mockito.when(demoHelper.getEntityInfoAsString(DemoMatchType.PHONE, idInfo)).thenReturn(mobileNumber);

		Optional<String> uinOpt = Optional.of("426789089018");

		ReflectionTestUtils.setField(dateHelper, "env", env);
		ReflectionTestUtils.setField(otpFacadeImpl, "dateHelper", dateHelper);

		otpFacadeImpl.generateOtp(otpRequestDto);
	}

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
		Mockito.when(idAuthService.processIdType(otpRequestDto.getIdvIdType(), otpRequestDto.getIdvId(), false))
				.thenReturn(idRepo);
		String otpKey = "IDA_MTIzNDU2Nzg5MA==_2345678901234_2345678901234";

		Mockito.when(otpManager.generateOTP(otpKey)).thenReturn("123456");
		Mockito.when(otpService.generateOtp(otpKey)).thenReturn("123456");
		Mockito.when(idInfoService.getIdInfo(repoDetails())).thenReturn(idInfo);
		Mockito.when(demoHelper.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo)).thenReturn("abc@test.com");
		Mockito.when(demoHelper.getEntityInfoAsString(DemoMatchType.PHONE, idInfo)).thenReturn("1234567890");

		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "getEmail", idInfo);
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "getMobileNumber", idInfo);
		otpFacadeImpl.generateOtp(otpRequestDto);

	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void testGenerateOTP_WhenOtpIsFlooded_ThrowException() throws IdAuthenticationBusinessException {
		Mockito.when(autntxnrepository.countRequestDTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(5);
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "isOtpFlooded", otpRequestDto);
		Mockito.when(otpFacadeImpl.generateOtp(otpRequestDto))
				.thenThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_REQUEST_FLOODED));
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void testGenerateOTP_WhenOTPIsNull_ThrowException() throws IdAuthenticationBusinessException {
		String unqueId = otpRequestDto.getIdvId();
		String txnID = otpRequestDto.getTxnID();
		String productid = "IDA";
		String uin = "8765";
		String otp = null;

		Mockito.when(idAuthService.getIdRepoByUIN(unqueId, false)).thenReturn(repoDetails());
		String otpKey = OTPUtil.generateKey(productid, uin, txnID, otpRequestDto.getTspID());
		Mockito.when(otpService.generateOtp(otpKey)).thenReturn(otp);
		Mockito.when(otpFacadeImpl.generateOtp(otpRequestDto))
				.thenThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED));
	}

	@Test
	public void testIsOtpFlooded_False() throws IDDataValidationException {
		String uniqueID = otpRequestDto.getIdvId();
		Date requestTime = dateHelper.convertStringToDate(otpRequestDto.getReqTime());
		Date addMinutesInOtpRequestDTime = new Date();

		ReflectionTestUtils.setField(otpFacadeImpl, "autntxnrepository", autntxnrepository);
		ReflectionTestUtils.invokeMethod(autntxnrepository, "countRequestDTime", requestTime,
				addMinutesInOtpRequestDTime, uniqueID);
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "isOtpFlooded", otpRequestDto);
	}

	@Test
	public void testAddMinute() {
		otpRequestDto.getReqTime();
	}

	@Test
	public void testGetRefIdForUIN() {
		String uniqueID = otpRequestDto.getIdvId();
		Object invokeMethod = ReflectionTestUtils.invokeMethod(idAuthService, "getIdRepoByUinNumber", uniqueID, false);
		assertNotNull(invokeMethod);
	}

	@Test
	public void test_WhenInvalidID_ForUIN_RefIdIsNull() throws IdAuthenticationBusinessException {
		otpRequestDto.setIdvId("cvcvcjhg76");
		String uniqueID = otpRequestDto.getIdvId();
		ReflectionTestUtils.invokeMethod(idAuthService, "getIdRepoByUinNumber", uniqueID, false);
	}

	@Test
	public void testGetRefIdForVID() {
		String uniqueID = otpRequestDto.getIdvId();
		otpRequestDto.setIdvIdType(IdType.VID.getType());
		Object invokeMethod = ReflectionTestUtils.invokeMethod(idAuthService, "getIdRepoByVidNumber", uniqueID, false);

		assertNotNull(invokeMethod);
	}

	@Test
	public void test_WhenInvalidID_ForVID_RefIdIsNull() throws IdAuthenticationBusinessException {
		otpRequestDto.setIdvId("cvcvcjhg76");
		otpRequestDto.setIdvIdType(IdType.VID.getType());
		String uniqueID = otpRequestDto.getIdvId();
		ReflectionTestUtils.invokeMethod(idAuthService, "getIdRepoByVidNumber", uniqueID, false);
	}

	@Test
	public void testGetDateAndTime() {
		String reqquestTime = otpRequestDto.getReqTime();
		DateHelper.getDateAndTime(reqquestTime, env.getProperty("datetime.pattern"));
	}

	// =========================================================
	// ************ Helping Method *****************************
	// =========================================================

	private OtpRequestDTO getOtpRequestDTO() {
		OtpRequestDTO otpRequestDto = new OtpRequestDTO();
		otpRequestDto.setId("id");
		otpRequestDto.setTspID("2345678901234");
		otpRequestDto.setIdvIdType(IdType.UIN.getType());
		otpRequestDto.setReqTime(new SimpleDateFormat(env.getProperty("datetime.pattern")).format(new Date()));
		otpRequestDto.setTxnID("2345678901234");
		otpRequestDto.setIdvId("2345678901234");
		// otpRequestDto.setVer("1.0");

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
}
