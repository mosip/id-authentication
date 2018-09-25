package org.mosip.auth.service.util;

import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import javax.validation.Valid;

import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;
import org.mosip.auth.core.exception.RestServiceException;
import org.mosip.auth.core.util.dto.RestRequestDTO;
import org.mosip.kernel.core.logging.MosipLogger;
import org.mosip.kernel.core.logging.appenders.MosipRollingFileAppender;
import org.mosip.kernel.core.logging.factory.MosipLogfactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import reactor.core.publisher.Mono;

/**
 * The Class RestUtil - to send/receive HTTP requests and return the response.
 *
 * @author Manoj SP
 */
public final class RestUtil {
	// TODO Check for response body
	private static MosipLogger logger;

	/**
	 * Instantiates a new rest util.
	 */
	public RestUtil(MosipRollingFileAppender idaRollingFileAppender) {
		RestUtil.logger = MosipLogfactory.getMosipDefaultRollingFileLogger(idaRollingFileAppender, RestUtil.class);
	}

	/**
	 * Request sync.
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
	public static <T> T requestSync(@Valid RestRequestDTO request) throws RestServiceException {
		Object response;
		if (request.getTimeout() != null) {
			try {
				logger.info("sessionId", "RestUtil", "requestSync", "Request : " + request);
				response = RestUtil.request(request).timeout(Duration.ofSeconds(request.getTimeout())).block();
				logger.info("sessionId", "RestUtil", "requestSync", "Response : " + response);
				return (T) response;
			} catch (RuntimeException e) {
				if (e.getCause().getClass().equals(TimeoutException.class)) {
					logger.error("sessionId", "RestUtil", "requestSync", "Throwing RestServiceException - CONNECTION_TIMED_OUT - " + e.getCause());
					throw new RestServiceException(IdAuthenticationErrorConstants.CONNECTION_TIMED_OUT, e);
				} else {
					logger.error("sessionId", "RestUtil", "requestSync-RuntimeException", "Throwing RestServiceException - UNKNOWN_ERROR - " + e);
					throw new RestServiceException(IdAuthenticationErrorConstants.UNKNOWN_ERROR, e);
				}
			}
		} else {
			logger.info("sessionId", "RestUtil", "requestSync", "Request : " + request);
			response = RestUtil.request(request).block();
			logger.info("sessionId", "RestUtil", "requestSync", "Response : " + response);
			return (T) response;
		}
	}

	/**
	 * Request async.
	 *
	 * @param request
	 *            the request
	 * @return the supplier
	 */
	public static Supplier<?> requestAsync(@Valid RestRequestDTO request) {
		logger.info("sessionId", "RestUtil", "requestAsync", "Request : " + request);
		Mono<?> sendRequest = RestUtil.request(request);
		sendRequest.subscribe();
		logger.info("sessionId", "RestUtil", "requestAsync", "Request subscribed");
		return () -> sendRequest.block();
	}

	/**
	 * Request.
	 *
	 * @param request
	 *            the request
	 * @return the mono
	 */
	private static Mono<?> request(RestRequestDTO request) {
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

		monoResponse = exchange.onStatus(HttpStatus::isError, RestUtil::handleStatusError)
				.bodyToMono(request.getResponseType());

		return monoResponse;
	}

	/**
	 * Handle status error.
	 *
	 * @param response
	 *            the response
	 * @return the mono<? extends throwable>
	 */
	private static Mono<? extends Throwable> handleStatusError(ClientResponse response) {
		Mono<?> body = response.body(BodyExtractors.toMono(String.class));
		logger.error("sessionId", "RestUtil", "handleStatusError", "Status error : " + response.statusCode() + " " + response.statusCode().getReasonPhrase());
		if (response.statusCode().is4xxClientError()) {
			logger.error("sessionId", "RestUtil", "handleStatusError", "Status error - returning RestServiceException - CLIENT_ERROR");
			return body.flatMap(
					responseBody -> Mono.error(new RestServiceException(IdAuthenticationErrorConstants.CLIENT_ERROR)));
		} else {
			logger.error("sessionId", "RestUtil", "handleStatusError", "Status error - returning RestServiceException - SERVER_ERROR");
			return body.flatMap(
					responseBody -> Mono.error(new RestServiceException(IdAuthenticationErrorConstants.SERVER_ERROR)));
		}

	}
}