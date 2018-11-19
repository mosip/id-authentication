package io.mosip.preregistration.documents.dto;

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

	/**
	 * @param errorcode
	 * @param message
	 * 
	 */
	public ExceptionJSONInfo(String errorcode, String message) {
		super();
		errorCode = errorcode;
		this.message = message;
	}

	/**
	 * @return
	 */
	public String getErrorcode() {
		return errorCode;
	}


	/**
	 * @return
	 */
	public String getMessage() {
		return message;
	}


}
