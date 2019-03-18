package io.mosip.preregistration.auth.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	
	@Value("${mosip.regex.phone}")
	private String mobileRegex;
	
	@Value("${mosip.regex.email}")
	private String emailRegex;
	
	@Value("${otpChannel.mobile}")
	private String mobileChannel;
	
	@Value("${otpChannel.email}")
	private String emailChannel;
	/**
	 * This method will return the MainResponseDTO with id and version
	 * 
	 * @param mainRequestDto
	 * @return MainResponseDTO<?>
	 */
	public  MainResponseDTO<?> getMainResponseDto(MainRequestDTO<?> mainRequestDto ){
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
	
	public ResponseEntity<?> getResponseEntity(String url,HttpMethod httpMethodType,MediaType mediaType,Object body,Map<String,String> headersMap,Class<?> responseClass){
		RestTemplate restTemplate=restTemplateBuilder.build();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(mediaType);
		HttpEntity<?> request=null;
		if(headersMap != null){
			headersMap.forEach((k,v)->headers.add(k,v));
		}
		if(body != null) {
			request = new HttpEntity<>(body,headers);
		}
		else {
			request = new HttpEntity<>(headers);
		}
		
		//HttpEntity<?> request = new HttpEntity<>(body, headers);
		return restTemplate.exchange(url,httpMethodType,request,responseClass);
		
	}
	

	public  List<String> validateUserIdAndLangCode(String userId,String langCode) {
		List<String> list=new ArrayList<>();
		if(langCode == null || userId == null) {
			return list;
		}
		if(userId.matches(mobileRegex)) {
			list.add(mobileChannel);
			}
		else if(userId.matches(emailRegex)) {
			list.add(emailChannel);
			}
		return list;
	}

	public String getCurrentResponseTime() {
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), utcDateTimePattern);

	}
}
