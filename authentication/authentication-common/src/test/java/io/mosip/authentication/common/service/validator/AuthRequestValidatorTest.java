package io.mosip.authentication.common.service.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.integration.MasterDataManager;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.hotlist.dto.HotlistDTO;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthTypeDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.indauth.dto.DigitalId;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.IdentityDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.spi.hotlist.service.HotlistService;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.util.IdValidationUtil;
import io.mosip.kernel.core.hotlist.constant.HotlistStatus;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.logger.logback.appender.RollingFileAppender;
import io.mosip.kernel.pinvalidator.impl.PinValidatorImpl;

/**
 * This class validates the AuthRequestValidator
 * 
 * @author Arun Bose
 * @author Rakesh Roshan
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class AuthRequestValidatorTest {

	@Mock
	private SpringValidatorAdapter validator;

	@Mock
	private PinValidatorImpl pinValidator;

	@Mock
	Errors error;

	@Autowired
	Environment env;

	@Mock
	IdValidationUtil idValidator;

	@Mock
	private IdInfoFetcher idInfoFetcher;

	@InjectMocks
	RollingFileAppender idaRollingFileAppender;

	@InjectMocks
	private AuthRequestValidator authRequestValidator;

	@Mock
	private IdInfoHelper idinfoHelper;

	@Mock
	private MasterDataManager masterDataManager;

	@InjectMocks
	private IDAMappingConfig idMappingConfig;

	@Mock
	private HotlistService hotlistService;

	@Before
	public void before() {
		ReflectionTestUtils.setField(authRequestValidator, "env", env);
		HotlistDTO response = new HotlistDTO();
		response.setStatus(HotlistStatus.UNBLOCKED);
		when(hotlistService.getHotlistStatus(Mockito.any(), Mockito.any())).thenReturn(response);
		authRequestValidator.initialize();
	}

	@Test
	public void testSupportTrue() {
		assertTrue(authRequestValidator.supports(AuthRequestDTO.class));
	}

	@Test
	public void testValidUin() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();

		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setEnv("");
		authRequestDTO.setDomainUri("");
		authRequestDTO.setVersion("1.1");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		authTypeDTO.setOtp(true);
		authTypeDTO.setPin(true);
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
		reqDTO.setOtp("123456");
		reqDTO.setStaticPin("123456");
		reqDTO.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestDTO.setTransactionID("1234567890");
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		List<String> value = new ArrayList<>();
		value.add("dateOfBirth");
		Mockito.when(idinfoHelper.getIdMappingValue(Mockito.any(), Mockito.any())).thenReturn(value);
		authRequestValidator.validate(authRequestDTO, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void testInvalidUin() throws IdAuthenticationBusinessException {
		Mockito.when(idValidator.validateUIN(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("1.1");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		authTypeDTO.setOtp(true);
		authTypeDTO.setPin(true);
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
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		reqDTO.setOtp("123456");
		reqDTO.setStaticPin("123456");
		reqDTO.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		List<String> value = new ArrayList<>();
		value.add("dateOfBirth");
		Mockito.when(idinfoHelper.getIdMappingValue(Mockito.any(), Mockito.any())).thenReturn(value);
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	@Ignore
	public void testValidVid() throws IdAuthenticationBusinessException {
		Mockito.when(idValidator.validateUIN(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("1.1");
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
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.VID.getType());
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
		authRequestValidator.validate(authRequestDTO, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void testInvalidVid() throws IdAuthenticationBusinessException {
		Mockito.when(idValidator.validateVID(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("1.1");
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
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.VID.getType());
		List<String> value = new ArrayList<>();
		value.add("dateOfBirth");
		Mockito.when(idinfoHelper.getIdMappingValue(Mockito.any(), Mockito.any())).thenReturn(value);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInvalidRequestDTO() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInvalidTimestamp() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setRequestTime("2001-07-04T12:08:56.235-0700");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInvalidTimestamp2() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setRequestTime(Instant.now().plus(2, ChronoUnit.DAYS).atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInvalidTimestamp3() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		ReflectionTestUtils
				.invokeMethod(authRequestValidator, "validateRequestTimedOut",
						Instant.now().minus(2, ChronoUnit.DAYS).atOffset(ZoneOffset.of("+0530"))
								.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString(),
						errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInvalidVer() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setRequestTime(Instant.now().toString());
		authRequestDTO.setVersion("1.12");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInvalidTxnId() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setRequestTime(Instant.now().toString());
		authRequestDTO.setVersion("1.1");
		authRequestDTO.setTransactionID("");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testNullId() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("1.1");
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
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInValidRequest() throws IdAuthenticationBusinessException {
		Mockito.when(idValidator.validateUIN(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("1.1");
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
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	@Ignore
	public void testValidRequest() throws IdAuthenticationBusinessException {
		Mockito.when(idValidator.validateUIN(Mockito.anyString())).thenReturn(Boolean.TRUE);
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("1.0");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		// name
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary-language"));
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary-language"));
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		// dob
		IdentityInfoDTO idInfoDTO2 = new IdentityInfoDTO();
		idInfoDTO2.setLanguage(null);
		idInfoDTO2.setValue(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("dob.req.date.pattern"))).toString());
		List<IdentityInfoDTO> idInfoList1 = new ArrayList<>();
		idInfoList1.add(idInfoDTO2);
		// dobtype
		IdentityInfoDTO idInfoDTO4 = new IdentityInfoDTO();
		idInfoDTO4.setLanguage(null);
		idInfoDTO4.setValue("V");
		List<IdentityInfoDTO> idInfoList2 = new ArrayList<>();
		idInfoList2.add(idInfoDTO4);
		// age
		IdentityInfoDTO idInfoDTO3 = new IdentityInfoDTO();
		idInfoDTO3.setLanguage(null);
		idInfoDTO3.setValue("25");
		List<IdentityInfoDTO> idInfoList3 = new ArrayList<>();
		idInfoList3.add(idInfoDTO3);
		// gender
		IdentityInfoDTO idInfoDTO5 = new IdentityInfoDTO();
		idInfoDTO5.setLanguage(null);
		idInfoDTO5.setValue("M");
		List<IdentityInfoDTO> idInfoList4 = new ArrayList<>();
		idInfoList4.add(idInfoDTO5);

		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setDobType(idInfoList2);
		idDTO.setGender(idInfoList4);
		idDTO.setAge("25");
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		reqDTO.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		List<String> value = new ArrayList<>();
		value.add("dateOfBirth");
		Mockito.when(idinfoHelper.getIdMappingValue(Mockito.any(), Mockito.any())).thenReturn(value);
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		authRequestValidator.validate(authRequestDTO, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void testInValidRequest2() throws IdAuthenticationBusinessException {
		Mockito.when(idValidator.validateUIN(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		// name
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary-language"));
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary-language"));
		idInfoDTO1.setValue("Mike");
		IdentityInfoDTO idInfoDTO6 = new IdentityInfoDTO();
		idInfoDTO6.setLanguage(env.getProperty("mosip.primary-language"));
		idInfoDTO6.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		idInfoList.add(idInfoDTO6);
		// dob
		IdentityInfoDTO idInfoDTO2 = new IdentityInfoDTO();
		idInfoDTO2.setLanguage(null);
		idInfoDTO2.setValue(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("dob.req.date.pattern"))).toString());
		List<IdentityInfoDTO> idInfoList1 = new ArrayList<>();
		idInfoList1.add(idInfoDTO2);
		// dobtype
		IdentityInfoDTO idInfoDTO4 = new IdentityInfoDTO();
		idInfoDTO4.setLanguage(null);
		idInfoDTO4.setValue("V");
		List<IdentityInfoDTO> idInfoList2 = new ArrayList<>();
		idInfoList2.add(idInfoDTO4);
		// age
		IdentityInfoDTO idInfoDTO3 = new IdentityInfoDTO();
		idInfoDTO3.setLanguage(null);
		idInfoDTO3.setValue("25");
		List<IdentityInfoDTO> idInfoList3 = new ArrayList<>();
		idInfoList3.add(idInfoDTO3);
		// gender
		IdentityInfoDTO idInfoDTO5 = new IdentityInfoDTO();
		idInfoDTO5.setLanguage(null);
		idInfoDTO5.setValue("M");
		List<IdentityInfoDTO> idInfoList4 = new ArrayList<>();
		idInfoList4.add(idInfoDTO5);

		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setDobType(idInfoList2);
		idDTO.setGender(idInfoList4);
		idDTO.setAge("25");
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType("D");
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		List<String> value = new ArrayList<>();
		value.add("dateOfBirth");
		Mockito.when(idinfoHelper.getIdMappingValue(Mockito.any(), Mockito.any())).thenReturn(value);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInValidRequest3() throws IdAuthenticationBusinessException {
		Mockito.when(idValidator.validateUIN(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("dob.req.date.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		// name
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage("EN");
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary-language"));
		idInfoDTO1.setValue("Mike");
		IdentityInfoDTO idInfoDTO6 = new IdentityInfoDTO();
		idInfoDTO6.setLanguage(null);
		idInfoDTO6.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		idInfoList.add(idInfoDTO6);
		// dob
		IdentityInfoDTO idInfoDTO2 = new IdentityInfoDTO();
		idInfoDTO2.setLanguage(null);
		idInfoDTO2.setValue(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		List<IdentityInfoDTO> idInfoList1 = new ArrayList<>();
		idInfoList1.add(idInfoDTO2);
		// dobtype
		IdentityInfoDTO idInfoDTO4 = new IdentityInfoDTO();
		idInfoDTO4.setLanguage(null);
		idInfoDTO4.setValue("V");
		List<IdentityInfoDTO> idInfoList2 = new ArrayList<>();
		idInfoList2.add(idInfoDTO4);
		// age
		IdentityInfoDTO idInfoDTO3 = new IdentityInfoDTO();
		idInfoDTO3.setLanguage(null);
		idInfoDTO3.setValue("25");
		List<IdentityInfoDTO> idInfoList3 = new ArrayList<>();
		idInfoList3.add(idInfoDTO3);
		// gender
		IdentityInfoDTO idInfoDTO5 = new IdentityInfoDTO();
		idInfoDTO5.setLanguage(null);
		idInfoDTO5.setValue("M");
		List<IdentityInfoDTO> idInfoList4 = new ArrayList<>();
		idInfoList4.add(idInfoDTO5);

		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setDobType(idInfoList2);
		idDTO.setGender(idInfoList4);
		idDTO.setAge("25");
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType("D");
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInValidRequest4() throws IdAuthenticationBusinessException {
		Mockito.when(idValidator.validateUIN(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		// name
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.secondary-language"));
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary-language"));
		idInfoDTO1.setValue("Mike");
		IdentityInfoDTO idInfoDTO6 = new IdentityInfoDTO();
		idInfoDTO6.setLanguage(env.getProperty("mosip.primary-language"));
		idInfoDTO6.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		idInfoList.add(idInfoDTO6);
		// dob
		IdentityInfoDTO idInfoDTO2 = new IdentityInfoDTO();
		idInfoDTO2.setLanguage(null);
		idInfoDTO2.setValue(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		List<IdentityInfoDTO> idInfoList1 = new ArrayList<>();
		idInfoList1.add(idInfoDTO2);
		// dobtype
		IdentityInfoDTO idInfoDTO4 = new IdentityInfoDTO();
		idInfoDTO4.setLanguage(null);
		idInfoDTO4.setValue("V");
		List<IdentityInfoDTO> idInfoList2 = new ArrayList<>();
		idInfoList2.add(idInfoDTO4);
		// age
		IdentityInfoDTO idInfoDTO3 = new IdentityInfoDTO();
		idInfoDTO3.setLanguage(null);
		idInfoDTO3.setValue("25");
		List<IdentityInfoDTO> idInfoList3 = new ArrayList<>();
		idInfoList3.add(idInfoDTO3);
		// gender
		IdentityInfoDTO idInfoDTO5 = new IdentityInfoDTO();
		idInfoDTO5.setLanguage(null);
		idInfoDTO5.setValue("M");
		List<IdentityInfoDTO> idInfoList4 = new ArrayList<>();
		idInfoList4.add(idInfoDTO5);

		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob(null);
		idDTO.setDobType(null);
		idDTO.setGender(null);
		idDTO.setAge(null);
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType("D");
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInValidRequest5() throws IdAuthenticationBusinessException {
		Mockito.when(idValidator.validateUIN(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("dob.req.date.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		// name
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary-language"));
		idInfoDTO.setValue(null);
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary-language"));
		idInfoDTO1.setValue("Mike");
		IdentityInfoDTO idInfoDTO6 = new IdentityInfoDTO();
		idInfoDTO6.setLanguage(null);
		idInfoDTO6.setValue(null);
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		idInfoList.add(idInfoDTO6);
		// dob
		IdentityInfoDTO idInfoDTO2 = new IdentityInfoDTO();
		idInfoDTO2.setLanguage(null);
		idInfoDTO2.setValue(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		List<IdentityInfoDTO> idInfoList1 = new ArrayList<>();
		idInfoList1.add(idInfoDTO2);
		// dobtype
		IdentityInfoDTO idInfoDTO4 = new IdentityInfoDTO();
		idInfoDTO4.setLanguage(null);
		idInfoDTO4.setValue("V");
		List<IdentityInfoDTO> idInfoList2 = new ArrayList<>();
		idInfoList2.add(idInfoDTO4);
		// age
		IdentityInfoDTO idInfoDTO3 = new IdentityInfoDTO();
		idInfoDTO3.setLanguage(null);
		idInfoDTO3.setValue(null);
		List<IdentityInfoDTO> idInfoList3 = new ArrayList<>();
		idInfoList3.add(idInfoDTO3);
		// gender
		IdentityInfoDTO idInfoDTO5 = new IdentityInfoDTO();
		idInfoDTO5.setLanguage(env.getProperty("mosip.primary-language"));
		idInfoDTO5.setValue(null);
		List<IdentityInfoDTO> idInfoList4 = new ArrayList<>();
		idInfoList4.add(idInfoDTO5);

		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setDobType(idInfoList2);
		idDTO.setGender(idInfoList4);
		idDTO.setAge("25");
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType("D");
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInValidRequest6() throws IdAuthenticationBusinessException {
		Mockito.when(idValidator.validateUIN(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(false);
		// name
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary-language"));
		idInfoDTO.setValue(null);
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary-language"));
		idInfoDTO1.setValue("Mike");
		IdentityInfoDTO idInfoDTO6 = new IdentityInfoDTO();
		idInfoDTO6.setLanguage(null);
		idInfoDTO6.setValue(null);
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		idInfoList.add(idInfoDTO6);
		// dob
		IdentityInfoDTO idInfoDTO2 = new IdentityInfoDTO();
		idInfoDTO2.setLanguage(null);
		idInfoDTO2.setValue(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		List<IdentityInfoDTO> idInfoList1 = new ArrayList<>();
		idInfoList1.add(idInfoDTO2);
		// dobtype
		IdentityInfoDTO idInfoDTO4 = new IdentityInfoDTO();
		idInfoDTO4.setLanguage(null);
		idInfoDTO4.setValue("V");
		List<IdentityInfoDTO> idInfoList2 = new ArrayList<>();
		idInfoList2.add(idInfoDTO4);
		// age
		IdentityInfoDTO idInfoDTO3 = new IdentityInfoDTO();
		idInfoDTO3.setLanguage(null);
		idInfoDTO3.setValue(null);
		List<IdentityInfoDTO> idInfoList3 = new ArrayList<>();
		idInfoList3.add(idInfoDTO3);
		// gender
		IdentityInfoDTO idInfoDTO5 = new IdentityInfoDTO();
		idInfoDTO5.setLanguage(env.getProperty("mosip.primary-language"));
		idInfoDTO5.setValue(null);
		List<IdentityInfoDTO> idInfoList4 = new ArrayList<>();
		idInfoList4.add(idInfoDTO5);

		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setDobType(idInfoList2);
		idDTO.setGender(idInfoList4);
		idDTO.setAge("25");
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType("D");
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInValidRequest7() throws IdAuthenticationBusinessException {
		Mockito.when(idValidator.validateUIN(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		// name
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary-language"));
		idInfoDTO.setValue(null);
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary-language"));
		idInfoDTO1.setValue("Mike");
		IdentityInfoDTO idInfoDTO6 = new IdentityInfoDTO();
		idInfoDTO6.setLanguage(null);
		idInfoDTO6.setValue(null);
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		idInfoList.add(idInfoDTO6);
		// dob
		IdentityInfoDTO idInfoDTO2 = new IdentityInfoDTO();
		idInfoDTO2.setLanguage(null);
		idInfoDTO2.setValue(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		List<IdentityInfoDTO> idInfoList1 = new ArrayList<>();
		idInfoList1.add(idInfoDTO2);
		// dobtype
		IdentityInfoDTO idInfoDTO4 = new IdentityInfoDTO();
		idInfoDTO4.setLanguage(null);
		idInfoDTO4.setValue("V");
		List<IdentityInfoDTO> idInfoList2 = new ArrayList<>();
		idInfoList2.add(idInfoDTO4);
		// age
		IdentityInfoDTO idInfoDTO3 = new IdentityInfoDTO();
		idInfoDTO3.setLanguage(null);
		idInfoDTO3.setValue(null);
		List<IdentityInfoDTO> idInfoList3 = new ArrayList<>();
		idInfoList3.add(idInfoDTO3);
		// gender
		IdentityInfoDTO idInfoDTO5 = new IdentityInfoDTO();
		idInfoDTO5.setLanguage(env.getProperty("mosip.primary-language"));
		idInfoDTO5.setValue(null);
		List<IdentityInfoDTO> idInfoList4 = new ArrayList<>();
		idInfoList4.add(idInfoDTO5);

		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setDobType(idInfoList2);
		idDTO.setGender(idInfoList4);
		idDTO.setAge("25");
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType("D");
		authTypeDTO.setOtp(true);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInValidRequest8() throws IdAuthenticationBusinessException {
		Mockito.when(idValidator.validateUIN(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		// name
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary-language"));
		idInfoDTO.setValue(null);
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary-language"));
		idInfoDTO1.setValue("Mike");
		IdentityInfoDTO idInfoDTO6 = new IdentityInfoDTO();
		idInfoDTO6.setLanguage(null);
		idInfoDTO6.setValue(null);
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		idInfoList.add(idInfoDTO6);
		// dob
		IdentityInfoDTO idInfoDTO2 = new IdentityInfoDTO();
		idInfoDTO2.setLanguage(null);
		idInfoDTO2.setValue(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		List<IdentityInfoDTO> idInfoList1 = new ArrayList<>();
		idInfoList1.add(idInfoDTO2);
		// dobtype
		IdentityInfoDTO idInfoDTO4 = new IdentityInfoDTO();
		idInfoDTO4.setLanguage(null);
		idInfoDTO4.setValue("V");
		List<IdentityInfoDTO> idInfoList2 = new ArrayList<>();
		idInfoList2.add(idInfoDTO4);
		// age
		IdentityInfoDTO idInfoDTO3 = new IdentityInfoDTO();
		idInfoDTO3.setLanguage(null);
		idInfoDTO3.setValue(null);
		List<IdentityInfoDTO> idInfoList3 = new ArrayList<>();
		idInfoList3.add(idInfoDTO3);
		// gender
		IdentityInfoDTO idInfoDTO5 = new IdentityInfoDTO();
		idInfoDTO5.setLanguage(env.getProperty("mosip.primary-language"));
		idInfoDTO5.setValue(null);
		List<IdentityInfoDTO> idInfoList4 = new ArrayList<>();
		idInfoList4.add(idInfoDTO5);

		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setDobType(idInfoList2);
		idDTO.setGender(idInfoList4);
		idDTO.setAge("25");
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType("D");
		RequestDTO reqDTO = new RequestDTO();
		String pin = "123456";
		authTypeDTO.setOtp(true);
		reqDTO.setOtp(pin);
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testValidRequest2() throws IdAuthenticationBusinessException {
		Mockito.when(idValidator.validateUIN(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		// name
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary-language"));
		idInfoDTO.setValue(null);
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary-language"));
		idInfoDTO1.setValue("Mike");
		IdentityInfoDTO idInfoDTO6 = new IdentityInfoDTO();
		idInfoDTO6.setLanguage(null);
		idInfoDTO6.setValue(null);
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		idInfoList.add(idInfoDTO6);
		// dob
		IdentityInfoDTO idInfoDTO2 = new IdentityInfoDTO();
		idInfoDTO2.setLanguage(null);
		idInfoDTO2.setValue(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		List<IdentityInfoDTO> idInfoList1 = new ArrayList<>();
		idInfoList1.add(idInfoDTO2);
		// dobtype
		IdentityInfoDTO idInfoDTO4 = new IdentityInfoDTO();
		idInfoDTO4.setLanguage(null);
		idInfoDTO4.setValue("V");
		List<IdentityInfoDTO> idInfoList2 = new ArrayList<>();
		idInfoList2.add(idInfoDTO4);
		// age
		IdentityInfoDTO idInfoDTO3 = new IdentityInfoDTO();
		idInfoDTO3.setLanguage(null);
		idInfoDTO3.setValue(null);
		List<IdentityInfoDTO> idInfoList3 = new ArrayList<>();
		idInfoList3.add(idInfoDTO3);
		// gender
		IdentityInfoDTO idInfoDTO5 = new IdentityInfoDTO();
		idInfoDTO5.setLanguage(env.getProperty("mosip.primary-language"));
		idInfoDTO5.setValue(null);
		List<IdentityInfoDTO> idInfoList4 = new ArrayList<>();
		idInfoList4.add(idInfoDTO5);

		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setDobType(idInfoList2);
		idDTO.setGender(idInfoList4);
		idDTO.setAge("25");
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType("D");
		authTypeDTO.setOtp(true);
		String pin = "123456";
		authTypeDTO.setOtp(true);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setOtp(pin);
		reqDTO.setDemographics(idDTO);
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testValidRequest10() throws IdAuthenticationBusinessException {
		Mockito.when(idValidator.validateUIN(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setConsentObtained(true);

		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		// name
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary-language"));
		idInfoDTO.setValue(null);
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary-language"));
		idInfoDTO1.setValue("Mike");
		IdentityInfoDTO idInfoDTO6 = new IdentityInfoDTO();
		idInfoDTO6.setLanguage(null);
		idInfoDTO6.setValue(null);
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		idInfoList.add(idInfoDTO6);
		// dob
		IdentityInfoDTO idInfoDTO2 = new IdentityInfoDTO();
		idInfoDTO2.setLanguage(null);
		idInfoDTO2.setValue(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		List<IdentityInfoDTO> idInfoList1 = new ArrayList<>();
		idInfoList1.add(idInfoDTO2);
		// dobtype
		IdentityInfoDTO idInfoDTO4 = new IdentityInfoDTO();
		idInfoDTO4.setLanguage(null);
		idInfoDTO4.setValue("V");
		List<IdentityInfoDTO> idInfoList2 = new ArrayList<>();
		idInfoList2.add(idInfoDTO4);
		// age
		IdentityInfoDTO idInfoDTO3 = new IdentityInfoDTO();
		idInfoDTO3.setLanguage(null);
		idInfoDTO3.setValue(null);
		List<IdentityInfoDTO> idInfoList3 = new ArrayList<>();
		idInfoList3.add(idInfoDTO3);
		// gender
		IdentityInfoDTO idInfoDTO5 = new IdentityInfoDTO();
		idInfoDTO5.setLanguage(env.getProperty("mosip.primary-language"));
		idInfoDTO5.setValue(null);
		List<IdentityInfoDTO> idInfoList4 = new ArrayList<>();
		idInfoList4.add(idInfoDTO5);

		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("1990/11/25");
		idDTO.setDobType(idInfoList2);
		idDTO.setGender(idInfoList4);
		idDTO.setAge("252");

		authTypeDTO.setOtp(true);
		RequestDTO reqDTO = new RequestDTO();
		String otp = "";
		authTypeDTO.setOtp(true);
		reqDTO.setOtp(otp);
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(true);
		List<String> value = new ArrayList<>();
		value.add("dateOfBirth");
		Mockito.when(idinfoHelper.getIdMappingValue(Mockito.any(), Mockito.any())).thenReturn(value);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void TestTimeexceeds() {
		AuthRequestDTO authRequestDTO = getAuthRequestDto();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateRequestTimedOut",
				"2019-04-24T09:41:57.086+05:30", errors);
		assertTrue(errors.hasErrors());
	}

	// ----------- Supporting method ---------------

	private AuthRequestDTO getAuthRequestDto() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());

		return authRequestDTO;
	}

	@Test
	public void testInValidAuthType() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
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
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testNullAuthType2() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Method declaredMethod = AuthRequestValidator.class.getDeclaredMethod("checkAuthRequest", AuthRequestDTO.class,
				Errors.class);
		declaredMethod.setAccessible(true);
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(false);
		authType.setOtp(false);
		authType.setDemo(false);
		authType.setPin(false);
		authRequestDTO.setRequestedAuth(authType);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		declaredMethod.invoke(authRequestValidator, authRequestDTO, errors);
	}

	@Test
	public void testInvalidTimeStamp() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Method declaredMethod = AuthRequestValidator.class.getDeclaredMethod("validateReqTime", String.class,
				Errors.class, String.class);
		declaredMethod.setAccessible(true);
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		declaredMethod.invoke(authRequestValidator, "2019-01-28", errors, "");
	}

	@Test
	public void testOTPNotPresent() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setOtp(true);
		RequestDTO request = new RequestDTO();
		request.setOtp("");
		authRequestDTO.setRequest(request);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
	}

	@Test
	public void testNoErrorForDomainUriEnvOptional() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("v1");
		authRequestDTO.setIndividualId("12345");
		authRequestDTO.setIndividualIdType("UIN");
		authRequestDTO.setVersion("v1");
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setOtp(true);
		RequestDTO request = new RequestDTO();
		request.setOtp("111111");
		authRequestDTO.setRequestedAuth(authType);
		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequest(request);
		String timestamp = Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString();
		authRequestDTO.setRequestTime(timestamp);
		authRequestDTO.setTransactionID("1234567890");
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		request.setTimestamp(timestamp);
		authRequestDTO.setRequest(request);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().isEmpty());
	}

	@Test
	public void testErrorForDomainUriInBioData() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("v1");
		authRequestDTO.setIndividualId("12345");
		authRequestDTO.setIndividualIdType("UIN");
		authRequestDTO.setVersion("v1");
		authRequestDTO.setDomainUri("localhost");
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(true);
		RequestDTO request = new RequestDTO();

		List<BioIdentityInfoDTO> biometrics = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityDto = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		data.setBioValue("adsadas");
		data.setBioType("Face");
		bioIdentityDto.setData(data);
		biometrics.add(bioIdentityDto);
		request.setBiometrics(biometrics);

		authRequestDTO.setRequestedAuth(authType);
		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequest(request);
		String timestamp = Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString();
		authRequestDTO.setRequestTime(timestamp);
		authRequestDTO.setTransactionID("1234567890");
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		request.setTimestamp(timestamp);
		authRequestDTO.setRequest(request);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(!errors.getAllErrors().isEmpty() && errors.getAllErrors().stream()
				.anyMatch(err -> err.getCode().equals(IdAuthenticationErrorConstants.INPUT_MISMATCH.getErrorCode())));
	}

	@Test
	public void testErrorForDomainUriMissingInAuthReq() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("v1");
		authRequestDTO.setIndividualId("12345");
		authRequestDTO.setIndividualIdType("UIN");
		authRequestDTO.setVersion("v1");
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(true);
		RequestDTO request = new RequestDTO();

		List<BioIdentityInfoDTO> biometrics = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityDto = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		data.setBioValue("adsadas");
		data.setBioType("Face");
		data.setDomainUri("localhost");
		bioIdentityDto.setData(data);
		biometrics.add(bioIdentityDto);
		request.setBiometrics(biometrics);

		authRequestDTO.setRequestedAuth(authType);
		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequest(request);
		String timestamp = Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString();
		authRequestDTO.setRequestTime(timestamp);
		authRequestDTO.setTransactionID("1234567890");
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		request.setTimestamp(timestamp);
		authRequestDTO.setRequest(request);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(!errors.getAllErrors().isEmpty() && errors.getAllErrors().stream()
				.anyMatch(err -> err.getCode().equals(IdAuthenticationErrorConstants.INPUT_MISMATCH.getErrorCode())));
	}

	@Test
	public void testErrorForDomainUriNotMatchingBetweenReqAndBio() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("v1");
		authRequestDTO.setIndividualId("12345");
		authRequestDTO.setIndividualIdType("UIN");
		authRequestDTO.setVersion("v1");
		authRequestDTO.setDomainUri("localhost1");
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(true);
		RequestDTO request = new RequestDTO();

		List<BioIdentityInfoDTO> biometrics = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityDto = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		data.setBioValue("adsadas");
		data.setBioType("Face");
		data.setDomainUri("localhost2");
		bioIdentityDto.setData(data);
		biometrics.add(bioIdentityDto);
		request.setBiometrics(biometrics);

		authRequestDTO.setRequestedAuth(authType);
		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequest(request);
		String timestamp = Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString();
		authRequestDTO.setRequestTime(timestamp);
		authRequestDTO.setTransactionID("1234567890");
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		request.setTimestamp(timestamp);
		authRequestDTO.setRequest(request);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(!errors.getAllErrors().isEmpty() && errors.getAllErrors().stream()
				.anyMatch(err -> err.getCode().equals(IdAuthenticationErrorConstants.INPUT_MISMATCH.getErrorCode())));
	}

	@Test
	public void testNoErrorForDomainUriNullOnBothReqAndBio() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("v1");
		authRequestDTO.setIndividualId("12345");
		authRequestDTO.setIndividualIdType("UIN");
		authRequestDTO.setVersion("v1");
		// authRequestDTO.setDomainUri("localhost1");
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(true);
		RequestDTO request = new RequestDTO();

		List<BioIdentityInfoDTO> biometrics = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityDto = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("");
		digitalId.setMake("");
		digitalId.setModel("");
		digitalId.setDeviceProvider("");
		digitalId.setDeviceProviderId("");
		data.setDigitalId(digitalId);
		data.setBioValue("adsadas");
		data.setBioType("Face");
		// data.setDomainUri("localhost2");
		bioIdentityDto.setData(data);
		biometrics.add(bioIdentityDto);
		request.setBiometrics(biometrics);

		authRequestDTO.setRequestedAuth(authType);
		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequest(request);
		String timestamp = Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString();
		authRequestDTO.setRequestTime(timestamp);
		
		data.setTimestamp(timestamp);
		digitalId.setDateTime(timestamp);
		
		authRequestDTO.setTransactionID("1234567890");
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		request.setTimestamp(timestamp);
		authRequestDTO.setRequest(request);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().isEmpty());
	}

	@Test
	public void testNoErrorForDomainUriMatchesOnBothReqAndBio() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("v1");
		authRequestDTO.setIndividualId("12345");
		authRequestDTO.setIndividualIdType("UIN");
		authRequestDTO.setVersion("v1");
		authRequestDTO.setDomainUri("https://dev.mosip.net");
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(true);
		RequestDTO request = new RequestDTO();

		List<BioIdentityInfoDTO> biometrics = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityDto = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		data.setBioValue("adsadas");
		data.setBioType("Face");
		data.setDomainUri("https://dev.mosip.net");
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("");
		digitalId.setMake("");
		digitalId.setModel("");
		digitalId.setDeviceProvider("");
		digitalId.setDeviceProviderId("");
		data.setDigitalId(digitalId);
		bioIdentityDto.setData(data);
		biometrics.add(bioIdentityDto);
		request.setBiometrics(biometrics);

		authRequestDTO.setRequestedAuth(authType);
		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequest(request);
		String timestamp = Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString();
		authRequestDTO.setRequestTime(timestamp);
		authRequestDTO.setTransactionID("1234567890");
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		request.setTimestamp(timestamp);
		authRequestDTO.setRequest(request);
		
		data.setTimestamp(timestamp);
		digitalId.setDateTime(timestamp);
		
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().isEmpty());
	}

	@Test
	public void testErrorForEnvInBioData() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("v1");
		authRequestDTO.setIndividualId("12345");
		authRequestDTO.setIndividualIdType("UIN");
		authRequestDTO.setVersion("v1");
		authRequestDTO.setEnv("Staging");
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(true);
		RequestDTO request = new RequestDTO();

		List<BioIdentityInfoDTO> biometrics = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityDto = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		data.setBioValue("adsadas");
		data.setBioType("Face");
		bioIdentityDto.setData(data);
		biometrics.add(bioIdentityDto);
		request.setBiometrics(biometrics);

		authRequestDTO.setRequestedAuth(authType);
		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequest(request);
		String timestamp = Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString();
		authRequestDTO.setRequestTime(timestamp);
		authRequestDTO.setTransactionID("1234567890");
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		request.setTimestamp(timestamp);
		authRequestDTO.setRequest(request);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(!errors.getAllErrors().isEmpty() && errors.getAllErrors().stream()
				.anyMatch(err -> err.getCode().equals(IdAuthenticationErrorConstants.INPUT_MISMATCH.getErrorCode())));
	}

	@Test
	public void testErrorForEnvMissingInAuthReq() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("v1");
		authRequestDTO.setIndividualId("12345");
		authRequestDTO.setIndividualIdType("UIN");
		authRequestDTO.setVersion("v1");
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(true);
		RequestDTO request = new RequestDTO();

		List<BioIdentityInfoDTO> biometrics = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityDto = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		data.setBioValue("adsadas");
		data.setBioType("Face");
		data.setEnv("Staging");
		bioIdentityDto.setData(data);
		biometrics.add(bioIdentityDto);
		request.setBiometrics(biometrics);

		authRequestDTO.setRequestedAuth(authType);
		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequest(request);
		String timestamp = Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString();
		authRequestDTO.setRequestTime(timestamp);
		authRequestDTO.setTransactionID("1234567890");
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		request.setTimestamp(timestamp);
		authRequestDTO.setRequest(request);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(!errors.getAllErrors().isEmpty() && errors.getAllErrors().stream()
				.anyMatch(err -> err.getCode().equals(IdAuthenticationErrorConstants.INPUT_MISMATCH.getErrorCode())));
	}

	@Test
	public void testErrorForEnvNotMatchingBetweenReqAndBio() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("v1");
		authRequestDTO.setIndividualId("12345");
		authRequestDTO.setIndividualIdType("UIN");
		authRequestDTO.setVersion("v1");
		authRequestDTO.setEnv("Staging1");
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(true);
		RequestDTO request = new RequestDTO();

		List<BioIdentityInfoDTO> biometrics = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityDto = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		data.setBioValue("adsadas");
		data.setBioType("Face");
		data.setEnv("Staging2");
		bioIdentityDto.setData(data);
		biometrics.add(bioIdentityDto);
		request.setBiometrics(biometrics);

		authRequestDTO.setRequestedAuth(authType);
		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequest(request);
		String timestamp = Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString();
		authRequestDTO.setRequestTime(timestamp);
		authRequestDTO.setTransactionID("1234567890");
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		request.setTimestamp(timestamp);
		authRequestDTO.setRequest(request);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(!errors.getAllErrors().isEmpty() && errors.getAllErrors().stream()
				.anyMatch(err -> err.getCode().equals(IdAuthenticationErrorConstants.INPUT_MISMATCH.getErrorCode())));
	}

	@Test
	public void testNoErrorForEnvNullOnBothReqAndBio() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("v1");
		authRequestDTO.setIndividualId("12345");
		authRequestDTO.setIndividualIdType("UIN");
		authRequestDTO.setVersion("v1");
		// authRequestDTO.setEnv("Staging");
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(true);
		RequestDTO request = new RequestDTO();

		List<BioIdentityInfoDTO> biometrics = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityDto = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		data.setBioValue("adsadas");
		data.setBioType("Face");
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("");
		digitalId.setMake("");
		digitalId.setModel("");
		digitalId.setDeviceProvider("");
		digitalId.setDeviceProviderId("");
		data.setDigitalId(digitalId);
		// data.setEnv("Staging");
		bioIdentityDto.setData(data);
		biometrics.add(bioIdentityDto);
		request.setBiometrics(biometrics);

		authRequestDTO.setRequestedAuth(authType);
		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequest(request);
		String timestamp = Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString();
		authRequestDTO.setRequestTime(timestamp);
		authRequestDTO.setTransactionID("1234567890");
		
		data.setTimestamp(timestamp);
		digitalId.setDateTime(timestamp);
		
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		request.setTimestamp(timestamp);
		authRequestDTO.setRequest(request);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().isEmpty());
	}

	@Test
	public void testNoErrorForEnvMatchesOnBothReqAndBio() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("v1");
		authRequestDTO.setIndividualId("12345");
		authRequestDTO.setIndividualIdType("UIN");
		authRequestDTO.setVersion("v1");
		authRequestDTO.setEnv("Staging");
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(true);
		RequestDTO request = new RequestDTO();

		List<BioIdentityInfoDTO> biometrics = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityDto = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("");
		digitalId.setMake("");
		digitalId.setModel("");
		digitalId.setDeviceProvider("");
		digitalId.setDeviceProviderId("");
		data.setDigitalId(digitalId);
		data.setBioValue("adsadas");
		data.setBioType("Face");
		data.setEnv("Staging");
		bioIdentityDto.setData(data);
		biometrics.add(bioIdentityDto);
		request.setBiometrics(biometrics);

		authRequestDTO.setRequestedAuth(authType);
		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequest(request);
		String timestamp = Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString();
		authRequestDTO.setRequestTime(timestamp);
		authRequestDTO.setTransactionID("1234567890");
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		request.setTimestamp(timestamp);
		
		data.setTimestamp(timestamp);
		digitalId.setDateTime(timestamp);
		
		authRequestDTO.setRequest(request);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().isEmpty());
	}

}
