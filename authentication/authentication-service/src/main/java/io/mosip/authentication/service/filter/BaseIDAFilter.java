package io.mosip.authentication.service.filter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthError;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

/**
 * The Class BaseIDAFilter.
 * 
 * @author Sanjay Murali
 */
public abstract class BaseIDAFilter implements Filter {

	/** The Constant VER_REX. */
	private static final String VER_REX = "[\\s+a-zA-Z]";

	/** The Constant TRANSACTION_ID. */
	private static final String TRANSACTION_ID = "transactionID";

	/** The Constant RESPONSE. */
	private static final String RESPONSE = "response";

	/** The Constant RES_TIME. */
	private static final String RES_TIME = "resTime";

	/** The Constant DATETIME_PATTERN. */
	private static final String DATETIME_PATTERN = "datetime.pattern";

	/** The Constant REQ_TIME. */
	private static final String REQ_TIME = "reqTime";

	/** The Constant DEFAULT_VERSION. */
	private static final String DEFAULT_VERSION = "v1.0";

	/** The Constant BASE_IDA_FILTER. */
	private static final String BASE_IDA_FILTER = "BaseIDAFilter";

	/** The Constant EVENT_FILTER. */
	private static final String EVENT_FILTER = "Event_filter";

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "SessionId";
	
	/** The request time. */
	private String requestTime;
	
	/** The Constant EMPTY_JSON_OBJ_STRING. */
	private static final String EMPTY_JSON_OBJ_STRING = "{";
	
	/** The env. */
	protected Environment env;

	/** The mapper. */
	protected ObjectMapper mapper;
	
	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(BaseIDAFilter.class);

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		WebApplicationContext context = WebApplicationContextUtils
				.getRequiredWebApplicationContext(filterConfig.getServletContext());
		env = context.getBean(Environment.class);
		mapper = context.getBean(ObjectMapper.class);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		ResettableStreamHttpServletRequest requestWrapper = new ResettableStreamHttpServletRequest(
				(HttpServletRequest) request);
		CharResponseWrapper responseWrapper = new CharResponseWrapper((HttpServletResponse) response);		
		try {
			consumeRequest(requestWrapper);
			chain.doFilter(requestWrapper, responseWrapper);			
			String responseAsString = mapResponse(requestWrapper, responseWrapper);		
			response.getWriter().write(responseAsString);
		} catch (IdAuthenticationAppException e) {
			mosipLogger.error(SESSION_ID, EVENT_FILTER, BASE_IDA_FILTER, "\n" + ExceptionUtils.getStackTrace(e));
			requestWrapper.resetInputStream();
			AuthError authError = new AuthError();
			authError.setErrorCode(e.getErrorCode());
			authError.setErrorMessage(e.getErrorText());
			sendErrorResponse(response, responseWrapper, requestWrapper, authError);
		} finally {
			logDataSize(responseWrapper.toString(), RESPONSE);
		}
		
	}
	
	/**
	 * Send error response.
	 *
	 * @param response            the response
	 * @param responseWrapper 
	 * @param chain            the chain
	 * @param requestWrapper            the request wrapper
	 * @param authError 
	 * @return the char response wrapper
	 * @throws IOException             Signals that an I/O exception has occurred.
	 * @throws ServletException             the servlet exception
	 */
	private CharResponseWrapper sendErrorResponse(ServletResponse response,
			CharResponseWrapper responseWrapper, ResettableStreamHttpServletRequest requestWrapper, AuthError authError) throws IOException {
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		authResponseDTO.setErrors(Collections.singletonList(authError));
		Map<String, Object> requestMap = null;
		try {
			requestMap = getRequestBody(requestWrapper.getInputStream()); 
			requestWrapper.resetInputStream();
		} catch (IdAuthenticationAppException e) {
			mosipLogger.error(SESSION_ID, EVENT_FILTER, BASE_IDA_FILTER,
					"Cannot log time \n" + ExceptionUtils.getStackTrace(e));
		}
		requestWrapper.replaceData(EMPTY_JSON_OBJ_STRING.getBytes());
		String resTime = DateUtils.formatDate(
				DateUtils.parseToDate(DateUtils.getUTCCurrentDateTimeString(),
						env.getProperty(DATETIME_PATTERN), TimeZone.getTimeZone(ZoneOffset.UTC)),
				env.getProperty(DATETIME_PATTERN), TimeZone.getTimeZone(ZoneOffset.UTC));
		authResponseDTO.setStatus("N");
		if (Objects.nonNull(requestMap) && Objects.nonNull(requestMap.get(REQ_TIME))
				&& isDate((String) requestMap.get(REQ_TIME))) {
			ZoneId zone = ZonedDateTime
					.parse((CharSequence) requestMap.get(REQ_TIME), DateTimeFormatter.ISO_ZONED_DATE_TIME)
					.getZone();
			resTime = DateUtils.formatDate(
					DateUtils.parseToDate(resTime,
							env.getProperty(DATETIME_PATTERN), TimeZone.getTimeZone(zone)),
					env.getProperty(DATETIME_PATTERN), TimeZone.getTimeZone(zone));
		}
		
		if (Objects.nonNull(requestMap) && Objects.nonNull(requestMap.get(TRANSACTION_ID))) {
			authResponseDTO.setTransactionID((String) requestMap.get(TRANSACTION_ID));
		}
		authResponseDTO.setResponseTime(resTime);
		requestWrapper.resetInputStream();
		authResponseDTO.setVersion(getVersionFromUrl(requestWrapper));
		Map<String, Object> responseMap = mapper.convertValue(mapper.writeValueAsString(authResponseDTO), new TypeReference<Map<String, Object>>() {
		});
		Map<String, Object> resultMap = new LinkedHashMap<>();
		for(Map.Entry<String, Object> map : responseMap.entrySet()) {
			if(Objects.nonNull(map.getValue())) {
				resultMap.put(map.getKey(), map.getValue());
			}
		}		
		response.getWriter().write(mapper.writeValueAsString(resultMap));
		responseWrapper.setResponse(response);
		responseWrapper.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
		logTime(authResponseDTO.getResponseTime(), RESPONSE);
		return responseWrapper;
	}
	
	/**
	 * Log data size.
	 *
	 * @param data the data
	 * @param type the type
	 */
	private void logDataSize(String data, String type) {
		double size = ((double) data.length()) / 1024;
		mosipLogger.info(SESSION_ID, EVENT_FILTER, BASE_IDA_FILTER, "Data size of " + type + " : " + ((size > 0) ? size : 1) + " kb");
	}
	
	/**
	 * Log time.
	 *
	 * @param time the time
	 * @param type the type
	 */
	private void logTime(String time, String type) {
		mosipLogger.info(SESSION_ID, EVENT_FILTER, BASE_IDA_FILTER, type + " at : " + time);
		long duration = Duration.between(
				LocalDateTime.parse(requestTime, DateTimeFormatter.ofPattern(env.getProperty(DATETIME_PATTERN))),
				LocalDateTime.parse(time, DateTimeFormatter.ofPattern(env.getProperty(DATETIME_PATTERN))))
				.toMillis();
		mosipLogger.info(SESSION_ID, EVENT_FILTER, BASE_IDA_FILTER,
				"Time difference between request and response in millis:" + duration
						+ ".  Time difference between request and response in Seconds: " + ((double) duration / 1000));
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
	private Map<String, Object> getResponseBody(String output) throws IdAuthenticationAppException {
		try {
			return mapper.readValue(output, Map.class);
		} catch (IOException | ClassCastException e) {
			mosipLogger.error(SESSION_ID, EVENT_FILTER, BASE_IDA_FILTER, e.getMessage());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST, e);
		}
	}
	
	/**
	 * Consume request.
	 *
	 * @param requestWrapper the request wrapper
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	protected void consumeRequest(ResettableStreamHttpServletRequest requestWrapper) throws IdAuthenticationAppException {
		try {
			requestTime = DateUtils.formatDate(new Date(), env.getProperty(DATETIME_PATTERN));
			byte[] requestAsByte = null;
			requestAsByte = IOUtils.toByteArray(requestWrapper.getInputStream());
			logTime(requestTime, "request");
			logDataSize(new String(requestAsByte), "request");
		} catch (IOException e) {
			mosipLogger.error(SESSION_ID, EVENT_FILTER, BASE_IDA_FILTER, e.getMessage());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST, e);
		}
	}
	
	/**
	 * Map response.
	 *
	 * @param requestWrapper the request wrapper
	 * @param responseWrapper the response wrapper
	 * @return the string
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	protected String mapResponse(ResettableStreamHttpServletRequest requestWrapper, CharResponseWrapper responseWrapper) throws IdAuthenticationAppException {
		try {
			requestWrapper.resetInputStream();
			Map<String, Object> responseMap = setResponseParams(getRequestBody(requestWrapper.getInputStream()),
					getResponseBody(responseWrapper.toString()));
			responseMap.put("version", getVersionFromUrl(requestWrapper));
			String responseAsString = mapper.writeValueAsString(transformResponse(responseMap));
			logTime((String) getResponseBody(responseAsString).get(RES_TIME), RESPONSE);
			return responseAsString;
		} catch (IdAuthenticationAppException | IOException e) {
			mosipLogger.error(SESSION_ID, EVENT_FILTER, BASE_IDA_FILTER, e.getMessage());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST, e);
		}
	}

	protected String getVersionFromUrl(ResettableStreamHttpServletRequest requestWrapper) {
		String ver = null;
		if (requestWrapper instanceof HttpServletRequestWrapper) {
			String url = requestWrapper.getRequestURL().toString();
			String contextPath = requestWrapper.getContextPath();

			if ((Objects.nonNull(url) && !url.isEmpty()) && (Objects.nonNull(contextPath) && !contextPath.isEmpty())) {
				String[] splitedUrlByContext = url.split(contextPath);
				ver = Arrays.stream(splitedUrlByContext[1].split("/")).filter(s -> !s.isEmpty()).findFirst()
						.orElse(DEFAULT_VERSION).replaceAll(VER_REX, "");
			}
		}
		return ver;
	}
	
	/**
	 * Sets the response params.
	 *
	 * @param requestBody the request body
	 * @param responseBody the response body
	 * @return the map
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	protected Map<String, Object> setResponseParams(Map<String, Object> requestBody, Map<String, Object> responseBody) throws IdAuthenticationAppException {
		if (Objects.nonNull(requestBody) && Objects.nonNull(requestBody.get(TRANSACTION_ID))) {
			responseBody.replace(TRANSACTION_ID, requestBody.get(TRANSACTION_ID));
		}
		if (Objects.nonNull(requestBody) && Objects.nonNull(requestBody.get(REQ_TIME))
				&& isDate((String) requestBody.get(REQ_TIME))) {
			ZoneId zone = ZonedDateTime.parse((CharSequence) requestBody.get(REQ_TIME)).getZone();
			responseBody.replace(RES_TIME,
					DateUtils.formatDate(
							DateUtils.parseToDate((String) responseBody.get(RES_TIME),
									env.getProperty(DATETIME_PATTERN), TimeZone.getTimeZone(zone)),
							env.getProperty(DATETIME_PATTERN), TimeZone.getTimeZone(zone)));
			return responseBody;
		} else {
			return responseBody;
		}
	}
	
	/**
	 * Transform response.
	 *
	 * @param response the response
	 * @return the map
	 * @throws IdAuthenticationAppException 
	 */
	protected Map<String, Object> transformResponse(Map<String, Object> responseMap) throws IdAuthenticationAppException {
		return responseMap;
	}
	
	protected Map<String, Object> getRequestBody(InputStream inputStream) throws IdAuthenticationAppException {
		try {
			String reqStr = IOUtils.toString(inputStream, Charset.defaultCharset());
			return reqStr.isEmpty() ? null : mapper.readValue(reqStr,
					new TypeReference<Map<String, Object>>() {
					});
		} catch (IOException | ClassCastException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());
		}
	}
	
	/**
	 * To validate a string whether its a date or not.
	 *
	 * @param date the date
	 * @return true, if is date
	 */
	protected boolean isDate(String date) {
		try {
			DateUtils.parseToDate(date, env.getProperty(DATETIME_PATTERN));
			return true;
		} catch (ParseException | java.text.ParseException e) {
			mosipLogger.error("sessionId", BASE_IDA_FILTER, "validateDate", "\n" + ExceptionUtils.getStackTrace(e));
		}
		return false;
	}
	
	/**
	 * Authenticate request.
	 *
	 * @param requestWrapper the request wrapper
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	protected abstract void authenticateRequest(ResettableStreamHttpServletRequest requestWrapper) throws IdAuthenticationAppException;

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {}


}
