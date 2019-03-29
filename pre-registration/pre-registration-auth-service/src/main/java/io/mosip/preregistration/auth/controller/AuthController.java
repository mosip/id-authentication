package io.mosip.preregistration.auth.controller;

import java.net.HttpCookie;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.coyote.http2.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.auth.dto.MainRequestDTO;
import io.mosip.preregistration.auth.dto.MainResponseDTO;
import io.mosip.preregistration.auth.dto.OtpUserDTO;
import io.mosip.preregistration.auth.dto.Otp;
import io.mosip.preregistration.auth.dto.UserOtpDTO;
import io.mosip.preregistration.auth.dto.User;
import io.mosip.preregistration.auth.service.AuthService;
import io.mosip.preregistration.auth.util.AuthCommonUtil;
import io.mosip.preregistration.core.common.dto.AuthNResponse;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

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
	@Autowired
	private AuthCommonUtil authCommonUtil;
	
	
	private Logger log = LoggerConfiguration.logConfig(AuthController.class);
	
	/**
	 * Post api to send otp
	 * @param userOtpRequest
	 * @return MainResponseDTO
	 */
	@PostMapping(value = "/sendotp",produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Send Otp to UserId")
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<MainResponseDTO<AuthNResponse>> sendOTP(@RequestBody MainRequestDTO<Otp> userOtpRequest ){
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
	public ResponseEntity<MainResponseDTO<AuthNResponse>> validateWithUserIdOtp(@RequestBody MainRequestDTO<User> userIdOtpRequest,HttpServletResponse res){
		log.info("sessionId", "idType", "id",
				"In validateWithUserIdotp method of Auth controller for validating user and Otp and providing the access token ");
		MainResponseDTO<ResponseEntity<String>> serviceResponse=authService.validateWithUserIdOtp(userIdOtpRequest);
		MainResponseDTO<AuthNResponse> responseBody=new MainResponseDTO<>();
		responseBody.setId(serviceResponse.getId());
		responseBody.setResponsetime(serviceResponse.getResponsetime());
		responseBody.setVersion(serviceResponse.getVersion());
		ResponseEntity<String> response=serviceResponse.getResponse();
		responseBody.setResponse(authCommonUtil.requestBodyExchange(response.getBody()));
		HttpHeaders headers=response.getHeaders();
		String content=headers.get("Set-Cookie").get(0);
		List<HttpCookie> httpCookies=HttpCookie.parse(content);
		httpCookies.stream().forEach(httpCookie->res.addCookie(createCookie(httpCookie)));
		return ResponseEntity.status(HttpStatus.OK).body(responseBody);
	}
	
	/**
	 * Post api to invalidate the token for logout.
	 * @param req
	 * @return AuthNResponse
	 */
	@PostMapping(value="/invalidatetoken",produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Inavlidate the token")
	@ResponseStatus(value=HttpStatus.OK)
	public ResponseEntity<AuthNResponse> invalidateToken(HttpServletRequest req){
		log.info("sessionId", "idType", "id",
				"In invalidateToken method of Auth controller for invalidating access token ");
		String authHeader=req.getHeader("Cookie");
		System.out.println(authHeader);
		//List<HttpCookie> authCookie=HttpCookie.parse(authHeader);
		return ResponseEntity.status(HttpStatus.OK).body(authService.invalidateToken(authHeader));
		
	}
	/**
	 * This method is used to create a cookie
	 * @param cookie
	 * @return cookie
	 */
	private Cookie createCookie(final HttpCookie cookie) {
        final Cookie responseCookie = new Cookie(cookie.getName(),cookie.getValue());
        responseCookie.setMaxAge((int) cookie.getMaxAge());
        responseCookie.setHttpOnly(cookie.isHttpOnly());
        responseCookie.setSecure(cookie.getSecure());
        responseCookie.setPath(cookie.getPath());
        return responseCookie;
  }
	
	/**
	 *
	 * @return the response entity
	 */
	@GetMapping(path="/config" ,produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get global and Pre-Registration config data")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "global and Pre-Registration config data successfully retrieved"),
			@ApiResponse(code = 400, message = "Unable to get the global and Pre-Registration config data") })
	public ResponseEntity<MainResponseDTO<Map<String,String>>> configParams() {
		log.info("sessionId", "idType", "id",
				"In notification controller for getting config values ");
		return  new ResponseEntity<>( authService.getConfig(),HttpStatus.OK);
		
	}

}
