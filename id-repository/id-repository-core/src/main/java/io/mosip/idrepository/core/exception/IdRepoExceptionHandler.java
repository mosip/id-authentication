package io.mosip.idrepository.core.exception;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.servlet.ServletException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import io.mosip.idrepository.core.constant.IdRepoConstants;
import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.dto.IdResponseDTO;
import io.mosip.idrepository.core.logger.IdRepoLogger;
import io.mosip.idrepository.core.security.IdRepoSecurityManager;
import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The Class IdRepoExceptionHandler - Handler class for all exceptions thrown in
 * Id Repository Idenitty and VID service.
 *
 * @author Manoj SP
 */
@RestControllerAdvice
public class IdRepoExceptionHandler extends ResponseEntityExceptionHandler {

	/** The Constant ID_REPO_EXCEPTION_HANDLER. */
	private static final String ID_REPO_EXCEPTION_HANDLER = "IdRepoExceptionHandler";
	
	/** The Constant TIMESTAMP. */
	private static final String REQUEST_TIME = "requesttime";

	/** The Constant ID_REPO. */
	private static final String ID_REPO = "IdRepo";

	/** The Constant READ. */
	private static final String READ = "read";

	/** The Constant CREATE. */
	private static final String CREATE = "create";

	/** The Constant UPDATE. */
	private static final String UPDATE = "update";

	/** The mosip logger. */
	Logger mosipLogger = IdRepoLogger.getLogger(IdRepoExceptionHandler.class);

	/** The env. */
	@Autowired
	private Environment env;

	/** The id. */
	@Resource
	private Map<String, String> id;

	/**
	 * Handles exceptions that are not handled by other methods in
	 * {@code IdRepoExceptionHandler}.
	 *
	 * @param ex the exception
	 * @param request the request
	 * @return the response entity
	 */
	@ExceptionHandler(Exception.class)
	protected ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
		mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO, ID_REPO_EXCEPTION_HANDLER,
				"handleAllExceptions - \n" + ExceptionUtils.getStackTrace(ex));
		IdRepoUnknownException e = new IdRepoUnknownException(IdRepoErrorConstants.UNKNOWN_ERROR);
		return new ResponseEntity<>(
				buildExceptionResponse((BaseCheckedException) e, ((ServletWebRequest) request).getHttpMethod(), null),
				HttpStatus.OK);
	}
	
	/**
	 * Handles bean creation exception.{@code BeanCreationException} is handled because
	 * IdObjetMasterDataValidator is loaded lazily and maskes use of RestTemplate in
	 * PostConstruct. When RestTemplate throws any exception inside PostConstruct,
	 * it is wrapped as BeanCreationException and thrown by Spring.
	 *
	 * @param ex the ex
	 * @param request the request
	 * @return the response entity
	 */
	@ExceptionHandler(BeanCreationException.class)
	protected ResponseEntity<Object> handleBeanCreationException(BeanCreationException ex, WebRequest request) {
		mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO, ID_REPO_EXCEPTION_HANDLER,
				"handleBeanCreationException - \n" + ExceptionUtils.getStackTrace(ex));
		Throwable rootCause = org.apache.commons.lang3.exception.ExceptionUtils.getRootCause(ex);
		if (rootCause.getClass().isAssignableFrom(AuthenticationException.class)) {
			return handleAuthenticationException((AuthenticationException) rootCause, request);
		} else if (rootCause.getClass().isAssignableFrom(IdRepoAppUncheckedException.class)) {
			return handleIdAppUncheckedException((IdRepoAppUncheckedException) rootCause, request);
		} else {
			return handleAllExceptions((Exception) rootCause, request);
		}
	}

	/**
	 * Handle access denied exception thrown when user is not allowed to access the
	 * specific API.
	 *
	 * @param ex
	 *            the ex
	 * @param request
	 *            the request
	 * @return the response entity
	 */
	@ExceptionHandler(AccessDeniedException.class)
	protected ResponseEntity<Object> handleAccessDeniedException(Exception ex, WebRequest request) {
		mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO, ID_REPO_EXCEPTION_HANDLER,
				"handleAccessDeniedException - \n" + ExceptionUtils.getStackTrace(ex));
		IdRepoUnknownException e = new IdRepoUnknownException(IdRepoErrorConstants.AUTHORIZATION_FAILED);
		return new ResponseEntity<>(
				buildExceptionResponse((BaseCheckedException) e, ((ServletWebRequest) request).getHttpMethod(), null),
				HttpStatus.OK);
	}

	/**
	 * Handle authentication exception - thrown in {@code RestHelper} when an 
	 * application fails to authenticate the rest request.
	 *
	 * @param ex the ex
	 * @param request the request
	 * @return the response entity
	 */
	@ExceptionHandler(AuthenticationException.class)
	protected ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
		mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO, ID_REPO_EXCEPTION_HANDLER,
				"handleAuthenticationException - \n" + ExceptionUtils.getStackTrace(ex));
		IdRepoUnknownException e = new IdRepoUnknownException(
				ex.getErrorTexts().isEmpty() ? "KER-ATH-401" : ex.getErrorCode(),
				ex.getErrorTexts().isEmpty() ? "Authentication Failed" : ex.getErrorText());
		return new ResponseEntity<>(
				buildExceptionResponse((BaseCheckedException) e, ((ServletWebRequest) request).getHttpMethod(), null),
				ex.getStatusCode() == 0 ? HttpStatus.UNAUTHORIZED : HttpStatus.valueOf(ex.getStatusCode()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.mvc.method.annotation.
	 * ResponseEntityExceptionHandler#handleExceptionInternal(java.lang.Exception,
	 * java.lang.Object, org.springframework.http.HttpHeaders,
	 * org.springframework.http.HttpStatus,
	 * org.springframework.web.context.request.WebRequest)
	 */
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object errorMessage,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO, ID_REPO_EXCEPTION_HANDLER,
				"handleExceptionInternal - \n" + ExceptionUtils.getStackTrace(ex));
		if (ex instanceof HttpMessageNotReadableException
				&& ex.getCause().getClass().isAssignableFrom(InvalidFormatException.class)) {
			ex = new IdRepoAppException(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), REQUEST_TIME));

			return new ResponseEntity<>(buildExceptionResponse(ex, ((ServletWebRequest) request).getHttpMethod(), null),
					HttpStatus.OK);
		} else if (ex instanceof HttpMessageNotReadableException || ex instanceof ServletException
				|| ex instanceof BeansException) {
			ex = new IdRepoAppException(IdRepoErrorConstants.INVALID_REQUEST.getErrorCode(),
					IdRepoErrorConstants.INVALID_REQUEST.getErrorMessage());

			return new ResponseEntity<>(buildExceptionResponse(ex, ((ServletWebRequest) request).getHttpMethod(), null),
					HttpStatus.OK);
		} else {
			return handleAllExceptions(ex, request);
		}
	}

	/**
	 * Handle id app exception - handle {@code IdRepoAppException} thrown from 
	 * application.
	 *
	 * @param ex the ex
	 * @param request the request
	 * @return the response entity
	 */
	@ExceptionHandler(IdRepoAppException.class)
	protected ResponseEntity<Object> handleIdAppException(IdRepoAppException ex, WebRequest request) {

		mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO, ID_REPO_EXCEPTION_HANDLER,
				"handleIdAppException - \n" + ExceptionUtils.getStackTrace(ex));

		return new ResponseEntity<>(buildExceptionResponse((Exception) ex,
				((ServletWebRequest) request).getHttpMethod(), ex.getOperation()), HttpStatus.OK);
	}

	/**
	 * Handle id app unchecked exception - handle {@code IdRepoAppUncheckedException} thrown from 
	 * application..
	 *
	 * @param ex the ex
	 * @param request the request
	 * @return the response entity
	 */
	@ExceptionHandler(IdRepoAppUncheckedException.class)
	protected ResponseEntity<Object> handleIdAppUncheckedException(IdRepoAppUncheckedException ex, WebRequest request) {

		mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO, ID_REPO_EXCEPTION_HANDLER,
				"handleIdAppUncheckedException - \n" + ExceptionUtils.getStackTrace(ex));

		return new ResponseEntity<>(
				buildExceptionResponse((Exception) ex, ((ServletWebRequest) request).getHttpMethod(), null),
				HttpStatus.OK);
	}

	/**
	 * Constructs exception response body for all exceptions.
	 *
	 * @param ex the ex
	 * @param httpMethod the http method
	 * @param operation the operation
	 * @return the object
	 */
	private Object buildExceptionResponse(Exception ex, HttpMethod httpMethod, String operation) {

		IdResponseDTO response = new IdResponseDTO();

		Throwable e = getRootCause(ex);

		if (Objects.nonNull(operation)) {
			response.setId(id.get(operation));
		} else if (httpMethod.compareTo(HttpMethod.GET) == 0) {
			response.setId(id.get(READ));
		} else if (httpMethod.compareTo(HttpMethod.POST) == 0) {
			response.setId(id.get(CREATE));
		} else if (httpMethod.compareTo(HttpMethod.PATCH) == 0) {
			response.setId(id.get(UPDATE));
		}

		if (e instanceof BaseCheckedException) {
			List<String> errorCodes = ((BaseCheckedException) e).getCodes();
			List<String> errorTexts = ((BaseCheckedException) e).getErrorTexts();

			List<ServiceError> errors = errorTexts.parallelStream()
					.map(errMsg -> new ServiceError(errorCodes.get(errorTexts.indexOf(errMsg)), errMsg)).distinct()
					.collect(Collectors.toList());

			response.setErrors(errors);
		}

		if (e instanceof BaseUncheckedException) {
			List<String> errorCodes = ((BaseUncheckedException) e).getCodes();
			List<String> errorTexts = ((BaseUncheckedException) e).getErrorTexts();

			List<ServiceError> errors = errorTexts.parallelStream()
					.map(errMsg -> new ServiceError(errorCodes.get(errorTexts.indexOf(errMsg)), errMsg)).distinct()
					.collect(Collectors.toList());

			response.setErrors(errors);
		}

		response.setVersion(env.getProperty(IdRepoConstants.APPLICATION_VERSION.getValue()));

		return response;
	}

	/**
	 * Gets the root cause.
	 *
	 * @param ex the ex
	 * @return the root cause
	 */
	private Throwable getRootCause(Exception ex) {
		Throwable e = ex;
		while (e != null) {
			if (Objects.nonNull(e.getCause()) && (e.getCause() instanceof IdRepoAppException)) {
				e = e.getCause();
			} else {
				break;
			}
		}
		return e;
	}
}
