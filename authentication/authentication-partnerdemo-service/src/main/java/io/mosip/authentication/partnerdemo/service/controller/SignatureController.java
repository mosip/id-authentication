package io.mosip.authentication.partnerdemo.service.controller;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.partnerdemo.service.dto.ValidateSignRequestDto;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.util.DateUtils;

@RestController
public class SignatureController {
	
	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	Decrypt decrypt;
	
	/** The encrypt URL. */
	@Value("${kernel.validate.signature-url}")
	private String validateSignUrl;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping(path = "/validateSign",consumes=MediaType.APPLICATION_JSON_VALUE)
	public String validateSign(@RequestParam String signature, 
			@RequestBody Map<String, Object> data) throws KeyManagementException, NoSuchAlgorithmException, JsonProcessingException {
		ValidateSignRequestDto validateSignRequestDto = new ValidateSignRequestDto();
		validateSignRequestDto.setData(mapper.writeValueAsString(data));
		validateSignRequestDto.setSignature(signature);
		validateSignRequestDto.setTimestamp(DateUtils.getUTCCurrentDateTime());
		
		Encrypt.turnOffSslChecking();
		RestTemplate restTemplate = new RestTemplate();
		ClientHttpRequestInterceptor interceptor = new ClientHttpRequestInterceptor() {

			@Override
			public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
					throws IOException {
				String authToken = decrypt.generateAuthToken();
				if(authToken != null && !authToken.isEmpty()) {
					request.getHeaders().set("Cookie", "Authorization=" + authToken);
				}
				return execution.execute(request, body);
			}
		};

		restTemplate.setInterceptors(Collections.singletonList(interceptor));
		
		HttpEntity<RequestWrapper<ValidateSignRequestDto>> httpEntity = new HttpEntity<>(Decrypt.createRequest(validateSignRequestDto));
		ResponseEntity<Map> response = restTemplate.exchange(validateSignUrl, HttpMethod.POST, httpEntity, Map.class);
		if(response.getStatusCode() == HttpStatus.OK && response.getBody().containsKey(IdAuthCommonConstants.RESPONSE) 
				&& Objects.nonNull(response.getBody().get(IdAuthCommonConstants.RESPONSE))) {
			return (String) ((Map<String, Object>) response.getBody().get(IdAuthCommonConstants.RESPONSE)).get(IdAuthCommonConstants.STATUS);
		}
		return "failure";
	}

}
