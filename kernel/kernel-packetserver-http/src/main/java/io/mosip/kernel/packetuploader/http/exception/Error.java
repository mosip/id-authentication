package io.mosip.kernel.packetuploader.http.exception;

import net.bytebuddy.dynamic.scaffold.MethodRegistry.Handler.ForAbstractMethod;

/**
 * Error item bean class having error code and error message
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 *
 */

public class Error {

	/**
	 * The error code field
	 */

	private String code;

	/**
	 * The error message field
	 */
	private String message;

	/**
	 * Getter for {@link #code}s
	 * 
	 * @return {@link #code}
	 */
	public String getCode() {
		return code;
	}

	/** Setter for {@link #code}
	 * @param code
	 *            {@link #code}
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * Getter for {@link #message}
	 * 
	 * @return {@link #message}
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Setter {@link ForAbstractMethod} {@link #message}
	 * 
	 * @param message
	 *            {@link #message}
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Constructor for this class
	 * 
	 * @param code
	 *            {@link #code}
	 * @param message
	 *            {@link #message}
	 */
	public Error(String code, String message) {
		this.code = code;
		this.message = message;
	}

	/**
	 * Constructor for this class
	 */
	public Error() {

	}

}