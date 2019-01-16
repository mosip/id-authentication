package io.mosip.registration.test.service.packet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.PreRegistrationDataSyncDAO;
import io.mosip.registration.dto.MainResponseDTO;
import io.mosip.registration.dto.PreRegistrationDTO;
import io.mosip.registration.dto.PreRegistrationResponseDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.PreRegistrationList;
import io.mosip.registration.entity.SyncTransaction;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.jobs.SyncManager;
import io.mosip.registration.service.external.PreRegZipHandlingService;
import io.mosip.registration.service.sync.impl.PreRegistrationDataSyncServiceImpl;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

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
	private PreRegistrationResponseDTO preRegistrationResponseDTO;

	@Mock
	SyncTransaction syncTransaction;

	@Mock
	PreRegistrationList preRegistrationList;

	@Mock
	PreRegZipHandlingService preRegZipHandlingService;

	static byte[] preRegPacket;

	static Map<String, Object> preRegData = new HashMap<>();

	@BeforeClass
	public static void initialize() throws IOException {

		URL url = PreRegistrationDataSyncServiceImpl.class.getResource("/70694681371453.zip");
		File packetZipFile = new File(url.getFile());
		preRegPacket = FileUtils.readFileToByteArray(packetZipFile);

		preRegData.put(RegistrationConstants.PRE_REG_FILE_NAME, "filename_2018-12-12 09:39:08.272.zip");
		preRegData.put(RegistrationConstants.PRE_REG_FILE_CONTENT, preRegPacket);
	}

	@Ignore
	@Test
	public void getPreRegistrationsTest()
			throws HttpClientErrorException, ResourceAccessException, SocketTimeoutException, RegBaseCheckedException {

		LinkedHashMap<String, Object> responseData = new LinkedHashMap<>();

		ArrayList<String> ids = new ArrayList<String>();
		ids.add("70694681371453");

		HashMap<String, Object> map = new HashMap<>();
		map.put("preRegistrationIds", ids);

		ArrayList<Map<String, Object>> list = new ArrayList<>();

		list.add(map);

		responseData.put("response", map);

		
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.any())).thenReturn(responseData);
		Mockito.when(preRegistrationResponseDTO.getResponse()).thenReturn(list);

		Mockito.when(preRegistrationDAO.get(Mockito.anyString())).thenReturn(null);
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean()))
				.thenReturn(getTestPacketData());
		Mockito.when(syncManager.createSyncTransaction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn(syncTransaction);
		Mockito.when(preRegistrationDAO.save(preRegistrationList)).thenReturn(preRegistrationList);

		mockEncryptedPacket();

		preRegistrationDataSyncServiceImpl.getPreRegistrationIds("System");

	}

	private MainResponseDTO<LinkedHashMap<String, Object>> getTestPacketData() {
		MainResponseDTO<LinkedHashMap<String, Object>> testData = new MainResponseDTO<>();
		LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>();
		linkedHashMap.put("zip-bytes", preRegData);
		return testData;
	}

	@Test
	public void getPreRegistrationTest()
			throws HttpClientErrorException, ResourceAccessException, SocketTimeoutException, RegBaseCheckedException {

		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean()))
				.thenReturn(getTestPacketData());
		Mockito.when(syncManager.createSyncTransaction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn(syncTransaction);
		// Mockito.when(preRegistrationDAO.get(Mockito.anyString())).thenReturn(new
		// PreRegistrationList());

		mockEncryptedPacket();

		ResponseDTO responseDTO = preRegistrationDataSyncServiceImpl.getPreRegistration("70694681371453");
		assertNotNull(responseDTO);

	}

	@Test
	public void getPreRegistrationNegativeTest()
			throws HttpClientErrorException, ResourceAccessException, SocketTimeoutException, RegBaseCheckedException {

		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean()))
				.thenThrow(HttpClientErrorException.class);

		preRegistrationDataSyncServiceImpl.getPreRegistration("70694681371453");

	}

	@Test
	public void getPreRegistrationsTestNegative()
			throws HttpClientErrorException, ResourceAccessException, SocketTimeoutException, RegBaseCheckedException {
		// Test-2
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.any()))
				.thenThrow(HttpClientErrorException.class);
		preRegistrationDataSyncServiceImpl.getPreRegistrationIds(null);
	}

	private void mockEncryptedPacket() throws RegBaseCheckedException {
		PreRegistrationDTO preRegistrationDTO = new PreRegistrationDTO();
		preRegistrationDTO.setPacketPath("path");
		preRegistrationDTO.setSymmetricKey("0E8BAAEB3CED73CBC9BF4964F321824A");
		preRegistrationDTO.setEncryptedPacket(preRegPacket);
		preRegistrationDTO.setPreRegId("70694681371453");

		Mockito.when(preRegZipHandlingService.encryptAndSavePreRegPacket("70694681371453", preRegPacket))
				.thenReturn(preRegistrationDTO);

		Mockito.when(preRegZipHandlingService.decryptPreRegPacket("0E8BAAEB3CED73CBC9BF4964F321824A", preRegPacket))
				.thenReturn(preRegPacket);

		Mockito.when(preRegZipHandlingService.extractPreRegZipFile(preRegPacket)).thenReturn(new RegistrationDTO());

	}

	@Test
	public void fetchAndDeleteRecordsTest() {
		List<PreRegistrationList> preRegList = new ArrayList<>();
		PreRegistrationList preRegistrationList = new PreRegistrationList();
		preRegistrationList.setPacketPath("");
		preRegList.add(preRegistrationList);
		Mockito.when(preRegistrationDAO.fetchRecordsToBeDeleted(Mockito.any())).thenReturn(preRegList);
		Mockito.when(preRegistrationDAO.update(Mockito.anyObject())).thenReturn(preRegistrationList);
		assertEquals(null, preRegistrationDataSyncServiceImpl.fetchAndDeleteRecords().getErrorResponseDTOs());
	}

}
