package io.mosip.authentication.service.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthError;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;

/**
 * The Base Auth Filter that does all necessary authentication/authorization
 * before allowing the request to the respective controllers.
 *
 * @author Loganathan Sekar
 * @param <REQUEST_DTO> the generic type
 * @param <RESPONSE_DTO> the generic type
 * @param <AUTH_INFO> the generic type
 */
public abstract class BaseAuthFilter<REQUEST_DTO, RESPONSE_DTO, AUTH_INFO> implements Filter {

	/** The Constant BASE_AUTH_FILTER. */
	private static final String BASE_AUTH_FILTER = "BaseAuthFilter";
	
	/** The Constant EVENT_FILTER. */
	private static final String EVENT_FILTER = "Event_filter";
	
	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "SessionId";

	/** The mosip logger. */
	private MosipLogger mosipLogger;

	/** The request time. */
	private Instant requestTime;

	/** The Constant EMPTY_JSON_OBJ_STRING. */
	private static final String EMPTY_JSON_OBJ_STRING = "{}";
	
	/** The Constant javaxValidator. */
	private static final Validator javaxValidator = Validation.buildDefaultValidatorFactory().getValidator();
	
	/** The mapper. */
	ObjectMapper mapper = new ObjectMapper();

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(filterConfig.getServletContext());
		MosipRollingFileAppender idaRollingFileAppender = context.getBean(MosipRollingFileAppender.class);
		mosipLogger = MosipLogfactory.getMosipDefaultRollingFileLogger(idaRollingFileAppender, this.getClass());
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		requestTime = Instant.now();
		
		mosipLogger.info(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER, "Request received at : " + requestTime);
		
		ResettableStreamHttpServletRequest requestWrapper = new ResettableStreamHttpServletRequest(
				(HttpServletRequest) request);
		
		try {
			authenticateRequest(requestWrapper);
			requestWrapper.resetInputStream();

			CharResponseWrapper responseWrapper = new CharResponseWrapper((HttpServletResponse) response);
			chain.doFilter(requestWrapper, responseWrapper);

			requestWrapper.resetInputStream();
			AuthResponseDTO wrappedResponse = mapper.readValue(responseWrapper.toString(), AuthResponseDTO.class);
			wrappedResponse.setTxnID(((AuthRequestDTO) getRequestBody(requestWrapper.getInputStream())).getTxnID());
			response.getWriter().write(mapper.writeValueAsString(wrappedResponse));
			
			logResponseTime(wrappedResponse.getResTime());

		} catch (IdAuthenticationAppException e) {
			CharResponseWrapper responseWrapper = new CharResponseWrapper((HttpServletResponse) response);
			requestWrapper.resetInputStream();
			chain.doFilter(requestWrapper, responseWrapper);
			AuthResponseDTO wrappedResponse = mapper.readValue(responseWrapper.toString(), AuthResponseDTO.class);
			sendAuthErrorResponse((HttpServletResponse) response, e, request.getContentType());
			logResponseTime(wrappedResponse.getResTime());
			mosipLogger.error(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER, "Error : " + e);
		}

	}

	/**
	 * Log response time.
	 *
	 * @param responseTime the response time
	 */
	private void logResponseTime(String responseTime) {
		mosipLogger.info(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER, "Response sent at : " + responseTime);
		DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_DATE_TIME;
		TemporalAccessor accessor = timeFormatter.parse(responseTime);
		mosipLogger.info(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER, "Time difference between request and response in millis: "
				+ Duration.between(requestTime, Instant.from(accessor)).toMillis());
	}

	/**
	 * Send auth error response.
	 *
	 * @param response the response
	 * @param e the e
	 * @param contentType the content type
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void sendAuthErrorResponse(HttpServletResponse response, IdAuthenticationAppException e, String contentType)
			throws IOException {
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		PrintWriter writer = response.getWriter();
		String errorMessageBody = getErrorMessageBody(e);
		response.setContentLength(errorMessageBody.length());
		response.setContentType(contentType);
		writer.write(errorMessageBody); // Here you can change the response
	}

	/**
	 * Gets the error message body.
	 *
	 * @param e the e
	 * @return the error message body
	 */
	private String getErrorMessageBody(IdAuthenticationAppException e) {
		try {
			return mapper.writeValueAsString(createResponseDTO(e));
		} catch (JsonProcessingException e1) {
			return EMPTY_JSON_OBJ_STRING;
		}
	}

	/**
	 * Creates the response DTO.
	 *
	 * @param e the e
	 * @return the response dto
	 */
	protected RESPONSE_DTO createResponseDTO(IdAuthenticationAppException e) {
		AuthResponseDTO authResp = new AuthResponseDTO();

		authResp.setStatus(false);

		List<String> errorMessages = e.getErrorTexts();

		List<AuthError> errors = errorMessages.parallelStream()
				.map(message -> new AuthError(e.getErrorCode(), (String) message)).collect(Collectors.toList());

		authResp.setErr(errors);

		authResp.setResTime(Instant.now().toString());

		mosipLogger.error("sessionId", "Response", e.getClass().getName(), authResp.toString());
		return (RESPONSE_DTO) authResp;
	}

	/**
	 * Authenticate request.
	 *
	 * @param servletRequest the servlet request
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	private void authenticateRequest(HttpServletRequest servletRequest)
			throws IOException, IdAuthenticationAppException {
		REQUEST_DTO requestDTO = getRequestBody(servletRequest.getInputStream());
		AUTH_INFO authInfo = getAuthInfo(requestDTO);
		// validateForRequiredfields(authInfo);
		authenticateRequest(authInfo);
	}

	/**
	 * Gets the request body.
	 *
	 * @param inputStream the input stream
	 * @return the request body
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	private REQUEST_DTO getRequestBody(InputStream inputStream) throws IOException, IdAuthenticationAppException {
		try {
			return mapper.readValue(inputStream, getRequestDTOClass());
		} catch (JsonParseException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());
		}
	}

	/**
	 * Validate for requiredfields.
	 *
	 * @param tspInfo the tsp info
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	private void validateForRequiredfields(AUTH_INFO tspInfo) throws IdAuthenticationAppException {
		Set<ConstraintViolation<AUTH_INFO>> violations = javaxValidator.validate(tspInfo, Default.class);
		if (!violations.isEmpty()) {
			String message = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("\n"));
			// FIXME check this exception type is correct
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(), new Exception(message));
		}
	}

	/**
	 * Gets the auth info.
	 *
	 * @param requestDTO the request DTO
	 * @return the auth info
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	protected abstract AUTH_INFO getAuthInfo(REQUEST_DTO requestDTO) throws IdAuthenticationAppException;

	/**
	 * Authenticate request.
	 *
	 * @param requestDTO the request DTO
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	private void authenticateRequest(AUTH_INFO requestDTO) throws IdAuthenticationAppException {
		// TODO authenticate/authorize TSP, validate UIN and VID. Throw exception upon
		// any validation failure
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {

	}

	/**
	 * Gets the request DTO class.
	 *
	 * @return the request DTO class
	 */
	protected abstract Class<REQUEST_DTO> getRequestDTOClass();

}
