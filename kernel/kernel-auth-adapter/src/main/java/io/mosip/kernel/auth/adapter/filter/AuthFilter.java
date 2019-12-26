/**
 * 
 */
package io.mosip.kernel.auth.adapter.filter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.auth.adapter.constant.AuthAdapterConstant;
import io.mosip.kernel.auth.adapter.constant.AuthAdapterErrorCode;
import io.mosip.kernel.auth.adapter.exception.AuthManagerException;
import io.mosip.kernel.auth.adapter.model.AuthToken;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.EmptyCheckUtils;

/**
 * @author Ramadurai Saravana Pandian
 * @author Raj Jha 
 * @author Urvil Joshi
 *
 */
public class AuthFilter extends AbstractAuthenticationProcessingFilter {
	
	private static String token;

	private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

	private String[] allowedEndPoints() {
		return new String[] { "/**/assets/**", "/**/icons/**", "/**/screenshots/**", "/favicon**", "/**/favicon**",
				"/**/css/**", "/**/js/**", "/**/error**", "/**/webjars/**", "/**/v2/api-docs", "/**/configuration/ui",
				"/**/configuration/security", "/**/swagger-resources/**", "/**/swagger-ui.html", "/**/csrf", "/*/",
				"**/authenticate/**", "/**/actuator/**", "/**/authmanager/**","/sendOtp",
				"/validateOtp", "/invalidateToken", "/config", "/login", "/logout","/validateOTP","/sendOTP","/**/login","/**/logout"};

	}

	public AuthFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
		super(requiresAuthenticationRequestMatcher);
		// this.requestMatcher = requiresAuthenticationRequestMatcher;
	}

	@Override
	protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
		String[] endpoints = allowedEndPoints();
		for (String pattern : endpoints) {
			RequestMatcher ignorePattern = new AntPathRequestMatcher(pattern);
			if (ignorePattern.matches(request)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
			throws AuthenticationException, JsonProcessingException, IOException {
//		String token = null;
//		Cookie[] cookies = null;
//		try
//		{
//			cookies = httpServletRequest.getCookies();
//			if (cookies != null) {
//				for (Cookie cookie : cookies) {
//					if (cookie.getName().contains(AuthAdapterConstant.AUTH_REQUEST_COOOKIE_HEADER)) {
//						token = cookie.getValue();
//					}
//				}
//			}
//		}catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//		
//		if (token == null) {
//			ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
//			ServiceError error = new ServiceError(AuthAdapterErrorCode.UNAUTHORIZED.getErrorCode(),
//					"Authentication Failed");
//			errorResponse.getErrors().add(error);
//			httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
//			httpServletResponse.setContentType("application/json");
//			httpServletResponse.setCharacterEncoding("UTF-8");
//			httpServletResponse.getWriter().write(convertObjectToJson(errorResponse));
//			logger.error("\n\n Exception : Authorization token not present > " + httpServletRequest.getRequestURL()
//					+ "\n\n");
//			return null;
//		}
		if (token == null) {
			String req = "{\"id\":\"string\",\"request\":{\"clientId\":\"resident\",\"secretKey\":\"dbe554cd-81e8-44c2-b8ec-3d8090aca1f4\",\"appId\":\"resident\"},\"requesttime\":\"2019-04-23T09:41:05.633Z\",\"version\":\"string\"}";
			HttpHeaders headers = WebClient.create("https://dev.mosip.io/v1/authmanager/authenticate/clientidsecretkey").post()
			.syncBody(new ObjectMapper().readValue(req, Object.class)).exchange()
			.map(res -> res.headers().asHttpHeaders()).block();
			token = headers.get(AuthAdapterConstant.AUTH_HEADER_SET_COOKIE).get(0).replace("Authorization=", "");
		}
		AuthToken authToken = new AuthToken(token);
		return getAuthenticationManager().authenticate(authToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		super.successfulAuthentication(request, response, chain, authResult);
		chain.doFilter(request, response);
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		String req = "{\"id\":\"mosip.otpnotification.send\",\"metadata\":{},\"request\":{\"clientId\":\"registration-processor\",\"secretKey\":\"d80ec0be-bba7-4d8e-bf0c-85ab45bb976b\",\"appId\":\"registrationprocessor\"},\"requesttime\":\"2018-12-09T06:39:03.683Z\",\"version\":\"v1.0\"}";
		HttpHeaders headers = WebClient.create("https://dev.mosip.io/v1/authmanager/authenticate/clientidsecretkey").post()
		.syncBody(new ObjectMapper().readValue(req, Object.class)).exchange()
		.map(res -> res.headers().asHttpHeaders()).block();
		token = headers.get(AuthAdapterConstant.AUTH_HEADER_SET_COOKIE).get(0).replace("Authorization=", "");
		this.attemptAuthentication(request, response);
	}

	private ResponseWrapper<ServiceError> setErrors(HttpServletRequest httpServletRequest) throws IOException {
		ResponseWrapper<ServiceError> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponsetime(LocalDateTime.now(ZoneId.of("UTC")));
		String requestBody = null;
		if (httpServletRequest instanceof ContentCachingRequestWrapper) {
			requestBody = new String(((ContentCachingRequestWrapper) httpServletRequest).getContentAsByteArray());
		}
		if (EmptyCheckUtils.isNullEmpty(requestBody)) {
			return responseWrapper;
		}
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		JsonNode reqNode = objectMapper.readTree(requestBody);
		responseWrapper.setId(reqNode.path("id").asText());
		responseWrapper.setVersion(reqNode.path("version").asText());
		return responseWrapper;
	}

	private String convertObjectToJson(Object object) throws JsonProcessingException {
		if (object == null) {
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		return mapper.writeValueAsString(object);
	}

}