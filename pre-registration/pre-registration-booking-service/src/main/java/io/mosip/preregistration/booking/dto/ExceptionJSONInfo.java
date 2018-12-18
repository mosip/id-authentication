package io.mosip.preregistration.booking.dto;

import java.io.Serializable;

/**
 * Exception json Info
 * 
 * @author M1037717
 *
 */
public class ExceptionJSONInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3999014525078508265L;

	private String errorCode;
	private String message;

	public ExceptionJSONInfo(String errorcode, String message) {
		super();
		errorCode = errorcode;
		this.message = message;
	}

	public String getErrorcode() {
		return errorCode;
	}


	public String getMessage() {
		return message;
	}


}
