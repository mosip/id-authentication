package io.mosip.resident.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.resident.constant.ApiName;
import io.mosip.resident.dto.AutnTxnResponseDto;
import io.mosip.resident.exception.ApisResourceAccessException;

@RunWith(MockitoJUnitRunner.class)
public class ResidentServiceRestClientTests {
	@InjectMocks
	ResidentServiceRestClient residentServiceRestClient=new ResidentServiceRestClient();
	
	@Mock
	RestTemplateBuilder builder;

	@Mock
	Environment environment;
	
	RestTemplate restTemplate;
	
	
	
	@Before 
	public void setup() {
		 restTemplate = mock( RestTemplate.class);
		
		 
		
	}
	
	@Test
	public void testgetApi() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, ApisResourceAccessException {
		AutnTxnResponseDto autnTxnResponseDto=new AutnTxnResponseDto();
		autnTxnResponseDto.setId("ancd");
		ResponseEntity<AutnTxnResponseDto> obj=new ResponseEntity<AutnTxnResponseDto>(autnTxnResponseDto, HttpStatus.OK);
		URI uri=UriComponentsBuilder.fromUriString("https://int.mosip.io/individualIdType/UIN/individualId/1234").build(false).encode().toUri();
		ResidentServiceRestClient client=Mockito.spy(residentServiceRestClient);
		doReturn(restTemplate).when(client).getRestTemplate();
		when(restTemplate.exchange(any(URI.class), any(HttpMethod.class),any(),Matchers.<Class<AutnTxnResponseDto>>any())).
		thenReturn(obj);
		
		assertTrue(client.getApi(uri, AutnTxnResponseDto.class, "abcde").toString().contains("ancd"));
	}
	@Test(expected=ApisResourceAccessException.class)
	public void testgetApiException() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, ApisResourceAccessException {
		AutnTxnResponseDto autnTxnResponseDto=new AutnTxnResponseDto();
		autnTxnResponseDto.setId("ancd");
		
		URI uri=UriComponentsBuilder.fromUriString("https://int.mosip.io/individualIdType/UIN/individualId/1234").build(false).encode().toUri();
		ResidentServiceRestClient client=Mockito.spy(residentServiceRestClient);
		doReturn(restTemplate).when(client).getRestTemplate();
		when(restTemplate.exchange(any(URI.class), any(HttpMethod.class),any(),Matchers.<Class<AutnTxnResponseDto>>any())).
		thenThrow(new RestClientException(""));
		
		client.getApi(uri, AutnTxnResponseDto.class, "abcde");
	}
	
	@Test
	public void testgetApiObject() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, ApisResourceAccessException {
		AutnTxnResponseDto autnTxnResponseDto=new AutnTxnResponseDto();
		autnTxnResponseDto.setId("ancd");
		
		when(environment.getProperty(any(String.class))).thenReturn("https://int.mosip.io/");
		ResidentServiceRestClient client=Mockito.spy(residentServiceRestClient);
		doReturn(autnTxnResponseDto).when(client).getApi(any(),any(),any());
		List<String> list=new ArrayList<>();
		list.add("individualIdType");
		list.add("UIN");
		list.add("individualId");
		list.add("1234");
		
		
		assertTrue(client.getApi(ApiName.INTERNALAUTHTRANSACTIONS,list,null,null, AutnTxnResponseDto.class, "abcde").toString().contains("ancd"));
	}
	@Test(expected=ApisResourceAccessException.class)
	public void testgetApiObjectException() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, ApisResourceAccessException {
		AutnTxnResponseDto autnTxnResponseDto=new AutnTxnResponseDto();
		autnTxnResponseDto.setId("ancd");
		
		when(environment.getProperty(any(String.class))).thenReturn("https://int.mosip.io/");
		ResidentServiceRestClient client=Mockito.spy(residentServiceRestClient);
		doThrow(new ApisResourceAccessException()).when(client).getApi(any(),any(),any());
		List<String> list=new ArrayList<>();
		list.add("individualIdType");
		list.add("UIN");
		list.add("individualId");
		list.add("1234");
		
		
		client.getApi(ApiName.INTERNALAUTHTRANSACTIONS,list,"pageFetch,pageStart","50,1", AutnTxnResponseDto.class, "abcde");
	}
	
	@Test
	public void testpostApi() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, ApisResourceAccessException {
		AutnTxnResponseDto autnTxnResponseDto=new AutnTxnResponseDto();
		autnTxnResponseDto.setId("ancd");
		
		
		ResidentServiceRestClient client=Mockito.spy(residentServiceRestClient);
		doReturn(restTemplate).when(client).getRestTemplate();
		when(restTemplate.postForObject(any(String.class),any(),Matchers.<Class<AutnTxnResponseDto>>any())).
		thenReturn(autnTxnResponseDto);
		
		assertTrue(client.postApi("https://int.mosip.io/individualIdType/UIN/individualId/1234",MediaType.APPLICATION_JSON,autnTxnResponseDto, AutnTxnResponseDto.class, "abcde").toString().contains("ancd"));
	}
	@Test(expected=ApisResourceAccessException.class)
	public void testpostApiException() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, ApisResourceAccessException {
		AutnTxnResponseDto autnTxnResponseDto=new AutnTxnResponseDto();
		autnTxnResponseDto.setId("ancd");
		
		
		ResidentServiceRestClient client=Mockito.spy(residentServiceRestClient);
		doReturn(restTemplate).when(client).getRestTemplate();
		when(restTemplate.postForObject(any(String.class),any(),Matchers.<Class<AutnTxnResponseDto>>any())).
		thenThrow(new RestClientException(""));
		
		assertTrue(client.postApi("https://int.mosip.io/individualIdType/UIN/individualId/1234",MediaType.APPLICATION_JSON,autnTxnResponseDto, AutnTxnResponseDto.class, "abcde").toString().contains("ancd"));
	}
	
	@Test
	public void testpatchApi() throws Exception {
		AutnTxnResponseDto autnTxnResponseDto=new AutnTxnResponseDto();
		autnTxnResponseDto.setId("ancd");
		
		
		ResidentServiceRestClient client=Mockito.spy(residentServiceRestClient);
		doReturn(restTemplate).when(client).getRestTemplate();
		when(restTemplate.patchForObject(any(String.class),any(),Matchers.<Class<AutnTxnResponseDto>>any())).
		thenReturn(autnTxnResponseDto);
		
		assertTrue(client.patchApi("https://int.mosip.io/individualIdType/UIN/individualId/1234",autnTxnResponseDto, AutnTxnResponseDto.class, "abcde").toString().contains("ancd"));
	}
	@Test(expected=ApisResourceAccessException.class)
	public void testpatchApiException() throws Exception {
		AutnTxnResponseDto autnTxnResponseDto=new AutnTxnResponseDto();
		autnTxnResponseDto.setId("ancd");
		
		
		ResidentServiceRestClient client=Mockito.spy(residentServiceRestClient);
		doReturn(restTemplate).when(client).getRestTemplate();
		when(restTemplate.patchForObject(any(String.class),any(),Matchers.<Class<AutnTxnResponseDto>>any())).
		thenThrow(new RestClientException(""));
		
		assertTrue(client.patchApi("https://int.mosip.io/individualIdType/UIN/individualId/1234",autnTxnResponseDto, AutnTxnResponseDto.class, "abcde").toString().contains("ancd"));
	}
	
	@Test
	public void testputApi() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, ApisResourceAccessException {
		AutnTxnResponseDto autnTxnResponseDto=new AutnTxnResponseDto();
		autnTxnResponseDto.setId("ancd");
		ResponseEntity<AutnTxnResponseDto> obj=new ResponseEntity<AutnTxnResponseDto>(autnTxnResponseDto, HttpStatus.OK);
		
		ResidentServiceRestClient client=Mockito.spy(residentServiceRestClient);
		doReturn(restTemplate).when(client).getRestTemplate();
		when(restTemplate.exchange(any(String.class), any(HttpMethod.class),any(),Matchers.<Class<AutnTxnResponseDto>>any())).
		thenReturn(obj);
		
		assertTrue(client.putApi("https://int.mosip.io/individualIdType/UIN/individualId/1234",autnTxnResponseDto, AutnTxnResponseDto.class,MediaType.APPLICATION_JSON, "abcde").toString().contains("ancd"));
	}
	@Test(expected=ApisResourceAccessException.class)
	public void testputApiException() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, ApisResourceAccessException {
		AutnTxnResponseDto autnTxnResponseDto=new AutnTxnResponseDto();
		autnTxnResponseDto.setId("ancd");
		
		
		ResidentServiceRestClient client=Mockito.spy(residentServiceRestClient);
		doReturn(restTemplate).when(client).getRestTemplate();
		when(restTemplate.exchange(any(String.class), any(HttpMethod.class),any(),Matchers.<Class<AutnTxnResponseDto>>any())).
		thenThrow(new RestClientException(""));
		
		client.putApi("https://int.mosip.io/individualIdType/UIN/individualId/1234",autnTxnResponseDto, AutnTxnResponseDto.class,MediaType.APPLICATION_JSON, "abcde");
	}
	@Test
	public void testgetRestTemplate() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		when(environment.getActiveProfiles()).thenReturn(new String[10]);
		assertEquals(residentServiceRestClient.getRestTemplate().getRequestFactory().getClass(),HttpComponentsClientHttpRequestFactory.class);
	} 
}
