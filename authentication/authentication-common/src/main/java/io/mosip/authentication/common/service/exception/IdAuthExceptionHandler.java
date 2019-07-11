package io.mosip.authentication.common.service.exception;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
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

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IDAuthenticationUnknownException;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBaseException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.indauth.dto.ActionableAuthError;
import io.mosip.authentication.core.indauth.dto.AuthError;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.KycResponseDTO;
import io.mosip.authentication.core.indauth.dto.ResponseDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.otp.dto.OtpResponseDTO;
import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

/**
 * The Class IDAExceptionHandler - Spring MVC Exceptions as defined in
 * {@link ResponseEntityExceptionHandler} and any other Exceptions occurs and
 * returns custom exception response {@link AuthResponseDTO}.
 *
 * @author Manoj SP
 */
@RestControllerAdvice
public class IdAuthExceptionHandler extends ResponseEntityExceptionHandler {

	/** The Constant ID_AUTHENTICATION_APP_EXCEPTION. */
	private static final String ID_AUTHENTICATION_APP_EXCEPTION = "IdAuthenticationAppException";

	/** The Constant PREFIX_HANDLING_EXCEPTION. */
	private static final String PREFIX_HANDLING_EXCEPTION = "Handling exception :";

	/** The Constant EVENT_EXCEPTION. */
	private static final String EVENT_EXCEPTION = "Exception";


	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(IdAuthExceptionHandler.class);
	
	@Autowired
	private HttpServletRequest servletRequest;

	/**
	 * Instantiates a new id auth exception handler.
	 */
	public IdAuthExceptionHandler() {

	}

	/**
	 * Handle all exceptions.
	 *
	 * @param ex      the ex
	 * @param request the request
	 * @return the response entity
	 */
	@ExceptionHandler(Exception.class)
	protected ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
		mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, EVENT_EXCEPTION, "Entered handleAllExceptions",
				PREFIX_HANDLING_EXCEPTION + ex.getClass().toString());
		mosipLogger.error(IdAuthCommonConstants.SESSION_ID, EVENT_EXCEPTION, ex.getClass().getName(), ex.toString() + "\n Request : "
				+ request + "\n Status returned : " + HttpStatus.OK + "\n" + ExceptionUtils.getStackTrace(ex));
		IDAuthenticationUnknownException unknownException = new IDAuthenticationUnknownException(
				IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, EVENT_EXCEPTION, "Changing exception",
				"Returing exception as " + ex.getClass().toString());
		return new ResponseEntity<>(buildExceptionResponse(unknownException, servletRequest), HttpStatus.OK);
	}

	/**
	 * Method to handle all exception and return as customized response object.
	 * 
	 * @param ex           Exception
	 * @param errorMessage List of error messages
	 * @param headers      Http headers
	 * @param status       Http status
	 * @param request      Web request
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

		mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, EVENT_EXCEPTION, "Entered handleExceptionInternal",
				PREFIX_HANDLING_EXCEPTION + ex.getClass().toString());

		mosipLogger.error(IdAuthCommonConstants.SESSION_ID, "Spring MVC Exception", ex.getClass().getName(),
				ex.toString() + "Error message Object : "
						+ Optional.ofNullable(errorMessage).orElseGet(() -> "null").toString() + "\nStatus returned: "
						+ Optional.ofNullable(status).orElseGet(() -> HttpStatus.OK).toString() + "\n"
						+ ExceptionUtils.getStackTrace(ex));

		if (ex instanceof ServletException || ex instanceof BeansException
				|| ex instanceof HttpMessageConversionException || ex instanceof AsyncRequestTimeoutException) {
			ex = new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
					IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage());
			return new ResponseEntity<>(buildExceptionResponse(ex, servletRequest), HttpStatus.OK);
		} else {
			return handleAllExceptions(ex, request);
		}
	}

	/**
	 * Method to handle and customize the response.
	 *
	 * @param ex      Exception
	 * @param request Web request
	 * @return ResponseEntity containing error response object and http status.
	 */
	@ExceptionHandler(IdAuthenticationAppException.class)
	protected ResponseEntity<Object> handleIdAppException(IdAuthenticationBaseException ex, WebRequest request) {

		mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, ID_AUTHENTICATION_APP_EXCEPTION, "Entered handleIdUsageException",
				PREFIX_HANDLING_EXCEPTION + ex.getClass().toString());

		mosipLogger.error(IdAuthCommonConstants.SESSION_ID, ID_AUTHENTICATION_APP_EXCEPTION, ex.getErrorCode(),
				ex.toString() + "\n Status returned: " + HttpStatus.OK + ExceptionUtils.getStackTrace(ex));

		Throwable e = ex;
		while (e.getCause() != null) {
			if (e.getCause() instanceof BaseCheckedException
					&& !e.getCause().getClass().isAssignableFrom(RestServiceException.class)) {
				e = e.getCause();
			} else if (ex.getCause() instanceof BaseCheckedException) {
				e = new IdAuthenticationAppException(ex.getErrorCode(), ex.getErrorText());
				break;
			} else {
				break;
			}
		}
		return new ResponseEntity<>(buildExceptionResponse((BaseCheckedException) e, servletRequest), HttpStatus.OK);
	}

	/**
	 * Constructs exception response body for all exceptions.
	 *
	 * @param ex the exception occurred
	 * @param request the request
	 * @return Object .
	 */
	public static Object buildExceptionResponse(Exception ex, HttpServletRequest request) {
		mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, "Building exception response", "Entered buildExceptionResponse",
				PREFIX_HANDLING_EXCEPTION + ex.getClass().toString());
		String contextPath = request.getContextPath();
		String[] splitedContext = contextPath.split("/");
		String requestReceived = splitedContext[splitedContext.length - 1];
		List<AuthError> errors = null;
		Object response;
		if (ex instanceof IdAuthenticationBaseException) {
			IdAuthenticationBaseException baseException = (IdAuthenticationBaseException) ex;
			List<String> errorCodes = ((BaseCheckedException) ex).getCodes();
			List<String> errorMessages = ((BaseCheckedException) ex).getErrorTexts();

			//Retrived error codes and error messages are in reverse order.
			Collections.reverse(errorCodes);
			Collections.reverse(errorMessages);
			if (ex instanceof IDDataValidationException) {
				IDDataValidationException validationException = (IDDataValidationException) ex;
				List<Object[]> args = validationException.getArgs();
				List<String> actionArgs = validationException.getActionargs();
				errors = IntStream.range(0, errorCodes.size())
						.mapToObj(i -> createAuthError(validationException, errorCodes.get(i),
								args != null ? String.format(errorMessages.get(i), args) : errorMessages.get(i),
								args != null && actionArgs != null && !actionArgs.contains(null)
										? String.format(actionArgs.get(i), args.get(i))
										: actionArgs.get(i)))
						.distinct().collect(Collectors.toList());
			} else {
				errors = IntStream.range(0, errorCodes.size())
						.mapToObj(i -> createAuthError(baseException, errorCodes.get(i), errorMessages.get(i), null))
						.distinct().collect(Collectors.toList());
			}

			response = frameErrorResponse(requestReceived, errors);
			mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, "Response", ex.getClass().getName(),
					response.toString());
			return response;
		}

		return null;
	}
	
	/**
	 * This method used to construct response
	 * respective to the request received
	 *
	 * @param requestReceived the fetched for the servlet path
	 * @param errors the errors
	 * @return the object
	 */
	private static Object frameErrorResponse(String requestReceived, List<AuthError> errors) {
		String responseTime = DateUtils.getUTCCurrentDateTimeString();
		switch (requestReceived) {
		case "kyc":
			KycAuthResponseDTO kycAuthResponseDTO = new KycAuthResponseDTO();
			KycResponseDTO kycResponseDTO = new KycResponseDTO();
			kycAuthResponseDTO.setErrors(errors);
			kycAuthResponseDTO.setResponseTime(responseTime);
			kycAuthResponseDTO.setResponse(kycResponseDTO);
			return kycAuthResponseDTO;
		case "otp":
			OtpResponseDTO otpResponseDTO = new OtpResponseDTO();
			otpResponseDTO.setErrors(errors);
			otpResponseDTO.setResponseTime(responseTime);
			return otpResponseDTO;
		default:
			AuthResponseDTO authResp = new AuthResponseDTO();
			ResponseDTO res = new ResponseDTO();
			authResp.setErrors(errors);
			authResp.setResponse(res);
			authResp.setResponseTime(responseTime);
			return authResp;
		}
	}

	/**
	 * Creates the auth error based on ActionItem.
	 *
	 * @param authException the auth exception
	 * @param errorCode     the error code
	 * @param errorMessage  the error message
	 * @param actionMessage the action message
	 * @return the auth error
	 */
	private static AuthError createAuthError(IdAuthenticationBaseException authException, String errorCode,
			String errorMessage, String actionMessage) {
		String actionMessageEx = authException.getActionMessage();
		AuthError err;
		if (actionMessageEx == null) {
			actionMessageEx = actionMessage;
		}

		if (actionMessageEx == null || actionMessageEx.isEmpty()) {
			err = new AuthError(errorCode, errorMessage);
		} else {
			err = new ActionableAuthError(errorCode, errorMessage, actionMessageEx);
		}

		return err;
	}
}
