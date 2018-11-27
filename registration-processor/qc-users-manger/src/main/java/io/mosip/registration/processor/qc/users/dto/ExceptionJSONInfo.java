package io.mosip.registration.processor.qc.users.dto;

import java.io.Serializable;

public class ExceptionJSONInfo implements Serializable {

	private static final long serialVersionUID = -4870610344003366727L;

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

	public void setErrorcode(String errorcode) {
		errorCode = errorcode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
