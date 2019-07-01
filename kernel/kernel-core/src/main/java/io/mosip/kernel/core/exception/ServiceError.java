package io.mosip.kernel.core.exception;

import java.io.Serializable;

import lombok.Data;

/**
 * @author Bal Vikash Sharma
 * @since 1.0.0
 *
 */
@Data
public class ServiceError implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5501835725093230935L;
	/**
	 * The error code.
	 */
	private String errorCode;
	/**
	 * The error message.
	 */
	private String message;

	/**
	 * Constructor for ErrorBean.
	 * 
	 * @param errorCode
	 *            The error code.
	 * @param errorMessage
	 *            The error message.
	 */
	public ServiceError(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.message = errorMessage;
	}

	public ServiceError() {

	}

}
