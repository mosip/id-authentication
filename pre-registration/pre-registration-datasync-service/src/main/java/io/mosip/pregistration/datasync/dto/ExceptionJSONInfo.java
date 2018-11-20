package io.mosip.pregistration.datasync.dto;

import java.io.Serializable;

/**
 * @author M1046129 - Jagadishwari
 *
 */
public class ExceptionJSONInfo implements Serializable {
	
	/** The Constant serialVersionUID. */
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
