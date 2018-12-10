package io.mosip.registration.util.restclient;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationExceptions;
import io.mosip.registration.exception.RegBaseCheckedException;

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

	private static final Logger LOGGER = AppConfig.getLogger(ServiceDelegateUtil.class);

	/**
	 * Prepare GET request
	 * 
	 * @param serviceName   service to be invoked
	 * @param requestParams parameters along with url
	 * @return Object requiredType of object response Body
	 * @throws RegBaseCheckedException  generalised exception with errorCode and
	 *                                  errorMessage
	 * @throws HttpClientErrorException when client error exception from server
	 * @throws HttpServerErrorException when server exception from server
	 */
	public Object get(String serviceName, Map<String, String> requestParams)
			throws RegBaseCheckedException, HttpClientErrorException, SocketTimeoutException {

		LOGGER.debug("REGISTRATION - SERVICE_DELEGATE_UTIL - GET", APPLICATION_NAME, APPLICATION_ID,
				"Get method has been called");

		Object responseBody = null;
		RequestHTTPDTO requestDto = null;
		try {
			requestDto = prepareGETRequest(serviceName, requestParams);

		} catch (RegBaseCheckedException baseCheckedException) {
			throw new RegBaseCheckedException(RegistrationExceptions.REG_SERVICE_DELEGATE_UTIL_CODE.getErrorCode(),
					RegistrationExceptions.REG_SERVICE_DELEGATE_UTIL_CODE.getErrorMessage());
		}

		responseBody = restClientUtil.invoke(requestDto);
		LOGGER.debug("REGISTRATION - SERVICE_DELEGATE_UTIL - GET", APPLICATION_NAME, APPLICATION_ID,
				"Get method has been ended");

		return responseBody;

	}

	/**
	 * prepare POST request
	 * 
	 * @param serviceName service to be invoked
	 * @param object      request type
	 * @return Object requiredType of object response Body
	 * @throws RegBaseCheckedException  generalised exception with errorCode and
	 *                                  errorMessage
	 * @throws HttpClientErrorException when client error exception from server
	 * @throws HttpServerErrorException when server exception from server
	 */
	public Object post(String serviceName, Object object)
			throws RegBaseCheckedException, HttpClientErrorException, SocketTimeoutException ,ResourceAccessException {
		LOGGER.debug("REGISTRATION - SERVICE_DELEGATE_UTIL - POST", APPLICATION_NAME, APPLICATION_ID,
				" post method called");

		RequestHTTPDTO requestDto;
		Object responseBody = null;
		try {
			requestDto = preparePOSTRequest(serviceName, object);
		} catch (RegBaseCheckedException baseCheckedException) {
			throw new RegBaseCheckedException(RegistrationConstants.SERVICE_DELEGATE_UTIL,
					baseCheckedException.getMessage());
		}
		// set timeout
		setTimeout(requestDto);
		responseBody = restClientUtil.invoke(requestDto);
		LOGGER.debug("REGISTRATION - SERVICE_DELEGATE_UTIL - POST", APPLICATION_NAME, APPLICATION_ID,
				"post method ended");

		return responseBody;
	}

	/**
	 * Prepare GET request
	 * 
	 * @param serviceName   service to be invoked
	 * @param requestParams params need to add along with url
	 * @return RequestHTTPDTO requestHTTPDTO with required data
	 * @throws RegBaseCheckedException
	 * 
	 */
	private RequestHTTPDTO prepareGETRequest(final String serviceName, final Map<String, String> requestParams)
			throws RegBaseCheckedException {
		LOGGER.debug("REGISTRATION - SERVICE_DELEGATE_UTIL - GET", APPLICATION_NAME, APPLICATION_ID,
				"Prepare Get request method called");

		// DTO need to to be prepared
		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();

		// prepare httpDTO except rquest type and uri build
		requestHTTPDTO = prepareRequest(requestHTTPDTO, serviceName, null);

		// URI creation
		String url = environment.getProperty(serviceName + "." + RegistrationConstants.SERVICE_URL);
		URI uri = getUri(requestParams, url);
		LOGGER.debug("REGISTRATION - SERVICE_DELEGATE_UTIL - GET", APPLICATION_NAME, APPLICATION_ID,
				"get uri method called");

		// ResponseType
		String responseClassName = environment.getProperty(serviceName + "." + RegistrationConstants.RESPONSE_TYPE);
		Class<?> responseClass = null;
		try {
			responseClass = Class.forName(responseClassName);
		} catch (ClassNotFoundException classNotFoundException) {
			throw new RegBaseCheckedException(RegistrationExceptions.REG_CLASS_NOT_FOUND_ERROR_CODE.getErrorCode(),
					RegistrationExceptions.REG_CLASS_NOT_FOUND_ERROR_CODE.getErrorMessage());
		}

		requestHTTPDTO.setClazz(responseClass);
		requestHTTPDTO.setUri(uri);
		LOGGER.debug("REGISTRATION - SERVICE_DELEGATE_UTIL - GET", APPLICATION_NAME, APPLICATION_ID,
				"Prepare Get request method ended");

		return requestHTTPDTO;
	}

	/**
	 * @param serviceName service to be invoked
	 * @param object      request type
	 * @return RequestHTTPDTO requestHTTPDTO with required data
	 * @throws RegBaseCheckedException
	 */
	private RequestHTTPDTO preparePOSTRequest(final String serviceName, final Object object)
			throws RegBaseCheckedException {
		LOGGER.debug("REGISTRATION - SERVICE_DELEGATE_UTIL - POST", APPLICATION_NAME, APPLICATION_ID,
				"Prepare post request method called");

		// DTO need to to be prepared
		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();

		// prepare httpDTO except rquest type and uri build
		requestHTTPDTO = prepareRequest(requestHTTPDTO, serviceName, object);
		
		// URI creation
		String url = environment.getProperty(serviceName + "." + RegistrationConstants.SERVICE_URL);
		URI uri = getUri(null, url);
		LOGGER.debug("REGISTRATION - SERVICE_DELEGATE_UTIL - POST", APPLICATION_NAME, APPLICATION_ID,
				"get uri method called");

		// RequestType
		String requestClassName = environment.getProperty(serviceName + "." + RegistrationConstants.REQUEST_TYPE);
		Class<?> requestClass = null;
		try {
			requestClass = Class.forName(requestClassName);
		} catch (ClassNotFoundException classNotFoundException) {
			throw new RegBaseCheckedException(RegistrationExceptions.REG_CLASS_NOT_FOUND_ERROR_CODE.getErrorCode(),
					RegistrationExceptions.REG_CLASS_NOT_FOUND_ERROR_CODE.getErrorMessage());
		}

		requestHTTPDTO.setUri(uri);
		requestHTTPDTO.setClazz(requestClass);
		LOGGER.debug("REGISTRATION - SERVICE_DELEGATE_UTIL - POST", APPLICATION_NAME, APPLICATION_ID,
				"Prepare post request method ended");

		return requestHTTPDTO;

	}

	/**
	 * URI creation
	 * 
	 * @param requestParams params need to add along with url
	 * @param url           url to be invoked
	 * @return URI uri created by url
	 */
	public URI getUri(Map<String, String> requestParams, String url) {
		// BuildURIComponent
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(url);
		if (requestParams != null) {
			Set<String> set = requestParams.keySet();
			for (String queryParamName : set) {
				uriComponentsBuilder.queryParam(queryParamName, requestParams.get(queryParamName));

			}
		}
		URI uri = uriComponentsBuilder.build().toUri();
		return uri;
	}

	/**
	 * Setup of Auth Headers
	 * 
	 * @param httpHeaders  http headers
	 * @param authRequired whether auth required or not
	 * @param authHeader   auth header
	 * @param authDetails  auth details
	 */
	private void setAuthHeaders(HttpHeaders httpHeaders, boolean authRequired, String authHeader, String authDetails) {
		String[] arrayAuthHeaders = null;

		if (authRequired && authHeader != null) {
			arrayAuthHeaders = authHeader.split(":");
			if (arrayAuthHeaders[1].equals(RegistrationConstants.AUTH_TYPE)) {
				httpHeaders.add(arrayAuthHeaders[0], arrayAuthHeaders[1] + " " + authDetails);

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
	private RequestHTTPDTO prepareRequest(RequestHTTPDTO requestHTTPDTO, String serviceName, Object object) {
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

		setAuthHeaders(httpHeaders, authRequired, authHeader, null);
		LOGGER.debug("REGISTRATION - SERVICE_DELEGATE_UTIL - PREPARE_REQUEST", APPLICATION_NAME, APPLICATION_ID,
				" set Auth Headers  method  called");

		// HTTP entity
		@SuppressWarnings({ "unchecked", "rawtypes" })
		HttpEntity<?> httpEntity = new HttpEntity(object, httpHeaders);
		
		requestHTTPDTO.setHttpMethod(httpMethod);
		requestHTTPDTO.setHttpEntity(httpEntity);
		LOGGER.debug("REGISTRATION - SERVICE_DELEGATE_UTIL - PREPARE_REQUEST", APPLICATION_NAME, APPLICATION_ID,
				" prepare request method  called");

		return requestHTTPDTO;

	}
	
	/**
	 * Method to set the request timeout 
	 * @param requestHTTPDTO
	 */
	private void  setTimeout(RequestHTTPDTO requestHTTPDTO) {
		// Timeout in milli second
		SimpleClientHttpRequestFactory requestFactory=new SimpleClientHttpRequestFactory(); 
		requestFactory.setReadTimeout(readTimeout);
		requestFactory.setConnectTimeout(connectTimeout);
		requestHTTPDTO.setSimpleClientHttpRequestFactory(requestFactory);
	}

}
