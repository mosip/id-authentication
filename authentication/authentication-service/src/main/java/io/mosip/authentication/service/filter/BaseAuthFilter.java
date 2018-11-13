package io.mosip.authentication.service.filter;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Base64;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.spi.logger.MosipLogger;

/**
 * @author Manoj SP
 *
 */
public abstract class BaseAuthFilter implements Filter {

	private static final String BASE_AUTH_FILTER = "BaseAuthFilter";

	private static final String EVENT_FILTER = "Event_filter";

	private static final String SESSION_ID = "SessionId";

	protected ObjectMapper mapper;

	private static final String EMPTY_JSON_OBJ_STRING = "{";

	private static MosipLogger mosipLogger = IdaLogger.getLogger(BaseAuthFilter.class);

	private Instant requestTime;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		mapper = new ObjectMapper();
		requestTime = Instant.now();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		mosipLogger.info(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER, "Request received at : " + requestTime);

		ResettableStreamHttpServletRequest requestWrapper = new ResettableStreamHttpServletRequest(
				(HttpServletRequest) request);

		try {
			ObjectWriter objectWriter = new ObjectMapper().writerWithDefaultPrettyPrinter();
			
			mosipLogger.info(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER, "Input Request: \n" + objectWriter
					.writeValueAsString(decodedRequest(getRequestBody(requestWrapper.getInputStream()))));
			requestWrapper.resetInputStream();

			requestWrapper.replaceData(objectWriter
					.writeValueAsString(decodedRequest(getRequestBody(requestWrapper.getInputStream()))).getBytes());
			requestWrapper.resetInputStream();

			CharResponseWrapper responseWrapper = new CharResponseWrapper((HttpServletResponse) response);

			chain.doFilter(requestWrapper, responseWrapper);

			response.getWriter().write(
					mapper.writeValueAsString(encodedResponse(setTxnId(getRequestBody(requestWrapper.getInputStream()),
							getResponseBody(responseWrapper.toString())))));

			logResponseTime((String) getResponseBody(responseWrapper.toString()).get("resTime"));
		} catch (IdAuthenticationAppException e) {
			mosipLogger.error(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER, "\n" + ExceptionUtils.getStackTrace(e));
			requestWrapper.resetInputStream();
			requestWrapper.replaceData(EMPTY_JSON_OBJ_STRING.getBytes());
			CharResponseWrapper responseWrapper = new CharResponseWrapper((HttpServletResponse) response);
			chain.doFilter(requestWrapper, responseWrapper);
			try {
				response.getWriter().write(responseWrapper.toString());
				logResponseTime((String) getResponseBody(responseWrapper.toString()).get("resTime"));
			} catch (IdAuthenticationAppException e1) {
				mosipLogger.error(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER,
						"Cannot log time \n" + ExceptionUtils.getStackTrace(e1));
				mosipLogger.info(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER,
						"Cannot log time. Response sent at : " + requestTime + ". Time taken in millis: "
								+ Duration.between(Instant.now(), requestTime).toMillis());
			}
		}
	}

	private Map<String, Object> getRequestBody(InputStream inputStream) throws IdAuthenticationAppException {
		try {
			return mapper.readValue(inputStream, new TypeReference<Map<String, Object>>() {
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
		DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_DATE_TIME;
		TemporalAccessor accessor = timeFormatter.parse(responseTime);
		mosipLogger.info(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER,
				"Time difference between request and response in millis: "
						+ Duration.between(requestTime, Instant.from(accessor)).toMillis());
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
