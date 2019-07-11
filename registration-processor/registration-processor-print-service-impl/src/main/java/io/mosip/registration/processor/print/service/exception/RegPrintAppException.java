package io.mosip.registration.processor.print.service.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The Class RegPrintAppException.
 * 
 * @author M1048358 Alok
 */
public class RegPrintAppException extends BaseCheckedException {

	private static final long serialVersionUID = 1L;

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
	public RegPrintAppException() {
		super();
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param errorCode
	 *            the error code
	 * @param errorMessage
	 *            the error message
	 */
	public RegPrintAppException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param errorCode
	 *            the error code
	 * @param errorMessage
	 *            the error message
	 * @param rootCause
	 *            the root cause
	 */
	public RegPrintAppException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param errorCode
	 *            the error code
	 * @param errorMessage
	 *            the error message
	 * @param rootCause
	 *            the root cause
	 * @param id
	 *            the id
	 */
	public RegPrintAppException(String errorCode, String errorMessage, Throwable rootCause, String id) {
		super(errorCode, errorMessage, rootCause);
		this.id = id;
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param exceptionConstant
	 *            the exception constant
	 */
	public RegPrintAppException(PlatformErrorMessages exceptionConstant) {
		this(exceptionConstant.getCode(), exceptionConstant.getMessage());
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param exceptionConstant
	 *            the exception constant
	 * @param rootCause
	 *            the root cause
	 */
	public RegPrintAppException(PlatformErrorMessages exceptionConstant, Throwable rootCause) {
		this(exceptionConstant.getCode(), exceptionConstant.getMessage(), rootCause);
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param exceptionConstant
	 *            the exception constant
	 * @param id
	 *            the id
	 */
	public RegPrintAppException(PlatformErrorMessages exceptionConstant, String id) {
		this(exceptionConstant.getCode(), exceptionConstant.getMessage());
		this.id = id;
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param exceptionConstant
	 *            the exception constant
	 * @param rootCause
	 *            the root cause
	 * @param id
	 *            the id
	 */
	public RegPrintAppException(PlatformErrorMessages exceptionConstant, Throwable rootCause, String id) {
		this(exceptionConstant.getCode(), exceptionConstant.getMessage(), rootCause);
		this.id = id;
	}

}
