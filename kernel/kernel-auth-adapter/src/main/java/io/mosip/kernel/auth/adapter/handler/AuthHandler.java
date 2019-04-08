package io.mosip.kernel.auth.adapter.handler;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.auth.adapter.config.LoggerConfiguration;
import io.mosip.kernel.auth.adapter.constant.AuthAdapterConstant;
import io.mosip.kernel.auth.adapter.constant.AuthAdapterErrorCode;
import io.mosip.kernel.auth.adapter.exception.AuthManagerException;
import io.mosip.kernel.auth.adapter.exception.ParseResponseException;
import io.mosip.kernel.auth.adapter.model.AuthToken;
import io.mosip.kernel.auth.adapter.model.AuthUserDetails;
import io.mosip.kernel.auth.adapter.model.MosipUserDto;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * Contacts auth server to verify token validity.
 *
 * Tasks: 1. Contacts auth server to verify token validity. 2. Stores the
 * response body in an instance of MosipUserDto. 3. Updates token into in the
 * security context through AuthUserDetails. 4. Bind MosipUserDto instance
 * details with the AuthUserDetails that extends Spring Security's UserDetails.
 * 
 * @author Ramadurai Saravana Pandian
 * @author Urvil Joshi
 * @since 1.0.0
 */

@Component
public class AuthHandler extends AbstractUserDetailsAuthenticationProvider {

	private static final Logger LOGGER = LoggerConfiguration.logConfig(AuthHandler.class);

	@Value("${auth.server.validate.url}")
	private String validateUrl;

	@Autowired
	private ObjectMapper objectMapper;

	private RestTemplate restTemplate;

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
	}

	private RestTemplate getRestTemplate() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
		SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy)
				.build();
		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpClient);
		return new RestTemplate(requestFactory);

	}

	private ResponseEntity<String> getResponseEntity(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.set(AuthAdapterConstant.AUTH_HEADER_COOKIE, AuthAdapterConstant.AUTH_COOOKIE_HEADER + token);
		HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
		try {
			return getRestTemplate().exchange(validateUrl, HttpMethod.POST, entity, String.class);
		} catch (RestClientException | KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			LOGGER.error("", "", "", e.getMessage());
		}
		return null;
	}

	@Override
	protected UserDetails retrieveUser(String userName,
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
		ResponseEntity<String> response = null;
		String token = null;
		AuthToken authToken = (AuthToken) usernamePasswordAuthenticationToken;
		token = authToken.getToken();
		MosipUserDto mosipUserDto = null;

		response = getResponseEntity(token);
		List<ServiceError> validationErrorsList = null;
		validationErrorsList = ExceptionUtils.getServiceErrorList(response.getBody());

		if (!validationErrorsList.isEmpty()) {
			throw new AuthManagerException(AuthAdapterErrorCode.UNAUTHORIZED.getErrorCode(),validationErrorsList);
		}

		ResponseWrapper<?> responseObject;
		try {
			responseObject = objectMapper.readValue(response.getBody(), ResponseWrapper.class);
			mosipUserDto = objectMapper.readValue(objectMapper.writeValueAsString(responseObject.getResponse()),
					MosipUserDto.class);
		} catch (Exception e) {
			throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), e.getMessage());
		}
		List<GrantedAuthority> grantedAuthorities = AuthorityUtils
				.commaSeparatedStringToAuthorityList(mosipUserDto.getRole());
		String responseToken = response.getHeaders().get(AuthAdapterConstant.AUTH_HEADER_SET_COOKIE).get(0)
				.replaceAll(AuthAdapterConstant.AUTH_COOOKIE_HEADER, "");
		AuthUserDetails authUserDetails = new AuthUserDetails(mosipUserDto, responseToken);
		authUserDetails.setAuthorities(grantedAuthorities);
		return authUserDetails;

	}

	private String validateToken(RoutingContext routingContext, String[] roles) {
		boolean isAuthorized = false;
		HttpServerRequest httpRequest = routingContext.request();
		String token = httpRequest.getHeader(AuthAdapterConstant.AUTH_HEADER_COOKIE);
		if (token == null || !token.contains(AuthAdapterConstant.AUTH_COOOKIE_HEADER)
				|| (token = token.replace(AuthAdapterConstant.AUTH_COOOKIE_HEADER, "").trim()).isEmpty()) {
			List<ServiceError> errors = new ArrayList<>();
			ServiceError error = new ServiceError(AuthAdapterErrorCode.UNAUTHORIZED.getErrorCode(),
					AuthAdapterErrorCode.UNAUTHORIZED.getErrorMessage());
			errors.add(error);
			sendErrors(routingContext, errors, AuthAdapterConstant.NOTAUTHENTICATED);
			return "";
		}
		ResponseEntity<String> response = getResponseEntity(token);
		if (response == null) {
			List<ServiceError> errors = new ArrayList<>();
			ServiceError error = new ServiceError(AuthAdapterErrorCode.CONNECT_EXCEPTION.getErrorCode(),
					AuthAdapterErrorCode.CONNECT_EXCEPTION.getErrorMessage());
			errors.add(error);
			sendErrors(routingContext, errors, AuthAdapterConstant.INTERNEL_SERVER_ERROR);
			return "";
		}
		List<ServiceError> validationErrorsList = ExceptionUtils.getServiceErrorList(response.getBody());
		if (!validationErrorsList.isEmpty()) {
			sendErrors(routingContext, validationErrorsList, AuthAdapterConstant.NOTAUTHENTICATED);
			return "";
		}
		ResponseWrapper<?> responseObject = null;
		MosipUserDto mosipUserDto = null;
		try {
			responseObject = objectMapper.readValue(response.getBody(), ResponseWrapper.class);
			mosipUserDto = objectMapper.readValue(objectMapper.writeValueAsString(responseObject.getResponse()),
					MosipUserDto.class);
		} catch (IOException | NullPointerException exception) {
			throw new ParseResponseException(AuthAdapterErrorCode.RESPONSE_PARSE_ERROR.getErrorCode(),
					AuthAdapterErrorCode.RESPONSE_PARSE_ERROR.getErrorMessage(), exception);
		}

		for (String role : roles) {
			if (role.equals(mosipUserDto.getRole())) {
				isAuthorized = true;
				break;
			}
		}
		if (!isAuthorized) {
			List<ServiceError> errors = new ArrayList<>();
			ServiceError error = new ServiceError(AuthAdapterErrorCode.FORBIDDEN.getErrorCode(),
					AuthAdapterErrorCode.FORBIDDEN.getErrorMessage());
			errors.add(error);
			sendErrors(routingContext, errors, AuthAdapterConstant.UNAUTHORIZED);
			return "";
		}
		return response.getHeaders().get(AuthAdapterConstant.AUTH_HEADER_SET_COOKIE).get(0)
				.replaceAll(AuthAdapterConstant.AUTH_COOOKIE_HEADER, "");
	}

	private void sendErrors(RoutingContext routingContext, List<ServiceError> errors, int statusCode) {

		ResponseWrapper<ServiceError> errorResponse = new ResponseWrapper<>();
		errorResponse.getErrors().addAll(errors);
		objectMapper.registerModule(new JavaTimeModule());
		JsonNode reqNode;
		if (routingContext.getBodyAsJson() != null) {
			try {
				reqNode = objectMapper.readTree(routingContext.getBodyAsJson().toString());
				errorResponse.setId(reqNode.path("id").asText());
				errorResponse.setVersion(reqNode.path("version").asText());
			} catch (IOException exception) {
				LOGGER.error("", "", "", exception.getMessage());
			}
		}

		try {
			routingContext.response().putHeader("content-type", "application/json").setStatusCode(statusCode)
					.end(objectMapper.writeValueAsString(errorResponse));

		} catch (JsonProcessingException exception) {
			LOGGER.error("", "", "", exception.getMessage());
		}

	}

	public void addCorsFilter(HttpServer httpServer, Vertx vertx) {
		Router router = Router.router(vertx);

		// CORS filters
		/*
		 * router.route().handler(routingContext -> { HttpServerRequest
		 * httpServerRequest = routingContext.request();
		 * CorsHandler.create(httpServerRequest.getHeader("Origin")).allowCredentials(
		 * true) .allowedHeader("POST, GET, OPTIONS, DELETE, PUT, PATCH").exposedHeader(
		 * "Set-Cookie"); });
		 */

		// Basic security to make cookie not accessed by script
		/*
		 * router.route().handler(CookieHandler.create()).handler(SessionHandler.create(
		 * LocalSessionStore.create(vertx))
		 * .setCookieHttpOnlyFlag(true).setCookieSecureFlag(true));
		 */

		// CSRF token
		/* router.route().handler(CSRFHandler.create("")); */

		// Basic security headers by OWASP
		router.route().handler(routingContext -> {
			HttpServerResponse httpServerResponse = routingContext.response();
			httpServerResponse.putHeader("Cache-Control", "no-store, no-cache,max-age=0, must-revalidate")
					.putHeader("Pragma", "no-cache").putHeader("X-Content-Type-Options", "nosniff")
					.putHeader("Strict-Transport-Security", "max-age=" + 15768000 + "; includeSubDomains")
					.putHeader("X-Download-Options", "noopen").putHeader("X-XSS-Protection", "1; mode=block")
					.putHeader("X-FRAME-OPTIONS", "DENY");

			routingContext.next();
		});
		httpServer.requestHandler(router);

	}

	public void addAuthFilter(Router router, String path, io.vertx.core.http.HttpMethod httpMethod, String[] roles) {
		Route filterRoute = null;
		if (httpMethod == null) {
			filterRoute = router.route(path);
		} else {
			filterRoute = router.route(httpMethod, path);
		}
		filterRoute.handler(routingContext -> {
			String token = validateToken(routingContext, roles);
			if (token.isEmpty()) {
				return;
			}
			HttpServerResponse httpServerResponse = routingContext.response();
			httpServerResponse.putHeader(AuthAdapterConstant.AUTH_HEADER_SET_COOKIE, token);
			routingContext.next();
		});
	}
}