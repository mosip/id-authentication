package io.mosip.registration.test.dao.impl;

import static io.mosip.kernel.core.util.JsonUtils.javaObjectToJsonString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;

import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.core.util.exception.JsonProcessingException;
import io.mosip.registration.audit.AuditManagerService;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.context.SessionContext.UserContext;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.dto.RegistrationPacketSyncDTO;
import io.mosip.registration.dto.SyncRegistrationDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.packet.impl.PacketSynchServiceImpl;
import io.mosip.registration.service.security.AESEncryptionService;
import io.mosip.registration.util.restclient.RequestHTTPDTO;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ HMACUtils.class, SessionContext.class })
public class PacketSynchServiceImplTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private RegistrationDAO registrationDAO;

	@Mock
	private ServiceDelegateUtil serviceDelegateUtil;
	
	@Mock
	private AESEncryptionService aesEncryptionService;

	@Mock
	private RequestHTTPDTO requestHTTPDTO;
	@Mock
	private AuditManagerService auditFactory;
	@InjectMocks
	private PacketSynchServiceImpl packetSynchServiceImpl;

	@Before
	public void initialize() throws Exception{
		
		PowerMockito.mockStatic(HMACUtils.class);
		PowerMockito.mockStatic(SessionContext.class);
				
		doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(Components.class),
				Mockito.anyString(), Mockito.anyString());
		
		UserContext userContext = Mockito.mock(SessionContext.UserContext.class);		
		PowerMockito.doReturn(userContext).when(SessionContext.class, "userContext");
		PowerMockito.when(SessionContext.userContext().getUserId()).thenReturn("mosip1214");
		
		Map<String, Object> maplastTime = new HashMap<>();
		maplastTime.put("PRIMARY_LANGUAGE", "ENG");
		ApplicationContext.getInstance().setApplicationMap(maplastTime);

	}

	@Test
	public void testFetchPacketsToBeSynched() {
		List<Registration> syncList = new ArrayList<>();
		Registration reg = new Registration();
		reg.setCrDtime(Timestamp.from(Instant.now()));
		reg.setId("12345");
		reg.setCrDtime(Timestamp.from(Instant.now()));
		syncList.add(reg);
		List<PacketStatusDTO> packetsList = new ArrayList<>();
		PacketStatusDTO packetStatusDTO = new PacketStatusDTO();
		packetStatusDTO.setFileName("12345");
		packetsList.add(packetStatusDTO);
		Mockito.when(registrationDAO.fetchPacketsToUpload(Mockito.anyList(),Mockito.anyString())).thenReturn(syncList);
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
		Mockito.when(aesEncryptionService.encrypt(javaObjectToJsonString(registrationPacketSyncDTO).getBytes())).thenReturn("aes".getBytes());
		assertEquals("Success", packetSynchServiceImpl.syncPacketsToServer("123456789", "System")
				.getSuccessResponseDTO().getOtherAttributes().get("123456789"));
	}

	@Test
	public void testUpdateSyncStatus() {
		List<PacketStatusDTO> synchedPackets = new ArrayList<>();
		PacketStatusDTO reg = new PacketStatusDTO();
		synchedPackets.add(reg);
		Mockito.when(registrationDAO.updatePacketSyncStatus(reg)).thenReturn(new Registration());
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
		Mockito.when(aesEncryptionService.encrypt(javaObjectToJsonString(registrationPacketSyncDTO).getBytes())).thenReturn("aes".getBytes());
		assertEquals(respObj, packetSynchServiceImpl.syncPacketsToServer("123456789", "System"));
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
		Mockito.when(aesEncryptionService.encrypt(javaObjectToJsonString(registrationPacketSyncDTO).getBytes())).thenReturn("aes".getBytes());
		assertEquals(respObj, packetSynchServiceImpl.syncPacketsToServer("123456789", "System"));
	}
	
	@Test(expected = RegBaseCheckedException.class)
	public void testSocketTimeoutException() throws RegBaseCheckedException, JsonProcessingException, URISyntaxException,
			HttpClientErrorException, HttpServerErrorException, ResourceAccessException, SocketTimeoutException {
		List<SyncRegistrationDTO> syncDtoList = new ArrayList<>();
		syncDtoList.add(new SyncRegistrationDTO());
		RegistrationPacketSyncDTO registrationPacketSyncDTO = new RegistrationPacketSyncDTO();
		Object respObj = new Object();
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenThrow(new SocketTimeoutException());
		Mockito.when(aesEncryptionService.encrypt(javaObjectToJsonString(registrationPacketSyncDTO).getBytes())).thenReturn("aes".getBytes());
		assertEquals(respObj, packetSynchServiceImpl.syncPacketsToServer("123456789", "System"));
	}

	@Test
	public void packetSyncTest() throws RegBaseCheckedException, JsonProcessingException, URISyntaxException,
			HttpClientErrorException, ResourceAccessException, SocketTimeoutException {
		
		List<PacketStatusDTO> synchedPackets = new ArrayList<>();
		HashMap<String, Object> obj = new LinkedHashMap<>();
		List<LinkedHashMap<String, Object>> resplist= new ArrayList<>();
		LinkedHashMap<String, Object> obj1 = new LinkedHashMap<>();
		obj1.put("Success", "Success");
		resplist.add(obj1);
		obj.put("response", resplist);
		Registration reg = new Registration();
		reg.setId("123456789");
		reg.setClientStatusCode("SYNCED");
		reg.setAckFilename("10001100010025920190430051904_Ack.html");
		reg.setStatusCode("NEW");
		PacketStatusDTO packetStatusDTO = new PacketStatusDTO();
		synchedPackets.add(packetStatusDTO);

		Mockito.when(registrationDAO.getRegistrationById(Mockito.anyString(), Mockito.anyString())).thenReturn(reg);
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(obj);
		Mockito.when(registrationDAO.updatePacketSyncStatus(packetStatusDTO)).thenReturn(reg);
				
		Mockito.when(HMACUtils.generateHash(Mockito.anyString().getBytes())).thenReturn("asa".getBytes());		
		packetSynchServiceImpl.packetSync("123456789");
		assertEquals("SYNCED", reg.getClientStatusCode());
	}

	@Ignore
	@Test(expected = RegBaseCheckedException.class)
	public void testsyncPacketException() throws RegBaseCheckedException, JsonProcessingException, URISyntaxException,
			HttpClientErrorException, ResourceAccessException, SocketTimeoutException {
		List<SyncRegistrationDTO> syncDtoList = new ArrayList<>();
		syncDtoList.add(new SyncRegistrationDTO());
		Registration reg = new Registration();
		reg.setId("12345");
		reg.setAckFilename("10001100010025920190430051904_Ack.html");
		reg.setStatusCode("NEW");
		
		Mockito.when(registrationDAO.getRegistrationById(Mockito.anyString(), Mockito.anyString())).thenReturn(reg);
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenThrow(new RuntimeException());
		Mockito.when(HMACUtils.generateHash(Mockito.anyString().getBytes())).thenReturn("asa".getBytes());
		packetSynchServiceImpl.packetSync("123456789");
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testsyncPacketException1()
			throws HttpClientErrorException, HttpServerErrorException, ResourceAccessException, SocketTimeoutException,
			RegBaseCheckedException, JsonProcessingException, URISyntaxException {
		Registration reg = new Registration();
		reg.setId("123456789");
		reg.setAckFilename("10001100010025920190430051904_Ack.html");
		reg.setStatusCode("NEW");
		
		Object respObj = new Object();
		Mockito.when(registrationDAO.getRegistrationById(Mockito.anyString(), Mockito.anyString())).thenReturn(reg);

		List<SyncRegistrationDTO> syncDtoList = new ArrayList<>();
		syncDtoList.add(new SyncRegistrationDTO());
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenThrow(new HttpClientErrorException(HttpStatus.ACCEPTED));
		RegistrationPacketSyncDTO registrationPacketSyncDTO = new RegistrationPacketSyncDTO();
		Mockito.when(HMACUtils.generateHash(Mockito.anyString().getBytes())).thenReturn("asa".getBytes());
		packetSynchServiceImpl.packetSync("123456789");
		Mockito.when(aesEncryptionService.encrypt(javaObjectToJsonString(registrationPacketSyncDTO).getBytes())).thenReturn("aes".getBytes());
		assertEquals(respObj, packetSynchServiceImpl.syncPacketsToServer("123456789", "System"));
	}

	@Test
	public void testsyncEODPackets() throws HttpClientErrorException, ResourceAccessException, SocketTimeoutException,
			RegBaseCheckedException, JsonProcessingException, URISyntaxException {
		List<String> idlist = new ArrayList<>();
		idlist.add("123456789");
		List<Registration> synchedPackets = new ArrayList<>();
		Registration reg = new Registration();
		reg.setId("123456789");
		reg.setAckFilename("10001100010025920190430051904_Ack.html");
		reg.setStatusCode("NEW");
		synchedPackets.add(reg);
		reg.setClientStatusCode("SYNCED");
		Mockito.when(registrationDAO.get(idlist)).thenReturn(synchedPackets);

		List<SyncRegistrationDTO> syncDtoList = new ArrayList<>();
		syncDtoList.add(new SyncRegistrationDTO());
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(new LinkedHashMap<>());
		Mockito.when(HMACUtils.generateHash(Mockito.anyString().getBytes())).thenReturn("asa".getBytes());
		packetSynchServiceImpl.syncEODPackets(idlist);
		assertEquals("SYNCED", reg.getClientStatusCode());
	}
	
	@Test
	public void testSyncPacketsToServer_1() throws RegBaseCheckedException, JsonProcessingException, URISyntaxException,
			HttpClientErrorException, HttpServerErrorException, ResourceAccessException, SocketTimeoutException {
		List<SyncRegistrationDTO> syncDtoList = new ArrayList<>();
		LinkedHashMap<String, Object> respObj = new LinkedHashMap<>();
		LinkedHashMap<String, Object> msg = new LinkedHashMap<>();
		msg.put("registrationId", "123456789");
		msg.put("errors", "errors");
		List<LinkedHashMap<String, Object>> mapList = new ArrayList<>();
		mapList.add(msg);
		respObj.put("errors", mapList);
		RegistrationPacketSyncDTO registrationPacketSyncDTO = new RegistrationPacketSyncDTO();
		syncDtoList.add(new SyncRegistrationDTO());
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(respObj);
		Mockito.when(aesEncryptionService.encrypt(javaObjectToJsonString(registrationPacketSyncDTO).getBytes())).thenReturn("aes".getBytes());
		assertTrue(packetSynchServiceImpl.syncPacketsToServer("123456789", "System").getErrorResponseDTOs()!=null);
	}
	
	@Test
	public void testfetchSynchedPacket() {
		Registration reg=new Registration();
		reg.setId("123456789");
		Mockito.when(registrationDAO.getRegistrationById(Mockito.anyString(), Mockito.anyString())).thenReturn(reg);
		assertTrue(packetSynchServiceImpl.fetchSynchedPacket("123456789"));
	}
}
