package io.mosip.registration.test.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.MasterSyncDao;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.mastersync.BiometricAttributeDto;
import io.mosip.registration.dto.mastersync.MasterDataResponseDto;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.entity.mastersync.MasterLocation;
import io.mosip.registration.entity.mastersync.MasterReasonCategory;
import io.mosip.registration.entity.mastersync.MasterReasonList;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.impl.MasterSyncServiceImpl;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

/**
 * @author Sreekar Chukka
 *
 * @since 1.0.0
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({ RegistrationAppHealthCheckUtil.class, UriComponentsBuilder.class, URI.class })
public class MasterSyncServiceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private MasterSyncServiceImpl masterSyncServiceImpl;
	@Mock
	private MasterSyncDao masterSyncDao;

	@Mock
	private RegistrationAppHealthCheckUtil registrationAppHealthCheckUtil;

	@Mock
	ObjectMapper objectMapper;
	
	@Mock
	private ServiceDelegateUtil serviceDelegateUtil;

	/*
	 * @Mock UriComponentsBuilder UriComponentsBuilder;
	 */

	@Mock
	private AuditFactory auditFactory;

	private static ApplicationContext applicationContext = ApplicationContext.getInstance();

	@BeforeClass
	public static void beforeClass() throws URISyntaxException {

		ReflectionTestUtils.setField(SessionContext.class, "sessionContext", null);
		RegistrationCenterDetailDTO centerDetailDTO = new RegistrationCenterDetailDTO();
		centerDetailDTO.setRegistrationCenterId("mosip");
		SessionContext.getInstance().getUserContext().setRegistrationCenterDetailDTO(centerDetailDTO);
		applicationContext.setApplicationMessagesBundle();
	}

	@Test
	public void testMasterSyncSucessCase()
			throws RegBaseCheckedException, JsonParseException, JsonMappingException, IOException {
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		MasterDataResponseDto masterSyncDto = new MasterDataResponseDto();
		SuccessResponseDTO sucessResponse = new SuccessResponseDTO();
		ResponseDTO responseDTO = new ResponseDTO();

		BiometricAttributeDto biometricattributes = new BiometricAttributeDto();

		BiometricAttributeDto biometricAttributeResponseDto = new BiometricAttributeDto();

		biometricattributes.setBiometricTypeCode("1");
		biometricattributes.setCode("1");
		biometricattributes.setDescription("finerprints");
		biometricattributes.setLangCode("eng");
		biometricattributes.setName("littile finger");

		List<BiometricAttributeDto> biometricattribute = new ArrayList<>();
		biometricattribute.add(biometricattributes);

		biometricAttributeResponseDto.setBiometricTypeCode("1");
		biometricAttributeResponseDto.setCode("1");
		biometricAttributeResponseDto.setDescription("finerprints");
		biometricAttributeResponseDto.setLangCode("eng");
		biometricAttributeResponseDto.setName("littile finger");

		List<BiometricAttributeDto> biometrictypes = new ArrayList<>();
		biometrictypes.add(biometricAttributeResponseDto);

		masterSyncDto.setBiometricattributes(biometrictypes);

		SyncControl masterSyncDetails = new SyncControl();

		masterSyncDetails.setSyncJobId("MDS_J00001");
		masterSyncDetails.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));
		masterSyncDetails.setCrBy("mosip");
		masterSyncDetails.setIsActive(true);
		masterSyncDetails.setLangCode("eng");
		masterSyncDetails.setCrDtime(new Timestamp(System.currentTimeMillis()));

		String masterJson = "{\"languages\":[{\"language\":[{\"languageCode\":\"1\",\"languageName\":\"eng\",\"languageFamily\":\"Engish\",\"nativeName\":\"Engilsh\"},{\"languageCode\":\"2\",\"languageName\":\"arb\",\"languageFamily\":\"Arab\",\"nativeName\":\"Arab\"}]}],\"biometricattributes\":[{\"biometricattribute\":[{\"code\":\"1\",\"name\":\"asdf\",\"description\":\"testing\",\"biometricTypeCode\":\"1\",\"langCode\":\"eng\"}]},{\"biometricattribute\":[{\"code\":\"2\",\"name\":\"asdf\",\"description\":\"testing\",\"biometricTypeCode\":\"1\",\"langCode\":\"eng\"}]},{\"biometricattribute\":[{\"code\":\"3\",\"name\":\"asfesr\",\"description\":\"testing2\",\"biometricTypeCode\":\"2\",\"langCode\":\"eng\"}]}],\"blacklistedwords\":[{\"word\":\"1\",\"description\":\"asdf1\",\"langCode\":\"eng\"},{\"word\":\"2\",\"description\":\"asdf2\",\"langCode\":\"eng\"},{\"word\":\"3\",\"description\":\"asdf3\",\"langCode\":\"eng\"}],\"biometrictypes\":[{\"biometrictype\":[{\"code\":\"1\",\"name\":\"fingerprints\",\"description\":\"fingerprint\",\"langCode\":\"eng\"}]},{\"biometrictype\":[{\"code\":\"2\",\"name\":\"iries\",\"description\":\"iriescapture\",\"langCode\":\"eng\"}]}],\"idtypes\":[{\"code\":\"1\",\"name\":\"PAN\",\"description\":\"pan card\",\"langCode\":\"eng\"},{\"code\":\"1\",\"name\":\"VID\",\"description\":\"voter id\",\"langCode\":\"eng\"}],\"documentcategories\":[{\"code\":\"1\",\"name\":\"POA\",\"description\":\"poaaa\",\"langCode\":\"eng\"},{\"code\":\"2\",\"name\":\"POI\",\"description\":\"porrr\",\"langCode\":\"eng\"},{\"code\":\"3\",\"name\":\"POR\",\"description\":\"pobbb\",\"langCode\":\"eng\"},{\"code\":\"4\",\"name\":\"POB\",\"description\":\"poiii\",\"langCode\":\"eng\"}],\"documenttypes\":[{\"code\":\"1\",\"name\":\"passport\",\"description\":\"passportid\",\"langCode\":\"eng\"},{\"code\":\"2\",\"name\":\"passport\",\"description\":\"passportid's\",\"langCode\":\"eng\"},{\"code\":\"3\",\"name\":\"voterid\",\"description\":\"votercards\",\"langCode\":\"eng\"},{\"code\":\"4\",\"name\":\"passport\",\"description\":\"passports\",\"langCode\":\"eng\"}],\"locations\":[{\"code\":\"1\",\"name\":\"chennai\",\"hierarchyLevel\":\"1\",\"hierarchyName\":\"Tamil Nadu\",\"parentLocCode\":\"1\",\"langCode\":\"eng\"},{\"code\":\"2\",\"name\":\"hyderabad\",\"hierarchyLevel\":\"2\",\"hierarchyName\":\"Telengana\",\"parentLocCode\":\"2\",\"langCode\":\"eng\"}],\"titles\":[{\"title\":[{\"code\":\"1\",\"titleName\":\"admin\",\"titleDescription\":\"ahsasa\",\"langCode\":\"eng\"}]},{\"title\":[{\"code\":\"2\",\"titleDescription\":\"asas\",\"titleName\":\"superadmin\",\"langCode\":\"eng\"}]}],\"genders\":[{\"gender\":[{\"genderCode\":\"1\",\"genderName\":\"male\",\"langCode\":\"eng\"},{\"genderCode\":\"2\",\"genderName\":\"female\",\"langCode\":\"eng\"}]},{\"gender\":[{\"genderCode\":\"1\",\"genderName\":\"female\",\"langCode\":\"eng\"}]}],\"reasonCategory\":{\"code\":\"1\",\"name\":\"rejected\",\"description\":\"rejectedfile\",\"langCode\":\"eng\",\"reasonLists\":[{\"code\":\"1\",\"name\":\"document\",\"description\":\"inavliddoc\",\"langCode\":\"eng\",\"reasonCategoryCode\":\"1\"},{\"code\":\"2\",\"name\":\"document\",\"description\":\"inavlidtype\",\"langCode\":\"eng\",\"reasonCategoryCode\":\"2\"}]}}";

		Mockito.when(masterSyncDao.syncJobDetails(Mockito.anyString())).thenReturn(masterSyncDetails);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		Mockito.when(objectMapper.readValue(masterJson, MasterDataResponseDto.class)).thenReturn(masterSyncDto);

		Mockito.when(masterSyncDao.save(masterSyncDto)).thenReturn("");

		sucessResponse.setCode(RegistrationConstants.MASTER_SYNC_SUCESS_MSG_CODE);
		sucessResponse.setInfoType(RegistrationConstants.ALERT_INFORMATION);
		sucessResponse.setMessage(RegistrationConstants.MASTER_SYNC_SUCCESS);

		responseDTO.setSuccessResponseDTO(sucessResponse);

		ResponseDTO responseDto = masterSyncServiceImpl.getMasterSync("MDS_J00001");
		// assertEquals(RegistrationConstants.MASTER_SYNC_SUCCESS,
		// responseDto.getSuccessResponseDTO().getMessage());
	}

	@Test
	public void testMasterSyncConnectionFailure()
			throws RegBaseCheckedException, JsonParseException, JsonMappingException, IOException {

		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);

		ErrorResponseDTO errorResponse = new ErrorResponseDTO();
		ResponseDTO responseDTO = new ResponseDTO();
		LinkedList<ErrorResponseDTO> errorResponses = new LinkedList<>();

		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(false);

		errorResponse.setCode(RegistrationConstants.MASTER_SYNC_OFFLINE_FAILURE_MSG_CODE);
		errorResponse.setInfoType(RegistrationConstants.ALERT_ERROR);
		errorResponse.setMessage(RegistrationConstants.MASTER_SYNC_OFFLINE_FAILURE_MSG);

		errorResponses.add(errorResponse);

		responseDTO.setErrorResponseDTOs(errorResponses);

		ResponseDTO responseDto = masterSyncServiceImpl.getMasterSync("MDS_J00001");
		assertEquals(RegistrationConstants.MASTER_SYNC_OFFLINE_FAILURE_MSG,
				responseDto.getErrorResponseDTOs().get(0).getMessage());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testExpectedIOException() throws JsonParseException, JsonMappingException, IOException {

		ResponseDTO responseDTO = new ResponseDTO();
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		MasterDataResponseDto masterSyncDto = new MasterDataResponseDto();
		String masterSyncJson="";
		LinkedList<ErrorResponseDTO> errorResponses = new LinkedList<>();

		// Error response
		ErrorResponseDTO errorResponse = new ErrorResponseDTO();
		errorResponse.setCode(RegistrationConstants.MASTER_SYNC_FAILURE_MSG_CODE);
		errorResponse.setInfoType(RegistrationConstants.ALERT_ERROR);
		errorResponse.setMessage(RegistrationConstants.MASTER_SYNC_FAILURE_MSG_INFO);

		errorResponses.add(errorResponse);

		// Adding list of error responses to response
		responseDTO.setErrorResponseDTOs(errorResponses);
		String masterJson = "{\"languages\":[{\"language\":[{\"languageCode\":\"1\",\"languageName\":\"eng\",\"languageFamily\":\"Engish\",\"nativeName\":\"Engilsh\"},{\"languageCode\":\"2\",\"languageName\":\"arb\",\"languageFamily\":\"Arab\",\"nativeName\":\"Arab\"}]}],\"biometricattributes\":[{\"biometricattribute\":[{\"code\":\"1\",\"name\":\"asdf\",\"description\":\"testing\",\"biometricTypeCode\":\"1\",\"langCode\":\"eng\"}]},{\"biometricattribute\":[{\"code\":\"2\",\"name\":\"asdf\",\"description\":\"testing\",\"biometricTypeCode\":\"1\",\"langCode\":\"eng\"}]},{\"biometricattribute\":[{\"code\":\"3\",\"name\":\"asfesr\",\"description\":\"testing2\",\"biometricTypeCode\":\"2\",\"langCode\":\"eng\"}]}],\"blacklistedwords\":[{\"word\":\"1\",\"description\":\"asdf1\",\"langCode\":\"eng\"},{\"word\":\"2\",\"description\":\"asdf2\",\"langCode\":\"eng\"},{\"word\":\"3\",\"description\":\"asdf3\",\"langCode\":\"eng\"}],\"biometrictypes\":[{\"biometrictype\":[{\"code\":\"1\",\"name\":\"fingerprints\",\"description\":\"fingerprint\",\"langCode\":\"eng\"}]},{\"biometrictype\":[{\"code\":\"2\",\"name\":\"iries\",\"description\":\"iriescapture\",\"langCode\":\"eng\"}]}],\"idtypes\":[{\"code\":\"1\",\"name\":\"PAN\",\"description\":\"pan card\",\"langCode\":\"eng\"},{\"code\":\"1\",\"name\":\"VID\",\"description\":\"voter id\",\"langCode\":\"eng\"}],\"documentcategories\":[{\"code\":\"1\",\"name\":\"POA\",\"description\":\"poaaa\",\"langCode\":\"eng\"},{\"code\":\"2\",\"name\":\"POI\",\"description\":\"porrr\",\"langCode\":\"eng\"},{\"code\":\"3\",\"name\":\"POR\",\"description\":\"pobbb\",\"langCode\":\"eng\"},{\"code\":\"4\",\"name\":\"POB\",\"description\":\"poiii\",\"langCode\":\"eng\"}],\"documenttypes\":[{\"code\":\"1\",\"name\":\"passport\",\"description\":\"passportid\",\"langCode\":\"eng\"},{\"code\":\"2\",\"name\":\"passport\",\"description\":\"passportid's\",\"langCode\":\"eng\"},{\"code\":\"3\",\"name\":\"voterid\",\"description\":\"votercards\",\"langCode\":\"eng\"},{\"code\":\"4\",\"name\":\"passport\",\"description\":\"passports\",\"langCode\":\"eng\"}],\"locations\":[{\"code\":\"1\",\"name\":\"chennai\",\"hierarchyLevel\":\"1\",\"hierarchyName\":\"Tamil Nadu\",\"parentLocCode\":\"1\",\"langCode\":\"eng\"},{\"code\":\"2\",\"name\":\"hyderabad\",\"hierarchyLevel\":\"2\",\"hierarchyName\":\"Telengana\",\"parentLocCode\":\"2\",\"langCode\":\"eng\"}],\"titles\":[{\"title\":[{\"code\":\"1\",\"titleName\":\"admin\",\"titleDescription\":\"ahsasa\",\"langCode\":\"eng\"}]},{\"title\":[{\"code\":\"2\",\"titleDescription\":\"asas\",\"titleName\":\"superadmin\",\"langCode\":\"eng\"}]}],\"genders\":[{\"gender\":[{\"genderCode\":\"1\",\"genderName\":\"male\",\"langCode\":\"eng\"},{\"genderCode\":\"2\",\"genderName\":\"female\",\"langCode\":\"eng\"}]},{\"gender\":[{\"genderCode\":\"1\",\"genderName\":\"female\",\"langCode\":\"eng\"}]}],\"reasonCategory\":{\"code\":\"1\",\"name\":\"rejected\",\"description\":\"rejectedfile\",\"langCode\":\"eng\",\"reasonLists\":[{\"code\":\"1\",\"name\":\"document\",\"description\":\"inavliddoc\",\"langCode\":\"eng\",\"reasonCategoryCode\":\"1\"},{\"code\":\"2\",\"name\":\"document\",\"description\":\"inavlidtype\",\"langCode\":\"eng\",\"reasonCategoryCode\":\"2\"}]}}";

		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		Mockito.when(objectMapper.readValue(masterSyncJson.toString(), MasterDataResponseDto.class)).thenReturn(masterSyncDto);

		Mockito.when(masterSyncDao.save(masterSyncDto)).thenReturn("");
		when(masterSyncDao.syncJobDetails(Mockito.anyString())).thenThrow(IOException.class);
		masterSyncServiceImpl.getMasterSync("MDS_J00001");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testExpectedNullException() throws JsonParseException, JsonMappingException, IOException {

		ResponseDTO responseDTO = new ResponseDTO();
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		MasterDataResponseDto masterSyncDto = new MasterDataResponseDto();
		String masterSyncJson="";
		LinkedList<ErrorResponseDTO> errorResponses = new LinkedList<>();

		// Error response
		ErrorResponseDTO errorResponse = new ErrorResponseDTO();
		errorResponse.setCode(RegistrationConstants.MASTER_SYNC_FAILURE_MSG_CODE);
		errorResponse.setInfoType(RegistrationConstants.ALERT_ERROR);
		errorResponse.setMessage(RegistrationConstants.MASTER_SYNC_FAILURE_MSG_INFO);

		errorResponses.add(errorResponse);

		// Adding list of error responses to response
		responseDTO.setErrorResponseDTOs(errorResponses);
		String masterJson = "{\"languages\":[{\"language\":[{\"languageCode\":\"1\",\"languageName\":\"eng\",\"languageFamily\":\"Engish\",\"nativeName\":\"Engilsh\"},{\"languageCode\":\"2\",\"languageName\":\"arb\",\"languageFamily\":\"Arab\",\"nativeName\":\"Arab\"}]}],\"biometricattributes\":[{\"biometricattribute\":[{\"code\":\"1\",\"name\":\"asdf\",\"description\":\"testing\",\"biometricTypeCode\":\"1\",\"langCode\":\"eng\"}]},{\"biometricattribute\":[{\"code\":\"2\",\"name\":\"asdf\",\"description\":\"testing\",\"biometricTypeCode\":\"1\",\"langCode\":\"eng\"}]},{\"biometricattribute\":[{\"code\":\"3\",\"name\":\"asfesr\",\"description\":\"testing2\",\"biometricTypeCode\":\"2\",\"langCode\":\"eng\"}]}],\"blacklistedwords\":[{\"word\":\"1\",\"description\":\"asdf1\",\"langCode\":\"eng\"},{\"word\":\"2\",\"description\":\"asdf2\",\"langCode\":\"eng\"},{\"word\":\"3\",\"description\":\"asdf3\",\"langCode\":\"eng\"}],\"biometrictypes\":[{\"biometrictype\":[{\"code\":\"1\",\"name\":\"fingerprints\",\"description\":\"fingerprint\",\"langCode\":\"eng\"}]},{\"biometrictype\":[{\"code\":\"2\",\"name\":\"iries\",\"description\":\"iriescapture\",\"langCode\":\"eng\"}]}],\"idtypes\":[{\"code\":\"1\",\"name\":\"PAN\",\"description\":\"pan card\",\"langCode\":\"eng\"},{\"code\":\"1\",\"name\":\"VID\",\"description\":\"voter id\",\"langCode\":\"eng\"}],\"documentcategories\":[{\"code\":\"1\",\"name\":\"POA\",\"description\":\"poaaa\",\"langCode\":\"eng\"},{\"code\":\"2\",\"name\":\"POI\",\"description\":\"porrr\",\"langCode\":\"eng\"},{\"code\":\"3\",\"name\":\"POR\",\"description\":\"pobbb\",\"langCode\":\"eng\"},{\"code\":\"4\",\"name\":\"POB\",\"description\":\"poiii\",\"langCode\":\"eng\"}],\"documenttypes\":[{\"code\":\"1\",\"name\":\"passport\",\"description\":\"passportid\",\"langCode\":\"eng\"},{\"code\":\"2\",\"name\":\"passport\",\"description\":\"passportid's\",\"langCode\":\"eng\"},{\"code\":\"3\",\"name\":\"voterid\",\"description\":\"votercards\",\"langCode\":\"eng\"},{\"code\":\"4\",\"name\":\"passport\",\"description\":\"passports\",\"langCode\":\"eng\"}],\"locations\":[{\"code\":\"1\",\"name\":\"chennai\",\"hierarchyLevel\":\"1\",\"hierarchyName\":\"Tamil Nadu\",\"parentLocCode\":\"1\",\"langCode\":\"eng\"},{\"code\":\"2\",\"name\":\"hyderabad\",\"hierarchyLevel\":\"2\",\"hierarchyName\":\"Telengana\",\"parentLocCode\":\"2\",\"langCode\":\"eng\"}],\"titles\":[{\"title\":[{\"code\":\"1\",\"titleName\":\"admin\",\"titleDescription\":\"ahsasa\",\"langCode\":\"eng\"}]},{\"title\":[{\"code\":\"2\",\"titleDescription\":\"asas\",\"titleName\":\"superadmin\",\"langCode\":\"eng\"}]}],\"genders\":[{\"gender\":[{\"genderCode\":\"1\",\"genderName\":\"male\",\"langCode\":\"eng\"},{\"genderCode\":\"2\",\"genderName\":\"female\",\"langCode\":\"eng\"}]},{\"gender\":[{\"genderCode\":\"1\",\"genderName\":\"female\",\"langCode\":\"eng\"}]}],\"reasonCategory\":{\"code\":\"1\",\"name\":\"rejected\",\"description\":\"rejectedfile\",\"langCode\":\"eng\",\"reasonLists\":[{\"code\":\"1\",\"name\":\"document\",\"description\":\"inavliddoc\",\"langCode\":\"eng\",\"reasonCategoryCode\":\"1\"},{\"code\":\"2\",\"name\":\"document\",\"description\":\"inavlidtype\",\"langCode\":\"eng\",\"reasonCategoryCode\":\"2\"}]}}";

		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		Mockito.when(objectMapper.readValue(masterSyncJson.toString(), MasterDataResponseDto.class)).thenReturn(masterSyncDto);

		Mockito.when(masterSyncDao.save(masterSyncDto)).thenReturn("");
		when(masterSyncDao.syncJobDetails(Mockito.anyString())).thenThrow(NullPointerException.class);
		masterSyncServiceImpl.getMasterSync("MDS_J00001");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testExpectedRegBaseUncheckedException() throws JsonParseException, JsonMappingException, IOException {

		ResponseDTO responseDTO = new ResponseDTO();
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		MasterDataResponseDto masterSyncDto = new MasterDataResponseDto();

		LinkedList<ErrorResponseDTO> errorResponses = new LinkedList<>();

		// Error response
		ErrorResponseDTO errorResponse = new ErrorResponseDTO();
		errorResponse.setCode(RegistrationConstants.MASTER_SYNC_FAILURE_MSG_CODE);
		errorResponse.setInfoType(RegistrationConstants.ALERT_ERROR);
		errorResponse.setMessage(RegistrationConstants.MASTER_SYNC_FAILURE_MSG_INFO);

		errorResponses.add(errorResponse);

		// Adding list of error responses to response
		responseDTO.setErrorResponseDTOs(errorResponses);
		String masterJson = "{\"languages\":[{\"language\":[{\"languageCode\":\"1\",\"languageName\":\"eng\",\"languageFamily\":\"Engish\",\"nativeName\":\"Engilsh\"},{\"languageCode\":\"2\",\"languageName\":\"arb\",\"languageFamily\":\"Arab\",\"nativeName\":\"Arab\"}]}],\"biometricattributes\":[{\"biometricattribute\":[{\"code\":\"1\",\"name\":\"asdf\",\"description\":\"testing\",\"biometricTypeCode\":\"1\",\"langCode\":\"eng\"}]},{\"biometricattribute\":[{\"code\":\"2\",\"name\":\"asdf\",\"description\":\"testing\",\"biometricTypeCode\":\"1\",\"langCode\":\"eng\"}]},{\"biometricattribute\":[{\"code\":\"3\",\"name\":\"asfesr\",\"description\":\"testing2\",\"biometricTypeCode\":\"2\",\"langCode\":\"eng\"}]}],\"blacklistedwords\":[{\"word\":\"1\",\"description\":\"asdf1\",\"langCode\":\"eng\"},{\"word\":\"2\",\"description\":\"asdf2\",\"langCode\":\"eng\"},{\"word\":\"3\",\"description\":\"asdf3\",\"langCode\":\"eng\"}],\"biometrictypes\":[{\"biometrictype\":[{\"code\":\"1\",\"name\":\"fingerprints\",\"description\":\"fingerprint\",\"langCode\":\"eng\"}]},{\"biometrictype\":[{\"code\":\"2\",\"name\":\"iries\",\"description\":\"iriescapture\",\"langCode\":\"eng\"}]}],\"idtypes\":[{\"code\":\"1\",\"name\":\"PAN\",\"description\":\"pan card\",\"langCode\":\"eng\"},{\"code\":\"1\",\"name\":\"VID\",\"description\":\"voter id\",\"langCode\":\"eng\"}],\"documentcategories\":[{\"code\":\"1\",\"name\":\"POA\",\"description\":\"poaaa\",\"langCode\":\"eng\"},{\"code\":\"2\",\"name\":\"POI\",\"description\":\"porrr\",\"langCode\":\"eng\"},{\"code\":\"3\",\"name\":\"POR\",\"description\":\"pobbb\",\"langCode\":\"eng\"},{\"code\":\"4\",\"name\":\"POB\",\"description\":\"poiii\",\"langCode\":\"eng\"}],\"documenttypes\":[{\"code\":\"1\",\"name\":\"passport\",\"description\":\"passportid\",\"langCode\":\"eng\"},{\"code\":\"2\",\"name\":\"passport\",\"description\":\"passportid's\",\"langCode\":\"eng\"},{\"code\":\"3\",\"name\":\"voterid\",\"description\":\"votercards\",\"langCode\":\"eng\"},{\"code\":\"4\",\"name\":\"passport\",\"description\":\"passports\",\"langCode\":\"eng\"}],\"locations\":[{\"code\":\"1\",\"name\":\"chennai\",\"hierarchyLevel\":\"1\",\"hierarchyName\":\"Tamil Nadu\",\"parentLocCode\":\"1\",\"langCode\":\"eng\"},{\"code\":\"2\",\"name\":\"hyderabad\",\"hierarchyLevel\":\"2\",\"hierarchyName\":\"Telengana\",\"parentLocCode\":\"2\",\"langCode\":\"eng\"}],\"titles\":[{\"title\":[{\"code\":\"1\",\"titleName\":\"admin\",\"titleDescription\":\"ahsasa\",\"langCode\":\"eng\"}]},{\"title\":[{\"code\":\"2\",\"titleDescription\":\"asas\",\"titleName\":\"superadmin\",\"langCode\":\"eng\"}]}],\"genders\":[{\"gender\":[{\"genderCode\":\"1\",\"genderName\":\"male\",\"langCode\":\"eng\"},{\"genderCode\":\"2\",\"genderName\":\"female\",\"langCode\":\"eng\"}]},{\"gender\":[{\"genderCode\":\"1\",\"genderName\":\"female\",\"langCode\":\"eng\"}]}],\"reasonCategory\":{\"code\":\"1\",\"name\":\"rejected\",\"description\":\"rejectedfile\",\"langCode\":\"eng\",\"reasonLists\":[{\"code\":\"1\",\"name\":\"document\",\"description\":\"inavliddoc\",\"langCode\":\"eng\",\"reasonCategoryCode\":\"1\"},{\"code\":\"2\",\"name\":\"document\",\"description\":\"inavlidtype\",\"langCode\":\"eng\",\"reasonCategoryCode\":\"2\"}]}}";

		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		Mockito.when(objectMapper.readValue(masterJson, MasterDataResponseDto.class)).thenReturn(masterSyncDto);

		Mockito.when(masterSyncDao.save(masterSyncDto)).thenReturn("");
		when(masterSyncDao.syncJobDetails(Mockito.anyString())).thenThrow(RegBaseUncheckedException.class);
		masterSyncServiceImpl.getMasterSync("MDS_J00001");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testExpectedRunException() throws JsonParseException, JsonMappingException, IOException {

		ResponseDTO responseDTO = new ResponseDTO();
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		MasterDataResponseDto masterSyncDto = new MasterDataResponseDto();

		LinkedList<ErrorResponseDTO> errorResponses = new LinkedList<>();

		// Error response
		ErrorResponseDTO errorResponse = new ErrorResponseDTO();
		errorResponse.setCode(RegistrationConstants.MASTER_SYNC_FAILURE_MSG_CODE);
		errorResponse.setInfoType(RegistrationConstants.ALERT_ERROR);
		errorResponse.setMessage(RegistrationConstants.MASTER_SYNC_FAILURE_MSG_INFO);

		errorResponses.add(errorResponse);

		// Adding list of error responses to response
		responseDTO.setErrorResponseDTOs(errorResponses);
		String masterJson = "{\"languages\":[{\"language\":[{\"languageCode\":\"1\",\"languageName\":\"eng\",\"languageFamily\":\"Engish\",\"nativeName\":\"Engilsh\"},{\"languageCode\":\"2\",\"languageName\":\"arb\",\"languageFamily\":\"Arab\",\"nativeName\":\"Arab\"}]}],\"biometricattributes\":[{\"biometricattribute\":[{\"code\":\"1\",\"name\":\"asdf\",\"description\":\"testing\",\"biometricTypeCode\":\"1\",\"langCode\":\"eng\"}]},{\"biometricattribute\":[{\"code\":\"2\",\"name\":\"asdf\",\"description\":\"testing\",\"biometricTypeCode\":\"1\",\"langCode\":\"eng\"}]},{\"biometricattribute\":[{\"code\":\"3\",\"name\":\"asfesr\",\"description\":\"testing2\",\"biometricTypeCode\":\"2\",\"langCode\":\"eng\"}]}],\"blacklistedwords\":[{\"word\":\"1\",\"description\":\"asdf1\",\"langCode\":\"eng\"},{\"word\":\"2\",\"description\":\"asdf2\",\"langCode\":\"eng\"},{\"word\":\"3\",\"description\":\"asdf3\",\"langCode\":\"eng\"}],\"biometrictypes\":[{\"biometrictype\":[{\"code\":\"1\",\"name\":\"fingerprints\",\"description\":\"fingerprint\",\"langCode\":\"eng\"}]},{\"biometrictype\":[{\"code\":\"2\",\"name\":\"iries\",\"description\":\"iriescapture\",\"langCode\":\"eng\"}]}],\"idtypes\":[{\"code\":\"1\",\"name\":\"PAN\",\"description\":\"pan card\",\"langCode\":\"eng\"},{\"code\":\"1\",\"name\":\"VID\",\"description\":\"voter id\",\"langCode\":\"eng\"}],\"documentcategories\":[{\"code\":\"1\",\"name\":\"POA\",\"description\":\"poaaa\",\"langCode\":\"eng\"},{\"code\":\"2\",\"name\":\"POI\",\"description\":\"porrr\",\"langCode\":\"eng\"},{\"code\":\"3\",\"name\":\"POR\",\"description\":\"pobbb\",\"langCode\":\"eng\"},{\"code\":\"4\",\"name\":\"POB\",\"description\":\"poiii\",\"langCode\":\"eng\"}],\"documenttypes\":[{\"code\":\"1\",\"name\":\"passport\",\"description\":\"passportid\",\"langCode\":\"eng\"},{\"code\":\"2\",\"name\":\"passport\",\"description\":\"passportid's\",\"langCode\":\"eng\"},{\"code\":\"3\",\"name\":\"voterid\",\"description\":\"votercards\",\"langCode\":\"eng\"},{\"code\":\"4\",\"name\":\"passport\",\"description\":\"passports\",\"langCode\":\"eng\"}],\"locations\":[{\"code\":\"1\",\"name\":\"chennai\",\"hierarchyLevel\":\"1\",\"hierarchyName\":\"Tamil Nadu\",\"parentLocCode\":\"1\",\"langCode\":\"eng\"},{\"code\":\"2\",\"name\":\"hyderabad\",\"hierarchyLevel\":\"2\",\"hierarchyName\":\"Telengana\",\"parentLocCode\":\"2\",\"langCode\":\"eng\"}],\"titles\":[{\"title\":[{\"code\":\"1\",\"titleName\":\"admin\",\"titleDescription\":\"ahsasa\",\"langCode\":\"eng\"}]},{\"title\":[{\"code\":\"2\",\"titleDescription\":\"asas\",\"titleName\":\"superadmin\",\"langCode\":\"eng\"}]}],\"genders\":[{\"gender\":[{\"genderCode\":\"1\",\"genderName\":\"male\",\"langCode\":\"eng\"},{\"genderCode\":\"2\",\"genderName\":\"female\",\"langCode\":\"eng\"}]},{\"gender\":[{\"genderCode\":\"1\",\"genderName\":\"female\",\"langCode\":\"eng\"}]}],\"reasonCategory\":{\"code\":\"1\",\"name\":\"rejected\",\"description\":\"rejectedfile\",\"langCode\":\"eng\",\"reasonLists\":[{\"code\":\"1\",\"name\":\"document\",\"description\":\"inavliddoc\",\"langCode\":\"eng\",\"reasonCategoryCode\":\"1\"},{\"code\":\"2\",\"name\":\"document\",\"description\":\"inavlidtype\",\"langCode\":\"eng\",\"reasonCategoryCode\":\"2\"}]}}";

		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		Mockito.when(objectMapper.readValue(masterJson, MasterDataResponseDto.class)).thenReturn(masterSyncDto);

		Mockito.when(masterSyncDao.save(masterSyncDto)).thenReturn("");
		when(masterSyncDao.syncJobDetails(Mockito.anyString())).thenThrow(new RuntimeException().getClass());
		masterSyncServiceImpl.getMasterSync("MDS_J00001");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testExpectedRegBasecheckedException() throws JsonParseException, JsonMappingException, IOException {

		ResponseDTO responseDTO = new ResponseDTO();
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		MasterDataResponseDto masterSyncDto = new MasterDataResponseDto();

		LinkedList<ErrorResponseDTO> errorResponses = new LinkedList<>();

		// Error response
		ErrorResponseDTO errorResponse = new ErrorResponseDTO();
		errorResponse.setCode(RegistrationConstants.MASTER_SYNC_FAILURE_MSG_CODE);
		errorResponse.setInfoType(RegistrationConstants.ALERT_ERROR);
		errorResponse.setMessage(RegistrationConstants.MASTER_SYNC_FAILURE_MSG_INFO);

		errorResponses.add(errorResponse);

		// Adding list of error responses to response
		responseDTO.setErrorResponseDTOs(errorResponses);
		String masterJson = "{\"languages\":[{\"language\":[{\"languageCode\":\"1\",\"languageName\":\"eng\",\"languageFamily\":\"Engish\",\"nativeName\":\"Engilsh\"},{\"languageCode\":\"2\",\"languageName\":\"arb\",\"languageFamily\":\"Arab\",\"nativeName\":\"Arab\"}]}],\"biometricattributes\":[{\"biometricattribute\":[{\"code\":\"1\",\"name\":\"asdf\",\"description\":\"testing\",\"biometricTypeCode\":\"1\",\"langCode\":\"eng\"}]},{\"biometricattribute\":[{\"code\":\"2\",\"name\":\"asdf\",\"description\":\"testing\",\"biometricTypeCode\":\"1\",\"langCode\":\"eng\"}]},{\"biometricattribute\":[{\"code\":\"3\",\"name\":\"asfesr\",\"description\":\"testing2\",\"biometricTypeCode\":\"2\",\"langCode\":\"eng\"}]}],\"blacklistedwords\":[{\"word\":\"1\",\"description\":\"asdf1\",\"langCode\":\"eng\"},{\"word\":\"2\",\"description\":\"asdf2\",\"langCode\":\"eng\"},{\"word\":\"3\",\"description\":\"asdf3\",\"langCode\":\"eng\"}],\"biometrictypes\":[{\"biometrictype\":[{\"code\":\"1\",\"name\":\"fingerprints\",\"description\":\"fingerprint\",\"langCode\":\"eng\"}]},{\"biometrictype\":[{\"code\":\"2\",\"name\":\"iries\",\"description\":\"iriescapture\",\"langCode\":\"eng\"}]}],\"idtypes\":[{\"code\":\"1\",\"name\":\"PAN\",\"description\":\"pan card\",\"langCode\":\"eng\"},{\"code\":\"1\",\"name\":\"VID\",\"description\":\"voter id\",\"langCode\":\"eng\"}],\"documentcategories\":[{\"code\":\"1\",\"name\":\"POA\",\"description\":\"poaaa\",\"langCode\":\"eng\"},{\"code\":\"2\",\"name\":\"POI\",\"description\":\"porrr\",\"langCode\":\"eng\"},{\"code\":\"3\",\"name\":\"POR\",\"description\":\"pobbb\",\"langCode\":\"eng\"},{\"code\":\"4\",\"name\":\"POB\",\"description\":\"poiii\",\"langCode\":\"eng\"}],\"documenttypes\":[{\"code\":\"1\",\"name\":\"passport\",\"description\":\"passportid\",\"langCode\":\"eng\"},{\"code\":\"2\",\"name\":\"passport\",\"description\":\"passportid's\",\"langCode\":\"eng\"},{\"code\":\"3\",\"name\":\"voterid\",\"description\":\"votercards\",\"langCode\":\"eng\"},{\"code\":\"4\",\"name\":\"passport\",\"description\":\"passports\",\"langCode\":\"eng\"}],\"locations\":[{\"code\":\"1\",\"name\":\"chennai\",\"hierarchyLevel\":\"1\",\"hierarchyName\":\"Tamil Nadu\",\"parentLocCode\":\"1\",\"langCode\":\"eng\"},{\"code\":\"2\",\"name\":\"hyderabad\",\"hierarchyLevel\":\"2\",\"hierarchyName\":\"Telengana\",\"parentLocCode\":\"2\",\"langCode\":\"eng\"}],\"titles\":[{\"title\":[{\"code\":\"1\",\"titleName\":\"admin\",\"titleDescription\":\"ahsasa\",\"langCode\":\"eng\"}]},{\"title\":[{\"code\":\"2\",\"titleDescription\":\"asas\",\"titleName\":\"superadmin\",\"langCode\":\"eng\"}]}],\"genders\":[{\"gender\":[{\"genderCode\":\"1\",\"genderName\":\"male\",\"langCode\":\"eng\"},{\"genderCode\":\"2\",\"genderName\":\"female\",\"langCode\":\"eng\"}]},{\"gender\":[{\"genderCode\":\"1\",\"genderName\":\"female\",\"langCode\":\"eng\"}]}],\"reasonCategory\":{\"code\":\"1\",\"name\":\"rejected\",\"description\":\"rejectedfile\",\"langCode\":\"eng\",\"reasonLists\":[{\"code\":\"1\",\"name\":\"document\",\"description\":\"inavliddoc\",\"langCode\":\"eng\",\"reasonCategoryCode\":\"1\"},{\"code\":\"2\",\"name\":\"document\",\"description\":\"inavlidtype\",\"langCode\":\"eng\",\"reasonCategoryCode\":\"2\"}]}}";

		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		Mockito.when(objectMapper.readValue(masterJson, MasterDataResponseDto.class)).thenReturn(masterSyncDto);

		Mockito.when(masterSyncDao.save(masterSyncDto)).thenReturn("");
		when(masterSyncDao.syncJobDetails(Mockito.anyString())).thenThrow(RegBaseCheckedException.class);
		masterSyncServiceImpl.getMasterSync("MDS_J00001");
	}

	@SuppressWarnings("static-access")
	@Test
	public void testMasterSyncSucessCaseJson()
			throws RegBaseCheckedException, JsonParseException, JsonMappingException, IOException, URISyntaxException {
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		PowerMockito.mockStatic(UriComponentsBuilder.class);
		PowerMockito.mockStatic(URI.class);
		MasterDataResponseDto masterSyncDto = new MasterDataResponseDto();
		MasterDataResponseDto masterSyncDt = new MasterDataResponseDto();
		SuccessResponseDTO sucessResponse = new SuccessResponseDTO();
		ResponseDTO responseDTO = new ResponseDTO();

		BiometricAttributeDto biometricattributes = new BiometricAttributeDto();

		BiometricAttributeDto biometricAttributeResponseDto = new BiometricAttributeDto();

		biometricattributes.setBiometricTypeCode("1");
		biometricattributes.setCode("1");
		biometricattributes.setDescription("finerprints");
		biometricattributes.setLangCode("eng");
		biometricattributes.setName("littile finger");

		List<BiometricAttributeDto> biometricattribute = new ArrayList<>();
		biometricattribute.add(biometricattributes);

		biometricAttributeResponseDto.setBiometricTypeCode("1");
		biometricAttributeResponseDto.setCode("1");
		biometricAttributeResponseDto.setDescription("finerprints");
		biometricAttributeResponseDto.setLangCode("eng");
		biometricAttributeResponseDto.setName("littile finger");

		List<BiometricAttributeDto> biometrictypes = new ArrayList<>();
		biometrictypes.add(biometricAttributeResponseDto);

		masterSyncDto.setBiometricattributes(biometrictypes);

		SyncControl masterSyncDetails = new SyncControl();

		masterSyncDetails.setSyncJobId("MDS_J00001");
		masterSyncDetails.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));
		masterSyncDetails.setCrBy("mosip");
		masterSyncDetails.setIsActive(true);
		masterSyncDetails.setLangCode("eng");
		masterSyncDetails.setCrDtime(new Timestamp(System.currentTimeMillis()));

		String masterSyncJson="";
		
		String masterJson = "{\r\n" + 
				"   \"registrationCenter\": [\r\n" + 
				"      {\r\n" + 
				"         \"id\": \"1\",\r\n" + 
				"         \"name\": \"BangaloreMain\",\r\n" + 
				"         \"centerTypeCode\": \"REG01\",\r\n" + 
				"         \"addressLine1\": \"Global village\",\r\n" + 
				"         \"addressLine2\": null,\r\n" + 
				"         \"addressLine3\": null,\r\n" + 
				"         \"latitude\": \"12.9180022\",\r\n" + 
				"         \"longitude\": \"77.5028892\",\r\n" + 
				"         \"locationCode\": \"LOC01\",\r\n" + 
				"         \"holidayLocationCode\": \"LOC01\",\r\n" + 
				"         \"contactPhone\": \"9348548\",\r\n" + 
				"         \"numberOfStations\": null,\r\n" + 
				"         \"workingHours\": \"8\",\r\n" + 
				"         \"languageCode\": \"ENG\",\r\n" + 
				"         \"numberOfKiosks\": 4,\r\n" + 
				"         \"perKioskProcessTime\": \"00:13:00\",\r\n" + 
				"         \"centerStartTime\": \"09:00:00\",\r\n" + 
				"         \"centerEndTime\": \"17:00:00\",\r\n" + 
				"         \"timeZone\": null,\r\n" + 
				"         \"contactPerson\": null,\r\n" + 
				"         \"lunchStartTime\": \"13:00:00\",\r\n" + 
				"         \"lunchEndTime\": \"14:00:00\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": false\r\n" + 
				"      }\r\n" + 
				"   ],\r\n" + 
				"   \"registrationCenterTypes\": null,\r\n" + 
				"   \"machineDetails\": [\r\n" + 
				"      {\r\n" + 
				"         \"id\": \"HP\",\r\n" + 
				"         \"name\": \"HP\",\r\n" + 
				"         \"serialNum\": \"12345\",\r\n" + 
				"         \"macAddress\": null,\r\n" + 
				"         \"ipAddress\": \"127.01.01.01\",\r\n" + 
				"         \"machineSpecId\": \"HP_ID\",\r\n" + 
				"         \"langCode\": \"ENG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"validityDateTime\": \"2022-11-15T22:55:42\",\r\n" + 
				"         \"isDeleted\": false\r\n" + 
				"      }\r\n" + 
				"   ],\r\n" + 
				"   \"machineSpecification\": [\r\n" + 
				"      {\r\n" + 
				"         \"id\": \"HP_ID\",\r\n" + 
				"         \"name\": \"HP\",\r\n" + 
				"         \"brand\": \"HP\",\r\n" + 
				"         \"model\": \"Intel\",\r\n" + 
				"         \"machineTypeCode\": \"1001\",\r\n" + 
				"         \"minDriverversion\": \"0.05\",\r\n" + 
				"         \"description\": \"HP laptop\",\r\n" + 
				"         \"langCode\": \"ENG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      }\r\n" + 
				"   ],\r\n" + 
				"   \"machineType\": [\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"1001\",\r\n" + 
				"         \"langCode\": \"ENG\",\r\n" + 
				"         \"name\": \"HP\",\r\n" + 
				"         \"description\": \"HP laptop\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      }\r\n" + 
				"   ],\r\n" + 
				"   \"devices\": [\r\n" + 
				"      {\r\n" + 
				"         \"id\": \"1001\",\r\n" + 
				"         \"name\": \"laptop\",\r\n" + 
				"         \"serialNum\": \"1234\",\r\n" + 
				"         \"deviceSpecId\": \"laptop_id\",\r\n" + 
				"         \"macAddress\": \"127.01.01.01\",\r\n" + 
				"         \"ipAddress\": \"127.01.01.01\",\r\n" + 
				"         \"langCode\": \"ENG\",\r\n" + 
				"         \"isDeleted\": null,\r\n" + 
				"         \"active\": false\r\n" + 
				"      }\r\n" + 
				"   ],\r\n" + 
				"   \"deviceTypes\": [\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"laptop_code\",\r\n" + 
				"         \"langCode\": \"ENG\",\r\n" + 
				"         \"name\": \"Laptop\",\r\n" + 
				"         \"description\": \"laptop hp\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      }\r\n" + 
				"   ],\r\n" + 
				"   \"deviceSpecifications\": [\r\n" + 
				"      {\r\n" + 
				"         \"id\": \"laptop_id\",\r\n" + 
				"         \"name\": \"laptop\",\r\n" + 
				"         \"brand\": \"HP\",\r\n" + 
				"         \"model\": \"HP\",\r\n" + 
				"         \"deviceTypeCode\": \"laptop_code\",\r\n" + 
				"         \"minDriverversion\": \"10.0\",\r\n" + 
				"         \"description\": \"hp laptop\",\r\n" + 
				"         \"langCode\": \"ENG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": false\r\n" + 
				"      }\r\n" + 
				"   ],\r\n" + 
				"   \"holidays\": [\r\n" + 
				"      {\r\n" + 
				"         \"holidayId\": \"0\",\r\n" + 
				"         \"holidayDate\": \"2012-06-21\",\r\n" + 
				"         \"holidayDay\": \"4\",\r\n" + 
				"         \"holidayMonth\": \"6\",\r\n" + 
				"         \"holidayYear\": \"2012\",\r\n" + 
				"         \"holidayName\": \"string\",\r\n" + 
				"         \"languageCode\": \"ENG\",\r\n" + 
				"         \"locationCode\": \"LOC01\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"holidayId\": \"4\",\r\n" + 
				"         \"holidayDate\": \"2017-12-12\",\r\n" + 
				"         \"holidayDay\": \"2\",\r\n" + 
				"         \"holidayMonth\": \"12\",\r\n" + 
				"         \"holidayYear\": \"2017\",\r\n" + 
				"         \"holidayName\": \"string\",\r\n" + 
				"         \"languageCode\": \"ENG\",\r\n" + 
				"         \"locationCode\": \"LOC01\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"holidayId\": \"56\",\r\n" + 
				"         \"holidayDate\": \"1994-12-12\",\r\n" + 
				"         \"holidayDay\": \"1\",\r\n" + 
				"         \"holidayMonth\": \"12\",\r\n" + 
				"         \"holidayYear\": \"1994\",\r\n" + 
				"         \"holidayName\": \"string\",\r\n" + 
				"         \"languageCode\": \"ENG\",\r\n" + 
				"         \"locationCode\": \"LOC01\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"holidayId\": \"5\",\r\n" + 
				"         \"holidayDate\": \"2018-10-21\",\r\n" + 
				"         \"holidayDay\": \"7\",\r\n" + 
				"         \"holidayMonth\": \"10\",\r\n" + 
				"         \"holidayYear\": \"2018\",\r\n" + 
				"         \"holidayName\": \"string\",\r\n" + 
				"         \"languageCode\": \"ENG\",\r\n" + 
				"         \"locationCode\": \"LOC01\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      }\r\n" + 
				"   ],\r\n" + 
				"   \"documentCategories\": [\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"string\",\r\n" + 
				"         \"name\": \"Proof of adress\",\r\n" + 
				"         \"description\": \"Address\",\r\n" + 
				"         \"langCode\": \"ENG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"string\",\r\n" + 
				"         \"name\": \"string\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"str\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"st5ing\",\r\n" + 
				"         \"name\": \"string\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"str\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"POR\",\r\n" + 
				"         \"name\": \"Residency\",\r\n" + 
				"         \"description\": \"Proof of residency\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"acegikmoqsuwacegikmoqsuwacegikmoqsu\",\r\n" + 
				"         \"name\": \"Residency\",\r\n" + 
				"         \"description\": \"code length 35\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"acegikmoqsuwacegikmoqsuwacegikmoqsua\",\r\n" + 
				"         \"name\": \"Residency\",\r\n" + 
				"         \"description\": \"code length 36\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"POP\",\r\n" + 
				"         \"name\": \"acegikmoqsuwacegikmoqsuwacegikmoqsuwacegikmoqsuwacegikmoqsuwace\",\r\n" + 
				"         \"description\": \"name length 63\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"POP\",\r\n" + 
				"         \"name\": \"acegikmoqsuwacegikmoqsuwacegikmoqsuwacegikmoqsuwacegikmoqsuwace\",\r\n" + 
				"         \"description\": \"name length 63\",\r\n" + 
				"         \"langCode\": \"fre\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"POJ\",\r\n" + 
				"         \"name\": \"acegikmoqsuwacegikmoqsuwacegikmoqsuwacegikmoqsuwacegikmoqsuwaceg\",\r\n" + 
				"         \"description\": \"name length 64\",\r\n" + 
				"         \"langCode\": \"fre\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"POM\",\r\n" + 
				"         \"name\": \"description missing\",\r\n" + 
				"         \"description\": \"\",\r\n" + 
				"         \"langCode\": \"arb\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"PON\",\r\n" + 
				"         \"name\": \"description 128\",\r\n" + 
				"         \"description\": \"acegikmoqsuwacegikmoqsuwacegikmoqsuwacegikmoqsuwacegikmoqsuwacegikmoqsuwacegikmoqsuwacegikmoqsuwacegikmoqsuwacegikmoqsuwacegikmo\",\r\n" + 
				"         \"langCode\": \"arb\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"POL\",\r\n" + 
				"         \"name\": \"description 127\",\r\n" + 
				"         \"description\": \"acegikmoqsuwacegikmoqsuwacegikmoqsuwacegikmoqsuwacegikmoqsuwacegikmoqsuwacegikmoqsuwacegikmoqsuwacegikmoqsuwacegikmoqsuwacegikm\",\r\n" + 
				"         \"langCode\": \"arb\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"POA\",\r\n" + 
				"         \"name\": \"Proof of adress\",\r\n" + 
				"         \"description\": \"Address\",\r\n" + 
				"         \"langCode\": \"ENG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj\",\r\n" + 
				"         \"name\": \"Proof of adress\",\r\n" + 
				"         \"description\": \"Address\",\r\n" + 
				"         \"langCode\": \"ENG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"POI\",\r\n" + 
				"         \"name\": \"jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj\",\r\n" + 
				"         \"description\": \"Address\",\r\n" + 
				"         \"langCode\": \"ENG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"POB\",\r\n" + 
				"         \"name\": \"jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj\",\r\n" + 
				"         \"description\": \"Adgahsg hdfhkjhf hfkajfh af hdfkjhdaf  hdjakf adkfa hjkdafh dafad kdajfhkdafh  hakdfh afk fgah g gsgashgaksdaskddksad ksjdhaks q\",\r\n" + 
				"         \"langCode\": \"ENG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      }\r\n" + 
				"   ],\r\n" + 
				"   \"documentTypes\": [\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"POA05\",\r\n" + 
				"         \"name\": \"DL\",\r\n" + 
				"         \"description\": \"Driving Licence\",\r\n" + 
				"         \"langCode\": \"EN\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"string\",\r\n" + 
				"         \"name\": \"Proof of adress\",\r\n" + 
				"         \"description\": \"Address\",\r\n" + 
				"         \"langCode\": \"ENG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"POA\",\r\n" + 
				"         \"name\": \"Rental Agreement\",\r\n" + 
				"         \"description\": \"proof of address\",\r\n" + 
				"         \"langCode\": \"ENG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"POA01\",\r\n" + 
				"         \"name\": \"Rental Agreement\",\r\n" + 
				"         \"description\": \"Rental Agreement\",\r\n" + 
				"         \"langCode\": \"ENG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"POA02\",\r\n" + 
				"         \"name\": \"Passport\",\r\n" + 
				"         \"description\": \"Passport\",\r\n" + 
				"         \"langCode\": \"ENG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"POA03\",\r\n" + 
				"         \"name\": \"Passport\",\r\n" + 
				"         \"description\": \"Passport\",\r\n" + 
				"         \"langCode\": \"ENG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"POA04\",\r\n" + 
				"         \"name\": \"Driving Licence\",\r\n" + 
				"         \"description\": \"Driving Licence\",\r\n" + 
				"         \"langCode\": \"ENG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      }\r\n" + 
				"   ],\r\n" + 
				"   \"validDocumentMapping\": [\r\n" + 
				"      {\r\n" + 
				"         \"docTypeCode\": \"string\",\r\n" + 
				"         \"docCategoryCode\": \"string\",\r\n" + 
				"         \"langCode\": \"ENG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"docTypeCode\": \"POA01\",\r\n" + 
				"         \"docCategoryCode\": \"POA\",\r\n" + 
				"         \"langCode\": \"ENG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"docTypeCode\": \"POA02\",\r\n" + 
				"         \"docCategoryCode\": \"POA\",\r\n" + 
				"         \"langCode\": \"ENG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"docTypeCode\": \"POA03\",\r\n" + 
				"         \"docCategoryCode\": \"POA\",\r\n" + 
				"         \"langCode\": \"ENG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"docTypeCode\": \"POA04\",\r\n" + 
				"         \"docCategoryCode\": \"POA\",\r\n" + 
				"         \"langCode\": \"ENG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      }\r\n" + 
				"   ],\r\n" + 
				"   \"templates\": [\r\n" + 
				"      {\r\n" + 
				"         \"id\": \"SMS\",\r\n" + 
				"         \"name\": \"SMS template\",\r\n" + 
				"         \"description\": \"SMS template to mobile\",\r\n" + 
				"         \"fileFormatCode\": \"TEXT\",\r\n" + 
				"         \"model\": \"PRE-reg\",\r\n" + 
				"         \"fileText\": \"\",\r\n" + 
				"         \"moduleId\": \"PRE\",\r\n" + 
				"         \"moduleName\": \"Pre-Registration\",\r\n" + 
				"         \"templateTypeCode\": \"SMS type\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"id\": \"SMS1\",\r\n" + 
				"         \"name\": \"SMS template\",\r\n" + 
				"         \"description\": \"SMS template to mobile\",\r\n" + 
				"         \"fileFormatCode\": \"JSON\",\r\n" + 
				"         \"model\": \"reg-proc model\",\r\n" + 
				"         \"fileText\": \"\",\r\n" + 
				"         \"moduleId\": \"REGPR\",\r\n" + 
				"         \"moduleName\": \"Registration-Processor\",\r\n" + 
				"         \"templateTypeCode\": \"SMS type\",\r\n" + 
				"         \"langCode\": \"ARB\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"id\": \"EMAIL\",\r\n" + 
				"         \"name\": \"Email template\",\r\n" + 
				"         \"description\": \"Email template to mobile\",\r\n" + 
				"         \"fileFormatCode\": \"HTML\",\r\n" + 
				"         \"model\": \"reg model\",\r\n" + 
				"         \"fileText\": \"\",\r\n" + 
				"         \"moduleId\": \"REG\",\r\n" + 
				"         \"moduleName\": \"Registration\",\r\n" + 
				"         \"templateTypeCode\": \"EMAIL type\",\r\n" + 
				"         \"langCode\": \"fre\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"id\": \"EMAIL1\",\r\n" + 
				"         \"name\": \"Email template\",\r\n" + 
				"         \"description\": \"Email template to mobile\",\r\n" + 
				"         \"fileFormatCode\": \"XML\",\r\n" + 
				"         \"model\": \"ida model\",\r\n" + 
				"         \"fileText\": \"\",\r\n" + 
				"         \"moduleId\": \"IDA\",\r\n" + 
				"         \"moduleName\": \"IDA module\",\r\n" + 
				"         \"templateTypeCode\": \"EMAIL type\",\r\n" + 
				"         \"langCode\": \"arb\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"id\": \"ida-otp-sms-template.txt\",\r\n" + 
				"         \"name\": \"Sample IDA OTP SMS Template\",\r\n" + 
				"         \"description\": \"ID Authentication OTP SMS template\",\r\n" + 
				"         \"fileFormatCode\": \"TEXT\",\r\n" + 
				"         \"model\": \"dev\",\r\n" + 
				"         \"fileText\": \"OTP for UIN  $uin is $otp and is valid for $validTime minutes. (Generated on $date at $time Hrs)\",\r\n" + 
				"         \"moduleId\": \"IDA\",\r\n" + 
				"         \"moduleName\": \"ID Authentication\",\r\n" + 
				"         \"templateTypeCode\": \"SMS type\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      }\r\n" + 
				"   ],\r\n" + 
				"   \"templatesTypes\": [\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"SMS type\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"description\": \"SMS template type\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"EMAIL type\",\r\n" + 
				"         \"langCode\": \"fre\",\r\n" + 
				"         \"description\": \"EMAIl template type\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"SMS type\",\r\n" + 
				"         \"langCode\": \"ARB\",\r\n" + 
				"         \"description\": \"SMS template type\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"EMAIL type\",\r\n" + 
				"         \"langCode\": \"arb\",\r\n" + 
				"         \"description\": \"EMAIl template type\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      }\r\n" + 
				"   ],\r\n" + 
				"   \"templateFileFormat\": [\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"TEXT\",\r\n" + 
				"         \"description\": \"Text file format \",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"HTML\",\r\n" + 
				"         \"description\": \"HTML file format\",\r\n" + 
				"         \"langCode\": \"fre\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"JSON\",\r\n" + 
				"         \"description\": \"JSON file format\",\r\n" + 
				"         \"langCode\": \"ARB\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"XML\",\r\n" + 
				"         \"description\": \"XML file format\",\r\n" + 
				"         \"langCode\": \"arb\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      }\r\n" + 
				"   ],\r\n" + 
				"   \"reasonCategory\": [\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"DEMO\",\r\n" + 
				"         \"name\": \"Demogrphic is not valid\",\r\n" + 
				"         \"description\": \"Pincode is not valid\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"DOCUMENTS\",\r\n" + 
				"         \"name\": \"Document is not valid\",\r\n" + 
				"         \"description\": \"poa is not valid\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"IRIS\",\r\n" + 
				"         \"name\": \"Iris scan is missing\",\r\n" + 
				"         \"description\": \"Iris scan is missing\",\r\n" + 
				"         \"langCode\": \"fre\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"Biometric\",\r\n" + 
				"         \"name\": \"Biometric scan is missing\",\r\n" + 
				"         \"description\": \"Biometric scan is missing\",\r\n" + 
				"         \"langCode\": \"ARB\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"Biometric\",\r\n" + 
				"         \"name\": \"Biometric scan is missing\",\r\n" + 
				"         \"description\": \" Biometric scan is missing\",\r\n" + 
				"         \"langCode\": \"arb\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      }\r\n" + 
				"   ],\r\n" + 
				"   \"reasonList\": null,\r\n" + 
				"   \"blackListedWords\": [\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"black6\",\r\n" + 
				"         \"description\": \"blacklistedword6\",\r\n" + 
				"         \"langCode\": \"ENG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"black\",\r\n" + 
				"         \"description\": \"blacklistedword\",\r\n" + 
				"         \"langCode\": \"HIN\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"black3\",\r\n" + 
				"         \"description\": \"blacklistedword3\",\r\n" + 
				"         \"langCode\": \"KAN\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"black2\",\r\n" + 
				"         \"description\": \"blacklistedword2\",\r\n" + 
				"         \"langCode\": \"KAN\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"black5\",\r\n" + 
				"         \"description\": \"blacklistedword5\",\r\n" + 
				"         \"langCode\": \"eng\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"black4\",\r\n" + 
				"         \"description\": \"blacklistedword4\",\r\n" + 
				"         \"langCode\": \"KAN\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"string\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"123\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"786\",\r\n" + 
				"         \"description\": \"arabic number\",\r\n" + 
				"         \"langCode\": \"arb\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"abcdefghijklmnopqrstuvwxyabcdefghijk\",\r\n" + 
				"         \"description\": \"code length 36\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"abcdefghijklmnopqrstuvwxyabcdefghij\",\r\n" + 
				"         \"description\": \"code length 36\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"abcdefghijklmnopqrstuvwxyabcdefghijkl\",\r\n" + 
				"         \"description\": \"code length 36\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"dove\",\r\n" + 
				"         \"description\": \"code length 37\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": false,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"123\",\r\n" + 
				"         \"description\": \"is active is missing\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"plane\",\r\n" + 
				"         \"description\": \"13\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"string\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"SAMPLE_WORD\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"${uniqueStr}\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"FQlxnvvxwaoGJLXvXdsXuATgOeqFUkIo\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"dSnWuAsURsAlruCvAzMdSfBHGmuNRWva\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"nLLSiFykpDmcikJJygONNBFRsLPouTZd\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"OudWVLukISoAWdaGimTBqaiiZTMQBNQr\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"EdGcmIdcFxLmyPkFqnpJTvJSmvuPyxzw\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"usqtZBVHeDXdKLTHGRPNnvaHZPKLGNAl\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"${extcode1}\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"${randomstring}\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"TrtTeDXbHwfxZeUKeZPKECwZlWnrOOaO\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"vTPFkPxLixMkEEKmnpksAZWftBuRCGPy\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"yrpMBCqPBNncbqepfCkSlTIOcngHLdRE\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"novHVGCGAxJwAXoVXpvffrZfDgWuBTlK\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"xaxfplvhnigsfph\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"fvwsrzmalqofmqy\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"kesmwycciqyqblf\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"lxfhymqsjxyycrc\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"kalirmoisculedx\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"mffjsffiqroxglm\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"haiiipgonvlzjka\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"vvluwxexllyvrvs\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"word\": \"acnpaljakoxuvtm\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      }\r\n" + 
				"   ],\r\n" + 
				"   \"locationHierarchy\": [\r\n" + 
				"      {\r\n" + 
				"         \"isDeleted\": null,\r\n" + 
				"         \"code\": \"LOC09\",\r\n" + 
				"         \"name\": \"string\",\r\n" + 
				"         \"hierarchyLevel\": 0,\r\n" + 
				"         \"hierarchyName\": \"string\",\r\n" + 
				"         \"parentLocCode\": \"string\",\r\n" + 
				"         \"languageCode\": \"ENG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"createdBy\": \"defaultadmin@mosip.io\",\r\n" + 
				"         \"updatedBy\": \"defaultadmin@mosip.io\"\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"isDeleted\": null,\r\n" + 
				"         \"code\": \"TTK\",\r\n" + 
				"         \"name\": \"TTK\",\r\n" + 
				"         \"hierarchyLevel\": 0,\r\n" + 
				"         \"hierarchyName\": \"ZIPCODE\",\r\n" + 
				"         \"parentLocCode\": \"IND\",\r\n" + 
				"         \"languageCode\": \"KAN\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"createdBy\": \"defaultadmin@mosip.io\",\r\n" + 
				"         \"updatedBy\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"isDeleted\": null,\r\n" + 
				"         \"code\": \"TN\",\r\n" + 
				"         \"name\": \"TamilNadu\",\r\n" + 
				"         \"hierarchyLevel\": 0,\r\n" + 
				"         \"hierarchyName\": \"STATE\",\r\n" + 
				"         \"parentLocCode\": \"IND\",\r\n" + 
				"         \"languageCode\": \"TAM\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"createdBy\": \"defaultadmin@mosip.io\",\r\n" + 
				"         \"updatedBy\": \"defaultadmin@mosip.io\"\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"isDeleted\": null,\r\n" + 
				"         \"code\": \"IND\",\r\n" + 
				"         \"name\": \"India\",\r\n" + 
				"         \"hierarchyLevel\": 0,\r\n" + 
				"         \"hierarchyName\": \"Country\",\r\n" + 
				"         \"parentLocCode\": null,\r\n" + 
				"         \"languageCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"createdBy\": \"Arun\",\r\n" + 
				"         \"updatedBy\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"isDeleted\": null,\r\n" + 
				"         \"code\": \"BLR\",\r\n" + 
				"         \"name\": \"Bangalore\",\r\n" + 
				"         \"hierarchyLevel\": 0,\r\n" + 
				"         \"hierarchyName\": \"city\",\r\n" + 
				"         \"parentLocCode\": \"IND\",\r\n" + 
				"         \"languageCode\": \"ENG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"createdBy\": \"defaultadmin@mosip.io\",\r\n" + 
				"         \"updatedBy\": \"Rajath\"\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"isDeleted\": null,\r\n" + 
				"         \"code\": \"RRN\",\r\n" + 
				"         \"name\": \"Rajarajeshwari Nagar\",\r\n" + 
				"         \"hierarchyLevel\": 0,\r\n" + 
				"         \"hierarchyName\": \"province\",\r\n" + 
				"         \"parentLocCode\": \"BLR\",\r\n" + 
				"         \"languageCode\": \"ENG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"createdBy\": \"defaultadmin@mosip.io\",\r\n" + 
				"         \"updatedBy\": \"Rajath\"\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"isDeleted\": null,\r\n" + 
				"         \"code\": \"BBMP\",\r\n" + 
				"         \"name\": \"BBMP\",\r\n" + 
				"         \"hierarchyLevel\": 0,\r\n" + 
				"         \"hierarchyName\": \"local admin autority\",\r\n" + 
				"         \"parentLocCode\": \"RRN\",\r\n" + 
				"         \"languageCode\": \"ENG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"createdBy\": \"defaultadmin@mosip.io\",\r\n" + 
				"         \"updatedBy\": \"Rajath\"\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"isDeleted\": null,\r\n" + 
				"         \"code\": \"POSTAL_CODE\",\r\n" + 
				"         \"name\": \"560059\",\r\n" + 
				"         \"hierarchyLevel\": 0,\r\n" + 
				"         \"hierarchyName\": \"Postal Code\",\r\n" + 
				"         \"parentLocCode\": \"RRN\",\r\n" + 
				"         \"languageCode\": \"ENG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"createdBy\": \"defaultadmin@mosip.io\",\r\n" + 
				"         \"updatedBy\": \"Rajath\"\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"isDeleted\": null,\r\n" + 
				"         \"code\": \"KER\",\r\n" + 
				"         \"name\": \"KERALA\",\r\n" + 
				"         \"hierarchyLevel\": 0,\r\n" + 
				"         \"hierarchyName\": \"STATE\",\r\n" + 
				"         \"parentLocCode\": \"IND\",\r\n" + 
				"         \"languageCode\": \"MAL\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"createdBy\": \"defaultadmin@mosip.io\",\r\n" + 
				"         \"updatedBy\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"isDeleted\": null,\r\n" + 
				"         \"code\": \"KAR\",\r\n" + 
				"         \"name\": \"Karnataka\",\r\n" + 
				"         \"hierarchyLevel\": 0,\r\n" + 
				"         \"hierarchyName\": \"Region\",\r\n" + 
				"         \"parentLocCode\": \"IND\",\r\n" + 
				"         \"languageCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"createdBy\": \"Arun\",\r\n" + 
				"         \"updatedBy\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"isDeleted\": null,\r\n" + 
				"         \"code\": \"MH\",\r\n" + 
				"         \"name\": \"Maharastra\",\r\n" + 
				"         \"hierarchyLevel\": 0,\r\n" + 
				"         \"hierarchyName\": \"Region\",\r\n" + 
				"         \"parentLocCode\": \"IND\",\r\n" + 
				"         \"languageCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"createdBy\": \"Arun\",\r\n" + 
				"         \"updatedBy\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"isDeleted\": null,\r\n" + 
				"         \"code\": \"RCH\",\r\n" + 
				"         \"name\": \"Raichur\",\r\n" + 
				"         \"hierarchyLevel\": 0,\r\n" + 
				"         \"hierarchyName\": \"Province\",\r\n" + 
				"         \"parentLocCode\": \"KAR\",\r\n" + 
				"         \"languageCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"createdBy\": \"Arun\",\r\n" + 
				"         \"updatedBy\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"isDeleted\": null,\r\n" + 
				"         \"code\": \"MSK\",\r\n" + 
				"         \"name\": \"Maski\",\r\n" + 
				"         \"hierarchyLevel\": 0,\r\n" + 
				"         \"hierarchyName\": \"City\",\r\n" + 
				"         \"parentLocCode\": \"RCH\",\r\n" + 
				"         \"languageCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"createdBy\": \"Arun\",\r\n" + 
				"         \"updatedBy\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"isDeleted\": null,\r\n" + 
				"         \"code\": \"MNC\",\r\n" + 
				"         \"name\": \"Municipal\",\r\n" + 
				"         \"hierarchyLevel\": 0,\r\n" + 
				"         \"hierarchyName\": \"LocalAuthority\",\r\n" + 
				"         \"parentLocCode\": \"MSK\",\r\n" + 
				"         \"languageCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"createdBy\": \"Arun\",\r\n" + 
				"         \"updatedBy\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"isDeleted\": null,\r\n" + 
				"         \"code\": \"584124\",\r\n" + 
				"         \"name\": \"PinCode\",\r\n" + 
				"         \"hierarchyLevel\": 0,\r\n" + 
				"         \"hierarchyName\": \"PostalCode\",\r\n" + 
				"         \"parentLocCode\": \"MSK\",\r\n" + 
				"         \"languageCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"createdBy\": \"Arun\",\r\n" + 
				"         \"updatedBy\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"isDeleted\": null,\r\n" + 
				"         \"code\": \"SIN\",\r\n" + 
				"         \"name\": \"Sindhanur\",\r\n" + 
				"         \"hierarchyLevel\": 0,\r\n" + 
				"         \"hierarchyName\": \"City\",\r\n" + 
				"         \"parentLocCode\": \"RCH\",\r\n" + 
				"         \"languageCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"createdBy\": \"Arun\",\r\n" + 
				"         \"updatedBy\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"isDeleted\": null,\r\n" + 
				"         \"code\": \"584128\",\r\n" + 
				"         \"name\": \"PinCode\",\r\n" + 
				"         \"hierarchyLevel\": 0,\r\n" + 
				"         \"hierarchyName\": \"PostalCode\",\r\n" + 
				"         \"parentLocCode\": \"SIN\",\r\n" + 
				"         \"languageCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"createdBy\": \"Arun\",\r\n" + 
				"         \"updatedBy\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"isDeleted\": null,\r\n" + 
				"         \"code\": \"KPL\",\r\n" + 
				"         \"name\": \"koppal\",\r\n" + 
				"         \"hierarchyLevel\": 0,\r\n" + 
				"         \"hierarchyName\": \"Province\",\r\n" + 
				"         \"parentLocCode\": \"KAR\",\r\n" + 
				"         \"languageCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"createdBy\": \"Arun\",\r\n" + 
				"         \"updatedBy\": null\r\n" + 
				"      }\r\n" + 
				"   ],\r\n" + 
				"   \"biometricattributes\": [\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"string\",\r\n" + 
				"         \"name\": \"string\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"biometricTypeCode\": \"0101114122\",\r\n" + 
				"         \"langCode\": \"ASG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"12345\",\r\n" + 
				"         \"name\": \"string\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"biometricTypeCode\": \"0101114122\",\r\n" + 
				"         \"langCode\": \"ASG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"0101114122\",\r\n" + 
				"         \"name\": \"string\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"biometricTypeCode\": \"0101114122\",\r\n" + 
				"         \"langCode\": \"ASG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"LTH\",\r\n" + 
				"         \"name\": \"left thumb\",\r\n" + 
				"         \"description\": \"left thumb\",\r\n" + 
				"         \"biometricTypeCode\": \"LTH\",\r\n" + 
				"         \"langCode\": \"ARB\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"acegikmoqsuwacegikmoqsuwacegikmoqsuw\",\r\n" + 
				"         \"name\": \"left thumb\",\r\n" + 
				"         \"description\": \"code length 36\",\r\n" + 
				"         \"biometricTypeCode\": \"LTH\",\r\n" + 
				"         \"langCode\": \"ARB\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"acegikmoqsuwacegikmoqsuwacegikmoqsu\",\r\n" + 
				"         \"name\": \"left thumb\",\r\n" + 
				"         \"description\": \"code length 36\",\r\n" + 
				"         \"biometricTypeCode\": \"LTH\",\r\n" + 
				"         \"langCode\": \"ARB\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"LIR\",\r\n" + 
				"         \"name\": \"abcdefghijklmnopqrstuvwxabcdefghijklmnopqrstuvwxabcdefghijklmnop\",\r\n" + 
				"         \"description\": \"code length 37\",\r\n" + 
				"         \"biometricTypeCode\": \"LIR\",\r\n" + 
				"         \"langCode\": \"ARB\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"LIR\",\r\n" + 
				"         \"name\": \"abcdefghijklmnopqrstuvwxabcdefghijklmnopqrstuvwxabcdefghijklmno\",\r\n" + 
				"         \"description\": \"name length 63\",\r\n" + 
				"         \"biometricTypeCode\": \"LIR\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      }\r\n" + 
				"   ],\r\n" + 
				"   \"biometricTypes\": [\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"01011151242\",\r\n" + 
				"         \"name\": \"Kumar\",\r\n" + 
				"         \"description\": \"Left_Finger\",\r\n" + 
				"         \"langCode\": \"ASG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"010111212426\",\r\n" + 
				"         \"name\": \"Kumar\",\r\n" + 
				"         \"description\": \"Right_finger\",\r\n" + 
				"         \"langCode\": \"ASG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"010111312426\",\r\n" + 
				"         \"name\": \"Kumar\",\r\n" + 
				"         \"description\": \"Index_Finger\",\r\n" + 
				"         \"langCode\": \"ASG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"0101111126\",\r\n" + 
				"         \"name\": \"Kumar\",\r\n" + 
				"         \"description\": \"Left_Finger\",\r\n" + 
				"         \"langCode\": \"ASG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"0101114126\",\r\n" + 
				"         \"name\": \"Kumar\",\r\n" + 
				"         \"description\": \"Middle_Finger\",\r\n" + 
				"         \"langCode\": \"ASG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"010111212226\",\r\n" + 
				"         \"name\": \"Kumar\",\r\n" + 
				"         \"description\": \"Right_finger\",\r\n" + 
				"         \"langCode\": \"ASG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"010111512226\",\r\n" + 
				"         \"name\": \"Kumar\",\r\n" + 
				"         \"description\": \"Left_Finger\",\r\n" + 
				"         \"langCode\": \"ASG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"0101111312226\",\r\n" + 
				"         \"name\": \"Kumar\",\r\n" + 
				"         \"description\": \"Left_Finger\",\r\n" + 
				"         \"langCode\": \"ASG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"0101114312226\",\r\n" + 
				"         \"name\": \"Kumar\",\r\n" + 
				"         \"description\": \"Middle_Finger\",\r\n" + 
				"         \"langCode\": \"ASG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      }\r\n" + 
				"   ],\r\n" + 
				"   \"applications\": [\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"11111586\",\r\n" + 
				"         \"name\": \"Pre_Reg\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"ard\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"11111588\",\r\n" + 
				"         \"name\": \"Pre_Reg\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"ard\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"11111593\",\r\n" + 
				"         \"name\": \"Pre_Reg\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"ard\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"11111594\",\r\n" + 
				"         \"name\": \"Pre_Reg\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"ard\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"11111592\",\r\n" + 
				"         \"name\": \"Pre_Reg\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"ard\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"11111590\",\r\n" + 
				"         \"name\": \"Pre_Reg\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"ard\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"11111595\",\r\n" + 
				"         \"name\": \"Pre_Reg\",\r\n" + 
				"         \"description\": \"string\",\r\n" + 
				"         \"langCode\": \"ard\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      }\r\n" + 
				"   ],\r\n" + 
				"   \"idTypes\": null,\r\n" + 
				"   \"titles\": [\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"LOGIN\",\r\n" + 
				"         \"titleName\": \"LoginPage\",\r\n" + 
				"         \"titleDescription\": \"login page\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": false\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"HOME\",\r\n" + 
				"         \"titleName\": \"HomePage\",\r\n" + 
				"         \"titleDescription\": \"Home page\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": false\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"REG\",\r\n" + 
				"         \"titleName\": \"RegistrationPage\",\r\n" + 
				"         \"titleDescription\": \"Registration page\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": false\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"DEMO\",\r\n" + 
				"         \"titleName\": \"DemographicPage\",\r\n" + 
				"         \"titleDescription\": \"Demographic page\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": false\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"UPLOAD\",\r\n" + 
				"         \"titleName\": \"FileUpload\",\r\n" + 
				"         \"titleDescription\": \"file upload page\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": false\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"LOGIN\",\r\n" + 
				"         \"titleName\": \"LoginPage\",\r\n" + 
				"         \"titleDescription\": \"login page\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": false\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"HOME\",\r\n" + 
				"         \"titleName\": \"HomePage\",\r\n" + 
				"         \"titleDescription\": \"Home page\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": false\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"REG\",\r\n" + 
				"         \"titleName\": \"RegistrationPage\",\r\n" + 
				"         \"titleDescription\": \"Registration page\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": false\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"DEMO\",\r\n" + 
				"         \"titleName\": \"DemographicPage\",\r\n" + 
				"         \"titleDescription\": \"Demographic page\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": false\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"UPLOAD\",\r\n" + 
				"         \"titleName\": \"FileUpload\",\r\n" + 
				"         \"titleDescription\": \"file upload page\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": false\r\n" + 
				"      }\r\n" + 
				"   ],\r\n" + 
				"   \"genders\": [\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"OTH\",\r\n" + 
				"         \"genderName\": \"OTHERS\",\r\n" + 
				"         \"langCode\": \"KAN\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"FE\",\r\n" + 
				"         \"genderName\": \"FEMALE\",\r\n" + 
				"         \"langCode\": \"KAN\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"M\",\r\n" + 
				"         \"genderName\": \"MALE\",\r\n" + 
				"         \"langCode\": \"KAN\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"m\",\r\n" + 
				"         \"genderName\": \"MALE\",\r\n" + 
				"         \"langCode\": \"ENG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"fe\",\r\n" + 
				"         \"genderName\": \"FEMALE\",\r\n" + 
				"         \"langCode\": \"ENG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"oth\",\r\n" + 
				"         \"genderName\": \"OTHERS\",\r\n" + 
				"         \"langCode\": \"ENG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"1234567890123456\",\r\n" + 
				"         \"genderName\": \"OTHERS\",\r\n" + 
				"         \"langCode\": \"ENG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"M\",\r\n" + 
				"         \"genderName\": \"MALE\",\r\n" + 
				"         \"langCode\": \"ENG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"ENG\",\r\n" + 
				"         \"genderName\": \"string\",\r\n" + 
				"         \"langCode\": \"ENG\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"M\",\r\n" + 
				"         \"genderName\": \"Male\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"jklmnopqrstuvwx\",\r\n" + 
				"         \"genderName\": \"Male\",\r\n" + 
				"         \"langCode\": \"FRE\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"jklmnopqrstuvwxy\",\r\n" + 
				"         \"genderName\": \"Male\",\r\n" + 
				"         \"langCode\": \"fre\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"M\",\r\n" + 
				"         \"genderName\": \"abcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyabcdefghijklm\",\r\n" + 
				"         \"langCode\": \"fre\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"F\",\r\n" + 
				"         \"genderName\": \"abcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyabcdefghijklmn\",\r\n" + 
				"         \"langCode\": \"fre\",\r\n" + 
				"         \"isActive\": true,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"code\": \"Fem\",\r\n" + 
				"         \"genderName\": \"Female\",\r\n" + 
				"         \"langCode\": \"arb\",\r\n" + 
				"         \"isActive\": false,\r\n" + 
				"         \"isDeleted\": null\r\n" + 
				"      }\r\n" + 
				"   ],\r\n" + 
				"   \"languages\": null\r\n" + 
				"}";

		Mockito.when(masterSyncDao.syncJobDetails(Mockito.anyString())).thenReturn(masterSyncDetails);
		
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.any())).thenReturn(masterJson);
		
		Mockito.when(objectMapper.readValue(masterSyncJson.toString(), MasterDataResponseDto.class)).thenReturn(masterSyncDt);

		Mockito.when(masterSyncDao.save(masterSyncDto)).thenReturn("");

		sucessResponse.setCode(RegistrationConstants.MASTER_SYNC_SUCESS_MSG_CODE);
		sucessResponse.setInfoType(RegistrationConstants.ALERT_INFORMATION);
		sucessResponse.setMessage(RegistrationConstants.MASTER_SYNC_SUCCESS);

		responseDTO.setSuccessResponseDTO(sucessResponse);

		ResponseDTO responseDto = masterSyncServiceImpl.getMasterSync("MDS_J00001");
		// assertEquals(RegistrationConstants.MASTER_SYNC_SUCCESS,
		// responseDto.getSuccessResponseDTO().getMessage());
	}
	@SuppressWarnings({ "static-access", "unchecked" })
	@Test
	public void testMasterSyncHttpCaseJson()
			throws RegBaseCheckedException, JsonParseException, JsonMappingException, IOException, URISyntaxException {

		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		PowerMockito.mockStatic(UriComponentsBuilder.class);
		PowerMockito.mockStatic(URI.class);
		MasterDataResponseDto masterSyncDto = new MasterDataResponseDto();
		MasterDataResponseDto masterSyncDt = new MasterDataResponseDto();
		SuccessResponseDTO sucessResponse = new SuccessResponseDTO();
		ResponseDTO responseDTO = new ResponseDTO();

		BiometricAttributeDto biometricattributes = new BiometricAttributeDto();

		BiometricAttributeDto biometricAttributeResponseDto = new BiometricAttributeDto();

		biometricattributes.setBiometricTypeCode("1");
		biometricattributes.setCode("1");
		biometricattributes.setDescription("finerprints");
		biometricattributes.setLangCode("eng");
		biometricattributes.setName("littile finger");

		List<BiometricAttributeDto> biometricattribute = new ArrayList<>();
		biometricattribute.add(biometricattributes);

		biometricAttributeResponseDto.setBiometricTypeCode("1");
		biometricAttributeResponseDto.setCode("1");
		biometricAttributeResponseDto.setDescription("finerprints");
		biometricAttributeResponseDto.setLangCode("eng");
		biometricAttributeResponseDto.setName("littile finger");

		List<BiometricAttributeDto> biometrictypes = new ArrayList<>();
		biometrictypes.add(biometricAttributeResponseDto);

		masterSyncDto.setBiometricattributes(biometrictypes);

		SyncControl masterSyncDetails = new SyncControl();

		masterSyncDetails.setSyncJobId("MDS_J00001");
		masterSyncDetails.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));
		masterSyncDetails.setCrBy("mosip");
		masterSyncDetails.setIsActive(true);
		masterSyncDetails.setLangCode("eng");
		masterSyncDetails.setCrDtime(new Timestamp(System.currentTimeMillis()));

		String masterSyncJson="";
		
        Mockito.when(masterSyncDao.syncJobDetails(Mockito.anyString())).thenReturn(masterSyncDetails);
		
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.any())).thenThrow(HttpClientErrorException.class);
		
		Mockito.when(objectMapper.readValue(masterSyncJson.toString(), MasterDataResponseDto.class)).thenReturn(masterSyncDt);

		Mockito.when(masterSyncDao.save(masterSyncDto)).thenReturn("");

		sucessResponse.setCode(RegistrationConstants.MASTER_SYNC_SUCESS_MSG_CODE);
		sucessResponse.setInfoType(RegistrationConstants.ALERT_INFORMATION);
		sucessResponse.setMessage(RegistrationConstants.MASTER_SYNC_SUCCESS);
		
		responseDTO.setSuccessResponseDTO(sucessResponse);

		ResponseDTO responseDto = masterSyncServiceImpl.getMasterSync("MDS_J00001");
		
	}
	@SuppressWarnings({ "static-access", "unchecked" })
	@Test
	public void testMasterSyncSocketCaseJson()
			throws RegBaseCheckedException, JsonParseException, JsonMappingException, IOException, URISyntaxException {

		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		PowerMockito.mockStatic(UriComponentsBuilder.class);
		PowerMockito.mockStatic(URI.class);
		MasterDataResponseDto masterSyncDto = new MasterDataResponseDto();
		MasterDataResponseDto masterSyncDt = new MasterDataResponseDto();
		SuccessResponseDTO sucessResponse = new SuccessResponseDTO();
		ResponseDTO responseDTO = new ResponseDTO();

		BiometricAttributeDto biometricattributes = new BiometricAttributeDto();

		BiometricAttributeDto biometricAttributeResponseDto = new BiometricAttributeDto();

		biometricattributes.setBiometricTypeCode("1");
		biometricattributes.setCode("1");
		biometricattributes.setDescription("finerprints");
		biometricattributes.setLangCode("eng");
		biometricattributes.setName("littile finger");

		List<BiometricAttributeDto> biometricattribute = new ArrayList<>();
		biometricattribute.add(biometricattributes);

		biometricAttributeResponseDto.setBiometricTypeCode("1");
		biometricAttributeResponseDto.setCode("1");
		biometricAttributeResponseDto.setDescription("finerprints");
		biometricAttributeResponseDto.setLangCode("eng");
		biometricAttributeResponseDto.setName("littile finger");

		List<BiometricAttributeDto> biometrictypes = new ArrayList<>();
		biometrictypes.add(biometricAttributeResponseDto);

		masterSyncDto.setBiometricattributes(biometrictypes);

		SyncControl masterSyncDetails = new SyncControl();

		masterSyncDetails.setSyncJobId("MDS_J00001");
		masterSyncDetails.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));
		masterSyncDetails.setCrBy("mosip");
		masterSyncDetails.setIsActive(true);
		masterSyncDetails.setLangCode("eng");
		masterSyncDetails.setCrDtime(new Timestamp(System.currentTimeMillis()));

		String masterSyncJson="";
		
        Mockito.when(masterSyncDao.syncJobDetails(Mockito.anyString())).thenReturn(masterSyncDetails);
		
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.any())).thenThrow(SocketTimeoutException.class);
		
		Mockito.when(objectMapper.readValue(masterSyncJson.toString(), MasterDataResponseDto.class)).thenReturn(masterSyncDt);

		Mockito.when(masterSyncDao.save(masterSyncDto)).thenReturn("");

		sucessResponse.setCode(RegistrationConstants.MASTER_SYNC_SUCESS_MSG_CODE);
		sucessResponse.setInfoType(RegistrationConstants.ALERT_INFORMATION);
		sucessResponse.setMessage(RegistrationConstants.MASTER_SYNC_SUCCESS);
		
		responseDTO.setSuccessResponseDTO(sucessResponse);

		ResponseDTO responseDto = masterSyncServiceImpl.getMasterSync("MDS_J00001");
		
	}
	
	@Test
	public void findLocationByHierarchyCode() {

		List<MasterLocation> locations = new ArrayList<>();
		MasterLocation locattion = new MasterLocation();
		locattion.setCode("LOC01");
		locattion.setName("english");
		locattion.setLanguageCode("ENG");
		locattion.setHierarchyLevel(1);
		locattion.setHierarchyName("english");
		locattion.setParentLocCode("english");
		locations.add(locattion);
		
        Mockito.when(masterSyncDao.findLocationByLangCode(Mockito.anyString(), Mockito.anyString())).thenReturn(locations);
		
        masterSyncServiceImpl.findLocationByHierarchyCode("LOC01", "ENG");
		

	}
	
	@Test
	public void findProvianceByHierarchyCode() {

		List<MasterLocation> locations = new ArrayList<>();
		MasterLocation locattion = new MasterLocation();
		locattion.setCode("LOC01");
		locattion.setName("english");
		locattion.setLanguageCode("ENG");
		locattion.setHierarchyLevel(1);
		locattion.setHierarchyName("english");
		locattion.setParentLocCode("english");
		locations.add(locattion);
		
        Mockito.when(masterSyncDao.findLocationByParentLocCode(Mockito.anyString())).thenReturn(locations);
		
        masterSyncServiceImpl.findProvianceByHierarchyCode("LOC01");
		

	}
	
	@Test
	public void findAllReasons() {

		List<MasterReasonCategory> allReason = new ArrayList<>();
		MasterReasonCategory reasons = new MasterReasonCategory();
		reasons.setCode("DEMO");
		reasons.setName("InvalidData");
		reasons.setLangCode("FRE");
		allReason.add(reasons);
		
		List<MasterReasonList> allReasonList = new ArrayList<>();
		MasterReasonList reasonList = new MasterReasonList();
		reasonList.setCode("DEMO");
		reasonList.setName("InvalidData");
		reasonList.setLangCode("FRE");
		allReasonList.add(reasonList);
		
        Mockito.when(masterSyncDao.getAllReasonCatogery()).thenReturn(allReason);
        Mockito.when(masterSyncDao.getReasonList(Mockito.anyString(), Mockito.anyList())).thenReturn(allReasonList);
		
        masterSyncServiceImpl.getAllReasonsList("ENG");
		

	}
}
