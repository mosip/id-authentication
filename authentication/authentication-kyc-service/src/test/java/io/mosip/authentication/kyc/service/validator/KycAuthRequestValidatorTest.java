package io.mosip.authentication.kyc.service.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.match.DemoMatchType;
import io.mosip.authentication.common.service.integration.MasterDataManager;
import io.mosip.authentication.common.service.validator.AuthRequestValidator;
import io.mosip.authentication.core.indauth.dto.AuthTypeDTO;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.IdentityDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.mosip.kernel.idvalidator.vid.impl.VidValidatorImpl;
import io.mosip.kernel.logger.logback.appender.RollingFileAppender;
import io.mosip.kernel.pinvalidator.impl.PinValidatorImpl;

/**
 * 
 * @author Prem Kumar
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class KycAuthRequestValidatorTest {

	private static final String MOSIP_SECONDARY_LANG_CODE = "mosip.secondary-language";

	@Mock
	private SpringValidatorAdapter validator;

	@Mock
	Errors errors;

	@InjectMocks
	RollingFileAppender appender;

	@InjectMocks
	KycAuthRequestValidator KycAuthRequestValidator;

	@Mock
	IdInfoHelper idInfoHelper;

	@InjectMocks
	AuthRequestValidator authRequestValidator;

	@Mock
	UinValidatorImpl uinValidator;

	@Mock
	VidValidatorImpl vidValidator;

	@Mock
	PinValidatorImpl pinValidator;

	@Autowired
	Environment env;

	@Mock
	private MasterDataManager masterDataManager;

	@Before
	public void before() {
		ReflectionTestUtils.setField(authRequestValidator, "env", env);
		ReflectionTestUtils.setField(KycAuthRequestValidator, "idInfoHelper", idInfoHelper);
		ReflectionTestUtils.setField(KycAuthRequestValidator, "env", env);
		ReflectionTestUtils.setField(idInfoHelper, "environment", env);
		ReflectionTestUtils.setField(authRequestValidator, "idInfoHelper", idInfoHelper);

	}

	@Test
	public void testSupportTrue() {
		assertTrue(KycAuthRequestValidator.supports(KycAuthRequestDTO.class));
	}

	@Test
	public void testSupportFalse() {
		assertFalse(KycAuthRequestValidator.supports(KycAuthRequestValidator.class));
	}

	@Test
	public void testValidateAuthRequest() {
		KycAuthRequestDTO kycAuthRequestDTO = new KycAuthRequestDTO();
		kycAuthRequestDTO.setConsentObtained(Boolean.TRUE);
		kycAuthRequestDTO.setSecondaryLangCode("fra");
		kycAuthRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setVersion("1.1");
		kycAuthRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(false);
		authTypeDTO.setOtp(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage("EN");
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage("fre");
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);

		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(env.getProperty(MOSIP_SECONDARY_LANG_CODE));
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		kycAuthRequestDTO.setIndividualIdType(IdType.UIN.getType());
		RequestDTO request = new RequestDTO();
		String otp = "123456";
		request.setOtp(otp);
		kycAuthRequestDTO.setIndividualId("5134256294");
		request.setDemographics(idDTO);
		request.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		kycAuthRequestDTO.setConsentObtained(true);
		kycAuthRequestDTO.setRequest(request);
		kycAuthRequestDTO.setRequestedAuth(authTypeDTO);
		kycAuthRequestDTO.setRequest(request);
		Errors errors = new BeanPropertyBindingResult(kycAuthRequestDTO, "kycAuthRequestDTO");
		Mockito.when(idInfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		KycAuthRequestValidator.validate(kycAuthRequestDTO, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void TestInvalidAuthRequest() {
		KycAuthRequestDTO kycAuthRequestDTO = new KycAuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(kycAuthRequestDTO, "kycAuthRequestDTO");
		Mockito.when(idInfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		KycAuthRequestValidator.validate(kycAuthRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInvalidAuthRequest() {
		KycAuthRequestDTO kycAuthRequestDTO = new KycAuthRequestDTO();
		kycAuthRequestDTO.setConsentObtained(Boolean.TRUE);
		kycAuthRequestDTO.setSecondaryLangCode("fra");
		kycAuthRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		kycAuthRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		kycAuthRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		authTypeDTO.setOtp(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage("EN");
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage("fre");
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);

		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(env.getProperty(MOSIP_SECONDARY_LANG_CODE));
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		kycAuthRequestDTO.setIndividualIdType("D");
		RequestDTO request = new RequestDTO();
		String otp = "123456";
		request.setOtp(otp);
		kycAuthRequestDTO.setIndividualId("5134256294");
		request.setDemographics(idDTO);
		kycAuthRequestDTO.setRequest(request);
		kycAuthRequestDTO.setRequestedAuth(authTypeDTO);
		kycAuthRequestDTO.setRequest(request);
		Mockito.when(idInfoHelper.isMatchtypeEnabled(DemoMatchType.NAME)).thenReturn(Boolean.TRUE);
		Errors errors = new BeanPropertyBindingResult(kycAuthRequestDTO, "baseAuthRequestDTO");
		KycAuthRequestValidator.validate(kycAuthRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void TestMUAPermissionisNotAvail() {
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.merge(((AbstractEnvironment) mockenv));
		mockenv.setProperty("ekyc.auth.types.allowed", "otp,bio,pin");
		ReflectionTestUtils.setField(KycAuthRequestValidator, "env", mockenv);
		KycAuthRequestDTO kycAuthRequestDTO = new KycAuthRequestDTO();

		kycAuthRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		kycAuthRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		kycAuthRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setOtp(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage("EN");
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage("fre");
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);

		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		RequestDTO request = new RequestDTO();
		String otp = "123456";
		request.setOtp(otp);
		kycAuthRequestDTO.setIndividualId("5134256294");
		request.setDemographics(idDTO);
		kycAuthRequestDTO.setRequest(request);
		kycAuthRequestDTO.setRequestedAuth(authTypeDTO);
		kycAuthRequestDTO.setRequest(request);
		Mockito.when(idInfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		Errors errors = new BeanPropertyBindingResult(kycAuthRequestDTO, "kycAuthRequestDTO");
		KycAuthRequestValidator.validate(kycAuthRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void TestInvalidAuthType() {
		KycAuthRequestDTO kycAuthRequestDTO = new KycAuthRequestDTO();
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setVersion("1.1");
		kycAuthRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		authTypeDTO.setOtp(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage("EN");
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage("fre");
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(env.getProperty(MOSIP_SECONDARY_LANG_CODE));
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		kycAuthRequestDTO.setIndividualIdType("D");
		kycAuthRequestDTO.setConsentObtained(Boolean.FALSE);
		kycAuthRequestDTO.setSecondaryLangCode("fra");
		RequestDTO reqDTO = new RequestDTO();
		String otp = "123456";
		reqDTO.setOtp(otp);
		kycAuthRequestDTO.setIndividualId("5134256294");
		reqDTO.setDemographics(idDTO);
		kycAuthRequestDTO.setRequestedAuth(authTypeDTO);
		kycAuthRequestDTO.setRequest(reqDTO);
		Mockito.when(idInfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		Errors errors = new BeanPropertyBindingResult(kycAuthRequestDTO, "kycAuthRequestDTO");
		KycAuthRequestValidator.validate(kycAuthRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void TestkycAuthRequestDtoisNull() {
		KycAuthRequestDTO kycAuthRequestDTO = new KycAuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(kycAuthRequestDTO, "kycAuthRequestDTO");
		kycAuthRequestDTO = null;
		KycAuthRequestValidator.validate(kycAuthRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void TestkycvalidateAuthType() {
		KycAuthRequestDTO kycAuthRequestDTO = new KycAuthRequestDTO();
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setVersion("1.1");
		kycAuthRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		authTypeDTO.setOtp(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage("EN");
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage("fre");
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(env.getProperty(MOSIP_SECONDARY_LANG_CODE));
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		kycAuthRequestDTO.setIndividualIdType("D");
		kycAuthRequestDTO.setConsentObtained(Boolean.TRUE);
		kycAuthRequestDTO.setSecondaryLangCode("fra");
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		kycAuthRequestDTO.setRequestedAuth(authTypeDTO);
		kycAuthRequestDTO.setRequest(reqDTO);
		Mockito.when(idInfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		Errors errors = new BeanPropertyBindingResult(kycAuthRequestDTO, "kycAuthRequestDTO");
		KycAuthRequestValidator.validate(kycAuthRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void TestkycAuthRequestisNull() {
		KycAuthRequestDTO kycAuthRequestDTO = new KycAuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(kycAuthRequestDTO, "kycAuthRequestDTO");
		kycAuthRequestDTO.setRequestedAuth(null);
		KycAuthRequestValidator.validate(kycAuthRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void TestInvalidConsentReq() {
		KycAuthRequestDTO kycAuthRequestDTO = new KycAuthRequestDTO();
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setVersion("1.1");
		kycAuthRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		authTypeDTO.setOtp(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage("EN");
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage("fre");
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(env.getProperty(MOSIP_SECONDARY_LANG_CODE));
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		kycAuthRequestDTO.setIndividualIdType("D");
		kycAuthRequestDTO.setConsentObtained(Boolean.FALSE);
		kycAuthRequestDTO.setSecondaryLangCode("fra");
		RequestDTO request = new RequestDTO();
		request.setDemographics(idDTO);
		kycAuthRequestDTO.setRequestedAuth(authTypeDTO);
		String otp = "456789";
		request.setOtp(otp);
		kycAuthRequestDTO.setIndividualId("5134256294");
		kycAuthRequestDTO.setRequest(request);
		Mockito.when(idInfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		Errors errors = new BeanPropertyBindingResult(kycAuthRequestDTO, "kycAuthRequestDTO");
		KycAuthRequestValidator.validate(kycAuthRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testForIsValidAuthtype() {
		KycAuthRequestDTO kycAuthRequestDTO = new KycAuthRequestDTO();
		kycAuthRequestDTO.setConsentObtained(Boolean.TRUE);
		kycAuthRequestDTO.setSecondaryLangCode("fra");
		kycAuthRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setVersion("1.1");
		kycAuthRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(false);
		authTypeDTO.setOtp(false);
		authTypeDTO.setPin(false);
		authTypeDTO.setBio(false);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage("EN");
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage("fre");
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);

		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(env.getProperty(MOSIP_SECONDARY_LANG_CODE));
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		kycAuthRequestDTO.setIndividualIdType(IdType.UIN.getType());
		RequestDTO request = new RequestDTO();
		String otp = "123456";
		request.setOtp(otp);
		kycAuthRequestDTO.setIndividualId("5134256294");
		request.setDemographics(idDTO);
		request.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		kycAuthRequestDTO.setConsentObtained(true);
		kycAuthRequestDTO.setRequest(request);
		kycAuthRequestDTO.setRequestedAuth(authTypeDTO);
		kycAuthRequestDTO.setRequest(request);
		Errors errors = new BeanPropertyBindingResult(kycAuthRequestDTO, "kycAuthRequestDTO");
		Mockito.when(idInfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.FALSE);
		KycAuthRequestValidator.validate(kycAuthRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

}
