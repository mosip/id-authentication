/**
 * 
 */
package io.mosip.registration.processor.stages.osivalidator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.junit.Before;
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
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.registration.processor.core.auth.dto.AuthResponseDTO;
import io.mosip.registration.processor.core.constant.JsonConstant;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.FieldValueArray;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.core.packet.dto.RegOsiDto;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.stages.osivalidator.utils.OSIUtils;
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
@PrepareForTest({Utilities.class,  IOUtils.class, JsonUtil.class })
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class OSIValidatorTest {

	/** The input stream. */
	@Mock
	private InputStream inputStream;

	/** The packet info manager. */
	@Mock
	PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The registration status service. */
	@Mock
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The adapter. */
	@Mock
	FileSystemAdapter adapter;

	/** The rest client service. */
	@Mock
	RegistrationProcessorRestClientService<Object> restClientService;

	/** The transcation status service. */
	@Mock
	private TransactionService<TransactionDto> transcationStatusService;

	/** The auth response DTO. */
	@Mock
	AuthResponseDTO authResponseDTO = new AuthResponseDTO();

	/** The env. */
	@Mock
	Environment env;
	
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

	/** The demographic dedupe dto list. */
	List<DemographicInfoDto> demographicDedupeDtoList = new ArrayList<>();

	/** The demographic info dto. */
	DemographicInfoDto demographicInfoDto = new DemographicInfoDto();
	/** The packet meta info. */
	private PacketMetaInfo packetMetaInfo;

	/** The identity. */
	Identity identity = new Identity();

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
				+ "			\"value\": \"fullName\",\r\n" + "			\"weight\": 20\r\n" + "		},\r\n"
				+ "		\"gender\": {\r\n" + "			\"value\": \"gender\",\r\n" + "			\"weight\": 20\r\n"
				+ "		},\r\n" + "		\"dob\": {\r\n" + "			\"value\": \"dateOfBirth\",\r\n"
				+ "			\"weight\": 20\r\n" + "		},\r\n" + "		\"pheoniticName\": {\r\n"
				+ "			\"weight\": 20\r\n" + "		},\r\n" + "		\"poa\": {\r\n"
				+ "			\"value\" : \"proofOfAddress\"\r\n" + "		},\r\n" + "		\"poi\": {\r\n"
				+ "			\"value\" : \"proofOfIdentity\"\r\n" + "		},\r\n" + "		\"por\": {\r\n"
				+ "			\"value\" : \"proofOfRelationship\"\r\n" + "		},\r\n" + "		\"pob\": {\r\n"
				+ "			\"value\" : \"proofOfDateOfBirth\"\r\n" + "		},\r\n"
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
				+ "		\"city\": {\r\n" + "			\"value\" : \"city\"\r\n" + "		}\r\n" + "	}\r\n" + "} ";

		PowerMockito.mockStatic(Utilities.class);
		PowerMockito.when(Utilities.class, "getJson", anyString(), anyString()).thenReturn(value);
		
		
		demographicInfoDto.setUin("1234");
		osiValidator.registrationStatusDto = registrationStatusDto;
		regOsiDto.setOfficerId("O1234");
		regOsiDto.setOfficerFingerpImageName("fingerprint");
		regOsiDto.setOfficerfingerType("RIGHTLITTLE");
		regOsiDto.setOfficerIrisImageName(null);
		regOsiDto.setOfficerIrisType("LEFTEYE");
		regOsiDto.setOfficerPhotoName(null);
		regOsiDto.setOfficerHashedPin("officerHashedPin");
		regOsiDto.setSupervisorId("S1234");
		regOsiDto.setSupervisorFingerpImageName("supervisorFingerpImageName");
		regOsiDto.setSupervisorFingerType("LEFTINDEX");
		regOsiDto.setSupervisorIrisImageName("supervisorIrisImageName");
		regOsiDto.setSupervisorIrisType("LEFTEYE");
		regOsiDto.setSupervisorPhotoName("supervisorPhotoName");
		regOsiDto.setSupervisorHashedPin("supervisorHashedPin");
		regOsiDto.setIntroducerUin(null);
		regOsiDto.setIntroducerRegId("reg1234");
		regOsiDto.setIntroducerTyp("Parent");
		regOsiDto.setIntroducerFingerpImageName("introducerFingerpImageName");
		regOsiDto.setIntroducerFingerpType("RIGHTRING");
		regOsiDto.setIntroducerIrisImageName("IntroducerIrisImageName");
		regOsiDto.setIntroducerPhotoName("IntroducerPhotoName");
		regOsiDto.setIntroducerIrisType("RIGHTEYE");
		registrationStatusDto.setApplicantType("Child");
		demographicDedupeDtoList.add(demographicInfoDto);

		Mockito.when(env.getProperty("registration.processor.fingerType")).thenReturn("LeftThumb");
		
		Mockito.when(env.getProperty("mosip.kernel.applicant.type.age.limit")).thenReturn("5");

		Mockito.when(adapter.getFile(anyString(), anyString())).thenReturn(inputStream);
		Mockito.when(adapter.checkFileExistence(anyString(), anyString())).thenReturn(true);

		PowerMockito.mockStatic(IOUtils.class);
		PowerMockito.when(IOUtils.class, "toByteArray", inputStream).thenReturn(data);

		authResponseDTO.setStatus("y");
		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(authResponseDTO);

		registrationStatusDto.setRegistrationId("reg1234");
		registrationStatusDto.setApplicantType("Child");
		registrationStatusDto.setRegistrationType("New");

		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		packetMetaInfo = new PacketMetaInfo();

		FieldValue officerBiofileName = new FieldValue();
		officerBiofileName.setLabel(JsonConstant.OFFICERBIOMETRICFILENAME);
		officerBiofileName.setValue("officer_bio_CBEFF");

		FieldValue officerPassword= new FieldValue();
		officerPassword.setLabel(JsonConstant.OFFICERPWR);
		officerPassword.setValue("false");
		
		FieldValue officerOtp= new FieldValue();
		officerOtp.setLabel(JsonConstant.OFFICEROTPAUTHENTICATION);
		officerOtp.setValue("false");
		
		FieldValue supervisorPassword= new FieldValue();
		supervisorPassword.setLabel(JsonConstant.SUPERVISORPWR);
		supervisorPassword.setValue("true");
		
		FieldValue supervisorId= new FieldValue();
		supervisorId.setLabel(JsonConstant.SUPERVISORID);
		supervisorId.setValue("false");
		
		FieldValue supervisorOtp= new FieldValue();
		supervisorOtp.setLabel(JsonConstant.SUPERVISOROTPAUTHENTICATION);
		supervisorOtp.setValue("false");
		
		FieldValue supervisorBiofileName = new FieldValue();
		supervisorBiofileName.setLabel(JsonConstant.SUPERVISORBIOMETRICFILENAME);
		officerBiofileName.setValue("supervisor_bio_CBEFF");

		identity.setOsiData((Arrays.asList(officerBiofileName, officerBiofileName,officerOtp,officerPassword,supervisorOtp,supervisorPassword,supervisorId)));
		List<FieldValueArray> fieldValueArrayList = new ArrayList<FieldValueArray>();
		FieldValueArray introducerBiometric = new FieldValueArray();
		introducerBiometric.setLabel(PacketFiles.INTRODUCERBIOMETRICSEQUENCE.name());
		List<String> introducerBiometricValues = new ArrayList<String>();
		introducerBiometricValues.add("introducer_bio_CBEFF");
		introducerBiometric.setValue(introducerBiometricValues);
		fieldValueArrayList.add(introducerBiometric);
		identity.setHashSequence(fieldValueArrayList);
		packetMetaInfo.setIdentity(identity);
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.class, "inputStreamtoJavaObject", inputStream, PacketMetaInfo.class)
				.thenReturn(packetMetaInfo);

	}

	/**
	 * Testis valid OSI success.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testisValidOSISuccess() throws Exception {
		regOsiDto.setOfficerFingerpImageName(null);
		regOsiDto.setOfficerHashedPwd("true");
		regOsiDto.setSupervisorHashedPwd("true");
		regOsiDto.setIntroducerUin("U1234");
		regOsiDto.setIntroducerFingerpImageName(null);
		JSONObject demoJson = new JSONObject();
		demoJson.put("age", "10");
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.class, "objectMapperReadValue", anyString(), anyObject()).thenReturn(demoJson);
		PowerMockito.when(JsonUtil.class, "getJSONObject", anyObject(), anyString()).thenReturn(demoJson);
		PowerMockito.when(JsonUtil.class, "getJSONValue", anyObject(), anyString()).thenReturn("2015/01/01").thenReturn(10);

		Mockito.when(osiUtils.getIdentity(anyString())).thenReturn(identity);
		Mockito.when(osiUtils.getOSIDetailsFromMetaInfo(anyString(),any())).thenReturn(regOsiDto);
		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsiDto);
		Mockito.when(packetInfoManager.findDemoById(anyString())).thenReturn(demographicDedupeDtoList);
		Mockito.when(transcationStatusService.getTransactionByRegIdAndStatusCode(anyString(), anyString()))
				.thenReturn(transactionDto);

		boolean isValid = osiValidator.isValidOSI("reg1234");

		assertTrue(isValid);

	}

	/**
	 * Test officer details null.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testOfficerDetailsNull() throws Exception {
		regOsiDto.setOfficerFingerpImageName(null);
		regOsiDto.setOfficerIrisImageName(null);
		regOsiDto.setOfficerPhotoName(null);
		regOsiDto.setOfficerHashedPin(null);
		Mockito.when(osiUtils.getIdentity(anyString())).thenReturn(identity);
		Mockito.when(osiUtils.getOSIDetailsFromMetaInfo(anyString(),any())).thenReturn(regOsiDto);
		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsiDto);

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
		Mockito.when(transcationStatusService.getTransactionByRegIdAndStatusCode(anyString(), anyString()))
				.thenReturn(transactionDto);
		
		regOsiDto.setOfficerfingerType("LEFTMIDDLE");
		regOsiDto.setSupervisorFingerType("RIGHTINDEX");
		regOsiDto.setIntroducerFingerpImageName(null);
		regOsiDto.setIntroducerIrisImageName(null);
		regOsiDto.setIntroducerPhotoName(null);
		Mockito.when(osiUtils.getIdentity(anyString())).thenReturn(identity);
		Mockito.when(osiUtils.getOSIDetailsFromMetaInfo(anyString(),any())).thenReturn(regOsiDto);
		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsiDto);
		Mockito.when(packetInfoManager.findDemoById(anyString())).thenReturn(demographicDedupeDtoList);

		boolean isValid = osiValidator.isValidOSI("reg1234");

		assertFalse(isValid);
	}

	/**
	 * Testis valid OSI failure.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testisValidOSIFailure() throws Exception {
		authResponseDTO.setStatus("N");
		regOsiDto.setOfficerfingerType("LEFTLITTLE");
		Mockito.when(osiUtils.getIdentity(anyString())).thenReturn(identity);
		Mockito.when(osiUtils.getOSIDetailsFromMetaInfo(anyString(),any())).thenReturn(regOsiDto);
		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsiDto);
		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(authResponseDTO);

		boolean isValid = osiValidator.isValidOSI("reg1234");

		assertFalse(isValid);
	}

	/**
	 * Testvalidate fingerprint failure.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testvalidateFingerprintFailure() throws Exception {
		Mockito.when(osiUtils.getIdentity(anyString())).thenReturn(identity);
		Mockito.when(osiUtils.getOSIDetailsFromMetaInfo(anyString(),any())).thenReturn(regOsiDto);
		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsiDto);
		Mockito.when(packetInfoManager.findDemoById(anyString())).thenReturn(demographicDedupeDtoList);
		Mockito.when(transcationStatusService.getTransactionByRegIdAndStatusCode(anyString(), anyString()))
				.thenReturn(transactionDto);
		Mockito.when(adapter.checkFileExistence(anyString(), anyString())).thenReturn(false);
		boolean isValid = osiValidator.isValidOSI("reg1234");

		assertFalse(isValid);
	}

	/**
	 * Testvalidate face failure.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testvalidateFaceFailure() throws Exception {
		regOsiDto.setOfficerFingerpImageName(null);
		Mockito.when(osiUtils.getIdentity(anyString())).thenReturn(identity);
		Mockito.when(osiUtils.getOSIDetailsFromMetaInfo(anyString(),any())).thenReturn(regOsiDto);
		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsiDto);
		Mockito.when(packetInfoManager.findDemoById(anyString())).thenReturn(demographicDedupeDtoList);
		Mockito.when(transcationStatusService.getTransactionByRegIdAndStatusCode(anyString(), anyString()))
				.thenReturn(transactionDto);
		Mockito.when(adapter.checkFileExistence(anyString(), anyString())).thenReturn(false);
		boolean isValid = osiValidator.isValidOSI("reg1234");

		assertFalse(isValid);
	}

	/**
	 * Test supervisor details null.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testSupervisorDetailsNull() throws Exception {
		regOsiDto.setOfficerfingerType("RIGHTTHUMB");

		regOsiDto.setSupervisorFingerpImageName(null);
		regOsiDto.setSupervisorIrisImageName(null);
		regOsiDto.setSupervisorPhotoName(null);
		regOsiDto.setSupervisorHashedPin(null);
		
		Mockito.when(osiUtils.getIdentity(anyString())).thenReturn(identity);
		Mockito.when(osiUtils.getOSIDetailsFromMetaInfo(anyString(),any())).thenReturn(regOsiDto);

		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsiDto);

		boolean isValid = osiValidator.isValidOSI("reg1234");

		assertFalse(isValid);
	}

	/**
	 * Test invalid iris.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testInvalidIris() throws Exception {
		authResponseDTO.setStatus("N");
		regOsiDto.setOfficerId(null);
		regOsiDto.setSupervisorFingerpImageName(null);
		Mockito.when(osiUtils.getIdentity(anyString())).thenReturn(identity);
		Mockito.when(osiUtils.getOSIDetailsFromMetaInfo(anyString(),any())).thenReturn(regOsiDto);
		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsiDto);
		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(authResponseDTO);

		boolean isValid = osiValidator.isValidOSI("reg1234");

		assertFalse(isValid);
	}

	/**
	 * Test introducer UIN.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testIntroducerUIN() throws Exception {
		regOsiDto.setIntroducerRegId(null);
		regOsiDto.setIntroducerUin(null);

		Mockito.when(osiUtils.getIdentity(anyString())).thenReturn(identity);
		Mockito.when(osiUtils.getOSIDetailsFromMetaInfo(anyString(),any())).thenReturn(regOsiDto);
		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsiDto);
		Mockito.when(packetInfoManager.findDemoById(anyString())).thenReturn(demographicDedupeDtoList);
		boolean isValid = osiValidator.isValidOSI("reg1234");

		assertFalse(isValid);
	}

	/**
	 * Tes all introducer finger print 1.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void tesAllIntroducerFingerPrint1() throws ApisResourceAccessException, IOException {
		regOsiDto.setIntroducerFingerpType("LEFTINDEX");
		regOsiDto.setOfficerfingerType("LEFTRING");
		regOsiDto.setSupervisorFingerType("RIGHTINDEX");
		demographicInfoDto.setUin(null);
		Mockito.when(osiUtils.getIdentity(anyString())).thenReturn(identity);
		Mockito.when(osiUtils.getOSIDetailsFromMetaInfo(anyString(),any())).thenReturn(regOsiDto);
		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsiDto);
		Mockito.when(packetInfoManager.findDemoById(anyString())).thenReturn(demographicDedupeDtoList);
		Mockito.when(transcationStatusService.getTransactionByRegIdAndStatusCode(anyString(), anyString()))
				.thenReturn(transactionDto);

		boolean isValid = osiValidator.isValidOSI("reg1234");

		assertFalse(isValid);
	}

	/**
	 * Test invalid iris.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void tesAllIntroducerFingerPrint() throws ApisResourceAccessException, IOException, Exception{
		regOsiDto.setIntroducerFingerpType("LEFTTHUMB");
		regOsiDto.setOfficerfingerType("RIGHTMIDDLE");
		regOsiDto.setSupervisorFingerType("LEFTRING");
		regOsiDto.setSupervisorHashedPwd("true");
		regOsiDto.setOfficerFingerpImageName(null);
		regOsiDto.setOfficerHashedPwd("true");
		regOsiDto.setIntroducerUin("U1234");
		//regOsiDto.setIntroducerRegId("reg1234");
		regOsiDto.setIntroducerFingerpImageName(null);
		
		JSONObject demoJson = new JSONObject();
		demoJson.put("age", "10");
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.class, "objectMapperReadValue", anyString(), anyObject()).thenReturn(demoJson);
		PowerMockito.when(JsonUtil.class, "getJSONObject", anyObject(), anyString()).thenReturn(demoJson);
		PowerMockito.when(JsonUtil.class, "getJSONValue", anyObject(), anyString()).thenReturn("2015/01/01").thenReturn(10);
		
		Mockito.when(osiUtils.getIdentity(anyString())).thenReturn(identity);
		Mockito.when(osiUtils.getOSIDetailsFromMetaInfo(anyString(),any())).thenReturn(regOsiDto);
		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsiDto);
		Mockito.when(packetInfoManager.findDemoById(anyString())).thenReturn(demographicDedupeDtoList);
		Mockito.when(transcationStatusService.getTransactionByRegIdAndStatusCode(anyString(), anyString()))
				.thenReturn(transactionDto);

		boolean isValid = osiValidator.isValidOSI("reg1234");

		assertTrue(isValid);
	}
	/**
	 * Test invalid iris.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void tesAllIntroducerRIDFingerPrint() throws ApisResourceAccessException, IOException, Exception{
		regOsiDto.setIntroducerFingerpType("LEFTTHUMB");
		regOsiDto.setOfficerfingerType("RIGHTMIDDLE");
		regOsiDto.setSupervisorFingerType("LEFTRING");
		regOsiDto.setSupervisorHashedPwd("true");
		regOsiDto.setOfficerFingerpImageName(null);
		regOsiDto.setOfficerHashedPwd("true");
		
	    regOsiDto.setIntroducerRegId("reg1234");
		regOsiDto.setIntroducerFingerpImageName(null);
		
		JSONObject demoJson = new JSONObject();
		demoJson.put("age", "10");
		List<String> ridList=new ArrayList<>();
		ridList.add("reg1234");
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.class, "objectMapperReadValue", anyString(), anyObject()).thenReturn(demoJson);
		PowerMockito.when(JsonUtil.class, "getJSONObject", anyObject(), anyString()).thenReturn(demoJson);
		PowerMockito.when(JsonUtil.class, "getJSONValue", anyObject(), anyString()).thenReturn("2015/01/01").thenReturn(10);
		
		Mockito.when(osiUtils.getIdentity(anyString())).thenReturn(identity);
		Mockito.when(osiUtils.getOSIDetailsFromMetaInfo(anyString(),any())).thenReturn(regOsiDto);
		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsiDto);
		Mockito.when(packetInfoManager.findDemoById(anyString())).thenReturn(demographicDedupeDtoList);
		Mockito.when(packetInfoManager.getUINByRid(anyString())).thenReturn(ridList);
		Mockito.when(transcationStatusService.getTransactionByRegIdAndStatusCode(anyString(), anyString()))
				.thenReturn(transactionDto);

		boolean isValid = osiValidator.isValidOSI("reg1234");

		assertTrue(isValid);
	}
	/**
	 * Test invalid iris.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void tesAllIntroducerRIDOnHold() throws ApisResourceAccessException, IOException, Exception{
		regOsiDto.setIntroducerFingerpType("LEFTTHUMB");
		regOsiDto.setOfficerfingerType("RIGHTMIDDLE");
		regOsiDto.setSupervisorFingerType("LEFTRING");
		regOsiDto.setSupervisorHashedPwd("true");
		regOsiDto.setOfficerFingerpImageName(null);
		regOsiDto.setOfficerHashedPwd("true");
		
	    regOsiDto.setIntroducerRegId("reg1234");
		regOsiDto.setIntroducerFingerpImageName(null);
		
		JSONObject demoJson = new JSONObject();
		demoJson.put("age", "10");
		List<String> ridList=new ArrayList<>();
		
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.class, "objectMapperReadValue", anyString(), anyObject()).thenReturn(demoJson);
		PowerMockito.when(JsonUtil.class, "getJSONObject", anyObject(), anyString()).thenReturn(demoJson);
		PowerMockito.when(JsonUtil.class, "getJSONValue", anyObject(), anyString()).thenReturn("2015/01/01").thenReturn(10);
		
		Mockito.when(osiUtils.getIdentity(anyString())).thenReturn(identity);
		Mockito.when(osiUtils.getOSIDetailsFromMetaInfo(anyString(),any())).thenReturn(regOsiDto);
		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsiDto);
		Mockito.when(packetInfoManager.findDemoById(anyString())).thenReturn(demographicDedupeDtoList);
		Mockito.when(packetInfoManager.getUINByRid(anyString())).thenReturn(ridList);
		Mockito.when(transcationStatusService.getTransactionByRegIdAndStatusCode(anyString(), anyString()))
				.thenReturn(transactionDto);

		boolean isValid = osiValidator.isValidOSI("reg1234");

		assertFalse(isValid);
	}

}
