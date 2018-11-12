package io.mosip.kernel.smsnotification.exception;

import lombok.Data;

/**
 * This class sets the error response.
 * 
 * @author Ritesh sinha
 * @since 1.0.0
 *
 */
@Data
public class Error {

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
	public Error(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

}
