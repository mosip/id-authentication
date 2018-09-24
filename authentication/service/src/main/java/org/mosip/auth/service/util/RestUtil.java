package org.mosip.auth.service.util;

import java.net.URI;
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
import org.springframework.beans.factory.annotation.Autowired;
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
	// FIXME Update log details
	private static MosipLogger logger;

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
	 * Instantiates a new rest util.
	 */
	private RestUtil() {

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
	 */
	@SuppressWarnings("unchecked")
	public static <T> T requestSync(@Valid RestRequestDTO request) throws RestServiceException {
		Object response;
		if (request.getTimeout() != null) {
			try {
				response = RestUtil.request(request).timeout(Duration.ofSeconds(request.getTimeout())).onErrorMap(
						TimeoutException.class,
						error -> new RestServiceException(IdAuthenticationErrorConstants.CONNECTION_TIMED_OUT, error))
						.block();
				return (T) response;
			} catch (RuntimeException e) {
				logger.error("sessionId", "RestUtil", "requestSync", "RunTimeException - " + e);
				if (e.getCause().getClass().equals(RestServiceException.class)) {
					logger.error("sessionId", "RestUtil", "requestSync",
							"RunTimeException - RestServiceException - CONNECTION_TIMED_OUT - " + e);
					throw (RestServiceException) e.getCause();
				} else {
					logger.error("sessionId", "RestUtil", "requestSync",
							"RunTimeException - RestServiceException - UNKNOWN_ERROR - " + e);
					throw new RestServiceException(IdAuthenticationErrorConstants.UNKNOWN_ERROR);
				}
			}
		} else {
			response = RestUtil.request(request).block();
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
		Mono<?> sendRequest = RestUtil.request(request);
		sendRequest.subscribe();
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
		WebClient webClient = WebClient.builder().build();
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
			uri = method.uri(builder -> {
				URI build = builder.queryParams(request.getParams()).build();
				logger.info("sessionId", "RestUtil", "buildRequest", "URI for rest call is : " + build);
				return build;
			});
		} else if (request.getParams() == null && request.getPathVariables() != null) {
			uri = method.uri(builder -> {
				URI build = builder.build(request.getPathVariables());
				logger.info("sessionId", "RestUtil", "buildRequest", "URI for rest call is : " + build);
				return build;
			});
		} else {
			uri = method.uri(builder -> {
				URI build = builder.build();
				logger.info("sessionId", "RestUtil", "buildRequest", "URI for rest call is : " + build);
				return build;
			});
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

	private static Mono<? extends Throwable> handleStatusError(ClientResponse response) {
		Mono<?> body = response.body(BodyExtractors.toMono(String.class));
		logger.error("sessionId", "RestUtil", "status error",
				"isError - " + response.statusCode() + "   " + response.statusCode().getReasonPhrase());
		if (response.statusCode().is4xxClientError()) {
			logger.error("sessionId", "RestUtil", "status error",
					"is4xxClientError - " + response.statusCode() + "   " + response.statusCode().getReasonPhrase());
			return body
					.flatMap(str -> Mono.error(new RestServiceException(IdAuthenticationErrorConstants.CLIENT_ERROR)));
		} else {
			logger.error("sessionId", "RestUtil", "status error",
					"is5xxServerError - " + response.statusCode() + "   " + response.statusCode().getReasonPhrase());
			return body
					.flatMap(str -> Mono.error(new RestServiceException(IdAuthenticationErrorConstants.SERVER_ERROR)));
		}

	}
}