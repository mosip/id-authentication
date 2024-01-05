package io.mosip.authentication.common.service.filter;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.ERRORS;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.METADATA;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.SIGNATURE;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.VERSION;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
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
import org.springframework.http.MediaType;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.manager.IdAuthFraudAnalysisEventManager;
import io.mosip.authentication.common.service.exception.IdAuthExceptionHandler;
import io.mosip.authentication.common.service.integration.KeyManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.common.service.util.IdaRequestResponsConsumerUtil;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBaseException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthError;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.partner.dto.MispPolicyDTO;
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

	/** The Constant BASE_IDA_FILTER. */
	private static final String BASE_IDA_FILTER = "BaseIDAFilter";

	/** The Constant EVENT_FILTER. */
	private static final String EVENT_FILTER = "Event_filter";

	/** The Constant VERSION_REGEX. */
	private static final String VERSION_REGEX = "\\d\\.\\d(\\.\\d)?";

	/** The Constant VERSION_PATTERN. */
	private static final Pattern VERSION_PATTERN = Pattern.compile(VERSION_REGEX);

	/** The env. */
	protected EnvUtil env;

	/** The mapper. */
	protected ObjectMapper mapper;

	/** The key manager. */
	protected KeyManager keyManager;

	private IdAuthFraudAnalysisEventManager fraudEventManager;
	
	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(BaseIDAFilter.class);
	
	private IdaRequestResponsConsumerUtil requestResponsConsumerUtil;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		WebApplicationContext context = WebApplicationContextUtils
				.getRequiredWebApplicationContext(filterConfig.getServletContext());
		env = context.getBean(EnvUtil.class);
		mapper = context.getBean(ObjectMapper.class);
		keyManager = context.getBean(KeyManager.class);
		fraudEventManager = context.getBean(IdAuthFraudAnalysisEventManager.class);
		requestResponsConsumerUtil = context.getBean(IdaRequestResponsConsumerUtil.class);
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
		if (reqUrl.contains("swagger") || reqUrl.contains("api-docs") || reqUrl.contains("actuator") || reqUrl.contains("callback")) {
			chain.doFilter(request, response);
			return;
		}
		mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, EVENT_FILTER, BASE_IDA_FILTER,
				"Request URL: " + reqUrl);
		
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
		
		Map<String, Object> requestBody = null;
		try {
			requestBody = getRequestBody(requestWrapper.getInputStream());
			if (requestBody == null) {
				addIdAndVersionToRequestMetadata(requestWrapper);
				chain.doFilter(requestWrapper, responseWrapper);
				String responseAsString = responseWrapper.toString();
				consumeResponse(requestWrapper, responseWrapper, responseAsString, requestTime, requestBody);
				response.getWriter().write(responseAsString);
				return;
			}
			
			addIdAndVersionToRequestMetadata(requestWrapper);
			addTransactionIdToRequestMetadata(requestWrapper,requestBody);
			
			requestWrapper.resetInputStream();
			consumeRequest(requestWrapper, requestBody);
			requestWrapper.resetInputStream();
			chain.doFilter(requestWrapper, responseWrapper);
			String responseAsString = responseWrapper.toString();
			consumeResponse(requestWrapper, responseWrapper, responseAsString, requestTime, requestBody);
			response.getWriter().write(responseAsString);
		} catch (IdAuthenticationAppException  e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, EVENT_FILTER, BASE_IDA_FILTER,
					"\n" + ExceptionUtils.getStackTrace(e));
			if(requestBody != null && e.getErrorCode().equals(IdAuthenticationErrorConstants.DSIGN_FALIED.getErrorCode())) {
				String errorMessage = e.getErrorText();
				fraudEventManager.analyseDigitalSignatureFailure(requestWrapper.getRequestURI(), requestBody, errorMessage);
			}
			requestWrapper.resetInputStream();
			sendErrorResponse(response, responseWrapper, requestWrapper, requestTime, e, requestBody);
		} finally {
			logDataSize(responseWrapper.toString(), IdAuthCommonConstants.RESPONSE);
		}
		

	}

	private void addIdAndVersionToRequestMetadata(ResettableStreamHttpServletRequest requestWrapper) {
		requestWrapper.putMetadata(VERSION,
				env.getProperty(fetchId(requestWrapper, IdAuthConfigKeyConstants.MOSIP_IDA_API_VERSION)));
		requestWrapper.resetInputStream();
		requestWrapper.putMetadata(IdAuthCommonConstants.ID,
				env.getProperty(fetchId(requestWrapper, IdAuthConfigKeyConstants.MOSIP_IDA_API_ID)));
	}
	
	private void addTransactionIdToRequestMetadata(ResettableStreamHttpServletRequest requestWrapper, Map<String, Object> requestBody) {
		requestWrapper.putMetadata(IdAuthCommonConstants.TRANSACTION_ID,
				requestBody.get(IdAuthCommonConstants.TRANSACTION_ID));
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
	 * @param requestBody 
	 * @return the charResponseWrapper which consists of the response
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private CharResponseWrapper sendErrorResponse(ServletResponse response, CharResponseWrapper responseWrapper,
			ResettableStreamHttpServletRequest requestWrapper, Temporal requestTime, IdAuthenticationBaseException ex, Map<String, Object> requestMap)
			throws IOException {
		IdaRequestResponsConsumerUtil.setIdVersionToObjectWithMetadata(requestWrapper, ex);
		List<AuthError> errors = IdAuthExceptionHandler.getAuthErrors(ex);
		IdAuthenticationBaseException exception = ex;
		//If the exception is not a validation exception we wrap it around unable to process exception
		boolean hasUnableToProcessError = errors.stream()
				.anyMatch(err -> err.getErrorCode()
								.equals(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode()));
		if(!hasUnableToProcessError) {
			exception = new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
					IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage(), ex);
		}
		
		exception.putMetadata(IdAuthCommonConstants.TRANSACTION_ID, requestMap.get(IdAuthCommonConstants.TRANSACTION_ID));
		Object responseObj = IdAuthExceptionHandler.buildExceptionResponse(exception, requestWrapper);
		String responseAsString = mapper.writeValueAsString(responseObj);
		String responseSignature = null;
		
		try {
			if (isSigningRequired()) {
				responseSignature = keyManager.signResponse(responseAsString);
				responseWrapper.setHeader(EnvUtil.getSignResponse(),
						responseSignature);
			}
		} catch (IdAuthenticationAppException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, EVENT_FILTER, BASE_IDA_FILTER,
					"\n" + ExceptionUtils.getStackTrace(e));
		}
		
		//Try storing auth transaction
		if(needStoreAuthTransaction()) {
			try {
				String requestSignature = requestWrapper.getHeader(SIGNATURE);
				requestResponsConsumerUtil.storeAuthTransaction((Map<String, Object>) requestMap.get(METADATA), requestSignature, responseSignature);
			} catch (IdAuthenticationAppException e) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, EVENT_FILTER, BASE_IDA_FILTER,
						"\n" + ExceptionUtils.getStackTrace(e));
			}
		}
		
		//Try storing anonymous profile
		if(needStoreAnonymousProfile()) {
			boolean status = false;
			requestResponsConsumerUtil.storeAnonymousProfile(requestMap, (Map<String, Object>) requestMap.get(METADATA), exception.getMetadata(), status, errors);
		}

		response.getWriter().write(responseAsString);
		responseWrapper.setResponse(response);
		responseWrapper.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

		logTime(null, IdAuthCommonConstants.RESPONSE, requestTime);
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
	 * @param timeInTheAllowedPattern        the response time
	 * @param type        the type is response
	 * @param actualRequestTime
	 */
	private void logTime(String timeInTheAllowedPattern, String type, Temporal actualRequestTime) {
		String dateTimePattern = EnvUtil.getDateTimePattern();
		if (timeInTheAllowedPattern == null || timeInTheAllowedPattern.isEmpty()) {
			timeInTheAllowedPattern = IdaRequestResponsConsumerUtil.getResponseTime(null, dateTimePattern);
		}
		mosipLogger.info(IdAuthCommonConstants.SESSION_ID, EVENT_FILTER, BASE_IDA_FILTER, type + " at : " + timeInTheAllowedPattern);
		long duration = Duration
				.between(actualRequestTime,
						LocalDateTime.parse(timeInTheAllowedPattern,
								DateTimeFormatter
										.ofPattern(dateTimePattern)))
				.toMillis();
		mosipLogger.info(IdAuthCommonConstants.SESSION_ID, EVENT_FILTER, BASE_IDA_FILTER,
				"Time difference between request and response in millis:" + duration
						+ ".  Time difference between request and response in Seconds: " + ((double) duration / 1000));
	}
	
	protected boolean needStoreAuthTransaction() {
		return false;
	}
	
	protected boolean needStoreAnonymousProfile() {
		return false;
	}

	/**
	 * consumeRequest method is used to manipulate the request where the request is
	 * first reached and along this all validation are done further after successful
	 * decipher.
	 *
	 * @param requestWrapper {@link ResettableStreamHttpServletRequest}
	 * @param requestBody    the request body
	 * @throws IdAuthenticationAppException the id authentication app exception
	 * @throws IdAuthenticationBusinessException 
	 */
	protected void consumeRequest(ResettableStreamHttpServletRequest requestWrapper, Map<String, Object> requestBody)
			throws IdAuthenticationAppException {
		try {
			byte[] requestAsByte = IOUtils.toByteArray(requestWrapper.getInputStream());
			logDataSize(new String(requestAsByte), IdAuthCommonConstants.REQUEST);
			requestWrapper.resetInputStream();
			validateRequest(requestWrapper, requestBody);
		} catch (IOException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, EVENT_FILTER, BASE_IDA_FILTER, ExceptionUtils.getStackTrace(e));
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

	protected abstract String fetchId(ResettableStreamHttpServletRequest requestWrapper, String attribute);

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
	 * @param actualRequestTime     the request time
	 * @param requestBody 
	 * @return the string response finally built
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	protected String consumeResponse(ResettableStreamHttpServletRequest requestWrapper, CharResponseWrapper responseWrapper, String responseAsString,
			Temporal actualRequestTime, Map<String, Object> requestBody) throws IdAuthenticationAppException {
		try {
			// The metadata from requestWrapper is actually response metadata which is
			// mutated from with the controller so that the values can be obtained here from that.
			// This is mainly used to pass the Auth transaction details and Identity Infos
			// for storing auth transaction and anonymous profile.
			Map<String, Object> responseMetadata = requestWrapper.getMetadata();
			requestWrapper.resetInputStream();
			String requestSignature = requestWrapper.getHeader(SIGNATURE);
			String responseSignature = null;
			if(isSigningRequired()) {
				if (Objects.isNull(responseAsString) || responseAsString.trim().length() == 0) {
					mosipLogger.error(IdAuthCommonConstants.SESSION_ID, EVENT_FILTER, BASE_IDA_FILTER,
									" Response String is null or empty for response (JWT) signing");
					throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(), 
								IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage());
				}
				responseSignature = keyManager.signResponse(responseAsString);
				responseWrapper.setHeader(EnvUtil.getSignResponse(), responseSignature);
			}
			if(needStoreAuthTransaction()) {
				requestResponsConsumerUtil.storeAuthTransaction(responseMetadata, requestSignature, responseSignature);
			}
			if(needStoreAnonymousProfile()) {
				if (requestBody != null) {
					boolean status = Boolean.valueOf(String.valueOf(responseMetadata.get(IdAuthCommonConstants.STATUS)));
					List<AuthError> errors =  responseMetadata.get(ERRORS) instanceof List ? (List<AuthError>) responseMetadata.get(ERRORS) : List.of();
					requestResponsConsumerUtil.storeAnonymousProfile(requestBody, (Map<String, Object>) requestBody.get(METADATA),
							responseMetadata, status, errors);
				}
			}
			
			Object inputRequestTime = requestBody == null ? null : requestBody.get(IdAuthCommonConstants.REQ_TIME);
			String inputReqTimeStr = inputRequestTime instanceof String? (String) inputRequestTime : null;
			logTime(inputReqTimeStr, IdAuthCommonConstants.RESPONSE, actualRequestTime);
			return responseAsString;
		} catch (IdAuthenticationAppException e ) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, EVENT_FILTER, BASE_IDA_FILTER, e.getMessage());
			return responseAsString;
		}
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
			DateUtils.parseToDate(date, EnvUtil.getDateTimePattern());
			return true;
		} catch (ParseException e) {
			mosipLogger.warn("sessionId", BASE_IDA_FILTER, "validateDate", "\n" + ExceptionUtils.getStackTrace(e));
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
	
	protected abstract boolean isSigningRequired();
	
	protected abstract boolean isSignatureVerificationRequired();
	
	protected abstract boolean isThumbprintValidationRequired();
	
	protected abstract boolean isTrustValidationRequired();

	protected abstract boolean isMispPolicyValidationRequired();

	protected abstract boolean isCertificateValidationRequired();

	protected abstract boolean isAMRValidationRequired();

	protected abstract void checkAllowedAMRForKBT(Map<String, Object> requestBody, Set<String> allowedAMRs) throws IdAuthenticationAppException;

	protected abstract void checkMispPolicyAllowed(MispPolicyDTO mispPolicy) throws IdAuthenticationAppException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
	}

}
