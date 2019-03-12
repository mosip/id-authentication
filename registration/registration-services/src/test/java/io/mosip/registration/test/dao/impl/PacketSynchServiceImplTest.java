package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.kernel.core.util.exception.JsonProcessingException;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.RegistrationPacketSyncDTO;
import io.mosip.registration.dto.SyncRegistrationDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.packet.impl.PacketSynchServiceImpl;
import io.mosip.registration.util.restclient.RequestHTTPDTO;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

public class PacketSynchServiceImplTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private RegistrationDAO registrationDAO;

	@Mock
	private ServiceDelegateUtil serviceDelegateUtil;

	@Mock
	private RequestHTTPDTO requestHTTPDTO;

	@InjectMocks
	private PacketSynchServiceImpl packetSynchServiceImpl;

	@Before
	public void initialize() {
		ReflectionTestUtils.setField(packetSynchServiceImpl, "syncUrlPath",
				"http://104.211.209.102:8080/v0.1/registration-processor/registration-status/sync");
	}

	@Test
	public void testFetchPacketsToBeSynched() {
		List<Registration> syncList = new ArrayList<>();
		syncList.add(new Registration());
		Mockito.when(registrationDAO.getPacketsToBeSynched(Mockito.anyList())).thenReturn(syncList);
		assertEquals(syncList, packetSynchServiceImpl.fetchPacketsToBeSynched());
	}

	@Test
	public void testSyncPacketsToServer() throws RegBaseCheckedException, JsonProcessingException, URISyntaxException,
			HttpClientErrorException, HttpServerErrorException, ResourceAccessException, SocketTimeoutException {
		List<SyncRegistrationDTO> syncDtoList = new ArrayList<>();
		LinkedHashMap<String, Object> respObj = new LinkedHashMap<>();
		LinkedHashMap<String, Object> msg = new LinkedHashMap<>();
		msg.put("registrationId", "123456789");
		msg.put("status", "Success");
		List<LinkedHashMap<String, Object>> mapList = new ArrayList<>();
		mapList.add(msg);
		respObj.put("response", mapList);
		RegistrationPacketSyncDTO registrationPacketSyncDTO = new RegistrationPacketSyncDTO();
		syncDtoList.add(new SyncRegistrationDTO());
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.anyString())).thenReturn(respObj);
		assertEquals("Success", packetSynchServiceImpl.syncPacketsToServer(registrationPacketSyncDTO)
				.getSuccessResponseDTO().getOtherAttributes().get("123456789"));
	}

	@Test
	public void testUpdateSyncStatus() {
		List<Registration> synchedPackets = new ArrayList<>();
		Registration reg = new Registration();
		synchedPackets.add(reg);
	//	Mockito.when(registrationDAO.updatePacketSyncStatus(reg)).thenReturn(new Registration());
	//	assertTrue(packetSynchServiceImpl.updateSyncStatus(synchedPackets));
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testHttpException() throws RegBaseCheckedException, JsonProcessingException, URISyntaxException,
			HttpClientErrorException, HttpServerErrorException, ResourceAccessException, SocketTimeoutException {
		List<SyncRegistrationDTO> syncDtoList = new ArrayList<>();
		syncDtoList.add(new SyncRegistrationDTO());
		RegistrationPacketSyncDTO registrationPacketSyncDTO = new RegistrationPacketSyncDTO();
		Object respObj = new Object();
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(new HttpClientErrorException(HttpStatus.ACCEPTED));
		assertEquals(respObj, packetSynchServiceImpl.syncPacketsToServer(registrationPacketSyncDTO));
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void testUnCheckedException() throws RegBaseCheckedException, JsonProcessingException, URISyntaxException,
			HttpClientErrorException, HttpServerErrorException, ResourceAccessException, SocketTimeoutException {
		List<SyncRegistrationDTO> syncDtoList = new ArrayList<>();
		syncDtoList.add(new SyncRegistrationDTO());
		Object respObj = new Object();
		RegistrationPacketSyncDTO registrationPacketSyncDTO = new RegistrationPacketSyncDTO();
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(new RuntimeException());
		assertEquals(respObj, packetSynchServiceImpl.syncPacketsToServer(registrationPacketSyncDTO));
	}

	@Test
	public void packetSyncTest() throws RegBaseCheckedException, JsonProcessingException, URISyntaxException,
			HttpClientErrorException, ResourceAccessException, SocketTimeoutException {
		List<Registration> synchedPackets = new ArrayList<>();
		Registration reg = new Registration();
		reg.setId("123456789");
		synchedPackets.add(reg);

		Mockito.when(registrationDAO.getRegistrationById(Mockito.anyString(), Mockito.anyString())).thenReturn(reg);
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(new LinkedHashMap<>());
		//Mockito.when(registrationDAO.updatePacketSyncStatus(reg)).thenReturn(new Registration());
		packetSynchServiceImpl.packetSync("123456789");
		assertEquals("SYNCED", reg.getClientStatusCode());
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void testsyncPacketException() throws RegBaseCheckedException, JsonProcessingException, URISyntaxException,
			HttpClientErrorException, ResourceAccessException, SocketTimeoutException {
		List<SyncRegistrationDTO> syncDtoList = new ArrayList<>();
		syncDtoList.add(new SyncRegistrationDTO());
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(new RuntimeException());
		packetSynchServiceImpl.packetSync("123456789");
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testsyncPacketException1()
			throws HttpClientErrorException, HttpServerErrorException, ResourceAccessException, SocketTimeoutException,
			RegBaseCheckedException, JsonProcessingException, URISyntaxException {
		List<Registration> synchedPackets = new ArrayList<>();
		Registration reg = new Registration();
		reg.setId("123456789");
		synchedPackets.add(reg);

		Object respObj = new Object();
		Mockito.when(registrationDAO.getRegistrationById(Mockito.anyString(), Mockito.anyString())).thenReturn(reg);

		List<SyncRegistrationDTO> syncDtoList = new ArrayList<>();
		syncDtoList.add(new SyncRegistrationDTO());
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(new HttpClientErrorException(HttpStatus.ACCEPTED));
		RegistrationPacketSyncDTO registrationPacketSyncDTO = new RegistrationPacketSyncDTO();
		packetSynchServiceImpl.packetSync("123456789");
		assertEquals(respObj, packetSynchServiceImpl.syncPacketsToServer(registrationPacketSyncDTO));
	}

	@Test
	public void testsyncEODPackets() throws HttpClientErrorException, ResourceAccessException, SocketTimeoutException,
			RegBaseCheckedException, JsonProcessingException, URISyntaxException {
		List<String> idlist = new ArrayList<>();
		idlist.add("123456789");
		List<Registration> synchedPackets = new ArrayList<>();
		Registration reg = new Registration();
		reg.setId("123456789");
		synchedPackets.add(reg);

		Mockito.when(registrationDAO.get(idlist)).thenReturn(synchedPackets);

		List<SyncRegistrationDTO> syncDtoList = new ArrayList<>();
		syncDtoList.add(new SyncRegistrationDTO());
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(new LinkedHashMap<>());
		packetSynchServiceImpl.syncEODPackets(idlist);
		assertEquals("SYNCED", reg.getClientStatusCode());
	}
}
