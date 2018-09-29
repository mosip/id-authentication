package org.mosip.kernel.otpmanagerservice.exceptionhandler;

import java.io.Serializable;

/**
 * This class sets the error response.
 * 
 * @author Ritesh Sinha
 * @author Sagar Mahapatra
 * @since 1.0.0
 */
public class MosipErrors implements Serializable {
	/**
	 * Serializable version ID.
	 */
	private static final long serialVersionUID = -7725390540618015762L;
	
	/**
	 * The error code.
	 */
	private String errorCode;
	
	
	/**
	 *The error message.
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
