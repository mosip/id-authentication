package io.mosip.authentication.common.service.filter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.exception.IdAuthExceptionHandler;
import io.mosip.authentication.common.service.integration.KeyManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.indauth.dto.AuthError;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.StringUtils;

/**
 * The Class BaseIDAFilter - The Base IDA Filter that does all necessary
 * authentication/authorization before allowing the request to the respective
 * controllers.
 * 
 * @author Sanjay Murali
 */
public abstract class BaseIDAFilter implements Filter {

	/** The Constant ERRORS. */
	private static final String ERRORS = "errors";

	/** The Constant VERSION. */
	private static final String VERSION = "version";

	/** The Constant RES_TIME. */
	private static final String RES_TIME = "responseTime";

	/** The Constant BASE_IDA_FILTER. */
	private static final String BASE_IDA_FILTER = "BaseIDAFilter";

	/** The Constant EVENT_FILTER. */
	private static final String EVENT_FILTER = "Event_filter";

	/** The Constant EMPTY_JSON_OBJ_STRING. */
	private static final String EMPTY_JSON_OBJ_STRING = "{";

	/** The Constant VERSION_REGEX. */
	private static final String VERSION_REGEX = "\\d\\.\\d(\\.\\d)?";

	/** The Constant VERSION_PATTERN. */
	private static final Pattern VERSION_PATTERN = Pattern.compile(VERSION_REGEX);

	/** The env. */
	protected Environment env;

	/** The mapper. */
	protected ObjectMapper mapper;

	/** The key manager. */
	protected KeyManager keyManager;

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(BaseIDAFilter.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		WebApplicationContext context = WebApplicationContextUtils
				.getRequiredWebApplicationContext(filterConfig.getServletContext());
		env = context.getBean(Environment.class);
		mapper = context.getBean(ObjectMapper.class);
		keyManager = context.getBean(KeyManager.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 * javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String reqUrl = ((HttpServletRequest) request).getRequestURL().toString();
		if (reqUrl.contains("swagger") || reqUrl.contains("api-docs") || reqUrl.contains("actuator")) {
			chain.doFilter(request, response);
			return;
		}

		LocalDateTime requestTime = DateUtils.getUTCCurrentDateTime();
		mosipLogger.info(IdAuthCommonConstants.SESSION_ID, EVENT_FILTER, BASE_IDA_FILTER,
				IdAuthCommonConstants.REQUEST + " at : " + requestTime);

		ResettableStreamHttpServletRequest requestWrapper = new ResettableStreamHttpServletRequest(
				(HttpServletRequest) request);
		CharResponseWrapper responseWrapper = new CharResponseWrapper((HttpServletResponse) response) {

			@Override
			public void flushBuffer() throws IOException {
				// Avoiding flush and commit while data validation exception handling to set
				// response header(response-signature) later in the filter.
				// Positive response does not invoke this
				// super.flushBuffer();
			}
		};
		try {
			Map<String, Object> requestBody = getRequestBody(requestWrapper.getInputStream());
			if (requestBody == null) {
				chain.doFilter(requestWrapper, responseWrapper);
				String responseAsString = mapResponse(requestWrapper, responseWrapper, requestTime);
				response.getWriter().write(responseAsString);
				return;
			}

			requestWrapper.resetInputStream();
			consumeRequest(requestWrapper, requestBody);
			chain.doFilter(requestWrapper, responseWrapper);
			String responseAsString = mapResponse(requestWrapper, responseWrapper, requestTime);
			response.getWriter().write(responseAsString);
		} catch (IdAuthenticationAppException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, EVENT_FILTER, BASE_IDA_FILTER,
					"\n" + ExceptionUtils.getStackTrace(e));
			requestWrapper.resetInputStream();
			sendErrorResponse(response, responseWrapper, requestWrapper, requestTime, e);
		} finally {
			logDataSize(responseWrapper.toString(), IdAuthCommonConstants.RESPONSE);
		}

	}

	/**
	 * sendErrorResponse method is used to construct error response when any
	 * exception is thrown while deciphering or validating the authenticating
	 * partner .
	 *
	 * @param response        where the response is written
	 * @param responseWrapper {@link CharResponseWrapper}
	 * @param requestWrapper  {@link ResettableStreamHttpServletRequest}
	 * @param requestTime     the request time
	 * @param ex              the ex
	 * @return the charResponseWrapper which consists of the response
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	private CharResponseWrapper sendErrorResponse(ServletResponse response, CharResponseWrapper responseWrapper,
			ResettableStreamHttpServletRequest requestWrapper, Temporal requestTime, IdAuthenticationAppException ex)
			throws IOException {
		Object responseObj = IdAuthExceptionHandler.buildExceptionResponse(ex, requestWrapper);
		Map<String, Object> responseMap = mapper.convertValue(responseObj, new TypeReference<Map<String, Object>>() {
		});

		Map<String, Object> requestMap = null;
		try {
			responseMap = transformResponse(responseMap);
			requestMap = getRequestBody(requestWrapper.getInputStream());
			requestWrapper.resetInputStream();
		} catch (IdAuthenticationAppException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, EVENT_FILTER, BASE_IDA_FILTER,
					"Cannot log time \n" + ExceptionUtils.getStackTrace(e));
		}

		requestWrapper.replaceData(EMPTY_JSON_OBJ_STRING.getBytes());
		String resTime = DateUtils.formatDate(DateUtils.parseToDate(DateUtils.getUTCCurrentDateTimeString(),
				env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN), TimeZone.getTimeZone(ZoneOffset.UTC)),
				env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN), TimeZone.getTimeZone(ZoneOffset.UTC));
		if (Objects.nonNull(requestMap) && Objects.nonNull(requestMap.get(IdAuthCommonConstants.REQ_TIME))
				&& isDate((String) requestMap.get(IdAuthCommonConstants.REQ_TIME))) {
			ZoneId zone = ZonedDateTime.parse((CharSequence) requestMap.get(IdAuthCommonConstants.REQ_TIME),
					DateTimeFormatter.ISO_ZONED_DATE_TIME).getZone();
			resTime = DateUtils.formatDate(
					DateUtils.parseToDate(resTime, env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN),
							TimeZone.getTimeZone(zone)),
					env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN), TimeZone.getTimeZone(zone));
		}

		if (Objects.nonNull(requestMap) && Objects.nonNull(requestMap.get(IdAuthCommonConstants.TRANSACTION_ID))) {
			responseMap.replace(IdAuthCommonConstants.TRANSACTION_ID,
					(String) requestMap.get(IdAuthCommonConstants.TRANSACTION_ID));
		}

		responseMap.replace(RES_TIME, resTime);
		requestWrapper.resetInputStream();
		responseMap.replace(IdAuthCommonConstants.ID,
				env.getProperty(fetchId(requestWrapper, IdAuthConfigKeyConstants.MOSIP_IDA_API_ID)));
		requestWrapper.resetInputStream();
		responseMap.replace(VERSION,
				env.getProperty(fetchId(requestWrapper, IdAuthConfigKeyConstants.MOSIP_IDA_API_VERSION)));

		try {
			responseWrapper.setHeader(env.getProperty(IdAuthConfigKeyConstants.SIGN_RESPONSE),
					keyManager.signResponse(mapper.writeValueAsString(responseMap)));
		} catch (IdAuthenticationAppException e) {
			if (responseMap.containsKey(IdAuthCommonConstants.ERRORS)
					&& responseMap.get(IdAuthCommonConstants.ERRORS) instanceof List) {
				List<Object> errors = (List<Object>) responseMap.get(IdAuthCommonConstants.ERRORS);
				boolean hasUnableToProcessError = errors.stream().filter(obj -> obj instanceof Map)
						.map(obj -> (Map<String, Object>) obj)
						.anyMatch(map -> map.containsKey(IdAuthCommonConstants.ERROR_CODE)
								&& map.get(IdAuthCommonConstants.ERROR_CODE)
										.equals(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode()));

				if (!hasUnableToProcessError) {
					AuthError authError = new AuthError(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
							IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage());
					String errStr = mapper.writeValueAsString(authError);
					errors.add(mapper.readValue(errStr.getBytes(), Map.class));
				}
			}
		}

		String responseAsString = mapper.writeValueAsString(responseMap);
		response.getWriter().write(responseAsString);
		responseWrapper.setResponse(response);
		responseWrapper.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
		logTime(resTime, IdAuthCommonConstants.RESPONSE, requestTime);
		return responseWrapper;
	}

	/**
	 * removeNullOrEmptyFieldsInResponse method is used to remove all the empty and
	 * null values present in the response
	 *
	 * @param responseMap the response got after the authentication
	 * @return the map consists of filter response without null or empty
	 */
	protected Map<String, Object> removeNullOrEmptyFieldsInResponse(Map<String, Object> responseMap) {
		return responseMap.entrySet().stream().filter(map -> Objects.nonNull(map.getValue()))
				.filter(entry -> !(entry.getValue() instanceof List) || !((List<?>) entry.getValue()).isEmpty())
				.collect(Collectors.toMap(Entry<String, Object>::getKey, Entry<String, Object>::getValue,
						(map1, map2) -> map1, LinkedHashMap<String, Object>::new));
	}

	/**
	 * logDataSize method is used to log the size of the request and response data
	 *
	 * @param data the request or response boby
	 * @param type wither request or response
	 */
	private void logDataSize(String data, String type) {
		double size = ((double) data.length()) / 1024;
		mosipLogger.info(IdAuthCommonConstants.SESSION_ID, EVENT_FILTER, BASE_IDA_FILTER,
				"Data size of " + type + " : " + ((size > 0) ? size : 1) + " kb");
	}

	/**
	 * logTime method is used to log the response time between the request and
	 * response processed
	 *
	 * @param time        the response time
	 * @param type        the type is response
	 * @param requestTime
	 */
	private void logTime(String time, String type, Temporal requestTime) {

		if (time == null || time.isEmpty()) {
			time = DateUtils.formatDate(DateUtils.parseToDate(DateUtils.getUTCCurrentDateTimeString(),
					env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN), TimeZone.getTimeZone(ZoneOffset.UTC)),
					env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN), TimeZone.getTimeZone(ZoneOffset.UTC));
		}
		mosipLogger.info(IdAuthCommonConstants.SESSION_ID, EVENT_FILTER, BASE_IDA_FILTER, type + " at : " + time);
		long duration = Duration
				.between(requestTime,
						LocalDateTime.parse(time,
								DateTimeFormatter
										.ofPattern(env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN))))
				.toMillis();
		mosipLogger.info(IdAuthCommonConstants.SESSION_ID, EVENT_FILTER, BASE_IDA_FILTER,
				"Time difference between request and response in millis:" + duration
						+ ".  Time difference between request and response in Seconds: " + ((double) duration / 1000));
	}

	/**
	 * getResponseBody method used to retrieve the response body
	 *
	 * @param responseBody the output
	 * @return the response body
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> getResponseBody(String responseBody) throws IdAuthenticationAppException {
		try {
			return mapper.readValue(responseBody, Map.class);
		} catch (IOException | ClassCastException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, EVENT_FILTER, BASE_IDA_FILTER, e.getMessage());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	/**
	 * consumeRequest method is used to manipulate the request where the request is
	 * first reached and along this all validation are done further after successful
	 * decipher.
	 *
	 * @param requestWrapper {@link ResettableStreamHttpServletRequest}
	 * @param requestBody    the request body
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	protected void consumeRequest(ResettableStreamHttpServletRequest requestWrapper, Map<String, Object> requestBody)
			throws IdAuthenticationAppException {
		try {
			byte[] requestAsByte = IOUtils.toByteArray(requestWrapper.getInputStream());
			logDataSize(new String(requestAsByte), IdAuthCommonConstants.REQUEST);
			requestWrapper.resetInputStream();
			validateRequest(requestWrapper, requestBody);
		} catch (IOException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, EVENT_FILTER, BASE_IDA_FILTER, e.getMessage());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	/**
	 * validateRequest method is used to validate the version and the ID passed for
	 * the each request
	 *
	 * @param requestWrapper {@link ResettableStreamHttpServletRequest}
	 * @param requestBody    the request body is the request body fetched from input
	 *                       stream
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	protected void validateRequest(ResettableStreamHttpServletRequest requestWrapper, Map<String, Object> requestBody)
			throws IdAuthenticationAppException {

		String id = fetchId(requestWrapper, IdAuthConfigKeyConstants.MOSIP_IDA_API_ID);
		requestWrapper.resetInputStream();
		if (Objects.nonNull(requestBody) && !requestBody.isEmpty()) {
			validateId(requestBody, id);
			validateVersion(requestBody);
		}

	}

	/**
	 * fetchId used to fetch and determine the id of request
	 *
	 * @param requestWrapper the {@link ResettableStreamHttpServletRequest}
	 * @return the string
	 */

	protected String fetchId(ResettableStreamHttpServletRequest requestWrapper, String attribute) {
		String id = null;
		String contextPath = requestWrapper.getContextPath();
		if (!StringUtils.isEmpty(contextPath)) {
			String[] splitedContext = contextPath.split("/");
			id = attribute + splitedContext[splitedContext.length - 1];
		}
		return id;

	}

	/**
	 * validateVersion method is used to validate the version present in the request
	 * body and URL
	 *
	 * @param requestBody the request body is the request body fetched from input
	 *                    stream
	 * @param id          the id present in the request in the request
	 * @param verFromUrl  the version from URL
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	private void validateVersion(Map<String, Object> requestBody) throws IdAuthenticationAppException {
		String verFromRequest = requestBody.containsKey(VERSION) ? (String) requestBody.get(VERSION) : null;
		if (StringUtils.isEmpty(verFromRequest)) {
			handleException(VERSION, false);
		}
		if (!VERSION_PATTERN.matcher(verFromRequest).matches()) {
			handleException(VERSION, true);
		}
	}

	/**
	 * validateId is used to validate the id present in the request
	 *
	 * @param requestBody the request body
	 * @param id          the id
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	protected void validateId(Map<String, Object> requestBody, String id) throws IdAuthenticationAppException {
		String idFromRequest = requestBody.containsKey(IdAuthCommonConstants.ID)
				? (String) requestBody.get(IdAuthCommonConstants.ID)
				: null;
		String property = env.getProperty(id);
		if (StringUtils.isEmpty(idFromRequest)) {
			handleException(IdAuthCommonConstants.ID, false);
		}
		if (Objects.nonNull(property) && !property.equals(idFromRequest)) {
			handleException(IdAuthCommonConstants.ID, true);
		}
	}

	/**
	 * exceptionHandling used to handle the exception when validation of version and
	 * ID fails
	 *
	 * @param type the type is either ID or Version
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	private void handleException(String type, boolean isPresent) throws IdAuthenticationAppException {
		if (!isPresent) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, EVENT_FILTER, BASE_IDA_FILTER,
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
			throw new IdAuthenticationAppException(
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), type));
		} else {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, EVENT_FILTER, BASE_IDA_FILTER,
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
			throw new IdAuthenticationAppException(
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), type));
		}
	}

	/**
	 * mapResponse method is used to construct the response for the successful
	 * authentication.
	 *
	 * @param requestWrapper  {@link ResettableStreamHttpServletRequest}
	 * @param responseWrapper {@link CharResponseWrapper}
	 * @param requestTime     the request time
	 * @return the string response finally built
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@SuppressWarnings("unchecked")
	protected String mapResponse(ResettableStreamHttpServletRequest requestWrapper, CharResponseWrapper responseWrapper,
			Temporal requestTime) throws IdAuthenticationAppException {
		try {
			requestWrapper.resetInputStream();
			Map<String, Object> requestBody = getRequestBody(requestWrapper.getInputStream());
			Map<String, Object> responseMap = setResponseParams(requestBody,
					getResponseBody(responseWrapper.toString()));
			requestWrapper.resetInputStream();
			responseMap.replace(VERSION,
					env.getProperty(fetchId(requestWrapper, IdAuthConfigKeyConstants.MOSIP_IDA_API_VERSION)));
			requestWrapper.resetInputStream();
			responseMap.put(IdAuthCommonConstants.ID,
					env.getProperty(fetchId(requestWrapper, IdAuthConfigKeyConstants.MOSIP_IDA_API_ID)));
			if (responseMap.containsKey(ERRORS)) {
				List<AuthError> errorList = responseMap.get(ERRORS) instanceof List
						? (List<AuthError>) responseMap.get(ERRORS)
						: Collections.emptyList();
				if (errorList.isEmpty()) {
					responseMap.put(ERRORS, null);
				}
			}
			String responseAsString = mapper.writeValueAsString(transformResponse(responseMap));
			responseWrapper.setHeader(env.getProperty(IdAuthConfigKeyConstants.SIGN_RESPONSE),
					keyManager.signResponse(responseAsString));
			logTime((String) getResponseBody(responseAsString).get(RES_TIME), IdAuthCommonConstants.RESPONSE,
					requestTime);
			return responseAsString;
		} catch (IdAuthenticationAppException | IOException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, EVENT_FILTER, BASE_IDA_FILTER, e.getMessage());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	/**
	 * setResponseParams method is set the transaction ID and response time based on
	 * the request time zone
	 *
	 * @param requestBody  the request body
	 * @param responseBody the response body
	 * @return the map
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	protected Map<String, Object> setResponseParams(Map<String, Object> requestBody, Map<String, Object> responseBody)
			throws IdAuthenticationAppException {
		if (Objects.nonNull(requestBody) && Objects.nonNull(requestBody.get(IdAuthCommonConstants.TRANSACTION_ID))) {
			responseBody.replace(IdAuthCommonConstants.TRANSACTION_ID,
					requestBody.get(IdAuthCommonConstants.TRANSACTION_ID));
		}

		if (Objects.nonNull(requestBody) && Objects.nonNull(requestBody.get(IdAuthCommonConstants.REQ_TIME))
				&& isDate((String) requestBody.get(IdAuthCommonConstants.REQ_TIME))) {
			ZoneId zone = ZonedDateTime.parse((CharSequence) requestBody.get(IdAuthCommonConstants.REQ_TIME)).getZone();

			String responseTime = Objects.nonNull(responseBody.get(RES_TIME)) ? (String) responseBody.get(RES_TIME)
					: DateUtils.getUTCCurrentDateTimeString();
			responseBody.remove("responsetime");// Handled for forbidden error scenario
			responseBody.remove("metadata");// Handled for forbidden error scenario
			responseBody.put(RES_TIME,
					DateUtils.formatDate(DateUtils.parseToDate(responseTime,
							env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN), TimeZone.getTimeZone(zone)),
							env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN), TimeZone.getTimeZone(zone)));
			return responseBody;
		} else {
			return responseBody;
		}
	}

	/**
	 * transformResponse used to manipulate the response if any.
	 *
	 * @param responseMap the response map
	 * @return the map
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	protected Map<String, Object> transformResponse(Map<String, Object> responseMap)
			throws IdAuthenticationAppException {
		return responseMap;
	}

	/**
	 * getRequestBody used to get the request body from the raw input stream
	 *
	 * @param requestBody {@link ResettableStreamHttpServletRequest} get request as
	 *                    input stream
	 * @return the request body
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	protected Map<String, Object> getRequestBody(InputStream requestBody) throws IdAuthenticationAppException {
		try {
			String reqStr = IOUtils.toString(requestBody, Charset.defaultCharset());
			// requestBody empty for service like VID
			return reqStr.isEmpty() ? null : mapper.readValue(reqStr, new TypeReference<Map<String, Object>>() {
			});
		} catch (IOException | ClassCastException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
					IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage(), e);
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
			DateUtils.parseToDate(date, env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN));
			return true;
		} catch (ParseException e) {
			mosipLogger.error("sessionId", BASE_IDA_FILTER, "validateDate", "\n" + ExceptionUtils.getStackTrace(e));
		}
		return false;
	}

	/**
	 * authenticateRequest method used to validate the JSON signature pay load and
	 * the certificate
	 *
	 * @param requestWrapper {@link ResettableStreamHttpServletRequest}
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	protected abstract void authenticateRequest(ResettableStreamHttpServletRequest requestWrapper)
			throws IdAuthenticationAppException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
	}

}
