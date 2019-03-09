package io.mosip.preregistration.auth.service;

/**
 * This class provides different methods for login called by the controller 
 * 
 * @author M1050360
 * @since 1.0.0
 */

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.auth.dto.MainRequestDTO;
import io.mosip.preregistration.auth.dto.MainResponseDTO;
import io.mosip.preregistration.auth.dto.OtpUser;
import io.mosip.preregistration.auth.dto.OtpUserDTO;
import io.mosip.preregistration.auth.dto.Otp;
import io.mosip.preregistration.auth.dto.UserOtp;
import io.mosip.preregistration.auth.dto.UserOtpDTO;
import io.mosip.preregistration.auth.dto.User;
import io.mosip.preregistration.auth.exceptions.util.AuthExceptionCatcher;
import io.mosip.preregistration.auth.util.AuthCommonUtil;
import io.mosip.preregistration.core.common.dto.AuthNResponse;
import io.mosip.preregistration.core.config.LoggerConfiguration;

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
	//Need to remove the default value
	@Value("${sendOtp.resource.url}")
	private String sendOtpResourceUrl;
	
	@Value("${otpChannel}")
	private List<String> otpChannel;
	
	@Value("${userIdType}")
	private String useridtype;
	
	@Value("${appId}")
	private String appId;
	
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
			Otp otp=userOtpRequest.getRequest();
			OtpUser user=new OtpUser(otp.getUserId(), otp.getLangCode(), otpChannel, appId, useridtype);
			OtpUserDTO otpUserDTO=new OtpUserDTO();
			otpUserDTO.setRequest(user);
			response  =	(MainResponseDTO<AuthNResponse>) AuthCommonUtil.getMainResponseDto(userOtpRequest);
		ResponseEntity<AuthNResponse> responseEntity = null;
		String url=sendOtpResourceUrl+"/v1.0/authenticate/sendotp";
		responseEntity=(ResponseEntity<AuthNResponse>) authCommonUtil.getResponseEntity(url,HttpMethod.POST,MediaType.APPLICATION_JSON,otpUserDTO,AuthNResponse.class);
		response.setResponsetime(DateUtils.getUTCCurrentDateTimeString());
		response.setResponse(responseEntity.getBody());
		}
		catch(HttpClientErrorException | HttpServerErrorException ex) {
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
	public MainResponseDTO<ResponseEntity<AuthNResponse>> validateWithUserIdOtp(MainRequestDTO<User> userIdOtpRequest){
		log.info("sessionId", "idType", "id",
				"In calluserIdOtp method of kernel service ");
		MainResponseDTO<ResponseEntity<AuthNResponse>> response  = null;
		try {
		User user=userIdOtpRequest.getRequest();
		UserOtp userOtp=new UserOtp(user.getUserId(), user.getOtp(), appId);
		UserOtpDTO userOtpDTO=new UserOtpDTO();
		userOtpDTO.setRequest(userOtp);
		response  =	(MainResponseDTO<ResponseEntity<AuthNResponse>>) AuthCommonUtil.getMainResponseDto(userIdOtpRequest);
		ResponseEntity<AuthNResponse> responseEntity = null;
		String url=sendOtpResourceUrl+"/v1.0/authenticate/useridOTP";
		responseEntity=(ResponseEntity<AuthNResponse>) authCommonUtil.getResponseEntity(url,HttpMethod.POST,MediaType.APPLICATION_JSON_UTF8,userOtpDTO,AuthNResponse.class);
		response.setResponsetime(DateUtils.getUTCCurrentDateTimeString());
		response.setResponse(responseEntity);
		}
		catch(HttpClientErrorException | HttpServerErrorException ex) {
			log.error("sessionId", "idType", "id",
					"In calluserIdOtp method of kernel service- " + ex.getMessage());
			new AuthExceptionCatcher().handle(ex,"userIdOtp");	
		}
		
		return response;
	}

	public MainResponseDTO<AuthNResponse> invalidateToken(MainRequestDTO<?> invalidateTokenRequest){
		log.info("sessionId", "idType", "id",
				"In calluserIdOtp method of kernel service ");
		MainResponseDTO<AuthNResponse> response  = null;
		try {
			response  =	(MainResponseDTO<AuthNResponse>) AuthCommonUtil.getMainResponseDto(invalidateTokenRequest);
		ResponseEntity<AuthNResponse> responseEntity = null;
		String url=sendOtpResourceUrl+"/v1.0/authenticate/invalidateToken";
		responseEntity=(ResponseEntity<AuthNResponse>) authCommonUtil.getResponseEntity(url,HttpMethod.POST,MediaType.APPLICATION_JSON_UTF8,null,AuthNResponse.class);
		response.setResponsetime(DateUtils.getUTCCurrentDateTimeString());
		response.setResponse(responseEntity.getBody());
		}
		catch(HttpClientErrorException | HttpServerErrorException ex) {
			
			log.error("sessionId", "idType", "id",
					"In calluserIdOtp method of kernel service- " + ex.getMessage());
			new AuthExceptionCatcher().handle(ex,"userIdOtp");	
		}
		
		return response;
	}
	
}
