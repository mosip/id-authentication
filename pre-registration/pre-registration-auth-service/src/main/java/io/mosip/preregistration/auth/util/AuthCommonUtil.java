package io.mosip.preregistration.auth.util;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.auth.dto.MainRequestDTO;
import io.mosip.preregistration.auth.dto.MainResponseDTO;
import io.mosip.preregistration.auth.dto.User;
import io.mosip.preregistration.auth.errorcodes.ErrorCodes;
import io.mosip.preregistration.auth.errorcodes.ErrorMessages;
import io.mosip.preregistration.auth.exceptions.ParseResponseException;
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
	 * Environment instance
	 */
	@Autowired
	private Environment env;
	
	
	@Autowired
	private RestTemplate restTemplate;
	
	/**
	 * Logger instance
	 */
	private Logger log = LoggerConfiguration.logConfig(AuthCommonUtil.class);
	
	private final ObjectMapper objectMapper=new ObjectMapper();
	
	
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
		log.info("sessionId", "idType", "id", "In getMainResponseDTO method of Auth Common Util");
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
		log.info("sessionId", "idType", "id", "In getResponseEntity method of Auth Common Util");
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
	
	/**
	 * This method provides validation of the userid and returns the otpChannel list 
	 * @param userId
	 * @param langCode
	 * @return List<String>
	 */
	public  List<String> validateUserIdAndLangCode(String userId,String langCode) {
		log.info("sessionId", "idType", "id", "In validateUserIdandLangCode method of Auth Common Util");
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
	
	/**
	 * This method provides current response time
	 * @return String
	 */

	public String getCurrentResponseTime() {
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), utcDateTimePattern);

	}
	
	/**
	 * This method will validate the null check for incoming request
	 * @param mainRequest
	 * @return
	 */
	
	public boolean validateRequest(MainRequestDTO<?> mainRequest) {
		log.info("sessionId", "idType", "id", "In validateRequest method of Auth Common Util");
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
	
	/**
	 * This method will validate the otp and userid for null values
	 * @param user
	 */
	public void validateOtpAndUserid(User user) {
		log.info("sessionId", "idType", "id", "In validateOtpAndUserid method of Auth Common Util");
		if(user.getUserId() == null) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_AUTH_008.getCode(), ErrorMessages.INVALID_REQUEST_USERID.getMessage());
		}
		else if (user.getOtp() == null) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_AUTH_010.getCode(), ErrorMessages.INVALID_REQUEST_OTP.getMessage());
		}
	}
	
	/**
	 * This method will read value from response body and covert it into requested class object
	 * @param serviceResponseBody
	 * @return
	 */
	public AuthNResponse requestBodyExchange(String serviceResponseBody) {
		try {
			return objectMapper.readValue(serviceResponseBody, AuthNResponse.class);
		} catch (IOException e) {
			throw new ParseResponseException(ErrorCodes.PRG_AUTH_011.getCode(), ErrorMessages.ERROR_WHILE_PARSING.getMessage());
			
		} 
	}
	
	public Properties parsePropertiesString(String s) throws IOException {
		final Properties p = new Properties();
		p.load(new StringReader(s));
		return p;
	}

	public String configRestCall(String filname) {
		String configServerUri = env.getProperty("spring.cloud.config.uri");
		String configLabel = env.getProperty("spring.cloud.config.label");
		String configProfile = env.getProperty("spring.profiles.active");
		String configAppName = env.getProperty("spring.cloud.config.name");
		StringBuilder uriBuilder= new StringBuilder();

		uriBuilder.append(configServerUri + "/").append(configAppName + "/").append(configProfile + "/")
				.append(configLabel + "/").append(filname);
		log.info("sessionId", "idType", "id", " URL in notification service util of configRestCall" + uriBuilder);
		return restTemplate.getForObject(uriBuilder.toString(), String.class);

	}

	public void getConfigParams(Properties prop, Map<String, String> configParamMap, List<String> reqParams) {
		for (Entry<Object, Object> e : prop.entrySet()) {
			if (reqParams.contains(String.valueOf(e.getKey()))) {
				configParamMap.put(String.valueOf(e.getKey()), e.getValue().toString());
			}

		}
	}

}
