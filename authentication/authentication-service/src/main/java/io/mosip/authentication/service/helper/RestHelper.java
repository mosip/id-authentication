package io.mosip.authentication.service.helper;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.kernel.core.logger.spi.Logger;
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

	/** The Constant DEFAULT_SESSION_ID. */
	private static final String DEFAULT_SESSION_ID = "sessionId";

	/** The mosipLogger. */
	private static Logger mosipLogger = IdaLogger.getLogger(RestHelper.class);
	
	/**
	 * Request to send/receive HTTP requests and return the response synchronously.
	 *
	 * @param <T>
	 *            the generic type
	 * @param request
	 *            the request
	 * @return the response object or null in case of exception
	 * @throws RestServiceException
	 *             the rest service exception
	 */
	@SuppressWarnings("unchecked")
	public <T> T requestSync(@Valid RestRequestDTO request) throws RestServiceException {
		Object response;
		if (request.getTimeout() != null) {
			try {
				mosipLogger.info(DEFAULT_SESSION_ID, CLASS_REST_HELPER, METHOD_REQUEST_SYNC, PREFIX_REQUEST + request);
				response = request(request).timeout(Duration.ofSeconds(request.getTimeout())).block();
				mosipLogger.info(DEFAULT_SESSION_ID, CLASS_REST_HELPER, METHOD_REQUEST_SYNC, PREFIX_RESPONSE + response);
				return (T) response;
			} catch (RuntimeException e) {
				if (e.getCause().getClass().equals(TimeoutException.class)) {
					mosipLogger.error(DEFAULT_SESSION_ID, CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
							"Throwing RestServiceException - CONNECTION_TIMED_OUT - " + e.getCause());
					throw new RestServiceException(IdAuthenticationErrorConstants.CONNECTION_TIMED_OUT, e);
				} else {
					mosipLogger.error(DEFAULT_SESSION_ID, CLASS_REST_HELPER, "requestSync-RuntimeException",
							"Throwing RestServiceException - UNKNOWN_ERROR - " + e);
					throw new RestServiceException(IdAuthenticationErrorConstants.UNKNOWN_ERROR, e);
				}
			}
		} else {
			mosipLogger.info(DEFAULT_SESSION_ID, CLASS_REST_HELPER, METHOD_REQUEST_SYNC, PREFIX_REQUEST + request);
			response = request(request).block();
			mosipLogger.info(DEFAULT_SESSION_ID, CLASS_REST_HELPER, METHOD_REQUEST_SYNC, PREFIX_RESPONSE + response);
			return (T) response;
		}
	}

	/**
	 * Request to send/receive HTTP requests and return the response asynchronously.
	 *
	 * @param request
	 *            the request
	 * @return the supplier
	 */
	public Supplier<Object> requestAsync(@Valid RestRequestDTO request) {
		mosipLogger.info(DEFAULT_SESSION_ID, CLASS_REST_HELPER, METHOD_REQUEST_ASYNC, PREFIX_REQUEST + request);
		Mono<?> sendRequest = request(request);
		sendRequest.subscribe();
		mosipLogger.info(DEFAULT_SESSION_ID, CLASS_REST_HELPER, METHOD_REQUEST_ASYNC, "Request subscribed");
		return () -> sendRequest.block();
	}

	/**
	 * Method to send/receive HTTP requests and return the response as Mono.
	 *
	 * @param request
	 *            the request
	 * @return the mono
	 */
	private Mono<?> request(RestRequestDTO request) {
		WebClient webClient;
		Mono<?> monoResponse;
		RequestBodySpec uri;
		ResponseSpec exchange;
		RequestBodyUriSpec method;

		if (request.getHeaders() != null) {
			webClient = WebClient.builder().baseUrl(request.getUri()).defaultHeaders(request.getHeaders()).build();
		} else {
			webClient = WebClient.builder().baseUrl(request.getUri()).build();
		}

		method = webClient.method(request.getHttpMethod());
		if (request.getParams() != null && request.getPathVariables() == null) {
			uri = method.uri(builder -> builder.queryParams(request.getParams()).build());
		} else if (request.getParams() == null && request.getPathVariables() != null) {
			uri = method.uri(builder -> builder.build(request.getPathVariables()));
		} else {
			uri = method.uri(builder -> builder.build());
		}

		if (request.getRequestBody() != null) {
			exchange = uri.syncBody(request.getRequestBody()).retrieve();
		} else {
			exchange = uri.retrieve();
		}

		monoResponse = exchange.onStatus(HttpStatus::isError, this::handleStatusError)
				.bodyToMono(request.getResponseType());

		return monoResponse;
	}

	/**
	 * Handle 4XX/5XX status error.
	 *
	 * @param response
	 *            the response
	 * @return the mono<? extends throwable>
	 */
	private Mono<Throwable> handleStatusError(ClientResponse response) {
		Mono<Object> body = response.body(BodyExtractors.toMono(Object.class));
		mosipLogger.error(DEFAULT_SESSION_ID, CLASS_REST_HELPER, METHOD_HANDLE_STATUS_ERROR,
				"Status error : " + response.statusCode() + " " + response.statusCode().getReasonPhrase());
		if (response.statusCode().is4xxClientError()) {
			mosipLogger.error(DEFAULT_SESSION_ID, CLASS_REST_HELPER, METHOD_HANDLE_STATUS_ERROR,
					"Status error - returning RestServiceException - CLIENT_ERROR");
			return body.flatMap(responseBody -> Mono.error(
					new RestServiceException(IdAuthenticationErrorConstants.CLIENT_ERROR, Optional.of(responseBody))));
		} else {
			mosipLogger.error(DEFAULT_SESSION_ID, CLASS_REST_HELPER, METHOD_HANDLE_STATUS_ERROR,
					"Status error - returning RestServiceException - SERVER_ERROR");
			return body.flatMap(responseBody -> Mono.error(
					new RestServiceException(IdAuthenticationErrorConstants.SERVER_ERROR, Optional.of(responseBody))));
		}

	}
}