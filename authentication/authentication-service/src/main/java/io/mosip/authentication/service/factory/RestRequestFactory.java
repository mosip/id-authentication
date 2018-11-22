package io.mosip.authentication.service.factory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.kernel.core.logger.spi.Logger;
import lombok.NoArgsConstructor;

/**
 * A factory for creating and building RestRequest objects from
 * rest-services.properties
 * 
 * @author Manoj SP
 *
 */
@Component
@NoArgsConstructor
public class RestRequestFactory {

	/** The Constant DEFAULT_SESSION_ID. */
	private static final String DEFAULT_SESSION_ID = "sessionId";

	/** The Constant METHOD_BUILD_REQUEST. */
	private static final String METHOD_BUILD_REQUEST = "buildRequest";

	/** The env. */
	@Autowired
	private Environment env;

	/** The logger. */
	private static Logger mosipLogger =
			IdaLogger.getLogger(RestRequestFactory.class);

	/**
	 * Builds the request.
	 *
	 * @param restService
	 *            the rest service
	 * @param requestBody
	 *            the request body
	 * @param returnType
	 *            the return type
	 * @return the rest request DTO
	 * @throws IDDataValidationException
	 *             the ID data validation exception
	 */
	public RestRequestDTO buildRequest(RestServicesConstants restService,
			Object requestBody, Class<?> returnType)
			throws IDDataValidationException {
		RestRequestDTO request = new RestRequestDTO();
		MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
		Map<String, String> pathVariables = new HashMap<>();
		HttpHeaders headers = new HttpHeaders();

		String serviceName = restService.getServiceName();

		String uri = env.getProperty(serviceName.concat(".rest.uri"));
		String httpMethod =
				env.getProperty(serviceName.concat(".rest.httpMethod"));
		String timeout = env.getProperty(serviceName.concat(".rest.timeout"));
		headers.setContentType(MediaType.valueOf(env
				.getProperty(serviceName.concat(".rest.headers.mediaType"))));
		checkUri(request, uri);

		checkHttpMethod(request, httpMethod);

		if (requestBody != null) {
			if (headers != null && !headers.getContentType()
					.includes(MediaType.MULTIPART_FORM_DATA)) {
				request.setRequestBody(requestBody);
			} else {
				if (requestBody instanceof MultiValueMap) {
					request.setRequestBody(requestBody);
				} else {
					throw new IDDataValidationException(
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER
									.getErrorCode(),
							String.format(
									IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER
											.getErrorMessage(),
									"requestBody"));
				}
			}
		}

		checkReturnType(returnType, request);

		constructParams(paramMap, pathVariables, headers, serviceName);

		request.setHeaders(headers);

		if (!paramMap.isEmpty()) {
			request.setParams(paramMap);
		}

		if (!pathVariables.isEmpty()) {
			request.setPathVariables(pathVariables);
		}

		if (checkIfEmptyOrWhiteSpace(timeout)) {
			request.setTimeout(Integer.parseInt(timeout));
		}

		return request;
	}

	/**
	 * Construct params.
	 *
	 * @param paramMap
	 *            the param map
	 * @param pathVariables
	 *            the path variables
	 * @param headers
	 *            the headers
	 * @param serviceName
	 *            the service name
	 */
	private void constructParams(MultiValueMap<String, String> paramMap,
			Map<String, String> pathVariables, HttpHeaders headers,
			String serviceName) {
		((AbstractEnvironment) env).getPropertySources()
				.forEach((PropertySource<?> source) -> {
					if (source instanceof MapPropertySource) {
						Map<String, Object> systemProperties =
								((MapPropertySource) source).getSource();

						systemProperties.keySet().forEach((String property) -> {
							if (property.startsWith(
									serviceName.concat(".rest.headers"))) {
								headers.add(
										property.replace(serviceName
												.concat(".rest.headers."), ""),
										env.getProperty(property));
							}
							if (property.startsWith(serviceName
									.concat(".rest.uri.queryparam."))) {
								paramMap.put(
										property.replace(serviceName.concat(
												".rest.uri.queryparam."), ""),
										Collections.singletonList(
												env.getProperty(property)));
							}
							if (property.startsWith(serviceName
									.concat(".rest.uri.pathparam."))) {
								pathVariables
										.put(property.replace(
												serviceName.concat(
														".rest.uri.pathparam."),
												""), env.getProperty(property));
							}
						});
					}
				});
	}

	/**
	 * Check return type.
	 *
	 * @param returnType
	 *            the return type
	 * @param request
	 *            the request
	 * @throws IDDataValidationException
	 *             the ID data validation exception
	 */
	private void checkReturnType(Class<?> returnType, RestRequestDTO request)
			throws IDDataValidationException {
		if (returnType != null) {
			request.setResponseType(returnType);
		} else {

			mosipLogger.error(DEFAULT_SESSION_ID, METHOD_BUILD_REQUEST,
					"returnType",
					"throwing IDDataValidationException - INVALID_RETURN_TYPE"
							+ returnType);
			throw new IDDataValidationException(
					IdAuthenticationErrorConstants.INVALID_RETURN_TYPE);
		}
	}

	/**
	 * Check http method.
	 *
	 * @param request
	 *            the request
	 * @param httpMethod
	 *            the http method
	 * @throws IDDataValidationException
	 *             the ID data validation exception
	 */
	private void checkHttpMethod(RestRequestDTO request, String httpMethod)
			throws IDDataValidationException {
		if (checkIfEmptyOrWhiteSpace(httpMethod)) {
			request.setHttpMethod(HttpMethod.valueOf(httpMethod));
		} else {

			mosipLogger.error(DEFAULT_SESSION_ID, METHOD_BUILD_REQUEST,
					"httpMethod",
					"throwing IDDataValidationException - INVALID_HTTP_METHOD"
							+ httpMethod);
			throw new IDDataValidationException(
					IdAuthenticationErrorConstants.INVALID_HTTP_METHOD);
		}
	}

	/**
	 * Check uri.
	 *
	 * @param request
	 *            the request
	 * @param uri
	 *            the uri
	 * @throws IDDataValidationException
	 *             the ID data validation exception
	 */
	private void checkUri(RestRequestDTO request, String uri)
			throws IDDataValidationException {
		if (checkIfEmptyOrWhiteSpace(uri)) {
			request.setUri(uri);
		} else {
			mosipLogger.error(DEFAULT_SESSION_ID, METHOD_BUILD_REQUEST, "uri",
					"throwing IDDataValidationException - uri is empty or whitespace"
							+ uri);
			throw new IDDataValidationException(
					IdAuthenticationErrorConstants.INVALID_URI);
		}
	}

	/**
	 * Check if empty or white space.
	 *
	 * @param string
	 *            the string
	 * @return true, if successful
	 */
	private boolean checkIfEmptyOrWhiteSpace(String string) {
		boolean result = false;

		if (string != null && !string.isEmpty()) {
			result = true;
		}
		return result;
	}
}
