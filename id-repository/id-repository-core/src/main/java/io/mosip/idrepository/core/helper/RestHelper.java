package io.mosip.idrepository.core.helper;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.dto.RestRequestDTO;
import io.mosip.idrepository.core.exception.AuthenticationException;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.idrepository.core.logger.IdRepoLogger;
import io.mosip.idrepository.core.security.IdRepoSecurityManager;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * The Class RestHelper - to send/receive HTTP requests and return the response.
 *
 * @author Manoj SP
 */
@Component
@NoArgsConstructor
public class RestHelper {

	/** The Constant ERRORS. */
	private static final String ERRORS = "errors";

	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	/** The Constant METHOD_REQUEST_SYNC. */
	private static final String METHOD_REQUEST_SYNC = "requestSync";

	/** The Constant METHOD_HANDLE_STATUS_ERROR. */
	private static final String METHOD_HANDLE_STATUS_ERROR = "handleStatusError";

	/** The Constant PREFIX_RESPONSE. */
	private static final String PREFIX_RESPONSE = "Response : ";

	/** The Constant PREFIX_REQUEST. */
	private static final String PREFIX_REQUEST = "Request : ";

	/** The Constant METHOD_REQUEST_ASYNC. */
	private static final String METHOD_REQUEST_ASYNC = "requestAsync";

	/** The Constant CLASS_REST_HELPER. */
	private static final String CLASS_REST_HELPER = "RestHelper";

	/** The Constant THROWING_REST_SERVICE_EXCEPTION. */
	private static final String THROWING_REST_SERVICE_EXCEPTION = "Throwing RestServiceException";

	/** The Constant REQUEST_SYNC_RUNTIME_EXCEPTION. */
	private static final String REQUEST_SYNC_RUNTIME_EXCEPTION = "requestSync-RuntimeException";

	private LocalDateTime requestTime;

	/** The mosipLogger. */
	private static Logger mosipLogger = IdRepoLogger.getLogger(RestHelper.class);
	
	@Autowired
	private WebClient webClient;
	
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
		Object response;
		try {
			requestTime = DateUtils.getUTCCurrentDateTime();
			mosipLogger.debug(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
					"Request received at : " + requestTime);
			mosipLogger.debug(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_SYNC, PREFIX_REQUEST + request);
			if (request.getTimeout() != null) {
				response = request(request).timeout(Duration.ofSeconds(request.getTimeout())).block();
				mosipLogger.debug(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
						PREFIX_RESPONSE + response);
			} else {
				response = request(request).block();
				mosipLogger.debug(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
						PREFIX_RESPONSE + response);
			}
			checkErrorResponse(response, request.getResponseType());
			return (T) response;
		} catch (WebClientResponseException e) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
					THROWING_REST_SERVICE_EXCEPTION + "- Http Status error - \n " + e.getMessage()
							+ " \n Response Body : \n" + ExceptionUtils.getStackTrace(e));
			throw handleStatusError(e, request.getResponseType());
		} catch (RuntimeException e) {
			if (e.getCause() != null && e.getCause().getClass().equals(TimeoutException.class)) {
				mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
						THROWING_REST_SERVICE_EXCEPTION + "- CONNECTION_TIMED_OUT - \n " + e.getMessage());
				throw new RestServiceException(IdRepoErrorConstants.CONNECTION_TIMED_OUT, e);
			} else {
				mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, REQUEST_SYNC_RUNTIME_EXCEPTION,
						THROWING_REST_SERVICE_EXCEPTION + "- UNKNOWN_ERROR - " + e.getMessage());
				throw new RestServiceException(IdRepoErrorConstants.UNKNOWN_ERROR, e);
			}
		} finally {
			LocalDateTime responseTime = DateUtils.getUTCCurrentDateTime();
			mosipLogger.debug(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
					"Response sent at : " + responseTime);
			long duration = Duration.between(requestTime, responseTime).toMillis();
			mosipLogger.debug(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
					"Time difference between request and response in millis:" + duration
							+ ".  Time difference between request and response in Seconds: "
							+ ((double) duration / 1000));
		}

	}

	/**
	 * Request to send/receive HTTP requests and return the response asynchronously.
	 *
	 * @param request the request
	 * @return the supplier
	 */
	public Supplier<Object> requestAsync(@Valid RestRequestDTO request) {
		mosipLogger.debug(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_ASYNC, PREFIX_REQUEST + request);
		Mono<?> sendRequest = request(request);
		sendRequest.subscribe();
		mosipLogger.debug(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_ASYNC, "Request subscribed");
		return () -> sendRequest.block();
	}

	/**
	 * Method to send/receive HTTP requests and return the response as Mono.
	 *
	 * @param request    the request
	 * @param sslContext the ssl context
	 * @return the mono
	 */
	private Mono<?> request(RestRequestDTO request) {
		Mono<?> monoResponse;
		RequestBodySpec requestBodySpec;
		ResponseSpec exchange;
		
		if (request.getParams() != null && request.getPathVariables() == null) {
			request.setUri(UriComponentsBuilder
					.fromUriString(request.getUri())
					.queryParams(request.getParams())
					.toUriString());
		} else if (request.getParams() == null && request.getPathVariables() != null) {
			request.setUri(UriComponentsBuilder
					.fromUriString(request.getUri())
					.buildAndExpand(request.getPathVariables())
					.toUriString());
		} else if (request.getParams() != null && request.getPathVariables() != null) {
			request.setUri(UriComponentsBuilder
					.fromUriString(request.getUri())
					.queryParams(request.getParams())
					.buildAndExpand(request.getPathVariables())
					.toUriString());
		}
		
		requestBodySpec = webClient.method(request.getHttpMethod()).uri(request.getUri());

		if (request.getHeaders() != null) {
			requestBodySpec = requestBodySpec
					.headers(headers -> headers.addAll(request.getHeaders()));
		}

		if (request.getRequestBody() != null) {
			exchange = requestBodySpec.syncBody(request.getRequestBody()).retrieve();
		} else {
			exchange = requestBodySpec.retrieve();
		}

		monoResponse = exchange.bodyToMono(request.getResponseType());

		return monoResponse;
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
			ObjectNode responseNode = mapper.readValue(mapper.writeValueAsBytes(response), ObjectNode.class);
			if (responseNode.has(ERRORS) && !responseNode.get(ERRORS).isNull() && responseNode.get(ERRORS).isArray()
					&& responseNode.get(ERRORS).size() > 0) {
				throw new RestServiceException(IdRepoErrorConstants.CLIENT_ERROR, responseNode.toString(),
						mapper.readValue(responseNode.toString().getBytes(), responseType));
			}
		} catch (IOException e) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, REQUEST_SYNC_RUNTIME_EXCEPTION,
					THROWING_REST_SERVICE_EXCEPTION + "- UNKNOWN_ERROR - " + e.getMessage());
			throw new RestServiceException(IdRepoErrorConstants.UNKNOWN_ERROR, e);
		}
	}

	/**
	 * Handle 4XX/5XX status error.
	 *
	 * @param e            the response
	 * @param responseType the response type
	 * @return the mono<? extends throwable>
	 */
	private RestServiceException handleStatusError(WebClientResponseException e, Class<?> responseType) {
		try {
			mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_HANDLE_STATUS_ERROR,
					"Status error : " + e.getRawStatusCode() + " " + e.getStatusCode() + "  " + e.getStatusText());
			if (e.getStatusCode().is4xxClientError()) {
				if (e.getRawStatusCode() == 401 || e.getRawStatusCode() == 403) {
					mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER,
							"request failed with status code :" + e.getRawStatusCode(),
							"\n\n" + e.getResponseBodyAsString());
					List<ServiceError> errorList = ExceptionUtils.getServiceErrorList(e.getResponseBodyAsString());
					mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, "Throwing AuthenticationException",
							errorList.toString());
					throw new AuthenticationException(errorList.get(0).getErrorCode(), errorList.get(0).getMessage(),
							e.getRawStatusCode());
					
				} else {
				mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_HANDLE_STATUS_ERROR,
						"Status error - returning RestServiceException - CLIENT_ERROR -- "
								+ e.getResponseBodyAsString());
				return new RestServiceException(IdRepoErrorConstants.CLIENT_ERROR, e.getResponseBodyAsString(),
						mapper.readValue(e.getResponseBodyAsString().getBytes(), responseType));
				}
			} else {
				mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_HANDLE_STATUS_ERROR,
						"Status error - returning RestServiceException - SERVER_ERROR -- "
								+ e.getResponseBodyAsString());
				return new RestServiceException(IdRepoErrorConstants.SERVER_ERROR, e.getResponseBodyAsString(),
						mapper.readValue(e.getResponseBodyAsString().getBytes(), responseType));
			}
		} catch (IOException ex) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_HANDLE_STATUS_ERROR, ex.getMessage());
			return new RestServiceException(IdRepoErrorConstants.UNKNOWN_ERROR, ex);
		}

	}
}