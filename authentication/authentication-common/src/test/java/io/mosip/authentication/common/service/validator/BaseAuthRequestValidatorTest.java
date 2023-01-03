package io.mosip.authentication.common.service.validator;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.BIO_PATH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
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
import io.mosip.authentication.common.service.impl.IdInfoFetcherImpl;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.indauth.dto.DigitalId;
import io.mosip.authentication.core.indauth.dto.IdentityDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.kernel.core.pinvalidator.exception.InvalidPinException;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.pinvalidator.impl.PinValidatorImpl;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;

/**
 * Test class for {@link AuthRequestValidator}.
 *
 * @author Rakesh Roshan
 */

@RunWith(SpringRunner.class)
@WebMvcTest
@Import({ IDAMappingConfig.class, EnvUtil.class })
@ConfigurationProperties("mosip.id")
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, TemplateManagerBuilderImpl.class })
public class BaseAuthRequestValidatorTest {

	/** The validator. */
	@Mock
	private SpringValidatorAdapter validator;

	@Mock
	private PinValidatorImpl pinValidator;

	/** The auth request DTO. */
	@Mock
	AuthRequestDTO authRequestDTO;

	@InjectMocks
	AuthRequestValidator AuthRequestValidator;

	/** The id info helper. */
	@InjectMocks
	IdInfoHelper idInfoHelper;

	/** The id mapping config. */
	@Autowired
	private IDAMappingConfig idMappingConfig;
	
	@Mock
	private IdInfoFetcherImpl idInfoFetcher;

	/** The error. */
	Errors error;

	/**
	 * Before.
	 */
	@Before
	public void before() {
		error = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		ReflectionTestUtils.setField(AuthRequestValidator, "idInfoHelper", idInfoHelper);
		ReflectionTestUtils.setField(idInfoHelper, "idMappingConfig", idMappingConfig);
	}

	@Test
	public void testSupportTrue() {
		assertTrue(AuthRequestValidator.supports(AuthRequestDTO.class));
	}

	/**
	 * Test validate version and id.
	 */
	@Test
	public void testValidateVersionAndId() {
		AuthRequestDTO baseAuthRequestDTO = new AuthRequestDTO();
		baseAuthRequestDTO.setId("123456");
		baseAuthRequestDTO.setVersion("1.0");
		AuthRequestValidator.validate(baseAuthRequestDTO, error);
		assertTrue(error.hasErrors());
	}

	/**
	 * Test validate id has id no error.
	 */
	@Test
	public void testValidateId_HasId_NoError() {

		String id = "12345678";
		AuthRequestValidator.validateId(id, error);
		assertFalse(error.hasErrors());
	}

	/**
	 * Test validate id no id has error.
	 */
	@Test
	public void testValidateId_NoId_HasError() {

		String id = null;
		AuthRequestValidator.validateId(id, error);
		assertTrue(error.hasErrors());
	}

	/**
	 * Test validate bio details if bio info is null has error.
	 */
	@Test
	public void testValidateBioDetails_IfBioInfoIsNull_hasError() {

		authRequestDTO = getAuthRequestDTO();

		RequestDTO request = new RequestDTO();
		List<BioIdentityInfoDTO> bioInfo = new ArrayList<BioIdentityInfoDTO>();
		BioIdentityInfoDTO bioIdentity = new BioIdentityInfoDTO();
		bioIdentity.setData(null);
		bioInfo.add(bioIdentity);
		request.setBiometrics(bioInfo);
		authRequestDTO.setRequest(request);
		Set<String> allowedAuthtype = new HashSet<>();
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateBioMetadataDetails", authRequestDTO, error,
				allowedAuthtype);
		assertTrue(error.hasErrors());

	}

	/**
	 * Test validate bio details if bio info is not null but bio info is empty has
	 * error.
	 */
	@Test
	public void testValidateBioDetails_IfBioInfoIsNotNullButBioInfoIsEmpty_hasError() {

		authRequestDTO = getAuthRequestDTO();

		List<BioIdentityInfoDTO> bioInfoList = new ArrayList<BioIdentityInfoDTO>();
		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger = new DataDTO();
		dataDTOFinger.setDeviceCode("1");
		dataDTOFinger.setDeviceServiceVersion("1");
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("1");
		digitalId.setMake("1");
		digitalId.setModel("1");
		digitalId.setType("1");
		digitalId.setDeviceProvider("1");
		digitalId.setDeviceProviderId("1");
		digitalId.setDateTime(DateUtils.getCurrentDateTimeString());
		dataDTOFinger.setDigitalId(digitalId);
		dataDTOFinger.setBioValue("finger");
		dataDTOFinger.setBioSubType("Thumb");
		dataDTOFinger.setBioType(BioAuthType.FGR_IMG.getType());
		fingerValue.setData(dataDTOFinger);
		bioInfoList.add(fingerValue);
		RequestDTO dto = new RequestDTO();
		dto.setBiometrics(null);
		authRequestDTO.setRequest(dto);
		Set<String> allowedAuthtype = new HashSet<>();
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateBioMetadataDetails", authRequestDTO, error,
				allowedAuthtype);
		assertFalse(error.hasErrors());

	}
	
	/**
	 * Test validate bio details if bio info is not null but bio info is empty has
	 * error.
	 */
	@Test
	public void testValidateBioDetail_ForFMR_Enabled_NoError() {

		authRequestDTO = getAuthRequestDTO();
		
		EnvUtil.setIsFmrEnabled(true);

		List<BioIdentityInfoDTO> bioInfoList = new ArrayList<BioIdentityInfoDTO>();
		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger = new DataDTO();
		dataDTOFinger.setDeviceCode("1");
		dataDTOFinger.setDeviceServiceVersion("1");
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("1");
		digitalId.setMake("1");
		digitalId.setModel("1");
		digitalId.setType("1");
		digitalId.setDeviceProvider("1");
		digitalId.setDeviceProviderId("1");
		digitalId.setDateTime(DateUtils.getCurrentDateTimeString());
		dataDTOFinger.setDigitalId(digitalId);
		dataDTOFinger.setBioValue("finger");
		dataDTOFinger.setBioSubType("Left Thumb");
		dataDTOFinger.setBioType(BioAuthType.FGR_MIN.getType());
		fingerValue.setData(dataDTOFinger);
		bioInfoList.add(fingerValue);
		RequestDTO dto = new RequestDTO();
		dto.setBiometrics(bioInfoList);
		authRequestDTO.setRequest(dto);
		Set<String> allowedAuthtype = new HashSet<>();
		allowedAuthtype.add("bio-FMR");
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateBioMetadataDetails", authRequestDTO, error,
				allowedAuthtype);
		assertTrue(!error.hasErrors());

	}
	
	@Test
	public void testValidateBioDetail_ForFMR_NotEnabled_NoError() {

		authRequestDTO = getAuthRequestDTO();
		
		EnvUtil.setIsFmrEnabled(false);

		List<BioIdentityInfoDTO> bioInfoList = new ArrayList<BioIdentityInfoDTO>();
		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger = new DataDTO();
		dataDTOFinger.setDeviceCode("1");
		dataDTOFinger.setDeviceServiceVersion("1");
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("1");
		digitalId.setMake("1");
		digitalId.setModel("1");
		digitalId.setType("1");
		digitalId.setDeviceProvider("1");
		digitalId.setDeviceProviderId("1");
		digitalId.setDateTime(DateUtils.getCurrentDateTimeString());
		dataDTOFinger.setDigitalId(digitalId);
		dataDTOFinger.setBioValue("finger");
		dataDTOFinger.setBioSubType("Left Thumb");
		dataDTOFinger.setBioType(BioAuthType.FGR_MIN.getType());
		fingerValue.setData(dataDTOFinger);
		bioInfoList.add(fingerValue);
		RequestDTO dto = new RequestDTO();
		dto.setBiometrics(bioInfoList);
		authRequestDTO.setRequest(dto);
		Set<String> allowedAuthtype = new HashSet<>();
		allowedAuthtype.add("bio-FMR");
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateBioMetadataDetails", authRequestDTO, error,
				allowedAuthtype);
		assertTrue(!error.hasErrors());

	}

	/**
	 * Test validate bio details if bio info is not null but bio info is empty.
	 */
	@Test
	public void testValidateBioDetails_IfBioInfoIsNotNullButBioTypeIsEmpty() {

		authRequestDTO = getAuthRequestDTO();
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		List<BioIdentityInfoDTO> bioInfoList = new ArrayList<BioIdentityInfoDTO>();
		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger = new DataDTO();
		dataDTOFinger.setDeviceCode("1");
		dataDTOFinger.setDeviceServiceVersion("1");
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("1");
		digitalId.setMake("1");
		digitalId.setModel("1");
		digitalId.setType("1");
		digitalId.setDeviceProvider("1");
		digitalId.setDeviceProviderId("1");
		digitalId.setDateTime(DateUtils.getCurrentDateTimeString());
		dataDTOFinger.setDigitalId(digitalId);
		dataDTOFinger.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		dataDTOFinger.setBioValue("finger");
		dataDTOFinger.setBioSubType("Thumb");
		dataDTOFinger.setBioType("");
		fingerValue.setData(dataDTOFinger);
		bioInfoList.add(fingerValue);
		RequestDTO dto = new RequestDTO();
		dto.setBiometrics(bioInfoList);
		authRequestDTO.setRequest(dto);
		Set<String> allowedAuthtype = new HashSet<>();
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateBioMetadataDetails", authRequestDTO, error,
				allowedAuthtype);
		assertTrue(error.hasErrors());

	}

	/**
	 * Test validate bio details.
	 */
	@Test
	public void testValidateBioDetails() {

		authRequestDTO = getAuthRequestDTO();

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		dataDTO.setDeviceCode("1");
		dataDTO.setDeviceServiceVersion("1");
		dataDTO.setDeviceCode("1");
		dataDTO.setDeviceServiceVersion("1");
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("1");
		digitalId.setMake("1");
		digitalId.setModel("1");
		digitalId.setType("1");
		digitalId.setDeviceSubType("1");
		digitalId.setDeviceProvider("1");
		digitalId.setDeviceProviderId("1");
		digitalId.setDateTime(DateUtils.getCurrentDateTimeString());
		dataDTO.setDigitalId(digitalId);
		dataDTO.setBioValue("finger");
		dataDTO.setBioSubType("Left Thumb");
		dataDTO.setBioType("Finger");
		dataDTO.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		DataDTO dataDTO1 = new DataDTO();
		dataDTO.setDeviceCode("1");
		dataDTO.setDeviceServiceVersion("1");
		dataDTO1.setDeviceCode("1");
		dataDTO1.setDeviceServiceVersion("1");
		DigitalId digitalId1 = new DigitalId();
		digitalId1.setSerialNo("1");
		digitalId1.setMake("1");
		digitalId1.setModel("1");
		digitalId1.setType("1");
		digitalId1.setDeviceSubType("1");
		digitalId1.setDeviceProvider("1");
		digitalId1.setDeviceProviderId("1");
		digitalId1.setDateTime(DateUtils.getCurrentDateTimeString());
		dataDTO1.setDigitalId(digitalId1);
		dataDTO1.setBioValue("iris img");
		dataDTO1.setBioSubType("Left");
		dataDTO1.setBioType("Iris");
		dataDTO1.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		irisValue.setData(dataDTO1);
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		DataDTO dataDTO2 = new DataDTO();
		dataDTO.setDeviceCode("1");
		dataDTO.setDeviceServiceVersion("1");
		dataDTO2.setDeviceCode("1");
		dataDTO2.setDeviceServiceVersion("1");
		DigitalId digitalId2 = new DigitalId();
		digitalId2.setSerialNo("1");
		digitalId2.setMake("1");
		digitalId2.setModel("1");
		digitalId2.setType("1");
		digitalId2.setDeviceSubType("1");
		digitalId2.setDeviceProvider("1");
		digitalId2.setDeviceProviderId("1");
		digitalId2.setDateTime(DateUtils.getCurrentDateTimeString());
		dataDTO2.setDigitalId(digitalId2);
		dataDTO2.setBioValue("face img");
		dataDTO2.setBioType("FACE");
		dataDTO2.setTimestamp(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
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
		Set<String> allowedAuthtype = new HashSet<>();
		allowedAuthtype.add("bio-FACE");
		allowedAuthtype.add("bio-Finger");
		allowedAuthtype.add("bio-Iris");
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateBioMetadataDetails", authRequestDTO, error,
				allowedAuthtype);
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
		dataDTO.setBioType("Finger");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		dataDTO.setBioValue("iris img");
		dataDTO.setBioSubType("LEFT");
		dataDTO.setBioType("Iris");
		irisValue.setData(dataDTO);
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		dataDTO.setBioValue("face img");
		dataDTO.setBioSubType("Thumb");
		dataDTO.setBioType("FID");
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
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateFinger", authRequestDTO, data, error);
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
		dataDTO.setBioType("Finger");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		dataDTO.setBioValue("iris img");
		dataDTO.setBioSubType("LEFT");
		dataDTO.setBioType("Iris");
		irisValue.setData(dataDTO);
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		dataDTO.setBioValue("face img");
		dataDTO.setBioSubType("Thumb");
		dataDTO.setBioType("FID");
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

		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateIris", authRequestDTO, bioInfoList, error);
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
		dataDTO.setBioType("Finger");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		dataDTO.setBioValue("iris img");
		dataDTO.setBioSubType("right");
		dataDTO.setBioType("Iris");
		irisValue.setData(dataDTO);
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		dataDTO.setBioValue("face img");
		dataDTO.setBioSubType("Thumb");
		dataDTO.setBioType("FID");
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

		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateIris", authRequestDTO, bioInfoList, error);
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
		dataDTO.setBioType("Finger");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		dataDTO.setBioValue("iris img");
		dataDTO.setBioSubType("left");
		dataDTO.setBioType("Iris");
		irisValue.setData(dataDTO);
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		DataDTO faceData = new DataDTO();
		dataDTO.setDeviceCode("1");
		dataDTO.setDeviceServiceVersion("1");
		DigitalId digitalId1 = new DigitalId();
		digitalId1.setSerialNo("1");
		digitalId1.setMake("1");
		digitalId1.setModel("1");
		digitalId1.setType("1");
		digitalId1.setDeviceProvider("1");
		digitalId1.setDeviceProviderId("1");
		digitalId1.setDateTime(DateUtils.getCurrentDateTimeString());
		dataDTO.setDigitalId(digitalId1);
		faceData.setBioValue("face img");
		faceData.setBioType("Face");
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

		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateFace", authRequestDTO, bioInfoList, error);
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
		faceData.setDeviceCode("1");
		faceData.setDeviceServiceVersion("1");
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("1");
		digitalId.setMake("1");
		digitalId.setModel("1");
		digitalId.setType("1");
		digitalId.setDeviceProvider("1");
		digitalId.setDeviceProviderId("1");
		digitalId.setDateTime(DateUtils.getCurrentDateTimeString());
		faceData.setDigitalId(digitalId);
		faceData.setBioValue("face img");
		faceData.setBioType("FACE");
		faceValue.setData(faceData);

		BioIdentityInfoDTO faceValue1 = new BioIdentityInfoDTO();

		faceData.setBioValue("face img");
		faceData.setBioType("FACE");
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

		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateFace", authRequestDTO, bioInfoList, error);
		assertTrue(error.hasErrors());

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
		boolean checkAnyIdInfoAvailable = AuthRequestValidator.checkAnyIdInfoAvailable(authRequestDTO, fun);
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
		boolean checkAnyIdInfoAvailable = AuthRequestValidator.checkAnyIdInfoAvailable(authRequestDTO, fun);
		assertFalse(checkAnyIdInfoAvailable);
	}

	/**
	 * Test is bio type available bio type availabe return true.
	 */
	@Test
	public void testIsBioTypeAvailable_BioTypeAvailabe_ReturnTrue() {
		List<DataDTO> bioInfoList = new ArrayList<DataDTO>();
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
		dataDTO.setBioType(BioAuthType.FACE_IMG.getType());
		bioInfoList.add(dataDTO);
		boolean isBioTypeAvailable = ReflectionTestUtils.invokeMethod(AuthRequestValidator, "isAvailableBioType",
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
		dataDTO.setBioType(BioAuthType.IRIS_COMP_IMG.getType());
		bioInfoList.add(dataDTO);
		boolean isBioTypeAvailable = ReflectionTestUtils.invokeMethod(AuthRequestValidator, "isAvailableBioType",
				bioInfoList, BioAuthType.FACE_IMG);
		assertFalse(isBioTypeAvailable);

	}

	/**
	 * Test is duplicate bio type true.
	 */
	@Ignore
	@Test
	public void testIsDuplicateBioType_True() {

		authRequestDTO = getAuthRequestDTO();

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
		dataDTO.setBioType("Iris");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		DataDTO dataDTOIris = new DataDTO();
		dataDTO.setDeviceCode("1");
		dataDTO.setDeviceServiceVersion("1");
		DigitalId digitalId1 = new DigitalId();
		digitalId1.setSerialNo("1");
		digitalId1.setMake("1");
		digitalId1.setModel("1");
		digitalId1.setType("1");
		digitalId1.setDeviceProvider("1");
		digitalId1.setDeviceProviderId("1");
		digitalId1.setDateTime(DateUtils.getCurrentDateTimeString());
		dataDTO.setDigitalId(digitalId1);
		dataDTOIris.setBioValue("iris img");
		dataDTOIris.setBioSubType("left");
		dataDTOIris.setBioType("Iris");
		irisValue.setData(dataDTOIris);
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFace = new DataDTO();
		dataDTO.setDeviceCode("1");
		dataDTO.setDeviceServiceVersion("1");
		DigitalId digitalId2 = new DigitalId();
		digitalId2.setSerialNo("1");
		digitalId2.setMake("1");
		digitalId2.setModel("1");
		digitalId2.setType("1");
		digitalId2.setDeviceProvider("1");
		digitalId2.setDeviceProviderId("1");
		digitalId2.setDateTime(DateUtils.getCurrentDateTimeString());
		dataDTO.setDigitalId(digitalId2);
		dataDTOFace.setBioValue("face img");
		dataDTOFace.setBioSubType("Thumb");
		dataDTOFace.setBioType("Iris");
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

		boolean isDuplicateBioType = ReflectionTestUtils.invokeMethod(AuthRequestValidator, "isDuplicateBioType",
				authRequestDTO, BioAuthType.IRIS_IMG);
		assertTrue(isDuplicateBioType);
	}

	/**
	 * Test is duplicate bio type false.
	 */
	@Ignore
	@Test
	public void testIsDuplicateBioType_False() {
		authRequestDTO = getAuthRequestDTO();

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
		dataDTO.setBioType("Iris");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		DataDTO dataDTOIris = new DataDTO();
		dataDTO.setDeviceCode("1");
		dataDTO.setDeviceServiceVersion("1");
		DigitalId digitalId1 = new DigitalId();
		digitalId1.setSerialNo("1");
		digitalId1.setMake("1");
		digitalId1.setModel("1");
		digitalId1.setType("1");
		digitalId1.setDeviceProvider("1");
		digitalId1.setDeviceProviderId("1");
		digitalId1.setDateTime(DateUtils.getCurrentDateTimeString());
		dataDTO.setDigitalId(digitalId1);
		dataDTOIris.setBioValue("iris img");
		dataDTOIris.setBioSubType("left");
		dataDTOIris.setBioType("");
		irisValue.setData(dataDTOIris);

		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);

		boolean isDuplicateBioType = ReflectionTestUtils.invokeMethod(AuthRequestValidator, "isDuplicateBioType",
				authRequestDTO, BioAuthType.IRIS_IMG);
		assertTrue(isDuplicateBioType);
	}

	/**
	 * Test is duplicate bio type iris.
	 */
	@Ignore
	@Test
	public void testIsDuplicateBioTypeIris() {
		authRequestDTO = getAuthRequestDTO();

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
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		DataDTO dataDTOIris = new DataDTO();
		dataDTO.setDeviceCode("1");
		dataDTO.setDeviceServiceVersion("1");
		DigitalId digitalId1 = new DigitalId();
		digitalId1.setSerialNo("1");
		digitalId1.setMake("1");
		digitalId1.setModel("1");
		digitalId1.setType("1");
		digitalId1.setDeviceProvider("1");
		digitalId1.setDeviceProviderId("1");
		digitalId1.setDateTime(DateUtils.getCurrentDateTimeString());
		dataDTO.setDigitalId(digitalId1);
		dataDTOIris.setBioValue("iris img");
		dataDTOIris.setBioSubType("left");
		dataDTOIris.setBioType("");
		irisValue.setData(dataDTOIris);

		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);

		boolean isDuplicateBioType = ReflectionTestUtils.invokeMethod(AuthRequestValidator, "isDuplicateBioType",
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

		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateFingerRequestCount", authRequestDTO, error,
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
		dataDTO.setBioType("FMR");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		DataDTO dataDTOIris = new DataDTO();
		dataDTO.setDeviceCode("1");
		dataDTO.setDeviceServiceVersion("1");
		DigitalId digitalId1 = new DigitalId();
		digitalId1.setSerialNo("1");
		digitalId1.setMake("1");
		digitalId1.setModel("1");
		digitalId1.setType("1");
		digitalId1.setDeviceProvider("1");
		digitalId1.setDeviceProviderId("1");
		digitalId1.setDateTime(DateUtils.getCurrentDateTimeString());
		dataDTO.setDigitalId(digitalId1);
		dataDTOIris.setBioValue("finger");
		dataDTOIris.setBioSubType("Thumb");
		dataDTOIris.setBioType("FMR");
		irisValue.setData(dataDTOIris);
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFace = new DataDTO();
		dataDTO.setDeviceCode("1");
		dataDTO.setDeviceServiceVersion("1");
		DigitalId digitalId2 = new DigitalId();
		digitalId2.setSerialNo("1");
		digitalId2.setMake("1");
		digitalId2.setModel("1");
		digitalId2.setType("1");
		digitalId2.setDeviceProvider("1");
		digitalId2.setDeviceProviderId("1");
		digitalId2.setDateTime(DateUtils.getCurrentDateTimeString());
		dataDTO.setDigitalId(digitalId2);
		dataDTOFace.setBioValue("face img");
		dataDTOFace.setBioSubType("Thumb");
		dataDTOFace.setBioType("FID");
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
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateFingerRequestCount", authRequestDTO, error,
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
		dataDTO.setBioType("Finger");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		DataDTO fingerValue2 = new DataDTO();
		dataDTO.setDeviceCode("1");
		dataDTO.setDeviceServiceVersion("1");
		DigitalId digitalId1 = new DigitalId();
		digitalId1.setSerialNo("1");
		digitalId1.setMake("1");
		digitalId1.setModel("1");
		digitalId1.setType("1");
		digitalId1.setDeviceProvider("1");
		digitalId1.setDeviceProviderId("1");
		digitalId1.setDateTime(DateUtils.getCurrentDateTimeString());
		dataDTO.setDigitalId(digitalId1);
		fingerValue2.setBioValue("finger");
		fingerValue2.setBioSubType("INDEXFINGER");
		fingerValue2.setBioType("Finger");
		irisValue.setData(fingerValue2);
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);

		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateFingerRequestCount", authRequestDTO, error,
				"Finger");
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
		dataDTO.setBioType("FMR");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		DataDTO fingerValue2 = new DataDTO();
		dataDTO.setDeviceCode("1");
		dataDTO.setDeviceServiceVersion("1");
		DigitalId digitalId1 = new DigitalId();
		digitalId1.setSerialNo("1");
		digitalId1.setMake("1");
		digitalId1.setModel("1");
		digitalId1.setType("1");
		digitalId1.setDeviceProvider("1");
		digitalId1.setDeviceProviderId("1");
		digitalId1.setDateTime(DateUtils.getCurrentDateTimeString());
		dataDTO.setDigitalId(digitalId1);
		fingerValue2.setBioValue("finger");
		fingerValue2.setBioSubType("INDEXFINGER");
		fingerValue2.setBioType("FMR");
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

		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateFingerRequestCount", authRequestDTO, error,
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
		dataDTO.setBioValue("iris img");
		dataDTO.setBioSubType("left");
		dataDTO.setBioType("Iris");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		DataDTO fingerValue2 = new DataDTO();
		dataDTO.setDeviceCode("1");
		dataDTO.setDeviceServiceVersion("1");
		DigitalId digitalId1 = new DigitalId();
		digitalId1.setSerialNo("1");
		digitalId1.setMake("1");
		digitalId1.setModel("1");
		digitalId1.setType("1");
		digitalId1.setDeviceProvider("1");
		digitalId1.setDeviceProviderId("1");
		digitalId1.setDateTime(DateUtils.getCurrentDateTimeString());
		dataDTO.setDigitalId(digitalId1);
		fingerValue2.setBioValue("finger");
		fingerValue2.setBioSubType("INDEXFINGER");
		fingerValue2.setBioType("FMR");
		irisValue.setData(fingerValue2);
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(fingerValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);

		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateIrisRequestCount", authRequestDTO, error);

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
		dataDTO.setBioValue("iris img");
		dataDTO.setBioSubType("left");
		dataDTO.setBioType("Iris");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		DataDTO fingerValue2 = new DataDTO();
		dataDTO.setDeviceCode("1");
		dataDTO.setDeviceServiceVersion("1");
		DigitalId digitalId1 = new DigitalId();
		digitalId1.setSerialNo("1");
		digitalId1.setMake("1");
		digitalId1.setModel("1");
		digitalId1.setType("1");
		digitalId1.setDeviceProvider("1");
		digitalId1.setDeviceProviderId("1");
		digitalId1.setDateTime(DateUtils.getCurrentDateTimeString());
		dataDTO.setDigitalId(digitalId1);
		fingerValue2.setBioValue("iris img");
		fingerValue2.setBioSubType("left");
		fingerValue2.setBioType("Iris");
		irisValue.setData(fingerValue2);
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);

		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateIrisRequestCount", authRequestDTO, error);
		assertTrue(error.hasErrors());

	}

	// /**
	// * Test check OTP auth has no error.
	// */
	// @Test
	// public void testCheckOTPAuth_HasNoError() {
	// String otp = "456789";
	// AuthRequestDTO authRequestDTO = getAuthRequestDTO();
	// RequestDTO request = new RequestDTO();
	// request.setOtp(otp);
	// authRequestDTO.setRequest(request);
	//
	// ReflectionTestUtils.invokeMethod(AuthRequestValidator, "checkOTPAuth",
	// authRequestDTO, error);
	// assertFalse(error.hasErrors());
	// }
	//
	// /**
	// * Test check OTP auth has null value has error.
	// */
	// @Test
	// public void testCheckOTPAuth_HasNullValue_HasError() {
	// AuthRequestDTO authRequestDTO = getAuthRequestDTO();
	//
	// ReflectionTestUtils.invokeMethod(AuthRequestValidator, "checkOTPAuth",
	// authRequestDTO, error);
	// assertTrue(error.hasErrors());
	// }

	// /**
	// * Test check OTP auth has empty OT P has error.
	// */
	// @Test
	// public void testCheckOTPAuth_HasEmptyOTP_HasError() {
	// String otp = "";
	// AuthRequestDTO authRequestDTO = getAuthRequestDTO();
	// RequestDTO request = new RequestDTO();
	// request.setOtp(otp);
	// authRequestDTO.setRequest(request);
	// ReflectionTestUtils.invokeMethod(AuthRequestValidator, "checkOTPAuth",
	// authRequestDTO, error);
	// assertTrue(error.hasErrors());
	// }

	// /**
	// * Test get otp value.
	// */
	// @Test
	// public void testGetOtpValue() {
	//
	// String otp = "456789";
	// AuthRequestDTO authRequestDTO = getAuthRequestDTO();
	// RequestDTO request = new RequestDTO();
	// request.setOtp(otp);
	// authRequestDTO.setRequest(request);
	//
	// Optional<String> isOtp =
	// ReflectionTestUtils.invokeMethod(AuthRequestValidator, "getOtpValue",
	// authRequestDTO);
	// assertTrue(isOtp.isPresent());
	// }

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
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());

		return authRequestDTO;
	}

	/**
	 * Test valid auth request.
	 */
	@Test
	public void testValidAuthRequest() {
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
		idDTO.setDob("25/11/1990");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequest(reqDTO);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "checkDemoAuth", authRequestDTO, error);
		assertFalse(errors.hasErrors());
	}

	/**
	 * Test in valid auth request secondary language.
	 */

	@Test
	public void testInValidAuthRequest_SecondaryLanguage() {
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
		idDTO.setDob("25/11/1990");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequest(reqDTO);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "checkDemoAuth", authRequestDTO, error);
		assertFalse(errors.hasErrors());
	}

	/**
	 * Test in valid auth request.
	 */

	@Test
	public void testInValidAuthRequest() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setTransactionID("1234567890");
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
		idInfoDTOs.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequest(reqDTO);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "checkDemoAuth", authRequestDTO, error);
		assertFalse(errors.hasErrors());
	}

	/**
	 * Test valid auth request 2.
	 */

	@Test
	public void testValidAuthRequest2() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setTransactionID("1234567890");
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage("eng");
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage("fra");
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);

		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setDob("1990/11/25");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage("fra");
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		idDTO.setName(idInfoList);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequest(reqDTO);
		Mockito.when(idInfoFetcher.getSystemSupportedLanguageCodes()).thenReturn(List.of("eng","fra"));
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "checkDemoAuth", authRequestDTO, error);
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
		// identity.setLeftEye(leftEye);
		// identity.setRightEye(leftEye);

		RequestDTO request = new RequestDTO();
		request.setDemographics(identity);
		authRequestDTO.setRequest(request);

		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateIrisRequestCount", authRequestDTO, error);
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
		dataDTO.setBioValue("iris img");
		dataDTO.setBioSubType("left");
		dataDTO.setBioType("Iris");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		DataDTO fingerValue2 = new DataDTO();
		dataDTO.setDeviceCode("1");
		dataDTO.setDeviceServiceVersion("1");
		DigitalId digitalId2 = new DigitalId();
		digitalId2.setSerialNo("1");
		digitalId2.setMake("1");
		digitalId2.setModel("1");
		digitalId2.setType("1");
		digitalId2.setDeviceProvider("1");
		digitalId2.setDeviceProviderId("1");
		digitalId2.setDateTime(DateUtils.getCurrentDateTimeString());
		dataDTO.setDigitalId(digitalId2);
		fingerValue2.setBioValue("finger");
		fingerValue2.setBioSubType("right");
		fingerValue2.setBioType("Iris");
		irisValue.setData(fingerValue2);
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(fingerValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);

		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateMultiIrisValue", authRequestDTO, error);
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
		dataDTO.setBioValue("iris img");
		dataDTO.setBioSubType("left");
		dataDTO.setBioType("Iris");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		DataDTO fingerValue2 = new DataDTO();
		dataDTO.setDeviceCode("1");
		dataDTO.setDeviceServiceVersion("1");
		DigitalId digitalId1 = new DigitalId();
		digitalId1.setSerialNo("1");
		digitalId1.setMake("1");
		digitalId1.setModel("1");
		digitalId1.setType("1");
		digitalId1.setDeviceProvider("1");
		digitalId1.setDeviceProviderId("1");
		digitalId1.setDateTime(DateUtils.getCurrentDateTimeString());
		dataDTO.setDigitalId(digitalId1);
		fingerValue2.setBioValue("iris img");
		fingerValue2.setBioSubType("left");
		fingerValue2.setBioType("Iris");
		irisValue.setData(fingerValue2);
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(fingerValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);

		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateMultiIrisValue", authRequestDTO, error);
		assertTrue(error.hasErrors());

	}

	/**
	 * Test validate adand full add.
	 */
	@Test
	public void testValidateAdandFullAdd() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Set<String> availableAuthTypeInfos = new HashSet<>();
		availableAuthTypeInfos.add("address");
		availableAuthTypeInfos.add("fullAddress");
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateAdAndFullAd", availableAuthTypeInfos, error);
		assertTrue(error.hasErrors());
	}

	/**
	 * Test validate age.
	 */

	@Test
	public void testValidateAge_Valid() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTO.setValue("16");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setAge("25");
		idDTO.setDob("25/11/1990");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequest(reqDTO);
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "checkAge", authRequestDTO, error);
		assertFalse(error.hasErrors());
	}

	@Test
	public void testValidateAge_InValid() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTO.setValue("16");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setAge("25/01/1998");
		idDTO.setDob("25/11/1990");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequest(reqDTO);
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "checkAge", authRequestDTO, error);
		assertTrue(error.hasErrors());
	}

	@Test
	public void testPinDetails_success() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setTransactionID("1234567890");
		String pin = "456789";
		RequestDTO request = new RequestDTO();
		request.setStaticPin(pin);
		authRequestDTO.setRequest(request);
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateAdditionalFactorsDetails", authRequestDTO,
				error);
	}

	@Test
	public void testPinDetails_isEmpty() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setTransactionID("1234567890");
		String pin = "";
		RequestDTO request = new RequestDTO();
		request.setStaticPin(pin);
		authRequestDTO.setRequest(request);
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateAdditionalFactorsDetails", authRequestDTO,
				error);
	}

	@Test
	public void testPinDetails_isNull() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setTransactionID("1234567890");
		String pin = null;
		RequestDTO request = new RequestDTO();
		request.setStaticPin(pin);
		authRequestDTO.setRequest(request);
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateAdditionalFactorsDetails", authRequestDTO,
				error);
	}

	@Test
	public void testPinDetails_invalidPinTypePinValue() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setTransactionID("1234567890");
		String pin = "123e45";
		RequestDTO request = new RequestDTO();
		request.setStaticPin(pin);
		authRequestDTO.setRequest(request);
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateAdditionalFactorsDetails", authRequestDTO,
				error);
	}

	@Test
	public void testOTP_validOTPValue() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setTransactionID("1234567890");
		String otp = "123445";
		RequestDTO request = new RequestDTO();
		request.setOtp(otp);
		authRequestDTO.setRequest(request);
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateAdditionalFactorsDetails", authRequestDTO,
				error);
	}

	@Test
	public void testOTP_InValidOTPValue() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setTransactionID("1234567890");
		String otp = null;
		RequestDTO request = new RequestDTO();
		request.setOtp(otp);
		authRequestDTO.setRequest(request);
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateAdditionalFactorsDetails", authRequestDTO,
				error);
	}

	@Test
	public void testBioType() {
		List<DataDTO> bioInfoList = new ArrayList<DataDTO>();
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
		bioInfoList.add(dataDTO);
		Set<String> allowedAuthtype = new HashSet<>();
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateBioType", bioInfoList, error, allowedAuthtype);
		assertTrue(error.hasErrors());
	}

	@Test
	public void testDOBType() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setTransactionID("1234567890");

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
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "checkDOBType", authRequestDTO, error);
		assertTrue(error.hasErrors());
	}

	@Test
	public void testInValidateBioDetails_DeviceProviderID() {

		authRequestDTO = getAuthRequestDTO();

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
		dataDTO.setBioType("Finger");
		fingerValue.setData(dataDTO);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		dataDTO.setBioValue("iris img");
		dataDTO.setBioSubType("left");
		dataDTO.setBioType("Iris");
		irisValue.setData(dataDTO);
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		dataDTO.setBioValue("face img");
		dataDTO.setBioSubType("Thumb");
		dataDTO.setBioType("FID");
		faceValue.setData(dataDTO);

		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);
		RequestDTO reqDTO = new RequestDTO();
		authRequestDTO.setRequest(reqDTO);
		reqDTO.setBiometrics(fingerIdentityInfoDtoList);
		Set<String> allowedAuthtype = new HashSet<>();
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateBioMetadataDetails", authRequestDTO, error,
				allowedAuthtype);
		assertTrue(error.hasErrors());

	}

	@Test
	public void TestvalidateBioData() {
		List<DataDTO> bioData = new ArrayList<>();
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
		dataDTO.setBioSubType("LEFT_INDEX");
		dataDTO.setBioType("FMR");
		dataDTO.setBioValue(null);
		bioData.add(dataDTO);
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateBioData", bioData, error);
		assertTrue(error.hasErrors());
	}

	@Test
	public void TestvalidateBioType() {
		Set<String> allowedType = new HashSet<>();
		allowedType.add("FACE");
		DataDTO bioInfo = new DataDTO();
		bioInfo.setDeviceCode("1");
		bioInfo.setDeviceServiceVersion("1");
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("1");
		digitalId.setMake("1");
		digitalId.setModel("1");
		digitalId.setType("1");
		digitalId.setDeviceProvider("1");
		digitalId.setDeviceProviderId("1");
		digitalId.setDateTime(DateUtils.getCurrentDateTimeString());
		bioInfo.setDigitalId(digitalId);
		bioInfo.setBioType("FINGER");
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateBioType", error, allowedType, bioInfo, 0);
		assertTrue(error.hasErrors());
	}

	@Test
	public void TestInvalidPinException() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		request.setStaticPin("123456");
		authRequestDTO.setRequest(request);
		Mockito.when(pinValidator.validatePin(Mockito.anyString()))
				.thenThrow(new InvalidPinException(IdAuthenticationErrorConstants.PIN_MISMATCH.getErrorCode(),
						IdAuthenticationErrorConstants.PIN_MISMATCH.getErrorCode()));
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateAdditionalFactorsDetails", authRequestDTO,
				error);
	}
	
	@Test
	public void TestInvalidPinExceptionForOTP() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		request.setOtp("123456");
		authRequestDTO.setRequest(request);
		Mockito.when(pinValidator.validatePin(Mockito.anyString()))
				.thenThrow(new InvalidPinException(IdAuthenticationErrorConstants.PIN_MISMATCH.getErrorCode(),
						IdAuthenticationErrorConstants.PIN_MISMATCH.getErrorCode()));
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateAdditionalFactorsDetails", authRequestDTO,
				error);
	}

	@Test
	public void testValidateDeviceDetailsMissingDeviceCode() {
		DataDTO dataDTO = new DataDTO();
		dataDTO.setDeviceCode("");
		dataDTO.setDeviceServiceVersion("1");
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("1");
		digitalId.setMake("1");
		digitalId.setModel("1");
		digitalId.setType("1");
		digitalId.setDeviceSubType("1");
		digitalId.setDeviceProvider("1");
		digitalId.setDeviceProviderId("1");
		digitalId.setDateTime(DateUtils.getCurrentDateTimeString());
		dataDTO.setDigitalId(digitalId);
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		BioIdentityInfoDTO bioInfo = new BioIdentityInfoDTO();
		bioInfo.setData(dataDTO);
		List<BioIdentityInfoDTO> biometrics = Collections.singletonList(bioInfo);
		request.setBiometrics(biometrics);
		authRequestDTO.setRequest(request);
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateDeviceDetails", authRequestDTO, error);
		assertEquals(String.format(BIO_PATH, 0, "deviceCode"), error.getFieldErrors().get(0).getArguments()[0]);
	}

	@Test
	public void testValidateDeviceDetailsMissingDeviceServiceVersion() {
		DataDTO dataDTO = new DataDTO();
		dataDTO.setDeviceCode("1");
		dataDTO.setDeviceServiceVersion("");
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("1");
		digitalId.setMake("1");
		digitalId.setModel("1");
		digitalId.setType("1");
		digitalId.setDeviceProvider("1");
		digitalId.setDeviceProviderId("1");
		digitalId.setDateTime(DateUtils.getCurrentDateTimeString());
		dataDTO.setDigitalId(digitalId);
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		BioIdentityInfoDTO bioInfo = new BioIdentityInfoDTO();
		bioInfo.setData(dataDTO);
		List<BioIdentityInfoDTO> biometrics = Collections.singletonList(bioInfo);
		request.setBiometrics(biometrics);
		authRequestDTO.setRequest(request);
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateDeviceDetails", authRequestDTO, error);
		assertEquals(String.format(BIO_PATH, 0, "deviceServiceVersion"),
				error.getFieldErrors().get(0).getArguments()[0]);
	}

	@Test
	public void testValidateDeviceDetailsMissingSerialNo() {
		DataDTO dataDTO = new DataDTO();
		dataDTO.setDeviceCode("1");
		dataDTO.setDeviceServiceVersion("1");
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("");
		digitalId.setMake("1");
		digitalId.setModel("1");
		digitalId.setType("1");
		digitalId.setDeviceProvider("1");
		digitalId.setDeviceProviderId("1");
		digitalId.setDateTime(DateUtils.getCurrentDateTimeString());
		dataDTO.setDigitalId(digitalId);
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		BioIdentityInfoDTO bioInfo = new BioIdentityInfoDTO();
		bioInfo.setData(dataDTO);
		List<BioIdentityInfoDTO> biometrics = Collections.singletonList(bioInfo);
		request.setBiometrics(biometrics);
		authRequestDTO.setRequest(request);
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateDeviceDetails", authRequestDTO, error);
		assertEquals(String.format(BIO_PATH, 0, "digitalId/serialNo"), error.getFieldErrors().get(0).getArguments()[0]);
	}

	@Test
	public void testValidateDeviceDetailsMissingMake() {
		DataDTO dataDTO = new DataDTO();
		dataDTO.setDeviceCode("1");
		dataDTO.setDeviceServiceVersion("1");
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("1");
		digitalId.setMake("");
		digitalId.setModel("1");
		digitalId.setType("1");
		digitalId.setDeviceProvider("1");
		digitalId.setDeviceProviderId("1");
		digitalId.setDateTime(DateUtils.getCurrentDateTimeString());
		dataDTO.setDigitalId(digitalId);
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		BioIdentityInfoDTO bioInfo = new BioIdentityInfoDTO();
		bioInfo.setData(dataDTO);
		List<BioIdentityInfoDTO> biometrics = Collections.singletonList(bioInfo);
		request.setBiometrics(biometrics);
		authRequestDTO.setRequest(request);
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateDeviceDetails", authRequestDTO, error);
		assertEquals(String.format(BIO_PATH, 0, "data/digitalId/make"), error.getFieldErrors().get(0).getArguments()[0]);
	}

	@Test
	public void testValidateDeviceDetailsMissingModel() {
		DataDTO dataDTO = new DataDTO();
		dataDTO.setDeviceCode("1");
		dataDTO.setDeviceServiceVersion("1");
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("1");
		digitalId.setMake("1");
		digitalId.setModel("");
		digitalId.setType("1");
		digitalId.setDeviceProvider("1");
		digitalId.setDeviceProviderId("1");
		digitalId.setDateTime(DateUtils.getCurrentDateTimeString());
		dataDTO.setDigitalId(digitalId);
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		BioIdentityInfoDTO bioInfo = new BioIdentityInfoDTO();
		bioInfo.setData(dataDTO);
		List<BioIdentityInfoDTO> biometrics = Collections.singletonList(bioInfo);
		request.setBiometrics(biometrics);
		authRequestDTO.setRequest(request);
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateDeviceDetails", authRequestDTO, error);
		assertEquals(String.format(BIO_PATH, 0, "data/digitalId/model"), error.getFieldErrors().get(0).getArguments()[0]);
	}

	@Test
	public void testValidateDeviceDetailsMissingType() {
		DataDTO dataDTO = new DataDTO();
		dataDTO.setDeviceCode("1");
		dataDTO.setDeviceServiceVersion("1");
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("1");
		digitalId.setMake("1");
		digitalId.setModel("1");
		digitalId.setType("");
		digitalId.setDeviceProvider("1");
		digitalId.setDeviceProviderId("1");
		digitalId.setDateTime(DateUtils.getCurrentDateTimeString());
		dataDTO.setDigitalId(digitalId);
		RequestDTO request = new RequestDTO();
		BioIdentityInfoDTO bioInfo = new BioIdentityInfoDTO();
		bioInfo.setData(dataDTO);
		List<BioIdentityInfoDTO> biometrics = Collections.singletonList(bioInfo);
		request.setBiometrics(biometrics);
		AuthRequestDTO authRequest = new AuthRequestDTO();
		authRequest.setRequest(request);
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateDeviceDetails", authRequest, error);
		assertEquals(String.format(BIO_PATH, 0, "data/digitalId/type"), error.getFieldErrors().get(0).getArguments()[0]);
	}

	@Test
	public void testValidateDeviceDetailsMissingDeviceProvider() {
		DataDTO dataDTO = new DataDTO();
		dataDTO.setDeviceCode("1");
		dataDTO.setDeviceServiceVersion("1");
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("1");
		digitalId.setMake("1");
		digitalId.setModel("1");
		digitalId.setType("1");
		digitalId.setDeviceSubType("1");
		digitalId.setDeviceProvider("");
		digitalId.setDeviceProviderId("1");
		digitalId.setDateTime(DateUtils.getCurrentDateTimeString());
		dataDTO.setDigitalId(digitalId);
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		BioIdentityInfoDTO bioInfo = new BioIdentityInfoDTO();
		bioInfo.setData(dataDTO);
		List<BioIdentityInfoDTO> biometrics = Collections.singletonList(bioInfo);
		request.setBiometrics(biometrics);
		authRequestDTO.setRequest(request);
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateDeviceDetails", authRequestDTO, error);
		assertEquals(String.format(BIO_PATH, 0, "data/digitalId/deviceProvider"),
				error.getFieldErrors().get(0).getArguments()[0]);
	}

	@Test
	public void testValidateDeviceDetailsMissingDeviceProviderId() {
		DataDTO dataDTO = new DataDTO();
		dataDTO.setDeviceCode("1");
		dataDTO.setDeviceServiceVersion("1");
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("1");
		digitalId.setMake("1");
		digitalId.setModel("1");
		digitalId.setType("1");
		digitalId.setDeviceSubType("1");
		digitalId.setDeviceProvider("1");
		digitalId.setDeviceProviderId("");
		digitalId.setDateTime(DateUtils.getCurrentDateTimeString());
		dataDTO.setDigitalId(digitalId);
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		BioIdentityInfoDTO bioInfo = new BioIdentityInfoDTO();
		bioInfo.setData(dataDTO);
		List<BioIdentityInfoDTO> biometrics = Collections.singletonList(bioInfo);
		request.setBiometrics(biometrics);
		authRequestDTO.setRequest(request);
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateDeviceDetails", authRequestDTO, error);
		assertEquals(String.format(BIO_PATH, 0, "data/digitalId/deviceProviderId"),
				error.getFieldErrors().get(0).getArguments()[0]);
	}
	
	@Test
	public void testValidateDeviceDetailsMissingBioTimestamp() {
		DataDTO dataDTO = new DataDTO();
		dataDTO.setDeviceCode("1");
		dataDTO.setDeviceServiceVersion("1");
		dataDTO.setTransactionId("");
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("1");
		digitalId.setMake("1");
		digitalId.setModel("1");
		digitalId.setType("1");
		digitalId.setDeviceSubType("1");
		digitalId.setDeviceProvider("1");
		digitalId.setDeviceProviderId("1");
		digitalId.setDateTime("");
		dataDTO.setDigitalId(digitalId);
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setTransactionID("");
		RequestDTO request = new RequestDTO();
		BioIdentityInfoDTO bioInfo = new BioIdentityInfoDTO();
		bioInfo.setData(dataDTO);
		List<BioIdentityInfoDTO> biometrics = Collections.singletonList(bioInfo);
		request.setBiometrics(biometrics);
		authRequestDTO.setRequest(request);
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateBiometrics", biometrics, "", error);
		assertEquals(String.format(BIO_PATH, 0, "data/timestamp"), error.getFieldErrors().get(0).getArguments()[0]);
		
	}

	@Test
	public void testValidateDeviceDetailsMissingDateTime() {
		DataDTO dataDTO = new DataDTO();
		dataDTO.setDeviceCode("1");
		dataDTO.setDeviceServiceVersion("1");
		dataDTO.setTransactionId("");
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("1");
		digitalId.setMake("1");
		digitalId.setModel("1");
		digitalId.setType("1");
		digitalId.setDeviceSubType("1");
		digitalId.setDeviceProvider("1");
		digitalId.setDeviceProviderId("1");
		digitalId.setDateTime("");
		dataDTO.setDigitalId(digitalId);
		String timestamp = Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		dataDTO.setTimestamp(timestamp);
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		BioIdentityInfoDTO bioInfo = new BioIdentityInfoDTO();
		bioInfo.setData(dataDTO);
		List<BioIdentityInfoDTO> biometrics = Collections.singletonList(bioInfo);
		request.setBiometrics(biometrics);
		authRequestDTO.setTransactionID("");
		authRequestDTO.setRequest(request);
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateBiometrics", biometrics, "", error);
		assertEquals(String.format(BIO_PATH, 0, "data/digitalId/dateTime"), error.getFieldErrors().get(0).getArguments()[0]);
		
	}

	@Test
	public void testValidateDeviceDetailsInvalidDateTime() {
		DataDTO dataDTO = new DataDTO();
		dataDTO.setDeviceCode("1");
		dataDTO.setDeviceServiceVersion("1");
		dataDTO.setTransactionId("");
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("1");
		digitalId.setMake("1");
		digitalId.setModel("1");
		digitalId.setType("1");
		digitalId.setDeviceSubType("1");
		digitalId.setDeviceProvider("1");
		digitalId.setDeviceProviderId("1");
		digitalId.setDateTime("1");
		dataDTO.setDigitalId(digitalId);
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setTransactionID("");
		RequestDTO request = new RequestDTO();
		BioIdentityInfoDTO bioInfo = new BioIdentityInfoDTO();
		bioInfo.setData(dataDTO);
		List<BioIdentityInfoDTO> biometrics = Collections.singletonList(bioInfo);
		request.setBiometrics(biometrics);
		authRequestDTO.setRequest(request);
		String timestamp = Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString();
		dataDTO.setTimestamp(timestamp);
		
		ReflectionTestUtils.invokeMethod(AuthRequestValidator, "validateBiometrics", biometrics, "", error);
		assertEquals(String.format(BIO_PATH, 0, "data/digitalId/dateTime"), error.getFieldErrors().get(0).getArguments()[0]);
	}

	private Map<String, List<String>> fetchGenderType() {
		Map<String, List<String>> map = new HashMap<>();
		List<String> list = new ArrayList<>();
		list.add("M");
		map.put(EnvUtil.getMandatoryLanguages(), list);
		return map;
	}

	private Map<String, List<String>> fetchGenderTypeNull() {
		Map<String, List<String>> map = new HashMap<>();
		List<String> list = new ArrayList<>();
		list.add("Test");
		map.put(EnvUtil.getMandatoryLanguages(), list);
		return map;
	}
}