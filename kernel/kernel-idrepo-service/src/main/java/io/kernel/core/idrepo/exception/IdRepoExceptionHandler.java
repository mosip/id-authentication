package io.kernel.core.idrepo.exception;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.ServletException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import io.kernel.core.idrepo.constant.IdRepoErrorConstants;
import io.kernel.core.idrepo.dto.ErrorDTO;
import io.kernel.core.idrepo.dto.IdResponseDTO;
import io.mosip.kernel.core.exception.BaseCheckedException;

/**
 * The Class IdRepoExceptionHandler.
 *
 * @author Manoj SP
 */
@RestControllerAdvice
public class IdRepoExceptionHandler extends ResponseEntityExceptionHandler {

	/** The env. */
	@Autowired
	private Environment env;

	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	/**
	 * Handle all exceptions.
	 *
	 * @param ex
	 *            the ex
	 * @param request
	 *            the request
	 * @return the response entity
	 */
	@ExceptionHandler(Exception.class)
	protected ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
		IdRepoUnknownException e = new IdRepoUnknownException(IdRepoErrorConstants.UNKNOWN_ERROR);
		return new ResponseEntity<>(buildExceptionResponse((BaseCheckedException) e), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler#handleExceptionInternal(java.lang.Exception, java.lang.Object, org.springframework.http.HttpHeaders, org.springframework.http.HttpStatus, org.springframework.web.context.request.WebRequest)
	 */
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object errorMessage,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		if (ex instanceof ServletException || ex instanceof BeansException
				|| ex instanceof HttpMessageConversionException) {
			ex = new IdRepoAppException(IdRepoErrorConstants.INVALID_REQUEST.getErrorCode(),
					IdRepoErrorConstants.INVALID_REQUEST.getErrorMessage());

			return new ResponseEntity<>(buildExceptionResponse(ex), HttpStatus.BAD_REQUEST);
		} else if (ex instanceof AsyncRequestTimeoutException) {
			ex = new IdRepoAppException(IdRepoErrorConstants.CONNECTION_TIMED_OUT.getErrorCode(),
					IdRepoErrorConstants.CONNECTION_TIMED_OUT.getErrorMessage());

			return new ResponseEntity<>(buildExceptionResponse(ex), HttpStatus.REQUEST_TIMEOUT);
		} else {
			return handleAllExceptions(ex, request);
		}
	}

	/**
	 * Handle id app exception.
	 *
	 * @param ex the ex
	 * @param request the request
	 * @return the response entity
	 */
	@ExceptionHandler(IdRepoAppException.class)
	protected ResponseEntity<Object> handleIdAppException(IdRepoAppException ex, WebRequest request) {

		return new ResponseEntity<>(buildExceptionResponse((Exception) ex), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Constructs exception response body for all exceptions.
	 *
	 * @param ex            the exception occurred
	 * @return Object .
	 */
	private Object buildExceptionResponse(Exception ex) {

		IdResponseDTO response = new IdResponseDTO();

		Throwable e = ex;
		while (e.getCause() != null) {
			if (e.getCause() instanceof IdRepoAppException) {
				if (Objects.nonNull(((IdRepoAppException) e).getId())) {
					response.setId(((IdRepoAppException) e).getId());
				}
				e = e.getCause();
			} else {
				break;
			}
		}

		if (Objects.isNull(response.getId())) {
			response.setId("mosip.id.error");
		}

		response.setVer(env.getProperty("mosip.idrepo.version"));

		if (e instanceof BaseCheckedException) {
			List<String> errorCodes = ((BaseCheckedException) e).getCodes();
			List<String> errorTexts = ((BaseCheckedException) e).getErrorTexts();

			List<ErrorDTO> errors = errorTexts.parallelStream()
					.map(errMsg -> new ErrorDTO(errorCodes.get(errorTexts.indexOf(errMsg)), errMsg)).distinct()
					.collect(Collectors.toList());

			response.setErr(errors);
		}

		response.setTimestamp(mapper.convertValue(new Date(), String.class));

		mapper.setFilterProvider(new SimpleFilterProvider().addFilter("responseFilter",
				SimpleBeanPropertyFilter.serializeAllExcept("registrationId", "status", "response")));

		return response;
	}
}
