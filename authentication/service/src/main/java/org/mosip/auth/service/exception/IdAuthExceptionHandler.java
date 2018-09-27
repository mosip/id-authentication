package org.mosip.auth.service.exception;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;
import org.mosip.auth.core.dto.indauth.AuthError;
import org.mosip.auth.core.dto.indauth.AuthResponseDTO;
import org.mosip.auth.core.exception.IDAuthenticationUnknownException;
import org.mosip.auth.core.exception.IdAuthenticationAppException;
import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.mosip.kernel.logger.factory.MosipLogfactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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

	private MosipLogger logger;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender idaRollingFileAppender) {
		logger = MosipLogfactory.getMosipDefaultRollingFileLogger(idaRollingFileAppender, this.getClass());
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

		logger.debug("sessionId", "Exception", "Entered handleAllExceptions",
				"Handling exception :" + ex.getClass().toString());

		logger.error("sessionId", "Exception", ex.getClass().getName(),
				ex.toString() + "\n Request : " + request + "\n Status returned : " + HttpStatus.INTERNAL_SERVER_ERROR);

		IDAuthenticationUnknownException unknownException = new IDAuthenticationUnknownException(IdAuthenticationErrorConstants.UNKNOWN_ERROR);

		logger.debug("sessionId", "Exception", "Changing exception",
				"Returing exception as " + ex.getClass().toString());

		return new ResponseEntity<>(buildExceptionResponse(unknownException, 
				unknownException.getErrorCode(),
				unknownException.getErrorTexts(), 
				request), 
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

		logger.debug("sessionId", "Exception", "Entered handleExceptionInternal",
				"Handling exception :" + ex.getClass().toString());

		logger.error("sessionId", "Spring MVC Exception", ex.getClass().getName(),
				ex.toString() + "Error message Object : "
						+ Optional.ofNullable(errorMessage.toString()).orElseGet(() -> "null") + "\nStatus returned: "
						+ Optional.ofNullable(status.toString()).orElseGet(() -> "null"));

		return new ResponseEntity<>(buildExceptionResponse(ex, ex.getMessage(), errorMessage, request),
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

		logger.debug("sessionId", "IdUsageException", "Entered handleIdUsageException",
				"Handling exception :" + ex.getClass().toString());

		logger.error("sessionId", "IdUsageException", ex.getErrorCode(),
				ex.toString() + "\n Status returned: " + HttpStatus.INTERNAL_SERVER_ERROR);

		List<String> errorMessage = new ArrayList<>();
		errorMessage.add(ex.getErrorText());
		return new ResponseEntity<>(buildExceptionResponse(ex, ex.getErrorCode(), errorMessage, request),
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
	private Object buildExceptionResponse(Exception ex, @Nullable String errorCode, @Nullable Object errorMessages,
			WebRequest request) {

		logger.debug("sessionId", "Building exception response", "Entered buildExceptionResponse",
				"Handling exception :" + ex.getClass().toString());

		AuthResponseDTO authResp = new AuthResponseDTO();

		authResp.setStatus(false);


		if (errorMessages != null) {
			List<AuthError> errors = ((List<String>) errorMessages).parallelStream().map(message -> new AuthError(errorCode, (String) message))
					.collect(Collectors.toList());
			authResp.setErr(errors);
		}

		authResp.setResTime(new Date());


		logger.error("sessionId", "Response", ex.getClass().getName(), authResp.toString());

		return authResp;
	}
}
