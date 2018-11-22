package io.mosip.authentication.service.helper;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.kernel.core.exception.ExceptionUtils;
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

	@Autowired
	ObjectMapper mapper;

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
	 * @param         <T> the generic type
	 * @param request the request
	 * @return the response object or null in case of exception
	 * @throws RestServiceException the rest service exception
	 */
	@SuppressWarnings("unchecked")
	public <T> T requestSync(@Valid RestRequestDTO request) throws RestServiceException {
		Object response;
		if (request.getTimeout() != null) {
			try {
				mosipLogger.info(DEFAULT_SESSION_ID, CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
						PREFIX_REQUEST + request + "\n" + request.getHeaders().getContentType());
				response = request(request).timeout(Duration.ofSeconds(request.getTimeout())).block();
				mosipLogger.info(DEFAULT_SESSION_ID, CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
						PREFIX_RESPONSE + response);
				return (T) response;
			} catch (WebClientResponseException e) {
				mosipLogger.error(DEFAULT_SESSION_ID, CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
						"Throwing RestServiceException - Http Status error - \n " + ExceptionUtils.getStackTrace(e)
								+ " \n Response Body : \n" + e.getResponseBodyAsString());
				throw handleStatusError(e, request.getResponseType());
			} catch (RuntimeException e) {
				if (e.getCause() != null && e.getCause().getClass().equals(TimeoutException.class)) {
					mosipLogger.error(DEFAULT_SESSION_ID, CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
							"Throwing RestServiceException - CONNECTION_TIMED_OUT - \n "
									+ ExceptionUtils.getStackTrace(e));
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
	 * @param request the request
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
	 * @param request the request
	 * @return the mono
	 * @throws RestServiceException
	 */
	@SuppressWarnings("unchecked")
	private Mono<?> request(RestRequestDTO request) {
		WebClient webClient;
		Mono<?> monoResponse;
		RequestBodySpec uri;
		ResponseSpec exchange;
		RequestBodyUriSpec method;

		if (request.getHeaders() != null) {
			webClient = WebClient.builder().baseUrl(request.getUri())
					.defaultHeader(HttpHeaders.CONTENT_TYPE, request.getHeaders().getContentType().toString()).build();
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
			if (request.getHeaders() != null
					&& request.getHeaders().getContentType().includes(MediaType.MULTIPART_FORM_DATA)) {
				exchange = uri
						.body(BodyInserters.fromFormData((MultiValueMap<String, String>) request.getRequestBody()))
						.retrieve();
			} else {
				exchange = uri.syncBody(request.getRequestBody()).retrieve();
			}
		} else {
			exchange = uri.retrieve();
		}

		monoResponse = exchange.bodyToMono(request.getResponseType());

		return monoResponse;
	}

	/**
	 * Handle 4XX/5XX status error.
	 *
	 * @param e            the response
	 * @param responseType
	 * @return the mono<? extends throwable>
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	private RestServiceException handleStatusError(WebClientResponseException e, Class<?> responseType) {
		try {
			mosipLogger.error(DEFAULT_SESSION_ID, CLASS_REST_HELPER, METHOD_HANDLE_STATUS_ERROR,
					"Status error : " + e.getRawStatusCode() + " " + e.getStatusCode() + "  " + e.getStatusText());
			if (e.getStatusCode().is4xxClientError()) {
				mosipLogger.error(DEFAULT_SESSION_ID, CLASS_REST_HELPER, METHOD_HANDLE_STATUS_ERROR,
						"Status error - returning RestServiceException - CLIENT_ERROR");
				return new RestServiceException(IdAuthenticationErrorConstants.CLIENT_ERROR,
						e.getResponseBodyAsString(),
						mapper.readValue(e.getResponseBodyAsString().getBytes(), responseType));
			} else {
				mosipLogger.error(DEFAULT_SESSION_ID, CLASS_REST_HELPER, METHOD_HANDLE_STATUS_ERROR,
						"Status error - returning RestServiceException - SERVER_ERROR");
				return new RestServiceException(IdAuthenticationErrorConstants.SERVER_ERROR,
						e.getResponseBodyAsString(),
						mapper.readValue(e.getResponseBodyAsString().getBytes(), responseType));
			}
		} catch (IOException ex) {
			return new RestServiceException(IdAuthenticationErrorConstants.UNKNOWN_ERROR, ex);
		}

	}
}