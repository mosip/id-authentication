package io.mosip.registration.processor.stages.osivalidator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.bioapi.exception.BiometricException;
import io.mosip.registration.processor.core.auth.dto.AuthResponseDTO;
import io.mosip.registration.processor.core.auth.dto.ErrorDTO;
import io.mosip.registration.processor.core.constant.JsonConstant;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.BioTypeException;
import io.mosip.registration.processor.core.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.core.idrepo.dto.IdResponseDTO;
import io.mosip.registration.processor.core.idrepo.dto.ResponseDTO;
import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.FieldValueArray;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.RIDResponseDto;
import io.mosip.registration.processor.core.packet.dto.RegOsiDto;
import io.mosip.registration.processor.core.packet.dto.RidDto;
import io.mosip.registration.processor.core.packet.dto.ServerError;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.packet.dto.masterdata.UserDetailsDto;
import io.mosip.registration.processor.core.packet.dto.masterdata.UserDetailsResponseDto;
import io.mosip.registration.processor.core.packet.dto.masterdata.UserResponseDto;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileSystemManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.packet.storage.utils.ABISHandlerUtil;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.stages.osivalidator.utils.AuthUtil;
import io.mosip.registration.processor.stages.osivalidator.utils.OSIUtils;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.TransactionDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.status.service.TransactionService;

/**
 * The Class OSIValidatorTest.
 *
 * @author M1022006
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Utilities.class, IOUtils.class, JsonUtil.class })
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class OSIValidatorTest {

	/** The input stream. */
	@Mock
	private InputStream inputStream;

	/** The registration status service. */
	@Mock
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The adapter. */
	@Mock
	FileSystemManager adapter;

	/** The rest client service. */
	@Mock
	RegistrationProcessorRestClientService<Object> restClientService;

	/** The transcation status service. */
	@Mock
	private TransactionService<TransactionDto> transcationStatusService;

	/** The auth response DTO. */
	@Mock
	AuthResponseDTO authResponseDTO = new AuthResponseDTO();

	@Mock
	private ABISHandlerUtil abisHandlerUtil;

	/** The env. */
	@Mock
	Environment env;

	@Mock
	RegistrationProcessorIdentity registrationProcessorIdentity = new RegistrationProcessorIdentity();

	@Mock
	private OSIUtils osiUtils;

	/** The data. */
	byte[] data = "1234567890".getBytes();

	/** The reg osi dto. */
	private RegOsiDto regOsiDto = new RegOsiDto();

	/** The registration status dto. */
	InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();

	/** The transaction dto. */
	TransactionDto transactionDto = new TransactionDto();

	/** The osi validator. */
	@InjectMocks
	OSIValidator osiValidator;

	@Mock
	private Utilities utility;

	@Mock
	private AuthUtil authUtil;

	/** The demographic dedupe dto list. */
	List<DemographicInfoDto> demographicDedupeDtoList = new ArrayList<>();

	/** The demographic info dto. */
	DemographicInfoDto demographicInfoDto = new DemographicInfoDto();

	/** The identity. */
	private Identity identity = new Identity();

	private JSONObject demoJson = new JSONObject();
	private UserResponseDto userResponseDto = new UserResponseDto();
	private RidDto ridDto = new RidDto();
	private ResponseDTO responseDTO1 = new ResponseDTO();
	private RIDResponseDto ridResponseDto1 = new RIDResponseDto();
	private IdResponseDTO idResponseDTO = new IdResponseDTO();

	/**
	 * Sets the up.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		ReflectionTestUtils.setField(osiValidator, "ageLimit", "5");
		ReflectionTestUtils.setField(osiValidator, "dobFormat", "yyyy/MM/dd");

		Mockito.when(utility.getGetRegProcessorDemographicIdentity()).thenReturn("identity");
		String value = "{\r\n" + "	\"identity\": {\r\n" + "		\"name\": {\r\n"
				+ "			\"value\": \"fullName\",\r\n" + "			\"isMandatory\" : true\r\n" + "		},\r\n"
				+ "		\"gender\": {\r\n" + "			\"value\": \"gender\",\r\n"
				+ "			\"isMandatory\" : true\r\n" + "		},\r\n" + "		\"dob\": {\r\n"
				+ "			\"value\": \"dateOfBirth\",\r\n" + "			\"isMandatory\" : true\r\n" + "		},\r\n"
				+ "		\"parentOrGuardianRID\": {\r\n" + "			\"value\" : \"parentOrGuardianRID\"\r\n"
				+ "		},\r\n" + "		\"parentOrGuardianUIN\": {\r\n"
				+ "			\"value\" : \"parentOrGuardianUIN\"\r\n" + "		},\r\n"
				+ "		\"parentOrGuardianName\": {\r\n" + "			\"value\" : \"parentOrGuardianName\"\r\n"
				+ "		},\r\n" + "		\"poa\": {\r\n" + "			\"value\" : \"proofOfAddress\"\r\n" + "		},\r\n"
				+ "		\"poi\": {\r\n" + "			\"value\" : \"proofOfIdentity\"\r\n" + "		},\r\n"
				+ "		\"por\": {\r\n" + "			\"value\" : \"proofOfRelationship\"\r\n" + "		},\r\n"
				+ "		\"pob\": {\r\n" + "			\"value\" : \"proofOfDateOfBirth\"\r\n" + "		},\r\n"
				+ "		\"individualBiometrics\": {\r\n" + "			\"value\" : \"individualBiometrics\"\r\n"
				+ "		},\r\n" + "		\"age\": {\r\n" + "			\"value\" : \"age\"\r\n" + "		},\r\n"
				+ "		\"addressLine1\": {\r\n" + "			\"value\" : \"addressLine1\"\r\n" + "		},\r\n"
				+ "		\"addressLine2\": {\r\n" + "			\"value\" : \"addressLine2\"\r\n" + "		},\r\n"
				+ "		\"addressLine3\": {\r\n" + "			\"value\" : \"addressLine3\"\r\n" + "		},\r\n"
				+ "		\"region\": {\r\n" + "			\"value\" : \"region\"\r\n" + "		},\r\n"
				+ "		\"province\": {\r\n" + "			\"value\" : \"province\"\r\n" + "		},\r\n"
				+ "		\"postalCode\": {\r\n" + "			\"value\" : \"postalCode\"\r\n" + "		},\r\n"
				+ "		\"phone\": {\r\n" + "			\"value\" : \"phone\"\r\n" + "		},\r\n"
				+ "		\"email\": {\r\n" + "			\"value\" : \"email\"\r\n" + "		},\r\n"
				+ "		\"localAdministrativeAuthority\": {\r\n"
				+ "			\"value\" : \"localAdministrativeAuthority\"\r\n" + "		},\r\n"
				+ "		\"idschemaversion\": {\r\n" + "			\"value\" : \"IDSchemaVersion\"\r\n" + "		},\r\n"
				+ "		\"cnienumber\": {\r\n" + "			\"value\" : \"CNIENumber\"\r\n" + "		},\r\n"
				+ "		\"city\": {\r\n" + "			\"value\" : \"city\",\r\n"
				+ "			\"isMandatory\" : true\r\n" + "		},\r\n" + "		\"parentOrGuardianBiometrics\": {\r\n"
				+ "			\"value\" : \"parentOrGuardianBiometrics\"\r\n" + "		}\r\n" + "	}\r\n" + "}  \r\n";
		PowerMockito.mockStatic(Utilities.class);
		PowerMockito.when(Utilities.class, "getJson", anyString(), anyString()).thenReturn(value);

		ObjectMapper mapIdentityJsonStringToObject = new ObjectMapper();
		RegistrationProcessorIdentity val = mapIdentityJsonStringToObject.readValue(value,
				RegistrationProcessorIdentity.class);

		Mockito.when(utility.getRegistrationProcessorIdentityJson()).thenReturn(val);

		osiValidator.registrationStatusDto = registrationStatusDto;
		regOsiDto.setOfficerId("O1234");
		regOsiDto.setOfficerHashedPin("officerHashedPin");
		regOsiDto.setSupervisorId("S1234");
		regOsiDto.setSupervisorHashedPin("supervisorHashedPin");
		regOsiDto.setIntroducerTyp("Parent");
		demographicDedupeDtoList.add(demographicInfoDto);

		Mockito.when(env.getProperty("registration.processor.fingerType")).thenReturn("LeftThumb");

		Mockito.when(env.getProperty("mosip.kernel.applicant.type.age.limit")).thenReturn("5");

		Mockito.when(adapter.getFile(anyString(), anyString())).thenReturn(inputStream);
		Mockito.when(adapter.checkFileExistence(anyString(), anyString())).thenReturn(true);

		PowerMockito.mockStatic(IOUtils.class);
		PowerMockito.when(IOUtils.class, "toByteArray", inputStream).thenReturn(data);

		io.mosip.registration.processor.core.auth.dto.ResponseDTO responseDTO = new io.mosip.registration.processor.core.auth.dto.ResponseDTO();
		responseDTO.setAuthStatus(true);
		authResponseDTO.setResponse(responseDTO);
		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(authResponseDTO);

		Mockito.when(authUtil.authByIdAuthentication(anyString(), any(), any())).thenReturn(authResponseDTO);

		registrationStatusDto.setRegistrationId("reg1234");
		registrationStatusDto.setApplicantType("Child");
		registrationStatusDto.setRegistrationType("New");

		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);

		FieldValue officerBiofileName = new FieldValue();
		officerBiofileName.setLabel(JsonConstant.OFFICERBIOMETRICFILENAME);
		officerBiofileName.setValue("officer_bio_CBEFF");

		FieldValue officerPassword = new FieldValue();
		officerPassword.setLabel(JsonConstant.OFFICERPWR);
		officerPassword.setValue("false");

		FieldValue officerOtp = new FieldValue();
		officerOtp.setLabel(JsonConstant.OFFICEROTPAUTHENTICATION);
		officerOtp.setValue("false");

		FieldValue supervisorPassword = new FieldValue();
		supervisorPassword.setLabel(JsonConstant.SUPERVISORPWR);
		supervisorPassword.setValue("true");

		FieldValue supervisorId = new FieldValue();
		supervisorId.setLabel(JsonConstant.SUPERVISORID);
		supervisorId.setValue("110016");

		FieldValue supervisorOtp = new FieldValue();
		supervisorOtp.setLabel(JsonConstant.SUPERVISOROTPAUTHENTICATION);
		supervisorOtp.setValue("false");

		FieldValue supervisorBiofileName = new FieldValue();
		supervisorBiofileName.setLabel(JsonConstant.SUPERVISORBIOMETRICFILENAME);
		supervisorBiofileName.setValue("supervisor_bio_CBEFF");

		FieldValue creationDate = new FieldValue();
		creationDate.setLabel("creationDate");
		creationDate.setValue("2019-04-30T12:42:03.541Z");

		identity.setOsiData((Arrays.asList(officerBiofileName, officerOtp, officerPassword, supervisorOtp,
				supervisorPassword, supervisorId, supervisorBiofileName)));
		identity.setMetaData((Arrays.asList(creationDate)));
		List<FieldValueArray> fieldValueArrayList = new ArrayList<FieldValueArray>();
		FieldValueArray introducerBiometric = new FieldValueArray();
		introducerBiometric.setLabel(PacketFiles.INTRODUCERBIOMETRICSEQUENCE.name());
		List<String> introducerBiometricValues = new ArrayList<String>();
		introducerBiometricValues.add("introducer_bio_CBEFF");
		introducerBiometric.setValue(introducerBiometricValues);
		fieldValueArrayList.add(introducerBiometric);
		identity.setHashSequence(fieldValueArrayList);
		regOsiDto.setSupervisorBiometricFileName("supervisor_bio_CBEFF");

		UserDetailsResponseDto userDetailsResponseDto = new UserDetailsResponseDto();
		UserDetailsDto userDetailsDto = new UserDetailsDto();
		userDetailsDto.setIsActive(true);
		userDetailsResponseDto.setUserResponseDto(Arrays.asList(userDetailsDto));
		userResponseDto.setResponse(userDetailsResponseDto);
		ridDto.setRid("reg4567");
		ridResponseDto1.setResponse(ridDto);
		String identityJson = "{\"UIN\":\"123456\"}";
		responseDTO1.setIdentity(identityJson);
		idResponseDTO.setResponse(responseDTO1);

		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.class, "objectMapperReadValue", anyString(), anyObject()).thenReturn(demoJson);
		PowerMockito.when(JsonUtil.class, "getJSONObject", anyObject(), anyString()).thenReturn(demoJson);

		Map<String, String> map = new LinkedHashMap<>();
		map.put("value", "biometreics");

		PowerMockito.when(JsonUtil.class, "getJSONValue", anyObject(), anyString()).thenReturn(null)
				.thenReturn(123456789).thenReturn(map);
		regOsiDto.setSupervisorHashedPwd("true");
		regOsiDto.setOfficerHashedPwd("true");
		Mockito.when(osiUtils.getIdentity(anyString())).thenReturn(identity);
		Mockito.when(restClientService.getApi(any(), any(), any(), any(), any())).thenReturn(userResponseDto)
				.thenReturn(userResponseDto).thenReturn(ridResponseDto1).thenReturn(idResponseDTO)
				.thenReturn(ridResponseDto1).thenReturn(idResponseDTO);
		Mockito.when(osiUtils.getOSIDetailsFromMetaInfo(anyString(), any())).thenReturn(regOsiDto);

	}

	/**
	 * Testis valid OSI success.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testisValidOSISuccess() throws Exception {
		Mockito.when(osiUtils.getMetaDataValue(anyString(), any()))
				.thenReturn(identity.getMetaData().get(0).getValue());
		Mockito.when(registrationStatusService.checkUinAvailabilityForRid(any())).thenReturn(true);
		registrationStatusDto.setRegistrationType("ACTIVATED");
		boolean isValid = osiValidator.isValidOSI("reg1234");
		assertTrue(isValid);
	}

	@Test
	public void testoperatorPasswordNull() throws Exception {
		regOsiDto.setOfficerBiometricFileName(null);
		regOsiDto.setSupervisorBiometricFileName(null);
		regOsiDto.setSupervisorHashedPwd(null);
		regOsiDto.setOfficerHashedPwd(null);
		Mockito.when(osiUtils.getOSIDetailsFromMetaInfo(anyString(), any())).thenReturn(regOsiDto);
		Mockito.when(osiUtils.getMetaDataValue(anyString(), any()))
				.thenReturn(identity.getMetaData().get(0).getValue());
		Mockito.when(registrationStatusService.checkUinAvailabilityForRid(any())).thenReturn(true);
		registrationStatusDto.setRegistrationType("ACTIVATED");
		boolean isValid = osiValidator.isValidOSI("reg1234");
		assertFalse(isValid);
	}

	@Test
	public void testusernotActive() throws Exception {
		UserDetailsResponseDto userDetailsResponseDto = new UserDetailsResponseDto();
		UserDetailsDto userDetailsDto = new UserDetailsDto();
		userDetailsDto.setIsActive(false);
		userDetailsResponseDto.setUserResponseDto(Arrays.asList(userDetailsDto));
		userResponseDto.setResponse(userDetailsResponseDto);
		Mockito.when(osiUtils.getMetaDataValue(anyString(), any()))
				.thenReturn(identity.getMetaData().get(0).getValue());
		Mockito.when(registrationStatusService.checkUinAvailabilityForRid(any())).thenReturn(true);
		Mockito.when(restClientService.getApi(any(), any(), any(), any(), any())).thenReturn(userResponseDto)
				.thenReturn(userResponseDto);
		registrationStatusDto.setRegistrationType("ACTIVATED");
		boolean isValid = osiValidator.isValidOSI("reg1234");
		assertFalse(isValid);
	}

	@Test
	public void testinvalidUserInput() throws Exception {
		ServerError error = new ServerError();
		error.setMessage("Invalid Date format");
		List<ServerError> errors = new ArrayList<>();
		errors.add(error);
		userResponseDto.setErrors(errors);
		Mockito.when(osiUtils.getMetaDataValue(anyString(), any()))
				.thenReturn(identity.getMetaData().get(0).getValue());
		Mockito.when(registrationStatusService.checkUinAvailabilityForRid(any())).thenReturn(true);
		Mockito.when(restClientService.getApi(any(), any(), any(), any(), any())).thenReturn(userResponseDto)
				.thenReturn(userResponseDto);
		registrationStatusDto.setRegistrationType("ACTIVATED");
		boolean isValid = osiValidator.isValidOSI("reg1234");
		assertFalse(isValid);
	}

	@Test
	public void testoperatorBiometricaAuthenticationFailure() throws Exception {
		io.mosip.registration.processor.core.auth.dto.ResponseDTO responseDTO = new io.mosip.registration.processor.core.auth.dto.ResponseDTO();
		responseDTO.setAuthStatus(false);
		authResponseDTO.setResponse(responseDTO);
		ErrorDTO errorDTO = new ErrorDTO();
		errorDTO.setErrorMessage("authentication failed");
		List<ErrorDTO> errors = new ArrayList<>();
		errors.add(errorDTO);
		authResponseDTO.setErrors(errors);
		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(authResponseDTO);
		Mockito.when(osiUtils.getMetaDataValue(anyString(), any()))
				.thenReturn(identity.getMetaData().get(0).getValue());
		Mockito.when(registrationStatusService.checkUinAvailabilityForRid(any())).thenReturn(true);
		registrationStatusDto.setRegistrationType("ACTIVATED");
		boolean isValid = osiValidator.isValidOSI("reg1234");
		assertFalse(isValid);
	}

	@Test(expected = ApisResourceAccessException.class)
	public void tesApisResourceAccessException() throws Exception {
		ApisResourceAccessException apisResourceAccessException = new ApisResourceAccessException("bad request");
		Mockito.when(restClientService.getApi(any(), any(), any(), any(), any())).thenReturn(userResponseDto)
				.thenThrow(apisResourceAccessException);
		Mockito.when(osiUtils.getMetaDataValue(anyString(), any()))
				.thenReturn(identity.getMetaData().get(0).getValue());
		Mockito.when(registrationStatusService.checkUinAvailabilityForRid(any())).thenReturn(true);
		registrationStatusDto.setRegistrationType("ACTIVATED");
		osiValidator.isValidOSI("reg1234");

	}

	@Test(expected = ApisResourceAccessException.class)
	public void testHttpClientErrorException() throws Exception {
		HttpClientErrorException httpClientErrorException = new HttpClientErrorException(HttpStatus.BAD_REQUEST,
				"error");
		ApisResourceAccessException apisResourceAccessException = new ApisResourceAccessException("bad request",
				httpClientErrorException);
		Mockito.when(restClientService.getApi(any(), any(), any(), any(), any())).thenReturn(userResponseDto)
				.thenThrow(apisResourceAccessException);
		Mockito.when(osiUtils.getMetaDataValue(anyString(), any()))
				.thenReturn(identity.getMetaData().get(0).getValue());
		Mockito.when(registrationStatusService.checkUinAvailabilityForRid(any())).thenReturn(true);
		registrationStatusDto.setRegistrationType("ACTIVATED");
		osiValidator.isValidOSI("reg1234");

	}

	@Test(expected = ApisResourceAccessException.class)
	public void testHttpServerErrorException() throws Exception {
		HttpServerErrorException httpServerErrorException = new HttpServerErrorException(HttpStatus.BAD_REQUEST,
				"error");
		ApisResourceAccessException apisResourceAccessException = new ApisResourceAccessException("bad request",
				httpServerErrorException);
		Mockito.when(restClientService.getApi(any(), any(), any(), any(), any())).thenReturn(userResponseDto)
				.thenThrow(apisResourceAccessException);
		Mockito.when(osiUtils.getMetaDataValue(anyString(), any()))
				.thenReturn(identity.getMetaData().get(0).getValue());
		Mockito.when(registrationStatusService.checkUinAvailabilityForRid(any())).thenReturn(true);
		registrationStatusDto.setRegistrationType("ACTIVATED");
		osiValidator.isValidOSI("reg1234");

	}

	/**
	 * Test officer details null.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testOfficerDetailsNull() throws Exception {
		regOsiDto.setOfficerId(null);
		regOsiDto.setSupervisorId(null);
		Mockito.when(osiUtils.getOSIDetailsFromMetaInfo(anyString(), any())).thenReturn(regOsiDto);
		boolean isValid = osiValidator.isValidOSI("reg1234");
		assertFalse(isValid);
	}

	/**
	 * Test introducer details null.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testIntroducerDetailsNull() throws Exception {
		PowerMockito.when(JsonUtil.class, "getJSONValue", anyObject(), anyString()).thenReturn("2015/01/01")
				.thenReturn(null).thenReturn(null);
		boolean isValid = osiValidator.isValidOSI("reg1234");
		assertFalse(isValid);
	}

	/**
	 * Test invalid iris.
	 * 
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws BioTypeException
	 * @throws BiometricException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws NumberFormatException
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testIntroducerRIDFailedOnHold() throws ApisResourceAccessException, IOException, Exception {
		Mockito.when(osiUtils.getMetaDataValue(anyString(), any())).thenReturn("2015/01/01");
		registrationStatusDto.setStatusCode("FAILED");
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		boolean isValid = osiValidator.isValidOSI("reg1234");
		assertFalse(isValid);
	}

	@Test
	public void testIntroducerRIDProcessingOnHold() throws NumberFormatException, ApisResourceAccessException,
			InvalidKeySpecException, NoSuchAlgorithmException, BiometricException, BioTypeException, IOException,
			ParserConfigurationException, SAXException, PacketDecryptionFailureException, io.mosip.kernel.core.exception.IOException {
		Mockito.when(osiUtils.getMetaDataValue(anyString(), any())).thenReturn("2015/01/01");
		InternalRegistrationStatusDto introducerRegistrationStatusDto = new InternalRegistrationStatusDto();

		introducerRegistrationStatusDto.setStatusCode((RegistrationStatusCode.PROCESSING.toString()));
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString()))
				.thenReturn(introducerRegistrationStatusDto);
		boolean isValid = osiValidator.isValidOSI("reg1234");
		assertFalse(isValid);
	}

	@Test
	public void testIntroducerNotInRegProc() throws ApisResourceAccessException, IOException, Exception {
		Mockito.when(osiUtils.getMetaDataValue(anyString(), any())).thenReturn("2015/01/01");
		registrationStatusDto = null;
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		boolean isValid = osiValidator.isValidOSI("reg1234");
		assertFalse(isValid);
	}

	@Test
	public void testIntroducerUINAndRIDNull() throws Exception {
		Mockito.when(osiUtils.getMetaDataValue(anyString(), any())).thenReturn("2015/01/01");
		PowerMockito.when(JsonUtil.class, "getJSONValue", anyObject(), anyString()).thenReturn(null).thenReturn(null)
				.thenReturn(null);

		boolean isValid = osiValidator.isValidOSI("reg1234");

		assertFalse(isValid);
	}

	@Test
	public void testIntroducerBioFileNull() throws Exception {
		Mockito.when(osiUtils.getMetaDataValue(anyString(), any())).thenReturn("2015/01/01");
		PowerMockito.when(JsonUtil.class, "getJSONValue", anyObject(), anyString()).thenReturn(123456);
		boolean isValid = osiValidator.isValidOSI("reg1234");
		assertFalse(isValid);
	}

	@Test
	public void testIntroducerBioFileNotNull() throws Exception {
		Mockito.when(osiUtils.getMetaDataValue(anyString(), any())).thenReturn("2015/01/01");
		Map<String, String> map = new LinkedHashMap<>();
		map.put("value", "biometreics");
		PowerMockito.when(JsonUtil.class, "getJSONValue", anyObject(), anyString()).thenReturn(123456)
				.thenReturn(123456).thenReturn(map).thenReturn(map);
		demoJson.put("value", "biometreics");
		PowerMockito.when(JsonUtil.class, "getJSONObject", anyObject(), anyString()).thenReturn(demoJson);

		authResponseDTO.setErrors(null);
		io.mosip.registration.processor.core.auth.dto.ResponseDTO responseDTO = new io.mosip.registration.processor.core.auth.dto.ResponseDTO();
		responseDTO.setAuthStatus(true);
		Mockito.when(authUtil.authByIdAuthentication(anyString(), any(), any())).thenReturn(authResponseDTO);

		boolean isValid = osiValidator.isValidOSI("reg1234");

		assertTrue(isValid);
	}

	@Test
	public void testIntroducerErrorTrue() throws Exception {
		Mockito.when(osiUtils.getMetaDataValue(anyString(), any())).thenReturn("2015/01/01");
		Map<String, String> map = new LinkedHashMap<>();
		map.put("value", "biometreics");
		PowerMockito.when(JsonUtil.class, "getJSONValue", anyObject(), anyString()).thenReturn(123456)
				.thenReturn(123456).thenReturn(map).thenReturn(map);
		demoJson.put("value", "biometreics");
		PowerMockito.when(JsonUtil.class, "getJSONObject", anyObject(), anyString()).thenReturn(demoJson);
		ErrorDTO errordto = new ErrorDTO();
		errordto.setErrorCode("true");
		List errorDtoList = new ArrayList<>();
		errorDtoList.add(errordto);
		authResponseDTO.setErrors(errorDtoList);
		io.mosip.registration.processor.core.auth.dto.ResponseDTO responseDTO = new io.mosip.registration.processor.core.auth.dto.ResponseDTO();
		responseDTO.setAuthStatus(true);
		Mockito.when(authUtil.authByIdAuthentication(anyString(), any(), any())).thenReturn(authResponseDTO);

		boolean isValid = osiValidator.isValidOSI("reg1234");

		assertFalse(isValid);
	}

	@Test
	public void testIntroducerAuthFalse() throws Exception {
		Mockito.when(osiUtils.getMetaDataValue(anyString(), any())).thenReturn("2015/01/01");
		Map<String, String> map = new LinkedHashMap<>();
		map.put("value", "biometreics");
		PowerMockito.when(JsonUtil.class, "getJSONValue", anyObject(), anyString()).thenReturn(123456)
				.thenReturn(123456).thenReturn(map).thenReturn(map);
		demoJson.put("value", "biometreics");
		PowerMockito.when(JsonUtil.class, "getJSONObject", anyObject(), anyString()).thenReturn(demoJson);

		AuthResponseDTO authResponseDTO1 = new AuthResponseDTO();
		authResponseDTO1.setErrors(null);
		io.mosip.registration.processor.core.auth.dto.ResponseDTO responseDTO = new io.mosip.registration.processor.core.auth.dto.ResponseDTO();
		responseDTO.setAuthStatus(false);
		authResponseDTO1.setResponse(responseDTO);
		Mockito.when(authUtil.authByIdAuthentication(anyString(), any(), any())).thenReturn(authResponseDTO)
				.thenReturn(authResponseDTO1);

		boolean isValid = osiValidator.isValidOSI("reg1234");

		assertFalse(isValid);
	}

	@Test
	@Ignore
	public void testIntroducerUINNull() throws ApisResourceAccessException, IOException, Exception {
		Mockito.when(osiUtils.getMetaDataValue(anyString(), any())).thenReturn("2015/01/01");

		InternalRegistrationStatusDto introducerRegistrationStatusDto = new InternalRegistrationStatusDto();

		introducerRegistrationStatusDto.setStatusCode((RegistrationStatusCode.PROCESSED.toString()));
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString()))
				.thenReturn(introducerRegistrationStatusDto);

		PowerMockito.when(JsonUtil.class, "getJSONValue", anyObject(), anyString()).thenReturn(null).thenReturn(12345);// .thenReturn(map);

		// Mockito.when(abisHandlerUtil.getUinFromIDRepo(any())).thenReturn(null);
		boolean isValid = osiValidator.isValidOSI("reg1234");
		assertFalse(isValid);
	}

}
