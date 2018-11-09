package io.mosip.registrationprocessor.mosip_regprocessor_rest_client.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import io.mosip.registrationprocessor.mosip_regprocessor_rest_client.dto.RequestDetails;

/**
 * The Class RegProcGenericRestClient.
 *
 * @param <T> the generic type
 * @param <V> the value type
 * @author Rishabh Keshari
 */
public class RegProcGenericRestClient<T, V>  {
	
	/** The rest template. */
	private RestTemplate restTemplate = new RestTemplate();

	/**
	 * Execute.
	 *
	 * @param requestDetails the request details
	 * @param requestedData the requested data
	 * @param errorHandler the error handler
	 * @param reponseType the reponse type
	 * @return the v
	 */
	public V execute(RequestDetails requestDetails, T requestedData, ResponseErrorHandler errorHandler, Class<V> reponseType) {

		restTemplate.setErrorHandler(errorHandler);
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<T> entity = new HttpEntity<>(requestedData, headers);
		
		ResponseEntity<V> response = restTemplate.exchange(requestDetails.getRequestUrl(), requestDetails.getRequestMethodType(), entity, reponseType);
	
		return response.getBody();
	}	

}
