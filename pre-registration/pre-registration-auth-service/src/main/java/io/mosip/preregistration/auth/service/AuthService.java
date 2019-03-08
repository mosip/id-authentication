package io.mosip.preregistration.auth.service;



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
import io.mosip.preregistration.auth.dto.OtpUserDTO;
import io.mosip.preregistration.auth.dto.UserOtpDTO;
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
	
	/**
	 * Reference for ${mosip.prereg.app-id} from property file
	 */
	@Value("${mosip.prereg.app-id}")
	private String appId;
	
	/**
	 * It will fetch otp from Kernel auth service  and send to the userId provided
	 * 
	 * @param userOtpRequest
	 * @return MainResponseDTO<AuthNResponse>
	 */
	public MainResponseDTO<AuthNResponse> sendOTP(MainRequestDTO<OtpUserDTO> userOtpRequest) {
		log.info("sessionId", "idType", "id",
				"In callsendOtp method of kernel service ");
		MainResponseDTO<AuthNResponse> response  = null;
		try {
			response  =	(MainResponseDTO<AuthNResponse>) AuthCommonUtil.getMainResponseDto(userOtpRequest);
		ResponseEntity<AuthNResponse> responseEntity = null;
		String url=sendOtpResourceUrl+"/v1.0/authenticate/sendotp";
		responseEntity=(ResponseEntity<AuthNResponse>) authCommonUtil.getResponseEntity(url,HttpMethod.POST,MediaType.APPLICATION_JSON,userOtpRequest.getRequest(),AuthNResponse.class);
		//RestTemplate restTemplate=restTemplateBuilder.build();
//		UriComponentsBuilder uriBuilder = UriComponentsBuilder
//			.fromHttpUrl(url);
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		HttpEntity<OtpUserDTO> request = new HttpEntity<>(userOtpRequest.getRequest(), headers);
//		String strUriBuilder = uriBuilder.build().encode().toUriString();
//		responseEntity = restTemplate.exchange(url, HttpMethod.POST, request,AuthNResponse.class);
		
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
	public MainResponseDTO<ResponseEntity<AuthNResponse>> validateWithUserIdOtp(MainRequestDTO<UserOtpDTO> userIdOtpRequest){
		log.info("sessionId", "idType", "id",
				"In calluserIdOtp method of kernel service ");
		MainResponseDTO<ResponseEntity<AuthNResponse>> response  = null;
		try {
			response  =	(MainResponseDTO<ResponseEntity<AuthNResponse>>) AuthCommonUtil.getMainResponseDto(userIdOtpRequest);
		ResponseEntity<AuthNResponse> responseEntity = null;
		String url=sendOtpResourceUrl+"/v1.0/authenticate/useridOTP";
		responseEntity=(ResponseEntity<AuthNResponse>) authCommonUtil.getResponseEntity(url,HttpMethod.POST,MediaType.APPLICATION_JSON_UTF8,userIdOtpRequest.getRequest(),AuthNResponse.class);
		response.setResponsetime(DateUtils.getUTCCurrentDateTimeString());
		response.setResponse(responseEntity);
		}
		catch(HttpClientErrorException | HttpServerErrorException ex) {
			System.out.println(ex.getResponseBodyAsString());
			log.error("sessionId", "idType", "id",
					"In calluserIdOtp method of kernel service- " + ex.getMessage());
			new AuthExceptionCatcher().handle(ex,"userIdOtp");	
		}
		
		return response;
	}

	public MainResponseDTO<ResponseEntity<AuthNResponse>> invalidateToken(MainRequestDTO<?> invalidateTokenRequest){
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
			System.out.println(ex.getResponseBodyAsString());
			log.error("sessionId", "idType", "id",
					"In calluserIdOtp method of kernel service- " + ex.getMessage());
			new AuthExceptionCatcher().handle(ex,"userIdOtp");	
		}
		
		return response;
	}
	
}
