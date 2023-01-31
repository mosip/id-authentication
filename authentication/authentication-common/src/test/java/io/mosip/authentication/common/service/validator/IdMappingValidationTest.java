package io.mosip.authentication.common.service.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.IdInfoFetcherImpl;
import io.mosip.authentication.common.service.impl.match.DOBType;
import io.mosip.authentication.common.service.impl.match.DemoMatchType;
import io.mosip.authentication.common.service.impl.match.IdaIdMapping;
import io.mosip.authentication.common.service.integration.MasterDataManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.indauth.dto.DigitalId;
import io.mosip.authentication.core.indauth.dto.IdentityDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.idrepository.core.helper.RestHelper;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.pinvalidator.impl.PinValidatorImpl;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class IdMappingValidationTest {

	@InjectMocks
	private AuthRequestValidator authRequestValidator;

	@Mock
	private PinValidatorImpl pinValidator;

	@Mock
	private IdInfoFetcherImpl idInfoFetcher;

	@Mock
	private IdInfoHelper idinfoHelper;

	@InjectMocks
	private static ObjectMapper mapper;

	@Mock
	private IDAMappingConfig idMappingConfig;

	@Mock
	private MasterDataManager masterDataManager;

	@Mock
	private PinValidatorImpl pinValidatorImpl;

	/**
	 * The Rest Helper
	 */
	@Mock
	private RestHelper restHelper;

	@Mock
	private RestRequestFactory restFactory;

	@Before
	public void before() throws IdAuthenticationDaoException {
		ReflectionTestUtils.setField(authRequestValidator, "idInfoHelper", idinfoHelper);
	}

	@Test
	public void checkNoDemoAuth() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(false);
		ReflectionTestUtils.invokeMethod(authRequestValidator, "checkAuthRequest", authRequestDTO, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void checkValidDemoAuth() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = getAuthRequestDtoValue();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(true);
		List<String> phoneList = new ArrayList<>();
		phoneList.add("phone");
		List<String> emailList = new ArrayList<>();
		emailList.add("phone");
		List<String> pincodeList = new ArrayList<>();
		pincodeList.add("phone");
		List<String> dobList = new ArrayList<>();
		dobList.add("dateofBirth");
		Mockito.when(idinfoHelper.getIdMappingValue(IdaIdMapping.PHONE, DemoMatchType.PHONE)).thenReturn(phoneList);
		Mockito.when(idinfoHelper.getIdMappingValue(IdaIdMapping.EMAIL, DemoMatchType.EMAIL)).thenReturn(emailList);
		Mockito.when(idinfoHelper.getIdMappingValue(IdaIdMapping.PINCODE, DemoMatchType.PINCODE))
				.thenReturn(pincodeList);
		Mockito.when(idinfoHelper.getIdMappingValue(IdaIdMapping.DOB, DemoMatchType.DOB))
		.thenReturn(dobList);		
		Mockito.when(idInfoFetcher.getSystemSupportedLanguageCodes()).thenReturn(List.of("eng","fra","ara"));
		ReflectionTestUtils.invokeMethod(authRequestValidator, "checkAuthRequest", authRequestDTO, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void TestValidAdditionalFactorsinOtp() {
		AuthRequestDTO authRequestDTO = getOtpRequest();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(false);
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateAdditionalFactorsDetails", authRequestDTO,
				errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void TestValidAdditionalFactorsinSPin() {
		AuthRequestDTO authRequestDTO = getSPinRequest();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(false);
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateAdditionalFactorsDetails", authRequestDTO,
				errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void TestNoValidAdditionalFactorsinOtp() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		request.setOtp(null);
		authRequestDTO.setRequest(request);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(true);
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateAdditionalFactorsDetails", authRequestDTO,
				errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void TestNoValidAdditionalFactorsinSPin() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		request.setStaticPin(null);
		authRequestDTO.setRequest(request);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(true);
		Mockito.when(pinValidator.validatePin(Mockito.anyString())).thenReturn(false);
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateAdditionalFactorsDetails", authRequestDTO,
				errors);
		assertFalse(errors.hasErrors());
	}

	@Ignore
	@Test
	public void TestBioMetricNotValidated() {
		AuthRequestDTO authRequestDTO = getBioFingerDetails();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		Set<String> allowedAuthtype = new HashSet<>();
		allowedAuthtype.add("bio-FACE");
		allowedAuthtype.add("bio-Finger");
		allowedAuthtype.add("bio-Iris");
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateBioMetadataDetails", authRequestDTO, errors,
				allowedAuthtype);
		assertFalse(errors.hasErrors());
	}

	@Ignore
	@Test
	public void TestBioMetricValidationsforFingerPrint() {
		AuthRequestDTO authRequestDTO = getBioFingerDetails();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(true);
		Set<String> allowedAuthtype = new HashSet<>();
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateBioMetadataDetails", authRequestDTO, errors,
				allowedAuthtype);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void TestBioMetricValidationsforIris() {
		AuthRequestDTO authRequestDTO = getIrisDetails();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(true);
		Set<String> allowedAuthtype = new HashSet<>();
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateBioMetadataDetails", authRequestDTO, errors,
				allowedAuthtype);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void TestBioMetricValidationsforFace() {
		AuthRequestDTO authRequestDTO = getFaceDetails();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(true);
		Set<String> allowedAuthtype = new HashSet<>();
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateBioMetadataDetails", authRequestDTO, errors,
				allowedAuthtype);
		assertTrue(errors.hasErrors());
	}

	private AuthRequestDTO getFaceDetails() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
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
		dataDTO.setBioValue("face img");
		dataDTO.setBioSubType("Thumb");
		dataDTO.setBioType("FID");
		faceValue.setData(dataDTO);

		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(faceValue);
		RequestDTO reqDTO = new RequestDTO();
		authRequestDTO.setRequest(reqDTO);
		reqDTO.setBiometrics(fingerIdentityInfoDtoList);
		return authRequestDTO;
	}

	private AuthRequestDTO getIrisDetails() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
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
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		dataDTO.setBioValue("iris img");
		dataDTO.setBioSubType("left");
		dataDTO.setBioType("Iris");
		irisValue.setData(dataDTO);

		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(irisValue);
		RequestDTO reqDTO = new RequestDTO();
		authRequestDTO.setRequest(reqDTO);
		reqDTO.setBiometrics(fingerIdentityInfoDtoList);
		return authRequestDTO;
	}

	private AuthRequestDTO getBioFingerDetails() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
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
		dataDTO.setBioSubType("Thumb");
		dataDTO.setBioType("test");
		dataDTO.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO fingerValue1 = new BioIdentityInfoDTO();
		DataDTO dataDTO1 = new DataDTO();
		dataDTO1.setDeviceCode("1");
		dataDTO1.setDeviceServiceVersion("1");
		DigitalId digitalId1 = new DigitalId();
		digitalId1.setSerialNo("1");
		digitalId1.setMake("1");
		digitalId1.setModel("1");
		digitalId1.setType("1");
		digitalId1.setDeviceProvider("1");
		digitalId1.setDeviceProviderId("1");
		digitalId1.setDateTime(DateUtils.getCurrentDateTimeString());
		dataDTO1.setDigitalId(digitalId1);
		dataDTO1.setBioValue("finger");
		dataDTO1.setBioSubType("Left Thumb");
		dataDTO1.setBioType("Finger");
		dataDTO1.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		fingerValue1.setData(dataDTO1);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		dataDTO.setBioValue("iris img");
		dataDTO.setBioSubType("Left");
		dataDTO.setBioType("Iris");
		dataDTO.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		irisValue.setData(dataDTO);
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFace = new DataDTO();
		dataDTOFace.setDeviceCode("1");
		dataDTOFace.setDeviceServiceVersion("1");
		DigitalId digitalId2 = new DigitalId();
		digitalId2.setSerialNo("1");
		digitalId2.setMake("1");
		digitalId2.setModel("1");
		digitalId2.setType("1");
		digitalId2.setDeviceProvider("1");
		digitalId2.setDeviceProviderId("1");
		digitalId2.setDateTime(DateUtils.getCurrentDateTimeString());
		dataDTOFace.setDigitalId(digitalId2);
		dataDTOFace.setBioValue("face img");
		dataDTOFace.setBioType("FACE");
		dataDTOFace.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		faceValue.setData(dataDTOFace);

		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(fingerValue1);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(reqDTO);
		return authRequestDTO;
	}

	private AuthRequestDTO getOtpRequest() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setOtp("123456");
		authRequestDTO.setRequest(requestDTO);
		return authRequestDTO;
	}

	private AuthRequestDTO getSPinRequest() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setStaticPin("123456");
		authRequestDTO.setRequest(requestDTO);
		return authRequestDTO;
	}

	private AuthRequestDTO getAuthRequestDtoValue() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		IdentityDTO identity = new IdentityDTO();
		/* Name */
		List<IdentityInfoDTO> nameList = new ArrayList<>();
		IdentityInfoDTO namedto = new IdentityInfoDTO();
		namedto.setLanguage("ara");
		namedto.setValue("name");
		nameList.add(namedto);
		identity.setName(nameList);
		/* Address */
		List<IdentityInfoDTO> addressLine1List = new ArrayList<>();
		IdentityInfoDTO addressline1dto = new IdentityInfoDTO();
		addressline1dto.setLanguage("ara");
		addressline1dto.setValue("chennai");
		addressLine1List.add(addressline1dto);
		identity.setAddressLine1(addressLine1List);
		identity.setAddressLine2(addressLine1List);
		identity.setAddressLine3(addressLine1List);
		identity.setLocation1(addressLine1List);
		identity.setLocation2(addressLine1List);
		identity.setLocation3(addressLine1List);
		identity.setPostalCode("343434");
		IdentityInfoDTO agedto = new IdentityInfoDTO();
		agedto.setLanguage("ara");
		agedto.setValue("19");
		identity.setAge("25");
		/* Dob */
		List<IdentityInfoDTO> dobList = new ArrayList<>();
		IdentityInfoDTO dob = new IdentityInfoDTO();
		dob.setLanguage("ara");
		dob.setValue("18/03/1999/");
		dobList.add(dob);
		identity.setDob("25/11/1990");
		/* Dob type */
		List<IdentityInfoDTO> dobtypeList = new ArrayList<>();
		IdentityInfoDTO dob1 = new IdentityInfoDTO();
		dob1.setLanguage("ara");
		dob1.setValue(DOBType.VERIFIED.getDobType());
		dobtypeList.add(dob1);
		identity.setDobType(dobtypeList);
		/* E-mail */
		identity.setEmailId("testemailvalue@testmail.com");
		/* Full Address */
		List<IdentityInfoDTO> fadList = new ArrayList<>();
//		IdentityInfoDTO fad = new IdentityInfoDTO();
//		fad.setLanguage("ara");
//		fad.setValue("chennai");
//		fadList.add(fad);
		identity.setFullAddress(fadList);
		/* Gender */
		List<IdentityInfoDTO> genderList = new ArrayList<>();
		IdentityInfoDTO gender = new IdentityInfoDTO();
		gender.setLanguage("ara");
		gender.setValue("MLE");
		genderList.add(gender);
		identity.setGender(genderList);
		/* Phone Number */
		identity.setPhoneNumber("9002020012");
		identity.setPostalCode("2323232");

		request.setDemographics(identity);
		authRequestDTO.setRequest(request);
		return authRequestDTO;
	}

}
