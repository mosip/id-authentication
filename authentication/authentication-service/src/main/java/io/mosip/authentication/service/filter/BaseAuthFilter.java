package io.mosip.authentication.service.filter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * @author Manoj SP
 *
 */
@Component
public abstract class BaseAuthFilter implements Filter {

    @Autowired
    private Environment env;

    private static final String BASE_AUTH_FILTER = "BaseAuthFilter";

    private static final String EVENT_FILTER = "Event_filter";

    private static final String SESSION_ID = "SessionId";

    @Autowired
    protected ObjectMapper mapper;

    private static final String EMPTY_JSON_OBJ_STRING = "{";

    private static Logger mosipLogger = IdaLogger.getLogger(BaseAuthFilter.class);

    private String requestTime;

    private DateTimeFormatter timeFormatter;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
	WebApplicationContext context =
		WebApplicationContextUtils.getRequiredWebApplicationContext(filterConfig.getServletContext());
	env = context.getBean(Environment.class);
	mapper = context.getBean(ObjectMapper.class);
	timeFormatter = DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
	    throws IOException, ServletException {
	requestTime = mapper.convertValue(new Date(), String.class);

	ResettableStreamHttpServletRequest requestWrapper =
		new ResettableStreamHttpServletRequest((HttpServletRequest) request);

	CharResponseWrapper responseWrapper = new CharResponseWrapper((HttpServletResponse) response);

	double requestSize = IOUtils.toString(requestWrapper.getInputStream(), Charset.defaultCharset()).length() / 1024;
	mosipLogger.info(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER, "Request received at : " + requestTime
		+ " with Request size : " + ((requestSize > 0) ? requestSize : 1) + " kb");
	requestWrapper.resetInputStream();

	try {
	    ObjectWriter objectWriter = mapper.writerWithDefaultPrettyPrinter();

	    Map<String, Object> decodedRequest = decodedRequest(getRequestBody(requestWrapper.getInputStream()));

	    mosipLogger.info(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER,
		    "Input Request: \n" + objectWriter.writeValueAsString(decodedRequest));
	    requestWrapper.resetInputStream();

	    requestWrapper.replaceData(objectWriter.writeValueAsString(decodedRequest).getBytes());
	    requestWrapper.resetInputStream();

	    responseWrapper = new CharResponseWrapper((HttpServletResponse) response);

	    chain.doFilter(requestWrapper, responseWrapper);

	    requestWrapper.resetInputStream();

	    response.getWriter().write(
		    mapper.writeValueAsString(encodedResponse(setTxnId(getRequestBody(requestWrapper.getInputStream()),
			    getResponseBody(responseWrapper.toString())))));

	    logResponseTime((String) getResponseBody(responseWrapper.toString()).get("resTime"));
	} catch (IdAuthenticationAppException e) {
	    mosipLogger.error(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER, "\n" + ExceptionUtils.getStackTrace(e));
	    requestWrapper.resetInputStream();
	    requestWrapper.replaceData(EMPTY_JSON_OBJ_STRING.getBytes());
	    responseWrapper = new CharResponseWrapper((HttpServletResponse) response);
	    chain.doFilter(requestWrapper, responseWrapper);
	    try {
		response.getWriter().write(responseWrapper.toString());
		logResponseTime((String) getResponseBody(responseWrapper.toString()).get("resTime"));
	    } catch (IdAuthenticationAppException e1) {
		String responseTime = mapper.convertValue(new Date(), String.class);
		mosipLogger.error(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER,
			"Cannot log time \n" + ExceptionUtils.getStackTrace(e1));
		long duration = Duration.between(Instant.from(timeFormatter.parse(requestTime)),
			Instant.from(timeFormatter.parse(responseTime))).toMillis();
		mosipLogger.info(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER,
			"Cannot log time. Response sent at : " + responseTime + ". Time taken in millis: " + duration
				+ ". Time taken in seconds: " + ((duration / 1000) % 60));
	    }
	} finally {
	    double responseSize = responseWrapper.toString().length() / 1024;
	    mosipLogger.info(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER,
		    "Response sent with Request size : " + ((responseSize > 0) ? responseSize : 1) + " kb");
	}
    }

    private Map<String, Object> getRequestBody(InputStream inputStream) throws IdAuthenticationAppException {
	try {
	    return mapper.readValue(IOUtils.toString(inputStream, Charset.defaultCharset()),
		    new TypeReference<Map<String, Object>>() {
		    });
	} catch (IOException | ClassCastException e) {
	    throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
		    IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());
	}
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getResponseBody(String output) throws IdAuthenticationAppException {
	try {
	    return mapper.readValue(output, Map.class);
	} catch (IOException | ClassCastException e) {
	    throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
		    IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());
	}
    }

    protected String encode(String stringToEncode) throws IdAuthenticationAppException {
	try {
	    if (stringToEncode != null) {
		return Base64.getEncoder().encodeToString(stringToEncode.getBytes());
	    } else {
		return stringToEncode;
	    }
	} catch (IllegalArgumentException e) {
	    throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
		    IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());
	}
    }

    protected Object decode(String stringToDecode) throws IdAuthenticationAppException {
	try {
	    if (stringToDecode != null) {
		return mapper.readValue(Base64.getDecoder().decode(stringToDecode),
			new TypeReference<Map<String, Object>>() {
			});
	    } else {
		return stringToDecode;
	    }
	} catch (IllegalArgumentException | IOException e) {
	    throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
		    IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());
	}
    }

    /**
     * Log response time.
     *
     * @param responseTime
     *            the response time
     */
    private void logResponseTime(String responseTime) {
	mosipLogger.info(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER, "Response sent at : " + responseTime);
	long duration = Duration.between(Instant.from(timeFormatter.parse(requestTime)),
		Instant.from(timeFormatter.parse(responseTime))).toMillis();
	mosipLogger.info(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER,
		"Time difference between request and response in millis:" + duration
			+ ".  Time difference between request and response in Seconds: " + ((duration / 1000) % 60));
    }

    protected abstract Map<String, Object> decodedRequest(Map<String, Object> requestBody)
	    throws IdAuthenticationAppException;

    protected abstract Map<String, Object> encodedResponse(Map<String, Object> responseBody)
	    throws IdAuthenticationAppException;

    protected abstract Map<String, Object> setTxnId(Map<String, Object> requestBody, Map<String, Object> responseBody);

    @Override
    public void destroy() {

    }

}
