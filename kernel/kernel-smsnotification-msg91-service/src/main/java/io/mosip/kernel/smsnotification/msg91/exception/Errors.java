package io.mosip.kernel.smsnotification.msg91.exception;

import lombok.Data;

/**
 * This class sets the error response.
 * 
 * @author Ritesh sinha
 * @since 1.0.0
 *
 */
@Data
public class Errors {

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
	public Errors(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

}
