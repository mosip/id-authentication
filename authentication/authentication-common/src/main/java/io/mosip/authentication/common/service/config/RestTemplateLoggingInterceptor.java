package io.mosip.authentication.common.service.config;

import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Kamesh Shekhar Prasad
 */
@Component
@ConditionalOnProperty(value = "true", havingValue = "true", matchIfMissing = false)
public class RestTemplateLoggingInterceptor implements ClientHttpRequestInterceptor {

    private static Logger logger = IdaLogger.getLogger(RestTemplateLoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(
            HttpRequest req, byte[] reqBody, ClientHttpRequestExecution ex) throws IOException {

        Thread currentThread = Thread.currentThread();
        StackTraceElement[] stackTrace = currentThread.getStackTrace();
        String stackTraceString = Stream.of(stackTrace).map(String::valueOf).collect(Collectors.joining("\n"));

        long currentTimeBeforeExecution = System.currentTimeMillis();
        ClientHttpResponse response = ex.execute(req, reqBody);
        long currentTimeAfterExecution = System.currentTimeMillis();
        long timeDiff = currentTimeAfterExecution - currentTimeBeforeExecution;
        logger.debug("#rest-template-log#"+ ","+ req.getMethod() + ","+ req.getURI() + ","+timeDiff+"ms,"+stackTraceString);
        return response;
    }
}
