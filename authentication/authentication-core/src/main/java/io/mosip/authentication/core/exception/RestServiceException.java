package io.mosip.authentication.core.exception;

import java.util.Optional;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;

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
	 * @param exceptionConstant the exception constant
	 * @param responseBody      the response body
	 */
	public RestServiceException(IdAuthenticationErrorConstants exceptionConstant, String responseBodyAsString,
			Object responseBody) {
		super(exceptionConstant);
		this.responseBody = responseBody;
		this.responseBodyAsString = responseBodyAsString;
	}

	/**
	 * Gets the response body.
	 *
	 * @return the response body
	 */
	public Optional<Object> getResponseBody() {
		return Optional.of(responseBody);
	}
	
	public Optional<String> getResponseBodyAsString() {
		return Optional.of(responseBodyAsString);
	}

}
