package io.mosip.registrationprocessor.mosip_regprocessor_rest_client.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registrationprocessor.mosip_regprocessor_rest_client.dto.RequestDetails;

/**
 * The Class RegProcGenericRestClient.
 *
 * @param <T> the generic type
 * @param <V> the value type
 * @author Rishabh Keshari
 */
public class RegProcGenericRestClient<T,V>  {
	
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
	//	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(requestDetails.getRequestUrl()).queryParam("registrationIds", registrationIds);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(requestDetails.getRequestUrl());
		 

		
		HttpEntity<T> entity = new HttpEntity<>(requestedData, headers);
		 ResponseEntity<V> response=restTemplate.exchange(builder.toUriString(), requestDetails.getRequestMethodType(), entity, reponseType);
		 return response.getBody();
	}
	
	/*
	@SuppressWarnings("unchecked")
	public void run() {
		RestTemplate template = new RestTemplate();
		String url = "http://localhost:8080/v0.1/registration-processor/registration-status/registrationstatus?registrationIds=2018782130000224092018121229";
		HttpHeaders headers = new HttpHeaders();
		//headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<List<RegistrationStatusDto>> entity = new HttpEntity<List<RegistrationStatusDto>>(headers);
		List<RegistrationStatusDto> list = new ArrayList<>();
		ResponseEntity<? extends List> response = template.exchange(url,HttpMethod.GET,entity,list.getClass());
        System.out.println(response);
	
	} 
	
	public void generic(String url) {
		RestTemplate template = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<R> entity = new HttpEntity<R>(headers);
		List<T> list = new ArrayList<>();
		ResponseEntity<? extends List> response = template.exchange(url,HttpMethod.GET,entity,list.getClass());

        System.out.println(response);

	}*/
	
	

}
