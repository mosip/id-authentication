package io.mosip.authentication.common.service.factory;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.CLASS_REST_HELPER;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.idrepository.core.helper.RestHelper;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import lombok.NoArgsConstructor;

/**
 * A factory for creating and building RestRequest objects from
 * rest-services.properties
 *
 * @author Manoj SP
 */
@Component
@NoArgsConstructor
public class RestRequestFactory {
	
	private static final String GENERATE_AUTH_TOKEN = "generateAuthToken";


    private static final String REST_HEADERS_MEDIA_TYPE = ".rest.headers.mediaType";


    /** The Constant METHOD_BUILD_REQUEST. */
    private static final String METHOD_BUILD_REQUEST = "buildRequest";

    /** The env. */
    @Autowired
    private Environment env;
    
    @Autowired
	private ObjectMapper mapper;

    /** The logger. */
    private static Logger mosipLogger = IdaLogger.getLogger(RestRequestFactory.class);

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
    public RestRequestDTO buildRequest(RestServicesConstants restService, Object requestBody, Class<?> returnType)
	    throws IDDataValidationException {
	RestRequestDTO request = new RestRequestDTO();
	MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
	Map<String, String> pathVariables = new HashMap<>();

	String serviceName = restService.getServiceName();

	String uri = env.getProperty(serviceName.concat(IdAuthConfigKeyConstants.REST_URI));
	String httpMethod = env.getProperty(serviceName.concat(IdAuthConfigKeyConstants.REST_HTTP_METHOD));
	String timeout = env.getProperty(serviceName.concat(IdAuthConfigKeyConstants.REST_TIMEOUT));

	HttpHeaders headers = constructHttpHeaders(serviceName);

	checkUri(request, uri);

	checkHttpMethod(request, httpMethod);

	if (requestBody != null) {
	    if (!headers.getContentType().includes(MediaType.MULTIPART_FORM_DATA)) {
		request.setRequestBody(requestBody);
	    } else {
		if (requestBody instanceof MultiValueMap) {
		    request.setRequestBody(requestBody);
		} else {
		    throw new IDDataValidationException(
			    IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
			    String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
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

    private HttpHeaders constructHttpHeaders(String serviceName) throws IDDataValidationException {
	try {
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.valueOf(env.getProperty(serviceName.concat(IdAuthConfigKeyConstants.REST_HEADERS_MEDIA_TYPE))));
	    Optional<String> authToken = RestHelper.getAuthToken();
		if(authToken.isEmpty()) {
			Optional<String> newAuthToken = generateAuthToken();
			newAuthToken.ifPresent(token -> headers.add("Cookie", token));
		}
	    return headers;
	} catch (InvalidMediaTypeException e) {
	    mosipLogger.error(IdAuthCommonConstants.SESSION_ID, METHOD_BUILD_REQUEST, "returnType",
		    "throwing IDDataValidationException - INVALID_INPUT_PARAMETER"
			    + env.getProperty(serviceName.concat(IdAuthConfigKeyConstants.REST_HEADERS_MEDIA_TYPE)));
	    throw new IDDataValidationException(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
		    String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
			    serviceName.concat(REST_HEADERS_MEDIA_TYPE)));
	}
    }
    
    private Optional<String> generateAuthToken() {
		ObjectNode requestBody = mapper.createObjectNode();
		requestBody.put("clientId", env.getProperty("auth-token-generator.rest.clientId"));
		requestBody.put("secretKey", env.getProperty("auth-token-generator.rest.secretKey"));
		requestBody.put("appId", env.getProperty("auth-token-generator.rest.appId"));
		RequestWrapper<ObjectNode> request = new RequestWrapper<>();
		request.setRequesttime(DateUtils.getUTCCurrentDateTime());
		request.setRequest(requestBody);
		ClientResponse response = WebClient.create(env.getProperty("auth-token-generator.rest.uri")).post()
				.syncBody(request).exchange().block();
		if (response.statusCode() == HttpStatus.OK) {
			ObjectNode responseBody = response.bodyToMono(ObjectNode.class).block();
			if (responseBody != null && responseBody.get("response").get("status").asText().equalsIgnoreCase("success")) {
				ResponseCookie responseCookie = response.cookies().get("Authorization").get(0);
				mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, GENERATE_AUTH_TOKEN,
						"Auth token generated successfully and set");
				return Optional.of(responseCookie.getValue());
			} else {
				mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, GENERATE_AUTH_TOKEN,
						"Auth token generation failed: " + response);
			}
		} else {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, GENERATE_AUTH_TOKEN,
					"AuthResponse : status-" + response.statusCode() + " :\n"
							+ response.toEntity(String.class).block().getBody());
		}
		return Optional.empty();
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
    private void constructParams(MultiValueMap<String, String> paramMap, Map<String, String> pathVariables,
	    HttpHeaders headers, String serviceName) {
	((AbstractEnvironment) env).getPropertySources().forEach((PropertySource<?> source) -> {
	    if (source instanceof MapPropertySource) {
		Map<String, Object> systemProperties = ((MapPropertySource) source).getSource();

		systemProperties.keySet().forEach((String property) -> {
		    if (property.startsWith(serviceName.concat(".rest.headers"))) {
			headers.add(property.replace(serviceName.concat(".rest.headers."), ""),
				env.getProperty(property));
		    }
		    if (property.startsWith(serviceName.concat(".rest.uri.queryparam."))) {
			paramMap.put(property.replace(serviceName.concat(".rest.uri.queryparam."), ""),
				Collections.singletonList(env.getProperty(property)));
		    }
		    if (property.startsWith(serviceName.concat(".rest.uri.pathparam."))) {
			pathVariables.put(property.replace(serviceName.concat(".rest.uri.pathparam."), ""),
				env.getProperty(property));
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
    private void checkReturnType(Class<?> returnType, RestRequestDTO request) throws IDDataValidationException {
	if (returnType != null) {
	    request.setResponseType(returnType);
	} else {

	    mosipLogger.error(IdAuthCommonConstants.SESSION_ID, METHOD_BUILD_REQUEST, "returnType",
		    "throwing IDDataValidationException - INVALID_RETURN_TYPE");
	    throw new IDDataValidationException(IdAuthenticationErrorConstants.INVALID_RETURN_TYPE);
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
    private void checkHttpMethod(RestRequestDTO request, String httpMethod) throws IDDataValidationException {
	if (checkIfEmptyOrWhiteSpace(httpMethod)) {
	    request.setHttpMethod(HttpMethod.valueOf(httpMethod));
	} else {

	    mosipLogger.error(IdAuthCommonConstants.SESSION_ID, METHOD_BUILD_REQUEST, "httpMethod",
		    "throwing IDDataValidationException - INVALID_HTTP_METHOD" + httpMethod);
	    throw new IDDataValidationException(IdAuthenticationErrorConstants.INVALID_HTTP_METHOD);
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
    private void checkUri(RestRequestDTO request, String uri) throws IDDataValidationException {
	if (checkIfEmptyOrWhiteSpace(uri)) {
	    request.setUri(uri);
	} else {
	    mosipLogger.error(IdAuthCommonConstants.SESSION_ID, METHOD_BUILD_REQUEST, "uri",
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

	if (string != null && !string.isEmpty()) {
	    result = true;
	}
	return result;
    }
    
    public static <T> RequestWrapper<T> createRequest(T t){
    	RequestWrapper<T> request = new RequestWrapper<>();
    	request.setRequest(t);
    	request.setId("ida");
    	request.setRequesttime(DateUtils.getUTCCurrentDateTime());
    	return request;
    }
}
