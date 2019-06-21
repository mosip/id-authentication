package io.mosip.registration.test.service.packet;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.doThrow;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.context.SessionContext.UserContext;
import io.mosip.registration.dao.PreRegistrationDataSyncDAO;
import io.mosip.registration.dto.MainResponseDTO;
import io.mosip.registration.dto.PreRegistrationDTO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.PreRegistrationList;
import io.mosip.registration.entity.SyncTransaction;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.jobs.SyncManager;
import io.mosip.registration.service.external.PreRegZipHandlingService;
import io.mosip.registration.service.sync.impl.PreRegistrationDataSyncServiceImpl;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ RegistrationAppHealthCheckUtil.class, SessionContext.class })
public class PreRegistrationDataSyncServiceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private PreRegistrationDataSyncServiceImpl preRegistrationDataSyncServiceImpl;

	@Mock
	private ServiceDelegateUtil serviceDelegateUtil;

	@Mock
	private SyncManager syncManager;

	@Mock
	private PreRegistrationDataSyncDAO preRegistrationDAO;

	@Mock
	SyncTransaction syncTransaction;

	@Mock
	PreRegistrationList preRegistrationList;

	@Mock
	PreRegZipHandlingService preRegZipHandlingService;

	static byte[] preRegPacket;

	static Map<String, Object> preRegData = new HashMap<>();

	@Before
	public void dtoInitalization() {
		PreRegistrationDTO preRegistrationDTO = new PreRegistrationDTO();
		preRegistrationDTO.setPacketPath("path");
		preRegistrationDTO.setSymmetricKey("0E8BAAEB3CED73CBC9BF4964F321824A");
		preRegistrationDTO.setEncryptedPacket(preRegPacket);
		preRegistrationDTO.setPreRegId("70694681371453");
	}

	@BeforeClass
	public static void initialize() throws IOException, UnsupportedEncodingException {

		URL url = PreRegistrationDataSyncServiceImpl.class.getResource("/preRegSample.zip");
		File packetZipFile = new File(URLDecoder.decode(url.getFile(), "UTF-8"));
		preRegPacket = FileUtils.readFileToByteArray(packetZipFile);

		preRegData.put(RegistrationConstants.PRE_REG_FILE_NAME, "filename_2018-12-12 09:39:08.272.zip");
		preRegData.put(RegistrationConstants.PRE_REG_FILE_CONTENT, preRegPacket);
	}

	@Before
	public void initiate() throws Exception {
		Map<String, Object> applicationMap = new HashMap<>();
		applicationMap.put(RegistrationConstants.PRE_REG_DELETION_CONFIGURED_DAYS, "45");
		applicationMap.put(RegistrationConstants.PRE_REG_DAYS_LIMIT, "5");

		Map<String, Object> map = new HashMap<>();
		map.put(RegistrationConstants.PRE_REG_DELETION_CONFIGURED_DAYS, "5");

		io.mosip.registration.context.ApplicationContext.setApplicationMap(applicationMap);

		UserContext userContext = Mockito.mock(SessionContext.UserContext.class);
		PowerMockito.mockStatic(SessionContext.class);
		PowerMockito.doReturn(userContext).when(SessionContext.class, "userContext");
		RegistrationCenterDetailDTO registrationCenterDetailDTO = new RegistrationCenterDetailDTO();
		registrationCenterDetailDTO.setRegistrationCenterId("10031");
		PowerMockito.when(SessionContext.userContext().getRegistrationCenterDetailDTO())
				.thenReturn(registrationCenterDetailDTO);
		PowerMockito.when(SessionContext.isSessionContextAvailable()).thenReturn(true);
		PowerMockito.when(SessionContext.userId()).thenReturn("mosip");

	}

	@AfterClass
	public static void destroy() {
		SessionContext.destroySession();
	}

	@Test
	public void getPreRegistrationsTest()
			throws HttpClientErrorException, ResourceAccessException, SocketTimeoutException, RegBaseCheckedException {

		LinkedHashMap<String, Object> postResponse = new LinkedHashMap<>();

		LinkedHashMap<String, Object> responseData = new LinkedHashMap<>();

		HashMap<String, String> map = new HashMap<>();
		map.put("70694681371453", "2019-01-17T05:42:35.747Z");
		responseData.put("preRegistrationIds", map);

		postResponse.put("response", responseData);
		mockPreRegServices(postResponse);

		mockEncryptedPacket();
		PreRegistrationDTO preRegistrationDTO = new PreRegistrationDTO();
		preRegistrationDTO.setPacketPath("path");
		preRegistrationDTO.setSymmetricKey("0E8BAAEB3CED73CBC9BF4964F321824A");
		preRegistrationDTO.setEncryptedPacket(preRegPacket);
		preRegistrationDTO.setPreRegId("70694681371453");
		Mockito.when(preRegZipHandlingService
		.encryptAndSavePreRegPacket(Mockito.anyString(), Mockito.any())).thenReturn(preRegistrationDTO);
		Mockito.when(preRegZipHandlingService.extractPreRegZipFile(Mockito.any())).thenReturn(new RegistrationDTO());

		preRegistrationDataSyncServiceImpl.getPreRegistrationIds("System");

	}

	protected void mockPreRegServices(LinkedHashMap<String, Object> postResponse)
			throws RegBaseCheckedException, SocketTimeoutException {
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.any(), Mockito.anyString()))
				.thenReturn(postResponse);
		// Mockito.when(preRegistrationResponseDTO.getResponse()).thenReturn(list);

		Map<String, Object> responseMap = new LinkedHashMap<>();
		LinkedHashMap<String, Object> valuesMap = new LinkedHashMap<>();
		valuesMap.put("pre-registration-id", "70694681371453");
		valuesMap.put("registration-client-id", "10003");
		valuesMap.put("appointment-date", "2019-06-16");
		valuesMap.put("from-time-slot", "09:00");
		valuesMap.put("to-time-slot", "09:15");
		valuesMap.put("zip-filename", "70694681371453");
		valuesMap.put("zip-bytes", RegistrationConstants.FACE_STUB);
		responseMap.put(RegistrationConstants.PACKET_STATUS_READER_RESPONSE, valuesMap);
		Mockito.when(preRegistrationDAO.get(Mockito.anyString())).thenReturn(new PreRegistrationList());
		Mockito.when(
				serviceDelegateUtil.get(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyString()))
				.thenReturn(responseMap);
		Mockito.when(syncManager.createSyncTransaction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn(syncTransaction);
		Mockito.when(preRegistrationDAO.save(preRegistrationList)).thenReturn(preRegistrationList);
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);
	}

	@Test
	public void getPreRegistrationsAlternateFlowTest()
			throws HttpClientErrorException, ResourceAccessException, SocketTimeoutException, RegBaseCheckedException {

		LinkedHashMap<String, Object> postResponse = new LinkedHashMap<>();

		mockPreRegServices(postResponse);

		mockEncryptedPacket();

		preRegistrationDataSyncServiceImpl.getPreRegistrationIds("System");

	}

	private MainResponseDTO<LinkedHashMap<String, Object>> getTestPacketData() {
		MainResponseDTO<LinkedHashMap<String, Object>> testData = new MainResponseDTO<>();
		LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>();
		linkedHashMap.put("appointment-date", "2019-01-12");
		linkedHashMap.put("zip-bytes", preRegPacket);
		testData.setResponse(linkedHashMap);
		return testData;
	}

	@Test
	public void getPreRegistrationTest()
			throws HttpClientErrorException, ResourceAccessException, SocketTimeoutException, RegBaseCheckedException {

		mockData();

		mockEncryptedPacket();
		PreRegistrationDTO preRegistrationDTO = new PreRegistrationDTO();
		preRegistrationDTO.setPacketPath("path");
		preRegistrationDTO.setSymmetricKey("0E8BAAEB3CED73CBC9BF4964F321824A");
		preRegistrationDTO.setEncryptedPacket(preRegPacket);
		preRegistrationDTO.setPreRegId("70694681371453");
		Mockito.when(preRegZipHandlingService
		.encryptAndSavePreRegPacket(Mockito.anyString(), Mockito.any())).thenReturn(preRegistrationDTO);
		Mockito.when(preRegZipHandlingService.extractPreRegZipFile(Mockito.any())).thenReturn(new RegistrationDTO());

		ResponseDTO responseDTO = preRegistrationDataSyncServiceImpl.getPreRegistration("70694681371453");
		assertNotNull(responseDTO);

	}

	protected void mockData() throws RegBaseCheckedException, SocketTimeoutException {
		Map<String, Object> responseMap = new LinkedHashMap<>();
		LinkedHashMap<String, Object> valuesMap = new LinkedHashMap<>();
		valuesMap.put("pre-registration-id", "70694681371453");
		valuesMap.put("registration-client-id", "10003");
		valuesMap.put("appointment-date", "2019-06-16");
		valuesMap.put("from-time-slot", "09:00");
		valuesMap.put("to-time-slot", "09:15");
		valuesMap.put("zip-filename", "70694681371453");
		valuesMap.put("zip-bytes", RegistrationConstants.FACE_STUB);
		responseMap.put(RegistrationConstants.PACKET_STATUS_READER_RESPONSE, valuesMap);
		Mockito.when(
				serviceDelegateUtil.get(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyString()))
				.thenReturn(responseMap);
		Mockito.when(syncManager.createSyncTransaction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn(syncTransaction);
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);
	}

	@Test
	public void getPreRegistrationAlternateTest()
			throws HttpClientErrorException, ResourceAccessException, SocketTimeoutException, RegBaseCheckedException {

		Mockito.when(
				serviceDelegateUtil.get(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyString()))
				.thenReturn(223233223);
		Mockito.when(syncManager.createSyncTransaction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn(syncTransaction);
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(false);

		// Mockito.when(preRegistrationDAO.get(Mockito.anyString())).thenReturn(new
		// PreRegistrationList());

		ResponseDTO responseDTO = preRegistrationDataSyncServiceImpl.getPreRegistration("70694681371453");
		assertNotNull(responseDTO);

	}

	@SuppressWarnings("unchecked")
	@Test
	public void getPreRegistrationNegativeTest()
			throws HttpClientErrorException, ResourceAccessException, SocketTimeoutException, RegBaseCheckedException {

		Mockito.when(
				serviceDelegateUtil.get(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyString()))
				.thenThrow(HttpClientErrorException.class);
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		preRegistrationDataSyncServiceImpl.getPreRegistration("70694681371453");

	}

	@Test
	public void getPreRegistrationExceptionTest()
			throws HttpClientErrorException, ResourceAccessException, SocketTimeoutException, RegBaseCheckedException {

		mockData();

		mockEncryptedPacket();
		PreRegistrationDTO preRegistrationDTO = new PreRegistrationDTO();
		preRegistrationDTO.setPacketPath("path");
		preRegistrationDTO.setSymmetricKey("0E8BAAEB3CED73CBC9BF4964F321824A");
		preRegistrationDTO.setEncryptedPacket(preRegPacket);
		preRegistrationDTO.setPreRegId("70694681371453");
		Mockito.when(preRegZipHandlingService
		.encryptAndSavePreRegPacket(Mockito.anyString(), Mockito.any())).thenReturn(preRegistrationDTO);
	
		doThrow(new RegBaseCheckedException()).when(preRegZipHandlingService).extractPreRegZipFile(Mockito.any());

		preRegistrationDataSyncServiceImpl.getPreRegistration("70694681371453");

	}

	@SuppressWarnings("unchecked")
	@Test
	public void getPreRegistrationsTestNegative()
			throws HttpClientErrorException, ResourceAccessException, SocketTimeoutException, RegBaseCheckedException { // Test-2
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.any(), Mockito.anyString()))
				.thenThrow(HttpClientErrorException.class);
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);
		preRegistrationDataSyncServiceImpl.getPreRegistrationIds("System");
	}

	private void mockEncryptedPacket() throws RegBaseCheckedException {
		mockEncryptedData();

		Mockito.when(preRegZipHandlingService.extractPreRegZipFile(preRegPacket)).thenReturn(new RegistrationDTO());

	}

	protected void mockEncryptedData() throws RegBaseCheckedException {
		PreRegistrationDTO preRegistrationDTO = new PreRegistrationDTO();
		preRegistrationDTO.setPacketPath("path");
		preRegistrationDTO.setSymmetricKey("0E8BAAEB3CED73CBC9BF4964F321824A");
		preRegistrationDTO.setEncryptedPacket(preRegPacket);
		preRegistrationDTO.setPreRegId("70694681371453");

		Mockito.when(preRegZipHandlingService.encryptAndSavePreRegPacket("70694681371453", preRegPacket))
				.thenReturn(preRegistrationDTO);

		Mockito.when(preRegZipHandlingService.decryptPreRegPacket("0E8BAAEB3CED73CBC9BF4964F321824A", preRegPacket))
				.thenReturn(preRegPacket);
	}

	@Test
	public void fetchAndDeleteRecordsTest() throws java.io.IOException {
		File file = mockDeleteMethodFiles();
		preRegistrationDataSyncServiceImpl.fetchAndDeleteRecords();

		if (file.exists()) {
			file.delete();
		}
	}

	@Test
	public void fetchAndDeleteRecordsRuntimeExceptionTest() throws java.io.IOException {
		File file = mockDeleteMethodFiles();
		doThrow(new RuntimeException()).when(preRegistrationDAO).deleteAll(Mockito.anyList());
		preRegistrationDataSyncServiceImpl.fetchAndDeleteRecords();

		if (file.exists()) {
			file.delete();
		}
	}

	protected File mockDeleteMethodFiles() throws java.io.IOException {
		File file = new File("testDeletePacket.txt");
		file.createNewFile();
		List<PreRegistrationList> preRegList = new ArrayList<>();
		PreRegistrationList preRegistrationList = new PreRegistrationList();
		preRegistrationList.setPacketPath(file.getAbsolutePath());
		preRegList.add(preRegistrationList);
		Mockito.when(preRegistrationDAO.fetchRecordsToBeDeleted(Mockito.any())).thenReturn(preRegList);
		Mockito.when(preRegistrationDAO.update(Mockito.anyObject())).thenReturn(preRegistrationList);
		return file;
	}

	@Test
	public void fetchAndDeleteRecordsAlternateTest() throws java.io.IOException {
		File file = new File("testDeletePac.txt");
		file.createNewFile();
		List<PreRegistrationList> preRegList = new ArrayList<>();
		PreRegistrationList preRegistrationList = new PreRegistrationList();
		preRegistrationList.setPacketPath(file.getAbsolutePath());
		preRegList.add(preRegistrationList);
		Mockito.when(preRegistrationDAO.fetchRecordsToBeDeleted(Mockito.any())).thenReturn(null);
		Mockito.when(preRegistrationDAO.update(Mockito.anyObject())).thenReturn(preRegistrationList);
		preRegistrationDataSyncServiceImpl.fetchAndDeleteRecords();

		if (file.exists()) {
			file.delete();
		}
	}

	@Test
	public void getPreRegistrationRecordForDeletionTest() throws java.io.IOException {

		PreRegistrationList preRegistrationList = new PreRegistrationList();
		preRegistrationList.setId("123456789");
		preRegistrationList.setPreRegId("987654321");
		Mockito.when(preRegistrationDAO.get(Mockito.anyString())).thenReturn(preRegistrationList);
		PreRegistrationList preRegistration = preRegistrationDataSyncServiceImpl
				.getPreRegistrationRecordForDeletion("987654321");

		assertTrue(preRegistration.getId().equals("123456789"));
		assertTrue(preRegistration.getPreRegId().equals("987654321"));

	}
}
