package io.mosip.authentication.service.exception;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.ServletException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
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

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.ActionableAuthError;
import io.mosip.authentication.core.dto.indauth.AuthError;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.dto.indauth.BaseAuthResponseDTO;
import io.mosip.authentication.core.exception.IDAuthenticationUnknownException;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBaseException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;

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

    /** The Constant ID_AUTHENTICATION_APP_EXCEPTION. */
    private static final String ID_AUTHENTICATION_APP_EXCEPTION = "IdAuthenticationAppException";

    /** The message source. */
    @Autowired
    private MessageSource messageSource;

    /** The mapper. */
    @Autowired
    private ObjectMapper mapper;

    /** The Constant PREFIX_HANDLING_EXCEPTION. */
    private static final String PREFIX_HANDLING_EXCEPTION = "Handling exception :";

    /** The Constant EVENT_EXCEPTION. */
    private static final String EVENT_EXCEPTION = "Exception";

    /** The Constant DEFAULT_SESSION_ID. */
    private static final String DEFAULT_SESSION_ID = "sessionId";

    /** The mosip logger. */
    private static Logger mosipLogger = IdaLogger.getLogger(IdAuthExceptionHandler.class);

    /**
     * Instantiates a new id auth exception handler.
     */
    private IdAuthExceptionHandler() {

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
		ex.toString() + "\n Request : " + request + "\n Status returned : " + HttpStatus.INTERNAL_SERVER_ERROR
			+ "\n" + ExceptionUtils.getStackTrace(ex));

	IDAuthenticationUnknownException unknownException = new IDAuthenticationUnknownException(
		IdAuthenticationErrorConstants.UNKNOWN_ERROR);

	mosipLogger.debug(DEFAULT_SESSION_ID, EVENT_EXCEPTION, "Changing exception",
		"Returing exception as " + ex.getClass().toString());

	return new ResponseEntity<>(buildExceptionResponse(unknownException), HttpStatus.INTERNAL_SERVER_ERROR);
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
			+ Optional.ofNullable(status).orElseGet(() -> HttpStatus.INTERNAL_SERVER_ERROR).toString()
			+ "\n" + ExceptionUtils.getStackTrace(ex));

	if (ex instanceof ServletException || ex instanceof BeansException
		|| ex instanceof HttpMessageConversionException) {
	    ex = new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
		    IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());

	    return new ResponseEntity<>(buildExceptionResponse(ex), HttpStatus.BAD_REQUEST);
	} else if (ex instanceof AsyncRequestTimeoutException) {
	    ex = new IdAuthenticationAppException(IdAuthenticationErrorConstants.CONNECTION_TIMED_OUT.getErrorCode(),
		    IdAuthenticationErrorConstants.CONNECTION_TIMED_OUT.getErrorMessage());

	    return new ResponseEntity<>(buildExceptionResponse(ex), HttpStatus.REQUEST_TIMEOUT);
	} else {
	    // HttpMessageConversionException
	    // MethodArgumentNotValidException
	    return handleAllExceptions(ex, request);
	}
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
    protected ResponseEntity<Object> handleIdAppException(IdAuthenticationBaseException ex, WebRequest request) {

	mosipLogger.debug(DEFAULT_SESSION_ID, ID_AUTHENTICATION_APP_EXCEPTION, "Entered handleIdUsageException",
		PREFIX_HANDLING_EXCEPTION + ex.getClass().toString());

	mosipLogger.error(DEFAULT_SESSION_ID, ID_AUTHENTICATION_APP_EXCEPTION, ex.getErrorCode(), ex.toString()
		+ "\n Status returned: " + HttpStatus.INTERNAL_SERVER_ERROR + ExceptionUtils.getStackTrace(ex));

	Throwable e = ex;
	while (e.getCause() != null) {
	    if (e.getCause() instanceof BaseCheckedException) {
		e = e.getCause();
	    }
	}

	return new ResponseEntity<>(buildExceptionResponse((BaseCheckedException) e), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Constructs exception response body for all exceptions.
     *
     * @param ex
     *            the exception occurred
     * @return Object .
     */
    private Object buildExceptionResponse(Exception ex) {

	mosipLogger.debug(DEFAULT_SESSION_ID, "Building exception response", "Entered buildExceptionResponse",
		PREFIX_HANDLING_EXCEPTION + ex.getClass().toString());

	BaseAuthResponseDTO authResp = new BaseAuthResponseDTO();

	authResp.setStatus("N");

	if (ex instanceof IdAuthenticationBaseException) {
	    IdAuthenticationBaseException baseException = (IdAuthenticationBaseException) ex;
	    Locale locale = LocaleContextHolder.getLocale();
	    List<String> errorCodes = ((BaseCheckedException) ex).getCodes();
	    Collections.reverse(errorCodes);

	    try {
		if (ex instanceof IDDataValidationException) {
		    IDDataValidationException validationException = (IDDataValidationException) ex;
		    List<Object[]> args = validationException.getArgs();

		    List<AuthError> errors = IntStream.range(0, errorCodes.size())
			    .mapToObj(i -> createAuthError(validationException, errorCodes.get(i),
				    messageSource.getMessage(errorCodes.get(i), args.get(i), locale)))
			    .distinct().collect(Collectors.toList());

		    authResp.setErr(errors);
		} else {
		    List<AuthError> errors = IntStream.range(0, errorCodes.size())
			    .mapToObj(i -> new AuthError(errorCodes.get(i),
				    messageSource.getMessage(errorCodes.get(i), null, locale)))
			    .distinct().collect(Collectors.toList());

		    authResp.setErr(errors);
		}
	    } catch (NoSuchMessageException e) {
		mosipLogger.error(DEFAULT_SESSION_ID, ID_AUTHENTICATION_APP_EXCEPTION, e.toString(),
			"\n" + ExceptionUtils.getStackTrace(e));
		authResp.setErr(Arrays.<AuthError>asList(
			createAuthError(baseException, IdAuthenticationErrorConstants.UNKNOWN_ERROR.getErrorCode(),
				IdAuthenticationErrorConstants.UNKNOWN_ERROR.getErrorMessage())));
	    }
	}

	authResp.setResTime(mapper.convertValue(new Date(), String.class));

	mosipLogger.error(DEFAULT_SESSION_ID, "Response", ex.getClass().getName(), authResp.toString());

	return authResp;
    }

    /**
     * Creates the auth error based on ActionItem
     *
     * @param authException
     *            the auth exception
     * @param errorCode
     *            the error code
     * @param errorMessage
     *            the error message
     * @return the auth error
     */
    private AuthError createAuthError(IdAuthenticationBaseException authException, String errorCode,
	    String errorMessage) {
	String actionCode = authException.getActionCode();
	AuthError err;
	if (actionCode == null) {
	    err = new AuthError(errorCode, errorMessage);
	} else {
	    err = new ActionableAuthError(errorCode, errorMessage, actionCode);
	}

	return err;
    }
}
