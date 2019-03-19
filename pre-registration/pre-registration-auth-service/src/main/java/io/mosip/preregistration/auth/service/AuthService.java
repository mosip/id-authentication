package io.mosip.preregistration.auth.service;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class provides different methods for login called by the controller 
 * 
 * @author M1050360
 * @since 1.0.0
 */

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.auth.dto.MainRequestDTO;
import io.mosip.preregistration.auth.dto.MainResponseDTO;
import io.mosip.preregistration.auth.dto.Otp;
import io.mosip.preregistration.auth.dto.OtpUser;
import io.mosip.preregistration.auth.dto.OtpUserDTO;
import io.mosip.preregistration.auth.dto.User;
import io.mosip.preregistration.auth.dto.UserOtp;
import io.mosip.preregistration.auth.dto.UserOtpDTO;
import io.mosip.preregistration.auth.exceptions.AuthServiceException;
import io.mosip.preregistration.auth.exceptions.util.AuthExceptionCatcher;
import io.mosip.preregistration.auth.util.AuthCommonUtil;
import io.mosip.preregistration.core.common.dto.AuthNResponse;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.util.ValidationUtil;

@Service
public class AuthService {

	private Logger log = LoggerConfiguration.logConfig(AuthService.class);
	
	/**
	 * Autowired reference for {@link #authCommonUtil}
	 */
	@Autowired
	private AuthCommonUtil authCommonUtil;
	
	/**
	 * Reference for ${sendOtp.resource.url} from property file
	 */
	@Value("${sendOtp.resource.url}")
	private String sendOtpResourceUrl;
	
	private List<String> otpChannel;
	
	@Value("${userIdType}")
	private String useridtype;
	
	@Value("${appId}")
	private String appId;
	
	Map<String, String> requiredRequestMap = new HashMap<>();
	
	/**
	 * Reference for ${mosip.prereg.app-id} from property file
	 */
//	@Value("${mosip.prereg.app-id}")
//	private String appId;
	
	/**
	 * It will fetch otp from Kernel auth service  and send to the userId provided
	 * 
	 * @param userOtpRequest
	 * @return MainResponseDTO<AuthNResponse>
	 */
	public MainResponseDTO<AuthNResponse> sendOTP(MainRequestDTO<Otp> userOtpRequest) {
		log.info("sessionId", "idType", "id",
				"In callsendOtp method of kernel service ");
		MainResponseDTO<AuthNResponse> response  = null;
		try {
			if(authCommonUtil.validateRequest(userOtpRequest)) {
				Otp otp=userOtpRequest.getRequest();
				otpChannel=authCommonUtil.validateUserIdAndLangCode(otp.getUserId(),otp.getLangCode());
				OtpUser user=new OtpUser(otp.getUserId(), otp.getLangCode(), otpChannel, appId, useridtype);
				OtpUserDTO otpUserDTO=new OtpUserDTO();
				otpUserDTO.setRequest(user);
				response  =	(MainResponseDTO<AuthNResponse>) authCommonUtil.getMainResponseDto(userOtpRequest);
				String url=sendOtpResourceUrl+"/v1.0/authenticate/sendotp";
				ResponseEntity<String> responseEntity=(ResponseEntity<String>) authCommonUtil.getResponseEntity(url,HttpMethod.POST,MediaType.APPLICATION_JSON,otpUserDTO,null,String.class);
				List<ServiceError> validationErrorList=ExceptionUtils.getServiceErrorList(responseEntity.getBody());
				if(!validationErrorList.isEmpty()) {
					throw new AuthServiceException(validationErrorList,response);
				}
				response.setResponsetime(authCommonUtil.getCurrentResponseTime());
				response.setResponse(authCommonUtil.requestBodyExchange(responseEntity.getBody()));
			}
		}
		catch(Exception ex) {
			log.error("sessionId", "idType", "id",
					"In callsendOtp method of kernel service- " + ex.getMessage());
			new AuthExceptionCatcher().handle(ex,"sendOtp");	
		}
		
		return response;
	}
	
	
	/**
	 * It will validate userId & otp and provide with a access token 
	 * 
	 * @param userIdOtpRequest
	 * @return MainResponseDTO<AuthNResponse>
	 */
	public MainResponseDTO<ResponseEntity<String>> validateWithUserIdOtp(MainRequestDTO<User> userIdOtpRequest){
		log.info("sessionId", "idType", "id",
				"In calluserIdOtp method of kernel service ");
		MainResponseDTO<ResponseEntity<String>> response  = null;
		try {
			if(authCommonUtil.validateRequest(userIdOtpRequest)) {
				User user=userIdOtpRequest.getRequest();
				authCommonUtil.validateOtpAndUserid(user);
				UserOtp userOtp=new UserOtp(user.getUserId(), user.getOtp(), appId);
				UserOtpDTO userOtpDTO=new UserOtpDTO();
				userOtpDTO.setRequest(userOtp);
				response  =	(MainResponseDTO<ResponseEntity<String>>) authCommonUtil.getMainResponseDto(userIdOtpRequest);
				ResponseEntity<String> responseEntity = null;
				String url=sendOtpResourceUrl+"/v1.0/authenticate/useridOTP";
				responseEntity=(ResponseEntity<String>) authCommonUtil.getResponseEntity(url,HttpMethod.POST,MediaType.APPLICATION_JSON_UTF8,userOtpDTO,null,String.class);
				List<ServiceError> validationErrorList=null;
				validationErrorList=ExceptionUtils.getServiceErrorList(responseEntity.getBody());
				if(!validationErrorList.isEmpty()) {
					throw new AuthServiceException(validationErrorList,response);
				}
				response.setResponsetime(authCommonUtil.getCurrentResponseTime());
				response.setResponse(responseEntity);
			}
		}
		catch(Exception ex) {
			log.error("sessionId", "idType", "id",
					"In calluserIdOtp method of kernel service- " + ex.getMessage());
			new AuthExceptionCatcher().handle(ex,"userIdOtp");	
		}
		
		return response;
	}
	
	/**
	 * This method will invalidate the access token
	 * 
	 * @param authHeader
	 * @return AuthNResponse
	 */

	public AuthNResponse invalidateToken(String authHeader){
		log.info("sessionId", "idType", "id",
				"In calluserIdOtp method of kernel service ");
		ResponseEntity<String> responseEntity = null;
		AuthNResponse authNResponse = null;
		try {
			Map<String,String> headersMap=new HashMap<>();
			headersMap.put("Cookie",authHeader);
			String url=sendOtpResourceUrl+"/v1.0/authorize/invalidateToken";
			responseEntity=(ResponseEntity<String>) authCommonUtil.getResponseEntity(url,HttpMethod.POST,MediaType.APPLICATION_JSON,null,headersMap,String.class);
			List<ServiceError> validationErrorList=null;
			validationErrorList=ExceptionUtils.getServiceErrorList(responseEntity.getBody());
			if(!validationErrorList.isEmpty()) {
				throw new AuthServiceException(validationErrorList,null);
			}
			authNResponse = authCommonUtil.requestBodyExchange(responseEntity.getBody());
		}
		catch(Exception ex) {	
			log.error("sessionId", "idType", "id",
					"In call invalidateToken method of kernel service- " + ex.getMessage());
			new AuthExceptionCatcher().handle(ex,"invalidateToken");	
		}
		
		return authNResponse;
	}
	
}
