package io.mosip.registration.util.restclient;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.io.StringReader;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.LoginMode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.AuthNClientIDDTO;
import io.mosip.registration.dto.AuthNRequestDTO;
import io.mosip.registration.dto.AuthNUserOTPDTO;
import io.mosip.registration.dto.AuthNUserPasswordDTO;
import io.mosip.registration.dto.AuthTokenDTO;
import io.mosip.registration.dto.LoginUserDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;

/**
 * This is a helper class .it invokes with different classes to get the response
 * 
 * @author Yaswanth S
 * @since 1.0.0
 *
 */
@Component("serviceDelegateUtil")
public class ServiceDelegateUtil {

	@Autowired
	private RestClientUtil restClientUtil;

	@Autowired
	private Environment environment;

	@Value("${HTTP_API_READ_TIMEOUT}")
	private int readTimeout;

	@Value("${HTTP_API_WRITE_TIMEOUT}")
	private int connectTimeout;

	@Value("${AUTH_CLIENT_ID:}")
	private String clientId;

	@Value("${AUTH_SECRET_KEY:}")
	private String secretKey;

	@Value("${validate_auth_token.service.url:}")
	private String urlPath;

	@Value("${invalidate_auth_token.service.url:}")
	private String invalidateUrlPath;

	private static final Logger LOGGER = AppConfig.getLogger(ServiceDelegateUtil.class);

	/**
	 * Prepare GET request.
	 *
	 * @param serviceName   service to be invoked
	 * @param requestParams parameters along with url
	 * @param hasPathParams the has path params
	 * @param triggerPoint  system or user driven invocation
	 * @return Object requiredType of object response Body
	 * @throws RegBaseCheckedException  generalised exception with errorCode and
	 *                                  errorMessage
	 * @throws HttpClientErrorException when client error exception from server
	 * @throws SocketTimeoutException   the socket timeout exception
	 * @throws HttpServerErrorException when server exception from server
	 */
	public Object get(String serviceName, Map<String, String> requestParams, boolean hasPathParams, String triggerPoint)
			throws RegBaseCheckedException, HttpClientErrorException, SocketTimeoutException {

		LOGGER.debug(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_GET, APPLICATION_NAME, APPLICATION_ID,
				"Get method has been called");

		Map<String, Object> responseMap = null;
		Object responseBody = null;

		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();

		try {
			requestHTTPDTO = prepareGETRequest(requestHTTPDTO, serviceName, requestParams);
			requestHTTPDTO.setAuthRequired(
					Boolean.valueOf(getEnvironmentProperty(serviceName, RegistrationConstants.AUTH_REQUIRED)));
			requestHTTPDTO.setAuthZHeader(getEnvironmentProperty(serviceName, RegistrationConstants.AUTH_HEADER));
			requestHTTPDTO.setIsSignRequired(
					Boolean.valueOf(getEnvironmentProperty(serviceName, RegistrationConstants.SIGN_REQUIRED)));
			requestHTTPDTO.setTriggerPoint(triggerPoint);
			requestHTTPDTO.setRequestSignRequired(
					Boolean.valueOf(getEnvironmentProperty(serviceName, RegistrationConstants.REQUEST_SIGN_REQUIRED)));

			// URI creation
			String url = getEnvironmentProperty(serviceName, RegistrationConstants.SERVICE_URL);

		
			Map<String, String> queryParams = new HashMap<>();
			for (String key : requestParams.keySet()) {
				if (!url.contains("{" + key + "}")) {
					queryParams.put(key, requestParams.get(key));
				}

			}

			if (hasPathParams) {
				requestHTTPDTO.setUri(UriComponentsBuilder.fromUriString(url).build(requestParams));
				url=requestHTTPDTO.getUri().toString();
			}
			if (!queryParams.isEmpty()) {
				/** Set URI */
				setURI(requestHTTPDTO, queryParams, url);
			}

			LOGGER.debug(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_GET, APPLICATION_NAME, APPLICATION_ID,
					"set uri method called");

		} catch (RegBaseCheckedException baseCheckedException) {
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_SERVICE_DELEGATE_UTIL_CODE.getErrorCode(),
					RegistrationExceptionConstants.REG_SERVICE_DELEGATE_UTIL_CODE.getErrorMessage(),
					baseCheckedException);
		}

		responseMap = restClientUtil.invoke(requestHTTPDTO);
		if (isResponseValid(responseMap, RegistrationConstants.REST_RESPONSE_BODY)) {
			responseBody = responseMap.get(RegistrationConstants.REST_RESPONSE_BODY);
		}
		LOGGER.debug(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_GET, APPLICATION_NAME, APPLICATION_ID,
				"Get method has been ended");

		return responseBody;
	}

	/**
	 * prepare POST request.
	 *
	 * @param serviceName  service to be invoked
	 * @param object       request type
	 * @param triggerPoint system or user driven invocation
	 * @return Object requiredType of object response Body
	 * @throws RegBaseCheckedException  generalised exception with errorCode and
	 *                                  errorMessage
	 * @throws HttpClientErrorException when client error exception from server
	 * @throws SocketTimeoutException   the socket timeout exception
	 * @throws ResourceAccessException  the resource access exception
	 * @throws HttpServerErrorException when server exception from server
	 */
	public Object post(String serviceName, Object object, String triggerPoint)
			throws RegBaseCheckedException, HttpClientErrorException, SocketTimeoutException, ResourceAccessException {
		LOGGER.debug(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_POST, APPLICATION_NAME, APPLICATION_ID,
				" post method called");

		RequestHTTPDTO requestDto;
		Object responseBody = null;
		Map<String, Object> responseMap = null;

		try {
			requestDto = preparePOSTRequest(serviceName, object);
			requestDto.setAuthRequired(
					Boolean.valueOf(getEnvironmentProperty(serviceName, RegistrationConstants.AUTH_REQUIRED)));
			requestDto.setAuthZHeader(getEnvironmentProperty(serviceName, RegistrationConstants.AUTH_HEADER));
			requestDto.setIsSignRequired(
					Boolean.valueOf(getEnvironmentProperty(serviceName, RegistrationConstants.SIGN_REQUIRED)));
			requestDto.setTriggerPoint(triggerPoint);
			requestDto.setRequestSignRequired(
					Boolean.valueOf(getEnvironmentProperty(serviceName, RegistrationConstants.REQUEST_SIGN_REQUIRED)));
		} catch (RegBaseCheckedException baseCheckedException) {
			throw new RegBaseCheckedException(RegistrationConstants.SERVICE_DELEGATE_UTIL,
					baseCheckedException.getMessage() + ExceptionUtils.getStackTrace(baseCheckedException));
		}
		responseMap = restClientUtil.invoke(requestDto);
		if (isResponseValid(responseMap, RegistrationConstants.REST_RESPONSE_BODY)) {
			responseBody = responseMap.get(RegistrationConstants.REST_RESPONSE_BODY);
		}
		LOGGER.debug(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_POST, APPLICATION_NAME, APPLICATION_ID,
				"post method ended");

		return responseBody;
	}

	/**
	 * Builds the request and passess it to REST client util
	 * 
	 * @param url
	 *            - MDM service url
	 * @param serviceName
	 *            - MDM service name
	 * @param request
	 *            - request data
	 * @param responseType
	 *            - response format
	 * @return
	 * @throws RegBaseCheckedException
	 */
	public Object invokeRestService(String url, String serviceName, Object request, Class<?> responseType)
			throws RegBaseCheckedException {

		LOGGER.debug(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_GET, APPLICATION_NAME, APPLICATION_ID,
				"invokeRestService method has been called");

		Map<String, Object> responseMap = null;
		Object responseBody = null;

		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();

		prepareRequest(requestHTTPDTO, serviceName, request, responseType, url);

		try {
			responseMap = restClientUtil.invoke(requestHTTPDTO);
		} catch (HttpClientErrorException | HttpServerErrorException | ResourceAccessException
				| SocketTimeoutException exception) {
			throw new RegBaseUncheckedException(
					RegistrationExceptionConstants.REG_SERVICE_DELEGATE_UTIL_CODE.getErrorCode(),
					RegistrationExceptionConstants.REG_SERVICE_DELEGATE_UTIL_CODE.getErrorMessage(), exception);
		}
		if (isResponseValid(responseMap, RegistrationConstants.REST_RESPONSE_BODY)) {
			responseBody = responseMap.get(RegistrationConstants.REST_RESPONSE_BODY);
		}
		LOGGER.debug(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_GET, APPLICATION_NAME, APPLICATION_ID,
				"invokeRestService method has been ended");

		return responseBody;

	}
	
	/**
	 * prepares the request
	 * 
	 * @param requestHTTPDTO
	 *            - holds the request data for a REST call
	 * @param serviceName
	 *            - service name
	 * @param request
	 *            - request data
	 * @param responseType
	 *            - response format
	 */
	protected void prepareRequest(RequestHTTPDTO requestHTTPDTO, String serviceName, Object request,
			Class<?> responseType, String url) {
		LOGGER.info(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_PREPARE_REQUEST, APPLICATION_NAME, APPLICATION_ID,
				"Preparing request");

		requestHTTPDTO.setHttpMethod(
				HttpMethod.valueOf(getEnvironmentProperty(serviceName, RegistrationConstants.HTTPMETHOD)));
		requestHTTPDTO.setHttpHeaders(new HttpHeaders());
		requestHTTPDTO.setRequestBody(request);
		requestHTTPDTO.setClazz(Object.class);
		requestHTTPDTO.setIsSignRequired(false);
		try {
			requestHTTPDTO.setUri(new URI(url));
		} catch (URISyntaxException uriSyntaxException) {
		}
		// set timeout
		setTimeout(requestHTTPDTO);
		// Headers
		setHeaders(requestHTTPDTO.getHttpHeaders(), getEnvironmentProperty(serviceName, RegistrationConstants.HEADERS));
		requestHTTPDTO.setAuthRequired(false);
	}
	
	/**
	 * Prepare GET request.
	 *
	 * @param requestHTTPDTO the request HTTPDTO
	 * @param serviceName    service to be invoked
	 * @param requestParams  params need to add along with url
	 * @param authHeader     the auth header
	 * @return RequestHTTPDTO requestHTTPDTO with required data
	 * @throws RegBaseCheckedException the reg base checked exception
	 */
	private RequestHTTPDTO prepareGETRequest(RequestHTTPDTO requestHTTPDTO, final String serviceName,
			final Map<String, String> requestParams) throws RegBaseCheckedException {
		LOGGER.debug(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_PREPARE_GET, APPLICATION_NAME, APPLICATION_ID,
				"Prepare Get request method called");

		// prepare httpDTO except rquest type and uri build
		prepareRequest(requestHTTPDTO, serviceName, null);

		// ResponseType
		Class<?> responseClass = null;
		try {
			responseClass = Class.forName(getEnvironmentProperty(serviceName, RegistrationConstants.RESPONSE_TYPE));
		} catch (ClassNotFoundException classNotFoundException) {
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_CLASS_NOT_FOUND_ERROR_CODE.getErrorCode(),
					RegistrationExceptionConstants.REG_CLASS_NOT_FOUND_ERROR_CODE.getErrorMessage(),
					classNotFoundException);
		}

		requestHTTPDTO.setClazz(responseClass);

		LOGGER.debug("REGISTRATION - SERVICE_DELEGATE_UTIL - GET", APPLICATION_NAME, APPLICATION_ID,
				"Prepare Get request method ended");

		return requestHTTPDTO;
	}

	/**
	 * Prepare POST request.
	 *
	 * @param serviceName service to be invoked
	 * @param object      request type
	 * @param authHeader  the auth header
	 * @return RequestHTTPDTO requestHTTPDTO with required data
	 * @throws RegBaseCheckedException the reg base checked exception
	 */
	private RequestHTTPDTO preparePOSTRequest(final String serviceName, final Object object)
			throws RegBaseCheckedException {
		LOGGER.info(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_PREPARE_POST, APPLICATION_NAME, APPLICATION_ID,
				"Preparing post request for web-service");

		// DTO need to to be prepared
		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();

		// prepare httpDTO except rquest type and uri build
		prepareRequest(requestHTTPDTO, serviceName, object);

		// URI creation
		setURI(requestHTTPDTO, null, getEnvironmentProperty(serviceName, RegistrationConstants.SERVICE_URL));

		// RequestType
		requestHTTPDTO.setClazz(Object.class);

		LOGGER.info(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_PREPARE_POST, APPLICATION_NAME, APPLICATION_ID,
				"Completed preparing post request for web-service");

		return requestHTTPDTO;

	}

	/**
	 * Sets the URI.
	 *
	 * @param requestHTTPDTO the request HTTPDTO
	 * @param requestParams  the request params
	 * @param url            the url
	 */
	private void setURI(RequestHTTPDTO requestHTTPDTO, Map<String, String> requestParams, String url) {
		LOGGER.info(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_PREPARE_POST, APPLICATION_NAME, APPLICATION_ID,
				"Preparing URI for web-service");

		// BuildURIComponent
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(url);

		if (requestParams != null) {
			Set<String> set = requestParams.keySet();
			for (String queryParamName : set) {
				uriComponentsBuilder.queryParam(queryParamName, requestParams.get(queryParamName));

			}
		}
		URI uri = uriComponentsBuilder.build().toUri();

		requestHTTPDTO.setUri(uri);

		LOGGER.info(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_PREPARE_POST, APPLICATION_NAME, APPLICATION_ID,
				"Completed preparing URI for web-service");
	}

	/**
	 * Setup of headers
	 * 
	 * @param httpHeaders http headers
	 * @param headers     headers
	 */
	private void setHeaders(HttpHeaders httpHeaders, String headers) {
		LOGGER.info(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_PREPARE_REQUEST, APPLICATION_NAME, APPLICATION_ID,
				"Preparing Header for web-service request");

		String[] header = headers.split(",");
		String[] headerValues = null;
		if (header != null) {
			for (String subheader : header) {
				if (subheader != null) {
					headerValues = subheader.split(":");
					if(headerValues[0].equalsIgnoreCase("timestamp")) {
						headerValues[1] = DateUtils.getUTCCurrentDateTimeString();
					} else if(headerValues[0].equalsIgnoreCase("Center-Machine-RefId")) {
						headerValues[1] = String
								.valueOf(ApplicationContext.map().get(RegistrationConstants.USER_CENTER_ID))
								.concat(RegistrationConstants.UNDER_SCORE).concat(String
										.valueOf(ApplicationContext.map().get(RegistrationConstants.USER_STATION_ID)));
					} 
					httpHeaders.add(headerValues[0], headerValues[1]);
				}
			}
			httpHeaders.add("Cache-Control", "no-cache,max-age=0");
		}

		LOGGER.info(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_PREPARE_REQUEST, APPLICATION_NAME, APPLICATION_ID,
				"Completed reparing Header for web-service request");
	}

	/**
	 * @param requestHTTPDTO create requestedHTTPDTO
	 * @param serviceName    service name to be called
	 * @param requestBody    object to be included in HTTP entities
	 */
	private void prepareRequest(RequestHTTPDTO requestHTTPDTO, String serviceName, Object requestBody) {
		LOGGER.info(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_PREPARE_REQUEST, APPLICATION_NAME, APPLICATION_ID,
				"Preparing RequestHTTPDTO object for web-service");

		requestHTTPDTO.setHttpMethod(
				HttpMethod.valueOf(getEnvironmentProperty(serviceName, RegistrationConstants.HTTPMETHOD)));
		requestHTTPDTO.setHttpHeaders(new HttpHeaders());
		requestHTTPDTO.setRequestBody(requestBody);
		// set timeout
		setTimeout(requestHTTPDTO);
		// Headers
		setHeaders(requestHTTPDTO.getHttpHeaders(), getEnvironmentProperty(serviceName, RegistrationConstants.HEADERS));

		LOGGER.info(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_PREPARE_REQUEST, APPLICATION_NAME, APPLICATION_ID,
				"Completed preparing RequestHTTPDTO object for web-service");
	}

	/**
	 * Method to set the request timeout
	 * 
	 * @param requestHTTPDTO
	 */
	private void setTimeout(RequestHTTPDTO requestHTTPDTO) {
		// Timeout in milli second
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setReadTimeout(readTimeout);
		requestFactory.setConnectTimeout(connectTimeout);
		requestHTTPDTO.setSimpleClientHttpRequestFactory(requestFactory);
	}

	private AuthNRequestDTO prepareAuthNRequestDTO(LoginMode loginMode) {
		LOGGER.info(LoggerConstants.LOG_SERVICE_DELEGATE_AUTH_DTO, APPLICATION_NAME, APPLICATION_ID,
				"Preparing AuthNRequestDTO Based on Login Mode");

		AuthNRequestDTO authNRequestDTO = new AuthNRequestDTO();
		LoginUserDTO loginUserDTO = (LoginUserDTO) ApplicationContext.map().get(RegistrationConstants.USER_DTO);

		switch (loginMode) {
		case PASSWORD:
			AuthNUserPasswordDTO authNUserPasswordDTO = new AuthNUserPasswordDTO();
			authNUserPasswordDTO.setAppId(
					String.valueOf(ApplicationContext.map().get(RegistrationConstants.REGISTRATION_CLIENT)));
			authNUserPasswordDTO.setUserName(loginUserDTO.getUserId());
			authNUserPasswordDTO.setPassword(loginUserDTO.getPassword());
			authNRequestDTO.setRequest(authNUserPasswordDTO);
			break;
		case OTP:
			AuthNUserOTPDTO authNUserOTPDTO = new AuthNUserOTPDTO();
			authNUserOTPDTO.setAppId(
					String.valueOf(ApplicationContext.map().get(RegistrationConstants.REGISTRATION_CLIENT)));
			authNUserOTPDTO.setUserId(loginUserDTO.getUserId());
			authNUserOTPDTO.setOtp(loginUserDTO.getOtp());
			authNRequestDTO.setRequest(authNUserOTPDTO);
			break;
		default:
			AuthNClientIDDTO authNClientIDDTO = new AuthNClientIDDTO();
			authNClientIDDTO.setAppId(
					String.valueOf(ApplicationContext.map().get(RegistrationConstants.REGISTRATION_CLIENT)));
			authNClientIDDTO.setClientId(clientId);
			authNClientIDDTO.setSecretKey(secretKey);
			authNRequestDTO.setRequest(authNClientIDDTO);
			break;
		}

		LOGGER.info(LoggerConstants.LOG_SERVICE_DELEGATE_AUTH_DTO, APPLICATION_NAME, APPLICATION_ID,
				"Completed preparing AuthNRequestDTO Based on Login Mode");

		return authNRequestDTO;
	}

	@SuppressWarnings("unchecked")
	public void getAuthToken(LoginMode loginMode) throws RegBaseCheckedException {

		LOGGER.info(LoggerConstants.LOG_SERVICE_DELEGATE_GET_TOKEN, APPLICATION_NAME, APPLICATION_ID,
				"Fetching Auth Token based on Login Mode");

		try {
			Map<String, Object> responseMap = null;
			HttpHeaders responseHeader = null;
			RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();
			Map<String, String> requestParams = new HashMap<>();
			String cookie = null;

			// setting headers
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			AuthNRequestDTO authNRequestDTO = prepareAuthNRequestDTO(loginMode);
			requestHTTPDTO.setClazz(Object.class);
			requestHTTPDTO.setRequestBody(authNRequestDTO);
			requestHTTPDTO.setHttpHeaders(headers);
			requestHTTPDTO.setIsSignRequired(false);
			requestHTTPDTO.setRequestSignRequired(false);
			
			setURI(requestHTTPDTO, requestParams, getEnvironmentProperty(
					"auth_by_".concat(loginMode.getCode().toLowerCase()), RegistrationConstants.SERVICE_URL));

			requestHTTPDTO.setHttpMethod(HttpMethod.POST);

			// set simple client http request
			setTimeout(requestHTTPDTO);
			

			responseMap = restClientUtil.invoke(requestHTTPDTO);

			boolean isResponseValid = isResponseValid(responseMap, RegistrationConstants.REST_RESPONSE_HEADERS);
			if (isResponseValid) {
				responseHeader = (HttpHeaders) responseMap.get(RegistrationConstants.REST_RESPONSE_HEADERS);
				isResponseValid = responseHeader.containsKey(RegistrationConstants.AUTH_SET_COOKIE)
						&& responseHeader.get(RegistrationConstants.AUTH_SET_COOKIE).get(0) != null;
			}

			if (!isResponseValid) {
				throw new RegBaseCheckedException(RegistrationExceptionConstants.INVALID_RESPONSE_HEADER.getErrorCode(),
						RegistrationExceptionConstants.INVALID_RESPONSE_HEADER.getErrorMessage());
			}

			LinkedHashMap<String, Object> responseBody = (LinkedHashMap<String, Object>) responseMap
					.get(RegistrationConstants.REST_RESPONSE_BODY);

			if (loginMode.equals(LoginMode.OTP) && responseBody.get("response") != null) {

				LinkedHashMap<String, String> otpResponseBody = (LinkedHashMap<String, String>) responseBody
						.get("response");

				if (otpResponseBody == null
						|| !"Validation_Successful".equalsIgnoreCase(otpResponseBody.get("message"))) {
					throw new RegBaseCheckedException(RegistrationExceptionConstants.INVALID_OTP.getErrorCode(),
							RegistrationExceptionConstants.INVALID_OTP.getErrorMessage());
				}
			}

			cookie = responseHeader.get(RegistrationConstants.AUTH_SET_COOKIE).get(0);
			Properties properties = new Properties();
			properties.load(new StringReader(cookie.replaceAll(";", "\n")));
			AuthTokenDTO authTokenDTO = new AuthTokenDTO();
			authTokenDTO.setCookie(cookie);
			// authTokenDTO.setToken(properties.getProperty(RegistrationConstants.AUTH_AUTHORIZATION));
			// authTokenDTO.setTokenMaxAge(Long.valueOf(properties.getProperty(RegistrationConstants.AUTH_MAX_AGE)));
			authTokenDTO.setLoginMode(loginMode.getCode());

			if (loginMode.equals(LoginMode.CLIENTID)) {
				ApplicationContext.setAuthTokenDTO(authTokenDTO);
			} else {
				SessionContext.setAuthTokenDTO(authTokenDTO);
			}

		} catch (HttpClientErrorException | HttpServerErrorException | ResourceAccessException
				| IOException restException) {
			throw new RegBaseCheckedException(RegistrationConstants.REST_OAUTH_ERROR_CODE,
					RegistrationConstants.REST_OAUTH_ERROR_MSG, restException);
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.REST_OAUTH_ERROR_CODE,
					RegistrationConstants.REST_OAUTH_ERROR_MSG, runtimeException);
		}

		LOGGER.info(LoggerConstants.LOG_SERVICE_DELEGATE_GET_TOKEN, APPLICATION_NAME, APPLICATION_ID,
				"Completed fetching Auth Token based on Login Mode");
	}

	private String getEnvironmentProperty(String serviceName, String serviceComponent) {
		return environment.getProperty(serviceName.concat(RegistrationConstants.DOT).concat(serviceComponent));
	}

	public boolean isAuthTokenValid(String cookie) {
		LOGGER.info(LoggerConstants.LOG_SERVICE_DELEGATE_VALIDATE_TOKEN, APPLICATION_NAME, APPLICATION_ID,
				" get auth method called");

		boolean isTokenValid = false;

		try {
			if (cookie != null) {
				Map<String, Object> responseMap = null;

				responseMap = restClientUtil.invoke(buildRequestHTTPDTO(cookie, urlPath, HttpMethod.POST));

				isTokenValid = isResponseValid(responseMap, RegistrationConstants.REST_RESPONSE_BODY);
				if (isTokenValid) {
					@SuppressWarnings("unchecked")
					Map<String, Object> responseBody = (Map<String, Object>) responseMap
							.get(RegistrationConstants.REST_RESPONSE_BODY);
					if (responseBody != null && responseBody.get("errors") != null) {
						isTokenValid = false;
					}
				}
			}
		} catch (URISyntaxException | HttpClientErrorException | HttpServerErrorException | ResourceAccessException
				| SocketTimeoutException | RegBaseCheckedException restException) {
			LOGGER.error(LoggerConstants.LOG_SERVICE_DELEGATE_VALIDATE_TOKEN, APPLICATION_NAME, APPLICATION_ID,
					restException.getMessage() + ExceptionUtils.getStackTrace(restException));
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LoggerConstants.LOG_SERVICE_DELEGATE_VALIDATE_TOKEN, APPLICATION_NAME, APPLICATION_ID,
					String.format("Exception while validating AuthZ Token --> %s",
							ExceptionUtils.getStackTrace(runtimeException)));
		}

		LOGGER.info(LoggerConstants.LOG_SERVICE_DELEGATE_VALIDATE_TOKEN, APPLICATION_NAME, APPLICATION_ID,
				" get auth method calling ends");

		return isTokenValid;
	}

	private boolean isResponseValid(Map<String, Object> responseMap, String key) {
		return !(null == responseMap || responseMap.isEmpty() || !responseMap.containsKey(key));
	}

	/**
	 * Invalidate token.
	 *
	 * @param cookie the cookie
	 */
	public void invalidateToken(String cookie) {
		LOGGER.info(LoggerConstants.LOG_SERVICE_DELEGATE_VALIDATE_TOKEN, APPLICATION_NAME, APPLICATION_ID,
				" invalidate auth token method calling starts");
		try {
			if (cookie != null) {
				Map<String, Object> responseMap = null;

				responseMap = restClientUtil.invoke(buildRequestHTTPDTO(cookie, invalidateUrlPath, HttpMethod.POST));

				if (isResponseValid(responseMap, RegistrationConstants.REST_RESPONSE_BODY)) {
					LOGGER.info(LoggerConstants.LOG_SERVICE_DELEGATE_VALIDATE_TOKEN, APPLICATION_NAME, APPLICATION_ID,
							"Token invalidated successfully");
				}
			}
		} catch (HttpClientErrorException | HttpServerErrorException | ResourceAccessException | SocketTimeoutException
				| URISyntaxException | RegBaseCheckedException restException) {
			LOGGER.error(LoggerConstants.LOG_SERVICE_DELEGATE_VALIDATE_TOKEN, APPLICATION_NAME, APPLICATION_ID,
					restException.getMessage() + ExceptionUtils.getStackTrace(restException));
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LoggerConstants.LOG_SERVICE_DELEGATE_VALIDATE_TOKEN, APPLICATION_NAME, APPLICATION_ID,
					"Invalid Token for validation");
		}
		LOGGER.info(LoggerConstants.LOG_SERVICE_DELEGATE_VALIDATE_TOKEN, APPLICATION_NAME, APPLICATION_ID,
				" invalidate auth token method calling ends");
	}

	/**
	 * Create a {@link RequestHTTPDTO} for a web-service. Add Cookie to the request
	 * header and URL to request
	 *
	 * @param cookie         the cookie
	 * @param requestHTTPDTO the request HTTPDTO
	 * @throws URISyntaxException if requestURL is invalid
	 */
	private RequestHTTPDTO buildRequestHTTPDTO(String cookie, String requestURL, HttpMethod httpMethod)
			throws URISyntaxException {
		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();
		// setting headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Cookie", cookie);
		requestHTTPDTO.setClazz(Object.class);
		requestHTTPDTO.setHttpHeaders(headers);

		requestHTTPDTO.setUri(new URI(requestURL));

		requestHTTPDTO.setHttpMethod(httpMethod);
		requestHTTPDTO.setIsSignRequired(false);
		requestHTTPDTO.setRequestSignRequired(false);

		// set simple client http request
		setTimeout(requestHTTPDTO);

		return requestHTTPDTO;
	}
}
