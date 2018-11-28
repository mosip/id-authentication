package io.mosip.registration.processor.quality.check.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * The Class QcUserOrRegIdNotFound.
 */
public class QcUserOrRegIdNotFound extends BaseUncheckedException{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new qc user or reg id not found.
	 */
	public QcUserOrRegIdNotFound() {
		super();
		
	}

	/**
	 * Instantiates a new qc user or reg id not found.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @param arg2 the arg 2
	 */
	public QcUserOrRegIdNotFound(String arg0, String arg1, Throwable arg2) {
		super(arg0, arg1, arg2);
		
	}

	/**
	 * Instantiates a new qc user or reg id not found.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 */
	public QcUserOrRegIdNotFound(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
		
	}

	/**
	 * Instantiates a new qc user or reg id not found.
	 *
	 * @param errorMessage the error message
	 */
	public QcUserOrRegIdNotFound(String errorMessage) {
		super(errorMessage);
		
	}
	

}
