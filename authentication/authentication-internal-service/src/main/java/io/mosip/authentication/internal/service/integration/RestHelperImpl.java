package io.mosip.authentication.internal.service.integration;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * This class used to send/receive HTTP requests and return the response.
 * 
 * @author Sanjay Murali
 */
@Component
@NoArgsConstructor
public class RestHelperImpl implements RestHelper{

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
	private static Logger mosipLogger = IdaLogger.getLogger(RestHelper.class);
	
	@Autowired
	private WebClient webClient;

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
					.header(HttpHeaders.CONTENT_TYPE,
							request.getHeaders().getContentType().toString());
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
				throw new RestServiceException(IdAuthenticationErrorConstants.CLIENT_ERROR, responseNode.toString(),
						mapper.readValue(responseNode.toString().getBytes(), responseType));
			}
		} catch (IOException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, REQUEST_SYNC_RUNTIME_EXCEPTION,
					THROWING_REST_SERVICE_EXCEPTION + "- UNKNOWN_ERROR - " + e);
			throw new RestServiceException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	/**
	 * Handle 4XX/5XX status error.
	 *
	 * @param e            the response
	 * @param responseType the response type
	 * @return the mono<? extends throwable>
	 * @throws RestServiceException 
	 */
	private RestServiceException handleStatusError(WebClientResponseException e, Class<?> responseType) throws RestServiceException {
		try {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, METHOD_HANDLE_STATUS_ERROR,
					"Status error : " + e.getRawStatusCode() + " " + e.getStatusCode() + "  " + e.getStatusText());
			if (e.getStatusCode().is4xxClientError()) {
				if (e.getRawStatusCode() == 401 || e.getRawStatusCode() == 403) {
					mosipLogger.error(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER,
							"request failed with status code :" + e.getRawStatusCode(),
							"\n\n" + e.getResponseBodyAsString());
					List<ServiceError> errorList = ExceptionUtils.getServiceErrorList(e.getResponseBodyAsString());
					mosipLogger.error(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, "Throwing AuthenticationException",
							errorList.toString());
					throw new RestServiceException(errorList.get(0).getErrorCode(), errorList.get(0).getMessage(), e);
					
				} else {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, METHOD_HANDLE_STATUS_ERROR,
						"Status error - returning RestServiceException - CLIENT_ERROR -- "
								+ e.getResponseBodyAsString());
				return new RestServiceException(IdAuthenticationErrorConstants.CLIENT_ERROR, e.getResponseBodyAsString(),
						mapper.readValue(e.getResponseBodyAsString().getBytes(), responseType));
				}
			} else {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, METHOD_HANDLE_STATUS_ERROR,
						"Status error - returning RestServiceException - SERVER_ERROR -- "
								+ e.getResponseBodyAsString());
				return new RestServiceException(IdAuthenticationErrorConstants.SERVER_ERROR, e.getResponseBodyAsString(),
						mapper.readValue(e.getResponseBodyAsString().getBytes(), responseType));
			}
		} catch (IOException ex) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, METHOD_HANDLE_STATUS_ERROR, ex.getMessage());
			return new RestServiceException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, ex);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T requestSync(RestRequestDTO request)
			throws RestServiceException {
		Object response;
		try {
			requestTime = DateUtils.getUTCCurrentDateTime();
			mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
					"Request received at : " + requestTime);
			mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, METHOD_REQUEST_SYNC, PREFIX_REQUEST + request);
			if (request.getTimeout() != null) {
				response = request(request).timeout(Duration.ofSeconds(request.getTimeout())).block();
				mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
						PREFIX_RESPONSE + response);
				checkErrorResponse(response, request.getResponseType());
				return (T) response;
			} else {
				response = request(request).block();
				mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
						PREFIX_RESPONSE + response);
				checkErrorResponse(response, request.getResponseType());
				return (T) response;
			}
		} catch (WebClientResponseException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
					THROWING_REST_SERVICE_EXCEPTION + "- Http Status error - \n " + e.getMessage()
							+ " \n Response Body : \n" + ExceptionUtils.getStackTrace(e));
			throw handleStatusError(e, request.getResponseType());
		} catch (RuntimeException e) {
			if (e.getCause() != null && e.getCause().getClass().equals(TimeoutException.class)) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
						THROWING_REST_SERVICE_EXCEPTION + "- CONNECTION_TIMED_OUT - \n " + e.getMessage());
				throw new RestServiceException(IdAuthenticationErrorConstants.CONNECTION_TIMED_OUT, e);
			} else {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, REQUEST_SYNC_RUNTIME_EXCEPTION,
						THROWING_REST_SERVICE_EXCEPTION + "- UNKNOWN_ERROR - " + e.getMessage());
				throw new RestServiceException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
			}
		} finally {
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

	@Override
	public Supplier<Object> requestAsync(io.mosip.authentication.core.dto.RestRequestDTO request) {
		mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, METHOD_REQUEST_ASYNC, PREFIX_REQUEST + request);
		Mono<?> sendRequest = request(request);
		sendRequest.subscribe();
		mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, CLASS_REST_HELPER, METHOD_REQUEST_ASYNC, "Request subscribed");
		return sendRequest::block;
	}


}
