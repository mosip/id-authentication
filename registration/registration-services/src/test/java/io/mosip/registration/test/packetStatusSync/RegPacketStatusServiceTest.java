package io.mosip.registration.test.packetStatusSync;

import static io.mosip.kernel.core.util.JsonUtils.javaObjectToJsonString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.core.util.exception.JsonProcessingException;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.context.SessionContext.UserContext;
import io.mosip.registration.dao.RegPacketStatusDAO;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.RegistrationPacketSyncDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.packet.impl.RegPacketStatusServiceImpl;
import io.mosip.registration.service.security.AESEncryptionService;
import io.mosip.registration.service.sync.PacketSynchService;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({  HMACUtils.class, ApplicationContext.class, SessionContext.class })
public class RegPacketStatusServiceTest {
	private Map<String, Object> applicationMap = new HashMap<>();

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock
	private ServiceDelegateUtil serviceDelegateUtil;
	@Mock
	private RegPacketStatusDAO packetStatusDao;
	@Mock
	private PacketSynchService packetSynchService;
	@Mock
	private AESEncryptionService aesEncryptionService;
	@InjectMocks
	private RegPacketStatusServiceImpl packetStatusService;

	@Mock
	RegistrationDAO registrationDAO;

	@Before
	public void initiate() throws Exception{
		PowerMockito.mockStatic(HMACUtils.class);
		
		applicationMap.put(RegistrationConstants.REG_DELETION_CONFIGURED_DAYS, "5");
		applicationMap.put("PRIMARY_LANGUAGE", "ENG");

		ApplicationContext.setApplicationMap(applicationMap);
		UserContext userContext = Mockito.mock(SessionContext.UserContext.class);
		PowerMockito.mockStatic(SessionContext.class);
		PowerMockito.doReturn(userContext).when(SessionContext.class, "userContext");
		PowerMockito.when(SessionContext.userContext().getUserId()).thenReturn("mosip");

	}

	@AfterClass
	public static void destroy() {
		SessionContext.destroySession();
	}

	@Test
	public void packetSyncStatusSuccessTest()
			throws HttpClientErrorException, RegBaseCheckedException, SocketTimeoutException {
		List<LinkedHashMap<String, String>> registrations = new ArrayList<>();
		LinkedHashMap<String, String> registration = new LinkedHashMap<>();
		registration.put("registrationId", "12345");
		registration.put("statusCode", RegistrationConstants.PACKET_STATUS_CODE_PROCESSED);
		registrations.add(registration);

		LinkedHashMap<String, Object> response = new LinkedHashMap<>();
		response.put(RegistrationConstants.PACKET_STATUS_READER_RESPONSE, registrations);

		LinkedHashMap<String, String> registration12 = new LinkedHashMap<>();

		registration12.put("registrationId", "12345");
		registration12.put("statusCode", RegistrationConstants.PACKET_STATUS_CODE_PROCESSED + "123");
		registrations.add(registration12);

		List<Registration> list = new LinkedList<>();
		Registration regis = new Registration();
		regis.setId("12345");
		regis.setAckFilename("..//PacketStore/02-Jan-2019/2018782130000102012019115112_Ack.png");
		regis.setClientStatusCode(RegistrationConstants.PACKET_STATUS_CODE_PROCESSED);
		list.add(regis);

		when(packetStatusDao.getPacketIdsByStatusUploaded()).thenReturn(list);

		when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.anyMap(), Mockito.anyString())).thenReturn(response);
		Assert.assertNotNull(packetStatusService.packetSyncStatus("System").getSuccessResponseDTO());

		when(packetStatusDao.update(Mockito.any())).thenThrow(RuntimeException.class);
		packetStatusService.packetSyncStatus("System");

	}
	
	@Test
	public void packetSyncStatusSuccessTestWithEmptyPackets()
			throws HttpClientErrorException, RegBaseCheckedException, SocketTimeoutException {
		List<LinkedHashMap<String, String>> registrations = new ArrayList<>();
		LinkedHashMap<String, String> registration = new LinkedHashMap<>();
		registration.put("registrationId", "12345");
		registration.put("statusCode", RegistrationConstants.PACKET_STATUS_CODE_PROCESSED);
		registrations.add(registration);

		LinkedHashMap<String, Object> response = new LinkedHashMap<>();
		response.put(RegistrationConstants.PACKET_STATUS_READER_RESPONSE, registrations);

		LinkedHashMap<String, String> registration12 = new LinkedHashMap<>();

		registration12.put("registrationId", "12345");
		registration12.put("statusCode", RegistrationConstants.PACKET_STATUS_CODE_PROCESSED + "123");
		registrations.add(registration12);

		List<Registration> list = new LinkedList<>();
		when(packetStatusDao.getPacketIdsByStatusUploaded()).thenReturn(list);

		when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.anyMap(), Mockito.anyString())).thenReturn(response);
		Assert.assertNotNull(packetStatusService.packetSyncStatus("System").getSuccessResponseDTO());

		when(packetStatusDao.update(Mockito.any())).thenThrow(RuntimeException.class);
		packetStatusService.packetSyncStatus("System");
	}

	@Test
	public void packetSyncStatusExceptionTest()
			throws HttpClientErrorException, RegBaseCheckedException, SocketTimeoutException {
		
		List<Registration> list = new LinkedList<>();
		Registration regis = new Registration();
		regis.setId("12345");
		regis.setAckFilename("..//PacketStore/02-Jan-2019/2018782130000102012019115112_Ack.png");
		regis.setClientStatusCode(RegistrationConstants.PACKET_STATUS_CODE_PROCESSED);
		list.add(regis);
		when(packetStatusDao.getPacketIdsByStatusUploaded()).thenReturn(list);

		when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.anyMap(), Mockito.anyString())).thenThrow(SocketTimeoutException.class);
		Assert.assertNotNull(packetStatusService.packetSyncStatus("System").getErrorResponseDTOs());

		packetStatusService.packetSyncStatus("System");
	}
	
	@Test
	public void packetSyncStatusRuntimeExceptionTest()
			throws HttpClientErrorException, RegBaseCheckedException, SocketTimeoutException {
		
		List<Registration> list = new LinkedList<>();
		Registration regis = new Registration();
		regis.setId("12345");
		regis.setAckFilename("..//PacketStore/02-Jan-2019/2018782130000102012019115112_Ack.png");
		regis.setClientStatusCode(RegistrationConstants.PACKET_STATUS_CODE_PROCESSED);
		list.add(regis);
		when(packetStatusDao.getPacketIdsByStatusUploaded()).thenReturn(list);

		when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.anyMap(), Mockito.anyString())).thenThrow(RuntimeException.class);
		Assert.assertNotNull(packetStatusService.packetSyncStatus("System").getErrorResponseDTOs());

		packetStatusService.packetSyncStatus("System");
	}
	
	@Test
	public void packetSyncStatusFailureTest()
			throws HttpClientErrorException, RegBaseCheckedException, SocketTimeoutException {
		List<Registration> list = new LinkedList<>();
		Registration regis = new Registration();
		regis.setId("12345");
		regis.setAckFilename("..//PacketStore/02-Jan-2019/2018782130000102012019115112_Ack.png");
		regis.setClientStatusCode(RegistrationConstants.PACKET_STATUS_CODE_PROCESSED);
		list.add(regis);

		when(packetStatusDao.getPacketIdsByStatusUploaded()).thenReturn(list);

		List<LinkedHashMap<String, String>> registrations = new ArrayList<>();

		LinkedHashMap<String, Object> response = new LinkedHashMap<>();
		response.put(RegistrationConstants.PACKET_STATUS_READER_RESPONSE, registrations);

		when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.anyMap(), Mockito.anyString())).thenReturn(response);
		Assert.assertNotNull(packetStatusService.packetSyncStatus("System").getErrorResponseDTOs());

		when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap(), Mockito.anyBoolean(), Mockito.anyString()))
				.thenThrow(HttpClientErrorException.class);
		packetStatusService.packetSyncStatus("System");
	}

	@Test
	public void deleteReRegistrationPacketsTest() {
		List<Registration> list = prepareSamplePackets();
		SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
		successResponseDTO.setMessage(RegistrationConstants.REGISTRATION_DELETION_BATCH_JOBS_SUCCESS);

		when(registrationDAO.get(Mockito.any(), Mockito.anyString())).thenReturn(list);

		Mockito.doNothing().when(packetStatusDao).delete(Mockito.any());

		assertSame(successResponseDTO.getMessage(),
				packetStatusService.deleteRegistrationPackets().getSuccessResponseDTO().getMessage());

	}

	protected List<Registration> prepareSamplePackets() {
		List<Registration> list = new LinkedList<>();
		Registration regis = new Registration();
		regis.setId("12345");
		regis.setAckFilename("..//PacketStore/02-Jan-2019/2018782130000102012019115112_Ack.png");
		regis.setClientStatusCode(RegistrationConstants.PACKET_STATUS_CODE_PROCESSED);
		regis.setStatusCode(RegistrationConstants.PACKET_STATUS_CODE_PROCESSED);
		regis.setServerStatusCode(RegistrationConstants.PACKET_STATUS_CODE_PROCESSED);

		list.add(regis);
		return list;
	}

	@Test
	public void deleteReRegistrationPacketsFailureTest() {
		List<Registration> list = prepareSamplePackets();

		when(registrationDAO.get(Mockito.any(), Mockito.anyString())).thenThrow(RuntimeException.class);

		assertSame(RegistrationConstants.REGISTRATION_DELETION_BATCH_JOBS_FAILURE,
				packetStatusService.deleteRegistrationPackets().getErrorResponseDTOs().get(0).getMessage());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void syncPacketTest() throws HttpClientErrorException, ResourceAccessException, SocketTimeoutException,
			RegBaseCheckedException, JsonProcessingException, URISyntaxException {
		PowerMockito.mockStatic(io.mosip.registration.context.ApplicationContext.class);
		when(io.mosip.registration.context.ApplicationContext.map()).thenReturn(applicationMap);
		
		List<Registration> packetsToBeSynched = new ArrayList<>();
		Registration reg = new Registration();
		reg.setId("123456");
		reg.setAckFilename("10001100010025920190430051904_Ack.html");
		reg.setStatusCode("NEW");
		packetsToBeSynched.add(reg);
		Mockito.when(registrationDAO.getPacketsToBeSynched(Mockito.anyList())).thenReturn(packetsToBeSynched);
		ResponseDTO responseDTO = new ResponseDTO();
		SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
		Map<String, Object> otherAttributes = new HashMap<>();
		otherAttributes.put("123456", "Success");
		successResponseDTO.setOtherAttributes(otherAttributes);
		responseDTO.setSuccessResponseDTO(successResponseDTO);
		Mockito.when(packetSynchService.syncPacketsToServer(Mockito.anyObject(), Mockito.anyString()))
				.thenReturn(responseDTO);
		Mockito.when(packetSynchService.updateSyncStatus(Mockito.anyList())).thenReturn(true);
		RegistrationPacketSyncDTO registrationPacketSyncDTO = new RegistrationPacketSyncDTO();
		ApplicationContext.map().get(RegistrationConstants.PRIMARY_LANGUAGE);
		Mockito.when(aesEncryptionService.encrypt(javaObjectToJsonString(registrationPacketSyncDTO).getBytes()))
				.thenReturn("aes".getBytes());
		Mockito.when(HMACUtils.generateHash(Mockito.anyString().getBytes())).thenReturn("asa".getBytes());
		assertEquals("Success", packetStatusService.syncPacket("System").getSuccessResponseDTO().getMessage());
	}

	@Test
	public void deleteAllProcessedRegPacketsTest() {
		List<Registration> list = prepareSamplePackets();
		Mockito.when(
				registrationDAO.findByServerStatusCodeIn(RegistrationConstants.PACKET_STATUS_CODES_FOR_REMAPDELETE))
				.thenReturn(list);
		packetStatusService.deleteAllProcessedRegPackets();

	}
}
