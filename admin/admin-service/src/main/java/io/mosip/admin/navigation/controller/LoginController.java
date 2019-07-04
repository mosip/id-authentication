package io.mosip.admin.navigation.controller;

import static io.mosip.admin.navigation.constant.LoginUri.VALIDATE_USER;
import static io.mosip.admin.navigation.constant.LoginUri.INVALIDATE_TOKEN;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.admin.navigation.constant.LoginErrorCode;
import io.mosip.admin.navigation.dto.UserRequestDTO;
import io.mosip.admin.navigation.dto.UserResponseDTO;
import io.mosip.admin.navigation.exception.AdminServiceException;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.swagger.annotations.Api;

/**
 * Rest controller to handle login and logout operations.
 * 
 * @author Bal Vikash Sharma
 *
 */
@RestController
@Api(value = "API for Login and Logout operation", tags = { "Login" })
public class LoginController {

    @Value("${mosip.admin.navigation.base-uri}")
    private String baseUri;
    @Value("${mosip.admin.navigation.authmanager-uri}")
    private String authmanagerUri;
    @Value("${mosip.admin.navigation.userIdPwd-uri}")
    private String pwdUri;
    @Value("${mosip.admin.navigation.invalidateToken-uri}")
    private String invalidateTokenUri;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper mapper;

    @PostMapping(value = VALIDATE_USER)
    public ResponseWrapper<UserResponseDTO> validate(
	    @RequestBody @Valid RequestWrapper<UserRequestDTO> request,
	    HttpServletResponse res) throws IOException {

	HttpEntity<String> httpResponse = restTemplate.exchange(
		baseUri.concat(authmanagerUri).concat(pwdUri),
		HttpMethod.POST,
		new HttpEntity<RequestWrapper<UserRequestDTO>>(request),
		String.class);
	HttpHeaders headers = httpResponse.getHeaders();
	res.addHeader(SET_COOKIE, headers.getFirst(SET_COOKIE));
	mapper.registerModule(new JavaTimeModule());
	return mapper.readValue(httpResponse.getBody(),
		new TypeReference<ResponseWrapper<UserResponseDTO>>() {
		});
    }

    @PostMapping(value = INVALIDATE_TOKEN)
    public ResponseWrapper<UserResponseDTO> inValidate(HttpServletRequest req)
	    throws IOException {

	String cookieString = req.getHeader(HttpHeaders.COOKIE);
	if (cookieString == null || cookieString.trim().length() <= 0) {
	    throw new AdminServiceException(
		    LoginErrorCode.EMPTY_COOKIE.getErrorCode(),
		    LoginErrorCode.EMPTY_COOKIE.getErrorMessage());
	}
	HttpHeaders headers = new HttpHeaders();
	headers.add(HttpHeaders.COOKIE, cookieString);
	HttpEntity<String> entity = new HttpEntity<>(null, headers);
	HttpEntity<String> httpResponse = restTemplate.exchange(
		baseUri.concat(authmanagerUri).concat(invalidateTokenUri),
		HttpMethod.POST,
		entity,
		String.class);
	mapper.registerModule(new JavaTimeModule());
	return mapper.readValue(httpResponse.getBody(),
		new TypeReference<ResponseWrapper<UserResponseDTO>>() {
		});
    }

}
