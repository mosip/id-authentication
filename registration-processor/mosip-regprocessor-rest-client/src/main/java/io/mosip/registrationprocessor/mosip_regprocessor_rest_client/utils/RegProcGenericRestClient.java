package io.mosip.registrationprocessor.mosip_regprocessor_rest_client.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import io.mosip.registrationprocessor.mosip_regprocessor_rest_client.dto.RequestDetails;

public class RegProcGenericRestClient<T, V>  {
	private RestTemplate restTemplate = new RestTemplate();

	public V execute(RequestDetails requestDetails, T requestedData, ResponseErrorHandler errorHandler, Class<V> reponseType)
			throws ResourceAccessException, Exception {

		restTemplate.setErrorHandler(errorHandler);
		HttpHeaders headers = new HttpHeaders();

		HttpEntity<T> entity = new HttpEntity<T>(requestedData, headers);
		
		ResponseEntity<V> response = restTemplate.exchange(requestDetails.getRequestUrl(), HttpMethod.GET,entity, reponseType);
	
		return response.getBody();
	
	}	

}
