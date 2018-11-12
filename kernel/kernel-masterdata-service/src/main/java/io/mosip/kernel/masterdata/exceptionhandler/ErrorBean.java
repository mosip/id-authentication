package io.mosip.kernel.masterdata.exceptionhandler;

import lombok.Data;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Data
public class ErrorBean {

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
	public ErrorBean(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

}
