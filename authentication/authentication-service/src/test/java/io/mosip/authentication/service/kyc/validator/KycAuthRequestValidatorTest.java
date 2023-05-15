package io.mosip.authentication.service.kyc.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
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
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.common.service.validator.AuthRequestValidator;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.IdentityDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.EkycAuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
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
@Import(EnvUtil.class)
public class KycAuthRequestValidatorTest {

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
	PinValidatorImpl pinValidator;

	@Autowired
	EnvUtil env;

	@Mock
	private MasterDataManager masterDataManager;

	@Before
	public void before() {
		ReflectionTestUtils.setField(KycAuthRequestValidator, "idInfoHelper", idInfoHelper);
		ReflectionTestUtils.setField(authRequestValidator, "idInfoHelper", idInfoHelper);

	}

	@Test
	public void testSupportTrue() {
		assertTrue(KycAuthRequestValidator.supports(EkycAuthRequestDTO.class));
	}

	@Test
	public void testSupportFalse() {
		assertFalse(KycAuthRequestValidator.supports(KycAuthRequestValidator.class));
	}

	@Test
	@Ignore
	public void testValidateAuthRequest() {
		EkycAuthRequestDTO kycAuthRequestDTO = new EkycAuthRequestDTO();
		kycAuthRequestDTO.setConsentObtained(Boolean.TRUE);
		kycAuthRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setVersion("1.1");
		kycAuthRequestDTO.setTransactionID("1234567890");
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
		idInfoDTOs.setLanguage("fra");
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
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		kycAuthRequestDTO.setConsentObtained(true);
		kycAuthRequestDTO.setRequest(request);
		kycAuthRequestDTO.setRequest(request);
		Errors errors = new BeanPropertyBindingResult(kycAuthRequestDTO, "kycAuthRequestDTO");
		Mockito.when(idInfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		KycAuthRequestValidator.validate(kycAuthRequestDTO, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void TestInvalidAuthRequest() {
		EkycAuthRequestDTO kycAuthRequestDTO = new EkycAuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(kycAuthRequestDTO, "kycAuthRequestDTO");
		Mockito.when(idInfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		KycAuthRequestValidator.validate(kycAuthRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInvalidAuthRequest() {
		EkycAuthRequestDTO kycAuthRequestDTO = new EkycAuthRequestDTO();
		kycAuthRequestDTO.setConsentObtained(Boolean.TRUE);
		
		kycAuthRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		kycAuthRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		kycAuthRequestDTO.setTransactionID("1234567890");
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
		idInfoDTOs.setLanguage("fra");
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
		kycAuthRequestDTO.setRequest(request);
		Mockito.when(idInfoHelper.isMatchtypeEnabled(DemoMatchType.NAME)).thenReturn(Boolean.TRUE);
		Errors errors = new BeanPropertyBindingResult(kycAuthRequestDTO, "baseAuthRequestDTO");
		KycAuthRequestValidator.validate(kycAuthRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void TestMUAPermissionisNotAvail() {
		EnvUtil.setEkycAllowedAuthType("otp,bio,pin");
		EkycAuthRequestDTO kycAuthRequestDTO = new EkycAuthRequestDTO();

		kycAuthRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		kycAuthRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		kycAuthRequestDTO.setTransactionID("1234567890");
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
		kycAuthRequestDTO.setRequest(request);
		Mockito.when(idInfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		Errors errors = new BeanPropertyBindingResult(kycAuthRequestDTO, "kycAuthRequestDTO");
		KycAuthRequestValidator.validate(kycAuthRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void TestInvalidAuthType() {
		EkycAuthRequestDTO kycAuthRequestDTO = new EkycAuthRequestDTO();
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setVersion("1.1");
		kycAuthRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setTransactionID("1234567890");
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
		idInfoDTOs.setLanguage("fra");
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		kycAuthRequestDTO.setIndividualIdType("D");
		kycAuthRequestDTO.setConsentObtained(Boolean.FALSE);
		
		RequestDTO reqDTO = new RequestDTO();
		String otp = "123456";
		reqDTO.setOtp(otp);
		kycAuthRequestDTO.setIndividualId("5134256294");
		reqDTO.setDemographics(idDTO);
		kycAuthRequestDTO.setRequest(reqDTO);
		Mockito.when(idInfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		Errors errors = new BeanPropertyBindingResult(kycAuthRequestDTO, "kycAuthRequestDTO");
		KycAuthRequestValidator.validate(kycAuthRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void TestkycAuthRequestDtoisNull() {
		EkycAuthRequestDTO kycAuthRequestDTO = new EkycAuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(kycAuthRequestDTO, "kycAuthRequestDTO");
		kycAuthRequestDTO = null;
		KycAuthRequestValidator.validate(kycAuthRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void TestkycvalidateAuthType() {
		EkycAuthRequestDTO kycAuthRequestDTO = new EkycAuthRequestDTO();
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setVersion("1.1");
		kycAuthRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setTransactionID("1234567890");
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
		idInfoDTOs.setLanguage("fra");
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		kycAuthRequestDTO.setIndividualIdType("D");
		kycAuthRequestDTO.setConsentObtained(Boolean.TRUE);
		
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		kycAuthRequestDTO.setRequest(reqDTO);
		Mockito.when(idInfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		Errors errors = new BeanPropertyBindingResult(kycAuthRequestDTO, "kycAuthRequestDTO");
		KycAuthRequestValidator.validate(kycAuthRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void TestkycAuthRequestisNull() {
		EkycAuthRequestDTO kycAuthRequestDTO = new EkycAuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(kycAuthRequestDTO, "kycAuthRequestDTO");
		KycAuthRequestValidator.validate(kycAuthRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void TestInvalidConsentReq() {
		EkycAuthRequestDTO kycAuthRequestDTO = new EkycAuthRequestDTO();
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setVersion("1.1");
		kycAuthRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setTransactionID("1234567890");
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
		idInfoDTOs.setLanguage("fra");
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		kycAuthRequestDTO.setIndividualIdType("D");
		kycAuthRequestDTO.setConsentObtained(Boolean.FALSE);
		
		RequestDTO request = new RequestDTO();
		request.setDemographics(idDTO);
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
		EkycAuthRequestDTO kycAuthRequestDTO = new EkycAuthRequestDTO();
		kycAuthRequestDTO.setConsentObtained(Boolean.TRUE);
		
		kycAuthRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setVersion("1.1");
		kycAuthRequestDTO.setTransactionID("1234567890");
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
		idInfoDTOs.setLanguage("fra");
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
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		kycAuthRequestDTO.setConsentObtained(true);
		kycAuthRequestDTO.setRequest(request);
		kycAuthRequestDTO.setRequest(request);
		Errors errors = new BeanPropertyBindingResult(kycAuthRequestDTO, "kycAuthRequestDTO");
		Mockito.when(idInfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.FALSE);
		KycAuthRequestValidator.validate(kycAuthRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

}
