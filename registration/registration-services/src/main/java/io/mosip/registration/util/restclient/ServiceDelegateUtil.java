package io.mosip.registration.util.restclient;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
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

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dto.LoginUserDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;

/**
 * This is a helper class .it invokes with different classes to get the response
 * 
 * @author Yaswanth S
 * @since 1.0.0
 *
 */
@Component("serviceDelegateUtil")
@PropertySource(value = "classpath:spring.properties")
public class ServiceDelegateUtil {

	@Autowired
	RestClientUtil restClientUtil;

	@Autowired
	Environment environment;

	@Value("${HTTP_API_READ_TIMEOUT}")
	int readTimeout;

	@Value("${HTTP_API_WRITE_TIMEOUT}")
	int connectTimeout;

	@Value("${AUTH_URL}")
	private String urlPath;

	private static final Logger LOGGER = AppConfig.getLogger(ServiceDelegateUtil.class);

	/**
	 * Prepare GET request.
	 *
	 * @param serviceName   service to be invoked
	 * @param requestParams parameters along with url
	 * @param hasPathParams the has path params
	 * @return Object requiredType of object response Body
	 * @throws RegBaseCheckedException  generalised exception with errorCode and
	 *                                  errorMessage
	 * @throws HttpClientErrorException when client error exception from server
	 * @throws SocketTimeoutException   the socket timeout exception
	 * @throws HttpServerErrorException when server exception from server
	 */
	public Object get(String serviceName, Map<String, String> requestParams, boolean hasPathParams)
			throws RegBaseCheckedException, HttpClientErrorException, SocketTimeoutException {

		LOGGER.debug("REGISTRATION - SERVICE_DELEGATE_UTIL - GET", APPLICATION_NAME, APPLICATION_ID,
				"Get method has been called");

		Map<String, Object> responseMap = null;
		Object responseBody = null;
		String authHeader = RegistrationConstants.EMPTY;

		Boolean authRequired = Boolean
				.valueOf(environment.getProperty(serviceName + "." + RegistrationConstants.AUTH_REQUIRED));

		if (authRequired) {
			// TODO - if batch get secrete key , normal login get user from session context
			LoginUserDTO userDTO = (LoginUserDTO) ApplicationContext.getApplicationContext().getApplicationMap()
					.get(RegistrationConstants.USER_DTO);
			authHeader = getAuthTokenId(userDTO);

		}

		if ((!authRequired) || (authRequired && !authHeader.isEmpty())) {

			RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();

			try {

				requestHTTPDTO = prepareGETRequest(requestHTTPDTO, serviceName, requestParams, authHeader);

				// URI creation
				String url = environment.getProperty(serviceName + "." + RegistrationConstants.SERVICE_URL);

				if (hasPathParams) {
					requestHTTPDTO.setUri(UriComponentsBuilder.fromUriString(url).build(requestParams));
				} else {
					/** Set URI */
					setURI(requestHTTPDTO, requestParams, url);
				}

				LOGGER.debug("REGISTRATION - SERVICE_DELEGATE_UTIL - GET", APPLICATION_NAME, APPLICATION_ID,
						"set uri method called");

			} catch (RegBaseCheckedException baseCheckedException) {
				throw new RegBaseCheckedException(
						RegistrationExceptionConstants.REG_SERVICE_DELEGATE_UTIL_CODE.getErrorCode(),
						RegistrationExceptionConstants.REG_SERVICE_DELEGATE_UTIL_CODE.getErrorMessage());
			}

			responseMap = restClientUtil.invoke(requestHTTPDTO);
			if (null != responseMap && responseMap.size() > 0
					&& null != responseMap.get(RegistrationConstants.REST_RESPONSE_BODY)) {
				responseBody = responseMap.get(RegistrationConstants.REST_RESPONSE_BODY);
			}
			LOGGER.debug("REGISTRATION - SERVICE_DELEGATE_UTIL - GET", APPLICATION_NAME, APPLICATION_ID,
					"Get method has been ended");
		}

		return responseBody;

	}

	/**
	 * prepare POST request.
	 *
	 * @param serviceName service to be invoked
	 * @param object      request type
	 * @return Object requiredType of object response Body
	 * @throws RegBaseCheckedException  generalised exception with errorCode and
	 *                                  errorMessage
	 * @throws HttpClientErrorException when client error exception from server
	 * @throws SocketTimeoutException   the socket timeout exception
	 * @throws ResourceAccessException  the resource access exception
	 * @throws HttpServerErrorException when server exception from server
	 */
	public Object post(String serviceName, Object object)
			throws RegBaseCheckedException, HttpClientErrorException, SocketTimeoutException, ResourceAccessException {
		LOGGER.debug("REGISTRATION - SERVICE_DELEGATE_UTIL - POST", APPLICATION_NAME, APPLICATION_ID,
				" post method called");

		RequestHTTPDTO requestDto;
		Object responseBody = null;
		Map<String, Object> responseMap = null;
		String authHeader = RegistrationConstants.EMPTY;

		Boolean authRequired = Boolean
				.valueOf(environment.getProperty(serviceName + "." + RegistrationConstants.AUTH_REQUIRED));

		if (authRequired) {
			// TODO - if batch get secrete key , normal login get user from session context
			LoginUserDTO userDTO = (LoginUserDTO) ApplicationContext.getApplicationContext().getApplicationMap()
					.get(RegistrationConstants.USER_DTO);
			authHeader = getAuthTokenId(userDTO);

		}

		if ((!authRequired) || (authRequired && !authHeader.isEmpty())) {

			try {
				requestDto = preparePOSTRequest(serviceName, object, authHeader);
			} catch (RegBaseCheckedException baseCheckedException) {
				throw new RegBaseCheckedException(RegistrationConstants.SERVICE_DELEGATE_UTIL,
						baseCheckedException.getMessage());
			}
			responseMap = restClientUtil.invoke(requestDto);
			if (null != responseMap && responseMap.size() > 0
					&& null != responseMap.get(RegistrationConstants.REST_RESPONSE_BODY)) {
				responseBody = responseMap.get(RegistrationConstants.REST_RESPONSE_BODY);
			}
			LOGGER.debug("REGISTRATION - SERVICE_DELEGATE_UTIL - POST", APPLICATION_NAME, APPLICATION_ID,
					"post method ended");
		}
		return responseBody;
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
			final Map<String, String> requestParams, String authHeader) throws RegBaseCheckedException {
		LOGGER.debug("REGISTRATION - SERVICE_DELEGATE_UTIL - GET", APPLICATION_NAME, APPLICATION_ID,
				"Prepare Get request method called");

		// prepare httpDTO except rquest type and uri build
		requestHTTPDTO = prepareRequest(requestHTTPDTO, serviceName, null, authHeader);
		// ResponseType
		String responseClassName = environment.getProperty(serviceName + "." + RegistrationConstants.RESPONSE_TYPE);
		Class<?> responseClass = null;
		try {
			responseClass = Class.forName(responseClassName);
		} catch (ClassNotFoundException classNotFoundException) {
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
	 * @param serviceName service to be invoked
	 * @param object      request type
	 * @param authHeader  the auth header
	 * @return RequestHTTPDTO requestHTTPDTO with required data
	 * @throws RegBaseCheckedException the reg base checked exception
	 */
	private RequestHTTPDTO preparePOSTRequest(final String serviceName, final Object object, String authHeader)
			throws RegBaseCheckedException {
		LOGGER.debug("REGISTRATION - SERVICE_DELEGATE_UTIL - POST", APPLICATION_NAME, APPLICATION_ID,
				"Prepare post request method called");

		// DTO need to to be prepared
		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();

		// prepare httpDTO except rquest type and uri build
		requestHTTPDTO = prepareRequest(requestHTTPDTO, serviceName, object, authHeader);
		// URI creation
		String url = environment.getProperty(serviceName + "." + RegistrationConstants.SERVICE_URL);
		setURI(requestHTTPDTO, null, url);
		LOGGER.debug("REGISTRATION - SERVICE_DELEGATE_UTIL - POST", APPLICATION_NAME, APPLICATION_ID,
				"get uri method called");

		// RequestType
		String requestClassName = environment.getProperty(serviceName + "." + RegistrationConstants.REQUEST_TYPE);
		Class<?> requestClass = null;
		requestHTTPDTO.setClazz(Object.class);
		LOGGER.debug("REGISTRATION - SERVICE_DELEGATE_UTIL - POST", APPLICATION_NAME, APPLICATION_ID,
				"Prepare post request method ended");

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
	 * Setup of Auth Headers.
	 *
	 * @param httpHeaders  http headers
	 * @param authRequired whether auth required or not
	 * @param authHeader   auth header
	 * @param authDetails  auth details
	 * @param oauthHeader  the oauth header
	 */
	private void setAuthHeaders(HttpHeaders httpHeaders, boolean authRequired, String authHeader, String authDetails,
			String oauthHeader) {
		String[] arrayAuthHeaders = null;

		if (authRequired && authHeader != null) {
			arrayAuthHeaders = authHeader.split(":");
			if (arrayAuthHeaders[1].equals(RegistrationConstants.AUTH_TYPE)) {
				httpHeaders.add(arrayAuthHeaders[0], arrayAuthHeaders[1] + " " + authDetails);

			} else if (arrayAuthHeaders[1].equals(RegistrationConstants.REST_OAUTH)) {
				httpHeaders.add(arrayAuthHeaders[0], oauthHeader);
			}

		}

	}

	/**
	 * Setup of headers
	 * 
	 * @param httpHeaders http headers
	 * @param headers     headers
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
	 * @param requestHTTPDTO create requestedHTTPDTO
	 * @param serviceName    service name to be called
	 * @param object         object to be included in HTTP entities
	 * @return
	 */
	private RequestHTTPDTO prepareRequest(RequestHTTPDTO requestHTTPDTO, String serviceName, Object object,
			String autHeader) {
		LOGGER.debug("REGISTRATION - SERVICE_DELEGATE_UTIL - PREPARE_REQUEST", APPLICATION_NAME, APPLICATION_ID,
				" prepare request method  called");

		// HTTP headers
		HttpHeaders httpHeaders = new HttpHeaders();

		// HTTP method
		HttpMethod httpMethod = HttpMethod
				.valueOf(environment.getProperty(serviceName + "." + RegistrationConstants.HTTPMETHOD));
		// Headers

		String headers = environment.getProperty(serviceName + "." + RegistrationConstants.HEADERS);
		setHeaders(httpHeaders, headers);
		LOGGER.debug("REGISTRATION - SERVICE_DELEGATE_UTIL - PREPARE_REQUEST", APPLICATION_NAME, APPLICATION_ID,
				" set Headers method called");

		// AuthHeader
		String authHeader = environment.getProperty(serviceName + "." + RegistrationConstants.AUTH_HEADER);

		// Auth required
		Boolean authRequired = Boolean
				.valueOf(environment.getProperty(serviceName + "." + RegistrationConstants.AUTH_REQUIRED));

		setAuthHeaders(httpHeaders, authRequired, authHeader, null, autHeader);
		LOGGER.debug("REGISTRATION - SERVICE_DELEGATE_UTIL - PREPARE_REQUEST", APPLICATION_NAME, APPLICATION_ID,
				" set Auth Headers  method  called");

		// HTTP entity
		@SuppressWarnings({ "unchecked", "rawtypes" })
		HttpEntity<?> httpEntity = new HttpEntity(object, httpHeaders);

		requestHTTPDTO.setHttpMethod(httpMethod);
		requestHTTPDTO.setHttpEntity(httpEntity);
		// set timeout
		setTimeout(requestHTTPDTO);

		LOGGER.debug("REGISTRATION - SERVICE_DELEGATE_UTIL - PREPARE_REQUEST", APPLICATION_NAME, APPLICATION_ID,
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

	/**
	 * Gets the auth token id.
	 *
	 * @param loginUserDTO the login user DTO
	 * @return the auth token id
	 * @throws RegBaseCheckedException the reg base checked exception
	 */
	private String getAuthTokenId(LoginUserDTO loginUserDTO) throws RegBaseCheckedException {

		LOGGER.debug("REGISTRATION - SERVICE_DELEGATE_UTIL - GET_AUTH_TOKEN", APPLICATION_NAME, APPLICATION_ID,
				" get auth method called");

		String oAuthToken = RegistrationConstants.EMPTY;
		List<String> authToken = new ArrayList<>();
		Map<String, Object> responseMap = null;
		HttpHeaders responseHeader = null;
		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();

		// setting params
		Map<String, Object> map = new HashMap<>();
		map.put(RegistrationConstants.REST_OAUTH_USER_NAME, loginUserDTO.getUserId());
		map.put(RegistrationConstants.REST_OAUTH_USER_PSWD, loginUserDTO.getPassword());

		// setting headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(map, headers);
		requestHTTPDTO.setHttpEntity(requestEntity);
		requestHTTPDTO.setClazz(Object.class);

		try {
			requestHTTPDTO.setUri(new URI(urlPath));
		} catch (URISyntaxException uriSyntaxException) {
			LOGGER.error("REGISTRATION - SERVICE_DELEGATE_UTIL - GET_AUTH_TOKEN", APPLICATION_NAME, APPLICATION_ID,
					uriSyntaxException.getMessage());
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
			LOGGER.error("REGISTRATION - SERVICE_DELEGATE_UTIL - GET_AUTH_TOKEN", APPLICATION_NAME, APPLICATION_ID,
					restException.getMessage());
			throw new RegBaseCheckedException(RegistrationConstants.REST_OAUTH_ERROR_CODE,
					RegistrationConstants.REST_OAUTH_ERROR_MSG);
		}

		if (null != responseMap && responseMap.size() > 0) {

			responseHeader = (HttpHeaders) responseMap.get(RegistrationConstants.REST_RESPONSE_HEADERS);

			if (null != responseHeader.get(RegistrationConstants.REST_AUTHORIZATION)
					&& null != responseHeader.get(RegistrationConstants.REST_AUTHORIZATION).get(0)) {

				oAuthToken = responseHeader.get(RegistrationConstants.REST_AUTHORIZATION).get(0);

			}
		}

		LOGGER.debug("REGISTRATION - SERVICE_DELEGATE_UTIL - GET_AUTH_TOKEN", APPLICATION_NAME, APPLICATION_ID,
				" get auth method calling ends");

		return oAuthToken;

	}

}
