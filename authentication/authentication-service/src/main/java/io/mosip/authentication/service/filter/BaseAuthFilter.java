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
 * The Class BaseAuthFilter - The Base Auth Filter that does all necessary
 * authentication/authorization before allowing the request to the respective
 * controllers.
 * 
 * @author Manoj SP
 */
@Component
public abstract class BaseAuthFilter implements Filter {

	/** The env. */
	@Autowired
	private Environment env;

	/** The Constant BASE_AUTH_FILTER. */
	private static final String BASE_AUTH_FILTER = "BaseAuthFilter";

	/** The Constant EVENT_FILTER. */
	private static final String EVENT_FILTER = "Event_filter";

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "SessionId";

	/** The mapper. */
	@Autowired
	protected ObjectMapper mapper;

	/** The Constant EMPTY_JSON_OBJ_STRING. */
	private static final String EMPTY_JSON_OBJ_STRING = "{";

	/** The mosip logger. */
	private static Logger mosipLogger =
			IdaLogger.getLogger(BaseAuthFilter.class);

	/** The request time. */
	private String requestTime;

	/** The time formatter. */
	private DateTimeFormatter timeFormatter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		WebApplicationContext context =
				WebApplicationContextUtils.getRequiredWebApplicationContext(
						filterConfig.getServletContext());
		env = context.getBean(Environment.class);
		mapper = context.getBean(ObjectMapper.class);
		timeFormatter = DateTimeFormatter
				.ofPattern(env.getProperty("datetime.pattern"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 * javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		requestTime = mapper.convertValue(new Date(), String.class);

		ResettableStreamHttpServletRequest requestWrapper =
				new ResettableStreamHttpServletRequest(
						(HttpServletRequest) request);

		CharResponseWrapper responseWrapper =
				new CharResponseWrapper((HttpServletResponse) response);

		double requestSize = ((double) IOUtils.toString(requestWrapper.getInputStream(),
				Charset.defaultCharset()).length()) / 1024;
		mosipLogger.info(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER,
				"Request received at : " + requestTime + " with Request size : "
						+ ((requestSize > 0) ? requestSize : 1) + " kb");
		requestWrapper.resetInputStream();

		try {
			ObjectWriter objectWriter = mapper.writerWithDefaultPrettyPrinter();

			Map<String, Object> decodedRequest = decodedRequest(
					getRequestBody(requestWrapper.getInputStream()));

			mosipLogger.info(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER,
					"Input Request: \n"
							+ objectWriter.writeValueAsString(decodedRequest));
			requestWrapper.resetInputStream();

			requestWrapper.replaceData(
					objectWriter.writeValueAsString(decodedRequest).getBytes());
			requestWrapper.resetInputStream();

			responseWrapper =
					new CharResponseWrapper((HttpServletResponse) response);

			chain.doFilter(requestWrapper, responseWrapper);

			requestWrapper.resetInputStream();

			response.getWriter()
					.write(mapper.writeValueAsString(encodedResponse(setTxnId(
							getRequestBody(requestWrapper.getInputStream()),
							getResponseBody(responseWrapper.toString())))));

			logResponseTime((String) getResponseBody(responseWrapper.toString())
					.get("resTime"));
		} catch (IdAuthenticationAppException e) {
			mosipLogger.error(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER,
					"\n" + ExceptionUtils.getStackTrace(e));
			requestWrapper.resetInputStream();
			responseWrapper =
					sendErrorResponse(response, chain, requestWrapper);
		} finally {
			double responseSize = ((double) responseWrapper.toString().length()) / 1024;
			mosipLogger.info(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER,
					"Response sent with Request size : "
							+ ((responseSize > 0) ? responseSize : 1) + " kb");
		}
	}

	/**
	 * Gets the request body.
	 *
	 * @param inputStream
	 *            the input stream
	 * @return the request body
	 * @throws IdAuthenticationAppException
	 *             the id authentication app exception
	 */
	private Map<String, Object> getRequestBody(InputStream inputStream)
			throws IdAuthenticationAppException {
		try {
			return mapper.readValue(
					IOUtils.toString(inputStream, Charset.defaultCharset()),
					new TypeReference<Map<String, Object>>() {
					});
		} catch (IOException | ClassCastException e) {
			throw new IdAuthenticationAppException(
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST
							.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST
							.getErrorMessage());
		}
	}

	/**
	 * Gets the response body.
	 *
	 * @param output
	 *            the output
	 * @return the response body
	 * @throws IdAuthenticationAppException
	 *             the id authentication app exception
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> getResponseBody(String output)
			throws IdAuthenticationAppException {
		try {
			return mapper.readValue(output, Map.class);
		} catch (IOException | ClassCastException e) {
			throw new IdAuthenticationAppException(
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST
							.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST
							.getErrorMessage());
		}
	}

	/**
	 * Encode.
	 *
	 * @param stringToEncode
	 *            the string to encode
	 * @return the string
	 * @throws IdAuthenticationAppException
	 *             the id authentication app exception
	 */
	protected String encode(String stringToEncode)
			throws IdAuthenticationAppException {
		try {
			if (stringToEncode != null) {
				return Base64.getEncoder()
						.encodeToString(stringToEncode.getBytes());
			} else {
				return stringToEncode;
			}
		} catch (IllegalArgumentException e) {
			throw new IdAuthenticationAppException(
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST
							.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST
							.getErrorMessage());
		}
	}

	/**
	 * Decode.
	 *
	 * @param stringToDecode
	 *            the string to decode
	 * @return the object
	 * @throws IdAuthenticationAppException
	 *             the id authentication app exception
	 */
	protected Object decode(String stringToDecode)
			throws IdAuthenticationAppException {
		try {
			if (stringToDecode != null) {
				return mapper.readValue(
						Base64.getDecoder().decode(stringToDecode),
						new TypeReference<Map<String, Object>>() {
						});
			} else {
				return stringToDecode;
			}
		} catch (IllegalArgumentException | IOException e) {
			throw new IdAuthenticationAppException(
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST
							.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST
							.getErrorMessage());
		}
	}

	/**
	 * Log response time.
	 *
	 * @param responseTime
	 *            the response time
	 */
	private void logResponseTime(String responseTime) {
		mosipLogger.info(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER,
				"Response sent at : " + responseTime);
		long duration = Duration
				.between(Instant.from(timeFormatter.parse(requestTime)),
						Instant.from(timeFormatter.parse(responseTime)))
				.toMillis();
		mosipLogger.info(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER,
				"Time difference between request and response in millis:"
						+ duration
						+ ".  Time difference between request and response in Seconds: "
						+ ((duration / 1000) % 60));
	}

	/**
	 * Send error response.
	 *
	 * @param response
	 *            the response
	 * @param chain
	 *            the chain
	 * @param requestWrapper
	 *            the request wrapper
	 * @return the char response wrapper
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ServletException
	 *             the servlet exception
	 */
	private CharResponseWrapper sendErrorResponse(ServletResponse response,
			FilterChain chain,
			ResettableStreamHttpServletRequest requestWrapper)
			throws IOException, ServletException {
		CharResponseWrapper responseWrapper;
		requestWrapper.replaceData(EMPTY_JSON_OBJ_STRING.getBytes());
		responseWrapper =
				new CharResponseWrapper((HttpServletResponse) response);
		chain.doFilter(requestWrapper, responseWrapper);
		try {
			response.getWriter().write(responseWrapper.toString());
			logResponseTime((String) getResponseBody(responseWrapper.toString())
					.get("resTime"));
		} catch (IdAuthenticationAppException e1) {
			String responseTime = mapper.convertValue(new Date(), String.class);
			mosipLogger.error(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER,
					"Cannot log time \n" + ExceptionUtils.getStackTrace(e1));
			long duration = Duration
					.between(Instant.from(timeFormatter.parse(requestTime)),
							Instant.from(timeFormatter.parse(responseTime)))
					.toMillis();
			mosipLogger.info(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER,
					"Cannot log time. Response sent at : " + responseTime
							+ ". Time taken in millis: " + duration
							+ ". Time taken in seconds: "
							+ ((duration / 1000) % 60));
		}
		return responseWrapper;
	}

	/**
	 * Decoded request.
	 *
	 * @param requestBody
	 *            the request body
	 * @return the map
	 * @throws IdAuthenticationAppException
	 *             the id authentication app exception
	 */
	protected abstract Map<String, Object> decodedRequest(
			Map<String, Object> requestBody)
			throws IdAuthenticationAppException;

	/**
	 * Encoded response.
	 *
	 * @param responseBody
	 *            the response body
	 * @return the map
	 * @throws IdAuthenticationAppException
	 *             the id authentication app exception
	 */
	protected abstract Map<String, Object> encodedResponse(
			Map<String, Object> responseBody)
			throws IdAuthenticationAppException;

	/**
	 * Sets the txn id.
	 *
	 * @param requestBody
	 *            the request body
	 * @param responseBody
	 *            the response body
	 * @return the map
	 */
	protected abstract Map<String, Object> setTxnId(
			Map<String, Object> requestBody, Map<String, Object> responseBody);

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {

	}

}
