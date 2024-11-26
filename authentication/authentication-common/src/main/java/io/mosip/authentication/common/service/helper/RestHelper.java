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
import java.util.concurrent.TimeoutException;

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
import io.mosip.kernel.core.retry.WithRetry;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Value;

/**
 * The Class RestHelper - to send/receive HTTP requests and return the response.
 *
 * @author Manoj SP
 */
@NoArgsConstructor
public class RestHelper {

    private static final String CHECK_ERROR_RESPONSE = "checkErrorResponse";

    private static final String UNKNOWN_ERROR_LOG = "- UNKNOWN_ERROR - ";

    /** The Constant ERRORS. */
    private static final String ERRORS = "errors";

    /** The mapper. */
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ApplicationContext ctx;

    /** The Constant METHOD_REQUEST_SYNC. */
    private static final String METHOD_REQUEST_SYNC = "requestSync";

    /** The Constant METHOD_HANDLE_STATUS_ERROR. */
    private static final String METHOD_HANDLE_STATUS_ERROR = "handleStatusError";

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

    /** The mosipLogger. */
    private static Logger mosipLogger = IdRepoLogger.getLogger(io.mosip.idrepository.core.helper.RestHelper.class);

    private WebClient webClient;

    @Value("${webclient.buffer.max-in-memory-size:262144}") // Default to 256 KB if not set
    private int maxInMemorySize;

    public RestHelper(WebClient webClient) {
        this.webClient = webClient;
    }

    @PostConstruct
    public void init() {
        if (Objects.isNull(webClient)) {
            webClient = ctx.getBean("webClient", WebClient.class);
        }

        // Retrieve buffer size from configuration
        int bufferSize = maxInMemorySize;

        // Increase buffer size limit
        this.webClient = this.webClient.mutate()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(bufferSize))
                .build();
    }


    /**
     * Request to send/receive HTTP requests and return the response synchronously.
     *
     * @param         <T> the generic type
     * @param request the request
     * @return the response object or null in case of exception
     * @throws RestServiceException the rest service exception
     */
    @SuppressWarnings("unchecked")
    @WithRetry
    public <T> T requestSync(@Valid RestRequestDTO request) throws RestServiceException {
        Object response;
        try {
            mosipLogger.debug(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
                    request.getUri());
            if (request.getTimeout() != null) {
                response = request(request).timeout(Duration.ofSeconds(request.getTimeout())).block();
            } else {
                response = request(request).block();
            }
            if(!String.class.equals(request.getResponseType())) {
                checkErrorResponse(response, request.getResponseType());
                if(RestUtil.containsError(response.toString(), mapper)) {
                    mosipLogger.debug("Error in response %s", response.toString());
                }
            }
            mosipLogger.debug(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
                    "Received valid response");
            return (T) response;
        } catch (WebClientResponseException e) {
            mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
                    THROWING_REST_SERVICE_EXCEPTION + "- Http Status error - \n " + e.getMessage()
                            + " \n Response Body : \n" + e.getResponseBodyAsString());
            throw handleStatusError(e, request.getResponseType());
        } catch (RuntimeException e) {
            if (e.getCause() != null && e.getCause().getClass().equals(TimeoutException.class)) {
                mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_SYNC,
                        THROWING_REST_SERVICE_EXCEPTION + "- CONNECTION_TIMED_OUT - \n " + ExceptionUtils.getStackTrace(e));
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
     *
     * @param request the request
     * @return the supplier
     * @throws RestServiceException
     */
    @Async
    public CompletableFuture<Object> requestAsync(@Valid RestRequestDTO request) {
        mosipLogger.debug(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_ASYNC,
                PREFIX_REQUEST + request.getUri());
        try {
            Object obj =  requestSync(request);
            return CompletableFuture.completedFuture(obj);
        } catch (RestServiceException e) {
            mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_ASYNC,
                    ExceptionUtils.getStackTrace(e));
            return CompletableFuture.failedFuture(e);
        }
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
            if (Objects.nonNull(response)) {
                ObjectNode responseNode = mapper.readValue(mapper.writeValueAsBytes(response), ObjectNode.class);
                if (responseNode.has(ERRORS) && !responseNode.get(ERRORS).isNull() && responseNode.get(ERRORS).isArray()
                        && responseNode.get(ERRORS).size() > 0) {
                    mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, CHECK_ERROR_RESPONSE,
                            THROWING_REST_SERVICE_EXCEPTION + UNKNOWN_ERROR_LOG
                                    + responseNode.get(ERRORS).toString());
                    throw new RestServiceException(CLIENT_ERROR, responseNode.toString(),
                            mapper.readValue(responseNode.toString().getBytes(), responseType));
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
     * Handle 4XX/5XX status error. Retry is triggered using {@code IdRepoRetryException}.
     * Retry is done for 401 and 5xx status codes.
     *
     * @param e            the response
     * @param responseType the response type
     * @return the mono<? extends throwable>
     * @throws RestServiceException
     */
    private RestServiceException handleStatusError(WebClientResponseException e, Class<?> responseType)
            throws RestServiceException {
        try {
            mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER,
                    "request failed with status code :" + e.getRawStatusCode(), "\n\n" + e.getResponseBodyAsString());
            if (e.getStatusCode().is4xxClientError()) {
                if (e.getRawStatusCode() == HttpStatus.UNAUTHORIZED.value()) {
                    List<ServiceError> errorList = ExceptionUtils.getServiceErrorList(e.getResponseBodyAsString());
                    throw new AuthenticationException(errorList.get(0).getErrorCode(), errorList.get(0).getMessage(),
                            e.getRawStatusCode());
                } else if (e.getRawStatusCode() == HttpStatus.FORBIDDEN.value()) {
                    List<ServiceError> errorList = ExceptionUtils.getServiceErrorList(e.getResponseBodyAsString());
                    throw new IdRepoRetryException(new AuthenticationException(errorList.get(0).getErrorCode(),
                            errorList.get(0).getMessage(), e.getRawStatusCode()));
                } else {
                    mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_HANDLE_STATUS_ERROR,
                            "Status error - returning RestServiceException - CLIENT_ERROR ");
                    throw new RestServiceException(CLIENT_ERROR, e.getResponseBodyAsString(),
                            mapper.readValue(e.getResponseBodyAsString().getBytes(), responseType));
                }
            } else {
                mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_HANDLE_STATUS_ERROR,
                        "Status error - returning RestServiceException - SERVER_ERROR");
                throw new IdRepoRetryException(new RestServiceException(SERVER_ERROR, e.getResponseBodyAsString(),
                        mapper.readValue(e.getResponseBodyAsString().getBytes(), responseType)));
            }
        } catch (IOException ex) {
            mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_HANDLE_STATUS_ERROR,
                    ex.getMessage());
            return new RestServiceException(UNKNOWN_ERROR, ex);
        }
    }
}