package io.mosip.authentication.service.impl.indauth.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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

import io.mosip.authentication.core.dto.indauth.AdditionalFactorsDTO;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.BaseAuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.BioIdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.BioInfo;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
import io.mosip.authentication.service.config.IDAMappingConfig;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.impl.indauth.service.bio.BioAuthType;
import io.mosip.authentication.service.impl.otpgen.validator.OTPRequestValidator;
import io.mosip.authentication.service.integration.MasterDataManager;
import io.mosip.kernel.core.datavalidator.exception.InvalidPhoneNumberException;
import io.mosip.kernel.core.datavalidator.exception.InvalideEmailException;
import io.mosip.kernel.datavalidator.email.impl.EmailValidatorImpl;
import io.mosip.kernel.datavalidator.phone.impl.PhoneValidatorImpl;
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

	/** The email validator impl. */
	@Mock
	EmailValidatorImpl emailValidatorImpl;

	/** The phone validator impl. */
	@Mock
	PhoneValidatorImpl phoneValidatorImpl;

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
		ReflectionTestUtils.setField(baseAuthRequestValidator, "emailValidatorImpl", emailValidatorImpl);
		ReflectionTestUtils.setField(baseAuthRequestValidator, "phoneValidatorImpl", phoneValidatorImpl);
		ReflectionTestUtils.setField(baseAuthRequestValidator, "idInfoHelper", idInfoHelper);
		ReflectionTestUtils.setField(baseAuthRequestValidator, "env", environment);
		ReflectionTestUtils.setField(idInfoHelper, "environment", environment);
		ReflectionTestUtils.setField(idInfoHelper, "idMappingConfig", idMappingConfig);
		ReflectionTestUtils.setField(baseAuthRequestValidator, "masterDataManager", masterDataManager);

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

		BioInfo bioinfo = new BioInfo();

		List<BioInfo> bioInfoList = new ArrayList<BioInfo>();
		bioInfoList.add(bioinfo);
		authRequestDTO.setBioMetadata(null);

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

		List<BioInfo> bioInfoList = new ArrayList<BioInfo>();
		authRequestDTO.setBioMetadata(bioInfoList);

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

		List<BioInfo> bioInfoList = null;
		authRequestDTO.setBioMetadata(bioInfoList);

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
		fingerValue.setValue("finger");
		fingerValue.setSubType("Thumb");
		fingerValue.setType("finger");
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		irisValue.setValue("iris img");
		irisValue.setSubType("left");
		irisValue.setType("iris");
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		faceValue.setValue("face img");
		faceValue.setSubType("Thumb");
		faceValue.setType("face");
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);

		IdentityDTO identitydto = new IdentityDTO();
		identitydto.setBiometrics(fingerIdentityInfoDtoList);

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setIdentity(identitydto);

		authRequestDTO.setRequest(requestDTO);

		BioInfo bioinfo = new BioInfo();
		bioinfo.setBioType(BioAuthType.FGR_IMG.getType());
		bioinfo.setDeviceId("123456789");
		bioinfo.setDeviceProviderID("1234567890");

		List<BioInfo> bioInfoList = new ArrayList<BioInfo>();
		bioInfoList.add(bioinfo);
		authRequestDTO.setBioMetadata(bioInfoList);

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
		fingerValue.setValue("finger");
		fingerValue.setSubType("Thumb");
		fingerValue.setType("finger");
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		irisValue.setValue("iris img");
		irisValue.setSubType("left");
		irisValue.setType("iris");
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		faceValue.setValue("face img");
		faceValue.setSubType("Thumb");
		faceValue.setType("face");
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);

		IdentityDTO identitydto = new IdentityDTO();
		identitydto.setBiometrics(fingerIdentityInfoDtoList);

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setIdentity(identitydto);

		authRequestDTO.setRequest(requestDTO);

		BioInfo bioinfo = new BioInfo();
		bioinfo.setBioType(BioAuthType.FGR_IMG.getType());
		List<BioInfo> bioInfoList = new ArrayList<BioInfo>();
		bioInfoList.add(bioinfo);
		authRequestDTO.setBioMetadata(bioInfoList);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateFinger", authRequestDTO, bioInfoList,
				error);
		assertFalse(error.hasErrors());
	}

	/**
	 * Test validate iris.
	 */
	@Test
	public void testValidateIris() {
		authRequestDTO = getAuthRequestDTO();

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		fingerValue.setValue("finger");
		fingerValue.setSubType("Thumb");
		fingerValue.setType("finger");
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		irisValue.setValue("iris img");
		irisValue.setSubType("left");
		irisValue.setType("iris");
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		faceValue.setValue("face img");
		faceValue.setSubType("Thumb");
		faceValue.setType("face");
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);

		IdentityDTO identitydto = new IdentityDTO();
		identitydto.setBiometrics(fingerIdentityInfoDtoList);

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setIdentity(identitydto);

		authRequestDTO.setRequest(requestDTO);

		BioInfo bioinfo = new BioInfo();
		bioinfo.setBioType(BioAuthType.IRIS_IMG.getType());
		List<BioInfo> bioInfoList = new ArrayList<BioInfo>();
		bioInfoList.add(bioinfo);
		authRequestDTO.setBioMetadata(bioInfoList);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateIris", authRequestDTO, bioInfoList, error);
		assertFalse(error.hasErrors());

	}

	/**
	 * Test validate irisright eye.
	 */
	@Test
	public void testValidateIrisrightEye() {
		authRequestDTO = getAuthRequestDTO();

		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		irisValue.setValue("iris img");
		irisValue.setSubType("right");
		irisValue.setType("iris");
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(irisValue);

		IdentityDTO identitydto = new IdentityDTO();
		identitydto.setBiometrics(fingerIdentityInfoDtoList);

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setIdentity(identitydto);

		authRequestDTO.setRequest(requestDTO);

		BioInfo bioinfo = new BioInfo();
		bioinfo.setBioType(BioAuthType.IRIS_IMG.getType());
		List<BioInfo> bioInfoList = new ArrayList<BioInfo>();
		bioInfoList.add(bioinfo);
		authRequestDTO.setBioMetadata(bioInfoList);

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
		fingerValue.setValue("finger");
		fingerValue.setSubType("Thumb");
		fingerValue.setType("finger");
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		irisValue.setValue("iris img");
		irisValue.setSubType("left");
		irisValue.setType("iris");
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		faceValue.setValue("face img");
		faceValue.setSubType("Thumb");
		faceValue.setType("face");
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);

		IdentityDTO identitydto = new IdentityDTO();
		identitydto.setBiometrics(fingerIdentityInfoDtoList);

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setIdentity(identitydto);

		authRequestDTO.setRequest(requestDTO);

		BioInfo bioinfo = new BioInfo();
		bioinfo.setBioType(BioAuthType.FACE_IMG.getType());
		List<BioInfo> bioInfoList = new ArrayList<BioInfo>();
		bioInfoList.add(bioinfo);
		authRequestDTO.setBioMetadata(bioInfoList);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateFace", authRequestDTO, bioInfoList, error);
		assertFalse(error.hasErrors());

	}

	/**
	 * Test check atleast one finger request available has error.
	 */
	@Test
	public void testCheckAtleastOneFingerRequestAvailable_hasError() {
		authRequestDTO = getAuthRequestDTO();

		authRequestDTO.setRequest(null);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkAtleastOneFingerRequestAvailable",
				authRequestDTO, error);
		assertTrue(error.hasErrors());
	}

	/**
	 * Test check atleast one finger request available.
	 */
	@Test
	public void testCheckAtleastOneFingerRequestAvailable() {
		authRequestDTO = getAuthRequestDTO();

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		fingerValue.setValue("finger");
		fingerValue.setSubType("Thumb");
		fingerValue.setType("finger");
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		irisValue.setValue("iris img");
		irisValue.setSubType("left");
		irisValue.setType("iris");
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		faceValue.setValue("face img");
		faceValue.setSubType("Thumb");
		faceValue.setType("face");
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);

		IdentityDTO identitydto = new IdentityDTO();
		identitydto.setBiometrics(fingerIdentityInfoDtoList);

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setIdentity(identitydto);

		authRequestDTO.setRequest(requestDTO);
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkAtleastOneFingerRequestAvailable",
				authRequestDTO, error);
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
		fingerValue.setValue("finger");
		fingerValue.setSubType("Thumb");
		fingerValue.setType("finger");
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		irisValue.setValue("iris img");
		irisValue.setSubType("left");
		irisValue.setType("iris");
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		faceValue.setValue("face img");
		faceValue.setSubType("Thumb");
		faceValue.setType("face");
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);

		IdentityDTO identitydto = new IdentityDTO();
		identitydto.setBiometrics(fingerIdentityInfoDtoList);

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setIdentity(identitydto);

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
		fingerValue.setValue("finger");
		fingerValue.setSubType("Thumb");
		fingerValue.setType("finger");
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		irisValue.setValue("iris img");
		irisValue.setSubType("left");
		irisValue.setType("iris");
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		faceValue.setValue("face img");
		faceValue.setSubType("Thumb");
		faceValue.setType("face");
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);

		IdentityDTO identitydto = new IdentityDTO();
		identitydto.setBiometrics(fingerIdentityInfoDtoList);

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setIdentity(identitydto);

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
		request.setIdentity(identitydto);
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
		request.setIdentity(identitydto);
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
		BioInfo bioinfo = new BioInfo();
		bioinfo.setBioType(BioAuthType.FACE_IMG.getType());
		List<BioInfo> bioInfoList = new ArrayList<BioInfo>();
		bioInfoList.add(bioinfo);

		boolean isBioTypeAvailable = ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "isAvailableBioType",
				bioInfoList, BioAuthType.FACE_IMG);
		assertTrue(isBioTypeAvailable);

	}

	/**
	 * Test is bio type available bio type not availabe return false.
	 */
	@Test
	public void testIsBioTypeAvailable_BioTypeNotAvailabe_ReturnFalse() {
		List<BioInfo> bioInfoList = new ArrayList<BioInfo>();

		boolean isBioTypeAvailable = ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "isAvailableBioType",
				bioInfoList, BioAuthType.FACE_IMG);
		assertFalse(isBioTypeAvailable);

	}

	/**
	 * Test is contain device info device available return true.
	 */
	@Test
	public void testIsContainDeviceInfo_DeviceAvailable_ReturnTrue() {
		BioInfo bioinfo = new BioInfo();
		bioinfo.setDeviceId("1234567890");
		bioinfo.setDeviceProviderID("1234567890");
		List<BioInfo> deviceInfoList = new ArrayList<BioInfo>();
		deviceInfoList.add(bioinfo);

		boolean isDeviceAvailableForBio = ReflectionTestUtils.invokeMethod(baseAuthRequestValidator,
				"isContainDeviceId", deviceInfoList);
		assertTrue(isDeviceAvailableForBio);

	}

	/**
	 * Test is contain device info device not available return false.
	 */
	@Test
	public void testIsContainDeviceInfo_DeviceNotAvailable_ReturnFalse() {
		BioInfo bioinfo = new BioInfo();
		List<BioInfo> deviceInfoList = new ArrayList<BioInfo>();
		deviceInfoList.add(bioinfo);

		boolean isDeviceAvailableForBio = ReflectionTestUtils.invokeMethod(baseAuthRequestValidator,
				"isContainDeviceId", deviceInfoList);
		assertFalse(isDeviceAvailableForBio);

	}

	/**
	 * Test is duplicate bio type true.
	 */
	@Test
	public void testIsDuplicateBioType_True() {
		authRequestDTO = getAuthRequestDTO();

		BioInfo bioinfo = new BioInfo();
		bioinfo.setBioType(BioAuthType.FACE_IMG.getType());
		BioInfo bioinfo1 = new BioInfo();
		bioinfo1.setBioType(BioAuthType.FACE_IMG.getType());
		List<BioInfo> bioInfoList = new ArrayList<BioInfo>();
		bioInfoList.add(bioinfo);
		bioInfoList.add(bioinfo1);

		authRequestDTO.setBioMetadata(bioInfoList);

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

		BioInfo bioinfo = new BioInfo();
		bioinfo.setBioType(BioAuthType.IRIS_IMG.getType());
		BioInfo bioinfo1 = new BioInfo();
		bioinfo1.setBioType("");
		List<BioInfo> bioInfoList = new ArrayList<BioInfo>();
		bioInfoList.add(bioinfo);

		authRequestDTO.setBioMetadata(bioInfoList);

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

		BioInfo bioinfo = new BioInfo();
		bioinfo.setBioType("");
		BioInfo bioinfo1 = new BioInfo();
		bioinfo1.setBioType("test");
		List<BioInfo> bioInfoList = new ArrayList<BioInfo>();

		authRequestDTO.setBioMetadata(bioInfoList);

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
		request.setIdentity(identity);
		authRequestDTO.setRequest(request);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateFingerRequestCount", authRequestDTO, error);
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
		fingerValue.setValue("finger");
		fingerValue.setSubType("Thumb");
		fingerValue.setType("finger");
		BioIdentityInfoDTO fingerValue2 = new BioIdentityInfoDTO();
		fingerValue2.setValue("");
		fingerValue2.setSubType("Thumb");
		fingerValue2.setType("finger");
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(fingerValue2);

		IdentityDTO identitydto = new IdentityDTO();
		identitydto.setBiometrics(fingerIdentityInfoDtoList);

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setIdentity(identitydto);

		authRequestDTO.setRequest(requestDTO);
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateFingerRequestCount", authRequestDTO, error);
		assertTrue(error.hasErrors());
	}

	/**
	 * Test validate finger request count any info is more than one.
	 */
	@Test
	public void testValidateFingerRequestCount_anyInfoIsMoreThanOne() {
		authRequestDTO = getAuthRequestDTO();

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		fingerValue.setValue("finger");
		fingerValue.setSubType("Thumb");
		fingerValue.setType("finger");
		BioIdentityInfoDTO fingerValue2 = new BioIdentityInfoDTO();
		fingerValue2.setValue("finger");
		fingerValue2.setSubType("INDEXFINGER");
		fingerValue2.setType("finger");
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(fingerValue2);

		IdentityDTO identitydto = new IdentityDTO();
		identitydto.setBiometrics(fingerIdentityInfoDtoList);

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setIdentity(identitydto);

		authRequestDTO.setRequest(requestDTO);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateFingerRequestCount", authRequestDTO, error);
		assertTrue(error.hasErrors());
	}

	/**
	 * Test validate finger request count finger count exceeding 10.
	 */
	@Test
	public void testValidateFingerRequestCount_fingerCountExceeding10() {
		authRequestDTO = getAuthRequestDTO();

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		fingerValue.setValue("finger");
		fingerValue.setSubType("Thumb");
		fingerValue.setType("finger");
		BioIdentityInfoDTO fingerValue2 = new BioIdentityInfoDTO();
		fingerValue2.setValue("finger");
		fingerValue2.setSubType("INDEXFINGER");
		fingerValue2.setType("finger");
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(fingerValue2);
		fingerIdentityInfoDtoList.add(fingerValue2);
		fingerIdentityInfoDtoList.add(fingerValue2);
		fingerIdentityInfoDtoList.add(fingerValue2);
		fingerIdentityInfoDtoList.add(fingerValue2);
		fingerIdentityInfoDtoList.add(fingerValue2);
		fingerIdentityInfoDtoList.add(fingerValue2);
		fingerIdentityInfoDtoList.add(fingerValue2);
		fingerIdentityInfoDtoList.add(fingerValue2);
		fingerIdentityInfoDtoList.add(fingerValue2);
		fingerIdentityInfoDtoList.add(fingerValue2);
		fingerIdentityInfoDtoList.add(fingerValue2);

		IdentityDTO identitydto = new IdentityDTO();
		identitydto.setBiometrics(fingerIdentityInfoDtoList);

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setIdentity(identitydto);

		authRequestDTO.setRequest(requestDTO);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateFingerRequestCount", authRequestDTO, error);
		assertTrue(error.hasErrors());
	}

	/**
	 * Test validate iris request count.
	 */
	@Test
	public void testValidateIrisRequestCount() {
		authRequestDTO = getAuthRequestDTO();

		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		irisValue.setValue("iris img");
		irisValue.setSubType("left");
		irisValue.setType("iris");
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		faceValue.setValue("face img");
		faceValue.setSubType("Thumb");
		faceValue.setType("face");
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);

		IdentityDTO identitydto = new IdentityDTO();
		identitydto.setBiometrics(fingerIdentityInfoDtoList);

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setIdentity(identitydto);

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

		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		irisValue.setValue("iris img");
		irisValue.setSubType("left");
		irisValue.setType("iris");
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		faceValue.setValue("face img");
		faceValue.setSubType("Thumb");
		faceValue.setType("face");
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);
		fingerIdentityInfoDtoList.add(irisValue);

		IdentityDTO identitydto = new IdentityDTO();
		identitydto.setBiometrics(fingerIdentityInfoDtoList);

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setIdentity(identitydto);

		authRequestDTO.setRequest(requestDTO);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateIrisRequestCount", authRequestDTO, error);
		System.err.println(error);
		assertTrue(error.hasErrors());

	}

	/**
	 * Test check OTP auth has no error.
	 */
	@Test
	public void testCheckOTPAuth_HasNoError() {
		String otp = "456789";
		AuthRequestDTO authRequestDTO = getAuthRequestDTO();
		RequestDTO request = new RequestDTO();
		AdditionalFactorsDTO additionalFactors = new AdditionalFactorsDTO();
		additionalFactors.setTotp(otp);
		request.setAdditionalFactors(additionalFactors);
		authRequestDTO.setRequest(request);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkOTPAuth", authRequestDTO, error);
		assertFalse(error.hasErrors());
	}

	/**
	 * Test check OTP auth has null value has error.
	 */
	@Test
	public void testCheckOTPAuth_HasNullValue_HasError() {
		AuthRequestDTO authRequestDTO = getAuthRequestDTO();

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkOTPAuth", authRequestDTO, error);
		assertTrue(error.hasErrors());
	}

	/**
	 * Test check OTP auth has empty OT P has error.
	 */
	@Test
	public void testCheckOTPAuth_HasEmptyOTP_HasError() {
		String otp = "";
		AuthRequestDTO authRequestDTO = getAuthRequestDTO();
		RequestDTO request = new RequestDTO();
		AdditionalFactorsDTO additionalFactors = new AdditionalFactorsDTO();
		additionalFactors.setTotp(otp);
		request.setAdditionalFactors(additionalFactors);
		authRequestDTO.setRequest(request);
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkOTPAuth", authRequestDTO, error);
		assertTrue(error.hasErrors());
	}

	/**
	 * Test get otp value.
	 */
	@Test
	public void testGetOtpValue() {

		String otp = "456789";
		AuthRequestDTO authRequestDTO = getAuthRequestDTO();
		RequestDTO request = new RequestDTO();
		AdditionalFactorsDTO additionalFactors = new AdditionalFactorsDTO();
		additionalFactors.setTotp(otp);
		request.setAdditionalFactors(additionalFactors);
		authRequestDTO.setRequest(request);

		Optional<String> isOtp = ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "getOtpValue",
				authRequestDTO);
		assertTrue(isOtp.isPresent());
	}

	/**
	 * Test validate email validate email is true.
	 */
	@Test
	public void testValidateEmail_ValidateEmail_IsTrue() {
		AuthRequestDTO authRequestDTO = getAuthRequestDTO();

		RequestDTO request = new RequestDTO();
		IdentityDTO identity = new IdentityDTO();

		List<IdentityInfoDTO> emailId = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setLanguage("fre");
		identityInfoDTO.setValue("sample@sample.com");
		emailId.add(identityInfoDTO);
		identity.setEmailId(emailId);

		identity.setEmailId(emailId);
		request.setIdentity(identity);
		authRequestDTO.setRequest(request);

		Mockito.when(emailValidatorImpl.validateEmail(Mockito.anyString())).thenReturn(true);

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

		List<IdentityInfoDTO> emailId = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setLanguage("fre");
		identityInfoDTO.setValue("sample5878");
		emailId.add(identityInfoDTO);
		identity.setEmailId(emailId);

		identity.setEmailId(emailId);
		request.setIdentity(identity);
		authRequestDTO.setRequest(request);

		Mockito.when(emailValidatorImpl.validateEmail(Mockito.anyString()))
				.thenThrow(new InvalideEmailException("", ""));

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateEmail", authRequestDTO, error);
		assertTrue(error.hasErrors());
	}

	/**
	 * Test validate phone validate phone is true.
	 */
	@Test
	public void testValidatePhone_ValidatePhone_IsTrue() {
		List<IdentityInfoDTO> phoneNumber = new ArrayList<IdentityInfoDTO>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setLanguage("fre");
		identityInfoDTO.setValue("89754765987676");

		phoneNumber.add(identityInfoDTO);

		IdentityDTO phone = new IdentityDTO();
		phone.setPhoneNumber(phoneNumber);

		RequestDTO phoneRequest = new RequestDTO();
		phoneRequest.setIdentity(phone);
		AuthRequestDTO authRequestDTO = getAuthRequestDTO();
		authRequestDTO.setRequest(phoneRequest);

		Mockito.when(phoneValidatorImpl.validatePhone(Mockito.anyString())).thenReturn(true);
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validatePhone", authRequestDTO, error);
		assertFalse(error.hasErrors());

	}

	/**
	 * Test validate phone validate phone is false.
	 */
	@Test
	public void testValidatePhone_ValidatePhone_IsFalse() {
		List<IdentityInfoDTO> phoneNumber = new ArrayList<IdentityInfoDTO>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setLanguage("fre");
		identityInfoDTO.setValue("8975476lghfhhj");

		phoneNumber.add(identityInfoDTO);

		IdentityDTO phone = new IdentityDTO();
		phone.setPhoneNumber(phoneNumber);

		RequestDTO phoneRequest = new RequestDTO();
		phoneRequest.setIdentity(phone);
		AuthRequestDTO authRequestDTO = getAuthRequestDTO();
		authRequestDTO.setRequest(phoneRequest);

		Mockito.when(phoneValidatorImpl.validatePhone(Mockito.anyString()))
				.thenThrow(new InvalidPhoneNumberException("", ""));
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
		authRequestDTO.setPolicyID("1234567890");
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
		authRequestDTO.setPolicyID("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(environment.getProperty("mosip.primary.lang-code"));
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(environment.getProperty("mosip.secondary.lang-code"));
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
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
		authRequestDTO.setPolicyID("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(environment.getProperty("mosip.secondary.lang-code"));
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(environment.getProperty("mosip.secondary.lang-code"));
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
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
		authRequestDTO.setPolicyID("1234567890");
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
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
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
		authRequestDTO.setPolicyID("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		authTypeDTO.setBio(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(environment.getProperty("mosip.primary.lang-code"));
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(environment.getProperty("mosip.secondary.lang-code"));
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
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
		request.setIdentity(identity);
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
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		irisValue.setValue("irisImage");
		irisValue.setSubType("left");
		irisValue.setType("iris");
		BioIdentityInfoDTO irisValue2 = new BioIdentityInfoDTO();
		irisValue2.setValue("iris img");
		irisValue2.setSubType("right");
		irisValue2.setType("iris");
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		faceValue.setValue("face img");
		faceValue.setSubType("Thumb");
		faceValue.setType("face");
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);
		fingerIdentityInfoDtoList.add(irisValue2);

		IdentityDTO identitydto = new IdentityDTO();
		identitydto.setBiometrics(fingerIdentityInfoDtoList);

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setIdentity(identitydto);

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

		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		irisValue.setValue("iris img");
		irisValue.setSubType("left");
		irisValue.setType("iris");
		BioIdentityInfoDTO irisValue2 = new BioIdentityInfoDTO();
		irisValue2.setValue("iris img");
		irisValue2.setSubType("right");
		irisValue2.setType("iris");
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		faceValue.setValue("face img");
		faceValue.setSubType("Thumb");
		faceValue.setType("face");
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);
		fingerIdentityInfoDtoList.add(irisValue2);

		IdentityDTO identitydto = new IdentityDTO();
		identitydto.setBiometrics(fingerIdentityInfoDtoList);

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setIdentity(identitydto);

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
	public void testValidateAge() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(environment.getProperty("mosip.primary.lang-code"));
		idInfoDTO.setValue("16");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(environment.getProperty("mosip.secondary.lang-code"));
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setAge(idInfoList);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkAge", authRequestDTO, error);
		assertTrue(error.hasErrors());
	}

	/**
	 * Test validate DOB.
	 */
	@Test
	public void testValidateDOB() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(environment.getProperty("mosip.primary.lang-code"));
		idInfoDTO.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setDob(idInfoList);
		idDTO.setAge(idInfoList);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
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
		authRequestDTO.setPolicyID("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPin(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		String pin = "456789";
		RequestDTO request = new RequestDTO();
		AdditionalFactorsDTO additionalFactors = new AdditionalFactorsDTO();
		additionalFactors.setStaticPin(pin);
		request.setAdditionalFactors(additionalFactors);
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
		authRequestDTO.setPolicyID("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPin(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		String pin = "";
		RequestDTO request = new RequestDTO();
		AdditionalFactorsDTO additionalFactors = new AdditionalFactorsDTO();
		additionalFactors.setStaticPin(pin);
		request.setAdditionalFactors(additionalFactors);
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
		authRequestDTO.setPolicyID("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPin(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		String pin = null;
		RequestDTO request = new RequestDTO();
		AdditionalFactorsDTO additionalFactors = new AdditionalFactorsDTO();
		additionalFactors.setStaticPin(pin);
		request.setAdditionalFactors(additionalFactors);
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
		authRequestDTO.setPolicyID("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPin(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		String pin = "123e45";
		RequestDTO request = new RequestDTO();
		AdditionalFactorsDTO additionalFactors = new AdditionalFactorsDTO();
		additionalFactors.setStaticPin(pin);
		request.setAdditionalFactors(additionalFactors);
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
		authRequestDTO.setPolicyID("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setOtp(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		String otp = "123445";
		RequestDTO request = new RequestDTO();
		AdditionalFactorsDTO additionalFactors = new AdditionalFactorsDTO();
		additionalFactors.setTotp(otp);
		request.setAdditionalFactors(additionalFactors);
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
		authRequestDTO.setPolicyID("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setOtp(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		String otp = null;
		RequestDTO request = new RequestDTO();
		AdditionalFactorsDTO additionalFactors = new AdditionalFactorsDTO();
		additionalFactors.setTotp(otp);
		request.setAdditionalFactors(additionalFactors);
		authRequestDTO.setRequest(request);
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateAdditionalFactorsDetails", authRequestDTO,
				error);
	}

	@Test
	public void testBioType() {
		BioInfo bioInfo = new BioInfo();
		bioInfo.setBioType("fgrMins");
		List<BioInfo> bioMetadata = new ArrayList<BioInfo>();
		bioMetadata.add(bioInfo);
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateBioType", bioMetadata, error);
		assertTrue(error.hasErrors());
	}
	
	@Test
	public void testDOBType() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setPolicyID("1234567890");
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
		reqDTO.setIdentity(dobIdentityDTO);
		authRequestDTO.setRequest(reqDTO);
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkDOBType", authRequestDTO, error);
		System.err.println(error);
		assertTrue(error.hasErrors());
	}
	
	@Test
	public void testInValidateBioDetails_DeviceID() {

		authRequestDTO = getAuthRequestDTO();
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(true);
		authRequestDTO.setRequestedAuth(authType);

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		fingerValue.setValue("finger");
		fingerValue.setSubType("Thumb");
		fingerValue.setType("finger");
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		irisValue.setValue("iris img");
		irisValue.setSubType("left");
		irisValue.setType("iris");
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		faceValue.setValue("face img");
		faceValue.setSubType("Thumb");
		faceValue.setType("face");
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);

		IdentityDTO identitydto = new IdentityDTO();
		identitydto.setBiometrics(fingerIdentityInfoDtoList);

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setIdentity(identitydto);

		authRequestDTO.setRequest(requestDTO);

		BioInfo bioinfo = new BioInfo();
		bioinfo.setBioType(BioAuthType.FGR_IMG.getType());
		bioinfo.setDeviceId("");
		bioinfo.setDeviceProviderID("1234567890");

		List<BioInfo> bioInfoList = new ArrayList<BioInfo>();
		bioInfoList.add(bioinfo);
		authRequestDTO.setBioMetadata(bioInfoList);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateBioMetadataDetails", authRequestDTO, error);
		System.err.println(error);
		assertTrue(error.hasErrors());

	}
	
	@Test
	public void testInValidateBioDetails_DeviceProviderID() {

		authRequestDTO = getAuthRequestDTO();
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(true);
		authRequestDTO.setRequestedAuth(authType);

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		fingerValue.setValue("finger");
		fingerValue.setSubType("Thumb");
		fingerValue.setType("finger");
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		irisValue.setValue("iris img");
		irisValue.setSubType("left");
		irisValue.setType("iris");
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		faceValue.setValue("face img");
		faceValue.setSubType("Thumb");
		faceValue.setType("face");
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);

		IdentityDTO identitydto = new IdentityDTO();
		identitydto.setBiometrics(fingerIdentityInfoDtoList);

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setIdentity(identitydto);

		authRequestDTO.setRequest(requestDTO);

		BioInfo bioinfo = new BioInfo();
		bioinfo.setBioType(BioAuthType.FGR_IMG.getType());
		bioinfo.setDeviceId("1234567890");
		bioinfo.setDeviceProviderID(null);

		List<BioInfo> bioInfoList = new ArrayList<BioInfo>();
		bioInfoList.add(bioinfo);
		authRequestDTO.setBioMetadata(bioInfoList);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateBioMetadataDetails", authRequestDTO, error);
		System.err.println(error);
		assertTrue(error.hasErrors());

	}
}
