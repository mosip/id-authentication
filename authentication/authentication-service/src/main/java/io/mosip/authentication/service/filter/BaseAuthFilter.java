package io.mosip.authentication.service.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
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

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.kernel.core.spi.logger.MosipLogger;

/**
 * The Base Auth Filter that does all necessary authentication/authorization
 * before allowing the request to the respective controllers.
 *
 * @author Loganathan Sekar
 */
public abstract class BaseAuthFilter<REQUEST_DTO, RESPONSE_DTO, AUTH_INFO> implements Filter {
	
	private MosipLogger logger;

	private static final String EMPTY_JSON_OBJ_STRING = "{}";
	private static final Validator javaxValidator = Validation.buildDefaultValidatorFactory().getValidator();
	ObjectMapper mapper = new ObjectMapper();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.err.println("Entered filter");
		ResettableStreamHttpServletRequest requestWrapper = new ResettableStreamHttpServletRequest(
				(HttpServletRequest) request);
		try {
			authenticateRequest(requestWrapper);
			requestWrapper.resetInputStream();

			chain.doFilter(requestWrapper, response);
		} catch (IdAuthenticationAppException e) {
			CharResponseWrapper responseWrapper = new CharResponseWrapper((HttpServletResponse) response);
			requestWrapper.resetInputStream();
			chain.doFilter(requestWrapper, responseWrapper);
			sendAuthErrorResponse((HttpServletResponse) response, e, request.getContentType());
			// TODO log the error
		}

	}

	private void sendAuthErrorResponse(HttpServletResponse response, IdAuthenticationAppException e, String contentType)
			throws IOException {
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		PrintWriter out = response.getWriter();
		String errorMessageBody = getErrorMessageBody(e);
		response.setContentLength(errorMessageBody.length());
		response.setContentType(contentType);
		out.write(errorMessageBody); // Here you can change the response
	}

	private String getErrorMessageBody(IdAuthenticationAppException e) {
		// TODO should be similar to the controller advice
		RESPONSE_DTO responseDTO = createResponseDTO(e);
		try {
			return mapper.writeValueAsString(responseDTO);
		} catch (JsonProcessingException e1) {
			return EMPTY_JSON_OBJ_STRING;
		}
	}

	protected abstract RESPONSE_DTO createResponseDTO(IdAuthenticationAppException e);

	private void authenticateRequest(HttpServletRequest servletRequest)
			throws IOException, IdAuthenticationAppException {
		REQUEST_DTO requestDTO = getRequestBody(servletRequest.getInputStream());
		AUTH_INFO authInfo = getAuthInfo(requestDTO);
		validateForRequiredfields(authInfo);
		authenticateRequest(authInfo);
	}

	private REQUEST_DTO getRequestBody(InputStream inputStream) throws IOException, IdAuthenticationAppException {
		return mapper.readValue(inputStream, getRequestDTOClass());
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
