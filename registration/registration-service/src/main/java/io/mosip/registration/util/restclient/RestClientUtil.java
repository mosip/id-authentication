package io.mosip.registration.util.restclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import io.mosip.registration.exception.RegBaseUncheckedException;

/**
 * This is a general method which gives the response for all httpmethod
 * designators
 * 
 * @author Yaswanth S
 * @since 1.0.0
 *
 */
@Service
public class RestClientUtil {

	/**
	 * Rest Template is a interaction with HTTP servers and enforces RESTful systems
	 */
		 @Autowired 
		 RestTemplate restTemplate;
		 
	/**
	 * Actual exchange using rest template
	 * 
	 * @param requestDto
	 * @return ResponseEntity<?> response entity obtained from api
	 *  @throws RegBaseUncheckedException when exception from server
	 */
	public Object invoke(RequestHTTPDTO requestHTTPDTO) throws HttpClientErrorException {
		ResponseEntity<?> responseEntity = null;
		Object responseBody=null;
			responseEntity = restTemplate.exchange(requestHTTPDTO.getUri(), requestHTTPDTO.getHttpMethod(),
					requestHTTPDTO.getHttpEntity(), requestHTTPDTO.getClazz());
			responseBody=responseEntity.getBody();
		return responseBody;

	}

}
