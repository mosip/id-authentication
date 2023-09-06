package io.mosip.authentication.common.service.exception;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import io.mosip.authentication.common.service.util.IdaRequestResponsConsumerUtil;
import io.mosip.authentication.core.authtype.dto.AuthtypeResponseDto;
import io.mosip.authentication.core.autntxn.dto.AutnTxnResponseDto;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.ObjectWithIdVersionTransactionID;
import io.mosip.authentication.core.dto.ObjectWithMetadata;
import io.mosip.authentication.core.exception.IDAuthenticationUnknownException;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBaseException;
import io.mosip.authentication.core.hotlist.dto.HotlistResponseDTO;
import io.mosip.authentication.core.indauth.dto.ActionableAuthError;
import io.mosip.authentication.core.indauth.dto.AuthError;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.EKycAuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.EKycResponseDTO;
import io.mosip.authentication.core.indauth.dto.EncryptedKycRespDTO;
import io.mosip.authentication.core.indauth.dto.KycExchangeResponseDTO;
import io.mosip.authentication.core.indauth.dto.ResponseDTO;
import io.mosip.authentication.core.indauth.dto.VCResponseDTO;
import io.mosip.authentication.core.indauth.dto.VciExchangeResponseDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.otp.dto.OtpResponseDTO;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.StringUtils;

/**
 * The Class IDAExceptionHandler - Spring MVC Exceptions as defined in
 * {@link ResponseEntityExceptionHandler} and any other Exceptions occurs and
 * returns custom exception response {@link AuthResponseDTO}.
 *
 * @author Manoj SP
 * @author Mamta A
 */
@RestControllerAdvice
public class IdAuthExceptionHandler extends ResponseEntityExceptionHandler {

	/** The Constant ID_AUTHENTICATION_APP_EXCEPTION. */
	private static final String ID_AUTHENTICATION_APP_EXCEPTION = "IdAuthenticationAppException";

	/** The Constant PREFIX_HANDLING_EXCEPTION. */
	private static final String PREFIX_HANDLING_EXCEPTION = "Handling exception :";

	/** The Constant EVENT_EXCEPTION. */
	private static final String EVENT_EXCEPTION = "Exception";

	private static final String INTERNAL = "/internal";

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(IdAuthExceptionHandler.class);

	@Autowired
	private HttpServletRequest servletRequest;

	/**
	 * Instantiates a new id auth exception handler.
	 */
	public IdAuthExceptionHandler() {
		//Default constructor
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
		mosipLogger.error(IdAuthCommonConstants.SESSION_ID, EVENT_EXCEPTION, ex.getClass().getName(),
				ex.toString() + "\n Request : " + request + "\n Status returned : " + HttpStatus.OK + "\n"
						+ ExceptionUtils.getStackTrace(ex));
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

		if (servletRequest.getRequestURL().toString().endsWith("notify")
				&& ex instanceof HttpMessageNotReadableException
				&& ex.getCause().getClass().isAssignableFrom(InvalidFormatException.class)
				&& Objects.nonNull(((InvalidFormatException) ex.getCause()).getPath())
				&& Objects.nonNull(((InvalidFormatException) ex.getCause()).getPath().get(2))) {
			int index = ((InvalidFormatException) ex.getCause()).getPath().get(2).getIndex();
			if (ex.getCause().getMessage().contains("eventType") && Objects.nonNull(index)) {
				ex = new IdAuthenticationAppException(
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
								"request/events/" + index + "/eventType"));
			} else if (ex.getCause().getMessage().contains("expiryTimestamp") && Objects.nonNull(index)) {
				ex = new IdAuthenticationAppException(
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
								"request/events/" + index + "/expiryTimestamp"));
			} else {
				ex = new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
						IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage());
			}
			return new ResponseEntity<>(buildExceptionResponse(ex, servletRequest), HttpStatus.OK);
		} else if (ex instanceof ServletException || ex instanceof BeansException
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
	@ExceptionHandler(IdAuthenticationBaseException.class)
	protected ResponseEntity<Object> handleIdAppException(IdAuthenticationBaseException ex, WebRequest request) {

		mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, ID_AUTHENTICATION_APP_EXCEPTION,
				"Entered handleIdUsageException", PREFIX_HANDLING_EXCEPTION + ex.getClass().toString());

		mosipLogger.error(IdAuthCommonConstants.SESSION_ID, ID_AUTHENTICATION_APP_EXCEPTION, ex.getErrorCode(),
				ex.toString() + "\n Status returned: " + HttpStatus.OK + ExceptionUtils.getStackTrace(ex));
		BaseCheckedException e = ex;
		while (e.getCause() != null) {
			if (e.getCause() instanceof BaseCheckedException
					&& !e.getCause().getClass().isAssignableFrom(RestServiceException.class)) {
				e = (BaseCheckedException) e.getCause();
			} else if (e.getCause() instanceof BaseCheckedException) {
				if(e.getCause() instanceof IdAuthenticationBaseException) {
					e = (IdAuthenticationBaseException) e.getCause();
				} else {
					e = new IdAuthenticationAppException(((BaseCheckedException) e.getCause()).getErrorCode(),
							((BaseCheckedException) e.getCause()).getErrorText());
				}
				break;
			} else {
				break;
			}
		}
		return new ResponseEntity<>(buildExceptionResponse(e, servletRequest), HttpStatus.OK);
	}

	/**
	 * Constructs exception response body for all exceptions.
	 *
	 * @param ex      the exception occurred
	 * @param request the request
	 * @return Object .
	 */
	public static Object buildExceptionResponse(Exception ex, HttpServletRequest request) {
		List<AuthError> errors = getAuthErrors(ex);
		return buildExceptionResponse(ex, request, errors);
	}

	public static Object buildExceptionResponse(Exception ex, HttpServletRequest request, List<AuthError> errors) {
		mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, "Building exception response",
				"Entered buildExceptionResponse", PREFIX_HANDLING_EXCEPTION + ex.getClass().toString());
		String type = null;
		String contextPath = request.getRequestURL().toString();
		String[] splitedContext = contextPath.split("/");
		String requestReceived = splitedContext.length >= 5 ? splitedContext[5] : "";
		if (requestReceived.equalsIgnoreCase("internal")) {
			String reqUrl = request.getRequestURL().toString();
			type = fetchInternalAuthtype(reqUrl);
		}
		if (errors != null && !errors.isEmpty()) {
			Object response = frameErrorResponse(requestReceived, type, errors);
			//Try copying ID, version and transaction ID from request metadata
			if(request instanceof ObjectWithMetadata && response instanceof ObjectWithIdVersionTransactionID) {
				ObjectWithMetadata requestWithMetadata = (ObjectWithMetadata) request;
				ObjectWithIdVersionTransactionID responsWithMetadata = (ObjectWithIdVersionTransactionID) response;
				if(requestWithMetadata.getMetadata() != null) {
					IdaRequestResponsConsumerUtil.setIdVersionToResponse(requestWithMetadata, responsWithMetadata);
					IdaRequestResponsConsumerUtil.setTransactionIdToResponse(requestWithMetadata, responsWithMetadata);
				}
			}
			
			//Try copying ID, version and transaction ID from exception metadata
			if(ex instanceof ObjectWithMetadata && response instanceof ObjectWithIdVersionTransactionID) {
				ObjectWithMetadata exceptionWithMetadata = (ObjectWithMetadata) ex;
				ObjectWithIdVersionTransactionID responsWithMetadata = (ObjectWithIdVersionTransactionID) response;
				if(exceptionWithMetadata.getMetadata() != null) {
					IdaRequestResponsConsumerUtil.setIdVersionToResponse(exceptionWithMetadata, responsWithMetadata);
					IdaRequestResponsConsumerUtil.setTransactionIdToResponse(exceptionWithMetadata, responsWithMetadata);
				}
			}
			
			if(ex instanceof ObjectWithMetadata) {
				ObjectWithMetadata exceptionWithMetadata = (ObjectWithMetadata) ex;
				//This errors list is used some times by the caller for storing in auth transaction
				exceptionWithMetadata.putMetadata(IdAuthCommonConstants.ERRORS, errors);
			}
			mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, "Response", ex.getClass().getName(),
					response.toString());
			return response;
		}

		return null;
	}
	
	public static List<AuthError> getAuthErrors(Exception ex) {
		List<AuthError> errors;
		if (ex instanceof IdAuthenticationBaseException) {
			IdAuthenticationBaseException baseException = (IdAuthenticationBaseException) ex;
			List<String> errorCodes = ((BaseCheckedException) ex).getCodes();
			List<String> errorMessages = ((BaseCheckedException) ex).getErrorTexts();

			// Retrived error codes and error messages are in reverse order.
			Collections.reverse(errorCodes);
			Collections.reverse(errorMessages);
			if (ex instanceof IDDataValidationException) {
				IDDataValidationException validationException = (IDDataValidationException) ex;
				List<Object[]> args = validationException.getArgs();
				List<String> actionArgs = validationException.getActionargs();
				errors = IntStream.range(0, errorCodes.size()).mapToObj(i -> {
					String errorMessage;
					if (args != null && !args.isEmpty()) {
						if(args.get(i) != null) {
							errorMessage = String.format(errorMessages.get(i), args.get(i));
						} else {
							errorMessage = errorMessages.get(i);
						}
					} else {
						errorMessage = errorMessages.get(i);
					}

					String actionMessage;
					if (args != null && !args.isEmpty() && actionArgs != null && !actionArgs.isEmpty()) {
						if(actionArgs.get(i) != null && args.get(i) != null) {
							actionMessage = String.format(actionArgs.get(i), args.get(i));
						} else {
							actionMessage = actionArgs.get(i);
						}
					} else {
						if(actionArgs != null && actionArgs.size() > (i + 1)) {
							actionMessage = actionArgs.get(i);
						} else {
							actionMessage = null;
						}
					}

					return createAuthError(validationException, errorCodes.get(i), errorMessage, actionMessage);
				}).distinct().collect(Collectors.toList());
			} else {
				errors = IntStream.range(0, errorCodes.size())
						.mapToObj(i -> createAuthError(baseException, errorCodes.get(i), errorMessages.get(i), null))
						.distinct().collect(Collectors.toList());
			}

		} else {
			errors = Collections.emptyList();
		}
		
		return errors;
	}

	private static String fetchInternalAuthtype(String reqURL) {
		String type = null;
		if (reqURL != null && !reqURL.isEmpty()) {
			String[] path = reqURL.split(INTERNAL);
			if (path.length > 1 && path[1] != null && !path[1].isEmpty()) {
				String[] urlPath = path[1].split("/");
				String contextPath = urlPath[1];
				if (!StringUtils.isEmpty(contextPath)) {
					if (contextPath.equalsIgnoreCase(IdAuthCommonConstants.OTP)) {
						type = IdAuthCommonConstants.OTP;
					} else if (contextPath.equalsIgnoreCase(IdAuthCommonConstants.AUTH_TRANSACTIONS)) {
						type = IdAuthCommonConstants.AUTH_TRANSACTIONS;
					} else if (contextPath.equalsIgnoreCase(IdAuthCommonConstants.AUTH_TYPE)) {
						type = IdAuthCommonConstants.AUTH_TYPE;
					}else if (contextPath.equalsIgnoreCase(IdAuthCommonConstants.PUBLICKEY)) {
						type = IdAuthCommonConstants.PUBLICKEY;
					}else if (contextPath.equalsIgnoreCase(IdAuthCommonConstants.ENCRYPT)) {
						type = IdAuthCommonConstants.ENCRYPT;
					}else if (contextPath.equalsIgnoreCase(IdAuthCommonConstants.DECRYPT)) {
						type = IdAuthCommonConstants.DECRYPT;
					}else if (contextPath.equalsIgnoreCase(IdAuthCommonConstants.VERIFY)) {
						type = IdAuthCommonConstants.VERIFY;
					}else if (contextPath.equalsIgnoreCase(IdAuthCommonConstants.VALIDATESIGN)) {
						type = IdAuthCommonConstants.VALIDATESIGN;
					}else if (contextPath.equalsIgnoreCase(IdAuthCommonConstants.HOTLIST)) {
						type = IdAuthCommonConstants.HOTLIST;
					}					
				}
			}
		}
		return type;
	}

	/**
	 * This method used to construct response respective to the request received
	 *
	 * @param requestReceived the fetched for the servlet path
	 * @param errors          the errors
	 * @return the object
	 */
	private static Object frameErrorResponse(String requestReceived, String type, List<AuthError> errors) {
		String responseTime = DateUtils.formatToISOString(DateUtils.getUTCCurrentDateTime());
		switch (requestReceived) {
		case "kyc":
			EKycAuthResponseDTO kycAuthResponseDTO = new EKycAuthResponseDTO();
			EKycResponseDTO kycResponse = new EKycResponseDTO();
			kycResponse.setKycStatus(false);
			kycAuthResponseDTO.setResponse(kycResponse);
			kycAuthResponseDTO.setErrors(errors);
			kycAuthResponseDTO.setResponseTime(responseTime);
			return kycAuthResponseDTO;
		case "otp":
			OtpResponseDTO otpResponseDTO = new OtpResponseDTO();
			otpResponseDTO.setErrors(errors);
			otpResponseDTO.setResponseTime(responseTime);
			return otpResponseDTO;
		case "kyc-exchange":
			KycExchangeResponseDTO kycExchangeResponseDTO = new KycExchangeResponseDTO();
			kycExchangeResponseDTO.setErrors(errors);
			kycExchangeResponseDTO.setResponseTime(responseTime);
			EncryptedKycRespDTO encryptedKycRespDTO = new EncryptedKycRespDTO();
			kycExchangeResponseDTO.setResponse(encryptedKycRespDTO);
			return kycExchangeResponseDTO;
		case "vci-exchange":
			VciExchangeResponseDTO vciExchangeResponseDTO = new VciExchangeResponseDTO();
			vciExchangeResponseDTO.setErrors(errors);
			vciExchangeResponseDTO.setResponseTime(responseTime);
			VCResponseDTO<?> vcResponseDTO = null;
			vciExchangeResponseDTO.setResponse(vcResponseDTO);
			return vciExchangeResponseDTO;
		case "internal":
			if (Objects.nonNull(type) && type.equalsIgnoreCase(IdAuthCommonConstants.OTP)) {
				OtpResponseDTO internalotpresponsedto = new OtpResponseDTO();
				internalotpresponsedto.setErrors(errors);
				internalotpresponsedto.setResponseTime(responseTime);
				return internalotpresponsedto;
			} else if (Objects.nonNull(type) && type.equalsIgnoreCase(IdAuthCommonConstants.AUTH_TRANSACTIONS)) {
				AutnTxnResponseDto autnTxnResponseDto = new AutnTxnResponseDto();
				autnTxnResponseDto.setErrors(errors);
				autnTxnResponseDto.setResponseTime(responseTime);
				return autnTxnResponseDto;
			} else if (Objects.nonNull(type) && type.equalsIgnoreCase(IdAuthCommonConstants.AUTH_TYPE)) {
				AuthtypeResponseDto authtypeResponseDto = new AuthtypeResponseDto();
				authtypeResponseDto.setErrors(errors);
				authtypeResponseDto.setResponseTime(responseTime);
				return authtypeResponseDto;
			}else if (Objects.nonNull(type) && type.equalsIgnoreCase(IdAuthCommonConstants.DECRYPT)) {
				AuthtypeResponseDto authtypeResponseDto = new AuthtypeResponseDto();
				authtypeResponseDto.setErrors(errors);
				authtypeResponseDto.setResponseTime(responseTime);
				return authtypeResponseDto;
			}else if (Objects.nonNull(type) && type.equalsIgnoreCase(IdAuthCommonConstants.ENCRYPT)) {
				AuthtypeResponseDto authtypeResponseDto = new AuthtypeResponseDto();
				authtypeResponseDto.setErrors(errors);
				authtypeResponseDto.setResponseTime(responseTime);
				return authtypeResponseDto;
			}else if (Objects.nonNull(type) && type.equalsIgnoreCase(IdAuthCommonConstants.PUBLICKEY)) {
				AuthtypeResponseDto authtypeResponseDto = new AuthtypeResponseDto();
				authtypeResponseDto.setErrors(errors);
				authtypeResponseDto.setResponseTime(responseTime);
				return authtypeResponseDto;
			}else if (Objects.nonNull(type) && type.equalsIgnoreCase(IdAuthCommonConstants.VERIFY)) {
				AuthtypeResponseDto authtypeResponseDto = new AuthtypeResponseDto();
				authtypeResponseDto.setErrors(errors);
				authtypeResponseDto.setResponseTime(responseTime);
				return authtypeResponseDto;
			}else if (Objects.nonNull(type) && type.equalsIgnoreCase(IdAuthCommonConstants.VALIDATESIGN)) {
				AuthtypeResponseDto authtypeResponseDto = new AuthtypeResponseDto();
				authtypeResponseDto.setErrors(errors);
				authtypeResponseDto.setResponseTime(responseTime);
				return authtypeResponseDto;
			}else if (Objects.nonNull(type) && type.equalsIgnoreCase(IdAuthCommonConstants.HOTLIST)) {
				HotlistResponseDTO hotlistResponseDto = new HotlistResponseDTO();
				hotlistResponseDto.setErrors(errors);
				hotlistResponseDto.setResponseTime(responseTime);
				return hotlistResponseDto;
			}
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
