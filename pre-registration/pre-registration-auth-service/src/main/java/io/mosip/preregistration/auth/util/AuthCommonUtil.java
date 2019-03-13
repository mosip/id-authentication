package io.mosip.preregistration.auth.util;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.auth.dto.MainRequestDTO;
import io.mosip.preregistration.auth.dto.MainResponseDTO;

/**
 * 
 *	@author Akshay Jain
 *	@since 1.0.0
 */
@Component
public class AuthCommonUtil {
	
	
	@Value("${mosip.utc-datetime-pattern}")
	private String utcDateTimePattern;
	
	/**
	 * Autowired reference for {@link #restTemplateBuilder}
	 */
	@Autowired
	private RestTemplateBuilder restTemplateBuilder;
	
	/**
	 * This method will return the MainResponseDTO with id and version
	 * 
	 * @param mainRequestDto
	 * @return MainResponseDTO<?>
	 */
	public static MainResponseDTO<?> getMainResponseDto(MainRequestDTO<?> mainRequestDto ){
		MainResponseDTO<?> response=new MainResponseDTO<>();
		if(mainRequestDto.getRequest()==null) {
			return response;
		}
		response.setId(mainRequestDto.getId());
		response.setVersion(mainRequestDto.getVersion());
		
		return response;
	}
	
	/**
	 * This method return ResponseEntity for the rest call made to the designated url
	 * 
	 * @param url
	 * @param mediaType
	 * @param body
	 * @param responseClass
	 * @return ResponseEntity<?>
	 */
	
	public ResponseEntity<?> getResponseEntity(String url,HttpMethod httpMethodType,MediaType mediaType,Object body,Class<?> responseClass){
		RestTemplate restTemplate=restTemplateBuilder.build();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(mediaType);
		HttpEntity<?> request = new HttpEntity<>(body, headers);
		return restTemplate.exchange(url,httpMethodType, request,responseClass);
		
	}
	
	public String getCurrentResponseTime() {
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), utcDateTimePattern);
	}
}
