package io.mosip.kernel.core.idrepo.exception;

import java.util.Optional;

import io.mosip.kernel.core.idrepo.constant.IdRepoErrorConstants;

/**
 * Thrown when an exception occurs using Rest exchange.
 * 
 * @author Manoj SP
 *
 */
public class RestServiceException extends IdRepoAppException {

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
	public RestServiceException(IdRepoErrorConstants exceptionConstant) {
		super(exceptionConstant);
	}

	/**
	 * Instantiates a new rest client exception.
	 *
	 * @param exceptionConstant the exception constant
	 * @param rootCause         the root cause
	 */
	public RestServiceException(IdRepoErrorConstants exceptionConstant, Throwable rootCause) {
		super(exceptionConstant, rootCause);
	}

	/**
	 * Instantiates a new rest service exception.
	 *
	 * @param exceptionConstant    the exception constant
	 * @param responseBodyAsString the response body as string
	 * @param responseBody         the response body
	 */
	public RestServiceException(IdRepoErrorConstants exceptionConstant, String responseBodyAsString,
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
		return Optional.ofNullable(responseBody);
	}

	public Optional<String> getResponseBodyAsString() {
		return Optional.ofNullable(responseBodyAsString);
	}

}
