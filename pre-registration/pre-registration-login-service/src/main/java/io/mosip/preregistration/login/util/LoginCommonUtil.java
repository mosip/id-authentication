package io.mosip.preregistration.login.util;

import java.io.IOException;
import java.io.StringReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;

import java.util.Properties;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.core.common.dto.AuthNResponse;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.ResponseWrapper;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.util.ValidationUtil;
import io.mosip.preregistration.login.dto.MosipUserDTO;
import io.mosip.preregistration.login.dto.User;
import io.mosip.preregistration.login.errorcodes.ErrorCodes;
import io.mosip.preregistration.login.errorcodes.ErrorMessages;
import io.mosip.preregistration.login.exception.LoginServiceException;
import io.mosip.preregistration.login.exception.ParseResponseException;

/**
 * 
 *	@author Akshay Jain
 *	@since 1.0.0
 */
@Component
public class LoginCommonUtil {
	
	/**
	 * Environment instance
	 */
	@Autowired
	private Environment env;
	
	
	@Autowired
	@Qualifier("restTemplateConfig")
	private RestTemplate restTemplate;
	
	
	@Autowired
	@Qualifier("restTemplateConfig")
	private RestTemplate restTemplate1;
	/**
	 * Logger instance
	 */
	private Logger log = LoggerConfiguration.logConfig(LoginCommonUtil.class);
	
	@Autowired
	private  ObjectMapper objectMapper;
	
	
	@Value("${otpChannel.mobile}")
	private String mobileChannel;
	
	@Value("${otpChannel.email}")
	private String emailChannel;
	
	@Value("${sendOtp.resource.url}")
	private String sendOtpResourceUrl;
	/**
	 * This method will return the MainResponseDTO with id and version
	 * 
	 * @param mainRequestDto
	 * @return MainResponseDTO<?>
	 */
	public  MainResponseDTO<?> getMainResponseDto(MainRequestDTO<?> mainRequestDto ){
		log.info("sessionId", "idType", "id", "In getMainResponseDTO method of Login Common Util");
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
	 * @throws KeyStoreException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 * @throws RestClientException 
	 */
	
	public ResponseEntity<?> callAuthService(String url,HttpMethod httpMethodType,MediaType mediaType,Object body,Map<String,String> headersMap,Class<?> responseClass) {
		ResponseEntity<?> response=null;
		try {
		log.info("sessionId", "idType", "id", "In getResponseEntity method of Login Common Util");
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
		log.info("sessionId", "idType", "id", "In call to kernel rest service :"+url);
		response=getRestTemplate().exchange(url,httpMethodType,request,responseClass);
		}
		catch(Exception ex) {
			throw new RestClientException("rest call failed");
		}
		return response;
		
	}
	
	/**
	 * This method provides validation of the userid and returns the otpChannel list 
	 * @param userId
	 * @param langCode
	 * @return List<String>
	 */
	public  List<String> validateUserId(String userId) {
		log.info("sessionId", "idType", "id", "In validateUserIdandLangCode method of Login Common Util");
		List<String> list=new ArrayList<>();
		 if(userId == null || userId.isEmpty()) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_AUTH_008.getCode(), ErrorMessages.INVALID_REQUEST_USERID.getMessage(),null);
		}
		if(ValidationUtil.phoneValidator(userId)) {
			list.add(mobileChannel);
			return list;
			}
		else if(ValidationUtil.emailValidator(userId)) {
			list.add(emailChannel);
			return list;
			}
		
		throw new InvalidRequestParameterException(ErrorCodes.PRG_AUTH_008.getCode(), ErrorMessages.INVALID_REQUEST_USERID.getMessage(),null);
	}
		
	/**
	 * This method will validate the null check for incoming request
	 * @param mainRequest
	 * @return
	 */
	
	public boolean validateRequest(MainRequestDTO<?> mainRequest) {
		log.info("sessionId", "idType", "id", "In validateRequest method of Login Common Util");
	 if(mainRequest.getId() == null  ) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_AUTH_004.getCode(), ErrorMessages.INVALID_REQUEST_ID.getMessage(),null);
		}
		else if (mainRequest.getRequest() == null) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_AUTH_007.getCode(), ErrorMessages.INVALID_REQUEST_BODY.getMessage(),null);
		}
		else if (mainRequest.getRequesttime() == null) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_AUTH_006.getCode(), ErrorMessages.INVALID_REQUEST_DATETIME.getMessage(),null);
		}
		else if (mainRequest.getVersion() == null) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_AUTH_005.getCode(), ErrorMessages.INVALID_REQUEST_VERSION.getMessage(),null);
		}
		return true;
	}
	
	/**
	 * This method will validate the otp and userid for null values
	 * @param user
	 */
	public void validateOtpAndUserid(User user) {
		log.info("sessionId", "idType", "id", "In validateOtpAndUserid method of Login Common Util");
		if(user.getUserId() == null) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_AUTH_008.getCode(), ErrorMessages.INVALID_REQUEST_USERID.getMessage(),null);
		}
		else if (user.getOtp() == null) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_AUTH_010.getCode(), ErrorMessages.INVALID_REQUEST_OTP.getMessage(),null);
		}
	}
	
	/**
	 * This method will read value from response body and covert it into requested class object
	 * @param serviceResponseBody
	 * @return
	 */
	public ResponseWrapper<?> requestBodyExchange(String serviceResponseBody) throws ParseResponseException {
		try {
			return objectMapper.readValue(serviceResponseBody, ResponseWrapper.class);
		} catch (IOException e) {
			throw new ParseResponseException(ErrorCodes.PRG_AUTH_011.getCode(), ErrorMessages.ERROR_WHILE_PARSING.getMessage(),null);
			
		} 
	}
	
	/**
	 * This method is used to parse string to required object
	 * @param serviceResponseBody
	 * @param responseClass
	 * @return
	 * @throws ParseResponseException
	 */
	public Object requestBodyExchangeObject(String serviceResponseBody,Class<?> responseClass) throws ParseResponseException{
		try {
			objectMapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			return objectMapper.readValue(serviceResponseBody,responseClass);
		} catch (IOException e) {
			throw new ParseResponseException(ErrorCodes.PRG_AUTH_011.getCode(), ErrorMessages.ERROR_WHILE_PARSING.getMessage(),null);
			
		} 
	}
	
	/**
	 * This method is used for parse object to string
	 * @param response
	 * @return
	 */
	public String responseToString(Object response) {
		try {
			return objectMapper.writeValueAsString(response);
		} catch (JsonProcessingException e) {
			
			throw new ParseResponseException("","",null);
		}
	}
	
	/**
	 * This method is used for parsing string to properties
	 * @param s
	 * @return
	 * @throws IOException
	 */
	public Properties parsePropertiesString(String s) throws IOException {
		final Properties p = new Properties();
		p.load(new StringReader(s));
		return p;
	}

	/**
	 * This method is used config rest call
	 * @param filname
	 * @return
	 */
	public String getConfig(String filname) {
		String configServerUri = env.getProperty("spring.cloud.config.uri");
		String configLabel = env.getProperty("spring.cloud.config.label");
		String configProfile = env.getProperty("spring.profiles.active");
		String configAppName = env.getProperty("spring.cloud.config.name");
		StringBuilder uriBuilder= new StringBuilder();

		uriBuilder.append(configServerUri + "/").append(configAppName + "/").append(configProfile + "/")
				.append(configLabel + "/").append(filname);
		log.info("sessionId", "idType", "id", " URL in login service util of configRestCall" + uriBuilder);
		return restTemplate.getForObject(uriBuilder.toString(), String.class);

	}

	/**This method is used for create key value pair from congif file
	 * @param prop
	 * @param configParamMap
	 * @param reqParams
	 */
	public void getConfigParams(Properties prop, Map<String, String> configParamMap, List<String> reqParams) {
		for (Entry<Object, Object> e : prop.entrySet()) {
			if (reqParams.contains(String.valueOf(e.getKey()))) {
				configParamMap.put(String.valueOf(e.getKey()), e.getValue().toString());
			}

		}
	}
	
	/**
	 * This method is used for create request map
	 * @param requestDto
	 * @return
	 */
	public Map<String, String> createRequestMap(MainRequestDTO<?> requestDto) {
		log.info("sessionId", "idType", "id", "In prepareRequestMap method of Login Service Util");
		Map<String, String> requestMap = new HashMap<>();
		requestMap.put("id", requestDto.getId());
		requestMap.put("version", requestDto.getVersion());
		if(!(requestDto.getRequesttime()==null || requestDto.getRequesttime().toString().isEmpty())) {
			LocalDate date = requestDto.getRequesttime().toInstant().atZone(ZoneId.of("UTC")).toLocalDate();
			requestMap.put("requesttime", date.toString());
		}
		else {
		requestMap.put("requesttime",null);
		}
		requestMap.put("request", requestDto.getRequest().toString());
		return requestMap;
	}

	public String getUserDetailsFromToken(Map<String,String> authHeader) {
		String url=sendOtpResourceUrl+"/authorize/validateToken";
		ResponseEntity<String> response=(ResponseEntity<String>) callAuthService(url, HttpMethod.POST, MediaType.APPLICATION_JSON, null, authHeader,String.class);
		ResponseWrapper<?> responseKernel=requestBodyExchange(response.getBody());
		if(! (responseKernel.getErrors()==null)) {
			log.error("sessionId", "idType", "id", "Invalid Token");
			return null;
		}
		MosipUserDTO userDetailsDto=(MosipUserDTO) requestBodyExchangeObject(responseToString(responseKernel.getResponse()), MosipUserDTO.class);
		
		return userDetailsDto.getUserId();
	}
	
	private RestTemplate getRestTemplate()
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		
			TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

			SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
					.loadTrustMaterial(null, acceptingTrustStrategy).build();

			SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

			CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

			requestFactory.setHttpClient(httpClient);
			return new RestTemplate(requestFactory);
	}

}
