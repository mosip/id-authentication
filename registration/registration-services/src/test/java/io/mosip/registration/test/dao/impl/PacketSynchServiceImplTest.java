package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;

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
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.PacketStatusDTO;
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
	@Mock
	private AuditFactory auditFactory;
	@InjectMocks
	private PacketSynchServiceImpl packetSynchServiceImpl;
	
	@Before
	public void initialize() {
		ReflectionTestUtils.setField(packetSynchServiceImpl, "syncUrlPath",
				"http://104.211.209.102:8080/v0.1/registration-processor/registration-status/sync");
		
		doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(Components.class),
				Mockito.anyString(), Mockito.anyString());
		SessionContext.getInstance().getUserContext().setUserId("mosip1214");

	}

	@Test
	public void testFetchPacketsToBeSynched() {List<Registration> syncList = new ArrayList<>();
	Registration reg = new Registration();
	reg.setId("12345");
	syncList.add(reg);
	List<PacketStatusDTO> packetsList = new ArrayList<>();
	PacketStatusDTO packetStatusDTO=new PacketStatusDTO();
	packetStatusDTO.setFileName("12345");
	packetsList.add(packetStatusDTO);
	Mockito.when(registrationDAO.getPacketsToBeSynched(Mockito.anyList())).thenReturn(syncList);
	assertEquals(syncList.get(0).getId(), packetSynchServiceImpl.fetchPacketsToBeSynched().get(0).getFileName());
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
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(respObj);
		assertEquals("Success", packetSynchServiceImpl.syncPacketsToServer(registrationPacketSyncDTO, "System")
				.getSuccessResponseDTO().getOtherAttributes().get("123456789"));
	}

	@Test
	public void testUpdateSyncStatus() {
		List<PacketStatusDTO> synchedPackets = new ArrayList<>();
		PacketStatusDTO reg = new PacketStatusDTO();
		synchedPackets.add(reg);
		Mockito.when(registrationDAO.updatePacketSyncStatus(reg)).thenReturn(new
		Registration());
		assertTrue(packetSynchServiceImpl.updateSyncStatus(synchedPackets));
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testHttpException() throws RegBaseCheckedException, JsonProcessingException, URISyntaxException,
			HttpClientErrorException, HttpServerErrorException, ResourceAccessException, SocketTimeoutException {
		List<SyncRegistrationDTO> syncDtoList = new ArrayList<>();
		syncDtoList.add(new SyncRegistrationDTO());
		RegistrationPacketSyncDTO registrationPacketSyncDTO = new RegistrationPacketSyncDTO();
		Object respObj = new Object();
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenThrow(new HttpClientErrorException(HttpStatus.ACCEPTED));
		assertEquals(respObj, packetSynchServiceImpl.syncPacketsToServer(registrationPacketSyncDTO, "System"));
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void testUnCheckedException() throws RegBaseCheckedException, JsonProcessingException, URISyntaxException,
			HttpClientErrorException, HttpServerErrorException, ResourceAccessException, SocketTimeoutException {
		List<SyncRegistrationDTO> syncDtoList = new ArrayList<>();
		syncDtoList.add(new SyncRegistrationDTO());
		Object respObj = new Object();
		RegistrationPacketSyncDTO registrationPacketSyncDTO = new RegistrationPacketSyncDTO();
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenThrow(new RuntimeException());
		assertEquals(respObj, packetSynchServiceImpl.syncPacketsToServer(registrationPacketSyncDTO, "System"));
	}

	@Test
	public void packetSyncTest() throws RegBaseCheckedException, JsonProcessingException, URISyntaxException,
			HttpClientErrorException, ResourceAccessException, SocketTimeoutException {
		List<PacketStatusDTO> synchedPackets = new ArrayList<>();
		Registration reg = new Registration();
		reg.setId("123456789");
		reg.setClientStatusCode("SYNCED");
		PacketStatusDTO packetStatusDTO=new PacketStatusDTO();
		synchedPackets.add(packetStatusDTO);

		Mockito.when(registrationDAO.getRegistrationById(Mockito.anyString(), Mockito.anyString())).thenReturn(reg);
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(new LinkedHashMap<>());
		 Mockito.when(registrationDAO.updatePacketSyncStatus(packetStatusDTO)).thenReturn(reg);
		packetSynchServiceImpl.packetSync("123456789");
		assertEquals("SYNCED", reg.getClientStatusCode());
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testsyncPacketException() throws RegBaseCheckedException, JsonProcessingException, URISyntaxException,
			HttpClientErrorException, ResourceAccessException, SocketTimeoutException {
		List<SyncRegistrationDTO> syncDtoList = new ArrayList<>();
		syncDtoList.add(new SyncRegistrationDTO());
		Registration reg=new Registration();
		reg.setId("12345");
		Mockito.when(registrationDAO.getRegistrationById(Mockito.anyString(), Mockito.anyString())).thenReturn(reg);
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenThrow(new RuntimeException());
		packetSynchServiceImpl.packetSync("123456789");
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testsyncPacketException1()
			throws HttpClientErrorException, HttpServerErrorException, ResourceAccessException, SocketTimeoutException,
			RegBaseCheckedException, JsonProcessingException, URISyntaxException {
		Registration reg = new Registration();
		reg.setId("123456789");

		Object respObj = new Object();
		Mockito.when(registrationDAO.getRegistrationById(Mockito.anyString(), Mockito.anyString())).thenReturn(reg);

		List<SyncRegistrationDTO> syncDtoList = new ArrayList<>();
		syncDtoList.add(new SyncRegistrationDTO());
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenThrow(new HttpClientErrorException(HttpStatus.ACCEPTED));
		RegistrationPacketSyncDTO registrationPacketSyncDTO = new RegistrationPacketSyncDTO();
		packetSynchServiceImpl.packetSync("123456789");
		assertEquals(respObj, packetSynchServiceImpl.syncPacketsToServer(registrationPacketSyncDTO, "System"));
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
		reg.setClientStatusCode("SYNCED");
		Mockito.when(registrationDAO.get(idlist)).thenReturn(synchedPackets);

		List<SyncRegistrationDTO> syncDtoList = new ArrayList<>();
		syncDtoList.add(new SyncRegistrationDTO());
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(new LinkedHashMap<>());
		packetSynchServiceImpl.syncEODPackets(idlist);
		assertEquals("SYNCED", reg.getClientStatusCode());
	}
}
