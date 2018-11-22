package io.mosip.authentication.core.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * The Class IDDataValidationException - Thrown when any Data validation error occurs.
 *
 * @author Manoj SP
 */
public class IDDataValidationException extends IdAuthenticationBusinessException {
	
	private final transient List<Object[]> args;

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7248433575478299970L;
	
	/**
	 * Instantiates a new ID data validation exception.
	 */
	public IDDataValidationException() {
		super();
		args = new ArrayList<>();
	}

	/**
	 * Instantiates a new ID data validation exception.
	 *
	 * @param exceptionConstant the exception constant
	 */
	public IDDataValidationException(IdAuthenticationErrorConstants exceptionConstant) {
		super(exceptionConstant);
		args = new ArrayList<>();
	}

	/**
	 * Instantiates a new ID data validation exception.
	 *
	 * @param exceptionConstant the exception constant
	 * @param rootCause the root cause
	 */
	public IDDataValidationException(IdAuthenticationErrorConstants exceptionConstant, Throwable rootCause) {
		super(exceptionConstant, rootCause);
		args = new ArrayList<>();
	}
	
	/**
	 * Constructs exception for the given error code and error message.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 * @see BaseUncheckedException#BaseUncheckedException(String, String)
	 */
	public IDDataValidationException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
		args = new ArrayList<>();
	}
	
	/**
	 * Constructs exception for the given  error code, error message and {@code Throwable}.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 * @param cause the cause
	 * @see BaseUncheckedException#BaseUncheckedException(String, String, Throwable)
	 */
	public IDDataValidationException(String errorCode, String errorMessage, Throwable cause) {
		super(errorCode, errorMessage, cause);
		args = new ArrayList<>();
	}
	
	public void addInfo(String errorCode, String errorMessage, Object... args) {
		super.addInfo(errorCode, String.format(Optional.ofNullable(errorMessage).orElseGet(() -> ""), args));
		this.args.add(args);
	}
	
	public List<Object[]> getArgs() {
		return Collections.unmodifiableList(args);
	}
}
