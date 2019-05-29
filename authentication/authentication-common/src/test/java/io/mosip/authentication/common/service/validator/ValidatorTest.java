package io.mosip.authentication.common.service.validator;

import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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

import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.match.BioMatchType;
import io.mosip.authentication.common.service.impl.match.DemoMatchType;
import io.mosip.authentication.common.service.integration.MasterDataManager;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthTypeDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.indauth.dto.IdentityDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.kernel.core.idobjectvalidator.spi.IdObjectValidator;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.mosip.kernel.idvalidator.vid.impl.VidValidatorImpl;
import io.mosip.kernel.logger.logback.appender.RollingFileAppender;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class ValidatorTest {

	@Mock
	private SpringValidatorAdapter validator;

	@Mock
	Errors error;

	@Autowired
	Environment env;

	@Mock
	UinValidatorImpl uinValidator;

	@Mock
	VidValidatorImpl vidValidator;

	@InjectMocks
	RollingFileAppender idaRollingFileAppender;

	@InjectMocks
	private AuthRequestValidator authRequestValidator;

	@Mock
	IdInfoHelper idInfoHelper;

	@Mock
	private MasterDataManager masterDataManager;

	@Mock
	private IDAMappingConfig idMappingConfig;

	@Mock
	private IdInfoFetcher idInfoFetcher;
	
	@Mock
	private IdObjectValidator IdObjectValidator;

	@Before
	public void before() {
		ReflectionTestUtils.setField(authRequestValidator, "env", env);
		ReflectionTestUtils.setField(authRequestValidator, "uinValidator", uinValidator);
		ReflectionTestUtils.setField(authRequestValidator, "vidValidator", vidValidator);
	}

	@Test
	public void validateIDNull() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		authRequestDTO.setId(null);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-006")));
	}

	@Test
	public void validateIDEmpty() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		authRequestDTO.setId("");
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-006")));
	}

	@Test
	public void validateIdvIdNull() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		authRequestDTO.setIndividualId(null);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-006")));
	}

	@Test
	public void validateIdvIdEmpty() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		authRequestDTO.setIndividualId("");
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-006")));
	}

	@Test
	public void validateIdvIdUinFailure() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-002")));
	}

	@Test
	public void validateIdvIdVidFailure() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		authRequestDTO.setIndividualIdType("VID");
		Mockito.when(vidValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-004")));
	}

	@Test
	public void validateIdtypeUinVidNull() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		authRequestDTO.setIndividualIdType(null);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-006")));
	}

	@Test
	public void validateIdtypeUinVidEmpty() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		authRequestDTO.setIndividualIdType("");
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-006")));
	}

	@Test
	public void validateTxnIdNull() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		authRequestDTO.setTransactionID(null);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-006")));
		errors.getAllErrors().stream().forEach(
				err -> assertTrue(Stream.of(err.getArguments()).anyMatch(error -> error.equals("transactionID"))));
	}

	@Test
	public void validateTxnIdEmpty() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		authRequestDTO.setTransactionID("");
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-006")));
		errors.getAllErrors().stream().forEach(
				err -> assertTrue(Stream.of(err.getArguments()).anyMatch(error -> error.equals("transactionID"))));
	}

	@Test
	public void validateTxnIdInvalid() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		authRequestDTO.setTransactionID("!@#$4w5445");
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-009")));
		errors.getAllErrors().stream().forEach(
				err -> assertTrue(Stream.of(err.getArguments()).anyMatch(error -> error.equals("transactionID"))));
	}

	@Test
	public void validateReqTimeNull() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		authRequestDTO.setRequestTime(null);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-006")));
		errors.getAllErrors().stream().forEach(
				err -> assertTrue(Stream.of(err.getArguments()).anyMatch(error -> error.equals("requestTime"))));
	}

	@Test
	public void validateReqTimeEmpty() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		authRequestDTO.setRequestTime("");
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-006")));
		errors.getAllErrors().stream().forEach(
				err -> assertTrue(Stream.of(err.getArguments()).anyMatch(error -> error.equals("requestTime"))));
	}

	@Test
	public void validateReqTimeInvalid() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		authRequestDTO.setRequestTime("awer4w5445");
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-009")));
		errors.getAllErrors().stream().forEach(
				err -> assertTrue(Stream.of(err.getArguments()).anyMatch(error -> error.equals("requestTime"))));
	}

	@Test
	public void validateReqTimeInvalid2() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		authRequestDTO.setRequestTime(Instant.now().plus(2, ChronoUnit.DAYS).atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-001")));
	}

	@Test
	public void validateRequestReqTimeNull() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		RequestDTO requestDto = authRequestDTO.getRequest();
		requestDto.setTimestamp(null);
		authRequestDTO.setRequest(requestDto);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-006")));
		errors.getAllErrors().stream().forEach(
				err -> assertTrue(Stream.of(err.getArguments()).anyMatch(error -> error.equals("request/timestamp"))));
	}

	@Test
	public void validateRequestReqTimeEmpty() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		RequestDTO requestDto = authRequestDTO.getRequest();
		requestDto.setTimestamp("");
		authRequestDTO.setRequest(requestDto);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-006")));
		errors.getAllErrors().stream().forEach(
				err -> assertTrue(Stream.of(err.getArguments()).anyMatch(error -> error.equals("request/timestamp"))));
	}

	@Test
	public void validateRequestReqTimeInvalid2() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		RequestDTO requestDto = authRequestDTO.getRequest();
		requestDto.setTimestamp(Instant.now().plus(2, ChronoUnit.DAYS).atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setRequest(requestDto);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-001")));
	}

	@Test
	public void validateRequestReqTimeInvalid() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		RequestDTO requestDto = authRequestDTO.getRequest();
		requestDto.setTimestamp("awer4w5445");
		authRequestDTO.setRequest(requestDto);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-009")));
		errors.getAllErrors().stream().forEach(
				err -> assertTrue(Stream.of(err.getArguments()).anyMatch(error -> error.equals("request/timestamp"))));
	}

	@Test
	public void validateConsentReqInvalid() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		authRequestDTO.setConsentObtained(false);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-014")));
	}

	@Test
	public void validateAllowedAuthTypes() {
		MockEnvironment mockEnv = new MockEnvironment();
		mockEnv.setProperty("auth.types.allowed", "demo,otp");
		ReflectionTestUtils.setField(authRequestValidator, "env", mockEnv);
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MPA-006")));
	}

	@Test
	public void validateAllowedAuthTypes2() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		authRequestDTO.setRequestedAuth(null);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MPA-006")));
	}

	@Test
	public void validateAuthType() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		authRequestDTO.setRequestedAuth(new AuthTypeDTO());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-008")));
	}

	@Test
	public void validateRequestTimedOut() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		authRequestDTO.setRequestTime(Instant.now().minus(2, ChronoUnit.DAYS).atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-001")));
	}

	@Test
	public void checkDemoAuth() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		RequestDTO requestDto = authRequestDTO.getRequest();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		IdentityDTO identityDTO = new IdentityDTO();
		identityDTO.setName(Collections.singletonList(identityInfoDTO));
		requestDto.setDemographics(identityDTO);
		authRequestDTO.setRequest(requestDto);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-009")));
		errors.getAllErrors().stream().forEach(
				err -> assertTrue(Stream.of(err.getArguments()).anyMatch(error -> error.equals("IdentityInfoDTO"))));
	}

	@Test
	public void checkIdentityInfoValue() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		RequestDTO requestDto = authRequestDTO.getRequest();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		IdentityDTO identityDTO = new IdentityDTO();
		identityDTO.setName(Collections.singletonList(identityInfoDTO));
		requestDto.setDemographics(identityDTO);
		authRequestDTO.setRequest(requestDto);
		Mockito.when(idInfoHelper.isMatchtypeEnabled(DemoMatchType.NAME)).thenReturn(Boolean.TRUE);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-009")));
		errors.getAllErrors().stream().forEach(
				err -> assertTrue(Stream.of(err.getArguments()).anyMatch(error -> error.equals("IdentityInfoDTO"))));
	}

	@Test
	public void checkLangaugeDetails() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		RequestDTO requestDto = authRequestDTO.getRequest();
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setValue("adasd");
		IdentityInfoDTO identityInfoDTO2 = new IdentityInfoDTO();
		identityInfoDTO2.setValue("adasd");
		List<IdentityInfoDTO> identityInfoDTOList = new ArrayList<>();
		identityInfoDTOList.add(identityInfoDTO1);
		identityInfoDTOList.add(identityInfoDTO2);
		IdentityDTO identityDTO = new IdentityDTO();
		identityDTO.setName(identityInfoDTOList);
		requestDto.setDemographics(identityDTO);
		authRequestDTO.setRequest(requestDto);
		Mockito.when(idInfoHelper.isMatchtypeEnabled(DemoMatchType.NAME)).thenReturn(Boolean.TRUE);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-009")));
		errors.getAllErrors().stream().forEach(
				err -> assertTrue(Stream.of(err.getArguments()).anyMatch(error -> error.equals("LanguageCode"))));
	}

	@Test
	public void validateBioMetadataDetails() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		RequestDTO requestDto = authRequestDTO.getRequest();
		requestDto.setBiometrics(null);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-BIA-010")));
	}

	@Test
	public void validateBioMetadataDetails_validateBioType_Null() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		RequestDTO requestDto = authRequestDTO.getRequest();
		requestDto.getBiometrics().get(0).getData().setBioType(null);
		authRequestDTO.setRequest(requestDto);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-006")));
		errors.getAllErrors().stream()
				.forEach(err -> assertTrue(Stream.of(err.getArguments()).anyMatch(error -> error.equals("bioType"))));
	}

	@Test
	public void validateBioMetadataDetails_validateBioType_Empty() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		RequestDTO requestDto = authRequestDTO.getRequest();
		requestDto.getBiometrics().get(0).getData().setBioType("");
		authRequestDTO.setRequest(requestDto);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-006")));
		errors.getAllErrors().stream()
				.forEach(err -> assertTrue(Stream.of(err.getArguments()).anyMatch(error -> error.equals("bioType"))));
	}

	@Test
	public void validateBioMetadataDetails_validateBioData_Null() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		RequestDTO requestDto = authRequestDTO.getRequest();
		requestDto.getBiometrics().get(0).setData(null);
		authRequestDTO.setRequest(requestDto);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-BIA-010")));
	}

	@Test
	public void validateBioMetadataDetails_validateBioType_Invalid() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		RequestDTO requestDto = authRequestDTO.getRequest();
		requestDto.getBiometrics().get(0).getData().setBioType("XYZ");
		authRequestDTO.setRequest(requestDto);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MPA-006")));
		errors.getAllErrors().stream()
				.forEach(err -> assertTrue(Stream.of(err.getArguments()).anyMatch(error -> error.equals("XYZ"))));
	}

	@Test
	public void validateBioMetadataDetails_validateBioSubType_Null() { // TODO check error code
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		RequestDTO requestDto = authRequestDTO.getRequest();
		requestDto.getBiometrics().get(0).getData().setBioSubType(null);
		authRequestDTO.setRequest(requestDto);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-006")));
		errors.getAllErrors().stream().forEach(
				err -> assertTrue(Stream.of(err.getArguments()).anyMatch(error -> error.equals("bioSubType"))));
	}

	@Test
	public void validateBioMetadataDetails_validateBioSubType_Empty() { // TODO check error code
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		RequestDTO requestDto = authRequestDTO.getRequest();
		requestDto.getBiometrics().get(0).getData().setBioSubType("");
		authRequestDTO.setRequest(requestDto);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-006")));
		errors.getAllErrors().stream().forEach(
				err -> assertTrue(Stream.of(err.getArguments()).anyMatch(error -> error.equals("bioSubType"))));
	}

	@Test
	public void validateBioMetadataDetails_validateBioSubType_Invalid() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		RequestDTO requestDto = authRequestDTO.getRequest();
		requestDto.getBiometrics().get(0).getData().setBioSubType("XYZ");
		authRequestDTO.setRequest(requestDto);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-009")));
		errors.getAllErrors().stream().forEach(err -> assertTrue(
				Stream.of(err.getArguments()).anyMatch(error -> error.equals("bioSubType - XYZ for bioType FID"))));
	}

	@Test
	public void validateBioMetadataDetails_validateFaceBioType() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		RequestDTO requestDto = authRequestDTO.getRequest();
		List<BioIdentityInfoDTO> biometrics = new ArrayList<>();
		biometrics.addAll(requestDto.getBiometrics());
		DataDTO dataDTO = new DataDTO();// DataDTO
		dataDTO.setBioType("FID");
		dataDTO.setBioSubType("UNKNOWN");
		dataDTO.setBioValue(
				"Rk1SACAyMAAAAAFcAAABPAFiAMUAxQEAAAAoNUB9AMF0V4CBAKBBPEC0AL68ZIC4AKjNZEBiAJvWXUBPANPWNUDSAK7RUIC2AQIfZEDJAPMxPEByAGwPXYCpARYPZECfAFjoZECGAEv9ZEBEAFmtV0BpAUGNXUC/AUEESUCUAVIEPEC2AVNxPICcALWuZICuALm3ZECNAJqxQ0CUAI3GQ0CXAPghV0BVAKDOZEBfAPqHXUBDAKe/ZIB9AG3xXUDPAIbZUEBcAGYhZECIASgHXYBJAGAnV0DjAR4jG0DKATqJIUCGADGSZEDSAUYGIUAxAD+nV0CXAK+oSUBoALr6Q4CSAOuKXUCiAIvNZEC9AJzQZIBNALbTXUBBAL68V0CeAHDZZECwAHPaZEBRAPwHUIBHAHW2XUDXARAUDUC4AS4HZEDXAS0CQ0CYADL4ZECsAUzuPEBkACgRZAAA");
		dataDTO.setDeviceProviderID("cogent");
		dataDTO.setTransactionID("1234567890");
		dataDTO.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();// BioIdentityInfoDTO
		bioIdentityInfoDTO.setData(dataDTO);
		biometrics.add(bioIdentityInfoDTO);
		requestDto.setBiometrics(biometrics);
		authRequestDTO.setRequest(requestDto);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		Mockito.when(idInfoHelper.isMatchtypeEnabled(BioMatchType.FACE)).thenReturn(Boolean.TRUE);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-BIA-009")));
		errors.getAllErrors().stream()
				.forEach(err -> assertTrue(Stream.of(err.getArguments()).anyMatch(error -> error.equals("face"))));
	}

	@Test
	public void validateBioMetadataDetails_validateFingerRequestValueCount() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFinger();
		RequestDTO requestDto = authRequestDTO.getRequest();
		List<BioIdentityInfoDTO> biometrics = new ArrayList<>();
		biometrics.addAll(requestDto.getBiometrics());
		DataDTO dataDTO = new DataDTO();// DataDTO
		dataDTO.setBioType("FMR");
		dataDTO.setBioSubType("UNKNOWN");
		dataDTO.setBioValue(
				"Rk1SACAyMAAAAAFcAAABPAFiAMUAxQEAAAAoNUB9AMF0V4CBAKBBPEC0AL68ZIC4AKjNZEBiAJvWXUBPANPWNUDSAK7RUIC2AQIfZEDJAPMxPEByAGwPXYCpARYPZECfAFjoZECGAEv9ZEBEAFmtV0BpAUGNXUC/AUEESUCUAVIEPEC2AVNxPICcALWuZICuALm3ZECNAJqxQ0CUAI3GQ0CXAPghV0BVAKDOZEBfAPqHXUBDAKe/ZIB9AG3xXUDPAIbZUEBcAGYhZECIASgHXYBJAGAnV0DjAR4jG0DKATqJIUCGADGSZEDSAUYGIUAxAD+nV0CXAK+oSUBoALr6Q4CSAOuKXUCiAIvNZEC9AJzQZIBNALbTXUBBAL68V0CeAHDZZECwAHPaZEBRAPwHUIBHAHW2XUDXARAUDUC4AS4HZEDXAS0CQ0CYADL4ZECsAUzuPEBkACgRZAAA");
		dataDTO.setDeviceProviderID("cogent");
		dataDTO.setTransactionID("1234567890");
		dataDTO.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();// BioIdentityInfoDTO
		bioIdentityInfoDTO.setData(dataDTO);
		biometrics.add(bioIdentityInfoDTO);
		requestDto.setBiometrics(biometrics);
		authRequestDTO.setRequest(requestDto);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		Mockito.when(idInfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-BIA-002")));
	}

	@Test
	public void validateBioMetadataDetails_validateFingerRequestTypeCount() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFinger();
		RequestDTO requestDto = authRequestDTO.getRequest();
		List<BioIdentityInfoDTO> biometrics = new ArrayList<>();
		biometrics.addAll(requestDto.getBiometrics());
		DataDTO dataDTO = new DataDTO();// DataDTO
		dataDTO.setBioType("FMR");
		dataDTO.setBioSubType("LEFT_INDEX");
		dataDTO.setBioValue(
				"AUEESUCUAVIEPEC2AVNxPICcALWuZICuALm3ZECNAJqxQ0CUAI3GQ0CXAPghV0BVAKDOZEBfAPqHXUBDAKe/ZIB9AG3xXUDPAIbZUEBcAGYhZECIASgHXYBJAGAnV0DjAR4jG0DKATqJIUCGADGSZEDSAUYGIUAxAD+nV0CXAK+oSUBoALr6Q4CSAOuKXUCiAIvNZEC9AJzQZIBNALbTXUBBAL68V0CeAHDZZECwAHPaZEBRAPwHUIBHAHW2XUDXARAUDUC4AS4HZEDXAS0CQ0CYADL4ZECsAUzuPEBkACgRZAAA");
		dataDTO.setDeviceProviderID("cogent");
		dataDTO.setTransactionID("1234567890");
		dataDTO.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();// BioIdentityInfoDTO
		bioIdentityInfoDTO.setData(dataDTO);
		biometrics.add(bioIdentityInfoDTO);
		requestDto.setBiometrics(biometrics);
		authRequestDTO.setRequest(requestDto);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		Mockito.when(idInfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-BIA-002")));
	}

	@Test
	public void validateBioMetadataDetails_validateFingerRequestExceedingCount() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFinger();
		RequestDTO requestDto = authRequestDTO.getRequest();
		List<BioIdentityInfoDTO> biometrics = new ArrayList<>();
		biometrics.addAll(requestDto.getBiometrics());
		DataDTO dataDTO = new DataDTO();// DataDTO
		dataDTO.setBioType("FMR");
		dataDTO.setBioSubType("RIGHT_INDEX");
		dataDTO.setBioValue(
				"AUEESUCUAVIEPEC2AVNxPICcALWuZICuALm3ZECNAJqxQ0CUAI3GQ0CXAPghV0BVAKDOZEBfAPqHXUBDAKe/ZIB9AG3xXUDPAIbZUEBcAGYhZECIASgHXYBJAGAnV0DjAR4jG0DKATqJIUCGADGSZEDSAUYGIUAxAD+nV0CXAK+oSUBoALr6Q4CSAOuKXUCiAIvNZEC9AJzQZIBNALbTXUBBAL68V0CeAHDZZECwAHPaZEBRAPwHUIBHAHW2XUDXARAUDUC4AS4HZEDXAS0CQ0CYADL4ZECsAUzuPEBkACgRZAAA");
		dataDTO.setDeviceProviderID("cogent");
		dataDTO.setTransactionID("1234567890");
		dataDTO.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();// BioIdentityInfoDTO
		bioIdentityInfoDTO.setData(dataDTO);
		biometrics.add(bioIdentityInfoDTO);
		DataDTO dataDTO1 = new DataDTO();// DataDTO
		dataDTO1.setBioType("FMR");
		dataDTO1.setBioSubType("UNKNOWN");
		dataDTO1.setBioValue(
				"ZIB9AG3xXUDPAIbZUEBcAGYhZECIASgHXYBJAGAnV0DjAR4jG0DKATqJIUCGADGSZEDSAUYGIUAxAD+nV0CXAK+oSUBoALr6Q4CSAOuKXUCiAIvNZEC9AJzQZIBNALbTXUBBAL68V0CeAHDZZECwAHPaZEBRAPwHUIBHAHW2XUDXARAUDUC4AS4HZEDXAS0CQ0CYADL4ZECsAUzuPEBkACgRZAAA");
		dataDTO1.setDeviceProviderID("cogent");
		dataDTO1.setTransactionID("1234567890");
		dataDTO1.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		BioIdentityInfoDTO bioIdentityInfoDTO1 = new BioIdentityInfoDTO();// BioIdentityInfoDTO
		bioIdentityInfoDTO1.setData(dataDTO1);
		biometrics.add(bioIdentityInfoDTO1);
		requestDto.setBiometrics(biometrics);
		authRequestDTO.setRequest(requestDto);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		Mockito.when(idInfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-BIA-003")));
	}

	@Test
	public void validateBioMetadataDetails_validateIrisRequestValueCount() {
		AuthRequestDTO authRequestDTO = createAuthRequestForIris();
		RequestDTO requestDto = authRequestDTO.getRequest();
		List<BioIdentityInfoDTO> biometrics = new ArrayList<>();
		biometrics.addAll(requestDto.getBiometrics());
		DataDTO dataDTO = new DataDTO();// DataDTO
		dataDTO.setBioType("IIR");
		dataDTO.setBioSubType("RIGHT");
		dataDTO.setBioValue(
				"Rk1SACAyMAAAAAFcAAABPAFiAMUAxQEAAAAoNUB9AMF0V4CBAKBBPEC0AL68ZIC4AKjNZEBiAJvWXUBPANPWNUDSAK7RUIC2AQIfZEDJAPMxPEByAGwPXYCpARYPZECfAFjoZECGAEv9ZEBEAFmtV0BpAUGNXUC/AUEESUCUAVIEPEC2AVNxPICcALWuZICuALm3ZECNAJqxQ0CUAI3GQ0CXAPghV0BVAKDOZEBfAPqHXUBDAKe/ZIB9AG3xXUDPAIbZUEBcAGYhZECIASgHXYBJAGAnV0DjAR4jG0DKATqJIUCGADGSZEDSAUYGIUAxAD+nV0CXAK+oSUBoALr6Q4CSAOuKXUCiAIvNZEC9AJzQZIBNALbTXUBBAL68V0CeAHDZZECwAHPaZEBRAPwHUIBHAHW2XUDXARAUDUC4AS4HZEDXAS0CQ0CYADL4ZECsAUzuPEBkACgRZAAA");
		dataDTO.setDeviceProviderID("cogent");
		dataDTO.setTransactionID("1234567890");
		dataDTO.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();// BioIdentityInfoDTO
		bioIdentityInfoDTO.setData(dataDTO);
		biometrics.add(bioIdentityInfoDTO);
		requestDto.setBiometrics(biometrics);
		authRequestDTO.setRequest(requestDto);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		Mockito.when(idInfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-BIA-007")));
	}

	@Test
	public void validateBioMetadataDetails_validateIrisRequestExceedingCount() {
		AuthRequestDTO authRequestDTO = createAuthRequestForIris();
		RequestDTO requestDto = authRequestDTO.getRequest();
		List<BioIdentityInfoDTO> biometrics = new ArrayList<>();
		biometrics.addAll(requestDto.getBiometrics());
		DataDTO dataDTO = new DataDTO();// DataDTO
		dataDTO.setBioType("IIR");
		dataDTO.setBioSubType("RIGHT");
		dataDTO.setBioValue(
				"AUEESUCUAVIEPEC2AVNxPICcALWuZICuALm3ZECNAJqxQ0CUAI3GQ0CXAPghV0BVAKDOZEBfAPqHXUBDAKe/ZIB9AG3xXUDPAIbZUEBcAGYhZECIASgHXYBJAGAnV0DjAR4jG0DKATqJIUCGADGSZEDSAUYGIUAxAD+nV0CXAK+oSUBoALr6Q4CSAOuKXUCiAIvNZEC9AJzQZIBNALbTXUBBAL68V0CeAHDZZECwAHPaZEBRAPwHUIBHAHW2XUDXARAUDUC4AS4HZEDXAS0CQ0CYADL4ZECsAUzuPEBkACgRZAAA");
		dataDTO.setDeviceProviderID("cogent");
		dataDTO.setTransactionID("1234567890");
		dataDTO.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();// BioIdentityInfoDTO
		bioIdentityInfoDTO.setData(dataDTO);
		biometrics.add(bioIdentityInfoDTO);
		DataDTO dataDTO1 = new DataDTO();// DataDTO
		dataDTO1.setBioType("IIR");
		dataDTO1.setBioSubType("LEFT");
		dataDTO1.setBioValue(
				"ZIB9AG3xXUDPAIbZUEBcAGYhZECIASgHXYBJAGAnV0DjAR4jG0DKATqJIUCGADGSZEDSAUYGIUAxAD+nV0CXAK+oSUBoALr6Q4CSAOuKXUCiAIvNZEC9AJzQZIBNALbTXUBBAL68V0CeAHDZZECwAHPaZEBRAPwHUIBHAHW2XUDXARAUDUC4AS4HZEDXAS0CQ0CYADL4ZECsAUzuPEBkACgRZAAA");
		dataDTO1.setDeviceProviderID("cogent");
		dataDTO1.setTransactionID("1234567890");
		dataDTO1.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		BioIdentityInfoDTO bioIdentityInfoDTO1 = new BioIdentityInfoDTO();// BioIdentityInfoDTO
		bioIdentityInfoDTO1.setData(dataDTO1);
		biometrics.add(bioIdentityInfoDTO1);
		requestDto.setBiometrics(biometrics);
		authRequestDTO.setRequest(requestDto);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		Mockito.when(idInfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-BIA-008")));
		errors.getAllErrors().stream()
				.forEach(err -> assertTrue(Stream.of(err.getArguments()).anyMatch(error -> error.equals("iris"))));
	}

	@Test
	public void validateAllowedAuthTypesNull() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		authRequestDTO.setRequestedAuth(null);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MPA-006")));
	}

	@Test
	public void checkAuthRequest() {
		AuthRequestDTO authRequestDTO = createAuthRequestForFace();
		authRequestDTO.setRequestedAuth(new AuthTypeDTO());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-008")));
	}

	private AuthRequestDTO createAuthRequestForFace() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setIndividualId("3926509647");
		authRequestDTO.setIndividualIdType("UIN");
		authRequestDTO.setKeyIndex("string");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setVersion("1.0");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setBio(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		DataDTO dataDTO = new DataDTO();// DataDTO
		dataDTO.setBioType("FID");
		dataDTO.setBioSubType("UNKNOWN");
		dataDTO.setBioValue(
				"Rk1SACAyMAAAAAFcAAABPAFiAMUAxQEAAAAoNUB9AMF0V4CBAKBBPEC0AL68ZIC4AKjNZEBiAJvWXUBPANPWNUDSAK7RUIC2AQIfZEDJAPMxPEByAGwPXYCpARYPZECfAFjoZECGAEv9ZEBEAFmtV0BpAUGNXUC/AUEESUCUAVIEPEC2AVNxPICcALWuZICuALm3ZECNAJqxQ0CUAI3GQ0CXAPghV0BVAKDOZEBfAPqHXUBDAKe/ZIB9AG3xXUDPAIbZUEBcAGYhZECIASgHXYBJAGAnV0DjAR4jG0DKATqJIUCGADGSZEDSAUYGIUAxAD+nV0CXAK+oSUBoALr6Q4CSAOuKXUCiAIvNZEC9AJzQZIBNALbTXUBBAL68V0CeAHDZZECwAHPaZEBRAPwHUIBHAHW2XUDXARAUDUC4AS4HZEDXAS0CQ0CYADL4ZECsAUzuPEBkACgRZAAA");
		dataDTO.setDeviceProviderID("cogent");
		dataDTO.setTransactionID("1234567890");
		dataDTO.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();// BioIdentityInfoDTO
		bioIdentityInfoDTO.setData(dataDTO);
		RequestDTO reqDTO = new RequestDTO();// RequestDTO
		reqDTO.setBiometrics(Collections.singletonList(bioIdentityInfoDTO));
		reqDTO.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setRequest(reqDTO);
		return authRequestDTO;
	}

	private AuthRequestDTO createAuthRequestForFinger() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setIndividualId("3926509647");
		authRequestDTO.setIndividualIdType("UIN");
		authRequestDTO.setKeyIndex("string");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setVersion("1.0");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setBio(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		DataDTO dataDTO = new DataDTO();// DataDTO
		dataDTO.setBioType("FMR");
		dataDTO.setBioSubType("LEFT_INDEX");
		dataDTO.setBioValue(
				"Rk1SACAyMAAAAAFcAAABPAFiAMUAxQEAAAAoNUB9AMF0V4CBAKBBPEC0AL68ZIC4AKjNZEBiAJvWXUBPANPWNUDSAK7RUIC2AQIfZEDJAPMxPEByAGwPXYCpARYPZECfAFjoZECGAEv9ZEBEAFmtV0BpAUGNXUC/AUEESUCUAVIEPEC2AVNxPICcALWuZICuALm3ZECNAJqxQ0CUAI3GQ0CXAPghV0BVAKDOZEBfAPqHXUBDAKe/ZIB9AG3xXUDPAIbZUEBcAGYhZECIASgHXYBJAGAnV0DjAR4jG0DKATqJIUCGADGSZEDSAUYGIUAxAD+nV0CXAK+oSUBoALr6Q4CSAOuKXUCiAIvNZEC9AJzQZIBNALbTXUBBAL68V0CeAHDZZECwAHPaZEBRAPwHUIBHAHW2XUDXARAUDUC4AS4HZEDXAS0CQ0CYADL4ZECsAUzuPEBkACgRZAAA");
		dataDTO.setDeviceProviderID("cogent");
		dataDTO.setTransactionID("1234567890");
		dataDTO.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();// BioIdentityInfoDTO
		bioIdentityInfoDTO.setData(dataDTO);
		RequestDTO reqDTO = new RequestDTO();// RequestDTO
		reqDTO.setBiometrics(Collections.singletonList(bioIdentityInfoDTO));
		reqDTO.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setRequest(reqDTO);
		return authRequestDTO;
	}

	private AuthRequestDTO createAuthRequestForIris() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setIndividualId("3926509647");
		authRequestDTO.setIndividualIdType("UIN");
		authRequestDTO.setKeyIndex("string");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setVersion("1.0");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setBio(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		DataDTO dataDTO = new DataDTO();// DataDTO
		dataDTO.setBioType("IIR");
		dataDTO.setBioSubType("LEFT");
		dataDTO.setBioValue(
				"Rk1SACAyMAAAAAFcAAABPAFiAMUAxQEAAAAoNUB9AMF0V4CBAKBBPEC0AL68ZIC4AKjNZEBiAJvWXUBPANPWNUDSAK7RUIC2AQIfZEDJAPMxPEByAGwPXYCpARYPZECfAFjoZECGAEv9ZEBEAFmtV0BpAUGNXUC/AUEESUCUAVIEPEC2AVNxPICcALWuZICuALm3ZECNAJqxQ0CUAI3GQ0CXAPghV0BVAKDOZEBfAPqHXUBDAKe/ZIB9AG3xXUDPAIbZUEBcAGYhZECIASgHXYBJAGAnV0DjAR4jG0DKATqJIUCGADGSZEDSAUYGIUAxAD+nV0CXAK+oSUBoALr6Q4CSAOuKXUCiAIvNZEC9AJzQZIBNALbTXUBBAL68V0CeAHDZZECwAHPaZEBRAPwHUIBHAHW2XUDXARAUDUC4AS4HZEDXAS0CQ0CYADL4ZECsAUzuPEBkACgRZAAA");
		dataDTO.setDeviceProviderID("cogent");
		dataDTO.setTransactionID("1234567890");
		dataDTO.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();// BioIdentityInfoDTO
		bioIdentityInfoDTO.setData(dataDTO);
		RequestDTO reqDTO = new RequestDTO();// RequestDTO
		reqDTO.setBiometrics(Collections.singletonList(bioIdentityInfoDTO));
		reqDTO.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setRequest(reqDTO);
		return authRequestDTO;
	}

}
