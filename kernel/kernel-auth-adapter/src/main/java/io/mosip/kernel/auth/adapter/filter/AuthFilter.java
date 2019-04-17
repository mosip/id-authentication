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

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.auth.adapter.constant.AuthAdapterConstant;
import io.mosip.kernel.auth.adapter.constant.AuthAdapterErrorCode;
import io.mosip.kernel.auth.adapter.exception.AuthManagerException;
import io.mosip.kernel.auth.adapter.model.AuthToken;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.EmptyCheckUtils;

/**
 * @author Ramadurai Saravana Pandian
 * @author Urvil Joshi
 *
 */
public class AuthFilter extends AbstractAuthenticationProcessingFilter {

	private String[] allowedEndPoints() {
		return new String[] { "/**/assets/**", "/**/icons/**", "/**/screenshots/**", "/favicon**", "/**/favicon**",
				"/**/css/**", "/**/js/**", "/**/error**", "/**/webjars/**", "/**/v2/api-docs", "/**/configuration/ui",
				"/**/configuration/security", "/**/swagger-resources/**", "/**/swagger-ui.html", "/**/csrf", "/*/",
				"**/authenticate/**","/**/actuator/**","/v1/syncdata" };
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
		String token = null;
		Cookie[] cookies = httpServletRequest.getCookies();
		System.out.println("Cookies List "+cookies);
		System.out.println("Inside Auth Filter");
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().contains(AuthAdapterConstant.AUTH_REQUEST_COOOKIE_HEADER)) {
					token = cookie.getValue();
					System.out.println("Cookie token with Auth header "+cookie.getValue());
				}
			}
		}
		System.out.println("Outside Auth Filter");
		if (token == null) {
			ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
			ServiceError error = new ServiceError(AuthAdapterErrorCode.UNAUTHORIZED.getErrorCode(),
					"Authentication Failed");
			errorResponse.getErrors().add(error);
			httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
			httpServletResponse.setContentType("application/json");
			httpServletResponse.setCharacterEncoding("UTF-8");
			httpServletResponse.getWriter().write(convertObjectToJson(errorResponse));
			return null;
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
		AuthManagerException exception = (AuthManagerException) failed;
		ResponseWrapper<ServiceError> errorResponse = setErrors(request);
		errorResponse.getErrors().addAll(exception.getList());
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(convertObjectToJson(errorResponse));

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