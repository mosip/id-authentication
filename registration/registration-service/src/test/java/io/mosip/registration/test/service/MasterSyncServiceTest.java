package io.mosip.registration.test.service;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.BeforeClass;
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
import io.mosip.registration.dto.mastersync.BiometricAttributeResponseDto;
import io.mosip.registration.dto.mastersync.MasterSyncDto;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.impl.MasterSyncServiceImpl;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;

/**
 * @author Sreekar Chukka
 *
 * @since 1.0.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ RegistrationAppHealthCheckUtil.class })
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
	ObjectMapper mapper;

	@Mock
	private AuditFactory auditFactory;

	private static ApplicationContext applicationContext = ApplicationContext.getInstance();

	@BeforeClass
	public static void beforeClass() {

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
		MasterSyncDto masterSyncDto = new MasterSyncDto();
		SuccessResponseDTO sucessResponse = new SuccessResponseDTO();
		ResponseDTO responseDTO = new ResponseDTO();

		BiometricAttributeDto biometricattributes = new BiometricAttributeDto();

		BiometricAttributeResponseDto biometricAttributeResponseDto = new BiometricAttributeResponseDto();

		biometricattributes.setBiometricTypeCode("1");
		biometricattributes.setCode("1");
		biometricattributes.setDescription("finerprints");
		biometricattributes.setLangCode("eng");
		biometricattributes.setName("littile finger");

		List<BiometricAttributeDto> biometricattribute = new ArrayList<>();
		biometricattribute.add(biometricattributes);

		biometricAttributeResponseDto.setBiometricattribute(biometricattribute);

		List<BiometricAttributeResponseDto> biometrictypes = new ArrayList<>();
		biometrictypes.add(biometricAttributeResponseDto);

		masterSyncDto.setBiometricattributes(biometrictypes);

		SyncControl masterSyncDetails = new SyncControl();

		masterSyncDetails.setSyncJobId("MDS_J00001");
		masterSyncDetails.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));
		masterSyncDetails.setCrBy("mosip");
		masterSyncDetails.setIsActive(true);
		masterSyncDetails.setLangCode("eng");
		masterSyncDetails.setCrDtime(new Timestamp(System.currentTimeMillis()));

		String masterJson = "{\"languages\":[{\"language\":[{\"languageCode\":\"1\",\"languageName\":\"eng\",\"languageFamily\":\"Engish\",\"nativeName\":\"Engilsh\"},{\"languageCode\":\"2\",\"languageName\":\"arb\",\"languageFamily\":\"Arab\",\"nativeName\":\"Arab\"}]}],\"biometricattributes\":[{\"biometricattribute\":[{\"code\":\"1\",\"name\":\"asdf\",\"description\":\"testing\",\"biometricTypeCode\":\"1\",\"langCode\":\"eng\"}]},{\"biometricattribute\":[{\"code\":\"2\",\"name\":\"asdf\",\"description\":\"testing\",\"biometricTypeCode\":\"1\",\"langCode\":\"eng\"}]},{\"biometricattribute\":[{\"code\":\"3\",\"name\":\"asfesr\",\"description\":\"testing2\",\"biometricTypeCode\":\"2\",\"langCode\":\"eng\"}]}],\"blacklistedwords\":[{\"word\":\"1\",\"description\":\"asdf1\",\"langCode\":\"eng\"},{\"word\":\"2\",\"description\":\"asdf2\",\"langCode\":\"eng\"},{\"word\":\"3\",\"description\":\"asdf3\",\"langCode\":\"eng\"}],\"biometrictypes\":[{\"biometrictype\":[{\"code\":\"1\",\"name\":\"fingerprints\",\"description\":\"fingerprint\",\"langCode\":\"eng\"}]},{\"biometrictype\":[{\"code\":\"2\",\"name\":\"iries\",\"description\":\"iriescapture\",\"langCode\":\"eng\"}]}],\"idtypes\":[{\"code\":\"1\",\"name\":\"PAN\",\"description\":\"pan card\",\"langCode\":\"eng\"},{\"code\":\"1\",\"name\":\"VID\",\"description\":\"voter id\",\"langCode\":\"eng\"}],\"documentcategories\":[{\"code\":\"1\",\"name\":\"POA\",\"description\":\"poaaa\",\"langCode\":\"eng\"},{\"code\":\"2\",\"name\":\"POI\",\"description\":\"porrr\",\"langCode\":\"eng\"},{\"code\":\"3\",\"name\":\"POR\",\"description\":\"pobbb\",\"langCode\":\"eng\"},{\"code\":\"4\",\"name\":\"POB\",\"description\":\"poiii\",\"langCode\":\"eng\"}],\"documenttypes\":[{\"code\":\"1\",\"name\":\"passport\",\"description\":\"passportid\",\"langCode\":\"eng\"},{\"code\":\"2\",\"name\":\"passport\",\"description\":\"passportid's\",\"langCode\":\"eng\"},{\"code\":\"3\",\"name\":\"voterid\",\"description\":\"votercards\",\"langCode\":\"eng\"},{\"code\":\"4\",\"name\":\"passport\",\"description\":\"passports\",\"langCode\":\"eng\"}],\"locations\":[{\"code\":\"1\",\"name\":\"chennai\",\"hierarchyLevel\":\"1\",\"hierarchyName\":\"Tamil Nadu\",\"parentLocCode\":\"1\",\"langCode\":\"eng\"},{\"code\":\"2\",\"name\":\"hyderabad\",\"hierarchyLevel\":\"2\",\"hierarchyName\":\"Telengana\",\"parentLocCode\":\"2\",\"langCode\":\"eng\"}],\"titles\":[{\"title\":[{\"titleCode\":\"1\",\"titleName\":\"admin\",\"titleDescription\":\"ahsasa\",\"langCode\":\"eng\"}]},{\"title\":[{\"titleCode\":\"2\",\"titleDescription\":\"asas\",\"titleName\":\"superadmin\",\"langCode\":\"eng\"}]}],\"genders\":[{\"gender\":[{\"genderCode\":\"1\",\"genderName\":\"male\",\"langCode\":\"eng\"},{\"genderCode\":\"2\",\"genderName\":\"female\",\"langCode\":\"eng\"}]},{\"gender\":[{\"genderCode\":\"1\",\"genderName\":\"female\",\"langCode\":\"eng\"}]}],\"reasonCategory\":{\"code\":\"1\",\"name\":\"rejected\",\"description\":\"rejectedfile\",\"langCode\":\"eng\",\"reasonLists\":[{\"code\":\"1\",\"name\":\"document\",\"description\":\"inavliddoc\",\"langCode\":\"eng\",\"reasonCategoryCode\":\"1\"},{\"code\":\"2\",\"name\":\"document\",\"description\":\"inavlidtype\",\"langCode\":\"eng\",\"reasonCategoryCode\":\"2\"}]}}";

		Mockito.when(masterSyncDao.getMasterSyncStatus(Mockito.anyString())).thenReturn(masterSyncDetails);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		Mockito.when(mapper.readValue(masterJson, MasterSyncDto.class)).thenReturn(masterSyncDto);

		Mockito.when(masterSyncDao.insertMasterSyncData(masterSyncDto)).thenReturn("");

		sucessResponse.setCode(RegistrationConstants.MASTER_SYNC_SUCESS_MSG_CODE);
		sucessResponse.setInfoType(RegistrationConstants.ALERT_INFORMATION);
		sucessResponse.setMessage(RegistrationConstants.MASTER_SYNC_SUCCESS);

		responseDTO.setSuccessResponseDTO(sucessResponse);

		ResponseDTO responseDto = masterSyncServiceImpl.getMasterSync("MDS_J00001");
		assertEquals(RegistrationConstants.MASTER_SYNC_SUCCESS, responseDto.getSuccessResponseDTO().getMessage());
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

}
