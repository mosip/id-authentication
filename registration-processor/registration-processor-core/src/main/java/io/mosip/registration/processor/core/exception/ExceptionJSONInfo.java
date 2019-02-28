package io.mosip.registration.processor.core.exception;

import java.io.Serializable;

public class ExceptionJSONInfo implements Serializable {

	/** ExceptionJSONInfo dto class. */
	private static final long serialVersionUID = 3999014525078508265L;

	/** The error code. */
	private String errorCode;
	
	/** The message. */
	private String message;

	/**
	 * Instantiates a new exception JSON info.
	 *
	 * @param errorcode the errorcode
	 * @param message the message
	 */
	public ExceptionJSONInfo(String errorcode, String message) {
		super();
		errorCode = errorcode;
		this.message = message;
	}

	/**
	 * Gets the errorcode.
	 *
	 * @return the errorcode
	 */
	public String getErrorcode() {
		return errorCode;
	}

	/**
	 * Sets the errorcode.
	 *
	 * @param errorcode the new errorcode
	 */
	public void setErrorcode(String errorcode) {
		errorCode = errorcode;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message.
	 *
	 * @param message the new message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

}
