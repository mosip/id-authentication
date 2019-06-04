package io.mosip.registration.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
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

import io.mosip.registration.audit.AuditManagerSerivceImpl;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.MachineMappingDAO;
import io.mosip.registration.dao.MasterSyncDao;
import io.mosip.registration.dao.UserOnboardDAO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.mastersync.BiometricAttributeDto;
import io.mosip.registration.dto.mastersync.MasterDataResponseDto;
import io.mosip.registration.entity.BiometricAttribute;
import io.mosip.registration.entity.BlacklistedWords;
import io.mosip.registration.entity.DocumentType;
import io.mosip.registration.entity.Gender;
import io.mosip.registration.entity.IndividualType;
import io.mosip.registration.entity.Location;
import io.mosip.registration.entity.MachineMaster;
import io.mosip.registration.entity.ReasonCategory;
import io.mosip.registration.entity.ReasonList;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.entity.SyncTransaction;
import io.mosip.registration.entity.id.IndividualTypeId;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.jobs.SyncManager;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.operator.UserOnboardService;
import io.mosip.registration.service.remap.CenterMachineReMapService;
import io.mosip.registration.service.sync.impl.MasterSyncServiceImpl;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

/**
 * @author Sreekar Chukka
 *
 * @since 1.0.0
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({ RegistrationAppHealthCheckUtil.class, UriComponentsBuilder.class, URI.class, ApplicationContext.class })
public class MasterSyncServiceTest {
//java test
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

	@Mock
	private UserOnboardService userOnboardService;
	
	@Mock
	private UserOnboardDAO userOnboardDao;

	@Mock
	private GlobalParamService globalParamService;

	@Mock
	private AuditManagerSerivceImpl auditFactory;
	
	@Mock
	private CenterMachineReMapService centerMachineReMapService;
	
	@Mock
	private SyncManager syncManager;
	
	@Mock
	private SyncTransaction syncTransaction;
	
	@Mock
	private ApplicationContext context;
	@Mock
	private MachineMappingDAO machineMappingDAO;

	@Before
	public void beforeClass() throws URISyntaxException {
		doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(Components.class),
				Mockito.anyString(), Mockito.anyString());
		ReflectionTestUtils.setField(SessionContext.class, "sessionContext", null);
		RegistrationCenterDetailDTO centerDetailDTO = new RegistrationCenterDetailDTO();
		centerDetailDTO.setRegistrationCenterId("mosip");
		SessionContext.getInstance().getUserContext().setRegistrationCenterDetailDTO(centerDetailDTO);
		
		PowerMockito.mockStatic(ApplicationContext.class);
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

		String masterJson = "{\n" + "   \"registrationCenter\": [\n" + "      {\n" + "         \"id\": \"10011\",\n"
				+ "         \"name\": \"centre Souissi\",\n" + "         \"centerTypeCode\": \"REG\",\n"
				+ "         \"addressLine1\": \"avenue de Mohammed VI\",\n" + "         \"addressLine2\": \"Rabat\",\n"
				+ "         \"addressLine3\": \"Maroc\",\n" + "         \"latitude\": \"33.986608\",\n"
				+ "         \"longitude\": \"-6.828873\",\n" + "         \"locationCode\": \"10105\",\n"
				+ "         \"holidayLocationCode\": \"RBT\",\n" + "         \"contactPhone\": \"878691008\",\n"
				+ "         \"numberOfStations\": null,\n" + "         \"workingHours\": \"8:00:00\",\n"
				+ "         \"numberOfKiosks\": 1,\n" + "         \"perKioskProcessTime\": \"00:15:00\",\n"
				+ "         \"centerStartTime\": \"09:00:00\",\n" + "         \"centerEndTime\": \"17:00:00\",\n"
				+ "         \"timeZone\": \"GTM + 01h00) HEURE EUROPEENNE CENTRALE\",\n"
				+ "         \"contactPerson\": \"Minnie Mum\",\n" + "         \"lunchStartTime\": \"13:00:00\",\n"
				+ "         \"lunchEndTime\": \"14:00:00\",\n" + "         \"isDeleted\": null,\n"
				+ "         \"langCode\": \"fra\",\n" + "         \"isActive\": true\n" + "      }\n" + "   ]\n" + "}";

		Map<String,Object> myMap=new HashMap<>();
		myMap.put(RegistrationConstants.INITIAL_SETUP, RegistrationConstants.ENABLE);
		Map<String, String> map = new HashMap<>();
		LinkedHashMap<String, Object> responseMap=new LinkedHashMap<>();
		Map<String, String> masterSyncMap = new LinkedHashMap<>();
		masterSyncMap.put("lastSyncTime", "2019-03-27T11:07:34.408Z");
		responseMap.put("response", masterSyncMap);
		map.put(RegistrationConstants.USER_CENTER_ID, "10011");
		Mockito.when(userOnboardService.getMachineCenterId()).thenReturn(map);
		Mockito.when(globalParamService.getGlobalParams()).thenReturn(myMap);
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean(),Mockito.anyString()))
		.thenReturn(responseMap);
		Mockito.when(masterSyncDao.syncJobDetails(Mockito.anyString())).thenReturn(masterSyncDetails);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);
		
		Mockito.when(userOnboardDao.getStationID(Mockito.anyString())).thenReturn("8C-16-45-5A-5D-0D");
		
		Mockito.when(userOnboardDao.getCenterID(Mockito.anyString())).thenReturn("10003");
		
		Mockito.when(syncManager.createSyncTransaction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn(syncTransaction);

		Mockito.when(objectMapper.readValue(masterJson, MasterDataResponseDto.class)).thenReturn(masterSyncDto);

		Mockito.when(masterSyncDao.save(Mockito.any(MasterDataResponseDto.class)))
				.thenReturn(RegistrationConstants.SUCCESS);
		Mockito.when(machineMappingDAO.getMachineByName(Mockito.anyString()))
		.thenReturn(Mockito.mock(MachineMaster.class));

		sucessResponse.setCode(RegistrationConstants.MASTER_SYNC_SUCESS_MSG_CODE);
		sucessResponse.setInfoType(RegistrationConstants.ALERT_INFORMATION);
		sucessResponse.setMessage(RegistrationConstants.MASTER_SYNC_SUCCESS);

		responseDTO.setSuccessResponseDTO(sucessResponse);

		@SuppressWarnings("unused")
		ResponseDTO responseDto = masterSyncServiceImpl.getMasterSync("MDS_J00001","System");
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
		Mockito.when(machineMappingDAO.getMachineByName(Mockito.anyString()))
		.thenReturn(Mockito.mock(MachineMaster.class));

		errorResponse.setCode(RegistrationConstants.MASTER_SYNC_OFFLINE_FAILURE_MSG_CODE);
		errorResponse.setInfoType(RegistrationConstants.ERROR);
		errorResponse.setMessage(RegistrationConstants.MASTER_SYNC_OFFLINE_FAILURE_MSG);

		errorResponses.add(errorResponse);

		responseDTO.setErrorResponseDTOs(errorResponses);

		@SuppressWarnings("unused")
		ResponseDTO responseDto = masterSyncServiceImpl.getMasterSync("MDS_J00001","System");
		/*assertEquals(RegistrationConstants.MASTER_SYNC_FAILURE_MSG,
				responseDto.getErrorResponseDTOs().get(0).getMessage());*/
	}

	@SuppressWarnings({ "unchecked", "unused" })
	@Test
	public void testExpectedIOException() throws Exception {

		ResponseDTO responseDTO = new ResponseDTO();
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		MasterDataResponseDto masterSyncDto = new MasterDataResponseDto();
		String masterSyncJson = "";
		LinkedList<ErrorResponseDTO> errorResponses = new LinkedList<>();

		// Error response
		ErrorResponseDTO errorResponse = new ErrorResponseDTO();
		errorResponse.setCode(RegistrationConstants.MASTER_SYNC_FAILURE_MSG_CODE);
		errorResponse.setInfoType(RegistrationConstants.ERROR);
		errorResponse.setMessage(RegistrationConstants.MASTER_SYNC_FAILURE_MSG_INFO);

		errorResponses.add(errorResponse);

		// Adding list of error responses to response
		responseDTO.setErrorResponseDTOs(errorResponses);
		String masterJson = "{\n" + "   \"registrationCenter\": [\n" + "      {\n" + "         \"id\": \"10011\",\n"
				+ "         \"name\": \"centre Souissi\",\n" + "         \"centerTypeCode\": \"REG\",\n"
				+ "         \"addressLine1\": \"avenue de Mohammed VI\",\n" + "         \"addressLine2\": \"Rabat\",\n"
				+ "         \"addressLine3\": \"Maroc\",\n" + "         \"latitude\": \"33.986608\",\n"
				+ "         \"longitude\": \"-6.828873\",\n" + "         \"locationCode\": \"10105\",\n"
				+ "         \"holidayLocationCode\": \"RBT\",\n" + "         \"contactPhone\": \"878691008\",\n"
				+ "         \"numberOfStations\": null,\n" + "         \"workingHours\": \"8:00:00\",\n"
				+ "         \"numberOfKiosks\": 1,\n" + "         \"perKioskProcessTime\": \"00:15:00\",\n"
				+ "         \"centerStartTime\": \"09:00:00\",\n" + "         \"centerEndTime\": \"17:00:00\",\n"
				+ "         \"timeZone\": \"GTM + 01h00) HEURE EUROPEENNE CENTRALE\",\n"
				+ "         \"contactPerson\": \"Minnie Mum\",\n" + "         \"lunchStartTime\": \"13:00:00\",\n"
				+ "         \"lunchEndTime\": \"14:00:00\",\n" + "         \"isDeleted\": null,\n"
				+ "         \"langCode\": \"fra\",\n" + "         \"isActive\": true\n" + "      }\n" + "   ]\n" + "}";

		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		Mockito.when(objectMapper.readValue(masterSyncJson.toString(), MasterDataResponseDto.class))
				.thenReturn(masterSyncDto);

		Mockito.when(masterSyncDao.save(Mockito.any(MasterDataResponseDto.class)))
				.thenThrow(IOException.class);
		Mockito.when(machineMappingDAO.getMachineByName(Mockito.anyString()))
		.thenReturn(Mockito.mock(MachineMaster.class));

		when(masterSyncDao.syncJobDetails(Mockito.anyString())).thenReturn(null);
		
		masterSyncServiceImpl.getMasterSync("MDS_J00001","System");
	}

	@SuppressWarnings("unused")
	@Test
	public void testExpectedNullException() throws JsonParseException, JsonMappingException, IOException {

		ResponseDTO responseDTO = new ResponseDTO();
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		MasterDataResponseDto masterSyncDto = new MasterDataResponseDto();
		String masterSyncJson = "";
		LinkedList<ErrorResponseDTO> errorResponses = new LinkedList<>();

		// Error response
		ErrorResponseDTO errorResponse = new ErrorResponseDTO();
		errorResponse.setCode(RegistrationConstants.MASTER_SYNC_FAILURE_MSG_CODE);
		errorResponse.setInfoType(RegistrationConstants.ERROR);
		errorResponse.setMessage(RegistrationConstants.MASTER_SYNC_FAILURE_MSG_INFO);

		errorResponses.add(errorResponse);

		// Adding list of error responses to response
		responseDTO.setErrorResponseDTOs(errorResponses);
		String masterJson = "{\n" + "   \"registrationCenter\": [\n" + "      {\n" + "         \"id\": \"10011\",\n"
				+ "         \"name\": \"centre Souissi\",\n" + "         \"centerTypeCode\": \"REG\",\n"
				+ "         \"addressLine1\": \"avenue de Mohammed VI\",\n" + "         \"addressLine2\": \"Rabat\",\n"
				+ "         \"addressLine3\": \"Maroc\",\n" + "         \"latitude\": \"33.986608\",\n"
				+ "         \"longitude\": \"-6.828873\",\n" + "         \"locationCode\": \"10105\",\n"
				+ "         \"holidayLocationCode\": \"RBT\",\n" + "         \"contactPhone\": \"878691008\",\n"
				+ "         \"numberOfStations\": null,\n" + "         \"workingHours\": \"8:00:00\",\n"
				+ "         \"numberOfKiosks\": 1,\n" + "         \"perKioskProcessTime\": \"00:15:00\",\n"
				+ "         \"centerStartTime\": \"09:00:00\",\n" + "         \"centerEndTime\": \"17:00:00\",\n"
				+ "         \"timeZone\": \"GTM + 01h00) HEURE EUROPEENNE CENTRALE\",\n"
				+ "         \"contactPerson\": \"Minnie Mum\",\n" + "         \"lunchStartTime\": \"13:00:00\",\n"
				+ "         \"lunchEndTime\": \"14:00:00\",\n" + "         \"isDeleted\": null,\n"
				+ "         \"langCode\": \"fra\",\n" + "         \"isActive\": true\n" + "      }\n" + "   ]\n" + "}";

		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		Mockito.when(objectMapper.readValue(masterSyncJson.toString(), MasterDataResponseDto.class))
				.thenReturn(masterSyncDto);

		Mockito.when(masterSyncDao.save(Mockito.any(MasterDataResponseDto.class)))
				.thenReturn(RegistrationConstants.SUCCESS);
		Mockito.when(machineMappingDAO.getMachineByName(Mockito.anyString()))
		.thenReturn(Mockito.mock(MachineMaster.class));
		when(masterSyncDao.syncJobDetails(Mockito.anyString())).thenReturn(null);

		masterSyncServiceImpl.getMasterSync("MDS_J00001","System");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testExpectedRegBaseUncheckedException() throws Exception {

		ResponseDTO responseDTO = new ResponseDTO();
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		MasterDataResponseDto masterSyncDto = new MasterDataResponseDto();

		LinkedList<ErrorResponseDTO> errorResponses = new LinkedList<>();

		// Error response
		ErrorResponseDTO errorResponse = new ErrorResponseDTO();
		errorResponse.setCode(RegistrationConstants.MASTER_SYNC_FAILURE_MSG_CODE);
		errorResponse.setInfoType(RegistrationConstants.ERROR);
		errorResponse.setMessage(RegistrationConstants.MASTER_SYNC_FAILURE_MSG_INFO);

		errorResponses.add(errorResponse);

		// Adding list of error responses to response
		responseDTO.setErrorResponseDTOs(errorResponses);
		String masterJson = "{\n" + "   \"registrationCenter\": [\n" + "      {\n" + "         \"id\": \"10011\",\n"
				+ "         \"name\": \"centre Souissi\",\n" + "         \"centerTypeCode\": \"REG\",\n"
				+ "         \"addressLine1\": \"avenue de Mohammed VI\",\n" + "         \"addressLine2\": \"Rabat\",\n"
				+ "         \"addressLine3\": \"Maroc\",\n" + "         \"latitude\": \"33.986608\",\n"
				+ "         \"longitude\": \"-6.828873\",\n" + "         \"locationCode\": \"10105\",\n"
				+ "         \"holidayLocationCode\": \"RBT\",\n" + "         \"contactPhone\": \"878691008\",\n"
				+ "         \"numberOfStations\": null,\n" + "         \"workingHours\": \"8:00:00\",\n"
				+ "         \"numberOfKiosks\": 1,\n" + "         \"perKioskProcessTime\": \"00:15:00\",\n"
				+ "         \"centerStartTime\": \"09:00:00\",\n" + "         \"centerEndTime\": \"17:00:00\",\n"
				+ "         \"timeZone\": \"GTM + 01h00) HEURE EUROPEENNE CENTRALE\",\n"
				+ "         \"contactPerson\": \"Minnie Mum\",\n" + "         \"lunchStartTime\": \"13:00:00\",\n"
				+ "         \"lunchEndTime\": \"14:00:00\",\n" + "         \"isDeleted\": null,\n"
				+ "         \"langCode\": \"fra\",\n" + "         \"isActive\": true\n" + "      }\n" + "   ]\n" + "}";

		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		Mockito.when(objectMapper.readValue(masterJson, MasterDataResponseDto.class)).thenReturn(masterSyncDto);

		Mockito.when(masterSyncDao.save(Mockito.any(MasterDataResponseDto.class)))
				.thenReturn(RegistrationConstants.SUCCESS);
		when(masterSyncDao.syncJobDetails(Mockito.anyString())).thenReturn(null);
		Mockito.when(machineMappingDAO.getMachineByName(Mockito.anyString()))
		.thenReturn(Mockito.mock(MachineMaster.class));
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap(), Mockito.anyBoolean(),
				Mockito.anyString())).thenThrow(RegBaseUncheckedException.class);

		masterSyncServiceImpl.getMasterSync("MDS_J00001","System");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testExpectedRunException() throws Exception {

		ResponseDTO responseDTO = new ResponseDTO();
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		MasterDataResponseDto masterSyncDto = new MasterDataResponseDto();

		LinkedList<ErrorResponseDTO> errorResponses = new LinkedList<>();

		// Error response
		ErrorResponseDTO errorResponse = new ErrorResponseDTO();
		errorResponse.setCode(RegistrationConstants.MASTER_SYNC_FAILURE_MSG_CODE);
		errorResponse.setInfoType(RegistrationConstants.ERROR);
		errorResponse.setMessage(RegistrationConstants.MASTER_SYNC_FAILURE_MSG_INFO);

		errorResponses.add(errorResponse);

		// Adding list of error responses to response
		responseDTO.setErrorResponseDTOs(errorResponses);
		String masterJson = "{\n" + "   \"registrationCenter\": [\n" + "      {\n" + "         \"id\": \"10011\",\n"
				+ "         \"name\": \"centre Souissi\",\n" + "         \"centerTypeCode\": \"REG\",\n"
				+ "         \"addressLine1\": \"avenue de Mohammed VI\",\n" + "         \"addressLine2\": \"Rabat\",\n"
				+ "         \"addressLine3\": \"Maroc\",\n" + "         \"latitude\": \"33.986608\",\n"
				+ "         \"longitude\": \"-6.828873\",\n" + "         \"locationCode\": \"10105\",\n"
				+ "         \"holidayLocationCode\": \"RBT\",\n" + "         \"contactPhone\": \"878691008\",\n"
				+ "         \"numberOfStations\": null,\n" + "         \"workingHours\": \"8:00:00\",\n"
				+ "         \"numberOfKiosks\": 1,\n" + "         \"perKioskProcessTime\": \"00:15:00\",\n"
				+ "         \"centerStartTime\": \"09:00:00\",\n" + "         \"centerEndTime\": \"17:00:00\",\n"
				+ "         \"timeZone\": \"GTM + 01h00) HEURE EUROPEENNE CENTRALE\",\n"
				+ "         \"contactPerson\": \"Minnie Mum\",\n" + "         \"lunchStartTime\": \"13:00:00\",\n"
				+ "         \"lunchEndTime\": \"14:00:00\",\n" + "         \"isDeleted\": null,\n"
				+ "         \"langCode\": \"fra\",\n" + "         \"isActive\": true\n" + "      }\n" + "   ]\n" + "}";

		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		Mockito.when(objectMapper.readValue(masterJson, MasterDataResponseDto.class)).thenReturn(masterSyncDto);

		Mockito.when(masterSyncDao.save(Mockito.any(MasterDataResponseDto.class)))
				.thenReturn(RegistrationConstants.SUCCESS);
		Mockito.when(machineMappingDAO.getMachineByName(Mockito.anyString()))
		.thenReturn(Mockito.mock(MachineMaster.class));

		when(masterSyncDao.syncJobDetails(Mockito.anyString())).thenReturn(null);
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap(), Mockito.anyBoolean(),
				Mockito.anyString())).thenThrow(RuntimeException.class);

		masterSyncServiceImpl.getMasterSync("MDS_J00001","System");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testExpectedRegBasecheckedException() throws Exception {

		ResponseDTO responseDTO = new ResponseDTO();
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		MasterDataResponseDto masterSyncDto = new MasterDataResponseDto();

		LinkedList<ErrorResponseDTO> errorResponses = new LinkedList<>();

		// Error response
		ErrorResponseDTO errorResponse = new ErrorResponseDTO();
		errorResponse.setCode(RegistrationConstants.MASTER_SYNC_FAILURE_MSG_CODE);
		errorResponse.setInfoType(RegistrationConstants.ERROR);
		errorResponse.setMessage(RegistrationConstants.MASTER_SYNC_FAILURE_MSG_INFO);

		errorResponses.add(errorResponse);

		// Adding list of error responses to response
		responseDTO.setErrorResponseDTOs(errorResponses);
		String masterJson = "{\"languages\":[{\"language\":[{\"langCode\":\"1\",\"languageName\":\"eng\",\"languageFamily\":\"Engish\",\"nativeName\":\"Engilsh\"},{\"langCode\":\"2\",\"languageName\":\"arb\",\"languageFamily\":\"Arab\",\"nativeName\":\"Arab\"}]}],\"biometricattributes\":[{\"biometricattribute\":[{\"code\":\"1\",\"name\":\"asdf\",\"description\":\"testing\",\"biometricTypeCode\":\"1\",\"langCode\":\"eng\"}]},{\"biometricattribute\":[{\"code\":\"2\",\"name\":\"asdf\",\"description\":\"testing\",\"biometricTypeCode\":\"1\",\"langCode\":\"eng\"}]},{\"biometricattribute\":[{\"code\":\"3\",\"name\":\"asfesr\",\"description\":\"testing2\",\"biometricTypeCode\":\"2\",\"langCode\":\"eng\"}]}],\"blacklistedwords\":[{\"word\":\"1\",\"description\":\"asdf1\",\"langCode\":\"eng\"},{\"word\":\"2\",\"description\":\"asdf2\",\"langCode\":\"eng\"},{\"word\":\"3\",\"description\":\"asdf3\",\"langCode\":\"eng\"}],\"biometrictypes\":[{\"biometrictype\":[{\"code\":\"1\",\"name\":\"fingerprints\",\"description\":\"fingerprint\",\"langCode\":\"eng\"}]},{\"biometrictype\":[{\"code\":\"2\",\"name\":\"iries\",\"description\":\"iriescapture\",\"langCode\":\"eng\"}]}],\"idtypes\":[{\"code\":\"1\",\"name\":\"PAN\",\"description\":\"pan card\",\"langCode\":\"eng\"},{\"code\":\"1\",\"name\":\"VID\",\"description\":\"voter id\",\"langCode\":\"eng\"}],\"documentcategories\":[{\"code\":\"1\",\"name\":\"POA\",\"description\":\"poaaa\",\"langCode\":\"eng\"},{\"code\":\"2\",\"name\":\"POI\",\"description\":\"porrr\",\"langCode\":\"eng\"},{\"code\":\"3\",\"name\":\"POR\",\"description\":\"pobbb\",\"langCode\":\"eng\"},{\"code\":\"4\",\"name\":\"POB\",\"description\":\"poiii\",\"langCode\":\"eng\"}],\"documenttypes\":[{\"code\":\"1\",\"name\":\"passport\",\"description\":\"passportid\",\"langCode\":\"eng\"},{\"code\":\"2\",\"name\":\"passport\",\"description\":\"passportid's\",\"langCode\":\"eng\"},{\"code\":\"3\",\"name\":\"voterid\",\"description\":\"votercards\",\"langCode\":\"eng\"},{\"code\":\"4\",\"name\":\"passport\",\"description\":\"passports\",\"langCode\":\"eng\"}],\"locations\":[{\"code\":\"1\",\"name\":\"chennai\",\"hierarchyLevel\":\"1\",\"hierarchyName\":\"Tamil Nadu\",\"parentLocCode\":\"1\",\"langCode\":\"eng\"},{\"code\":\"2\",\"name\":\"hyderabad\",\"hierarchyLevel\":\"2\",\"hierarchyName\":\"Telengana\",\"parentLocCode\":\"2\",\"langCode\":\"eng\"}],\"titles\":[{\"title\":[{\"code\":\"1\",\"titleName\":\"admin\",\"titleDescription\":\"ahsasa\",\"langCode\":\"eng\"}]},{\"title\":[{\"code\":\"2\",\"titleDescription\":\"asas\",\"titleName\":\"superadmin\",\"langCode\":\"eng\"}]}],\"genders\":[{\"gender\":[{\"genderCode\":\"1\",\"genderName\":\"male\",\"langCode\":\"eng\"},{\"genderCode\":\"2\",\"genderName\":\"female\",\"langCode\":\"eng\"}]},{\"gender\":[{\"genderCode\":\"1\",\"genderName\":\"female\",\"langCode\":\"eng\"}]}],\"reasonCategory\":{\"code\":\"1\",\"name\":\"rejected\",\"description\":\"rejectedfile\",\"langCode\":\"eng\",\"reasonLists\":[{\"code\":\"1\",\"name\":\"document\",\"description\":\"inavliddoc\",\"langCode\":\"eng\",\"reasonCategoryCode\":\"1\"},{\"code\":\"2\",\"name\":\"document\",\"description\":\"inavlidtype\",\"langCode\":\"eng\",\"reasonCategoryCode\":\"2\"}]}}";

		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		Mockito.when(objectMapper.readValue(masterJson, MasterDataResponseDto.class)).thenReturn(masterSyncDto);

		Mockito.when(masterSyncDao.save(Mockito.any(MasterDataResponseDto.class)))
				.thenReturn(RegistrationConstants.SUCCESS);
		when(masterSyncDao.syncJobDetails(Mockito.anyString())).thenReturn(null);
		Mockito.when(machineMappingDAO.getMachineByName(Mockito.anyString()))
		.thenReturn(Mockito.mock(MachineMaster.class));
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap(), Mockito.anyBoolean(),
				Mockito.anyString())).thenThrow(RegBaseCheckedException.class);

		masterSyncServiceImpl.getMasterSync("MDS_J00001","System");
	}

	@SuppressWarnings("unused")
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

		String masterSyncJson = "";

		String masterJson = "{\n" + "   \"registrationCenter\": [\n" + "      {\n" + "         \"id\": \"10011\",\n"
				+ "         \"name\": \"centre Souissi\",\n" + "         \"centerTypeCode\": \"REG\",\n"
				+ "         \"addressLine1\": \"avenue de Mohammed VI\",\n" + "         \"addressLine2\": \"Rabat\",\n"
				+ "         \"addressLine3\": \"Maroc\",\n" + "         \"latitude\": \"33.986608\",\n"
				+ "         \"longitude\": \"-6.828873\",\n" + "         \"locationCode\": \"10105\",\n"
				+ "         \"holidayLocationCode\": \"RBT\",\n" + "         \"contactPhone\": \"878691008\",\n"
				+ "         \"numberOfStations\": null,\n" + "         \"workingHours\": \"8:00:00\",\n"
				+ "         \"numberOfKiosks\": 1,\n" + "         \"perKioskProcessTime\": \"00:15:00\",\n"
				+ "         \"centerStartTime\": \"09:00:00\",\n" + "         \"centerEndTime\": \"17:00:00\",\n"
				+ "         \"timeZone\": \"GTM + 01h00) HEURE EUROPEENNE CENTRALE\",\n"
				+ "         \"contactPerson\": \"Minnie Mum\",\n" + "         \"lunchStartTime\": \"13:00:00\",\n"
				+ "         \"lunchEndTime\": \"14:00:00\",\n" + "         \"isDeleted\": null,\n"
				+ "         \"langCode\": \"fra\",\n" + "         \"isActive\": true\n" + "      }\n" + "   ]\n" + "}";

		Mockito.when(masterSyncDao.syncJobDetails(Mockito.anyString())).thenReturn(masterSyncDetails);

		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean(),Mockito.anyString()))
				.thenReturn(masterJson);

		Mockito.when(objectMapper.readValue(masterSyncJson.toString(), MasterDataResponseDto.class))
				.thenReturn(masterSyncDt);

		Mockito.when(masterSyncDao.save(Mockito.any(MasterDataResponseDto.class)))
				.thenReturn(RegistrationConstants.SUCCESS);
		Mockito.when(machineMappingDAO.getMachineByName(Mockito.anyString()))
		.thenReturn(Mockito.mock(MachineMaster.class));

		sucessResponse.setCode(RegistrationConstants.MASTER_SYNC_SUCESS_MSG_CODE);
		sucessResponse.setInfoType(RegistrationConstants.ALERT_INFORMATION);
		sucessResponse.setMessage(RegistrationConstants.MASTER_SYNC_SUCCESS);

		responseDTO.setSuccessResponseDTO(sucessResponse);

		ResponseDTO responseDto = masterSyncServiceImpl.getMasterSync("MDS_J00001","System");
		// assertEquals(RegistrationConstants.MASTER_SYNC_SUCCESS,
		// responseDto.getSuccessResponseDTO().getMessage());
	}

	@SuppressWarnings({ "unchecked", "unused" })
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

		String masterSyncJson = "";

		Mockito.when(machineMappingDAO.getMachineByName(Mockito.anyString()))
				.thenReturn(Mockito.mock(MachineMaster.class));
		Mockito.when(masterSyncDao.syncJobDetails(Mockito.anyString())).thenReturn(masterSyncDetails);

		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean(),Mockito.anyString()))
				.thenThrow(HttpClientErrorException.class);

		Mockito.when(objectMapper.readValue(masterSyncJson.toString(), MasterDataResponseDto.class))
				.thenReturn(masterSyncDt);

		Mockito.when(masterSyncDao.save(Mockito.any(MasterDataResponseDto.class)))
				.thenReturn(RegistrationConstants.SUCCESS);

		sucessResponse.setCode(RegistrationConstants.MASTER_SYNC_SUCESS_MSG_CODE);
		sucessResponse.setInfoType(RegistrationConstants.ALERT_INFORMATION);
		sucessResponse.setMessage(RegistrationConstants.MASTER_SYNC_SUCCESS);

		responseDTO.setSuccessResponseDTO(sucessResponse);

		ResponseDTO responseDto = masterSyncServiceImpl.getMasterSync("MDS_J00001","System");

	}

	@SuppressWarnings({ "unchecked", "unused" })
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

		String masterSyncJson = "";

		Mockito.when(masterSyncDao.syncJobDetails(Mockito.anyString())).thenReturn(masterSyncDetails);

		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean(),Mockito.anyString()))
				.thenThrow(SocketTimeoutException.class);

		Mockito.when(objectMapper.readValue(masterSyncJson.toString(), MasterDataResponseDto.class))
				.thenReturn(masterSyncDt);

		Mockito.when(masterSyncDao.save(Mockito.any(MasterDataResponseDto.class)))
				.thenReturn(RegistrationConstants.SUCCESS);
		Mockito.when(machineMappingDAO.getMachineByName(Mockito.anyString()))
		.thenReturn(Mockito.mock(MachineMaster.class));

		sucessResponse.setCode(RegistrationConstants.MASTER_SYNC_SUCESS_MSG_CODE);
		sucessResponse.setInfoType(RegistrationConstants.ALERT_INFORMATION);
		sucessResponse.setMessage(RegistrationConstants.MASTER_SYNC_SUCCESS);

		responseDTO.setSuccessResponseDTO(sucessResponse);

		ResponseDTO responseDto = masterSyncServiceImpl.getMasterSync("MDS_J00001","System");

	}

	@Test
	public void findLocationByHierarchyCode() {

		List<Location> locations = new ArrayList<>();
		Location locattion = new Location();
		locattion.setCode("LOC01");
		locattion.setName("english");
		locattion.setLangCode("ENG");
		locattion.setHierarchyLevel(1);
		locattion.setHierarchyName("english");
		locattion.setParentLocCode("english");
		locations.add(locattion);

		Mockito.when(masterSyncDao.findLocationByLangCode(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(locations);

		masterSyncServiceImpl.findLocationByHierarchyCode("LOC01", "ENG");

	}

	@Test
	public void findProvianceByHierarchyCode() {

		List<Location> locations = new ArrayList<>();
		Location locattion = new Location();
		locattion.setCode("LOC01");
		locattion.setName("english");
		locattion.setLangCode("ENG");
		locattion.setHierarchyLevel(1);
		locattion.setHierarchyName("english");
		locattion.setParentLocCode("english");
		locations.add(locattion);

		Mockito.when(masterSyncDao.findLocationByParentLocCode(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(locations);

		masterSyncServiceImpl.findProvianceByHierarchyCode("LOC01", "eng");

	}

	@SuppressWarnings("unchecked")
	@Test
	public void findAllReasons() {

		List<ReasonCategory> allReason = new ArrayList<>();
		ReasonCategory reasons = new ReasonCategory();
		reasons.setCode("DEMO");
		reasons.setName("InvalidData");
		reasons.setLangCode("FRE");
		allReason.add(reasons);

		List<ReasonList> allReasonList = new ArrayList<>();
		ReasonList reasonList = new ReasonList();
		reasonList.setCode("DEMO");
		reasonList.setName("InvalidData");
		reasonList.setLangCode("FRE");
		allReasonList.add(reasonList);

		Mockito.when(masterSyncDao.getAllReasonCatogery(Mockito.anyString())).thenReturn(allReason);
		Mockito.when(masterSyncDao.getReasonList(Mockito.anyString(), Mockito.anyList())).thenReturn(allReasonList);

		masterSyncServiceImpl.getAllReasonsList("ENG");

	}

	@Test
	public void findAllBlackWords() {

		List<BlacklistedWords> allBlackWords = new ArrayList<>();
		BlacklistedWords blackWord = new BlacklistedWords();
		blackWord.setWord("asdfg");
		blackWord.setDescription("asdfg");
		blackWord.setLangCode("ENG");
		allBlackWords.add(blackWord);
		allBlackWords.add(blackWord);

		Mockito.when(masterSyncDao.getBlackListedWords(Mockito.anyString())).thenReturn(allBlackWords);

		masterSyncServiceImpl.getAllBlackListedWords("ENG");

	}

	@SuppressWarnings("unchecked")
	@Test
	public void findDocumentCategories() {

		List<DocumentType> documents = new ArrayList<>();
		DocumentType document = new DocumentType();
		document.setName("Aadhar");
		document.setDescription("Aadhar card");
		document.setLangCode("ENG");
		documents.add(document);
		documents.add(document);
		List<String> validDocuments = new ArrayList<>();
		validDocuments.add("CLR");
		// validDocuments.add("MNA");
		Mockito.when(masterSyncDao.getDocumentTypes(Mockito.anyList(), Mockito.anyString())).thenReturn(documents);

		masterSyncServiceImpl.getDocumentCategories("ENG", "Test");

	}

	@Test
	public void findGender() {

		List<Gender> genderList = new ArrayList<>();
		Gender gender = new Gender();
		gender.setCode("1");
		gender.setGenderName("male");
		gender.setLangCode("ENG");
		gender.setIsActive(true);
		genderList.add(gender);

		Mockito.when(masterSyncDao.getGenderDtls(Mockito.anyString())).thenReturn(genderList);

		masterSyncServiceImpl.getGenderDtls("ENG");

	}

	@Test
	public void findIndividualType() {

		List<IndividualType> masterIndividualType = new ArrayList<>();
		IndividualType individualTypeEntity = new IndividualType();
		IndividualTypeId individualTypeId = new IndividualTypeId();
		individualTypeId.setCode("NFR");
		individualTypeId.setLangCode("eng");
		individualTypeEntity.setIndividualTypeId(individualTypeId);
		individualTypeEntity.setName("National");
		individualTypeEntity.setIsActive(true);
		masterIndividualType.add(individualTypeEntity);

		Mockito.when(masterSyncDao.getIndividulType(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(masterIndividualType);

		assertEquals("NFR", masterSyncServiceImpl.getIndividualType("NFR", "eng").get(0).getCode());
	}
	
	@SuppressWarnings("unused")
	@Test
	public void testMasterSyncSucessFail()
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

		String masterJson = "{\n" + "   \"registrationCenter\": [\n" + "      {\n" + "         \"id\": \"10011\",\n"
				+ "         \"name\": \"centre Souissi\",\n" + "         \"centerTypeCode\": \"REG\",\n"
				+ "         \"addressLine1\": \"avenue de Mohammed VI\",\n" + "         \"addressLine2\": \"Rabat\",\n"
				+ "         \"addressLine3\": \"Maroc\",\n" + "         \"latitude\": \"33.986608\",\n"
				+ "         \"longitude\": \"-6.828873\",\n" + "         \"locationCode\": \"10105\",\n"
				+ "         \"holidayLocationCode\": \"RBT\",\n" + "         \"contactPhone\": \"878691008\",\n"
				+ "         \"numberOfStations\": null,\n" + "         \"workingHours\": \"8:00:00\",\n"
				+ "         \"numberOfKiosks\": 1,\n" + "         \"perKioskProcessTime\": \"00:15:00\",\n"
				+ "         \"centerStartTime\": \"09:00:00\",\n" + "         \"centerEndTime\": \"17:00:00\",\n"
				+ "         \"timeZone\": \"GTM + 01h00) HEURE EUROPEENNE CENTRALE\",\n"
				+ "         \"contactPerson\": \"Minnie Mum\",\n" + "         \"lunchStartTime\": \"13:00:00\",\n"
				+ "         \"lunchEndTime\": \"14:00:00\",\n" + "         \"isDeleted\": null,\n"
				+ "         \"langCode\": \"fra\",\n" + "         \"isActive\": true\n" + "      }\n" + "   ]\n" + "}";
        List<Map<String,String>> temp=new ArrayList<>();
		Map<String, String> map = new LinkedHashMap<>();
		LinkedHashMap<String, Object> responseMap=new LinkedHashMap<>();
		map.put("errorMessage", "Mac-Address and/or Serial Number does not exist");
		temp.add(map);
		responseMap.put("errors", temp);
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean(),Mockito.anyString()))
		.thenReturn(responseMap);
		Mockito.when(masterSyncDao.syncJobDetails(Mockito.anyString())).thenReturn(masterSyncDetails);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		Mockito.when(objectMapper.readValue(masterJson, MasterDataResponseDto.class)).thenReturn(masterSyncDto);

		Mockito.when(masterSyncDao.save(Mockito.any(MasterDataResponseDto.class)))
				.thenReturn(RegistrationConstants.SUCCESS);
		Mockito.when(machineMappingDAO.getMachineByName(Mockito.anyString()))
		.thenReturn(Mockito.mock(MachineMaster.class));

		sucessResponse.setCode(RegistrationConstants.MASTER_SYNC_SUCESS_MSG_CODE);
		sucessResponse.setInfoType(RegistrationConstants.ALERT_INFORMATION);
		sucessResponse.setMessage(RegistrationConstants.MASTER_SYNC_SUCCESS);

		responseDTO.setSuccessResponseDTO(sucessResponse);

		ResponseDTO responseDto = masterSyncServiceImpl.getMasterSync("MDS_J00001","System");
		// assertEquals(RegistrationConstants.MASTER_SYNC_SUCCESS,
		// responseDto.getSuccessResponseDTO().getMessage());
	}
	
	@SuppressWarnings("unused")
	@Test
	public void testMasterSyncSucessFailure()
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

		String masterJson = "{\n" + "   \"registrationCenter\": [\n" + "      {\n" + "         \"id\": \"10011\",\n"
				+ "         \"name\": \"centre Souissi\",\n" + "         \"centerTypeCode\": \"REG\",\n"
				+ "         \"addressLine1\": \"avenue de Mohammed VI\",\n" + "         \"addressLine2\": \"Rabat\",\n"
				+ "         \"addressLine3\": \"Maroc\",\n" + "         \"latitude\": \"33.986608\",\n"
				+ "         \"longitude\": \"-6.828873\",\n" + "         \"locationCode\": \"10105\",\n"
				+ "         \"holidayLocationCode\": \"RBT\",\n" + "         \"contactPhone\": \"878691008\",\n"
				+ "         \"numberOfStations\": null,\n" + "         \"workingHours\": \"8:00:00\",\n"
				+ "         \"numberOfKiosks\": 1,\n" + "         \"perKioskProcessTime\": \"00:15:00\",\n"
				+ "         \"centerStartTime\": \"09:00:00\",\n" + "         \"centerEndTime\": \"17:00:00\",\n"
				+ "         \"timeZone\": \"GTM + 01h00) HEURE EUROPEENNE CENTRALE\",\n"
				+ "         \"contactPerson\": \"Minnie Mum\",\n" + "         \"lunchStartTime\": \"13:00:00\",\n"
				+ "         \"lunchEndTime\": \"14:00:00\",\n" + "         \"isDeleted\": null,\n"
				+ "         \"langCode\": \"fra\",\n" + "         \"isActive\": true\n" + "      }\n" + "   ]\n" + "}";

		Map<String, String> map = new HashMap<>();
		LinkedHashMap<String, Object> responseMap=new LinkedHashMap<>();
		Map<String, String> masterSyncMap = new LinkedHashMap<>();
		masterSyncMap.put("lastSyncTime", "2019-03-27T11:07:34.408Z");
		responseMap.put("response", masterSyncMap);
		map.put(RegistrationConstants.USER_CENTER_ID, "10011");
		Mockito.when(userOnboardService.getMachineCenterId()).thenReturn(map);
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean(),Mockito.anyString()))
		.thenReturn(responseMap);
		Mockito.when(masterSyncDao.syncJobDetails(Mockito.anyString())).thenReturn(masterSyncDetails);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		Mockito.when(objectMapper.readValue(masterJson, MasterDataResponseDto.class)).thenReturn(masterSyncDto);

		Mockito.when(masterSyncDao.save(Mockito.any(MasterDataResponseDto.class)))
				.thenReturn(RegistrationConstants.FAILURE);
		Mockito.when(machineMappingDAO.getMachineByName(Mockito.anyString()))
		.thenReturn(Mockito.mock(MachineMaster.class));

		sucessResponse.setCode(RegistrationConstants.MASTER_SYNC_SUCESS_MSG_CODE);
		sucessResponse.setInfoType(RegistrationConstants.ALERT_INFORMATION);
		sucessResponse.setMessage(RegistrationConstants.MASTER_SYNC_SUCCESS);

		responseDTO.setSuccessResponseDTO(sucessResponse);

		ResponseDTO responseDto = masterSyncServiceImpl.getMasterSync("MDS_J00001","System");
		// assertEquals(RegistrationConstants.MASTER_SYNC_SUCCESS,
		// responseDto.getSuccessResponseDTO().getMessage());
	}
	
	@SuppressWarnings("unused")
	@Test
	public void centerRemap() throws HttpClientErrorException, SocketTimeoutException, RegBaseCheckedException {

		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Map<String, Object> myMap = new HashMap<>();
		myMap.put(RegistrationConstants.INITIAL_SETUP, RegistrationConstants.ENABLE);

		SyncControl masterSyncDetails = new SyncControl();
		masterSyncDetails.setSyncJobId("MDS_J00001");
		masterSyncDetails.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));
		masterSyncDetails.setCrBy("mosip");
		masterSyncDetails.setIsActive(true);
		masterSyncDetails.setLangCode("eng");
		masterSyncDetails.setCrDtime(new Timestamp(System.currentTimeMillis()));

		LinkedHashMap<String, Object> mainMap = new LinkedHashMap<>();

		Map<String, Object> masterDetailsMap = new LinkedHashMap<>();
		masterDetailsMap.put("errorCode", "KER-SNC-303");
		masterDetailsMap.put("message", "Registration Center has been updated for the received Machine ID");
		List<Map<String, Object>> masterFailureList = new ArrayList<>();
		masterFailureList.add(masterDetailsMap);
		mainMap.put(RegistrationConstants.ERRORS, masterFailureList);

		Mockito.when(masterSyncDao.syncJobDetails(Mockito.anyString())).thenReturn(masterSyncDetails);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);
		Mockito.when(userOnboardDao.getStationID(Mockito.anyString())).thenReturn("8C-16-45-5A-5D-0D");
		Mockito.when(userOnboardDao.getCenterID(Mockito.anyString())).thenReturn("10003");
		Mockito.when(globalParamService.getGlobalParams()).thenReturn(myMap);
		doNothing().when(globalParamService).update(Mockito.anyString(), Mockito.anyString());
		doNothing().when(centerMachineReMapService).startRemapProcess();
		Mockito.when(
				serviceDelegateUtil.get(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyString()))
				.thenReturn(mainMap);
		Mockito.when(machineMappingDAO.getMachineByName(Mockito.anyString()))
				.thenReturn(Mockito.mock(MachineMaster.class));

		ResponseDTO responseDto = masterSyncServiceImpl.getMasterSync("MDS_J00001", "System");
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void getBiometricTypeWithFingerprintEnable() {
		List<String> biometricType = new LinkedList<>(Arrays.asList(RegistrationConstants.FNR, RegistrationConstants.IRS));
		Map<String, Object> appMap = new HashMap<>();
		appMap.put(RegistrationConstants.FINGERPRINT_DISABLE_FLAG, "Y");
		when(context.map()).thenReturn(appMap);
		
		List<BiometricAttribute> biometricAttributes = new ArrayList<>();
		BiometricAttribute biometricAttribute = new BiometricAttribute();
		biometricAttribute.setCode("RS");
		biometricAttribute.setBiometricTypeCode("FNR");
		biometricAttribute.setName("Right Slap");
		biometricAttribute.setLangCode("eng");
		biometricAttributes.add(biometricAttribute);
		
		Mockito.when(masterSyncDao.getBiometricType("eng", biometricType)).thenReturn(biometricAttributes);	
		List<BiometricAttributeDto> biometricAttributeDtos = masterSyncServiceImpl.getBiometricType("eng");
		assertNotNull(biometricAttributeDtos);
		
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void getBiometricTypeWithIrisEnable() {
		List<String> biometricType = new LinkedList<>(Arrays.asList(RegistrationConstants.FNR, RegistrationConstants.IRS));
		Map<String, Object> appMap = new HashMap<>();
		appMap.put(RegistrationConstants.IRIS_DISABLE_FLAG, "Y");
		when(context.map()).thenReturn(appMap);
		
		List<BiometricAttribute> biometricAttributes = new ArrayList<>();
		BiometricAttribute biometricAttribute = new BiometricAttribute();
		biometricAttribute.setCode("RS");
		biometricAttribute.setBiometricTypeCode("FNR");
		biometricAttribute.setName("Right Slap");
		biometricAttribute.setLangCode("eng");
		biometricAttributes.add(biometricAttribute);
		
		Mockito.when(masterSyncDao.getBiometricType("eng", biometricType)).thenReturn(biometricAttributes);		
		List<BiometricAttributeDto> biometricAttributeDtos = masterSyncServiceImpl.getBiometricType("eng");
		assertNotNull(biometricAttributeDtos);
		
	}

	@SuppressWarnings("static-access")
	@Test
	public void getBiometricTypeWithFingerprintDisble() {
		List<String> biometricType = new LinkedList<>(Arrays.asList(RegistrationConstants.FNR, RegistrationConstants.IRS));
		Map<String, Object> appMap = new HashMap<>();
		appMap.put(RegistrationConstants.FINGERPRINT_DISABLE_FLAG, "N");
		when(context.map()).thenReturn(appMap);
		biometricType.remove(RegistrationConstants.FNR);
		
		List<BiometricAttribute> biometricAttributes = new ArrayList<>();
		BiometricAttribute biometricAttribute = new BiometricAttribute();
		biometricAttribute.setCode("RS");
		biometricAttribute.setBiometricTypeCode("FNR");
		biometricAttribute.setName("Right Slap");
		biometricAttribute.setLangCode("eng");
		biometricAttributes.add(biometricAttribute);
		
		Mockito.when(masterSyncDao.getBiometricType("eng", biometricType)).thenReturn(biometricAttributes);	
		List<BiometricAttributeDto> biometricAttributeDtos = masterSyncServiceImpl.getBiometricType("eng");
		assertNotNull(biometricAttributeDtos);
		
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void getBiometricTypeWithIrisDisble() {
		List<String> biometricType = new LinkedList<>(Arrays.asList(RegistrationConstants.FNR, RegistrationConstants.IRS));
		Map<String, Object> appMap = new HashMap<>();
		appMap.put(RegistrationConstants.IRIS_DISABLE_FLAG, "N");
		when(context.map()).thenReturn(appMap);
		biometricType.remove(RegistrationConstants.IRS);
		
		List<BiometricAttribute> biometricAttributes = new ArrayList<>();
		BiometricAttribute biometricAttribute = new BiometricAttribute();
		biometricAttribute.setCode("RS");
		biometricAttribute.setBiometricTypeCode("FNR");
		biometricAttribute.setName("Right Slap");
		biometricAttribute.setLangCode("eng");
		biometricAttributes.add(biometricAttribute);
		
		Mockito.when(masterSyncDao.getBiometricType("eng", biometricType)).thenReturn(biometricAttributes);		
		List<BiometricAttributeDto> biometricAttributeDtos = masterSyncServiceImpl.getBiometricType("eng");
		assertNotNull(biometricAttributeDtos);
		
	}

	@Test
	public void getRequestParamsOnlyMacIdTest() {
		PowerMockito.mockStatic(ApplicationContext.class);

		Mockito.when(machineMappingDAO.getMachineByName(Mockito.anyString()))
				.thenReturn(Mockito.mock(MachineMaster.class));
		Mockito.when(masterSyncDao.syncJobDetails(Mockito.anyString())).thenReturn(null);

		Map<String, String> requestParams = ReflectionTestUtils.invokeMethod(masterSyncServiceImpl, "getRequestParams",
				"macId", null);

		Assert.assertTrue(requestParams.containsKey(RegistrationConstants.MAC_ADDRESS));
		Assert.assertEquals(1, requestParams.size());
	}

	@Test
	public void getRequestParamsKeyIndexContextTest() throws Exception {
		PowerMockito.mockStatic(ApplicationContext.class);
		Map<String, Object> applicationContext = new HashMap<>();
		applicationContext.put(RegistrationConstants.INITIAL_SETUP, RegistrationConstants.ENABLE);

		PowerMockito.doReturn(applicationContext).when(ApplicationContext.class, "map");
		PowerMockito.when(masterSyncDao.syncJobDetails(Mockito.anyString())).thenReturn(null);

		Map<String, String> requestParams = ReflectionTestUtils.invokeMethod(masterSyncServiceImpl, "getRequestParams",
				"macId", "keyIndex");

		Assert.assertTrue(requestParams.containsKey(RegistrationConstants.KEY_INDEX.toLowerCase()));
		Assert.assertEquals(2, requestParams.size());

	}

	@Test
	public void getRequestParamsKeyIndexServiceTest() throws Exception {
		PowerMockito.mockStatic(ApplicationContext.class);
		Map<String, Object> applicationContext = new HashMap<>();
		MachineMaster machineMaster = new MachineMaster();
		machineMaster.setKeyIndex("keyIndex");

		PowerMockito.doReturn(applicationContext).when(ApplicationContext.class, "map");
		PowerMockito.when(machineMappingDAO.getMachineByName(Mockito.anyString())).thenReturn(machineMaster);

		Map<String, String> requestParams = ReflectionTestUtils.invokeMethod(masterSyncServiceImpl, "getRequestParams",
				"masterSync", null);

		Assert.assertTrue(requestParams.containsKey(RegistrationConstants.KEY_INDEX.toLowerCase()));
		Assert.assertEquals(2, requestParams.size());

	}

	@Test
	public void getRequestParamsUpdatedDateTest() throws Exception {
		PowerMockito.mockStatic(ApplicationContext.class);
		Map<String, Object> applicationContext = new HashMap<>();
		applicationContext.put(RegistrationConstants.TPM_AVAILABILITY, RegistrationConstants.ENABLE);
		MachineMaster machineMaster = new MachineMaster();
		machineMaster.setKeyIndex("keyIndex");
		SyncControl syncControl = new SyncControl();
		syncControl.setLastSyncDtimes(Mockito.mock(Timestamp.class));

		PowerMockito.doReturn(applicationContext).when(ApplicationContext.class, "map");
		PowerMockito.when(machineMappingDAO.getMachineByName(Mockito.anyString())).thenReturn(machineMaster);
		PowerMockito.when(masterSyncDao.syncJobDetails(Mockito.anyString())).thenReturn(syncControl);

		Map<String, String> requestParams = ReflectionTestUtils.invokeMethod(masterSyncServiceImpl, "getRequestParams",
				null, null);

		Assert.assertTrue(requestParams.containsKey(RegistrationConstants.KEY_INDEX.toLowerCase()));
		Assert.assertTrue(requestParams.containsKey(RegistrationConstants.MASTER_DATA_LASTUPDTAE));
		Assert.assertEquals(3, requestParams.size());

	}

	@Test
	public void getMasterSyncWithKeyIndexExceptionTest() throws Exception {
		PowerMockito.mockStatic(ApplicationContext.class);

		masterSyncServiceImpl.getMasterSync("MDS_J00001","System", "keyIndex");
		
	}

	@Test
	public void getMasterSyncWithoutKeyIndexExceptionTest() throws Exception {
		PowerMockito.mockStatic(ApplicationContext.class);

		masterSyncServiceImpl.getMasterSync("MDS_J00001","System");
		
	}

	@Test
	public void testMasterSyncWithKeyIndexSucessCaseJson() throws Exception {
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

		String masterSyncJson = "";

		String masterJson = "{\n" + "   \"registrationCenter\": [\n" + "      {\n" + "         \"id\": \"10011\",\n"
				+ "         \"name\": \"centre Souissi\",\n" + "         \"centerTypeCode\": \"REG\",\n"
				+ "         \"addressLine1\": \"avenue de Mohammed VI\",\n" + "         \"addressLine2\": \"Rabat\",\n"
				+ "         \"addressLine3\": \"Maroc\",\n" + "         \"latitude\": \"33.986608\",\n"
				+ "         \"longitude\": \"-6.828873\",\n" + "         \"locationCode\": \"10105\",\n"
				+ "         \"holidayLocationCode\": \"RBT\",\n" + "         \"contactPhone\": \"878691008\",\n"
				+ "         \"numberOfStations\": null,\n" + "         \"workingHours\": \"8:00:00\",\n"
				+ "         \"numberOfKiosks\": 1,\n" + "         \"perKioskProcessTime\": \"00:15:00\",\n"
				+ "         \"centerStartTime\": \"09:00:00\",\n" + "         \"centerEndTime\": \"17:00:00\",\n"
				+ "         \"timeZone\": \"GTM + 01h00) HEURE EUROPEENNE CENTRALE\",\n"
				+ "         \"contactPerson\": \"Minnie Mum\",\n" + "         \"lunchStartTime\": \"13:00:00\",\n"
				+ "         \"lunchEndTime\": \"14:00:00\",\n" + "         \"isDeleted\": null,\n"
				+ "         \"langCode\": \"fra\",\n" + "         \"isActive\": true\n" + "      }\n" + "   ]\n" + "}";

		Mockito.when(masterSyncDao.syncJobDetails(Mockito.anyString())).thenReturn(masterSyncDetails);

		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		Mockito.when(
				serviceDelegateUtil.get(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyString()))
				.thenReturn(masterJson);

		Mockito.when(objectMapper.readValue(masterSyncJson.toString(), MasterDataResponseDto.class))
				.thenReturn(masterSyncDt);

		Mockito.when(masterSyncDao.save(Mockito.any(MasterDataResponseDto.class)))
				.thenReturn(RegistrationConstants.SUCCESS);
		Mockito.when(machineMappingDAO.getMachineByName(Mockito.anyString()))
				.thenReturn(Mockito.mock(MachineMaster.class));

		sucessResponse.setCode(RegistrationConstants.MASTER_SYNC_SUCESS_MSG_CODE);
		sucessResponse.setInfoType(RegistrationConstants.ALERT_INFORMATION);
		sucessResponse.setMessage(RegistrationConstants.MASTER_SYNC_SUCCESS);

		responseDTO.setSuccessResponseDTO(sucessResponse);

		masterSyncServiceImpl.getMasterSync("MDS_J00001", "System", "Index");
	}
	
}
