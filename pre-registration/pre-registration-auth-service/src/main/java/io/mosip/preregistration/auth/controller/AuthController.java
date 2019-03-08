package io.mosip.preregistration.auth.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.auth.dto.MainRequestDTO;
import io.mosip.preregistration.auth.dto.MainResponseDTO;
import io.mosip.preregistration.auth.dto.OtpUserDTO;
import io.mosip.preregistration.auth.dto.UserOtpDTO;
import io.mosip.preregistration.auth.service.AuthService;
import io.mosip.preregistration.core.common.dto.AuthNResponse;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * This class provides different api to perform operation for login 
 * 
 * @author Akshay Jain
 * @since 1.0.0
 *
 */
@RestController
@Api(tags = "PreAuth")
@CrossOrigin("*")
public class AuthController {

	/** Autowired reference for {@link #authService}. */
	@Autowired
	private AuthService authService;
	
	private Logger log = LoggerConfiguration.logConfig(AuthController.class);
	
	/**
	 * Post api to send otp
	 * @param userOtpRequest
	 * @return MainResponseDTO
	 */
	@PostMapping(value = "/sendotp",produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Send Otp to UserId")
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<MainResponseDTO<AuthNResponse>> sendOTP(@RequestBody MainRequestDTO<OtpUserDTO> userOtpRequest ){
		log.info("sessionId", "idType", "id",
				"In sendOtp method of Auth controller for sending Otp ");
		return ResponseEntity.status(HttpStatus.OK).body(authService.sendOTP(userOtpRequest));
		}
	
	/**
	 * Post api to validate userid and otp
	 * @param userIdOtpRequest
	 * @return MainResponseDTO
	 */
	@PostMapping(value="/useridotp",produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Validate UserId and Otp")
	@ResponseStatus(value=HttpStatus.OK)
	public ResponseEntity<MainResponseDTO<AuthNResponse>> validateWithUserIdOtp(@RequestBody MainRequestDTO<UserOtpDTO> userIdOtpRequest,HttpServletResponse res){
		log.info("sessionId", "idType", "id",
				"In validateWithUserIdotp method of Auth controller for validating user and Otp and providing the access token ");
		MainResponseDTO<ResponseEntity<AuthNResponse>> serviceResponse=authService.validateWithUserIdOtp(userIdOtpRequest);
		MainResponseDTO<AuthNResponse> responseBody=new MainResponseDTO<>();
		responseBody.setId(serviceResponse.getId());
		responseBody.setResponsetime(serviceResponse.getResponsetime());
		ResponseEntity<AuthNResponse> response=serviceResponse.getResponse();
		responseBody.setResponse(response.getBody());
		HttpHeaders headers=response.getHeaders();
		System.out.println(headers.get("Set-Cookie"));
		String content=headers.get("Set-Cookie").get(0).replaceAll("Authorization=", "");
		System.out.println("Cookie added : "+content);
		List<String> contentArray=Arrays.asList(content.split(";"));
		
		Cookie cookie=createCookie(contentArray.get(0),6000000);
		res.addCookie(cookie);
		return ResponseEntity.status(HttpStatus.OK).body(responseBody);
	}
	private Cookie createCookie(final String content, final int expirationTimeSeconds) {
        final Cookie cookie = new Cookie("Authorization", content);
        cookie.setMaxAge(expirationTimeSeconds);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        return cookie;
  }

}
