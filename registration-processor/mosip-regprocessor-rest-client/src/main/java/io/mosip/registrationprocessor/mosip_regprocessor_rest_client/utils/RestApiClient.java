package io.mosip.registrationprocessor.mosip_regprocessor_rest_client.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * The Class RestApiClient.
 * 
 * @author Rishabh Keshari
 */
@Component
public class RestApiClient{

	/**
	 * Gets the api.
	 *
	 * @param <T> the generic type
	 * @param getURI the get URI
	 * @param responseType the response type
	 * @return the api
	 */
	public <T> T getApi(String getURI , Class<?> responseType) {
		RestTemplate restTemplate = new RestTemplate();
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
		RestTemplate restTemplate = new RestTemplate();
		T result = (T) restTemplate.postForObject(uri, requestType, responseClass);
		return result;

	}
	
	
	
}
