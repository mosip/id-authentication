package io.mosip.kernel.notifier.sms.exceptionhandler;

import lombok.Data;

/**
 * This class sets the error response.
 * 
 * @author Ritesh sinha
 * @since 1.0.0
 *
 */
@Data
public class MosipErrors {

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
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

}
