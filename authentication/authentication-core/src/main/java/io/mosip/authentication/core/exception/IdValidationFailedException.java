package io.mosip.authentication.core.exception;

import org.springframework.validation.Errors;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.kernel.core.exception.BaseCheckedException;

/**
 * Exception thrown when UID/VIN is invalid.
 *
 * @author Manoj SP
 */
public class IdValidationFailedException extends IdAuthenticationBusinessException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1171032299623260757L;
	
	public IdValidationFailedException() {
		super();
	}

	/**
	 * Instantiates a new id validation failed exception.
	 *
	 * @param exceptionConstant the exception constant
	 */
	public IdValidationFailedException(IdAuthenticationErrorConstants exceptionConstant) {
		super(exceptionConstant);
	}

	/**
	 * Instantiates a new id validation failed exception.
	 *
	 * @param exceptionConstant the exception constant
	 * @param rootCause the root cause
	 */
	public IdValidationFailedException(IdAuthenticationErrorConstants exceptionConstant, Throwable rootCause) {
		super(exceptionConstant, rootCause);
	}

	/**
	 * Instantiates a new data validation exception.
	 *
	 * @param errors
	 *            the errors
	 */
	public IdValidationFailedException(Errors errors) {
		this(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, errors);
	}

	/**
	 * Constructs exception for the given {@code IdAuthenticationErrorConstants}.
	 *
	 * @param exceptionConstant
	 *            the exception constant
	 * @param errors
	 *            the errors
	 * @see BaseCheckedException#BaseCheckedException(String, String)
	 */
	public IdValidationFailedException(IdAuthenticationErrorConstants exceptionConstant, Errors errors) {
		super(exceptionConstant);
		errors.getAllErrors()
				.forEach(errorText -> addInfo(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED.getErrorCode(),
						errorText.getDefaultMessage()));
	}
}
