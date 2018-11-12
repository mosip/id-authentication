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
public class GenericRestClient{

	public <T> T genericGETClient(String getURI, String queryParam, String queryParamValue, Class<?> responseType) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(getURI).queryParam(queryParam,
				queryParamValue);
		RestTemplate restTemplate = new RestTemplate();
		T result = (T) restTemplate.getForObject(builder.buildAndExpand().toUri(), responseType);
		return result;
	}

	public <T> T genericPostClient(String uri, T responseType,Class<?> responseClass) {
		RestTemplate restTemplate = new RestTemplate();
		T result = (T) restTemplate.postForObject(uri, responseType, responseClass);
		return result;

	}
	
	
	
}
