package io.mosip.authentication.service.impl.indauth.validator;

import static org.junit.Assert.assertEquals;
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

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.BaseAuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.BioInfo;
import io.mosip.authentication.core.dto.indauth.BioType;
import io.mosip.authentication.core.dto.indauth.DeviceInfo;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.MatchInfo;
import io.mosip.authentication.core.dto.indauth.PinInfo;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
import io.mosip.authentication.service.config.IDAMappingConfig;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.integration.IdTemplateManager;
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
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, IdTemplateManager.class,
		TemplateManagerBuilderImpl.class })
public class BaseAuthRequestValidatorTest {

	@Mock
	private SpringValidatorAdapter validator;

	@Mock
	AuthRequestDTO authRequestDTO;

	@Autowired
	Environment env;

	@InjectMocks
	BaseAuthRequestValidator baseAuthRequestValidator;

	@Mock
	EmailValidatorImpl emailValidatorImpl;

	@Mock
	PhoneValidatorImpl phoneValidatorImpl;

	@InjectMocks
	IdInfoHelper idInfoHelper;

	@Autowired
	private IDAMappingConfig idMappingConfig;

	Errors error;

	@Before
	public void before() {
		error = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		ReflectionTestUtils.setField(baseAuthRequestValidator, "emailValidatorImpl", emailValidatorImpl);
		ReflectionTestUtils.setField(baseAuthRequestValidator, "phoneValidatorImpl", phoneValidatorImpl);
		ReflectionTestUtils.setField(baseAuthRequestValidator, "idInfoHelper", idInfoHelper);
		ReflectionTestUtils.setField(baseAuthRequestValidator, "env", env);
		ReflectionTestUtils.setField(idInfoHelper, "environment", env);
		ReflectionTestUtils.setField(idInfoHelper, "idMappingConfig", idMappingConfig);

	}

	@Test
	public void testValidateVersionAndId() {
		BaseAuthRequestDTO baseAuthRequestDTO = new BaseAuthRequestDTO();
		baseAuthRequestDTO.setId("123456");
		// baseAuthRequestDTO.setVer("1.0");
		baseAuthRequestValidator.validate(baseAuthRequestDTO, error);
		assertFalse(error.hasErrors());
	}

	@Test
	public void testValidateId_HasId_NoError() {

		String id = "12345678";
		baseAuthRequestValidator.validateId(id, error);
		assertFalse(error.hasErrors());
	}

	@Test
	public void testValidateId_NoId_HasError() {

		String id = null;
		baseAuthRequestValidator.validateId(id, error);
		assertTrue(error.hasErrors());
	}

	// @Test
	// public void testValidateVersion_ValidVersion_NoError() {
	// String ver = "1.0";
	// baseAuthRequestValidator.validateVer(ver, error);
	// assertFalse(error.hasErrors());
	// }
	//
	// @Test
	// public void testValidateVersion_InvalidVersion_hasError() {
	// String ver = "1.00";
	// baseAuthRequestValidator.validateVer(ver, error);
	// assertTrue(error.hasErrors());
	// }
	//
	// @Test
	// public void testValidateVersion_NoVersion_hasError() {
	// String ver = null;
	// baseAuthRequestValidator.validateVer(ver, error);
	// assertTrue(error.hasErrors());
	// }

	@Test
	public void testValidateBioDetails_IfBioInfoIsNull_hasError() {

		authRequestDTO = getAuthRequestDTO();
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(true);
		authRequestDTO.setAuthType(authType);

		BioInfo bioinfo = new BioInfo();

		List<BioInfo> bioInfoList = new ArrayList<BioInfo>();
		bioInfoList.add(bioinfo);
		authRequestDTO.setBioInfo(null);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateBioDetails", authRequestDTO, error);
		assertTrue(error.hasErrors());

	}

	@Test
	public void testValidateBioDetails_IfBioInfoIsNotNullButBioInfoIsEmpty_hasError() {

		authRequestDTO = getAuthRequestDTO();
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(true);
		authRequestDTO.setAuthType(authType);

		List<BioInfo> bioInfoList = new ArrayList<BioInfo>();
		authRequestDTO.setBioInfo(bioInfoList);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateBioDetails", authRequestDTO, error);
		assertTrue(error.hasErrors());

	}

	@Test
	public void testValidateBioDetails_IfBioInfoIsNotNullButBioInfoIsEmpty() {

		authRequestDTO = getAuthRequestDTO();
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(false);
		authRequestDTO.setAuthType(authType);

		List<BioInfo> bioInfoList = null;
		authRequestDTO.setBioInfo(bioInfoList);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateBioDetails", authRequestDTO, error);
		assertFalse(error.hasErrors());

	}

	@Test
	public void testValidateBioDetails() {

		authRequestDTO = getAuthRequestDTO();
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(true);
		authRequestDTO.setAuthType(authType);

		IdentityInfoDTO fingerValue = new IdentityInfoDTO();
		fingerValue.setValue("finger img");

		IdentityInfoDTO irisValue = new IdentityInfoDTO();
		irisValue.setValue("iris img");

		IdentityInfoDTO faceValue = new IdentityInfoDTO();
		faceValue.setValue("face img");

		List<IdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<IdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		List<IdentityInfoDTO> irisIdentityInfoDtoList = new ArrayList<IdentityInfoDTO>();
		irisIdentityInfoDtoList.add(irisValue);
		List<IdentityInfoDTO> faceIdentityInfoDtoList = new ArrayList<IdentityInfoDTO>();
		faceIdentityInfoDtoList.add(faceValue);

		IdentityDTO identitydto = new IdentityDTO();
		identitydto.setLeftThumb(fingerIdentityInfoDtoList);
		identitydto.setLeftEye(irisIdentityInfoDtoList);
		identitydto.setFace(faceIdentityInfoDtoList);

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setIdentity(identitydto);

		authRequestDTO.setRequest(requestDTO);

		BioInfo bioinfo = new BioInfo();
		bioinfo.setBioType(BioType.FGRIMG.getType());

		DeviceInfo deviceInfo = new DeviceInfo();
		deviceInfo.setDeviceId("12345");
		deviceInfo.setMake("Mantra");
		deviceInfo.setModel("M123");
		bioinfo.setDeviceInfo(deviceInfo);

		List<BioInfo> bioInfoList = new ArrayList<BioInfo>();
		bioInfoList.add(bioinfo);
		authRequestDTO.setBioInfo(bioInfoList);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateBioDetails", authRequestDTO, error);
		assertFalse(error.hasErrors());

	}

	@Test
	public void testValidateFinger_NoErrors() {
		authRequestDTO = getAuthRequestDTO();

		IdentityInfoDTO fingerValue = new IdentityInfoDTO();
		fingerValue.setValue("finger img");
		List<IdentityInfoDTO> finger = new ArrayList<IdentityInfoDTO>();
		finger.add(fingerValue);

		IdentityDTO fingerIdentity = new IdentityDTO();
		fingerIdentity.setLeftThumb(finger);

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setIdentity(fingerIdentity);

		authRequestDTO.setRequest(requestDTO);

		BioInfo bioinfo = new BioInfo();
		bioinfo.setBioType(BioType.FGRIMG.getType());
		List<BioInfo> bioInfoList = new ArrayList<BioInfo>();
		bioInfoList.add(bioinfo);
		authRequestDTO.setBioInfo(bioInfoList);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateFinger", authRequestDTO, bioInfoList,
				error);
		assertFalse(error.hasErrors());
	}

	@Test
	public void testValidateIris() {
		authRequestDTO = getAuthRequestDTO();

		IdentityInfoDTO irisValue = new IdentityInfoDTO();
		irisValue.setValue("iris");
		List<IdentityInfoDTO> iris = new ArrayList<IdentityInfoDTO>();
		iris.add(irisValue);

		IdentityDTO irisIdentity = new IdentityDTO();
		irisIdentity.setLeftEye(iris);

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setIdentity(irisIdentity);

		authRequestDTO.setRequest(requestDTO);

		BioInfo bioinfo = new BioInfo();
		bioinfo.setBioType(BioType.IRISIMG.getType());
		List<BioInfo> bioInfoList = new ArrayList<BioInfo>();
		bioInfoList.add(bioinfo);
		authRequestDTO.setBioInfo(bioInfoList);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateIris", authRequestDTO, bioInfoList, error);
		assertFalse(error.hasErrors());

	}

	@Test
	public void testValidateIrisrightEye() {
		authRequestDTO = getAuthRequestDTO();

		IdentityInfoDTO irisValue = new IdentityInfoDTO();
		irisValue.setValue("iris");
		List<IdentityInfoDTO> iris = new ArrayList<IdentityInfoDTO>();
		iris.add(irisValue);

		IdentityDTO irisIdentity = new IdentityDTO();
		irisIdentity.setLeftEye(iris);
		irisIdentity.setRightEye(iris);
		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setIdentity(irisIdentity);

		authRequestDTO.setRequest(requestDTO);

		BioInfo bioinfo = new BioInfo();
		bioinfo.setBioType(BioType.IRISIMG.getType());
		List<BioInfo> bioInfoList = new ArrayList<BioInfo>();
		bioInfoList.add(bioinfo);
		authRequestDTO.setBioInfo(bioInfoList);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateIris", authRequestDTO, bioInfoList, error);
		assertTrue(error.hasErrors());

	}

	@Test
	public void testValidateFace() {

		authRequestDTO = getAuthRequestDTO();

		IdentityInfoDTO faceValue = new IdentityInfoDTO();
		faceValue.setValue("face");
		List<IdentityInfoDTO> face = new ArrayList<IdentityInfoDTO>();
		face.add(faceValue);

		IdentityDTO faceIdentity = new IdentityDTO();
		faceIdentity.setFace(face);

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setIdentity(faceIdentity);

		authRequestDTO.setRequest(requestDTO);

		BioInfo bioinfo = new BioInfo();
		bioinfo.setBioType(BioType.FACEIMG.getType());
		List<BioInfo> bioInfoList = new ArrayList<BioInfo>();
		bioInfoList.add(bioinfo);
		authRequestDTO.setBioInfo(bioInfoList);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateFace", authRequestDTO, bioInfoList, error);
		assertFalse(error.hasErrors());

	}

	@Test
	public void testCheckAtleastOneFingerRequestAvailable_hasError() {
		authRequestDTO = getAuthRequestDTO();

		authRequestDTO.setRequest(null);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkAtleastOneFingerRequestAvailable",
				authRequestDTO, error);
		assertTrue(error.hasErrors());
	}

	@Test
	public void testCheckAtleastOneFingerRequestAvailable() {
		authRequestDTO = getAuthRequestDTO();

		IdentityDTO identitydto = new IdentityDTO();
		RequestDTO request = new RequestDTO();

		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue("fingerImage");
		List<IdentityInfoDTO> leftThumb = new ArrayList<IdentityInfoDTO>();
		leftThumb.add(identityInfoDTO);

		identitydto.setLeftThumb(leftThumb);
		request.setIdentity(identitydto);
		authRequestDTO.setRequest(request);

		Function<IdentityDTO, List<IdentityInfoDTO>> fun = new Function<IdentityDTO, List<IdentityInfoDTO>>() {
			@Override
			public List<IdentityInfoDTO> apply(IdentityDTO t) {
				return t.getLeftThumb();
			}
		};
		@SuppressWarnings("unchecked")
		boolean checkAnyIdInfoAvailable = baseAuthRequestValidator.checkAnyIdInfoAvailable(authRequestDTO, fun);
		assertTrue(checkAnyIdInfoAvailable);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkAtleastOneFingerRequestAvailable",
				authRequestDTO, error);
		assertFalse(error.hasErrors());
	}

	@Test
	public void testNoIrisRequestAvailable_HasError() {

		authRequestDTO = getAuthRequestDTO();

		authRequestDTO.setRequest(null);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkAtleastOneIrisRequestAvailable",
				authRequestDTO, error);

		assertTrue(error.hasErrors());

	}

	@Test
	public void testAtleastOneIrisRequestAvailable_NoError() {

		authRequestDTO = getAuthRequestDTO();

		IdentityDTO identitydto = new IdentityDTO();
		RequestDTO request = new RequestDTO();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue("iris");
		List<IdentityInfoDTO> iris = new ArrayList<IdentityInfoDTO>();
		iris.add(identityInfoDTO);
		identitydto.setLeftEye(iris);
		identitydto.setRightEye(iris);
		request.setIdentity(identitydto);
		authRequestDTO.setRequest(request);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkAtleastOneIrisRequestAvailable",
				authRequestDTO, error);

		assertFalse(error.hasErrors());

	}

	@Test
	public void test_NoFaceRequestAvailable() {
		authRequestDTO = getAuthRequestDTO();

		authRequestDTO.setRequest(null);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkAtleastOneFaceRequestAvailable",
				authRequestDTO, error);
		assertTrue(error.hasErrors());
	}

	@Test
	public void test_AtleastOneFaceRequestAvailable() {
		authRequestDTO = getAuthRequestDTO();

		IdentityInfoDTO faceValue = new IdentityInfoDTO();
		faceValue.setValue("face");
		List<IdentityInfoDTO> face = new ArrayList<IdentityInfoDTO>();
		face.add(faceValue);

		IdentityDTO faceIdentity = new IdentityDTO();
		faceIdentity.setFace(face);

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setIdentity(faceIdentity);

		authRequestDTO.setRequest(requestDTO);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkAtleastOneFaceRequestAvailable",
				authRequestDTO, error);
		assertFalse(error.hasErrors());
	}

	@Test
	public void testAnyIdInfoAvailable() {
		authRequestDTO = getAuthRequestDTO();

		IdentityDTO identitydto = new IdentityDTO();
		RequestDTO request = new RequestDTO();

		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue("fingerImage");
		List<IdentityInfoDTO> leftThumb = new ArrayList<IdentityInfoDTO>();
		leftThumb.add(identityInfoDTO);

		identitydto.setLeftThumb(leftThumb);
		request.setIdentity(identitydto);
		authRequestDTO.setRequest(request);

		Function<IdentityDTO, List<IdentityInfoDTO>> fun = new Function<IdentityDTO, List<IdentityInfoDTO>>() {
			@Override
			public List<IdentityInfoDTO> apply(IdentityDTO t) {
				return t.getLeftThumb();
			}
		};
		@SuppressWarnings("unchecked")
		boolean checkAnyIdInfoAvailable = baseAuthRequestValidator.checkAnyIdInfoAvailable(authRequestDTO, fun);
		assertTrue(checkAnyIdInfoAvailable);
	}

	@Test
	public void testAnyIdInfoNotAvailable() {
		authRequestDTO = getAuthRequestDTO();

		IdentityDTO identitydto = new IdentityDTO();
		RequestDTO request = new RequestDTO();

		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue("fingerImage");
		List<IdentityInfoDTO> leftThumb = new ArrayList<IdentityInfoDTO>();
		leftThumb.add(identityInfoDTO);

		identitydto.setLeftThumb(leftThumb);
		request.setIdentity(identitydto);
		authRequestDTO.setRequest(null);

		Function<IdentityDTO, List<IdentityInfoDTO>> fun = new Function<IdentityDTO, List<IdentityInfoDTO>>() {
			@Override
			public List<IdentityInfoDTO> apply(IdentityDTO t) {
				return t.getLeftThumb();
			}
		};
		@SuppressWarnings("unchecked")
		boolean checkAnyIdInfoAvailable = baseAuthRequestValidator.checkAnyIdInfoAvailable(authRequestDTO, fun);
		assertFalse(checkAnyIdInfoAvailable);
	}

	@Test
	public void testIsBioTypeAvailable_BioTypeAvailabe_ReturnTrue() {
		BioInfo bioinfo = new BioInfo();
		bioinfo.setBioType(BioType.FACEIMG.getType());
		List<BioInfo> bioInfoList = new ArrayList<BioInfo>();
		bioInfoList.add(bioinfo);

		boolean isBioTypeAvailable = ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "isAvailableBioType",
				bioInfoList, BioType.FACEIMG);
		assertTrue(isBioTypeAvailable);

	}

	@Test
	public void testIsBioTypeAvailable_BioTypeNotAvailabe_ReturnFalse() {
		List<BioInfo> bioInfoList = new ArrayList<BioInfo>();

		boolean isBioTypeAvailable = ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "isAvailableBioType",
				bioInfoList, BioType.FACEIMG);
		assertFalse(isBioTypeAvailable);

	}

	@Test
	public void testIsContainDeviceInfo_DeviceAvailable_ReturnTrue() {
		BioInfo bioinfo = new BioInfo();
		DeviceInfo deviceInfo = new DeviceInfo();
		deviceInfo.setDeviceId("12345");
		deviceInfo.setMake("Mantra");
		deviceInfo.setModel("M123");
		bioinfo.setDeviceInfo(deviceInfo);
		List<BioInfo> deviceInfoList = new ArrayList<BioInfo>();
		deviceInfoList.add(bioinfo);

		boolean isDeviceAvailableForBio = ReflectionTestUtils.invokeMethod(baseAuthRequestValidator,
				"isContainDeviceInfo", deviceInfoList);
		assertTrue(isDeviceAvailableForBio);

	}

	@Test
	public void testIsContainDeviceInfo_DeviceNotAvailable_ReturnFalse() {
		BioInfo bioinfo = new BioInfo();
		bioinfo.setDeviceInfo(null);
		List<BioInfo> deviceInfoList = new ArrayList<BioInfo>();
		deviceInfoList.add(bioinfo);

		boolean isDeviceAvailableForBio = ReflectionTestUtils.invokeMethod(baseAuthRequestValidator,
				"isContainDeviceInfo", deviceInfoList);
		assertFalse(isDeviceAvailableForBio);

	}

	@Test
	public void testIsDuplicateBioType_True() {
		authRequestDTO = getAuthRequestDTO();

		BioInfo bioinfo = new BioInfo();
		bioinfo.setBioType(BioType.FACEIMG.getType());
		BioInfo bioinfo1 = new BioInfo();
		bioinfo1.setBioType(BioType.FACEIMG.getType());
		List<BioInfo> bioInfoList = new ArrayList<BioInfo>();
		bioInfoList.add(bioinfo);
		bioInfoList.add(bioinfo1);

		authRequestDTO.setBioInfo(bioInfoList);

		boolean isDuplicateBioType = ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "isDuplicateBioType",
				authRequestDTO, BioType.IRISIMG);
		assertTrue(isDuplicateBioType);
	}

	@Test
	public void testIsDuplicateBioType_False() {
		authRequestDTO = getAuthRequestDTO();

		BioInfo bioinfo = new BioInfo();
		bioinfo.setBioType(BioType.IRISIMG.getType());
		BioInfo bioinfo1 = new BioInfo();
		bioinfo1.setBioType("");
		List<BioInfo> bioInfoList = new ArrayList<BioInfo>();
		bioInfoList.add(bioinfo);

		authRequestDTO.setBioInfo(bioInfoList);

		boolean isDuplicateBioType = ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "isDuplicateBioType",
				authRequestDTO, BioType.IRISIMG);
		assertTrue(isDuplicateBioType);
	}

	@Test
	public void testIsDuplicateBioTypeIris() {
		authRequestDTO = getAuthRequestDTO();

		BioInfo bioinfo = new BioInfo();
		bioinfo.setBioType("");
		BioInfo bioinfo1 = new BioInfo();
		bioinfo1.setBioType("test");
		List<BioInfo> bioInfoList = new ArrayList<BioInfo>();
	

		authRequestDTO.setBioInfo(bioInfoList);

		boolean isDuplicateBioType = ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "isDuplicateBioType",
				authRequestDTO, BioType.FGRIMG);
		assertTrue(isDuplicateBioType);
	}

	@Test
	public void testValidateFingerRequestCount_anyInfoIsEqualToOneOrLessThanOne_fingerCountNotExceeding2() {
		authRequestDTO = getAuthRequestDTO();

		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue("fingerImage");
		List<IdentityInfoDTO> finger = new ArrayList<IdentityInfoDTO>();
		finger.add(identityInfoDTO);

		IdentityDTO identity = new IdentityDTO();
		identity.setLeftThumb(finger);
		identity.setLeftIndex(finger);

		RequestDTO request = new RequestDTO();
		request.setIdentity(identity);
		authRequestDTO.setRequest(request);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateFingerRequestCount", authRequestDTO, error);
		assertFalse(error.hasErrors());
	}

	@Test
	public void testValidateFingerRequestCount_anyInfoIsEqualToOneOrLessThanOne_fingerCountExceeding2() {
		authRequestDTO = getAuthRequestDTO();

		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue("fingerImage");
		List<IdentityInfoDTO> finger = new ArrayList<IdentityInfoDTO>();
		finger.add(identityInfoDTO);

		IdentityDTO identity = new IdentityDTO();
		identity.setLeftThumb(finger);
		identity.setLeftIndex(finger);
		identity.setLeftMiddle(finger);
		identity.setLeftRing(finger);
		identity.setLeftLittle(finger);
		identity.setRightThumb(finger);
		identity.setRightIndex(finger);
		identity.setRightMiddle(finger);
		identity.setRightRing(finger);
		identity.setRightLittle(finger);

		RequestDTO request = new RequestDTO();
		request.setIdentity(identity);
		authRequestDTO.setRequest(request);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateFingerRequestCount", authRequestDTO, error);
		assertTrue(error.hasErrors());
	}

	@Test
	public void testValidateFingerRequestCount_anyInfoIsMoreThanOne() {
		authRequestDTO = getAuthRequestDTO();

		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue("fingerImage");
		List<IdentityInfoDTO> finger = new ArrayList<IdentityInfoDTO>();
		finger.add(identityInfoDTO);
		finger.add(identityInfoDTO);

		IdentityDTO identity = new IdentityDTO();
		identity.setLeftThumb(finger);

		RequestDTO request = new RequestDTO();
		request.setIdentity(identity);
		authRequestDTO.setRequest(request);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateFingerRequestCount", authRequestDTO, error);
		assertTrue(error.hasErrors());
	}

	@Test
	public void testValidateFingerRequestCount_fingerCountExceeding10() {
		authRequestDTO = getAuthRequestDTO();

		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue("fingerImage");
		List<IdentityInfoDTO> finger = new ArrayList<IdentityInfoDTO>();
		finger.add(identityInfoDTO);
		finger.add(identityInfoDTO);
		finger.add(identityInfoDTO);
		finger.add(identityInfoDTO);
		finger.add(identityInfoDTO);
		finger.add(identityInfoDTO);
		finger.add(identityInfoDTO);
		finger.add(identityInfoDTO);
		finger.add(identityInfoDTO);
		finger.add(identityInfoDTO);
		finger.add(identityInfoDTO);
		finger.add(identityInfoDTO);

		IdentityDTO identity = new IdentityDTO();
		identity.setLeftThumb(finger);

		RequestDTO request = new RequestDTO();
		request.setIdentity(identity);
		authRequestDTO.setRequest(request);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateFingerRequestCount", authRequestDTO, error);
		assertTrue(error.hasErrors());
	}

	@Test
	public void testMultiFingersValueisExist() {
		authRequestDTO = getAuthRequestDTO();

		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue("fingerImage");
		List<IdentityInfoDTO> finger = new ArrayList<IdentityInfoDTO>();
		finger.add(identityInfoDTO);
		finger.add(identityInfoDTO);
		finger.add(identityInfoDTO);
		finger.add(identityInfoDTO);
		finger.add(identityInfoDTO);
		finger.add(identityInfoDTO);
		finger.add(identityInfoDTO);
		finger.add(identityInfoDTO);
		finger.add(identityInfoDTO);
		finger.add(identityInfoDTO);
		finger.add(identityInfoDTO);
		finger.add(identityInfoDTO);
		IdentityDTO identity = new IdentityDTO();
		identity.setLeftThumb(finger);
		identity.setRightThumb(finger);
		RequestDTO request = new RequestDTO();
		request.setIdentity(identity);
		authRequestDTO.setRequest(request);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateMultiFingersValue", authRequestDTO, error);
		assertTrue(error.hasErrors());
	}

	@Test
	public void testIdInfoCount() {
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue("fingerImage");
		List<IdentityInfoDTO> deviceInfoList = new ArrayList<IdentityInfoDTO>();
		deviceInfoList.add(identityInfoDTO);

		Long idInfoCount = ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "getIdInfoCount", deviceInfoList);
		assertEquals(idInfoCount, Long.valueOf(1));
	}

	@Test
	public void testValidateIrisRequestCount() {
		authRequestDTO = getAuthRequestDTO();

		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue("iris");
		List<IdentityInfoDTO> leftEye = new ArrayList<IdentityInfoDTO>();
		leftEye.add(identityInfoDTO);

		IdentityDTO identity = new IdentityDTO();
		identity.setLeftEye(leftEye);

		RequestDTO request = new RequestDTO();
		request.setIdentity(identity);
		authRequestDTO.setRequest(request);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateIrisRequestCount", authRequestDTO, error);

		assertFalse(error.hasErrors());

	}

	@Test
	public void testValidateIrisRequestCount_hasLeftEyeRequestMoreThanOne() {
		authRequestDTO = getAuthRequestDTO();

		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue("iris");
		List<IdentityInfoDTO> leftEye = new ArrayList<IdentityInfoDTO>();
		leftEye.add(identityInfoDTO);
		leftEye.add(identityInfoDTO);

		IdentityDTO identity = new IdentityDTO();
		identity.setLeftEye(leftEye);

		RequestDTO request = new RequestDTO();
		request.setIdentity(identity);
		authRequestDTO.setRequest(request);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateIrisRequestCount", authRequestDTO, error);
		System.err.println(error);
		assertTrue(error.hasErrors());

	}

	@Test
	public void testCheckOTPAuth_HasNoError() {
		String otp = "456789";
		PinInfo pinInfo = new PinInfo();
		pinInfo.setType("OTP");
		pinInfo.setValue(otp);
		AuthRequestDTO authRequestDTO = getAuthRequestDTO();
		List<PinInfo> listOfPinInfo = new ArrayList<>();
		listOfPinInfo.add(pinInfo);
		authRequestDTO.setPinInfo(listOfPinInfo);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkOTPAuth", authRequestDTO, error);
		assertFalse(error.hasErrors());
	}

	@Test
	public void testCheckOTPAuth_HasNullValue_HasError() {
		AuthRequestDTO authRequestDTO = getAuthRequestDTO();
		authRequestDTO.setPinInfo(null);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkOTPAuth", authRequestDTO, error);
		assertTrue(error.hasErrors());
	}

	@Test
	public void testCheckOTPAuth_HasEmptyOTP_HasError() {
		String otp = "";
		PinInfo pinInfo = new PinInfo();
		pinInfo.setType("OTP");
		pinInfo.setValue(otp);
		AuthRequestDTO authRequestDTO = getAuthRequestDTO();
		List<PinInfo> listOfPinInfo = new ArrayList<>();
		listOfPinInfo.add(pinInfo);
		authRequestDTO.setPinInfo(listOfPinInfo);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkOTPAuth", authRequestDTO, error);
		assertTrue(error.hasErrors());
	}

	@Test
	public void testGetOtpValue() {

		String otp = "456789";
		PinInfo pinInfo = new PinInfo();
		pinInfo.setType("OTP");
		pinInfo.setValue(otp);
		AuthRequestDTO authRequestDTO = getAuthRequestDTO();
		List<PinInfo> listOfPinInfo = new ArrayList<>();
		listOfPinInfo.add(pinInfo);

		authRequestDTO.setPinInfo(listOfPinInfo);
		Optional<String> isOtp = ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "getOtpValue",
				authRequestDTO);
		assertTrue(isOtp.isPresent());
	}

	@Test
	public void testValidateEmail_ValidateEmail_IsTrue() {
		AuthRequestDTO authRequestDTO = getAuthRequestDTO();

		RequestDTO request = new RequestDTO();
		IdentityDTO identity = new IdentityDTO();

		List<IdentityInfoDTO> emailId = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setLanguage("FR");
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

	@Test
	public void testValidateEmail_ValidateEmail_IsFalse() {
		AuthRequestDTO authRequestDTO = getAuthRequestDTO();

		RequestDTO request = new RequestDTO();
		IdentityDTO identity = new IdentityDTO();

		List<IdentityInfoDTO> emailId = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setLanguage("FR");
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

	@Test
	public void testValidatePhone_ValidatePhone_IsTrue() {
		List<IdentityInfoDTO> phoneNumber = new ArrayList<IdentityInfoDTO>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setLanguage("FR");
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

	@Test
	public void testValidatePhone_ValidatePhone_IsFalse() {
		List<IdentityInfoDTO> phoneNumber = new ArrayList<IdentityInfoDTO>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setLanguage("FR");
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

	// ----------- Supporting method ---------------
	private AuthRequestDTO getAuthRequestDTO() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setTspID("1234567890");
		authRequestDTO.setTxnID("1234567890");
		authRequestDTO.setReqTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());

		authRequestDTO.setIdvIdType(IdType.UIN.getType());
		authRequestDTO.setIdvId("5371843613598206");

		return authRequestDTO;
	}

	@Test
	public void testValidAuthRequest() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIdvIdType(IdType.UIN.getType());
		authRequestDTO.setIdvId("234567890123");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setReqTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setTspID("1234567890");
		authRequestDTO.setTxnID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPersonalIdentity(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary.lang-code"));
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary.lang-code"));
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setAuthType(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		MatchInfo matchInfo = new MatchInfo();
		matchInfo.setAuthType("personalIdentity");
		matchInfo.setLanguage("FR");
		matchInfo.setMatchingStrategy("E");
		matchInfo.setMatchingThreshold(100);
		List<MatchInfo> matList = new ArrayList<>();
		matList.add(matchInfo);
		authRequestDTO.setMatchInfo(matList);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkDemoAuth", authRequestDTO, error);
		assertFalse(errors.hasErrors());
	}
	@Test
	public void testInValidAuthRequest_SecondaryLanguage() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIdvIdType(IdType.UIN.getType());
		authRequestDTO.setIdvId("234567890123");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setReqTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setTspID("1234567890");
		authRequestDTO.setTxnID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPersonalIdentity(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.secondary.lang-code"));
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary.lang-code"));
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setAuthType(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		MatchInfo matchInfo = new MatchInfo();
		matchInfo.setAuthType("fullAddress");
		matchInfo.setLanguage("Test");
		matchInfo.setMatchingStrategy("E");
		matchInfo.setMatchingThreshold(100);
		MatchInfo matchInfo111 = new MatchInfo();
		matchInfo111.setAuthType("fullAddress");
		matchInfo111.setLanguage("AR");
		matchInfo111.setMatchingStrategy("T");
		matchInfo111.setMatchingThreshold(100);
		List<MatchInfo> matList = new ArrayList<>();
		matList.add(matchInfo);
		matList.add(matchInfo111);
		authRequestDTO.setMatchInfo(matList);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkDemoAuth", authRequestDTO, error);
		assertFalse(errors.hasErrors());
	}
	@Test
	public void testInValidAuthRequest() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIdvIdType(IdType.UIN.getType());
		authRequestDTO.setIdvId("234567890123");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setReqTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setTspID("1234567890");
		authRequestDTO.setTxnID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPersonalIdentity(true);
		authTypeDTO.setFullAddress(true);
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
		authRequestDTO.setAuthType(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		MatchInfo matchInfo = new MatchInfo();
		matchInfo.setAuthType("personalIdentity");
		matchInfo.setLanguage("FR");
		matchInfo.setMatchingStrategy("E");
		matchInfo.setMatchingThreshold(100);
		MatchInfo matchInfo111 = new MatchInfo();
		matchInfo111.setAuthType("fullAddress");
		matchInfo111.setLanguage("AR");
		matchInfo111.setMatchingStrategy("T");
		matchInfo111.setMatchingThreshold(100);
		List<MatchInfo> matList = new ArrayList<>();
		matList.add(matchInfo);
		matList.add(matchInfo111);
		authRequestDTO.setMatchInfo(matList);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkDemoAuth", authRequestDTO, error);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void testValidAuthRequest2() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIdvIdType(IdType.UIN.getType());
		authRequestDTO.setIdvId("234567890123");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setReqTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setTspID("1234567890");
		authRequestDTO.setTxnID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPersonalIdentity(true);
		authTypeDTO.setAddress(true);
		authTypeDTO.setBio(true);
		authTypeDTO.setFullAddress(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary.lang-code"));
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary.lang-code"));
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setAuthType(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		MatchInfo matchInfo = new MatchInfo();
		matchInfo.setAuthType("personalIdentity");
		matchInfo.setLanguage("AR");
		matchInfo.setMatchingStrategy("Q");
		matchInfo.setMatchingThreshold(100);

		MatchInfo matchInfo1 = new MatchInfo();
		matchInfo1.setAuthType("address");
		matchInfo1.setLanguage("AR");
		matchInfo1.setMatchingStrategy("A");
		matchInfo1.setMatchingThreshold(100);

		MatchInfo matchInfo11 = new MatchInfo();
		matchInfo11.setAuthType("personalIdentity");
		matchInfo11.setLanguage("FR");
		matchInfo11.setMatchingStrategy("S");
		matchInfo11.setMatchingThreshold(100);

		MatchInfo matchInfo111 = new MatchInfo();
		matchInfo111.setAuthType("fullAddress");
		matchInfo111.setLanguage("AR");
		matchInfo111.setMatchingStrategy("T");
		matchInfo111.setMatchingThreshold(100);

		MatchInfo matchInfo1111 = new MatchInfo();
		matchInfo1111.setAuthType("fullAddress");
		matchInfo1111.setLanguage("FR");
		matchInfo1111.setMatchingStrategy("T");
		matchInfo1111.setMatchingThreshold(100);

		List<MatchInfo> matList = new ArrayList<>();
		matList.add(matchInfo1111);
		matList.add(matchInfo111);
		matList.add(matchInfo1);
		matList.add(matchInfo11);
		matList.add(matchInfo);
		authRequestDTO.setMatchInfo(matList);
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkDemoAuth", authRequestDTO, error);
		assertTrue(error.hasErrors());
	}

	@Test
	public void testValidateIrisRequestCountZero() {
		authRequestDTO = getAuthRequestDTO();

		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue("");
		List<IdentityInfoDTO> leftEye = new ArrayList<IdentityInfoDTO>();
		leftEye.add(identityInfoDTO);

		IdentityDTO identity = new IdentityDTO();
		identity.setLeftEye(leftEye);
		identity.setRightEye(leftEye);

		RequestDTO request = new RequestDTO();
		request.setIdentity(identity);
		authRequestDTO.setRequest(request);

		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateIrisRequestCount", authRequestDTO, error);
		assertFalse(error.hasErrors());

	}

	@Test
	public void testValidateMultiIrisRequest() {
		authRequestDTO = getAuthRequestDTO();

		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue(null);
		List<IdentityInfoDTO> leftEye = new ArrayList<IdentityInfoDTO>();
		leftEye.add(identityInfoDTO);
		IdentityInfoDTO identityInfoDTOs = new IdentityInfoDTO();
		identityInfoDTOs.setValue("");
		List<IdentityInfoDTO> rightEye = new ArrayList<IdentityInfoDTO>();
		rightEye.add(identityInfoDTOs);
		IdentityDTO identity = new IdentityDTO();
		identity.setLeftEye(leftEye);
		identity.setRightEye(rightEye);

		RequestDTO request = new RequestDTO();
		request.setIdentity(identity);
		authRequestDTO.setRequest(request);
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateMultiIrisValue", authRequestDTO, error);
		assertFalse(error.hasErrors());

	}

	@Test
	public void testInvalidMultiIrisRequest() {
		authRequestDTO = getAuthRequestDTO();

		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue(null);
		List<IdentityInfoDTO> leftEye = new ArrayList<IdentityInfoDTO>();
		leftEye.add(identityInfoDTO);
		IdentityInfoDTO identityInfoDTOs = new IdentityInfoDTO();
		identityInfoDTOs.setValue("Finger1");
		List<IdentityInfoDTO> rightEye = new ArrayList<IdentityInfoDTO>();
		rightEye.add(identityInfoDTOs);
		IdentityDTO identity = new IdentityDTO();
		identity.setLeftEye(rightEye);
		identity.setRightEye(rightEye);
		RequestDTO request = new RequestDTO();
		request.setIdentity(identity);
		authRequestDTO.setRequest(request);
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateMultiIrisValue", authRequestDTO, error);

		assertTrue(error.hasErrors());

	}
	
	@Test
	public void testValidateAdandFullAdd()
	{
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPersonalIdentity(true);
		authTypeDTO.setFullAddress(true);
		AuthRequestDTO authRequestDTO=new AuthRequestDTO();
		authRequestDTO.setAuthType(authTypeDTO);
		Set<String> availableAuthTypeInfos=new HashSet<>();
		availableAuthTypeInfos.add("address");
		availableAuthTypeInfos.add("fullAddress");
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "validateAdAndFullAd", availableAuthTypeInfos, error);
		assertTrue(error.hasErrors());
	}
	@Test
	public void testValidateAge()
	{
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary.lang-code"));
		idInfoDTO.setValue("16");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary.lang-code"));
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPersonalIdentity(true);
		authTypeDTO.setFullAddress(true);
		authRequestDTO.setAuthType(authTypeDTO);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setAge(idInfoList);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setAuthType(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkAge", authRequestDTO, error);
		assertTrue(error.hasErrors());
	}
	@Test
	public void testValidateDOB()
	{
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary.lang-code"));
		idInfoDTO.setValue("16");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary.lang-code"));
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPersonalIdentity(true);
		authTypeDTO.setFullAddress(true);
		authRequestDTO.setAuthType(authTypeDTO);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setDob(idInfoList);
		idDTO.setAge(idInfoList);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setAuthType(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		ReflectionTestUtils.invokeMethod(baseAuthRequestValidator, "checkDOB", authRequestDTO, error);
		assertTrue(error.hasErrors());
	}
	
	@Test
	public void testValidateDeviceInfo() {
		BioInfo bioinfo = new BioInfo();
	
		DeviceInfo deviceInfo = new DeviceInfo();
		deviceInfo.setDeviceId("test1");
		deviceInfo.setMake("test");
		deviceInfo.setModel("M123");
		DeviceInfo deviceInfo1 = new DeviceInfo();
		deviceInfo1.setDeviceId(null);
		deviceInfo1.setMake(null);
		deviceInfo1.setModel("M123");
		DeviceInfo deviceInfo2 = new DeviceInfo();
		deviceInfo2.setDeviceId("");
		deviceInfo2.setMake("");
		deviceInfo2.setModel("M123");
		DeviceInfo deviceInfo3 = new DeviceInfo();
		deviceInfo3.setDeviceId(null);
		deviceInfo3.setMake("");
		deviceInfo3.setModel("M123");
		bioinfo.setDeviceInfo(deviceInfo1);
		List<BioInfo> deviceInfoList = new ArrayList<BioInfo>();
		deviceInfoList.add(bioinfo);

		 ReflectionTestUtils.invokeMethod(baseAuthRequestValidator,
				"validateDeviceInfo", deviceInfoList,error);
		assertTrue(error.hasErrors());
	}
	@Test
	public void testValidateBioType() {
		BioInfo bioinfo = new BioInfo();
		bioinfo.setBioType("test");
		DeviceInfo deviceInfo = new DeviceInfo();
		deviceInfo.setDeviceId("test1");
		deviceInfo.setMake("test");
		deviceInfo.setModel("M123");
		DeviceInfo deviceInfo1 = new DeviceInfo();
		deviceInfo1.setDeviceId(null);
		deviceInfo1.setMake(null);
		deviceInfo1.setModel("M123");
		DeviceInfo deviceInfo2 = new DeviceInfo();
		deviceInfo2.setDeviceId("");
		deviceInfo2.setMake("");
		deviceInfo2.setModel("M123");
		DeviceInfo deviceInfo3 = new DeviceInfo();
		deviceInfo3.setDeviceId(null);
		deviceInfo3.setMake("");
		deviceInfo3.setModel("M123");
		bioinfo.setDeviceInfo(deviceInfo1);
		List<BioInfo> deviceInfoList = new ArrayList<BioInfo>();
		deviceInfoList.add(bioinfo);

		 ReflectionTestUtils.invokeMethod(baseAuthRequestValidator,
				"validateBioType", deviceInfoList,error);
		assertTrue(error.hasErrors());
	}
	
}
