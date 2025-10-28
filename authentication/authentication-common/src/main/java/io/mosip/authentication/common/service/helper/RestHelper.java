package io.mosip.authentication.common.service.helper;

import static io.mosip.idrepository.core.constant.IdRepoErrorConstants.CLIENT_ERROR;
import static io.mosip.idrepository.core.constant.IdRepoErrorConstants.CONNECTION_TIMED_OUT;
import static io.mosip.idrepository.core.constant.IdRepoErrorConstants.SERVER_ERROR;
import static io.mosip.idrepository.core.constant.IdRepoErrorConstants.UNKNOWN_ERROR;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

import jakarta.annotation.PostConstruct;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
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
import io.mosip.kernel.core.retry.WithRetry;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

// Netty client tuning (optional but recommended)
import io.netty.channel.ChannelOption;
import reactor.netty.http.client.HttpClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;

/**
 * The Class RestHelper - to send/receive HTTP requests and return the response.
 *
 * Production-hardened version:
 * - Does not mutate incoming RestRequestDTO (builds URI locally)
 * - Null-safe responseType handling
 * - Safer instanceof checks for timeouts
 * - One-pass JSON tree parsing for error detection
 * - Optional connect/read timeouts on WebClient
 * - Sets Content-Type when body exists
 */
@NoArgsConstructor(force = true)
public class RestHelper {

    private static final String CHECK_ERROR_RESPONSE = "checkErrorResponse";
    private static final String UNKNOWN_ERROR_LOG = "- UNKNOWN_ERROR - ";
    private static final String ERRORS = "errors";
    private static final String METHOD_REQUEST_SYNC = "requestSync";
    private static final String METHOD_REQUEST_ASYNC = "requestAsync";
    private static final String PREFIX_REQUEST = "Request : ";
    private static final String CLASS_REST_HELPER = "RestHelper";
    private static final String THROWING_REST_SERVICE_EXCEPTION = "Throwing RestServiceException";
    private static final String METHOD_HANDLE_STATUS_ERROR = "handleStatusError";
    private static final String REQUEST_SYNC_RUNTIME_EXCEPTION = "requestSync-RuntimeException";

    private static final Logger mosipLogger =
            IdRepoLogger.getLogger(RestHelper.class);

    @Autowired
    private ObjectMapper mapper;
    private WebClient webClient;

    @Autowired
    private ApplicationContext ctx;

    @Value("${webclient.buffer.max-in-memory-size:10485760}") // 10 MB default
    private int maxInMemorySize;

    @Value("${webclient.connect-timeout-millis:3000}")
    private int connectTimeoutMillis;

    @Value("${webclient.response-timeout-seconds:5}")
    private int responseTimeoutSeconds;

    public RestHelper(WebClient webClient) {
        this.webClient = webClient;
    }

    @PostConstruct
    public void init() {
        // WebClient could be injected via ctor or bean name; mutate for codecs and timeouts
        if (Objects.isNull(webClient)) {
            webClient = ctx.getBean("webClient", WebClient.class);
        }
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMillis)
                .responseTimeout(Duration.ofSeconds(responseTimeoutSeconds));

        this.webClient = this.webClient.mutate()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .codecs(c -> c.defaultCodecs().maxInMemorySize(maxInMemorySize))
                .build();
    }

    /**
     * Request to send/receive HTTP requests and return the response synchronously.
     */
    @SuppressWarnings("unchecked")
    @WithRetry
    public <T> T requestSync(@Valid RestRequestDTO request, MediaType mediaType) throws RestServiceException {
        Object response;
        Class<?> responseType = (request.getResponseType() == null) ? String.class : request.getResponseType();

        try {
            mosipLogger.debug(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
                    request.getUri());

            Mono<?> mono = request(request, responseType, mediaType);
            if (request.getTimeout() != null) {
                response = mono.timeout(Duration.ofSeconds(request.getTimeout())).block();
            } else {
                response = mono.block();
            }

            if (!String.class.equals(responseType)) {
                checkErrorResponseSync(response, responseType);
                if (response != null && RestUtil.containsError(response.toString(), mapper)) {
                    mosipLogger.debug(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
                            "Error in response {}", String.valueOf(response));
                }
            }
            mosipLogger.debug(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
                    "Received valid response");
            return (T) response;

        } catch (WebClientResponseException e) {
            mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
                    THROWING_REST_SERVICE_EXCEPTION + " - Http Status error - \n " + e.getMessage()
                            + " \n Response Body : \n" + e.getResponseBodyAsString(StandardCharsets.UTF_8));
            throw handleStatusErrorSync(e, responseType);

        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof TimeoutException) {
                mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
                        THROWING_REST_SERVICE_EXCEPTION + " - CONNECTION_TIMED_OUT - \n "
                                + ExceptionUtils.getStackTrace(e));
                throw new IdRepoRetryException(new RestServiceException(CONNECTION_TIMED_OUT, e));
            } else {
                mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, REQUEST_SYNC_RUNTIME_EXCEPTION,
                        THROWING_REST_SERVICE_EXCEPTION + UNKNOWN_ERROR_LOG + ExceptionUtils.getStackTrace(e));
                throw new IdRepoRetryException(new RestServiceException(UNKNOWN_ERROR, e));
            }
        }
    }

    /**
     * Request to send/receive HTTP requests and return the response asynchronously.
     */
    @WithRetry
    @SuppressWarnings("unchecked")
    public <T> Mono<T> requestAsync(@Valid RestRequestDTO request, MediaType mediaType) {
        Class<?> responseType = (request.getResponseType() == null) ? String.class : request.getResponseType();

        // Log request metadata
        mosipLogger.info(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_ASYNC,
                "Initiating async REST call to URI: " + request.getUri());
        mosipLogger.info(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_ASYNC,
                "Request Method: " + request.getRequestBody() +
                        ", Headers: " + request.getHeaders());

        // Actual async request flow
        return request(request, responseType, mediaType)
                .doOnNext(response -> mosipLogger.info(
                        IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_ASYNC,
                        "Response received for URI: " + request.getUri() +
                                ", Type: " + responseType.getSimpleName() +
                                ", Response: " + response))
                .flatMap(response -> {
                    if (!String.class.equals(responseType)) {
                        return checkErrorResponse(response, responseType).then(Mono.just(response));
                    }
                    return Mono.just(response);
                })
                .map(response -> (T) response)
                .onErrorMap(WebClientResponseException.class, e -> {
                    mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_ASYNC,
                            "HTTP Error for URI: " + request.getUri() +
                                    ", Status: " + e.getStatusCode() +
                                    ", Response Body: " + e.getResponseBodyAsString(), e);
                    return handleStatusError(e, responseType);
                })
                .onErrorMap(e -> {
                    Throwable cause = e.getCause();
                    if (cause instanceof TimeoutException) {
                        mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_ASYNC,
                                "Timeout while calling URI: " + request.getUri() +
                                        "\nException: " + ExceptionUtils.getStackTrace(e));
                        return new IdRepoRetryException(new RestServiceException(CONNECTION_TIMED_OUT, e));
                    }
                    mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_ASYNC,
                            "Unknown error during REST call to URI: " + request.getUri() +
                                    "\nException: " + ExceptionUtils.getStackTrace(e));
                    return new IdRepoRetryException(new RestServiceException(UNKNOWN_ERROR, e));
                });
    }

    /**
     * Build the final URI WITHOUT mutating the incoming DTO.
     */
    private String buildUri(RestRequestDTO req) {
        UriComponentsBuilder b = UriComponentsBuilder.fromUriString(req.getUri());
        if (req.getParams() != null) {
            b.queryParams(req.getParams());
        }
        if (req.getPathVariables() != null) {
            return b.buildAndExpand(req.getPathVariables()).toUriString();
        }
        return b.toUriString();
    }

    /**
     * Method to send/receive HTTP requests and return the response as Mono.
     */
    private Mono<?> request(RestRequestDTO request, Class<?> responseType, MediaType mediaType) {
        String uri = buildUri(request);

        RequestBodySpec spec = webClient.method(request.getHttpMethod()).uri(uri);

        if (request.getHeaders() != null) {
            spec = spec.headers(h -> h.addAll(request.getHeaders()));
        }

        ResponseSpec exchange;
        if (request.getRequestBody() != null) {
            // Set JSON content-type when a body is present (adjust if you support other media types)
            spec = spec.contentType(mediaType);
            exchange = spec.bodyValue(request.getRequestBody()).retrieve();
        } else {
            exchange = spec.retrieve();
        }

        // Centralize status-to-exception mapping with onStatus (keeps errors out of happy path)
        exchange = exchange
                .onStatus(HttpStatusCode::is4xxClientError, r ->
                        r.bodyToMono(String.class).map(body -> {
                            if (r.statusCode().value() == HttpStatus.UNAUTHORIZED.value()) {
                                List<ServiceError> errs = ExceptionUtils.getServiceErrorList(body);
                                return new AuthenticationException(errs.get(0).getErrorCode(), errs.get(0).getMessage(), 401);
                            } else if (r.statusCode().value() == HttpStatus.FORBIDDEN.value()) {
                                List<ServiceError> errs = ExceptionUtils.getServiceErrorList(body);
                                return new IdRepoRetryException(new AuthenticationException(
                                        errs.get(0).getErrorCode(), errs.get(0).getMessage(), 403));
                            } else {
                                Object parsed = tryRead(mapper, body, responseType);
                                return new RestServiceException(CLIENT_ERROR, body, parsed);
                            }
                        })
                ).onStatus(HttpStatusCode::is5xxServerError, r ->
                        r.bodyToMono(String.class).map(body -> {
                            Object parsed = tryRead(mapper, body, responseType);
                            return new IdRepoRetryException(new RestServiceException(SERVER_ERROR, body, parsed));
                        })
                );

        Mono<?> monoResponse = exchange.bodyToMono(responseType);

        if (request.getTimeout() != null) {
            monoResponse = monoResponse.timeout(Duration.ofSeconds(request.getTimeout()));
        }
        return monoResponse;
    }

    /**
     * Check error response (synchronous) — single tree pass, no double (de)serialization.
     */
    private void checkErrorResponseSync(Object response, Class<?> responseType) throws RestServiceException {
        try {
            if (Objects.nonNull(response)) {
                ObjectNode node = (ObjectNode) mapper.valueToTree(response);
                if (node.hasNonNull(ERRORS) && node.get(ERRORS).isArray() && node.get(ERRORS).size() > 0) {
                    mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, CHECK_ERROR_RESPONSE,
                            THROWING_REST_SERVICE_EXCEPTION + " " + node.get(ERRORS).toString());
                    Object parsed = mapper.treeToValue(node, responseType);
                    throw new RestServiceException(CLIENT_ERROR, node.toString(), parsed);
                }
            } else {
                mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, CHECK_ERROR_RESPONSE,
                        THROWING_REST_SERVICE_EXCEPTION + UNKNOWN_ERROR_LOG + "Response is null");
                throw new RestServiceException(CLIENT_ERROR);
            }
        } catch (IOException e) {
            mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, CHECK_ERROR_RESPONSE,
                    THROWING_REST_SERVICE_EXCEPTION + UNKNOWN_ERROR_LOG + e.getMessage());
            throw new RestServiceException(UNKNOWN_ERROR, e);
        }
    }

    /**
     * Check error response (reactive).
     */
    private Mono<Void> checkErrorResponse(Object response, Class<?> responseType) {
        return Mono.fromCallable(() -> {
            if (Objects.nonNull(response)) {
                ObjectNode node = (ObjectNode) mapper.valueToTree(response);
                if (node.hasNonNull(ERRORS) && node.get(ERRORS).isArray() && node.get(ERRORS).size() > 0) {
                    mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, CHECK_ERROR_RESPONSE,
                            THROWING_REST_SERVICE_EXCEPTION + " " + node.get(ERRORS).toString());
                    Object parsed = mapper.treeToValue(node, responseType);
                    throw new RestServiceException(CLIENT_ERROR, node.toString(), parsed);
                }
                return null;
            }
            mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, CHECK_ERROR_RESPONSE,
                    THROWING_REST_SERVICE_EXCEPTION + UNKNOWN_ERROR_LOG + "Response is null");
            throw new RestServiceException(CLIENT_ERROR);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    /**
     * Handle 4XX/5XX status error (synchronous).
     * Retry is triggered using IdRepoRetryException.
     */
    private RestServiceException handleStatusErrorSync(WebClientResponseException e, Class<?> responseType)
            throws RestServiceException {
        try {
            String responseBodyAsString = e.getResponseBodyAsString(StandardCharsets.UTF_8);
            Object responseBody = tryRead(mapper, responseBodyAsString, responseType);

            mosipLogger.error(
                    IdRepoSecurityManager.getUser(), CLASS_REST_HELPER,
                    "request failed with status code :" + e.getRawStatusCode(),
                    "\n\n" + responseBodyAsString);

            if (e.getStatusCode().is4xxClientError()) {
                if (e.getRawStatusCode() == HttpStatus.UNAUTHORIZED.value()) {
                    List<ServiceError> errorList = ExceptionUtils.getServiceErrorList(responseBodyAsString);
                    throw new AuthenticationException(errorList.get(0).getErrorCode(),
                            errorList.get(0).getMessage(), e.getRawStatusCode());
                } else if (e.getRawStatusCode() == HttpStatus.FORBIDDEN.value()) {
                    List<ServiceError> errorList = ExceptionUtils.getServiceErrorList(responseBodyAsString);
                    throw new IdRepoRetryException(new AuthenticationException(errorList.get(0).getErrorCode(),
                            errorList.get(0).getMessage(), e.getRawStatusCode()));
                } else {
                    mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_HANDLE_STATUS_ERROR,
                            "Status error - returning RestServiceException - CLIENT_ERROR ");
                    throw new RestServiceException(CLIENT_ERROR, responseBodyAsString, responseBody);
                }
            } else if (e.getStatusCode().is5xxServerError()) {
                mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_HANDLE_STATUS_ERROR,
                        "Status error - returning RestServiceException - SERVER_ERROR");
                throw new IdRepoRetryException(new RestServiceException(SERVER_ERROR, responseBodyAsString, responseBody));
            } else {
                mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_HANDLE_STATUS_ERROR,
                        "Status error - returning RestServiceException - UNKNOWN_ERROR");
                throw new RestServiceException(UNKNOWN_ERROR, responseBodyAsString, responseBody);
            }
        } catch (Exception ex) {
            mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_HANDLE_STATUS_ERROR,
                    ex.getMessage());
            return new RestServiceException(UNKNOWN_ERROR, ex);
        }
    }

    /**
     * Handle 4XX/5XX status error (reactive) — kept for parity with previous design,
     * though retrieve().onStatus(...) now handles most mappings upstream.
     */
    private Throwable handleStatusError(WebClientResponseException e, Class<?> responseType) {
        try {
            String responseBodyAsString = e.getResponseBodyAsString(StandardCharsets.UTF_8);
            Object responseBody = tryRead(mapper, responseBodyAsString, responseType);

            mosipLogger.error(
                    IdRepoSecurityManager.getUser(), CLASS_REST_HELPER,
                    "request failed with status code :" + e.getRawStatusCode(),
                    "\n\n" + responseBodyAsString);

            if (e.getStatusCode().is4xxClientError()) {
                if (e.getRawStatusCode() == HttpStatus.UNAUTHORIZED.value()) {
                    List<ServiceError> errorList = ExceptionUtils.getServiceErrorList(responseBodyAsString);
                    return new AuthenticationException(errorList.get(0).getErrorCode(),
                            errorList.get(0).getMessage(), e.getRawStatusCode());
                } else if (e.getRawStatusCode() == HttpStatus.FORBIDDEN.value()) {
                    List<ServiceError> errorList = ExceptionUtils.getServiceErrorList(responseBodyAsString);
                    return new IdRepoRetryException(new AuthenticationException(errorList.get(0).getErrorCode(),
                            errorList.get(0).getMessage(), e.getRawStatusCode()));
                } else {
                    mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_HANDLE_STATUS_ERROR,
                            "Status error - returning RestServiceException - CLIENT_ERROR ");
                    return new RestServiceException(CLIENT_ERROR, responseBodyAsString, responseBody);
                }
            } else if (e.getStatusCode().is5xxServerError()) {
                mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_HANDLE_STATUS_ERROR,
                        "Status error - returning RestServiceException - SERVER_ERROR");
                return new IdRepoRetryException(new RestServiceException(SERVER_ERROR, responseBodyAsString, responseBody));
            } else {
                mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_HANDLE_STATUS_ERROR,
                        "Status error - returning RestServiceException - UNKNOWN_ERROR");
                return new RestServiceException(UNKNOWN_ERROR, responseBodyAsString, responseBody);
            }
        } catch (Exception ex) {
            mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_HANDLE_STATUS_ERROR,
                    ex.getMessage());
            return new IdRepoRetryException(new RestServiceException(UNKNOWN_ERROR, ex));
        }
    }

    /**
     * Safe JSON parse helper (returns null if parse fails).
     */
    private static Object tryRead(ObjectMapper mapper, String body, Class<?> type) {
        if (type == null || type == String.class || body == null) return null;
        try {
            return mapper.readValue(body.getBytes(StandardCharsets.UTF_8), type);
        } catch (IOException ignored) {
            return null;
        }
    }
}
