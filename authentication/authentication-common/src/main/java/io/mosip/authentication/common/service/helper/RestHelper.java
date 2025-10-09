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

import jakarta.annotation.PostConstruct;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
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
    private static final String ERRORS = "errors";
    private static final String METHOD_REQUEST_ASYNC = "requestAsync";
    private static final String PREFIX_REQUEST = "Request : ";
    private static final String CLASS_REST_HELPER = "RestHelper";
    private static final String THROWING_REST_SERVICE_EXCEPTION = "Throwing RestServiceException";
    private static final String METHOD_HANDLE_STATUS_ERROR = "handleStatusError";

    private static Logger mosipLogger = IdRepoLogger.getLogger(io.mosip.idrepository.core.helper.RestHelper.class);

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ApplicationContext ctx;

    private WebClient webClient;

    @Value("${webclient.buffer.max-in-memory-size:10485760}")
    private int maxInMemorySize;

    public RestHelper(WebClient webClient) {
        this.webClient = webClient;
    }

    @PostConstruct
    public void init() {
        if (Objects.isNull(webClient)) {
            webClient = ctx.getBean("webClient", WebClient.class);
        }
        this.webClient = this.webClient.mutate()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxInMemorySize))
                .build();
    }

    /**
     * Request to send/receive HTTP requests and return the response asynchronously.
     *
     * @param request the request
     * @return the Mono of response
     */
    @WithRetry
    public <T> Mono<T> requestAsync(@Valid RestRequestDTO request) {
        mosipLogger.debug(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_ASYNC,
                PREFIX_REQUEST + request.getUri());

        return request(request)
                .flatMap(response -> {
                    if (!String.class.equals(request.getResponseType())) {
                        return checkErrorResponse(response, request.getResponseType())
                                .then(Mono.just(response));
                    }
                    return Mono.just(response);
                })
                .map(response -> (T) response)
                .onErrorMap(WebClientResponseException.class, e ->
                        handleStatusError(e, request.getResponseType()))
                .onErrorMap(e -> {
                    if (e.getCause() != null && e.getCause().getClass().equals(java.util.concurrent.TimeoutException.class)) {
                        mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_ASYNC,
                                THROWING_REST_SERVICE_EXCEPTION + "- CONNECTION_TIMED_OUT - \n " + ExceptionUtils.getStackTrace(e));
                        return new IdRepoRetryException(new RestServiceException(CONNECTION_TIMED_OUT, e));
                    }
                    mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_REQUEST_ASYNC,
                            THROWING_REST_SERVICE_EXCEPTION + UNKNOWN_ERROR_LOG + ExceptionUtils.getStackTrace(e));
                    return new IdRepoRetryException(new RestServiceException(UNKNOWN_ERROR, e));
                });
    }

    /**
     * Method to send/receive HTTP requests and return the response as Mono.
     *
     * @param request the request
     * @return the Mono
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

        Mono<?> monoResponse = exchange.bodyToMono(request.getResponseType());

        if (request.getTimeout() != null) {
            monoResponse = monoResponse.timeout(Duration.ofSeconds(request.getTimeout()));
        }

        return monoResponse;
    }

    /**
     * Check error response.
     *
     * @param response     the response
     * @param responseType the response type
     * @return the Mono
     */
    private Mono<Void> checkErrorResponse(Object response, Class<?> responseType) {
        return Mono.fromCallable(() -> {
            if (Objects.nonNull(response)) {
                ObjectNode responseNode = mapper.readValue(mapper.writeValueAsBytes(response), ObjectNode.class);
                if (responseNode.has(ERRORS) && !responseNode.get(ERRORS).isNull() && responseNode.get(ERRORS).isArray()
                        && responseNode.get(ERRORS).size() > 0) {
                    mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, CHECK_ERROR_RESPONSE,
                            THROWING_REST_SERVICE_EXCEPTION + UNKNOWN_ERROR_LOG
                                    + responseNode.get(ERRORS).toString());
                    throw new RestServiceException(CLIENT_ERROR, responseNode.toString(),
                            mapper.readValue(responseNode.toString().getBytes(StandardCharsets.UTF_8), responseType));
                }
                return null;
            }
            mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, CHECK_ERROR_RESPONSE,
                    THROWING_REST_SERVICE_EXCEPTION + UNKNOWN_ERROR_LOG + "Response is null");
            throw new RestServiceException(CLIENT_ERROR);
        }).subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic()).then();
    }

    /**
     * Handle 4XX/5XX status error. Retry is triggered using {@code IdRepoRetryException}.
     * Retry is done for 401 and 5xx status codes.
     *
     * @param e            the response
     * @param responseType the response type
     * @return the Throwable
     */
    private Throwable handleStatusError(WebClientResponseException e, Class<?> responseType) {
        try {
            String responseBodyAsString = e.getResponseBodyAsString(StandardCharsets.UTF_8);
            Object responseBody = null;
            try {
                responseBody = mapper.readValue(responseBodyAsString.getBytes(StandardCharsets.UTF_8), responseType);
            } catch (IOException ex) {
                mosipLogger.warn(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER, METHOD_HANDLE_STATUS_ERROR,
                        "Failed to parse response body: " + ex.getMessage());
            }
            mosipLogger.error(IdRepoSecurityManager.getUser(), CLASS_REST_HELPER,
                    "request failed with status code :" + e.getRawStatusCode(), "\n\n" + responseBodyAsString);
            if (e.getStatusCode().is4xxClientError()) {
                if (e.getRawStatusCode() == HttpStatus.UNAUTHORIZED.value()) {
                    List<ServiceError> errorList = ExceptionUtils.getServiceErrorList(responseBodyAsString);
                    return new AuthenticationException(errorList.get(0).getErrorCode(), errorList.get(0).getMessage(),
                            e.getRawStatusCode());
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
}