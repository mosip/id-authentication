package io.mosip.authentication.service.filter;

import java.io.IOException;
import java.io.InputStream;
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
import javax.servlet.ServletOutputStream;
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
 */
public abstract class BaseAuthFilter<REQUEST_DTO, RESPONSE_DTO, AUTH_INFO> implements Filter {

	private static final String BASE_AUTH_FILTER = "BaseAuthFilter";
	private static final String EVENT_FILTER = "Event_filter";
	private static final String SESSION_ID = "SessionId";

	private MosipLogger mosipLogger;

	private Instant requestTime;

	private static final String EMPTY_JSON_OBJ_STRING = "{}";
	private static final Validator javaxValidator = Validation.buildDefaultValidatorFactory().getValidator();
	ObjectMapper mapper = new ObjectMapper();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(filterConfig.getServletContext());
		MosipRollingFileAppender idaRollingFileAppender = context.getBean(MosipRollingFileAppender.class);
		mosipLogger = MosipLogfactory.getMosipDefaultRollingFileLogger(idaRollingFileAppender, this.getClass());
	}

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

			AuthResponseDTO wrappedResponse = mapper.readValue(responseWrapper.toString(), AuthResponseDTO.class);
			logResponseTime(wrappedResponse.getResTime());

		} catch (IdAuthenticationAppException e) {
			CharResponseWrapper responseWrapper = new CharResponseWrapper((HttpServletResponse) response);
			requestWrapper.resetInputStream();
			responseWrapper.clear();
			chain.doFilter(requestWrapper, responseWrapper);
			AuthResponseDTO wrappedResponse = mapper.readValue(responseWrapper.toString(), AuthResponseDTO.class);
			sendAuthErrorResponse((HttpServletResponse) responseWrapper, e, request.getContentType());
			logResponseTime(wrappedResponse.getResTime());
			mosipLogger.error(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER, "Error : " + e);
		}

	}

	private void logResponseTime(String responseTime) {
		mosipLogger.info(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER, "Response sent at : " + responseTime);
		DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_DATE_TIME;
		TemporalAccessor accessor = timeFormatter.parse(responseTime);
		mosipLogger.info(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER, "Time difference between request and response : "
				+ Duration.between(requestTime, Instant.from(accessor)));
	}

	private void sendAuthErrorResponse(HttpServletResponse response, IdAuthenticationAppException e, String contentType)
			throws IOException {
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		ServletOutputStream out = response.getOutputStream();
		String errorMessageBody = getErrorMessageBody(e);
		response.setContentLength(errorMessageBody.length());
		response.setContentType(contentType);
		out.write(errorMessageBody.getBytes()); // Here you can change the response
	}

	private String getErrorMessageBody(IdAuthenticationAppException e) {
		try {
			return mapper.writeValueAsString(createResponseDTO(e));
		} catch (JsonProcessingException e1) {
			return EMPTY_JSON_OBJ_STRING;
		}
	}

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

	private void authenticateRequest(HttpServletRequest servletRequest)
			throws IOException, IdAuthenticationAppException {
		REQUEST_DTO requestDTO = getRequestBody(servletRequest.getInputStream());
		AUTH_INFO authInfo = getAuthInfo(requestDTO);
		// validateForRequiredfields(authInfo);
		authenticateRequest(authInfo);
	}

	private REQUEST_DTO getRequestBody(InputStream inputStream) throws IOException, IdAuthenticationAppException {
		try {
			return mapper.readValue(inputStream, getRequestDTOClass());
		} catch (JsonParseException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());
		}
	}

	private void validateForRequiredfields(AUTH_INFO tspInfo) throws IdAuthenticationAppException {
		Set<ConstraintViolation<AUTH_INFO>> violations = javaxValidator.validate(tspInfo, Default.class);
		if (!violations.isEmpty()) {
			String message = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("\n"));
			// FIXME check this exception type is correct
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(), new Exception(message));
		}
	}

	protected abstract AUTH_INFO getAuthInfo(REQUEST_DTO requestDTO) throws IdAuthenticationAppException;

	private void authenticateRequest(AUTH_INFO requestDTO) throws IdAuthenticationAppException {
		// TODO authenticate/authorize TSP, validate UIN and VID. Throw exception upon
		// any validation failure
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	protected abstract Class<REQUEST_DTO> getRequestDTOClass();

}
