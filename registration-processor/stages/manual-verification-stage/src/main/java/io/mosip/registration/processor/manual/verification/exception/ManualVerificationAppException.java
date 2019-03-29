package io.mosip.registration.processor.manual.verification.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The Class ManualVerificationAppException.
 * @author Rishabh Keshari
 */
public class ManualVerificationAppException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6748760277721155095L;

	/** The id. */
	private String id;

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Instantiates a new id repo app exception.
	 */
	public ManualVerificationAppException() {
		super();
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 */
	public ManualVerificationAppException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 * @param rootCause the root cause
	 */
	public ManualVerificationAppException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
	
	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 * @param rootCause the root cause
	 * @param id the id
	 */
	public ManualVerificationAppException(String errorCode, String errorMessage, Throwable rootCause, String id) {
		super(errorCode, errorMessage, rootCause);
		this.id = id;
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param exceptionConstant the exception constant
	 */
	public ManualVerificationAppException(PlatformErrorMessages exceptionConstant) {
		this(exceptionConstant.getCode(), exceptionConstant.getMessage());
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param exceptionConstant the exception constant
	 * @param rootCause the root cause
	 */
	public ManualVerificationAppException(PlatformErrorMessages exceptionConstant, Throwable rootCause) {
		this(exceptionConstant.getCode(), exceptionConstant.getMessage(), rootCause);
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param exceptionConstant the exception constant
	 * @param id the id
	 */
	public ManualVerificationAppException(PlatformErrorMessages exceptionConstant, String id) {
		this(exceptionConstant.getCode(), exceptionConstant.getMessage());
		this.id = id;
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param exceptionConstant the exception constant
	 * @param rootCause the root cause
	 * @param id the id
	 */
	public ManualVerificationAppException(PlatformErrorMessages exceptionConstant, Throwable rootCause, String id) {
		this(exceptionConstant.getCode(), exceptionConstant.getMessage(), rootCause);
		this.id = id;
	}

}
