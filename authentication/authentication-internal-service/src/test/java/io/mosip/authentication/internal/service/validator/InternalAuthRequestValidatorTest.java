package io.mosip.authentication.internal.service.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
import io.mosip.authentication.common.service.integration.MasterDataManager;
import io.mosip.authentication.common.service.validator.AuthRequestValidator;
import io.mosip.authentication.core.authtype.dto.AuthtypeRequestDto;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthTypeDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.indauth.dto.DigitalId;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.IdentityDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.mosip.kernel.idvalidator.vid.impl.VidValidatorImpl;
import io.mosip.kernel.logger.logback.appender.RollingFileAppender;

/**
 * @author Prem Kumar
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class InternalAuthRequestValidatorTest {

	@Mock
	private SpringValidatorAdapter validator;

	@Autowired
	private Environment environment;

	@Mock
	Errors errors;

	@InjectMocks
	RollingFileAppender appender;

	@InjectMocks
	private InternalAuthRequestValidator internalAuthRequestValidator;
	
	@InjectMocks
	private AuthtypeStatusValidator authtypeStatusValidator;

	@InjectMocks
	private AuthRequestValidator baseAuthRequestValidator;

	@Mock
	IdInfoHelper idinfoHelper;

	@Mock
	UinValidatorImpl uinValidator;

	@Mock
	VidValidatorImpl vidValidator;

	@Autowired
	Environment env;

	@Mock
	private MasterDataManager masterDataManager;

	@Before
	public void before() {
		ReflectionTestUtils.setField(internalAuthRequestValidator, "env", env);
		ReflectionTestUtils.setField(internalAuthRequestValidator, "idInfoHelper", idinfoHelper);
		ReflectionTestUtils.setField(baseAuthRequestValidator, "env", env);
		ReflectionTestUtils.setField(idinfoHelper, "environment", env);
	}

	@Test
	public void testSupportTrue() {
		assertTrue(internalAuthRequestValidator.supports(AuthRequestDTO.class));
	}
	
	@Test
	public void TestAuthTypeSupportTrue() {
		assertTrue(authtypeStatusValidator.supports(AuthtypeRequestDto.class));
	}

	@Test
	public void testSupportFalse() {
		assertFalse(internalAuthRequestValidator.supports(OtpRequestDTO.class));
	}

	@Test
	public void testinValidInternalAuthRequestValidator() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setRequestTime(null);
		authRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		authTypeDTO.setOtp(true);
		authTypeDTO.setBio(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary-language"));
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary-language"));
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(env.getProperty("mosip.secondary-language"));
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		authRequestDTO.setIndividualIdType("UIN");
		authRequestDTO.setIndividualId("274390482564");
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		reqDTO.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestDTO.setConsentObtained(true);
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.merge(((AbstractEnvironment) mockenv));
		mockenv.setProperty("internal.auth.types.allowed", "fulladdress");
		mockenv.setProperty("mosip.idtype.allowed", "UIN,VID");
		mockenv.setProperty("datetime.pattern", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		ReflectionTestUtils.setField(internalAuthRequestValidator, "env", mockenv);
		/*
		 * Environment env = mock(Environment.class);
		 * Mockito.when(env.getProperty("internal.allowed.auth.type")).thenReturn(
		 * "fulladdresss");
		 */
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		internalAuthRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testinValidInternalAuthRequestValidator2() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		authTypeDTO.setOtp(true);
		authTypeDTO.setBio(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary-language"));
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary-language"));
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(env.getProperty("mosip.secondary-language"));
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		authRequestDTO.setIndividualIdType("D");
		authRequestDTO.setIndividualId("274390482564");
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.merge(((AbstractEnvironment) mockenv));
		mockenv.setProperty("internal.auth.types.allowed", "fulladdress");
		ReflectionTestUtils.setField(internalAuthRequestValidator, "env", mockenv);
		/*
		 * Environment env = mock(Environment.class);
		 * Mockito.when(env.getProperty("internal.allowed.auth.type")).thenReturn(
		 * "fulladdresss");
		 */
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		internalAuthRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testValidInternalAuthRequestValidator2() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary-language"));
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary-language"));
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(env.getProperty("mosip.secondary-language"));
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setIndividualId("274390482564");
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		reqDTO.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);List<String> value = new ArrayList<>();
		value.add("dateOfBirth");
		Mockito.when(idinfoHelper.getIdMappingValue(Mockito.any(), Mockito.any())).thenReturn(value);
		
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		internalAuthRequestValidator.validate(authRequestDTO, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void testinValiddata() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		authTypeDTO.setOtp(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary-language"));
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary-language"));
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(env.getProperty("mosip.secondary-language"));
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		authRequestDTO.setIndividualIdType("D");
		authRequestDTO.setIndividualId("274390482564");
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		List<String> value = new ArrayList<>();
		value.add("dateOfBirth");
		Mockito.when(idinfoHelper.getIdMappingValue(Mockito.any(), Mockito.any())).thenReturn(value);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		internalAuthRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testValidInternalAuthRequestValidator() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(false);
		authTypeDTO.setBio(true);
		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		dataDTO.setDeviceCode("1");
		dataDTO.setDeviceServiceVersion("1");
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("1");
		digitalId.setMake("1");
		digitalId.setModel("1");
		digitalId.setType("1");
		digitalId.setDeviceProvider("1");
		digitalId.setDeviceProviderId("1");
		digitalId.setDateTime(DateUtils.getCurrentDateTimeString());
		dataDTO.setDigitalId(digitalId);
		dataDTO.setBioValue("finger");
		dataDTO.setBioSubType("Left Thumb");
		dataDTO.setBioType("FIR");
		dataDTO.setDeviceProviderID("provider001");
		dataDTO.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString());
		fingerValue.setData(dataDTO);

		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		DataDTO irisData = new DataDTO();
		irisData.setDeviceCode("1");
		irisData.setDeviceServiceVersion("1");
		DigitalId digitalId1 = new DigitalId();
		digitalId1.setSerialNo("1");
		digitalId1.setMake("1");
		digitalId1.setModel("1");
		digitalId1.setType("1");
		digitalId1.setDeviceProvider("1");
		digitalId1.setDeviceProviderId("1");
		digitalId1.setDateTime(DateUtils.getCurrentDateTimeString());
		irisData.setDigitalId(digitalId1);
		irisData.setBioValue("iris img");
		irisData.setBioSubType("Left");
		irisData.setBioType("IIR");
		irisData.setDeviceProviderID("provider001");
		irisData.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString());
		irisValue.setData(irisData);
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		DataDTO faceData = new DataDTO();
		faceData.setDeviceCode("1");
		faceData.setDeviceServiceVersion("1");
		DigitalId digitalId2= new DigitalId();
		digitalId2.setSerialNo("1");
		digitalId2.setMake("1");
		digitalId2.setModel("1");
		digitalId2.setType("1");
		digitalId2.setDeviceProvider("1");
		digitalId2.setDeviceProviderId("1");
		digitalId2.setDateTime(DateUtils.getCurrentDateTimeString());
		faceData.setDigitalId(digitalId2);
		faceData.setBioValue("face img");
		faceData.setBioType("FACE");
		faceData.setBioSubType("FACE");
		faceData.setDeviceProviderID("provider001");
		faceData.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString());
		faceValue.setData(faceData);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		requestDTO.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(requestDTO);
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setIndividualId("274390482564");
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		internalAuthRequestValidator.validate(authRequestDTO, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void testValidInternalAuthRequestValidatorEmptyID() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setRequestTime(Instant.now().toString());
		authRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		IdentityDTO identitydto = new IdentityDTO();
		authRequestDTO.setIndividualId("");
		authRequestDTO.setIndividualIdType("D");
		authTypeDTO.setDemo(true);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(identitydto);
		reqDTO.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		internalAuthRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInvalidInternalAuthRequestValidator() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();

		authRequestDTO.setRequestTime("2018-11-23T17:00:57.086+0530");
		authRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);

		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage("EN");
		idInfoDTO.setValue(null);
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage("fre");
		idInfoDTO.setValue(null);
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(env.getProperty("mosip.secondary-language"));
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		authRequestDTO.setIndividualIdType("D");
		authRequestDTO.setIndividualId("274390482564");
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		List<String> value = new ArrayList<>();
		value.add("dateOfBirth");
		Mockito.when(idinfoHelper.getIdMappingValue(Mockito.any(), Mockito.any())).thenReturn(value);
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		internalAuthRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInvalidDate() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();

		authRequestDTO.setRequestTime(Instant.now().toString());
		authRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		// authTypeDTO.setFace(true);
		// authTypeDTO.setFingerPrint(true);
		// authTypeDTO.setIris(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage("EN");
		idInfoDTO.setValue(null);
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage("fre");
		idInfoDTO.setValue(null);
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(env.getProperty("mosip.secondary-language"));
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		authRequestDTO.setIndividualIdType("D");
		authRequestDTO.setIndividualId("274390482564");
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		List<String> value = new ArrayList<>();
		value.add("dateOfBirth");
		Mockito.when(idinfoHelper.getIdMappingValue(Mockito.any(), Mockito.any())).thenReturn(value);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		internalAuthRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

//	@Test
//	public void TestInvalidTimeFormat() {
//		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
//		authRequestDTO.setId("id");
//		// authRequestDTO.setVer("1.1");
//		authRequestDTO.setTransactionID("1234567890");
//		authRequestDTO.setRequestTime("a2018-11-11");
//		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
//		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
//		internalAuthRequestValidator.validateDate(authRequestDTO, errors);
//		assertTrue(errors.hasErrors());
//	}

	@Test
	public void testInValidInternalAuthRequestValidator2() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setRequestTime(Instant.now().plus(2, ChronoUnit.DAYS).atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary-language"));
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary-language"));
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(env.getProperty("mosip.secondary-language"));
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setIndividualId("274390482564");
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		reqDTO.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		List<String> value = new ArrayList<>();
		value.add("dateOfBirth");
		Mockito.when(idinfoHelper.getIdMappingValue(Mockito.any(), Mockito.any())).thenReturn(value);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		internalAuthRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

}
