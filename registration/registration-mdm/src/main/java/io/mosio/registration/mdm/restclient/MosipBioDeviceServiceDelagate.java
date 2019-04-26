package io.mosio.registration.mdm.restclient;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.net.SocketTimeoutException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.util.restclient.RequestHTTPDTO;
import io.mosip.registration.util.restclient.RestClientUtil;

/**
 * Delegates all the rest service calls related to biometric device access
 * 
 * @author balamurugan.ramamoorthy
 *
 */
@Service
public class MosipBioDeviceServiceDelagate {

	@Value("${HTTP_API_READ_TIMEOUT}")
	private int readTimeout;

	@Value("${HTTP_API_WRITE_TIMEOUT}")
	private int connectTimeout;

	@Autowired
	private RestClientUtil restClientUtil;

	@Autowired
	private Environment environment;

	private static final Logger LOGGER = AppConfig.getLogger(MosipBioDeviceServiceDelagate.class);

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

		prepareRequest(requestHTTPDTO, serviceName, request, responseType);

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
			Class<?> responseType) {
		requestHTTPDTO.setHttpMethod(
				HttpMethod.valueOf(getEnvironmentProperty(serviceName, RegistrationConstants.HTTPMETHOD)));
		requestHTTPDTO.setHttpHeaders(new HttpHeaders());
		requestHTTPDTO.setRequestBody(request);
		requestHTTPDTO.setClazz(responseType != null ? responseType.getClass() : Object.class);
		// set timeout
		setTimeout(requestHTTPDTO);
		// Headers
		setHeaders(requestHTTPDTO.getHttpHeaders(), getEnvironmentProperty(serviceName, RegistrationConstants.HEADERS));
		requestHTTPDTO.setAuthRequired(false);
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
	 * Setup of headers
	 * 
	 * @param httpHeaders
	 *            http headers
	 * @param headers
	 *            headers
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
					httpHeaders.add(headerValues[0], headerValues[1]);
				}
			}
		}

		LOGGER.info(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_PREPARE_REQUEST, APPLICATION_NAME, APPLICATION_ID,
				"Completed reparing Header for web-service request");
	}

	private String getEnvironmentProperty(String serviceName, String serviceComponent) {
		return environment.getProperty(serviceName.concat(RegistrationConstants.DOT).concat(serviceComponent));
	}

	private boolean isResponseValid(Map<String, Object> responseMap, String key) {
		return !(null == responseMap || responseMap.isEmpty() || !responseMap.containsKey(key));
	}
}
