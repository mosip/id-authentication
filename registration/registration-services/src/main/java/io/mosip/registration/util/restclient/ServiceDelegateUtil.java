package io.mosip.registration.util.restclient;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.io.StringReader;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
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
	RestClientUtil restClientUtil;

	@Autowired
	Environment environment;

	@Value("${HTTP_API_READ_TIMEOUT}")
	int readTimeout;

	@Value("${HTTP_API_WRITE_TIMEOUT}")
	int connectTimeout;

	@Value("${AUTH_CLIENT_ID:}")
	private String clientId;

	@Value("${AUTH_SECRET_KEY:}")
	private String secretKey;

	@Value("${validate_auth_token.service.url:}")
	private String urlPath;

	private static final Logger LOGGER = AppConfig.getLogger(ServiceDelegateUtil.class);

	/**
	 * Prepare GET request.
	 *
	 * @param serviceName
	 *            service to be invoked
	 * @param requestParams
	 *            parameters along with url
	 * @param hasPathParams
	 *            the has path params
	 * @return Object requiredType of object response Body
	 * @throws RegBaseCheckedException
	 *             generalised exception with errorCode and errorMessage
	 * @throws HttpClientErrorException
	 *             when client error exception from server
	 * @throws SocketTimeoutException
	 *             the socket timeout exception
	 * @throws HttpServerErrorException
	 *             when server exception from server
	 */
	public Object get(String serviceName, Map<String, String> requestParams, boolean hasPathParams,String triggerPoint)
			throws RegBaseCheckedException, HttpClientErrorException, SocketTimeoutException {

		LOGGER.debug(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_GET, APPLICATION_NAME, APPLICATION_ID,
				"Get method has been called");

		Map<String, Object> responseMap = null;
		Object responseBody = null;

		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();

		try {
			requestHTTPDTO = prepareGETRequest(requestHTTPDTO, serviceName, requestParams);
			requestHTTPDTO.setAuthRequired(
					Boolean.valueOf(environment.getProperty(serviceName + "." + RegistrationConstants.AUTH_REQUIRED)));
			requestHTTPDTO.setAuthZHeader(environment.getProperty(serviceName + "." + RegistrationConstants.AUTH_HEADER));
			requestHTTPDTO.setTriggerPoint(triggerPoint);

			// URI creation
			String url = environment.getProperty(serviceName + "." + RegistrationConstants.SERVICE_URL);

			if (hasPathParams) {
				requestHTTPDTO.setUri(UriComponentsBuilder.fromUriString(url).build(requestParams));
			} else {
				/** Set URI */
				setURI(requestHTTPDTO, requestParams, url);
			}

			LOGGER.debug(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_GET, APPLICATION_NAME, APPLICATION_ID,
					"set uri method called");

		} catch (RegBaseCheckedException baseCheckedException) {
			LOGGER.error(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_GET, APPLICATION_NAME, APPLICATION_ID,
					baseCheckedException.getMessage() + ExceptionUtils.getStackTrace(baseCheckedException));
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_SERVICE_DELEGATE_UTIL_CODE.getErrorCode(),
					RegistrationExceptionConstants.REG_SERVICE_DELEGATE_UTIL_CODE.getErrorMessage());
		}

		responseMap = restClientUtil.invoke(requestHTTPDTO);
		if (null != responseMap && responseMap.size() > 0
				&& null != responseMap.get(RegistrationConstants.REST_RESPONSE_BODY)) {
			responseBody = responseMap.get(RegistrationConstants.REST_RESPONSE_BODY);
		}
		LOGGER.debug(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_GET, APPLICATION_NAME, APPLICATION_ID,
				"Get method has been ended");

		return responseBody;
	}

	/**
	 * prepare POST request.
	 *
	 * @param serviceName
	 *            service to be invoked
	 * @param object
	 *            request type
	 * @return Object requiredType of object response Body
	 * @throws RegBaseCheckedException
	 *             generalised exception with errorCode and errorMessage
	 * @throws HttpClientErrorException
	 *             when client error exception from server
	 * @throws SocketTimeoutException
	 *             the socket timeout exception
	 * @throws ResourceAccessException
	 *             the resource access exception
	 * @throws HttpServerErrorException
	 *             when server exception from server
	 */
	public Object post(String serviceName, Object object,String triggerPoint)
			throws RegBaseCheckedException, HttpClientErrorException, SocketTimeoutException, ResourceAccessException {
		LOGGER.debug(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_POST, APPLICATION_NAME, APPLICATION_ID,
				" post method called");

		RequestHTTPDTO requestDto;
		Object responseBody = null;
		Map<String, Object> responseMap = null;

		try {
			requestDto = preparePOSTRequest(serviceName, object);
			requestDto.setAuthRequired(
					Boolean.valueOf(environment.getProperty(serviceName + "." + RegistrationConstants.AUTH_REQUIRED)));
			requestDto.setAuthZHeader(environment.getProperty(serviceName + "." + RegistrationConstants.AUTH_HEADER));
			requestDto.setTriggerPoint(triggerPoint);
		} catch (RegBaseCheckedException baseCheckedException) {
			LOGGER.error(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_POST, APPLICATION_NAME, APPLICATION_ID,
					baseCheckedException.getMessage() + ExceptionUtils.getStackTrace(baseCheckedException));

			throw new RegBaseCheckedException(RegistrationConstants.SERVICE_DELEGATE_UTIL,
					baseCheckedException.getMessage() + ExceptionUtils.getStackTrace(baseCheckedException));
		}
		responseMap = restClientUtil.invoke(requestDto);
		if (null != responseMap && responseMap.size() > 0
				&& null != responseMap.get(RegistrationConstants.REST_RESPONSE_BODY)) {
			responseBody = responseMap.get(RegistrationConstants.REST_RESPONSE_BODY);
		}
		LOGGER.debug(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_POST, APPLICATION_NAME, APPLICATION_ID,
				"post method ended");

		return responseBody;
	}

	/**
	 * Prepare GET request.
	 *
	 * @param requestHTTPDTO
	 *            the request HTTPDTO
	 * @param serviceName
	 *            service to be invoked
	 * @param requestParams
	 *            params need to add along with url
	 * @param authHeader
	 *            the auth header
	 * @return RequestHTTPDTO requestHTTPDTO with required data
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	private RequestHTTPDTO prepareGETRequest(RequestHTTPDTO requestHTTPDTO, final String serviceName,
			final Map<String, String> requestParams) throws RegBaseCheckedException {
		LOGGER.debug(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_PREPARE_GET, APPLICATION_NAME, APPLICATION_ID,
				"Prepare Get request method called");

		// prepare httpDTO except rquest type and uri build
		prepareRequest(requestHTTPDTO, serviceName, null);
		// ResponseType
		String responseClassName = environment.getProperty(serviceName + "." + RegistrationConstants.RESPONSE_TYPE);
		Class<?> responseClass = null;
		try {
			responseClass = Class.forName(responseClassName);
		} catch (ClassNotFoundException classNotFoundException) {
			LOGGER.error(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_PREPARE_GET, APPLICATION_NAME, APPLICATION_ID,
					classNotFoundException.getMessage() + ExceptionUtils.getStackTrace(classNotFoundException));
		
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_CLASS_NOT_FOUND_ERROR_CODE.getErrorCode(),
					RegistrationExceptionConstants.REG_CLASS_NOT_FOUND_ERROR_CODE.getErrorMessage());
		}

		requestHTTPDTO.setClazz(responseClass);
		LOGGER.debug("REGISTRATION - SERVICE_DELEGATE_UTIL - GET", APPLICATION_NAME, APPLICATION_ID,
				"Prepare Get request method ended");

		return requestHTTPDTO;
	}

	/**
	 * Prepare POST request.
	 *
	 * @param serviceName
	 *            service to be invoked
	 * @param object
	 *            request type
	 * @param authHeader
	 *            the auth header
	 * @return RequestHTTPDTO requestHTTPDTO with required data
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	private RequestHTTPDTO preparePOSTRequest(final String serviceName, final Object object)
			throws RegBaseCheckedException {
		LOGGER.debug(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_PREPARE_POST, APPLICATION_NAME, APPLICATION_ID,
				"Prepare post request method called");

		// DTO need to to be prepared
		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();

		// prepare httpDTO except rquest type and uri build
		prepareRequest(requestHTTPDTO, serviceName, object);
		// URI creation
		String url = environment.getProperty(serviceName + "." + RegistrationConstants.SERVICE_URL);
		setURI(requestHTTPDTO, null, url);
		LOGGER.debug(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_PREPARE_POST, APPLICATION_NAME, APPLICATION_ID,
				"get uri method called");

		// RequestType
		requestHTTPDTO.setClazz(Object.class);
		LOGGER.debug(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_PREPARE_POST, APPLICATION_NAME, APPLICATION_ID,
				"Prepare post request method ended");

		return requestHTTPDTO;

	}

	/**
	 * Sets the URI.
	 *
	 * @param requestHTTPDTO
	 *            the request HTTPDTO
	 * @param requestParams
	 *            the request params
	 * @param url
	 *            the url
	 */
	private void setURI(RequestHTTPDTO requestHTTPDTO, Map<String, String> requestParams, String url) {
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

	}

	/**
	 * Setup of headers
	 * 
	 * @param httpHeaders
	 *            http headers
	 * @param headers
	 *            headers
	 */
	private void setHeaders(HttpHeaders httpHeaders, String headers) {

		String[] header = headers.split(",");
		String[] headerValues = null;
		if (header != null) {
			for (String subheader : header) {
				if (subheader != null) {
					headerValues = subheader.split(":");
					httpHeaders.add(headerValues[0], headerValues[1]);
				}
			}
		}
	}

	/**
	 * @param requestHTTPDTO
	 *            create requestedHTTPDTO
	 * @param serviceName
	 *            service name to be called
	 * @param object
	 *            object to be included in HTTP entities
	 * @return
	 */
	private RequestHTTPDTO prepareRequest(RequestHTTPDTO requestHTTPDTO, String serviceName, Object object) {
		LOGGER.debug(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_PREPARE_REQUEST, APPLICATION_NAME, APPLICATION_ID,
				" prepare request method  called");

		// HTTP headers
		HttpHeaders httpHeaders = new HttpHeaders();

		// HTTP method
		HttpMethod httpMethod = HttpMethod
				.valueOf(environment.getProperty(serviceName + "." + RegistrationConstants.HTTPMETHOD));
		// Headers

		String headers = environment.getProperty(serviceName + "." + RegistrationConstants.HEADERS);
		setHeaders(httpHeaders, headers);
		LOGGER.debug(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_PREPARE_REQUEST, APPLICATION_NAME, APPLICATION_ID,
				" set Headers method called");

		// HTTP entity
		@SuppressWarnings({ "unchecked", "rawtypes" })
		HttpEntity<?> httpEntity = new HttpEntity(object, httpHeaders);

		requestHTTPDTO.setHttpMethod(httpMethod);
		requestHTTPDTO.setHttpEntity(httpEntity);
		// set timeout
		setTimeout(requestHTTPDTO);

		LOGGER.debug(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_PREPARE_REQUEST, APPLICATION_NAME, APPLICATION_ID,
				" prepare request method  called");

		return requestHTTPDTO;

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
		LOGGER.info(LoggerConstants.LOG_SERVICE_DELEGATE_AUTH_DTO, APPLICATION_NAME,
				APPLICATION_ID, "Preparing AuthNRequestDTO Based on Login Mode");

		AuthNRequestDTO authNRequestDTO = new AuthNRequestDTO();
		LoginUserDTO loginUserDTO = (LoginUserDTO) ApplicationContext.map().get(RegistrationConstants.USER_DTO);

		switch (loginMode) {
		case PASSWORD:
			AuthNUserPasswordDTO authNUserPasswordDTO = new AuthNUserPasswordDTO();
			authNUserPasswordDTO.setAppId(RegistrationConstants.REGISTRATION_CLIENT);
			authNUserPasswordDTO.setUserName(loginUserDTO.getUserId());
			authNUserPasswordDTO.setPassword(loginUserDTO.getPassword());
			authNRequestDTO.setRequest(authNUserPasswordDTO);
			break;
		case OTP:
			AuthNUserOTPDTO authNUserOTPDTO = new AuthNUserOTPDTO();
			authNUserOTPDTO.setAppId(RegistrationConstants.REGISTRATION_CLIENT);
			authNUserOTPDTO.setUserId(loginUserDTO.getUserId());
			authNUserOTPDTO.setOtp(loginUserDTO.getOtp());
			authNRequestDTO.setRequest(authNUserOTPDTO);
			break;
		default:
			AuthNClientIDDTO authNClientIDDTO = new AuthNClientIDDTO();
			authNClientIDDTO.setAppId(RegistrationConstants.REGISTRATION_CLIENT);
			authNClientIDDTO.setClientId(clientId);
			authNClientIDDTO.setSecretKey(secretKey);
			authNRequestDTO.setRequest(authNClientIDDTO);
			break;
		}

		LOGGER.info(LoggerConstants.LOG_SERVICE_DELEGATE_AUTH_DTO, APPLICATION_NAME,
				APPLICATION_ID, "Completed preparing AuthNRequestDTO Based on Login Mode");
		
		return authNRequestDTO;
	}

	@SuppressWarnings("unchecked")
	public void getAuthToken(LoginMode loginMode) throws RegBaseCheckedException {

		LOGGER.info(LoggerConstants.LOG_SERVICE_DELEGATE_GET_TOKEN, APPLICATION_NAME, APPLICATION_ID,
				"Fetching Auth Token based on Login Mode");

		Map<String, Object> responseMap = null;
		HttpHeaders responseHeader = null;
		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();

		// setting headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		AuthNRequestDTO authNRequestDTO = prepareAuthNRequestDTO(loginMode);
		HttpEntity<Object> requestEntity = new HttpEntity<>(authNRequestDTO, headers);
		requestHTTPDTO.setHttpEntity(requestEntity);
		requestHTTPDTO.setClazz(Object.class);
		requestHTTPDTO.setAuthRequired(false);

		try {
			String authNURL = environment
					.getProperty("auth_by_" + loginMode.getCode().toLowerCase() + "." + RegistrationConstants.SERVICE_URL);
			
			if (loginMode.compareTo(LoginMode.CLIENTID) == 0) {
				AuthNClientIDDTO authNClientIDDTO = (AuthNClientIDDTO) authNRequestDTO.getRequest();
				authNURL = authNURL.concat(String.format("?request.appId=%s&request.clientId=%s&request.secretKey=%s",
						authNClientIDDTO.getAppId(), authNClientIDDTO.getClientId(), authNClientIDDTO.getSecretKey()));
			}
			
			requestHTTPDTO.setUri(new URI(authNURL));
		} catch (URISyntaxException uriSyntaxException) {
			throw new RegBaseCheckedException(RegistrationConstants.REST_OAUTH_ERROR_CODE,
					RegistrationConstants.REST_OAUTH_ERROR_MSG, uriSyntaxException);
		}

		requestHTTPDTO.setHttpMethod(HttpMethod.POST);

		// set simple client http request
		setTimeout(requestHTTPDTO);

		try {
			responseMap = restClientUtil.invoke(requestHTTPDTO);
		} catch (HttpClientErrorException | HttpServerErrorException | ResourceAccessException
				| SocketTimeoutException restException) {
			throw new RegBaseCheckedException(RegistrationConstants.REST_OAUTH_ERROR_CODE,
					RegistrationConstants.REST_OAUTH_ERROR_MSG, restException);
		}

		if (null != responseMap && responseMap.size() > 0) {

			responseHeader = (HttpHeaders) responseMap.get(RegistrationConstants.REST_RESPONSE_HEADERS);
			
			LinkedHashMap<String, String> responseBody = (LinkedHashMap<String, String>) responseMap
					.get(RegistrationConstants.REST_RESPONSE_BODY);

			if (loginMode.equals(LoginMode.OTP) && !"Valdiation_Successful".equalsIgnoreCase(responseBody.get("message"))) {
				throw new RegBaseUncheckedException("OTP expired", "OTP expired");
			}
				

			if (null != responseHeader.get(RegistrationConstants.AUTH_SET_COOKIE)
					&& null != responseHeader.get(RegistrationConstants.AUTH_SET_COOKIE).get(0)) {
				try {
					Properties properties = new Properties();
					properties.load(new StringReader(
							responseHeader.get(RegistrationConstants.AUTH_SET_COOKIE).get(0).replaceAll(";", "\n")));
					AuthTokenDTO authTokenDTO = new AuthTokenDTO();
					authTokenDTO.setCookie(responseHeader.get(RegistrationConstants.AUTH_SET_COOKIE).get(0));
					authTokenDTO.setToken(properties.getProperty(RegistrationConstants.AUTH_AUTHORIZATION));
					authTokenDTO
							.setTokenMaxAge(Long.valueOf(properties.getProperty(RegistrationConstants.AUTH_MAX_AGE)));
					authTokenDTO.setLoginMode(loginMode.getCode());

					if (loginMode.compareTo(LoginMode.CLIENTID) == 0) {
						ApplicationContext.setAuthTokenDTO(authTokenDTO);
					} else {
						SessionContext.setAuthTokenDTO(authTokenDTO);
					}
				} catch (IOException ioException) {
					throw new RegBaseCheckedException(RegistrationConstants.REST_OAUTH_ERROR_CODE,
							RegistrationConstants.REST_OAUTH_ERROR_MSG, ioException);
				}

			}
		}

		LOGGER.info(LoggerConstants.LOG_SERVICE_DELEGATE_GET_TOKEN, APPLICATION_NAME, APPLICATION_ID,
				"Completed fetching Auth Token based on Login Mode");

	}

	public boolean isAuthTokenValid(String cookie) throws RegBaseCheckedException {

		LOGGER.info(LoggerConstants.LOG_SERVICE_DELEGATE_VALIDATE_TOKEN, APPLICATION_NAME, APPLICATION_ID,
				" get auth method called");

		boolean isTokenValid = false;

		try {
			if (cookie != null) {
				Map<String, Object> responseMap = null;
				RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();

				// setting headers
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				headers.add("Cookie", cookie);
				HttpEntity<?> requestEntity = new HttpEntity<>(headers);
				requestHTTPDTO.setHttpEntity(requestEntity);
				requestHTTPDTO.setClazz(Object.class);

				try {
					requestHTTPDTO.setUri(new URI(urlPath));
				} catch (URISyntaxException uriSyntaxException) {
					LOGGER.error(LoggerConstants.LOG_SERVICE_DELEGATE_VALIDATE_TOKEN, APPLICATION_NAME,
							APPLICATION_ID,
							uriSyntaxException.getMessage() + ExceptionUtils.getStackTrace(uriSyntaxException));
					throw new RegBaseCheckedException(RegistrationConstants.REST_OAUTH_ERROR_CODE,
							RegistrationConstants.REST_OAUTH_ERROR_MSG);
				}

				requestHTTPDTO.setHttpMethod(HttpMethod.POST);

				// set simple client http request
				setTimeout(requestHTTPDTO);

				try {
					responseMap = restClientUtil.invoke(requestHTTPDTO);
				} catch (HttpClientErrorException | HttpServerErrorException | ResourceAccessException
						| SocketTimeoutException restException) {
					LOGGER.error(LoggerConstants.LOG_SERVICE_DELEGATE_VALIDATE_TOKEN, APPLICATION_NAME,
							APPLICATION_ID, restException.getMessage() + ExceptionUtils.getStackTrace(restException));
				}

				if (null != responseMap && responseMap.size() > 0) {
					isTokenValid = true;
				}
			}
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LoggerConstants.LOG_SERVICE_DELEGATE_VALIDATE_TOKEN, APPLICATION_NAME, APPLICATION_ID, "Invalid Token for validation");
		}

		LOGGER.info(LoggerConstants.LOG_SERVICE_DELEGATE_VALIDATE_TOKEN, APPLICATION_NAME, APPLICATION_ID,
				" get auth method calling ends");

		return isTokenValid;

	}

}
