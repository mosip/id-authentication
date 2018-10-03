package io.mosip.registration.util.restclient;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.registration.constants.RegConstants;
import io.mosip.registration.constants.RegProcessorExceptionCode;
import io.mosip.registration.constants.RegProcessorExceptionEnum;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * This is a helper class .it invokes with different classes to get the response
 * 
 * @author Yaswanth S
 * @since 1.0.0
 *
 */
@Component("serviceDelegateUtil")
@PropertySource(value = "classpath:config.properties")
public class ServiceDelegateUtil {

	@Autowired
	RestClientUtil restClientUtil;

	@Autowired
	Environment environment;

	
	/** Prepare GET request
	 * 
	 * @param serviceName
	 *            service to be invoked
	 * @param requestParams
	 *            parameters along with url
	 * @return  Object requiredType of object response Body
	 * @throws RegBaseCheckedException
	 * @throws HttpClientErrorException
	 */
	public Object get(String serviceName, Map<String, String> requestParams)
			throws RegBaseCheckedException, HttpClientErrorException {

		Object responseBody = null;
		RequestHTTPDTO requestDto = null;
        try {
		requestDto = prepareGETRequest(serviceName, requestParams);
        } catch (RegBaseCheckedException baseCheckedException) {
			throw new RegBaseCheckedException(RegProcessorExceptionEnum.REG_SERVICE_DELEGATE_UTIL_CODE.getErrorCode(),
					RegProcessorExceptionEnum.REG_SERVICE_DELEGATE_UTIL_CODE.getErrorMessage());
		}
		
		responseBody = restClientUtil.invoke(requestDto);

		return responseBody;

	}

	/**
	 * prepare POST request
	 * 
	 * @param serviceName
	 *            service to be invoked
	 * @param object
	 *            request type
	 * @return  Object requiredType of object response Body
	 * @throws RegBaseCheckedException
	 *             generalised exception with errorCode and errorMessage
	 * @throws HttpClientErrorException
	 */
	public Object post(String serviceName, Object object) throws RegBaseCheckedException, HttpClientErrorException {

		RequestHTTPDTO requestDto;
		Object responseBody = null;
		try {
			requestDto = preparePOSTRequest(serviceName, object);
		} catch (RegBaseCheckedException baseCheckedException) {
			throw new RegBaseCheckedException(RegProcessorExceptionCode.SERVICE_DELEGATE_UTIL,
					baseCheckedException.getMessage());
		}

		responseBody = restClientUtil.invoke(requestDto);

		return responseBody;
	}

	/**
	 * Prepare GET request
	 * 
	 * @param serviceName
	 *            service to be invoked
	 * @param requestParams
	 *            params need to add along with url
	 * @return RequestHTTPDTO requestHTTPDTO with required data
	 * @throws RegBaseCheckedException
	 * 
	 */
	private RequestHTTPDTO prepareGETRequest(final String serviceName, final Map<String, String> requestParams)
			throws RegBaseCheckedException {
		// DTO need to to be prepared
		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();

		// prepare httpDTO except rquest type and uri build
		requestHTTPDTO = prepareRequest(requestHTTPDTO, serviceName, null);

		// URI creation
		String url = environment.getProperty(serviceName + "." + RegConstants.SERVICE_URL);
		URI uri = getUri(requestParams, url);

		// ResponseType
		String responseClassName = environment.getProperty(serviceName + "." + RegConstants.RESPONSE_TYPE);
		Class<?> responseClass = null;
		try {
			responseClass = Class.forName(responseClassName);
		} catch (ClassNotFoundException classNotFoundException) {
			throw new RegBaseCheckedException(RegProcessorExceptionEnum.REG_CLASS_NOT_FOUND_ERROR_CODE.getErrorCode(),
					RegProcessorExceptionEnum.REG_CLASS_NOT_FOUND_ERROR_CODE.getErrorMessage());
		}

		requestHTTPDTO.setClazz(responseClass);
		requestHTTPDTO.setUri(uri);
		return requestHTTPDTO;
	}

	/**
	 * @param serviceName
	 *            service to be invoked
	 * @param object
	 *            request type
	 * @return RequestHTTPDTO requestHTTPDTO with required data
	 * @throws RegBaseCheckedException
	 * @throws ClassNotFoundException
	 */
	private RequestHTTPDTO preparePOSTRequest(final String serviceName, final Object object)
			throws RegBaseCheckedException {
		// DTO need to to be prepared
		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();

		// prepare httpDTO except rquest type and uri build
		requestHTTPDTO = prepareRequest(requestHTTPDTO, serviceName, object);

		// URI creation
		String url = environment.getProperty(serviceName + "." + RegConstants.SERVICE_URL);
		URI uri = getUri(null, url);

		// RequestType
		String requestClassName = environment.getProperty(serviceName + "." + RegConstants.REQUEST_TYPE);
		Class<?> requestClass = null;
		try {
			requestClass = Class.forName(requestClassName);
		} catch (ClassNotFoundException classNotFoundException) {
			throw new RegBaseCheckedException(RegProcessorExceptionEnum.REG_CLASS_NOT_FOUND_ERROR_CODE.getErrorCode(),
					RegProcessorExceptionEnum.REG_CLASS_NOT_FOUND_ERROR_CODE.getErrorMessage());
		}

		requestHTTPDTO.setUri(uri);
		requestHTTPDTO.setClazz(requestClass);

		return requestHTTPDTO;

	}

	/**
	 * URI creation
	 * 
	 * @param requestParams
	 *            params need to add along with url
	 * @param url
	 *            url to be invoked
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
	 * @param httpHeaders
	 *            http headers
	 * @param authRequired
	 *            whether auth required or not
	 * @param authHeader
	 *            auth header
	 * @param authDetails
	 *            auth details
	 */
	private void setAuthHeaders(HttpHeaders httpHeaders, boolean authRequired, String authHeader, String authDetails) {
		String[] arrayAuthHeaders = null;

		if (authRequired && authHeader != null) {
			arrayAuthHeaders = authHeader.split(":");
			if (arrayAuthHeaders[1].equals(RegConstants.AUTH_TYPE)) {
				httpHeaders.add(arrayAuthHeaders[0], arrayAuthHeaders[1] + " " + authDetails);

			}

		}

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

	private RequestHTTPDTO prepareRequest(RequestHTTPDTO requestHTTPDTO, String serviceName, Object object) {
		// HTTP headers
		HttpHeaders httpHeaders = new HttpHeaders();

		// HTTP method
		HttpMethod httpMethod = HttpMethod
				.valueOf(environment.getProperty(serviceName + "." + RegConstants.HTTPMETHOD));
		// Headers
		String headers = environment.getProperty(serviceName + "." + RegConstants.HEADERS);
		setHeaders(httpHeaders, headers);

		// AuthHeader
		String authHeader = environment.getProperty(serviceName + "." + RegConstants.AUTH_HEADER);

		// Auth required
		Boolean authRequired = Boolean.valueOf(environment.getProperty(serviceName + "." + RegConstants.AUTH_REQUIRED));

		setAuthHeaders(httpHeaders, authRequired, authHeader, null);

		// HTTP entity
		@SuppressWarnings({ "unchecked", "rawtypes" })
		HttpEntity<?> httpEntity = new HttpEntity(object, httpHeaders);

		requestHTTPDTO.setHttpMethod(httpMethod);
		requestHTTPDTO.setHttpEntity(httpEntity);
		return requestHTTPDTO;

	}

}
