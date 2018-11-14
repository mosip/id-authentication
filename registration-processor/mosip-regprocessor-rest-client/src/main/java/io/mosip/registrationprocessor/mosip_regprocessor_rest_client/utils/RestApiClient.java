package io.mosip.registrationprocessor.mosip_regprocessor_rest_client.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * The Class RegProcGenericRestClient.
 *
 * @param <T>
 *            the generic type
 * @param <V>
 *            the value type
 * @author Rishabh Keshari
 */
@Component
public class RestApiClient{

	public <T> T getApi(String getURI , Class<?> responseType) {
		RestTemplate restTemplate = new RestTemplate();
		T result = (T) restTemplate.getForObject(getURI, responseType);
		System.out.println("output is "+result);
		System.out.println("output is "+result.toString());
		return result;
	}

	public <T> T postApi(String uri, T requestType,Class<?> responseClass) {
		RestTemplate restTemplate = new RestTemplate();
		T result = (T) restTemplate.postForObject(uri, requestType, responseClass);
		return result;

	}
	
	
	
}
