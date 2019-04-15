package io.mosip.authentication.service.impl.indauth.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.BaseAuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.BioIdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.DataDTO;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.service.config.IDAMappingConfig;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.impl.indauth.service.bio.BioAuthType;
import io.mosip.authentication.service.impl.otpgen.validator.OTPRequestValidator;
import io.mosip.authentication.service.integration.MasterDataManager;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;

/**
 * Test class for {@link BaseAuthRequestValidator}.
 *
 * @author Rakesh Roshan
 */

@RunWith(SpringRunner.class)
@WebMvcTest
@Import(IDAMappingConfig.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, TemplateManagerBuilderImpl.class })
public class BaseAuthRequestValidatorTest {


	/** The validator. */
	@Mock
	private SpringValidatorAdapter validator;

	/** The auth request DTO. */
	@Mock
	AuthRequestDTO authRequestDTO;

	/** The environment. */
	@Autowired
	protected Environment environment;

	/** The base auth request validator. */
	@InjectMocks
	BaseAuthRequestValidator baseAuthRequestValidator;

	/** The id info helper. */
	@InjectMocks
	IdInfoHelper idInfoHelper;

	/** The id mapping config. */
	@Autowired
	private IDAMappingConfig idMappingConfig;

	/** The error. */
	Errors error;

	@Mock
	private MasterDataManager masterDataManager;

	/**
	 * Before.
	 */
	@Before
	public void before() {
		error = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		ReflectionTestUtils.setField(baseAuthRequestValidator, "idInfoHelper", idInfoHelper);
		ReflectionTestUtils.setField(baseAuthRequestValidator, "env", environment);
		ReflectionTestUtils.setField(idInfoHelper, "environment", environment);
		ReflectionTestUtils.setField(idInfoHelper, "idMappingConfig", idMappingConfig);
		ReflectionTestUtils.setField(baseAuthRequestValidator, "masterDataManager", masterDataManager);
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "initialize");

	}

	@Test
	public void testSupportTrue() {
		assertTrue(baseAuthRequestValidator.supports(AuthRequestDTO.class));
	}

	@Test
	public void testSupportFalse() {
		assertFalse(baseAuthRequestValidator.supports(OTPRequestValidator.class));
	}

	/**
	 * Test validate version and id.
	 */
	@Test
	public void testValidateVersionAndId() {
		BaseAuthRequestDTO baseAuthRequestDTO = new BaseAuthRequestDTO();
		baseAuthRequestDTO.setId("123456");
		baseAuthRequestDTO.setVersion("1.0");
		baseAuthRequestValidator.validate(baseAuthRequestDTO, error);
		assertFalse(error.hasErrors());
	}

	/**
	 * Test validate id has id no error.
	 */
	@Test
	public void testValidateId_HasId_NoError() {

		String id = "12345678";
		baseAuthRequestValidator.validateId(id, error);
		assertFalse(error.hasErrors());
	}

	/**
	 * Test validate id no id has error.
	 */
	@Test
	public void testValidateId_NoId_HasError() {

		String id = null;
		baseAuthRequestValidator.validateId(id, error);
		assertTrue(error.hasErrors());
	}

	/**
	 * Test validate bio details if bio info is null has error.
	 */
	@Test
	public void testValidateBioDetails_IfBioInfoIsNull_hasError() {

		authRequestDTO = getAuthRequestDTO();
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(true);
		authRequestDTO.setRequestedAuth(authType);

		RequestDTO request = new RequestDTO();
		List<BioIdentityInfoDTO> bioInfo = new ArrayList<BioIdentityInfoDTO>();
		BioIdentityInfoDTO bioIdentity = new BioIdentityInfoDTO();
		bioIdentity.setData(null);
		bioInfo.add(bioIdentity);
		request.setBiometrics(bioInfo);
		authRequestDTO.setRequest(request);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateBioMetadataDetails", authRequestDTO, error);
		assertTrue(error.hasErrors());

	}

	/**
	 * Test validate bio details if bio info is not null but bio info is empty has
	 * error.
	 */
	@Test
	public void testValidateBioDetails_IfBioInfoIsNotNullButBioInfoIsEmpty_hasError() {

		authRequestDTO = getAuthRequestDTO();
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(true);
		authRequestDTO.setRequestedAuth(authType);

		List<BioIdentityInfoDTO> bioInfoList = new ArrayList<BioIdentityInfoDTO>();
		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger = new DataDTO();
		dataDTOFinger.setBioValue("finger");
		dataDTOFinger.setBioSubType("Thumb");
		dataDTOFinger.setBioType(BioAuthType.FGR_IMG.getType());
		dataDTOFinger.setDeviceProviderID("provider001");
		fingerValue.setData(dataDTOFinger);
		bioInfoList.add(fingerValue);
		RequestDTO dto = new RequestDTO();
		dto.setBiometrics(null);
		authRequestDTO.setRequest(dto);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateBioMetadataDetails", authRequestDTO, error);
		assertTrue(error.hasErrors());

	}

	/**
	 * Test validate bio details if bio info is not null but bio info is empty.
	 */
	@Test
	public void testValidateBioDetails_IfBioInfoIsNotNullButBioInfoIsEmpty() {

		authRequestDTO = getAuthRequestDTO();
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(false);
		authRequestDTO.setRequestedAuth(authType);

		List<BioIdentityInfoDTO> bioInfoList = new ArrayList<BioIdentityInfoDTO>();
		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger = new DataDTO();
		dataDTOFinger.setBioValue("finger");
		dataDTOFinger.setBioSubType("Thumb");
		dataDTOFinger.setBioType("");
		dataDTOFinger.setDeviceProviderID("provider001");
		fingerValue.setData(dataDTOFinger);
		bioInfoList.add(fingerValue);
		RequestDTO dto = new RequestDTO();
		dto.setBiometrics(bioInfoList);
		authRequestDTO.setRequest(dto);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateBioMetadataDetails", authRequestDTO, error);
		assertFalse(error.hasErrors());

	}

	/**
	 * Test validate bio details.
	 */
	@Test
	public void testValidateBioDetails() {

		authRequestDTO = getAuthRequestDTO();
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(true);
		authRequestDTO.setRequestedAuth(authType);

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		dataDTO.setBioValue("finger");
		dataDTO.setBioSubType("LEFT_THUMB");
		dataDTO.setBioType("FIR");
		dataDTO.setDeviceProviderID("provider001");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		DataDTO dataDTO1 = new DataDTO();
		dataDTO1.setBioValue("iris img");
		dataDTO1.setBioSubType("LEFT");
		dataDTO1.setBioType("IIR");
		dataDTO1.setDeviceProviderID("provider001");
		irisValue.setData(dataDTO1);
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		DataDTO dataDTO2 = new DataDTO();
		dataDTO2.setBioValue("face img");
		dataDTO2.setBioType("FID");
		dataDTO2.setBioSubType("Face");
		dataDTO2.setDeviceProviderID("provider001");
		faceValue.setData(dataDTO2);

		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateBioMetadataDetails", authRequestDTO, error);
		assertFalse(error.hasErrors());

	}

	/**
	 * Test validate finger no errors.
	 */
	@Test
	public void testValidateFinger_NoErrors() {
		authRequestDTO = getAuthRequestDTO();

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		dataDTO.setBioValue("finger");
		dataDTO.setBioSubType("Thumb");
		dataDTO.setBioType("FIR");
		dataDTO.setDeviceProviderID("provider001");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		dataDTO.setBioValue("iris img");
		dataDTO.setBioSubType("LEFT");
		dataDTO.setBioType("IIR");
		dataDTO.setDeviceProviderID("provider001");
		irisValue.setData(dataDTO);
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		dataDTO.setBioValue("face img");
		dataDTO.setBioSubType("Thumb");
		dataDTO.setBioType("FID");
		dataDTO.setDeviceProviderID("provider001");
		faceValue.setData(dataDTO);

		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);

		List<DataDTO> data = new ArrayList<DataDTO>();
		data.add(dataDTO);
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateFinger", authRequestDTO, data, error);
		assertFalse(error.hasErrors());
	}

	/**
	 * Test validate iris.
	 */
	@Test
	public void testValidateIris() {
		authRequestDTO = getAuthRequestDTO();

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		dataDTO.setBioValue("finger");
		dataDTO.setBioSubType("Thumb");
		dataDTO.setBioType("FIR");
		dataDTO.setDeviceProviderID("provider001");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		dataDTO.setBioValue("iris img");
		dataDTO.setBioSubType("LEFT");
		dataDTO.setBioType("IIR");
		dataDTO.setDeviceProviderID("provider001");
		irisValue.setData(dataDTO);
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		dataDTO.setBioValue("face img");
		dataDTO.setBioSubType("Thumb");
		dataDTO.setBioType("FID");
		dataDTO.setDeviceProviderID("provider001");
		faceValue.setData(dataDTO);
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);

		List<DataDTO> bioInfoList = new ArrayList<DataDTO>();
		bioInfoList.add(dataDTO);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateIris", authRequestDTO, bioInfoList, error);
		assertFalse(error.hasErrors());

	}

	/**
	 * Test validate irisright eye.
	 */
	@Test
	public void testValidateIrisrightEye() {
		authRequestDTO = getAuthRequestDTO();

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		dataDTO.setBioValue("finger");
		dataDTO.setBioSubType("Thumb");
		dataDTO.setBioType("FIR");
		dataDTO.setDeviceProviderID("provider001");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		dataDTO.setBioValue("iris img");
		dataDTO.setBioSubType("right");
		dataDTO.setBioType("IIR");
		dataDTO.setDeviceProviderID("provider001");
		irisValue.setData(dataDTO);
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		dataDTO.setBioValue("face img");
		dataDTO.setBioSubType("Thumb");
		dataDTO.setBioType("FID");
		dataDTO.setDeviceProviderID("provider001");
		faceValue.setData(dataDTO);
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);

		List<DataDTO> bioInfoList = new ArrayList<DataDTO>();
		bioInfoList.add(dataDTO);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateIris", authRequestDTO, bioInfoList, error);
		assertFalse(error.hasErrors());

	}

	/**
	 * Test validate face.
	 */
	@Test
	public void testValidateFace() {

		authRequestDTO = getAuthRequestDTO();
		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		dataDTO.setBioValue("finger");
		dataDTO.setBioSubType("Thumb");
		dataDTO.setBioType("FIR");
		dataDTO.setDeviceProviderID("provider001");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		dataDTO.setBioValue("iris img");
		dataDTO.setBioSubType("left");
		dataDTO.setBioType("IIR");
		dataDTO.setDeviceProviderID("provider001");
		irisValue.setData(dataDTO);
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		DataDTO faceData = new DataDTO();
		faceData.setBioValue("face img");
		faceData.setBioSubType("face");
		faceData.setBioType("FID");
		faceData.setDeviceProviderID("provider001");
		faceValue.setData(faceData);
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);

		List<DataDTO> bioInfoList = new ArrayList<DataDTO>();
		bioInfoList.add(faceData);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateFace", authRequestDTO, bioInfoList, error);
		assertFalse(error.hasErrors());

	}
	
	
	
	/**
	 * Test validate face if more than one face data is present.
	 */
	@Test
	public void testValidateFaceReq() {

		authRequestDTO = getAuthRequestDTO();
		
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		DataDTO faceData = new DataDTO();
		faceData.setBioValue("face img");
		faceData.setBioSubType("face");
		faceData.setBioType("FID");
		faceData.setDeviceProviderID("provider001");
		faceValue.setData(faceData);
		
		BioIdentityInfoDTO faceValue1 = new BioIdentityInfoDTO();
		
		faceData.setBioValue("face img");
		faceData.setBioSubType("face");
		faceData.setBioType("FID");
		faceData.setDeviceProviderID("provider001");
		faceValue1.setData(faceData);
		List<BioIdentityInfoDTO> faceIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		faceIdentityInfoDtoList.add(faceValue);
		faceIdentityInfoDtoList.add(faceValue1);
		IdentityDTO identitydto = new IdentityDTO();
		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(faceIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);
        List<DataDTO> bioInfoList = new ArrayList<DataDTO>();
		bioInfoList.add(faceData);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateFace", authRequestDTO, bioInfoList, error);
		assertTrue(error.hasErrors());

	}

	/**
	 * Test check atleast one finger request available has error.
	 */
	@Test
	public void testCheckAtleastOneFingerRequestAvailable_hasError() {
		authRequestDTO = getAuthRequestDTO();

		authRequestDTO.setRequest(null);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkAtleastOneFingerRequestAvailable",
				authRequestDTO, error, "FMR");
		assertTrue(error.hasErrors());
	}

	/**
	 * Test check atleast one finger request available.
	 */
	@Test
	public void testCheckAtleastOneFingerRequestAvailable() {
		authRequestDTO = getAuthRequestDTO();

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		dataDTO.setBioValue("finger");
		dataDTO.setBioSubType("Thumb");
		dataDTO.setBioType("FMR");
		dataDTO.setDeviceProviderID("provider001");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		DataDTO faceData = new DataDTO();
		faceData.setBioValue("face img");
		faceData.setBioSubType("face");
		faceData.setBioType("FID");
		faceData.setDeviceProviderID("provider001");
		faceValue.setData(faceData);

		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(faceValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkAtleastOneFingerRequestAvailable",
				authRequestDTO, error, "FMR");
		assertFalse(error.hasErrors());
	}

	/**
	 * Test no iris request available has error.
	 */
	@Test
	public void testNoIrisRequestAvailable_HasError() {

		authRequestDTO = getAuthRequestDTO();

		authRequestDTO.setRequest(null);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkAtleastOneIrisRequestAvailable",
				authRequestDTO, error);

		assertTrue(error.hasErrors());

	}

	/**
	 * Test atleast one iris request available no error.
	 */
	@Test
	public void testAtleastOneIrisRequestAvailable_NoError() {

		authRequestDTO = getAuthRequestDTO();

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		dataDTO.setBioValue("finger");
		dataDTO.setBioSubType("Thumb");
		dataDTO.setBioType("FIR");
		dataDTO.setDeviceProviderID("provider001");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		dataDTO.setBioValue("iris img");
		dataDTO.setBioSubType("left");
		dataDTO.setBioType("IIR");
		dataDTO.setDeviceProviderID("provider001");
		irisValue.setData(dataDTO);
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		DataDTO faceData = new DataDTO();
		faceData.setBioValue("face img");
		faceData.setBioSubType("face");
		faceData.setBioType("FID");
		faceData.setDeviceProviderID("provider001");
		faceValue.setData(faceData);

		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkAtleastOneIrisRequestAvailable",
				authRequestDTO, error);

		assertFalse(error.hasErrors());

	}

	/**
	 * Test no face request available.
	 */
	@Test
	public void test_NoFaceRequestAvailable() {
		authRequestDTO = getAuthRequestDTO();

		authRequestDTO.setRequest(null);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkAtleastOneFaceRequestAvailable",
				authRequestDTO, error);
		assertTrue(error.hasErrors());
	}

	/**
	 * Test atleast one face request available.
	 */
	@Test
	public void test_AtleastOneFaceRequestAvailable() {
		authRequestDTO = getAuthRequestDTO();

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		dataDTO.setBioValue("finger");
		dataDTO.setBioSubType("Thumb");
		dataDTO.setBioType("FIR");
		dataDTO.setDeviceProviderID("provider001");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		dataDTO.setBioValue("iris img");
		dataDTO.setBioSubType("left");
		dataDTO.setBioType("IIR");
		dataDTO.setDeviceProviderID("provider001");
		irisValue.setData(dataDTO);
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		DataDTO faceData = new DataDTO();
		faceData.setBioValue("face img");
		faceData.setBioSubType("face");
		faceData.setBioType("FID");
		faceData.setDeviceProviderID("provider001");
		faceValue.setData(faceData);

		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkAtleastOneFaceRequestAvailable",
				authRequestDTO, error);
		assertFalse(error.hasErrors());
	}

	/**
	 * Test any id info available.
	 */
	@Test
	public void testAnyIdInfoAvailable() {
		authRequestDTO = getAuthRequestDTO();

		IdentityDTO identitydto = new IdentityDTO();
		RequestDTO request = new RequestDTO();

		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue("V");
		List<IdentityInfoDTO> dobType = new ArrayList<IdentityInfoDTO>();
		dobType.add(identityInfoDTO);
		identitydto.setDobType(dobType);
		request.setDemographics(identitydto);
		authRequestDTO.setRequest(request);

		Function<IdentityDTO, List<IdentityInfoDTO>> fun = new Function<IdentityDTO, List<IdentityInfoDTO>>() {
			@Override
			public List<IdentityInfoDTO> apply(IdentityDTO t) {
				return t.getDobType();
			}
		};
		@SuppressWarnings("unchecked")
		boolean checkAnyIdInfoAvailable = baseAuthRequestValidator.checkAnyIdInfoAvailable(authRequestDTO, fun);
		assertTrue(checkAnyIdInfoAvailable);
	}

	/**
	 * Test any id info not available.
	 */
	@Test
	public void testAnyIdInfoNotAvailable() {
		authRequestDTO = getAuthRequestDTO();

		IdentityDTO identitydto = new IdentityDTO();
		RequestDTO request = new RequestDTO();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue("");
		List<IdentityInfoDTO> dobType = new ArrayList<IdentityInfoDTO>();
		dobType.add(identityInfoDTO);
		identitydto.setDobType(dobType);
		request.setDemographics(identitydto);
		authRequestDTO.setRequest(null);

		Function<IdentityDTO, List<IdentityInfoDTO>> fun = new Function<IdentityDTO, List<IdentityInfoDTO>>() {
			@Override
			public List<IdentityInfoDTO> apply(IdentityDTO t) {
				return t.getDobType();
			}
		};
		@SuppressWarnings("unchecked")
		boolean checkAnyIdInfoAvailable = baseAuthRequestValidator.checkAnyIdInfoAvailable(authRequestDTO, fun);
		assertFalse(checkAnyIdInfoAvailable);
	}

	/**
	 * Test is bio type available bio type availabe return true.
	 */
	@Test
	public void testIsBioTypeAvailable_BioTypeAvailabe_ReturnTrue() {
		List<DataDTO> bioInfoList = new ArrayList<DataDTO>();
		DataDTO dataDTO = new DataDTO();
		dataDTO.setBioValue("finger");
		dataDTO.setBioSubType("Thumb");
		dataDTO.setBioType(BioAuthType.FACE_IMG.getType());
		dataDTO.setDeviceProviderID("provider001");
		bioInfoList.add(dataDTO);
		boolean isBioTypeAvailable = ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "isAvailableBioType",
				bioInfoList, BioAuthType.FACE_IMG);
		assertTrue(isBioTypeAvailable);

	}

	/**
	 * Test is bio type available bio type not availabe return false.
	 */
	@Test
	public void testIsBioTypeAvailable_BioTypeNotAvailabe_ReturnFalse() {
		List<DataDTO> bioInfoList = new ArrayList<DataDTO>();
		DataDTO dataDTO = new DataDTO();
		dataDTO.setBioValue("finger");
		dataDTO.setBioSubType("Thumb");
		dataDTO.setBioType(BioAuthType.IRIS_COMP_IMG.getType());
		dataDTO.setDeviceProviderID("provider001");
		bioInfoList.add(dataDTO);
		boolean isBioTypeAvailable = ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "isAvailableBioType",
				bioInfoList, BioAuthType.FACE_IMG);
		assertFalse(isBioTypeAvailable);

	}

	/**
	 * Test is duplicate bio type true.
	 */
	@Test
	public void testIsDuplicateBioType_True() {

		authRequestDTO = getAuthRequestDTO();

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		dataDTO.setBioValue("finger");
		dataDTO.setBioSubType("Thumb");
		dataDTO.setBioType("IIR");
		dataDTO.setDeviceProviderID("provider001");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		DataDTO dataDTOIris = new DataDTO();
		dataDTOIris.setBioValue("iris img");
		dataDTOIris.setBioSubType("left");
		dataDTOIris.setBioType("IIR");
		dataDTOIris.setDeviceProviderID("provider001");
		irisValue.setData(dataDTOIris);
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFace = new DataDTO();
		dataDTOFace.setBioValue("face img");
		dataDTOFace.setBioSubType("Thumb");
		dataDTOFace.setBioType("IIR");
		dataDTOFace.setDeviceProviderID("provider001");
		faceValue.setData(dataDTOFace);

		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);

		boolean isDuplicateBioType = ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "isDuplicateBioType",
				authRequestDTO, BioAuthType.IRIS_IMG);
		assertTrue(isDuplicateBioType);
	}

	/**
	 * Test is duplicate bio type false.
	 */
	@Test
	public void testIsDuplicateBioType_False() {
		authRequestDTO = getAuthRequestDTO();

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		dataDTO.setBioValue("finger");
		dataDTO.setBioSubType("Thumb");
		dataDTO.setBioType("IIR");
		dataDTO.setDeviceProviderID("provider001");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		DataDTO dataDTOIris = new DataDTO();
		dataDTOIris.setBioValue("iris img");
		dataDTOIris.setBioSubType("left");
		dataDTOIris.setBioType("");
		dataDTOIris.setDeviceProviderID("provider001");
		irisValue.setData(dataDTOIris);

		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);

		boolean isDuplicateBioType = ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "isDuplicateBioType",
				authRequestDTO, BioAuthType.IRIS_IMG);
		assertTrue(isDuplicateBioType);
	}

	/**
	 * Test is duplicate bio type iris.
	 */
	@Test
	public void testIsDuplicateBioTypeIris() {
		authRequestDTO = getAuthRequestDTO();

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		dataDTO.setBioValue("finger");
		dataDTO.setBioSubType("Thumb");
		dataDTO.setBioType("test");
		dataDTO.setDeviceProviderID("provider001");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		DataDTO dataDTOIris = new DataDTO();
		dataDTOIris.setBioValue("iris img");
		dataDTOIris.setBioSubType("left");
		dataDTOIris.setBioType("");
		dataDTOIris.setDeviceProviderID("provider001");
		irisValue.setData(dataDTOIris);

		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);

		boolean isDuplicateBioType = ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "isDuplicateBioType",
				authRequestDTO, BioAuthType.FGR_IMG);
		assertTrue(isDuplicateBioType);
	}

	/**
	 * Test validate finger request count any info is equal to one or less than one
	 * finger count not exceeding 2.
	 */
	@Test
	public void testValidateFingerRequestCount_anyInfoIsEqualToOneOrLessThanOne_fingerCountNotExceeding2() {
		authRequestDTO = getAuthRequestDTO();

		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue("fingerImage");
		List<IdentityInfoDTO> finger = new ArrayList<IdentityInfoDTO>();
		finger.add(identityInfoDTO);

		IdentityDTO identity = new IdentityDTO();

		RequestDTO request = new RequestDTO();
		request.setDemographics(identity);
		authRequestDTO.setRequest(request);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateFingerRequestCount", authRequestDTO, error,
				"FMR");
		assertFalse(error.hasErrors());
	}

	/**
	 * Test validate finger request count any info is equal to one or less than one
	 * finger count exceeding 2.
	 */
	@Test
	public void testValidateFingerRequestCount_anyInfoIsEqualToOneOrLessThanOne_fingerCountExceeding2() {
		authRequestDTO = getAuthRequestDTO();
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(true);
		authRequestDTO.setRequestedAuth(authType);

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		dataDTO.setBioValue("finger");
		dataDTO.setBioSubType("Thumb");
		dataDTO.setBioType("FMR");
		dataDTO.setDeviceProviderID("provider001");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		DataDTO dataDTOIris = new DataDTO();
		dataDTOIris.setBioValue("finger");
		dataDTOIris.setBioSubType("Thumb");
		dataDTOIris.setBioType("FMR");
		dataDTOIris.setDeviceProviderID("provider001");
		irisValue.setData(dataDTOIris);
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFace = new DataDTO();
		dataDTOFace.setBioValue("face img");
		dataDTOFace.setBioSubType("Thumb");
		dataDTOFace.setBioType("FID");
		dataDTOFace.setDeviceProviderID("provider001");
		faceValue.setData(dataDTOFace);

		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateFingerRequestCount", authRequestDTO, error,
				"FMR");
		assertTrue(error.hasErrors());
	}

	/**
	 * Test validate finger request count any info is more than one.
	 */
	@Test
	public void testValidateFingerRequestCount_anyInfoIsMoreThanOne() {
		authRequestDTO = getAuthRequestDTO();
		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		dataDTO.setBioValue("finger");
		dataDTO.setBioSubType("Thumb");
		dataDTO.setBioType("FIR");
		dataDTO.setDeviceProviderID("provider001");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		DataDTO fingerValue2 = new DataDTO();
		fingerValue2.setBioValue("finger");
		fingerValue2.setBioSubType("INDEXFINGER");
		fingerValue2.setBioType("FIR");
		fingerValue2.setDeviceProviderID("provider001");
		irisValue.setData(fingerValue2);
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateFingerRequestCount", authRequestDTO, error,
				"FIR");
		assertTrue(error.hasErrors());
	}

	/**
	 * Test validate finger request count finger count exceeding 10.
	 */
	@Test
	public void testValidateFingerRequestCount_fingerCountExceeding10() {
		authRequestDTO = getAuthRequestDTO();

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		dataDTO.setBioValue("finger");
		dataDTO.setBioSubType("Thumb");
		dataDTO.setBioType("FMR");
		dataDTO.setDeviceProviderID("provider001");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		DataDTO fingerValue2 = new DataDTO();
		fingerValue2.setBioValue("finger");
		fingerValue2.setBioSubType("INDEXFINGER");
		fingerValue2.setBioType("FMR");
		fingerValue2.setDeviceProviderID("provider001");
		irisValue.setData(fingerValue2);
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(irisValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateFingerRequestCount", authRequestDTO, error,
				"FMR");
		assertTrue(error.hasErrors());
	}

	/**
	 * Test validate iris request count.
	 */
	@Test
	public void testValidateIrisRequestCount() {
		authRequestDTO = getAuthRequestDTO();
		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		dataDTO.setBioValue("iris img");
		dataDTO.setBioSubType("left");
		dataDTO.setBioType("IIR");
		dataDTO.setDeviceProviderID("provider001");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		DataDTO fingerValue2 = new DataDTO();
		fingerValue2.setBioValue("finger");
		fingerValue2.setBioSubType("INDEXFINGER");
		fingerValue2.setBioType("FMR");
		fingerValue2.setDeviceProviderID("provider001");
		irisValue.setData(fingerValue2);
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(fingerValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateIrisRequestCount", authRequestDTO, error);

		assertFalse(error.hasErrors());

	}

	/**
	 * Test validate iris request count has left eye request more than one.
	 */
	@Test
	public void testValidateIrisRequestCount_hasLeftEyeRequestMoreThanOne() {
		authRequestDTO = getAuthRequestDTO();

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		dataDTO.setBioValue("iris img");
		dataDTO.setBioSubType("left");
		dataDTO.setBioType("IIR");
		dataDTO.setDeviceProviderID("provider001");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		DataDTO fingerValue2 = new DataDTO();
		fingerValue2.setBioValue("iris img");
		fingerValue2.setBioSubType("left");
		fingerValue2.setBioType("IIR");
		fingerValue2.setDeviceProviderID("provider001");
		irisValue.setData(fingerValue2);
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateIrisRequestCount", authRequestDTO, error);
		assertTrue(error.hasErrors());

	}

//	/**
//	 * Test check OTP auth has no error.
//	 */
//	@Test
//	public void testCheckOTPAuth_HasNoError() {
//		String otp = "456789";
//		AuthRequestDTO authRequestDTO = getAuthRequestDTO();
//		RequestDTO request = new RequestDTO();
//		request.setOtp(otp);
//		authRequestDTO.setRequest(request);
//
//		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkOTPAuth", authRequestDTO, error);
//		assertFalse(error.hasErrors());
//	}
//
//	/**
//	 * Test check OTP auth has null value has error.
//	 */
//	@Test
//	public void testCheckOTPAuth_HasNullValue_HasError() {
//		AuthRequestDTO authRequestDTO = getAuthRequestDTO();
//
//		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkOTPAuth", authRequestDTO, error);
//		assertTrue(error.hasErrors());
//	}

//	/**
//	 * Test check OTP auth has empty OT P has error.
//	 */
//	@Test
//	public void testCheckOTPAuth_HasEmptyOTP_HasError() {
//		String otp = "";
//		AuthRequestDTO authRequestDTO = getAuthRequestDTO();
//		RequestDTO request = new RequestDTO();
//		request.setOtp(otp);
//		authRequestDTO.setRequest(request);
//		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkOTPAuth", authRequestDTO, error);
//		assertTrue(error.hasErrors());
//	}

//	/**
//	 * Test get otp value.
//	 */
//	@Test
//	public void testGetOtpValue() {
//
//		String otp = "456789";
//		AuthRequestDTO authRequestDTO = getAuthRequestDTO();
//		RequestDTO request = new RequestDTO();
//		request.setOtp(otp);
//		authRequestDTO.setRequest(request);
//
//		Optional<String> isOtp = ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "getOtpValue",
//				authRequestDTO);
//		assertTrue(isOtp.isPresent());
//	}

	/**
	 * Test validate email validate email is true.
	 */
	@Test
	public void testValidateEmail_ValidateEmail_IsTrue() {
		AuthRequestDTO authRequestDTO = getAuthRequestDTO();

		RequestDTO request = new RequestDTO();
		IdentityDTO identity = new IdentityDTO();

		identity.setEmailId("sample@sample.com");
		request.setDemographics(identity);
		authRequestDTO.setRequest(request);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateEmail", authRequestDTO, error);
		assertFalse(error.hasErrors());
	}

	/**
	 * Test validate email validate email is false.
	 */
	@Test
	public void testValidateEmail_ValidateEmail_IsFalse() {
		AuthRequestDTO authRequestDTO = getAuthRequestDTO();

		RequestDTO request = new RequestDTO();
		IdentityDTO identity = new IdentityDTO();

		identity.setEmailId("sample5878");

		identity.setEmailId("sample5878");
		request.setDemographics(identity);
		authRequestDTO.setRequest(request);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateEmail", authRequestDTO, error);
		assertTrue(error.hasErrors());
	}

	/**
	 * Test validate phone validate phone is true.
	 */
	@Test
	public void testValidatePhone_ValidatePhone_IsTrue() {

		IdentityDTO phone = new IdentityDTO();
		phone.setPhoneNumber("8975476599");

		RequestDTO phoneRequest = new RequestDTO();
		phoneRequest.setDemographics(phone);
		AuthRequestDTO authRequestDTO = getAuthRequestDTO();
		authRequestDTO.setRequest(phoneRequest);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validatePhone", authRequestDTO, error);
		assertFalse(error.hasErrors());

	}

	/**
	 * Test validate phone validate phone is false.
	 */
	@Test
	public void testValidatePhone_ValidatePhone_IsFalse() {

		IdentityDTO phone = new IdentityDTO();
		phone.setPhoneNumber("8975476lghfhhj");

		RequestDTO phoneRequest = new RequestDTO();
		phoneRequest.setDemographics(phone);
		AuthRequestDTO authRequestDTO = getAuthRequestDTO();
		authRequestDTO.setRequest(phoneRequest);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validatePhone", authRequestDTO, error);
		assertTrue(error.hasErrors());

	}

	/**
	 * Gets the auth request DTO.
	 *
	 * @return the auth request DTO
	 */
	// ----------- Supporting method ---------------
	private AuthRequestDTO getAuthRequestDTO() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString());

		return authRequestDTO;
	}

	/**
	 * Test valid auth request.
	 */
	@Test
	public void testValidAuthRequest() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(environment.getProperty("mosip.primary-language"));
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(environment.getProperty("mosip.secondary-language"));
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(environment.getProperty("mosip.secondary-language"));
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkDemoAuth", authRequestDTO, error);
		assertFalse(errors.hasErrors());
	}

	/**
	 * Test in valid auth request secondary language.
	 */

	@Test
	public void testInValidAuthRequest_SecondaryLanguage() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(environment.getProperty("mosip.secondary-language"));
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(environment.getProperty("mosip.secondary-language"));
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(environment.getProperty("mosip.secondary-language"));
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkDemoAuth", authRequestDTO, error);
		assertFalse(errors.hasErrors());
	}

	/**
	 * Test in valid auth request.
	 */

	@Test
	public void testInValidAuthRequest() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(null);
		idInfoDTO.setValue(null);
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(null);
		idInfoDTO1.setValue(null);
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(environment.getProperty("mosip.secondary-language"));
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkDemoAuth", authRequestDTO, error);
		assertFalse(errors.hasErrors());
	}

	/**
	 * Test valid auth request 2.
	 */

	@Test
	public void testValidAuthRequest2() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		authTypeDTO.setBio(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(environment.getProperty("mosip.primary-language"));
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(environment.getProperty("mosip.secondary-language"));
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);

		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setDob("25/11/1990");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(environment.getProperty("mosip.secondary-language"));
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		idDTO.setName(idInfoList);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkDemoAuth", authRequestDTO, error);
		assertFalse(error.hasErrors());
	}

	/**
	 * Test validate iris request count zero.
	 */
	@Test
	public void testValidateIrisRequestCountZero() {
		authRequestDTO = getAuthRequestDTO();

		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue("");
		List<IdentityInfoDTO> leftEye = new ArrayList<IdentityInfoDTO>();
		leftEye.add(identityInfoDTO);

		IdentityDTO identity = new IdentityDTO();
//		identity.setLeftEye(leftEye);
//		identity.setRightEye(leftEye);

		RequestDTO request = new RequestDTO();
		request.setDemographics(identity);
		authRequestDTO.setRequest(request);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateIrisRequestCount", authRequestDTO, error);
		assertFalse(error.hasErrors());

	}

	/**
	 * Test validate multi iris request.
	 */
	@Test
	public void testValidateMultiIrisRequest() {
		authRequestDTO = getAuthRequestDTO();
		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		dataDTO.setBioValue("iris img");
		dataDTO.setBioSubType("left");
		dataDTO.setBioType("IIR");
		dataDTO.setDeviceProviderID("provider001");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		DataDTO fingerValue2 = new DataDTO();
		fingerValue2.setBioValue("finger");
		fingerValue2.setBioSubType("right");
		fingerValue2.setBioType("IIR");
		fingerValue2.setDeviceProviderID("provider001");
		irisValue.setData(fingerValue2);
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(fingerValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateMultiIrisValue", authRequestDTO, error);
		assertFalse(error.hasErrors());

	}

	/**
	 * Test invalid multi iris request.
	 */
	@Test
	public void testInvalidMultiIrisRequest() {
		authRequestDTO = getAuthRequestDTO();
		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		dataDTO.setBioValue("iris img");
		dataDTO.setBioSubType("left");
		dataDTO.setBioType("IIR");
		dataDTO.setDeviceProviderID("provider001");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		DataDTO fingerValue2 = new DataDTO();
		fingerValue2.setBioValue("iris img");
		fingerValue2.setBioSubType("left");
		fingerValue2.setBioType("IIR");
		fingerValue2.setDeviceProviderID("provider001");
		irisValue.setData(fingerValue2);
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(fingerValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateMultiIrisValue", authRequestDTO, error);
		assertTrue(error.hasErrors());

	}

	/**
	 * Test validate adand full add.
	 */
	@Test
	public void testValidateAdandFullAdd() {
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setRequestedAuth(authTypeDTO);
		Set<String> availableAuthTypeInfos = new HashSet<>();
		availableAuthTypeInfos.add("address");
		availableAuthTypeInfos.add("fullAddress");
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateAdAndFullAd", availableAuthTypeInfos,
				error);
		assertTrue(error.hasErrors());
	}

	/**
	 * Test validate age.
	 */

	@Test
	public void testValidateAge_Valid() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(environment.getProperty("mosip.primary-language"));
		idInfoDTO.setValue("16");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(environment.getProperty("mosip.secondary-language"));
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setAge("25");
		idDTO.setDob("25/11/1990");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(environment.getProperty("mosip.secondary-language"));
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkAge", authRequestDTO, error);
		assertFalse(error.hasErrors());
	}

	@Test
	public void testValidateAge_InValid() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(environment.getProperty("mosip.primary-language"));
		idInfoDTO.setValue("16");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(environment.getProperty("mosip.secondary-language"));
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setAge("25/01/1998");
		idDTO.setDob("25/11/1990");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(environment.getProperty("mosip.secondary-language"));
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkAge", authRequestDTO, error);
		assertTrue(error.hasErrors());
	}

	/**
	 * Test validate DOB.
	 */

	@Test
	public void testValidateDOB_valid() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(environment.getProperty("mosip.primary-language"));
		idInfoDTO.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setDob("25/11/1990");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(environment.getProperty("mosip.secondary-language"));
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkDOB", authRequestDTO, error);
		assertFalse(error.hasErrors());
	}

	/**
	 * Test validate DOB.
	 */

	@Test
	public void testValidateDOB_Invalid() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(environment.getProperty("mosip.primary-language"));
		idInfoDTO.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setDob("25-11-1990");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(environment.getProperty("mosip.secondary-language"));
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkDOB", authRequestDTO, error);
		assertTrue(error.hasErrors());
	}

	@Test
	public void testPinDetails_success() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPin(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		String pin = "456789";
		RequestDTO request = new RequestDTO();
		request.setStaticPin(pin);
		authRequestDTO.setRequest(request);
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateAdditionalFactorsDetails", authRequestDTO,
				error);
	}

	@Test
	public void testPinDetails_isEmpty() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPin(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		String pin = "";
		RequestDTO request = new RequestDTO();
		request.setStaticPin(pin);
		authRequestDTO.setRequest(request);
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateAdditionalFactorsDetails", authRequestDTO,
				error);
	}

	@Test
	public void testPinDetails_isNull() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPin(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		String pin = null;
		RequestDTO request = new RequestDTO();
		request.setStaticPin(pin);
		authRequestDTO.setRequest(request);
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateAdditionalFactorsDetails", authRequestDTO,
				error);
	}

	@Test
	public void testPinDetails_invalidPinTypePinValue() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPin(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		String pin = "123e45";
		RequestDTO request = new RequestDTO();
		request.setStaticPin(pin);
		authRequestDTO.setRequest(request);
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateAdditionalFactorsDetails", authRequestDTO,
				error);
	}

	@Test
	public void testOTP_validOTPValue() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setOtp(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		String otp = "123445";
		RequestDTO request = new RequestDTO();
		request.setOtp(otp);
		authRequestDTO.setRequest(request);
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateAdditionalFactorsDetails", authRequestDTO,
				error);
	}

	@Test
	public void testOTP_InValidOTPValue() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setOtp(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		String otp = null;
		RequestDTO request = new RequestDTO();
		request.setOtp(otp);
		authRequestDTO.setRequest(request);
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateAdditionalFactorsDetails", authRequestDTO,
				error);
	}

	@Test
	public void testBioType() {
		List<DataDTO> bioInfoList = new ArrayList<DataDTO>();
		DataDTO dataDTO = new DataDTO();
		dataDTO.setBioValue("finger");
		dataDTO.setBioSubType("Thumb");
		dataDTO.setBioType("test");
		dataDTO.setDeviceProviderID("provider001");
		bioInfoList.add(dataDTO);
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateBioType", bioInfoList, error);
		assertTrue(error.hasErrors());
	}

	@Test
	public void testDOBType() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);

		List<IdentityInfoDTO> dobType = new ArrayList<IdentityInfoDTO>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setLanguage("fre");
		identityInfoDTO.setValue("C");

		dobType.add(identityInfoDTO);

		IdentityDTO dobIdentityDTO = new IdentityDTO();
		dobIdentityDTO.setDobType(dobType);

		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(dobIdentityDTO);
		authRequestDTO.setRequest(reqDTO);
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkDOBType", authRequestDTO, error);
		assertTrue(error.hasErrors());
	}

	@Test
	public void testInValidateBioDetails_DeviceProviderID() {

		authRequestDTO = getAuthRequestDTO();
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(true);
		authRequestDTO.setRequestedAuth(authType);

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		dataDTO.setBioValue("finger");
		dataDTO.setBioSubType("Thumb");
		dataDTO.setBioType("FIR");
		dataDTO.setDeviceProviderID(null);
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		dataDTO.setBioValue("iris img");
		dataDTO.setBioSubType("left");
		dataDTO.setBioType("IIR");
		dataDTO.setDeviceProviderID("provider001");
		irisValue.setData(dataDTO);
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		dataDTO.setBioValue("face img");
		dataDTO.setBioSubType("Thumb");
		dataDTO.setBioType("FID");
		dataDTO.setDeviceProviderID("provider001");
		faceValue.setData(dataDTO);

		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);
		RequestDTO reqDTO = new RequestDTO();
		authRequestDTO.setRequest(reqDTO);
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateBioMetadataDetails", authRequestDTO, error);
		assertTrue(error.hasErrors());

	}

	/**
	 * Test validate Gender.
	 * 
	 * @throws IdAuthenticationBusinessException
	 */

	@Test
	public void testValidateGender_valid() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(environment.getProperty("mosip.primary-language"));
		idInfoDTO.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		IdentityDTO idDTO = new IdentityDTO();
		IdentityInfoDTO idInfoDTOGender = new IdentityInfoDTO();
		idInfoDTOGender.setLanguage(environment.getProperty("mosip.secondary-language"));
		idInfoDTOGender.setValue("M");
		List<IdentityInfoDTO> idInfoListGender = new ArrayList<>();
		idInfoListGender.add(idInfoDTOGender);
		idDTO.setGender(idInfoListGender);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		Mockito.when(masterDataManager.fetchGenderType()).thenReturn(fetchGenderType());
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkGender", authRequestDTO, error);
		assertFalse(error.hasErrors());
	}

	/**
	 * Test validate Gender.
	 * 
	 * @throws IdAuthenticationBusinessException
	 */

	@Test
	public void testValidateGender_invalid() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(environment.getProperty("mosip.primary-language"));
		idInfoDTO.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		IdentityDTO idDTO = new IdentityDTO();
		IdentityInfoDTO idInfoDTOGender = new IdentityInfoDTO();
		idInfoDTOGender.setLanguage(environment.getProperty("mosip.secondary-language"));
		idInfoDTOGender.setValue("");
		List<IdentityInfoDTO> idInfoListGender = new ArrayList<>();
		idInfoListGender.add(idInfoDTOGender);
		idDTO.setGender(idInfoListGender);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		Mockito.when(masterDataManager.fetchGenderType()).thenReturn(fetchGenderTypeNull());
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkGender", authRequestDTO, error);
		assertTrue(error.hasErrors());
	}

	@Test
	public void testValidateGender_NullFetchType() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(environment.getProperty("mosip.primary-language"));
		idInfoDTO.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		IdentityDTO idDTO = new IdentityDTO();
		IdentityInfoDTO idInfoDTOGender = new IdentityInfoDTO();
		idInfoDTOGender.setLanguage(environment.getProperty("mosip.secondary-language"));
		idInfoDTOGender.setValue("");
		List<IdentityInfoDTO> idInfoListGender = new ArrayList<>();
		idInfoListGender.add(idInfoDTOGender);
		idDTO.setGender(idInfoListGender);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		Mockito.when(masterDataManager.fetchGenderType()).thenThrow(new IdAuthenticationBusinessException());
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkGender", authRequestDTO, error);
		assertTrue(error.hasErrors());
	}

	private Map<String, List<String>> fetchGenderType() {
		Map<String, List<String>> map = new HashMap<>();
		List<String> list = new ArrayList<>();
		list.add("M");
		map.put(environment.getProperty("mosip.secondary-language"), list);
		return map;
	}

	private Map<String, List<String>> fetchGenderTypeNull() {
		Map<String, List<String>> map = new HashMap<>();
		List<String> list = new ArrayList<>();
		list.add("Test");
		map.put(environment.getProperty("mosip.secondary-language"), list);
		return map;
	}
}
