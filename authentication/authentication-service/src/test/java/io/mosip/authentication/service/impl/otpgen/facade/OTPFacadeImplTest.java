package io.mosip.authentication.service.impl.otpgen.facade;

import static org.junit.Assert.assertEquals;

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
import io.mosip.authentication.core.dto.indauth.IdentityValue;
import io.mosip.authentication.core.dto.otpgen.OtpRequestDTO;
import io.mosip.authentication.core.dto.otpgen.OtpResponseDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.spi.id.service.IdInfoService;
import io.mosip.authentication.core.spi.otpgen.service.OTPService;
import io.mosip.authentication.core.util.OTPUtil;
import io.mosip.authentication.service.entity.AutnTxn;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.DateHelper;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoEntity;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoHelper;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.integration.IdTemplateManager;
import io.mosip.authentication.service.integration.NotificationManager;
import io.mosip.authentication.service.repository.AutnTxnRepository;
import io.mosip.authentication.service.repository.DemoRepository;
import io.mosip.kernel.core.templatemanager.spi.TemplateManagerBuilder;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;

/**
 * Test class for OTPFacadeImpl. Mockito with PowerMockito.
 *
 * @author Rakesh Roshan
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, IdTemplateManager.class, TemplateManagerBuilderImpl.class })
public class OTPFacadeImplTest {

	OtpRequestDTO otpRequestDto;

	@InjectMocks
	DateHelper dateHelper;
	@Mock
	OtpResponseDTO otpResponseDTO;
	@Mock
	OTPService otpService;
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
	
	@Autowired
	private TemplateManagerBuilder templateManagerBuilder;
	
	@InjectMocks
	private RestRequestFactory restRequestFactory;
	@InjectMocks
	private RestHelper restHelper;

	@Mock
	IdInfoService idInfoService;
	@Mock
	private DemoHelper demoHelper;

	@Mock
	DemoRepository demoRepository;

	@InjectMocks
	NotificationManager notificationManager;

	@InjectMocks
	OTPFacadeImpl otpFacadeImpl;

	@Before
	public void before() {
		otpRequestDto = getOtpRequestDTO();
		otpResponseDTO = getOtpResponseDTO();

		ReflectionTestUtils.setField(otpFacadeImpl, "env", env);
		ReflectionTestUtils.setField(dateHelper, "env", env);
		ReflectionTestUtils.setField(otpFacadeImpl, "dateHelper", dateHelper);
		
		ReflectionTestUtils.setField(idTemplateManager, "templateManagerBuilder", templateManagerBuilder);
		ReflectionTestUtils.setField(idTemplateManager, "templateManager", templateManagerBuilder.enableCache(false).build());
	}

	@Test
	public void test_GenerateOTP() throws IdAuthenticationBusinessException, IdAuthenticationDaoException {

		String date = null;
		String time = null;
		String language = "fr";
		String mobileNumber = "7697698650";
		String emailId = "abc@abc.com";
		String name = "mosip";

		DemoEntity demoEntity = new DemoEntity();
		demoEntity.setEmail(emailId);
		demoEntity.setMobile(mobileNumber);
		Mockito.when(demoRepository.findById(Mockito.anyString())).thenReturn(Optional.of(demoEntity));
		String unqueId = otpRequestDto.getIdvId();
		String txnID = otpRequestDto.getTxnID();
		String productid = "IDA";
		String refId = "8765";
		String otp = "987654";
		Mockito.when(idAuthService.validateUIN(unqueId)).thenReturn(refId);
		String otpKey = OTPUtil.generateKey(productid, refId, txnID, otpRequestDto.getMuaCode());
		Mockito.when(otpService.generateOtp(otpKey)).thenReturn(otp);

		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO(language, name));
		list.add(new IdentityInfoDTO(language, emailId));
		list.add(new IdentityInfoDTO(language, mobileNumber));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);

		IdentityValue identityValue = new IdentityValue(language, name);
		Mockito.when(idInfoService.getIdInfo(refId)).thenReturn(idInfo);
		Mockito.when(demoHelper.getEntityInfo(DemoMatchType.NAME_PRI, idInfo)).thenReturn(identityValue);
		identityValue.setLanguage(language);
		identityValue.setValue(emailId);
		Mockito.when(demoHelper.getEntityInfo(DemoMatchType.EMAIL, idInfo)).thenReturn(identityValue);
		identityValue.setLanguage(language);
		identityValue.setValue(mobileNumber);
		Mockito.when(demoHelper.getEntityInfo(DemoMatchType.PHONE, idInfo)).thenReturn(identityValue);

		Optional<String> uinOpt = Optional.of("426789089018");
		Mockito.when(idAuthService.getUIN(refId)).thenReturn(uinOpt);

		ReflectionTestUtils.setField(notificationManager, "environment", env);
		ReflectionTestUtils.setField(notificationManager, "idTemplateManager", idTemplateManager);
		ReflectionTestUtils.setField(notificationManager, "restRequestFactory", restRequestFactory);
		ReflectionTestUtils.setField(restRequestFactory, "env", env);
		ReflectionTestUtils.setField(notificationManager, "restHelper", restHelper);
		ReflectionTestUtils.setField(otpFacadeImpl, "notificationManager", notificationManager);

		ReflectionTestUtils.setField(dateHelper, "env", env);
		ReflectionTestUtils.setField(otpFacadeImpl, "dateHelper", dateHelper);

		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "getEmail", refId);
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "getMobileNumber", refId);

		String[] dateAndTime = ReflectionTestUtils.invokeMethod(otpFacadeImpl, "getDateAndTime",
				otpRequestDto.getReqTime());
		date = dateAndTime[0];
		time = dateAndTime[1];

		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "sendOtpNotification", otpRequestDto, otp, refId, date, time,
				emailId, mobileNumber);

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
		String refId = "8765";
		String otp = null;

		Mockito.when(idAuthService.validateUIN(unqueId)).thenReturn(refId);
		String otpKey = OTPUtil.generateKey(productid, refId, txnID, otpRequestDto.getMuaCode());
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
	public void testSaveAutnTxn() {
		String refId = "8765";
		String status = "Y";
		String comment = "OTP_GENERATED";
		ReflectionTestUtils.invokeMethod(autntxnrepository, "saveAndFlush", autnTxn);
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "saveAutnTxn", otpRequestDto, status, comment, refId);
	}

	@Test
	public void testGetRefIdForUIN() {
		String uniqueID = otpRequestDto.getIdvId();
		String actualrefid = ReflectionTestUtils.invokeMethod(idAuthService, "validateUIN", uniqueID);
		String expactedRefId = ReflectionTestUtils.invokeMethod(otpFacadeImpl, "getRefId", otpRequestDto);
		assertEquals(actualrefid, expactedRefId);
	}

	@Test
	public void test_WhenInvalidID_ForUIN_RefIdIsNull() throws IdAuthenticationBusinessException {
		otpRequestDto.setIdvId("cvcvcjhg76");
		String uniqueID = otpRequestDto.getIdvId();
		ReflectionTestUtils.invokeMethod(idAuthService, "validateUIN", uniqueID);
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "getRefId", otpRequestDto);
	}

	@Test
	public void testGetRefIdForVID() {
		String uniqueID = otpRequestDto.getIdvId();
		otpRequestDto.setIdvIdType(IdType.VID.getType());
		String actualrefid = ReflectionTestUtils.invokeMethod(idAuthService, "validateVID", uniqueID);
		String expactedRefId = ReflectionTestUtils.invokeMethod(otpFacadeImpl, "getRefId", otpRequestDto);

		assertEquals(actualrefid, expactedRefId);
	}

	@Test
	public void test_WhenInvalidID_ForVID_RefIdIsNull() throws IdAuthenticationBusinessException {
		otpRequestDto.setIdvId("cvcvcjhg76");
		otpRequestDto.setIdvIdType(IdType.VID.getType());
		String uniqueID = otpRequestDto.getIdvId();
		ReflectionTestUtils.invokeMethod(idAuthService, "validateVID", uniqueID);
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "getRefId", otpRequestDto);
	}

	@Test
	public void testSendOtpNotification() throws IdAuthenticationBusinessException, IdAuthenticationDaoException {

		otpRequestDto.setIdvId("8765");
		String otp = "987654";
		String refId = "8765";
		String date = "";
		String time = "";
		String email = "abc@abc.com";
		String mobileNumber = "968759687";

		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);

		IdentityValue identityValue = new IdentityValue("en", "mosip");
		Mockito.when(idInfoService.getIdInfo(refId)).thenReturn(idInfo);
		Mockito.when(demoHelper.getEntityInfo(DemoMatchType.NAME_PRI, idInfo)).thenReturn(identityValue);

		Mockito.when(idInfoService.getIdInfo(refId)).thenReturn(idInfo);
		Optional<String> uinOpt = Optional.of("426789089018");
		Mockito.when(idAuthService.getUIN(refId)).thenReturn(uinOpt);

		String[] dateAndTime = ReflectionTestUtils.invokeMethod(otpFacadeImpl, "getDateAndTime",
				otpRequestDto.getReqTime());
		date = dateAndTime[0];
		time = dateAndTime[1];

		ReflectionTestUtils.setField(notificationManager, "environment", env);
		ReflectionTestUtils.setField(notificationManager, "idTemplateManager", idTemplateManager);
		ReflectionTestUtils.setField(notificationManager, "restRequestFactory", restRequestFactory);
		ReflectionTestUtils.setField(restRequestFactory, "env", env);
		ReflectionTestUtils.setField(notificationManager, "restHelper", restHelper);

		ReflectionTestUtils.setField(otpFacadeImpl, "notificationManager", notificationManager);
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "sendOtpNotification", otpRequestDto, otp, refId, date, time,
				email, mobileNumber);
	}

	@Test
	public void testEmail() throws IdAuthenticationDaoException {
		String refId = "8765";
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		String language = "fr";
		String eamilId = "abc@abc.com";
		list.add(new IdentityInfoDTO(language, eamilId));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("email", list);

		IdentityValue identityValue = new IdentityValue(language, eamilId);
		Mockito.when(idInfoService.getIdInfo(refId)).thenReturn(idInfo);
		Mockito.when(demoHelper.getEntityInfo(DemoMatchType.EMAIL, idInfo)).thenReturn(identityValue);
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "getEmail", refId);
	}

	@Test
	public void testMobileNumber() throws IdAuthenticationDaoException {
		String refId = "8765";
		String language = "fr";
		String eamilId = "abc@abc.com";
		String mobileNumber = "7687687";
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO(language, eamilId));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("phone", list);

		IdentityValue identityValue = new IdentityValue(language, mobileNumber);
		Mockito.when(idInfoService.getIdInfo(refId)).thenReturn(idInfo);
		Mockito.when(demoHelper.getEntityInfo(DemoMatchType.PHONE, idInfo)).thenReturn(identityValue);
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "getMobileNumber", refId);
	}

	@Test
	public void testGetDateAndTime() {
		String reqquestTime = otpRequestDto.getReqTime();

		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "getDateAndTime", reqquestTime);
	}

	// =========================================================
	// ************ Helping Method *****************************
	// =========================================================

	private OtpRequestDTO getOtpRequestDTO() {
		OtpRequestDTO otpRequestDto = new OtpRequestDTO();
		otpRequestDto.setId("id");
		otpRequestDto.setMuaCode("2345678901234");
		otpRequestDto.setIdvIdType(IdType.UIN.getType());
		otpRequestDto.setReqTime(new SimpleDateFormat(env.getProperty("datetime.pattern")).format(new Date()));
		otpRequestDto.setTxnID("2345678901234");
		otpRequestDto.setIdvId("2345678901234");
		otpRequestDto.setVer("1.0");

		return otpRequestDto;
	}

	private OtpResponseDTO getOtpResponseDTO() {
		OtpResponseDTO otpResponseDTO = new OtpResponseDTO();
		otpResponseDTO.setStatus("OTP_GENERATED");
		otpResponseDTO.setResTime(new SimpleDateFormat(env.getProperty("datetime.pattern")).format(new Date()));

		return otpResponseDTO;
	}
}
