package io.mosip.kernel.emailnotifier.exceptionhandler;

import java.io.Serializable;

/**
 * This class sets the error response.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 */
public class MosipErrors implements Serializable {
	/**
	 * Generated serial version.
	 */
	private static final long serialVersionUID = -715186255407501148L;
	/**
	 * The error code.
	 */
	private String errorCode;
	/**
	 * The error message.
	 */
	private String errorMessage;

	/**
	 * Constructor for MosipErrors.
	 * 
	 * @param errorCode
	 *            The error code.
	 * @param errorMessage
	 *            The error message.
	 */
	public MosipErrors(String errorCode, String errorMessage) {
		super();
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * Getter for errorCode.
	 * 
	 * @return The error code.
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Getter for errorMessage.
	 * 
	 * @return The error message.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
}
