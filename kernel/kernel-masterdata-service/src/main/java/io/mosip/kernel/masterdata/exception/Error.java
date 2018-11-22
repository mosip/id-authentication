package io.mosip.kernel.masterdata.exception;

import lombok.Data;

/**
 * @author Bal Vikash Sharma
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
	 * Constructor for ErrorBean.
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
