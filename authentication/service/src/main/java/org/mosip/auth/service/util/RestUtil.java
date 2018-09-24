package org.mosip.auth.service.util;

import java.net.URI;
import java.time.Duration;
import java.util.function.Supplier;

import javax.validation.Valid;

import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;
import org.mosip.auth.core.exception.RestServiceException;
import org.mosip.auth.core.util.dto.RestRequestDTO;
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
				response = RestUtil.request(request).timeout(Duration.ofSeconds(request.getTimeout()))
						.onErrorMap(
								error -> new RestServiceException(IdAuthenticationErrorConstants.CONNECTION_TIMED_OUT))
						.block();
				return (T) response;
			} catch (RuntimeException e) {
				if (e.getCause().getClass().equals(RestServiceException.class)) {
					throw (RestServiceException) e.getCause();
				} else {
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
				System.err.println(build);
				return build;
			});
		} else if (request.getParams() == null && request.getPathVariables() != null) {
			uri = method.uri(builder -> {
				URI build = builder.build(request.getPathVariables());
				System.err.println(build);
				return build;
			});
		} else {
			uri = method.uri(builder -> {
				URI build = builder.build();
				System.err.println(build);
				return build;
			});
		}

		if (request.getRequestBody() != null) {
			exchange = uri.syncBody(request.getRequestBody()).retrieve();
		} else {
			exchange = uri.retrieve();
		}

		monoResponse = exchange
				.onStatus(HttpStatus::isError, RestUtil::handleStatusError).bodyToMono(request.getResponseType());

		return monoResponse;
	}

	private static Mono<? extends Throwable> handleStatusError(ClientResponse response) {
		Mono<?> body = response.body(BodyExtractors.toMono(String.class));
		if (response.statusCode().is4xxClientError()) {
			return body
					.flatMap(str -> Mono.error(new RestServiceException(IdAuthenticationErrorConstants.CLIENT_ERROR)));
		} else {
			return body
					.flatMap(str -> Mono.error(new RestServiceException(IdAuthenticationErrorConstants.SERVER_ERROR)));
		}

	}
}