package io.mosip.registration.processor.bio.dedupe.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.core.idrepo.constant.IdRepoErrorConstants;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The Class BioDedupeAppException.
 * @author Rishabh Keshari
 */
public class BioDedupeAppException extends BaseCheckedException {

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
	public BioDedupeAppException() {
		super();
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 */
	public BioDedupeAppException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 * @param rootCause the root cause
	 */
	public BioDedupeAppException(String errorCode, String errorMessage, Throwable rootCause) {
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
	public BioDedupeAppException(String errorCode, String errorMessage, Throwable rootCause, String id) {
		super(errorCode, errorMessage, rootCause);
		this.id = id;
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param exceptionConstant the exception constant
	 */
	public BioDedupeAppException(PlatformErrorMessages exceptionConstant) {
		this(exceptionConstant.getCode(), exceptionConstant.getMessage());
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param exceptionConstant the exception constant
	 * @param rootCause the root cause
	 */
	public BioDedupeAppException(PlatformErrorMessages exceptionConstant, Throwable rootCause) {
		this(exceptionConstant.getCode(), exceptionConstant.getMessage(), rootCause);
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param exceptionConstant the exception constant
	 * @param id the id
	 */
	public BioDedupeAppException(PlatformErrorMessages exceptionConstant, String id) {
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
	public BioDedupeAppException(PlatformErrorMessages exceptionConstant, Throwable rootCause, String id) {
		this(exceptionConstant.getCode(), exceptionConstant.getMessage(), rootCause);
		this.id = id;
	}

}
