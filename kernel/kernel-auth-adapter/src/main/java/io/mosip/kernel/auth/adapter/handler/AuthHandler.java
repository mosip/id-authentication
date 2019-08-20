package io.mosip.kernel.auth.adapter.handler;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.NonceExpiredException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.mosip.kernel.auth.adapter.config.LoggerConfiguration;
import io.mosip.kernel.auth.adapter.constant.AuthAdapterConstant;
import io.mosip.kernel.auth.adapter.constant.AuthAdapterErrorCode;
import io.mosip.kernel.auth.adapter.exception.AuthManagerException;
import io.mosip.kernel.auth.adapter.model.AuthToken;
import io.mosip.kernel.auth.adapter.model.AuthUserDetails;
import io.mosip.kernel.auth.adapter.model.MosipUserDto;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.EmptyCheckUtils;
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
 * @author Raj Jha
 * @author Urvil Joshi
 * @since 1.0.0
 */

@Component
public class AuthHandler extends AbstractUserDetailsAuthenticationProvider {

	private static final Logger LOGGER = LoggerConfiguration.logConfig(AuthHandler.class);

	@Value("${auth.server.validate.url}")
	private String validateUrl;
	
	@Value("${auth.server.admin.validate.url:https://dev.mosip.io/r2/v1/authmanager/authorize/admin/validateToken}")
	private String adminValidateUrl;
	
	@Value("${auth.jwt.base:Mosip-Token}")
	private String authJwtBase;
	
	@Value("${auth.jwt.secret:authjwtsecret}")
	private String authJwtSecret;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
	}

	@Override
	protected UserDetails retrieveUser(String userName,
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
		ResponseEntity<String> response = null;
		String token = null;
		AuthToken authToken = (AuthToken) usernamePasswordAuthenticationToken;
		token = authToken.getToken();
		MosipUserDto mosipUserDto = null;
		//added for keycloak impl
		if (token.startsWith(AuthAdapterConstant.AUTH_ADMIN_COOKIE_PREFIX)) {
             
             response = getKeycloakValidatedUserResponse(token);
             List<ServiceError> validationErrorsList = ExceptionUtils.getServiceErrorList(response.getBody());
     		if (!validationErrorsList.isEmpty()) {
     			throw new AuthManagerException(AuthAdapterErrorCode.UNAUTHORIZED.getErrorCode(), validationErrorsList);
     		}
     		try {
     			ResponseWrapper<?> responseObject = objectMapper.readValue(response.getBody(), ResponseWrapper.class);
     			mosipUserDto = objectMapper.readValue(objectMapper.writeValueAsString(responseObject.getResponse()),
     					MosipUserDto.class);
     		} catch (Exception e) {
     			throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), e.getMessage(), e);
     		}
		}else {
		Claims claims;
		try {
			claims = getClaims(token);
		} catch (Exception e1) {
			throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), e1.getMessage(), e1);
		}
		mosipUserDto = buildDto(claims);
		}
			
		List<GrantedAuthority> grantedAuthorities = AuthorityUtils
				.commaSeparatedStringToAuthorityList(mosipUserDto.getRole());
		AuthUserDetails authUserDetails = new AuthUserDetails(mosipUserDto, token);
		authUserDetails.setAuthorities(grantedAuthorities);
		return authUserDetails;

	}

	private Claims getClaims(String token) throws Exception {
		String token_base = authJwtBase;
		String secret = authJwtSecret;
		Claims claims = null;

		if (token == null || !token.startsWith(token_base)) {
			throw new NonceExpiredException("Invalid Token");
		}
//		try {
		claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token.substring(token_base.length())).getBody();

//		} catch (SignatureException e) {
//			throw new AuthManagerException(AuthErrorCode.UNAUTHORIZED.getErrorCode(), e.getMessage());
//		} catch (JwtException e) {
//			if( e instanceof ExpiredJwtException)
//			{
//				System.out.println("Token expired message "+ e.getMessage() + " Token "+token);
//				throw new AuthManagerException(AuthErrorCode.TOKEN_EXPIRED.getErrorCode(), AuthErrorCode.TOKEN_EXPIRED.getErrorMessage());
//			}
//			else
//			{
//				throw new AuthManagerException(AuthErrorCode.UNAUTHORIZED.getErrorCode(), e.getMessage());
//			}
//			
//		}
		return claims;
	}

	private MosipUserDto buildDto(Claims claims) {
		MosipUserDto mosipUserDto = new MosipUserDto();
		mosipUserDto.setUserId(claims.getSubject());
		mosipUserDto.setName((String) claims.get("name"));
		mosipUserDto.setRole((String) claims.get("role"));
		mosipUserDto.setMail((String) claims.get("mail"));
		mosipUserDto.setMobile((String) claims.get("mobile"));
		mosipUserDto.setRId((String) claims.get("rId"));
		return mosipUserDto;
	}

	private ResponseEntity<String> getValidatedUserResponse(String token) {
		HttpHeaders headers = new HttpHeaders();
		// System.out.println("\nInside Auth Handler");
		// System.out.println("Token details " + System.currentTimeMillis() + " : " +
		// token + "\n");
		headers.set(AuthAdapterConstant.AUTH_HEADER_COOKIE, AuthAdapterConstant.AUTH_COOOKIE_HEADER + token);
		HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
		try {
			return getRestTemplate().exchange(validateUrl, HttpMethod.POST, entity, String.class);
		} catch (RestClientException | KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			throw new AuthManagerException(AuthAdapterErrorCode.UNAUTHORIZED.getErrorCode(), e.getMessage(), e);
		}
	}
	
	private ResponseEntity<String> getKeycloakValidatedUserResponse(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.set(AuthAdapterConstant.AUTH_HEADER_COOKIE, AuthAdapterConstant.AUTH_COOOKIE_HEADER + token);
		HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
		try {
			return getRestTemplate().exchange(adminValidateUrl, HttpMethod.GET, entity, String.class);
		} catch (RestClientException | KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			throw new AuthManagerException(AuthAdapterErrorCode.UNAUTHORIZED.getErrorCode(), e.getMessage(), e);
		}
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

	public void addCorsFilter(HttpServer httpServer, Vertx vertx) {
		Router router = Router.router(vertx);
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

	public void addAuthFilter(Router router, String path, io.vertx.core.http.HttpMethod httpMethod,
			String commaSepratedRoles) {
		Objects.requireNonNull(httpMethod, AuthAdapterConstant.HTTP_METHOD_NOT_NULL);
		if (EmptyCheckUtils.isNullEmpty(commaSepratedRoles)) {
			throw new NullPointerException(AuthAdapterConstant.ROLES_NOT_EMPTY_NULL);
		}
		String[] roles = commaSepratedRoles.split(",");
		Route filterRoute = router.route(httpMethod, path);
		filterRoute.handler(routingContext -> {
			String token;
			try {
				token = validateToken(routingContext, roles);
				if (token.isEmpty()) {
					return;
				}
				HttpServerResponse httpServerResponse = routingContext.response();
				httpServerResponse.putHeader(AuthAdapterConstant.AUTH_HEADER_SET_COOKIE, token);
				routingContext.next();
			} catch (Exception e) {
				throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), e.getMessage(), e);
			}
		});
	}

	private String validateToken(RoutingContext routingContext, String[] roles)
			throws RestClientException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		boolean isAuthorized = false;
		HttpServerRequest httpRequest = routingContext.request();
		String token = null;
		MosipUserDto mosipUserDto = null;
		String cookies = httpRequest.getHeader(AuthAdapterConstant.AUTH_HEADER_COOKIE);
		if (cookies != null && !cookies.isEmpty() && cookies.contains(AuthAdapterConstant.AUTH_COOOKIE_HEADER)) {
			token = cookies.replace(AuthAdapterConstant.AUTH_COOOKIE_HEADER, "").trim();
		}
		if (token == null || token.isEmpty()) {
			List<ServiceError> errors = new ArrayList<>();
			ServiceError error = new ServiceError(AuthAdapterErrorCode.UNAUTHORIZED.getErrorCode(),
					AuthAdapterErrorCode.UNAUTHORIZED.getErrorMessage());
			errors.add(error);
			sendErrors(routingContext, errors, AuthAdapterConstant.NOTAUTHENTICATED);
			return "";
		}
		token = token.split(";")[0];
		/*ResponseEntity<String> response = getValidatedUserResponse(token);
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
		

		responseObject = objectMapper.readValue(response.getBody(), ResponseWrapper.class);
		mosipUserDto = objectMapper.readValue(objectMapper.writeValueAsString(responseObject.getResponse()),
				MosipUserDto.class);*/
		Claims claims;
		try {
			claims = getClaims(token);
		} catch (Exception e1) {
			throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), e1.getMessage(), e1);
		}
		mosipUserDto = buildDto(claims);
		AuthUserDetails authUserDetails = new AuthUserDetails(mosipUserDto, token);
		Authentication authentication = new UsernamePasswordAuthenticationToken(authUserDetails,
				authUserDetails.getPassword(), null);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String[] authorities = mosipUserDto.getRole().split(",");
		for (String role : roles) {
			for (String authority : authorities) {
				if (role.equals(authority)) {
					isAuthorized = true;
					break;
				}
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
		return token;
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
}