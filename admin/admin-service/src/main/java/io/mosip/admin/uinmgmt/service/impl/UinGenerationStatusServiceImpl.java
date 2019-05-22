package io.mosip.admin.uinmgmt.service.impl;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.admin.uinmgmt.constant.UinGenerationStatusErrorCode;
import io.mosip.admin.uinmgmt.dto.UinGenerationStatusDto;
import io.mosip.admin.uinmgmt.dto.UinGenerationStatusResponseDto;
import io.mosip.admin.uinmgmt.exception.UinGenerationStatusException;
import io.mosip.admin.uinmgmt.service.UinGenerationStatusService;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.util.JsonUtils;
import io.vertx.core.json.JsonObject;



@Service
public class UinGenerationStatusServiceImpl implements UinGenerationStatusService {
	
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${mosip.admin.packetstatus.api}")
	private String getPacketStatusApi;
	
	@Autowired
	ObjectMapper mapper;
	
	
	private RestTemplate getRestTemplate() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
		SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy)
				.build();
		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpClient);
		return new RestTemplate(requestFactory);

	}

	@Override
	public UinGenerationStatusResponseDto getPacketStatus(String rid) {
		/*UinGenerationStatusResponseDto respDto = null;
		UriComponentsBuilder packetStatus = UriComponentsBuilder.fromUriString(getPacketStatusApi)
		.queryParam("request", rid);
		try {
			respDto = restTemplate.getForObject(packetStatus.toUriString(), UinGenerationStatusResponseDto.class, rid);
		} catch (RestClientException e) {
			throw new UinGenerationStatusException(UinGenerationStatusErrorCode.UIN_GENERATION_STATUS_EXCEPTION.getErrorCode(),
					UinGenerationStatusErrorCode.UIN_GENERATION_STATUS_EXCEPTION.getErrorMessage(), e);
		}
		if (respDto != null && respDto.getErrors() != null && !respDto.getErrors().isEmpty()) {
			ServiceError error = respDto.getErrors().get(0);
			throw new UinGenerationStatusException(error.getErrorCode(), error.getMessage());
		}

		return respDto;*/
		
		
		//String id = "{\r\n   \"rid\": \"01006768480002820190122190830\"\r\n}";
		//ResponseEntity<String> response = null;
		RequestWrapper<List<UinGenerationStatusDto>> request = new RequestWrapper<>();
		request.setId("mosip.registration.status");
		request.setVersion("1.0");
		List<UinGenerationStatusDto> uingen = new ArrayList<>();
		UinGenerationStatusDto uin = new UinGenerationStatusDto();
		uin.setRegistrationId(rid);
		uingen.add(uin);
		request.setRequest(uingen);
//		JsonObject json= new JsonObject();
//		json.put("rid", rid);
//		String ridReq=null;
//		try {
//			ridReq=mapper.writeValueAsString(request);
//		} catch (JsonProcessingException e1) {
//		
//			e1.printStackTrace();
//		}
		String req=null;
		try {
			req = JsonUtils.javaObjectToJsonString(request);
		} catch (io.mosip.kernel.core.util.exception.JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		ResponseEntity<UinGenerationStatusResponseDto> respDto = null;
		UriComponentsBuilder packetStatus = UriComponentsBuilder.fromHttpUrl(getPacketStatusApi)
		.queryParam("request", req);
		try {
			//response = restTemplate.exchange(getPacketStatusApi, HttpMethod.GET, new HttpEntity<Object>(headers),
			//		String.class);
			//respDto = restTemplate.getForObject(packetStatus.toUriString(), UinGenerationStatusResponseDto.class);
			System.out.println(packetStatus.build().toUriString());
					respDto = restTemplate.exchange(packetStatus.build().toUriString(), HttpMethod.GET, null, UinGenerationStatusResponseDto.class);
		} catch (RestClientException e) {
			throw new UinGenerationStatusException(UinGenerationStatusErrorCode.UIN_GENERATION_STATUS_EXCEPTION.getErrorCode(),
					UinGenerationStatusErrorCode.UIN_GENERATION_STATUS_EXCEPTION.getErrorMessage(), e);
		}
		if (respDto.getBody() != null && respDto.getBody().getErrors() != null && !respDto.getBody().getErrors().isEmpty()) {
			ServiceError error = respDto.getBody().getErrors().get(0);
			throw new UinGenerationStatusException(error.getErrorCode(), error.getMessage());
		}

		return respDto.getBody();
		 
		
		
		
		
		
		
		
		/*UriComponentsBuilder packetStatus = UriComponentsBuilder.fromHttpUrl(getPacketStatusApi)
				.queryParam("request", rid);
		
		ResponseEntity<String> response = restTemplate.getForEntity(packetStatus.toUriString(), String.class);
		String responseBody=response.getBody();
		System.out.println("response = "+responseBody);
		
		
		UinGenerationStatusResponseDto respDto = null;
		UriComponentsBuilder packetStatus = UriComponentsBuilder.fromUriString(getPacketStatusApi)
		.queryParam("request", ridReq);
		try {
			respDto = restTemplate.getForObject(packetStatus.toUriString(), UinGenerationStatusResponseDto.class);
		} catch (RestClientException e) {
			throw new UinGenerationStatusException(UinGenerationStatusErrorCode.UIN_GENERATION_STATUS_EXCEPTION.getErrorCode(),
					UinGenerationStatusErrorCode.UIN_GENERATION_STATUS_EXCEPTION.getErrorMessage(), e);
		}
		if (respDto != null && respDto.getErrors() != null && !respDto.getErrors().isEmpty()) {
			ServiceError error = respDto.getErrors().get(0);
			throw new UinGenerationStatusException(error.getErrorCode(), error.getMessage());
		}

		return respDto;
		*/
		
		
		
	}

}
