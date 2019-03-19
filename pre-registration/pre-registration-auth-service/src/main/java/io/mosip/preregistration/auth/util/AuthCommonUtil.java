package io.mosip.preregistration.auth.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.Date;
import java.util.HashMap;

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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.auth.dto.MainRequestDTO;
import io.mosip.preregistration.auth.dto.MainResponseDTO;
import io.mosip.preregistration.auth.dto.User;
import io.mosip.preregistration.auth.errorcodes.ErrorCodes;
import io.mosip.preregistration.auth.errorcodes.ErrorMessages;
import io.mosip.preregistration.auth.exceptions.ParseResponseException;
import io.mosip.preregistration.auth.exceptions.SendOtpFailedException;
import io.mosip.preregistration.core.common.dto.AuthNResponse;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.util.ValidationUtil;

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
	 * Logger instance
	 */
	private Logger log = LoggerConfiguration.logConfig(AuthCommonUtil.class);
	
	private final ObjectMapper objectMapper=new ObjectMapper();
	
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
		if(langCode == null ) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_AUTH_009.getCode(),ErrorMessages.INVALID_REQUEST_LANGCODE.getMessage());
		}
		else if(userId == null) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_AUTH_008.getCode(), ErrorMessages.INVALID_REQUEST_USERID.getMessage());
		}
		if(ValidationUtil.phoneValidator(userId)) {
			list.add(mobileChannel);
			return list;
			}
		else if(ValidationUtil.emailValidator(userId)) {
			list.add(emailChannel);
			return list;
			}
		
		throw new InvalidRequestParameterException(ErrorCodes.PRG_AUTH_008.getCode(), ErrorMessages.INVALID_REQUEST_USERID.getMessage());
	}

	public String getCurrentResponseTime() {
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), utcDateTimePattern);

	}
	
	
	public boolean validateRequest(MainRequestDTO<?> mainRequest) {
		
	 if(mainRequest.getId() == null  ) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_AUTH_004.getCode(), ErrorMessages.INVALID_REQUEST_ID.getMessage());
		}
		else if (mainRequest.getRequest() == null) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_AUTH_007.getCode(), ErrorMessages.INVALID_REQUEST_BODY.getMessage());
		}
		else if (mainRequest.getRequesttime() == null) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_AUTH_006.getCode(), ErrorMessages.INVALID_REQUEST_DATETIME.getMessage());
		}
		else if (mainRequest.getVersion() == null) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_AUTH_005.getCode(), ErrorMessages.INVALID_REQUEST_VERSION.getMessage());
		}
		return true;
	}
	
	public void validateOtpAndUserid(User user) {
		if(user.getUserId() == null) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_AUTH_008.getCode(), ErrorMessages.INVALID_REQUEST_USERID.getMessage());
		}
		else if (user.getOtp() == null) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_AUTH_010.getCode(), ErrorMessages.INVALID_REQUEST_OTP.getMessage());
		}
	}
	
	public AuthNResponse requestBodyExchange(String serviceResponseBody) {
		try {
			return objectMapper.readValue(serviceResponseBody, AuthNResponse.class);
		} catch (IOException e) {
			throw new ParseResponseException(ErrorCodes.PRG_AUTH_011.getCode(), ErrorMessages.ERROR_WHILE_PARSING.getMessage());
			
		} 
	}
}
