package io.mosip.registration.test.service.packet.encryption;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.RegistrationRepository;
import io.mosip.registration.service.packet.impl.PacketUploadServiceImpl;
import io.mosip.registration.util.restclient.RequestHTTPDTO;
import io.mosip.registration.util.restclient.RestClientUtil;

public class PacketUploadServiceTest {
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@Mock
	private RegistrationDAO registrationDAO;
	
	@Mock
	private RequestHTTPDTO requestHTTPDTO;
	
	@Mock
	private RestClientUtil restClientUtil;

	@Mock
	private Environment environment;
	
	@Mock
	private RegistrationRepository registrationRepository;
	
	@InjectMocks
	private PacketUploadServiceImpl packetUploadServiceImpl;

	@Before
	public void initialize() {
		ReflectionTestUtils.setField(packetUploadServiceImpl, "urlPath", "http://104.211.209.102:8080/v0.1/registration-processor/packet-receiver/registrationpackets");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetSynchedPackets() {
		Registration registration=new Registration();
		List<Registration> regList=new ArrayList<>();
		registration.setId("1111111111");
		regList.add(registration);
		Mockito.when(registrationDAO.getRegistrationByStatus(Mockito.anyList())).thenReturn(regList);
		assertEquals(regList, packetUploadServiceImpl.getSynchedPackets());
	}
	
	@Test
	public void testPushPacket() throws URISyntaxException, RegBaseCheckedException, HttpClientErrorException, HttpServerErrorException, ResourceAccessException, SocketTimeoutException {
		LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		File f=new File("");
		map.add("file", new FileSystemResource(f));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		Mockito.when(environment.getProperty(Mockito.anyString())).thenReturn("http://104.211.209.102:8080/v0.1/registration-processor/packet-receiver/registrationpackets");
		HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
		requestHTTPDTO.setHttpEntity(requestEntity);
		requestHTTPDTO.setClazz(Object.class);
		requestHTTPDTO.setUri(new URI("http://104.211.209.102:8080/v0.1/registration-processor/packet-receiver/registrationpackets"));
		requestHTTPDTO.setHttpMethod(HttpMethod.POST);
		Object respObj = new Object();
		Mockito.when(restClientUtil.invoke(Mockito.anyObject())).thenReturn(respObj);
		assertEquals(respObj, packetUploadServiceImpl.pushPacket(f));
	}

	@Test
	public void testUpdateStatus() {
		List<Registration> packetList=new ArrayList<>();
		Registration registration = new Registration();
		packetList.add(registration);
		Mockito.when(registrationDAO.updateRegStatus(Mockito.anyObject())).thenReturn(registration);
		assertTrue(packetUploadServiceImpl.updateStatus(packetList));
	}
	
	@Test(expected=RegBaseCheckedException.class)
	public void testHttpException() throws URISyntaxException, RegBaseCheckedException, HttpClientErrorException, HttpServerErrorException, ResourceAccessException, SocketTimeoutException {
		File f=new File("");
		Object respObj = new Object();
		Mockito.when(restClientUtil.invoke(Mockito.anyObject()))
		.thenThrow(new HttpClientErrorException(HttpStatus.ACCEPTED));
		assertEquals(respObj, packetUploadServiceImpl.pushPacket(f));
	}
	
	@Test(expected=RegBaseUncheckedException.class)
	public void testRuntimeException() throws URISyntaxException, RegBaseCheckedException, HttpClientErrorException, HttpServerErrorException, ResourceAccessException, SocketTimeoutException {
		File f=new File("");
		Object respObj = new Object();
		Mockito.when(restClientUtil.invoke(Mockito.anyObject()))
		.thenThrow(new RuntimeException());
		assertEquals(respObj, packetUploadServiceImpl.pushPacket(f));
	}

}
