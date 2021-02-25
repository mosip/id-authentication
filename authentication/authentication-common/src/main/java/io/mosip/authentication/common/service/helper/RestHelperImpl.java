package io.mosip.authentication.common.service.helper;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.CLASS_REST_HELPER;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.METHOD_HANDLE_STATUS_ERROR;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.METHOD_REQUEST_ASYNC;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.METHOD_REQUEST_SYNC;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.PREFIX_RESPONSE;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.REQUEST_SYNC_RUNTIME_EXCEPTION;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.THROWING_REST_SERVICE_EXCEPTION;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import javax.net.ssl.SSLException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.EmptyCheckUtils;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.core.util.TokenHandlerUtil;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * The Class RestHelper - to send/receive HTTP requests and return the response.
 *
 * @author Manoj SP
 */
@Component
@Qualifier("external")
@NoArgsConstructor
public class RestHelperImpl implements RestHelper {

	private static final String KER_ATH_TOKEN_EXPIRY_ERROR_CODE = "KER-ATH-401";

	private static final String GENERATE_AUTH_TOKEN = "generateAuthToken";
	
	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	private LocalDateTime requestTime;

	/** The mosipLogger. */
	private static Logger mosipLogger = IdaLogger.getLogger(RestHelper.class);

	private String authToken;

	private int retry;

	@Autowired
	private Environment env;

	/**
	 * Request to send/receive HTTP requests and return the response synchronously.
	 *
	 * @param         <T> the generic type
	 * @param request the request
	 * @return the response object or null in case of exception
	 * @throws RestServiceException the rest service exception
	 */
	@SuppressWarnings("unchecked")
	public <T> T requestSync(@Valid RestRequestDTO request) throws RestServiceException {
		Object response = null;
		try {
			requestTime = DateUtils.getUTCCurrentDateTime();
			mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
					"Request received at : " + requestTime);
			if (retry <= 1) {
				if (request.getTimeout() != null) {
					response = request(request, getSslContext()).timeout(Duration.ofSeconds(request.getTimeout()))
							.block();
				} else {
					response = request(request, getSslContext()).block();
				}
			}
			checkErrorResponse(response, request.getResponseType());
			if(response != null && containsError(response.toString())) {
				mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
						PREFIX_RESPONSE + response);
			}
			return (T) response;

		} catch (WebClientResponseException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
					THROWING_REST_SERVICE_EXCEPTION + "- Http Status error - \n " + ExceptionUtils.getStackTrace(e)
							+ " \n Response Body : \n" + e.getResponseBodyAsString());
			Object statusError = handleStatusError(e, request.getResponseType());

			if (statusError instanceof RestServiceException) {
				throw ((RestServiceException) statusError);
			} else {
				return requestSync(request);
			}

		} catch (RuntimeException e) {
			if (e.getCause() != null && e.getCause().getClass().equals(TimeoutException.class)) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
						THROWING_REST_SERVICE_EXCEPTION + "- CONNECTION_TIMED_OUT - \n "
								+ ExceptionUtils.getStackTrace(e));
				throw new RestServiceException(IdAuthenticationErrorConstants.CONNECTION_TIMED_OUT, e);
			} else {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, REQUEST_SYNC_RUNTIME_EXCEPTION,
						THROWING_REST_SERVICE_EXCEPTION + "- UNKNOWN_ERROR - " + e);
				throw new RestServiceException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
			}
		} finally {
			retry = 0;
			LocalDateTime responseTime = DateUtils.getUTCCurrentDateTime();
			mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
					"Response sent at : " + responseTime);
			long duration = Duration.between(requestTime, responseTime).toMillis();
			mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
					"Time difference between request and response in millis:" + duration
							+ ".  Time difference between request and response in Seconds: "
							+ ((double) duration / 1000));
		}
	}
	
	private boolean containsError(String response) {
		return RestHelper.super.containsError(response, mapper);
	}

	/**
	 * Request to send/receive HTTP requests and return the response asynchronously.
	 *
	 * @param request the request
	 * @return the supplier
	 */
	public Supplier<Object> requestAsync(@Valid RestRequestDTO request) {
		try {
			Mono<?> sendRequest = request(request, getSslContext());
			sendRequest.subscribe();
			mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, METHOD_REQUEST_ASYNC,
					"Request subscribed");
			return () -> sendRequest.block();
		} catch (RestServiceException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, REQUEST_SYNC_RUNTIME_EXCEPTION,
					"Throwing RestServiceException - UNKNOWN_ERROR - " + e);
			return () -> new RestServiceException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	/**
	 * Gets the ssl context.
	 *
	 * @return the ssl context
	 * @throws RestServiceException the rest service exception
	 */
	private SslContext getSslContext() throws RestServiceException {
		try {
			return SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
		} catch (SSLException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, REQUEST_SYNC_RUNTIME_EXCEPTION,
					"Throwing RestServiceException - UNKNOWN_ERROR - " + e);
			throw new RestServiceException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	/**
	 * Method to send/receive HTTP requests and return the response as Mono.
	 *
	 * @param request    the request
	 * @param sslContext the ssl context
	 * @return the mono
	 */
	private Mono<?> request(RestRequestDTO request, SslContext sslContext) {
		WebClient webClient;
		Mono<?> monoResponse;
		RequestBodySpec uri;
		ResponseSpec exchange;
		RequestBodyUriSpec method;

		if (request.getHeaders() != null) {
			webClient = WebClient.builder()
					.clientConnector(new ReactorClientHttpConnector(builder -> builder.sslContext(sslContext)))
					.baseUrl(request.getUri())
					.defaultHeader(HttpHeaders.CONTENT_TYPE, request.getHeaders().getContentType().toString()).build();
		} else {
			webClient = WebClient.builder()
					.clientConnector(new ReactorClientHttpConnector(builder -> builder.sslContext(sslContext)))
					.baseUrl(request.getUri()).build();
		}

		method = webClient.method(request.getHttpMethod());
		if (request.getParams() != null && request.getPathVariables() == null) {
			uri = method.uri(builder -> builder.queryParams(request.getParams()).build());
		} else if (request.getParams() == null && request.getPathVariables() != null) {
			uri = method.uri(builder -> builder.build(request.getPathVariables()));
		} else {
			uri = method.uri(builder -> builder.build());
		}

		uri.cookie("Authorization", getAuthToken());

		if (request.getRequestBody() != null) {
			exchange = uri.syncBody(request.getRequestBody()).retrieve();
		} else {
			exchange = uri.retrieve();
		}

		monoResponse = exchange.bodyToMono(request.getResponseType());

		return monoResponse;
	}

	private String getAuthToken() {
		if (EmptyCheckUtils.isNullEmpty(authToken) || !TokenHandlerUtil.isValidBearerToken(authToken,
				env.getProperty("auth-token-generator.rest.issuerUrl"),
				env.getProperty("auth-token-generator.rest.clientId"))) {
			generateAuthToken();
			return authToken;
		} else {
			return authToken;
		}
	}

	private void generateAuthToken() {
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
				authToken = responseCookie.getValue();
				mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, GENERATE_AUTH_TOKEN,
						"Auth token generated successfully and set");
			} else {
				mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, GENERATE_AUTH_TOKEN,
						"Auth token generation failed: " + response);
			}
		} else {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, GENERATE_AUTH_TOKEN,
					"AuthResponse : status-" + response.statusCode() + " :\n"
							+ response.toEntity(String.class).block().getBody());
		}
	}

	/**
	 * Check error response.
	 *
	 * @param response     the response
	 * @param responseType the response type
	 * @throws RestServiceException the rest service exception
	 */
	private void checkErrorResponse(Object response, Class<?> responseType) throws RestServiceException {
		try {
			String responseBodyAsString = mapper.writeValueAsString(response);
			List<ServiceError> errorList = getErrorList(responseBodyAsString, mapper);
			if (Objects.nonNull(errorList)
					&& !errorList.isEmpty()
					&& Objects.nonNull(errorList.get(0).getErrorCode())
					&& !errorList.get(0).getErrorCode().startsWith(KER_ATH_TOKEN_EXPIRY_ERROR_CODE)) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, "checkErrorResponse",
						THROWING_REST_SERVICE_EXCEPTION + "- CLIENT_ERROR");
				mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, "checkErrorResponse",
						THROWING_REST_SERVICE_EXCEPTION + "- CLIENT_ERROR\n" + responseBodyAsString);
				throw new RestServiceException(IdAuthenticationErrorConstants.CLIENT_ERROR, responseBodyAsString,
						mapper.readValue(responseBodyAsString.getBytes(), responseType));
			} else if (Objects.nonNull(errorList)
					&& !errorList.isEmpty()
					&& Objects.nonNull(errorList.get(0).getErrorCode())
					&& errorList.get(0).getErrorCode().contentEquals(KER_ATH_TOKEN_EXPIRY_ERROR_CODE)) {
				retry++;
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, "checkErrorResponse",
						"errorCode -> KER-ATH-401" + " - retry++");
				mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, "checkErrorResponse",
						"errorCode -> KER-ATH-401" + " - retry++" + responseBodyAsString);
			}
		} catch (IOException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, "checkErrorResponse",
					THROWING_REST_SERVICE_EXCEPTION + "- UNKNOWN_ERROR - " + StringUtils.substring(e.getMessage(), 0, 500));
			throw new RestServiceException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	public static List<ServiceError> getErrorList(String responseBodyAsString, ObjectMapper mapper) {
		try {
			Map<String, Object> responseMap = mapper.readValue(responseBodyAsString.getBytes(), Map.class);
			Object errors = responseMap.get("errors");
			if(errors instanceof Map) {
				Map<String, Object> errorMap = (Map<String, Object>) errors;
				return List.of(new ServiceError((String)errorMap.get("errorCode"), (String)errorMap.get("message")));
			}
		} catch (IOException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, "checkErrorResponse",
					THROWING_REST_SERVICE_EXCEPTION + "- UNKNOWN_ERROR - " + StringUtils.substring(e.getMessage(), 0, 500));
			return Collections.emptyList();
		}
		
		return ExceptionUtils.getServiceErrorList(responseBodyAsString);
	}

	/**
	 * Handle 4XX/5XX status error.
	 *
	 * @param e            the response
	 * @param responseType the response type
	 * @return the mono<? extends throwable>
	 */
	private Object handleStatusError(WebClientResponseException e, Class<?> responseType) {
		try {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, METHOD_HANDLE_STATUS_ERROR,
					"Status error : " + e.getRawStatusCode() + " " + e.getStatusCode() + "  " + e.getStatusText());
			if (e.getStatusCode().is4xxClientError()) {
				if (e.getStatusCode().equals(HttpStatus.UNAUTHORIZED) && retry <= 1) {
					authToken = null;
					retry++;
					mosipLogger.error(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, "METHOD_HANDLE_STATUS_ERROR",
							"token expired" + " - retry++");
					return true;
				} else {
					mosipLogger.error(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, METHOD_HANDLE_STATUS_ERROR,
							"Status error - returning RestServiceException - CLIENT_ERROR");
					return new RestServiceException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS,
							e.getResponseBodyAsString(),
							mapper.readValue(e.getResponseBodyAsString().getBytes(), responseType));
				}
			} else {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, METHOD_HANDLE_STATUS_ERROR,
						"Status error - returning RestServiceException - SERVER_ERROR");
				return new RestServiceException(IdAuthenticationErrorConstants.SERVER_ERROR,
						e.getResponseBodyAsString(),
						mapper.readValue(e.getResponseBodyAsString().getBytes(), responseType));
			}
		} catch (IOException ex) {
			return new RestServiceException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, ex);
		}

	}
}