package io.mosip.authentication.common.service.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.exception.IdAuthExceptionHandler;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.integration.MasterDataManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.hotlist.dto.HotlistDTO;
import io.mosip.authentication.core.indauth.dto.ActionableAuthError;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.indauth.dto.DigitalId;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.IdentityDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.spi.hotlist.service.HotlistService;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.util.DataValidationUtil;
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
@Import(EnvUtil.class)
public class AuthRequestValidatorTest {

	@Mock
	private SpringValidatorAdapter validator;

	@Mock
	private PinValidatorImpl pinValidator;

	@Mock
	Errors error;

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
	public void testFingerprintcount() {
		int afc = 10;
		int fc = authRequestValidator.getMaxFingerCount();
		assertSame(afc, fc);
	}

	@Test
	public void testValidEmpty() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(null, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testValidUin() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();

		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setEnv("Staging");
		authRequestDTO.setDomainUri("https://dev.mosip.net");
		authRequestDTO.setVersion("1.1");
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(EnvUtil.getOptionalLanguages().split(",")[0]);
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(EnvUtil.getOptionalLanguages().split(",")[0]);
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
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequest(reqDTO);
		authRequestDTO.setTransactionID("1234567890");
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		List<String> value = new ArrayList<>();
		value.add("dateOfBirth");
		Mockito.when(idinfoHelper.getIdMappingValue(Mockito.any(), Mockito.any())).thenReturn(value);
		Mockito.when(idInfoFetcher.getSystemSupportedLanguageCodes()).thenReturn(List.of("eng", "fra", "ara"));
		authRequestValidator.validate(authRequestDTO, errors);
		System.err.println(errors.getAllErrors());
		assertFalse(errors.hasErrors());
	}

	@Test
	public void testInvalidUin() throws IdAuthenticationBusinessException {
		Mockito.when(idValidator.validateUIN(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("1.1");
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(EnvUtil.getOptionalLanguages().split(",")[0].split(",")[0]);
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(EnvUtil.getOptionalLanguages().split(",")[0]);
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
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setRequest(reqDTO);
		List<String> value = new ArrayList<>();
		value.add("dateOfBirth");
		Mockito.when(idinfoHelper.getIdMappingValue(Mockito.any(), Mockito.any())).thenReturn(value);
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testValidVid() throws IdAuthenticationBusinessException {
		Mockito.when(idValidator.validateUIN(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("1.1");
		authRequestDTO.setTransactionID("1234567890");
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(EnvUtil.getOptionalLanguages().split(",")[0]);
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(EnvUtil.getOptionalLanguages().split(",")[0]);
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.VID.getType());
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		reqDTO.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequest(reqDTO);
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		List<String> value = new ArrayList<>();
		value.add("dateOfBirth");
		Mockito.when(idinfoHelper.getIdMappingValue(Mockito.any(), Mockito.any())).thenReturn(value);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInvalidVid() throws IdAuthenticationBusinessException {
		Mockito.when(idValidator.validateVID(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("1.1");
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(EnvUtil.getMandatoryLanguages());
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
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInvalidTimestamp3() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateRequestTimedOut",
				Instant.now().minus(2, ChronoUnit.DAYS).atOffset(ZoneOffset.of("+0530"))
						.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString(),
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
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("1.1");
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(EnvUtil.getMandatoryLanguages());
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
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("1.1");
		authRequestDTO.setTransactionID("1234567890");
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(EnvUtil.getMandatoryLanguages());
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
		authRequestDTO.setRequest(reqDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testValidRequest() throws IdAuthenticationBusinessException {
		Mockito.when(idValidator.validateUIN(Mockito.anyString())).thenReturn(Boolean.TRUE);
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("1.0");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		// name
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		// dob
		IdentityInfoDTO idInfoDTO2 = new IdentityInfoDTO();
		idInfoDTO2.setLanguage(null);
		idInfoDTO2.setValue(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
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
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequest(reqDTO);
		List<String> value = new ArrayList<>();
		value.add("dateOfBirth");
		Mockito.when(idinfoHelper.getIdMappingValue(Mockito.any(), Mockito.any())).thenReturn(value);
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInValidRequest2() throws IdAuthenticationBusinessException {
		Mockito.when(idValidator.validateUIN(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		// name
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTO1.setValue("Mike");
		IdentityInfoDTO idInfoDTO6 = new IdentityInfoDTO();
		idInfoDTO6.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTO6.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		idInfoList.add(idInfoDTO6);
		// dob
		IdentityInfoDTO idInfoDTO2 = new IdentityInfoDTO();
		idInfoDTO2.setLanguage(null);
		idInfoDTO2.setValue(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
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
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		// name
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage("EN");
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(EnvUtil.getMandatoryLanguages());
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
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
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
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		// name
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTO1.setValue("Mike");
		IdentityInfoDTO idInfoDTO6 = new IdentityInfoDTO();
		idInfoDTO6.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTO6.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		idInfoList.add(idInfoDTO6);
		// dob
		IdentityInfoDTO idInfoDTO2 = new IdentityInfoDTO();
		idInfoDTO2.setLanguage(null);
		idInfoDTO2.setValue(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
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
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		// name
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTO.setValue(null);
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(EnvUtil.getMandatoryLanguages());
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
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
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
		idInfoDTO5.setLanguage(EnvUtil.getMandatoryLanguages());
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
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		// name
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTO.setValue(null);
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(EnvUtil.getMandatoryLanguages());
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
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
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
		idInfoDTO5.setLanguage(EnvUtil.getMandatoryLanguages());
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
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		// name
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTO.setValue(null);
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(EnvUtil.getMandatoryLanguages());
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
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
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
		idInfoDTO5.setLanguage(EnvUtil.getMandatoryLanguages());
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
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		// name
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTO.setValue(null);
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(EnvUtil.getMandatoryLanguages());
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
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
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
		idInfoDTO5.setLanguage(EnvUtil.getMandatoryLanguages());
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
		reqDTO.setOtp(pin);
		reqDTO.setDemographics(idDTO);
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
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		// name
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTO.setValue(null);
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(EnvUtil.getMandatoryLanguages());
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
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
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
		idInfoDTO5.setLanguage(EnvUtil.getMandatoryLanguages());
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
		String pin = "123456";
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setOtp(pin);
		reqDTO.setDemographics(idDTO);
		reqDTO.setDemographics(idDTO);
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
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		// name
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTO.setValue(null);
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(EnvUtil.getMandatoryLanguages());
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
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
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
		idInfoDTO5.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTO5.setValue(null);
		List<IdentityInfoDTO> idInfoList4 = new ArrayList<>();
		idInfoList4.add(idInfoDTO5);

		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("1990/11/25");
		idDTO.setDobType(idInfoList2);
		idDTO.setGender(idInfoList4);
		idDTO.setAge("252");

		RequestDTO reqDTO = new RequestDTO();
		String otp = "";
		reqDTO.setOtp(otp);
		reqDTO.setDemographics(idDTO);
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
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger = new DataDTO();
		dataDTOFinger.setBioValue("finger");
		dataDTOFinger.setBioSubType("Thumb");
		dataDTOFinger.setBioType(BioAuthType.FGR_IMG.getType());
		fingerValue.setData(dataDTOFinger);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		DataDTO dataDTOIris = new DataDTO();
		dataDTOIris.setBioValue("iris img");
		dataDTOIris.setBioSubType("left");
		dataDTOIris.setBioType(BioAuthType.IRIS_IMG.getType());
		irisValue.setData(dataDTOIris);
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFace = new DataDTO();
		dataDTOFace.setBioValue("face img");
		dataDTOFace.setBioSubType("Thumb");
		dataDTOFace.setBioType(BioAuthType.FACE_IMG.getType());
		faceValue.setData(dataDTOFace);
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);

		RequestDTO request = new RequestDTO();
		request.setOtp("123456");
		request.setTimestamp("1");
		request.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(request);
		return authRequestDTO;
	}

	@Test
	public void testInValidAuthType() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setTransactionID("1234567890");
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
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
		RequestDTO request = new RequestDTO();
		request.setOtp("111111");
		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequest(request);
		String timestamp = Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
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
		RequestDTO request = new RequestDTO();

		List<BioIdentityInfoDTO> biometrics = new ArrayList<>();

		BioIdentityInfoDTO bioIdentityDto1 = new BioIdentityInfoDTO();
		DataDTO data1 = new DataDTO();
		data1.setBioValue("adsadas");
		data1.setBioType("Face");
		bioIdentityDto1.setData(data1);
		biometrics.add(bioIdentityDto1);
		request.setBiometrics(biometrics);

		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequest(request);
		String timestamp = Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		authRequestDTO.setRequestTime(timestamp);
		authRequestDTO.setTransactionID("1234567890");
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		request.setTimestamp(timestamp);
		authRequestDTO.setRequest(request);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(!errors.getAllErrors().isEmpty() && errors.getAllErrors().stream().anyMatch(
				err -> err.getCode().equals(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode())));
	}

	@Test
	public void testErrorForDomainUriMissingInAuthReq() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("v1");
		authRequestDTO.setIndividualId("12345");
		authRequestDTO.setIndividualIdType("UIN");
		authRequestDTO.setVersion("v1");
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

		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequest(request);
		String timestamp = Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		authRequestDTO.setRequestTime(timestamp);
		authRequestDTO.setTransactionID("1234567890");
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		request.setTimestamp(timestamp);
		authRequestDTO.setRequest(request);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(!errors.getAllErrors().isEmpty() && errors.getAllErrors().stream().anyMatch(
				err -> err.getCode().equals(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode())));
	}
	
	@Test
	public void testErrorForDomainUriValidInAuthReq_withoutBio() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("v1");
		authRequestDTO.setIndividualId("12345");
		authRequestDTO.setIndividualIdType("UIN");
		authRequestDTO.setVersion("v1");
		authRequestDTO.setEnv("Staging");
		RequestDTO request = new RequestDTO();
		
		request.setOtp("111111");


		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequest(request);
		String timestamp = Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		authRequestDTO.setRequestTime(timestamp);
		authRequestDTO.setTransactionID("1234567890");
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		request.setTimestamp(timestamp);
		authRequestDTO.setRequest(request);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().isEmpty());
	}

	@Test
	public void testNoErrorForDomainUriNullOnBothReqAndBio() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("v1");
		authRequestDTO.setIndividualId("12345");
		authRequestDTO.setIndividualIdType("UIN");
		authRequestDTO.setVersion("v1");
		authRequestDTO.setEnv("Staging");
		// authRequestDTO.setDomainUri("localhost1");
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
		data.setTransactionId("1234567890");
		data.setEnv("Staging");
		// data.setDomainUri("localhost2");
		bioIdentityDto.setData(data);
		biometrics.add(bioIdentityDto);
		request.setBiometrics(biometrics);

		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequest(request);
		String timestamp = Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
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
		data.setTransactionId("1234567890");
		bioIdentityDto.setData(data);
		biometrics.add(bioIdentityDto);
		request.setBiometrics(biometrics);

		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequest(request);
		String timestamp = Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
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
		authRequestDTO.setDomainUri("https://dev.mosip.net");
		RequestDTO request = new RequestDTO();

		List<BioIdentityInfoDTO> biometrics = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityDto = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		data.setBioValue("adsadas");
		data.setBioType("Face");
		bioIdentityDto.setData(data);
		biometrics.add(bioIdentityDto);
		request.setBiometrics(biometrics);

		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequest(request);
		String timestamp = Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		authRequestDTO.setRequestTime(timestamp);
		authRequestDTO.setTransactionID("1234567890");
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		request.setTimestamp(timestamp);
		authRequestDTO.setRequest(request);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(!errors.getAllErrors().isEmpty() && errors.getAllErrors().stream()
				.anyMatch(err -> err.getCode().equals(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode())));
	}
	
	@Test
	public void testErrorForEmptyEnvInBioData() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("v1");
		authRequestDTO.setIndividualId("12345");
		authRequestDTO.setIndividualIdType("UIN");
		authRequestDTO.setVersion("v1");
		authRequestDTO.setDomainUri("https://dev.mosip.net");
		authRequestDTO.setEnv("Staging");
		RequestDTO request = new RequestDTO();

		List<BioIdentityInfoDTO> biometrics = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityDto = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		data.setBioValue("adsadas");
		data.setBioType("Face");
		data.setDomainUri("https://dev.mosip.net");
		bioIdentityDto.setData(data);
		biometrics.add(bioIdentityDto);
		request.setBiometrics(biometrics);

		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequest(request);
		String timestamp = Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		authRequestDTO.setRequestTime(timestamp);
		authRequestDTO.setTransactionID("1234567890");
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		request.setTimestamp(timestamp);
		authRequestDTO.setRequest(request);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(!errors.getAllErrors().isEmpty() && errors.getAllErrors().stream()
				.anyMatch(err -> err.getCode().equals(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode())));
	}
	
	@Test
	public void testErrorForEmptyEnvInReq() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("v1");
		authRequestDTO.setIndividualId("12345");
		authRequestDTO.setIndividualIdType("UIN");
		authRequestDTO.setVersion("v1");
		authRequestDTO.setDomainUri("https://dev.mosip.net");
		RequestDTO request = new RequestDTO();

		request.setOtp("111111");

		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequest(request);
		String timestamp = Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		authRequestDTO.setRequestTime(timestamp);
		authRequestDTO.setTransactionID("1234567890");
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		request.setTimestamp(timestamp);
		authRequestDTO.setRequest(request);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().isEmpty());
	}

	@Test
	public void testErrorForEnvMissingInAuthReq() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("v1");
		authRequestDTO.setIndividualId("12345");
		authRequestDTO.setIndividualIdType("UIN");
		authRequestDTO.setVersion("v1");
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

		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequest(request);
		String timestamp = Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		authRequestDTO.setRequestTime(timestamp);
		authRequestDTO.setTransactionID("1234567890");
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		request.setTimestamp(timestamp);
		authRequestDTO.setRequest(request);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(!errors.getAllErrors().isEmpty() && errors.getAllErrors().stream()
				.noneMatch(err -> err.getCode().equals(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode())));
	}
	
	@Test
	public void testErrorForEnvValidInAuthReq_withoutBio() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("v1");
		authRequestDTO.setIndividualId("12345");
		authRequestDTO.setIndividualIdType("UIN");
		authRequestDTO.setDomainUri("https://dev.mosip.net");
		authRequestDTO.setVersion("v1");
		RequestDTO request = new RequestDTO();
		request.setOtp("111111");

		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequest(request);
		String timestamp = Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		authRequestDTO.setRequestTime(timestamp);
		authRequestDTO.setTransactionID("1234567890");
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		request.setTimestamp(timestamp);
		authRequestDTO.setRequest(request);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().isEmpty());
	}
	
	@Test
	public void testErrorForDomainUriNotMatchingBetweenReqAndBio() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("v1");
		authRequestDTO.setIndividualId("12345");
		authRequestDTO.setIndividualIdType("UIN");
		authRequestDTO.setVersion("v1");
		authRequestDTO.setEnv("Staging");
		authRequestDTO.setDomainUri("domain1");
		RequestDTO request = new RequestDTO();

		List<BioIdentityInfoDTO> biometrics = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityDto = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		data.setBioValue("adsadas");
		data.setBioType("Face");
		data.setEnv("Staging");
		data.setDomainUri("domain2");
		bioIdentityDto.setData(data);
		biometrics.add(bioIdentityDto);
		request.setBiometrics(biometrics);

		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequest(request);
		String timestamp = Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
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

		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequest(request);
		String timestamp = Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
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
		authRequestDTO.setDomainUri("https://dev.mosip.net");
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
		data.setTransactionId("1234567890");
		// data.setEnv("Staging");
		bioIdentityDto.setData(data);
		biometrics.add(bioIdentityDto);
		request.setBiometrics(biometrics);

		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequest(request);
		String timestamp = Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
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
		data.setTransactionId("1234567890");
		bioIdentityDto.setData(data);
		biometrics.add(bioIdentityDto);
		request.setBiometrics(biometrics);

		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setRequest(request);
		String timestamp = Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
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

	@Test
	public void TestValidateSuccessiveBioSegmentTimestamp_emptyBiometricsList() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateSuccessiveBioSegmentTimestamp", List.of(),
				errors, 0, null);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void TestValidateSuccessiveBioSegmentTimestamp_singleBio_null_timestamp() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		List<BioIdentityInfoDTO> bioIds = List.of(new BioIdentityInfoDTO());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateSuccessiveBioSegmentTimestamp", bioIds, errors,
				0, null);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void TestValidateSuccessiveBioSegmentTimestamp_singleBio_non_null_timestamp() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		String timestamp = Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		data.setTimestamp(timestamp);
		bioIdentityInfoDTO.setData(data);
		List<BioIdentityInfoDTO> bioIds = List.of(bioIdentityInfoDTO);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateSuccessiveBioSegmentTimestamp", bioIds, errors,
				0, null);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void TestValidateSuccessiveBioSegmentTimestamp_multiBio_same_timestamp_index0() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();

		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		String timestamp = Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		data.setTimestamp(timestamp);
		bioIdentityInfoDTO.setData(data);

		BioIdentityInfoDTO bioIdentityInfoDTO1 = new BioIdentityInfoDTO();
		DataDTO data1 = new DataDTO();
		String timestamp1 = Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		data1.setTimestamp(timestamp1);
		bioIdentityInfoDTO1.setData(data1);

		List<BioIdentityInfoDTO> bioIds = List.of(bioIdentityInfoDTO, bioIdentityInfoDTO1);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateSuccessiveBioSegmentTimestamp", bioIds, errors,
				0, null);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void TestValidateSuccessiveBioSegmentTimestamp_multiBio_same_timestamp_index1() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();

		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		DigitalId digId = new DigitalId();
		data.setDigitalId(digId);
		OffsetDateTime offsetDateTime = Instant.now().atOffset(ZoneOffset.of("+0530"));
		String timestamp = offsetDateTime // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		data.setTimestamp(timestamp);
		digId.setDateTime(timestamp);
		bioIdentityInfoDTO.setData(data);

		BioIdentityInfoDTO bioIdentityInfoDTO1 = new BioIdentityInfoDTO();
		DataDTO data1 = new DataDTO();
		DigitalId digId1 = new DigitalId();
		data1.setDigitalId(digId1);
		String timestamp1 = offsetDateTime.plus(0, ChronoUnit.SECONDS) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		data1.setTimestamp(timestamp1);
		digId1.setDateTime(timestamp);
		bioIdentityInfoDTO1.setData(data1);

		List<BioIdentityInfoDTO> bioIds = List.of(bioIdentityInfoDTO, bioIdentityInfoDTO1);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		int index = 1;
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateSuccessiveBioSegmentTimestamp", bioIds, errors,
				index, bioIds.get(index));
		assertFalse(errors.hasErrors());
	}

	@Test
	public void TestValidateSuccessiveBioSegmentTimestamp_multiBio_min_timediff_index1() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();

		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		DigitalId digId = new DigitalId();
		data.setDigitalId(digId);
		OffsetDateTime offsetDateTime = Instant.now().atOffset(ZoneOffset.of("+0530"));
		String timestamp = offsetDateTime // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		data.setTimestamp(timestamp);
		digId.setDateTime(timestamp);
		bioIdentityInfoDTO.setData(data);

		BioIdentityInfoDTO bioIdentityInfoDTO1 = new BioIdentityInfoDTO();
		DataDTO data1 = new DataDTO();
		DigitalId digId1 = new DigitalId();
		data1.setDigitalId(digId1);
		String timestamp1 = offsetDateTime.plus(1, ChronoUnit.SECONDS) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		data1.setTimestamp(timestamp1);
		digId1.setDateTime(timestamp);
		bioIdentityInfoDTO1.setData(data1);

		List<BioIdentityInfoDTO> bioIds = List.of(bioIdentityInfoDTO, bioIdentityInfoDTO1);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		int index = 1;
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateSuccessiveBioSegmentTimestamp", bioIds, errors,
				index, bioIds.get(index));
		assertFalse(errors.hasErrors());
	}

	@Test
	public void TestValidateSuccessiveBioSegmentTimestamp_multiBio_max_timediff_index1() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();

		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		DigitalId digId = new DigitalId();
		data.setDigitalId(digId);
		OffsetDateTime offsetDateTime = Instant.now().atOffset(ZoneOffset.of("+0530"));
		String timestamp = offsetDateTime // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		data.setTimestamp(timestamp);
		digId.setDateTime(timestamp);
		bioIdentityInfoDTO.setData(data);

		BioIdentityInfoDTO bioIdentityInfoDTO1 = new BioIdentityInfoDTO();
		DataDTO data1 = new DataDTO();
		DigitalId digId1 = new DigitalId();
		data1.setDigitalId(digId1);
		String timestamp1 = offsetDateTime.plus(EnvUtil.getBioSegmentTimeDiffAllowed(), ChronoUnit.SECONDS) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		data1.setTimestamp(timestamp1);
		digId1.setDateTime(timestamp);
		bioIdentityInfoDTO1.setData(data1);

		List<BioIdentityInfoDTO> bioIds = List.of(bioIdentityInfoDTO, bioIdentityInfoDTO1);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		int index = 1;
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateSuccessiveBioSegmentTimestamp", bioIds, errors,
				index, bioIds.get(index));
		assertFalse(errors.hasErrors());
	}

	@Test
	public void TestValidateSuccessiveBioSegmentTimestamp_multiBio_morethan_allowed_timediff_index1() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();

		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		DigitalId digId = new DigitalId();
		data.setDigitalId(digId);
		OffsetDateTime offsetDateTime = Instant.now().atOffset(ZoneOffset.of("+0530"));
		String timestamp = offsetDateTime // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		data.setTimestamp(timestamp);
		digId.setDateTime(timestamp);
		bioIdentityInfoDTO.setData(data);

		BioIdentityInfoDTO bioIdentityInfoDTO1 = new BioIdentityInfoDTO();
		DataDTO data1 = new DataDTO();
		DigitalId digId1 = new DigitalId();
		data1.setDigitalId(digId1);
		Long maxAllowedTimeDiff = EnvUtil.getBioSegmentTimeDiffAllowed();
		String timestamp1 = offsetDateTime.plus(maxAllowedTimeDiff + 1, ChronoUnit.SECONDS) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		data1.setTimestamp(timestamp1);
		digId1.setDateTime(timestamp);
		bioIdentityInfoDTO1.setData(data1);

		List<BioIdentityInfoDTO> bioIds = List.of(bioIdentityInfoDTO, bioIdentityInfoDTO1);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		int index = 1;
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateSuccessiveBioSegmentTimestamp", bioIds, errors,
				index, bioIds.get(index));
		assertTrue(errors.hasErrors());
		assertTrue(((FieldError) errors.getAllErrors().get(0)).getCode()
				.equals(IdAuthenticationErrorConstants.INVALID_BIO_TIMESTAMP.getErrorCode()));
		try {
			DataValidationUtil.validate(errors);
		} catch (IDDataValidationException e) {
			HttpServletRequest mockReq = Mockito.mock(HttpServletRequest.class);
			Mockito.when(mockReq.getRequestURL()).thenReturn(new StringBuffer("/test"));
			AuthResponseDTO resp = (AuthResponseDTO) IdAuthExceptionHandler.buildExceptionResponse(e, mockReq);
			assertEquals(resp.getErrors().get(0).getErrorMessage(), String.format(
					IdAuthenticationErrorConstants.INVALID_BIO_TIMESTAMP.getErrorMessage(), "" + maxAllowedTimeDiff));
			assertEquals(((ActionableAuthError) resp.getErrors().get(0)).getActionMessage(), String.format(
					IdAuthenticationErrorConstants.INVALID_BIO_TIMESTAMP.getActionMessage(), "" + maxAllowedTimeDiff));
		}
	}

	@Test
	public void TestValidateSuccessiveBioSegmentTimestamp_multiBio_negative_timediff_index1() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();

		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		DigitalId digId = new DigitalId();
		data.setDigitalId(digId);
		OffsetDateTime offsetDateTime = Instant.now().atOffset(ZoneOffset.of("+0530"));
		String timestamp = offsetDateTime // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		data.setTimestamp(timestamp);
		digId.setDateTime(timestamp);
		bioIdentityInfoDTO.setData(data);

		BioIdentityInfoDTO bioIdentityInfoDTO1 = new BioIdentityInfoDTO();
		DataDTO data1 = new DataDTO();
		DigitalId digId1 = new DigitalId();
		data1.setDigitalId(digId1);
		String timestamp1 = offsetDateTime.minus(1, ChronoUnit.SECONDS) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		data1.setTimestamp(timestamp1);
		digId1.setDateTime(timestamp);
		bioIdentityInfoDTO1.setData(data1);

		Long maxAllowedTimeDiff = EnvUtil.getBioSegmentTimeDiffAllowed();

		List<BioIdentityInfoDTO> bioIds = List.of(bioIdentityInfoDTO, bioIdentityInfoDTO1);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		int index = 1;
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateSuccessiveBioSegmentTimestamp", bioIds, errors,
				index, bioIds.get(index));
		assertTrue(errors.hasErrors());
		assertTrue(((FieldError) errors.getAllErrors().get(0)).getCode()
				.equals(IdAuthenticationErrorConstants.INVALID_BIO_TIMESTAMP.getErrorCode()));
		try {
			DataValidationUtil.validate(errors);
		} catch (IDDataValidationException e) {
			HttpServletRequest mockReq = Mockito.mock(HttpServletRequest.class);
			Mockito.when(mockReq.getRequestURL()).thenReturn(new StringBuffer("/test"));
			AuthResponseDTO resp = (AuthResponseDTO) IdAuthExceptionHandler.buildExceptionResponse(e, mockReq);
			assertEquals(resp.getErrors().get(0).getErrorMessage(), String.format(
					IdAuthenticationErrorConstants.INVALID_BIO_TIMESTAMP.getErrorMessage(), "" + maxAllowedTimeDiff));
			assertEquals(((ActionableAuthError) resp.getErrors().get(0)).getActionMessage(), String.format(
					IdAuthenticationErrorConstants.INVALID_BIO_TIMESTAMP.getActionMessage(), "" + maxAllowedTimeDiff));
		}
	}

	@Test
	public void TestValidateSuccessiveDigitalIdTimestamp_multiBio_same_timestamp_index1() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();

		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		DigitalId digId = new DigitalId();
		data.setDigitalId(digId);
		OffsetDateTime offsetDateTime = Instant.now().atOffset(ZoneOffset.of("+0530"));
		String timestamp = offsetDateTime // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		data.setTimestamp(timestamp);
		digId.setDateTime(timestamp);
		bioIdentityInfoDTO.setData(data);

		BioIdentityInfoDTO bioIdentityInfoDTO1 = new BioIdentityInfoDTO();
		DataDTO data1 = new DataDTO();
		DigitalId digId1 = new DigitalId();
		data1.setDigitalId(digId1);
		String timestamp1 = offsetDateTime.plus(0, ChronoUnit.SECONDS) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		data1.setTimestamp(timestamp);
		digId1.setDateTime(timestamp1);
		bioIdentityInfoDTO1.setData(data1);

		Long maxAllowedTimeDiff = EnvUtil.getBioSegmentTimeDiffAllowed();

		List<BioIdentityInfoDTO> bioIds = List.of(bioIdentityInfoDTO, bioIdentityInfoDTO1);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		int index = 1;
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateSuccessiveDigitalIdTimestamp", bioIds, errors,
				index, bioIds.get(index), maxAllowedTimeDiff);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void TestValidateSuccessiveDigitalIdTimestamp_multiBio_min_timediff_index1() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();

		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		DigitalId digId = new DigitalId();
		data.setDigitalId(digId);
		OffsetDateTime offsetDateTime = Instant.now().atOffset(ZoneOffset.of("+0530"));
		String timestamp = offsetDateTime // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		data.setTimestamp(timestamp);
		digId.setDateTime(timestamp);
		bioIdentityInfoDTO.setData(data);

		BioIdentityInfoDTO bioIdentityInfoDTO1 = new BioIdentityInfoDTO();
		DataDTO data1 = new DataDTO();
		DigitalId digId1 = new DigitalId();
		data1.setDigitalId(digId1);
		String timestamp1 = offsetDateTime.plus(1, ChronoUnit.SECONDS) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		data1.setTimestamp(timestamp);
		digId1.setDateTime(timestamp1);
		bioIdentityInfoDTO1.setData(data1);

		Long maxAllowedTimeDiff = EnvUtil.getBioSegmentTimeDiffAllowed();

		List<BioIdentityInfoDTO> bioIds = List.of(bioIdentityInfoDTO, bioIdentityInfoDTO1);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		int index = 1;
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateSuccessiveDigitalIdTimestamp", bioIds, errors,
				index, bioIds.get(index), maxAllowedTimeDiff);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void TestValidateSuccessiveDigitalIdTimestamp_multiBio_max_timediff_index1() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();

		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		DigitalId digId = new DigitalId();
		data.setDigitalId(digId);
		OffsetDateTime offsetDateTime = Instant.now().atOffset(ZoneOffset.of("+0530"));
		String timestamp = offsetDateTime // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		data.setTimestamp(timestamp);
		digId.setDateTime(timestamp);
		bioIdentityInfoDTO.setData(data);

		BioIdentityInfoDTO bioIdentityInfoDTO1 = new BioIdentityInfoDTO();
		DataDTO data1 = new DataDTO();
		DigitalId digId1 = new DigitalId();
		data1.setDigitalId(digId1);
		String timestamp1 = offsetDateTime.plus(EnvUtil.getBioSegmentTimeDiffAllowed(), ChronoUnit.SECONDS) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		data1.setTimestamp(timestamp);
		digId1.setDateTime(timestamp1);
		bioIdentityInfoDTO1.setData(data1);

		List<BioIdentityInfoDTO> bioIds = List.of(bioIdentityInfoDTO, bioIdentityInfoDTO1);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		int index = 1;
		Long maxAllowedTimeDiff = EnvUtil.getBioSegmentTimeDiffAllowed();
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateSuccessiveDigitalIdTimestamp", bioIds, errors,
				index, bioIds.get(index), maxAllowedTimeDiff);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void TestValidateSuccessiveDigitalIdTimestamp_multiBio_morethan_allowed_timediff_index1() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();

		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		DigitalId digId = new DigitalId();
		data.setDigitalId(digId);
		OffsetDateTime offsetDateTime = Instant.now().atOffset(ZoneOffset.of("+0530"));
		String timestamp = offsetDateTime // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		data.setTimestamp(timestamp);
		digId.setDateTime(timestamp);
		bioIdentityInfoDTO.setData(data);

		BioIdentityInfoDTO bioIdentityInfoDTO1 = new BioIdentityInfoDTO();
		DataDTO data1 = new DataDTO();
		DigitalId digId1 = new DigitalId();
		data1.setDigitalId(digId1);
		Long maxAllowedTimeDiff = EnvUtil.getBioSegmentTimeDiffAllowed();
		String timestamp1 = offsetDateTime.plus(maxAllowedTimeDiff + 1, ChronoUnit.SECONDS) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		data1.setTimestamp(timestamp);
		digId1.setDateTime(timestamp1);
		bioIdentityInfoDTO1.setData(data1);

		List<BioIdentityInfoDTO> bioIds = List.of(bioIdentityInfoDTO, bioIdentityInfoDTO1);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		int index = 1;
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateSuccessiveDigitalIdTimestamp", bioIds, errors,
				index, bioIds.get(index), maxAllowedTimeDiff);
		assertTrue(errors.hasErrors());
		assertTrue(((FieldError) errors.getAllErrors().get(0)).getCode()
				.equals(IdAuthenticationErrorConstants.INVALID_BIO_DIGITALID_TIMESTAMP.getErrorCode()));
		try {
			DataValidationUtil.validate(errors);
		} catch (IDDataValidationException e) {
			HttpServletRequest mockReq = Mockito.mock(HttpServletRequest.class);
			Mockito.when(mockReq.getRequestURL()).thenReturn(new StringBuffer("/test"));
			AuthResponseDTO resp = (AuthResponseDTO) IdAuthExceptionHandler.buildExceptionResponse(e, mockReq);
			assertEquals(resp.getErrors().get(0).getErrorMessage(),
					String.format(IdAuthenticationErrorConstants.INVALID_BIO_DIGITALID_TIMESTAMP.getErrorMessage(),
							"" + maxAllowedTimeDiff));
			assertEquals(((ActionableAuthError) resp.getErrors().get(0)).getActionMessage(),
					String.format(IdAuthenticationErrorConstants.INVALID_BIO_DIGITALID_TIMESTAMP.getActionMessage(),
							"" + maxAllowedTimeDiff));
		}
	}

	@Test
	public void TestValidateSuccessiveDigitalIdTimestamp_multiBio_negative_timediff_index1() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();

		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		DigitalId digId = new DigitalId();
		data.setDigitalId(digId);
		OffsetDateTime offsetDateTime = Instant.now().atOffset(ZoneOffset.of("+0530"));
		String timestamp = offsetDateTime // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		data.setTimestamp(timestamp);
		digId.setDateTime(timestamp);
		bioIdentityInfoDTO.setData(data);

		BioIdentityInfoDTO bioIdentityInfoDTO1 = new BioIdentityInfoDTO();
		DataDTO data1 = new DataDTO();
		DigitalId digId1 = new DigitalId();
		data1.setDigitalId(digId1);
		String timestamp1 = offsetDateTime.minus(1, ChronoUnit.SECONDS) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		data1.setTimestamp(timestamp);
		digId1.setDateTime(timestamp1);
		bioIdentityInfoDTO1.setData(data1);

		Long maxAllowedTimeDiff = EnvUtil.getBioSegmentTimeDiffAllowed();

		List<BioIdentityInfoDTO> bioIds = List.of(bioIdentityInfoDTO, bioIdentityInfoDTO1);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		int index = 1;
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateSuccessiveDigitalIdTimestamp", bioIds, errors,
				index, bioIds.get(index), maxAllowedTimeDiff);
		assertTrue(errors.hasErrors());
		assertTrue(((FieldError) errors.getAllErrors().get(0)).getCode()
				.equals(IdAuthenticationErrorConstants.INVALID_BIO_DIGITALID_TIMESTAMP.getErrorCode()));
		try {
			DataValidationUtil.validate(errors);
		} catch (IDDataValidationException e) {
			HttpServletRequest mockReq = Mockito.mock(HttpServletRequest.class);
			Mockito.when(mockReq.getRequestURL()).thenReturn(new StringBuffer("/test"));
			AuthResponseDTO resp = (AuthResponseDTO) IdAuthExceptionHandler.buildExceptionResponse(e, mockReq);
			assertEquals(resp.getErrors().get(0).getErrorMessage(),
					String.format(IdAuthenticationErrorConstants.INVALID_BIO_DIGITALID_TIMESTAMP.getErrorMessage(),
							"" + maxAllowedTimeDiff));
			assertEquals(((ActionableAuthError) resp.getErrors().get(0)).getActionMessage(),
					String.format(IdAuthenticationErrorConstants.INVALID_BIO_DIGITALID_TIMESTAMP.getActionMessage(),
							"" + maxAllowedTimeDiff));
		}
	}

	@Test
	public void testValidateBiometrics() {
		List<BioIdentityInfoDTO> biometrics = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityDto = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		bioIdentityDto.setData(data);
		biometrics.add(bioIdentityDto);
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validateBiometrics(biometrics, "abpnx", errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testvalidateBioTxnIdEmptyBioTxnId() {
		String BioTxnId = "";
		String authTxnId = "Random";
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		int index = 100;
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateBioTxnId", authTxnId, errors, index, BioTxnId);
		assertTrue(errors.hasErrors());

	}

	@Test
	public void testvalidateBioTxnId() throws IdAuthenticationBusinessException {
		String BioTxnId = "Something";
		String authTxnId = "Random";
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		int index = 100;
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateBioTxnId", authTxnId, errors, index, BioTxnId);
		assertTrue(errors.hasErrors());

	}

	@Test
	public void testnullCheckOnBioTimestampAndDigitalIdTimestamp() {
		DataDTO data = new DataDTO();
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		ReflectionTestUtils.invokeMethod(authRequestValidator, "nullCheckOnBioTimestampAndDigitalIdTimestamp", errors,
				101, data, "param");
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testnullCheckDigitalIdAndTimestamp() {
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("");
		digitalId.setMake("");
		digitalId.setModel("");
		digitalId.setDeviceProvider("");
		digitalId.setDeviceProviderId("");
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		Boolean flag = authRequestValidator.nullCheckDigitalIdAndTimestamp(digitalId, errors, "param");
		assertFalse(flag);
	}

	@Test
	public void testnullCheckDigitalIdAndTimestampNull() {
		DigitalId digitalId = new DigitalId();
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		Boolean flag = authRequestValidator.nullCheckDigitalIdAndTimestamp(null, errors, "param");
		assertFalse(flag);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void validateDeviceDetailsTest() {
		authRequestValidator.validateDeviceDetails(getAuthRequestDto(), error);
		assertFalse(error.hasErrors());
	}

}
