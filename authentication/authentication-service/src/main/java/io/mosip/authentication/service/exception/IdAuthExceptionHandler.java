package io.mosip.authentication.service.exception;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthError;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.exception.IDAuthenticationUnknownException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;

/**
 * The Class IDAExceptionHandler - ControllerAdvice to handle
 * {@link IdUsageException}, Spring MVC Exceptions as defined in
 * {@link ResponseEntityExceptionHandler} and any other Exceptions occurs and
 * returns custom exception response {@link AuthResponseDTO}.
 *
 * @author Manoj SP
 */
@RestControllerAdvice
public class IdAuthExceptionHandler extends ResponseEntityExceptionHandler {

	private static final String PREFIX_HANDLING_EXCEPTION = "Handling exception :";
	private static final String EVENT_EXCEPTION = "Exception";
	private static final String DEFAULT_SESSION_ID = "sessionId";
	private MosipLogger mosipLogger;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender idaRollingFileAppender) {
		mosipLogger = MosipLogfactory.getMosipDefaultRollingFileLogger(idaRollingFileAppender, this.getClass());
	}

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
		mosipLogger.debug(DEFAULT_SESSION_ID, EVENT_EXCEPTION, "Entered handleAllExceptions",
				PREFIX_HANDLING_EXCEPTION + ex.getClass().toString());
		mosipLogger.error(DEFAULT_SESSION_ID, EVENT_EXCEPTION, ex.getClass().getName(),
				ex.toString() + "\n Request : " + request + "\n Status returned : " + HttpStatus.INTERNAL_SERVER_ERROR);

		IDAuthenticationUnknownException unknownException = new IDAuthenticationUnknownException(
				IdAuthenticationErrorConstants.UNKNOWN_ERROR);

		mosipLogger.debug(DEFAULT_SESSION_ID, EVENT_EXCEPTION, "Changing exception",
				"Returing exception as " + ex.getClass().toString());

		List<String> errorCodes = new ArrayList<>();
		errorCodes.add(unknownException.getErrorCode());

		return new ResponseEntity<>(
				buildExceptionResponse(unknownException, errorCodes, unknownException.getErrorTexts()),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Method to handle all exception and return as customized response object.
	 * 
	 * @param ex
	 *            Exception
	 * @param errorMessage
	 *            List of error messages
	 * @param headers
	 *            Http headers
	 * @param status
	 *            Http status
	 * @param request
	 *            Web request
	 * @return Customized response object
	 * 
	 * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler#handleExceptionInternal(java.lang.Exception,
	 *      java.lang.Object, org.springframework.http.HttpHeaders,
	 *      org.springframework.http.HttpStatus,
	 *      org.springframework.web.context.request.WebRequest)
	 */
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object errorMessage,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		mosipLogger.debug(DEFAULT_SESSION_ID, EVENT_EXCEPTION, "Entered handleExceptionInternal",
				PREFIX_HANDLING_EXCEPTION + ex.getClass().toString());

		mosipLogger.error(DEFAULT_SESSION_ID, "Spring MVC Exception", ex.getClass().getName(),
				ex.toString() + "Error message Object : "
						+ Optional.ofNullable(errorMessage).orElseGet(() -> "null").toString() + "\nStatus returned: "
						+ Optional.ofNullable(status).orElseGet(() -> HttpStatus.INTERNAL_SERVER_ERROR).toString());
		// FIXME Need to handle properly
		List<String> errorCodes = new ArrayList<>();
		errorCodes.add(ex.getMessage());

		List<String> errorMessages = new ArrayList<>();
		errorMessages.add(ex.getMessage());

		return new ResponseEntity<>(buildExceptionResponse(ex, errorCodes, errorMessages),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Method to handle {@link IdUsageException} and customize the response.
	 *
	 * @param ex
	 *            Exception
	 * @param request
	 *            Web request
	 * @return ResponseEntity containing error response object and http status.
	 */
	@ExceptionHandler(IdAuthenticationAppException.class)
	protected ResponseEntity<Object> handleIdAppException(IdAuthenticationAppException ex, WebRequest request) {

		mosipLogger.debug(DEFAULT_SESSION_ID, "IdAuthenticationAppException", "Entered handleIdUsageException",
				PREFIX_HANDLING_EXCEPTION + ex.getClass().toString());

		mosipLogger.error(DEFAULT_SESSION_ID, "IdAuthenticationAppException", ex.getErrorCode(),
				ex.toString() + "\n Status returned: " + HttpStatus.INTERNAL_SERVER_ERROR);

		Throwable e = ex;
		while (e.getCause() != null) {
			if (e.getCause() instanceof BaseCheckedException) {
				e = e.getCause();
			}
		}

		List<String> errorMessage = new ArrayList<>();
		errorMessage.addAll(((BaseCheckedException) e).getErrorTexts());

		return new ResponseEntity<>(buildExceptionResponse(ex, ex.getCodes(), errorMessage),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Constructs exception response body for all exceptions.
	 *
	 * @param ex
	 *            the exception occurred
	 * @param errorCode
	 *            the error code
	 * @param errorMessages
	 *            error message object.
	 * @param request
	 *            web request
	 * @return Object .
	 */
	@SuppressWarnings("unchecked")
	private Object buildExceptionResponse(Exception ex, Object errorCode, Object errorMessages) {

		mosipLogger.debug(DEFAULT_SESSION_ID, "Building exception response", "Entered buildExceptionResponse",
				PREFIX_HANDLING_EXCEPTION + ex.getClass().toString());

		AuthResponseDTO authResp = new AuthResponseDTO();

		authResp.setStatus(false);

		if (errorMessages != null) {
			List<String> errorCodeList = (List<String>) errorCode;
			List<String> errorMessageList = (List<String>) errorMessages;
			List<AuthError> errors = errorMessageList.parallelStream().distinct().map(
					message -> new AuthError(errorCodeList.get(errorMessageList.indexOf(message)), (String) message))
					.collect(Collectors.toList());
			authResp.setErr(errors);
		}

		authResp.setResTime(Instant.now().toString());

		mosipLogger.error(DEFAULT_SESSION_ID, "Response", ex.getClass().getName(), authResp.toString());

		return authResp;
	}
}
