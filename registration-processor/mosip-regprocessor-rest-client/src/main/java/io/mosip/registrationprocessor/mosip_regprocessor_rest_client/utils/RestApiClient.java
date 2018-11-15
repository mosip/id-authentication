package io.mosip.registrationprocessor.mosip_regprocessor_rest_client.utils;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import io.mosip.kernel.auditmanager.request.AuditRequestDto;

/**
 * The Class RestApiClient.
 * 
 * @author Rishabh Keshari
 */
@Component
public class RestApiClient{
	private String auditManagerServiceHost="http://localhost:8081/auditmanager/audits";
	RestTemplate restTemplate = new RestTemplate();
	
	/**
	 * Gets the api.
	 *
	 * @param <T> the generic type
	 * @param getURI the get URI
	 * @param responseType the response type
	 * @return the api
	 */
	public <T> T getApi(String getURI , Class<?> responseType) {
		T result = (T) restTemplate.getForObject(getURI, responseType);
		return result;
	}

	/**
	 * Post api.
	 *
	 * @param <T> the generic type
	 * @param uri the uri
	 * @param requestType the request type
	 * @param responseClass the response class
	 * @return the t
	 */
	public <T> T postApi(String uri, T requestType,Class<?> responseClass) {
		T result = (T) restTemplate.postForObject(uri, requestType, responseClass);
		return result;
	}



}
