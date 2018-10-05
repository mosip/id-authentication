package io.mosip.authentication.service.factory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;

/**
 * A factory for creating and building RestRequest objects from
 * rest-services.properties
 * 
 * @author Manoj SP
 *
 */
@Component
public class RestRequestFactory {

	private static final String DEFAULT_SESSION_ID = "sessionId";

	private static final String METHOD_BUILD_REQUEST = "buildRequest";

	@Autowired
	private Environment env;

	private MosipLogger logger;

	/**
	 * Initialize logger.
	 *
	 * @param idaRollingFileAppender
	 *            the ida rolling file appender
	 */
	@Autowired
	private void initializeLogger(MosipRollingFileAppender idaRollingFileAppender) {
		logger = MosipLogfactory.getMosipDefaultRollingFileLogger(idaRollingFileAppender, this.getClass());
	}

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
	public RestRequestDTO buildRequest(RestServicesConstants restService, @Nullable Object requestBody,
			Class<?> returnType) throws IDDataValidationException {
		RestRequestDTO request = new RestRequestDTO();
		MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
		Map<String, String> pathVariables = new HashMap<>();
		HttpHeaders headers = new HttpHeaders();

		String serviceName = restService.getServiceName();

		String uri = env.getProperty(serviceName.concat(".rest.uri"));
		String httpMethod = env.getProperty(serviceName.concat(".rest.httpMethod"));
		String timeout = env.getProperty(serviceName.concat(".rest.timeout"));

		checkUri(request, uri);

		checkHttpMethod(request, httpMethod);

		if (requestBody != null) {
			request.setRequestBody(requestBody);
		}

		checkReturnType(returnType, request);

		constructParams(paramMap, pathVariables, headers, serviceName);

		Consumer<HttpHeaders> consumerHeader = header -> {
		};
		consumerHeader.accept(headers);
		request.setHeaders(consumerHeader);

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

	private void constructParams(MultiValueMap<String, String> paramMap, Map<String, String> pathVariables,
			HttpHeaders headers, String serviceName) {
		((AbstractEnvironment) env).getPropertySources().forEach(source -> {
			if (source instanceof MapPropertySource) {
				Map<String, Object> systemProperties = ((MapPropertySource) source).getSource();

				systemProperties.keySet().forEach(property -> {
					if (property.startsWith(serviceName.concat(".rest.headers"))) {
						headers.add(property.replace(serviceName.concat(".rest.headers."), ""),
								env.getProperty(property));
					} else if (property.startsWith(serviceName.concat(".rest.uri.queryparam."))) {
						paramMap.put(property.replace(serviceName.concat(".rest.uri.queryparam."), ""),
								Collections.singletonList(env.getProperty(property)));
					} else if (property.startsWith(serviceName.concat(".rest.uri.pathparam."))) {
						pathVariables.put(property.replace(serviceName.concat(".rest.uri.pathparam."), ""),
								env.getProperty(property));
					}
				});
			}
		});
	}

	private void checkReturnType(Class<?> returnType, RestRequestDTO request) throws IDDataValidationException {
		if (returnType != null) {
			request.setResponseType(returnType);
		} else {
			// FIXME Update logger details
			logger.error(DEFAULT_SESSION_ID, METHOD_BUILD_REQUEST, "returnType",
					"throwing IDDataValidationException - INVALID_RETURN_TYPE" + returnType);
			throw new IDDataValidationException(IdAuthenticationErrorConstants.INVALID_RETURN_TYPE);
		}
	}

	private void checkHttpMethod(RestRequestDTO request, String httpMethod) throws IDDataValidationException {
		if (checkIfEmptyOrWhiteSpace(httpMethod)) {
			request.setHttpMethod(HttpMethod.valueOf(httpMethod));
		} else {
			// FIXME Update logger details
			logger.error(DEFAULT_SESSION_ID, METHOD_BUILD_REQUEST, "httpMethod",
					"throwing IDDataValidationException - INVALID_HTTP_METHOD" + httpMethod);
			throw new IDDataValidationException(IdAuthenticationErrorConstants.INVALID_HTTP_METHOD);
		}
	}

	private void checkUri(RestRequestDTO request, String uri) throws IDDataValidationException {
		if (checkIfEmptyOrWhiteSpace(uri)) {
			request.setUri(uri);
		} else {
			// FIXME Update logger details
			logger.error(DEFAULT_SESSION_ID, METHOD_BUILD_REQUEST, "uri",
					"throwing IDDataValidationException - uri is empty or whitespace" + uri);
			throw new IDDataValidationException(IdAuthenticationErrorConstants.INVALID_URI);
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

		if (string != null) {
			if (!string.isEmpty()) {
				result = true;
			}
		} else {
			result = false;
		}

		return result;
	}

}
