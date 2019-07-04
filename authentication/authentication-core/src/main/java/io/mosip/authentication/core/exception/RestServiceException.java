package io.mosip.authentication.core.exception;

import java.util.Optional;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Thrown when an exception occurs using Rest exchange.
 * 
 * @author Manoj SP
 *
 */
public class RestServiceException extends IdAuthenticationAppException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 372518972095526748L;

	/** The response body. */
	private transient String responseBodyAsString;

	private transient Object responseBody;

	/**
	 * Instantiates a new rest service exception.
	 */
	public RestServiceException() {
		super();
	}

	/**
	 * Instantiates a new rest client exception.
	 *
	 * @param exceptionConstant the exception constant
	 */
	public RestServiceException(IdAuthenticationErrorConstants exceptionConstant) {
		super(exceptionConstant);
	}

	/**
	 * Instantiates a new rest client exception.
	 *
	 * @param exceptionConstant the exception constant
	 * @param rootCause         the root cause
	 */
	public RestServiceException(IdAuthenticationErrorConstants exceptionConstant, Throwable rootCause) {
		super(exceptionConstant, rootCause);
	}

	/**
	 * Instantiates a new rest service exception.
	 *
	 * @param exceptionConstant    the exception constant
	 * @param responseBodyAsString the response body as string
	 * @param responseBody         the response body
	 */
	public RestServiceException(IdAuthenticationErrorConstants exceptionConstant, String responseBodyAsString,
			Object responseBody) {
		super(exceptionConstant);
		this.responseBody = responseBody;
		this.responseBodyAsString = responseBodyAsString;
	}
	
	/**
	 * Constructs exception for the given  error code, error message and {@code Throwable}.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 * @param cause the cause
	 * @see BaseUncheckedException#BaseUncheckedException(String, String, Throwable)
	 */
	public RestServiceException(String errorCode, String errorMessage, Throwable cause) {
		super(errorCode, errorMessage, cause);
	}

	/**
	 * Gets the response body.
	 *
	 * @return the response body
	 */
	public Optional<Object> getResponseBody() {
		return Optional.ofNullable(responseBody);
	}

	public Optional<String> getResponseBodyAsString() {
		return Optional.ofNullable(responseBodyAsString);
	}

}
