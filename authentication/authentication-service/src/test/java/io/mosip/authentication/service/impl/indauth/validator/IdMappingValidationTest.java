package io.mosip.authentication.service.impl.indauth.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
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
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.config.IDAMappingConfig;
import io.mosip.authentication.common.factory.RestRequestFactory;
import io.mosip.authentication.common.helper.IdInfoHelper;
import io.mosip.authentication.common.helper.RestHelper;
import io.mosip.authentication.common.impl.indauth.service.demo.DOBType;
import io.mosip.authentication.common.integration.MasterDataManager;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.BioIdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.DataDTO;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class IdMappingValidationTest {

	@InjectMocks
	private AuthRequestValidator authRequestValidator;

	@Mock
	private IdInfoHelper idinfoHelper;

	@InjectMocks
	private static ObjectMapper mapper;

	@Mock
	private IDAMappingConfig idMappingConfig;

	@Mock
	private MasterDataManager masterDataManager;

	@Autowired
	private Environment env;

	/**
	 * The Rest Helper
	 */
	@Mock
	private RestHelper restHelper;

	@Mock
	private RestRequestFactory restFactory;

	@Before
	public void before() throws IdAuthenticationDaoException {
		ReflectionTestUtils.setField(authRequestValidator, "env", env);
		ReflectionTestUtils.setField(authRequestValidator, "idInfoHelper", idinfoHelper);
		ReflectionTestUtils.setField(idinfoHelper, "environment", env);
		ReflectionTestUtils.invokeMethod(authRequestValidator, "initialize");
	}

	@Test
	public void checkInvalidDemoAuth() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(false);
		ReflectionTestUtils.invokeMethod(authRequestValidator, "checkAuthRequest", authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void checkValidDemoAuth() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = getAuthRequestDtoValue();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(true);
		Map<String, List<String>> valueMap = new HashMap<>();
		List<String> valueList = new ArrayList<>();
		valueList.add("ara");
		valueList.add("MLE");
		valueMap.put("ara", valueList);
		Mockito.when(masterDataManager.fetchGenderType()).thenReturn(valueMap);
		ReflectionTestUtils.invokeMethod(authRequestValidator, "checkAuthRequest", authRequestDTO, errors);
		System.err.println(errors);
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
	public void TestInValidAdditionalFactorsinOtp() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO requestedAuth = new AuthTypeDTO();
		requestedAuth.setOtp(true);
		authRequestDTO.setRequestedAuth(requestedAuth);
		RequestDTO request = new RequestDTO();
		request.setOtp("");
		authRequestDTO.setRequest(request);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(true);
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateAdditionalFactorsDetails", authRequestDTO,
				errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void TestInValidAdditionalFactorsinSPin() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO requestedAuth = new AuthTypeDTO();
		requestedAuth.setPin(true);
		authRequestDTO.setRequestedAuth(requestedAuth);
		RequestDTO request = new RequestDTO();
		request.setStaticPin("");
		authRequestDTO.setRequest(request);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(true);
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateAdditionalFactorsDetails", authRequestDTO,
				errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void TestBioMetricNotValidated() {
		AuthRequestDTO authRequestDTO = getBioFingerDetails();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateBioMetadataDetails", authRequestDTO, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void TestBioMetricValidationsforFingerPrint() {
		AuthRequestDTO authRequestDTO = getBioFingerDetails();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(true);
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateBioMetadataDetails", authRequestDTO, errors);
		System.err.println(errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void TestBioMetricValidationsforIris() {
		AuthRequestDTO authRequestDTO = getIrisDetails();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(true);
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateBioMetadataDetails", authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void TestBioMetricValidationsforFace() {
		AuthRequestDTO authRequestDTO = getFaceDetails();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(true);
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateBioMetadataDetails", authRequestDTO, errors);
		System.err.println(errors.hasErrors());
		assertTrue(errors.hasErrors());
	}

	private AuthRequestDTO getFaceDetails() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO requestedAuth = new AuthTypeDTO();
		requestedAuth.setBio(true);
		authRequestDTO.setRequestedAuth(requestedAuth);
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		dataDTO.setBioValue("face img");
		dataDTO.setBioSubType("Thumb");
		dataDTO.setBioType("FID");
		dataDTO.setDeviceProviderID("provider001");
		faceValue.setData(dataDTO);

		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(faceValue);
		RequestDTO reqDTO = new RequestDTO();
		authRequestDTO.setRequest(reqDTO);
		return authRequestDTO;
	}

	private AuthRequestDTO getIrisDetails() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO requestedAuth = new AuthTypeDTO();
		requestedAuth.setBio(true);
		authRequestDTO.setRequestedAuth(requestedAuth);
		DataDTO dataDTO = new DataDTO();
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		dataDTO.setBioValue("iris img");
		dataDTO.setBioSubType("left");
		dataDTO.setBioType("IIR");
		dataDTO.setDeviceProviderID("provider001");
		irisValue.setData(dataDTO);

		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(irisValue);
		RequestDTO reqDTO = new RequestDTO();
		authRequestDTO.setRequest(reqDTO);
		return authRequestDTO;
	}

	private AuthRequestDTO getBioFingerDetails() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO requestedAuth = new AuthTypeDTO();
		requestedAuth.setBio(true);
		authRequestDTO.setRequestedAuth(requestedAuth);
		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		dataDTO.setBioValue("finger");
		dataDTO.setBioSubType("Thumb");
		dataDTO.setBioType("test");
		dataDTO.setDeviceProviderID("test01");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO fingerValue1 = new BioIdentityInfoDTO();
		DataDTO dataDTO1 = new DataDTO();
		dataDTO1.setBioValue("finger");
		dataDTO1.setBioSubType("LEFT_THUMB");
		dataDTO1.setBioType("FIR");
		dataDTO1.setDeviceProviderID("test01");
		fingerValue1.setData(dataDTO1);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		dataDTO.setBioValue("iris img");
		dataDTO.setBioSubType("LEFT");
		dataDTO.setBioType("IIR");
		dataDTO.setDeviceProviderID("provider001");
		irisValue.setData(dataDTO);
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFace = new DataDTO();
		dataDTOFace.setBioValue("face img");
		dataDTOFace.setBioType("FID");
		dataDTOFace.setDeviceProviderID("provider001");
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
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setOtp(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setOtp("123456");
		authRequestDTO.setRequest(requestDTO);
		return authRequestDTO;
	}

	private AuthRequestDTO getSPinRequest() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPin(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setStaticPin("123456");
		authRequestDTO.setRequest(requestDTO);
		return authRequestDTO;
	}

	private AuthRequestDTO getAuthRequestDtoValue() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
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
		/* Age */
		List<IdentityInfoDTO> age = new ArrayList<>();
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
		/* Pin code */
		List<IdentityInfoDTO> pincodeList = new ArrayList<>();
		IdentityInfoDTO pincode = new IdentityInfoDTO();
		pincode.setLanguage("ara");
		pincode.setValue("345323");
		pincodeList.add(pincode);
		identity.setPinCode(pincodeList);

		request.setDemographics(identity);
		authRequestDTO.setRequest(request);
		return authRequestDTO;
	}

}
