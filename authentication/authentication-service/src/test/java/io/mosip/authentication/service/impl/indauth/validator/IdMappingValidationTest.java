package io.mosip.authentication.service.impl.indauth.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.indauth.AdditionalFactorsDTO;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.BioInfo;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.authentication.service.config.IDAMappingConfig;
import io.mosip.authentication.service.factory.IDAMappingFactory;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.impl.indauth.service.bio.BioAuthType;
import io.mosip.authentication.service.impl.indauth.service.demo.DOBType;
import io.mosip.authentication.service.integration.MasterDataManager;
import io.mosip.authentication.service.integration.MasterDataManagerTest;
import io.mosip.kernel.datavalidator.email.impl.EmailValidatorImpl;
import io.mosip.kernel.datavalidator.phone.impl.PhoneValidatorImpl;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, IDAMappingConfig.class,
		IDAMappingFactory.class })
public class IdMappingValidationTest {

	@InjectMocks
	private AuthRequestValidator authRequestValidator;

	@Mock
	EmailValidatorImpl emailValidatorImpl;

	/** phone Validator */
	@Mock
	PhoneValidatorImpl phoneValidatorImpl;

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
	public void TestInvalidOTPAuth() {
		AuthRequestDTO authRequestDTO = getOtpRequest();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(false);
		ReflectionTestUtils.invokeMethod(authRequestValidator, "checkOTPAuth", authRequestDTO, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void TestValidOTPAuth() {
		AuthRequestDTO authRequestDTO = getOtpRequest();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(true);
		ReflectionTestUtils.invokeMethod(authRequestValidator, "checkOTPAuth", authRequestDTO, errors);
		assertFalse(errors.hasErrors());
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
		Mockito.when(emailValidatorImpl.validateEmail(Mockito.any())).thenReturn(true);
		Mockito.when(phoneValidatorImpl.validatePhone(Mockito.any())).thenReturn(true);
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
	public void TestInValidAdditionalFactorsinOtp() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO requestedAuth = new AuthTypeDTO();
		requestedAuth.setOtp(true);
		authRequestDTO.setRequestedAuth(requestedAuth);
		RequestDTO request = new RequestDTO();
		AdditionalFactorsDTO additionalFactors = new AdditionalFactorsDTO();
		additionalFactors.setTotp("");
		request.setAdditionalFactors(additionalFactors);
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
		AdditionalFactorsDTO additionalFactors = new AdditionalFactorsDTO();
		additionalFactors.setStaticPin("");
		request.setAdditionalFactors(additionalFactors);
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
		List<BioInfo> bioMetadata = new ArrayList<>();
		BioInfo bioInfo = new BioInfo();
		bioInfo.setBioType(BioAuthType.FACE_IMG.getType());
		bioInfo.setDeviceId("123456");
		bioInfo.setDeviceProviderID("Mosip");
		bioMetadata.add(bioInfo);
		authRequestDTO.setBioMetadata(bioMetadata);
		return authRequestDTO;
	}

	private AuthRequestDTO getIrisDetails() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO requestedAuth = new AuthTypeDTO();
		requestedAuth.setBio(true);
		authRequestDTO.setRequestedAuth(requestedAuth);
		List<BioInfo> bioMetadata = new ArrayList<>();
		BioInfo bioInfo = new BioInfo();
		bioInfo.setBioType(BioAuthType.IRIS_COMP_IMG.getType());
		bioInfo.setDeviceId("123456");
		bioInfo.setDeviceProviderID("Mosip");
		bioMetadata.add(bioInfo);
		authRequestDTO.setBioMetadata(bioMetadata);
		return authRequestDTO;
	}

	private AuthRequestDTO getBioFingerDetails() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO requestedAuth = new AuthTypeDTO();
		requestedAuth.setBio(true);
		RequestDTO request = new RequestDTO();
		authRequestDTO.setRequestedAuth(requestedAuth);
		List<BioInfo> bioMetadata = new ArrayList<>();
		BioInfo bioInfo = new BioInfo();
		bioInfo.setBioType(BioAuthType.FGR_MIN.getType());
		bioInfo.setDeviceId("123456");
		bioInfo.setDeviceProviderID("Mosip");
		bioMetadata.add(bioInfo);
		authRequestDTO.setBioMetadata(bioMetadata);
		return authRequestDTO;
	}

	private AuthRequestDTO getOtpRequest() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setOtp(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		RequestDTO requestDTO = new RequestDTO();
		AdditionalFactorsDTO additionalFactors = new AdditionalFactorsDTO();
		additionalFactors.setTotp("123456");
		requestDTO.setAdditionalFactors(additionalFactors);
		authRequestDTO.setRequest(requestDTO);
		return authRequestDTO;
	}

	private AuthRequestDTO getSPinRequest() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPin(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		RequestDTO requestDTO = new RequestDTO();
		AdditionalFactorsDTO additionalFactors = new AdditionalFactorsDTO();
		additionalFactors.setStaticPin("123456");
		requestDTO.setAdditionalFactors(additionalFactors);
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
		identity.setAge(age);
		/* Dob */
		List<IdentityInfoDTO> dobList = new ArrayList<>();
		IdentityInfoDTO dob = new IdentityInfoDTO();
		dob.setLanguage("ara");
		dob.setValue("18/03/1999/");
		dobList.add(dob);
		identity.setDob(dobList);
		/* Dob type */
		List<IdentityInfoDTO> dobtypeList = new ArrayList<>();
		IdentityInfoDTO dob1 = new IdentityInfoDTO();
		dob1.setLanguage("ara");
		dob1.setValue(DOBType.VERIFIED.getDobType());
		dobtypeList.add(dob1);
		identity.setDobType(dobtypeList);
		/* E-mail */
		List<IdentityInfoDTO> emailList = new ArrayList<>();
		IdentityInfoDTO email = new IdentityInfoDTO();
		email.setLanguage("ara");
		email.setValue("testemailvalue@testmail.com");
		emailList.add(email);
		identity.setEmailId(emailList);
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
		List<IdentityInfoDTO> PhoneNumberList = new ArrayList<>();
		IdentityInfoDTO phoneNumber = new IdentityInfoDTO();
		phoneNumber.setLanguage("ara");
		phoneNumber.setValue("2002020012");
		PhoneNumberList.add(phoneNumber);
		identity.setPhoneNumber(PhoneNumberList);
		/* Pin code */
		List<IdentityInfoDTO> pincodeList = new ArrayList<>();
		IdentityInfoDTO pincode = new IdentityInfoDTO();
		pincode.setLanguage("ara");
		pincode.setValue("345323");
		pincodeList.add(pincode);
		identity.setPinCode(pincodeList);

		request.setIdentity(identity);
		authRequestDTO.setRequest(request);
		return authRequestDTO;
	}

}
