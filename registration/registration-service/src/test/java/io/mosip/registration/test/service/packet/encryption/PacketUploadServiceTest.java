package io.mosip.registration.test.service.packet.encryption;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.registration.dao.RegTransactionDAO;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.entity.RegistrationTransaction;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.PacketUploadService;
import io.mosip.registration.util.restclient.RequestHTTPDTO;
import io.mosip.registration.util.restclient.RestClientUtil;

public class PacketUploadServiceTest {
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@Mock
	private RegistrationDAO registrationDAO;
	
	@Mock
	private RegTransactionDAO regTransactionDAO;
	
	@Mock
	private RequestHTTPDTO requestHTTPDTO;
	
	@Mock
	private RestClientUtil restClientUtil;
	
	@InjectMocks
	private PacketUploadService packetUploadService;

	@Mock
	private MosipLogger logger;
	
	@Test
	public void testGetSynchedPackets() {
		ReflectionTestUtils.setField(packetUploadService, "LOGGER", logger);
		List<String> PACKET_STATUS = Arrays.asList("I", "H", "A", "S");
		Registration registration=new Registration();
		List<Registration> regList=new ArrayList<>();
		registration.setId("1111111111");
		regList.add(registration);
		Mockito.when(registrationDAO.getRegistrationByStatus(PACKET_STATUS)).thenReturn(regList);
		assertEquals(regList, registrationDAO.getRegistrationByStatus(PACKET_STATUS));
	}
	
	@Test
	public void testPushPacket() throws URISyntaxException, RegBaseCheckedException {
		ReflectionTestUtils.setField(packetUploadService, "LOGGER", logger);
		LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		File f=new File("");
		map.add("file", new FileSystemResource(f));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
		requestHTTPDTO.setHttpEntity(requestEntity);
		requestHTTPDTO.setClazz(Object.class);
		requestHTTPDTO.setUri(new URI("http://104.211.209.102:8080/v0.1/registration-processor/packet-receiver/registrationpackets"));
		requestHTTPDTO.setHttpMethod(HttpMethod.POST);
		Object respObj = new Object();
		Mockito.when(restClientUtil.invoke(Mockito.anyObject())).thenReturn(respObj);
		assertEquals(respObj, packetUploadService.pushPacket(f));
	}

	@Test
	public void testUpdateStatus() {
		ReflectionTestUtils.setField(packetUploadService, "LOGGER", logger);
		Map<String, String> packetStatus= new HashMap<>();
		packetStatus.put("1111111111", "P");
		packetStatus.put("2222222", "E");
		Registration registration = new Registration();
		RegistrationTransaction registrationTransaction=new RegistrationTransaction();
		List<RegistrationTransaction> registrationTransactions = new ArrayList<>();
		registrationTransactions.add(registrationTransaction);
		Mockito.when(registrationDAO.updateRegStatus(Mockito.anyString(), Mockito.anyString())).thenReturn(registration);
		Mockito.when(regTransactionDAO.buildRegTrans(Mockito.anyString(), Mockito.anyString())).thenReturn(registrationTransaction);
		Mockito.when(regTransactionDAO.insertPacketTransDetails(registrationTransactions)).thenReturn(registrationTransactions);
		assertTrue(packetUploadService.updateStatus(packetStatus));
	}

}
