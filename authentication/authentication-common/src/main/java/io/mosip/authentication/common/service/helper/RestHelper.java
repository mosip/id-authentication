package io.mosip.authentication.common.service.helper;

import static io.mosip.idrepository.core.constant.IdRepoErrorConstants.CLIENT_ERROR;
import static io.mosip.idrepository.core.constant.IdRepoErrorConstants.CONNECTION_TIMED_OUT;
import static io.mosip.idrepository.core.constant.IdRepoErrorConstants.SERVER_ERROR;
import static io.mosip.idrepository.core.constant.IdRepoErrorConstants.UNKNOWN_ERROR;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import jakarta.annotation.PostConstruct;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.mosip.idrepository.core.dto.RestRequestDTO;
import io.mosip.idrepository.core.exception.AuthenticationException;
import io.mosip.idrepository.core.exception.IdRepoRetryException;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.idrepository.core.logger.IdRepoLogger;
import io.mosip.idrepository.core.security.IdRepoSecurityManager;
import io.mosip.idrepository.core.util.RestUtil;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.logger.spi.Logger;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Value;
import reactor.util.retry.Retry;

/**
 * The Class RestHelper - to send/receive HTTP requests and return the response.
 *
 * @author Manoj SP
 */
@NoArgsConstructor
public class RestHelper {

    private static final String CHECK_ERROR_RESPONSE = "checkErrorResponse";
    private static final String UNKNOWN_ERROR_LOG = "- UNKNOWN_ERROR - ";
    private static final String ERRORS = "errors";
    private static final String METHOD_REQUEST_SYNC = "requestSync";
    private static final String METHOD_HANDLE_STATUS_ERROR = "handleStatusError";
    private static final String PREFIX_REQUEST = "Request : ";
    private static final String METHOD_REQUEST_ASYNC = "requestAsync";
    private static final String CLASS_REST_HELPER = "RestHelper";
    private static final String THROWING_REST_SERVICE_EXCEPTION = "Throwing RestServiceException";
    private static final String REQUEST_SYNC_RUNTIME_EXCEPTION = "requestSync-RuntimeException";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);

    private static Logger mosipLogger = IdRepoLogger.getLogger(RestHelper.class);

    private WebClient webClient;

    @Value("${webclient.buffer.max-in-memory-size:10485760}")
    private int maxInMemorySize;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ApplicationContext ctx;

    public RestHelper(WebClient webClient) {
        this.webClient = webClient;
    }

    @PostConstruct
    public void init() {
        if (Objects.isNull(webClient)) {
            this.webClient = WebClient.builder()
                    .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxInMemorySize))
                    .build();
        } else {
            this.webClient = webClient.mutate()
                    .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxInMemorySize))
                    .build();
        }
    }

    /**
     * Request to send/receive HTTP requests and return the response synchronously.
     *
     * @param <T>     the generic type
     * @param request the request
     * @return the response object or null in case of exception
     * @throws RestServiceException the rest service exception
     */
    @SuppressWarnings("unchecked")
    public <T> T requestSync(@Valid RestRequestDTO request) throws RestServiceException {
        Object response;
        try {
            mosipLogger.debug(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
                    "Request URI: " + request.getUri());
            Duration timeout = request.getTimeout() != null ? Duration.ofSeconds(request.getTimeout()) : DEFAULT_TIMEOUT;
            response = request(request)
                    .timeout(timeout)
                    .retryWhen(Retry.backoff(3, Duration.ofMillis(500)).filter(throwable ->
                            throwable instanceof IdRepoRetryException || throwable.getCause() instanceof java.util.concurrent.TimeoutException))
                    .block();
            if (!String.class.equals(request.getResponseType())) {
                checkErrorResponse(response, request.getResponseType());
                if (RestUtil.containsError(response.toString(), mapper)) {
                    mosipLogger.debug("Error in response: " + response.toString());
                }
            }
            mosipLogger.debug(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
                    "Received valid response");
            return (T) response;
        } catch (WebClientResponseException e) {
            mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
                    THROWING_REST_SERVICE_EXCEPTION + " - Http Status error - " + e.getMessage());
            throw handleStatusError(e, request.getResponseType());
        } catch (RuntimeException e) {
            if (e.getCause() != null && e.getCause().getClass().equals(java.util.concurrent.TimeoutException.class)) {
                mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
                        THROWING_REST_SERVICE_EXCEPTION + " - CONNECTION_TIMED_OUT - " + ExceptionUtils.getStackTrace(e));
                throw new IdRepoRetryException(new RestServiceException(CONNECTION_TIMED_OUT, e));
            } else {
                mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, REQUEST_SYNC_RUNTIME_EXCEPTION,
                        THROWING_REST_SERVICE_EXCEPTION + " " + UNKNOWN_ERROR_LOG + ExceptionUtils.getStackTrace(e));
                throw new IdRepoRetryException(new RestServiceException(UNKNOWN_ERROR, e));
            }
        }
    }

    /**
     * Request to send/receive HTTP requests and return the response asynchronously.
     *
     * @param request the request
     * @return the CompletableFuture
     * @throws RestServiceException
     */
    @Async
    public CompletableFuture<Object> requestAsync(@Valid RestRequestDTO request) {
        mosipLogger.debug(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_ASYNC,
                PREFIX_REQUEST + request.getUri());
        return (CompletableFuture<Object>) request(request)
                .map(response -> {
                    if (!String.class.equals(request.getResponseType())) {
                        try {
                            checkErrorResponse(response, request.getResponseType());
                        } catch (RestServiceException e) {
                            throw new RuntimeException(e);
                        }
                        if (RestUtil.containsError(response.toString(), mapper)) {
                            mosipLogger.debug("Error in response: " + response.toString());
                        }
                    }
                    return response;
                })
                .timeout(request.getTimeout() != null ? Duration.ofSeconds(request.getTimeout()) : DEFAULT_TIMEOUT)
                .retryWhen(Retry.backoff(3, Duration.ofMillis(500)).filter(throwable ->
                        throwable instanceof IdRepoRetryException || throwable.getCause() instanceof java.util.concurrent.TimeoutException))
                .toFuture()
                .exceptionally(throwable -> {
                    mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_ASYNC,
                            "Exception: " + ExceptionUtils.getStackTrace(throwable));
                    if (throwable instanceof WebClientResponseException) {
                        try {
                            throw handleStatusError((WebClientResponseException) throwable, request.getResponseType());
                        } catch (RestServiceException e) {
                            throw new RuntimeException(e);
                        }
                    } else if (throwable.getCause() instanceof java.util.concurrent.TimeoutException) {
                        throw new IdRepoRetryException(new RestServiceException(CONNECTION_TIMED_OUT, throwable));
                    } else {
                        throw new IdRepoRetryException(new RestServiceException(UNKNOWN_ERROR, throwable));
                    }
                });
    }

    /**
     * Method to send/receive HTTP requests and return the response as Mono.
     *
     * @param request the request
     * @return the mono
     */
    private Mono<?> request(RestRequestDTO request) {
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
            exchange = requestBodySpec.bodyValue(request.getRequestBody()).retrieve();
        } else {
            exchange = requestBodySpec.retrieve();
        }

        return exchange.bodyToMono(request.getResponseType());
    }

    /**
     * Check error response.
     *
     * @param response     the response
     * @param responseType the response type
     * @throws RestServiceException the rest service exception
     */
    private void checkErrorResponse(Object response, Class<?> responseType) throws RestServiceException {
        if (Objects.isNull(response)) {
            mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, CHECK_ERROR_RESPONSE,
                    THROWING_REST_SERVICE_EXCEPTION + " " + UNKNOWN_ERROR_LOG + " Response is null");
            throw new RestServiceException(CLIENT_ERROR);
        }
        if (response instanceof String) {
            String responseStr = (String) response;
            if (RestUtil.containsError(responseStr, mapper)) {
                try {
                    ObjectNode responseNode = mapper.readValue(responseStr, ObjectNode.class);
                    if (responseNode.has(ERRORS) && !responseNode.get(ERRORS).isNull() && responseNode.get(ERRORS).isArray()
                            && responseNode.get(ERRORS).size() > 0) {
                        mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, CHECK_ERROR_RESPONSE,
                                THROWING_REST_SERVICE_EXCEPTION + " " + UNKNOWN_ERROR_LOG + " " + responseNode.get(ERRORS).toString());
                        throw new RestServiceException(CLIENT_ERROR, responseNode.toString(),
                                mapper.readValue(responseStr, responseType));
                    }
                } catch (IOException e) {
                    mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, CHECK_ERROR_RESPONSE,
                            THROWING_REST_SERVICE_EXCEPTION + " " + UNKNOWN_ERROR_LOG + " " + e.getMessage());
                    throw new RestServiceException(UNKNOWN_ERROR, e);
                }
            }
        }
    }

    /**
     * Handle 4XX/5XX status error. Retry is triggered using {@code IdRepoRetryException}.
     *
     * @param e            the response
     * @param responseType the response type
     * @return the RestServiceException
     * @throws RestServiceException
     */
    private RestServiceException handleStatusError(WebClientResponseException e, Class<?> responseType)
            throws RestServiceException {
        String responseBody = e.getResponseBodyAsString();
        mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER,
                "request failed with status code: " + e.getRawStatusCode());
        try {
            if (e.getStatusCode().is4xxClientError()) {
                List<ServiceError> errorList = ExceptionUtils.getServiceErrorList(responseBody);
                if (e.getRawStatusCode() == HttpStatus.UNAUTHORIZED.value()) {
                    throw new AuthenticationException(errorList.get(0).getErrorCode(), errorList.get(0).getMessage(),
                            e.getRawStatusCode());
                } else if (e.getRawStatusCode() == HttpStatus.FORBIDDEN.value()) {
                    throw new IdRepoRetryException(new AuthenticationException(errorList.get(0).getErrorCode(),
                            errorList.get(0).getMessage(), e.getRawStatusCode()));
                } else {
                    mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_HANDLE_STATUS_ERROR,
                            "Status error - returning RestServiceException - CLIENT_ERROR");
                    Object parsedResponse = mapper.readValue(responseBody.getBytes(), responseType);
                    throw new RestServiceException(CLIENT_ERROR, responseBody, parsedResponse);
                }
            } else {
                mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_HANDLE_STATUS_ERROR,
                        "Status error - returning RestServiceException - SERVER_ERROR");
                Object parsedResponse = mapper.readValue(responseBody.getBytes(), responseType);
                throw new IdRepoRetryException(new RestServiceException(SERVER_ERROR, responseBody, parsedResponse));
            }
        } catch (IOException ex) {
            mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_HANDLE_STATUS_ERROR,
                    "IOException: " + ex.getMessage());
            return new RestServiceException(UNKNOWN_ERROR, ex);
        }
    }
}