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

		String value="{\"identity\":{\"name\":{\"value\":\"fullName\",\"weight\":20},\"gender\":{\"value\":\"gender\",\"weight\":20},\"dob\":{\"value\":\"dateOfBirth\",\"weight\":20},\"pheoniticName\":{\"weight\":20},\"poa\":{\"value\":\"proofOfAddress\"},\"poi\":{\"value\":\"proofOfIdentity\"},\"por\":{\"value\":\"proofOfRelationship\"},\"pob\":{\"value\":\"proofOfDateOfBirth\"},\"individualBiometrics\":{\"value\":\"individualBiometrics\"},\"age\":{\"value\":\"age\"},\"addressLine1\":{\"value\":\"addressLine1\"},\"addressLine2\":{\"value\":\"addressLine2\"},\"addressLine3\":{\"value\":\"addressLine3\"},\"region\":{\"value\":\"region\"},\"province\":{\"value\":\"province\"},\"postalCode\":{\"value\":\"postalCode\"},\"phone\":{\"value\":\"phone\"},\"email\":{\"value\":\"email\"},\"localAdministrativeAuthority\":{\"value\":\"localAdministrativeAuthority\"},\"idschemaversion\":{\"value\":\"IDSchemaVersion\"},\"cnienumber\":{\"value\":\"CNIENumber\"},\"city\":{\"value\":\"city\"}}}";
		PowerMockito.mockStatic(Utilities.class);
		PowerMockito.when(Utilities.class, "getJson", anyString(), anyString()).thenReturn(value);
		
		
		demographicInfoDto.setUin("1234");
		osiValidator.registrationStatusDto = registrationStatusDto;
		regOsiDto.setOfficerId("O1234");
		regOsiDto.setOfficerHashedPin("officerHashedPin");
		regOsiDto.setSupervisorId("S1234");
		regOsiDto.setSupervisorHashedPin("supervisorHashedPin");
		regOsiDto.setIntroducerTyp("Parent");
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
		regOsiDto.setOfficerHashedPwd("true");
		regOsiDto.setSupervisorHashedPwd("true");
		JSONObject demoJson = new JSONObject();
		demoJson.put("age", "10");
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.class, "objectMapperReadValue", anyString(), anyObject()).thenReturn(demoJson);
		PowerMockito.when(JsonUtil.class, "getJSONObject", anyObject(), anyString()).thenReturn(demoJson);
		PowerMockito.when(JsonUtil.class, "getJSONValue", anyObject(), anyString()).thenReturn("1990/01/01").thenReturn(10);

		Mockito.when(osiUtils.getIdentity(anyString())).thenReturn(identity);
		Mockito.when(osiUtils.getOSIDetailsFromMetaInfo(anyString(),any())).thenReturn(regOsiDto);
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
	@Ignore
	public void testOfficerDetailsNull() throws Exception {
		regOsiDto.setOfficerHashedPin(null);
		Mockito.when(osiUtils.getIdentity(anyString())).thenReturn(identity);
		Mockito.when(osiUtils.getOSIDetailsFromMetaInfo(anyString(),any())).thenReturn(regOsiDto);

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
	@Ignore
	public void testIntroducerDetailsNull() throws Exception {
		Mockito.when(transcationStatusService.getTransactionByRegIdAndStatusCode(anyString(), anyString()))
				.thenReturn(transactionDto);
		
		Mockito.when(osiUtils.getIdentity(anyString())).thenReturn(identity);
		Mockito.when(osiUtils.getOSIDetailsFromMetaInfo(anyString(),any())).thenReturn(regOsiDto);
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
	@Ignore
	public void testisValidOSIFailure() throws Exception {
		authResponseDTO.setStatus("N");
		Mockito.when(osiUtils.getIdentity(anyString())).thenReturn(identity);
		Mockito.when(osiUtils.getOSIDetailsFromMetaInfo(anyString(),any())).thenReturn(regOsiDto);
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
	@Ignore
	public void testvalidateFingerprintFailure() throws Exception {
		Mockito.when(osiUtils.getIdentity(anyString())).thenReturn(identity);
		Mockito.when(osiUtils.getOSIDetailsFromMetaInfo(anyString(),any())).thenReturn(regOsiDto);
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
	@Ignore
	public void testvalidateFaceFailure() throws Exception {
		Mockito.when(osiUtils.getIdentity(anyString())).thenReturn(identity);
		Mockito.when(osiUtils.getOSIDetailsFromMetaInfo(anyString(),any())).thenReturn(regOsiDto);
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
	@Ignore
	public void testSupervisorDetailsNull() throws Exception {
		regOsiDto.setSupervisorHashedPin(null);
		
		Mockito.when(osiUtils.getIdentity(anyString())).thenReturn(identity);
		Mockito.when(osiUtils.getOSIDetailsFromMetaInfo(anyString(),any())).thenReturn(regOsiDto);


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
	@Ignore
	public void testInvalidIris() throws Exception {
		authResponseDTO.setStatus("N");
		regOsiDto.setOfficerId(null);
		Mockito.when(osiUtils.getIdentity(anyString())).thenReturn(identity);
		Mockito.when(osiUtils.getOSIDetailsFromMetaInfo(anyString(),any())).thenReturn(regOsiDto);
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
	@Ignore
	public void testIntroducerUIN() throws Exception {

		Mockito.when(osiUtils.getIdentity(anyString())).thenReturn(identity);
		Mockito.when(osiUtils.getOSIDetailsFromMetaInfo(anyString(),any())).thenReturn(regOsiDto);
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
	@Ignore
	public void tesAllIntroducerFingerPrint1() throws ApisResourceAccessException, IOException {
		demographicInfoDto.setUin(null);
		Mockito.when(osiUtils.getIdentity(anyString())).thenReturn(identity);
		Mockito.when(osiUtils.getOSIDetailsFromMetaInfo(anyString(),any())).thenReturn(regOsiDto);
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
		regOsiDto.setSupervisorHashedPwd("true");
		regOsiDto.setOfficerHashedPwd("true");
		
		JSONObject demoJson = new JSONObject();
		demoJson.put("age", "10");
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.class, "objectMapperReadValue", anyString(), anyObject()).thenReturn(demoJson);
		PowerMockito.when(JsonUtil.class, "getJSONObject", anyObject(), anyString()).thenReturn(demoJson);
		PowerMockito.when(JsonUtil.class, "getJSONValue", anyObject(), anyString()).thenReturn("1990/01/01").thenReturn(10);
		
		Mockito.when(osiUtils.getIdentity(anyString())).thenReturn(identity);
		Mockito.when(osiUtils.getOSIDetailsFromMetaInfo(anyString(),any())).thenReturn(regOsiDto);
		Mockito.when(packetInfoManager.findDemoById(anyString())).thenReturn(demographicDedupeDtoList);
		Mockito.when(transcationStatusService.getTransactionByRegIdAndStatusCode(anyString(), anyString()))
				.thenReturn(transactionDto);

		boolean isValid = osiValidator.isValidOSI("reg1234");

		assertTrue(isValid);
	}

}